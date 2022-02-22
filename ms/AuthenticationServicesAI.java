import java.rmi.*;

public interface AuthenticationServicesAI extends java.rmi.Remote
{
	//Register the user for the first time on the system
	String register(String username, String password) throws RemoteException;

	//login after the user has been registered in the system
	String login(String username, String password) throws RemoteException;

	//verify if the authtoken is valid
	boolean verify(String token) throws RemoteException;
}
