/******************************************************************************************************************
* File: CreateServices.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the concrete implementation of the create micro services. These services run
* in their own process (JVM).
*
* Parameters: None
*
* Internal Methods:
*  String newOrder() - creates an order in the ms_orderinfo database from the supplied parameters.
*
* External Dependencies: 
*	- rmiregistry must be running to start this server
*	= MySQL
	- orderinfo database 
******************************************************************************************************************/
import java.io.FileReader;
import java.util.Properties;
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.sql.*;
import java.io.IOException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateServices extends UnicastRemoteObject implements CreateServicesAI
{ 
    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;
    Properties registry = null;
    
    // Do nothing constructor
    public CreateServices() throws IOException {
        registry = new Properties();
        registry.load(new FileReader("registry.properties"));
    }

    private boolean authenticate(String token) {
        String entry = registry.getProperty("AuthenticationServices");
        String host = entry.split(":")[0];
        String port = entry.split(":")[1];
        try {
            Registry reg = LocateRegistry.getRegistry(host, Integer.parseInt(port));
            AuthenticationServicesAI obj = (AuthenticationServicesAI)reg.lookup("AuthenticationServices");
            boolean response = obj.verify(token);
            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Main service loop
    public static void main(String args[]) 
    { 	
    	// What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
    	// RMI port. Note that I use rebind rather than bind. This is better as it lets you start
    	// and restart without having to shut down the rmiregistry. 

        try 
        { 
            CreateServices obj = new CreateServices();

            Registry registry = Configuration.createRegistry();
            registry.bind("CreateServices", obj);

            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
            }
            // Bind this object instance to the name RetrieveServices in the rmiregistry 
            // Naming.rebind("rmi://" + Configuration.getRemoteHost() + ":1099/CreateServices", obj); 

        } catch (Exception e) {

            System.out.println("CreateServices binding err: " + e.getMessage()); 
            e.printStackTrace();
        } 

    } // main


    // Inplmentation of the abstract classes in RetrieveServicesAI happens here.

    // This method add the entry into the ms_orderinfo database

    public String newOrder(String idate, String ifirst, String ilast, String iaddress, String iphone, String token) throws RemoteException
    {
        this.authenticate(token); //verify but don't deny access to service

      	// Local declarations

        Connection conn = null;		                 // connection to the orderinfo database
        Statement stmt = null;		                 // A Statement object is an interface that represents a SQL statement.
        String ReturnString = "Order Created";	     // Return string. If everything works you get an 'OK' message
        							                 // if not you get an error string
        try
        {
            // Here we load and initialize the JDBC connector. Essentially a static class
            // that is used to provide access to the database from inside this class.

            Class.forName(JDBC_CONNECTOR);

            //Open the connection to the orderinfo database

            //System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // Here we create the queery Execute a query. Not that the Statement class is part
            // of the Java.rmi.* package that enables you to submit SQL queries to the database
            // that we are connected to (via JDBC in this case).

            stmt = conn.createStatement();
            
            String sql = "INSERT INTO orders(order_date, first_name, last_name, address, phone) VALUES (\""+idate+"\",\""+ifirst+"\",\""+ilast+"\",\""+iaddress+"\",\""+iphone+"\")";

            // execute the update

            stmt.executeUpdate(sql);

            // clean up the environment

            stmt.close();
            conn.close();
            stmt.close(); 
            conn.close();

        } catch(Exception e) {

            ReturnString = e.toString();
        }

        try{
            addLog("user", "created an order.");

        } catch (IOException e)
        {

        }

        return(ReturnString);

    } //retrieve all orders

    private static void addLog(String username, String logEntry) throws IOException {

        String filePathString = "logs" + File.separator + "logs.txt";
        File f = new File(filePathString);

        final String dir = System.getProperty("user.dir");
        Path p = Paths.get(dir + "/logs/logs.txt");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String s = System.lineSeparator() + dtf.format(now) + " : " + username + " : " + logEntry;

        if(f.exists())
        {
            try {
                Files.write(p, s.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        else
        {
            f.getParentFile().mkdirs();
            f.createNewFile();

            try {
                Files.write(p, s.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

} // RetrieveServices