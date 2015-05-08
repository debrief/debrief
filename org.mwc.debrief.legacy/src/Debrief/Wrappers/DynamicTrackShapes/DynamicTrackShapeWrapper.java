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
package Debrief.Wrappers.DynamicTrackShapes;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.FireReformatted;
import MWC.GUI.Griddable;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

abstract public class DynamicTrackShapeWrapper extends PlainWrapper implements
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
		 * @param semiTransparent how to fill shape
		 */
		public void paint(CanvasType dest, Watchable hostState, Color color, boolean semiTransparent);

	}
	


	// ///////////////////////////////////////////
	// member variables
	/**
	 * /////////////////////////////////////////////
	 */
	private String _trackName;

	private HiResDate _startDTG, _endDTG;

	protected List<DynamicShape> _values = new ArrayList<DynamicShape>();

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

	private String _coverageName;

	private int _lineWidth;

	private boolean _semiTransparent = true;

	/**
	 * default constructor, used when we read in from XML
	 */
	public DynamicTrackShapeWrapper()
	{
		// by default, objects based on plain wrapper are coloured yellow.
		// but, we use a null colour value to indicate 'use parent color'
		setColor(null);

		setVisible(true);
	}

	/**
	 * build a new sensorarc contact wrapper
	 * 
	 */

	public DynamicTrackShapeWrapper(final String theTrack,
			final HiResDate startDtg, final HiResDate endDtg,
			List<DynamicShape> values, final Color theColor, final int theStyle,
			final String coverageName)
	{
		this();

		_trackName = theTrack;
		_startDTG = startDtg;
		_endDTG = endDtg;
		_values = values;

		// and the gui parameters
		setColor(theColor);
		_myLineStyle = theStyle;
		_coverageName = coverageName;
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
					.trace("This dynamic shape hasn't got a parent at time:" + DTG.getDate());
		}
		else
		{

			// ok, we've got enough to do the paint!
			for (DynamicShape value : _values)
			{
				value.paint(dest, hostState, getColor(), _semiTransparent);
			}
		}

		// and restore the background color
		dest.setBackgroundColor(oldColor);

	}
	

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
	public final boolean getSemiTransparent()
	{
		return _semiTransparent;
	}

	/**
	 * set the Label visibility
	 */
	public final void setSemiTransparent(final boolean val)
	{
		_semiTransparent = val;
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
			_myEditor = new DynamicTrackShapeWrapperInfo(this);

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
		else
		{
			builder.append("[UNSET]");			
		}
		builder.append("-");
		if (_endDTG != null)
		{
			builder.append(DebriefFormatDateTime.toStringHiRes(_endDTG));
		}
		else
		{
			builder.append("[UNSET]");			
		}
		return builder.toString();
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
	public static final class DynamicTrackShapeWrapperInfo extends Griddable
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public DynamicTrackShapeWrapperInfo(final DynamicTrackShapeWrapper data)
		{
			super(data, data.getName(), "DynamicShape");
		}

		@Override
		public PropertyDescriptor[] getGriddablePropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res = {
						prop("Visible", "whether this dynamic shape data is visible",
								FORMAT),
						prop("Color", "the color for this sensor contact", FORMAT),
						prop("StartDTG", "the start time this entry was recorded", FORMAT),
						prop("EndDTG", "the end time this entry was recorded", FORMAT),
						prop("Constraints", "sensor arcs: min max angle range", FORMAT) };

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
						prop("Visible", "whether this sensor contact data is visible",
								FORMAT),
						prop("Color", "the color for this sensor contact", FORMAT),
						prop("StartDTG", "the start time this entry was recorded", FORMAT),
						prop("EndDTG", "the end time this entry was recorded", FORMAT),
						prop("SemiTransparent", "whether to make the coverage semi-transparent", FORMAT),
						prop("Constraints", "sensor arcs: min max angle range", FORMAT),
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
	
	public List<DynamicShape> getValues()
	{
		return _values;
	}

	abstract public String getConstraints();

	abstract public void setConstraints(String arcs);

	protected int getValue(String[] elements, int index)
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
