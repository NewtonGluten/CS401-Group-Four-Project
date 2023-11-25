import java.io.ObjectOutputStream;

public class ClientWriter implements Runnable {
  private ObjectOutputStream objOut;
  // TODO: needs access to a shared structure that contains Messages to be sent

  public ClientWriter(ObjectOutputStream objOut) {
    this.objOut = objOut;
  }

  public void run() {
    try {
      while (true) {
        // TODO: flow here should be as follows:
        // 1. Check if there are any messages to be sent
        // 2. Send those messages
        // 3. If a message happens to be the logout message, break the loop
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }  
}
