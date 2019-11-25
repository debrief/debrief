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
package org.mwc.debrief.lite.gui.custom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Ayesha
 *
 */
public class LabelledRangeSlider extends JPanel
{
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private JLabel lblMinimumValue;
  private JLabel lblMaximumValue;
  private RangeSlider rangeSlider;
  
  public LabelledRangeSlider()
  {
    rangeSlider = new RangeSlider();
    rangeSlider.setBackground(new Color(180,180,220));
    lblMinimumValue = new JLabel();
    lblMaximumValue = new JLabel();
    
    final JPanel sliderPanel = new JPanel();
    sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
    sliderPanel.setSize(new Dimension(250,30));
    sliderPanel.add(rangeSlider);
    

    // Label's panel
    final JPanel valuePanel = new JPanel();
    valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.X_AXIS));
    valuePanel.add(lblMinimumValue);
    valuePanel.add(Box.createGlue());
    valuePanel.add(lblMaximumValue);
    valuePanel.setSize(new Dimension(250,30));
    setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    add(sliderPanel);
    this.add(Box.createHorizontalGlue());
    add(valuePanel);
  }

  public JLabel getLblMaximumValue()
  {
    return lblMaximumValue;
  }
  public JLabel getLblMinimumValue()
  {
    return lblMinimumValue;
  }
  public RangeSlider getRangeSlider()
  {
    return rangeSlider;
  }
}
