import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientReader implements Runnable {
  private ObjectInputStream objIn;
  private ArrayList<Message> messagesReceived;

  public ClientReader(ObjectInputStream objIn, ArrayList<Message> messagesReceived) {
    this.objIn = objIn;
    this.messagesReceived = messagesReceived;
  }

  public void run() {
    try {
      while (true) {
        try {
          // This can throw and EOFException if the client closes the connection
          // This is expected behaviour, so we catch it and break out of the loop
          Message msg = (Message) objIn.readObject();

          messagesReceived.add(msg);
        } catch (EOFException e) {
          break;
        } catch (SocketException e) {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
