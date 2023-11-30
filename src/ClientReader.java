import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.List;

import javax.swing.*;

public class ClientReader implements Runnable {
  private ObjectInputStream objIn;
  private List<Room> rooms;
  private List<User> users;
  private JList<String> roomsDisplay;
  private JTextArea msgDisplay;
  private JTextArea usersDisplay;
  private JTextArea logDisplay;

  public ClientReader(
    ObjectInputStream objIn, 
    List<Room> rooms,
    List<User> users,
    JList<String> roomsDisplay,
    JTextArea msgDisplay,
    JTextArea usersDisplay,
    JTextArea logDisplay
  ) {
    this.objIn = objIn;
    this.rooms = rooms;
    this.users = users;
    this.roomsDisplay = roomsDisplay;
    this.msgDisplay = msgDisplay;
    this.usersDisplay = usersDisplay;
    this.logDisplay = logDisplay;
  }

  public void run() {
    try {
      while (true) {
        try {
          // This can throw and EOFException if the client closes the connection
          // This is expected behaviour, so we catch it and break out of the loop
          Message msg = (Message) objIn.readObject();

          switch (msg.getType()) {
            case NewChat:
              // Update the room client side
              Room room = getRoomById(msg.getRoomId());

              room.addMessage(new ChatMessage(
                msg.getUserId(),
                msg.getContents(),
                MessageStatus.Delivered
              ));

              // Update the message display if the current room is the room
              // that the message was sent to
              if (room.getId().equals(roomsDisplay.getSelectedValue())) {
                String chat = "[" + msg.getTimestamp() + "] "
                  + msg.getUserId() + ": "
                  + msg.getContents() + "\n";

                msgDisplay.append(chat);
              }

              break;
            case NewRoom:
              // Update the room client side
              List<Room> newRooms = msg.getRooms();

              for (Room newRoom : newRooms) {
                rooms.add(newRoom);
              }

              updateRoomsDisplay();

              break;
            case LeaveRoom:
              // Update the room client side
              Room roomUserLeft = getRoomById(msg.getRoomId());

              roomUserLeft.removeUser(msg.getUserId());
              
              // if the current room is the room that the user left,
              // update the user display and show a message
              if (roomUserLeft.getId().equals(roomsDisplay.getSelectedValue())) {
                msgDisplay.append(msg.getUserId() + " left the room\n");
                updateUserDisplay(roomUserLeft);
              }

              break;
            case UpdateUserStatus:
              // Update the user client side
              String userId = msg.getUserId();

              for (User user : users) {
                if (user.getId().equals(userId)) {
                  user.setStatus(msg.getUserStatus());
                }
              }

              // Update the user display if the current room is the room
              // that the user is in
              Room currentRoom = getRoomById(roomsDisplay.getSelectedValue());

              if (currentRoom != null && currentRoom.getUsers().contains(userId)) {
                updateUserDisplay(currentRoom);
              }

              break;
            case GetLogs:
              if (logDisplay != null) {
                logDisplay.setText(msg.getContents());
              }

              break;
            default:
              break;
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

  private void updateRoomsDisplay() {
    String[] roomIds = new String[rooms.size()];

    for (int i = 0; i < rooms.size(); i++) {
      roomIds[i] = rooms.get(i).getId();
    }

    roomsDisplay.setListData(roomIds);
  }

  String getUserWithStatus(String userId) {
    for (User user : users) {
      if (user.getId().equals(userId)) {
        String status = "Online";

        switch (user.getStatus()) {
          case Away:
            status = "Away";
            break;
          case Busy:
            status = "Busy";
            break;
          case Offline:
            status = "Offline";
            break;
          default:

        }

        return user.getId() + " [" + status + "]";
      }
    }

    return userId;
  }

  private void updateUserDisplay(Room room) {
    usersDisplay.setText("");

    List<String> users = room.getUsers();

    for (String user : users) {
      usersDisplay.append(getUserWithStatus(user) + "\n");
    }
  }
}
