import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

public class AuthenticatorTest {

	@Test
	 public void test() {
		UserStorage userStorage = new UserStorage();
		
		Authenticator authenticator = new Authenticator(userStorage);
		
		assertNull(authenticator.authenticate("this user doesn't exist", "pass"));
		
		User normalUser = authenticator.authenticate("user1", "pass");
		assertNotNull(normalUser);
		assertNull(authenticator.authenticate("user1", "wrong password"));
		
		User ITUser = authenticator.authenticate("long name it user with spaces", "password");
		assertNotNull(ITUser);
		assertNull(authenticator.authenticate("long name it user with spaces", "wrong password"));
		
		
		
	}
}
