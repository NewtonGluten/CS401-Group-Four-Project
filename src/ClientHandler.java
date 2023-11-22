import java.io.*;
import java.net.Socket;

// ClientHandler class
public class ClientHandler implements Runnable {
  private final Socket clientSocket;
  private UserStorage users;
  private Authenticator authenticator;
  private RoomStorage rooms;
  // TODO: uncomment logger. 
  // private Logger logger;
  private boolean is_logged_in;
  private String user_id;
  
  //TODO: review. Shouldn't the client handler have the user object rather than just having the user id?
  private User currUser;
    
  
  //public ClientHandler(Socket client_Socket, Authenticator authenticator, Logger logger, UserStorage users, RoomStorage rooms) {
  public ClientHandler(Socket client_socket, Authenticator authenticator, UserStorage users, RoomStorage rooms) {
    this.clientSocket = client_socket;
    this.users = users;
    this.authenticator = authenticator;
    this.rooms = rooms;
    //this.logger = logger;
    
  }

  public void run() {
	  
		ObjectOutputStream outObj = null;
		ObjectInputStream inObj = null;
		
		Message message = null;
		is_logged_in = false;
		currUser = null;
    
    try {
    	
    	//Generate input and output stream objects.
    	outObj = new ObjectOutputStream(clientSocket.getOutputStream());
    	inObj = new ObjectInputStream(clientSocket.getInputStream());
    	
    	//Authentication Loop
    	do {
    		//See if message came in
    		if (inObj.available() > 0) {
        		message = (Message) inObj.readObject();
        		
        		//Authenticate user
        		currUser = authenticator.authenticate(message.getUserId(), message.getPassword());
    		}
    		
    		if (currUser != null) {
    			is_logged_in = true;
    		}

    	} while (!is_logged_in);
    	
    	
    	
    	
    	
    	

    } catch (IOException e) {
    	e.printStackTrace();
    } catch (ClassNotFoundException e) {
    	e.printStackTrace();
    } finally {
      try {
        if (outObj != null) {
          outObj.close();
        }
        if (inObj != null) {
          inObj.close();
          clientSocket.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  void printDebug() {
	    System.out.println("users: " + users.hashCode() + "\n" +
				   "authenticator: " + authenticator.hashCode() + "\n" +
				   "rooms: " + rooms.hashCode() + "\n" +
				   //"logger: " + logger.hashCode() + "\n" +
				   "\n" ); 
  }
}