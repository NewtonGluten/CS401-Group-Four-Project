import java.util.Date;
import java.util.UUID;

import java.io.Serializable;

import java.text.SimpleDateFormat;

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
	
	//TODO: file constructor could be useful here
	public ChatMessage(String line) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		try {
			sender = line.substring(1, 37);
			
			//starts at 2nd char and is length 36
			timestamp = simpleDateFormat.parse(line.substring(1, 37));
			System.out.println(timestamp);
			
		} catch(Exception e) {
			System.out.println("Invalid Format");
		}
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
	
	public String toString() {
		return '[' + id + "][" + sender + '@' + timestamp.toString() + "]:" + contents;
	}
}
