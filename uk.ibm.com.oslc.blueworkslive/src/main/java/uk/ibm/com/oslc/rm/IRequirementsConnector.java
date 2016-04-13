package uk.ibm.com.oslc.rm;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.wink.json4j.JSONException;

import uk.ibm.com.oslc.blueworks.BlueworksConnector;
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
	
	//
	public abstract Map<String, String> getServiceProviderCatalog(String username, String password);
	
	public abstract List<RequirementInfo> getRequirementsFromServiceProvider(String providerId) throws UnauthorizedException;
	
	//override method to allow searching
	public abstract List<RequirementInfo> getRequirementsFromServiceProvider(
			String providerId, String searchTerms) throws UnauthorizedException;
	
	public abstract String getRealm() ;

	public abstract String getToolName();

	public abstract String getPublisher() ;


	
	public abstract String validateUser(String username, String password) throws IOException, JSONException, UnauthorizedException;

	public abstract RequirementInfo getRequirementById(String id);
	
	public abstract URI getEndpointURIForRequirement(RequirementInfo req);

	public abstract String getEndpointStringForRequirement(RequirementInfo req);

	public abstract String getBaseEndpoint();

	public abstract String getIdentifier();
}