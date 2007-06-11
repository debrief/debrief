package MWC.Utilities.Errors.Testing;
import java.util.*;
import junit.framework.*;
import java.io.*;
import java.lang.reflect.*;
/**
 * Test suite for the entire Java implementation. Runs
 * all Java test cases in the source tree that extend TestCase and are of a specified type.
 * The following system properties must be set:
 * <ul>
 * <li><code>class_root</code>: Directory containing classes. e.g /product/classes
 * <li><code>test_type</code>: Type of test case to load. All test cases with public static final String TEST_ALL_TEST_TYPE defined
 * to match the value of <code>test_type</code> will be loaded.
 * </ul>
 */
public class TestAll extends TestCase {
    /**
     * Given a test to ignore (normally TestdSecure.class) this method iterates over all java files
     * in the <code>currentDirectory</code> (recursively). It takes each java file and passes it to
     * <code>addToSuiteIfTestCase ()</code>. The result is the <code>suite</code> parameter fully
     * populated with tests.
     */

    private static int addAllTests(final TestSuite suite, final Iterator classIterator)
    throws java.io.IOException {
        int testClassCount = 0;
        while (classIterator.hasNext ()) {
            Class testCaseClass = (Class)classIterator.next ();
            suite.addTest (new TestSuite (testCaseClass));
       //     System.out.println ("Loaded test case: " + testCaseClass.getName ());
            testClassCount++;
        }
        return testClassCount;
    }

    public static Test suite()
    throws Throwable {
        try {
          TestSuite suite = new TestSuite();
//            String classRootString = System.getProperty("class_root");
//          System.out.println("loading classes from:" + classRootString);
//            if (classRootString == null) throw new IllegalArgumentException ("System property class_root must be set.");
//            String testType = System.getProperty("test_type");
//            if (testType == null) throw new IllegalArgumentException ("System property test_type must be set.");
//            File classRoot = new File(classRootString);
//            ClassFinder classFinder = new ClassFinder (classRoot);
//            TestCaseLoader testCaseLoader = new TestCaseLoader (testType);
//            testCaseLoader.loadTestCases (classFinder.getClasses ());
//            int numberOfTests = addAllTests (suite, testCaseLoader.getClasses ());
//            System.out.println("Number of test classes found: " + numberOfTests);
            return suite;
        } catch (Throwable t) {
            // This ensures we have extra information. Otherwise all we get is a "Could not invoke the suite method." message.
            t.printStackTrace ();
            throw t;
        }
    }

    /**
     * Basic constructor - called by the test runners.
     */

    public TestAll(String s) {
        super(s);
    }


  public static void main(String[] args) {
    TestAll ts = new TestAll("test by hand");
    System.setProperty("test_type", "UNIT");
//    System.setProperty("class_root", "c:\\temp\\d2001\\d2002\\mwc\\gui\\java3d");
    System.setProperty("class_root", "d:\\dev\\debrief\\source\\build\\debrief");
    try {
      Test test = TestAll.suite();
      TestResult tr = new TestResult();
      test.run(tr);
    }
    catch (Throwable throwable) {
      throwable.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
  }

}
