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
import uk.ibm.com.oslc.exception.RestException;

/**
 * This Java template provides the developer an example of how you might call
 * the Blueworks Live REST API's.
 * 
 * The template makes use of the json4j library from the Apache Wink project
 * (http://wink.apache.org/) to decode the JSON responses sent back by the api.
 * 
 * 1. Download the jar
 * http://repo1.maven.org/maven2/org/apache/wink/wink-json4j/
 * 1.3.0/wink-json4j-1.3.0.jar
 * 
 * 2. Compile the sample (The following code assumes you are using the windows
 * command prompt): javac -cp .;wink-json4j-1.3.0.jar RestApiClientTemplate.java
 * 
 * 3. Run it (change the credentials to something valid): java -cp
 * .;wink-json4j-1.3.0.jar RestApiClientTemplate
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
	 * The Auth API call syntax. This is an unprotected call but still requires
	 * the HTTP Basic Authentication headers to be present.
	 */
	private final static String REST_API_CALL_AUTH = REST_API_SERVER
			+ "/api/Auth";

	/*
	 * The UserList API call syntax. This and the others EXCEPT Auth are
	 * protected by HTTP Basic Authentication.
	 */
	private final static String REST_API_CALL_FOLLOWED_PROCESSES = REST_API_SERVER
			+ "/scr/api/FollowedProcesses";

	/*
	 * The UserList API call syntax. This and the others EXCEPT Auth are
	 * protected by HTTP Basic Authentication.
	 */
	private final static String REST_API_CALL_SEARCH = REST_API_SERVER
			+ "/scr/api/Search";

	/*
	 * The ProcessData API call syntax. This and the others EXCEPT Auth are
	 * protected by HTTP Basic Authentication.
	 */
	private final static String REST_API_CALL_PROCESS_DATA = REST_API_SERVER
			+ "/scr/api/ProcessData";

	/*
	 * The ActivityDocumentation API call syntax. This and the others EXCEPT
	 * Auth are protected by HTTP Basic Authentication.
	 */
	private final static String REST_API_CALL_ACTIVITY_DOCUMENTATION = REST_API_SERVER
			+ "/scr/api/ActivityDocumentation";

	/*
	 * The username and password credentials for the user accessing the REST
	 * API's. Here we are just hardcoding the value for ease of instruction but
	 * in reality the credentials should be obtained using some other means. For
	 * example, you could prompt for them or retrieve from some external
	 * database.
	 */

	private static String api_username = "";
	private static String api_password = "";

	/*
	 * The version of the API we want to use. Different versions of the API
	 * require different input parameters and return different formats of
	 * results.
	 */
	private final static String REST_API_VERSION_FOR_AUTH_REQUEST = "20110917";
	private final static String REST_API_VERSION_FOR_SEARCH_REQUEST = "20150930";

	/*
	 * Date format string to use for BlueworksLive
	 */

	private final DateFormat BWL_DATE_FORMAT = new SimpleDateFormat("MM/dd/yy");

	/**
	 * Setup the connection to a REST API including handling the Basic
	 * Authentication request headers that must be present on every API call.
	 * 
	 * @param apiCall
	 *            the URL string indicating the api call and parameters.
	 * @return the open connection
	 */
	public static HttpURLConnection getRestApiConnection(String apiCall,
			String username, String password) throws IOException {

		// Call the provided api on the Blueworks Live server
		URL restApiUrl = new URL(apiCall);
		HttpURLConnection restApiURLConnection = (HttpURLConnection) restApiUrl
				.openConnection();

		// Add the HTTP Basic authentication header which should be present on
		// every API call.
		addAuthenticationHeader(restApiURLConnection, username, password);

		return restApiURLConnection;
	}

	/**
	 * Add the HTTP Basic authentication header which should be present on every
	 * API call.
	 * 
	 * @param restApiURLConnection
	 *            the open connection to the REST API.
	 */
	private static void addAuthenticationHeader(
			HttpURLConnection restApiURLConnection, String username,
			String password) {
		String userPwd = username + ":" + password;
		String encoded = DatatypeConverter
				.printBase64Binary(userPwd.getBytes());
		restApiURLConnection.setRequestProperty("Authorization", "Basic "
				+ encoded);
	}

	// TODO: RB - Check it
	/**
	 * Validate the user to make sure that, one, they can be authenticated and
	 * two, that they have a valid status. See the Auth api documentation for
	 * all the different values for the user status and which ones you want to
	 * handle specifically.
	 * 
	 * @return the account id for the user
	 * @throws UnauthorizedException
	 */
	public String validateUser(String username, String password)
			throws IOException, JSONException, UnauthorizedException {

		// Authenticate with the server
		StringBuilder authUrlBuilder = new StringBuilder(REST_API_CALL_AUTH);
		authUrlBuilder.append("?version=").append(REST_API_VERSION_FOR_AUTH_REQUEST);
		HttpURLConnection restApiURLConnection = getRestApiConnection(
				authUrlBuilder.toString(), username, password);
		if (restApiURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			System.err.println("Error calling the Blueworks Live REST API.");
			throw new UnauthorizedException();
		}

		// Process the user status
		InputStream restApiStream = restApiURLConnection.getInputStream();
		try {
			JSONObject authenticateResult = new JSONObject(restApiStream);
			String userStatus = (String) authenticateResult.get("result");

			// If all normal and user has one account, do this
			if (userStatus.equals("authenticated")) {
				BlueworksRestApiClient.api_username = username;
				BlueworksRestApiClient.api_password = password;
				return (String) authenticateResult.get("selectedAccountId");
			}
			// Otherwise user either has multiple accounts or is in another
			// state we don't recognise
			else {
				// User valid, but has multiple accounts
				// TODO: allow selection of accounts
				if (userStatus.equals("multiaccount")) {
					// Find the first account in the list and return that as
					// default
					JSONArray accounts = (JSONArray) authenticateResult
							.get("accounts");
					JSONObject firstAccount = (JSONObject) accounts.get(0);

					BlueworksRestApiClient.api_username = username;
					BlueworksRestApiClient.api_password = password;
					return (String) firstAccount.getString("id");
				}
				// User in a state we don't recognise
				else {

					System.err.println("Error: User has incorrect status="
							+ userStatus);
					throw new UnauthorizedException();

				}

			}

		} finally {
			// Cleanup an streams we have opened
			restApiStream.close();
		}
	}

	private JSONObject makeApiCall(String reqURL) throws UnauthorizedException,
			RestException {
		JSONObject apiCallResult = null;
		try {
			// Validate user and determine which account to use
			String accountId = validateUser(this.api_username,
					this.api_password);
			if (accountId == null) {
				throw new UnauthorizedException(
						"The entered credentials are wrong");
			}
			String finalURL = new String(reqURL + "&accountId=" + accountId);
			HttpURLConnection restApiURLConnection = getRestApiConnection(
					finalURL, this.api_username, this.api_password);
			if (restApiURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.err.println("Request URL is: " + finalURL);
				System.err
						.println("Error calling the Blueworks Live REST API.");
				throw new RestException(restApiURLConnection.getResponseCode(),
						restApiURLConnection.getResponseMessage());
			}
			InputStream restApiStream = restApiURLConnection.getInputStream();
			try {
				apiCallResult = new JSONObject(restApiStream);
			} finally {
				restApiStream.close();
			}
		} catch (JSONException e) {
			System.err.println("Error: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
		return apiCallResult;
	}

	// method to search for processes given an input string
	public List<BlueworksProcess> searchProcesses(String searchTerms)
			throws UnauthorizedException {

		searchTerms = searchTerms.trim();
		// BlueWorks doesn't use * as the wildcard character, it uses % instead
		// so replace all * with %
		searchTerms = searchTerms.replaceAll("\\*", "\\%");

		List<BlueworksProcess> blueworksProcesses = new ArrayList<BlueworksProcess>();
		try {

			StringBuilder reqURL = new StringBuilder(REST_API_CALL_SEARCH);
			reqURL.append("?version=").append(REST_API_VERSION_FOR_SEARCH_REQUEST);
			reqURL.append("&searchFieldName=process_name");
			reqURL.append("&searchValue=").append(
					URLEncoder.encode(searchTerms, "UTF-8"));
			reqURL.append("&returnFields=output");
			reqURL.append("&includeArchived=false");

			JSONObject reqResult = makeApiCall(reqURL.toString());
			
			JSONArray spaces = (JSONArray) reqResult.get("spaces");
			for (Object jsonObject : spaces) {
				JSONObject jsonSpace = (JSONObject) jsonObject;
				JSONArray processesInSpace = (JSONArray) jsonSpace
						.get("processes");
				for (Object jsonInnerObject : processesInSpace) {
					JSONObject jsonProcess = (JSONObject) jsonInnerObject;
					BlueworksProcess blueworksProcess = new BlueworksProcess(
							jsonProcess.getString("id"),
							jsonProcess.getString("name"),
							// we don't get the modified user name back so
							// set to null
							"",
							// we don't get the modified date back from
							// search either so set to null
							null);

					// Extract the activities more performantly as they're
					// returned anyway
					// The activities are listed against each milestone so
					// get the milestones first
					if (jsonProcess.containsKey("milestones")) {

						JSONArray milestonesInProcess = (JSONArray) jsonProcess
								.get("milestones");
						List<BlueworksProcessActivity> processActivities = new ArrayList<BlueworksProcessActivity>();
						for (Object jsonMilestoneObject : milestonesInProcess) {
							JSONObject jsonMilestone = (JSONObject) jsonMilestoneObject;

							// Now look inside each milestone for the
							// activities
							if (jsonMilestone.containsKey("activities")) {

								JSONArray activitiesInMilestone = (JSONArray) jsonMilestone
										.get("activities");

								for (Object jsonActivityObject : activitiesInMilestone) {
									JSONObject jsonActivity = (JSONObject) jsonActivityObject;
									processActivities
											.add(new BlueworksProcessActivity(
													blueworksProcess
															.getProcessId(),
													jsonActivity
															.getString("id"),
													jsonActivity
															.getString("name"),
													"ACTIVITY", "activity"));
								}
							}

							// Add the activities list to the
							// blueworksProcess we set up
							blueworksProcess
									.setProcessActivities(processActivities);
						}
					}
					blueworksProcesses.add(blueworksProcess);
				} // processes loop
			} // spaces loop
		} catch (IOException ioe) {
			System.err.println("Error: " + ioe.getMessage());
		} catch (JSONException e) {
			System.err.println("Error: " + e.getMessage());
		} catch (RestException e) {
			System.err.println("Error: " + e.getMessage());
		}
		return blueworksProcesses;
	}

	public BlueworksProcess getBlueworksProcessByItemId(String itemId)
			throws UnauthorizedException {
		StringBuilder followedProcessesRequest = new StringBuilder(
				REST_API_CALL_FOLLOWED_PROCESSES).append("?");
		BlueworksProcess foundProcess = null;
		try {
			JSONObject followedProcessesResult = makeApiCall(followedProcessesRequest
					.toString());
			JSONArray processes = (JSONArray) followedProcessesResult
					.get("followedProcesses");
			for (Object jsonObject : processes) {
				JSONObject process = (JSONObject) jsonObject;

				String processId = process.getString("processId");
				List<BlueworksProcessActivity> processActivities = getProcessActivitiesForProcess(processId);

				BlueworksProcess outputProcess = new BlueworksProcess(
						processId,
						process.getString("name"),
						process.getString("modifiedUserName"),
						BWL_DATE_FORMAT.parse(process.getString("modifiedDate")));
				outputProcess.setProcessActivities(processActivities);
				// It was process
				if (processId.equals(itemId)) {

					// Obtaining process documentation
					// TODO: RB - Do we need all the activities documentation?
					// I think, no

					/*
					 * for (BlueworksProcessActivity activity :
					 * processActivities) {
					 * setDocumentationForActivity(activity); }
					 */
					foundProcess = outputProcess;
					return foundProcess;
				} else {
					for (BlueworksProcessActivity activity : processActivities) {
						if (activity.getActivityId() != null
								&& activity.getActivityId().equals(itemId)) {
							setDocumentationForActivity(activity);
							foundProcess = outputProcess;
							return foundProcess;
						}
					}
				}
			}
		} catch (RestException e) {
			System.err.println("Error: " + e.getMessage());
		} catch (JSONException e) {
			System.err.println("Error: " + e.getMessage());
		} catch (ParseException e) {
			System.err.println("Parse exception caught");
		}

		return foundProcess;
	}

	// TODO: RB - REWRITE, we have Search call!
	public BlueworksProcess getBlueworksItemById(String processId, String testVar)
			throws UnauthorizedException {

		JSONObject processData = isProcess(processId);

		if (processData != null) {
			try {
				JSONObject items = (JSONObject) processData.get("items");
				JSONObject processInfo = (JSONObject) items.get(processId);

				String processName = (String) processInfo.get("name");

				BlueworksProcess foundProcess = new BlueworksProcess(processId,
						processName);
				// setProcessActivitiesForProcess(foundProcess, processData);
			} catch (JSONException e) {
				System.err.println("Error: " + e.getMessage());
			}
		} else {

		}
		return null;
	}

	// Using not the best mechanism for checking, return ProcessData if the
	// process exists
	// TODO: RB - maybe remove after isProcess method
	public JSONObject isProcess(String blueworksItemId)
			throws UnauthorizedException {
		StringBuilder reqUrlBuilder = new StringBuilder(
				REST_API_CALL_PROCESS_DATA);
		reqUrlBuilder.append("?processId=").append(blueworksItemId);

		JSONObject apiCallResult = null;
		try {
			apiCallResult = makeApiCall(reqUrlBuilder.toString());
		} catch (RestException e) {
			/* OK */
		}
		return apiCallResult;
	}

	private List<BlueworksProcessActivity> getProcessActivitiesForProcess(
			String processId) {
		List<BlueworksProcessActivity> processActivitiesList = new ArrayList<BlueworksProcessActivity>();
		try {
			StringBuilder processDataRequest = new StringBuilder(
					REST_API_CALL_PROCESS_DATA);
			processDataRequest.append("?processId=").append(processId);
			JSONObject reqResult = makeApiCall(processDataRequest.toString());

			JSONObject activities = (JSONObject) reqResult.get("items");

			Set<String> set = activities.keySet();
			Iterator<String> iterator = set.iterator();

			// Iterate over the set of activities
			while (iterator.hasNext()) {

				// First get the activity ID
				String activityId = iterator.next();

				// Then get the associated JSON activity from within the object
				JSONObject activity = (JSONObject) activities.get(activityId);

				// Now check that it's an element we're actually interested in
				// Let's get the Activity Type and Activity Major Type

				// If the activity has a type and major type, then set them

				String activityType = "";
				String activityMajorType = "";

				if (activity.containsKey("itemType")) {
					activityType = activity.getString("itemType");
				}

				if (activity.containsKey("majorType")) {
					activityMajorType = activity.getString("majorType");
				}

				if (activityType.equals("ACTIVITY")
						&& activityMajorType.equals("activity")) {
					BlueworksProcessActivity processActivity = new BlueworksProcessActivity(
							processId, activityId, activity.getString("name"),
							activityType, activityMajorType);

					processActivitiesList.add(processActivity);
				}
			}
		} catch (RestException e) {
			System.err.println("Error: " + e.getMessage());
		} catch (JSONException e) {
			System.err.println("Error: " + e.getMessage());
		}

		return processActivitiesList;
	}

	private void setProcessActivitiesForProcess(BlueworksProcess parentProcess)
			throws JSONException, UnauthorizedException, RestException {
		StringBuilder processDataRequest = new StringBuilder(
				REST_API_CALL_PROCESS_DATA);
		processDataRequest.append("?processId=").append(
				parentProcess.getProcessId());
		JSONObject processData = makeApiCall(processDataRequest.toString());

		List<BlueworksProcessActivity> processActivities = new ArrayList<BlueworksProcessActivity>();
		JSONObject activities = (JSONObject) processData.get("items");
		Set<String> set = activities.keySet();
		Iterator<String> iterator = set.iterator();

		// Iterate over the set of activities
		while (iterator.hasNext()) {

			// First get the activity ID
			String activityId = iterator.next();

			// Then get the associated JSON activity from within the object
			JSONObject activity = (JSONObject) activities.get(activityId);

			String activityType = "";
			String activityMajorType = "";

			if (activity.containsKey("itemType")) {
				activityType = activity.getString("itemType");
			}

			if (activity.containsKey("majorType")) {
				activityMajorType = activity.getString("majorType");
			}

			// Now determine if the activity type and major type are ones we're
			// interested in
			if (activityType.equals("ACTIVITY")
					&& activityMajorType.equals("activity")) {
				BlueworksProcessActivity processActivity = new BlueworksProcessActivity(
						parentProcess.getProcessId(), activityId,
						activity.getString("name"), activityType,
						activityMajorType);
				// Getting activity documentation
				setDocumentationForActivity(processActivity);
				processActivities.add(processActivity);
			}
		}
		parentProcess.setProcessActivities(processActivities);
	}

	private void setDocumentationForActivity(BlueworksProcessActivity activity)
			throws UnauthorizedException {
		// Make the call to Blueworks to get the documentation for the activity
		StringBuilder reqURL = new StringBuilder(
				REST_API_CALL_ACTIVITY_DOCUMENTATION);
		reqURL.append("?processId=").append(activity.getProcessId());
		reqURL.append("&activityId=").append(activity.getActivityId());
		try {
			JSONObject reqResult = makeApiCall(reqURL.toString());

			// If there is a documentation field, get it and add it to the
			// object
			if (reqResult.containsKey("description")) {
				activity.setActivityDocumentation(reqResult
						.getString("description"));
			}
		} catch (JSONException e) {
			activity.setActivityDocumentation("Not avaliable");
		} catch (RestException e) {
			activity.setActivityDocumentation("Not avaliable");
		}
	}

	public static void main(String[] args) throws UnauthorizedException,
			RestException {
		BlueworksRestApiClient client = new BlueworksRestApiClient();
		client.api_username = "ruslan_bakeyev@cz.ibm.com";
		client.api_password = "1q2w3e4r";

		boolean checkProcess = false;
		if (checkProcess) {
			long startTime = System.currentTimeMillis();
			System.out.println("started");
			client.getBlueworksProcessByItemId("49eae0edea");
			long endTime = System.currentTimeMillis();
			System.out.println("ended: " + (endTime - startTime) / 1000);
			long startTime2 = System.currentTimeMillis();
			System.out.println("started2");
//			client.getProcessById("49eae0edea");
			System.out.println("ended2");
			long endTime2 = System.currentTimeMillis();
			System.out.println("ended: " + (endTime2 - startTime2) / 1000);
		} else {
			long startTime = System.currentTimeMillis();
			System.out.println("started");
			client.getBlueworksProcessByItemId("49eae0edfd");
			long endTime = System.currentTimeMillis();
			System.out.println("ended: " + (endTime - startTime) / 1000);
			long startTime2 = System.currentTimeMillis();
			System.out.println("started2");
//			client.getProcessById("49eae0edfd");
			System.out.println("ended2");
			long endTime2 = System.currentTimeMillis();
			System.out.println("ended: " + (endTime2 - startTime2) / 1000);
		}
	}
}
