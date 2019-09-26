/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package Debrief.Wrappers.DynamicTrackShapes;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.FireReformatted;
import MWC.GUI.Griddable;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

abstract public class DynamicTrackShapeWrapper extends PlainWrapper implements
    Editable.DoNotHighlightMe, ExcludeFromRightClickEdit
{

  public static interface DynamicShape extends java.io.Serializable
  {
    /**
     * paint this dynamic shape
     * 
     * @param dest
     *          where to paint to
     * @param color
     *          color for this object
     * @param semiTransparent
     *          how to fill shape
     * @param location
     *          current platform location
     * @param courseDegs
     *          current platform heading
     */
    public void paint(CanvasType dest, Color color, boolean semiTransparent,
        WorldLocation location, double courseDegs, boolean filled);

  }

  // //////////////////////////////////////////////////////////////////////////
  // embedded class, used for editing the projection
  // //////////////////////////////////////////////////////////////////////////
  /**
   * the definition of what is editable about this object
   */
  public static final class DynamicTrackShapeWrapperInfo extends Griddable
  {

    /**
     * constructor for editable details of a set of Layers
     * 
     * @param data
     *          the Layers themselves
     */
    public DynamicTrackShapeWrapperInfo(final DynamicTrackShapeWrapper data)
    {
      super(data, data.getName(), "DynamicShape");
    }

    @Override
    public PropertyDescriptor[] getGriddablePropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {prop("Visible", "whether this dynamic shape data is visible", FORMAT),
            prop("Color", "the color for this sensor contact", FORMAT),
            displayProp("StartDTG", "Start DTG",
                "the start time this entry was recorded", FORMAT), displayProp(
                    "EndDTG", "End DTG", "the end time this entry was recorded",
                    FORMAT), prop("Constraints",
                        "sensor arcs: min max angle range", FORMAT)};

        return res;

      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

    @Override
    public NonBeanPropertyDescriptor[] getNonBeanGriddableDescriptors()
    {
      // don't worry - we do the bean-based method
      return null;
    }

    /**
     * The things about these Layers which are editable. We don't really use this list, since we
     * have our own custom editor anyway
     * 
     * @return property descriptions
     */
    @Override
    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {prop("Visible", "whether this sensor contact data is visible", FORMAT),
            prop("Color", "the color for this sensor contact", FORMAT),
            displayProp("StartDTG", "Start DTG",
                "the start time this entry was recorded", FORMAT), displayProp(
                    "EndDTG", "End DTG", "the end time this entry was recorded",
                    FORMAT), displayProp("SemiTransparent", "Semi transparent",
                        "whether to make the coverage semi-transparent",
                        FORMAT), prop("Filled", "whether to fill the circle",
                            FORMAT), prop("Constraints",
                                "sensor arcs: min max angle range", FORMAT),};
        return res;
      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  // ///////////////////////////////////////////
  // member variables
  /**
   * /////////////////////////////////////////////
   */
  private String _trackName;

  private HiResDate _startDTG, _endDTG;

  protected List<DynamicShape> _values = new ArrayList<DynamicShape>();

  /**
   * our editor
   */
  transient private MWC.GUI.Editable.EditorType _myEditor;

  /**
   * the style to plot this line
   */
  private int _myLineStyle = 0;

  /**
   * the parent object (which supplies our colour, should we need it)
   */
  private transient DynamicTrackShapeSetWrapper _mySensor;

  private String _coverageName;

  private int _lineWidth;

  private boolean _semiTransparent = true;

  /**
   * whether this shape is filled
   *
   */
  private boolean _isFilled = true;

  /**
   * default constructor, used when we read in from XML
   */
  public DynamicTrackShapeWrapper()
  {
    // by default, objects based on plain wrapper are coloured yellow.
    // but, we use a null colour value to indicate 'use parent color'
    setColor(null);

    setVisible(true);
  }

  /**
   * build a new sensorarc contact wrapper
   * 
   */

  public DynamicTrackShapeWrapper(final String theTrack,
      final HiResDate startDtg, final HiResDate endDtg,
      final List<DynamicShape> values, final Color theColor, final int theStyle,
      final String coverageName)
  {
    this();

    _trackName = theTrack;
    _startDTG = startDtg;
    _endDTG = endDtg;
    _values = values;

    // and the gui parameters
    setColor(theColor);
    _myLineStyle = theStyle;
    _coverageName = coverageName;
  }

  /**
   * member function to meet requirements of comparable interface *
   */
  @Override
  public final int compareTo(final Plottable o)
  {
    final DynamicTrackShapeWrapper other = (DynamicTrackShapeWrapper) o;
    if (_startDTG == null || other == null || other._startDTG == null)
    {
      return 1;
    }
    int res = 0;
    if (_startDTG.lessThan(other._startDTG))
      res = -1;
    else if (_startDTG.greaterThan(other._startDTG))
      res = 1;
    else
    {
      // just check if this is actually the same object (in which case return 0)
      if (o == this)
      {
        // we need a correct implementation of compare to for when we're finding
        // the position
        // of an item which is actually in the list - otherwise it won't get
        // found and we can't
        // delete it.
        res = 0;
      }
      else
      {
        // same times, make the newer item appear later. This is to overcome the
        // problem we experience where only the first contact at a particular
        // DTG gets recorded for a sensor
        res = 1;
      }
    }

    return res;

  }

  // ///////////////////////////////////////////
  // member methods to meet requirements of Plottable interface
  // ///////////////////////////////////////////

  /**
   * method to provide the actual colour value stored in this fix
   * 
   * @return fix colour, including null if applicable
   */
  public final Color getActualColor()
  {
    return super.getColor();
  }

  /**
   * find the data area occupied by this item
   */
  @Override
  public final MWC.GenericData.WorldArea getBounds()
  {
    // this object only has a context in time-stepping.
    // since it's dynamic, it doesn't have a concrete bounds.
    return null;
  }

  abstract public String getConstraints();

  public HiResDate getEndDTG()
  {
    return _endDTG;
  }

  /**
   * is this shape filled? (where applicable)
   *
   * @return
   */
  public boolean getFilled()
  {
    return _isFilled;
  }

  /**
   * getInfo
   * 
   * @return the returned MWC.GUI.Editable.EditorType
   */
  @Override
  public final MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new DynamicTrackShapeWrapperInfo(this);

    return _myEditor;
  }

  /**
   * retrieve the line style
   */
  public final Integer getLineStyle()
  {
    return new Integer(_myLineStyle);
  }

  /**
   * the line thickness (convenience wrapper around width)
   * 
   * @return
   */
  public final int getLineThickness()
  {
    return _lineWidth;
  }

  /**
   * get the name of this entry, using the formatted DTG
   */
  @Override
  public final String getName()
  {
    final StringBuilder builder = new StringBuilder();
    if (_startDTG != null)
    {
      builder.append(DebriefFormatDateTime.toStringHiRes(_startDTG));
    }
    else
    {
      builder.append("[UNSET]");
    }
    builder.append("-");
    if (_endDTG != null)
    {
      builder.append(DebriefFormatDateTime.toStringHiRes(_endDTG));
    }
    else
    {
      builder.append("[UNSET]");
    }
    return builder.toString();
  }

  public TimePeriod getPeriod()
  {
    return new TimePeriod.BaseTimePeriod(_startDTG, _endDTG);
  }

  /**
   * it this Label item currently visible?
   */
  public final boolean getSemiTransparent()
  {
    return _semiTransparent;
  }

  public final DynamicTrackShapeSetWrapper getSensor()
  {
    return _mySensor;
  }

  /**
   * find the name of the sensor which recorded this contact
   */
  public final String getSensorName()
  {
    String res;
    if (_mySensor != null)
    {
      res = _mySensor.getName();
    }
    else
    {
      res = _coverageName;
    }
    return res;
  }

  public HiResDate getStartDTG()
  {
    return _startDTG;
  }

  /**
   * getTrackName
   * 
   * @return the returned String
   */
  public final String getTrackName()
  {
    return _trackName;
  }

  protected int getValue(final ArrayList<String> elements, final int index)
  {
    try
    {
      return new Integer(elements.get(index)).intValue();
    }
    catch (final NumberFormatException e)
    {
      throw new RuntimeException("Error parsing arcs. Invalid number.");
    }
  }

  public List<DynamicShape> getValues()
  {
    return _values;
  }

  /**
   * whether there is any edit information for this item this is a convenience function to save
   * creating the EditorType data first
   * 
   * @return yes/no
   */
  @Override
  public final boolean hasEditor()
  {
    return true;
  }

  /**
   * paint this object to the specified canvas
   */
  @Override
  public final void paint(final MWC.GUI.CanvasType dest)
  {
    // DUFF METHOD TO MEET INTERFACE REQUIREMENTS
  }

  /**
   * paint this object to the specified canvas
   * 
   * @param track
   *          the parent list (from which we calculate origins if required)
   * @param dest
   *          where we're painting it to
   * @param keep_simple
   *          whether to allow a change in line styles
   */
  public final void paint(final MWC.GUI.CanvasType dest,
      final WorldLocation origin, final double courseDegs)
  {
    // ignore
    if (!getVisible())
      return;

    // restore the solid line style, for the next poor bugger
    dest.setLineStyle(MWC.GUI.CanvasType.SOLID);

    final Color oldColor = dest.getBackgroundColor();

    // ok, we've got enough to do the paint!
    for (final DynamicShape value : _values)
    {
      value.paint(dest, getColor(), _semiTransparent, origin, courseDegs,
          _isFilled);
    }

    // and restore the background color
    dest.setBackgroundColor(oldColor);

  }

  /**
   * how far away are we from this point? or return null if it can't be calculated
   */
  @Override
  public final double rangeFrom(final MWC.GenericData.WorldLocation other)
  {
    // Note: since this is a dynamic object, it doesn't have a concrete location
    return super.rangeFrom(other);
  }

  abstract public void setConstraints(String arcs);

  public void setEndDTG(final HiResDate date)
  {
    _endDTG = date;
  }

  /**
   * is this shape filled? (where applicable)
   *
   * @param isFilled
   *          yes/no
   */
  public void setFilled(final boolean isFilled)
  {
    this._isFilled = isFilled;
  }

  /**
   * update the line style
   */
  public final void setLineStyle(final Integer style)
  {
    _myLineStyle = style.intValue();
  }

  /**
   * the line thickness (convenience wrapper around width)
   */
  @FireReformatted
  public final void setLineThickness(final int val)
  {
    _lineWidth = val;
  }

  @Override
  public void setName(final String name)
  {
    // ok, this object doesn't have concept of name. So, we can't rename it
    Trace.trace("We can't rename Dynamic Track Shape objects");
  }

  /**
   * inform us of our sensor
   */
  public final void setParent(final DynamicTrackShapeSetWrapper sensor)
  {
    _mySensor = sensor;
  }

  /**
   * set the Label visibility
   */
  public final void setSemiTransparent(final boolean val)
  {
    _semiTransparent = val;
  }

  public void setStartDTG(final HiResDate date)
  {
    _startDTG = date;
  }

  /**
   * toString
   * 
   * @return the returned String
   */
  @Override
  public final String toString()
  {
    return getName();
  }

}
