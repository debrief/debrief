package com.planetmayo.debrief.satc.model.generator;

import java.util.ArrayList;

import com.vividsolutions.jts.algorithm.BoundaryNodeRule;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.relate.RelateOp;

public class tesselate
{

	public static ArrayList<Geometry> ST_Tile(Geometry p_geom, double p_Tile_X,
			double p_Tile_Y, int p_precision) throws Exception
	{
		String c_s_empty_geom = "Geometry must not be null or empty";
		String c_s_unsupported = "Unsupported geometry type (*GTYPE*)";
		double v_half_x = p_Tile_X / 2.0, v_half_y = p_Tile_Y / 2.0;
		int v_loCol, v_hiCol, v_loRow, v_hiRow;
		Geometry v_mbr = null;
		Point v_geometry = null;
		Geometry v_clip_geom = null;
		Coordinate[] v_vertices = null;
		ArrayList<Geometry> grid = new ArrayList<Geometry>();

		if (p_geom == null || p_geom.isEmpty() || !p_geom.isValid())
		{
			throw new Exception(c_s_empty_geom);
		}
		if (!(p_geom.getGeometryType().equalsIgnoreCase("Point")
				|| p_geom.getGeometryType().equalsIgnoreCase("LineString")
				|| p_geom.getGeometryType().equalsIgnoreCase("Polygon")
				|| p_geom.getGeometryType().equalsIgnoreCase("MultiPoint")
				|| p_geom.getGeometryType().equalsIgnoreCase("MultiLineString") || p_geom
				.getGeometryType().equalsIgnoreCase("MultiPolygon")))
		{
			throw new Exception(c_s_unsupported.replace("GTYPE",
					p_geom.getGeometryType()));
		}
		
		PrecisionModel pm = new PrecisionModel(getPrecisionScale(p_precision));
		GeometryFactory gf = new GeometryFactory(pm, p_geom.getSRID());

		v_mbr = p_geom.getEnvelope();
		// Check for horizontal/vertical
		v_vertices = v_mbr.getCoordinates();
		// LL and UR are coord 0 and 2
		if (v_vertices[2].x - v_vertices[0].x < p_Tile_X)
		{
			v_vertices[0].x = v_vertices[0].x - v_half_x;
			v_vertices[2].x = v_vertices[2].x + v_half_x;
		}
		if (v_vertices[2].y - v_vertices[0].y < p_Tile_Y)
		{
			v_vertices[0].y = v_vertices[0].y - v_half_y;
			v_vertices[2].y = v_vertices[2].y + v_half_y;
		}
		;
		v_loCol = (int) Math.floor(v_vertices[0].x / p_Tile_X);
		v_loRow = (int) Math.floor(v_vertices[0].y / p_Tile_Y);
		v_hiCol = (int) Math.ceil(v_vertices[2].x / p_Tile_X) - 1;
		v_hiRow = (int) Math.ceil(v_vertices[2].y / p_Tile_Y) - 1;
		for (int v_col = v_loCol; v_col <= v_hiCol; v_col++)
		{
			for (int v_row = v_loRow; v_row <= v_hiRow; v_row++)
			{
				v_geometry = gf.createPoint(new Coordinate((v_col * p_Tile_X)
						+ v_half_x, (v_row * p_Tile_Y) + v_half_y));
				
				v_clip_geom = v_geometry;

//				if(!p_geom.disjoint(v_geometry))
//				grid.add(v_clip_geom);
				
				BoundaryNodeRule bnr = BoundaryNodeRule.MOD2_BOUNDARY_RULE;
				// default (OGC SFS) Boundary Node Rule.
				RelateOp ro = new RelateOp(v_clip_geom, p_geom, bnr);
				IntersectionMatrix im = ro.getIntersectionMatrix();

				if (!im.isDisjoint())
				{
					if (v_clip_geom != null)
					{
						grid.add(v_clip_geom);
					}
				}
				
			} // row_iterator;
		} // col_iterator;
		return grid;
	}

	public static double getPrecisionScale(int _numDecPlaces)
	{
		return _numDecPlaces < 0 ? (double) (1.0 / Math.pow(10,
				Math.abs(_numDecPlaces))) : (double) Math.pow(10, _numDecPlaces);
	}

}
