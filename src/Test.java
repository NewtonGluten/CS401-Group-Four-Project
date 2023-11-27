import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({
	UserTest.class, UserStorageTest.class, AuthenticatorTest.class
})

public class Test {
}