package Debrief.Wrappers.Track;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/** the core elements of a TMA Segment - relative plotting, plus the fact
 * that the segment is actually defined by a course and speed, not a collection
 * of data points
 * @author ianmayo
 *
 */
abstract public class CoreTMASegment extends TrackSegment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * steady course (Degs)
	 * 
	 */
	protected double _courseDegs;
	/**
	 * steady speed
	 * 
	 */
	protected WorldSpeed _speed;
	/** the base frequency (f0) for this tma segment
	 * 
	 */
	private double _baseFreq = 0;

	/**
	 * get the current course of this leg
	 * 
	 * @return course (degs)
	 */
	public double getCourse() {
		return _courseDegs;
	}

	public double getBaseFrequency() {
		return _baseFreq;
	}

	public void setBaseFrequency(double baseFrequency) {
		_baseFreq = baseFrequency;
	}

	/**
	 * the constant speed of this segment
	 * 
	 * @return the current speed
	 */
	public WorldSpeed getSpeed() {
		return _speed;
	}

	/**
	 * message that we plot 1/2 way along segment when it's being stretched or
	 * rotated
	 * 
	 */
	protected String _dragMsg;

	
	/**
	 * base constructor - sorts out the obvious
	 * 
	 * @param courseDegs
	 * @param speed
	 * @param offset
	 * @param theLayers
	 */
	public CoreTMASegment(final double courseDegs, final WorldSpeed speed)
	{
		_courseDegs = courseDegs;
		_speed = speed;
		
		// tell the parent that we're a relative track
		setPlotRelative(true);
	}
	
	/**
	 * the current course (degs)
	 * 
	 * @param course
	 *          (degs)
	 */
	public void setCourse(double course) {
		_courseDegs = course;
	
		double crseRads = MWC.Algorithms.Conversions.Degs2Rads(course);
		Collection<Editable> data = getData();
		for (Iterator<Editable> iterator = data.iterator(); iterator.hasNext();)
		{
			FixWrapper fix = (FixWrapper) iterator.next();
			fix.getFix().setCourse(crseRads);
		}
	
		// ditch our temp vector, we've got to recalc it
		_vecTempLastDTG = -2;
	}

	/**
	 * set the constant speed of this segment
	 * 
	 * @param speed
	 *          the new speed
	 */
	public void setSpeed(WorldSpeed speed) {
		_speed = speed;
	
		double spdYps = speed.getValueIn(WorldSpeed.ft_sec) / 3;
		Collection<Editable> data = getData();
		for (Iterator<Editable> iterator = data.iterator(); iterator.hasNext();)
		{
			FixWrapper fix = (FixWrapper) iterator.next();
			fix.getFix().setSpeed(spdYps);
		}
	
		// ditch our temp vector, we've got to recalc it
		_vecTempLastDTG = -2;
	
	}


	@Override
	abstract public WorldLocation getTrackStart();
	
	@Override
	abstract public void rotate(double brg, final WorldLocation origin);
	
	@Override
	abstract public void shift(WorldVector vector);

	/**
	 * shear this whole track to the supplied destination
	 * 
	 * @param cursor
	 *          where the user's hovering
	 * @param origin
	 *          origin of stretch, probably one end of the track
	 */
	abstract public void shear(WorldLocation cursor, final WorldLocation origin);
	
	
	/**
	 * stretch this whole track to the supplied distance
	 * 
	 * @param rngDegs
	 *          distance to stretch through (degs)
	 * @param origin
	 *          origin of stretch, probably one end of the track
	 */
	abstract public void stretch(double rngDegs, final WorldLocation origin);
	


	@Override
	public void paint(CanvasType dest) {
		Collection<Editable> items = getData();
	
		// ok - draw that line!
		Point lastPoint = null;
		WorldLocation tmaLastLoc = null;
		long tmaLastDTG = 0;
		
		// try to create a dotted line
		dest.setLineStyle(CanvasType.DOTTED);
	
		// remember the ends, so we can plot a point 1/2 way along them
		WorldLocation firstEnd = null;
	//	WorldLocation lastEnd = null;
	
		for (Iterator<Editable> iterator = items.iterator(); iterator.hasNext();)
		{
			FixWrapper thisF = (FixWrapper) iterator.next();
	
			long thisTime = thisF.getDateTimeGroup().getDate().getTime();
	
			// ok, is this our first location?
			if (tmaLastLoc == null)
			{
				tmaLastLoc = new WorldLocation(getTrackStart());
				firstEnd = new WorldLocation(tmaLastLoc);
			}
			else
			{
				// calculate a new vector
				long timeDelta = thisTime - tmaLastDTG;
				WorldVector thisVec = vectorFor(timeDelta, thisF.getSpeed(), thisF
						.getCourse());
				tmaLastLoc.addToMe(thisVec);
	
		//		lastEnd = new WorldLocation(tmaLastLoc);
			}
	
			// dump the location into the fix
			thisF.setFixLocationSilent(new WorldLocation(tmaLastLoc));
	
			// cool, remember the time.
			tmaLastDTG = thisTime;
	
			Point thisPoint = dest.toScreen(thisF.getFixLocation());
	
			// do we have enough for a line?
			if (lastPoint != null)
			{
				// draw that line
				dest.drawLine(lastPoint.x, lastPoint.y, thisPoint.x, thisPoint.y);
			}
	
			lastPoint = new Point(thisPoint);
	
			// also draw in a marker for this point
			dest.drawRect(lastPoint.x - 1, lastPoint.y - 1, 3, 3);
		}
	
		// ok, plot the 1/2 way message
		if (_dragMsg != null)
		{
			Point pt = dest.toScreen(firstEnd);
			
			// project this point out past the actual start point
			pt.translate((int)(30d * Math.cos(MWC.Algorithms.Conversions.Degs2Rads(getCourse()))),
					(int)(30d * Math.sin(MWC.Algorithms.Conversions.Degs2Rads(getCourse()))));
			
			// try to make it bold
			Font newFont = new Font("Arial", Font.BOLD, 12);
			
			// put the text in a solid backdrop
			int ht = 	dest.getStringHeight(newFont) + 2;
			int wid = dest.getStringWidth(newFont, _dragMsg);
			dest.setColor(Color.BLACK);
			dest.fillRect(pt.x-2 , pt.y + 18 - ht, wid, ht);
			
			// and draw the text
			dest.setColor(java.awt.Color.red);
			dest.drawText(_dragMsg, pt.x, pt.y + 15);			
		}
	}

	/** create a nice shiny fix at the indicated time
	 * 
	 * @param theTime
	 * @return the new fix, with valid course and speed
	 */
	protected FixWrapper createFixAt(long theTime) {
		Fix fix = new Fix(new HiResDate(theTime), new WorldLocation(0, 0, 0),
				MWC.Algorithms.Conversions.Degs2Rads(_courseDegs), _speed
						.getValueIn(WorldSpeed.ft_sec) / 3);
		
		FixWrapper newFix = new FixWrapper(fix);
		return newFix;
	}



}
