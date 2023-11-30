import java.io.*;
import java.net.Socket;

// ClientHandler class
public class ClientHandler implements Runnable {
  private final Socket clientSocket;
  private UserStorage users;
  private Authenticator authenticator;
  private RoomStorage rooms;
	private UpdateManager updateManager;
  private boolean is_logged_in;
  private String user_id;
  
  //TODO: review. Shouldn't the client handler have the user object rather than just having the user id?
  private User currUser;
    
  
  public ClientHandler(
		Socket client_socket,
		Authenticator authenticator,
		UserStorage users,
		RoomStorage rooms,
		UpdateManager updateManager
	) {
    this.clientSocket = client_socket;
    this.users = users;
    this.authenticator = authenticator;
    this.rooms = rooms;
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

    	//Authentication Loop
    	do {
				message = (Message) inObj.readObject();

				if (message == null) {
					return;
				}
				
				if (message.getType() == MessageType.Login) {
						//Authenticate user
						message = authenticator.authenticate(message.getUserId(), message.getPassword());

						if (message.getUser() != null) {
							currUser = message.getUser();
						}
				}

				if (currUser != null) {
					user_id = currUser.getId();
					is_logged_in = true;
	
					// Pass the successful login message to the update manager
					// Doing this update the user's status as Online for everyone else
					updateManager.handleMessage(message);

					//Send client the User object, list of Rooms they're in
					//get list of room IDs that a user is in
					//then get the list of rooms based on those IDs 
					message.setRooms(rooms.getRoomsForUser(users.getUserRooms(user_id)));
					message.setUserList(users.getUserList(user_id));
				}
				
				outObj.writeObject(message);
    	} while (!is_logged_in);

			// This is done down here because it's unnecessary to create the
			// writer thread if the user failed to log in
			ClientHandlerWriter writer = new ClientHandlerWriter(outObj, updateManager, user_id);
			Thread writerThread = new Thread(writer);
    	
			writerThread.start();

    	//Main loop
    	while (true) {
				Message msg = (Message) inObj.readObject();

				// Pass any messages to the update manager
				updateManager.handleMessage(msg);

				// User has logged out so stop execution
				if (msg.getType() == MessageType.Logout) {
					writerThread.interrupt();

					break;
				}
			}

    } catch (IOException e) {
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
				   "\n" ); 
  }
}