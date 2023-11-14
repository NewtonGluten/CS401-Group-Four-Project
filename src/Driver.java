import java.util.List;

import java.io.PrintWriter;

public class Driver {
  public static void main(String[] args) {
    UserStorage userStorage = new UserStorage();
    Authenticator authenticator = new Authenticator(userStorage);
    User user = authenticator.authenticate("randomuser", "pass");
    System.out.println(user.getId() + ' ' + user.getPassword());
    List<String> rooms = userStorage.getUserRooms("randomuser");
    for (String roomId : rooms) {
    	System.out.println(roomId);
    }
    RoomStorage roomStorage = new RoomStorage();
    List<Room> someRooms = roomStorage.getRoomsForUser("randomuser");
    for (Room room : someRooms) {
    	System.out.println(room.getId());
    }
    /*try {
    	String users[] = {"randomuser", "normaluser", "someone"};
    	Room room = new Room(users);
    	room.addMessage(new ChatMessage("someone", "hi", MessageStatus.Delivered));
    	room.addMessage(new ChatMessage("normaluser", "lo", MessageStatus.Delivered));
    	PrintWriter writer = new PrintWriter("rooms/" + room.getId() + ".txt");
    	writer.print(room.toString());
    	writer.close();
    	
    } catch(Exception e) {
    	e.printStackTrace();
    }*/
  }
}
