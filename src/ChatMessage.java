import java.util.Date;
import java.util.UUID;

import java.io.Serializable;

enum MessageStatus {
	Pending, Delivered
}

public class ChatMessage implements Serializable {
	private static final long serialVersionUID = 3852684761703377574L;
	private String id;
	private Date timestamp;
	private String sender;
	private String contents;
	private MessageStatus status;
	
	public ChatMessage(String userId, String contents, MessageStatus status) {
		id = UUID.randomUUID().toString();
		timestamp = new Date();
		sender = userId;
		this.contents = contents;
		this.status = status;
	}
	
	public ChatMessage(String userId, Date timestamp, String contents) {
		id = UUID.randomUUID().toString();
		this.timestamp = timestamp;
		sender = userId;
		this.contents = contents;
		status = MessageStatus.Delivered;
	}
	
	public String getId() {
		return id;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String contents() {
		return contents;
	}
	
	public MessageStatus getStatus() {
		return status;
	}
	
	public void setStatus(MessageStatus status) {
		this.status = status;
	}
}
