import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

enum UpdateType {
  CreateRoom,
  ModifyRoom,
  ModifyUserList,
}

public class UpdateManager {
  private RoomStorage roomStorage;
  private UserStorage userStorage;
  private Logger logger;
  // Map of user id to a list of updates for that user
  private HashMap<String, ArrayList<Message>> updatesByUserId;

  public UpdateManager(RoomStorage roomStorage, UserStorage userStorage, Logger logger) {
    this.roomStorage = roomStorage;
    this.userStorage = userStorage;
    this.logger = logger;
    this.updatesByUserId = new HashMap<String, ArrayList<Message>>();

    // Create an empty list of updates for each user
    for (String userId : userStorage.getAllUsers()) {
      updatesByUserId.put(userId, new ArrayList<Message>());
    }
  }

  private String getTime() {
    return new SimpleDateFormat("MM-dd-yy HH:mm").format(new Date());
  }

  public void handleMessage(Message message) {
    String sender = message.getUserId();
    Message update = null;

    switch (message.getType()) {
      case Login:
      case Logout:
      case ChangeStatus:
        UserStatus userStatus = message.getUserStatus();

        // Update the user's status on the server first
        updateUserStatus(sender, userStatus);

        // Create a new UpdateUserStatus message that will
        // be sent to all other users
        update = new Message(MessageType.UpdateUserStatus);
        update.setUserId(sender);
        update.setUserStatus(userStatus);

        addUpdate(sender, update, UpdateType.ModifyUserList);

        break;
      case NewChat:
        // Update the room on the server first
        addMessageToRoom(message);

        // Create a new NewChat message that will be sent to all other users
        update = new Message(MessageType.NewChat);
        update.setUserId(sender);
        update.setContents(message.getContents());
        update.setRoomId(message.getRoomId());
        update.setTimestamp(getTime());

        addUpdate(sender, update, UpdateType.ModifyRoom);

        break;
      case CreateRoom:
        // Create the new room
        Room room = new Room(message.getUsers(), message.getContents());
        List<Room> roomsToSend = new ArrayList<Room>();

        // Add the new room to the server
        logger.createRoom(room);

        // Create a NewRoom message that will be sent to each user
        // that is in the new room
        roomsToSend.add(room);
        update = new Message(MessageType.NewRoom);
        update.setRooms(roomsToSend);
        update.setUserId(sender);
        update.setUsers(message.getUsers());

        addUpdate(sender, update, UpdateType.CreateRoom);

        break;
      case AddToRoom:
        logger.addUserToRoom(message.getContents(), message.getRoomId());

        // That user needs that room to be sent to them
        Room roomToSend = roomStorage.getRoomById(message.getRoomId());
        List<Room> roomsToSendToUser = new ArrayList<Room>();
        roomsToSendToUser.add(roomToSend);
        Message roomUpdate = new Message(MessageType.NewRoom);
        roomUpdate.setRooms(roomsToSendToUser);
        updatesByUserId.get(message.getContents()).add(roomUpdate);

        // And then send the user to everyone else in the room
        update = new Message(MessageType.AddToRoom);
        update.setUserId(sender);
        update.setContents(message.getContents());
        update.setRoomId(message.getRoomId());
        update.setTimestamp(getTime());

        for (String userId : roomToSend.getUsers()) {
          User user = userStorage.getUserById(userId);
          UserStatus status = user.getStatus();

          if (!userId.equals(message.getContents()) && status != UserStatus.Offline) {
            updatesByUserId.get(userId).add(update);
          }
        }

        break;
      case LeaveRoom:
        logger.removeUserFromRoom(sender, message.getRoomId());

        // Create a new AddToRoom or LeaveRoom message that will be sent to
        // all other users in the room
        update = new Message(message.getType());
        update.setUserId(sender);
        update.setContents(message.getContents());
        update.setRoomId(message.getRoomId());
        update.setTimestamp(getTime());

        addUpdate(sender, update, UpdateType.ModifyRoom);

        break;
      case GetLogs:
        String log = "";

        if (message.getContents() != null) {
          log = logger.getLogsForUser(message.getContents());
        } else if (message.getRoomId() != null) {
          log = logger.getLogForRoom(message.getRoomId());
        }

        update = new Message(MessageType.GetLogs);
        update.setContents(log);

        updatesByUserId.get(sender).add(update);

        break;
      default:
        break;
    }
  }

  private void addUpdate(String sender, Message update, UpdateType type) {
    // The update type determines which users will receive the update
    List<String> usersToSendTo = null;

    switch (type) {
      case CreateRoom:
        // Users who will be part of the new room should receive the update
        usersToSendTo = update.getUsers();

        break;
      case ModifyRoom:
        // Get the room first
        Room room = roomStorage.getRoomById(update.getRoomId());

        // Uesrs in the room should receive the update
        usersToSendTo = room.getUsers();

        break;
      case ModifyUserList:
        // All users should receive the update
        // Necessary to cast to ArrayList because getAllUsers returns a Set
        usersToSendTo = new ArrayList<String>(userStorage.getAllUsers());

        break;
      default:
        break;
    }

    if (update.getType() == MessageType.UpdateUserStatus
      && update.getUserId().equals(sender)
    ) {
      // Don't send the update to the user who sent it
      usersToSendTo.remove(sender);
    }

    // Add the update to the list of updates for each user, except the sender
    for (String userId : usersToSendTo) {
      User user = userStorage.getUserById(userId);
      UserStatus userStatus = user.getStatus();

      if (userStatus != UserStatus.Offline) {
        updatesByUserId.get(userId).add(update);
      }
    }
  }

  // Called when a new message needs to be added to the room on the server
  public void addMessageToRoom(Message message) {
    Room room = roomStorage.getRoomById(message.getRoomId());

    room.addMessage(new ChatMessage(
      message.getUserId(),
      message.getContents(),
      // any message sent to the server is considered delivered
      MessageStatus.Delivered
    ));
  }

  // Called when a user's status needs to be updated on the server
  public void updateUserStatus(String userId, UserStatus userStatus) {
    User user = userStorage.getUserById(userId);

    user.setStatus(userStatus);
  }

  public ArrayList<Message> getUpdates(String userId) {
    return updatesByUserId.get(userId);
  }
}
