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
package Debrief.GUI.Lite.custom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

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
  private JLabel _minimizeLabel;
  static final ImageIcon minimizeIcon;
  static final ImageIcon maximizeIcon;
  private static boolean minimize=false;
  static {
    final URL iconURL = JPanelWithTitleBar.class.getClassLoader().getResource("images/16/minimize.png");
    minimizeIcon = new ImageIcon(iconURL);
    final URL iconURL2 = JPanelWithTitleBar.class.getClassLoader().getResource("images/16/maximize.png");
    maximizeIcon = new ImageIcon(iconURL2);
  }
  
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
    JPanel iconPanel = new JPanel();
    iconPanel.setBackground(titleColor);
    iconPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    _minimizeLabel = new JLabel();
    _minimizeLabel.setIcon(minimizeIcon);  
    iconPanel.add(_minimizeLabel);
    add(iconPanel,BorderLayout.EAST);
    setBackground(titleColor);
    
    
  }
  
  public void addMinMaxListenerFor(final JSplitPane splitPane,final boolean maximize) {
    _minimizeLabel.addMouseListener(new MouseAdapter()
    {
      
      @Override
      public void mouseClicked(MouseEvent e)
      {
        
        minimize=!minimize;
        if(minimize) {
          _minimizeLabel.setIcon(maximizeIcon);
        }
        else {
          _minimizeLabel.setIcon(minimizeIcon);
        }
        toggle(splitPane,minimize?!maximize:maximize);
      }
    });
  }
  
  public void toggle(JSplitPane splitPane,boolean collapse) {
    try {
      BasicSplitPaneDivider bspd = ((BasicSplitPaneUI) splitPane.getUI()).getDivider();
      Field buttonField = BasicSplitPaneDivider.class.
              getDeclaredField(collapse ? "rightButton" : "leftButton");
      buttonField.setAccessible(true);
      JButton button = (JButton) buttonField.get(((BasicSplitPaneUI) splitPane.getUI()).getDivider());
      button.getActionListeners()[0].actionPerformed(new ActionEvent(bspd, MouseEvent.MOUSE_CLICKED,
              ""));
  } catch (Exception e) {
      e.printStackTrace();
  }
  }
  
  public void setTitle(String title) {
    _titleLabel.setText(title);
  }
  

}
