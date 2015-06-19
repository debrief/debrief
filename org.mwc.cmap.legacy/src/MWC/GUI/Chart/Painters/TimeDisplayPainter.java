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
package MWC.GUI.Chart.Painters;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorSupport;
import java.io.Serializable;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedEditable;
import MWC.GUI.MovingPlottable;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.DiagonalLocationPropertyEditor;
import MWC.GUI.Properties.MyDateFormatPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

/**
 * Class to plot a time display onto a plot
 */
public class TimeDisplayPainter implements Plottable, MovingPlottable,
		ExtendedEditable, Serializable
{

	private static final String DAYS = "Days";
	private static final String HOURS = "Hours";
	private static final String MINS = "Mins";
	private static final String SECS = "Secs";
	private static final String MILLIS = "Millis";
	
	public static final String ABSOLUTE_DEFAULT_FORMAT = "ddHHmm";
	public static final String RELATIVE_DEFAULT_FORMAT = MILLIS;
	public static final String TIME_DISPLAY_ABSOLUTE = "Time Display (Absolute)";
	public static final String TIME_DISPLAY_RELATIVE = "Time Display (Relative)";

	/**
	 * version number for this painter
	 */
	static final long serialVersionUID = -1;

	/**
	 * colour of this time display
	 */
	private Color _myColor = Color.darkGray;
	private Color _myBackgroundColor = Color.white;
	/**
	 * whether we are visible or not
	 */
	private boolean _isOn = true;
	
	private boolean _absolute = true;
	
	private HiResDate _origin;
	
	private boolean _fillBackground = false;
	
	/**
	 * default location for the time display
	 */
	private int _location = DiagonalLocationPropertyEditor.BOTTOM_RIGHT;

	/**
	 * our editor
	 */
	transient private Editable.EditorType _myEditor;

	private HiResDate _DTG;
	
	private String _name = TIME_DISPLAY_ABSOLUTE;
	
	private String _prefix = "";
	
	private String _suffix = "";

	/**
	 * the font 
	 */
	private java.awt.Font _myFont = new java.awt.Font("Arial",
			java.awt.Font.PLAIN, 12);
	
	private String _format = ABSOLUTE_DEFAULT_FORMAT;
			
	/**
	 * constructor
	 */
	public TimeDisplayPainter()
	{
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	/**
	 * whether the time display is visible or not
	 *
	 * @param val
	 *          yes/no visibility
	 */
	public void setVisible(final boolean val)
	{
		_isOn = val;
	}

	/**
	 * whether the time display is visible or not
	 *
	 * @return yes/no
	 */
	public boolean getVisible()
	{
		return _isOn;
	}

	/**
	 * current colour of the time display
	 *
	 * @param val
	 *          the colour
	 */
	public void setColor(final Color val)
	{
		_myColor = val;
	}

	/**
	 * current colour of the time display
	 *
	 * @return colour
	 */
	public Color getColor()
	{
		return _myColor;
	}

	public void setBackground(final Color val)
	{
		_myBackgroundColor = val;
	}
	
	public Color getBackground()
	{
		return _myBackgroundColor;
	}
	
	public java.awt.Font getFont()
	{
		return _myFont;
	}

	public void setFont(java.awt.Font myFont)
	{
		this._myFont = myFont;
	}
	/**
	 * which corner to position the time display
	 *
	 * @param loc
	 *          one of the enumerated types listed earlier
	 */
	public void setLocation(final Integer loc)
	{
		_location = loc.intValue();
	}

	/**
	 * retrieve the current location of the time display
	 *
	 * @return the current location, from the enumerated types defined for this
	 *         class
	 */
	public Integer getLocation()
	{
		return new Integer(_location);
	}

	/**
	 * redraw the time display
	 *
	 * @param g
	 *          the destination
	 */
	@Override
	public void paint(final CanvasType g)
	{
		// check we are visible
		if (!_isOn)
			return;

		if (_DTG == null)
		{
			return;
		}

		// what is the screen width in logical coordinate?
		final MWC.Algorithms.PlainProjection proj = g.getProjection();

		// find the screen width
		final java.awt.Dimension screen_size = proj.getScreenArea().getSize();

		final int txtHt = g.getStringHeight(_myFont);

		if (_myEditor != null)
		{
			_myEditor.fireChanged(this, "Calc", null, this);
		}

		String str;
		if (_absolute)
		{
			String formattedDTG = FormatRNDateTime.toStringLikeThis(_DTG.getMicros()/1000, _format);
			str = (_prefix == null ? "" : _prefix) + 
					formattedDTG + 
					(_suffix == null ? "" : _suffix);
			
		}
		else
		{
			str = getRelativeTime();
			if (str == null)
			{
				return;
			}
		}

		final int wid = g.getStringWidth(_myFont, str);

		int width = wid;

		java.awt.Point TL = null, BR = null;
		switch (_location)
		{
		case (DiagonalLocationPropertyEditor.TOP_LEFT):
			TL = new Point((int) (screen_size.width * 0.05),
					(int) (txtHt + screen_size.height * 0.032));
			BR = new Point((TL.x + width), (int) (txtHt + screen_size.height * 0.035));
			break;
		case (DiagonalLocationPropertyEditor.TOP_RIGHT):
			BR = new Point((int) (screen_size.width * 0.95),
					(int) (txtHt + screen_size.height * 0.035));
			TL = new Point((BR.x - width), (int) (txtHt + screen_size.height * 0.032));
			break;
		case (DiagonalLocationPropertyEditor.BOTTOM_LEFT):
			TL = new Point((int) (screen_size.width * 0.05),
					(int) (screen_size.height * 0.987));
			BR = new Point((TL.x + width), (int) (screen_size.height * 0.99));
			break;
		default:
			BR = new Point((int) (screen_size.width * 0.95),
					(int) (screen_size.height * 0.99));
			TL = new Point((BR.x - width), (int) (screen_size.height * 0.987));
			break;
		}

		int this_dist = TL.x;

		int x = this_dist - (wid / 2); 
		int y = (int) (TL.y - (0.7 * txtHt));
		
		// draw in the time display value
		int offset = (int) (txtHt * 1.5);
		// setup the drawing object
		Color oldBackground = g.getBackgroundColor();
		g.setBackgroundColor(_myBackgroundColor);
		
		if (_location == DiagonalLocationPropertyEditor.BOTTOM_LEFT || _location == DiagonalLocationPropertyEditor.BOTTOM_RIGHT)
		{
			y = (int) (y - offset*1.5);
		}
		else
		{
			y = (int) (y + offset*2);
		}
		g.setColor(_fillBackground ? _myBackgroundColor : oldBackground);
		int xx = x - offset;
		int yy = y - offset;
		if (xx < 5)
		{
			xx = 5;
		}
		if (yy < 5)
		{
			yy = 5;
		}
		g.fillRect(xx, yy, wid + 2 * offset, txtHt + offset);
		g.setColor(this.getColor());
		g.drawText(_myFont, str, x, y);
	
		g.setBackgroundColor(oldBackground);
	}

	private String getRelativeTime()
	{
		if (_origin == null)
		{
			return null;
		}
		long relativeTime = (_DTG.getMicros() - _origin.getMicros())/1000;
		StringBuilder builder = new StringBuilder();
		if (MILLIS.equals(_format))
		{
			builder.append(relativeTime);
			builder.append(" millis");
		}
		else if (SECS.equals(_format))
		{
			builder.append(relativeTime/1000);
			builder.append(" secs");
		}
		else if (MINS.equals(_format))
		{
			builder.append(relativeTime/(60*1000));
			builder.append(" minutes");
		}
		else if (HOURS.equals(_format))
		{
			builder.append(relativeTime/(60*60*1000));
			builder.append(" hours");
		}
		else if (DAYS.equals(_format))
		{
			builder.append(relativeTime/(24*60*60*1000));
			builder.append(" days");
		}
		return builder.toString();
	}

	/**
	 * the area covered by the time display. It's null in this case, since the
	 * time display resizes to suit the data area.
	 *
	 * @return always null - meaning the time display doesn't mind what size the
	 *         visible plot is
	 */
	public MWC.GenericData.WorldArea getBounds()
	{
		// doesn't return a sensible size
		return null;
	}

	/**
	 * the range of the time display from a point (ignored)
	 *
	 * @param other
	 *          the other point
	 * @return INVALID_RANGE since this is value can't be calculated
	 */
	public double rangeFrom(final MWC.GenericData.WorldLocation other)
	{
		// doesn't return a sensible distance;
		return INVALID_RANGE;
	}

	/**
	 * return this item as a string
	 *
	 * @return the name of the time display
	 */
	public String toString()
	{
		return getName();
	}

	/**
	 * get the name of the time display
	 *
	 * @return the name of the time display
	 */
	public String getName()
	{
		return _name;
	}
	
	public void setName(String name)
	{
		_name = name;
	}

	/**
	 * whether the time display has an editor
	 *
	 * @return yes
	 */
	public boolean hasEditor()
	{
		return true;
	}

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new TimeDisplayPainterInfo(this);

		return _myEditor;
	}

	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	public class TimeDisplayPainterInfo extends Editable.EditorType implements
			Serializable
	{

		// give it some old version id
		static final long serialVersionUID = 1L;

		public TimeDisplayPainterInfo(final TimeDisplayPainter data)
		{
			super(data, data.getName(), "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						prop("Color", "the Color to draw the time display", FORMAT),
						displayProp("Background", "Background color",
								"the Background color for the time display", FORMAT),
						displayProp("FillBackground", "Fill Background",
										"wether to fill background for the time display", FORMAT),
						prop("Name", "the Name for the time display", FORMAT),
						prop("Font", "the Font for the time display", FORMAT),
						prop("Prefix", "the Prefix label for the time display", FORMAT),
						prop("Suffix", "the Suffix label for the time display", FORMAT),
						prop("Visible", "whether this time display is visible", VISIBILITY),
						longProp("Location", "the time display location",
								MWC.GUI.Properties.DiagonalLocationPropertyEditor.class, FORMAT),
				};

				PropertyDescriptor[] tmp;
				if (!_absolute)
				{
					tmp = new PropertyDescriptor[res.length+2];
					System.arraycopy(res, 0, tmp, 0, res.length);
					tmp[res.length] = displayProp("Origin", "Time Origin", "the Time Origin for the time display", FORMAT);
					tmp[res.length+1] = displayLongProp("Format", "Time format", "the time format",
							RelativeTimeFormatPropertyEditor.class, FORMAT);
				} 
				else 
				{
					tmp = new PropertyDescriptor[res.length+1];
					System.arraycopy(res, 0, tmp, 0, res.length);
					tmp[res.length] = displayLongProp("Format", "Time format", "the time format",
							MyDateFormatPropertyEditor.class, FORMAT);
				}
				return tmp;
			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class TimeDisplayPainterTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public TimeDisplayPainterTest(final String val)
		{
			super(val);
		}

		public void testMyParams()
		{
			MWC.GUI.Editable ed = new TimeDisplayPainter();
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

	public int compareTo(final Plottable arg0)
	{
		final Plottable other = (Plottable) arg0;
		return this.getName().compareTo(other.getName());
	}

	public String getPrefix()
	{
		return _prefix;
	}

	public void setPrefix(String prefix)
	{
		this._prefix = prefix;
	}

	public String getSuffix()
	{
		return _suffix;
	}

	public void setSuffix(String suffix)
	{
		this._suffix = suffix;
	}

	public String getFormat()
	{
		return _format;
	}

	public void setFormat(String format)
	{
		this._format = format;
	}

	public HiResDate getDTG()
	{
		return _DTG;
	}

	public void setDTG(HiResDate DTG)
	{
		this._DTG = DTG;
	}

	@Override
	public void paint(CanvasType dest, long time)
	{
		_DTG = new HiResDate(time);
		if (_origin == null && !_absolute)
		{
			_origin = _DTG != null ? _DTG : new HiResDate();
		}
		paint(dest);
	}

	public boolean isFillBackground()
	{
		return _fillBackground;
	}

	public void setFillBackground(boolean fillBackground)
	{
		this._fillBackground = fillBackground;
	}

	public boolean isAbsolute()
	{
		return _absolute;
	}

	public void setAbsolute(boolean absolute)
	{
		this._absolute = absolute;
	}

	public HiResDate getOrigin()
	{
		return _absolute ? null : _origin;
	}

	public void setOrigin(HiResDate origin)
	{
		this._origin = origin;
	}
	
	public static final class RelativeTimeFormatPropertyEditor extends PropertyEditorSupport
  {

    String current;

    public final String[] getTags()
    {
    	return new String[] {MILLIS, SECS, MINS, HOURS, DAYS};
    }

    public final Object getValue()
    {
      return current;
    }

    public final void setValue(final Object p1)
    {
      if (p1 instanceof String)
      {
        final String val = (String) p1;
        setAsText(val);
      }
    }

    public final void setAsText(final String p1)
    {
      current = p1;
    }

    public final String getAsText()
    {
      return current;
    }
  }

}
