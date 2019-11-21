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
package Debrief.ReaderWriter.XML.csv_gz;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.LoggingService;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import junit.framework.TestCase;

public class Import_CSV_GZ
{

  private TrackWrapper trackFor(Layers layers, String trackName)
  {
    final TrackWrapper track;
    Layer layer = layers.findLayer(trackName);
    if(layer != null && layer instanceof TrackWrapper)
    {
      track = (TrackWrapper) layer;
    }
    else
    {
      final boolean needsRename;
      if(layer == null)
      {
        needsRename = false;
      }
      else
      {
        needsRename = true;
      }
    
      final String nameToUse;
      if(needsRename)
      {
        String suffix = "-" + (int)Math.random()*1000;
        nameToUse = trackName + suffix;
      }
      else
      {
        nameToUse = trackName;
      }
      
      track = new TrackWrapper();
      track.setName(nameToUse);

      // sort out a color
      // sort out a color
      final Color theCol =
          DebriefColors.RandomColorProvider.getRandomColor(colorCounter++);
      track.setColor(theCol);

      layers.addThisLayer(track);
    }
    return track;
  
  }
  
  private static final String CSV_DATE_FORMAT = "dd MMM yyyy - HH:mm:ss.SSS";

  public static class TestCSV_GZ_Import extends TestCase
  {
    public void testDateParse() throws ParseException
    {
      Import_CSV_GZ importer = new Import_CSV_GZ();
      final String test_date = "20 Nov 2019 - 11:22:33.000";
      Date date = importer.getDate(test_date);

      DateFormat df = new GMTDateFormat(CSV_DATE_FORMAT);
      assertEquals("matching date", test_date, df.format(date));
    }
    
    public void testOSD() throws ParseException
    {
      
//      attr_courseOverTheGround - rads
//      attr_depth - m
//      attr_latitude - rads
//      attr_longitude - rads
//      attr_speedOverTheGround - m/s
      
      List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("attr_courseOverTheGround");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI/2);
      tokens.add("attr_latitude");
      tokens.add("" + Math.PI/4);
      tokens.add("attr_depth");
      tokens.add("" + 22d);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");
      
      OSD_Importer importer = new OSD_Importer();
      FixWrapper res = importer.process(tokens.iterator());
      assertNotNull("should have fix", res);
      
    }
  }

  private final GMTDateFormat _formatter;
  
  private Date getDate(final String dateStr) throws ParseException
  {
    // 20 Nov 2019 - 11:22:33.000
    Date date = _formatter.parse(dateStr);
    return date;
  }
  
  protected HiResDate getHiResDate(final String dateStr) throws ParseException
  {
    return new HiResDate(getDate(dateStr).getTime());
  }
  
  protected Import_CSV_GZ()
  {
    _formatter = new GMTDateFormat(CSV_DATE_FORMAT);
  }
  

  /**
   * keep track of how many tracks we've created, so we can generate unique colors
   */
  private static int colorCounter = 0;

  private static TrackWrapper lastTrack = null;

  /**
   * create a fix using the supplied data
   * 
   * @param target
   * @param trackName
   * @param theDate
   * @param theLoc
   */
//  private static void addFix(final Layers target, final String trackName,
//      final HiResDate theDate, final WorldLocation theLoc,
//      final double courseDegs, final double speedKts)
//  {
//    // is this our current layer?
//    if (lastTrack  != null)
//    {
//      if (lastTrack.getName().equals(trackName))
//      {
//        // sorted
//      }
//      else
//      {
//        lastTrack = null;
//      }
//    }
//
//    if (lastTrack == null)
//    {
//      lastTrack = (TrackWrapper) target.findLayer(trackName);
//      if (lastTrack == null)
//      {
//        createTrack(target, trackName);
//      }
//    }
//
//    final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(courseDegs);
//    final double speedYPS =
//        new WorldSpeed(speedKts, WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec) / 3d;
//
//    final FixWrapper newFix =
//        new FixWrapper(new Fix(theDate, theLoc, courseRads, speedYPS));
//
//    // and reset the time value
//    newFix.resetName();
//
//    lastTrack.addFix(newFix);
//  }

//  /**
//   * extract the course element from the supplied string
//   * 
//   * @param descriptionTxt
//   * @return
//   * @throws ParseException
//   */
//  private static double courseFrom(final String descriptionTxt)
//      throws ParseException
//  {
//    double res = 0;
//    // STRING LOOKS LIKE
//    // <![CDATA[<b>RADAR PLOT 20:01:12 (GMT)</b><br><hr>Lat:
//    // 05.9696<br>Lon: 07.9633<br>Course: 253.0<br>Speed: 7.1
//    // knots<br>Date: September 12, 2009]]>
//    final int startI = descriptionTxt.indexOf("Course");
//    final int endI = descriptionTxt.indexOf("<br>Speed");
//    if ((startI > 0) && (endI > 0))
//    {
//      final String subStr = descriptionTxt.substring(startI + 7, endI - 1);
//      res = MWCXMLReader.readThisDouble(subStr.trim());
//    }
//    return res;
//  }



  protected static interface CSV_Importer
  {
    void doImport(Layers theLayers, List<CSVRecord> records);
  }
  
  private static class OSD_Importer extends Import_CSV_GZ implements CSV_Importer
  {


    public FixWrapper process(Iterator<String> tokens) throws ParseException
    {
      String dateStr = tokens.next();
      HiResDate date = getHiResDate(dateStr);
      System.out.println(date);
      return null;
    }
    
    @Override
    public void doImport(Layers theLayers, List<CSVRecord> records)
    {
      for(CSVRecord record : records)
      {
        // ok, get the date
        try
        {
          FixWrapper nextFix = process(record.iterator());
          
          // now the other fields
          System.out.println(record);
        }
        catch (ParseException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
      }
      
    }


    
  }
  
  private static CSV_Importer importerFor(final String filename)
  {
    return new OSD_Importer();
  }
  
  private static void doImport(final Layers theLayers,
      final InputStream inputStream, final String fileName)
  {
      // find out which type it is
      CSV_Importer importer = importerFor(fileName);
      
      try
      {
        // get the file as a string
        final String contents = inputStreamAsString(inputStream);
        
        // pass it through the parser
        final List<CSVRecord> records = CSVParser.parse(contents, CSVFormat.EXCEL).getRecords();
        
        // go for it
        importer.doImport(theLayers, records);
      }
    catch (IOException e)
    {
      LoggingService.INSTANCE().logError(LoggingService.ERROR,
          "Failed while importing CSV file:" + fileName, e);
    }
      
  }

  public static void doZipImport(final Layers theLayers,
      final InputStream inputStream, final String fileName)
  {
    final ZipInputStream zis = new ZipInputStream(inputStream);

    ZipEntry entry;
    try
    {
      while ((entry = zis.getNextEntry()) != null)
      {
        // is this one of ours?
        final String theName = entry.getName();

        if (theName.endsWith(".csv"))
        {
          // cool, here it is - process it

          // extract the data into a stream
          final ByteArrayOutputStream bos = new ByteArrayOutputStream();
          final BufferedOutputStream fout = new BufferedOutputStream(bos);
          for (int c = zis.read(); c != -1; c = zis.read())
          {
            fout.write(c);
          }
          zis.closeEntry();
          fout.close();
          bos.close();

          // now create a byte input stream from the byte output stream
          final ByteArrayInputStream bis =
              new ByteArrayInputStream(bos.toByteArray());

          // and create it
          doImport(theLayers, bis, theName);
        }
      }
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }

  }

  private static String inputStreamAsString(final InputStream stream)
      throws IOException
  {
    final InputStreamReader isr = new InputStreamReader(stream);
    final BufferedReader br = new BufferedReader(isr);
    final StringBuilder sb = new StringBuilder();
    String line = null;

    while ((line = br.readLine()) != null)
    {
      sb.append(line + "\n");
    }

    br.close();
    return sb.toString();
  }

//  /**
//   * extract the course element from the supplied string
//   * 
//   * @param descriptionTxt
//   * @return
//   * @throws ParseException
//   */
//  private static double speedFrom(final String descriptionTxt)
//      throws ParseException
//  {
//    double res = 0;
//    // STRING LOOKS LIKE
//    // <![CDATA[<b>RADAR PLOT 20:01:12 (GMT)</b><br><hr>Lat:
//    // 05.9696<br>Lon: 07.9633<br>Course: 253.0<br>Speed: 7.1
//    // knots<br>Date: September 12, 2009]]>
//    final int startI = descriptionTxt.indexOf("Speed");
//    final int endI = descriptionTxt.indexOf("knots");
//    if ((startI > 0) && (endI > 0))
//    {
//      final String subStr = descriptionTxt.substring(startI + 6, endI - 1);
//      res = MWCXMLReader.readThisDouble(subStr.trim());
//    }
//    return res;
//  }

 

  // private static String readFileAsString(String filePath)
  // throws java.io.IOException
  // {
  // StringBuffer fileData = new StringBuffer(1000);
  // BufferedReader reader = new BufferedReader(new FileReader(filePath));
  // char[] buf = new char[1024];
  // int numRead = 0;
  // while ((numRead = reader.read(buf)) != -1)
  // {
  // String readData = String.valueOf(buf, 0, numRead);
  // fileData.append(readData);
  // buf = new char[1024];
  // }
  // reader.close();
  // return fileData.toString();
  // }
  //
  // public static void main(String[] args)
  // {
  // doImport(null, null, null);
  // }

}
