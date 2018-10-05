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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.mwc.cmap.core.CorePlugin;
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
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import junit.framework.TestCase;

/**
 * @author ian.mayo
 */
public class UK_CSV_Loader extends IPlotLoader.BaseLoader
{

  public final static class TestLogImport extends TestCase implements CompleteListener
  {
    private IPlotLoader msgReceived;

    @Override
    public void complete(final IPlotLoader loader)
    {
      msgReceived = loader;
    }

    public void testGetName() throws ParseException
    {
      final String line =
          "22.1862861, -21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long s.  I'll 'll duplicate to get more content.\"\r\n";
      final String name = UK_CSV_Loader.getName(line);
      assertEquals("Correct name", "NELSON", name);
    }

    public void testImport() throws ParseException
    {

      final String testInput = "# UK TRACK EXCHANGE FORMAT, V1.0\r\n"
          + "# Lat,Long,DTG,UnitName,CaseNumber,Type,Flag,Sensor,MajorAxis,SemiMajorAxis,SemiMinorAxis,Course,Speed,Depth,Likelihood,Confidence,SuppliedBy,Provenance,InfoCutoffDate,Purpose,Classification,DistributionStatement\r\n"
          + "22.1862861, -21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long s.  I'll 'll duplicate to get more content.\"\r\n"
          + "22.1862722, -21.7008278,19951212T050100Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long more content. Quite a more content.\"\r\n"
          + "22.1862528, -21.7041139,19951212T050200Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.9000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long sentuite a cate to get more content.\"\r\n"
          + "22.1862528, -21.707575,19951212T050300Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,268.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long senttent. Quite a long sentence decontent.\"";

      final InputStream stream = new java.io.ByteArrayInputStream(testInput
          .getBytes(Charset.forName("UTF-8")));

      final Layers layers = new Layers();
      final UK_CSV_Loader loader = new UK_CSV_Loader();

      assertEquals("layers empty", 0, layers.size());
      assertNull("message not sent, yet", msgReceived);

      UK_CSV_Loader.doImport(stream, "test_file.csv", this, layers, loader);

      assertNotNull("message set", msgReceived);

      assertEquals("layers not empty", 1, layers.size());

      final TrackWrapper track = (TrackWrapper) layers.findLayer("NELSON");
      assertNotNull("found track", track);

      assertEquals("all points", 4, track.numFixes());
    }
    
    boolean tripped = false;
    
    public void testSingleLineBad1() throws ParseException 
    {
      final String line =
          "22.18a62861, -21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long s.  I'll 'll duplicate to get more content.\"\r\n";
      
      FixWrapper fix = null;
      try
      {
        fix = UK_CSV_Loader.readLine(line);
      }
      catch (NumberFormatException e)
      {
        tripped = true;
      }
      assertNull("didn't manage to produce fix", fix);
      assertTrue("parse exception thrown", tripped);
    }
    
    public void testSingleLineBad2() throws ParseException 
    {
      final String line =
          "22.1862861, -21.6978806,19951212T050000Z";
      
      tripped = false;
      FixWrapper fix = null;
      try
      {
        fix = UK_CSV_Loader.readLine(line);
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
        tripped = true;
      }
      assertNull("didn't manage to produce fix", fix);
      assertTrue("parse exception thrown", tripped);
    }

    public void testSingleLine() throws ParseException
    {
      final String line =
          "22.1862861, -21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long s.  I'll 'll duplicate to get more content.\"\r\n";
      final FixWrapper fix = UK_CSV_Loader.readLine(line);
      assertNotNull(fix);

      // test the params
      assertEquals("lat", 22.1862861, fix.getLocation().getLat());
      assertEquals("lon", -21.6978806, fix.getLocation().getLong());
      assertEquals("depth", 0d, fix.getLocation().getDepth());
      assertEquals("date", 818744400000L, fix.getDTG().getDate().getTime());
      assertEquals("course", 269.7, fix.getCourseDegs());
      assertEquals("speed", new WorldSpeed(2, WorldSpeed.M_sec).getValueIn(
          WorldSpeed.Kts), fix.getSpeed(), 0.001);
    }
  }

  private static TrackWrapper createParentTrack(final Layers theLayers,
      final String thisLine) throws ParseException
  {
    TrackWrapper tw;
    String trackName = getName(thisLine);

    final Layer matchingLayer = theLayers.findLayer(trackName);

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
    return tw;
  }

  private static Date dateFor(final String date) throws ParseException
  {
    final DateFormat sdf = new GMTDateFormat("yyyyMMdd'T'HHmmss'Z'",
        Locale.ENGLISH);
    return sdf.parse(date);
  }

  private static void doImport(final InputStream inputStream,
      final String fileName, final CompleteListener listener,
      final Layers theLayers, final IPlotLoader finalLoader)
  {
    // right, better suspend the LayerManager extended updates from
    // firing
    theLayers.suspendFiringExtended(true);

    try
    {
      // ok, go for it.
      importThis(theLayers, fileName, inputStream);

      // and inform the plot editor
      listener.complete(finalLoader);
    }
    catch (final RuntimeException e)
    {
      CorePlugin.showMessage("Import CSV Track file", "Failed to read:" + fileName + ". Please see error log.");
      DebriefPlugin.logError(IStatus.ERROR, "Problem loading log:" + fileName,
          e);
    }
    finally
    {
      // ok, allow the layers object to inform anybody what's
      // happening
      // again
      theLayers.suspendFiringExtended(false);
    }
  }

  private static String getName(final String thisLine) throws ParseException
  {
    // ok, segment the line
    final String[] blocks = thisLine.split(",");
    final String name = blocks[3];
    return name;
  }

  /**
   * import data from this stream
   *
   * @param theLayers
   */
  private final static void importThis(final Layers theLayers,
      final String fName, final java.io.InputStream is)
  {
    // declare linecounter
    int lineCounter = 0;
    TrackWrapper tw = null;
    final Reader reader = new InputStreamReader(is);
    final BufferedReader br = new BufferedReader(reader);
    try
    {
      if (is.available() > 0)
      {

        // ok, we know we have a header line, so skip it.
        br.readLine();
        br.readLine();

        final long start = System.currentTimeMillis();

        // loop through the lines
        while (br.read() != -1)
        {
          final String thisLine = br.readLine();

          // keep line counter (use it for error reporting)
          lineCounter++;

          // catch import problems
          final FixWrapper fw = readLine(thisLine);

          if (fw != null)
          {
            // ok, add the fix.

            // have we created a parent track yet?
            if (tw == null)
            {
              // sort out the track
              tw = createParentTrack(theLayers, thisLine);

              // store it
              theLayers.addThisLayer(tw);
            }

            // now add the fix
            tw.addFix(fw);
          }
        }
        final long end = System.currentTimeMillis();
        System.out.print(" |Elapsed:" + (end - start) + " ");
      }
    }
    catch (final IOException fe)
    {
      DebriefPlugin.logError(IStatus.INFO, "Trouble creating input stream for "
          + fName, fe);
    }
    catch (final ParseException e)
    {
      DebriefPlugin.logError(IStatus.INFO, "Date parse exception reading line "
          + lineCounter + " in " + fName, e);
    }
    catch (final NumberFormatException e)
    {
      CorePlugin.showMessage("Import CSV", "Incorrectly formatter number at line " + lineCounter);
      DebriefPlugin.logError(IStatus.INFO,
          "Number format exception reading line " + lineCounter + " in "
              + fName, e);
    }
    finally
    {
      try
      {
        br.close();
      }
      catch (final IOException e)
      {
        DebriefPlugin.logError(IStatus.INFO, "Buffer close problem:" + fName,
            e);
      }
      try
      {
        is.close();
      }
      catch (final IOException e)
      {
        DebriefPlugin.logError(IStatus.INFO, "Buffer close problem:" + fName,
            e);
      }
    }
  }

  private static FixWrapper readLine(final String thisLine)
      throws ParseException, NumberFormatException
  {
    // sample line
    // final String line = "22.1862861,
    // -21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT
    // ALPHA,NELSON,19951212,For planning,PUBLIC,\"Quite a long s. I'll 'll duplicate to get more
    // content.\"\r\n";

    // ok, segment the line
    final String[] blocks = thisLine.split(",");
    final Date date = dateFor(blocks[2]);
    final double lat = Double.parseDouble(blocks[0]);
    final double lon = Double.parseDouble(blocks[1]);
    final double courseDegs = Double.parseDouble(blocks[11]);
    final double speedM_Sec = Double.parseDouble(blocks[12]);
    final WorldSpeed speed = new WorldSpeed(speedM_Sec, WorldSpeed.M_sec);

    final Fix theFix = new Fix(new HiResDate(date), new WorldLocation(lat, lon,
        0), Conversions.Degs2Rads(courseDegs), speed.getValueIn(
            WorldSpeed.ft_sec) / 3d);
    final FixWrapper res = new FixWrapper(theFix);

    // reset the name (to put the time in as a label)
    res.resetName();

    return res;
  }

  public UK_CSV_Loader()
  {
  }

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
          @Override
          public void run(final IProgressMonitor pm)
          {
            doImport(inputStream, fileName, listener, theLayers, finalLoader);
          }
        });
      }
    }
    catch (final InvocationTargetException e)
    {
      DebriefPlugin.logError(IStatus.ERROR, "Problem loading log:" + fileName,
          e);
    }
    catch (final InterruptedException e)
    {
      DebriefPlugin.logError(IStatus.ERROR, "Problem loading log:" + fileName,
          e);
    }
    catch (final IOException e)
    {
      DebriefPlugin.logError(IStatus.ERROR, "Problem loading log:" + fileName,
          e);
    }
    finally
    {
    }
    DebriefPlugin.logError(IStatus.INFO, "Successfully loaded .LOG file", null);
  }

}
