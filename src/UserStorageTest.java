import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

import org.junit.Test;
import java.util.List;
public class UserStorageTest {

	@Test
	public void testConstructor() {
		//create user storage from files
		UserStorage userStorage = new UserStorage();
		//ensure we can find the user
		User user = userStorage.getUserById("user1");
		assertNotNull(user);
		//test users that don't exist return null
		assertNull(userStorage.getUserById("this user doesn't exist"));
		//ensure it is the correct user
		assertEquals(user.getId(), "user1");
		//compare the set of all users
		String users[] = {"user1", "user2", "long name it user with spaces", "user3"};
		Set<String> userSet = userStorage.getAllUsers();
		for (String userId : users) {
			assertTrue(userSet.contains(userId));
		}
		//ensure the particular user has no rooms
		assertTrue(userStorage.getUserRooms("user1").isEmpty());
		//ensure userrooms.txt was read correctly
		assertEquals(userStorage.getUserRooms("long name it user with spaces").size(), 1);
	}
	
	@Test
	public void testAddUser() {
		UserStorage userStorage = new UserStorage();
		//add a user
		userStorage.addUser("user4", "pass", UserRole.Normal);
		//ensure they are there in the user storage
		User user = userStorage.getUserById("user4");
		assertNotNull(user);
		assertEquals(user.getId(), "user4");
	}
	
	@Test
	public void testAddRoomToUser() {
		UserStorage userStorage = new UserStorage();
		//add the room to this user
		userStorage.addRoomToUser("user1", "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		//check if it was correctly added
		List<String> rooms = userStorage.getUserRooms("user1");
		assertEquals(rooms.size(), 1);
		assertEquals(rooms.get(0), "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		
	}
	
	@Test
	public void testRemoveUserFromRoom() {
		UserStorage userStorage = new UserStorage();
		//remove the particular room
		userStorage.removeRoom("long name it user with spaces", "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		//ensure it is no longer there
		assertTrue(userStorage.getUserRooms("long name it user with spaces").isEmpty());
	}
	
	@Test
	public void testSetUserStatus() {
		UserStorage userStorage = new UserStorage();
		//ensure default status if Offline
		assertEquals(userStorage.getUserById("user1").getStatus(), UserStatus.Offline);
		//ensure status is set to Online
		userStorage.setUserStatus("user1", UserStatus.Online);
		assertEquals(userStorage.getUserById("user1").getStatus(), UserStatus.Online);
		//ensure status is set to Away
		userStorage.setUserStatus("user1", UserStatus.Away);
		assertEquals(userStorage.getUserById("user1").getStatus(), UserStatus.Away);
		//ensure status is set to Busy
		userStorage.setUserStatus("user1", UserStatus.Busy);
		assertEquals(userStorage.getUserById("user1").getStatus(), UserStatus.Busy);
	}
}
