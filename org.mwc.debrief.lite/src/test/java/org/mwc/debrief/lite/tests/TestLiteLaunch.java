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

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.gui.custom.JXCollapsiblePaneWithTitle;
import org.mwc.debrief.lite.utils.TestUtils;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;

/**
 * @author Ayesha
 *
 */
public class TestLiteLaunch extends BaseTestCase
{

  public void testTitleAndIcon()
  {
    JFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    assertNotNull(ribbonFrame.getIconImage());
    assertNotNull(ribbonFrame.getTitle());
    assertTrue(ribbonFrame.getTitle().contains("Debrief Lite"));
  }
  
  public void testTaskBarButtons()
  {
    JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
//    assertNotNull(ribbonFrame.getRibbon().getTaskbarCommands());
//    assertTrue(ribbonFrame.getRibbon().getTaskbarCommands().size()==3);
//    assertFalse(ribbonFrame.getRibbon().getTaskbarCommands().get(0).isEnabled());
//    assertFalse(ribbonFrame.getRibbon().getTaskbarCommands().get(1).isEnabled());
//    assertTrue(ribbonFrame.getRibbon().getTaskbarCommands().get(2).isEnabled());
//    assertTrue(ribbonFrame.getRibbon().getTaskbarCommands().get(0).getTitle().equals("Undo"));
//    assertTrue(ribbonFrame.getRibbon().getTaskbarCommands().get(1).getTitle().equals("Redo"));
//    assertTrue(ribbonFrame.getRibbon().getTaskbarCommands().get(2).getTitle().equals("Collapse"));
  }
  
  public void testRibbonTabs()
  {
    JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    assertEquals(ribbonFrame.getRibbon().getTaskCount(),5);
    assertEquals(ribbonFrame.getRibbon().getTask(0).getTitle(),"Lite");
    assertEquals(ribbonFrame.getRibbon().getTask(1).getTitle(),"File");
    assertEquals(ribbonFrame.getRibbon().getTask(2).getTitle(),"View");
    assertEquals(ribbonFrame.getRibbon().getTask(3).getTitle(),"Insert");
    assertEquals(ribbonFrame.getRibbon().getTask(4).getTitle(),"Time");
  }
  
  public void testPanels()
  {
    JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    Component outlinePanel = TestUtils.getChildNamed(ribbonFrame,"Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    doOutlinePanelAssertions(outlinePanel);
    JXCollapsiblePaneWithTitle graphPanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame, "Graph");
    assertTrue(graphPanel.isCollapsed());
    JXCollapsiblePaneWithTitle narrativesPanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame, "Narratives");
    assertTrue(narrativesPanel.isCollapsed());
  } 
  public void doOutlinePanelAssertions(Component outlinePanel)
  {
    JButton button1 = (JButton)TestUtils.getChildNamed(outlinePanel, "Edit");
    assertNotNull(button1);
    assertTrue(button1.isEnabled());
    JTree tree = (JTree)TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),2);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 1);
    assertNotNull(node);
    assertEquals("last item is not selected:",1,tree.getSelectionCount());
    button1 = (JButton)TestUtils.getChildNamed(outlinePanel, "Cut");
    assertNotNull(button1);
    assertFalse(button1.isEnabled());
    button1 = (JButton)TestUtils.getChildNamed(outlinePanel, "Copy");
    assertNotNull(button1);
    assertFalse(button1.isEnabled());
    button1 = (JButton)TestUtils.getChildNamed(outlinePanel, "Paste");
    assertNotNull(button1);
    assertFalse(button1.isEnabled());
    button1 = (JButton)TestUtils.getChildNamed(outlinePanel, "Add Layer");
    assertNotNull(button1);
    assertTrue(button1.isEnabled());
    button1 = (JButton)TestUtils.getChildNamed(outlinePanel, "Delete");
    assertNotNull(button1);
    assertTrue(button1.isEnabled());
    button1 = (JButton)TestUtils.getChildNamed(outlinePanel, "Update View");
    assertNotNull(button1);
    assertTrue(button1.isEnabled());
  }
}
