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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.DynamicPlottable;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.WatchableList;

public class DynamicTrackShapeSetWrapper extends BaseLayer implements
    Cloneable, DynamicPlottable
{
  // //////////////////////////////////////////////////////////////////////////
  // embedded class, used for editing the projection
  // //////////////////////////////////////////////////////////////////////////
  /**
   * the definition of what is editable about this object
   */
  public final class SensorInfo extends Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     * 
     * @param data
     *          the Layers themselves
     */
    public SensorInfo(final DynamicTrackShapeSetWrapper data)
    {
      super(data, data.getName(), "Sensor");
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
            {
                prop("Name", "the name for this sensor"),
                prop("Visible", "whether this sensor data is visible"),
                displayProp("LineThickness", "Line thickness",
                    "the thickness to draw these sensor lines")};

        res[2]
            .setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);

        return res;
      }
      catch (final IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }
  }

  static public final class testSensors extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testSensors(final String val)
    {
      super(val);
    }

    public final void testValues()
    {

    }

  }

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  public static void main(final String[] args)
  {
    final testSensors ts = new testSensors("Ian");
    ts.testValues();
  }

  /**
   * our editor
   */
  protected transient MWC.GUI.Editable.EditorType _myEditor;

  private int _lineWidth;

  // //////////////////////////////////////
  // member methods to meet plain wrapper responsibilities
  // //////////////////////////////////////

  private TrackWrapper _myHost;

  private BaseTimePeriod _timePeriod;

  // //////////////////////////////////////
  // constructors
  /**
   * ////////////////////////////////////////
   */
  public DynamicTrackShapeSetWrapper(final String title)
  {
    super.setName(title);
  }

  // public final void append(final Layer theLayer)
  // {
  // if (theLayer instanceof DynamicTrackShapeSetWrapper)
  // {
  // final DynamicTrackShapeSetWrapper other = (DynamicTrackShapeSetWrapper) theLayer;
  //
  // final Enumeration<Editable> it = other.elements();
  // while (it.hasMoreElements())
  // {
  // final DynamicTrackShapeWrapper fw = (DynamicTrackShapeWrapper) it
  // .nextElement();
  // this.add(fw);
  // }
  //
  // // and clear him out...
  // other.removeAllElements();
  // }
  // }

  /**
   * add
   * 
   * @param plottable
   *          parameter for add
   */
  @Override
  public final void add(final MWC.GUI.Editable plottable)
  {
    // check it's a sensor contact entry
    if (plottable instanceof DynamicTrackShapeWrapper)
    {
      super.add(plottable);

      final DynamicTrackShapeWrapper scw = (DynamicTrackShapeWrapper) plottable;

      // maintain our time period
      if (_timePeriod == null)
      {
        _timePeriod =
            new MWC.GenericData.TimePeriod.BaseTimePeriod(scw.getStartDTG(),
                scw.getEndDTG());
      }
      else
      {
        _timePeriod.extend(scw.getStartDTG());
        _timePeriod.extend(scw.getEndDTG());
      }

      // and tell the contact about us
      scw.setParent(this);
    }
  }

  // ///////////////////////////////////////
  // other member functions
  // ///////////////////////////////////////

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

  public WatchableList getHost()
  {
    return _myHost;
  }

  // ////////////////////////////////////////////////////
  // nested class for testing
  // /////////////////////////////////////////////////////

  /**
   * getInfo
   * 
   * @return the returned MWC.GUI.Editable.EditorType
   */
  @Override
  public final MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
    {
      _myEditor = new SensorInfo(this);
    }

    return _myEditor;
  }

  /**
   * the line thickness (convenience wrapper around width)
   * 
   * @return
   */
  @Override
  public final int getLineThickness()
  {
    return _lineWidth;
  }

  public Editable getSampleGriddable()
  {
    Editable res = null;

    // check we have an item before we edit it
    final Enumeration<Editable> eles = this.elements();
    if (eles.hasMoreElements())
    {
      res = eles.nextElement();
    }
    return res;
  }

  @Override
  public boolean hasEditor()
  {
    return true;
  }

  @Override
  public boolean hasOrderedChildren()
  {
    return false;
  }

  @Override
  public void paint(final CanvasType dest)
  {
    // this method shouldn't be called = we're a time dependent object
    MWC.Utilities.Errors.Trace
        .trace("Sensor Arc Wrapper paint() should not be called!");

  }

  @Override
  public void paint(final CanvasType canvas, final long time)
  {
    if (!getVisible())
    {
      return;
    }

    // remember the current line width
    final float oldLineWidth = canvas.getLineWidth();

    // set the line width
    canvas.setLineWidth(getLineThickness());

    // trigger our child sensor contact data items to plot themselves
    final Enumeration<Editable> it = this.elements();
    while (it.hasMoreElements())
    {
      final DynamicTrackShapeWrapper con =
          (DynamicTrackShapeWrapper) it.nextElement();

      final HiResDate dtg = new HiResDate(time);

      if (con.getStartDTG() != null && dtg.lessThan(con.getStartDTG()))
      {
        continue;
      }
      if (con.getEndDTG() != null && dtg.greaterThanOrEqualTo(con.getEndDTG()))
      {
        continue;
      }
      // ok, plot it - and don't make it keep it simple, lets really go
      // for it man!
      con.paint(canvas, dtg);
    }

    // and restore the line width
    canvas.setLineWidth(oldLineWidth);

  }

  /**
   * set our host track
   */
  public void setHost(final TrackWrapper host)
  {
    _myHost = host;
  }

  /**
   * the line thickness (convenience wrapper around width)
   */
  @Override
  @FireReformatted
  public final void setLineThickness(final int val)
  {
    _lineWidth = val;
  }

  /**
   */

  @Override
  public final String toString()
  {
    return getName() + " (" + size() + " items)";
  }

  public void trimTo(final TimePeriod period)
  {
    final java.util.SortedSet<Editable> newList =
        new java.util.TreeSet<Editable>();

    final Enumeration<Editable> it = this.elements();
    while (it.hasMoreElements())
    {
      final DynamicTrackShapeWrapper thisE =
          (DynamicTrackShapeWrapper) it.nextElement();
      if (period.overlaps(thisE.getPeriod()))
      {
        newList.add(thisE);
      }
    }

    // ditch the current items
    removeAllElements();

    // ok, copy over the matching items
    // add the items individually, so we can update the segment
    for (final Editable item : newList)
    {
      add(item);
    }
  }

}
