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
        while (outMsgs.size() > 0) {
          Message msg = outMsgs.remove(0);
          
          objOut.writeObject(msg);

          // Logout message means the thread should finish running
          if (msg.getType() == MessageType.Logout) {
            return;
          }
        }

        Thread.sleep(50);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }  
}
