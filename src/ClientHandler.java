import java.io.*;
import java.net.Socket;

// ClientHandler class
public class ClientHandler implements Runnable {
  private final Socket clientSocket;
  private UserStorage users;
  private Authenticator authenticator;
  private RoomStorage rooms;
  private Logger logger;
	private UpdateManager updateManager;
  private boolean is_logged_in;
  private String user_id;
  
  //TODO: review. Shouldn't the client handler have the user object rather than just having the user id?
  private User currUser;
    
  
  public ClientHandler(
		Socket client_socket,
		Authenticator authenticator,
		Logger logger,
		UserStorage users,
		RoomStorage rooms,
		UpdateManager updateManager
	) {
    this.clientSocket = client_socket;
    this.users = users;
    this.authenticator = authenticator;
    this.rooms = rooms;
    this.logger = logger;
    this.updateManager = updateManager;
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

			ClientHandlerReader reader = new ClientHandlerReader(inObj, updateManager);
			ClientHandlerWriter writer = new ClientHandlerWriter(outObj, updateManager, user_id);
			Thread readerThread = new Thread(reader);
			Thread writerThread = new Thread(writer);
    	
    	
    	//Authentication Loop
    	do {
				message = (Message) inObj.readObject();
				
				if (message.getType() == MessageType.Login) {
						//Authenticate user
						currUser = authenticator.authenticate(message.getUserId(), message.getPassword());
				}

				// User is null if authentication failed, so try again
				if (currUser == null) {
					// A login message with a null user means authentication failed
					message = new Message(MessageType.Login);
				} else {
					user_id = currUser.getId();
					is_logged_in = true;
	
					// Pass the successful login message to the update manager
					// Doing this update the user's status as Online for everyone else
					updateManager.handleMessage(message);

					//Send client the User object, list of Rooms they're in
					// TODO: send the entire user list too
					message = new Message(MessageType.Login);
	
					//get list of room IDs that a user is in
					//then get the list of rooms based on those IDs 
					message.setRooms(rooms.getRoomsForUser(users.getUserRooms(user_id)));
					message.setUser(currUser);
				}
				
				outObj.writeObject(message);
    	} while (!is_logged_in);
    	
			//Start reader and writer threads
			readerThread.start();
			writerThread.start();

    	//Main loop
    	while (true) {
				// Busy wait until the reader thread is done, which means the client
				// has logged out
				if (!readerThread.isAlive()) {
					// Stop the writer thread
					writerThread.interrupt();

					break;
				}
			}

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