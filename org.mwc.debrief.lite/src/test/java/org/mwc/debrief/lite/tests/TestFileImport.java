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
import java.lang.reflect.InvocationTargetException;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.gui.custom.JXCollapsiblePaneWithTitle;
import org.mwc.debrief.lite.utils.TestUtils;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;

import MWC.GUI.Plottable;

/**
 * @author Ayesha
 *
 */
public class TestFileImport extends BaseTestCase
{
  //load a file that doesnt exist
  //load a file that exists and see if it is loaded
  
  public void testImportRepFile() throws InvocationTargetException, InterruptedException
  {
    System.out.println("started test");
    File[] f = new File[] {new File("../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep")};
    SwingUtilities.invokeAndWait(new Runnable()
    {
      @Override
      public void run()
      {
        DebriefLiteApp.openRepFile(f[0]);    
      }
    });
    //do assertions here
    //outline view should have track nelson and by default should have layers chart features and background
    JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
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
  }

}
