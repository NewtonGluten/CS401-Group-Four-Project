import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
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
				//first string is the user id, second is a comma-separated list of room id's
				String info[] = line.split(":");
				if (info.length != 2)
					//TODO: what should we do for corrupt data
					continue;
				
				//list of rooms user is in, may be empty
				List<String> rooms = new ArrayList<String>();
				for (String roomId : info[1].split(",")) {
					rooms.add(roomId);
				}
				
				userIdRooms.putIfAbsent(info[0], rooms);
			}
			scanner.close();
		} catch(Exception e) {
			System.out.println("userrooms.txt was not able to be opened");
			e.printStackTrace();
		}
		
	}
	
	//TODO: should this return null for invalid users?
	public List<String> getUserRooms(String userId) {
		return userIdRooms.get(userId);
	}
	
	//TODO: should this return null for invalid users?
	public User getUserById(String userId) {
		return users.get(userId);
	}
	
	//TODO: how should we handle invalid credentials
	public void addUser(String userId, String password, UserRole role) {
		if (userId.contains(":") || password.contains(":"))
			return;
		users.putIfAbsent(userId, new User(userId, password, role));
		userIdRooms.putIfAbsent(userId, new ArrayList<String>());
	}
	
	//TODO: does this involve any thread updates or are we doing that outside
	public void setUserStatus(String userId, UserStatus status) {
		User user = users.get(userId);
		if (user == null)
			return;
		user.setStatus(status);
	}
	
}
