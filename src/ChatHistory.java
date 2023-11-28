import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public class ChatHistory implements Serializable {
	List<ChatMessage> messages;
	
	public ChatHistory() {
		messages = new ArrayList<ChatMessage>();
	}
	
	//TODO: what does this do
	public String getLog(String userId) {
		return null;
	}
	
	public List<ChatMessage> getMessages() {
		return messages;
	}
	
	public List<ChatMessage> getMessagesFromUser(String userId) {
		List<ChatMessage> userMessages = new ArrayList<ChatMessage>();
		for (ChatMessage message : messages) {
			if (message.getSender().equals(userId)) {
				userMessages.add(message);
			}
		}
		return userMessages;
	}
	
	public void addMessage(ChatMessage chatMessage) {
		messages.add(chatMessage);
	}
	
	public void setMessageStatus(String messageId, MessageStatus status) {
		for (ChatMessage message : messages) {
			if (message.getId().equals(messageId)) {
				message.setStatus(status);
				return;
			}
		}
	}
}
