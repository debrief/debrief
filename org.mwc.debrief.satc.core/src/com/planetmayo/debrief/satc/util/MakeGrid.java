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
package com.planetmayo.debrief.satc.util;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.relate.RelateOp;

public class MakeGrid
{

	/**
	 * produce a grid of points over the specified area
	 * 
	 * @param p_geom
	 *          the area to cover (typically a polygon)
	 * @param numPoints
	 *          how many points to generate (roughly, see below)
	 * @param p_precision
	 * @return
	 * @throws Exception
	 * 
	 *           we aren't able reliable produce the correct amount. If you need
	 *           an exact amount, the best I can suggest is to generate too many,
	 *           and remove some - either at random, or from one end of the list
	 * 
	 *           This is a streamlined version of a class file provided in the JTS
	 *           mailing list:
	 *           http://sourceforge.net/mailarchive/forum.php?thread_name
	 *           =op.wrvbzhkpzbn0g9
	 *           %40big-spdba.greener.local&forum_name=jts-topo-suite-user
	 * 
	 */
	public static ArrayList<Point> ST_Tile(final Geometry p_geom,
			final int numPoints, final int p_precision)
	{

		// how long should each side of the area be if it was regular
		final double area = p_geom.getArea();

		final double side_length = Math.sqrt(area);

		// how many points along each side would I want?
		final double lat_interval = Math.sqrt(numPoints);

		// work out what their spacing would be
		final double interval = side_length / lat_interval;

		String c_s_empty_geom = "Geometry must not be null or empty";
		String c_s_unsupported = "Unsupported geometry type (*GTYPE*)";
		double v_half_x = interval / 2.0, v_half_y = interval / 2.0;
		int v_loCol, v_hiCol, v_loRow, v_hiRow;
		Geometry v_mbr = null;
		Point v_geometry = null;
		Point v_clip_geom = null;
		Coordinate[] v_vertices = null;
		ArrayList<Point> grid = new ArrayList<Point>();

		if (p_geom == null || p_geom.isEmpty() || !p_geom.isValid())
		{
			throw new RuntimeException(c_s_empty_geom);
		}
		if (!(p_geom.getGeometryType().equalsIgnoreCase("Point")
				|| p_geom.getGeometryType().equalsIgnoreCase("LineString")
				|| p_geom.getGeometryType().equalsIgnoreCase("Polygon")
				|| p_geom.getGeometryType().equalsIgnoreCase("MultiPoint")
				|| p_geom.getGeometryType().equalsIgnoreCase("MultiLineString") || p_geom
				.getGeometryType().equalsIgnoreCase("MultiPolygon")))
		{
			throw new RuntimeException(c_s_unsupported.replace("GTYPE",
					p_geom.getGeometryType()));
		}

		PrecisionModel pm = new PrecisionModel();
		GeometryFactory gf = new GeometryFactory(pm, p_geom.getSRID());

		v_mbr = p_geom.getEnvelope();
		// Check for horizontal/vertical
		v_vertices = v_mbr.getCoordinates();
		// LL and UR are coord 0 and 2
		if (v_vertices[2].x - v_vertices[0].x < interval)
		{
			v_vertices[0].x = v_vertices[0].x - v_half_x;
			v_vertices[2].x = v_vertices[2].x + v_half_x;
		}
		if (v_vertices[2].y - v_vertices[0].y < interval)
		{
			v_vertices[0].y = v_vertices[0].y - v_half_y;
			v_vertices[2].y = v_vertices[2].y + v_half_y;
		}
		;
		v_loCol = (int) Math.floor(v_vertices[0].x / interval);
		v_loRow = (int) Math.floor(v_vertices[0].y / interval);
		v_hiCol = (int) Math.ceil(v_vertices[2].x / interval) - 1;
		v_hiRow = (int) Math.ceil(v_vertices[2].y / interval) - 1;
				
		for (int v_col = v_loCol; v_col <= v_hiCol; v_col++)
		{
			for (int v_row = v_loRow; v_row <= v_hiRow; v_row++)
			{
				v_geometry = gf.createPoint(new Coordinate((v_col * interval)
						+ v_half_x, (v_row * interval) + v_half_y));

				v_clip_geom = v_geometry;

				// note: I did trial just calling the contains() method or using
				// !disjoint, but the processing
				// further below has better performance
				// if(!p_geom.disjoint(v_geometry))
				// grid.add(v_clip_geom);

				RelateOp ro = new RelateOp(v_clip_geom, p_geom);
				IntersectionMatrix im = ro.getIntersectionMatrix();

				if (!im.isDisjoint())
				{
					grid.add(v_clip_geom);
				}

			} // row_iterator;
		} // col_iterator;
		return grid;
	}
}
