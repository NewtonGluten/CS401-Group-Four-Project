package testSuite;
import commsProj.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

public class UserTest {
	
	@Test
	public void testConstrutor() {
		//test normal user creation
		User user = new User("bob", "password", UserRole.Normal);
		assertNotNull(user);
		//test IT user creation
		User IT = new User("jeff", "long password", UserRole.IT);
		assertEquals(IT.getRole(), UserRole.IT);
		//test getters
		assertEquals(user.getId(), "bob");
		assertEquals(user.getPassword(), "password");
		assertEquals(user.getRole(), UserRole.Normal);
		assertEquals(user.getStatus(), UserStatus.Offline);
	}
	
	@Test
	public void testStatus() {
		//create a user
		User user = new User("bob", "password", UserRole.Normal);
		//change the status to Online and check
		user.setStatus(UserStatus.Online);
		assertEquals(user.getStatus(), UserStatus.Online);
		//test changing to Away
		user.setStatus(UserStatus.Away);
		assertEquals(user.getStatus(), UserStatus.Away);
		//test changing to Busy
		user.setStatus(UserStatus.Busy);
		assertEquals(user.getStatus(), UserStatus.Busy);
	}

}
