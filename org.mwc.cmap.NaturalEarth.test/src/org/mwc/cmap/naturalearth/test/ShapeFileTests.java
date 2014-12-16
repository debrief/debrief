package org.mwc.cmap.naturalearth.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mwc.cmap.naturalearth.wrapper.CachedShapefile;

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

	private static File coastline;
	private static File marineAreas;

	@BeforeClass
	public static void setUp() throws Exception
	{
		// Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		// URL fileURL = FileLocator.resolve(
		// bundle.getEntry());
		// String zipLocation = fileURL.getFile();
		coastline = new File("data/ne_50m_coastline/ne_50m_coastline.shp");
		marineAreas = new File("data/ne_10m_geography_marine_polys/ne_10m_geography_marine_polys.shp");
		// String dest = Platform.getConfigurationLocation().getURL().getFile();
		// destination = new File(dest, "data");
		// extract(file, destination, new NullProgressMonitor());
	}

	@Test
	public void loadMultiFormatFile()
	{
		// check we have the marine areas
		assertTrue(marineAreas.getAbsolutePath() + " doesn't exists.", marineAreas.isFile());
		 
		// ok, load it 
		CachedShapefile csf = new CachedShapefile(marineAreas.getAbsolutePath());
		
		assertNotNull("found polygons", csf.getPolygons());
		assertEquals("loaded polygons", 1472, csf.getPolygons().size());
	}
	
	@Test
	public void testExists()
	{
		assertTrue(coastline.getAbsolutePath() + " doesn't exists.", coastline.isFile());
		assertTrue(marineAreas.getAbsolutePath() + " doesn't exists.", marineAreas.isFile());
	}

	@Test
	public void testVerify()
	{
		File file = coastline;
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
//			System.out.println("record: " + record.recordNumber);
//			System.out.println("nPoints: " + nPoints);
			
			WorldPath wp = new WorldPath();
			
			for (int j = 0; j < pts.length; j++)
			{
				double d = pts[j];
				wp.addPoint(new WorldLocation(pts[j],pts[++j], 0));				
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


	//
	// public static boolean extract(File file, File destination,
	// IProgressMonitor monitor)
	// {
	// ZipFile zipFile = null;
	// destination.mkdirs();
	// try
	// {
	// zipFile = new ZipFile(file);
	// Enumeration<? extends ZipEntry> entries = zipFile.entries();
	// while (entries.hasMoreElements())
	// {
	// if (monitor.isCanceled())
	// {
	// return false;
	// }
	// ZipEntry entry = (ZipEntry) entries.nextElement();
	// if (entry.isDirectory())
	// {
	// monitor.setTaskName("Extracting " + entry.getName());
	// File dir = new File(destination, entry.getName());
	// dir.mkdirs();
	// continue;
	// }
	// monitor.setTaskName("Extracting " + entry.getName());
	// File entryFile = new File(destination, entry.getName());
	// entryFile.getParentFile().mkdirs();
	// InputStream input = null;
	// OutputStream output = null;
	// try
	// {
	// input = zipFile.getInputStream(entry);
	// output = new FileOutputStream(entryFile);
	// copyFile(input, output);
	// }
	// finally
	// {
	// if (input != null)
	// {
	// try
	// {
	// input.close();
	// }
	// catch (Exception e)
	// {
	// }
	// }
	// if (output != null)
	// {
	// try
	// {
	// output.close();
	// }
	// catch (Exception e)
	// {
	// }
	// }
	// }
	// }
	// }
	// catch (IOException e)
	// {
	// Activator.log(e);
	// return false;
	// }
	// finally
	// {
	// if (zipFile != null)
	// {
	// try
	// {
	// zipFile.close();
	// }
	// catch (IOException e)
	// {
	// // ignore
	// }
	// }
	// }
	// return true;
	// }
	//
	// public static void copyFile(InputStream in, OutputStream out)
	// throws IOException
	// {
	// byte[] buffer = new byte[16 * 1024];
	// int len;
	// while ((len = in.read(buffer)) >= 0)
	// {
	// out.write(buffer, 0, len);
	// }
	// }

}
