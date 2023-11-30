import java.util.List;
import java.util.Set;

import java.io.PrintWriter;

public class Logger {
	private RoomStorage roomStorage;
	private UserStorage userStorage;
	
	public Logger(RoomStorage roomStorage, UserStorage userStorage) {
		this.roomStorage = roomStorage;
		this.userStorage = userStorage;
	}
	
	public void save() {
		Set<String> users = userStorage.getAllUsers();
		Set<String> rooms = roomStorage.getAllRooms();
		try {
	    	PrintWriter writer = new PrintWriter("userrooms.txt");
	    	for (String userId : users) {
	    		List<String> roomIds = userStorage.getUserRooms(userId);
	    		writer.print(userId + ':');
	    		if (roomIds != null) {
		    		for (String roomId : roomIds) {
		    			writer.print(roomId + ',');
		    		}
	    		}
	    		writer.println();
	    	}
	    	writer.close();
	    	for (String roomId : rooms) {
	    		Room room = roomStorage.getRoomById(roomId);
	    		writer = new PrintWriter("rooms/" + room.getId() + ".txt");
	        	writer.print(room.toString());
	        	writer.close();
	    	}
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	public void createRoom(Room room) {
		roomStorage.addRoom(room);
		for (String userId : room.getUsers()) {
			userStorage.addRoomToUser(userId, room.getId());
		}
	}
	
	public void addUserToRoom(String userId, String roomId) {
		roomStorage.addUserToRoom(roomId, userId);
		userStorage.addRoomToUser(userId, roomId);
	}
	
	public void removeUserFromRoom(String userId, String roomId) {
		roomStorage.removeUser(roomId, userId);
		userStorage.removeRoom(userId, roomId);
	}

	public String getLogsForUser(String userId) {
		List<String> roomIds = userStorage.getUserRooms(userId);
		String s = "";
		for (String roomId : roomIds) {
			Room room = roomStorage.getRoomById(roomId);
			ChatHistory chatHistory = room.getChatHistory();
			List<ChatMessage> messages = chatHistory.getMessagesFromUser(userId);

			if (messages.isEmpty()) {
				continue;
			}

			s += "Room ID: " + room.getId() + "\n";	

			for (ChatMessage message : messages) {
				s += message + "\n";
			}

			s += "================================\n";
		}

		if (s.isEmpty()) {
			s = "No logs found for " + userId;
		}

		return s;
	}

	public String getLogForRoom(String roomId) {
		Room room = roomStorage.getRoomById(roomId);

		if (room == null) {
			return "Room with ID " + roomId + " does not exist";
		}

		return room.toString();
	}
}
