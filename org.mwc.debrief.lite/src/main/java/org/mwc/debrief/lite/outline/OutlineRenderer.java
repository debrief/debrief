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
import java.net.URL;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import Debrief.GUI.DebriefImageHelper;
import MWC.GUI.Plottable;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class OutlineRenderer extends DefaultTreeCellRenderer
{
  
  //static Icon openIcon; 
  //static Icon closedIcon; 

  private int _xOffset = 0;
  private final Component strut = Box.createHorizontalStrut(5);
  private final JPanel panel = new JPanel();
  
  public OutlineRenderer()
  {
    panel.setBackground(UIManager.getColor("Tree.textBackground"));
    setOpaque(false);
    panel.setOpaque(false);
    panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panel.add(this);
    
    /*final URL iconURL1 = getClass().getClassLoader().getResource(
        "images/16/expand.png");
    final URL iconURL2 = getClass().getClassLoader().getResource(
        "images/16/collapse.png");
    closedIcon = new ImageIcon(iconURL2);
    openIcon = new ImageIcon(iconURL1);
    setClosedIcon(closedIcon);
    setOpenIcon(openIcon);*/
    panel.add(strut);
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
      if (data instanceof MWC.GUI.Plottable)
      {
        final Plottable pl = (Plottable) tn.getUserObject();
        DebriefImageHelper helper = new DebriefImageHelper();
        String icon = helper.getImageFor(pl);
        if(icon!=null) {
          URL iconURL = getClass().getClassLoader().getResource(icon);
          setIcon(new ImageIcon(iconURL));
        }
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

}
