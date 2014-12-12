package org.mwc.cmap.naturalearth.model;

import com.vividsolutions.jts.geom.Point;

public class LabelObject
{
	final private Point _point;
	final private String _string;
	
	public LabelObject(final Point point, final String str)
	{
		_point = point;
		_string = str;
	}
}
