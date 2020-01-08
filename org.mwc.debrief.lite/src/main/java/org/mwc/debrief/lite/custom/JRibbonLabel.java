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
package org.mwc.debrief.lite.custom;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import org.pushingpixels.flamingo.api.common.projection.Projection;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;

/**
 * @author Ayesha
 *
 */
public class JRibbonLabel extends JLabel
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private LabelComponentContentModel contentModel ;

  public JRibbonLabel(Projection<JRibbonLabel,
  LabelComponentContentModel, ComponentPresentationModel> projection)
  {
   
    this.contentModel = projection.getContentModel();
    setEnabled(contentModel.isEnabled());
    setText(contentModel.getText());
    contentModel.addPropertyChangeListener((PropertyChangeEvent event) -> {
      if ("text".equals(event.getPropertyName())) {
        setText(contentModel.getText());
      }
    });
  }
  
  protected void paintComponent(final Graphics g)
  {
    if(contentModel.getBackground()!=null) 
    {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, getWidth(), getHeight());
    }
    super.paintComponent(g);
  }
}
