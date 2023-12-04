import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class MessageTest {

	@Test
	public void testConstructor() {
		//test statuses
		Message loginMessage = new Message (MessageType.Login);
		Message logoutMessage = new Message (MessageType.Logout);
		Message newChatMessage = new Message (MessageType.NewChat);
		Message newRoomMessage = new Message (MessageType.NewRoom);
		Message createRoomMessage = new Message (MessageType.CreateRoom);
		Message leaveRoomMessage = new Message (MessageType.LeaveRoom);
		Message addToRoomMessage = new Message (MessageType.AddToRoom);
		Message changeStatusMessage = new Message (MessageType.ChangeStatus);
		Message updateUserStatusMessage = new Message (MessageType.UpdateUserStatus);
		//ensure the messages were constructed
		assertNotNull(loginMessage);
		assertNotNull(logoutMessage);
		assertNotNull(newChatMessage);
		assertNotNull(newRoomMessage);
		assertNotNull(createRoomMessage);
		assertNotNull(leaveRoomMessage);
		assertNotNull(addToRoomMessage);
		assertNotNull(changeStatusMessage);
		assertNotNull(updateUserStatusMessage);
		//ensure the statuses are correct
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
		//set the password
		message.setPassword("thisIsMyTestPassword");
		assertTrue(message.getPassword().equals("thisIsMyTestPassword"));
		//ensure the password is immutable
		message.setPassword("notchangable");
		assertFalse(message.getPassword().equals("notchangable"));
		
		
	}
	
	@Test
	public void testSetLoginStatus() {
		//set the login status
		Message message = new Message(MessageType.Login);
		message.setLoginStatus("Success");
		//ensure it matches
		assertTrue(message.getLoginStatus().equals("Success"));
		message.setLoginStatus("failure");
		
		assertFalse(message.getLoginStatus().equals("failure"));
		
	}
	
	@Test
	public void testSetRoomId() {
		//set the room id
		Message message = new Message(MessageType.Login);
		message.setRoomId("d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
		//ensure it matches
		assertTrue(message.getRoomId().equals("d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042"));
		message.setRoomId("other");
		//ensure it is immutable
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
		//create a login message
		Message message = new Message(MessageType.Login);
		String id = "testUser";
		String password = "letmein";
		UserRole role = UserRole.Normal;
		//create a user
		User testUser = new User(id, password, role);
		//place the user in the message
		message.setUser(testUser);
		assertNotNull(message.getUser());
		//create another user
		String id2 = "testUser2";
		User testUser2 = new User(id2, password, role);
		//ensure the user is immutable
		message.setUser(testUser2);
		assertNotSame(message.getUser(), testUser2);
		
	}
	
	@Test
	public void TestSetUserStatus() {
		//create a set user status message
		Message message = new Message(MessageType.Login);
		message.setUserStatus(UserStatus.Online);
		//ensure it is created
		assertNotNull(message.getUserStatus());
		//ensure it is immutable
		message.setUserStatus(UserStatus.Away);
		assertNotEquals(message.getUserStatus(), UserStatus.Away);
	
	}
	
	@Test
	public void TestSetContents() {
		//create a contents message
		Message message = new Message(MessageType.NewChat);
		String contents = "this is a test string for message body";
		message.setContents(contents);
		//ensure the contents match
		assertEquals(message.getContents(), contents);
		//ensure it is immutable
		message.setContents("new body");
		assertFalse(message.getContents().equals("new body"));
	}
	
	@Test
	public void TestSetRooms() {
		//create a set rooms message
		RoomStorage roomStorage = new RoomStorage();
		Room room1 = roomStorage.getRoomById("be108892-d79b-40c9-a9be-30eba420d1f0");
		Room room2 = roomStorage.getRoomById("c9420843-8052-4509-8a49-a25edfb63018");
		//add lists of rooms
		List<Room> rooms1 = new ArrayList<Room>();
		List<Room> rooms2 = new ArrayList<Room>();
	
		rooms1.add(room1);
		rooms2.add(room2);
		//create a message pertaining to this
		Message message1 = new Message(MessageType.NewRoom);		
		message1.setRooms(rooms1);
		//ensure it is created
		assertNotNull(message1.getRooms());
		//ensure it is immutable
		message1.setRooms(rooms2);
		assertNotEquals(message1.getRooms().get(0).getId(),rooms2.get(0).getId());
		
	}
}

