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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;

import uk.ibm.com.oslc.Constants;
import uk.ibm.com.oslc.blueworks.BlueworksProcess;
import uk.ibm.com.oslc.blueworks.BlueworksProcessActivity;
import uk.ibm.com.oslc.rm.IRequirementsConnector;

//OSLC4J should give an rdf:type of oslc_rm:Requrements
@OslcNamespace(Constants.REQUIREMENTS_MANAGEMENT_NAMESPACE)
@OslcName(Constants.REQUIREMENT)
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
}
