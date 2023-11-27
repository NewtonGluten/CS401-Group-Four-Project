import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;

import java.util.List;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.File;
public class RoomTest {

	@Test
	public void testNewConstructor() {
		String users[] = {"user1", "user2", "user3"};
		Room room = new Room(users);
		assertNotNull(room);
		
		List<String> userList = room.getUsers();
		
		for (int i = 0; i < 3; i++) {
			assertEquals(users[i], userList.get(i));
		}
		//test UUID
		assertEquals(room.getId().length(), 36);
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
			file = new File(filename + ".txt");
		} catch(Exception e) {
			fail("Failed to open file");
		}
		Room room = new Room(file);
		
		assertNotNull(room);
				
		String users[] = {"user1", "user2"};
		List<String> userList = room.getUsers();
		
		for (int i = 0; i < 3; i++) {
			assertEquals(users[i], userList.get(i));
		}
		//test UUID
		assertEquals(room.getId(), "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		//messages should be empty after constructor creation
		assertFalse(room.getMessages().isEmpty());
		//the room should no be empty (2 users)
		assertFalse(room.isEmpty());
	}

}
