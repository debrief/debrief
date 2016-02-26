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
package MWC.TacticalData;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Date;

import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public final class NarrativeEntry implements MWC.GUI.Plottable, Serializable,
		ExcludeFromRightClickEdit
{

	public static final String DTG = "DTG";
	public static final Color DEFAULT_COLOR = new Color(178,0,0);

	// ///////////////////////////////////////////
	// member variables
	// ///////////////////////////////////////////
	private String _track;
	private HiResDate _DTG;
	private String _entry;
	private String _type;
	String _DTGString = null;

	private transient NarrativeEntryInfo _myInfo;

	private Color _color = DEFAULT_COLOR;

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	// ///////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////

	/**
	 * new constructor - for narrative entries which include the type of entry
	 * (typically for SMNT narratives)
	 * 
	 * @param track
	 *          name of the track this applies to
	 * @param type
	 *          what sort of entry this is (or null)
	 * @param DTG
	 *          when the entry was recorded
	 * @param entry
	 *          the content of the entry
	 */
	public NarrativeEntry(final String track, final String type,
			final HiResDate DTG, final String entry)
	{
		_track = track;
		_DTG = DTG;
		_entry = entry;
		_type = type;
	}

	/**
	 * old constructor - for when narratives didn't include the type attribute
	 * 
	 * @param track
	 *          name of the track this applies to
	 * @param DTG
	 *          when the entry was recorded
	 * @param entry
	 *          the content of the entry
	 */
	public NarrativeEntry(final String track, final HiResDate DTG,
			final String entry)
	{
		this(track, null, DTG, entry);
	}

	// ///////////////////////////////////////////
	// accessor methods
	// ///////////////////////////////////////////
	public final String getTrackName()
	{
		return _track;
	}

	public final String getSource()
	{
		return _track;
	}

	@FireReformatted
	public final void setSource(final String track)
	{
		_track = track;
	}

	public final String getEntry()
	{
		return _entry;
	}

	@FireReformatted
	public void setEntry(final String val)
	{
		_entry = val;
	}

	public final HiResDate getDTG()
	{
		return _DTG;
	}

	@FireExtended
	public void setDTG(final HiResDate date)
	{
		_DTG = date;
	}

	public final String getType()
	{
		return _type;
	}

	@FireReformatted
	public void setType(final String type)
	{
		_type = type;
	}

	public final String getDTGString()
	{
		if (_DTGString == null)
			_DTGString = DebriefFormatDateTime.toStringHiRes(_DTG);

		return _DTGString;
	}

	public void setColor(Color color)
	{
		_color  = color;
	}
	
	public Color getColor()
	{
		return _color;
	}

	/**
	 * member function to meet requirements of comparable interface *
	 */
	public final int compareTo(final Plottable o)
	{
		final NarrativeEntry other = (NarrativeEntry) o;
		int result = _DTG.compareTo(other._DTG);
		if (result == 0)
			result = 1;
		return result;
	}

	// ///////////////////////////////////////////
	// member methods to meet requirements of Plottable interface
	// ///////////////////////////////////////////

	/**
	 * paint this object to the specified canvas
	 */
	public final void paint(final MWC.GUI.CanvasType dest)
	{
	}

	/**
	 * find the data area occupied by this item
	 */
	public final MWC.GenericData.WorldArea getBounds()
	{
		return null;
	}

	/**
	 * it this item currently visible?
	 */
	public final boolean getVisible()
	{
		return true;
	}

	/**
	 * set the visibility (although we ignore this)
	 */
	public final void setVisible(final boolean val)
	{
	}

	/**
	 * how far away are we from this point? or return null if it can't be
	 * calculated
	 */
	public final double rangeFrom(final MWC.GenericData.WorldLocation other)
	{
		return -1;
	}

	/**
	 * get the editor for this item
	 * 
	 * @return the BeanInfo data for this editable object
	 */
	public final MWC.GUI.Editable.EditorType getInfo()
	{
		if (_myInfo == null)
			_myInfo = new NarrativeEntryInfo(this, this.toString());
		return _myInfo;
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
		return DebriefFormatDateTime.toStringHiRes(_DTG);
	}

	public final String toString()
	{
		return getName();
	}
	
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof NarrativeEntry))
            return false;
        return super.equals(obj);
	}


	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public final class NarrativeEntryInfo extends Editable.EditorType
	{

		public NarrativeEntryInfo(final NarrativeEntry data, final String theName)
		{
			super(data, theName, data.toString());
		}

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] myRes =
				{

				prop("Type", "the type of entry", FORMAT),
						prop("Source", "the source for this entry", FORMAT),
						prop(DTG, "the time this entry was recorded", FORMAT),
						prop("Color", "the color for this narrative entry", FORMAT),
						prop("Entry", "the content of this entry", FORMAT), };

				return myRes;

			}
			catch (final IntrospectionException e)
			{
				e.printStackTrace();
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
			final HiResDate hd = new HiResDate(new Date());
			final NarrativeEntry ne = new NarrativeEntry("aaa", "bbb", hd, "vvvv");
			editableTesterSupport.testParams(ne, this);
		}
	}
	
	
}