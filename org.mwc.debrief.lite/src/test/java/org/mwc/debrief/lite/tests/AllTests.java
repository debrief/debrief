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
