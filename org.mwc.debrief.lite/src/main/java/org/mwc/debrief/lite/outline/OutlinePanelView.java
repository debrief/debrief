/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.outline;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

import Debrief.GUI.DebriefImageHelper;
import MWC.GUI.Plottable;
import MWC.GUI.LayerManager.Swing.SwingLayerManager;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class OutlinePanelView extends SwingLayerManager
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Override
  protected void initForm()
  {
    super.initForm();
    
    setCellRenderer(new OutlineRenderer());
    setCellEditor(new OutlineCellEditor());
  }
  
  private class OutlineRenderer extends DefaultTreeCellRenderer
  {
   
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Icon visibilityIconEnabled;
    private Icon visibilityIconDisabled;
    private int _xOffset = 0;
    private final Component strut = Box.createHorizontalStrut(5);
    private final JPanel panel = new JPanel();
    private JLabel visibility = new JLabel();
    private Border border = BorderFactory.createEmptyBorder ( 4, 2, 2, 4 );
    
    public OutlineRenderer()
    {
      panel.setBackground(UIManager.getColor("Tree.textBackground"));
      setOpaque(false);
      panel.setOpaque(false);
      panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      
      visibility.setOpaque(false);
      visibilityIconEnabled = new ImageIcon(getClass().getClassLoader().getResource("images/16/visible-eye.png")); 
      visibilityIconDisabled = new ImageIcon(getClass().getClassLoader().getResource("images/16/invisible-eye.png"));
      panel.add(visibility);
      panel.add(strut);
      panel.add(this);
      panel.setBorder(border);
    }
    
    public JLabel getVisibilityLabel() {
      return visibility;
    }
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object node,
        boolean selected, boolean expanded, boolean leaf, int row,
        boolean hasFocus)
    {
      super.getTreeCellRendererComponent(tree, node, selected, expanded, leaf,
          row, hasFocus);
      if (node instanceof DefaultMutableTreeNode)
      {
        final DefaultMutableTreeNode tn = (DefaultMutableTreeNode) node;
        final Object data = tn.getUserObject();
        if (data instanceof Plottable)
        {
          final Plottable pl = (Plottable) tn.getUserObject();
          DebriefImageHelper helper = new DebriefImageHelper();
          String icon = helper.getImageFor(pl);
          if(icon!=null) {
            URL iconURL = DebriefImageHelper.class.getClassLoader().getResource(icon);
            setIcon(new ImageIcon(iconURL));
          }
          setVisibility(pl.getVisible());
        }
      }
      
      
      panel.doLayout();
      return panel;
    }
    public void paint(final java.awt.Graphics g)
    {
      super.paint(g);

      // get the location of the check box, to check our ticking
      if (g != null)
      {
        try
        {
          final FontMetrics fm = g.getFontMetrics();
          _xOffset = fm.stringWidth(getText()) + strut.getPreferredSize().width;
        }
        finally
        {
          // g.dispose();
        }
      }
    }
    
    private void setVisibility(boolean visible) {
      if(visible) {
        visibility.setIcon(visibilityIconEnabled);
      }
      else {
        visibility.setIcon(visibilityIconDisabled);
      }
    }
    

  }

  private class OutlineCellEditor extends AbstractCellEditor implements TreeCellEditor
  {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JLabel visibilityLabel;
    private OutlineRenderer renderer;
    private DefaultMutableTreeNode lastEditedNode;
    
    public OutlineCellEditor()
    {
      renderer = new OutlineRenderer();
      visibilityLabel = renderer.getVisibilityLabel();

      visibilityLabel.addMouseListener(new MouseAdapter()
      {
        public void mouseClicked(final MouseEvent e)
        {
          final Plottable pl = (Plottable) lastEditedNode.getUserObject();
          final PlottableNode pln = (PlottableNode) lastEditedNode;
          boolean newVisibility = !pl.getVisible();
          changeVisOfThisElement(pl, newVisibility, pln
              .getParentLayer());
          pln.setSelected(newVisibility);
          renderer.setVisibility(newVisibility);
          stopCellEditing();
        }
      });
    }

    public Component getTreeCellEditorComponent(final JTree tree, final Object value,
        final boolean selected, final boolean expanded, final boolean leaf, final int row)
    {
      lastEditedNode = (DefaultMutableTreeNode) value;

      return renderer.getTreeCellRendererComponent(tree, value, selected,
          expanded, leaf, row, true); // hasFocus ignored
    }

    public Object getCellEditorValue()
    {
      return lastEditedNode.getUserObject();
    }

  }
  
}
