import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

public class AuthenticatorTest {

	@Test
	 public void test() {
		UserStorage userStorage = new UserStorage();
		
		Authenticator authenticator = new Authenticator(userStorage);
		
		assertNull(authenticator.authenticate("this user doesn't exist", "pass"));
		
		Message normalUserMsg = authenticator.authenticate("user1", "pass");
		User normalUser = normalUserMsg.getUser();
		assertNotNull(normalUser);
		assertNull(authenticator.authenticate("user1", "wrong password"));
		assertEquals(normalUser.getStatus(), UserStatus.Online);
		
		Message ITUserMsg = authenticator.authenticate("long name it user with spaces", "password");
		User ITUser = ITUserMsg.getUser();
		assertNotNull(ITUser);
		assertNull(authenticator.authenticate("long name it user with spaces", "wrong password"));
		assertEquals(normalUser.getStatus(), UserStatus.Online);
	}
}
