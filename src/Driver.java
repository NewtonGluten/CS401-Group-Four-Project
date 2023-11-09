import java.util.List;

public class Driver {
  public static void main(String[] args) {
    System.out.println("Hello World!");
    UserStorage userStorage = new UserStorage();
    Authenticator authenticator = new Authenticator(userStorage);
    User user = authenticator.authenticate("randomuser", "pass");
    System.out.println(user.getId() + ' ' + user.getPassword());
    List<String> rooms = userStorage.getUserRooms("randomuser");
    for (String roomId : rooms) {
    	System.out.println(roomId);
    }
    
  }
}
