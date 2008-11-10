/*
 * RangeHighlighter.java
 *
 * Created on 29 September 2000, 11:29
 */

package org.mwc.debrief.core.editors.painters.highlighters;

import java.awt.*;

import Debrief.GUI.Tote.Painters.SnailPainter.DoNotHighlightMe;
import MWC.GUI.*;
import MWC.GUI.Properties.*;
import MWC.GenericData.*;

/**
 * @author IAN MAYO
 * @version
 */
public final class SWTRangeHighlighter implements SWTPlotHighlighter
{

	public static final String RANGE_RING_HIGHLIGHT = "Range Ring Highlight";

	/**
	 * the radius of the outer ring (yds)
	 */
	private double _radius;

	/**
	 * how far either side the arcs should extend (degs)
	 */
	private int _arcs;

	/**
	 * // how many rings to plot
	 */
	private int _rings;

	/**
	 * how far apart to plot the spokes (degs)
	 */
	private int _spoke_separation;

	/**
	 * // the colour of the rings
	 */
	private java.awt.Color _myColor = Color.darkGray;

	/**
	 * whether just a plain rectangle should be plotted (ignoring the rings)
	 */
	private boolean _plainRectangle = false;

	/**
	 * Creates new RangeHighlighter
	 */
	public SWTRangeHighlighter()
	{
		// do some initialisation
		_radius = 3000;
		_arcs = 180;
		_rings = 3;
		_spoke_separation = 45;
	}

	/**
	 * Draw a highlight around this watchable
	 * 
	 * @param proj
	 *          the current projection
	 * @param watch
	 *          the current data point
	 */
	public final void highlightIt(MWC.Algorithms.PlainProjection proj, CanvasType dest,
			Debrief.Tools.Tote.WatchableList list, Debrief.Tools.Tote.Watchable watch)
	{
		
  	// sort out if this is an item that we plot
  	if(watch instanceof DoNotHighlightMe)
  	{
  		// hey, don't bother...
  		return;
  	}
  	
		if (_plainRectangle)
		{
			drawRectangle(watch, dest, proj, 5);
		}
		else
		{
			WorldLocation center = watch.getLocation();
			int header = (int) MWC.Algorithms.Conversions.Rads2Degs(watch.getCourse());
			drawRangeRings(center, _radius, header, _arcs, _rings, _spoke_separation, dest);
		}

	}

	/**
	 * the name of this object
	 * 
	 * @return the name of this editable object
	 */
	public final String getName()
	{
		return RANGE_RING_HIGHLIGHT;
	}

	/**
	 * the name of this object
	 * 
	 * @return the name of this editable object
	 */
	public final String toString()
	{
		return getName();
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 * 
	 * @return yes/no
	 */
	public final boolean hasEditor()
	{
		return true;
	}

	/**
	 * get the editor for this item
	 * 
	 * @return the BeanInfo data for this editable object
	 */
	public final Editable.EditorType getInfo()
	{
		return new RangeHighlightInfo(this);
	}

	private void drawRectangle(final Debrief.Tools.Tote.Watchable watch,
			final CanvasType dest, final MWC.Algorithms.PlainProjection proj, final int mySize)
	{
		// set the highlight colour
		dest.setColor(_myColor);
		// get the current area of the watchable
		final WorldArea wa = watch.getBounds();
		// convert to screen coordinates
		final Point tl = proj.toScreen(wa.getTopLeft());
		final int tlx = tl.x;
		final int tly = tl.y;
		final Point br = proj.toScreen(wa.getBottomRight());
		// get the width
		final int x = tlx - mySize;
		final int y = tly - mySize;
		final int wid = (br.x - tlx) + mySize * 2;
		final int ht = (br.y - tly) + mySize * 2;

		// plot the rectangle
		dest.drawRect(x, y, wid, ht);

	}

	private void drawRangeRings(final WorldLocation worldCentre, final double worldRadius,
			final int rawAxis, final int arcs, final int rings, final int spoke_separation,
			final CanvasType dest)
	{

		dest.setColor(_myColor);

		final MWC.Algorithms.PlainProjection _proj = dest.getProjection();

		// sort out the centre in screen coords
		final Point centrePos = _proj.toScreen(worldCentre);
		final int centrex = centrePos.x;
		final int centrey = centrePos.y;

		// sort out the range in screen coords
		final WorldLocation worldEdge = worldCentre.add(new WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(rawAxis), MWC.Algorithms.Conversions
						.Yds2Degs(worldRadius), 0));
		final Point screenEdge = _proj.toScreen(worldEdge);
		final int dx = screenEdge.x - centrex;
		final int dy = screenEdge.y - centrey;
		final int radius = (int) Math.sqrt(dx * dx + dy * dy);

		// check that the axis is in the correct direction (we may be in relative
		// projection)
		final int axis = (int) MWC.Algorithms.Conversions.Rads2Degs(Math.atan2(dx, -dy));

		// now the centre stalk
		final double axisRads = MWC.Algorithms.Conversions.Degs2Rads(axis);
		final Point edge = new Point((int) ((double) radius * Math.sin(axisRads)),
				-(int) ((double) radius * Math.cos(axisRads)));
		edge.translate(centrex, centrey);
		dest.drawLine(centrex, centrey, edge.x, edge.y);

		// now the start edge stalk
		final double startRads = MWC.Algorithms.Conversions.Degs2Rads(axis - arcs);
		edge.setLocation((int) ((double) radius * Math.sin(startRads)),
				-(int) ((double) radius * Math.cos(startRads)));
		edge.translate(centrex, centrey);
		dest.drawLine(centrex, centrey, edge.x, edge.y);

		// now the end edge stalk
		/**
		 * NOTE we only draw the end edge stalk if we are NOT drawing arcs of 180
		 * degrees either side (since in that case drawing it twice effectively
		 * hides it
		 */
		final double endRads = MWC.Algorithms.Conversions.Degs2Rads(axis + arcs);
		if (arcs != 180)
		{
			edge.setLocation((int) ((double) radius * Math.sin(endRads)),
					-(int) ((double) radius * Math.cos(endRads)));
			edge.translate(centrex, centrey);
			dest.drawLine(centrex, centrey, edge.x, edge.y);
		}

		// now draw the spokes, working out either side from the axis
		int thisSpoke = spoke_separation; // on the axis
		double spokeRads1 = 0;
		double spokeRads2 = 0;
		final Point edge1 = new Point();
		final Point edge2 = new Point();

		while (thisSpoke < arcs)
		{
			// find the left/right angles in rads
			spokeRads1 = MWC.Algorithms.Conversions.Degs2Rads(axis - thisSpoke);
			spokeRads2 = MWC.Algorithms.Conversions.Degs2Rads(axis + thisSpoke);
			// calculate the offset produced by this angle
			edge1.setLocation((int) ((double) radius * Math.sin(spokeRads1)),
					-(int) ((double) radius * Math.cos(spokeRads1)));
			edge2.setLocation((int) ((double) radius * Math.sin(spokeRads2)),
					-(int) ((double) radius * Math.cos(spokeRads2)));
			// add this to the centre
			edge1.translate(centrex, centrey);
			edge2.translate(centrex, centrey);
			// draw the line
			dest.drawLine(centrex, centrey, edge1.x, edge1.y);
			dest.drawLine(centrex, centrey, edge2.x, edge2.y);
			// move on to the next spoke
			thisSpoke += spoke_separation;
		}

		// now the range rings
		final int ring_separation = radius / rings;
		int thisRadius = ring_separation;
		final Point origin = new Point();

		// sort out the angles and arcs (all in degs)
		final int startAngle = 90 - axis + arcs;
		final int endAngle = startAngle - 2 * arcs;
		final int angle = endAngle - startAngle;

		for (int i = 0; i < rings; i++)
		{
			origin.setLocation(new Point(centrex, centrey));

			// shift the centre point to the TL corner of the area
			origin.translate(-thisRadius, -thisRadius);

			// draw in the arc itself
			dest.drawArc(origin.x, origin.y, thisRadius * 2, thisRadius * 2, startAngle, angle);

			// move on to the next radius
			thisRadius += ring_separation;
		}
	}

	// /////////////////////////////////////////////////////////
	// accessor methods for editing this highlighter
	/**
	 * /////////////////////////////////////////////////////////// the colour of
	 * the rings
	 * 
	 * @param col
	 *          the colour, of course
	 */
	public final void setColor(final java.awt.Color col)
	{
		_myColor = col;
	}

	/**
	 * get the colour of the rings
	 * 
	 * @return the colour, of course
	 */
	public final java.awt.Color getColor()
	{
		return _myColor;
	}

	/**
	 * /////////////////////////// set the radius of the outer ring (yds)
	 * 
	 * @param radius
	 *          yds
	 */
	public final void setRadius(final double radius)
	{
		_radius = radius;
	}

	/**
	 * get the radius of the outer ring (yds)
	 * 
	 * @return radius of the outer ring (yds)
	 */
	public final double getRadius()
	{
		return _radius;
	}

	/**
	 * /////////////////////////// whether just a plain rectangle should be
	 * plotted (ignoring the rings)
	 */
	public final void setPlainRectangle(final boolean val)
	{
		_plainRectangle = val;
	}

	/**
	 * whether just a plain rectangle should be plotted (ignoring the rings)
	 */
	public final boolean getPlainRectangle()
	{
		return _plainRectangle;
	}

	// //////////////////////
	/**
	 * get the arcs either side of ownship heading
	 * 
	 * @return range of arcs in degrees
	 */
	public final BoundedInteger getArcs()
	{
		return new BoundedInteger(_arcs, 0, 180);
	}

	/**
	 * get the arcs either side of ownship heading
	 * 
	 * @param val
	 *          range of arcs in degrees
	 */
	public final void setArcs(final BoundedInteger val)
	{
		_arcs = val.getCurrent();
	}

	/**
	 * get the arcs either side of ownship heading - convenience method for XML
	 * persistence
	 * 
	 * @param val
	 *          range of arcs in degrees
	 */
	public final void setArcs(final int val)
	{
		_arcs = val;
	}

	// ////////////////////////
	/**
	 * set the size of the spokes
	 * 
	 * @param val
	 *          separation of arcs in degrees
	 */
	public final void setSpokeSeparation(final Integer val)
	{
		_spoke_separation = val.intValue();
	}

	/**
	 * get the size of the spokes
	 * 
	 * @return val separation of arcs in degrees
	 */
	public final Integer getSpokeSeparation()
	{
		return new Integer(_spoke_separation);
	}

	/**
	 * the number of rings between the centre and the outer spoke
	 * 
	 * @param val number of rings
	 */
	public final void setNumRings(final BoundedInteger val)
	{
		_rings = val.getCurrent();
	}

	/**
	 * the number of rings between the centre and the outer spoke - convenience method for XML persistence
	 * 
	 * @param val number of rings
	 */
	public final void setNumRings(final int val)
	{
		_rings = val;
	}
	
	/**
	 * the number of rings between the centre and the outer spoke
	 * 
	 * @return the number of rings
	 */
	public final BoundedInteger getNumRings()
	{
		return new BoundedInteger(_rings, 1, 10);
	}

	// ///////////////////////////////////////////////////////////
	// nested class describing how to edit this class
	// //////////////////////////////////////////////////////////
	/**
	 * the set of editable details for the painter
	 */
	public static final class RangeHighlightInfo extends Editable.EditorType
	{

		/**
		 * constructor for editable
		 * 
		 * @param data
		 *          the object we are editing
		 */
		public RangeHighlightInfo(final SWTRangeHighlighter data)
		{
			super(data, "Range Highlight", "");
		}

		/**
		 * the set of descriptions for this object
		 * 
		 * @return the properties
		 */
		public final java.beans.PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final java.beans.PropertyDescriptor[] res = {
						prop("Color", "Color to paint highlight"),
						prop("Radius", "Radius of outer ring (yds)"),
						prop("Arcs", "Angle of arcs each side (degs)"),
						prop("SpokeSeparation", "Angle between spokes (degs)"),
						prop("NumRings", "Number of range rings to draw (including outer)"),
				// prop("PlainRectangle", "Draw simple rectangle around point"),
				};
				return res;
			}
			catch (Exception e)
			{
				MWC.Utilities.Errors.Trace.trace(e);
				return super.getPropertyDescriptors();
			}

		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}

		public final void testMyParams()
		{
			Editable ed = new SWTRangeHighlighter();
			Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}
}
