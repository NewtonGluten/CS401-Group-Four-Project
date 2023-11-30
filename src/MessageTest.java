import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class MessageTest {

	@Test
	public void testConstructor() {
		Message loginMessage = new Message (MessageType.Login);
		Message logoutMessage = new Message (MessageType.Logout);
		Message newChatMessage = new Message (MessageType.NewChat);
		Message newRoomMessage = new Message (MessageType.NewRoom);
		Message createRoomMessage = new Message (MessageType.CreateRoom);
		Message leaveRoomMessage = new Message (MessageType.LeaveRoom);
		Message addToRoomMessage = new Message (MessageType.AddToRoom);
		Message changeStatusMessage = new Message (MessageType.ChangeStatus);
		Message updateUserStatusMessage = new Message (MessageType.UpdateUserStatus);
		
		assertNotNull(loginMessage);
		assertNotNull(logoutMessage);
		assertNotNull(newChatMessage);
		assertNotNull(newRoomMessage);
		assertNotNull(createRoomMessage);
		assertNotNull(leaveRoomMessage);
		assertNotNull(addToRoomMessage);
		assertNotNull(changeStatusMessage);
		assertNotNull(updateUserStatusMessage);
		
		assertEquals(loginMessage.getType(), MessageType.Login);
		assertEquals(logoutMessage.getType(), MessageType.Logout);
		assertEquals(newChatMessage.getType(), MessageType.NewChat);
		assertEquals(newRoomMessage.getType(), MessageType.NewRoom);
		assertEquals(createRoomMessage.getType(), MessageType.CreateRoom);
		assertEquals(leaveRoomMessage.getType(), MessageType.LeaveRoom);
		assertEquals(addToRoomMessage.getType(), MessageType.AddToRoom);
		assertEquals(changeStatusMessage.getType(), MessageType.ChangeStatus);
		assertEquals(updateUserStatusMessage.getType(), MessageType.UpdateUserStatus);
	}
	
	@Test
	public void testSetPassword() {
		Message message = new Message(MessageType.Logout);
		message.setPassword("thisIsMyTestPassword");
		assertTrue(message.getPassword().equals("thisIsMyTestPassword"));
		
		message.setPassword("notchangable");
		assertFalse(message.getPassword().equals("notchangable"));
		
		
	}
	
	@Test
	public void testSetLoginStatus() {
		Message message = new Message(MessageType.Login);
		message.setLoginStatus("Success");
		
		assertTrue(message.getLoginStatus().equals("Success"));
		message.setLoginStatus("failure");
		
		assertFalse(message.getLoginStatus().equals("failure"));
		
	}
	
	@Test
	public void testSetRoomId() {
		
		Message message = new Message(MessageType.Login);
		message.setRoomId("d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		
		assertTrue(message.getRoomId().equals("d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042"));
		message.setRoomId("other");
		
		assertNotEquals(message.getLoginStatus(), "other");
		
	}
	
	@Test
	public void testSetUserId() {
		Message message = new Message(MessageType.Login);
		message.setUserId("testuser");
		assertTrue(message.getUserId().equals("testuser"));
		message.setRoomId("newID");
		assertFalse(message.getUserId().equals("newID"));
	}
	
	@Test
	public void testSetUser() {
		Message message = new Message(MessageType.Login);
		String id = "testUser";
		String password = "letmein";
		UserRole role = UserRole.Normal;
		User testUser = new User(id, password, role);
		
		message.setUser(testUser);
		assertNotNull(message.getUser());
		
		String id2 = "testUser2";
		User testUser2 = new User(id2, password, role);
		
		message.setUser(testUser2);
		assertNotSame(message.getUser(), testUser2);
		
	}
	
	@Test
	public void TestSetUserStatus() {
		Message message = new Message(MessageType.Login);
		message.setUserStatus(UserStatus.Online);
		
		assertNotNull(message.getUserStatus());
		
		message.setUserStatus(UserStatus.Away);
		assertNotEquals(message.getUserStatus(), UserStatus.Away);
	
	}
	
	@Test
	public void TestSetContents() {
		Message message = new Message(MessageType.NewChat);
		String contents = "this is a test string for message body";
		message.setContents(contents);
		
		assertEquals(message.getContents(), contents);
		
		message.setContents("new body");
		assertFalse(message.getContents().equals("new body"));
	}
	
	@Test
	public void TestSetRooms() {
		RoomStorage roomStorage = new RoomStorage();
		Room room1 = roomStorage.getRoomById("be108892-d79b-40c9-a9be-30eba420d1f0");
		Room room2 = roomStorage.getRoomById("c9420843-8052-4509-8a49-a25edfb63018");
		
		List<Room> rooms1 = new ArrayList<Room>();
		List<Room> rooms2 = new ArrayList<Room>();
	
		rooms1.add(room1);
		rooms2.add(room2);
		
		Message message1 = new Message(MessageType.NewRoom);		
		message1.setRooms(rooms1);

		assertNotNull(message1.getRooms());
		
		message1.setRooms(rooms2);
		assertNotEquals(message1.getRooms().get(0).getId(),rooms2.get(0).getId());
		
	}
}

