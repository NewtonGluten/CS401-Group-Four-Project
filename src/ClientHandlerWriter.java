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

        if (updatesToSend.size() > 0) {
          for (Message msg : updatesToSend) {
            objOut.writeObject(msg);
          }

          // Clear the list of updates
          // There is a possibility that an update is added to the list as
          // we are clearing it, but the throughput of the application
          // likely isn't high enough for this to be a concern
          updatesToSend.clear();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
