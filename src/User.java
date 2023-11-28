
public class User {
	private String id;
	private String password;
	private UserStatus status;
	private UserRole role;
	
	public User(String id, String password, UserRole role) {
		this.id = id;
		this.password = password;
		this.role = role;
		this.status = UserStatus.Offline;
	}
	
	public String getId() {
		return id;
	}
	
	public String getPassword() {
		return password;
	}
	
	public UserStatus getStatus() {
		return status;
	}
	
	public UserRole getRole() {
		return role;
	}
	
	public void setStatus(UserStatus status) {
		this.status = status;
	}
}
