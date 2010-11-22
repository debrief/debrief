package Debrief.Wrappers.Track;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.BaseItemLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.ToolParent;
import MWC.GUI.Layers.NeedsWrappingInLayerManager;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import flanagan.interpolation.CubicSpline;

/**
 * a single collection of track points
 * 
 * @author Administrator
 * 
 */
public class TrackSegment extends BaseItemLayer implements DraggableItem,
		GriddableSeriesMarker, NeedsWrappingInLayerManager
{

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
		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ expertProp("Visible", "whether this layer is visible", FORMAT),
						expertProp("LineStyle", "how to plot this line", FORMAT),
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
	private static ToolParent _myParent;

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

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

	private WorldVector _vecTempLastVector = null;

	protected long _vecTempLastDTG = -2;

	/**
	 * how to plot this line
	 * 
	 */
	private int _lineStyle = CanvasType.SOLID;

	/**
	 * how this line is plotted
	 * 
	 * @return
	 */
	public int getLineStyle()
	{
		return _lineStyle;
	}

	/**
	 * specify how this line is to be plotted
	 * 
	 * @param lineStyle
	 */
	public void setLineStyle(int lineStyle)
	{
		_lineStyle = lineStyle;
	}

	public TrackSegment()
	{
		// no-op constructor
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

	/**
	 * create an infill track segment between the two supplied tracks
	 * 
	 * @param trackOne
	 * @param trackTwo
	 */
	public TrackSegment(TrackSegment trackOne, TrackSegment trackTwo)
	{
		// remember if it's DR or OTG
		boolean isDR = trackOne.getPlotRelative();

		this.setPlotRelative(isDR);

		// now the num to use
		int oneUse = 2;
		int twoUse = 2;

		// generate the data for the splines
		FixWrapper[] oneElements = getLastElementsFrom(trackOne, oneUse);
		FixWrapper[] twoElements = getFirstElementsFrom(trackTwo, twoUse);
		FixWrapper[] allElements = new FixWrapper[oneUse + twoUse];
		System.arraycopy(oneElements, 0, allElements, 0, oneUse);
		System.arraycopy(twoElements, 0, allElements, oneUse, twoUse);

		if (_myParent != null)
		{
			_myParent.logError(ToolParent.INFO, "extracted " + oneElements.length
					+ " fixes from first segment", null);
			_myParent.logError(ToolParent.INFO, "extracted " + twoElements.length
					+ " fixes from second segment", null);
		}

		// generate the location spline
		double[] times = new double[allElements.length];
		double[] lats = new double[allElements.length];
		double[] longs = new double[allElements.length];
		double[] depths = new double[allElements.length];
		for (int i = 0; i < allElements.length; i++)
		{
			FixWrapper fw = allElements[i];
			times[i] = fw.getDTG().getDate().getTime();
			lats[i] = fw.getLocation().getLat();
			longs[i] = fw.getLocation().getLong();
			depths[i] = fw.getLocation().getDepth();
		}

		CubicSpline latSpline = new CubicSpline(times, lats);
		CubicSpline longSpline = new CubicSpline(times, longs);
		CubicSpline depthSpline = new CubicSpline(times, depths);

		// what's the interval?
		long tDelta = getTimeDelta(trackTwo);
		long tStart = trackOne.endDTG().getDate().getTime() + tDelta;
		long tEnd = trackTwo.startDTG().getDate().getTime();

		if (_myParent != null)
		{
			_myParent
					.logError(ToolParent.INFO, " using time delta of " + tDelta
							+ " millis based on times of first two items in second segment",
							null);
		}

		// remember the last point on the first track, in case we're generating a
		// relative solution
		FixWrapper origin = oneElements[oneElements.length - 1];
		boolean first = true;

		// get going then! Note, we go past the end of the required data,
		// - so that we can generate the correct course and speed for the last DR
		// entry
		for (long tNow = tStart; tNow < tEnd + tDelta; tNow += tDelta)
		{
			final double thisLat = latSpline.interpolate(tNow);
			final double thisLong = longSpline.interpolate(tNow);
			final double thisDepth = depthSpline.interpolate(tNow);

			// create the new location
			WorldLocation newLocation = new WorldLocation(thisLat, thisLong,
					thisDepth);

			if (_myParent != null)
			{
				if (newLocation == null)

					_myParent.logError(ToolParent.ERROR,
							" created null location at time " + tNow + "(" + new Date(tNow)
									+ ")", null);
			}

			WorldVector offset = newLocation.subtract(origin.getLocation());
			final double timeSecs = (tNow - origin.getTime().getDate().getTime()) / 1000;
			// start off with the course
			double thisCourseRads = offset.getBearing();

			// and now the speed
			final double distYds = new WorldDistance(offset.getRange(),
					WorldDistance.DEGS).getValueIn(WorldDistance.YARDS);
			final double spdYps = distYds / timeSecs;
			final double thisSpeedKts = MWC.Algorithms.Conversions.Yps2Kts(spdYps);

			// put course in the +ve domain
			while (thisCourseRads < 0)
				thisCourseRads += Math.PI * 2;

			if (first)
			{
				// we don't edit the origin, it's from another track
				first = false;
			}
			else
			{
				// over-write the course and speed of the previous entry
				origin.setSpeed(thisSpeedKts);
				origin.setCourse(thisCourseRads);
			}

			// put course in the +ve domain
			while (thisCourseRads < 0)
				thisCourseRads += Math.PI * 2;

			// convert the speed
			WorldSpeed theSpeed = new WorldSpeed(thisSpeedKts, WorldSpeed.Kts);

			// create the fix
			Fix newFix = new Fix(new HiResDate(tNow), newLocation, thisCourseRads,
					theSpeed.getValueIn(WorldSpeed.ft_sec) / 3);

			FixWrapper fw = new FixWrapper(newFix);
			fw.setSymbolShowing(true);

			// only add it if we're still in the time period. We generate one position
			// past the end of the time period in order to set the correct DR course
			// for the last position.
			if (tNow < tEnd)
			{
				this.addFix(fw);
			}

			// move along the bus, please (used if we're doing a DR Track).
			origin = fw;
		}

		// aaah, special case. If we are generating a DR track, we need to put the
		// next course and speed
		// into the last entry - in order to get a smooth graph.

		// sort out our name
		String name = "infill_"
				+ FormatRNDateTime.toShortString(new Date().getTime());
		this.setName(name);

		// also make it dotted, since it's artificially generated
		this.setLineStyle(CanvasType.DOTTED);
	}

	/**
	 * constructor that builds a plain track segment from a tma segment - an
	 * operation we must do when we try to merge track segments
	 * 
	 * @param tma
	 */
	public TrackSegment(CoreTMASegment tma)
	{
		setName(tma.getName());
		setVisible(tma.getVisible());
		setWrapper(tma.getWrapper());

		// add the elements from the target
		Enumeration<Editable> points = tma.elements();
		while (points.hasMoreElements())
		{
			Editable next = points.nextElement();
			add(next);
		}
	}

	/**
	 * get the first 'n' elements from this segment
	 * 
	 * @param trackOne
	 *          the segment to get the data from
	 * @param oneUse
	 *          how many elements to use
	 * @return the subset
	 */
	private FixWrapper[] getFirstElementsFrom(TrackSegment trackTwo, int twoUse)
	{
		FixWrapper[] res = new FixWrapper[twoUse];

		Enumeration<Editable> items = trackTwo.elements();
		for (int i = 0; i < twoUse; i++)
		{
			res[i] = (FixWrapper) items.nextElement();
		}

		return res;
	}

	/**
	 * get the last 'n' elements from this segment
	 * 
	 * @param trackOne
	 *          the segment to get the data from
	 * @param oneUse
	 *          how many elements to use
	 * @return the subset
	 */
	private FixWrapper[] getLastElementsFrom(TrackSegment trackOne, int oneUse)
	{
		FixWrapper[] res = new FixWrapper[oneUse];
		int theLen = trackOne.size();
		int firstIndex = theLen - oneUse;

		Enumeration<Editable> items = trackOne.elements();
		for (int i = 0; i < trackOne.size(); i++)
		{
			FixWrapper thisItem = (FixWrapper) items.nextElement();
			if (i >= firstIndex)
				res[i - firstIndex] = thisItem;
		}

		return res;
	}

	/**
	 * find the time between the first two entries in this track
	 * 
	 * @param segment
	 *          the segment we're looking at
	 * @return
	 */
	private static final long getTimeDelta(TrackSegment segment)
	{
		Enumeration<Editable> someIt = segment.elements();
		FixWrapper first = (FixWrapper) someIt.nextElement();
		FixWrapper second = (FixWrapper) someIt.nextElement();

		long timeDelta = (second.getDateTimeGroup().getDate().getTime() - first
				.getDateTimeGroup().getDate().getTime());
		return timeDelta;
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
			fix.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED, _myTrack
					.getLocationListener());
		}
	}

	/**
	 * add the elements in the indicated layer to us.
	 * 
	 */
	public void append(final Layer other)
	{
		// get the other track's elements
		final Enumeration<Editable> enumer = other.elements();

		// have a look and see if we're a DR track
		if (this.getPlotRelative())
		{
			// right, we've got to make sure our last point is correctly pointing to
			// the first point of this new track
			// - sort it out
			FixWrapper first = (FixWrapper) enumer.nextElement();
			FixWrapper myLast = (FixWrapper) this.last();
			WorldVector offset = first.getLocation().subtract(myLast.getLocation());

			double courseRads = offset.getBearing();
			double timeSecs = (first.getTime().getDate().getTime() - myLast.getTime()
					.getDate().getTime()) / 1000;
			// start off with the course

			// and now the speed
			double distYds = new WorldDistance(offset.getRange(), WorldDistance.DEGS)
					.getValueIn(WorldDistance.YARDS);
			double spdYps = distYds / timeSecs;
			double thisSpeedKts = MWC.Algorithms.Conversions.Yps2Kts(spdYps);

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
			res = startDTG().compareTo(other.startDTG());
		}
		else
		{
			// just use string comparison
			res = getName().compareTo(arg0.getName());
		}
		return res;
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
		final Collection<Editable> items = getData();
		final SortedSet<Editable> sortedItems = (SortedSet<Editable>) items;
		final Editable last = sortedItems.last();
		final FixWrapper fw = (FixWrapper) last;
		return fw.getDateTimeGroup();
	}

	public void findNearestHotSpotIn(final Point cursorPos,
			final WorldLocation cursorLoc, final LocationConstruct currentNearest,
			final Layer parentLayer, Layers theData)
	{
	}

	@Override
	public Editable.EditorType getInfo()
	{
		return new TrackSegmentInfo(this);
	}

	/**
	 * learn about the shared trouble reporter...
	 * 
	 * @param toolParent
	 */
	public static void initialise(ToolParent toolParent)
	{
		_myParent = toolParent;
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

	public boolean getPlotRelative()
	{
		return _plotRelative;
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

		if (getBounds() != null)
			res = getBounds().rangeFrom(other);

		return res;
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
		System.err.println("doing rotate about:" + brg);
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
	public final void shiftTrack(Enumeration<Editable> theEnum,
			final WorldVector offset)
	{
		if (theEnum == null)
			theEnum = elements();

		while (theEnum.hasMoreElements())
		{
			final Object thisO = theEnum.nextElement();
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

	public void sortOutDate(HiResDate startDTG)
	{
		if (getData().size() > 0)
		{
			if (startDTG == null)
				startDTG = startDTG();

			setName(FormatRNDateTime.toString(startDTG.getDate().getTime()));
		}
	}

	/**
	 * find the start time of each segment
	 * 
	 * @return
	 */
	public HiResDate startDTG()
	{
		final Collection<Editable> items = getData();
		final SortedSet<Editable> sortedItems = (SortedSet<Editable>) items;
		final Editable first = sortedItems.first();
		final FixWrapper fw = (FixWrapper) first;
		return fw.getDateTimeGroup();
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

	/**
	 * switch the sample rate of this track to the supplied frequency
	 * 
	 * @param theVal
	 */
	public void decimate(HiResDate theVal, TrackWrapper parentTrack,
			long startTime)
	{
		Vector<FixWrapper> newItems = new Vector<FixWrapper>();
		boolean oldInterpolateState = parentTrack.getInterpolatePoints();

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
		for (Iterator<FixWrapper> iterator = newItems.iterator(); iterator
				.hasNext();)
		{
			FixWrapper fix = iterator.next();
			this.addFix(fix);
		}

		// re-instate the interpolate status
		parentTrack.setInterpolatePoints(oldInterpolateState);
	}

	private void decimateRelative(HiResDate theVal, TrackWrapper parentTrack,
			long startTime, Vector<FixWrapper> newItems)
	{
		long tNow = 0;

		// get the time interval
		final long interval = theVal.getMicros();

		// round myStart time to the supplied interval
		long myStart = this.startDTG().getMicros();
		myStart = (myStart / interval) * interval;

		// set the start time to be the later of our start time and the provided
		// time
		startTime = Math.max(startTime, myStart);

		if (this instanceof CoreTMASegment)
		{
			CoreTMASegment tma = (CoreTMASegment) this;

			// hey, it's a TMA segment - on steady course/speed. cool
			double courseRads = MWC.Algorithms.Conversions.Degs2Rads(tma.getCourse());
			double speedYps = tma.getSpeed().getValueIn(WorldSpeed.ft_sec) / 3;

			// find the new start location - after we've slipped
			WorldLocation myStartLocation = new WorldLocation(tma.getTrackStart());

			// right - sort out what time period we're working through
			for (tNow = startTime; tNow <= endDTG().getMicros(); tNow += theVal
					.getMicros())
			{
				Fix theFix = new Fix(new HiResDate(0, tNow), new WorldLocation(
						myStartLocation), courseRads, speedYps);
				FixWrapper newFix = new FixWrapper(theFix);
				newFix.setSymbolShowing(true);
				newItems.add(newFix);
			}

			// right, if it's a relative segment, then we need to shift the offset to
			// reflect the new relationship
			if (tma instanceof RelativeTMASegment)
			{
				FixWrapper myStarter = (FixWrapper) tma.first();
				FixWrapper myEnder = (FixWrapper) tma.last();
				HiResDate startDTG = new HiResDate(0, startTime);
				FixWrapper newStarter = FixWrapper.interpolateFix(myStarter, myEnder,
						startDTG);
				WorldLocation newStartLoc = newStarter.getLocation();

				RelativeTMASegment rel = (RelativeTMASegment) tma;
				Watchable[] newHost = rel.getReferenceTrack().getNearestTo(startDTG);
				if (newHost.length > 0)
				{
					WorldLocation newOrigin = newHost[0].getLocation();
					WorldVector newOffset = newStartLoc.subtract(newOrigin);
					rel.setOffset(newOffset);
				}

				// lastly, reset the track name
				rel.sortOutDate(startDTG);

				// and change the track name
				rel._myTrack.setName(rel.getName());
			}

		}
		else
		{
			FixWrapper lastPositionStored = null;
			FixWrapper currentPosition = null;
			tNow = 0;

			// right - sort out what time period we're working through
			for (tNow = startTime; tNow <= endDTG().getMicros(); tNow += theVal
					.getMicros())
			{

				// find hte new datum
				Watchable[] matches = parentTrack.getNearestTo(new HiResDate(0, tNow));
				if (matches.length > 0)
				{
					// remember the last position - we;re going to be calculating future
					// courses and speeds from it
					lastPositionStored = currentPosition;

					currentPosition = (FixWrapper) matches[0];

					// is this our first point?
					if (lastPositionStored != null)
					{
						// start off with the course
						WorldVector offset = currentPosition.getLocation().subtract(
								lastPositionStored.getLocation());
						lastPositionStored.getFix().setCourse(offset.getBearing());

						// and now the speed
						double distYds = new WorldDistance(offset.getRange(),
								WorldDistance.DEGS).getValueIn(WorldDistance.YARDS);
						double timeSecs = (tNow - lastPositionStored.getTime().getMicros()) / 1000000d;
						double spdYps = distYds / timeSecs;
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

	private void decimateAbsolute(HiResDate theVal, TrackWrapper parentTrack,
			long startTime, Vector<FixWrapper> newItems)
	{
		// right - sort out what time period we're working through
		for (long tNow = startTime; tNow <= endDTG().getMicros(); tNow += theVal
				.getMicros())
		{
			// store the new position
			Watchable[] matches = parentTrack.getNearestTo(new HiResDate(0, tNow));
			if (matches.length > 0)
			{
				FixWrapper newF = (FixWrapper) matches[0];

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

	@Override
	public Editable getSampleGriddable()
	{
		HiResDate theTime = new HiResDate(10000000);
		WorldLocation theLocation = new WorldLocation(1, 1, 1);
		double courseRads = 3;
		double speedKts = 5;
		Fix newFix = new Fix(theTime, theLocation, courseRads, speedKts);
		FixWrapper res = new FixWrapper(newFix);
		return res;
	}

	@Override
	public TimeStampedDataItem makeCopy(TimeStampedDataItem item)
	{
		if (false == item instanceof FixWrapper)
		{
			throw new IllegalArgumentException(
					"I am expecting a position, don't know how to copy " + item);
		}
		FixWrapper template = (FixWrapper) item;
		FixWrapper result = new FixWrapper(template.getFix().makeCopy());
		result.setLabelShowing(template.getLabelShowing());
		result.setLineShowing(template.getLineShowing());
		result.setSymbolShowing(template.getSymbolShowing());
		result.setLabelLocation(template.getLabelLocation());

		Color col = template.getActualColor();
		if (col != null)
			result.setColor(col);

		return result;
	}

	@Override
	public Layer wrapMe()
	{
		// right, put the segment into a TrackWrapper
		TrackWrapper newTrack = new TrackWrapper();
		newTrack.setName(this.getName());
		newTrack.setColor(Color.red);
		newTrack.add(this);

		return newTrack;
	}

}