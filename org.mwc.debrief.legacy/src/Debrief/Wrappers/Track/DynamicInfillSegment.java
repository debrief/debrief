package Debrief.Wrappers.Track;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Enumeration;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.BaseItemLayer;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class DynamicInfillSegment extends TrackSegment
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * get the first 'n' elements from this segment
	 * 
	 * @param trackOne
	 *          the segment to get the data from
	 * @param oneUse
	 *          how many elements to use
	 * @return the subset
	 */
	public static FixWrapper[] getFirstElementsFrom(final TrackSegment trackTwo,
			final int num)
	{
		final FixWrapper[] res = new FixWrapper[num];

		HiResDate lastDate = null;
		int ctr = 0;

		final Enumeration<Editable> items = trackTwo.elements();
		for (int i = 0; i < trackTwo.size(); i++)
		{
			final FixWrapper next = (FixWrapper) items.nextElement();
			final HiResDate thisDate = next.getDateTimeGroup();
			if (lastDate != null)
			{
				// ok, is this a new date
				if (thisDate.equals(lastDate))
				{
					// skip this cycle
					continue;
				}
			}
			lastDate = thisDate;

			res[ctr++] = next;

			if (ctr == num)
				break;
		}

		return res;
	}

	/**
	 * get the last 'n' elements from this segment
	 * 
	 * @param trackOne
	 *          the segment to get the data from
	 * @param num
	 *          how many elements to use
	 * @return the subset
	 */
	public static FixWrapper[] getLastElementsFrom(final TrackSegment trackOne,
			final int num)
	{
		final FixWrapper[] res = new FixWrapper[num];

		// right, careful here, we can't use points at the same time
		final Object[] data = trackOne.getData().toArray();
		final int theLen = data.length;
		HiResDate lastDate = null;
		int ctr = 0;
		for (int i = theLen - 1; i >= 0; i--)
		{
			final FixWrapper fix = (FixWrapper) data[i];
			if (lastDate != null)
			{
				// is this the same date?
				final HiResDate thisDate = fix.getDateTimeGroup();
				if (thisDate.equals(lastDate))
				{
					// ok, can't use it - skip to next cycle
					continue;
				}
			}

			lastDate = fix.getDateTimeGroup();

			// ok, we must be ok
			res[res.length - ++ctr] = fix;

			// are we done?
			if (ctr == num)
				break;

		}

		return res;
	}

	/**
	 * the segment that appears immediately before us
	 * 
	 */
	private TrackSegment _before;

	/**
	 * the segment that appears immediately after us
	 * 
	 */
	private TrackSegment _after;

	/**
	 * our internal class that listens to tracks moving
	 * 
	 */
	private transient final PropertyChangeListener _moveListener;

	/**
	 * our internal class that listens to tracks moving
	 * 
	 */
	private transient final PropertyChangeListener _wrapperListener;

	/**
	 * a utility logger
	 * 
	 */
	private transient final ErrorLogger _myParent;

	/**
	 * for XML restore, we only have the name of the previous track. Store it.
	 */
	private String _beforeName;

	/**
	 * for XML restore, we only have the name of the following track. Store it.
	 */
	private String _afterName;

	/**
	 * ensure important objects are defined
	 * 
	 */
	protected DynamicInfillSegment()
	{
		_moveListener = new PropertyChangeListener()
		{
			@Override
			public void propertyChange(final PropertyChangeEvent evt)
			{
				reconstruct();
			}
		};

		_wrapperListener = new PropertyChangeListener()
		{
			@Override
			public void propertyChange(final PropertyChangeEvent evt)
			{
				wrapperChange();
			}
		};

		_myParent = Trace.getParent();

		// we have to be in absolute mode, due to the way we use spline positions
		super.setPlotRelative(false);
	}

	/**
	 * restore from file, where we only know the names of the legs
	 * 
	 * @param beforeName
	 * @param afterName
	 */
	public DynamicInfillSegment(final String beforeName, final String afterName)
	{
		this();
		_beforeName = beforeName;
		_afterName = afterName;
	}

	/**
	 * create an infill track segment between the two supplied tracks
	 * 
	 * @param before
	 * @param after
	 */
	public DynamicInfillSegment(final TrackSegment before,
			final TrackSegment after)
	{
		this();

		// ok, and start listening
		configure(before, after);
	}

	private void clear()
	{
		stopWatching(_before);
		stopWatching(_after);
	}

	/**
	 * listen to the specified tracks
	 * 
	 * @param before
	 * @param after
	 */
	private void configure(final TrackSegment before, final TrackSegment after)
	{
		// ok, remember the tracks
		_before = before;
		_after = after;

		// also, we need to listen out for changes in these tracks
		startWatching(_before);
		startWatching(_after);

		// ok, produce an initial version
		reconstruct();
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();

		// ditch the listeners
		clear();

		_before = null;
		_after = null;
	}

	/**
	 * find the named segment
	 * 
	 * @param name
	 *          name of the segment to find
	 * @return the matching segment
	 */
	private TrackSegment findSegment(final String name)
	{
		TrackSegment res = null;
		final SegmentList segs = super.getWrapper().getSegments();
		final Enumeration<Editable> numer = segs.elements();
		while (numer.hasMoreElements())
		{
			final Editable editable = numer.nextElement();
			if (editable.getName().equals(name))
			{
				res = (TrackSegment) editable;
				break;
			}
		}
		return res;
	}

	/**
	 * accessor, used for file storage
	 * 
	 * @return
	 */
	public String getAfterName()
	{
		return _after.getName();
	}

	/**
	 * accessor, used for file storage
	 * 
	 * @return
	 */
	public String getBeforeName()
	{
		return _before.getName();
	}

	@Override
	public String getName()
	{
		String res = super.getName();

		if (this.size() == 0)
		{
			res += " [Recalculating]";
		}

		return res;
	}

	/**
	 * we're overriding this method, since it's part of the rendering cycle, and
	 * we're confident that rendering will only happen once the data is loaded and
	 * collated.
	 */
	@Override
	public boolean getVisible()
	{
		// ok, do we know our tracks?
		if (_before == null)
		{
			final TrackSegment before = findSegment(_beforeName);
			final TrackSegment after = findSegment(_afterName);

			if ((before != null) && (after != null))
			{
				// ok, now we're ready
				configure(before, after);
			}
			else
			{
				// clear the listeners, just in case
				if (_before != null)
				{
					_before.removePropertyChangeListener(CoreTMASegment.ADJUSTED,
							_moveListener);
				}
				if (_after != null)
				{
					_after.removePropertyChangeListener(CoreTMASegment.ADJUSTED,
							_moveListener);
				}

				// we'd better hide ourselves too
				setVisible(false);
			}
		}
		else
		{
			// just double-check if we still need to be generated
			if (this.size() == 0)
			{
				// ok, tell the track to sort out the relative tracks
				this.getWrapper().sortOutRelativePositions();

				// and regenerate our positions
				reconstruct();
			}
		}

		return super.getVisible();
	}

	/**
	 * recalculate our set of positions
	 * 
	 */
	protected void reconstruct()
	{
		// ok, clear ourselves out
		this.removeAllElements();

		// now the num to use
		final int oneUse = 2;
		final int twoUse = 3;

		// generate the data for the splines
		final FixWrapper[] oneElements = getLastElementsFrom(_before, oneUse);
		final FixWrapper[] twoElements = getFirstElementsFrom(_after, twoUse);
		final FixWrapper[] allElements = new FixWrapper[oneUse + twoUse];
		System.arraycopy(oneElements, 0, allElements, 0, oneUse);
		System.arraycopy(twoElements, 0, allElements, oneUse, twoUse);

		// generate the location spline
		final double[] times = new double[allElements.length];
		final double[] lats = new double[allElements.length];
		final double[] longs = new double[allElements.length];
		final double[] depths = new double[allElements.length];
		for (int i = 0; i < allElements.length; i++)
		{
			final FixWrapper fw = allElements[i];
			times[i] = fw.getDTG().getDate().getTime();

			// we have an occasional problem where changing details of hte
			// before/after tracks results in us updating mid-way through their
			// update.
			// (since the RelativeTMA segment positions actually get generated in the
			// paint cycle).
			if (fw.getLocation() == null)
			{
				return;
			}
			lats[i] = fw.getLocation().getLat();
			longs[i] = fw.getLocation().getLong();
			depths[i] = fw.getLocation().getDepth();
		}

		final UnivariateInterpolator interpolator = new SplineInterpolator();
		final UnivariateFunction latInterp = interpolator.interpolate(times, lats);
		final UnivariateFunction longInterp = interpolator
				.interpolate(times, longs);
		final UnivariateFunction depthInterp = interpolator.interpolate(times,
				depths);

		// what's the interval?
		long tDelta = oneElements[1].getDateTimeGroup().getDate().getTime()
				- oneElements[0].getDateTimeGroup().getDate().getTime();

		// just check it's achievable
		if (tDelta == 0)
			throw new RuntimeException(
					"cannot generate infill when calculated step time is zero");

		// also give the time delta a minimum step size (10 secs), we were getting
		// really
		// screwy infills generated with tiny time deltas
		tDelta = Math.max(tDelta, 10000);

		// sort out the start & end times of the infill segment
		final long tStart = _before.endDTG().getDate().getTime() + tDelta;
		final long tEnd = _after.startDTG().getDate().getTime();

		// remember the last point on the first track, in case we're generating
		// a
		// relative solution
		FixWrapper origin = oneElements[oneElements.length - 1];

		// remember how the previous track is styled
		final String labelFormat = origin.getLabelFormat();
		final boolean labelOn = origin.getLabelShowing();
		final Integer labelLoc = origin.getLabelLocation();

		if (_myParent != null)
		{
			if (origin.getLocation() == null)
				_myParent.logError(ToolParent.INFO,
						"origin element has empty location", null);
		}

		// get going then! Note, we go past the end of the required data,
		// - so that we can generate the correct course and speed for the last
		// DR entry
		for (long tNow = tStart; tNow <= tEnd; tNow += tDelta)
		{
			final double thisLat = latInterp.value(tNow);
			final double thisLong = longInterp.value(tNow);
			final double thisDepth = depthInterp.value(tNow);

			// create the new location
			final WorldLocation newLocation = new WorldLocation(thisLat, thisLong,
					thisDepth);

			// how far have we travelled since the last location?
			final WorldVector offset = newLocation.subtract(origin.getLocation());

			// how long since the last position?
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

			// convert the speed
			final WorldSpeed theSpeed = new WorldSpeed(thisSpeedKts, WorldSpeed.Kts);

			// create the fix
			final Fix newFix = new Fix(new HiResDate(tNow), newLocation,
					thisCourseRads, theSpeed.getValueIn(WorldSpeed.ft_sec) / 3);

			final FixWrapper fw = new FixWrapper(newFix);
			fw.setSymbolShowing(true);

			// and sort out the time lable
			fw.resetName();

			// and copy the other formatting
			fw.setLabelFormat(labelFormat);
			fw.setLabelLocation(labelLoc);
			fw.setLabelShowing(labelOn);

			// only add it if we're still in the time period. We generate one
			// position
			// past the end of the time period in order to set the correct DR
			// course
			// for the last position.
			if (tNow < tEnd)
			{
				this.addFix(fw);
			}

			// move along the bus, please (used if we're doing a DR Track).
			origin = fw;
		}

		// sort out our name
		final String name = "infill_"
				+ FormatRNDateTime.toShortString(new Date().getTime());
		this.setName(name);

		// also make it dotted, since it's artificially generated
		this.setLineStyle(CanvasType.DOTTED);
	}

	private void startWatching(final TrackSegment segment)
	{
		segment.addPropertyChangeListener(CoreTMASegment.ADJUSTED, _moveListener);
		segment.addPropertyChangeListener(BaseItemLayer.WRAPPER_CHANGED,
				_wrapperListener);
	}

	private void stopWatching(final TrackSegment segment)
	{
		if (segment != null)
		{
			segment.removePropertyChangeListener(CoreTMASegment.ADJUSTED,
					_moveListener);
			segment.removePropertyChangeListener(BaseItemLayer.WRAPPER_CHANGED,
					_wrapperListener);
		}
	}

	/**
	 * one of our tracks has had it's wrapper change. handle this
	 * 
	 */
	private final void wrapperChange()
	{

		boolean codeRed = false;

		// check the before leg
		if ((_before != null) && (_before.getWrapper() == null))
		{
			codeRed = true;
		}

		// check the after leg
		if ((_after != null) && (_after.getWrapper() == null))
		{
			codeRed = true;
		}

		// are we in trouble
		if (codeRed)
		{
			// ok, burn everything!
			clear();

			// and remove ourselves from our parent
			getWrapper().removeElement(this);
		}
	}

}
