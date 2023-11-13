import java.util.List;

import java.io.PrintWriter;

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
    RoomStorage roomStorage = new RoomStorage();
    System.out.println(roomStorage.getRoomsForUser("1"));
    /*try {
    	PrintWriter writer = new PrintWriter("rooms/" + room.getId() + ".txt");
    	writer.print(room.toString());
    	writer.close();
    	
    } catch(Exception e) {
    	e.printStackTrace();
    }*/
  }
}
