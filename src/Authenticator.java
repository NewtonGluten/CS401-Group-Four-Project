public class Authenticator {
	private UserStorage userStorage;
	
	public Authenticator(UserStorage userStorage) {
		this.userStorage = userStorage;
	}
	
	//TODO: does this return null for invalid credentials?
	// RESPONSE: returns null if user doesn't exist so they shouldn't be authenticated
	public User authenticate(String userId, String password) {
		User user = userStorage.getUserById(userId);
		if (user == null || !user.getPassword().equals(password))
			return null;
		user.setStatus(UserStatus.Online);
		return user;
	}
}
