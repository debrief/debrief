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
package org.mwc.debrief.lite.custom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class JPanelWithTitleBar extends JPanel
{

  /**
   * 
   */
  private static final Color titleColor = new Color(0,0,180);
  private static final long serialVersionUID = 1L;
  
  private JLabel _titleLabel;
  private boolean minimize=false;
  public JPanelWithTitleBar(final String title)
  {
    setLayout(new BorderLayout());  
    setBorder(new CompoundBorder(new EmptyBorder(0,0,0,0), new MatteBorder(1, 1, 1, 1, Color.BLACK)));
    _titleLabel = new JLabel(title);
    _titleLabel.setFont(_titleLabel.getFont().deriveFont(Font.BOLD));
    _titleLabel.setBackground(titleColor);
    _titleLabel.setForeground(Color.WHITE);
    _titleLabel.setOpaque(true);
    add(_titleLabel,BorderLayout.WEST);
    
    setBackground(titleColor);
    
    
  }
  
 /* public void addMinMaxListenerFor(final JSplitPane splitPane,final boolean maximize) {
    addMouseListener(new MouseAdapter()
    {
      
      @Override
      public void mouseClicked(MouseEvent e)
      {
        
        minimize=!minimize;
        toggle(splitPane,minimize?!maximize:maximize);
      }
    });
  }
 
  
  public void toggle(JSplitPane pane,boolean collapse) {
    if(collapse) {
    pane.getRightComponent().setMinimumSize(new Dimension());
    pane.setDividerLocation(0.75d);
    }
    else {
    // Hide right or bottom
    pane.getRightComponent().setMinimumSize(new Dimension());
    pane.setDividerLocation(0.03d);
    }
  }
  */
  
  public void setTitle(final String title) {
    _titleLabel.setText(title);
  }

  public void addMaxListenerFor(final JSplitPane pane, final JSplitPane pane2)
  {
    
    addMouseListener(new MouseAdapter()
    {
      
      @Override
      public void mouseClicked(MouseEvent e)
      {
        minimize = !minimize;
        if(minimize) {
          pane.getLeftComponent().setMinimumSize(new Dimension());
          pane.setDividerLocation(0.03d);
          pane2.getRightComponent().setMinimumSize(new Dimension());
          pane2.setDividerLocation(0.97d);
        }
        else {
          pane.getLeftComponent().setMinimumSize(new Dimension());
          pane.setDividerLocation(0.3d);
          pane2.getRightComponent().setMinimumSize(new Dimension());
          pane2.setDividerLocation(0.7d);
        }
      }
    });
    
  }
  
  public void addMinListenerFor(final JSplitPane pane2)
  {
    
    addMouseListener(new MouseAdapter()
    {
      
      @Override
      public void mouseClicked(MouseEvent e)
      {
        minimize = !minimize;
        if(minimize) {
          pane2.getRightComponent().setMinimumSize(new Dimension());
          pane2.setDividerLocation(0.97d);
        }
        else {
          pane2.getRightComponent().setMinimumSize(new Dimension());
          pane2.setDividerLocation(0.7d);
        }
      }
    });
    
  }
  

}
