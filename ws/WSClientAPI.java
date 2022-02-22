/******************************************************************************************************************
* File:RESTClientAPI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class is used to provide access to a Node.js server. The server for this application is 
* Server.js which provides RESTful services to access a MySQL database.
*
* Parameters: None
*
* Internal Methods: None
*  String retrieveOrders() - gets and returns all the orders in the orderinfo database
*  String retrieveOrders(String id) - gets and returns the order associated with the order id
*  String sendPost(String Date, String FirstName, String LastName, String Address, String Phone) - creates a new 
*  order in the orderinfo database
*
* External Dependencies: None
******************************************************************************************************************/

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.json.JSONObject;

public class WSClientAPI
{
	/********************************************************************************
	* Description: Gets and returns all the orders in the orderinfo database
	* Parameters: None
	* Returns: String of all the current orders in the orderinfo database
	********************************************************************************/
	private boolean isAuthenticated = false;
	private String authToken = "";

	public void register(String username, String password) throws Exception {
		String url = "http://localhost:3000/api/register";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-GB,en;q=0.5");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("charset", "utf-8");
		con.setUseCaches(false);
		con.setDoOutput(true);

		String input = new JSONObject()
			.put("username", username)
			.put("password", password)
			.toString();

		OutputStream os = con.getOutputStream();
		os.write(input.getBytes());
		os.flush();

		int responseCode = con.getResponseCode();

		//Loop through the input and build the response string.
		//When done, close the stream.	
		BufferedReader in = new BufferedReader(new InputStreamReader((con.getInputStream())));
		String inputLine;		
		StringBuffer response = new StringBuffer();

		//Loop through the input and build the response string.
		//When done, close the stream.		

		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		String token = new JSONObject(response.toString()).getString("authToken");
		this.authToken = token;
		
		in.close();
		con.disconnect();
	}

	public void authenticate() throws Exception {
		System.out.println("You're not authenticated. Do you want to register or login?");
		System.out.println("1. Register");
		System.out.println("2. Login");

		Scanner keyboard = new Scanner(System.in);
		String option = keyboard.nextLine();
		System.out.println("Enter username: ");
		String username = keyboard.nextLine();

		System.out.println("Enter password: ");
		String password = keyboard.nextLine();

		if (option.strip().equals("1")) {
			this.register(username, password);
		} else {
			this.register(username, password);
		}
		this.isAuthenticated = true;
	}

	public String retrieveOrders() throws Exception
	{
		if (!this.isAuthenticated) {
			this.authenticate();
		}
		// Set up the URL and connect to the node server

		String url = "http://localhost:3000/api/orders";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//Form the request header and instantiate the response code
		con.setRequestMethod("GET");
		con.setRequestProperty("Authorization", this.authToken);
		int responseCode = con.getResponseCode();


		//Set up a buffer to read the response from the server
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		//Loop through the input and build the response string.
		//When done, close the stream.
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		return(response.toString());

	}
	
	/********************************************************************************
	* Description: Gets and returns the order based on the provided id from the
	*              orderinfo database.
	* Parameters: None
	* Returns: String of all the order corresponding to the id argument in the 
	*		   orderinfo database.
	********************************************************************************/

	public String retrieveOrders(String id) throws Exception
	{
		if (!this.isAuthenticated) {
			this.authenticate();
		}
		// Set up the URL and connect to the node server
		String url = "http://localhost:3000/api/orders/"+id;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//Form the request header and instantiate the response code
		con.setRequestMethod("GET");
		con.setRequestProperty("Authorization", this.authToken);
		int responseCode = con.getResponseCode();

		//Set up a buffer to read the response from the server
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		//Loop through the input and build the response string.
		//When done, close the stream.		

		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();

		return(response.toString());

	}

	/********************************************************************************
	* Description: Posts the new order to the orderinfo database
	* Parameters: None
	* Returns: String that contains the status of the POST operation
	********************************************************************************/

   	public String newOrder(String Date, String FirstName, String LastName, String Address, String Phone) throws Exception
	{
		if (!this.isAuthenticated) {
			this.authenticate();
		}
		// Set up the URL and connect to the node server		
		URL url = new URL("http://localhost:3000/api/orders");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// The POST parameters
		String input = "order_date="+Date+"&first_name="+FirstName+"&last_name="+LastName+"&address="+Address+"&phone="+Phone;

		//Configure the POST connection for the parameters
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", this.authToken);
        conn.setRequestProperty("Accept-Language", "en-GB,en;q=0.5");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-length", Integer.toString(input.length()));
        conn.setRequestProperty("Content-Language", "en-GB");
        conn.setRequestProperty("charset", "utf-8");
        conn.setUseCaches(false);
        conn.setDoOutput(true);

        // Set up a stream and write the parameters to the server
		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();

		//Loop through the input and build the response string.
		//When done, close the stream.	
		BufferedReader in = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		String inputLine;		
		StringBuffer response = new StringBuffer();

		//Loop through the input and build the response string.
		//When done, close the stream.		

		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		
		in.close();
		conn.disconnect();

		return(response.toString());
		
    } // newOrder

	/********************************************************************************
	 * Description: delete the order from the orderinfo database based on the provided id
	 * Parameters: None
	 * Returns: String that contains the status of the POST operation
	 ********************************************************************************/

	public String deleteOrder(String id) throws Exception{
		if (!this.isAuthenticated) {
			this.authenticate();
		}
		// Set up the URL and connect to the node server
		String url = "http://localhost:3000/api/delete-orders/"+id;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//Form the request header and instantiate the response code
		con.setRequestMethod("DELETE");
		con.setRequestProperty("Authorization", this.authToken);
		int responseCode = con.getResponseCode();

		//Set up a buffer to read the response from the server
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		//Loop through the input and build the response string.
		//When done, close the stream.

		while ((inputLine = in.readLine()) != null)
		{
			response.append(inputLine);
		}
		in.close();

		return(response.toString());
	}
} // WSClientAPI