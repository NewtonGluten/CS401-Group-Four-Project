import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientWriter implements Runnable {
  private ObjectOutputStream objOut;
  private ArrayList<Message> messagesToSend;
  // TODO: userId shouldn't be needed
  private String userId;

  public ClientWriter(
    ObjectOutputStream objOut,
    ArrayList<Message> messagesToSend,
    String userId
  ) {
    this.objOut = objOut;
    this.messagesToSend = messagesToSend;
    this.userId = userId;
  }

  public void run() {
    try {
      Scanner sc = new Scanner(System.in);
      String line;
      Message message = null;

      while (true) {
        // TODO: this whole block should be removed eventually
        //Prompt user
        System.out.println("\nEnter a line of text");
        line = sc.nextLine();
        
        if (line.equalsIgnoreCase("logout")) {
          message = new Message(MessageType.Logout);
          message.setUserId(userId);
          message.setUserStatus(UserStatus.Offline);
        } else {
          //Send to server as message
          message = new Message(MessageType.NewChat);
          message.setUserId(userId);
          message.setRoomId("d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
          message.setContents(line);
        }

        messagesToSend.add(message);
        // TODO: Block ends here

        if (messagesToSend.size() > 0) {
          for (Message msg : messagesToSend) {
            objOut.writeObject(msg);
  
            // Logout message means the thread should finish running
            if (msg.getType() == MessageType.Logout) {
              sc.close();

              return;
            }
  
            msg = null;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }  
}
