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
package Debrief.Wrappers;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.FireReformatted;
import MWC.GUI.Griddable;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.CircleShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public final class DynamicTrackShapeWrapper extends PlainWrapper implements
		Editable.DoNotHighlightMe, ExcludeFromRightClickEdit
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static interface DynamicShape
	{
		/** paint this dynamic shape
		 * 
		 * @param dest where to paint to
		 * @param hostState the host state at this time
		 * @param color color for this object
		 */
		public void paint(CanvasType dest, Watchable hostState, Color color);

	}
	
	/**
	 * utility class used to store a single sensor coverage arc
	 * 
	 * @author ian
	 * 
	 */
	public static class DynamicCoverageShape implements DynamicShape
	{
		
		final public int minYds, maxYds, minAngleDegs, maxAngleDegs;
		
		public DynamicCoverageShape(int MinAngleDegs, int MaxAngleDegs, int minYds, int maxYds)
		{
			this.minAngleDegs = MinAngleDegs;
			this.maxAngleDegs = MaxAngleDegs;
			this.minYds = minYds;
			this.maxYds = maxYds;
		}

	  /**
	   * calculate the shape as a series of WorldLocation points.  Joined up, these form a representation of the shape
	   * @param trackCourseDegs 
	   */
	  private Vector<WorldLocation> calcDataPoints(final WorldLocation origin, final double orient)
	  {
	    // get ready to store the list
	    final Vector<WorldLocation> res = new Vector<WorldLocation>(0, 1);

	    final double minDegs = new WorldDistance(minYds, WorldDistance.YARDS).getValueIn(WorldDistance.DEGS);
	    final double maxDegs = new WorldDistance(maxYds, WorldDistance.YARDS).getValueIn(WorldDistance.DEGS);
	    
	    for (int i = 0; i <= CircleShape.NUM_SEGMENTS; i++)
	    {
	      // produce the current bearing
	      final double this_brg = (360.0 / CircleShape.NUM_SEGMENTS * i) / 180.0 * Math.PI;


	      // first produce a standard ellipse of the correct size
	      final double x1 = Math.sin(this_brg) * maxDegs;
	      double y1 = Math.cos(this_brg) * maxDegs;

	      // now produce the range out to the edge of the ellipse at
	      // this point
	      final double r = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));

	      // to prevent div/0 error in atan, make y1 small if zero
	      if (y1 == 0)
	        y1 = 0.0000001;

	      // and the new bearing to the correct point on the ellipse
	      final double tr = Math.atan2(y1, x1) + orient;

	      // use our "add" function to add a vector, rather than the
	      // x-y components as we did, so that the ellipse stays correctly
	      // shaped as it travels further from the equator.
	      final WorldLocation wl = origin.add(new WorldVector(tr, r, 0));

	      res.add(wl);
	    }
	    
	    // and now the inner shape
	    for (int i = 0; i <= CircleShape.NUM_SEGMENTS; i++)
	    {
	      // produce the current bearing
	      final double this_brg = (360.0 / CircleShape.NUM_SEGMENTS * i) / 180.0 * Math.PI;


	      // first produce a standard ellipse of the correct size
	      final double x1 = Math.sin(this_brg) * minDegs;
	      double y1 = Math.cos(this_brg) * minDegs;

	      // now produce the range out to the edge of the ellipse at
	      // this point
	      final double r = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));

	      // to prevent div/0 error in atan, make y1 small if zero
	      if (y1 == 0)
	        y1 = 0.0000001;

	      // and the new bearing to the correct point on the ellipse
	      final double tr = Math.atan2(y1, x1) + orient;

	      // use our "add" function to add a vector, rather than the
	      // x-y components as we did, so that the ellipse stays correctly
	      // shaped as it travels further from the equator.
	      final WorldLocation wl = origin.add(new WorldVector(tr, r, 0));

	      res.add(wl);
	    }

	    return res;

	  }
		
		
		public void paint(CanvasType dest, Watchable hostState, Color color)
		{
				// get the polygon at this location
				Vector<WorldLocation> _theDataPoints = calcDataPoints(hostState.getLocation(), hostState.getCourse());
				

		    // create a polygon to represent the ellipse (so that we can fill or draw it)
		    final int len = _theDataPoints.size();
		    final int[] xPoints = new int[len];
		    final int[] yPoints = new int[len];

		    // work through the list to create the list of screen coordinates
		    for (int i = 0; i < _theDataPoints.size(); i++)
		    {
		      final WorldLocation location = (WorldLocation) _theDataPoints.elementAt(i);

		      final Point p2 = dest.toScreen(location);

		      xPoints[i] = p2.x;
		      yPoints[i] = p2.y;
		    }

//					if (getSemiTransparent() && dest instanceof ExtendedCanvasType)
//					{
						ExtendedCanvasType ext = (ExtendedCanvasType) dest;
		    		ext.semiFillPolygon(xPoints, yPoints, len);
//					}
//		    	else
//		    		dest.fillPolygon(xPoints, yPoints, len);
		}
		
		public String toString()
		{
			return minAngleDegs + " " + maxAngleDegs + " " + minYds + " " + maxYds + " ";
		}
	}

	// ///////////////////////////////////////////
	// member variables
	/**
	 * /////////////////////////////////////////////
	 */
	private String _trackName;

	private HiResDate _startDTG, _endDTG;

	private List<DynamicCoverageShape> _values = new ArrayList<DynamicCoverageShape>();

	private String _arcs;

	/**
	 * whether to show the label
	 */
	private boolean _showLabel = false;

	/**
	 * our editor
	 */
	transient private MWC.GUI.Editable.EditorType _myEditor;

	/**
	 * the style to plot this line
	 */
	private int _myLineStyle = 0;

	/**
	 * the parent object (which supplies our colour, should we need it)
	 */
	private transient DynamicTrackShapeSetWrapper _mySensor;

	/**
	 * the label describing this contact
	 */
	private final MWC.GUI.Shapes.TextLabel _theLabel;

	private String _coverageName;

	private int _lineWidth;

	/**
	 * default constructor, used when we read in from XML
	 */
	public DynamicTrackShapeWrapper()
	{
		// create the label
		_theLabel = new MWC.GUI.Shapes.TextLabel(new WorldLocation(0, 0, 0), null);

		// by default, objects based on plain wrapper are coloured yellow.
		// but, we use a null colour value to indicate 'use parent color'
		setColor(null);

		setVisible(true);
		setLabelVisible(false);
	}

	/**
	 * build a new sensorarc contact wrapper
	 * 
	 */

	public DynamicTrackShapeWrapper(final String theTrack,
			final HiResDate startDtg, final HiResDate endDtg,
			List<DynamicCoverageShape> values, final Color theColor, final int theStyle,
			final String coverageName)
	{
		this();

		_trackName = theTrack;
		_startDTG = startDtg;
		_endDTG = endDtg;
		_values = values;
		calculateArcs();

		// and the gui parameters
		setColor(theColor);
		_myLineStyle = theStyle;
		_theLabel.setString(coverageName);
		_coverageName = coverageName;
	}

	private void calculateArcs()
	{
		StringBuilder builder = new StringBuilder();
		for (DynamicCoverageShape value : _values)
		{
			builder.append(value.minAngleDegs);
			builder.append(" ");
			builder.append(value.maxAngleDegs);
			builder.append(" ");
			builder.append(value.minYds);
			builder.append(" ");
			builder.append(value.maxYds);
			builder.append(" ");
		}
		_arcs = builder.toString().trim();
	}

	/**
	 * getTrackName
	 * 
	 * @return the returned String
	 */
	public final String getTrackName()
	{
		return _trackName;
	}

	/**
	 * member function to meet requirements of comparable interface *
	 */
	public final int compareTo(final Plottable o)
	{
		final DynamicTrackShapeWrapper other = (DynamicTrackShapeWrapper) o;
		if (_startDTG == null || other == null || other._startDTG == null)
		{
			return 1;
		}
		int res = 0;
		if (_startDTG.lessThan(other._startDTG))
			res = -1;
		else if (_startDTG.greaterThan(other._startDTG))
			res = 1;
		else
		{
			// just check if this is actually the same object (in which case return 0)
			if (o == this)
			{
				// we need a correct implementation of compare to for when we're finding
				// the position
				// of an item which is actually in the list - otherwise it won't get
				// found and we can't
				// delete it.
				res = 0;
			}
			else
			{
				// same times, make the newer item appear later. This is to overcome the
				// problem we experience where only the first contact at a particular
				// DTG gets recorded for a sensor
				res = 1;
			}
		}

		return res;

	}

	// ///////////////////////////////////////////
	// member methods to meet requirements of Plottable interface
	// ///////////////////////////////////////////

	/**
	 * paint this object to the specified canvas
	 */
	public final void paint(final MWC.GUI.CanvasType dest)
	{
		// DUFF METHOD TO MEET INTERFACE REQUIREMENTS
	}

	/**
	 * paint this object to the specified canvas
	 * 
	 * @param track
	 *          the parent list (from which we calculate origins if required)
	 * @param dest
	 *          where we're painting it to
	 * @param keep_simple
	 *          whether to allow a change in line styles
	 */
	public final void paint(final MWC.GUI.CanvasType dest, final HiResDate DTG)
	{
		// ignore
		if (!getVisible())
			return;

		// restore the solid line style, for the next poor bugger
		dest.setLineStyle(MWC.GUI.CanvasType.SOLID);

		Color oldColor = dest.getBackgroundColor();

		Watchable hostState = null;

		if (DTG != null)
		{
			Watchable[] wList = getSensor().getHost().getNearestTo(DTG);
			if (wList.length > 0)
			{
				hostState = wList[0];
			}
		}

		// have we worked?
		if (hostState == null)
		{
			MWC.Utilities.Errors.Trace
					.trace("This sensor arc object hasn't got a parent at time:" + DTG);
		}
		else
		{

			// ok, we've got enough to do the paint!
			for (DynamicCoverageShape value : _values)
			{
				value.paint(dest, hostState, getColor());
//				paintArc(dest, origin, trackCourse, getColor(), value);
			}
		}

		dest.setBackgroundColor(oldColor);

	}
	

//	private void paintArc(final MWC.GUI.CanvasType dest,
//			final WorldLocation origin, int trackCourseDegs, Color color, DynamicCoverageShape value)
//	{
//		// 
//		
//		// get the polygon at this location
//		Vector<WorldLocation> _theDataPoints = calcDataPoints(origin, trackCourseDegs, value);
//		
//
//    // create a polygon to represent the ellipse (so that we can fill or draw it)
//    final int len = _theDataPoints.size();
//    final int[] xPoints = new int[len];
//    final int[] yPoints = new int[len];
//
//    // work through the list to create the list of screen coordinates
//    for (int i = 0; i < _theDataPoints.size(); i++)
//    {
//      final WorldLocation location = (WorldLocation) _theDataPoints.elementAt(i);
//
//      final Point p2 = dest.toScreen(location);
//
//      xPoints[i] = p2.x;
//      yPoints[i] = p2.y;
//    }
//
////			if (getSemiTransparent() && dest instanceof ExtendedCanvasType)
////			{
//				ExtendedCanvasType ext = (ExtendedCanvasType) dest;
//    		ext.semiFillPolygon(xPoints, yPoints, len);
////			}
////    	else
////    		dest.fillPolygon(xPoints, yPoints, len);
//	
//		
//	}

	/**
	 * find the name of the sensor which recorded this contact
	 */
	public final String getSensorName()
	{
		String res;
		if (_mySensor != null)
		{
			res = _mySensor.getName();
		}
		else
		{
			res = _coverageName;
		}
		return res;
	}

	/**
	 * find the data area occupied by this item
	 */
	public final MWC.GenericData.WorldArea getBounds()
	{
		// this object only has a context in time-stepping.
		// since it's dynamic, it doesn't have a concrete bounds.
		return null;
	}

	/**
	 * it this Label item currently visible?
	 */
	public final boolean getLabelVisible()
	{
		return _showLabel;
	}

	/**
	 * set the Label visibility
	 */
	public final void setLabelVisible(final boolean val)
	{
		_showLabel = val;
	}

	/**
	 * return the location of the label
	 */
	public final void setLabelLocation(final Integer loc)
	{
		_theLabel.setRelativeLocation(loc);
	}

	/**
	 * update the location of the label
	 */
	public final Integer getLabelLocation()
	{
		return _theLabel.getRelativeLocation();
	}

	/**
	 * update the line style
	 */
	public final void setLineStyle(final Integer style)
	{
		_myLineStyle = style.intValue();
	}

	/**
	 * retrieve the line style
	 */
	public final Integer getLineStyle()
	{
		return new Integer(_myLineStyle);
	}

	/**
	 * inform us of our sensor
	 */
	public final void setParent(final DynamicTrackShapeSetWrapper sensor)
	{
		_mySensor = sensor;
	}

	public final DynamicTrackShapeSetWrapper getSensor()
	{
		return _mySensor;
	}

	/**
	 * get the label for this data item
	 */
	public final String getLabel()
	{
		return _theLabel.getString();
	}

	/**
	 * set the label for this data item
	 */
	public final void setLabel(final String val)
	{
		_theLabel.setString(val);
	}

	/**
	 * how far away are we from this point? or return null if it can't be
	 * calculated
	 */
	public final double rangeFrom(final MWC.GenericData.WorldLocation other)
	{
		// Note: since this is a dynamic object, it doesn't have a concrete location
		return super.rangeFrom(other);
	}

	/**
	 * getInfo
	 * 
	 * @return the returned MWC.GUI.Editable.EditorType
	 */
	public final MWC.GUI.Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new SensorArcContactInfo(this);

		return _myEditor;
	}

	/**
	 * method to provide the actual colour value stored in this fix
	 * 
	 * @return fix colour, including null if applicable
	 */
	public final Color getActualColor()
	{
		return super.getColor();
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
	 * get the name of this entry, using the formatted DTG
	 */
	public final String getName()
	{
		StringBuilder builder = new StringBuilder();
		if (_startDTG != null)
		{
			builder.append(DebriefFormatDateTime.toStringHiRes(_startDTG));
		}
		builder.append("-");
		if (_endDTG != null)
		{
			builder.append(DebriefFormatDateTime.toStringHiRes(_endDTG));
		}
		return builder.toString();
	}

	/**
	 * get the data name in multi-line format (for tooltips)
	 * 
	 * @return multi-line text label
	 */
	public String getMultiLineName()
	{
		return "SensorArc:" + getName() + "\nTrack:" + getLabel();
	}

	/**
	 * toString
	 * 
	 * @return the returned String
	 */
	public final String toString()
	{
		return getName();
	}

	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the projection
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public static final class SensorArcContactInfo extends Griddable
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public SensorArcContactInfo(final DynamicTrackShapeWrapper data)
		{
			super(data, data.getName(), "SensorArc");
		}

		@Override
		public PropertyDescriptor[] getGriddablePropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res = {
						prop("Label", "the label for this data item", FORMAT),
						prop("Visible", "whether this sensor contact data is visible",
								FORMAT),
						prop("LabelVisible",
								"whether the label for this contact is visible", FORMAT),
						prop("Color", "the color for this sensor contact", FORMAT),
						prop("startDTG", "the start time this entry was recorded", FORMAT),
						prop("endDTG", "the end time this entry was recorded", FORMAT),
						prop("arcs", "sensor arcs: min max angle range", FORMAT),
						longProp("LabelLocation", "the label location",
								MWC.GUI.Properties.LocationPropertyEditor.class),
						longProp("LineStyle", "style to use to plot the line",
								MWC.GUI.Properties.LineStylePropertyEditor.class), };

				return res;

			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

		/**
		 * The things about these Layers which are editable. We don't really use
		 * this list, since we have our own custom editor anyway
		 * 
		 * @return property descriptions
		 */
		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res = {
						prop("Label", "the label for this data item", FORMAT),
						prop("Visible", "whether this sensor contact data is visible",
								FORMAT),
						prop("LabelVisible",
								"whether the label for this contact is visible", FORMAT),
						prop("Color", "the color for this sensor contact", FORMAT),
						prop("startDTG", "the start time this entry was recorded", FORMAT),
						prop("endDTG", "the end time this entry was recorded", FORMAT),
						prop("arcs", "sensor arcs: min max angle range", FORMAT),
						longProp("LabelLocation", "the label location",
								MWC.GUI.Properties.LocationPropertyEditor.class),
						longProp("PutLabelAt",
								"whereabouts on the line to position the label",
								MWC.GUI.Properties.LineLocationPropertyEditor.class),
						longProp("LineStyle", "style to use to plot the line",
								MWC.GUI.Properties.LineStylePropertyEditor.class),

				};
				return res;
			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

		@Override
		public NonBeanPropertyDescriptor[] getNonBeanGriddableDescriptors()
		{
			// don't worry - we do the bean-based method
			return null;
		}

	}

	public void setStartDTG(HiResDate date)
	{
		_startDTG = date;
	}

	public HiResDate getStartDTG()
	{
		return _startDTG;
	}

	public void setEndDTG(HiResDate date)
	{
		_endDTG = date;
	}

	public HiResDate getEndDTG()
	{
		return _endDTG;
	}

	public TimePeriod getPeriod()
	{
		return new TimePeriod.BaseTimePeriod(_startDTG, _endDTG);
	}
	
	public List<DynamicCoverageShape> getValues()
	{
		return _values;
	}

	public String getArcs()
	{
		return _arcs;
	}

	public void setArcs(String arcs)
	{
		if (arcs == null)
		{
			throw new RuntimeException("Error parsing arcs");
		}
		arcs = arcs.trim();
		String[] elements = arcs.split(" ");
		if (elements.length % 4 != 0)
		{
			throw new RuntimeException("Error parsing arcs");
		}
		List<DynamicCoverageShape> values = new ArrayList<DynamicCoverageShape>();
		int index = 0;
		while (index < elements.length)
		{
			int minAngleDegs = getValue(elements, index++);
			int maxAngleDegs = getValue(elements, index++);
			int minYds = getValue(elements, index++);
			int maxYds = getValue(elements, index++);
			DynamicCoverageShape value = new DynamicCoverageShape(minAngleDegs, maxAngleDegs, minYds, maxYds);
			values.add(value);
		}
		this._values = values;
		this._arcs = arcs;
	}

	private int getValue(String[] elements, int index)
	{
		try
		{
			return new Integer(elements[index]).intValue();
		}
		catch (NumberFormatException e)
		{
			throw new RuntimeException("Error parsing arcs. Invalid number.");
		}
	}
	
	/**
	 * the line thickness (convenience wrapper around width)
	 * 
	 * @return
	 */
	public final int getLineThickness()
	{
		return _lineWidth;
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 */
	@FireReformatted
	public final void setLineThickness(final int val)
	{
		_lineWidth = val;
	}

}
