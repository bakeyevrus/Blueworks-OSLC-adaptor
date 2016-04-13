package uk.ibm.com.oslc.blueworks;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.wink.json4j.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ibm.com.oslc.exception.UnauthorizedException;
import uk.ibm.com.oslc.rm.IRequirementsConnector;
import uk.ibm.com.oslc.rm.RequirementInfo;

public class TestBlueworksConnector {

	private static String pw = "bluem1x!";
	private static String user = "steve.arnold@uk.ibm.com";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAuthenticate()
	{
		try {
			IRequirementsConnector.getInstance().validateUser(user, pw);
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetServiceProviderCatalog() {
		IRequirementsConnector connector = IRequirementsConnector.getInstance();
		Map<String,String> serviceProviders = connector.getServiceProviderCatalog("", "");
		Assert.assertTrue(serviceProviders.size()>0);
		Set<String> keys = serviceProviders.keySet();
		for (String string : keys) {
			System.out.println("process id = " + string + " name=" + serviceProviders.get(string));
		}


	}

	@Test
	public void testGetProcessById()
	{
		try {
			IRequirementsConnector.getInstance().validateUser(user, pw);
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IRequirementsConnector connector = IRequirementsConnector.getInstance();
		RequirementInfo info = connector.getRequirementById("1000006400ed473");
		Assert.assertTrue(info != null);
		Assert.assertTrue(info.getId().equals("1000006400ed473"));
	}

	@Test
	public void testGetActivityById()
	{
		try {
			IRequirementsConnector.getInstance().validateUser(user, pw);
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IRequirementsConnector connector = IRequirementsConnector.getInstance();
		RequirementInfo info = connector.getRequirementById("80000d040653f04");
		Assert.assertTrue(info != null);
		Assert.assertTrue(info.getId().equals("80000d040653f04"));
	}



	@Test
	public void testGetProcesses() throws UnauthorizedException
	{
		try {
			IRequirementsConnector.getInstance().validateUser(user, pw);
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IRequirementsConnector connector = IRequirementsConnector.getInstance();
		List<RequirementInfo> info = connector.getRequirementsFromServiceProvider("");
		Assert.assertTrue(info != null);
	}

	@Test
	public void testSearchProcesses() throws UnauthorizedException
	{
		try {
			IRequirementsConnector.getInstance().validateUser(user, pw);
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IRequirementsConnector connector = IRequirementsConnector.getInstance();
		List<RequirementInfo> info = connector.getRequirementsFromServiceProvider("", "*blueprint*");
		Assert.assertTrue(info != null);
	}


}
