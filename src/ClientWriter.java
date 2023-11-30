import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ClientWriter implements Runnable {
  private ObjectOutputStream objOut;
  private ArrayList<Message> outMsgs;

  public ClientWriter(
    ObjectOutputStream objOut,
    ArrayList<Message> outMsgs
  ) {
    this.objOut = objOut;
    this.outMsgs = outMsgs;
  }

  public void run() {
    try {
      while (true) {
        boolean isLoggedOut = false;

        while (outMsgs.size() > 0) {
          Message msg = outMsgs.remove(0);

          objOut.writeObject(msg);

          // Stop sending messages if logged out
          if (msg.getType() == MessageType.Logout) {
            isLoggedOut = true;

            break;
          }
        }

        // Thread should finish running if logged out
        if (isLoggedOut) {
          return;
        }

        Thread.sleep(50);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }  
}
