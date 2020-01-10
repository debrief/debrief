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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;

import javax.swing.event.ChangeListener;

import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentContentModel;
import org.pushingpixels.neon.icon.ResizableIcon;
import org.pushingpixels.neon.icon.ResizableIcon.Factory;

/**
 * @author Ayesha
 *
 */
public class SliderComponentContentModel implements ComponentContentModel
{
  /**
   * 
   */
  private static final long serialVersionUID = 3020555105731152313L;
  private boolean isEnabled;
  private ResizableIcon.Factory iconFactory;
  private String caption;
  private String text;
  private RichTooltip richTooltip;
  private ChangeListener changeListener;
  
  private int majorTickSpacing;
  private int minorTickSpacing;
  private boolean paintTickSpacing;
  private boolean paintLabels;
  private int minimum;
  public int getMinimum()
  {
    return minimum;
  }

  public void setMinimum(int minimum)
  {
    if (this.minimum != minimum) {
      int oldValue = this.minimum;
      this.minimum = minimum;
      this.pcs.firePropertyChange("minimum", oldValue, minimum);
    }
  }

  public int getMaximum()
  {
    return maximum;
  }

  public void setMaximum(int maximum)
  {
    if (this.maximum != maximum) {
      int oldValue = this.maximum;
      this.maximum = maximum;
      this.pcs.firePropertyChange("maximum", oldValue, maximum);
    }
  }

  public int getValue()
  {
    return value;
  }

  public void setValue(int value)
  {
    if (this.value != value) {
      int oldValue = this.value;
      this.value = value;
      this.pcs.firePropertyChange("value", oldValue, value);
    }
  }


  private int maximum;
  private int value;
  
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  

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
  
  public void setRichTooltip(RichTooltip richTooltip)
  {
    this.richTooltip = richTooltip;
  }
  
  public void setIconFactory(ResizableIcon.Factory iconFactory)
  {
    this.iconFactory = iconFactory;
  }
 

  @Override
  public void addPropertyChangeListener(PropertyChangeListener pcl)
  {
    this.pcs.addPropertyChangeListener(pcl);

  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener pcl)
  {
    this.pcs.removePropertyChangeListener(pcl);

  }

  /**
   * @return the majorTickSpacing
   */
  public int getMajorTickSpacing()
  {
    return majorTickSpacing;
  }

  /**
   * @param majorTickSpacing the majorTickSpacing to set
   */
  public void setMajorTickSpacing(int majorTickSpacing)
  {
    if (this.majorTickSpacing != majorTickSpacing) {
      int oldValue = this.majorTickSpacing;
      this.majorTickSpacing = majorTickSpacing;
      this.pcs.firePropertyChange("majorTickSpacing", oldValue, majorTickSpacing);
    }
  }

  /**
   * @return the minorTickSpacing
   */
  public int getMinorTickSpacing()
  {
    return minorTickSpacing;
  }

  /**
   * @param minorTickSpacing the minorTickSpacing to set
   */
  public void setMinorTickSpacing(int minorTickSpacing)
  {
    if (this.minorTickSpacing != minorTickSpacing) {
      int oldValue = this.minorTickSpacing;
      this.minorTickSpacing = minorTickSpacing;
      this.pcs.firePropertyChange("minorTickSpacing", oldValue, minorTickSpacing);
    }
  }

  /**
   * @return the paintTickSpacing
   */
  public boolean isPaintTickSpacing()
  {
    return paintTickSpacing;
  }

  /**
   * @param paintTickSpacing the paintTickSpacing to set
   */
  public void setPaintTickSpacing(boolean paintTickSpacing)
  {
    if (this.paintTickSpacing != paintTickSpacing) {
      this.paintTickSpacing = paintTickSpacing;
      this.pcs.firePropertyChange("paintTickSpacing", !paintTickSpacing, paintTickSpacing);
    }
  }

  /**
   * @return the paintLabels
   */
  public boolean isPaintLabels()
  {
    return paintLabels;
  }

  /**
   * @param paintLabels the paintLabels to set
   */
  public void setPaintLabels(boolean paintLabels)
  {
    if (this.paintLabels != paintLabels) {
      this.paintLabels = paintLabels;
      this.pcs.firePropertyChange("paintTickSpacing", !paintLabels, paintLabels);
    }
  }

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

  /**
   * @return the changeListener
   */
  public ChangeListener getChangeListener()
  {
    return changeListener;
  }

  /**
   * @return the text
   */
  public String getText()
  {
    return text;
  }

  /**
   * @param text the text to set
   */
  public void setText(String text)
  {
    this.text = text;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  

  public static class Builder {
    private boolean isEnabled = true;
    private ResizableIcon.Factory iconFactory;
    private String caption;
    private RichTooltip richTooltip;
    private String text;
    private ChangeListener changeListener;
    
    private int minimum;
    public int getMinimum()
    {
      return minimum;
    }

    public Builder setMinimum(int minimum)
    {
      this.minimum = minimum;
      return this;
    }
    
    public Builder setMinimum(Calendar date)
    {
      return setMinimum(toInt(date));
    }
    public Builder setMaximum(Calendar date)
    {
      return setMaximum(toInt(date));
    }
    private int toInt(Calendar date)
    {
      return (int) (date.getTimeInMillis() / 1000L);
    }
    public int getMaximum()
    {
      return maximum;
    }

    public Builder setMaximum(int maximum)
    {
      this.maximum = maximum;
      return this;
    }

    public int getValue()
    {
      return value;
    }

    public Builder setValue(int value)
    {
      this.value = value;
      return this;
    }

    public int getMajorTickSpacing()
    {
      return majorTickSpacing;
    }

    public Builder setMajorTickSpacing(int majorTickSpacing)
    {
      this.majorTickSpacing = majorTickSpacing;
      return this;
    }

    public int getMinorTickSpacing()
    {
      return minorTickSpacing;
    }

    public Builder setMinorTickSpacing(int minorTickSpacing)
    {
      this.minorTickSpacing = minorTickSpacing;
      return this;
    }

    public boolean isPaintTickSpacing()
    {
      return paintTickSpacing;
    }

    public Builder setPaintTickSpacing(boolean paintTickSpacing)
    {
      this.paintTickSpacing = paintTickSpacing;
      return this;
    }

    public boolean isPaintLabels()
    {
      return paintLabels;
    }

    public Builder setPaintLabels(boolean paintLabels)
    {
      this.paintLabels = paintLabels;
      return this;
    }

    private int maximum;
    private int value;
    private int majorTickSpacing;
    private int minorTickSpacing;
    private boolean paintTickSpacing;
    private boolean paintLabels;

    public Builder setText(String text) {
        this.text = text;
        return this;
    }

   

    public Builder setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        return this;
    }

    public Builder setIconFactory(ResizableIcon.Factory iconFactory) {
        this.iconFactory = iconFactory;
        return this;
    }

    public Builder setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public Builder setRichTooltip(RichTooltip richTooltip) {
        this.richTooltip = richTooltip;
        return this;
    }

    public Builder setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
        return this;
    }

    public SliderComponentContentModel build() {
        SliderComponentContentModel model = new SliderComponentContentModel();
        model.setText(this.text);
        model.changeListener = this.changeListener;
        model.isEnabled = this.isEnabled;
        model.iconFactory = this.iconFactory;
        model.caption = this.caption;
        model.richTooltip = this.richTooltip;
        model.majorTickSpacing=this.majorTickSpacing;
        model.minorTickSpacing=this.minorTickSpacing;
        model.setValue(this.value);
        model.setMinimum(this.minimum);
        model.setMaximum(this.maximum);
        model.paintLabels=this.paintLabels;
        model.paintTickSpacing=this.paintTickSpacing;
        return model;
    }
}


 
}
