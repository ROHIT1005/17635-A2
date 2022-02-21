import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.FileReader;
import java.util.Properties;
import java.rmi.RemoteException; 
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.sql.*;
import java.io.IOException;

public class DeleteServices extends UnicastRemoteObject implements DeleteServicesAI {

    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";
    static final String DB_URL = Configuration.getJDBCConnection();

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;
    Properties registry = null;

    public DeleteServices() throws RemoteException, IOException {
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
            DeleteServices obj = new DeleteServices();

            Registry registry = Configuration.createRegistry();
            registry.bind("DeleteServices", obj);

            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
            }

        } catch (Exception e) {

            System.out.println("DeleteServices binding err: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public String deleteOrder(String orderid, String authToken) throws RemoteException {
        this.authenticate(authToken); //verify but don't deny access to service

        // Local declarations

        Connection conn = null;		// connection to the orderinfo database
        Statement stmt = null;		// A Statement object is an interface that represents a SQL statement.
        String ReturnString = "";

        try{

            // Here we load and initialize the JDBC connector. Essentially a static class
            // that is used to provide access to the database from inside this class.

            Class.forName(JDBC_CONNECTOR);

            //Open the connection to the orderinfo database

            //System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // Here we create the queery Execute a query. Not that the Statement class is part
            // of the Java.rmi.* package that enables you to submit SQL queries to the database
            // that we are connected to (via JDBC in this case).

            // System.out.println("Creating statement...");
            stmt = conn.createStatement();

            String sql;
            sql = "DELETE FROM orders WHERE order_id=" + orderid;

            stmt.executeUpdate(sql);

            //Clean-up environment

            stmt.close();
            conn.close();
            stmt.close();
            conn.close();

        } catch (Exception e){
            ReturnString = e.toString();
        }

        addLog("user", "deleted an order with ID: " + orderid);

        return ReturnString;
    }

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

}
