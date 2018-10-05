/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.interfaces.IPlotLoader;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import junit.framework.TestCase;

/**
 * @author ian.mayo
 */
public class UK_CSV_Loader extends IPlotLoader.BaseLoader
{

	public UK_CSV_Loader()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer
	 * .editors.CorePlotEditor, org.eclipse.ui.IEditorInput)
	 */
	@Override
  public void loadFile(final IAdaptable target, final InputStream inputStream,
      final String fileName, final CompleteListener listener)
	{
		final Layers theLayers = (Layers) target.getAdapter(Layers.class);
    final IPlotLoader finalLoader = this;

		try
		{

			// hmm, is there anything in the file?
			final int numAvailable = inputStream.available();
			if (numAvailable > 0)
			{

				final IWorkbench wb = PlatformUI.getWorkbench();
				final IProgressService ps = wb.getProgressService();
				ps.busyCursorWhile(new IRunnableWithProgress()
				{
					public void run(final IProgressMonitor pm)
					{
						// right, better suspend the LayerManager extended updates from
						// firing
						theLayers.suspendFiringExtended(true);

						try
						{
							DebriefPlugin.logError(Status.INFO, "about to start loading:"
									+ fileName, null);

							// quick check, is this a .log file
							if (fileName.toLowerCase().endsWith(".csv"))
							{
								// ok, go for it.
								importThis(theLayers, fileName, inputStream);
							}

							DebriefPlugin.logError(Status.INFO, "completed loading:"
									+ fileName, null);

							// and inform the plot editor
              listener.complete(finalLoader);

							DebriefPlugin.logError(Status.INFO, "parent plot informed", null);

						}
						catch (final RuntimeException e)
						{
							DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:"
									+ fileName, e);
						}
						finally
						{
							// ok, allow the layers object to inform anybody what's
							// happening
							// again
							theLayers.suspendFiringExtended(false);
						}
					}
				});

			}

		}
		catch (final InvocationTargetException e)
		{
			DebriefPlugin
					.logError(Status.ERROR, "Problem loading log:" + fileName, e);
		}
		catch (final InterruptedException e)
		{
			DebriefPlugin
					.logError(Status.ERROR, "Problem loading log:" + fileName, e);
		}
		catch (final IOException e)
		{
			DebriefPlugin
					.logError(Status.ERROR, "Problem loading log:" + fileName, e);
		}
		finally
		{
		}
		// ok, load the data...
		DebriefPlugin.logError(Status.INFO, "Successfully loaded .LOG file", null);
	}

	/**
	 * import data from this stream
	 * 
	 * @param theLayers
	 */
	public final void importThis(final Layers theLayers, final String fName,
			final java.io.InputStream is)
	{
		// declare linecounter
		int lineCounter = 0;

		TrackWrapper tw = null;

		final Reader reader = new InputStreamReader(is);
		final BufferedReader br = new BufferedReader(reader);
		String thisLine = null;

		// check stream is valid
		try
		{
			if (is.available() > 0)
			{

				// ok, we know we have a header line, so skip it.
				br.readLine();
				
				// ok, now the first real line
				thisLine = br.readLine();

				final long start = System.currentTimeMillis();

				// loop through the lines
				while (thisLine != null)
				{

					// keep line counter (use it for error reporting)
					lineCounter++;

					// catch import problems
					FixWrapper fw = readLine(thisLine);

					if (fw != null)
					{
						// ok, add the fix.

						// have we created a parent track yet?
						if (tw == null)
						{
							String trackName = filename(fName);

							Layer matchingLayer = theLayers.findLayer(trackName);

							// is this name already in use?
							if (matchingLayer != null)
							{
								// oops, there's already a track/layer with this name. create
								// another
								trackName = trackName + "_" + (int) (Math.random() * 1000);
							}

							// ok, create the track
							tw = new TrackWrapper();
							
							// give it the name
							tw.setName(trackName);
							
							// set the default color
							tw.setColor(DebriefColors.BLUE);
							
							// store it
							theLayers.addThisLayer(tw);
						}

						// now add the fix
						tw.addFix(fw);
					}

					// read another line
					thisLine = br.readLine();
				}

				final long end = System.currentTimeMillis();
				System.out.print(" |Elapsed:" + (end - start) + " ");

			}
		}
		catch (IOException fe)
		{
			DebriefPlugin.logError(Status.INFO, "Trouble creating input stream for "
					+ fName, fe);
		}
		catch (ParseException e)
		{
			DebriefPlugin.logError(Status.INFO, "Date parse exception reading line "
					+ lineCounter + " in " + fName, e);
		}
		catch (NumberFormatException e)
		{
			DebriefPlugin.logError(Status.INFO, "Number format exception reading line "
					+ lineCounter + " in " + fName, e);
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				DebriefPlugin.logError(Status.INFO, "Buffer close problem:" + fName, e);
			}
			try
			{
				is.close();
			}
			catch (IOException e)
			{
				DebriefPlugin.logError(Status.INFO, "Buffer close problem:" + fName, e);
			}
		}
	}

	private static String filename(String fullPath)
	{ // gets filename without extension
		int sep = fullPath.lastIndexOf(File.separator);
		int dot = fullPath.lastIndexOf(".");
		return fullPath.substring(sep + 1, dot);
	}

	private FixWrapper readLine(String thisLine) throws ParseException
	{
		// sample line
		// 23-08-2014 02 36 53,01-09-2014 00 00 12,31-08-2014 23 00
		// 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0

		// ok, segment the line
		String[] blocks = thisLine.split(",");
		Date date = dateFor(blocks[1]);
		double lat = Double.parseDouble(blocks[3]);
		double lon = Double.parseDouble(blocks[4]);
		double speedKts = Double.parseDouble(blocks[5]);
		double courseDegs = Double.parseDouble(blocks[6]);

		Fix theFix = new Fix(new HiResDate(date), new WorldLocation(lat, lon, 0),
				Conversions.Degs2Rads(courseDegs), Conversions.Kts2Yps(speedKts));
		FixWrapper res = new FixWrapper(theFix);

		// reset the name (to put the time in as a label)
		res.resetName();
		
		return res;
	}

	public Date dateFor(String date) throws ParseException
	{
		SimpleDateFormat sdf = new GMTDateFormat("dd-MM-yyyy HH mm ss");
		return sdf.parse(date);
	}

	public static class TestLogImport extends TestCase implements CompleteListener
	{
		public void testImport() throws ParseException
		{
		  
		  final String testInput = "# UK TRACK EXCHANGE FORMAT, V1.0\r\n" + 
		      "# Lat,Long,DTG,UnitName,CaseNumber,Type,Flag,Sensor,MajorAxis,SemiMajorAxis,SemiMinorAxis,Course,Speed,Depth,Likelihood,Confidence,SuppliedBy,Provenance,InfoCutoffDate,Purpose,Classification,DistributionStatement\r\n" + 
		      "22.1862861, -21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long s.  I'll 'll duplicate to get more content.\"\r\n" + 
		      "22.1862722, -21.7008278,19951212T050100Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long more content. Quite a more content.\"\r\n" + 
		      "22.1862528, -21.7041139,19951212T050200Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.9000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long sentuite a cate to get more content.\"\r\n" + 
		      "22.1862528, -21.707575,19951212T050300Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,268.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long senttent. Quite a long sentence decontent.\"";
		  
		  InputStream stream = new java.io.ByteArrayInputStream(testInput.getBytes(Charset.forName("UTF-8")));
		 
      UK_CSV_Loader loader = new UK_CSV_Loader();
      loader.loadFile(null, stream, "test_file.csv", this);
//
//		  // check the input
//		  
//			// check our understanding
//			String[] blocks = testLine.split(",");
//			assertEquals("correct num", 29, blocks.length);
//
//			// check the import, bit by bit
//			UK_CSV_Loader loader = new UK_CSV_Loader();
//
//			// and the date?
//			Date dt = loader.dateFor(blocks[0]);
//			assertEquals("correct date", "23-Aug-2014", DateFormat.getDateInstance()
//					.format(dt));
//			// note, 23rd Aug is summer, so daylight savings time is present in the
//			// next value
//			assertEquals("correct time", "03:36:53", DateFormat.getTimeInstance()
//					.format(dt));
//			assertEquals("correct millis", 1408761413000L, dt.getTime());
//
//			FixWrapper res = loader.readLine(testLine);
//			assertEquals("correct lat", "010913.14", FormatRNDateTime.toString(res.getDTG().getDate().getTime()));
//			assertEquals("correct lat", 48.5556666, res.getLocation().getLat(), 0.001);
//			assertEquals("correct lon", -9.359333, res.getLocation().getLong(), 0.001);
//			assertEquals("correct course", 318.8, res.getCourseDegs(), 0.001);
//			assertEquals("correct speed", 8.922246, res.getSpeed(), 0.001);
		}
		
		public void testDateImport() throws ParseException 
		{
			String testLine1 = "23-08-2014 02136 53,01-01-2014 11 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
			String testLine2 = "23-08-2014 02136 53,01-01-2014 12 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
			String testLine3 = "23-08-2014 02136 53,01-01-2014 13 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
			
			// check the import, bit by bit
			UK_CSV_Loader loader = new UK_CSV_Loader();
			
			Date d1 = loader.dateFor(testLine1.split(",")[1]);
			Date d2 = loader.dateFor(testLine2.split(",")[1]);
			Date d3 = loader.dateFor(testLine3.split(",")[1]);
			
			assertEquals("correct hours", "11:00:12", DateFormat.getTimeInstance()
					.format(d1));
			assertEquals("correct hours", "12:00:12", DateFormat.getTimeInstance()
					.format(d2));
			assertEquals("correct hours", "13:00:12", DateFormat.getTimeInstance()
					.format(d3));
		}

		
		public void testDaylightSavings() throws ParseException 
		{
			String testLine1 = "23-08-2014 02136 53,01-09-2014 11 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
			String testLine2 = "23-08-2014 02136 53,01-09-2014 12 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
			String testLine3 = "23-08-2014 02136 53,01-09-2014 13 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
			
			// check the import, bit by bit
			UK_CSV_Loader loader = new UK_CSV_Loader();
			
			Date d1 = loader.dateFor(testLine1.split(",")[1]);
			Date d2 = loader.dateFor(testLine2.split(",")[1]);
			Date d3 = loader.dateFor(testLine3.split(",")[1]);
			
			SimpleDateFormat sdf = new GMTDateFormat("HH:mm:ss");

			
			assertEquals("correct hours", "11:00:12", sdf.format(d1));
			assertEquals("correct hours", "12:00:12", sdf.format(d2));
			assertEquals("correct hours", "13:00:12", sdf.format(d3));
		}
		

		public void testDuffImport() throws ParseException
		{
			String testLine1 = "23-08-2014 02136 53,01-09-2014 00 00 12,31-08-2014 23 00 08,48.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";
			String testLine2 = "23-08-2014 02 36 53,01-09-2014 00 00 12,31-08-2014 23 00 08,48A.55566666666667,-9.359333333333334,8.922246514887131,318.8,9.349891895292325,319.2,303,48,-570.0,511.3,317.0,TA_DEPLOYED,70,64,-81.9,66.3,319.0,TB_DEPLOYED,1513.51,16.0,0.0,0.0,0.0,0.0,0.0,0.0";

			// check our understanding
			String[] blocks = testLine1.split(",");
			assertEquals("correct num", 29, blocks.length);

			// check the import, bit by bit
			UK_CSV_Loader loader = new UK_CSV_Loader();

			// and the date?
			boolean thrown = false;
			try{
			@SuppressWarnings("unused")
			Date dt = loader.dateFor(blocks[0]);
			}
			catch(ParseException pe)
			{
				thrown = true;
			}
			assertEquals("failed date parse", true, thrown);			

			thrown = false;
			try{
				@SuppressWarnings("unused")
				FixWrapper res = loader.readLine(testLine2);
			}
			catch(NumberFormatException pe)
			{
				thrown = true;
			}
			assertEquals("failed number parse", true, thrown);			
		}

    @Override
    public void complete(IPlotLoader loader)
    {
      // TODO Auto-generated method stub
      
    }
	}

}
