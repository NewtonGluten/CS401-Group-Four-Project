import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Room implements Serializable {
	private static final long serialVersionUID = -3423914092361594732L;
	private String id;
	private String title;
	private Date creationDate;
	private List<String> users;
	private ChatHistory chatHistory;
	private boolean empty;
	
	public Room(List<String> users) {
		id = UUID.randomUUID().toString();
		creationDate = new Date();
		this.users = new ArrayList<String>();
		for (String userId : users) {
			this.users.add(userId);
		}
		chatHistory = new ChatHistory();
		empty = this.users.isEmpty();		
		title = users.get(0) + "'s Room";
	}
	
	public Room(List<String> users, String title) {
		id = UUID.randomUUID().toString();
		creationDate = new Date();
		this.users = new ArrayList<String>();
		for (String userId : users) {
			this.users.add(userId);
		}
		chatHistory = new ChatHistory();
		empty = this.users.isEmpty();		
		this.title = title;
	}
	
	public Room(File file) {
		id = file.getName().substring(0, 36);
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
			title = scanner.nextLine();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
			creationDate = simpleDateFormat.parse(scanner.nextLine());
			users = new ArrayList<String>();
			String line = scanner.nextLine();
			for (String userId : line.split(",")) {
				if (userId != null && !userId.isEmpty())
					users.add(userId);
			}
			chatHistory = new ChatHistory();
			while (scanner.hasNextLine()) {
				chatHistory.addMessage(new ChatMessage(scanner.nextLine()));
			}
			empty = this.users.isEmpty();
		} catch(Exception e) {
			System.out.println("File not found");
		}
		if (scanner != null)
			scanner.close();
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public List<String> getUsers() {
		return users;
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
		chatHistory.addMessage(message);
	}
	
	public void setMessageStatus(String messageId, MessageStatus status) {
		chatHistory.setMessageStatus(messageId, status);
	}
	
	public String toString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		String file = title + "\n" + simpleDateFormat.format(creationDate) + '\n';
		if (empty)
			file += '\n';
		else {
			for (String userId : users) {
				file += userId + ',';
			}
			file += '\n';
		}
		for (ChatMessage message : chatHistory.getMessages()) {
			file += message.toString() + '\n';
		}
		return file;
	}
}
