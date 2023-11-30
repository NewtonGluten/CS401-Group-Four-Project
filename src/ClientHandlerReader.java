import java.io.*;

public class ClientHandlerReader implements Runnable {
  private ObjectInputStream objIn;
  private UpdateManager updateManager;

  public ClientHandlerReader(ObjectInputStream objIn, UpdateManager updateManager) {
    this.objIn = objIn;
    this.updateManager = updateManager;
  }

  public void run() {
    try {
      while (true) {
        // Blocking read
        Message msg = (Message) objIn.readObject();

        // Pass any messages to the update manager
        updateManager.handleMessage(msg);

        // Breaking the loop means the thread is done running
        // In this event, the ClientHandler should carry out the logout process
        if (msg.getType() == MessageType.Logout) {
          break;
        }

        Thread.sleep(50);
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
