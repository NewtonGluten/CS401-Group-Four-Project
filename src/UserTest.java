import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

public class UserTest {
	
	@Test
	public void testConstrutor() {
		User user = new User("bob", "password", UserRole.Normal);
		assertNotNull(user);
		
		User IT = new User("jeff", "long password", UserRole.IT);
		assertEquals(IT.getRole(), UserRole.IT);
		
		assertEquals(user.getId(), "bob");
		assertEquals(user.getPassword(), "password");
		assertEquals(user.getRole(), UserRole.Normal);
		assertEquals(user.getStatus(), UserStatus.Offline);
	}
	
	@Test
	public void testStatus() {
		User user = new User("bob", "password", UserRole.Normal);
		user.setStatus(UserStatus.Online);
		assertEquals(user.getStatus(), UserStatus.Online);
		
		user.setStatus(UserStatus.Away);
		assertEquals(user.getStatus(), UserStatus.Away);
		
		user.setStatus(UserStatus.Busy);
		assertEquals(user.getStatus(), UserStatus.Busy);
	}

}
