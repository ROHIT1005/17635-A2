import java.rmi.*;

public interface AuthenticationServicesAI extends java.rmi.Remote
{
	String register(String username, String password) throws RemoteException;
	String login(String username, String password) throws RemoteException;
	boolean verify(String token) throws RemoteException;
}
