import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;

public class AuthenticationServices extends UnicastRemoteObject implements AuthenticationServicesAI
{ 
    private HashMap<String, String> creds = new HashMap<String, String>();
    private HashSet<String> tokens = new HashSet<String>();
    public AuthenticationServices() throws RemoteException {}

    private String generateToken(String username) {
        return "token:" + username;
    }

    public static void main(String args[]) 
    {
        try 
        { 
            AuthenticationServices obj = new AuthenticationServices();

            Registry registry = Configuration.createRegistry();
            registry.bind("AuthenticationServices", obj);

            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
            }
        } catch (Exception e) {
            System.out.println("AuthenticationServices binding err: " + e.getMessage()); 
            e.printStackTrace();
        } 

    } // main

    public String register(String username, String password) throws RemoteException
    {
        if (!this.creds.containsKey(username)) {
            this.creds.put(username, password);
            String token = generateToken(username);
            this.tokens.add(token);
            return token;
        }
        return "";
    }

    public String login(String username, String password) throws RemoteException
    {
        if (this.creds.containsKey(username)) {
            String registeredPassword = this.creds.get(username);
            if (registeredPassword.equals(password)) {
                String token = generateToken(username);
                this.tokens.add(token);
                return token;
            }
        }
        return "";
    }
    
    public boolean verify(String token) throws RemoteException
    {
        return this.tokens.contains(token);
    }
}