import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

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
	
	//TODO: not sure if this is correct according to the design or if the name is a bit off
	// RESPONSE
	// argument should be list of room ids
	// UserStorage contains map of userId -> list of room ids
	//RESOLVED
	public List<Room> getRoomsForUser(List<String> roomIds) {
		List<Room> userRooms = new ArrayList<Room>();
		for (String roomId : roomIds) {
			Room room = rooms.get(roomId);
			if (room != null)
				userRooms.add(room);
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
	
	public Set<String> getAllRooms() {
		return rooms.keySet();
	}
}
