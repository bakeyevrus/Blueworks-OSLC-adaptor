package uk.ibm.com.oslc.rm;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.wink.json4j.JSONException;

import uk.ibm.com.oslc.blueworks.BlueworksConnector;
import uk.ibm.com.oslc.blueworks.BlueworksProcess;
import uk.ibm.com.oslc.blueworks.BlueworksProcessActivity;
import uk.ibm.com.oslc.exception.UnauthorizedException;

public abstract class IRequirementsConnector {

	private static IRequirementsConnector instance = null;
	
	public static IRequirementsConnector getInstance()
	{
		if ( instance == null)
		{
			instance = new BlueworksConnector();
		}
		return instance;
	}
	
	public abstract Map<String, String> getServiceProviderCatalog();
	
	public abstract String getRealm() ;

	public abstract String getToolName();

	public abstract String getPublisher() ;
	
	public abstract String validateUser(String username, String password) throws IOException, JSONException, UnauthorizedException;
	
	public abstract List<BlueworksProcess> getBlueworksProcessesBySearchTerms (String productId, String searchTerms);
	
	public abstract BlueworksProcess getBlueworksProcessByItemId(String itemId);
	
	public abstract String getEndpointStringFromBlueworksItem(BlueworksProcess process,
			String itemId);
	
	public abstract String getEndpointStringForProcess(BlueworksProcess process);
	
	public abstract String getEndpointStringForProcessActivity(BlueworksProcessActivity activity);

	public abstract String getBaseEndpoint();

	public abstract String getIdentifier();
}