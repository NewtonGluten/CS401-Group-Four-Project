import java.io.*;
import java.net.Socket;

// ClientHandler class
public class ClientHandler implements Runnable {
  private final Socket clientSocket;
  private UserStorage users;
  private Authenticator authenticator;
  private RoomStorage rooms;
  private Logger logger;
  private boolean is_logged_in;
  private String user_id;
  
  //TODO: review. Shouldn't the client handler have the user object rather than just having the user id?
  private User currUser;
    
  
  public ClientHandler(Socket client_socket, Authenticator authenticator, Logger logger, UserStorage users, RoomStorage rooms) {
    this.clientSocket = client_socket;
    this.users = users;
    this.authenticator = authenticator;
    this.rooms = rooms;
    this.logger = logger;
     
  }

  public void run() {
	  
		ObjectOutputStream outObj = null;
		ObjectInputStream inObj = null;
		
		Message message = null;
		is_logged_in = false;
		currUser = null;
		
		//TODO: remove
		printDebug();
    
    try {
    	
    	//Generate input and output stream objects.
    	outObj = new ObjectOutputStream(clientSocket.getOutputStream());
    	inObj = new ObjectInputStream(clientSocket.getInputStream());
    	
    	
    	//Authentication Loop
    	do {
				message = (Message) inObj.readObject();
				
				if (message.getType() == MessageType.Login) {
						//Authenticate user
						currUser = authenticator.authenticate(message.getUserId(), message.getPassword());
				}
				
				if (currUser != null) {
					user_id = currUser.getId();
					is_logged_in = true;
				}
    	} while (!is_logged_in);
    	
    	//Send client all of its relevant user data
    	message = new Message(MessageType.Login);
    	message.setContents("Login Success");
    	message.setUserId(user_id);
    	
    	//get list of room IDs that a user is in
    	//then get the list of rooms based on those IDs 
    	message.setRooms(rooms.getRoomsForUser(users.getUserRooms(user_id)));
    	
    	outObj.writeObject(message);
    	
    	 
    	//Main loop
    	do {
				message = (Message) inObj.readObject();
				MessageType type = message.getType();
				
				switch (type) {
					case Logout:
						//TODO: remove debug message
						System.out.println("Logout message recieved");
						is_logged_in = false;
						message = new Message(MessageType.Logout);
						message.setContents("::Server:: recieved client logout request.");
							
						outObj.writeObject(message);
						break;
						
					case NewChat:
						//TODO: remove debug message
						System.out.println("new chat message recieved");
						
						String contents = message.getContents();
						System.out.println(contents);
						message = new Message(MessageType.NewChat);
						message.setContents("\t" + contents);
						
						break;
						
					case CreateRoom:
						//TODO: remove debug message
						System.out.println("create room message recieved");
						
						break;
						
					case LeaveRoom:
						//TODO: remove debug message
						System.out.println("leave room message recieved");
						break;
						
					case AddToRoom:
						//TODO: remove debug message
						System.out.println("add to room message recieved"); 
						break;
						
					case ChangeStatus:
						//TODO: remove debug message
						System.out.println("change status message recieved"); 
						break;
						
					case UpdateUserStatus:
						//TODO: remove debug message
						System.out.println("update user status message recieved");
						break;
						
					default: break;
				}
			//Check to see if any server side data structures have been changed
			
			

			
			//Create message with updated data structures and send them back down the pipe
    		
    		
    		
    	} while (is_logged_in);

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