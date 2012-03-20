package org.mwc.debrief.core.editors.painters.snail;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.mwc.debrief.core.editors.painters.SnailHighlighter;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldDistance.ArrayLength;

/**
 * class to draw a 'back-track' of points backwards from the current datapoint
 * for the indicated period.
 * 
 * Internally, the class retrieves the list of included points from the track
 * itself and stores them in the HashTable indexed by the current fix. So, when
 * we are asked to plot a point, we look in the HashTable first -- if we have a
 * vector of points for this fix we re-plot these and then remove them from the
 * hashtable. If we don't find a vector of points for this Fix then we retrieve
 * the list from the track and then insert the list into our HashTable Ta-Da!
 * 
 */
final class SnailDrawSWTTrack
{

	/**
	 * the size of points to draw
	 */
	private int _pointSize;

	/**
	 * the length of trail to draw (microseconds)
	 */
	private long _trailLength;

	/**
	 * whether to join fixes
	 */
	private boolean _joinPoints;

	/**
	 * our list of Vectors of points
	 */
	private final java.util.Hashtable<FixWrapper, Collection<Editable>> _fixLists;

	/**
	 * whether to fade out the track and symbols
	 */
	private boolean _fadePoints;

	// /////////////////////////////////
	// constructor
	// ////////////////////////////////
	public SnailDrawSWTTrack()
	{
		setJoinPositions(true);
		setFadePoints(true);
		setTrailLength(new Long(15 * 1000 * 1000 * 60)); // 15 minutes
		setPointSize(6);

		_fixLists = new java.util.Hashtable<FixWrapper, Collection<Editable>>();
	}

	/** calculate the correct color fade for the supplied colors/times
	 * 
	 * @param mainCol the foreground color
	 * @param backColor the background color - to fade out to
	 * @param trail_len how long (in time) the trail should be
	 * @param stepTime the current step time
	 * @param datumTime the time of this data item
	 * @return
	 */
	public static Color getFadedColorFor(Color mainCol, Color backColor,
			long trail_len, HiResDate stepTime, HiResDate datumTime)
	{

		// how far back through the time period are we?
		long our_time = stepTime.getMicros()
				- datumTime.getMicros();

		// just double check that we have a positive time offset
		our_time = Math.max(0, our_time);
		
		float proportion = ((float) ((float)trail_len - our_time) / (float) trail_len);
		
		// just check we've got a realistic proportion
		proportion = Math.max(0, proportion);
		
		// now apply this proportion to the indicated color
		final float backR = backColor.getRed();
		final float backG = backColor.getGreen();
		final float backB = backColor.getBlue();

		int mainR = mainCol.getRed();
		int mainG = mainCol.getGreen();
		int mainB = mainCol.getBlue();

		// now apply this proportion to the indicated color
		final float r = (mainR - backR )* proportion;
		final float g = (mainG - backG )* proportion;
		final float b = (mainB - backB )* proportion;

		// create the colour shade for this item
		final int new_r = (int) (backR + r);
		final int new_g = (int) (backG + g);
		final int new_b = (int) (backB + b);
		
		Color thisCol = new Color(new_r, new_g, new_b);

		return thisCol;
	}

	// /////////////////////////////////
	// member functions
	// ////////////////////////////////
	public final java.awt.Rectangle drawMe(MWC.Algorithms.PlainProjection proj,
			CanvasType dest, Watchable watch, SnailHighlighter parent, HiResDate dtg,
			Color backColor)
	{
		// represent this area as a rectangle
		java.awt.Rectangle thisR = null;
		
		// how long? (convert to millis)
		final long trail_len = (long) parent.getSnailProperties().getTrailLength().getValueIn(
				Duration.MICROSECONDS);

		// get the fix and the track
		final FixWrapper theFix = (FixWrapper) watch;
		TrackWrapper trk = theFix.getTrackWrapper();

		// declare the Vector of track points we are using
		final Collection<Editable> dotPoints;

		// do we have these points already?
		Collection<Editable> myList = _fixLists.get(theFix);

		// did we find it?
		if (myList != null)
		{
			// cast it back to the vector
			dotPoints = myList;
		}
		else
		{
			// retrieve the points in range
			dotPoints = trk.getUnfilteredItems(new HiResDate(0, dtg.getMicros()
					- _trailLength), new HiResDate(0, dtg.getMicros() + 2));

			// add the target fix aswell. We are showing the symbol nearest to the
			// current DTG -
			// which may be ahead of the current DTG. We were drawing lines connecting
			// points
			// from the current DTG back through the indicated time period. This may
			// result in there being a gap between the current symbol and the snail
			// trail. Therefore,
			// add the current fix to the list of points if we don't already contain
			// it.
			if (!dotPoints.contains(theFix))
				dotPoints.add(theFix);

			// check that we found some points for this track
			if (dotPoints != null)
			{
				// and put them into the list
				_fixLists.put(theFix, dotPoints);
			}
		}

		// see if there are any points
		if (dotPoints != null)
		{
			if (dotPoints.size() > 0)
			{
				int _mySize = 5;
				
				// have a go at plotting the array sensor
				if (trk.getPlotArrayCentre()) {
					Enumeration<Editable> enumer = trk.getSensors()
							.elements();
					while (enumer.hasMoreElements()) {
						SensorWrapper sw = (SensorWrapper) enumer
								.nextElement();

						// is this sensor visible?
						if (sw.getVisible()) {
							ArrayLength len = sw.getSensorOffset();
							if (len != null) {
								if (len.getValue() != 0) {
									WorldLocation centre = trk
											.getBacktraceTo(dtg,
													len, sw.getWormInHole()).getLocation();
									Point pt = dest.toScreen(centre);
									dest.drawLine(pt.x - _mySize, pt.y
											- _mySize, pt.x + _mySize, pt.y
											+ _mySize);
									dest.drawLine(pt.x + _mySize, pt.y
											- _mySize, pt.x - _mySize, pt.y
											+ _mySize);
								}
							}
						}
					}
				}
				
				// remember the last location
				Point lastLoc = null;
				
				boolean titlePlotted = false;
				
				// set the line style
				dest.setLineStyle(trk.getLineStyle());
				
				Iterator<Editable> iter = dotPoints.iterator();
				while (iter.hasNext())
				{

					// get this fix
					FixWrapper gw = (FixWrapper) iter.next();

					// get the location
					WorldLocation loc = gw.getLocation();

					// get the screen location
					final Point screenP = new Point(proj.toScreen(loc));

					// initialise the area, if we have to
					if (thisR == null)
						thisR = new Rectangle(screenP);

					// the color to use for this fix
					final Color newCol;

					// see if we are fading to black
					if (_fadePoints)
					{
						// calculate the color for this point in the track (using the fix
						// color)
						newCol = getFadedColorFor(gw.getColor(), backColor, trail_len, dtg,
								gw.getDateTimeGroup());
					}
					else
					{
						// just use the normal track colour
						newCol = gw.getColor();
					}

					// should the track name be visible?
					if (trk.getNameVisible() && !titlePlotted)
					{
						
						Enumeration<Editable> numer = trk.getPositions();
						if (numer.hasMoreElements())
						{
							// is this the first fix of the track?
							if (gw.getDTG().equals(trk.getStartDTG()))
							{
								titlePlotted = true;

								
								final String msg = trk.getName();

								Point copyPt = new Point(screenP);

								// shift the centre point across a bit
								copyPt.translate(5, 0);

								dest.setColor(newCol);

								// and draw the text
								dest.drawText(msg, screenP.x, screenP.y);

								// somehow we need to include this extended area
								final int sWid = msg.length() * 6;

								// shift from the start of the string
								copyPt.translate(sWid, 0);

								// and add to the limits rectangle
								thisR.add(screenP);

								// move it up a little
								copyPt = new Point(screenP);
								copyPt.translate(sWid, 20);
								thisR.add(copyPt);
								copyPt = new Point(screenP);
								copyPt.translate(sWid, -20);
								thisR.add(copyPt);

								//								
								// LabelWrapper label = new LabelWrapper(trk.getName(), gw
								// .getLocation(), trk.getColor());
								// label.paint(dest);
								//								
								// // add a phony area thingy, to include the track name
								// Point p1 = new Point(screenP);
								// p1.translate(-40, -20);
								// Rectangle labelRect = new Rectangle(p1,new Dimension(80,40));
								// thisR.add(labelRect);
							}
						}
					}

					// update the colour for this segment
					dest.setColor(newCol);

					// get the fix to draw itself
					gw.paintMe(dest, loc, newCol);

					// see if we are joining them
					if (lastLoc == null)
					{
						lastLoc = screenP;
					}
					else
					{
						// see if we are joining the points
						if (_joinPoints)
						{
							dest.drawLine(lastLoc.x, lastLoc.y, screenP.x, screenP.y);
						}

						// extend the area
						thisR.add(lastLoc);
						thisR.add(screenP);

						lastLoc = screenP;

					}
				}
				
				// and restore the line style
				dest.setLineStyle(CanvasType.SOLID);
			}
		}

		return thisR;
	}

	public final void setJoinPositions(final boolean val)
	{
		_joinPoints = val;
	}

	public final boolean getJoinPositions()
	{
		return _joinPoints;
	}

	public final void setFadePoints(final boolean val)
	{
		_fadePoints = val;
	}

	public final boolean getFadePoints()
	{
		return _fadePoints;
	}

	/**
	 * point size of symbols (pixels)
	 */
	public final int getPointSize()
	{
		return _pointSize;
	}

	/**
	 * length of trail to plot (micros)
	 */
	public final Long getTrailLength()
	{
		return new Long(_trailLength);
	}

	/**
	 * size of points to draw (pixels)
	 */
	public final void setPointSize(final int val)
	{
		_pointSize = val;
	}

	/**
	 * length of trail to draw (micros)
	 */
	public final void setTrailLength(final Long len)
	{
		_trailLength = len.longValue();

		// and clear the lists of fixes we are using, so that they are re-calculated
		if (_fixLists != null)
			_fixLists.clear();
	}

}
