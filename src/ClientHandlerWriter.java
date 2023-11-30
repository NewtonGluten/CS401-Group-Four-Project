import java.io.*;
import java.util.ArrayList;

public class ClientHandlerWriter implements Runnable {
  private ObjectOutputStream objOut;
  private UpdateManager updateManager;
  private String userId;

  public ClientHandlerWriter(
    ObjectOutputStream objOut,
    UpdateManager updateManager,
    String userId
  ) {
    this.objOut = objOut;
    this.updateManager = updateManager;
    this.userId = userId;
  }

  public void run() {
    try {
      while (true) {
        // Get and send any updates for the user
        ArrayList<Message> updatesToSend = updateManager.getUpdates(userId);

        while (updatesToSend.size() > 0) {
          objOut.writeObject(updatesToSend.remove(0));
        }

        Thread.sleep(50);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
    }
  }
}
