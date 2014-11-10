package Debrief.Wrappers.Track;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Enumeration;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import Debrief.Wrappers.FixWrapper;
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
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class DynamicInfillSegment extends TrackSegment
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TrackSegment _before;
	private TrackSegment _after;
	private final PropertyChangeListener _moveListener;
	private ErrorLogger _myParent;
	private String _beforeName;
	private String _afterName;

	public DynamicInfillSegment()
	{
		_moveListener = new PropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				reconstruct();
			}
		};
	}

	/**
	 * create an infill track segment between the two supplied tracks
	 * 
	 * @param trackOne
	 * @param trackTwo
	 */
	public DynamicInfillSegment(final TrackSegment before,
			final TrackSegment after)
	{
		this();

		// ok, and start listening
		configure(before, after);
	}

	private void configure(final TrackSegment before, final TrackSegment after)
	{
		// ok, remember the tracks
		_before = before;
		_after = after;

		// also, we need to listen out for changes in these tracks
		_before.addPropertyChangeListener(CoreTMASegment.ADJUSTED, _moveListener);
		_after.addPropertyChangeListener(CoreTMASegment.ADJUSTED, _moveListener);

		// ok, produce an initial version
		reconstruct();
	}

	/**
	 * restore from file, where we only know the names of the legs
	 * 
	 * @param beforeName
	 * @param afterName
	 */
	public DynamicInfillSegment(String beforeName, String afterName)
	{
		this();
		_beforeName = beforeName;
		_afterName = afterName;
	}

	/**
	 * we're overriding this method, since it's part of the rendering cycle, and
	 * we're confident that rendering will only happen once the data is loaded and
	 * collated.
	 */
	public boolean getVisible()
	{
		// ok, do we know our tracks?
		if (_before == null)
		{
			TrackSegment before = findSegment(_beforeName);
			TrackSegment after = findSegment(_afterName);

			// ok, now we're ready
			configure(before, after);
		}

		return super.getVisible();
	}

	/**
	 * find the named segment
	 * 
	 * @param name
	 *          name of the segment to find
	 * @return the matching segment
	 */
	private TrackSegment findSegment(String name)
	{
		TrackSegment res = null;
		SegmentList segs = super.getWrapper().getSegments();
		Enumeration<Editable> numer = segs.elements();
		while (numer.hasMoreElements())
		{
			Editable editable = (Editable) numer.nextElement();
			if (editable.getName().equals(name))
			{
				res = (TrackSegment) editable;
				break;
			}
		}
		return res;
	}

	protected void reconstruct()
	{
		// ok, clear ourselves out
		this.removeAllElements();

		// remember if it's DR or OTG
		final boolean isDR = false; // false;// _before.getPlotRelative();

		this.setPlotRelative(isDR);

		// now the num to use
		final int oneUse = 2;
		final int twoUse = 3;

		// generate the data for the splines
		final FixWrapper[] oneElements = getLastElementsFrom(_before, oneUse);
		final FixWrapper[] twoElements = getFirstElementsFrom(_after, twoUse);
		final FixWrapper[] allElements = new FixWrapper[oneUse + twoUse];
		System.arraycopy(oneElements, 0, allElements, 0, oneUse);
		System.arraycopy(twoElements, 0, allElements, oneUse, twoUse);

		if (_myParent != null)
		{
			_myParent.logError(ToolParent.INFO, "extracted " + oneElements.length
					+ " fixes from first segment", null);
			_myParent.logError(ToolParent.INFO, "extracted " + twoElements.length
					+ " fixes from second segment", null);

			// extra diagnostics
			StringBuffer buff = new StringBuffer();
			buff.append("\n");
			for (int i = 0; i < allElements.length; i++)
			{
				final FixWrapper fixWrapper = allElements[i];
				buff.append("item: "
						+ i
						+ " ,lat:,"
						+ fixWrapper.getLocation().getLat()
						+ " ,lon:,"
						+ fixWrapper.getLocation().getLong()
						+ " ,course:,"
						+ fixWrapper.getCourseDegs()
						+ " ,speed:,"
						+ fixWrapper.getFix().getSpeed()
						+ " ,at:,"
						+ DebriefFormatDateTime.toString(fixWrapper.getTime().getDate()
								.getTime()) + "\n");
			}
			_myParent.logError(ToolParent.INFO, buff.toString(), null);

		}

		// generate the location spline
		final double[] times = new double[allElements.length];
		final double[] lats = new double[allElements.length];
		final double[] longs = new double[allElements.length];
		final double[] depths = new double[allElements.length];
		for (int i = 0; i < allElements.length; i++)
		{
			final FixWrapper fw = allElements[i];
			times[i] = fw.getDTG().getDate().getTime();
			lats[i] = fw.getLocation().getLat();
			longs[i] = fw.getLocation().getLong();
			depths[i] = fw.getLocation().getDepth();
		}

		UnivariateInterpolator interpolator = new SplineInterpolator();
		UnivariateFunction latInterp = interpolator.interpolate(times, lats);
		UnivariateFunction longInterp = interpolator.interpolate(times, longs);
		UnivariateFunction depthInterp = interpolator.interpolate(times, depths);

		// final CubicSpline latSpline = new CubicSpline(times, lats);
		// final CubicSpline longSpline = new CubicSpline(times, longs);
		// final CubicSpline depthSpline = new CubicSpline(times, depths);

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

		if (_myParent != null)
		{
			_myParent.logError(ToolParent.INFO, " using time delta of " + tDelta
					/ 1000 + " secs based on times of first two items in second segment",
					null);
		}

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
			else
				_myParent.logError(ToolParent.INFO,
						"origin element has valid location", null);
		}

		boolean first = true;

		System.out.println("===");

		// get going then! Note, we go past the end of the required data,
		// - so that we can generate the correct course and speed for the last
		// DR
		// entry
		StringBuffer buff = new StringBuffer();
		for (long tNow = tStart; tNow <= tEnd; tNow += tDelta)
		{
			final double thisLat = latInterp.value(tNow);
			final double thisLong = longInterp.value(tNow);
			final double thisDepth = depthInterp.value(tNow);

			// create the new location
			final WorldLocation newLocation = new WorldLocation(thisLat, thisLong,
					thisDepth);

			final WorldVector offset = newLocation.subtract(origin.getLocation());
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

			// is this a relative track? if it is, we need
			// to push the speed/course back into the previous leg
			if (this.getPlotRelative())
			{
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
			}

			// put course in the +ve domain
			while (thisCourseRads < 0)
				thisCourseRads += Math.PI * 2;

			// convert the speed
			final WorldSpeed theSpeed = new WorldSpeed(thisSpeedKts, WorldSpeed.Kts);

			// create the fix
			final Fix newFix = new Fix(new HiResDate(tNow), newLocation,
					thisCourseRads, theSpeed.getValueIn(WorldSpeed.ft_sec) / 3);

			System.out.println("T:" + new HiResDate(tNow) + " Speed:"
					+ theSpeed.getValueIn(WorldSpeed.Kts));

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
		if (_myParent != null)
			_myParent.logError(ToolParent.INFO, buff.toString(), null);

		// aaah, special case. If we are generating a DR track, we need to put
		// the
		// next course and speed
		// into the last entry - in order to get a smooth graph.

		// sort out our name
		final String name = "infill_"
				+ FormatRNDateTime.toShortString(new Date().getTime());
		this.setName(name);

		// also make it dotted, since it's artificially generated
		this.setLineStyle(CanvasType.DOTTED);
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();

		_before.removePropertyChangeListener(_moveListener);
		_after.removePropertyChangeListener(_moveListener);
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

	public String getBeforeName()
	{
		return _before.getName();
	}

	public String getAfterName()
	{
		return _after.getName();
	}

}
