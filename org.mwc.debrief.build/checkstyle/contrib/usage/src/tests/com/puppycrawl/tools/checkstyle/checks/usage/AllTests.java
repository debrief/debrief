/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package com.puppycrawl.tools.checkstyle.checks.usage;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTests.suite());
    }

    public static Test suite() {
        TestSuite suite =
            new TestSuite("Test for com.puppycrawl.tools.checkstyle.checks.usage");
        suite.addTest(new TestSuite(OneMethodPrivateFieldCheckTest.class));
        suite.addTest(new TestSuite(UnusedLocalVariableCheckTest.class));
        suite.addTest(new TestSuite(UnusedParameterCheckTest.class));
        suite.addTest(new TestSuite(UnusedPrivateFieldCheckTest.class));
        suite.addTest(new TestSuite(UnusedPrivateMethodCheckTest.class));

        return suite;
    }
}
