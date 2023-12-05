package commsProj;
public class Authenticator {
	private UserStorage userStorage;
	
	public Authenticator(UserStorage userStorage) {
		this.userStorage = userStorage;
	}
	
	public Message authenticate(String userId, String password) {
		Message msg = new Message(MessageType.Login);
		User user = userStorage.getUserById(userId);

		if (user == null) {
			msg.setContents("User does not exist");
		} else if (!user.getPassword().equals(password)) {
			msg.setContents("Password is incorrect");
		} else if (user.getStatus() == UserStatus.Online) {
			msg.setContents("User is already logged in");
		} else {
			user.setStatus(UserStatus.Online);
			msg.setUser(user);
			msg.setUserId(userId);
			msg.setUserStatus(UserStatus.Online);
		}

		return msg;
	}
}
