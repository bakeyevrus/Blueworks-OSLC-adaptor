package uk.ibm.com.oslc.blueworks;

import uk.ibm.com.oslc.rm.RequirementInfo;

public class BlueworksProcessActivity {
    private String processId = null;
	private String activityId=null;
	private String activityName=null;
    private String activityType=null;
    private String activityMajorType=null;
    private String activityDocumentation=null;
    
    
 

	public BlueworksProcessActivity(String processId, String activityId,
    		String activityName, String activityType, String activityMajorType) {
    	super();
    	this.processId = processId;
    	this.activityId = activityId;
    	this.activityName = activityName;
    	this.activityType = activityType;
    	this.activityMajorType = activityMajorType;
    }
	
	public BlueworksProcessActivity(String processId, String activityId,
    		String activityName, String activityType, String activityMajorType, String activityDocumentation) {
    	super();
    	this.processId = processId;
    	this.activityId = activityId;
    	this.activityName = activityName;
    	this.activityType = activityType;
    	this.activityMajorType = activityMajorType;
    	this.activityDocumentation = activityDocumentation;
    }
	

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}


	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getActivityMajorType() {
		return activityMajorType;
	}

	public void setActivityMajorType(String activityMajorType) {
		this.activityMajorType = activityMajorType;
	}

	public String getActivityDocumentation() {
		return activityDocumentation;
	}

	public void setActivityDocumentation(String activityDocumentation) {
		this.activityDocumentation = activityDocumentation;
	}
	
	@Override
	public String toString() {
		return "BlueworksProcessActivity [processId=" + processId
				+ ", activityId=" + activityId + ", activityName="
				+ activityName + ", activityType=" + activityType
				+ ", activityMajorType=" + activityMajorType + "]";
	}
	
	public RequirementInfo toRequirementInfo(){
		
		RequirementInfo requirementInfo = new RequirementInfo(
				activityId, 
				activityName, 
				activityType, 
				activityDocumentation, 
				null, 
				activityMajorType, 
				processId, 
				null);
		
		return requirementInfo;
	}
    
	
}
