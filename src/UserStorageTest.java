import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

import org.junit.Test;
import java.util.List;
public class UserStorageTest {

	@Test
	public void testConstructor() {
		UserStorage userStorage = new UserStorage();
		User user = userStorage.getUserById("user1");
		
		assertNotNull(user);
		assertNull(userStorage.getUserById("this user doesn't exist"));
		
		assertEquals(user.getId(), "user1");
		
		String users[] = {"user1", "user2", "long name it user with spaces", "user3"};
		Set<String> userSet = userStorage.getAllUsers();
		for (String userId : users) {
			assertTrue(userSet.contains(userId));
		}
		
		assertTrue(userStorage.getUserRooms("user1").isEmpty());
		assertEquals(userStorage.getUserRooms("long name it user with spaces").size(), 1);
	}
	
	@Test
	public void testAddUser() {
		UserStorage userStorage = new UserStorage();
		userStorage.addUser("user4", "pass", UserRole.Normal);
		User user = userStorage.getUserById("user4");
		
		assertNotNull(user);
		assertEquals(user.getId(), "user4");
	}
	
	@Test
	public void testAddRoomToUser() {
		UserStorage userStorage = new UserStorage();
		userStorage.addRoomToUser("user1", "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		
		List<String> rooms = userStorage.getUserRooms("user1");
		assertEquals(rooms.size(), 1);
		assertEquals(rooms.get(0), "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		
	}
	
	@Test
	public void testRemoveUserFromRoom() {
		UserStorage userStorage = new UserStorage();
		userStorage.removeRoom("long name it user with spaces", "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		
		assertTrue(userStorage.getUserRooms("long name it user with spaces").isEmpty());
	}
	
	@Test
	public void testSetUserStatus() {
		UserStorage userStorage = new UserStorage();
		assertEquals(userStorage.getUserById("user1").getStatus(), UserStatus.Offline);
		
		userStorage.setUserStatus("user1", UserStatus.Online);
		assertEquals(userStorage.getUserById("user1").getStatus(), UserStatus.Online);
		
		userStorage.setUserStatus("user1", UserStatus.Away);
		assertEquals(userStorage.getUserById("user1").getStatus(), UserStatus.Away);
		
		userStorage.setUserStatus("user1", UserStatus.Busy);
		assertEquals(userStorage.getUserById("user1").getStatus(), UserStatus.Busy);
	}
}
