import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.io.File;

public class RoomStorage {
	Map<String, Room> rooms;
	
	public RoomStorage() {
		rooms = new HashMap<String, Room>();
		File folder = new File("rooms");
		File roomFiles[] = folder.listFiles();
		for (File file : roomFiles) {
			rooms.putIfAbsent(file.getName().substring(0, 36), new Room(file));
		}
	}
	
	//TODO: not sure if this is correct according to the design
	public List<Room> getRoomsForUser(String userId) {
		List<Room> userRooms = new ArrayList<Room>();
		for (String roomId : rooms.keySet()) {
			Room room = rooms.get(roomId);
			if (room == null)
				continue;
			List<String> users = room.getUsers();
			for (String id : users) {
				if (userId.equals(id))
					userRooms.add(room);
			}
		}
		return userRooms;
	}
	
	public Room getRoomById(String roomId) {
		return rooms.get(roomId);
	}
	
	public void addRoom(Room room) {
		rooms.putIfAbsent(room.getId(), room);
	}
	
	public void addUserToRoom(String roomId, String userId) {
		Room room = rooms.get(roomId);
		if (room != null)
			room.addUser(userId);
	}
	
	public void removeUser(String roomId, String userId) {
		Room room = rooms.get(roomId);
		if (room != null)
			room.addUser(userId);
	}
	
	public void setMessageStatus(String roomId, String messageId, MessageStatus status) {
		Room room = rooms.get(roomId);
		if (room != null)
			room.setMessageStatus(messageId, status);
	}
}


/*
 * File folder = new File("your/path");
File[] listOfFiles = folder.listFiles();

for (int i = 0; i < listOfFiles.length; i++) {
  if (listOfFiles[i].isFile()) {
    System.out.println("File " + listOfFiles[i].getName());
  } else if (listOfFiles[i].isDirectory()) {
    System.out.println("Directory " + listOfFiles[i].getName());
  }
}
 */
