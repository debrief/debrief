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

  public void testNewFile() throws InvocationTargetException,
      InterruptedException
  {

    // load a file and on clicking new, the content of the file should be cleared
    System.out.println("Started test newfile");
    openRepFile(
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
    JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance()
        .getApplicationFrame();
    JXCollapsiblePaneWithTitle outlinePanel =
        (JXCollapsiblePaneWithTitle) TestUtils.getChildNamed(ribbonFrame,
            "Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    JTree tree = (JTree) TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()), 3);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel()
        .getChild(tree.getModel().getRoot(), 2);
    assertNotNull(node);
    assertEquals(ribbonFrame.getRibbon().getTask(1).getTitle(), "File");
    // ribbonFrame.getRibbon().setSelectedTask(DebriefRibbonFile.getFileTask());
    System.out.println("clicking new");
    JBandControlPanel liteBand = (JBandControlPanel) DebriefRibbonFile
        .getFileTask().getBand(0).getComponent(0);
    JCommandButton newButton = ((JCommandButton) liteBand.getComponent(0));
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
      try
      {
        Thread.sleep(200);
      }
      catch (InterruptedException e)
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
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()), 2);
    System.out.println("end new file test");
  }

  private void openRepFile(final String filename)
      throws InvocationTargetException, InterruptedException
  {
    File[] f = new File[1];
    f[0] = new File(filename);
    SwingUtilities.invokeAndWait(new Runnable()
    {

      public void run()
      {
        DebriefLiteApp.openRepFile(f[0]);
      }
    });
    System.out.println("done opening file");
  }

  public void testSaveAction() throws Exception
  {
    System.out.println("Start testing save");
    // open a rep file, insert some shape into it and save it, verify the shape is still there after
    // reopening.
    openRepFile(
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
    JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance()
        .getApplicationFrame();
    assertEquals(ribbonFrame.getRibbon().getTask(1).getTitle(), "File");
    /// ribbonFrame.getRibbon().setSelectedTask(DebriefRibbonFile.getFileTask());
    // insert a new shape.
    // ribbonFrame.getRibbon().setSelectedTask(ribbonFrame.getRibbon().getTask(3));
    JComboBox<String> combo = TestUtils.getActiveLayerDropDown();
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
    JBandControlPanel liteBand = (JBandControlPanel) TestUtils.getRibbonBand(3,
        2).getComponent(0);
    JCommandButton ellipseButton = (JCommandButton) ((JRibbonComponent) liteBand
        .getComponent(2)).getComponent(1);
    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        ellipseButton.doActionClick();
      }
    });
    Thread.sleep(200);
    JCommandMenuButton saveDDButton = TestUtils.getSaveButton();

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
    Thread.sleep(400);

    // wait for window to open and confirm it is open
    JFileChooser saveWindow = (JFileChooser) TestUtils.getChildIndexed(
        ribbonFrame, "JFileChooser", 0, false);
    assertNotNull("found save window", saveWindow);

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
        catch (AWTException e)
        {
          e.printStackTrace();
        }

      }
    });

    Thread.sleep(1000);

    JBandControlPanel saveBand = (JBandControlPanel) TestUtils.getRibbonBand(1,
        0).getComponent(0);
    JCommandButton closeButton = (JCommandButton) saveBand.getComponent(3);
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        closeButton.doActionClick();
      }
    });
    Thread.sleep(2000);
    File f = new File(
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.dpf");
    f.delete();
    System.out.println("Done deleting created file");
  }

  /*
   * public void testOpenFile() {
   * 
   * }
   */
  public void testClose() throws InterruptedException, InvocationTargetException
  {
    System.out.println("Starting test close");
    openRepFile(
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
    JBandControlPanel fileBand = (JBandControlPanel) TestUtils.getRibbonBand(1,
        0).getComponent(0);
    JCommandButton closeButton = (JCommandButton) fileBand.getComponent(3);
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
      catch (InterruptedException e)
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
    catch (InterruptedException e)
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

}
