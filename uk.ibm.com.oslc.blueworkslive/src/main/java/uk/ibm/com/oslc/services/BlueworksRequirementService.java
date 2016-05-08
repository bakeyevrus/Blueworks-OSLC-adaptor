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
import uk.ibm.com.oslc.blueworks.BlueworksProcess;
import uk.ibm.com.oslc.blueworks.BlueworksProcessActivity;
import uk.ibm.com.oslc.resources.OSLCRequirement;
import uk.ibm.com.oslc.resources.Requirement;
import uk.ibm.com.oslc.rm.IRequirementsConnector;
import uk.ibm.com.oslc.servlet.ServiceProviderCatalogSingleton;

@OslcService(Constants.REQUIREMENTS_MANAGEMENT_NAMESPACE)
@Path("{productId}/requirements")
public class BlueworksRequirementService

{

	@Context
	private HttpServletRequest httpServletRequest;
	@Context
	private HttpServletResponse httpServletResponse;
	@Context
	private UriInfo uriInfo;

	public BlueworksRequirementService() {
		super();
	}

	@OslcDialogs
	(
			{ @OslcDialog
				(
						title = "Blueworks Live Process Selection Dialog", 
						label = "Blueworks Live Process Selection Dialog", 
						uri = "/{productId}/requirements/selector", 
						hintWidth = "510px", 
						hintHeight = "500px", 
						resourceTypes = { Constants.TYPE_REQUIREMENT }, 
						usages = { OslcConstants.OSLC_USAGE_DEFAULT }
				)}
	)
	
	@OslcQueryCapability
	(
			title = "Requirement Query Capability", 
			label = "Requirement Catalog Query", 
			resourceShape = OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_REQUIREMENT, 
			resourceTypes = { Constants.TYPE_REQUIREMENT }, 
			usages = { OslcConstants.OSLC_USAGE_DEFAULT }
	)
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
	public List<OSLCRequirement> getRequirements(
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

	// TODO: RB - what is it? Rewrite
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
					.getRequestDispatcher("/jsps/requirement_collection_html.jsp");
			rd.forward(httpServletRequest, httpServletResponse);
		}

		throw new WebApplicationException(Status.NOT_FOUND);
	}

	/**
	 * RDF/XML, XML and JSON representation of a single requirement
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
	@Path("{requirementId}")
	@Produces({ OslcMediaType.APPLICATION_RDF_XML,
			OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
	public Response getRequirement(
			@PathParam("productId") final String productId,
			@PathParam("requirementId") final String requirementId,
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

		final BlueworksProcess bwlItem = IRequirementsConnector.getInstance()
				.getBlueworksProcessByItemId(requirementId);

		if (bwlItem != null) {

			OSLCRequirement requirement = null;
			requirement = OSLCRequirement.fromBlueworksItem(bwlItem,
					requirementId);

			requirement.setServiceProvider(ServiceProviderCatalogSingleton
					.getServiceProvider(httpServletRequest, productId)
					.getAbout());
			requirement.setAbout(getAboutURI(productId + "/requirements/"
					+ requirement.getIdentifier()));
			setETagHeader(getETagFromRequirement(requirement),
					httpServletResponse);

			httpServletRequest.setAttribute(
					OSLC4JConstants.OSLC4J_SELECTED_PROPERTIES,
					QueryUtils.invertSelectedProperties(properties));
			
			Response out = Response
					.ok(requirement)
					.header(Constants.HDR_OSLC_VERSION,
							Constants.OSLC_VERSION_V2).build();
			return out;
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
	@Path("{requirementId}")
	@Produces({ OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML })
	public Compact getCompact(@PathParam("productId") final String productId,
			@PathParam("requirementId") final String requirementId)
			throws URISyntaxException, IOException, ServletException {
		OSLCRequirement requirement = getRequirementFromId(requirementId);

		if (requirement != null) {
			final Compact compact = new Compact();

			compact.setAbout(getAboutURI(productId + "/requirements/"
					+ requirement.getIdentifier()));
			compact.setTitle(requirement.getTitle());

			String iconUri = null;
			if (requirement.getRequirementType() != null
					&& "ACTIVITY".equals(requirement.getRequirementType())) {
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
		final BlueworksProcess process = IRequirementsConnector.getInstance()
				.getBlueworksProcessByItemId(requirementId);
		OSLCRequirement req = null;

		if (process != null) {
			try {
				req = OSLCRequirement.fromBlueworksItem(process, requirementId);
			} catch (URISyntaxException e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
		return req;
	}

	/**
	 * HTML representation for a single change request - redirect the request
	 * directly to Blueworks Live
	 * 
	 * @param productId
	 * @param requirementId
	 * @throws ServletException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("{requirementId}")
	@Produces({ MediaType.TEXT_HTML })
	public Response getHtmlRequirement(
			@PathParam("productId") final String productId,
			@PathParam("requirementId") final String requirementId)
			throws ServletException, IOException, URISyntaxException {
		BlueworksProcess process = IRequirementsConnector.getInstance()
				.getBlueworksProcessByItemId(requirementId);
		String forwardUri = IRequirementsConnector.getInstance()
				.getEndpointStringFromBlueworksItem(process, requirementId);
		httpServletResponse.sendRedirect(forwardUri);
		return Response.seeOther(new URI(forwardUri)).build();
	}

	/**
	 * OSLC delegated selection dialog for change requests
	 * 
	 * If called without a "terms" parameter, forwards to
	 * changerequest_selector.jsp to build the html for the IFrame
	 * 
	 * If called with a "terms" parameter, sends a process search to Blueworks and
	 * then forwards to requirement_filtered_json.jsp to build a JSON response
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
	public void requirementSelector(@QueryParam("terms") final String terms,
			@PathParam("productId") final String productId)
			throws ServletException, IOException {
		
		httpServletRequest.setAttribute("productId", productId);
		httpServletRequest.setAttribute("selectionUri", uriInfo
				.getAbsolutePath().toString());
		httpServletRequest.setAttribute("baseURL",
				ApplicationManager.getServletBase());

		if (terms != null) {
			String trimmedTerms = terms.trim();
			httpServletRequest.setAttribute("terms", trimmedTerms);
			sendFilteredProcessesReponse(httpServletRequest, productId,
					trimmedTerms);
		} else {
			try {
				RequestDispatcher rd = httpServletRequest
						.getRequestDispatcher("/jsps/requirement_selector.jsp");
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
	@Path("{requirementId}/smallPreview")
	@Produces({ MediaType.TEXT_HTML })
	public void smallPreview(@PathParam("productId") final String productId,
			@PathParam("requirementId") final String requirementId)
			throws ServletException, IOException, URISyntaxException {

		OSLCRequirement requirement = getRequirementFromId(requirementId);

		if (requirement != null) {

			requirement.setServiceProvider(ServiceProviderCatalogSingleton
					.getServiceProvider(httpServletRequest, productId)
					.getAbout());
			requirement.setAbout(getAboutURI(productId + "/requirements/"
					+ requirement.getIdentifier()));

			final String baseUri = ApplicationManager.getServletBase();
			httpServletRequest.setAttribute("requirement", requirement);
			httpServletRequest.setAttribute("baseUri", baseUri);
			httpServletRequest.setAttribute("processType",
					requirement.getRequirementType());

			RequestDispatcher rd = httpServletRequest
					.getRequestDispatcher("/jsps/requirement_preview_small.jsp");
			rd.forward(httpServletRequest, httpServletResponse);
			return;
		}

		throw new WebApplicationException(Status.NOT_FOUND);

	}

	/**
	 * OSLC large preview for a single change request
	 * 
	 * Forwards to requirement_preview_large.jsp to build the html
	 * 
	 * @param productId
	 * @param changeRequestId
	 * @throws ServletException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("{requirementId}/largePreview")
	@Produces({ MediaType.TEXT_HTML })
	public void largePreview(@PathParam("productId") final String productId,
			@PathParam("requirementId") final String requirementId)
			throws ServletException, IOException, URISyntaxException {
		OSLCRequirement requirement = getRequirementFromId(requirementId);
		if (requirement != null) {

			requirement.setServiceProvider(ServiceProviderCatalogSingleton
					.getServiceProvider(httpServletRequest, productId)
					.getAbout());
			requirement.setAbout(getAboutURI(productId + "/requirements/"
					+ requirement.getIdentifier()));

			final String baseUri = ApplicationManager.getServletBase();
			httpServletRequest.setAttribute("requirement", requirement);
			httpServletRequest.setAttribute("baseUri", baseUri);
			httpServletRequest.setAttribute("processType",
					requirement.getRequirementType());

			RequestDispatcher rd = httpServletRequest
					.getRequestDispatcher("/jsps/requirement_preview_large.jsp");
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
	@OslcDialog
	(
	title = "Requirement Creation Dialog", 
	label = "Requirement Creation Dialog", 
	uri = "/{productId}/requirements/creator", 
	hintWidth = "570px", 
	hintHeight = "500px", 
	resourceTypes = { Constants.TYPE_REQUIREMENT }, 
	usages = { OslcConstants.OSLC_USAGE_DEFAULT }
	)
	
	@OslcCreationFactory
	(
			title = "Requirement Creation Factory", 
			label = "Requirement Creation", 
			resourceShapes = { OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_REQUIREMENT}, 
			resourceTypes = { Constants.TYPE_REQUIREMENT }, 
			usages = { OslcConstants.OSLC_USAGE_DEFAULT }
	)
	
	@POST
	@Consumes({ OslcMediaType.APPLICATION_RDF_XML,
			OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
	@Produces({ OslcMediaType.APPLICATION_RDF_XML,
			OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
	public Response addRequirement(
			@PathParam("productId") final String productId,
			final OSLCRequirement requirement) throws IOException,
			ServletException

	{
		URI uri = requirement.getAbout();
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
	public void requirementCreator(
			@PathParam("productId") final String productId) throws IOException,
			ServletException {
		try {

			IRequirementsConnector bc = ApplicationManager
					.getRequirementsConnector(httpServletRequest);

			httpServletRequest.setAttribute("baseUri",
					ApplicationManager.getServletBase());

			RequestDispatcher rd = httpServletRequest
					.getRequestDispatcher("/jsps/requirement_creator.jsp");
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
	public void createHtmlRequirement(
			@PathParam("productId") final String productId,
			@FormParam("version") final String version,
			@FormParam("summary") final String summary,
			@FormParam("description") final String description) {
		throw new RuntimeException(
				"Creating new Blueworks requirements is unsupported in this version.");
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
	@Path("{requirementId}")
	public Response updateRequirement(
			@HeaderParam("If-Match") final String eTagHeader,
			@PathParam("requirementId") final String requirementId,
			final OSLCRequirement changeRequest) throws IOException,
			ServletException {

		throw new RuntimeException(
				"Updating Blueworks requirements is unsupported in this version.");
	}

	private static void setETagHeader(final String eTagFromRequirement,
			final HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("ETag", eTagFromRequirement);
	}

	private static String getETagFromRequirement(final Requirement req) {
		Long eTag = null;

		if (req.getModified() != null) {
			eTag = req.getModified().getTime();
		} else if (req.getCreated() != null) {
			eTag = req.getCreated().getTime();
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
	 * Create and run a Blueworks Live search and return the result.
	 * 
	 * Forwards to requirement_filtered_json.jsp to create the JSON response
	 * 
	 * @param httpServletRequest
	 * @param productId
	 * @param terms
	 * @throws ServletException
	 * @throws IOException
	 */
	// The terms parameter is search query
	private void sendFilteredProcessesReponse(
			final HttpServletRequest httpServletRequest,
			final String productId, final String terms)
			throws ServletException, IOException {
		try {
			List<OSLCRequirement> results = new ArrayList<OSLCRequirement>();

			List<BlueworksProcess> processes = IRequirementsConnector
					.getInstance().getBlueworksProcessesBySearchTerms(
							productId, terms);

			for (BlueworksProcess process : processes) {
				OSLCRequirement requirement = OSLCRequirement
						.fromBlueworksItem(process, process.getProcessId());
				requirement.setAbout(getAboutURI(productId + "/requirements/"
						+ requirement.getIdentifier()));
				requirement.setServiceProvider(ServiceProviderCatalogSingleton
						.getServiceProvider(httpServletRequest, productId)
						.getAbout());
				setInstanceShape(requirement);				
				results.add(requirement);
				addChildrenToList(process, results, productId);
			}
			httpServletRequest.setAttribute("results", results);

			RequestDispatcher rd = httpServletRequest
					.getRequestDispatcher("/jsps/requirement_filtered_json.jsp");
			rd.forward(httpServletRequest, httpServletResponse);
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	private void addChildrenToList(BlueworksProcess parentProcess,
			List<OSLCRequirement> results, String productId) {

		List<BlueworksProcessActivity> children = parentProcess
				.getProcessActivities();
		if (children != null) {
			for (BlueworksProcessActivity child : children) {
				try {
					OSLCRequirement childReq = OSLCRequirement
							.fromBlueworksItem(parentProcess,
									child.getActivityId());
					childReq.setAbout(getAboutURI(productId + "/requirements/"
							+ childReq.getIdentifier()));
					childReq.setDisplayIndent("--");
					setInstanceShape(childReq);
					results.add(childReq);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void setInstanceShape(OSLCRequirement requirement) {
		String requirementType = requirement.getRequirementType();
		URI shapeURI = null;
		String baseURI = ApplicationManager.getServletBase();

		try {
			if (requirementType != null && "PROCESS".equals(requirementType)) {
				shapeURI = new URI(baseURI
						+ "/images/resources/BlueworksProcess16.gif");
			} else if (requirementType != null
					&& "ACTIVITY".equals(requirementType)) {
				shapeURI = new URI(baseURI
						+ "/images/resources/BlueworksActivity16.gif");
			} else {
				shapeURI = new URI(baseURI
						+ "/images/resources/BlueworksProcess16.gif");
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (shapeURI != null) {
			requirement.setInstanceShape(shapeURI);
		}
	}
}
