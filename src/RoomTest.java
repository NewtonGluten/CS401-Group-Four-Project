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
		List<String> users = new ArrayList<String>();
		users.add("user1");
		users.add("user2");
		users.add("user3");
		
		Room room = new Room(users);
		assertNotNull(room);
		
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
		
		//the date is stored correctly
		try {
			//check if the values are within 1 second
			assertTrue(Math.abs(time - simpleDateFormat.parse(stored.substring(0, 17)).getTime()) < 1000);
		} catch(ParseException e) {
			fail("Date stored incorrectly");
		}
		
		//the users are stored correctly
		assertEquals(stored.substring(18), "user1,user2,user3,\n");

	}
	
	@Test
	public void testTitleConstructor() {
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
		
		//the date is stored correctly
		try {
			//check if the values are within 1 second
			assertTrue(Math.abs(time - simpleDateFormat.parse(stored.substring(0, 17)).getTime()) < 1000);
		} catch(ParseException e) {
			fail("Date stored incorrectly");
		}
		
		//the users are stored correctly
		assertEquals(stored.substring(18), "user1,user2,user3,\n");
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
		
		room.removeUser("user1");
		
		List<String> users = room.getUsers();
		assertEquals(users.size(), 1);
		assertEquals(users.get(0), "user2");
		
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
		
		room.addUser("long name it user with spaces");
		
		String users[] = {"user1", "user2", "long name it user with spaces"};
		List<String> roomUsers = room.getUsers();
		assertEquals(roomUsers.size(), 3);
		for (int i = 0; i < 3; i++) {
			assertEquals(roomUsers.get(i), users[i]);
		}
	}
	
	@Test
	public void testAddMessage() {
		
	}

}
