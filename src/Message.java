import java.io.Serializable;
import java.util.ArrayList; 
import java.util.List;

public class Message implements Serializable {
		
	protected MessageType type;
	protected List<String> users;
	protected String username;
	protected String password;
	protected String login_status;
	protected String room_id;
	protected String user_id;
	protected String contents;
	protected List<Room> rooms;

	public Message (MessageType type){
		this.type = type;
		
		//Setting everything else to null so that unused 
		//message attributes don't contribute much to message size
		this.users = null;
		this.username = null;
		this.password = null;
		this.login_status = null;
		this.room_id = null;
		this.room_id = null;
		this.contents = null;
		this.rooms = null;
	}
	
	//making these immutable by not letting them get changed after they're assigned
	public void setUsername (String username){
		if (this.username == null) {
			this.username = new String(username);
		}
	}
	
	public void setPassword (String password){
		if (this.password == null) {
			this.password = new String(password);
		}
	}
	
	//TODO: Changed to list because getUserRooms in UserStorage is list. 
	public void setUsers (List<String> users) {
		if (this.users == null) {
			this.users = users;	
		}
	}
	
	public void setLoginStatus (String status) {
		if (this.login_status == null) {
			this.login_status = new String (status);
		}
	}
	
	public void setRoomId (String room_id) {
		if (this.room_id == null) {
			this.room_id = new String (room_id);
		}
	}
	
	public void setUserId (String user_id) {
		if (this.user_id == null) {
			this.user_id = new String (user_id);
		}
	}
	
	public void setContents (String contents) {
		if (this.contents == null) {
			this.contents = contents; 
		}
	}
	
	//TODO: Changed to list<Room> to reflect return data type in RoomStorage
	public void setRooms (List<Room> room) {
		if (this.rooms == null) {
			this.rooms = room;
		}
	}
	
	public MessageType getType() {
		return this.type;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getLoginStatus() {
		return this.login_status;
	}
	
	public String getRoomId() {
		return this.room_id;
	}
	
	public String getUserId() {
		return this.user_id;
	}
	
	public String getContents() {
		return this.contents;
	}
	
	//TODO: modified to list<Room> to reflect return data type in roomStorage
	public List<Room> getRooms(){
		return this.rooms;
	}
	
}
