package testSuite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import commsProj.*;

public class LoggerTest {

	@Test
	public void testConstructor() {
		//ensure construction was successful
		UserStorage userStorage = new UserStorage();
		RoomStorage roomStorage = new RoomStorage();
		Logger logger = new Logger(roomStorage, userStorage);
		assertNotNull(logger);
	}
	
	@Test
	public void testCreateRoom() {
		//setup
		UserStorage userStorage = new UserStorage();
		RoomStorage roomStorage = new RoomStorage();
		Logger logger = new Logger(roomStorage, userStorage);
		
		List<String> users = new ArrayList<String>();
		users.add("user1");
		users.add("user2");
		Room room = new Room(users);
		//add the room by the logger
		logger.createRoom(room);
		//ensure the room storage has the room
		assertSame(roomStorage.getRoomById(room.getId()), room);
		//ensure the users have the room
		assertTrue(userStorage.getUserRooms("user1").contains(room.getId()));
		assertTrue(userStorage.getUserRooms("user2").contains(room.getId()));
	}
	
	@Test
	public void testAddUserToRoom() {
		//setup
		UserStorage userStorage = new UserStorage();
		RoomStorage roomStorage = new RoomStorage();
		Logger logger = new Logger(roomStorage, userStorage);
		
		List<String> users = new ArrayList<String>();
		users.add("user1");
		users.add("user2");
		Room room = new Room(users);
		//add the room by the logger
		logger.createRoom(room);
		//ensure the room does not contain the user and vice versa
		assertFalse(userStorage.getUserRooms("user3").contains(room.getId()));
		assertFalse(roomStorage.getRoomById(room.getId()).getUsers().contains("user3"));
		//add the user to the room
		logger.addUserToRoom("user3", room.getId());
		//ensure the users have the room and vice versa
		assertTrue(userStorage.getUserRooms("user3").contains(room.getId()));
		assertTrue(roomStorage.getRoomById(room.getId()).getUsers().contains("user3"));
	}
	
	@Test
	public void testRemoveUserFromRoom() {
		//setup
		UserStorage userStorage = new UserStorage();
		RoomStorage roomStorage = new RoomStorage();
		Logger logger = new Logger(roomStorage, userStorage);
		
		List<String> users = new ArrayList<String>();
		users.add("user1");
		users.add("user2");
		Room room = new Room(users);
		//add the room by the logger
		logger.createRoom(room);
		//ensure the room does not contain the user and vice versa
		assertTrue(userStorage.getUserRooms("user2").contains(room.getId()));
		assertTrue(roomStorage.getRoomById(room.getId()).getUsers().contains("user2"));
		//remove the user from the room
		logger.removeUserFromRoom("user2", room.getId());
		//ensure the users have the room and vice versa
		assertFalse(userStorage.getUserRooms("user2").contains(room.getId()));
		assertFalse(roomStorage.getRoomById(room.getId()).getUsers().contains("user2"));
	}
	

}
