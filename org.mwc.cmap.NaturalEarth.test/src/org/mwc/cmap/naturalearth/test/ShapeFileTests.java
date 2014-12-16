package org.mwc.cmap.naturalearth.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mwc.cmap.gt2plot.data.CachedNauticalEarthFile;

import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;

import com.bbn.openmap.layer.shape.ESRIPointRecord;
import com.bbn.openmap.layer.shape.ESRIPoly;
import com.bbn.openmap.layer.shape.ESRIPoly.ESRIFloatPoly;
import com.bbn.openmap.layer.shape.ESRIPolygonRecord;
import com.bbn.openmap.layer.shape.ESRIRecord;
import com.bbn.openmap.layer.shape.ShapeFile;

public class ShapeFileTests
{

	private static File boundaryLines;
	private static File marineAreas;
	private static File pointNames;

	@BeforeClass
	public static void setUp() throws Exception
	{
		boundaryLines = new File("data/ne_10m_admin_0_boundary_lines_land/ne_10m_admin_0_boundary_lines_land.shp");
		marineAreas = new File("data/ne_10m_geography_marine_polys/ne_10m_geography_marine_polys.shp");
		pointNames = new File("data/ne_10m_geography_regions_points/ne_10m_geography_regions_points.shp");
	}

	@Test
	public void loadPolygons()
	{
		// check we have the marine areas
		assertTrue(marineAreas.getAbsolutePath() + " doesn't exists.", marineAreas.isFile());
		 
		// ok, load it 
		CachedNauticalEarthFile csf = new CachedNauticalEarthFile(marineAreas.getAbsolutePath());
		
		assertNotNull("found polygons", csf.getPolygons());
		assertEquals("loaded polygons", 312, csf.getPolygons().size());
	}
	
	@Test
	public void loadPoints()
	{
		// check we have the marine areas
		assertTrue(pointNames.getAbsolutePath() + " doesn't exists.", pointNames.isFile());
		 
		// ok, load it 
		CachedNauticalEarthFile csf = new CachedNauticalEarthFile(pointNames.getAbsolutePath());
		
		assertNotNull("found points", csf.getPoints());
		assertEquals("loaded points", 235, csf.getPoints().size());
	}

	
	@Test
	public void loadLines()
	{
		// check we have the marine areas
		assertTrue(boundaryLines.getAbsolutePath() + " doesn't exists.", boundaryLines.isFile());
		 
		// ok, load it 
		CachedNauticalEarthFile csf = new CachedNauticalEarthFile(boundaryLines.getAbsolutePath());
		
		assertNotNull("found lines", csf.getLines());
		assertEquals("loaded lines", 113, csf.getLines().size());
	}
	
	@Test
	public void testExists()
	{
		assertTrue(boundaryLines.getAbsolutePath() + " doesn't exists.", boundaryLines.isFile());
		assertTrue(marineAreas.getAbsolutePath() + " doesn't exists.", marineAreas.isFile());
		assertTrue(pointNames.getAbsolutePath() + " doesn't exists.", pointNames.isFile());
	}

}
