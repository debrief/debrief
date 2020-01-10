package org.mwc.debrief.lite.tests;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.gui.custom.JXCollapsiblePaneWithTitle;
import org.mwc.debrief.lite.utils.TestUtils;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.internal.ui.ribbon.JBandControlPanel;
import org.pushingpixels.flamingo.internal.ui.ribbon.JRibbonComponent;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.ArcShape;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.RectangleShape;

public class TestInsertRibbon extends BaseTestCase
{
  
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    SwingUtilities.invokeAndWait(new Runnable()
    {
      
      @Override
      public void run()
      {
        DebriefLiteApp.getInstance().getApplicationFrame().getRibbon().setSelectedTask(TestUtils.getTask(3));
      }
    });
  }

  public void testInsertEllipse() throws Exception
  {
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
        (JCommandButton) liteBand.getComponent(2);
    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        ellipseButton.doActionClick();
      }
    });
    Thread.sleep(2000);
    JFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    JXCollapsiblePaneWithTitle outlinePanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame,"Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    JTree tree = (JTree)TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),2);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 0);
    DefaultMutableTreeNode ellipseNode = (DefaultMutableTreeNode)node.getFirstChild();
    assertNotNull(ellipseNode);
    assertNotNull(ellipseNode.getUserObject());
    assertTrue(ellipseNode.getUserObject() instanceof ShapeWrapper);
    assertTrue(((ShapeWrapper)ellipseNode.getUserObject()).getShape() instanceof EllipseShape);
    EllipseShape es = (EllipseShape)((ShapeWrapper)ellipseNode.getUserObject()).getShape() ;
    assertEquals("Ellipse",es.getName());
    //assert the shape was drawn on the plot, how to  do that?
    
  }
  public void testInsertRectangle() throws Exception
  {
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
    final JCommandButton rectButton =
        (JCommandButton) liteBand.getComponent(3);
    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        rectButton.doActionClick();
      }
    });
    Thread.sleep(2000);
    JFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    JXCollapsiblePaneWithTitle outlinePanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame,"Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    JTree tree = (JTree)TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),2);
    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)tree.getModel().getRoot();
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)rootNode.getFirstChild();
    DefaultMutableTreeNode rectNode = (DefaultMutableTreeNode)node.getFirstChild();
    assertNotNull(rectNode);
    assertNotNull(rectNode.getUserObject());
    assertTrue(rectNode.getUserObject() instanceof ShapeWrapper);
    assertTrue(((ShapeWrapper)rectNode.getUserObject()).getShape() instanceof RectangleShape);
    RectangleShape es = (RectangleShape)((ShapeWrapper)rectNode.getUserObject()).getShape() ;
    assertEquals("Rectangle",es.getName());
    //assert the shape was drawn on the plot, how to  do that?
    
  }
  
  public void testInsertCircle() throws Exception
  {
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
    final JCommandButton rectButton =
        (JCommandButton) liteBand.getComponent(4);
    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        rectButton.doActionClick();
      }
    });
    Thread.sleep(200);
    JFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    JXCollapsiblePaneWithTitle outlinePanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame,"Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    JTree tree = (JTree)TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),2);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 0);
    DefaultMutableTreeNode circleNode = (DefaultMutableTreeNode)node.getFirstChild();
    assertNotNull(circleNode);
    assertNotNull(circleNode.getUserObject());
    assertTrue(circleNode.getUserObject() instanceof ShapeWrapper);
    assertTrue(((ShapeWrapper)circleNode.getUserObject()).getShape() instanceof CircleShape);
    CircleShape es = (CircleShape)((ShapeWrapper)circleNode.getUserObject()).getShape() ;
    assertEquals("Circle",es.getName());
    //assert the shape was drawn on the plot, how to  do that?
    
  }
  public void testInsertLine() throws Exception
  {
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
    final JCommandButton rectButton =
        (JCommandButton) liteBand.getComponent(6);
    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        rectButton.doActionClick();
      }
    });
    Thread.sleep(200);
    JFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    JXCollapsiblePaneWithTitle outlinePanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame,"Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    JTree tree = (JTree)TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),2);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 0);
    DefaultMutableTreeNode lineNode = (DefaultMutableTreeNode)node.getFirstChild();
    assertNotNull(lineNode);
    assertNotNull(lineNode.getUserObject());
    assertTrue(lineNode.getUserObject() instanceof ShapeWrapper);
    assertTrue(((ShapeWrapper)lineNode.getUserObject()).getShape() instanceof LineShape);
    LineShape es = (LineShape)((ShapeWrapper)lineNode.getUserObject()).getShape() ;

    assertEquals("Line",es.getName());
    //assert the shape was drawn on the plot, how to  do that?
    
  }
  public void testInsertArc() throws Exception
  {
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
    final JCommandButton rectButton =
        (JCommandButton)liteBand.getComponent(5);
    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        rectButton.doActionClick();
      }
    });
    Thread.sleep(200);
    JFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    JXCollapsiblePaneWithTitle outlinePanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame,"Outline");
    assertNotNull(outlinePanel);
    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    JTree tree = (JTree)TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),2);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 0);
    DefaultMutableTreeNode arcNode = (DefaultMutableTreeNode)node.getFirstChild();
    assertNotNull(arcNode);
    assertNotNull(arcNode.getUserObject());
    assertTrue(arcNode.getUserObject() instanceof ShapeWrapper);
    assertTrue(((ShapeWrapper)arcNode.getUserObject()).getShape() instanceof ArcShape);
    ArcShape es = (ArcShape)((ShapeWrapper)arcNode.getUserObject()).getShape() ;

    assertEquals("Arc",es.getName());
    //assert the shape was drawn on the plot, how to  do that?
    
  }
  
  public void testShapeEdit() throws Exception
  {
    
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
    final JCommandButton rectButton =
        (JCommandButton) liteBand.getComponent(5);
    SwingUtilities.invokeAndWait(new Runnable()
    {

      @Override
      public void run()
      {
        rectButton.doActionClick();
      }
    });
    Thread.sleep(200);
    JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    JXCollapsiblePaneWithTitle outlinePanel = (JXCollapsiblePaneWithTitle)TestUtils.getChildNamed(ribbonFrame,"Outline");
    assertNotNull(outlinePanel);

    assertTrue(outlinePanel.isVisible());
    assertTrue(outlinePanel.isEnabled());
    final JTree tree = (JTree)TestUtils.getChildNamed(outlinePanel, "Layer Tree");
    assertEquals(tree.getModel().getChildCount(tree.getModel().getRoot()),2);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getModel().getChild(tree.getModel().getRoot(), 0);
    DefaultMutableTreeNode arcNode = (DefaultMutableTreeNode)node.getFirstChild();
    assertNotNull(arcNode);
    assertNotNull(arcNode.getUserObject());
    assertTrue(arcNode.getUserObject() instanceof ShapeWrapper);
    assertTrue(((ShapeWrapper)arcNode.getUserObject()).getShape() instanceof ArcShape);
    assertEquals("Arc:new arc",arcNode.toString());
    SwingUtilities.invokeAndWait(new Runnable()
    {
      
      @Override
      public void run()
      {
        tree.expandRow(0);
        tree.setSelectionRow(1);
            
      }
    });
    Thread.sleep(2000);
    final JButton editButton = (JButton)TestUtils.getChildNamed(outlinePanel, "Edit");
    assertNotNull(editButton);
    assertTrue(editButton.isEnabled());
    SwingUtilities.invokeLater(new Runnable()
    {
      
      @Override
      public void run()
      {
        editButton.doClick();
      }
    });
    Thread.sleep(2000);
    final JTable table = (JTable)TestUtils.getChildIndexed(ribbonFrame, "MyTable", 0, false);
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    assertNotNull(table);
    
    SwingUtilities.invokeAndWait(new Runnable()
    {
      
      @Override
      public void run()
      {
        table.editCellAt(3, 1);
        TableCellRenderer r = table.getCellRenderer(3, 1);
        JComponent c = (JComponent) table.prepareRenderer(r, 3, 1);
        final JTextArea ta = (JTextArea)((JComponent)c.getComponent(0)).getComponent(0);
        assertEquals(ta.getText(),"new arc");
        ta.setText("arc renamed");
        /*if (table.isEditing()) {
          System.out.println("Stopping editing");
          table.getCellEditor().stopCellEditing();
        }*/
      }
    });
    
    /*final JButton applyButton = (JButton)TestUtils.getChildIndexed(ribbonFrame, "JButton",2,false);
    assertNotNull(applyButton);
    assertEquals(applyButton.getText(),"Apply");
    
    SwingUtilities.invokeAndWait(new Runnable()
    {
      
      @Override
      public void run()
      {
        applyButton.requestFocus();
        applyButton.doClick();
        System.out.println("Clicked apply");
      }
    });
*/    
    final JButton closeButton = (JButton)TestUtils.getChildIndexed(ribbonFrame, "JButton",1,false);
     
    assertNotNull(closeButton);
    assertEquals(closeButton.getText(),"Close");
    SwingUtilities.invokeAndWait(new Runnable()
    {
      
      @Override
      public void run()
      {
        closeButton.doClick();
        System.out.println("Clicked close");
      }
    });
    Thread.sleep(2000);
    //assertEquals("Arc:arc renamed",arcNode.toString());
    
  }
  

  
}
