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
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GUI.LoggingService;
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

  /**
   * the time-stamp presumed for LineString data that may not contain time data
   * 
   */
  private static int DEFAULT_TIME_STEP = 1000;

  private static TrackWrapper lastTrack = null;

  /**
   * create a fix using the supplied data
   * 
   * @param target
   * @param trackName
   * @param theDate
   * @param theLoc
   */
  private static void addFix(final Layers target, final String trackName,
      final HiResDate theDate, final WorldLocation theLoc,
      final double courseDegs, final double speedKts)
  {
    // is this our current layer?
    if (lastTrack  != null)
    {
      if (lastTrack.getName().equals(trackName))
      {
        // sorted
      }
      else
      {
        lastTrack = null;
      }
    }

    if (lastTrack == null)
    {
      lastTrack = (TrackWrapper) target.findLayer(trackName);
      if (lastTrack == null)
      {
        createTrack(target, trackName);
      }
    }

    final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(courseDegs);
    final double speedYPS =
        new WorldSpeed(speedKts, WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec) / 3d;

    final FixWrapper newFix =
        new FixWrapper(new Fix(theDate, theLoc, courseRads, speedYPS));

    // and reset the time value
    newFix.resetName();

    lastTrack.addFix(newFix);
  }

  /**
   * extract the course element from the supplied string
   * 
   * @param descriptionTxt
   * @return
   * @throws ParseException
   */
  private static double courseFrom(final String descriptionTxt)
      throws ParseException
  {
    double res = 0;
    // STRING LOOKS LIKE
    // <![CDATA[<b>RADAR PLOT 20:01:12 (GMT)</b><br><hr>Lat:
    // 05.9696<br>Lon: 07.9633<br>Course: 253.0<br>Speed: 7.1
    // knots<br>Date: September 12, 2009]]>
    final int startI = descriptionTxt.indexOf("Course");
    final int endI = descriptionTxt.indexOf("<br>Speed");
    if ((startI > 0) && (endI > 0))
    {
      final String subStr = descriptionTxt.substring(startI + 7, endI - 1);
      res = MWCXMLReader.readThisDouble(subStr.trim());
    }
    return res;
  }

  /**
   * create a track using the supplied name
   * 
   * @param target
   * @param trackName
   */
  private static void createTrack(final Layers target, final String trackName)
  {
    lastTrack = new TrackWrapper();
    lastTrack.setName(trackName);

    // sort out a color
    // sort out a color
    final Color theCol =
        DebriefColors.RandomColorProvider.getRandomColor(colorCounter++);
    lastTrack.setColor(theCol);

    target.addThisLayer(lastTrack);
  }

  protected static interface CSV_Importer
  {

    void doImport(Layers theLayers, List<CSVRecord> records);
    
  }
  
  private static class OSD_Importer extends Import_CSV_GZ implements CSV_Importer
  {

    @Override
    public void doImport(Layers theLayers, List<CSVRecord> records)
    {
      for(CSVRecord record : records)
      {
        // ok, get the date
        try
        {
          HiResDate date = getHiResDate(record.get(0));
          
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

  /**
   * utility to run through the contents of a LineString item - presuming the presence of altitude
   * data
   * 
   * @param contents
   *          the inside of the linestring construct
   * @param theLayers
   *          where we're going to stick the data
   * @param startDate
   *          the start date for the track
   * @throws ParseException
   */
  private static void parseTheseCoords(final String contents,
      final Layers theLayers, final Date startDate) throws ParseException
  {
    final StringTokenizer token = new StringTokenizer(contents, ",\n", false);

    Date newDate = new Date(startDate.getTime());

    while (token.hasMoreElements())
    {
      final String longV = token.nextToken();
      final String latV = token.nextToken();
      final String altitude = token.nextToken();

      // just check that we have altitude data
      double theAlt = 0;
      if (altitude.length() > 0)
      {
        theAlt = MWCXMLReader.readThisDouble(altitude);
      }

      addFix(theLayers, startDate.toString(), new HiResDate(newDate.getTime()),
          new WorldLocation(MWCXMLReader.readThisDouble(latV), MWCXMLReader
              .readThisDouble(longV), -theAlt), 0, 0);

      // add a second incremenet to the date, to create the new date
      newDate = new Date(newDate.getTime() + DEFAULT_TIME_STEP);
    }

  }

  /**
   * extract the course element from the supplied string
   * 
   * @param descriptionTxt
   * @return
   * @throws ParseException
   */
  private static double speedFrom(final String descriptionTxt)
      throws ParseException
  {
    double res = 0;
    // STRING LOOKS LIKE
    // <![CDATA[<b>RADAR PLOT 20:01:12 (GMT)</b><br><hr>Lat:
    // 05.9696<br>Lon: 07.9633<br>Course: 253.0<br>Speed: 7.1
    // knots<br>Date: September 12, 2009]]>
    final int startI = descriptionTxt.indexOf("Speed");
    final int endI = descriptionTxt.indexOf("knots");
    if ((startI > 0) && (endI > 0))
    {
      final String subStr = descriptionTxt.substring(startI + 6, endI - 1);
      res = MWCXMLReader.readThisDouble(subStr.trim());
    }
    return res;
  }

  /**
   * This method ensures that the output String has only valid XML unicode characters as specified
   * by the XML 1.0 standard. For reference, please see <a
   * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the standard</a>. This method will
   * return an empty String if the input is null or empty.
   * 
   * @param in
   *          The String whose non-valid characters we want to remove.
   * @return The in String, stripped of non-valid characters.
   */
  private static String stripNonValidXMLCharacters(final String in)
  {
    final StringBuffer out = new StringBuffer(); // Used to hold the output.
    char current; // Used to reference the current character.

    if (in == null || ("".equals(in)))
    {
      return ""; // vacancy test.
    }
    for (int i = 0; i < in.length(); i++)
    {
      current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here;
      // it should not happen.
      if ((current == 0x9) || (current == 0xA) || (current == 0xD)
          || ((current >= 0x20) && (current <= 0xD7FF))
          || ((current >= 0xE000) && (current <= 0xFFFD))
          || ((current >= 0x10000) && (current <= 0x10FFFF)))
      {
        out.append(current);
      }
    }
    return out.toString();
  }

  /**
   * Remove the suffix from the passed file name, together with any leading path.
   * 
   * @param fileName
   *          File name to remove suffix from.
   * 
   * @return <TT>fileName</TT> without a suffix.
   * 
   * @throws IllegalArgumentException
   *           if <TT>null</TT> file name passed.
   */
  private static String tidyFileName(final String fileName)
  {
    if (fileName == null)
    {
      throw new IllegalArgumentException("file name == null");
    }

    // start off by ditching the path
    final File holder = new File(fileName);
    String res = holder.getName();

    // now ditch the file suffix
    final int pos = res.lastIndexOf('.');
    if (pos > 0 && pos < res.length() - 1)
    {
      res = res.substring(0, pos);
    }
    return res;
  }

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
