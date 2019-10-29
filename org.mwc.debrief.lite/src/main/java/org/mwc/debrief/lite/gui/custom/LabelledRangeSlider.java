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
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.pushingpixels.flamingo.api.ribbon.synapse.JRibbonCheckBox;

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
    setLayout(new BorderLayout());
    rangeSlider = new RangeSlider();
    add(rangeSlider,BorderLayout.CENTER);
    final JPanel valuePanel = new JPanel();
    valuePanel.setLayout(new BorderLayout());
    lblMinimumValue = new JLabel();
    lblMaximumValue = new JLabel();
    valuePanel.add(lblMinimumValue,BorderLayout.WEST);
    valuePanel.add(lblMaximumValue,BorderLayout.EAST);
    add(valuePanel,BorderLayout.SOUTH);
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
