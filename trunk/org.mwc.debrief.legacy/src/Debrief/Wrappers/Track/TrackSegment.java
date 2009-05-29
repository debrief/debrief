package Debrief.Wrappers.Track;

import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;

import Debrief.Tools.Tote.Watchable;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.BaseItemLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

/**
 * a single collection of track points
 * 
 * @author Administrator
 * 
 */
public class TrackSegment extends BaseItemLayer implements DraggableItem
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
				{ expertProp("Visible", "whether this layer is visible", FORMAT), };
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
		sortOutDate();
	}

	@Override
	public void add(final Editable item)
	{
		System.err.println("SHOULD NOT BE ADDING NORMAL ITEM TO TRACK SEGMENT");
	}

	public void addFix(final FixWrapper fix)
	{
		super.add(fix);

		// override the name, just in case this point is earlier
		sortOutDate();

		// tell it about our daddy
		fix.setTrackWrapper(_myTrack);
	}

	public void append(final Layer other)
	{
		// ok, pass through and add the items
		final Enumeration<Editable> enumer = other.elements();
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
			final Layer parentLayer)
	{
	}

	@Override
	public Editable.EditorType getInfo()
	{
		return new TrackSegmentInfo(this);
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
		final FixWrapper first = (FixWrapper) this.first();
		final FixWrapper last = (FixWrapper) this.last();
		final WorldArea area = new WorldArea(first.getFixLocation(), last
				.getFixLocation());
		final WorldLocation centrePt = area.getCentre();
		final double centre = centrePt.rangeFrom(other);
		final double oneEnd = first.rangeFrom(other);
		final double otherEnd = last.rangeFrom(other);
		double res = Math.min(centre, oneEnd);
		res = Math.min(res, otherEnd);
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

		// update our segments
		final Collection<Editable> items = getData();
		for (final Iterator<Editable> iterator = items.iterator(); iterator
				.hasNext();)
		{
			final FixWrapper fix = (FixWrapper) iterator.next();
			fix.setTrackWrapper(_myTrack);
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

	public void sortOutDate()
	{
		if (getData().size() > 0)
			setName(FormatRNDateTime.toString(startDTG().getDate().getTime()));
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

	/** switch the sample rate of this track to the supplied frequency
	 * 
	 * @param theVal
	 */
	public void decimate(HiResDate theVal, TrackWrapper parentTrack)
	{
		Vector<FixWrapper> newItems = new Vector<FixWrapper>();
		boolean oldInterpolateState = parentTrack.getInterpolatePoints();
		
		// switch on interpolation
		parentTrack.setInterpolatePoints(true);
		
		// right - sort out what time period we're working through
		for(long tNow = startDTG().getMicros(); tNow <= endDTG().getMicros(); tNow += theVal.getMicros())
		{
			
			// store the new position
			Watchable[] matches = parentTrack.getNearestTo(new HiResDate(0,tNow));
			if(matches.length > 0)
			{
				FixWrapper newF = (FixWrapper) matches[0];
				
				// do we correct the name?
				if(newF.getName().equals(FixWrapper.INTERPOLATED_FIX))
				{
					// reset the name
					newF.resetName();
				}
				
				newItems.add(newF);
			}
		}
		
		// ditch our positions
		this.removeAllElements();
		
		// store the new positions
		for (Iterator<FixWrapper> iterator = newItems.iterator(); iterator.hasNext();)
		{
			FixWrapper fix = iterator.next();
			this.addFix(fix);
		}
		
		// re-instate the interpolate status
	  parentTrack.setInterpolatePoints(oldInterpolateState);
	}

}