/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.S57.features;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;

public class LineFeature extends S57Feature
{
	private Color _myColor;
	protected Vector<Vector<WorldLocation>> _lines = new Vector<Vector<WorldLocation>>(
			0, 1);
	final public static Double DEFAULT_SCALE = 1000000d;

	public LineFeature(final String name, final Double minScale, final Color defaultColor)
	{
		super(name, minScale);
		_myColor = defaultColor;
	}

	/**
	 * add a new line
	 * 
	 * @param pts
	 */
	public void addLine(final Vector<WorldLocation> pts)
	{
		_lines.add(pts);
	}

	final EditorType createEditor()
	{
		return new LineFeatureInfo(this, getName());
	}

	public void doPaint(final CanvasType dest)
	{
		dest.setColor(_myColor);
		for (final Iterator<Vector<WorldLocation>> iterator = _lines.iterator(); iterator
				.hasNext();)
		{
			final Vector<WorldLocation> thisLine = (Vector<WorldLocation>) iterator.next();
			Point last = null;
			Point startPt = null;
			for (final Iterator<WorldLocation> iter = thisLine.iterator(); iter.hasNext();)
			{
				final WorldLocation loc = (WorldLocation) iter.next();

				// if(ctr > 46)
				// {
				// System.err.println("loc " + ctr + " is:" + loc + " x:" +
				// loc.getLong());
				// }

				final Point pt = new Point(dest.toScreen(loc));
				if (startPt == null)
					startPt = new Point(pt);
				if (last != null)
				{
					if (last.equals(pt))
					{
					}
					dest.drawLine(last.x, last.y, pt.x, pt.y);
					// dest.drawText(myFont, "" + ctr, last.x + xOffset, last.y +
					// yOffset);
				}
				last = pt;
			}
			// and close it.
			if ((last != null) && (startPt != null))
				dest.drawLine(last.x, last.y, startPt.x, startPt.y);
			// dest.setColor(Color.red);
			// yOffset += 10;
			// xOffset += 5;
			// break;
		}
	}

	/**
	 * @return the _myColor
	 */
	public final Color getColor()
	{
		return _myColor;
	}

	/**
	 * @param color
	 *          the _myColor to set
	 */
	public final void setColor(final Color color)
	{
		_myColor = color;
	}

	public class LineFeatureInfo extends Editable.EditorType
	{

		public LineFeatureInfo(final LineFeature data, final String theName)
		{
			super(data, theName, "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ prop("Color", "the color to plot this feature", SPATIAL) };
				return res;
			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}
}
