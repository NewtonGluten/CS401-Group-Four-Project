import static org.junit.jupiter.api.Assertions.*;


import org.junit.Test;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
public class RoomStorageTest {

	@Test
	public void testConstructor() {
		RoomStorage roomStorage = new RoomStorage();
		Set<String> roomIds = roomStorage.getAllRooms();
		//check if file was read
		assertTrue(roomIds.contains("be108892-d79b-40c9-a9be-30eba420d1f0"));
		String rooms[] = {"ca0344b0-b027-40a0-8e07-25fe18db6355", "c9420843-8052-4509-8a49-a25edfb63018"};
		//check if the rooms are contained
		for (String roomId : rooms) {
			assertTrue(roomIds.contains(roomId));
		}
	}
	
	@Test
	public void testAddRoom() {
		RoomStorage roomStorage = new RoomStorage();
		List<String> userIds = new ArrayList<String>();
		//create room
		Room room = new Room(userIds, "some room title");
		//room does not exist yet in storage
		assertNull(roomStorage.getRoomById(room.getId()));
		roomStorage.addRoom(room);
		//room exists in storage
		Room roomInStorage = roomStorage.getRoomById(room.getId());
		//ensure it's the same room
		assertSame(room, roomInStorage);
	}
	
	@Test
	public void testAddUser() {
		RoomStorage roomStorage = new RoomStorage();
		Room room = roomStorage.getRoomById("d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		//user is not in the room
		assertFalse(room.getUsers().contains("user3"));
		roomStorage.addUserToRoom(room.getId(), "user3");
		//user is in the room
		assertTrue(room.getUsers().contains("user3"));
	}
	
	public void testRemoveUser() {
		RoomStorage roomStorage = new RoomStorage();
		Room room = roomStorage.getRoomById("d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		//user is not in the room
		assertTrue(room.getUsers().contains("user1"));
		roomStorage.removeUser(room.getId(), "user");
		//user is in the room
		assertFalse(room.getUsers().contains("user1"));
	}
	
	@Test
	public void testSetMessageStatus() {
		RoomStorage roomStorage = new RoomStorage();
		//create room with messages
		List<String> userIds = new ArrayList<String>();
		userIds.add("ca0344b0-b027-40a0-8e07-25fe18db6355");
		userIds.add("c9420843-8052-4509-8a49-a25edfb63018");
		Room room = new Room(userIds, "some room title");
		//add room
		roomStorage.addRoom(room);
		//add new message
		ChatMessage chatMessage = new ChatMessage("user1", "good day", MessageStatus.Pending);
		room.addMessage(chatMessage);
		//change its status
		roomStorage.setMessageStatus(room.getId(), chatMessage.getId(), MessageStatus.Delivered);
		//status has been changed
		assertEquals(chatMessage.getStatus(), MessageStatus.Delivered);
	}

}
