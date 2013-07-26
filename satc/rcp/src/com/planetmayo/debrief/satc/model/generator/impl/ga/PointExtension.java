package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.List;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MakeGrid;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class PointExtension
{
	private final Point point;
	private final BoundedState state;
	private double precision;
	
	public PointExtension(Point point, BoundedState state, double precision)
	{
		super();
		this.point = point;
		this.state = state;
		this.precision = precision;
	}
	
	public List<Point> extend() 
	{		
		if (precision < 10) 
		{
			return new ArrayList<Point>();
		}
		precision = precision / 2;
		double delta = GeoSupport.m2deg(precision) * 3;
		Geometry geometry = point.buffer(delta);
		geometry = geometry.intersection(state.getLocation().getGeometry());
		return MakeGrid.ST_Tile(geometry.intersection(state.getLocation().getGeometry()), 8, 6);
	}
}
