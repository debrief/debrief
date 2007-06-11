package MWC.Utilities.Errors.Testing;

import java.util.*;

import junit.framework.*;

public class MTTest extends MultiThreadedTestCase {
    /**
     * Basic constructor - called by the test runners.
     */
    public MTTest(String s) {
        super (s);
    }

    public static final String TEST_ALL_TEST_TYPE = "UNIT";


    public void test1 ()
    {
        TestCaseRunnable tct [] = new TestCaseRunnable [100];
        for (int i = 0; i < tct.length; i++)
        {
            tct[i] = new TestCaseRunnable () {
                public void runTestCase () {
                    assertTrue (true);
                }
            };
            runTestCaseRunnables (tct);
        }
    }
}