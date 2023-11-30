import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

// Server class
class Server {
  public static void main(String[] args) {
    ServerSocket server = null;
    
    // Create Data structures. 
    UserStorage users = new UserStorage();
    Authenticator authenticator = new Authenticator(users);
    RoomStorage rooms = new RoomStorage();
    Logger logger = new Logger(rooms, users);
    UpdateManager updateManager = new UpdateManager(rooms, users, logger);
    
    try {

      // server is listening on port 1234
      server = new ServerSocket(4014);
      server.setReuseAddress(true);

      createWindow(server, logger);

      // running infinite loop for getting
      // client request
      while (true) {

        // socket object to receive incoming client
        // requests
        Socket client = null;

        try {
          client = server.accept();
        } catch (SocketException e) {
          // Server was closed
          break;
        }
        
        
        //TODO: remove debugging code
        System.out.println("New client connected " + client + "\n");
        
        
        // create a new thread object
        ClientHandler clientSock = new ClientHandler(
          client,
          authenticator,
          logger,
          users,
          rooms,
          updateManager
        );

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
          logger.save();
          System.exit(0);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void createWindow(ServerSocket socket, Logger logger) {
    JFrame window = new JFrame("Server Application");
    JPanel panel = new JPanel();
    JButton saveBtn = new JButton("Save");
    JButton shutdownBtn = new JButton("Shutdown");

    saveBtn.addActionListener(e -> {
      logger.save();
    });

    shutdownBtn.addActionListener(e -> {
      try {
        socket.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }

      logger.save();

      System.exit(0);
    });

    panel.add(saveBtn);
    panel.add(shutdownBtn);
    
    window.setMinimumSize(new java.awt.Dimension(400, 100));
    window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    window.setLocationRelativeTo(null);
    window.add(panel);
    window.pack();
    window.setVisible(true);
  }
}
