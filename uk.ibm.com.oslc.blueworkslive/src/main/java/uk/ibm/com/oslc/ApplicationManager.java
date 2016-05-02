/*******************************************************************************
 * Copyright (c) 2011, 2013 IBM Corporation.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *  
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *  
 *  Contributors:
 *  
 *	   Sam Padgett	       - initial API and implementation
 *     Michael Fiedler     - adapted for OSLC4J
 *******************************************************************************/

package uk.ibm.com.oslc;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryURIs;

import uk.ibm.com.oslc.rm.IRequirementsConnector;
import uk.ibm.com.oslc.servlet.CredentialsFilter;

import com.j2bugzilla.base.Product;
import com.j2bugzilla.rpc.GetProduct;


public class ApplicationManager implements ServletContextListener  {



	
	private static String servletBase = null;
	private static String servicePath = null;
	private static String admin = null;
	
	
	private static final String SERVICE_PATH = "/services";
	private static final String PROPERTY_SCHEME = ApplicationManager.class.getPackage().getName() + ".scheme";
    private static final String PROPERTY_PORT   = ApplicationManager.class.getPackage().getName() + ".port";
    private static final String SYSTEM_PROPERTY_NAME_REGISTRY_URI = ServiceProviderRegistryURIs.class.getPackage().getName() + ".registryuri";
    

    private static String HOST = "";

	
    // Blueworks adapter properties from oslc.properties 
    static {
        Properties props = new Properties();
        try {
            props.load(ApplicationManager.class.getResourceAsStream("/oslc.properties"));
            
            HOST = props.getProperty("localhostname");
            admin = props.getProperty("admin");
            // necessary for RRC to find the services ok
            System.setProperty("org.eclipse.lyo.oslc4j.alwaysXMLAbbrev", "true");
            System.setProperty(PROPERTY_PORT, props.getProperty("port"));
            System.setProperty(PROPERTY_SCHEME, props.getProperty("scheme"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) 
	{
		//No need to de-register - catalog will go away with the web app		
	}

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent)
    {
    	//Get the servlet base URI and set some paths to the REST services and the catalog
    	String basePath = generateBasePath(servletContextEvent);
    	servletBase = basePath;
    	servicePath = basePath + SERVICE_PATH;
    	System.setProperty(SYSTEM_PROPERTY_NAME_REGISTRY_URI, basePath + SERVICE_PATH + "/catalog/singleton");
    }

    /**
     * get BugzillaConnector from the HttpSession
     * 
     * The connector should be placed in the session by the CredentialsFilter servlet filter
     * 
     * @param request
     * @return connector 
     */
	public static IRequirementsConnector getRequirementsConnector(HttpServletRequest request) 
	{	
		//connector should never be null if CredentialsFilter is doing its job
		IRequirementsConnector connector = (IRequirementsConnector) request.getSession().getAttribute(CredentialsFilter.CONNECTOR_ATTRIBUTE);	
		return connector;
	}
	
	
    private static String generateBasePath(final ServletContextEvent servletContextEvent)
    {
        final ServletContext servletContext = servletContextEvent.getServletContext();

        String scheme = System.getProperty(PROPERTY_SCHEME);
        if (scheme == null)
        {
            scheme = servletContext.getInitParameter(PROPERTY_SCHEME);
        }

        String port = System.getProperty(PROPERTY_PORT);
        if (port == null)
        {
            port = servletContext.getInitParameter(PROPERTY_PORT);
        }
        
        //deal with default http and https ports
        if (port == null || "".equals(port) || port.equals("80") || port.equals ("443"))
        {
        	port = "";
        }
        else
        {
        	port = ":" +port;
        } 
        return scheme + "://" + HOST + port + servletContext.getContextPath();
    }

   

   
	public static String getServletBase() {
		return servletBase;
	}
	
	public static String getServicePath() {
		return servicePath;
	}
	
	public static String getAdmin() {
		return admin;
	}
}
