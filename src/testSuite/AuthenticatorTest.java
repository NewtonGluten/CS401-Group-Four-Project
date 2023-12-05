package testSuite;
import commsProj.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

public class AuthenticatorTest {

	@Test
	 public void test() {
		UserStorage userStorage = new UserStorage();
		
		Authenticator authenticator = new Authenticator(userStorage);
		//test nonexistent user
		Message nonexistentMsg = authenticator.authenticate("this user doesn't exist", "pass");
		assertNull(nonexistentMsg.getUser());
		assertEquals(nonexistentMsg.getContents(), "User does not exist");
		//test right and wrong password
		Message normalUserMsg = authenticator.authenticate("user1", "pass");
		User normalUser = normalUserMsg.getUser();
		assertNotNull(normalUserMsg.getUser());
		Message wrongPasswordMsg = authenticator.authenticate("user1", "wrong password");
		User wrongPasswordUser = wrongPasswordMsg.getUser();
		assertNull(wrongPasswordUser);
		assertEquals(wrongPasswordMsg.getContents(), "Password is incorrect");
		//test status is online
		assertEquals(normalUser.getStatus(), UserStatus.Online);
		
		//same test for IT user
		Message ITUserMsg = authenticator.authenticate("long name it user with spaces", "password");
		User ITUser = ITUserMsg.getUser();
		assertNotNull(ITUser);
		assertNull(authenticator.authenticate("long name it user with spaces", "wrong password").getUser());
		assertEquals(normalUser.getStatus(), UserStatus.Online);
	}
}
