/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.lite.tests;

import javax.swing.SwingUtilities;

import org.mwc.debrief.lite.DebriefLiteApp;

import junit.framework.TestCase;

/**
 * @author Ayesha
 *
 */
public abstract class BaseTestCase extends TestCase
{
  
  protected void setUp() throws Exception
  {
    System.out.println("Setup");
    DebriefLiteApp.launchApp();
    Thread.sleep(3000);
  }
  

  protected void tearDown() throws Exception
  {
    System.out.println("teardown");
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        DebriefLiteApp.disposeForTest();
      }
    });
    Thread.sleep(3000);
    super.tearDown();
  }
}
