package uk.ibm.com.oslc.blueworks;

/* 
 * Licensed Materials - Property of IBM Corporation.
 * 
 * 5725-A20
 * 
 * Copyright IBM Corporation 2013. All Rights Reserved.
 * 
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corporation.
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import uk.ibm.com.oslc.exception.UnauthorizedException;

/**
 * This Java template provides the developer an example of how you might call the Blueworks Live REST API's.
 * 
 * The template makes use of the json4j library from the Apache Wink project (http://wink.apache.org/) to decode the
 * JSON responses sent back by the api.
 * 
 * 1. Download the jar http://repo1.maven.org/maven2/org/apache/wink/wink-json4j/1.3.0/wink-json4j-1.3.0.jar
 * 
 * 2. Compile the sample (The following code assumes you are using the windows command prompt):
 *    javac -cp .;wink-json4j-1.3.0.jar RestApiClientTemplate.java
 * 
 * 3. Run it (change the credentials to something valid):
 *    java -cp .;wink-json4j-1.3.0.jar RestApiClientTemplate
 * 
 * You are free to use your own favorite JSON library.
 * 
 */
public class BlueworksRestApiClient {

	/*
	 * The Blueworks Live server to access the API's from
	 */
	private final static String REST_API_SERVER = "https://www.blueworkslive.com";

	/*
	 * The Auth API call syntax. This is an unprotected call but still requires the HTTP Basic Authentication headers to
	 * be present.
	 */
	private final static String REST_API_CALL_AUTH = REST_API_SERVER + "/api/Auth";

	/*
	 * The UserList API call syntax. This and the others EXCEPT Auth are protected by HTTP Basic Authentication.
	 */
	private final static String REST_API_CALL_FOLLOWED_PROCESSES = REST_API_SERVER + "/scr/api/FollowedProcesses";

	/*
	 * The UserList API call syntax. This and the others EXCEPT Auth are protected by HTTP Basic Authentication.
	 */
	private final static String REST_API_CALL_SEARCH = REST_API_SERVER + "/scr/api/Search";

	/*
	 * The ProcessData API call syntax. This and the others EXCEPT Auth are protected by HTTP Basic Authentication.
	 */
	private final static String REST_API_CALL_PROCESS_DATA = REST_API_SERVER + "/scr/api/ProcessData";

	/*
	 * The ActivityDocumentation API call syntax. This and the others EXCEPT Auth are protected by HTTP Basic Authentication.
	 */
	private final static String REST_API_CALL_ACTIVITY_DOCUMENTATION = REST_API_SERVER + "/scr/api/ActivityDocumentation";

	/*
	 * The username and password credentials for the user accessing the REST API's. Here we are just hardcoding the
	 * value for ease of instruction but in reality the credentials should be obtained using some other means. For
	 * example, you could prompt for them or retrieve from some external database.
	 */

	private static String api_username = "";
	private static String api_password = "";

	/*
	 * The version of the API we want to use. Different versions of the API require different input parameters and
	 * return different formats of results.
	 */
	private final static String REST_API_VERSION = "20110917";

	/*
	 * Date format string to use for BlueworksLive
	 */

	private final DateFormat BWL_DATE_FORMAT = new SimpleDateFormat("MM/dd/yy");



	/**
	 * Setup the connection to a REST API including handling the Basic Authentication request headers that must be
	 * present on every API call.
	 * 
	 * @param apiCall
	 *            the URL string indicating the api call and parameters.
	 * @return the open connection
	 */
	public static HttpURLConnection getRestApiConnection(String apiCall, String username, String password) throws IOException {

		// Call the provided api on the Blueworks Live server
		URL restApiUrl = new URL(apiCall);
		HttpURLConnection restApiURLConnection = (HttpURLConnection) restApiUrl.openConnection();

		// Add the HTTP Basic authentication header which should be present on every API call.
		addAuthenticationHeader(restApiURLConnection, username, password);

		return restApiURLConnection;
	}

	/**
	 * Add the HTTP Basic authentication header which should be present on every API call.
	 * 
	 * @param restApiURLConnection
	 *            the open connection to the REST API.
	 */
	private static void addAuthenticationHeader(HttpURLConnection restApiURLConnection, String username, String password) {
		String userPwd = username + ":" + password;
		String encoded = DatatypeConverter.printBase64Binary(userPwd.getBytes());
		restApiURLConnection.setRequestProperty("Authorization", "Basic " + encoded);
	}

	/**
	 * Validate the user to make sure that, one, they can be authenticated and two, that they have a valid status. See
	 * the Auth api documentation for all the different values for the user status and which ones you want to handle
	 * specifically.
	 * 
	 * @return the account id for the user
	 * @throws UnauthorizedException 
	 */
	public String validateUser(String username, String password) throws IOException, JSONException, UnauthorizedException {

		// Authenticate with the server
		StringBuilder authUrlBuilder = new StringBuilder(REST_API_CALL_AUTH);
		authUrlBuilder.append("?version=").append(REST_API_VERSION);
		HttpURLConnection restApiURLConnection = getRestApiConnection(authUrlBuilder.toString(),username, password);
		if (restApiURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			System.err.println("Error calling the Blueworks Live REST API.");
			throw new UnauthorizedException();
		}

		// Process the user status
		InputStream restApiStream = restApiURLConnection.getInputStream();
		try {
			JSONObject authenticateResult = new JSONObject(restApiStream);
			String userStatus = (String) authenticateResult.get("result");

			//If all normal and user has one account, do this
			if (userStatus.equals("authenticated")) {
				BlueworksRestApiClient.api_username = username;
				BlueworksRestApiClient.api_password = password;
				return (String) authenticateResult.get("selectedAccountId");
			}
			//Otherwise user either has multiple accounts or is in another state we don't recognise
			else{
				//User valid, but has multiple accounts
				//TODO: allow selection of accounts
				if (userStatus.equals("multiaccount")) {
					//Find the first account in the list and return that as default
					JSONArray accounts = (JSONArray) authenticateResult.get("accounts");
					JSONObject firstAccount = (JSONObject) accounts.get(0);

					BlueworksRestApiClient.api_username = username;
					BlueworksRestApiClient.api_password = password;
					return (String) firstAccount.getString("id");
				}
				//User in a state we don't recognise
				else{

					System.err.println("Error: User has incorrect status=" + userStatus);
					throw new UnauthorizedException();

				}

			}

		} finally {
			// Cleanup an streams we have opened
			restApiStream.close();
		}
	}
	
	// 1640 ms for the method
	public List<BlueworksProcess> getProcesses() throws UnauthorizedException{
		List<BlueworksProcess> blueworksProcesses = new ArrayList<BlueworksProcess>();
		try {
			// Validate user and determine which account to use
			String accountId = validateUser(this.api_username, this.api_password);
			if (accountId == null) {
				System.exit(1);
			}

			// Call the REST APIs. In this example we are calling the api to return the list of processes.
			StringBuilder appListUrlBuilder = new StringBuilder(REST_API_CALL_FOLLOWED_PROCESSES);
			appListUrlBuilder.append("?version=").append(REST_API_VERSION);
			// Pass the account id we are using for this user
			appListUrlBuilder.append("&accountId=").append(accountId);
			HttpURLConnection restApiURLConnection = getRestApiConnection(appListUrlBuilder.toString(), this.api_username, this.api_password);
			if (restApiURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.err.println("Error calling the Blueworks Live REST API.");
				System.exit(1);
			}

			// Process the JSON result. In this example we print the name of each process
			InputStream restApiStream = restApiURLConnection.getInputStream();
			try {
				JSONObject appListResult = new JSONObject(restApiStream);
				JSONArray processes = (JSONArray) appListResult.get("followedProcesses");
				for (Object jsonObject : processes) {
					JSONObject jsonProcess = (JSONObject)jsonObject;

					//Extract the process information from the JSON response so we can refer to it easily (we'll add the activity information in later)
					BlueworksProcess blueworksProcess = new BlueworksProcess(
							jsonProcess.getString("processId"), 
							jsonProcess.getString("name"),
							jsonProcess.getString("modifiedUserName"),
							BWL_DATE_FORMAT.parse(jsonProcess.getString("modifiedDate")));

					// Get the process activities associated with this process                	
					List<BlueworksProcessActivity> processActivities = getProcessActivitesForProcess(accountId, blueworksProcess);

					//Add the activities list to the blueworksProcess we set up
					blueworksProcess.setProcessActivities(processActivities);

					//Add the blueworks process to the list
					blueworksProcesses.add(blueworksProcess);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// Cleanup an streams we have opened
				restApiStream.close();
			}

		} catch (IOException ioe) {
			// Handle any exceptions that may occur.
			// Here you would want to perform some exception handling suited to your application which may include
			// distinguishing the type of exception and handling appropriately. For example you may want to handle
			// authentication problems separately so that the user will know their credentials caused the problem.
			System.err.println("Error: " + ioe.getMessage());
		} catch (JSONException e) {
			System.err.println("Error: " + e.getMessage());
		}
		return blueworksProcesses;
	}

	//method to search for processes given an input string
	public List<BlueworksProcess> searchProcesses(String searchTerms) throws UnauthorizedException{
		
		// TODO: Can it be actually null after many checks before?
		if (searchTerms == null)
		{
			searchTerms = "";
		}
		searchTerms = searchTerms.trim();
		//BlueWorks doesn't use * as the wildcard character, it uses % instead so replace all * with %
		searchTerms = searchTerms.replaceAll("\\*", "\\%");    	
		List<BlueworksProcess> blueworksProcesses = new ArrayList<BlueworksProcess>();
		//TODO: remove requirementsSearchResults if not used
		List<BlueworksRequirement> requirementsSearchResults = new ArrayList<BlueworksRequirement>();
		try {
			// Validate user and determine which account to use
			
			// We are exploring the time of validateUser operation
			// 625 ms for this operation
			String accountId = validateUser(BlueworksRestApiClient.api_username, BlueworksRestApiClient.api_password);
			if (accountId == null) {
				System.exit(1);
			}

			// Call the REST APIs. In this example we are calling the api to return the list of users.
			// 1015 ms for this operation
			StringBuilder appListUrlBuilder = new StringBuilder(REST_API_CALL_SEARCH);
			appListUrlBuilder.append("?version=").append("20120130");
			// Pass the account id we are using for this user
			appListUrlBuilder.append("&accountId=").append(accountId);
			appListUrlBuilder.append("&returnFields=*");
			appListUrlBuilder.append("&searchFieldName=process_name");
			appListUrlBuilder.append("&searchValue=").append(URLEncoder.encode(searchTerms,"UTF-8"));
			HttpURLConnection restApiURLConnection = getRestApiConnection(appListUrlBuilder.toString(), BlueworksRestApiClient.api_username, BlueworksRestApiClient.api_password);
			
			//System.out.println("Search Requirements REST API Call: " + appListUrlBuilder.toString());
			if (restApiURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.err.println("Error calling the Blueworks Live REST API.");
				System.exit(1);
			}

			// Process the JSON result. In this example we print the name of each user
			InputStream restApiStream = restApiURLConnection.getInputStream();
			
			try {
				JSONObject appListResult = new JSONObject(restApiStream); 	
			//	System.out.println("Here is the search appListResult: " + appListResult.toString());
				//TODO: Need to allow multiple spaces here by iterating through each space rather than assuming only one
				JSONArray spaces = (JSONArray) appListResult.get("spaces");
				for (Object jsonObject : spaces) {
					JSONObject jsonSpace = (JSONObject)jsonObject;
					JSONArray processesInSpace = (JSONArray) jsonSpace.get("processes");
					for (Object jsonInnerObject : processesInSpace) {
						JSONObject jsonProcess = (JSONObject)jsonInnerObject;
						BlueworksProcess blueworksProcess = new BlueworksProcess(
								jsonProcess.getString("id"), 
								jsonProcess.getString("name"),
								//we don't get the modified user name back so set to null
								"",
								//we don't get the modified date back from search either so set to null
								null);
						
						//Extract the activities more performantly as they're returned anyway
						//The activities are listed against each milestone so get the milestones first
						if(jsonProcess.containsKey("milestones")){


							JSONArray milestonesInProcess = (JSONArray) jsonProcess.get("milestones");
							List<BlueworksProcessActivity> processActivities = new ArrayList<BlueworksProcessActivity>();
							for (Object jsonMilestoneObject : milestonesInProcess) {
								JSONObject jsonMilestone = (JSONObject) jsonMilestoneObject;

								//Now look inside each milestone for the activities
								if(jsonMilestone.containsKey("activities")){


									JSONArray activitiesInMilestone = (JSONArray) jsonMilestone.get("activities");

									for (Object jsonActivityObject : activitiesInMilestone) {
										JSONObject jsonActivity = (JSONObject) jsonActivityObject;
										processActivities.add(new BlueworksProcessActivity(blueworksProcess.getProcessId(), jsonActivity.getString("id"), jsonActivity.getString("name"), "activity", "activity"));							
									}
								}





								//Add the activities list to the blueworksProcess we set up
								blueworksProcess.setProcessActivities(processActivities);
							}
						}
						blueworksProcesses.add(blueworksProcess);




					}
					//System.out.println("Search result: " + blueworksProcesses.toString());


					//                	BlueworksProcess blueworksProcess = new BlueworksProcess(
					//                			jsonSpace.getString("id"), 
					//                			jsonSpace.getString("name"),
					//                			//we don't get the modified user name back so set to null
					//                			"",
					//                			//we don't get the modified date back from search either so set to null
					//                			"");
					//                	blueworksProcesses.add(blueworksProcess);
					//                	System.out.println("Search result: " + blueworksProcess.toString());
				}

			} finally {
				// Cleanup an streams we have opened
				restApiStream.close();
			}

		} catch (IOException ioe) {
			// Handle any exceptions that may occur.
			// Here you would want to perform some exception handling suited to your application which may include
			// distinguishing the type of exception and handling appropriately. For example you may want to handle
			// authentication problems separately so that the user will know their credentials caused the problem.
			System.err.println("Error: " + ioe.getMessage());
		} catch (JSONException e) {
			System.err.println("Error: " + e.getMessage());
		}
		//code added above to test search

		return blueworksProcesses;
	}

	public BlueworksProcess getProcessById(String processId) throws UnauthorizedException
	{
		List<BlueworksProcess> results = getProcesses();
		BlueworksProcess foundProcess = null;
		try {
			for (BlueworksProcess blueworksProcess : results) {
				if (blueworksProcess.getProcessId() != null && blueworksProcess.getProcessId().equals(processId))
				{
					foundProcess = blueworksProcess;
				}
				// If we can't find it directly, let's look inside the child activities
				else{
					List<BlueworksProcessActivity> activities = blueworksProcess.getProcessActivities();
					for (BlueworksProcessActivity activity : activities){
						if (activity.getActivityId() != null && activity.getActivityId().equals(processId)){
							// Get the Activity Documentation first
							activity = getDocumentationForActivity(activity);

							// Even though we know it's an activity, return the whole process object
							foundProcess = blueworksProcess;
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return foundProcess;

	}


	// Private method to handle looking up the associated process activities for a given process
	private List<BlueworksProcessActivity> getProcessActivitesForProcess(String accountId, BlueworksProcess blueworksProcess) throws IOException, JSONException{

		//TODO :: move this into a new try/catch block
		// Now try to get the underlying activities associated with each process
		StringBuilder appListUrlBuilder = new StringBuilder(REST_API_CALL_PROCESS_DATA);
		appListUrlBuilder.append("?version=").append(REST_API_VERSION);
		// Pass the account id we are using for this user
		appListUrlBuilder.append("&accountId=").append(accountId);
		appListUrlBuilder.append("&processId=").append(blueworksProcess.getProcessId());
		HttpURLConnection restApiURLConnection = getRestApiConnection(appListUrlBuilder.toString(), this.api_username, this.api_password);

		// Set up a list of process activities for this process
		List<BlueworksProcessActivity> processActivities = new ArrayList<BlueworksProcessActivity>();

		//Process the response code from the call to ProcessData. For some reason it returns a 403 for Process Apps - not sure why.
		//need to be more selective in what we ignore here - probably just 403s - so ignore themif its just a 403
		if (restApiURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			if (restApiURLConnection.getResponseCode() != HttpURLConnection.HTTP_FORBIDDEN)
			{
				System.err.println("HTTP Response code from ProcessData: " + restApiURLConnection.getResponseCode());
				System.err.println("User does not have access to the process.");
			}
			//skip trying to process the activities for this process.
			return processActivities;
		}

		// Get the set of activities for this process from the input stream
		InputStream restApiStream = restApiURLConnection.getInputStream();

		JSONObject appListResult = new JSONObject(restApiStream);
		JSONObject activities = (JSONObject) appListResult.get("items");
		//System.out.println("Activities object: " + activities.toString());

		Set<String> set = activities.keySet();
		Iterator<String> iterator = set.iterator();

		// Iterate over the set of activities
		while (iterator.hasNext()){

			// First get the activity ID
			String activityId = iterator.next();
			//System.out.println("Activity Id is: " + activityId);

			// Then get the associated JSON activity from within the object
			JSONObject activity = (JSONObject) activities.get(activityId);
			//System.out.println("Activity object: " + activity.toString());

			// Now check that it's an element we're actually interested in
			// Let's get the Activity Type and Activity Major Type

			//If the activity has a type and major type, then set them

			String activityType = "";
			String activityMajorType = "";

			if (activity.containsKey("itemType")){
				activityType = activity.getString("itemType");
			}

			if (activity.containsKey("majorType")){
				activityMajorType = activity.getString("majorType");
			}

			//System.out.println("Activity Type = " + activityType);
			//System.out.println("Activity Major Type: " + activityMajorType);


			// Now determine if the activity type and major type are ones we're interested in
			//TODO - make this configurable

			if (activityType.equals("ACTIVITY") && activityMajorType.equals("activity")){
				BlueworksProcessActivity processActivity = new BlueworksProcessActivity(
						blueworksProcess.getProcessId(), 
						activityId, 
						activity.getString("name"), 
						activityType, 
						activityMajorType);

				//System.out.println("ProcessActivity to add: " + processActivity.toString());

				// Add the process activity to the list
				processActivities.add(processActivity);
			}


		}

		//System.out.println("Process Activities List Size: " + processActivities.size());
		return processActivities;

	}

	private BlueworksProcessActivity getDocumentationForActivity(BlueworksProcessActivity blueworksProcessActivity) throws IOException, JSONException, UnauthorizedException{

		// Validate user and determine which account to use
		String accountId;

		accountId = validateUser(this.api_username, this.api_password);

		if (accountId == null) {
			System.exit(1);
		}

		// Make the call to Blueworks to get the documentation for the activity
		StringBuilder appListUrlBuilder = new StringBuilder(REST_API_CALL_ACTIVITY_DOCUMENTATION);
		appListUrlBuilder.append("?version=").append(REST_API_VERSION);
		// Pass the account id we are using for this user
		appListUrlBuilder.append("&accountId=").append(accountId);
		appListUrlBuilder.append("&processId=").append(blueworksProcessActivity.getProcessId());
		appListUrlBuilder.append("&activityId=").append(blueworksProcessActivity.getActivityId());
		HttpURLConnection restApiURLConnection = getRestApiConnection(appListUrlBuilder.toString(), this.api_username, this.api_password);

		if (restApiURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			if (restApiURLConnection.getResponseCode() != HttpURLConnection.HTTP_FORBIDDEN)
			{
			    System.err.println("HTTP Response code from ProcessData: " + restApiURLConnection.getResponseCode());
			    System.err.println("User does not have access to the process.");
			}
			//skip trying to process the activities for this process.
			return blueworksProcessActivity;
		}

		// Get the set of activities for this process from the input stream
		InputStream restApiStream = restApiURLConnection.getInputStream();

		JSONObject documentationResult = new JSONObject(restApiStream);
		//System.out.println("Activities object: " + documentationResult.toString());

		// If there is a documentation field, get it and add it to the object
		if (documentationResult.containsKey("description")){
			blueworksProcessActivity.setActivityDocumentation(documentationResult.getString("description"));	
		}



		BlueworksProcessActivity processActivityWithDocumentation = blueworksProcessActivity;
		return processActivityWithDocumentation;


	}
}
