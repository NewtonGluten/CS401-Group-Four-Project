import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({
	UserTest.class, 
	ChatMessageTest.class, 
	ChatHistoryTest.class, 
	RoomTest.class, 
	UserStorageTest.class, 
	AuthenticatorTest.class,
	RoomStorageTest.class,
	//MessageTest.class
})

public class Test {
}