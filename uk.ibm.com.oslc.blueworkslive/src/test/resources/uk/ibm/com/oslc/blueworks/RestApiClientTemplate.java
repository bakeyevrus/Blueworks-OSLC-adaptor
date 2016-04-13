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

import javax.xml.bind.DatatypeConverter;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

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
public class RestApiClientTemplate {

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
    private final static String REST_API_CALL_USERLIST = REST_API_SERVER + "/scr/api/UserList";

    /*
     * The username and password credentials for the user accessing the REST API's. Here we are just hardcoding the
     * value for ease of instruction but in reality the credentials should be obtained using some other means. For
     * example, you could prompt for them or retrieve from some external database.
     */
    private final static String REST_API_USERNAME = "bakeev_rus@mail.ru";
    private final static String REST_API_PASSWORD = "device03081994";

    /*
     * The version of the API we want to use. Different versions of the API require different input parameters and
     * return different formats of results.
     */
    private final static String REST_API_VERSION = "20110917";

    /**
     * Setup the connection to a REST API including handling the Basic Authentication request headers that must be
     * present on every API call.
     * 
     * @param apiCall
     *            the URL string indicating the api call and parameters.
     * @return the open connection
     */
    public static HttpURLConnection getRestApiConnection(String apiCall) throws IOException {

        // Call the provided api on the Blueworks Live server
        URL restApiUrl = new URL(apiCall);
        HttpURLConnection restApiURLConnection = (HttpURLConnection) restApiUrl.openConnection();

        // Add the HTTP Basic authentication header which should be present on every API call.
        addAuthenticationHeader(restApiURLConnection);

        return restApiURLConnection;
    }

    /**
     * Add the HTTP Basic authentication header which should be present on every API call.
     * 
     * @param restApiURLConnection
     *            the open connection to the REST API.
     */
    private static void addAuthenticationHeader(HttpURLConnection restApiURLConnection) {
        String userPwd = REST_API_USERNAME + ":" + REST_API_PASSWORD;
        String encoded = DatatypeConverter.printBase64Binary(userPwd.getBytes());
        restApiURLConnection.setRequestProperty("Authorization", "Basic " + encoded);
    }

    /**
     * Validate the user to make sure that, one, they can be authenticated and two, that they have a valid status. See
     * the Auth api documentation for all the different values for the user status and which ones you want to handle
     * specifically.
     * 
     * @return the account id for the user
     */
    public static String validateUser() throws IOException, JSONException {

        // Authenticate with the server
        StringBuilder authUrlBuilder = new StringBuilder(REST_API_CALL_AUTH);
        authUrlBuilder.append("?version=").append(REST_API_VERSION);
        HttpURLConnection restApiURLConnection = getRestApiConnection(authUrlBuilder.toString());
        if (restApiURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            System.err.println("Error calling the Blueworks Live REST API.");
            return null;
        }

        // Process the user status
        InputStream restApiStream = restApiURLConnection.getInputStream();
        try {
            JSONObject authenticateResult = new JSONObject(restApiStream);
            String userStatus = (String) authenticateResult.get("result");
            if (!userStatus.equals("authenticated")) {
                System.err.println("Error: User has incorrect status=" + userStatus);
                return null;
            }
            return (String) authenticateResult.get("selectedAccountId");
        } finally {
            // Cleanup an streams we have opened
            restApiStream.close();
        }
    }

    public static void main(String[] args) {

        try {
            // Validate user and determine which account to use
            String accountId = validateUser();
            if (accountId == null) {
                System.exit(1);
            }

            // Call the REST APIs. In this example we are calling the api to return the list of users.
            StringBuilder appListUrlBuilder = new StringBuilder(REST_API_CALL_USERLIST);
            appListUrlBuilder.append("?version=").append(REST_API_VERSION);
            // Pass the account id we are using for this user
            appListUrlBuilder.append("&accountId=").append(accountId);
            HttpURLConnection restApiURLConnection = getRestApiConnection(appListUrlBuilder.toString());
            if (restApiURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("Error calling the Blueworks Live REST API.");
                System.exit(1);
            }

            // Process the JSON result. In this example we print the name of each user
            InputStream restApiStream = restApiURLConnection.getInputStream();
            try {
                JSONObject appListResult = new JSONObject(restApiStream);
                JSONArray users = (JSONArray) appListResult.get("users");
                for (Object user : users) {
                    System.out.println("User name=" + ((JSONObject) user).get("name"));
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
    }
}