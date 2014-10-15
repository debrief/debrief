/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
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
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GenericData.WorldLocation;

public class AreaFeature extends LineFeature
{

	public AreaFeature(final String name, final Double minScale, final Color defaultColor)
	{
		super(name, minScale, defaultColor);
	}

	public void doPaint(final CanvasType dest)
	{
		dest.setColor(getColor());
		for (final Iterator<Vector<WorldLocation>> iterator = _lines.iterator(); iterator.hasNext();)
		{
			final Vector<WorldLocation> thisLine = (Vector<WorldLocation>) iterator.next();

			final int npts = thisLine.size();
			final int[] xpts = new int[npts];
			final int[] ypts = new int[npts];

			int ctr = 0;

			for (final Iterator<WorldLocation> iter = thisLine.iterator(); iter.hasNext();)
			{
				final WorldLocation loc = (WorldLocation) iter.next();

				final Point pt = new Point(dest.toScreen(loc));
				xpts[ctr] = pt.x;
				ypts[ctr] = pt.y;
				ctr++;
			}
			// and plot it.
			dest.fillPolygon(xpts, ypts, npts);
		}
	}
}
