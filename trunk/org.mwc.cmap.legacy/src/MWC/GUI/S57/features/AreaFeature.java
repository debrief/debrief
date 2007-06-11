package MWC.GUI.S57.features;

import java.awt.*;
import java.util.*;

import MWC.GUI.CanvasType;
import MWC.GenericData.WorldLocation;

public class AreaFeature extends LineFeature
{

	public AreaFeature(String name, Double minScale, Color defaultColor)
	{
		super(name, minScale, defaultColor);
	}

	public void doPaint(CanvasType dest)
	{
		dest.setColor(getColor());
		for (Iterator<Vector<WorldLocation>> iterator = _lines.iterator(); iterator.hasNext();)
		{
			Vector<WorldLocation> thisLine = (Vector<WorldLocation>) iterator.next();

			int npts = thisLine.size();
			int[] xpts = new int[npts];
			int[] ypts = new int[npts];

			int ctr = 0;

			for (Iterator<WorldLocation> iter = thisLine.iterator(); iter.hasNext();)
			{
				WorldLocation loc = (WorldLocation) iter.next();

				Point pt = new Point(dest.toScreen(loc));
				xpts[ctr] = pt.x;
				ypts[ctr] = pt.y;
				ctr++;
			}
			// and plot it.
			dest.fillPolygon(xpts, ypts, npts);
		}
	}
}
