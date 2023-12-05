package testSuite;
import commsProj.*;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.File;
public class RoomTest {

	@Test
	public void testNewConstructor() {
		//create a room with some users
		List<String> users = new ArrayList<String>();
		users.add("user1");
		users.add("user2");
		users.add("user3");
		//ensure it has been created
		Room room = new Room(users);
		assertNotNull(room);
		//compare users in the room
		List<String> userList = room.getUsers();
		for (int i = 0; i < 3; i++) {
			assertEquals(users.get(i), userList.get(i));
		}
		//test UUID
		assertEquals(room.getId().length(), 36);
		//title should be default
		assertEquals(room.getTitle(), "user1's Room");
		//messages should be empty after constructor creation
		assertTrue(room.getMessages().isEmpty());
		//the room should no be empty (2 users)
		assertFalse(room.isEmpty());
		
		long time = room.getCreationDate().getTime();
		
		//the creation date should be roughly the current time
		assertTrue(new Date().getTime() - time < 100);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		String stored = room.toString();
		int timeStart = stored.indexOf('\n') + 1;
		String timeString = stored.substring(timeStart, timeStart + 17);

		//the date can be parsed correctly
		try {
			assertNotNull(simpleDateFormat.parse(timeString));
		} catch(ParseException e) {
			fail("Date stored incorrectly");
		}
		
		//the users are stored correctly
		String usersStr = "user1,user2,user3,\n";
		int usersStart = stored.indexOf(timeString) + timeString.length() + 1;
		int usersEnd = usersStart + usersStr.length();
		assertEquals(stored.substring(usersStart, usersEnd), usersStr);

	}
	
	@Test
	public void testTitleConstructor() {
		//sample room with users
		List<String> users = new ArrayList<String>();
		users.add("user1");
		users.add("user2");
		users.add("user3");
		
		Room room = new Room(users, "The Room of Rooms");
		assertNotNull(room);
		
		List<String> userList = room.getUsers();
		
		for (int i = 0; i < 3; i++) {
			assertEquals(users.get(i), userList.get(i));
		}
		//test UUID
		assertEquals(room.getId().length(), 36);
		//title should be as set
		assertEquals(room.getTitle(), "The Room of Rooms");
		//messages should be empty after constructor creation
		assertTrue(room.getMessages().isEmpty());
		//the room should no be empty (2 users)
		assertFalse(room.isEmpty());
		
		long time = room.getCreationDate().getTime();
		
		//the creation date should be roughly the current time
		assertTrue(new Date().getTime() - time < 100);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		String stored = room.toString();
		int timeStart = stored.indexOf('\n') + 1;
		String timeString = stored.substring(timeStart, timeStart + 17);

		//the date can be parsed correctly
		try {
			assertNotNull(simpleDateFormat.parse(timeString));
		} catch(ParseException e) {
			fail("Date stored incorrectly");
		}
		
		//the users are stored correctly
		String usersStr = "user1,user2,user3,\n";
		int usersStart = stored.indexOf(timeString) + timeString.length() + 1;
		int usersEnd = usersStart + usersStr.length();
		assertEquals(stored.substring(usersStart, usersEnd), usersStr);
	}
	
	@Test
	public void testFileConstructor() {
		File file = null;
		String filename = "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042";
		try { 
			file = new File("rooms/" + filename + ".txt");
		} catch(Exception e) {
			fail("Failed to open file");
		}
		Room room = new Room(file);
		
		assertNotNull(room);
		//compare users
		String users[] = {"user1", "user2"};
		List<String> userList = room.getUsers();
		for (int i = 0; i < 2; i++) {
			assertEquals(users[i], userList.get(i));
		}
		//test UUID
		assertEquals(room.getId(), "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		//messages should be empty after constructor creation
		assertFalse(room.getMessages().isEmpty());
		//the room should no be empty (2 users)
		assertFalse(room.isEmpty());
	}
	
	@Test
	public void testRemoveUser() {
		File file = null;
		String filename = "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042";
		try { 
			file = new File("rooms/" + filename + ".txt");
		} catch(Exception e) {
			fail("Failed to open file");
		}
		Room room = new Room(file);
		//remove user from room
		room.removeUser("user1");
		//ensure user list is correct
		List<String> users = room.getUsers();
		assertEquals(users.size(), 1);
		assertEquals(users.get(0), "user2");
		//remove last remaining user and ensure room is empty
		room.removeUser("user2");
		
		users = room.getUsers();
		assertTrue(users.isEmpty());
		assertTrue(room.isEmpty());
	}
	
	@Test
	public void testAddUser() {
		File file = null;
		String filename = "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042";
		try { 
			file = new File("rooms/" + filename + ".txt");
		} catch(Exception e) {
			fail("Failed to open file");
		}
		Room room = new Room(file);
		//add the user
		room.addUser("long name it user with spaces");
		//ensure the user list has been updated
		String users[] = {"user1", "user2", "long name it user with spaces"};
		List<String> roomUsers = room.getUsers();
		assertEquals(roomUsers.size(), 3);
		for (int i = 0; i < 3; i++) {
			assertEquals(roomUsers.get(i), users[i]);
		}
	}
}
