import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;
import java.util.Date;
import java.text.SimpleDateFormat;
public class ChatMessageTest {

	@Test
	public void testNewConstructor() {
		ChatMessage chatMessage = new ChatMessage("user1", "hi", MessageStatus.Pending);
		long now = new Date().getTime();
		
		assertTrue(now - chatMessage.getTimestamp().getTime() < 100);
		
		assertEquals(chatMessage.contents(), "hi");
		assertEquals(chatMessage.getSender(), "user1");
	}
	
	@Test
	public void testStorageConstructor() {
		Date date = new Date(400000000);
		ChatMessage chatMessage = new ChatMessage("user1", date, "hello");
		
		assertTrue(date.getTime() - chatMessage.getTimestamp().getTime() < 100);
		
		assertEquals(chatMessage.contents(), "hello");
		assertEquals(chatMessage.getSender(), "user1");
		assertEquals(chatMessage.getStatus(), MessageStatus.Delivered);
	}
	
	@Test
	public void testFileConstructor() {
		ChatMessage chatMessage = new ChatMessage("[a9acc0b4-7201-4dda-9762-250290d80078][user1@11-14-23 15:52:20]:hi again");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		try {
			long time = simpleDateFormat.parse("11-14-23 15:52:20").getTime();
			assertTrue(Math.abs(time - chatMessage.getTimestamp().getTime()) < 100);
		} catch(Exception e) {
			fail();
		}
		
		assertEquals(chatMessage.getId(), "a9acc0b4-7201-4dda-9762-250290d80078");
		assertEquals(chatMessage.getSender(), "user1");
		assertEquals(chatMessage.getStatus(), MessageStatus.Delivered);
		assertEquals(chatMessage.contents(), "hi again");
	}
	
	@Test 
	public void testSetStatus() {
		ChatMessage chatMessage = new ChatMessage("user1", "hi", MessageStatus.Pending);
		chatMessage.setStatus(MessageStatus.Delivered);
		assertEquals(chatMessage.getStatus(), MessageStatus.Delivered);
	}

}
