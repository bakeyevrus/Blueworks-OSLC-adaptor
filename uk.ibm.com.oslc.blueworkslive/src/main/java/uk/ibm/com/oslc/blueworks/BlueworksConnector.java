package uk.ibm.com.oslc.blueworks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wink.json4j.JSONException;

import uk.ibm.com.oslc.exception.UnauthorizedException;
import uk.ibm.com.oslc.rm.IRequirementsConnector;
import uk.ibm.com.oslc.rm.RequirementInfo;

public class BlueworksConnector extends IRequirementsConnector {

	private static final String REALM = "Blueworks";
	private static final String BLUEWORKS_BASE_URL = "http://www.blueworkslive.com/";
	
	private BlueworksRestApiClient client = null;
	
	public BlueworksConnector() {
		client = new BlueworksRestApiClient();
	}
	@Override
	public Map<String,String> getServiceProviderCatalog(String username,
			String password) {

		// TODO - maybe change this to list out different accounts for the user ids....
		Map<String,String> serviceProviders = new HashMap<String,String>();
		serviceProviders.put("1", "Blueworks Live Processes");
		return serviceProviders;
	}
	@Override
	public String getRealm() {
		return REALM;
	}
	@Override
	public String getToolName() {
		return "Blueworks Live";
	}
	@Override
	public String getPublisher() {
		return "IBM Rational UK - Technical Professionals";
	}
	@Override
	public List<RequirementInfo> getRequirementsFromServiceProvider(String providerId) throws UnauthorizedException {
		// ok so the service provider catalog for blueworks is spaces ( i think)
		// we need to get all the spaces this userid is following
		List<BlueworksProcess> processes = client.getProcesses();
		
		List<RequirementInfo> requirements = new ArrayList<RequirementInfo>();
		for (Iterator<BlueworksProcess> iterator = processes.iterator(); iterator.hasNext();) {
			BlueworksProcess blueworksProcess = (BlueworksProcess) iterator.next();
			//String name = blueworksProcess.getName();
			//String id = blueworksProcess.getProcessId();
			//requirements.add(new RequirementInfo(id,name,"Process"));
			
			//Let's first convert all of the ProcessActivities
			List<RequirementInfo> activities = new ArrayList<RequirementInfo>();
			for (Iterator<BlueworksProcessActivity> activitiesIterator = blueworksProcess.getProcessActivities().iterator(); activitiesIterator.hasNext();){
				BlueworksProcessActivity blueworksProcessActivity = (BlueworksProcessActivity) activitiesIterator.next();
				
				// Convert each blueworks activity into a requirementinfo and put it in the list
				activities.add(blueworksProcessActivity.toRequirementInfo());
							
				
			}
			
			// Now let's create the new RequirementInfo object, add in the process level data and add the activities list as the children RequirementInfo objects
			//TODO - sort out passing the modified date through from Blueworks to the RequirementInfo obect
			requirements.add(new RequirementInfo(
					blueworksProcess.getProcessId(), 
					blueworksProcess.getName(), 
					"process", 
					"dummy documentation", 
					blueworksProcess.getModifiedDate(), 
					blueworksProcess.getModifiedUserName(),
					"process", 
					"", 
					activities));
			
		}		
		return requirements;
	}
	
	@Override
	public List<RequirementInfo> getRequirementsFromServiceProvider(String providerId, String searchTerms) throws UnauthorizedException {
		//System.out.println("Entering the search specific requirements method");
		// ok so the service provider catalog for blueworks is spaces ( i think)
		// we need to get all the spaces this userid is following
		if (searchTerms !=null)
		{
			searchTerms = searchTerms.trim();
			System.out.println("The product id is: " + providerId + "\nTerms are: " + searchTerms);
		}
		List<BlueworksProcess> processes = client.searchProcesses(searchTerms);
		
		List<RequirementInfo> requirements = new ArrayList<RequirementInfo>();
		for (Iterator<BlueworksProcess> iterator = processes.iterator(); iterator.hasNext();) {
			BlueworksProcess blueworksProcess = (BlueworksProcess) iterator.next();
			//String name = blueworksProcess.getName();
			//String id = blueworksProcess.getProcessId();
			//requirements.add(new RequirementInfo(id,name,"Process"));
			
			//Let's first convert all of the ProcessActivities
			List<RequirementInfo> activities = new ArrayList<RequirementInfo>();
			for (Iterator<BlueworksProcessActivity> activitiesIterator = blueworksProcess.getProcessActivities().iterator(); activitiesIterator.hasNext();){
				BlueworksProcessActivity blueworksProcessActivity = (BlueworksProcessActivity) activitiesIterator.next();
				
				// Convert each blueworks activity into a requirementinfo and put it in the list
				activities.add(blueworksProcessActivity.toRequirementInfo());
							
				
			}
			
			// Now let's create the new RequirementInfo object, add in the process level data and add the activities list as the children RequirementInfo objects
			//TODO - sort out passing the modified date through from Blueworks to the RequirementInfo object
			requirements.add(new RequirementInfo(
					blueworksProcess.getProcessId(), 
					blueworksProcess.getName(), 
					"process", 
					"dummy documentation", 
					blueworksProcess.getModifiedDate(),
					blueworksProcess.getModifiedUserName(),
					"process", 
					"", 
					activities));
		}
		return requirements;
	}

	@Override
	public String validateUser(String username, String password) throws IOException, JSONException, UnauthorizedException {
		return client.validateUser(username, password);
	}
	@Override
	public RequirementInfo getRequirementById(String processId) {
		RequirementInfo info = null;
					
			try {
				// Let's try and get the process associated with the processId. Note that the processId may actually be an activity Id. We'll get the parent process back along with the associated child processes.
				BlueworksProcess process = client.getProcessById(processId);
				
				// Now check if it actually was a process
				if (process !=null && process.getProcessId().equals(processId)){
					// If yes - then get it as a RequirementInfo object
					info = process.toRequirementInfo();				
					
				}
				else{
					// Find the activity that matches the ID and then return that instead
					for(BlueworksProcessActivity blueworksActivity : process.getProcessActivities()){
			    		
						if(blueworksActivity.getActivityId().equals(processId)){
							info = blueworksActivity.toRequirementInfo();
							break;
						}
			    	}
					
				}
				
			} catch (UnauthorizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return info;
	}
	@Override
	public URI getEndpointURIForRequirement(RequirementInfo req) {
		URI addressOfRequirementOnEndpoint = null;
		try {
			addressOfRequirementOnEndpoint = new URI(getEndpointStringForRequirement(req));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return addressOfRequirementOnEndpoint;
	}
	@Override
	public String getEndpointStringForRequirement(RequirementInfo req) { 
		String uriString = BLUEWORKS_BASE_URL + "scr/home";
		if (req.getType() != null && "activity".equals(req.getType().toLowerCase()))
		{
			uriString = BLUEWORKS_BASE_URL +"scr/processes/" + req.getParent() + "?processMode=VIEW&activityID=" + req.getId() + "#map";
		}
		else if (req.getType() !=null && "process".equals(req.getType().toLowerCase()))
		{
			uriString = BLUEWORKS_BASE_URL + "scr/processes/" + req.getId();
		}
		return uriString;
	}
	@Override
	public String getBaseEndpoint() {
		return BLUEWORKS_BASE_URL;
	}
	@Override
	public String getIdentifier() {
		return "uk.ibm.com.oslc.blueworks";
	}
}
