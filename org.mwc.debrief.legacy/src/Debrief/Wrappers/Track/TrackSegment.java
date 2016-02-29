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

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;

import junit.framework.TestCase;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.BaseItemLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsWrappingInLayerManager;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

/**
 * a single collection of track points
 * 
 * @author Administrator
 * 
 */
public class TrackSegment extends BaseItemLayer implements DraggableItem,
		GriddableSeriesMarker, NeedsWrappingInLayerManager
{

	public static class testListMgt extends TestCase
	{

		public void testTrim()
		{
			TrackSegment ts0 = getDummyList();
			TimePeriod newP = new TimePeriod.BaseTimePeriod(new HiResDate(30000),
					new HiResDate(40000));
			assertEquals("correct len", 4, ts0.size());
			ts0.trimTo(newP);
			assertEquals("correct new len", 2, ts0.size());

			ts0 = getDummyList();
			newP = new TimePeriod.BaseTimePeriod(new HiResDate(35000), new HiResDate(
					40000));
			assertEquals("correct len", 4, ts0.size());
			ts0.trimTo(newP);
			assertEquals("correct new len", 1, ts0.size());

			ts0 = getDummyList();
			newP = new TimePeriod.BaseTimePeriod(new HiResDate(15000), new HiResDate(
					40000));
			assertEquals("correct len", 4, ts0.size());
			ts0.trimTo(newP);
			assertEquals("correct new len", 3, ts0.size());

			ts0 = getDummyList();
			newP = new TimePeriod.BaseTimePeriod(new HiResDate(45000), new HiResDate(
					50000));
			assertEquals("correct len", 4, ts0.size());
			ts0.trimTo(newP);
			assertEquals("correct new len", 0, ts0.size());

		}

		private TrackSegment getDummyList()
		{
			final TrackSegment ts0 = new TrackSegment();
			final FixWrapper newFix1 = new FixWrapper(new Fix(new HiResDate(10000),
					new WorldLocation(1, -1, 3), 1, 2));
			final FixWrapper newFix2 = new FixWrapper(new Fix(new HiResDate(20000),
					new WorldLocation(1, 0, 3), 1, 2));
			final FixWrapper newFix3 = new FixWrapper(new Fix(new HiResDate(30000),
					new WorldLocation(1, 1, 3), 1, 2));
			final FixWrapper newFix4 = new FixWrapper(new Fix(new HiResDate(40000),
					new WorldLocation(1, 2, 3), 1, 2));
			ts0.addFix(newFix1);
			ts0.addFix(newFix2);
			ts0.addFix(newFix3);
			ts0.addFix(newFix4);
			return ts0;
		}
	}

	/**
	 * class containing editable details of a track
	 */
	public class TrackSegmentInfo extends Editable.EditorType
	{

		/**
		 * constructor for this editor, takes the actual track as a parameter
		 * 
		 * @param data
		 *          track being edited
		 */
		public TrackSegmentInfo(final TrackSegment data)
		{
			super(data, data.getName(), "");
		}

		@Override
		public final MethodDescriptor[] getMethodDescriptors()
		{

			// just add the reset color field first
			final Class<TrackSegment> c = TrackSegment.class;
			MethodDescriptor[] newMeds =
			{ method(c, "revealAllPositions", null, "Reveal all positions") };

			final MethodDescriptor[] mds = super.getMethodDescriptors();
			// we now need to combine the two sets
			if (mds != null)
			{
				final MethodDescriptor resMeds[] = new MethodDescriptor[mds.length
						+ newMeds.length];
				System.arraycopy(mds, 0, resMeds, 0, mds.length);
				System.arraycopy(newMeds, 0, resMeds, mds.length, newMeds.length);
				newMeds = resMeds;
			}
			return newMeds;
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ expertProp("Visible", "whether this layer is visible", FORMAT),
						displayExpertProp("LineStyle", "Line style", "how to plot this line", FORMAT),
						expertProp("Name", "Name of this track segment", FORMAT) };
				res[1].setPropertyEditorClass(LineStylePropertyEditor.class);
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
	 * someone to share life's troubles with
	 * 
	 */
	protected transient static ToolParent _myParent;

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	/**
	 * learn about the shared trouble reporter...
	 * 
	 * @param toolParent
	 */
	public static void initialise(final ToolParent toolParent)
	{
		_myParent = toolParent;
	}

	/**
	 * define the length of the stalk we plot when dragging
	 * 
	 */
	private final int STALK_LEN = 200;

	/**
	 * whether to determine this track's positions using DR calculations
	 * 
	 */
	boolean _plotRelative;

	private transient WorldVector _vecTempLastVector = null;

	protected long _vecTempLastDTG = -2;

	/**
	 * how to plot this line
	 * 
	 */
	private int _lineStyle = CanvasType.SOLID;

	public static final String TMA_LEADER = "TMA_";

	public TrackSegment()
	{
		// no-op constructor
	}

	/**
	 * constructor that builds a plain track segment from a tma segment - an
	 * operation we must do when we try to merge track segments
	 * 
	 * @param tma
	 */
	public TrackSegment(final CoreTMASegment tma)
	{
		setName(tma.getName());
		setVisible(tma.getVisible());
		setWrapper(tma.getWrapper());

		// add the elements from the target
		final Enumeration<Editable> points = tma.elements();
		while (points.hasMoreElements())
		{
			final Editable next = points.nextElement();
			add(next);
		}
	}

	/**
	 * create a segment based on the suppplied items
	 * 
	 * @param theItems
	 */
	public TrackSegment(final SortedSet<Editable> theItems)
	{
		getData().addAll(theItems);

		// now sort out the name
		sortOutDate(null);
	}

	@Override
	public void add(final Editable item)
	{
		if (item instanceof FixWrapper)
			addFix((FixWrapper) item);
		else
			System.err.println("SHOULD NOT BE ADDING NORMAL ITEM TO TRACK SEGMENT");
	}

	public void addFix(final FixWrapper fix)
	{
		// remember the fix
		this.addFixSilent(fix);

		// override the name, just in case this point is earlier
		sortOutDate(null);
	}

	public void addFixSilent(final FixWrapper fix)
	{
		super.add(fix);

		// and register the listener (if we know our track)
		if (_myTrack != null)
		{
			// tell it about our daddy
			fix.setTrackWrapper(_myTrack);
			fix.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED,
					_myTrack.getLocationListener());
		}
	}

	/**
	 * add the elements in the indicated layer to us.
	 * 
	 */
	@Override
	public void append(final Layer other)
	{
		// get the other track's elements
		final Enumeration<Editable> enumer = other.elements();

		// have a look and see if we're a DR track
		if (this.getPlotRelative())
		{
			// right, we've got to make sure our last point is correctly
			// pointing to
			// the first point of this new track
			// - sort it out
			final FixWrapper first = (FixWrapper) enumer.nextElement();
			final FixWrapper myLast = (FixWrapper) this.last();
			final WorldVector offset = first.getLocation().subtract(
					myLast.getLocation());

			final double courseRads = offset.getBearing();
			final double timeSecs = (first.getTime().getDate().getTime() - myLast
					.getTime().getDate().getTime()) / 1000;
			// start off with the course

			// and now the speed
			final double distYds = new WorldDistance(offset.getRange(),
					WorldDistance.DEGS).getValueIn(WorldDistance.YARDS);
			final double spdYps = distYds / timeSecs;
			final double thisSpeedKts = MWC.Algorithms.Conversions.Yps2Kts(spdYps);

			myLast.setCourse(courseRads);
			myLast.setSpeed(thisSpeedKts);

			// and add this one
			addFix(first);
		}

		// ok, pass through and add the remaining items
		while (enumer.hasMoreElements())
		{
			final FixWrapper pl = (FixWrapper) enumer.nextElement();

			addFix(pl);
		}
	}

	/**
	 * sort the items in ascending order
	 * 
	 */
	@Override
	public int compareTo(final Plottable arg0)
	{
		int res = 0;
		if (arg0 instanceof TrackSegment)
		{
			// sort them in dtg order
			final TrackSegment other = (TrackSegment) arg0;
			if ((startDTG() != null) && (other.startDTG() != null))
				res = startDTG().compareTo(other.startDTG());
			else
				res = getName().compareTo(arg0.getName());
		}
		else
		{
			// just use string comparison
			res = getName().compareTo(arg0.getName());
		}
		return res;
	}

	/**
	 * switch the sample rate of this track to the supplied frequency
	 * 
	 * @param theVal
	 */
	public void decimate(final HiResDate theVal, final TrackWrapper parentTrack,
			final long startTime)
	{
		final Vector<FixWrapper> newItems = new Vector<FixWrapper>();
		final boolean oldInterpolateState = parentTrack.getInterpolatePoints();

		// switch on interpolation
		parentTrack.setInterpolatePoints(true);

		// right, are we a relative or absolute track?
		if (this.getPlotRelative())
			decimateRelative(theVal, parentTrack, startTime, newItems);
		else
			decimateAbsolute(theVal, parentTrack, startTime, newItems);

		// ditch our positions
		this.removeAllElements();

		// store the new positions
		for (final Iterator<FixWrapper> iterator = newItems.iterator(); iterator
				.hasNext();)
		{
			final FixWrapper fix = iterator.next();
			this.addFix(fix);
		}

		// re-instate the interpolate status
		parentTrack.setInterpolatePoints(oldInterpolateState);
	}

	private void decimateAbsolute(final HiResDate theVal,
			final TrackWrapper parentTrack, final long startTime,
			final Vector<FixWrapper> newItems)
	{
		// right - sort out what time period we're working through
		for (long tNow = startTime; tNow <= endDTG().getMicros(); tNow += theVal
				.getMicros())
		{
			// store the new position
			final Watchable[] matches = parentTrack.getNearestTo(new HiResDate(0,
					tNow));
			if (matches.length > 0)
			{
				final FixWrapper newF = (FixWrapper) matches[0];

				// do we correct the name?
				if (newF.getName().equals(FixWrapper.INTERPOLATED_FIX))
				{
					// reset the name
					newF.resetName();
				}

				newF.setSymbolShowing(true);

				// add to our working list
				newItems.add(newF);
			}
		}
	}

	private void decimateRelative(final HiResDate theVal,
			final TrackWrapper parentTrack, final long startTime,
			final Vector<FixWrapper> newItems)
	{
		long tNow = 0;
		long theStartTime = startTime;

		// get the time interval
		final long interval = theVal.getMicros();

		// round myStart time to the supplied interval
		long myStart = this.startDTG().getMicros();
		myStart = (myStart / interval) * interval;

		// set the start time to be the later of our start time and the provided
		// time
		theStartTime = Math.max(theStartTime, myStart);

		if (this instanceof CoreTMASegment)
		{
			final CoreTMASegment tma = (CoreTMASegment) this;

			// hey, it's a TMA segment - on steady course/speed. cool
			final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(tma
					.getCourse());
			final double speedYps = tma.getSpeed().getValueIn(WorldSpeed.ft_sec) / 3;

			// find the new start location - after we've slipped
			final WorldLocation myStartLocation = new WorldLocation(
					tma.getTrackStart());

			// right - sort out what time period we're working through
			for (tNow = theStartTime; tNow <= endDTG().getMicros(); tNow += theVal
					.getMicros())
			{
				final Fix theFix = new Fix(new HiResDate(0, tNow), new WorldLocation(
						myStartLocation), courseRads, speedYps);
				final FixWrapper newFix = new FixWrapper(theFix);
				newFix.setSymbolShowing(true);

				// also give it a name
				newFix.resetName();

				newItems.add(newFix);
			}

			// right, if it's a relative segment, then we need to shift the
			// offset to
			// reflect the new relationship
			if (tma instanceof RelativeTMASegment)
			{
				final FixWrapper myStarter = (FixWrapper) tma.first();
				final FixWrapper myEnder = (FixWrapper) tma.last();
				final HiResDate startDTG = new HiResDate(0, theStartTime);
				final FixWrapper newStarter = FixWrapper.interpolateFix(myStarter,
						myEnder, startDTG);
				final WorldLocation newStartLoc = newStarter.getLocation();

				final RelativeTMASegment rel = (RelativeTMASegment) tma;
				final Watchable[] newHost = rel.getReferenceTrack().getNearestTo(
						startDTG);
				if (newHost.length > 0)
				{
					final WorldLocation newOrigin = newHost[0].getLocation();
					final WorldVector newOffset = newStartLoc.subtract(newOrigin);
					rel.setOffset(newOffset);
				}

				// is our name date-oriented?
				if (rel.getName().startsWith(TMA_LEADER))
				{
					// yes, calculate a new one

					// lastly, reset the track name
					rel.sortOutDate(startDTG);

					// and change the track name
					rel._myTrack.setName(rel.getName());
				}
			}

		}
		else
		{
			FixWrapper lastPositionStored = null;
			FixWrapper currentPosition = null;
			tNow = 0;

			// right - sort out what time period we're working through
			for (tNow = theStartTime; tNow <= endDTG().getMicros(); tNow += theVal
					.getMicros())
			{

				// find hte new datum
				final Watchable[] matches = parentTrack.getNearestTo(new HiResDate(0,
						tNow));
				if (matches.length > 0)
				{
					// remember the last position - we;re going to be
					// calculating future
					// courses and speeds from it
					lastPositionStored = currentPosition;

					currentPosition = (FixWrapper) matches[0];

					// is this our first point?
					if (lastPositionStored != null)
					{
						// start off with the course
						final WorldVector offset = currentPosition.getLocation().subtract(
								lastPositionStored.getLocation());
						lastPositionStored.getFix().setCourse(offset.getBearing());

						// and now the speed
						final double distYds = new WorldDistance(offset.getRange(),
								WorldDistance.DEGS).getValueIn(WorldDistance.YARDS);
						final double timeSecs = (tNow - lastPositionStored.getTime()
								.getMicros()) / 1000000d;
						final double spdYps = distYds / timeSecs;
						lastPositionStored.getFix().setSpeed(spdYps);

						// do we correct the name?
						if (lastPositionStored.getName()
								.equals(FixWrapper.INTERPOLATED_FIX))
						{
							// reset the name
							lastPositionStored.resetName();
						}
						// add to our working list
						newItems.add(lastPositionStored);
					}

				}

			}
		}

	}

	@Override
	public void doSave(final String message)
	{
		throw new RuntimeException(
				"should not have called manual save for Track Segment");
	}

	protected void drawMyStalk(final CanvasType dest, final Point lastPoint,
			final Point thisPoint, final boolean forwards)
	{
		// yup, we've now got just two points. plot a 'back-trace'
		final double xDelta = thisPoint.x - lastPoint.x;
		final double yDelta = thisPoint.y - lastPoint.y;

		final double gradient = xDelta / yDelta;

		int myLen = STALK_LEN;
		if (!forwards)
			myLen = -STALK_LEN;

		final Point backPoint = new Point(lastPoint.x + (int) (myLen * gradient),
				lastPoint.y + myLen);
		dest.setLineStyle(2);
		dest.drawLine(lastPoint.x, lastPoint.y, backPoint.x, backPoint.y);

		// hey, chuck in a circle
		final int radius = 10;
		dest.drawOval(lastPoint.x - radius - (int) xDelta, lastPoint.y - radius
				- (int) yDelta, radius * 2, radius * 2);

		dest.setLineStyle(CanvasType.SOLID);
	}

	public HiResDate endDTG()
	{
		HiResDate res = null;
		final Collection<Editable> items = getData();
		if ((items != null && (items.size() > 0)))
		{
			final SortedSet<Editable> sortedItems = (SortedSet<Editable>) items;
			final Editable last = sortedItems.last();
			final FixWrapper fw = (FixWrapper) last;
			res = fw.getDateTimeGroup();
		}
		return res;
	}

	@Override
	public void findNearestHotSpotIn(final Point cursorPos,
			final WorldLocation cursorLoc, final LocationConstruct currentNearest,
			final Layer parentLayer, final Layers theData)
	{
	}

	@Override
	public Editable.EditorType getInfo()
	{
		return new TrackSegmentInfo(this);
	}

	/**
	 * how this line is plotted
	 * 
	 * @return
	 */
	public int getLineStyle()
	{
		return _lineStyle;
	}

	public boolean getPlotRelative()
	{
		return _plotRelative;
	}

	@Override
	public Editable getSampleGriddable()
	{
		final HiResDate theTime = new HiResDate(10000000);
		final WorldLocation theLocation = new WorldLocation(1, 1, 1);
		final double courseRads = 3;
		final double speedKts = 5;
		final Fix newFix = new Fix(theTime, theLocation, courseRads, speedKts);
		final FixWrapper res = new FixWrapper(newFix);
		return res;
	}

	/**
	 * get the start of this segment (it's the location of the first point).
	 * 
	 * @return
	 */
	public WorldLocation getTrackStart()
	{
		final FixWrapper firstW = (FixWrapper) getData().iterator().next();
		return firstW.getFixLocation();
	}

	@Override
	public TimeStampedDataItem makeCopy(final TimeStampedDataItem item)
	{
		if (false == item instanceof FixWrapper)
		{
			throw new IllegalArgumentException(
					"I am expecting a position, don't know how to copy " + item);
		}
		final FixWrapper template = (FixWrapper) item;
		final FixWrapper result = new FixWrapper(template.getFix().makeCopy());
		result.setLabelShowing(template.getLabelShowing());
		result.setLineShowing(template.getLineShowing());
		result.setSymbolShowing(template.getSymbolShowing());
		result.setArrowShowing(template.getArrowShowing());
		result.setLabelLocation(template.getLabelLocation());

		final Color col = template.getActualColor();
		if (col != null)
			result.setColor(col);

		return result;
	}

	@Override
	public void paint(final CanvasType dest)
	{
		final Collection<Editable> items = getData();

		// ok - draw that line!
		Point lastPoint = null;
		Point lastButOne = null;
		for (final Iterator<Editable> iterator = items.iterator(); iterator
				.hasNext();)
		{
			final FixWrapper thisF = (FixWrapper) iterator.next();

			final Point thisPoint = dest.toScreen(thisF.getFixLocation());

			// do we have enough for a line?
			if (lastPoint != null)
			{
				// draw that line
				dest.drawLine(lastPoint.x, lastPoint.y, thisPoint.x, thisPoint.y);

				// are we at the start of the line?
				if (lastButOne == null)
				{
					drawMyStalk(dest, lastPoint, thisPoint, false);
				}
			}

			lastButOne = lastPoint;
			lastPoint = new Point(thisPoint);

			// also draw in a marker for this point
			dest.drawRect(lastPoint.x - 1, lastPoint.y - 1, 3, 3);
		}

		// lastly 'plot on' from the last points
		drawMyStalk(dest, lastPoint, lastButOne, true);

	}

	@Override
	public double rangeFrom(final WorldLocation other)
	{
		double res = Plottable.INVALID_RANGE;

		// have we got data?
		final WorldLocation firstLoc = this.getTrackStart();

		// do we have a start point?
		if (firstLoc != null)
		{
			// yes, sort range
			res = firstLoc.rangeFrom(other);

			// and try for the track end
			final Plottable lastP = this.last();
			// do we have an end point?
			if (lastP != null)
			{
				final FixWrapper lastF = (FixWrapper) lastP;
				final WorldLocation lastLoc = lastF.getLocation();
				final double otherRng = lastLoc.rangeFrom(other);
				res = Math.min(otherRng, res);
			}
		}
		return res;
	}

	@Override
	public boolean requiresManualSave()
	{
		return false;
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
			final Editable editable = theEnum.nextElement();
			final FixWrapper fix = (FixWrapper) editable;
			fix.setVisible(true);
		}
	}

	/**
	 * rotate this whole track around the supplied origin
	 * 
	 * @param brg
	 *          angle to rotate through (Radians)
	 * @param origin
	 *          origin of rotation, probably one end of the track
	 */
	public void rotate(final double brg, final WorldLocation origin)
	{
		// add this vector to all my points.
		final Collection<Editable> items = getData();
		for (final Iterator<Editable> iterator = items.iterator(); iterator
				.hasNext();)
		{
			final FixWrapper thisFix = (FixWrapper) iterator.next();

			// is this us?
			if (thisFix.getLocation() == origin)
			{
				// ignore, it's the origin
			}
			else
			{
				final WorldLocation newLoc = thisFix.getLocation().rotatePoint(origin,
						brg);
				thisFix.setFixLocation(newLoc);
			}
		}
	}

	/**
	 * specify how this line is to be plotted
	 * 
	 * @param lineStyle
	 */
	public void setLineStyle(final int lineStyle)
	{
		_lineStyle = lineStyle;
	}

	public void setPlotRelative(final boolean plotRelative)
	{
		_plotRelative = plotRelative;
	}

  @Override
  public void setWrapper(final TrackWrapper wrapper)
  {

    // is it different?
    if (wrapper == _myTrack)
      return;

    // ok, and clear the property change listeners, if necessary
    if (_myTrack != null)
    {
      // work through our fixes
      final Collection<Editable> items = getData();
      for (final Iterator<Editable> iterator = items.iterator(); iterator
          .hasNext();)
      {
        final FixWrapper fix = (FixWrapper) iterator.next();
        // now clear this property listener
        fix.removePropertyChangeListener(PlainWrapper.LOCATION_CHANGED,
            _myTrack.getLocationListener());
      }
    }

    // store the value
    super.setWrapper(wrapper);

    if (wrapper != null)
    {
      // update our segments
      final Collection<Editable> items = getData();
      for (final Iterator<Editable> iterator = items.iterator(); iterator
          .hasNext();)
      {
        final FixWrapper fix = (FixWrapper) iterator.next();
        fix.setTrackWrapper(_myTrack);
        // and let the track wrapper listen to location changed events
        fix.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED, wrapper
            .getLocationListener());
      }
    }
  }

	@Override
	public void shift(final WorldVector vector)
	{
		// add this vector to all my points.
		final Collection<Editable> items = getData();
		for (final Iterator<Editable> iterator = items.iterator(); iterator
				.hasNext();)
		{
			final FixWrapper thisFix = (FixWrapper) iterator.next();

			final WorldLocation copiedLoc = new WorldLocation(thisFix.getFix()
					.getLocation());
			copiedLoc.addToMe(vector);

			// and replace the location (this method updates all 3 location
			// contained
			// in the fix wrapper
			thisFix.setFixLocation(copiedLoc);
		}
	}

	/**
	 * move the whole of the track be the provided offset
	 */
	public final void shiftTrack(final Enumeration<Editable> theEnum,
			final WorldVector offset)
	{
		Enumeration<Editable> enumA = theEnum;
		if (enumA == null)
			enumA = elements();

		while (enumA.hasMoreElements())
		{
			final Object thisO = enumA.nextElement();
			if (thisO instanceof FixWrapper)
			{
				final FixWrapper fw = (FixWrapper) thisO;

				final WorldLocation copiedLoc = new WorldLocation(fw.getFix()
						.getLocation());
				copiedLoc.addToMe(offset);

				// and replace the location (this method updates all 3 location
				// contained
				// in the fix wrapper
				fw.setFixLocation(copiedLoc);

				// ok - job well done
			}
		}
	}

	protected void sortOutDate(final HiResDate startDTG)
	{
		HiResDate theStartDTG = startDTG;
		if (getData().size() > 0)
		{
			if (theStartDTG == null)
				theStartDTG = startDTG();

			setName(FormatRNDateTime.toString(theStartDTG.getDate().getTime()));
		}
	}

	/**
	 * find the start time of each segment
	 * 
	 * @return
	 */
	public HiResDate startDTG()
	{
		HiResDate res = null;
		final Collection<Editable> items = getData();
		final SortedSet<Editable> sortedItems = (SortedSet<Editable>) items;
		if ((sortedItems != null) && (sortedItems.size() > 0))
		{
			final Editable first = sortedItems.first();
			final FixWrapper fw = (FixWrapper) first;
			res = fw.getDateTimeGroup();
		}
		return res;
	}

	@Override
	public boolean supportsAddRemove()
	{
		return true;
	}

	/**
	 * represent the named leg as a DR vector
	 * 
	 * @param fw
	 *          the leg we're looking at
	 * @param period
	 *          how long it's travelling for (millis)
	 * @return a vector representing the subject
	 */
	public WorldVector vectorFor(final long period, final double speedKts,
			final double courseRads)
	{
		// have we already looked for this
		if (period != _vecTempLastDTG)
		{
			// nope better calc it
			final double timeHrs = period / (1000d * 60d * 60d);
			final double distanceNm = speedKts * timeHrs;
			final WorldDistance dist = new WorldDistance(distanceNm, WorldDistance.NM);
			_vecTempLastVector = new WorldVector(courseRads, dist, null);
		}

		return _vecTempLastVector;
	}

	@Override
	public Layer wrapMe()
	{
		// right, put the segment into a TrackWrapper
		final TrackWrapper newTrack = new TrackWrapper();
		newTrack.setName(this.getName());
		newTrack.setColor(Color.red);
		newTrack.add(this);

		return newTrack;
	}

	public void trimTo(TimePeriod period)
	{
		java.util.SortedSet<Editable> newList = new java.util.TreeSet<Editable>();

		Iterator<Editable> iter = getData().iterator();
		while (iter.hasNext())
		{
			FixWrapper thisE = (FixWrapper) iter.next();
			if (period.contains(thisE.getTime()))
			{
				newList.add(thisE);
			}
		}

		// clear the existing list
		super.removeAllElements();

		// ok, copy over the new list
		iter = newList.iterator();
		while (iter.hasNext())
		{
			Editable editable = (Editable) iter.next();
			super.add(editable);
		}
	}

}