import java.util.List;

public class Driver {
  public static void main(String[] args) {
	//authentication
    UserStorage userStorage = new UserStorage();
    Authenticator authenticator = new Authenticator(userStorage);
    User user = authenticator.authenticate("randomuser", "pass");
    System.out.println(user.getId() + ' ' + user.getPassword());
    
    //loading and saving
    RoomStorage roomStorage = new RoomStorage();
    /*String sampleUsers[] = {"randomuser", "anotheruser"};
    Room room = new Room(sampleUsers);
    room.addMessage(new ChatMessage("randomuser", "hi again", MessageStatus.Delivered));
    room.addMessage(new ChatMessage("anotheruser", "lo again", MessageStatus.Delivered));*/
    Logger logger = new Logger(roomStorage, userStorage);
    //logger.createRoom(room);
    logger.save();
  }
}
