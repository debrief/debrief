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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.GMTDateFormat;

/**
 */
public class SATCSampleLoader extends CoreLoader
{

  public SATCSampleLoader()
  {
    super("SATC Sample", null);
  }

  private static class ImportSATCSample
  {

    private final Layers _theLayers;
    private DateFormat _df;

    public ImportSATCSample(final Layers theLayers)
    {
      _theLayers = theLayers;
      _df = new GMTDateFormat("dd HH:mm:ss.SSS");
    }

    public void importThis(final String path, final InputStream inputStream)
        throws IOException, ParseException
    {
      TrackWrapper track = null;

      Enumeration<Editable> numer = _theLayers.elements();
      while (numer.hasMoreElements())
      {
        Editable editable = (Editable) numer.nextElement();
        if (editable instanceof TrackWrapper)
        {
          track = (TrackWrapper) editable;
          break;
        }
      }

      if (track == null)
      {
        return;
      }

      // trim the filename
      File file = new File(path);
      String fileName = file.getName();

      // ok, loop through the lines
      final BufferedReader br = new BufferedReader(new InputStreamReader(
          inputStream));

      String nmea_sentence;

      // come up with a color
      final float hue = (float) (Math.random() * 1000f);
      final Color thisCol = new Color(Color.HSBtoRGB(hue, 0.8f, 0.7f));

      SensorWrapper sw = new SensorWrapper(fileName);
      sw.setColor(thisCol);

      // loop through the lines
      while ((nmea_sentence = br.readLine()) != null)
      {
        // ok, we wrap this parsing in a try block, so we carry on
        // processing a file after a failure
        try
        {

          // check this isn't a comment
          if (nmea_sentence.startsWith("#Time") || nmea_sentence.startsWith(
              "DD HH"))
          {
            // ok, skip
            continue;
          }

          String[] fields = nmea_sentence.split("\\s*,\\s*");

          if (fields.length > 2)
          {
            // date
            HiResDate dt = dateFor(fields[0]);

            // bearing
            String trackNum = fields[1];
            double brgDegs = Double.valueOf(fields[2]);

            // public SensorContactWrapper(final String trackName, final HiResDate dtg,
            // final WorldDistance range, final Double bearingDegs,
            // final WorldLocation origin, final java.awt.Color color,
            // final String label, final int style, final String sensorName)

            SensorContactWrapper scw = new SensorContactWrapper(track.getName(),
                dt, null, brgDegs, null, thisCol, trackNum, 1, fileName);
            sw.add(scw);
          }

        }
        catch (ParseException pe)
        {
          pe.printStackTrace();
        }
      }

      track.add(sw);
    }

    @SuppressWarnings("deprecation")
    private HiResDate dateFor(String string) throws ParseException
    {
      Date dt = _df.parse(string);

      // check the day
      int day = Integer.parseInt(string.substring(0, 2));

      // fill in the month & year
      if (day == 31)
      {
        dt.setYear(99);
        dt.setMonth(11);
      }
      else
      {
        dt.setYear(100);
        dt.setMonth(00);
      }

      HiResDate res = new HiResDate(dt);
      return res;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer
   * .editors.CorePlotEditor, org.eclipse.ui.IEditorInput)
   */
  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final Layers layers, final InputStream inputStream, final String fileName)
  {

    // ok, we'll need somewhere to put the data
    return new IRunnableWithProgress()
    {
      @Override
      public void run(final IProgressMonitor pm)

      {
        // ok - get loading going
        ImportSATCSample importer = new ImportSATCSample(layers);
        try
        {
          importer.importThis(fileName, inputStream);
        }
        catch (IOException | ParseException e)
        {
          DebriefPlugin.logError(Status.ERROR, "Problem loading " + _fileType
              + ":" + fileName, e);
        }
      }
    };
  }
}
