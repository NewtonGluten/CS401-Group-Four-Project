import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ClientReader implements Runnable {
  private ObjectInputStream objIn;
  private ArrayList<Message> inMsgs;
  private List<Room> rooms;

  public ClientReader(
    ObjectInputStream objIn, 
    ArrayList<Message> inMsgs,
    List<Room> rooms
  ) {
    this.objIn = objIn;
    this.inMsgs = inMsgs;
    this.rooms = rooms;
  }

  public void run() {
    try {
      while (true) {
        try {
          // This can throw and EOFException if the client closes the connection
          // This is expected behaviour, so we catch it and break out of the loop
          Message msg = (Message) objIn.readObject();

          if (msg.getType() == MessageType.NewChat) {
            // Update the room client side
            Room room = getRoomById(msg.getRoomId());

            room.addMessage(new ChatMessage(
              msg.getUserId(),
              msg.getContents(),
              MessageStatus.Delivered
            ));
          } else {
            // The message doesn't modify the room and should be handled
            // somewhere else
            inMsgs.add(msg);
          }

          Thread.sleep(50);
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

  private Room getRoomById(String roomId) {
    for (Room room : rooms) {
      if (room.getId().equals(roomId)) {
        return room;
      }
    }

    return null;
  }
}
