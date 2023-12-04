import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class ChatHistoryTest {

	@Test
	public void testConstructor() {
		//ensure it is empty by default
		ChatHistory chatHistory = new ChatHistory();
		assertTrue(chatHistory.getMessages().isEmpty());
	}
	
	@Test
	public void testAddMessage() {
		ChatHistory chatHistory = new ChatHistory();
		//add 2 messages
		ChatMessage msg1 = new ChatMessage("user1", "hi", MessageStatus.Delivered);
		ChatMessage msg2 = new ChatMessage("user2", "lo", MessageStatus.Delivered);
		chatHistory.addMessage(msg1);
		chatHistory.addMessage(msg2);
		//compare the history
		List<ChatMessage> messages = chatHistory.getMessages();
		assertEquals(messages.size(), 2);
		assertEquals(messages.get(0).getId(), msg1.getId());
		assertEquals(messages.get(1).getId(), msg2.getId());
	}
	
	@Test
	public void testGetMessagesFromUser() {
		//create a chat history and add messages
		ChatHistory chatHistory = new ChatHistory();
		chatHistory.addMessage(new ChatMessage("user1", "hi", MessageStatus.Delivered));
		chatHistory.addMessage(new ChatMessage("user1", "lo", MessageStatus.Delivered));
		//ensure the message sender info matches
		List<ChatMessage> messages = chatHistory.getMessagesFromUser("user1");
		assertEquals(messages.size(), 2);
		assertEquals(messages.get(0).getSender(), "user1");
		assertEquals(messages.get(1).getSender(), "user1");
		
	}
	
	@Test 
	public void testSetStatus() {
		//createa a chat history with messages
		ChatHistory chatHistory = new ChatHistory();
		ChatMessage msg2 = new ChatMessage("user2", "lo", MessageStatus.Pending);
		chatHistory.addMessage(new ChatMessage("user1", "hi", MessageStatus.Delivered));
		chatHistory.addMessage(msg2);
		//ensure the messages were added
		assertEquals(chatHistory.getMessages().size(), 2);
		//change the status
		chatHistory.setMessageStatus(msg2.getId(), MessageStatus.Delivered);
		//ensure the status was changed
		ChatMessage msg = chatHistory.getMessages().get(1);
		assertEquals(msg.getStatus(), MessageStatus.Delivered);
	}

}
