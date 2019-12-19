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
package Debrief.Wrappers.Track;

import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Plottables;
import MWC.GUI.SupportsPropertyListeners;

public class TrackWrapper_Support
{

  /**
   * convenience class that makes our plottables look like a layer
   *
   * @author ian.mayo
   */
  abstract public static class BaseItemLayer extends Plottables implements
      Layer, SupportsPropertyListeners
  {

    /**
     * class containing editable details of a track
     */
    public final class BaseLayerInfo extends Editable.EditorType
    {

      /**
       * constructor for this editor, takes the actual track as a parameter
       *
       * @param data
       *          track being edited
       */
      public BaseLayerInfo(final BaseItemLayer data)
      {
        super(data, data.getName(), "");
      }

      @Override
      public final String getName()
      {
        return super.getName();
      }

      @Override
      public final PropertyDescriptor[] getPropertyDescriptors()
      {
        try
        {
          final PropertyDescriptor[] res =
          {expertProp("Visible", "whether this layer is visible", FORMAT),};
          return res;
        }
        catch (final IntrospectionException e)
        {
          e.printStackTrace();
          return super.getPropertyDescriptors();
        }
      }
    }

    public final static String WRAPPER_CHANGED = "WrapperChanged";

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * property support
     *
     */
    private transient PropertyChangeSupport _pSupport = null;

    protected TrackWrapper _myTrack;

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener)
    {
      checkSupport();
      _pSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(final String property,
        final PropertyChangeListener listener)
    {
      checkSupport();
      _pSupport.addPropertyChangeListener(property, listener);
    }

    private void checkSupport()
    {
      if (_pSupport == null)
      {
        _pSupport = new PropertyChangeSupport(this);
      }
    }

    @Override
    public void exportShape()
    {
      // ignore..
    }

    @Override
    public void firePropertyChange(final String propertyChanged,
        final Object oldValue, final Object newValue)
    {
      checkSupport();
      _pSupport.firePropertyChange(propertyChanged, oldValue, newValue);
    }

    /**
     * get the editing information for this type
     */
    @Override
    public Editable.EditorType getInfo()
    {
      return new BaseLayerInfo(this);
    }

    @Override
    public int getLineThickness()
    {
      // ignore..
      return 1;
    }

    public TrackWrapper getWrapper()
    {
      return _myTrack;
    }

    @Override
    public boolean hasOrderedChildren()
    {
      return true;
    }

    @Override
    public void removePropertyChangeListener(
        final PropertyChangeListener listener)
    {
      checkSupport();
      _pSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(final String property,
        final PropertyChangeListener listener)
    {
      checkSupport();
      _pSupport.removePropertyChangeListener(property, listener);
    }

    public void setWrapper(final TrackWrapper wrapper)
    {
      final TrackWrapper oldWrap = _myTrack;

      _myTrack = wrapper;

      // tell anybody who is interested
      checkSupport();
      _pSupport.firePropertyChange(WRAPPER_CHANGED, _myTrack, oldWrap);
    }

  }

  /**
   * interface defining a boolean operation which is applied to all fixes in a track
   */
  public interface FixSetter
  {
    /**
     * operation to apply to a fix
     *
     * @param fix
     *          subject of operation
     * @param val
     *          yes/no value to apply
     */
    public void execute(FixWrapper fix, boolean val);
  }

  /**
   * embedded class to allow us to pass the local iterator (Iterator) used internally outside as an
   * Enumeration
   */
  public static final class IteratorWrapper implements
      java.util.Enumeration<Editable>
  {
    private final Iterator<Editable> _val;

    public IteratorWrapper(final Iterator<Editable> iterator)
    {
      _val = iterator;
    }

    @Override
    public final boolean hasMoreElements()
    {
      return _val.hasNext();

    }

    @Override
    public final Editable nextElement()
    {
      return _val.next();
    }
  }

  /**
   * the collection of track segments
   *
   * @author Administrator
   *
   */
  final public static class SegmentList extends BaseItemLayer
  {

    /**
     * class containing editable details of a track
     */
    public final class SegmentInfo extends Editable.EditorType
    {

      /**
       * constructor for this editor, takes the actual track as a parameter
       *
       * @param data
       *          track being edited
       */
      public SegmentInfo(final SegmentList data)
      {
        super(data, data.getName(), "");
      }

      @Override
      public final MethodDescriptor[] getMethodDescriptors()
      {
        // just add the reset color field first
        final Class<SegmentList> c = SegmentList.class;
        MethodDescriptor[] mds =
        {method(c, "mergeAllSegments", null, "Merge all track segments"),
            method(c, "revealAllPositions", null, "Reveal All Positions")};

        final MethodDescriptor[] oldMeds = super.getMethodDescriptors();
        // we now need to combine the two sets
        if (oldMeds != null)
        {
          final MethodDescriptor resMeds[] = new MethodDescriptor[mds.length
              + oldMeds.length];
          System.arraycopy(mds, 0, resMeds, 0, mds.length);
          System.arraycopy(oldMeds, 0, resMeds, mds.length, oldMeds.length);
          mds = resMeds;
        }
        return mds;
      }

      @Override
      public final String getName()
      {
        return super.getName();
      }

      @Override
      public final PropertyDescriptor[] getPropertyDescriptors()
      {
        try
        {
          final PropertyDescriptor[] res =
          {expertProp("Visible", "whether this layer is visible", FORMAT),};
          return res;
        }
        catch (final IntrospectionException e)
        {
          e.printStackTrace();
          return super.getPropertyDescriptors();
        }
      }
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SegmentList()
    {
      setName("Positions");
    }

    @Override
    public void add(final Editable item)
    {
      // ok, when the cut/paste operations are done/undone
      // we may get asked to put back a previously
      // removed track segment
      if (item instanceof TrackSegment)
      {
        addSegment((TrackSegment) item);
      }
      else
      {
        System.err.println("SHOULD NOT BE ADDING NORMAL ITEM TO SEGMENT LIST");
      }
    }

    public void addSegment(final TrackSegment segment)
    {
      segment.setWrapper(_myTrack);

      if (this.size() == 1)
      {
        // aah, currently, it's name's probably wrong sort out it's date
        final TrackSegment first = (TrackSegment) getData().iterator().next();
        first.sortOutDateLabel(null);
      }

      super.add(segment);

      // if we've just got the one, set it's name to positions
      if (this.size() == 1)
      {
        final TrackSegment first = (TrackSegment) getData().iterator().next();
        first.setName("Positions");
      }
    }

    @Override
    public void append(final Layer other)
    {
      System.err.println("SHOULD NOT BE ADDING LAYER TO SEGMENTS LIST");
    }

    @Override
    protected String collectiveName()
    {
      return "legs";
    }

    @Override
    public EditorType getInfo()
    {
      return new SegmentInfo(this);
    }

    public TrackSegment getSegmentFor(final long time)
    {
      // update our segments
      final Collection<Editable> items = getData();
      for (final Iterator<Editable> iterator = items.iterator(); iterator
          .hasNext();)
      {
        final TrackSegment seg = (TrackSegment) iterator.next();
        if (seg.endDTG().getDate().getTime() >= time && seg.startDTG().getDate()
            .getTime() <= time)
        {
          return seg;
        }
      }

      return null;
    }

    @Override
    public boolean hasEditor()
    {
      return true;
    }

    @FireExtended
    public void mergeAllSegments()
    {
      final Collection<Editable> segs = getData();
      TrackSegment first = null;
      for (final Iterator<Editable> iterator = segs.iterator(); iterator
          .hasNext();)
      {
        final TrackSegment segment = (TrackSegment) iterator.next();

        if (first == null)
        {
          // aaah, now, if this is a TMA segment we've got to replace it with
          // a normal track segment. You can't join new track sections onto the
          // end
          // of a tma segment
          if (segment instanceof CoreTMASegment)
          {
            final CoreTMASegment tma = (CoreTMASegment) segment;
            first = new TrackSegment(tma);
          }
          else
          {
            // cool, just go ahead
            first = segment;
          }
        }
        else
        {
          first.append((Layer) segment);
        }
      }

      // ditch the segments
      this.removeAllElements();

      // and put the first one back in
      this.addSegment(first);

      // and fire some kind of update...
    }

    @Override
    public void removeElement(final Editable p)
    {
      super.removeElement(p);

      // if it's a dynamic infill, we've got to clear it
      if (p instanceof DynamicInfillSegment)
      {
        final DynamicInfillSegment fill = (DynamicInfillSegment) p;
        fill.clear();
      }

      final TrackSegment seg = (TrackSegment) p;
      seg.setWrapper(null);

    }

    /**
     * utility method to reveal all positions in a track
     *
     */
    @FireReformatted
    public void revealAllPositions()
    {
      final Enumeration<Editable> theEnum = elements();
      while (theEnum.hasMoreElements())
      {
        final TrackSegment seg = (TrackSegment) theEnum.nextElement();
        final Enumeration<Editable> ele = seg.elements();
        while (ele.hasMoreElements())
        {
          final Editable editable = ele.nextElement();
          final FixWrapper fix = (FixWrapper) editable;
          fix.setVisible(true);
        }
      }
    }

    @Override
    public void setWrapper(final TrackWrapper wrapper)
    {
      // is it different?
      if (wrapper == _myTrack)
        return;

      // store the value
      super.setWrapper(wrapper);

      // update our segments
      final Collection<Editable> items = getData();
      for (final Iterator<Editable> iterator = items.iterator(); iterator
          .hasNext();)
      {
        final TrackSegment seg = (TrackSegment) iterator.next();
        seg.setWrapper(_myTrack);
      }
    }
  }

  public static List<TrackSegment> splitTrackAtJumps(final TrackWrapper track,
      final long interval)
  {
    final Enumeration<Editable> segs = track.getSegments().elements();
    final List<FixWrapper> jumps = new ArrayList<FixWrapper>();
    final List<TrackSegment> newSegments = new ArrayList<TrackSegment>();

    // find the jumps
    while (segs.hasMoreElements())
    {
      final TrackSegment segment = (TrackSegment) segs.nextElement();
      final Enumeration<Editable> posits = segment.elements();
      Long lastT = null;
      while (posits.hasMoreElements())
      {
        final FixWrapper next = (FixWrapper) posits.nextElement();
        final long thisT = next.getDTG().getDate().getTime();
        if (lastT != null)
        {
          final long delta = thisT - lastT;
          if (delta > interval)
          {
            jumps.add(next);
          }
        }
        lastT = thisT;
      }
    }

    // now split on the jumps
    for (final FixWrapper jump : jumps)
    {
      // what's the old segment for this fix
      final TrackSegment oldSegment = jump.getSegment();
      final Vector<TrackSegment> newSegs = track.splitTrack(jump, true);

      // the old segment has been replaced by two new ones, so delete it
      newSegments.remove(oldSegment);

      // now put in the two new segments
      for (final TrackSegment seg : newSegs)
      {
        if (!newSegments.contains(seg))
        {
          newSegments.add(seg);
        }
      }
    }

    return newSegments;
  }

}
