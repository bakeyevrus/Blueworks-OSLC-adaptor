package uk.ibm.com.oslc.rm;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.wink.json4j.JSONException;

import uk.ibm.com.oslc.blueworks.BlueworksConnector;
import uk.ibm.com.oslc.blueworks.BlueworksProcess;
import uk.ibm.com.oslc.blueworks.BlueworksProcessActivity;
import uk.ibm.com.oslc.exception.UnauthorizedException;
import uk.ibm.com.oslc.resources.Requirement;

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
	
	@Deprecated
	public abstract List<RequirementInfo> getRequirementsFromServiceProvider(String providerId) throws UnauthorizedException;
	
	@Deprecated
	//override method to allow searching
	public abstract List<RequirementInfo> getRequirementsFromServiceProvider(
			String providerId, String searchTerms) throws UnauthorizedException;
	
	public abstract String getRealm() ;

	public abstract String getToolName();

	public abstract String getPublisher() ;
	
	public abstract String validateUser(String username, String password) throws IOException, JSONException, UnauthorizedException;
	
	@Deprecated
	public abstract RequirementInfo getRequirementById(String id);
	
	public abstract List<BlueworksProcess> getBlueworksProcessesBySearchTerms (String productId, String searchTerms);
	
	public abstract BlueworksProcess getBlueworksProcessByItemId(String itemId);
	
	public abstract URI getEndpointURIForRequirement(RequirementInfo req);
	
	@Deprecated
	public abstract String getEndpointStringForRequirement(RequirementInfo req);
	
	public abstract String getEndpointStringFromBlueworksItem(BlueworksProcess process,
			String itemId);
	
	public abstract String getEndpointStringForProcess(BlueworksProcess process);
	
	public abstract String getEndpointStringForProcessActivity(BlueworksProcessActivity activity);

	public abstract String getBaseEndpoint();

	public abstract String getIdentifier();
}