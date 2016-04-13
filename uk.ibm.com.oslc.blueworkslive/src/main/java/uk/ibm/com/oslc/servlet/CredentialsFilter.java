/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *     Michael Fiedler     - initial API and implementation for Bugzilla adapter
 *     
 *******************************************************************************/
package uk.ibm.com.oslc.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.oauth.OAuth;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.http.HttpMessage;
import net.oauth.server.OAuthServlet;

import org.eclipse.lyo.server.oauth.consumerstore.FileSystemConsumerStore;
import org.eclipse.lyo.server.oauth.core.Application;
import org.eclipse.lyo.server.oauth.core.AuthenticationException;
import org.eclipse.lyo.server.oauth.core.OAuthConfiguration;
import org.eclipse.lyo.server.oauth.core.OAuthRequest;
import org.eclipse.lyo.server.oauth.core.token.LRUCache;
import org.eclipse.lyo.server.oauth.core.token.SimpleTokenStrategy;

import uk.ibm.com.oslc.Credentials;
import uk.ibm.com.oslc.exception.UnauthorizedException;
import uk.ibm.com.oslc.rm.IRequirementsConnector;
import uk.ibm.com.oslc.utils.HttpUtils;

import com.j2bugzilla.base.ConnectionException;

public class CredentialsFilter implements Filter {


	
    public static final String CONNECTOR_ATTRIBUTE = "uk.ibm.com.oslc.BugzillaConnector";
    public static final String CREDENTIALS_ATTRIBUTE = "uk.ibm.com.oslc.Credentials";
    private static final String ADMIN_SESSION_ATTRIBUTE = "uk.ibm.com.oslc.AdminSession";
    public static final String JAZZ_INVALID_EXPIRED_TOKEN_OAUTH_PROBLEM = "invalid_expired_token";
    public static final String OAUTH_REALM = IRequirementsConnector.getInstance().getRealm();
		
	private static LRUCache<String, IRequirementsConnector> keyToConnectorCache = new LRUCache<String, IRequirementsConnector>(200);
	
	@Override
	public void destroy() {
		

	}
	


	/**
	 * Check for OAuth or BasicAuth credentials and challenge if not found.
	 * 
	 * Store the BugzillaConnector in the HttpSession for retrieval in the REST services.
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
		
		if(servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpServletResponse response = (HttpServletResponse) servletResponse;
		
			//Don't protect requests to oauth service.   TODO: possibly do this in web.xml
			if (! request.getPathInfo().startsWith("/oauth"))
			{
			
				// First check if this is an OAuth request.
				try {
					try {
						OAuthMessage message = OAuthServlet.getMessage(request, null);
						if (message.getToken() != null) {
							OAuthRequest oAuthRequest = new OAuthRequest(request);
							oAuthRequest.validate();
							IRequirementsConnector connector = keyToConnectorCache.get(message
									.getToken());
							if (connector == null) {
								throw new OAuthProblemException(
										OAuth.Problems.TOKEN_REJECTED);
							}
			
							request.getSession().setAttribute(CONNECTOR_ATTRIBUTE, connector);
						}
					} catch (OAuthProblemException e) {
						if (OAuth.Problems.TOKEN_REJECTED.equals(e.getProblem()))
							throwInvalidExpiredException(e);
						else
							throw e;
					}
				} catch (OAuthException e) {
					OAuthServlet.handleException(response, e, OAUTH_REALM);
					return;
				}
                
				
				// This is not an OAuth request. Check for basic access authentication.
				HttpSession session = request.getSession();
				IRequirementsConnector connector = (IRequirementsConnector) session
						.getAttribute(CONNECTOR_ATTRIBUTE);
				if (connector == null) {
					try {
						Credentials credentials = (Credentials) request.getSession().getAttribute(CREDENTIALS_ATTRIBUTE);
						if (credentials == null)
						{
							credentials = HttpUtils.getCredentials(request);
							if (credentials == null) {
								throw new UnauthorizedException();
							}
						}
						connector = getBugzillaConnector(credentials);
						session.setAttribute(CONNECTOR_ATTRIBUTE, connector);
						session.setAttribute(CREDENTIALS_ATTRIBUTE,
						        credentials);
				
					} catch (UnauthorizedException e)
					{
						HttpUtils.sendUnauthorizedResponse(response, e);
						System.err.println(e.getMessage());
						return;
					} catch (ConnectionException ce)
					{
						throw new ServletException(ce);
					}
					
				}
			}
		}

		chain.doFilter(servletRequest, servletResponse);
	}
	
	public static IRequirementsConnector getBugzillaConnector(Credentials credentials)
			throws ConnectionException, UnauthorizedException {
		IRequirementsConnector bc = IRequirementsConnector.getInstance();
		try {
			String status = bc.validateUser(credentials.getUsername(), credentials.getPassword());
			if (status == null) {
				throw new UnauthorizedException();
			}
		} catch (Exception e) {
			throw new UnauthorizedException(e.getCause().getMessage());
		}
		return bc;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		OAuthConfiguration config = OAuthConfiguration.getInstance();

		// Validates a user's ID and password.
		config.setApplication(new Application() {
			@Override
			public void login(HttpServletRequest request, String id,
					String password) throws AuthenticationException {
				try {
					IRequirementsConnector bc = IRequirementsConnector.getInstance();
					bc.validateUser(id, password);
					request.setAttribute(CONNECTOR_ATTRIBUTE, bc);
					request.getSession().setAttribute(ADMIN_SESSION_ATTRIBUTE,true);
					Credentials creds = new Credentials();
					creds.setUsername(id);
					creds.setPassword(password);
                    request.getSession().setAttribute(CREDENTIALS_ATTRIBUTE,
                            creds);
				} catch (Exception e) {
					throw new AuthenticationException(e.getMessage(), e);
				}
			}

			@Override
			public String getName() {
				// Display name for this application.
				return IRequirementsConnector.getInstance().getToolName();
			}

			@Override
			public boolean isAdminSession(HttpServletRequest request) {
				return Boolean.TRUE.equals(request.getSession().getAttribute(
						ADMIN_SESSION_ATTRIBUTE));
			}

			@Override
			public String getRealm(HttpServletRequest request) {
				//TODO need to work with blueworkslive realm
				//return IRequirementsConnector.getInstance().getRealm();
				return IRequirementsConnector.getInstance().getRealm();
			}

			@Override
			public boolean isAuthenticated(HttpServletRequest request) {
				IRequirementsConnector bc = (IRequirementsConnector) request.getSession()
						.getAttribute(CONNECTOR_ATTRIBUTE);
				if (bc == null) {
					return false;
				}
				
				request.setAttribute(CONNECTOR_ATTRIBUTE, bc);
				return true;
			}
		});

		/*
		 * Override some SimpleTokenStrategy methods so that we can keep the
		 * BugzillaConnection associated with the OAuth tokens.
		 */
		config.setTokenStrategy(new SimpleTokenStrategy() {
			@Override
			public void markRequestTokenAuthorized(
					HttpServletRequest httpRequest, String requestToken)
					throws OAuthProblemException {
				keyToConnectorCache.put(requestToken,
						(IRequirementsConnector) httpRequest.getAttribute(CONNECTOR_ATTRIBUTE));
				super.markRequestTokenAuthorized(httpRequest, requestToken);
			}

			@Override
			public void generateAccessToken(OAuthRequest oAuthRequest)
					throws OAuthProblemException, IOException {
				String requestToken = oAuthRequest.getMessage().getToken();
				IRequirementsConnector bc = keyToConnectorCache.remove(requestToken);
				super.generateAccessToken(oAuthRequest);
				keyToConnectorCache.put(oAuthRequest.getAccessor().accessToken, bc);
			}
		});

		try {
			// For now, hard-code the consumers.
			config.setConsumerStore(new FileSystemConsumerStore("bugzillaOAuthStore.xml"));
		} catch (Throwable t) {
			System.err.println("Error initializing the OAuth consumer store: " +  t.getMessage());
		
		}

	}
	
	/**
	 * Jazz requires a exception with the magic string "invalid_expired_token" to restart
	 * OAuth authentication
	 * @param e
	 * @return
	 * @throws OAuthProblemException 
	 */
	private void throwInvalidExpiredException(OAuthProblemException e) throws OAuthProblemException {
		OAuthProblemException ope = new OAuthProblemException(JAZZ_INVALID_EXPIRED_TOKEN_OAUTH_PROBLEM);
		ope.setParameter(HttpMessage.STATUS_CODE, new Integer(
				HttpServletResponse.SC_UNAUTHORIZED));
		throw ope;
	}

}
