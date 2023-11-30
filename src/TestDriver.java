import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

//you can also right click on Test.java and do Run As > JUnit Test
public class TestDriver {
   public static void main(String[] args) {
      Result result = JUnitCore.runClasses(Test.class);

      for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
      }
		
      System.out.println(result.wasSuccessful());
   }
}
