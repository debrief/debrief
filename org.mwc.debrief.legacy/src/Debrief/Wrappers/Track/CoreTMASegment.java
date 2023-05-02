/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package Debrief.Wrappers.Track;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedCanvasType;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * the core elements of a TMA Segment - relative plotting, plus the fact that
 * the segment is actually defined by a course and speed, not a collection of
 * data points
 *
 * @author ianmayo
 *
 */
abstract public class CoreTMASegment extends TrackSegment implements CanBePlottedWithTimeVariable {

	private static final int MAX_HEIGHT = 8;

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * event name for when track is dragged
	 *
	 */
	public static final String ADJUSTED = "Adjusted";

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

	/**
	 * message that we plot 1/2 way along segment when it's being stretched or
	 * rotated
	 *
	 */
	protected String _dragMsg;

	/**
	 * Position of the Draw
	 */
	protected WorldLocation _dragPosition;

	/**
	 * base constructor - sorts out the obvious
	 *
	 * @param courseDegs
	 * @param speed
	 * @param plotRelative
	 */
	public CoreTMASegment(final double courseDegs, final WorldSpeed speed, final boolean plotRelative) {
		super(plotRelative);
		_courseDegs = courseDegs;
		_speed = speed;
	}

	/**
	 * create a nice shiny fix at the indicated time
	 *
	 * @param theTime
	 * @return the new fix, with valid course and speed
	 */
	protected FixWrapper createFixAt(final long theTime) {
		final Fix fix = new Fix(new HiResDate(theTime), new WorldLocation(0, 0, 0),
				MWC.Algorithms.Conversions.Degs2Rads(_courseDegs), _speed.getValueIn(WorldSpeed.ft_sec) / 3);

		final FixWrapper newFix = new FixWrapper(fix);
		newFix.resetName();
		newFix.setLabelFormat("HHmm.ss");
		return newFix;
	}

	protected void fireAdjusted() {
		super.firePropertyChange(ADJUSTED, null, System.currentTimeMillis());
	}

	/**
	 * get the current course of this leg
	 *
	 * @return course (degs)
	 */
	public double getCourse() {
		return _courseDegs;
	}

	public String getDragTextMessage() {
		return _dragMsg;
	}

	/**
	 * the constant speed of this segment
	 *
	 * @return the current speed
	 */
	public WorldSpeed getSpeed() {
		return _speed;
	}

	@Override
	abstract public WorldLocation getTrackStart();

	@Override
	public void paint(final CanvasType dest) {
		paint(dest, null);
	}

	@Override
	public void paint(final CanvasType dest, final ITimeVariableProvider errorProvider) {
		final Collection<Editable> items = getData();

		// ok - draw that line!
		Point lastPoint = null;
		WorldLocation tmaLastLoc = null;
		long tmaLastDTG = 0;

		// try to create a dotted line
		dest.setLineStyle(CanvasType.SOLID);

		// remember the ends, so we can plot a point 1/2 way along them
		WorldLocation firstEnd = null;
		// WorldLocation lastEnd = null;

		for (final Iterator<Editable> iterator = items.iterator(); iterator.hasNext();) {
			final FixWrapper thisF = (FixWrapper) iterator.next();

			final long thisTime = thisF.getDateTimeGroup().getDate().getTime();

			// ok, is this our first location?
			if (tmaLastLoc == null) {
				tmaLastLoc = new WorldLocation(getTrackStart());
				_dragPosition = firstEnd = new WorldLocation(tmaLastLoc);
			} else {
				// calculate a new vector
				final long timeDelta = thisTime - tmaLastDTG;
				final WorldVector thisVec = vectorFor(timeDelta, thisF.getSpeed(), thisF.getCourse());
				tmaLastLoc.addToMe(thisVec);

				// lastEnd = new WorldLocation(tmaLastLoc);
			}

			// dump the location into the fix
			thisF.setFixLocationSilent(new WorldLocation(tmaLastLoc));

			// cool, remember the time.
			tmaLastDTG = thisTime;

			final Point thisPoint = dest.toScreen(thisF.getFixLocation());

			// handle unable to gen screen coords (if off visible area)
			if (thisPoint == null)
				return;

			// do we have enough for a line?
			if (lastPoint != null) {
				// draw that line
				dest.drawLine(lastPoint.x, lastPoint.y, thisPoint.x, thisPoint.y);
			}

			lastPoint = new Point(thisPoint);

			// also draw in a marker for this point
			final int height;
			if (errorProvider != null && errorProvider.applyStyling()) {
				final long thisH = Math.abs(errorProvider.getValueAt(thisF.getDateTimeGroup()));
				final int h = (int) (Math.min(thisH, MAX_HEIGHT) * 2);
				height = h == 0 ? 4 : h;
			} else {
				height = 6;
			}
			dest.fillOval(lastPoint.x - height / 2, lastPoint.y - height / 2, height, height);
		}

		// ok, plot the 1/2 way message
		if (_dragMsg != null) {
			// writeMessage(dest, firstEnd);
		}
	}

	@Override
	public void removeElement(final Editable p) {
		// let the parent do the remove
		super.removeElement(p);

		// now fire the adjusted call, so if we have a
		// dynamic infill, it will know to re-generate itself
		// to fill in the missing data
		fireAdjusted();
	}

	@Override
	abstract public void rotate(double brg, final WorldLocation origin);

	/**
	 * the current course (degs)
	 *
	 * @param course (degs)
	 */
	public void setCourse(final double course) {
		// ensure course is in +ve domain
		final double happyCourse;
		if (course > 0) {
			// all is fine
			happyCourse = course;
		} else if (course < -0.00000000001) {
			// check if we're a significant negative number
			happyCourse = course + 360;
		} else {
			// special case. Sometimes it's a really small
			// negative number, so we're better off making it zero
			happyCourse = 0;
		}

		// ok, store the satisfactory course
		_courseDegs = happyCourse;

		final double crseRads = MWC.Algorithms.Conversions.Degs2Rads(course);
		final Collection<Editable> data = getData();
		for (final Iterator<Editable> iterator = data.iterator(); iterator.hasNext();) {
			final FixWrapper fix = (FixWrapper) iterator.next();
			fix.getFix().setCourse(crseRads);
		}

		// ditch our temp vector, we've got to recalc it
		_vecTempLastDTG = -2;
	}

	/**
	 * set the constant speed of this segment
	 *
	 * @param speed the new speed
	 */
	public void setSpeed(final WorldSpeed speed) {
		_speed = speed;

		final double spdYps = speed.getValueIn(WorldSpeed.ft_sec) / 3;
		final Collection<Editable> data = getData();
		for (final Iterator<Editable> iterator = data.iterator(); iterator.hasNext();) {
			final FixWrapper fix = (FixWrapper) iterator.next();
			fix.getFix().setSpeed(spdYps);
		}

		// ditch our temp vector, we've got to recalc it
		_vecTempLastDTG = -2;

	}

	/**
	 * shear this whole track to the supplied destination
	 *
	 * @param cursor where the user's hovering
	 * @param origin origin of stretch, probably one end of the track
	 */
	abstract public void shear(WorldLocation cursor, final WorldLocation origin);

	@Override
	abstract public void shift(WorldVector vector);

	/**
	 * stretch this whole track to the supplied distance
	 *
	 * @param rngDegs distance to stretch through (degs)
	 * @param origin  origin of stretch, probably one end of the track
	 */
	abstract public void stretch(double rngDegs, final WorldLocation origin);

	public void writeMessage(final CanvasType dest) {
		if (_dragMsg != null && _dragPosition != null) {
			writeMessage(dest, _dragPosition);
		}
	}

	private void writeMessage(final CanvasType dest, final WorldLocation firstEnd) {
		final Point pt = dest.toScreen(firstEnd);

		// handle unable to gen screen coords (if off visible area)
		if (pt == null)
			return;

		// project this point out past the actual start point
		pt.translate((int) (30d * Math.cos(MWC.Algorithms.Conversions.Degs2Rads(getCourse()))),
				(int) (30d * Math.sin(MWC.Algorithms.Conversions.Degs2Rads(getCourse()))));

		// try to make it bold
		final Font newFont = Defaults.getFont().deriveFont(Font.BOLD);

		// put the text in a solid backdrop
		boolean xorMode = false;
		final Color color = dest.getBackgroundColor();
		if (dest instanceof ExtendedCanvasType) {
			// NOTE: this is a workaround, to overcome an occasional
			// SWT rendering problem - where the XOR text
			// wasn't being displayed
			xorMode = ((ExtendedCanvasType) dest).getXORMode();
			((ExtendedCanvasType) dest).setXORMode(false);
		}
		final int ht = dest.getStringHeight(newFont) + 8;
		final int wid = dest.getStringWidth(newFont, _dragMsg);
		dest.setColor(Color.WHITE);
		dest.fillRect(pt.x - 2, pt.y + 24 - ht, wid - 5, ht);
		// and draw the text
		dest.setColor(MWC.GUI.Properties.DebriefColors.BLACK);
		dest.drawText(_dragMsg, pt.x, pt.y + 15);
		if (dest instanceof ExtendedCanvasType) {
			((ExtendedCanvasType) dest).setXORMode(xorMode);
			dest.setColor(color);
		}
	}

}
