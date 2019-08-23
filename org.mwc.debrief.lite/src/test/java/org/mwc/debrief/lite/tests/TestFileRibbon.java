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
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Plottable;

/**
 * @author Ayesha
 *
 */
public class TestFileRibbon extends BaseTestCase
{

  private void openRepFile(final String filename)
      throws InvocationTargetException, InterruptedException
  {
    final File[] f = new File[1];
    f[0] = new File(filename);
    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        DebriefLiteApp.openRepFile(f[0]);
      }
    });
    System.out.println("done opening file");
  }

  
  public void testClose() throws InterruptedException, InvocationTargetException
  {
    System.out.println("Starting test close");
    openRepFile(
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
    final JBandControlPanel fileBand = (JBandControlPanel) TestUtils
        .getRibbonBand(1, 0).getComponent(0);
    final JCommandButton closeButton = (JCommandButton) fileBand.getComponent(
        3);
    assertTrue(closeButton.isEnabled());
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        closeButton.doActionClick();
      }
    });
    //
    // check for the dialog
    JButton ok = null;
    for (int i = 0; ok == null; ++i)
    {
      try
      {
        Thread.sleep(200);
      }
      catch (final InterruptedException e)
      {
        e.printStackTrace();
      }
      ok = (JButton) TestUtils.getChildIndexed(DebriefLiteApp.getInstance()
          .getApplicationFrame(), "JButton", 1, true);
      assertTrue(i < 20);
    }
    assertEquals("No", ok.getText());
    final JButton no = ok;
    SwingUtilities.invokeAndWait(new Runnable()
    {
      @Override
      public void run()
      {
        no.doClick();
      }
    });

    // wait for reset to be over.
    try
    {
      Thread.sleep(200);
    }
    catch (final InterruptedException e)
    {
      e.printStackTrace();
    }
    assertFalse(closeButton.isEnabled());
    System.out.println("Done test close");
  }

  public void testImportRepFile() throws InvocationTargetException,
      InterruptedException
  {
    System.out.println("start testing import rep file");
    openRepFile(
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
    System.out.println("done testing import rep file");
  }
  /*
   * public void testImportNMEAFile() {
   *
   * }
   */

  public void testNewFile() throws InvocationTargetException,
      InterruptedException
  {

    // load a file and on clicking new, the content of the file should be cleared
    System.out.println("Started test newfile");
    openRepFile(
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
    final JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance()
        .getApplicationFrame();
    final JXCollapsiblePaneWithTitle outlinePanel =
        (JXCollapsiblePaneWithTitle) TestUtils.getChildNamed(ribbonFrame,
            "Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    final JTree tree = (JTree) TestUtils.getChildNamed(outlinePanel,
        "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()), 3);
    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel()
        .getChild(tree.getModel().getRoot(), 2);
    assertNotNull(node);
    assertEquals(ribbonFrame.getRibbon().getTask(1).getTitle(), "File");
    // ribbonFrame.getRibbon().setSelectedTask(DebriefRibbonFile.getFileTask());
    System.out.println("clicking new");
    final JBandControlPanel liteBand = (JBandControlPanel) DebriefRibbonFile
        .getFileTask().getBand(0).getComponent(0);
    final JCommandButton newButton = ((JCommandButton) liteBand.getComponent(
        0));
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        newButton.doActionClick();
      }
    });
    System.out.println("clicked new");

    // check for the dialog
    JButton ok = null;
    for (int i = 0; ok == null; ++i)
    {
      Thread.sleep(200);
      ok = (JButton) TestUtils.getChildIndexed(ribbonFrame, "JButton", 1, true);
      assertTrue(i < 10);
    }
    assertEquals("No", ok.getText());
    final JButton no = ok;

    SwingUtilities.invokeLater(new Runnable()
    {

      @Override
      public void run()
      {
        no.doClick();
        System.out.println("Clicked no");
      }
    });
    // wait for reset to be over.
    try
    {
      Thread.sleep(200);
    }
    catch (final InterruptedException e)
    {
      e.printStackTrace();
    }
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()), 2);
    System.out.println("end new file test");
  }

  public void testSaveAction() throws Exception
  {
    System.out.println("Start testing save");
    // open a rep file, insert some shape into it and save it, verify the shape is still there after
    // reopening.
    openRepFile(
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
    final JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance()
        .getApplicationFrame();
    assertEquals(ribbonFrame.getRibbon().getTask(1).getTitle(), "File");
    /// ribbonFrame.getRibbon().setSelectedTask(DebriefRibbonFile.getFileTask());
    // insert a new shape.
    // ribbonFrame.getRibbon().setSelectedTask(ribbonFrame.getRibbon().getTask(3));
    final JComboBox<String> combo = TestUtils.getActiveLayerDropDown();
    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        combo.requestFocus();
      }
    });
    Thread.sleep(100);
    final String item = combo.getItemAt(1);
    SwingUtilities.invokeLater(new Runnable()
    {

      @Override
      public void run()
      {
        combo.setSelectedItem(item);
      }
    });
    Thread.sleep(100);
    final JBandControlPanel liteBand = (JBandControlPanel) TestUtils
        .getRibbonBand(3, 2).getComponent(0);
    final JCommandButton ellipseButton =
        (JCommandButton) ((JRibbonComponent) liteBand.getComponent(2))
            .getComponent(1);
    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        ellipseButton.doActionClick();
      }
    });
    Thread.sleep(200);
    final JCommandMenuButton saveDDButton = TestUtils.getSaveButton();

    /**
     * note: the following call appears to require invokeLater() rather than invokeAndWait. This is
     * because the modal dialog will block until there is user intervention (click)
     */
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        saveDDButton.doActionClick();
      }
    });
    JFileChooser saveWindow=null;
    for(int i=0;saveWindow == null;++i) {

    // wait for window to open and confirm it is open
        saveWindow = (JFileChooser) TestUtils.getChildIndexed(
        ribbonFrame, "JFileChooser", 0, false);
        Thread.sleep(200);
        assertTrue(i<10);
    }
    Thread.sleep(200);
    // now actually push the save button
    System.out.println("Save button:");

    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        Robot robot;
        try
        {
          robot = new Robot();
          robot.keyPress(KeyEvent.VK_ENTER);
        }
        catch (final AWTException e)
        {
          e.printStackTrace();
        }

      }
    });

    Thread.sleep(1000);

    final JBandControlPanel saveBand = (JBandControlPanel) TestUtils
        .getRibbonBand(1, 0).getComponent(0);
    final JCommandButton closeButton = (JCommandButton) saveBand.getComponent(
        3);
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        closeButton.doActionClick();
      }
    });
    Thread.sleep(2000);
    final File f = new File(
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.dpf");
    f.delete();
    System.out.println("Done deleting created file");
  }
  
  public void testImportNMEAFile() throws InterruptedException,InvocationTargetException
  {
    SwingUtilities.invokeAndWait(new Runnable()
    {

      public void run()
      {
        DebriefLiteApp.openNMEAFile(new File("../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/NMEA_TRIAL.log"));    
      }
    });
    JFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    JXCollapsiblePaneWithTitle outlinePanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame,"Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    JTree tree = (JTree)TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),6);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 5);
    assertNotNull(node);
    assertEquals("contacts validation failed",14,node.getChildCount());
    node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 2);
    assertTrue("didnt find a track object",node.getUserObject() instanceof TrackWrapper);
    assertEquals("invalid childcount for :"+node,node.getFirstChild().getChildCount(),1395);
    node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 3);
    assertTrue("didnt find a track object",node.getUserObject() instanceof TrackWrapper);
    assertEquals("invalid childcount for :"+node,node.getFirstChild().getChildCount(),1327);
    node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 4);
    assertTrue("didnt find a track object",node.getUserObject() instanceof TrackWrapper);
    assertEquals("invalid childcount for :"+node,node.getFirstChild().getChildCount(),9);
    System.out.println("done opening file");
  }
  
  public void testImportMultipleFiles()throws InterruptedException,InvocationTargetException
  {
    //when you import more than one file it should just import to the same file.
    //try adding boat1.rep and nmea file and verify all the elements are added in the outlline panel.
    System.out.println("Start testing save");
    // open a rep file, insert some shape into it and save it, verify the shape is still there after
    // reopening.
    openRepFile(
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
    final JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance()
        .getApplicationFrame();
    JXCollapsiblePaneWithTitle outlinePanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame,"Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    JTree tree = (JTree)TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),3);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 2);
    assertNotNull(node);
    
    assertFalse(node.isLeaf());
    assertNotNull(node.getFirstChild());
    DefaultMutableTreeNode treenode = (DefaultMutableTreeNode)node.getFirstChild();
    Plottable object = (Plottable)treenode.getUserObject();
    assertEquals(object.getName(),"120500.00");
    
    node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 0);
    assertNotNull(node);
    assertTrue(node.isLeaf());
    node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 1);
    assertTrue(node.isLeaf());
    assertNotNull(node);
    SwingUtilities.invokeAndWait(new Runnable()
    {

      public void run()
      {
        DebriefLiteApp.openNMEAFile(new File("../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/NMEA_TRIAL.log"));    
      }
    });
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),7);
    node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 6);
    assertNotNull(node);
    assertEquals("contacts validation failed",14,node.getChildCount());
    node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 3);
    assertTrue("didnt find a track object",node.getUserObject() instanceof TrackWrapper);
    assertEquals("invalid childcount for :"+node,node.getFirstChild().getChildCount(),1395);
    node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 4);
    assertTrue("didnt find a track object",node.getUserObject() instanceof TrackWrapper);
    assertEquals("invalid childcount for :"+node,node.getFirstChild().getChildCount(),1327);
    node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 5);
    assertTrue("didnt find a track object",node.getUserObject() instanceof TrackWrapper);
    assertEquals("invalid childcount for :"+node,node.getFirstChild().getChildCount(),9);
    System.out.println("done opening file");
    JBandControlPanel panel = (JBandControlPanel)TestUtils.getRibbonBand(1, 0).getComponent(0);
    JCommandButton closeButton = (JCommandButton)panel.getComponent(3);
    SwingUtilities.invokeLater(new Runnable()
    {
      
      @Override
      public void run()
      {
        closeButton.doActionClick();
        
      }
    });
    Thread.sleep(300);
    JButton ok = null;
    for (int i = 0; ok == null; ++i)
    {
      try
      {
        Thread.sleep(200);
      }
      catch (final InterruptedException e)
      {
        e.printStackTrace();
      }
      ok = (JButton) TestUtils.getChildIndexed(ribbonFrame, "JButton", 1, true);
      assertTrue(i < 20);
    }
    assertEquals("No", ok.getText());
    final JButton no = ok;

    SwingUtilities.invokeLater(new Runnable()
    {

      @Override
      public void run()
      {
        no.doClick();
      }
    });
    // wait for reset to be over.
    try
    {
      Thread.sleep(200);
    }
    catch (final InterruptedException e)
    {
      e.printStackTrace();
    }
    
  }
  
  public void testOpenDpfFile()throws InterruptedException,InvocationTargetException
  {
    JBandControlPanel liteBand = (JBandControlPanel)TestUtils.getRibbonBand(1,0).getComponent(0);
    JCommandButton openButton = (JCommandButton)liteBand.getComponent(1);
    SwingUtilities.invokeLater(new Runnable()
    {
      
      @Override
      public void run()
      {
        openButton.doActionClick();
      }
    });
    JFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    //wait for file chooser dialog
    JFileChooser fc=null;
    final File file = new File("../org.mwc.cmap.combined.feature/root_installs/sample_data/sample.dpf").getAbsoluteFile();
    
    for(int i=0;fc == null;++i) {

    // wait for window to open and confirm it is open
        fc = (JFileChooser) TestUtils.getChildIndexed(
        ribbonFrame, "JFileChooser", 0, false);
        Thread.sleep(200);
        assertTrue(i<10);
    }
    Thread.sleep(200);
    final JFileChooser saveWindow = fc;
    //we have the window, set the path on the filechooser dialog to open the file
    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        if(saveWindow!=null)
        {
          saveWindow.setSelectedFile(file);
        }
        Robot robot;
        try
        {
          robot = new Robot();
          robot.delay(200);
          robot.keyPress(KeyEvent.VK_ENTER);
        }
        catch (final AWTException e)
        {
          e.printStackTrace();
        }

      }
    });

    Thread.sleep(1000);
    //verify the file got opened
    //do save and close
    JCommandButton saveButton = TestUtils.getSaveButton();
    SwingUtilities.invokeAndWait(new Runnable()
    {
      
      @Override
      public void run()
      {
        saveButton.doActionClick();
      }
    });
    Thread.sleep(200);
    JXCollapsiblePaneWithTitle outlinePanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame,"Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    JTree tree = (JTree)TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),7);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getModel().getRoot();
    assertEquals(node.getLastChild().getChildCount(),19);
    
  }

  

}
