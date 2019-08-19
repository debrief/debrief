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

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.gui.custom.JXCollapsiblePaneWithTitle;
import org.mwc.debrief.lite.menu.DebriefRibbonFile;
import org.mwc.debrief.lite.utils.TestUtils;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandMenuButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.internal.ui.ribbon.JBandControlPanel;

/**
 * @author Ayesha
 *
 */
public class TestFileRibbon extends BaseTestCase
{
  
  public void testNewFile() {
  
    //load a file and on clicking new, the content of the file should be cleared
    openRepFile("../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
    System.out.println("done waiting for file load");
    JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    JXCollapsiblePaneWithTitle outlinePanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame,"Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    JTree tree = (JTree)TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),3);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 2);
    assertNotNull(node);
    assertEquals(ribbonFrame.getRibbon().getTask(1).getTitle(),"File");
    ribbonFrame.getRibbon().setSelectedTask(DebriefRibbonFile.getFileTask());
    System.out.println("clicking new");
    JBandControlPanel liteBand = (JBandControlPanel)DebriefRibbonFile.getFileTask().getBand(0).getComponent(0);
    JCommandButton newButton = ((JCommandButton)liteBand.getComponent(0));
    SwingUtilities.invokeLater(new Runnable()
    {
      
      @Override
      public void run()
      {
        newButton.doActionClick();    
      }
    });
    System.out.println("clicked new");
    
    //check for the dialog
    JButton ok=null;
    for (int i = 0; ok == null; ++i) {
      try
      {
        Thread.sleep(200);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
      ok = (JButton)TestUtils.getChildIndexed(ribbonFrame, "JButton", 1);
      assertTrue(i < 10);
     }
     assertEquals(
           "No", ok.getText());
     final JButton no = ok;
     SwingUtilities.invokeLater(new Runnable()
    {
      
      @Override
      public void run()
      {
        // TODO Auto-generated method stub
        no.doClick();
      }
    });
     //wait for reset to be over.
     try
     {
       Thread.sleep(200);
     }
     catch (InterruptedException e)
     {
       e.printStackTrace();
     }
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),2);
  }
  private void openRepFile(final String filename)
  {
    File[] f = new File[1];
    
    f[0]=new File(filename);
    SwingUtilities.invokeLater(new Runnable()
    {
      
      public void run()
      {
        DebriefLiteApp.openRepFile(f[0]);    
      }
    });
    try
    {
      Thread.sleep(500);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
    
  }
  public void testSaveAction()
  {
    //open a rep file, insert some shape into it and save it, verify the shape is still there after reopening.
    openRepFile("../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
    System.out.println("done waiting for file load");
    JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    assertEquals(ribbonFrame.getRibbon().getTask(1).getTitle(),"File");
    ribbonFrame.getRibbon().setSelectedTask(DebriefRibbonFile.getFileTask());
    System.out.println("clicking new");
    //insert a new shape.
    ribbonFrame.getRibbon().setSelectedTask(ribbonFrame.getRibbon().getTask(3));
    JComboBox<String> combo = TestUtils.getActiveLayerDropDown();
    SwingUtilities.invokeLater(new Runnable()
    {
      
      @Override
      public void run()
      {
        combo.requestFocus();    
      }
    });
    try {
      Thread.sleep(100);
    }catch(InterruptedException e) {}
    final String item = combo.getItemAt(1);
    SwingUtilities.invokeLater(new Runnable()
    {
      
      @Override
      public void run()
      {
        combo.setSelectedItem(item);    
      }
    });
    try {
      Thread.sleep(100);
    }catch(InterruptedException e) {}
    JBandControlPanel liteBand = (JBandControlPanel)TestUtils.getRibbonBand(3, 2).getComponent(0);
    JCommandButton ellipseButton = (JCommandButton)((JRibbonComponent)liteBand.getComponent(2)).getComponent(1);
    SwingUtilities.invokeLater(new Runnable()
    {
      
      @Override
      public void run()
      {
        ellipseButton.doActionClick();    
      }
    });
    try {
      Thread.sleep(200);
    }catch(InterruptedException e) {}
    JCommandMenuButton saveDDButton = TestUtils.getSaveButton();
    
    SwingUtilities.invokeLater(new Runnable()
    {
      
      @Override
      public void run()
      {
        saveDDButton.doActionClick();    
      }
    });
    try {
      Thread.sleep(200);
    }catch(InterruptedException e) {}
    //save as dpf file if it is first a rep file.
    JFileChooser saveWindow=null;
    for (int i = 0; saveWindow == null; ++i) {
      try
      {
        Thread.sleep(400);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
      saveWindow=(JFileChooser)TestUtils.getChildIndexed(ribbonFrame, "JFileChooser", 0);
      assertTrue(i < 10);
     }
    System.out.println("Save button:");
    final JFileChooser saveButton = saveWindow;
    new Thread(new Runnable() {
      @Override
      public void run() {
          Robot robot;
          try
          {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_ENTER);
          }
          catch (AWTException e)
          {
            e.printStackTrace();
          }
          
      }
  }).start();
    try {
      Thread.sleep(1000);
    }catch(InterruptedException e) {}
    
    JBandControlPanel saveBand = (JBandControlPanel)TestUtils.getRibbonBand(1,0).getComponent(0);
    JCommandButton closeButton = (JCommandButton)saveBand.getComponent(3);
    SwingUtilities.invokeLater(new Runnable()
    {
      
      @Override
      public void run()
      {
        closeButton.doActionClick();
      }
    });
    File f = new File("../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.dpf");
    f.delete();
    
  }
 }
  
