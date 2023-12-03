import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Scanner;

import java.io.File;

public class UserStorage {
	private Map<String, List<String>> userIdRooms;
	private Map<String, User> users;
	
	public UserStorage() {
		userIdRooms = new HashMap<String, List<String>>();
		users = new HashMap<String, User>();
		try {
			File file = new File("credentials.txt");
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String credentials[] = line.split(":");
				if (credentials.length != 3)
					//TODO: what should we do for corrupt data
					continue;
				//TODO: how should we store user roles
				users.putIfAbsent(credentials[0], new User(
						credentials[0], 
						credentials[1],
						credentials[2].charAt(0) == 'I' ? UserRole.IT : UserRole.Normal
				));
				userIdRooms.putIfAbsent(credentials[0], new ArrayList<String>());
			}
			scanner.close();
		} catch(Exception e) {
			System.out.println("credentials.txt was not able to be opened");
			e.printStackTrace();
		}
		
		try {
			File file = new File("userrooms.txt");
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.isBlank())
					continue;
				//first string is the user id, second is a comma-separated list of room id's
				String info[] = line.split(":");
				//list of rooms user is in, may be empty
				List<String> rooms = new ArrayList<String>();
				if (info.length == 2) {
					for (String roomId : info[1].split(",")) {
						if (roomId != null && !roomId.isBlank())
							rooms.add(roomId);
					}
				}
				userIdRooms.put(info[0], rooms);
			}
			scanner.close();
		} catch(Exception e) {
			System.out.println("userrooms.txt was not able to be opened");
			e.printStackTrace();
		}
		
	}
	
	public List<String> getUserRooms(String userId) {
		return userIdRooms.get(userId);
	}
	
	public User getUserById(String userId) {
		return users.get(userId);
	}
	
	public void addUser(String userId, String password, UserRole role) {
		if (userId.contains(":") || password.contains(":"))
			return;
		users.putIfAbsent(userId, new User(userId, password, role));
		userIdRooms.putIfAbsent(userId, new ArrayList<String>());
	}
	
	public void addRoomToUser(String userId, String roomId) {
		if (userIdRooms.containsKey(userId))
			userIdRooms.get(userId).add(roomId);
	}
	
	public void removeRoom(String userId, String roomId) {
		if (userIdRooms.containsKey(userId))
			userIdRooms.get(userId).remove(roomId);
	}
	
	//TODO: does this involve any thread updates or are we doing that outside
	public void setUserStatus(String userId, UserStatus status) {
		User user = users.get(userId);
		if (user == null)
			return;
		user.setStatus(status);
	}
	
	public Set<String> getAllUsers() {
		return users.keySet();
	}
	
	public List<User> getUserList(String userId) {
		List<User> userList = new ArrayList<User>();

		for (String user : getAllUsers()) {
			if (!user.equals(userId)) {
				userList.add(users.get(user));
			}
		}

		userList.sort((a, b) -> a.getId().compareTo(b.getId()));

		return userList;
	}
}
