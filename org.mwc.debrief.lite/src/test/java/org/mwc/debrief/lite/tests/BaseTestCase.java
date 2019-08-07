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

import java.io.File;

import javax.swing.SwingUtilities;

import org.mwc.debrief.lite.DebriefLiteApp;

import junit.framework.TestCase;

/**
 * @author Ayesha
 *
 */
public class TestApplicationLaunch extends TestCase
{
  
  @Override
  protected void setUp() throws Exception
  {
    System.out.println("Setup");
    DebriefLiteApp.launchApp();    
    try
    {
      System.out.println("waiting");
      Thread.sleep(20000);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void main(String args[]) {
    junit.textui.TestRunner.run(TestApplicationLaunch.class);    
  }
  public void testLaunch()
  {
    System.out.println("started test");
    File[] f = new File[1];
    f[0]=new File("c:/Users/ayesha/git/debrief/org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
    SwingUtilities.invokeLater(new Runnable()
    {
      
      @Override
      public void run()
      {
        DebriefLiteApp.openRepFile(f[0]);    
      }
    });
    
  }

  @Override
  protected void tearDown() throws Exception
  {
    System.out.println("teardown");
   DebriefLiteApp.getInstance().exit();
   super.tearDown();
  }
}
