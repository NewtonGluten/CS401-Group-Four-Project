import java.io.*;
import java.net.*;

// Server class
class Server {
  public static void main(String[] args) {
    ServerSocket server = null;
    
    // Create Data structures. 
    UserStorage users = new UserStorage();
    Authenticator authenticator = new Authenticator(users);
    RoomStorage rooms = new RoomStorage();
    // TODO: create logger. 
    // Logger logger = new Logger(rooms);
    
    //TODO: remove debug print
    System.out.println("users: " + users.hashCode() + "\n" +
    				   "authenticator: " + authenticator.hashCode() + "\n" +
    				   "rooms: " + rooms.hashCode() + "\n" +
    				   //"logger: " + logger.hashCode() + "\n" +
    				   "\n" );
    

    try {

      // server is listening on port 1234
      server = new ServerSocket(1234);
      server.setReuseAddress(true);

      // running infinite loop for getting
      // client request
      while (true) {

        // socket object to receive incoming client
        // requests
        Socket client = server.accept();
        
        
        //TODO: remove debugging code
        System.out.println("New client connected " + client + "\n");
        
        
        // create a new thread object
        //ClientHandler clientSock = new ClientHandler(client, authenticator, logger, users, rooms);
        ClientHandler clientSock = new ClientHandler(client, authenticator, users, rooms);

        // This thread will handle the client
        // separately
        new Thread(clientSock).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (server != null) {
        try {
          server.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
