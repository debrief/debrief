package org.mwc.cmap.naturalearth.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mwc.cmap.naturalearth.data.CachedNauticalEarthFile;
import org.opengis.feature.Property;

import MWC.GenericData.NamedWorldPath;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;

import com.bbn.openmap.layer.shape.ESRIPointRecord;
import com.bbn.openmap.layer.shape.ESRIPoly;
import com.bbn.openmap.layer.shape.ESRIPoly.ESRIFloatPoly;
import com.bbn.openmap.layer.shape.ESRIPolygonRecord;
import com.bbn.openmap.layer.shape.ESRIRecord;
import com.bbn.openmap.layer.shape.ShapeFile;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;

public class ShapeFileTests
{

	private static File boundaryLines;
	private static File marineAreas;
	private static File pointNames;
	private static File landAreas;

	@BeforeClass
	public static void setUp() throws Exception
	{
		boundaryLines = new File(
				"data/ne_10m_admin_0_boundary_lines_land/ne_10m_admin_0_boundary_lines_land.shp");
		marineAreas = new File(
				"data/ne_10m_geography_marine_polys/ne_10m_geography_marine_polys.shp");
		landAreas = new File("data/ne_10m_land/ne_10m_land.shp");
		pointNames = new File(
				"data/ne_10m_geography_regions_points/ne_10m_geography_regions_points.shp");
	}

	@Test
	public void loadPolygons()
	{
		// check we have the marine areas
		assertTrue(marineAreas.getAbsolutePath() + " doesn't exists.",
				marineAreas.isFile());

		// ok, load it
		CachedNauticalEarthFile csf = new CachedNauticalEarthFile(
				marineAreas.getAbsolutePath());
		csf.init();

		assertNotNull("found polygons", csf.getPolygons());
		assertEquals("loaded polygons", 312, csf.getPolygons().size());
	}

	@Test
	public void loadPoints()
	{
		// check we have the marine areas
		assertTrue(pointNames.getAbsolutePath() + " doesn't exists.",
				pointNames.isFile());

		// ok, load it
		CachedNauticalEarthFile csf = new CachedNauticalEarthFile(
				pointNames.getAbsolutePath());
		csf.init();

		assertNotNull("found points", csf.getPoints());
		assertEquals("loaded points", 235, csf.getPoints().size());
	}

	@Test
	public void loadLines()
	{
		// check we have the marine areas
		assertTrue(boundaryLines.getAbsolutePath() + " doesn't exists.",
				boundaryLines.isFile());

		// ok, load it
		CachedNauticalEarthFile csf = new CachedNauticalEarthFile(
				boundaryLines.getAbsolutePath());
		csf.init();

		assertNotNull("found lines", csf.getLines());
		assertEquals("loaded lines", 461, csf.getLines().size());
	}

	@Test
	public void testExists()
	{
		assertTrue(boundaryLines.getAbsolutePath() + " doesn't exists.",
				boundaryLines.isFile());
		assertTrue(marineAreas.getAbsolutePath() + " doesn't exists.",
				marineAreas.isFile());
		assertTrue(pointNames.getAbsolutePath() + " doesn't exists.",
				pointNames.isFile());
	}

	public void unTestGeoToolsPerformance() throws Exception
	{
		String filename = landAreas.getAbsolutePath();

		ShapefileDataStore store;
		ArrayList<NamedWorldPath> polygons = new ArrayList<NamedWorldPath>();

		final File openFile = new File(filename);
		store = new ShapefileDataStore(openFile.toURI().toURL());
		store.setIndexCreationEnabled(false);
		ContentFeatureSource featureSource = store.getFeatureSource();

		// hey, can we parse it?
		// x1, y1, x2, y2
//		Filter filter = CQL.toFilter("BBOX(the_geom, -1.2,60,2.3,64)");
//		final SimpleFeatureCollection fs = featureSource.getFeatures(filter);
		 final SimpleFeatureCollection fs = featureSource.getFeatures();

		String fType = fs.getSchema().getSuper().getName().getLocalPart()
				.toString();
		if (fType.equals("polygonFeature"))
		{
			polygons = gtLoadPolygons(fs.features());

			System.out.println("loaded " + polygons.size() + " polygons");
		}

	}

	private static ArrayList<NamedWorldPath> gtLoadPolygons(
			SimpleFeatureIterator features)
	{
		int ctr = 0;
		ArrayList<NamedWorldPath> res = new ArrayList<NamedWorldPath>();
		while (features.hasNext())
		{
			// get ready to load this feature
			WorldPath path = null;
			String name = null;
			
			System.out.println("in " + ctr);

			final SimpleFeatureImpl thisF = (SimpleFeatureImpl) features.next();
			final Collection<? extends Property> values = thisF.getValue();
			final Iterator<? extends Property> iter = values.iterator();
			while (iter.hasNext())
			{
				final Property thisProp = iter.next();
				final String propName = thisProp.getName().toString();

				// is this the geometry?
				if (propName.equals("the_geom"))
				{
					path = gtGetPolygon(thisProp.getValue());
				}
				else if (propName.equals("name"))
				{
					name = thisProp.getValue().toString();
				}
			}

			ctr++;
			System.out.println("handling poly #:" + ctr);
			
			// are we done?
			if ((path != null))
			{
				// do we have name?
				System.out.println("path=" + path + " ,bounds=" + path.getBounds());
				if (name == null)
					name = "" + ctr;

				NamedWorldPath nwa = new NamedWorldPath(path);
				nwa.setName(name);

				System.out.println("nwa=" + nwa + " ,name=" + nwa.getName() 
						+ " ,bounds=" + nwa.getBounds());

				res.add(nwa);
			}
		}

		System.out.println("handled:" + ctr + " polygons");

		return res;
	}

	private static WorldPath gtGetPolygon(final Object value)
	{
		WorldPath res = null;
		if (value instanceof MultiPolygon)
		{
			final MultiPolygon mp = (MultiPolygon) value;
			Coordinate[] coords = mp.getBoundary().getCoordinates();
			if (coords != null)
			{
				WorldLocation[] wls = new WorldLocation[coords.length];
				for (int i = 0; i < coords.length; i++)
				{
					final Coordinate coordinate = coords[i];
					final double zDepth;
					if (Double.isNaN(coordinate.z))
						zDepth = 0;
					else
						zDepth = coordinate.z;
					wls[i] = new WorldLocation(coordinate.y,
							coordinate.x, zDepth);
					//res.addPoint(newL);
				}
				res = new WorldPath(wls);
			}

		}
		return res;
	}

	public void unTestOpenMapPerformance()
	{
		File file = landAreas;
		if (!file.exists())
			return;

		try
		{
			ShapeFile sf = new ShapeFile(file);
			System.out.println("\tmin: " + sf.getBoundingBox().min);
			System.out.println("\tmax: " + sf.getBoundingBox().max);

			int nRecords = 0;
			ESRIRecord record = sf.getNextRecord();
			while (record != null)
			{
				if (record instanceof ESRIPointRecord)
				{
					// TODO WorldLocation
					double lat = ((ESRIPointRecord) record).getY();
					double lon = ((ESRIPointRecord) record).getX();
					System.out.println("point record: " + lat + ", " + lon);
				}
				else if (record instanceof ESRIPolygonRecord)
				{
					ESRIPolygonRecord polygon = (ESRIPolygonRecord) record;
					ArrayList<WorldPath> polyList = readPolygon(polygon);
					System.out.println("found " + polyList.size() + " polys");
				}
				else
				{
					System.out.println("record: " + record.getClass().getName());
				}
				nRecords++;
				record = sf.getNextRecord();
			}
			System.out.println("records: " + nRecords);
		}
		catch (IOException e)
		{
			Activator.log(e);
			assertTrue("invalid file " + file.getAbsolutePath(), false);
		}
	}

	private ArrayList<WorldPath> readPolygon(ESRIPolygonRecord record)
	{
		ArrayList<WorldPath> polyList = new ArrayList<WorldPath>();

		ESRIPoly[] polygons = record.polygons;
		int nPolys = polygons.length;
		if (nPolys <= 0)
			return null;

		for (int i = 0; i < nPolys; i++)
		{
			// these points are in RADIAN lat,lon order!...
			ESRIPoly thisPoly = polygons[i];
			ESRIFloatPoly floatP = (ESRIFloatPoly) thisPoly;
			double[] pts = floatP.getRadians();
			int nPoints = polygons[i].nPoints;
			System.out.println("record: " + record.recordNumber);
			System.out.println("nPoints: " + nPoints);

			WorldPath wp = new WorldPath();

			for (int j = 0; j < pts.length; j++)
			{
				WorldLocation newLoc = new WorldLocation(pts[j], pts[++j], 0);
				wp.addPoint(newLoc);
			}

			polyList.add(wp);

			// FIXME WorldPath
			// System.out.print("points (rad): ");
			// for (int j = 0; j < pts.length; j++)
			// {
			// System.out.print("(" + pts[j] + "," + pts[++j] + ") ");
			// }
			// System.out.println();

		}

		return polyList;
	}

}
