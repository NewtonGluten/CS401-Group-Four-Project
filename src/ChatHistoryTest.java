import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class ChatHistoryTest {

	@Test
	public void testConstructor() {
		ChatHistory chatHistory = new ChatHistory();
		assertTrue(chatHistory.getMessages().isEmpty());
	}
	
	@Test
	public void testAddMessage() {
		ChatHistory chatHistory = new ChatHistory();
		ChatMessage msg1 = new ChatMessage("user1", "hi", MessageStatus.Delivered);
		ChatMessage msg2 = new ChatMessage("user2", "lo", MessageStatus.Delivered);
		chatHistory.addMessage(msg1);
		chatHistory.addMessage(msg2);
		List<ChatMessage> messages = chatHistory.getMessages();
		assertEquals(messages.size(), 2);
		assertEquals(messages.get(0).getId(), msg1.getId());
		assertEquals(messages.get(1).getId(), msg2.getId());
	}
	
	@Test
	public void testGetMessagesFromUser() {
		ChatHistory chatHistory = new ChatHistory();
		chatHistory.addMessage(new ChatMessage("user1", "hi", MessageStatus.Delivered));
		chatHistory.addMessage(new ChatMessage("user1", "lo", MessageStatus.Delivered));
		
		List<ChatMessage> messages = chatHistory.getMessagesFromUser("user1");
		assertEquals(messages.size(), 2);
		assertEquals(messages.get(0).getSender(), "user1");
		assertEquals(messages.get(1).getSender(), "user1");
		
	}
	
	@Test 
	public void testSetStatus() {
		ChatHistory chatHistory = new ChatHistory();
		ChatMessage msg2 = new ChatMessage("user2", "lo", MessageStatus.Pending);
		chatHistory.addMessage(new ChatMessage("user1", "hi", MessageStatus.Delivered));
		chatHistory.addMessage(msg2);
		assertEquals(chatHistory.getMessages().size(), 2);
		chatHistory.setMessageStatus(msg2.getId(), MessageStatus.Delivered);
		
		ChatMessage msg = chatHistory.getMessages().get(1);
		assertEquals(msg.getStatus(), MessageStatus.Delivered);
	}

}
