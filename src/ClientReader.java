import java.io.ObjectInputStream;

public class ClientReader implements Runnable {
  private ObjectInputStream objIn;
  // TODO: needs access to a shared structure that will hold Messages
  // received from the server

  public ClientReader(ObjectInputStream objIn) {
    this.objIn = objIn;
  }

  public void run() {
    try {
      while (true) {
        // Blocking read
        Message msg = (Message) objIn.readObject();

        // TODO: add the message to the shared structure
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
