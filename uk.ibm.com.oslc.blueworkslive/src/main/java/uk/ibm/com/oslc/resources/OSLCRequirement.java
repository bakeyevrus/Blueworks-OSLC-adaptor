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
 *	   Sam Padgett	       - initial API and implementation
 *     Michael Fiedler     - adapted for OSLC4J
 *     
 *******************************************************************************/
package uk.ibm.com.oslc.resources;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.wink.common.model.wadl.Request;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcOccurs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.model.Occurs;

import uk.ibm.com.oslc.Constants;
import uk.ibm.com.oslc.blueworks.BlueworksProcess;
import uk.ibm.com.oslc.blueworks.BlueworksProcessActivity;
import uk.ibm.com.oslc.exception.UnauthorizedException;
import uk.ibm.com.oslc.rm.IRequirementsConnector;
import uk.ibm.com.oslc.rm.RequirementInfo;

//OSLC4J should give an rdf:type of oslc_rm:Requrements
@OslcNamespace(Constants.REQUIREMENTS_MANAGEMENT_NAMESPACE)
@OslcName(Constants.TYPE_REQUIREMENT)
@OslcResourceShape(title = "Requirements Management Resource Shape", describes = Constants.TYPE_REQUIREMENT)
public final class OSLCRequirement extends Requirement {

	public OSLCRequirement() throws URISyntaxException {
		super();
	}

	public OSLCRequirement(URI about) throws URISyntaxException {
		super(about);
	}

	// Blueworks extended attributes beyond OSLC base Requirement
	private String requirementType = null;
	// TODO: RB - What is subtype?
	// TODO: RB - OSLC Annotations to parameters
	private String requirementSubType = null;
	private String parentName = null;
	private String childrenNames = "";
	private String displayIndent = "";
	private String lastModifiedBy = null;

	public String getDisplayIndent() {
		return displayIndent;
	}

	public void setDisplayIndent(String displayIndent) {
		this.displayIndent = displayIndent;
	}

	public void setIdentifier(int identifier) throws URISyntaxException {
		setIdentifier(Integer.toString(identifier));
	}

	public String getRequirementType() {
		return requirementType;
	}

	public void setRequirementType(String product) {
		this.requirementType = product;
	}

	// TODO: RB - Possible remove this property
	public String getRequirementSubType() {
		return requirementSubType;
	}

	public void setRequirementSubType(String requirementSubType) {
		this.requirementSubType = requirementSubType;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getChildrenNames() {
		return childrenNames;
	}

	public void addChildNames(String childName) {
		if (childrenNames != null && !"".equals(childrenNames)) {
			childrenNames = childrenNames + "";
		}
		childrenNames = childrenNames + childName;
	}

	public String getModifiedDateAsString() {
		String formattedString = "unknown";
		if (getModified() != null) {
			SimpleDateFormat format = new SimpleDateFormat(
					"EEE, MMM d, yyyy HH:mm");
			formattedString = format.format(getModified());
		}
		return formattedString;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public static OSLCRequirement fromBlueworksItem(BlueworksProcess process,
			String itemId) throws URISyntaxException {

		// Now check if it actually was a process
		if (process.getProcessId().equals(itemId)) {
			// If yes - then get it as a RequirementInfo object
			return OSLCRequirement.fromBlueworksProcess(process);
		} else {
			// Find the activity that matches the ID and then return that
			// instead
			for (BlueworksProcessActivity blueworksActivity : process
					.getProcessActivities()) {

				if (blueworksActivity.getActivityId().equals(itemId)) {
					return OSLCRequirement.fromBlueworksProcessActivity(
							blueworksActivity, process);
				}
			}

		}
		return null;
	}

	private static OSLCRequirement fromBlueworksProcess(BlueworksProcess process)
			throws URISyntaxException {
		OSLCRequirement req = new OSLCRequirement();
		req.setIdentifier(process.getProcessId());
		req.setTitle(process.getName());
		req.setRequirementType(process.getType());
		req.setModified(process.getModifiedDate());
		req.setParentName("None");

		String owner = process.getModifiedUserName();
		if (owner == null || "".equals(owner)) {
			req.setLastModifiedBy("Unknown");
		} else {
			req.setLastModifiedBy(owner);
		}

		List<BlueworksProcessActivity> children = process
				.getProcessActivities();
		if (children == null || children.size() == 0) {
			req.addChildNames("None");
		} else {
			for (BlueworksProcessActivity activity : children) {
				String childLink = "<li><a target=\"_blank\" href=\""
						+ IRequirementsConnector.getInstance()
								.getEndpointStringForProcessActivity(activity)
						+ "\" >" + activity.getActivityName() + "</a></li>";
				req.addChildNames(childLink);
			}
		}

		return req;
	}

	private static OSLCRequirement fromBlueworksProcessActivity(
			BlueworksProcessActivity activity, BlueworksProcess parentProcess)
			throws URISyntaxException {
		OSLCRequirement req = new OSLCRequirement();
		req.setIdentifier(activity.getActivityId());
		req.setTitle(activity.getActivityName());
		req.setRequirementType(activity.getType());
		req.setModified(null);

		String parentEndPoint = IRequirementsConnector.getInstance()
				.getEndpointStringForProcess(parentProcess);
		String parentName = parentProcess.getName();
		String parentLink = "<a target=\"_blank\" href=\"" + parentEndPoint
				+ "\" >" + parentName + "</a>";
		req.setParentName(parentLink);

		String doc = activity.getActivityDocumentation();
		if (doc == null || doc.equals("")) {
			doc = "<i>Unfortunately the description is not available from BlueWorksLive</i>";
		}
		req.setDescription(doc);
		req.addChildNames("None");
		req.setLastModifiedBy("Unknown");

		return req;
	}

	@Deprecated
	/**
	 * Converts a {@link Bug} to an OSLC-CM OSLCRequirement.
	 * 
	 * @param bug
	 *            the bug
	 * @return the ChangeRequest to be serialized
	 * @throws URISyntaxException
	 *             on errors setting the bug URI
	 * @throws UnsupportedEncodingException
	 */
	public static OSLCRequirement fromRequirement(RequirementInfo req)
			throws URISyntaxException, UnsupportedEncodingException {
		OSLCRequirement changeRequest = new OSLCRequirement();
		changeRequest.setIdentifier(req.getId());
		changeRequest.setTitle(req.getName());
		changeRequest.setRequirementType(req.getType());
		// TODO: RB - try to find if the files subType != null
		changeRequest.setRequirementSubType(req.getSubType());
		changeRequest.setModified(req.getLastModified());
		if (req.getParent() == null || "".equals(req.getParent())) {
			changeRequest.setParentName("None");
		} else {
			RequirementInfo parentInfo = IRequirementsConnector.getInstance()
					.getRequirementById(req.getParent());
			String parentEndPoint = IRequirementsConnector.getInstance()
					.getEndpointStringForRequirement(parentInfo);
			String parentName = parentInfo.getName();
			String parentLink = "<a target=\"_blank\" href=\"" + parentEndPoint
					+ "\" >" + parentName + "</a>";
			changeRequest.setParentName(parentLink);

		}
		// set the documentation field
		String doc = req.getDocumentation();
		if (doc == null || "".equals(doc)) {
			doc = "<i>Unfortunately the description is not available from BlueWorksLive</i>";
		}
		changeRequest.setDescription(doc);

		// set the children
		List<RequirementInfo> children = req.getChildren();
		if (children == null || children.size() == 0) {
			changeRequest.addChildNames("None");
		} else {
			for (RequirementInfo requirementInfo : children) {
				String childLink = "<li><a target=\"_blank\" href=\""
						+ IRequirementsConnector.getInstance()
								.getEndpointStringForRequirement(
										requirementInfo) + "\" >"
						+ requirementInfo.getName() + "</a></li>";
				changeRequest.addChildNames(childLink);
			}
		}

		// finally set the modified by
		String owner = req.getLastModifiedBy();
		if (owner == null || "".equals(owner)) {
			changeRequest.setLastModifiedBy("Unknown");
		} else {
			changeRequest.setLastModifiedBy(owner);
		}

		return changeRequest;
	}

	@Deprecated
	/*
	 * Ok, we have a similar method, but it is too slow to get the parent info
	 * from Blueworks again and again, that's why we are passing info about a
	 * parent requirement
	 * 
	 * Where is Unsupported ex?
	 */
	public static OSLCRequirement fromRequirement(RequirementInfo req,
			RequirementInfo parentReq) throws URISyntaxException {
		OSLCRequirement changeRequest = new OSLCRequirement();
		changeRequest.setIdentifier(req.getId());
		changeRequest.setTitle(req.getName());
		changeRequest.setRequirementType(req.getType());
		changeRequest.setRequirementSubType(req.getSubType());
		changeRequest.setModified(req.getLastModified());

		String parentEndPoint = IRequirementsConnector.getInstance()
				.getEndpointStringForRequirement(parentReq);
		String parentName = parentReq.getName();
		String parentLink = "<a target=\"_blank\" href=\"" + parentEndPoint
				+ "\" >" + parentName + "</a>";
		changeRequest.setParentName(parentLink);

		String doc = req.getDocumentation();
		if (doc == null || doc.equals("")) {
			doc = "<i>Unfortunately the description is not available from BlueWorksLive</i>";
		}
		changeRequest.setDescription(doc);

		List<RequirementInfo> children = req.getChildren();
		if (children == null || children.size() == 0) {
			changeRequest.addChildNames("None");
		} else {
			for (RequirementInfo reqInfo : req.getChildren()) {
				String childLink = "<li><a target=\"_blank\" href=\""
						+ IRequirementsConnector.getInstance()
								.getEndpointStringForRequirement(reqInfo)
						+ "\" >" + reqInfo.getName() + "</a></li>";
				changeRequest.addChildNames(childLink);
			}
		}

		String owner = req.getLastModifiedBy();
		if (owner == null || "".equals(owner)) {
			changeRequest.setLastModifiedBy("Unknown");
		} else {
			changeRequest.setLastModifiedBy(owner);
		}

		return changeRequest;
	}
}
