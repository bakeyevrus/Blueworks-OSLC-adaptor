package uk.ibm.com.oslc.blueworks;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wink.json4j.JSONException;

import uk.ibm.com.oslc.exception.UnauthorizedException;
import uk.ibm.com.oslc.rm.IRequirementsConnector;

public class BlueworksConnector extends IRequirementsConnector {

	private static final String REALM = "Blueworks";
	private static final String BLUEWORKS_BASE_URL = "http://www.blueworkslive.com/";

	private BlueworksRestApiClient client = null;

	public BlueworksConnector() {
		client = new BlueworksRestApiClient();
	}

	@Override
	public Map<String, String> getServiceProviderCatalog() {

		// TODO - maybe change this to list out different accounts for the user
		// ids....
		Map<String, String> serviceProviders = new HashMap<String, String>();
		serviceProviders.put("bwl", "Blueworks Live");
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
		return "IBM Rational UK/CZ - Technical Professionals";
	}
	
	@Override
	public String validateUser(String username, String password)
			throws IOException, JSONException, UnauthorizedException {
		return client.validateUser(username, password);
	}

	@Override
	public List<BlueworksProcess> getBlueworksProcessesBySearchTerms(
			String productId, String searchTerms) {

		if (searchTerms.isEmpty()) {
			searchTerms = "*";
		}
		try {
			return client.searchProcesses(searchTerms);
		} catch (UnauthorizedException e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}

	@Override
	public BlueworksProcess getBlueworksProcessByItemId(String itemId) {
		try {
			return client.getBlueworksProcessByItemId(itemId);
		} catch (UnauthorizedException e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	@Override
	public String getEndpointStringFromBlueworksItem(BlueworksProcess process,
			String itemId) {

		// Now check if it actually was a process
		if (process.getProcessId().equals(itemId)) {
			// If yes - then get it as a RequirementInfo object
			return getEndpointStringForProcess(process);
		} else {
			// Find the activity that matches the ID and then return that
			// instead
			for (BlueworksProcessActivity blueworksActivity : process
					.getProcessActivities()) {
				if (blueworksActivity.getActivityId().equals(itemId)) {
					return getEndpointStringForProcessActivity(blueworksActivity);
				}
			}
		}
		return "";
	}

	@Override
	public String getEndpointStringForProcess(BlueworksProcess process) {
		StringBuilder uriString = new StringBuilder(BLUEWORKS_BASE_URL);
		uriString.append("scr/processes/");
		uriString.append(process.getProcessId());

		return uriString.toString();
	}

	@Override
	public String getEndpointStringForProcessActivity(
			BlueworksProcessActivity activity) {
		StringBuilder uriString = new StringBuilder(BLUEWORKS_BASE_URL);
		uriString.append("scr/processes/").append(activity.getProcessId());
		uriString.append("?processMode=VIEW&activityID=").append(
				activity.getActivityId());
		uriString.append("#map");

		return uriString.toString();
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
