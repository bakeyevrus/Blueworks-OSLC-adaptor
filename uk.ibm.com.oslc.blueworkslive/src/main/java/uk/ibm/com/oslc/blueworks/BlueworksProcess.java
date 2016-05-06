package uk.ibm.com.oslc.blueworks;

import java.util.Date;
import java.util.List;

public class BlueworksProcess {
	
	private static String TYPE = "PROCESS";
	
	private String processId = null;
	private String name = null;
	private String modifiedUserName = null;
	private Date modifiedDate = null;
	private List<BlueworksProcessActivity> processActivities;
	
	public BlueworksProcess(String processId, String processName) {
		super();
		this.processId = processId;
		this.name = processName;
		this.modifiedUserName = "Unknown";
		this.modifiedDate = null;
	}
	
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
	
	public String getType() {
		return TYPE;
	}
}
