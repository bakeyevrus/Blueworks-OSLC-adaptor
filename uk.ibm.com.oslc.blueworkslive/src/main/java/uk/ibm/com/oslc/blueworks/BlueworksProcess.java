package uk.ibm.com.oslc.blueworks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.ibm.com.oslc.rm.RequirementInfo;

public class BlueworksProcess {
	
	private String processId = null;
	private String name = null;
	private String modifiedUserName = null;
	private Date modifiedDate = null;
	private List<BlueworksProcessActivity> processActivities;

	public BlueworksProcess(String processId, String name,
			String modifiedUserName, Date modifiedDate) {
		super();
		this.processId = processId;
		this.name = name;
		this.modifiedUserName = modifiedUserName;
		this.modifiedDate = modifiedDate;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModifiedUserName() {
		return modifiedUserName;
	}

	public void setModifiedUserName(String modifiedUserName) {
		this.modifiedUserName = modifiedUserName;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public List<BlueworksProcessActivity> getProcessActivities() {
		return processActivities;
	}

	public void setProcessActivities(
			List<BlueworksProcessActivity> processActivities) {
		this.processActivities = processActivities;
	}

	public RequirementInfo toRequirementInfo() {

		// First get any child activities into the right format

		List<RequirementInfo> activityRequirementInfos = new ArrayList<RequirementInfo>();

		// For each process activity, turn it into a requirement info object and
		// add it to our list
		for (BlueworksProcessActivity blueworksActivity : processActivities) {
			RequirementInfo blueworksActivityAsRequirementInfo = blueworksActivity
					.toRequirementInfo();
			activityRequirementInfos.add(blueworksActivityAsRequirementInfo);
		}

		// Now we'll create the master object containing the fields from the
		// process plus the child activities
		// TODO - pass through the dates properly
		RequirementInfo requirementInfo = new RequirementInfo(processId, name,
				"PROCESS", "", modifiedDate, modifiedUserName, "", "",
				activityRequirementInfos);

		return requirementInfo;

	}
}
