package org.mwc.debrief.lite.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestCase;

@Suite.SuiteClasses({
  TestFileImport.class,
  TestFileRibbon.class,
  TestLiteLaunch.class,
  TestMenus.class
})

@RunWith(Suite.class)
public class AllTests extends TestCase
{

   
  
}
