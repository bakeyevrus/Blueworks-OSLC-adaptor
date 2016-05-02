/*******************************************************************************
 * Copyright (c) 2012, 2013 IBM Corporation.
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
package uk.ibm.com.oslc.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.lyo.core.query.ParseException;
import org.eclipse.lyo.core.query.Properties;
import org.eclipse.lyo.core.query.QueryUtils;
import org.eclipse.lyo.oslc4j.core.OSLC4JConstants;
import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespaceDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcSchema;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.core.model.Preview;

import uk.ibm.com.oslc.ApplicationManager;
import uk.ibm.com.oslc.Constants;
import uk.ibm.com.oslc.resources.ChangeRequest;
import uk.ibm.com.oslc.resources.OSLCRequirement;
import uk.ibm.com.oslc.rm.IRequirementsConnector;
import uk.ibm.com.oslc.rm.RequirementInfo;
import uk.ibm.com.oslc.servlet.ServiceProviderCatalogSingleton;

import com.j2bugzilla.rpc.BugSearch;

@OslcService(Constants.CHANGE_MANAGEMENT_DOMAIN)
@Path("{productId}/requirements")
public class BugzillaChangeRequestService

{

	@Context
	private HttpServletRequest httpServletRequest;
	@Context
	private HttpServletResponse httpServletResponse;
	@Context
	private UriInfo uriInfo;

	public BugzillaChangeRequestService() {
		super();
	}

	@OslcDialogs({ @OslcDialog(title = "Blueworks Live Process Selection Dialog", label = "Blueworks Live Process Selection Dialog", uri = "/{productId}/requirements/selector", hintWidth = "510px", hintHeight = "500px", resourceTypes = { Constants.TYPE_REQUIREMENT }, usages = { OslcConstants.OSLC_USAGE_DEFAULT })

	})
	@OslcQueryCapability(title = "Change Request Query Capability", label = "Change Request Catalog Query", resourceShape = OslcConstants.PATH_RESOURCE_SHAPES
			+ "/" + Constants.PATH_CHANGE_REQUEST, resourceTypes = { Constants.TYPE_REQUIREMENT }, usages = { OslcConstants.OSLC_USAGE_DEFAULT })
	/**
	 * RDF/XML, XML and JSON representation of a change request collection
	 * 
	 * TODO:  add query support
	 * 
	 * @param productId
	 * @param where
	 * @param select
	 * @param prefix
	 * @param pageString
	 * @param orderBy
	 * @param searchTerms
	 * @param paging
	 * @param pageSize
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	@GET
	@Produces({ OslcMediaType.APPLICATION_RDF_XML,
			OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
	public List<OSLCRequirement> getChangeRequests(
			@PathParam("productId") final String productId,
			@QueryParam("oslc.where") final String where,
			@QueryParam("oslc.select") final String select,
			@QueryParam("oslc.prefix") final String prefix,
			@QueryParam("page") final String pageString,
			@QueryParam("oslc.orderBy") final String orderBy,
			@QueryParam("oslc.searchTerms") final String searchTerms,
			@QueryParam("oslc.paging") final String paging,
			@QueryParam("oslc.pageSize") final String pageSize)
			throws IOException, ServletException {

		final List<OSLCRequirement> results = new ArrayList<OSLCRequirement>();
		return results;
	}

	private static void addDefaultPrefixes(final Map<String, String> prefixMap) {
		recursivelyCollectNamespaceMappings(prefixMap, OSLCRequirement.class);
	}

	private static void recursivelyCollectNamespaceMappings(
			final Map<String, String> prefixMap,
			final Class<? extends Object> resourceClass) {
		final OslcSchema oslcSchemaAnnotation = resourceClass.getPackage()
				.getAnnotation(OslcSchema.class);

		if (oslcSchemaAnnotation != null) {
			final OslcNamespaceDefinition[] oslcNamespaceDefinitionAnnotations = oslcSchemaAnnotation
					.value();

			for (final OslcNamespaceDefinition oslcNamespaceDefinitionAnnotation : oslcNamespaceDefinitionAnnotations) {
				final String prefix = oslcNamespaceDefinitionAnnotation
						.prefix();
				final String namespaceURI = oslcNamespaceDefinitionAnnotation
						.namespaceURI();

				prefixMap.put(prefix, namespaceURI);
			}
		}

		final Class<?> superClass = resourceClass.getSuperclass();

		if (superClass != null) {
			recursivelyCollectNamespaceMappings(prefixMap, superClass);
		}

		final Class<?>[] interfaces = resourceClass.getInterfaces();

		if (interfaces != null) {
			for (final Class<?> interfac : interfaces) {
				recursivelyCollectNamespaceMappings(prefixMap, interfac);
			}
		}
	}

	/**
	 * HTML representation of change request collection
	 * 
	 * Forwards to changerequest_collection_html.jsp to build the html page
	 * 
	 * @param productId
	 * @param where
	 * @param prefix
	 * @param pageString
	 * @param orderBy
	 * @param searchTerms
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML })
	public Response getHtmlCollection(
			@PathParam("productId") final String productId,
			@QueryParam("oslc.where") final String where,
			@QueryParam("oslc.prefix") final String prefix,
			@QueryParam("page") final String pageString,
			@QueryParam("oslc.orderBy") final String orderBy,
			@QueryParam("oslc.searchTerms") final String searchTerms)
			throws ServletException, IOException {
		final List<OSLCRequirement> results = new ArrayList<OSLCRequirement>();
		if (results != null) {

			RequestDispatcher rd = httpServletRequest
					.getRequestDispatcher("/rm/changerequest_collection_html.jsp");
			rd.forward(httpServletRequest, httpServletResponse);
		}

		throw new WebApplicationException(Status.NOT_FOUND);
	}

	/**
	 * RDF/XML, XML and JSON representation of a single change request
	 * 
	 * @param productId
	 * @param changeRequestId
	 * @param propertiesString
	 * @param prefix
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("{changeRequestId}")
	@Produces({ OslcMediaType.APPLICATION_RDF_XML,
			OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
	public Response getChangeRequest(
			@PathParam("productId") final String productId,
			@PathParam("changeRequestId") final String changeRequestId,
			@QueryParam("oslc.properties") final String propertiesString,
			@QueryParam("oslc.prefix") final String prefix) throws IOException,
			ServletException, URISyntaxException {
		Map<String, String> prefixMap;

		try {
			prefixMap = QueryUtils.parsePrefixes(prefix);
		} catch (ParseException e) {
			throw new IOException(e);
		}

		addDefaultPrefixes(prefixMap);

		Properties properties;

		if (propertiesString == null) {
			properties = QueryUtils.WILDCARD_PROPERTY_LIST;
		} else {
			try {
				properties = QueryUtils
						.parseSelect(propertiesString, prefixMap);
			} catch (ParseException e) {
				throw new IOException(e);
			}
		}

		final RequirementInfo req = IRequirementsConnector.getInstance()
				.getRequirementById(changeRequestId);

		if (req != null) {
			OSLCRequirement changeRequest = null;

			changeRequest = OSLCRequirement.fromRequirement(req);

			changeRequest.setServiceProvider(ServiceProviderCatalogSingleton
					.getServiceProvider(httpServletRequest, productId)
					.getAbout());
			changeRequest.setAbout(getAboutURI(productId + "/requirements/"
					+ changeRequest.getIdentifier()));
			setETagHeader(getETagFromChangeRequest(changeRequest),
					httpServletResponse);

			httpServletRequest.setAttribute(
					OSLC4JConstants.OSLC4J_SELECTED_PROPERTIES,
					QueryUtils.invertSelectedProperties(properties));

			return Response
					.ok(changeRequest)
					.header(Constants.HDR_OSLC_VERSION,
							Constants.OSLC_VERSION_V2).build();
		}

		throw new WebApplicationException(Status.NOT_FOUND);
	}

	/**
	 * OSLC Compact representation of a single change request
	 * 
	 * Contains a reference to the smallPreview method in this class for the
	 * preview document
	 * 
	 * @param productId
	 * @param changeRequestId
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws ServletException
	 */
	@GET
	@Path("{changeRequestId}")
	@Produces({ OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML })
	public Compact getCompact(@PathParam("productId") final String productId,
			@PathParam("changeRequestId") final String changeRequestId)
			throws URISyntaxException, IOException, ServletException {
		OSLCRequirement changeRequest = getRequirementFromId(changeRequestId);

		if (changeRequest != null) {
			final Compact compact = new Compact();

			compact.setAbout(getAboutURI(productId + "/requirements/"
					+ changeRequest.getIdentifier()));
			compact.setTitle(changeRequest.getTitle());

			String iconUri = null;
			if (changeRequest.getRequirementType() != null
					&& "ACTIVITY".equals(changeRequest.getRequirementType())) {
				iconUri = ApplicationManager.getServletBase()
						+ "/images/resources/BlueworksActivity16.gif";
			} else {
				iconUri = ApplicationManager.getServletBase()
						+ "/images/resources/BlueworksProcess16.gif";
			}
			compact.setIcon(new URI(iconUri));

			// Create and set attributes for OSLC preview resource
			final Preview smallPreview = new Preview();
			smallPreview.setHintHeight("18em");
			smallPreview.setHintWidth("41em");
			smallPreview.setDocument(new URI(compact.getAbout().toString()
					+ "/smallPreview"));
			compact.setSmallPreview(smallPreview);

			// Use the HTML representation of a change request as the large
			// preview as well
			// Why this?
			//TODO: Remove and check for bugs
			final Preview largePreview = new Preview();
			largePreview.setHintHeight("18em");
			largePreview.setHintWidth("41em");
			largePreview.setDocument(new URI(compact.getAbout().toString()
					+ "/largePreview"));
			compact.setLargePreview(largePreview);
			return compact;
		}

		throw new WebApplicationException(Status.NOT_FOUND);
	}

	private OSLCRequirement getRequirementFromId(final String requirementId) {
		final RequirementInfo info = IRequirementsConnector.getInstance()
				.getRequirementById(requirementId);
		OSLCRequirement changeRequest = null;
		if (info != null) {
			try {
				changeRequest = OSLCRequirement.fromRequirement(info);

			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return changeRequest;
	}

	/**
	 * HTML representation for a single change request - redirect the request
	 * directly to Bugzilla
	 * 
	 * @param productId
	 * @param changeRequestId
	 * @throws ServletException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("{changeRequestId}")
	@Produces({ MediaType.TEXT_HTML })
	public Response getHtmlChangeRequest(
			@PathParam("productId") final String productId,
			@PathParam("changeRequestId") final String changeRequestId)
			throws ServletException, IOException, URISyntaxException {
		RequirementInfo info = IRequirementsConnector.getInstance()
				.getRequirementById(changeRequestId);
		String forwardUri = IRequirementsConnector.getInstance()
				.getEndpointStringForRequirement(info);
		httpServletResponse.sendRedirect(forwardUri);
		return Response.seeOther(new URI(forwardUri)).build();
	}

	/**
	 * OSLC delegated selection dialog for change requests
	 * 
	 * If called without a "terms" parameter, forwards to
	 * changerequest_selector.jsp to build the html for the IFrame
	 * 
	 * If called with a "terms" parameter, sends a Bug search to Bugzilla and
	 * then forwards to changerequest_filtered_json.jsp to build a JSON response
	 * 
	 * 
	 * @param terms
	 * @param productId
	 * @throws ServletException
	 * @throws IOException
	 */
	@GET
	@Path("selector")
	@Consumes({ MediaType.TEXT_HTML, MediaType.WILDCARD })
	public void changeRequestSelector(@QueryParam("terms") final String terms,
			@PathParam("productId") final String productId)
			throws ServletException, IOException {
		// int productIdNum = Integer.parseInt(productId);
		httpServletRequest.setAttribute("productId", productId);
		httpServletRequest.setAttribute("selectionUri", uriInfo
				.getAbsolutePath().toString());
		httpServletRequest.setAttribute("baseURL",
				ApplicationManager.getServletBase());

		if (terms != null) {
			String trimmedTerms = terms.trim();
			httpServletRequest.setAttribute("terms", trimmedTerms);
			sendFilteredBugsReponse(httpServletRequest, productId, trimmedTerms);

		} else {
			try {
				RequestDispatcher rd = httpServletRequest
						.getRequestDispatcher("/rm/changerequest_selector.jsp");
				rd.forward(httpServletRequest, httpServletResponse);

			} catch (Exception e) {
				throw new ServletException(e);
			}
		}

	}

	/**
	 * OSLC small preview for a single change request
	 * 
	 * Forwards to changerequest_preview_small.jsp to build the html
	 * 
	 * @param productId
	 * @param changeRequestId
	 * @throws ServletException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("{changeRequestId}/smallPreview")
	@Produces({ MediaType.TEXT_HTML })
	public void smallPreview(@PathParam("productId") final String productId,
			@PathParam("changeRequestId") final String changeRequestId)
			throws ServletException, IOException, URISyntaxException {

		OSLCRequirement changeRequest = getRequirementFromId(changeRequestId);
		if (changeRequest != null) {

			changeRequest.setServiceProvider(ServiceProviderCatalogSingleton
					.getServiceProvider(httpServletRequest, productId)
					.getAbout());
			changeRequest.setAbout(getAboutURI(productId + "/requirements/"
					+ changeRequest.getIdentifier()));

			final String baseUri = ApplicationManager.getServletBase();
			httpServletRequest.setAttribute("changeRequest", changeRequest);
			httpServletRequest.setAttribute("baseUri", baseUri);
			httpServletRequest.setAttribute("processType",
					changeRequest.getRequirementType());

			RequestDispatcher rd = httpServletRequest
					.getRequestDispatcher("/rm/changerequest_preview_small.jsp");
			rd.forward(httpServletRequest, httpServletResponse);
			return;
		}

		throw new WebApplicationException(Status.NOT_FOUND);

	}

	/**
	 * OSLC large preview for a single change request
	 * 
	 * Forwards to changerequest_preview_large.jsp to build the html
	 * 
	 * @param productId
	 * @param changeRequestId
	 * @throws ServletException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("{changeRequestId}/largePreview")
	@Produces({ MediaType.TEXT_HTML })
	public void getLargePreview(@PathParam("productId") final String productId,
			@PathParam("changeRequestId") final String changeRequestId)
			throws ServletException, IOException, URISyntaxException {
		OSLCRequirement changeRequest = getRequirementFromId(changeRequestId);
		if (changeRequest != null) {

			changeRequest.setServiceProvider(ServiceProviderCatalogSingleton
					.getServiceProvider(httpServletRequest, productId)
					.getAbout());
			changeRequest.setAbout(getAboutURI(productId + "/requirements/"
					+ changeRequest.getIdentifier()));

			final String baseUri = ApplicationManager.getServletBase();
			httpServletRequest.setAttribute("changeRequest", changeRequest);
			httpServletRequest.setAttribute("baseUri", baseUri);
			httpServletRequest.setAttribute("processType",
					changeRequest.getRequirementType());

			RequestDispatcher rd = httpServletRequest
					.getRequestDispatcher("/rm/changerequest_preview_small.jsp");
			rd.forward(httpServletRequest, httpServletResponse);
		}

		throw new WebApplicationException(Status.NOT_FOUND);

	}

	/**
	 * Create a single OSLCRequirement via RDF/XML, XML or JSON POST
	 * 
	 * @param productId
	 * @param changeRequest
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	@OslcDialog(title = "Requirement Creation Dialog", label = "Requirement Creation Dialog", uri = "/{productId}/requirements/creator", hintWidth = "570px", hintHeight = "500px", resourceTypes = { Constants.TYPE_REQUIREMENT }, usages = { OslcConstants.OSLC_USAGE_DEFAULT })
	@OslcCreationFactory(title = "Requirement Creation Factory", label = "Requirement Creation", resourceShapes = { OslcConstants.PATH_RESOURCE_SHAPES
			+ "/" + Constants.PATH_CHANGE_REQUEST }, resourceTypes = { Constants.TYPE_CHANGE_REQUEST }, usages = { OslcConstants.OSLC_USAGE_DEFAULT })
	@POST
	@Consumes({ OslcMediaType.APPLICATION_RDF_XML,
			OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
	@Produces({ OslcMediaType.APPLICATION_RDF_XML,
			OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
	public Response addChangeRequest(
			@PathParam("productId") final String productId,
			final OSLCRequirement changeRequest) throws IOException,
			ServletException

	{
		URI uri = changeRequest.getAbout();
		httpServletResponse.sendRedirect(uri.toString());
		return Response.seeOther(uri).build();
	}

	/**
	 * OSLC delegated creation dialog for a single change request
	 * 
	 * Forwards to changerequest_creator.jsp to build the html form
	 * 
	 * @param productId
	 * @throws IOException
	 * @throws ServletException
	 */
	@GET
	@Path("creator")
	@Consumes({ MediaType.WILDCARD })
	public void changeRequestCreator(
			@PathParam("productId") final String productId) throws IOException,
			ServletException {
		try {

			IRequirementsConnector bc = ApplicationManager
					.getRequirementsConnector(httpServletRequest);

			httpServletRequest.setAttribute("baseUri",
					ApplicationManager.getServletBase());

			RequestDispatcher rd = httpServletRequest
					.getRequestDispatcher("/rm/changerequest_creator.jsp");
			rd.forward(httpServletRequest, httpServletResponse);

		} catch (Exception e) {
			throw new WebApplicationException(e);
		}

	}

	/**
	 * Backend creator for the OSLC delegated creation dialog.
	 * 
	 * Accepts the input in FormParams and returns a small JSON response
	 * 
	 * @param productId
	 * @param component
	 * @param version
	 * @param summary
	 * @param op_sys
	 * @param platform
	 * @param description
	 */
	@POST
	@Path("creator")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public void createHtmlChangeRequest(
			@PathParam("productId") final String productId,
			@FormParam("component") final String component,
			@FormParam("version") final String version,
			@FormParam("summary") final String summary,
			@FormParam("op_sys") final String op_sys,
			@FormParam("platform") final String platform,
			@FormParam("description") final String description) {
		throw new RuntimeException(
				"creating new blueworks requirements is unsupported in this version.");
	}

	/**
	 * Updates a single change request via RDF/XML, XML or JSON PUT
	 * 
	 * Currently, update only supports adding OSLC CM link attributes to a Bug
	 * in the Bug comments
	 * 
	 * @param eTagHeader
	 * @param changeRequestId
	 * @param changeRequest
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	@PUT
	@Consumes({ OslcMediaType.APPLICATION_RDF_XML,
			OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
	@Path("{changeRequestId}")
	public Response updateChangeRequest(
			@HeaderParam("If-Match") final String eTagHeader,
			@PathParam("changeRequestId") final String changeRequestId,
			final OSLCRequirement changeRequest) throws IOException,
			ServletException {

		throw new RuntimeException(
				"updating blueworks requirements is unsupported in this version.");
	}

	private static void setETagHeader(final String eTagFromChangeRequest,
			final HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("ETag", eTagFromChangeRequest);
	}

	private static String getETagFromChangeRequest(
			final ChangeRequest changeRequest) {
		Long eTag = null;

		if (changeRequest.getModified() != null) {
			eTag = changeRequest.getModified().getTime();
		} else if (changeRequest.getCreated() != null) {
			eTag = changeRequest.getCreated().getTime();
		} else {
			eTag = new Long(0);
		}

		return eTag.toString();
	}

	protected URI getAboutURI(final String fragment) {
		URI about;
		try {
			about = new URI(ApplicationManager.getServicePath() + "/"
					+ fragment);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e);
		}
		return about;
	}

	/**
	 * Create and run a Bugzilla search and return the result.
	 * 
	 * Forwards to changerequest_filtered_json.jsp to create the JSON response
	 * 
	 * @param httpServletRequest
	 * @param productId
	 * @param terms
	 * @throws ServletException
	 * @throws IOException
	 */
	// The terms parameter is search query
	private void sendFilteredBugsReponse(
			final HttpServletRequest httpServletRequest,
			final String productId, final String terms)
			throws ServletException, IOException {
		try {
			final IRequirementsConnector bc = ApplicationManager
					.getRequirementsConnector(httpServletRequest);

			List<OSLCRequirement> results = new ArrayList<OSLCRequirement>();
			if (terms.isEmpty()) {
				List<RequirementInfo> requirements = IRequirementsConnector
						.getInstance().getRequirementsFromServiceProvider(
								productId);

				for (RequirementInfo requirementInfo : requirements) {
					OSLCRequirement changeRequest = OSLCRequirement
							.fromRequirement(requirementInfo);
					changeRequest
							.setAbout(getAboutURI(productId + "/requirements/"
									+ changeRequest.getIdentifier()));
					setInstanceShape(requirementInfo, changeRequest);
					results.add(changeRequest);
					addChildrenToList(requirementInfo, results, " -", productId);
				}
			}
			// a search string must have been provided, so let's search for
			// it...
			else {
				List<RequirementInfo> requirements = IRequirementsConnector
						.getInstance().getRequirementsFromServiceProvider(
								productId, terms);

				for (RequirementInfo requirementInfo : requirements) {
					OSLCRequirement changeRequest = OSLCRequirement
							.fromRequirement(requirementInfo);
					changeRequest
							.setAbout(getAboutURI(productId + "/requirements/"
									+ changeRequest.getIdentifier()));
					setInstanceShape(requirementInfo, changeRequest);
					results.add(changeRequest);
					addChildrenToList(requirementInfo, results, " -", productId);
				}

			}

			// just set the service provider for each and every change request -
			// easier to do it here
			for (OSLCRequirement cr : results) {
				cr.setServiceProvider(ServiceProviderCatalogSingleton
						.getServiceProvider(httpServletRequest, productId)
						.getAbout());
			}
			httpServletRequest.setAttribute("results", results);

			// BugSearch bugSearch = createBugSearch(terms);
			// bc.executeMethod(bugSearch);
			// List<Bug> bugList = bugSearch.getSearchResults();
			// List<OSLCRequirement> results =
			// changeRequestsFromBugList(httpServletRequest, bugList,
			// productId);

			RequestDispatcher rd = httpServletRequest
					.getRequestDispatcher("/rm/changerequest_filtered_json.jsp");
			rd.forward(httpServletRequest, httpServletResponse);

		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	private void addChildrenToList(RequirementInfo req,
			List<OSLCRequirement> results, String indent, String productId) {
		List<RequirementInfo> children = req.getChildren();
		if (children != null) {
			for (RequirementInfo child : children) {
				try {
					OSLCRequirement childReq = OSLCRequirement.fromRequirement(
							child, req);
					childReq.setAbout(getAboutURI(productId + "/requirements/"
							+ childReq.getIdentifier()));
					childReq.setDisplayIndent(indent);
					setInstanceShape(child, childReq);
					results.add(childReq);
					// TODO: Ensure, that is no need in this line, cause
					// Blueworks doesn't differ between subprocesses
					// addChildrenToList(child,results,indent + "-",productId);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private void setInstanceShape(RequirementInfo requirementInfo,
			OSLCRequirement changeRequest) {
		String requirementType = requirementInfo.getType();
		String subType = requirementInfo.getSubType();
		URI shapeURI = null;
		String baseURI = ApplicationManager.getServletBase();

		try {
			if (requirementType != null && "process".equals(requirementType)) {
				shapeURI = new URI(baseURI
						+ "/images/resources/BlueworksProcess16.gif");
			} else if (requirementType != null
					&& "activity".equals(requirementType)) {
				shapeURI = new URI(baseURI
						+ "/images/resources/BlueworksActivity16.gif");
			} else {
				shapeURI = new URI(baseURI
						+ "/images/resources/BlueworksProcess16.gif");
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (shapeURI != null) {
			changeRequest.setInstanceShape(shapeURI);
		}
	}

	protected BugSearch createBugSearch(final String summary) {
		BugSearch.SearchQuery summaryQuery = new BugSearch.SearchQuery(
				BugSearch.SearchLimiter.SUMMARY, summary);
		BugSearch.SearchQuery limitQuery = new BugSearch.SearchQuery(
				BugSearch.SearchLimiter.LIMIT, "50");

		BugSearch bugSearch = new BugSearch(summaryQuery, limitQuery);

		return bugSearch;
	}

	public static String getChangeRequestLinkLabel(int bugId, String summary) {
		return "Bug " + bugId + ": " + summary;
	}

}
