/**
 * Right-click on this class in Java perspective PackageExplorer and select Run As > Junit test.
 * This will start all tests. 
 */
package org.mwc.debrief.test;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.extensions.cpsuite.ClasspathSuite.ClasspathProperty;
import org.junit.extensions.cpsuite.ClasspathSuite.SuiteTypes;
import org.junit.runner.RunWith;

@RunWith(ClasspathSuite.class)  
@ClasspathProperty("classpathsuite.path")
@SuiteTypes({org.junit.extensions.cpsuite.SuiteType.JUNIT38_TEST_CLASSES, 
	org.junit.extensions.cpsuite.SuiteType.TEST_CLASSES})
@ClassnameFilters({"ASSET.*", "Debrief.*", "MWC.*", "org.mwc.asset.*", "org.mwc.cmap.core.*", 
	"org.mwc.cmap.g*", "org.mwc.cmap.p*", "org.mwc.cmap.debrief.*"})

public class AllTests {}
