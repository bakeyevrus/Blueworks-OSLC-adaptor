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
import uk.ibm.com.oslc.rm.IRequirementsConnector;
import uk.ibm.com.oslc.rm.RequirementInfo;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugFactory;
import com.j2bugzilla.base.BugzillaException;
import com.j2bugzilla.base.ConnectionException;

//OSLC4J should give an rdf:type of oslc_cm:ChangeRequest
@OslcNamespace(Constants.REQUIREMENTS_MANAGEMENT_NAMESPACE)
@OslcName(Constants.TYPE_REQUIREMENT)
@OslcResourceShape(title = "Change Request Resource Shape", describes = Constants.TYPE_CHANGE_REQUEST)
public final class OSLCRequirement extends ChangeRequest {
	public OSLCRequirement() throws URISyntaxException {
		super();

	}

	public OSLCRequirement(URI about) throws URISyntaxException {
		super(about);

	}

	// Bugzilla extended attributes beyond OSLC base ChangeRequest
	private String requirementType = null;
	private String requirementSubType = null;
	private String parentName = null;
	private String childrenNames = "";
	private String component = null;
	private String version = null;
	private String priority = null;
	private String platform = null;
	private String operatingSystem = null;
	private String displayIndent = "";
	private String lastModifiedBy = null;

	public String getDisplayIndent() {
		return displayIndent;
	}

	public void setDisplayIndent(String displayIndent) {
		this.displayIndent = displayIndent;
	}

	@OslcDescription("The Bugzilla product definition for this change request.")
	@OslcOccurs(Occurs.ZeroOrOne)
	@OslcPropertyDefinition(Constants.BUGZILLA_NAMESPACE + "component")
	@OslcTitle("Component")
	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	@OslcDescription("The Bugzilla version for this change request.")
	@OslcOccurs(Occurs.ZeroOrOne)
	@OslcReadOnly
	@OslcPropertyDefinition(Constants.BUGZILLA_NAMESPACE + "version")
	@OslcTitle("Version")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@OslcDescription("The Bugzilla priority for this change request.")
	@OslcOccurs(Occurs.ZeroOrOne)
	@OslcPropertyDefinition(Constants.BUGZILLA_NAMESPACE + "priority")
	@OslcTitle("Priority")
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@OslcDescription("The Bugzilla platform for this change request.")
	@OslcOccurs(Occurs.ZeroOrOne)
	@OslcPropertyDefinition(Constants.BUGZILLA_NAMESPACE + "platform")
	@OslcTitle("Platform")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	@OslcDescription("The Bugzilla operating system for this change request.")
	@OslcOccurs(Occurs.ZeroOrOne)
	@OslcPropertyDefinition(Constants.BUGZILLA_NAMESPACE + "operatingSystem")
	@OslcTitle("Operating System")
	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

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
		changeRequest.setRequirementSubType(req.getSubType());
		changeRequest.setModified(req.getLastModified());
		if (req.getParent() == null || "".equals(req.getParent())) {
			changeRequest.setParentName("None");
		} else {
			// There are some problems with perfomance
			// TODO: try to optimize it!
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

	/**
	 * Creates a {@link Bug} from an OSLC-CM ChangeRequest.
	 * 
	 * @param bug
	 *            the bug
	 * @return the ChangeRequest to be serialized
	 * @throws BugzillaException
	 * @throws ConnectionException
	 * @throws InvalidDescriptionException
	 * @throws URISyntaxException
	 *             on errors setting the bug URI
	 */
	public Bug toBug() throws ConnectionException, BugzillaException {
		BugFactory factory = new BugFactory().newBug();
		if (requirementType != null) {
			factory.setProduct(requirementType);
		}
		if (this.getTitle() != null) {
			factory.setSummary(this.getTitle());
		}
		if (this.getDescription() != null) {
			factory.setDescription(this.getDescription());
		}
		if (version != null) {
			factory.setVersion(version);
		}
		if (component != null) {
			factory.setComponent(component);
		}
		if (platform != null) {
			factory.setPlatform(platform);
		}
		if (operatingSystem != null) {
			factory.setOperatingSystem(operatingSystem);
		}

		return factory.createBug();
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
}
