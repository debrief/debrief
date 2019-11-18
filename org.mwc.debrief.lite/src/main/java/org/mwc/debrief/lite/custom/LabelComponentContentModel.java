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

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentContentModel;
import org.pushingpixels.neon.icon.ResizableIcon;
import org.pushingpixels.neon.icon.ResizableIcon.Factory;

/**
 * @author Ayesha
 *
 */
public class LabelComponentContentModel implements ComponentContentModel
{

  private boolean isEnabled;
  private ResizableIcon.Factory iconFactory;
  private String caption;
  private String text;
  private RichTooltip richTooltip;
  private ActionListener actionListener;
  
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  
  @Override
  public boolean isEnabled()
  {
    return isEnabled;
  }

  @Override
  public void setEnabled(boolean enabled)
  {
    if (this.isEnabled != enabled) {
      this.isEnabled = enabled;
      this.pcs.firePropertyChange("enabled", !this.isEnabled, this.isEnabled);
  }
    
  }

  @Override
  public Factory getIconFactory()
  {
    return iconFactory;
  }

  @Override
  public String getCaption()
  {
    return caption;
  }

  @Override
  public RichTooltip getRichTooltip()
  {
    return richTooltip;
  }
  public void setText(String text)
  {
    this.text = text;
  }
  
  public String getText()
  {
    return text;
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener pcl)
  {
    pcs.addPropertyChangeListener(pcl);
    
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener pcl)
  {
    pcs.removePropertyChangeListener(pcl);
    
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  
  public static class Builder
  {
    private boolean isEnabled = true;
    private ResizableIcon.Factory iconFactory;
    private String caption;
    private RichTooltip richTooltip;
    private String text;
    private ActionListener actionListener;
    
    
    public LabelComponentContentModel build() {
      LabelComponentContentModel model = new LabelComponentContentModel();
      model.setText(this.text);
      model.actionListener = this.actionListener;
      model.isEnabled = this.isEnabled;
      model.iconFactory = this.iconFactory;
      model.caption = this.caption;
      model.richTooltip = this.richTooltip;
      return model;
    }
    public Builder setText(String text)
    {
      this.text=text;
      return this;
    }
  }
  
  

}
