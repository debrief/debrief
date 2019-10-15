package org.mwc.debrief.lite.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestCase;

@Suite.SuiteClasses({
  TestLiteLaunch.class,
  TestMenus.class,
  TestFileRibbon.class,
  TestInsertRibbon.class
})

@RunWith(Suite.class)
public class AllTests extends TestCase
{

   
  
}
