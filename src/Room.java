import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Room {
	private String id;
	private Date creationDate;
	private List<String> users;
	private ChatHistory chatHistory;
	private boolean empty;
	
	public Room(String[] users) {
		this.users = new ArrayList<String>();
		for (String userId : users) {
			this.users.add(userId);
		}
		empty = this.users.isEmpty();
	}
	
	//TODO: a file constructor could be useful here
	public Room(String filename) {
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	public String getId() {
		return id;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public List<String> getUsers() {
		return users;
	}
	
	//TODO: how does this work
	public String getLog() {
		return null;
	}
	
	public List<ChatMessage> getMessages() {
		return chatHistory.getMessages();
	}
	
	public void addUser(String userId) {
		users.add(userId);
		empty = users.isEmpty();
	}
	
	public void removeUser(String userId) {
		users.remove(userId);
		empty = users.isEmpty();
	}
	
	public void addMessage(ChatMessage message) {
		chatHistory.addMessage(null);
	}
	
	public void setMessageStatus(String messageId, MessageStatus status) {
		chatHistory.setMessageStatus(messageId, status);
	}
}
