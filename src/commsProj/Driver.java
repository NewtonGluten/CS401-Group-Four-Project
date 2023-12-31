package commsProj;
import java.util.List;

public class Driver {
  public static void main(String[] args) {
	//authentication
    UserStorage userStorage = new UserStorage();
    Authenticator authenticator = new Authenticator(userStorage);
    // Eclipse complains because Authenticator returns Message now
    // User user = authenticator.authenticate("randomuser", "pass");
    // System.out.println(user.getId() + ' ' + user.getPassword());
    
    //loading and saving
    RoomStorage roomStorage = new RoomStorage();
    /*String sampleUsers[] = {"randomuser", "anotheruser"};
    Room room = new Room(sampleUsers);
    room.addMessage(new ChatMessage("randomuser", "hi again", MessageStatus.Delivered));
    room.addMessage(new ChatMessage("anotheruser", "lo again", MessageStatus.Delivered));*/
    Logger logger = new Logger(roomStorage, userStorage);
    System.out.println(roomStorage.getRoomById("be108892-d79b-40c9-a9be-30eba420d1f0"));
    //logger.createRoom(room);
    logger.save();
  }
}
