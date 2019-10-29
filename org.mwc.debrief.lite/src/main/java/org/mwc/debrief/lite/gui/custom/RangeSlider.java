package org.mwc.debrief.lite.gui.custom;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JSlider;

import junit.framework.TestCase;

/**
 * An extension of JSlider to select a range of values using two thumb controls. The thumb controls
 * are used to select the lower and upper value of a range with predetermined minimum and maximum
 * values.
 * 
 * <p>
 * Note that RangeSlider makes use of the default BoundedRangeModel, which supports an inner range
 * defined by a value and an extent. The upper value returned by RangeSlider is simply the lower
 * value plus the extent.
 * </p>
 * 
 * Implementation taken from https://ernienotes.wordpress.com/2010/12/27/creating-a-java-swing-range-slider/
 */
public class RangeSlider extends JSlider
{

  public static class TestConversion extends TestCase
  {
    public void testConvert()
    {
      Calendar date = new GregorianCalendar();
      int millis = (int) (date.getTimeInMillis() / 1000L);
      Calendar newD = toDate(millis);
      int sliderVal = toInt(date);
      Calendar newD2 = toDate(sliderVal);
      assertEquals("conversion works", millis, sliderVal);
      assertEquals("conversion works", newD, newD2);
    }
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  public RangeSlider()
  {
    //TODO review this
    super(0,100);
    initSlider();
  }

  /**
   * Constructs a RangeSlider with using a Calendar, storing the values divided by 1000
   * 
   * @param min
   * @param max
   */
  public RangeSlider(Calendar min, Calendar max)
  {
    super(toInt(min), toInt(max), toInt(min) + (toInt(max) - toInt(min)) / 2);
    initSlider();
  }

  /**
   * Constructs a RangeSlider with using the time divided by 1000
   * @param min
   * @param max
   */
  public RangeSlider(int min, int max)
  {
    super(min, max, min + (max - min) / 2);
    initSlider();
  }

  public static int toInt(Calendar date)
  {
    return (int) (date.getTimeInMillis() / 1000L);
  }

  public static Calendar toDate(int val)
  {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTimeInMillis(val * 1000L);
    return cal;
  }

  /**
   * Initializes the slider by setting default properties.
   */
  private void initSlider()
  {
    setOrientation(HORIZONTAL);
    setValue(getMinimum());
    setUpperValue(getMaximum());
  }

  public Calendar getLowerDate()
  {
    return toDate(getValue());
  }

  public Calendar getUpperDate()
  {
    return toDate(getUpperValue());
  }

  public void setLowerDate(Calendar date)
  {
    setValue(toInt(date));
  }

  public void setUpperDate(Calendar date)
  {
    setUpperValue(toInt(date));
  }
  
  public void setMinimum(Calendar date)
  {
    setMinimum(toInt(date));
  }
  
  public void setMaximum(Calendar date)
  {
    setMaximum(toInt(date));
  }

  /**
   * Overrides the superclass method to install the UI delegate to draw two thumbs.
   */
  @Override
  public void updateUI()
  {
    setUI(new RangeSliderUI(this));
    // Update UI for slider labels. This must be called after updating the
    // UI of the slider. Refer to JSlider.updateUI().
    updateLabelUIs();
  }

  /**
   * Returns the lower value in the range.
   */
  @Override
  public int getValue()
  {
    return super.getValue();
  }

  /**
   * Sets the lower value in the range.
   */
  @Override
  public void setValue(int value)
  {
    int oldValue = getValue();
    if (oldValue == value)
    {
      return;
    }

    // Compute new value and extent to maintain upper value.
    int oldExtent = getExtent();
    int newValue = Math.min(Math.max(getMinimum(), value), oldValue
        + oldExtent);
    int newExtent = oldExtent + oldValue - newValue;

    // Set new value and extent, and fire a single change event.
    getModel().setRangeProperties(newValue, newExtent, getMinimum(),
        getMaximum(), getValueIsAdjusting());
  }

  /**
   * Returns the upper value in the range.
   */
  public int getUpperValue()
  {
    return getValue() + getExtent();
  }

  /**
   * Sets the upper value in the range.
   */
  public void setUpperValue(int value)
  {
    // Compute new extent.
    int lowerValue = getValue();
    int newExtent = Math.min(Math.max(0, value - lowerValue), getMaximum()
        - lowerValue);

    // Set extent to set upper value.
    setExtent(newExtent);
  }
}