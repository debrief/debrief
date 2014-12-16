package org.mwc.cmap.naturalearth.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.bbn.openmap.layer.shape.ESRIPointRecord;
import com.bbn.openmap.layer.shape.ESRIPoly;
import com.bbn.openmap.layer.shape.ESRIPolygonRecord;
import com.bbn.openmap.layer.shape.ESRIRecord;
import com.bbn.openmap.layer.shape.ShapeFile;

public class ShapeFileTests
{

	private static File destination;

	@BeforeClass
	public static void setUp() throws Exception
	{
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		String zipLocation = FileLocator.resolve(
				bundle.getEntry("data/ne_50m_coastline.zip")).getFile();
		File file = new File(zipLocation);
		String dest = Platform.getConfigurationLocation().getURL().getFile();
		destination = new File(dest, "data");
		extract(file, destination, new NullProgressMonitor());
	}

	@Test
	public void testExists()
	{
		File file = getShapeFile();
		assertTrue(file.getAbsolutePath() + " doesn't exists.", file.isFile());
	}

	@Test
	public void testVerify()
	{
		File file = getShapeFile();
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
					readPolygon(polygon);
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

	private void readPolygon(ESRIPolygonRecord record)
	{

		ESRIPoly[] polygons = record.polygons;
		int nPolys = polygons.length;
		if (nPolys <= 0)
			return;
		
			for (int i = 0; i < nPolys; i++)
			{
				// these points are in RADIAN lat,lon order!...
				double[] pts = ((ESRIPoly.ESRIFloatPoly) polygons[i]).getRadians();
				int nPoints = polygons[i].nPoints;
				// p = new OMPoly(pts, OMGraphic.RADIANS, OMGraphic.LINETYPE_STRAIGHT);
				System.out.println("record: " + record.recordNumber);
				System.out.println("nPoints: " + nPoints);
				// FIXME WorldPath
//				System.out.print("points (rad): ");
//				for (int j = 0; j < pts.length; j++)
//				{
//					System.out.print("(" + pts[j] + "," + pts[++j] + ") ");
//				}
//				System.out.println();
			}	
	}
	
	private File getShapeFile()
	{
		File file = new File(destination, "ne_50m_coastline.shp");
		return file;
	}

	public static boolean extract(File file, File destination,
			IProgressMonitor monitor)
	{
		ZipFile zipFile = null;
		destination.mkdirs();
		try
		{
			zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements())
			{
				if (monitor.isCanceled())
				{
					return false;
				}
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (entry.isDirectory())
				{
					monitor.setTaskName("Extracting " + entry.getName());
					File dir = new File(destination, entry.getName());
					dir.mkdirs();
					continue;
				}
				monitor.setTaskName("Extracting " + entry.getName());
				File entryFile = new File(destination, entry.getName());
				entryFile.getParentFile().mkdirs();
				InputStream input = null;
				OutputStream output = null;
				try
				{
					input = zipFile.getInputStream(entry);
					output = new FileOutputStream(entryFile);
					copyFile(input, output);
				}
				finally
				{
					if (input != null)
					{
						try
						{
							input.close();
						}
						catch (Exception e)
						{
						}
					}
					if (output != null)
					{
						try
						{
							output.close();
						}
						catch (Exception e)
						{
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			Activator.log(e);
			return false;
		}
		finally
		{
			if (zipFile != null)
			{
				try
				{
					zipFile.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
		return true;
	}

	public static void copyFile(InputStream in, OutputStream out)
			throws IOException
	{
		byte[] buffer = new byte[16 * 1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
		{
			out.write(buffer, 0, len);
		}
	}

}
