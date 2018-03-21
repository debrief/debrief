/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.ReaderWriter.Word;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.TrackDataProvider;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ImportRiderNarrativeDocument
{

  private static final String DATE_FORMAT_STR = "ddHHmm'Z' MMM yy";

  private static class DocHelper implements WordHelper
  {
    final private Table _table;

    private int rowCount = 2;

    private DocHelper(final Table table)
    {
      _table = table;
    }

    @Override
    public String getCell(final int i)
    {
      final TableRow row = _table.getRow(rowCount);
      final String res;
      if (row.numCells() <= i)
      {
        res = null;
      }
      else
      {
        res = row.getCell(i).text().trim();
      }
      return res;
    }

    @Override
    public String getFirstCell()
    {
      final TableRow headerRow = _table.getRow(0);
      final TableCell cell = headerRow.getCell(0);
      return cell.text().trim();
    }

    @Override
    public String getHeaderCell(final int num)
    {
      final TableRow headerRow = _table.getRow(1);
      final TableCell cell = headerRow.getCell(num);
      return cell.text().trim();
    }

    @Override
    public boolean hasMoreEntries()
    {
      final int rows = _table.numRows();
      return ++rowCount < rows;
    }

  }

  private static class DocXHelper implements WordHelper
  {
    final private XWPFTable _table;

    private int rowCount = 2;

    private DocXHelper(final XWPFTable table)
    {
      _table = table;
    }

    @Override
    public String getCell(final int i)
    {
      final XWPFTableRow row = _table.getRow(rowCount);
      final List<XWPFTableCell> cells = row.getTableCells();
      final String res;
      if (cells.size() <= i)
      {
        res = null;
      }
      else
      {
        res = row.getCell(i).getText().trim();
      }
      return res;
    }

    @Override
    public String getFirstCell()
    {
      final XWPFTableRow headerRow = _table.getRow(0);
      final XWPFTableCell cell = headerRow.getCell(0);
      return cell.getText().trim();
    }

    @Override
    public String getHeaderCell(final int num)
    {
      final XWPFTableRow headerRow = _table.getRow(1);
      final XWPFTableCell cell = headerRow.getCell(num);
      return cell.getText().trim();
    }

    @Override
    public boolean hasMoreEntries()
    {
      final int rows = _table.getRows().size();
      return ++rowCount < rows;
    }

  }

  private static class Header
  {
    private final Date startDate;
    private final String platform;

    public Header(final Date date, final String platform)
    {
      startDate = date;
      this.platform = platform;
    }
  }

  private static class NoHostPlatformException extends Exception
  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

  }

  private static class RiderEntry
  {
    private final Date date;
    private final Integer bearing;
    private final Integer ambig;
    private final String beam;
    private final String text;

    public RiderEntry(final Date date, final Integer bearing,
        final Integer ambig, final String beam, final String text)
    {
      this.date = date;
      this.bearing = bearing;
      this.ambig = ambig;
      this.beam = beam;
      this.text = ImportNarrativeDocument.removeBadChars(text);
    }

    @Override
    public String toString()
    {
      // first the bearing bits
      final String ambigStr = ambig == null ? "" : "/" + ambig;
      final String brgStr = bearing == null ? "" : "Brg:" + bearing + ambigStr;
      final String beamStr = beam == null ? "" : "Beam:" + beam;
      final String separator = !"".equals(brgStr) && !"".equals(beamStr) ? ", "
          : "";
      final String res = "".equals(beamStr) && "".equals(brgStr) ? text : "["
          + brgStr + separator + beamStr + "] " + text;
      return res;
    }
  }

  public static class TableBreakdown
  {
    private final Header header;
    private final List<RiderEntry> entries;

    public TableBreakdown(final Header header, final List<RiderEntry> entries)
    {
      this.header = header;
      this.entries = entries;
    }
  }

  public static class TestImportRider extends TestCase
  {
    private class DummyTrackDataProvider implements TrackDataProvider
    {
      final private TrackWrapper _primary;

      private DummyTrackDataProvider(final TrackWrapper primary)
      {
        _primary = primary;
      }

      @Override
      public void addTrackDataListener(final TrackDataListener listener)
      {
        throw new IllegalArgumentException("Not implemented");
      }

      @Override
      public void addTrackShiftListener(final TrackShiftListener listener)
      {
        throw new IllegalArgumentException("Not implemented");
      }

      @Override
      public void fireTracksChanged()
      {
        throw new IllegalArgumentException("Not implemented");
      }

      @Override
      public void fireTrackShift(final WatchableList watchableList)
      {
        throw new IllegalArgumentException("Not implemented");
      }

      @Override
      public WatchableList getPrimaryTrack()
      {
        return _primary;
      }

      @Override
      public WatchableList[] getSecondaryTracks()
      {
        throw new IllegalArgumentException("Not implemented");
      }

      @Override
      public void removeTrackDataListener(final TrackDataListener listener)
      {
        throw new IllegalArgumentException("Not implemented");
      }

      @Override
      public void removeTrackShiftListener(final TrackShiftListener listener)
      {
        throw new IllegalArgumentException("Not implemented");
      }

    }

    private final static String valid_doc_path =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/RiderNarrative.docx";

    private final static String valid_doc_path_bad_date =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/RiderNarrative2.docx";

    private final static String test_doc_root =
        "../org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Word/test_docs";
    private final static String bad_date_1 = test_doc_root + "/"
        + "BadDate1.docx";

    private final static String old_doc_format = test_doc_root + "/"
        + "RiderNarrative.doc";

    private final static String wrong_type_path = test_doc_root + "/"
        + "RiderNarrative_WrongType.doc";

    private final static String ownship_track =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/nonsuch.rep";

    public static int countLines(final String str)
    {
      if (str == null || str.isEmpty())
      {
        return 0;
      }
      int lines = 1;
      int pos = 0;
      while ((pos = str.indexOf("\n", pos) + 1) != 0)
      {
        lines++;
      }
      return lines;
    }

    @SuppressWarnings("unused")
    private String messageStr = null;

    @Override
    public void setUp()
    {

      System.out.println("setting up message provider ");

      // clear the message string
      messageStr = null;

      // initialise the message provider
      MessageProvider.Base.setProvider(new MessageProvider()
      {

        @Override
        public void show(final String title, final String message,
            final int status)
        {
          messageStr = message;
        }
      });
    }

    public void testTidyDate() throws ParseException
    {
      String GOOD_STR = "220000Z JUL 09";
      String BAD_STR = "220001Z JUL 09";
      DateFormat format = new GMTDateFormat(DATE_FORMAT_STR);
      Date goodDate = format.parse(GOOD_STR);
      Date fixedDate = tidyDate(BAD_STR, format);
      assertEquals("correct date", goodDate, fixedDate);
    }

    public void testBadDate() throws InterruptedException, IOException
    {
      final String testFile = bad_date_1;
      final File testI = new File(testFile);
      assertTrue(testI.exists());
      final InputStream is = new FileInputStream(testI);
      final XWPFDocument doc = new XWPFDocument(is);
      final List<XWPFTable> tables = doc.getTables();
      assertTrue("can import", canImport(new DocXHelper(tables.get(0))));
      doc.close();
    }

    public void testCanImport() throws InterruptedException, IOException
    {
      final String testFile = valid_doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());
      final InputStream is = new FileInputStream(testI);
      final XWPFDocument doc = new XWPFDocument(is);
      final List<XWPFTable> tables = doc.getTables();
      assertTrue("can import", canImport(new DocXHelper(tables.get(0))));
      doc.close();
    }

    @SuppressWarnings("deprecation")
    public void testCombineTime() throws ParseException
    {
      final ImportRiderNarrativeDocument im = new ImportRiderNarrativeDocument(
          null, null);

      final Date date = im.DATE_FORMAT.parse("090000Z JUL 17");
      final Date time = im.TIME_FORMAT.parse("02:01:34");

      final long millis = dateFor(date, time);

      final Date res = new Date(millis);
      assertEquals("9 Jul 2017 02:01:34 GMT", res.toGMTString());
    }

    @SuppressWarnings("deprecation")
    public void testDateImport() throws ParseException
    {
      final String string2 = "090000Z JUL 17";

      final DateFormat df = new GMTDateFormat(DATE_FORMAT_STR);
      assertEquals("correct date", "9 Jul 2017 00:00:00 GMT", df.parse(string2)
          .toGMTString());
    }

    public void testImportRiderNarrative() throws InterruptedException,
        IOException
    {
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, tLayers);

      assertEquals("read in track", 1, tLayers.size());
      final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
      assertNotNull("found parent track", parent);

      // fix the track name
      parent.setName("NONSUCH");

      final String testFile = old_doc_format;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);
      assertEquals("layers empty", 1, tLayers.size());

      final ImportRiderNarrativeDocument importer =
          new ImportRiderNarrativeDocument(tLayers, null);
      final HWPFDocument doc = new HWPFDocument(is);

      final Range range = doc.getRange();
      final Table table = new TableIterator(range).next();
      assertTrue(canImport(new DocHelper(table)));

      final TableBreakdown data = importer.importFromWord(doc);
      testThisData(tLayers, parent, importer, data);

    }

    public void testImportRiderNarrativeX() throws InterruptedException,
        IOException
    {
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, tLayers);

      assertEquals("read in track", 1, tLayers.size());

      assertEquals("read in track", 1, tLayers.size());
      final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
      assertNotNull("found parent track", parent);

      // fix the track name
      parent.setName("NONSUCH");

      final String testFile = valid_doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final ImportRiderNarrativeDocument importer =
          new ImportRiderNarrativeDocument(tLayers, null);
      assertEquals("layers empty", 1, tLayers.size());

      final XWPFDocument doc = new XWPFDocument(is);
      final TableBreakdown data = importer.importFromWordX(doc);

      testThisData(tLayers, parent, importer, data);
    }

    /**
     * test we don't fall over when origin date isn't at midnight
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    public void testImportRiderNarrativeOffsetDate()
        throws InterruptedException, IOException
    {
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, tLayers);

      assertEquals("read in track", 1, tLayers.size());

      assertEquals("read in track", 1, tLayers.size());
      final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
      assertNotNull("found parent track", parent);

      // fix the track name
      parent.setName("NONSUCH");

      final String testFile = valid_doc_path_bad_date;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final ImportRiderNarrativeDocument importer =
          new ImportRiderNarrativeDocument(tLayers, null);
      assertEquals("layers empty", 1, tLayers.size());

      final XWPFDocument doc = new XWPFDocument(is);
      final TableBreakdown data = importer.importFromWordX(doc);

      testThisData(tLayers, parent, importer, data);
    }

    public void testImportRiderNarrativeXMissingPlatformNoPrimaryTrack()
        throws InterruptedException, IOException
    {
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, tLayers);

      assertEquals("read in track", 1, tLayers.size());
      final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
      assertNotNull("found parent track", parent);

      // add in a second track
      final TrackWrapper secondTrack = new TrackWrapper();
      secondTrack.setName("some name");
      tLayers.addThisLayer(secondTrack);

      parent.setName("NELSON");

      final String testFile = valid_doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final ImportRiderNarrativeDocument importer =
          new ImportRiderNarrativeDocument(tLayers, new DummyTrackDataProvider(
              null));
      assertEquals("tracks present", 2, tLayers.size());

      final XWPFDocument doc = new XWPFDocument(is);
      final TableBreakdown data = importer.importFromWordX(doc);

      importer.processThese(data);

      // ok, check the sensors got added
      final BaseLayer sensors = parent.getSensors();
      assertEquals("No Sensors created", 0, sensors.size());

      // also check no narrative entries added
      final NarrativeWrapper narr = (NarrativeWrapper) tLayers.findLayer(
          ImportReplay.NARRATIVE_LAYER);
      assertNull(narr);
    }

    public void testImportRiderNarrativeXMissingPlatformPrimaryTrack()
        throws InterruptedException, IOException
    {
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, tLayers);

      assertEquals("read in track", 1, tLayers.size());
      final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
      assertNotNull("found parent track", parent);

      // add in a second track
      final TrackWrapper secondTrack = new TrackWrapper();
      secondTrack.setName("some name");
      tLayers.addThisLayer(secondTrack);

      parent.setName("NELSON");

      final String testFile = valid_doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final ImportRiderNarrativeDocument importer =
          new ImportRiderNarrativeDocument(tLayers, new DummyTrackDataProvider(
              parent));
      assertEquals("tracks present", 2, tLayers.size());

      final XWPFDocument doc = new XWPFDocument(is);
      final TableBreakdown data = importer.importFromWordX(doc);

      importer.processThese(data);

      // ok, check the sensors got added
      final BaseLayer sensors = parent.getSensors();
      assertEquals("Sensor created", 1, sensors.size());
      final SensorWrapper theS = (SensorWrapper) sensors.elements()
          .nextElement();
      assertEquals(7, theS.size());
    }

    public void testImportRiderNarrativeXMissingPlatformSingleTrack()
        throws InterruptedException, IOException
    {
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, tLayers);

      assertEquals("read in track", 1, tLayers.size());
      final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
      assertNotNull("found parent track", parent);

      parent.setName("NELSON");

      final String testFile = valid_doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final ImportRiderNarrativeDocument importer =
          new ImportRiderNarrativeDocument(tLayers, null);
      assertEquals("layers empty", 1, tLayers.size());

      final XWPFDocument doc = new XWPFDocument(is);
      final TableBreakdown data = importer.importFromWordX(doc);

      importer.processThese(data);

      // ok, check the sensors got added
      final BaseLayer sensors = parent.getSensors();
      assertEquals("Sensor created", 1, sensors.size());
      final SensorWrapper theS = (SensorWrapper) sensors.elements()
          .nextElement();
      assertEquals(7, theS.size());
    }

    private void testThisData(final Layers tLayers, final TrackWrapper parent,
        final ImportRiderNarrativeDocument importer, final TableBreakdown data)
    {
      final SimpleDateFormat dateF = new GMTDateFormat(
          "yyyy/MM/dd hh:mm:ss");

      assertNotNull(data);
      assertNotNull(data.header);
      assertNotNull(data.header.startDate);
      assertNotNull(data.entries);
      assertEquals(15, data.entries.size());
      assertEquals("HMS Nonsuch", data.header.platform);
      assertEquals("2009/07/22 12:00:00", dateF.format(data.header.startDate));

      final RiderEntry entry4 = data.entries.get(4);
      assertEquals("2009/07/22 04:14:16", dateF.format(entry4.date));
      assertEquals(null, entry4.bearing);
      assertEquals("Lorem ipsum 5", entry4.text);
      assertEquals("21", entry4.beam);
      assertEquals("[Beam:21] Lorem ipsum 5", entry4.toString());

      final RiderEntry entry5 = data.entries.get(5);
      assertEquals("2009/07/22 04:15:17", dateF.format(entry5.date));
      assertEquals(274, entry5.bearing.intValue());
      assertEquals("Lorem ipsum 6", entry5.text);
      assertEquals(null, entry5.beam);
      assertEquals("[Brg:274] Lorem ipsum 6", entry5.toString());

      final RiderEntry entry8 = data.entries.get(7);
      assertEquals("2009/07/22 04:17:19", dateF.format(entry8.date));
      assertEquals(null, entry8.bearing);
      assertTrue("Contains newline", entry8.text.contains("\n"));
      assertEquals(null, entry8.beam);

      // the two different importers handle \r newlines differently.
      // so we allow for both permutations here
      final boolean matchesText = entry8.toString().equals(
          "Lorem ipsum 8And more \nAnd more") || entry8.toString().equals(
              "Lorem ipsum 8\rAnd more \nAnd more");
      assertTrue("correct text", matchesText);

      final RiderEntry entry9 = data.entries.get(9);
      assertEquals("2009/07/22 04:19:21", dateF.format(entry9.date));
      assertEquals(92, entry9.bearing.intValue());
      assertEquals(112, entry9.ambig.intValue());
      assertEquals("12/13", entry9.beam);
      assertEquals("[Brg:92/112, Beam:12/13] Lorem ipsum 10", entry9
          .toString());

      final RiderEntry entry10 = data.entries.get(10);
      assertEquals("2009/07/22 04:20:22", dateF.format(entry10.date));
      assertEquals(94, entry10.bearing.intValue());
      assertEquals(123, entry10.ambig.intValue());
      assertEquals(null, entry10.beam);

      final RiderEntry entry11 = data.entries.get(11);
      assertEquals("2009/07/22 04:21:23", dateF.format(entry11.date));
      assertEquals(null, entry11.bearing);
      assertEquals(null, entry11.ambig);
      assertEquals(null, entry11.beam);
      assertEquals(
          "[Bearing represents arc (096-112). Not imported.]Lorem ipsum 12",
          entry11.toString());

      final RiderEntry entry12 = data.entries.get(12);
      assertEquals("2009/07/22 04:21:23", dateF.format(entry12.date));
      assertEquals(null, entry12.bearing);
      assertEquals(null, entry12.ambig);
      assertEquals(null, entry12.beam);
      assertEquals(DTG_MISSING_STR + "Lorem ipsum 13", entry12.toString());

      final RiderEntry entry13 = data.entries.get(13);
      assertEquals("2009/07/22 04:21:23", dateF.format(entry13.date));
      assertEquals(null, entry13.bearing);
      assertEquals(null, entry13.ambig);
      assertEquals(null, entry13.beam);
      assertEquals(DTG_MISSING_STR
          + "[Bearing represents arc (119-143). Not imported.]Lorem ipsum 14",
          entry13.toString());

      // ok, now store them
      importer.processThese(data);

      // hmmm, how many tracks
      assertEquals("got new tracks", 2, tLayers.size());

      final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.elementAt(
          1);
      // correct final count
      assertEquals("Got num lines", 15, narrLayer.size());

      // check ownship received cuts
      assertNotNull("got a sensor", parent.getSensors());
      final SensorWrapper sensor = findOurSensor(NARRATIVE_CUTS_SENSOR, parent,
          false);

      assertNotNull("got our sensor", sensor);
      assertEquals("got cuts", 7, sensor.size());

      final Enumeration<Editable> cuts = sensor.elements();
      SensorContactWrapper lastCut = null;
      SensorContactWrapper firstCut = null;
      while (cuts.hasMoreElements())
      {
        lastCut = (SensorContactWrapper) cuts.nextElement();
        if (firstCut == null)
        {
          firstCut = lastCut;
        }
      }

      assertNotNull(firstCut);
      assertEquals("090722 041012", firstCut.toString());
      assertNull(firstCut.getRange());
      assertEquals(NARRATIVE_CUTS_SENSOR, firstCut.getSensor().getName());
      assertFalse(firstCut.getHasAmbiguousBearing());
      assertEquals(273d, firstCut.getBearing(), 0.001);
      assertEquals(Double.NaN, firstCut.getAmbiguousBearing(), 0.001);

      assertNotNull(lastCut);
      assertTrue(lastCut.getHasAmbiguousBearing());
      assertEquals(094d, lastCut.getBearing(), 0.001);
      assertEquals(123d, lastCut.getAmbiguousBearing(), 0.001);
      assertEquals("090722 042022", lastCut.toString());
      assertNull(lastCut.getRange());
      assertEquals(NARRATIVE_CUTS_SENSOR, lastCut.getSensor().getName());

    }

    @SuppressWarnings("deprecation")
    public void testTimeImport() throws ParseException
    {
      final String string2 = "02:01:34";
      final DateFormat df = new GMTDateFormat("HH:mm:ss");
      assertEquals("correct time", "1 Jan 1970 02:01:34 GMT", df.parse(string2)
          .toGMTString());
    }

    public void testWronglyNamedVersion() throws FileNotFoundException
    {
      final String testFile = wrong_type_path;
      final Layers layers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, layers);

      final File testI = new File(testFile);
      assertTrue(testI.exists());
      final InputStream is = new FileInputStream(testI);
      final ImportRiderNarrativeDocument importer =
          new ImportRiderNarrativeDocument(layers, null);
      importer.handleImport(testFile, is);
      assertEquals("have loaded track and narrative", 2, layers.size());
    }

  }

  private static interface WordHelper
  {
    public String getCell(int i);

    public String getFirstCell();

    public String getHeaderCell(int num);

    public boolean hasMoreEntries();
  }

  private static final String DTG_MISSING_STR =
      "[DTG missing. Re-using previous DTG]";

  public static final String RIDER_SOURCE = "Rider";

  private static final String NARRATIVE_CUTS_SENSOR = "FromNarrative";

  private static List<String> SkipNames = null;

  private static boolean canImport(final WordHelper helper)
  {
    boolean isRider = false;
    String firstCell = helper.getFirstCell();

    if (firstCell != null)
    {
      // this block of text may have new-line or whitespace in it. Clean the text
      // before we do the compare
      firstCell = firstCell.trim();
      firstCell = firstCell.replace(" ", "");
      firstCell = firstCell.replace("\n", "");
      firstCell = firstCell.replace("\r", "");

      if (firstCell.toUpperCase().equals("DTGSTART"))
      {
        isRider = true;
      }
    }

    return isRider;
  }

  private static long dateFor(final Date start, final Date time)
  {
    return start.getTime() + time.getTime();
  }

  private static List<RiderEntry> entriesFor(final WordHelper helper,
      final Header header, final SimpleDateFormat timeFormat)
      throws ParseException
  {
    final List<RiderEntry> res = new ArrayList<RiderEntry>();

    // track the last date, in case a date is missing
    Date lastDate = null;

    while (helper.hasMoreEntries())
    {
      // ok, parse this row
      final String dateStr = helper.getCell(0);
      final String bearingStr = helper.getCell(1);
      final String beamStr = helper.getCell(2);
      final String text = helper.getCell(3);

      // just check if we have enough data
      if (dateStr == null || bearingStr == null || beamStr == null
          || text == null)
      {
        // ok, go to the next row
        continue;
      }

      // ability to store errors
      String warningStr = "";

      final Date date;
      if (!"".equals(dateStr))
      {
        date = timeFormat.parse(dateStr);

        // cache the date;
        lastDate = date;
      }
      else
      {
        if (lastDate != null)
        {
          date = new Date(lastDate.getTime() + 1);
          warningStr += DTG_MISSING_STR;
        }
        else
        {
          date = null;
          logError(ErrorLogger.ERROR,
              "Unable to find date in first row. Skipping this row", null);
          continue;
        }
      }

      final Integer bearing;
      final Integer ambig;
      if (bearingStr.isEmpty() || bearingStr.equalsIgnoreCase("NO B"))
      {
        bearing = null;
        ambig = null;
      }
      else
      {
        // hmm, do we have ambig data?
        if (bearingStr.contains("/"))
        {
          final String[] items = bearingStr.trim().split("/");
          if (items.length == 2)
          {
            bearing = Integer.parseInt(items[0].trim());
            ambig = Integer.parseInt(items[1].trim());
          }
          else
          {
            bearing = null;
            ambig = null;
            logThisError(ErrorLogger.ERROR,
                "Not correct number of bearings to be ambiguous", null);
          }
        }
        else if (bearingStr.contains("-"))
        {
          // it's an arc, ignore the bearing
          bearing = null;
          ambig = null;
          warningStr += "[Bearing represents arc (" + bearingStr
              + "). Not imported.]";
        }
        else
        {
          bearing = Integer.parseInt(bearingStr);
          ambig = null;
        }
      }

      final String beam;
      if (beamStr.isEmpty() || beamStr.equalsIgnoreCase("NO BM"))
      {
        beam = null;
      }
      else
      {
        beam = beamStr;
      }

      final Date newDate = new Date(dateFor(header.startDate, date));

      final String finalText = warningStr + text;

      final RiderEntry entry = new RiderEntry(newDate, bearing, ambig, beam,
          finalText);
      res.add(entry);

    }
    return res;
  }

  private static SensorWrapper findOurSensor(final String name,
      final TrackWrapper parent, final boolean allowCreate)
  {
    final BaseLayer sensors = parent.getSensors();
    final Enumeration<Editable> sIter = sensors.elements();
    while (sIter.hasMoreElements())
    {
      final SensorWrapper thisS = (SensorWrapper) sIter.nextElement();
      if (thisS.getName().equals(name))
      {
        return thisS;
      }
    }
    if (allowCreate)
    {
      // aah. we didn't find it. make one
      final SensorWrapper newS = new SensorWrapper(name);
      parent.add(newS);
      return newS;
    }
    else
    {
      return null;
    }
  }

  private static Date tidyDate(final String dateStr, DateFormat dateFormat)
      throws ParseException
  {
    // if the date looks like this: "220001Z JUL 09" change it to this "220000Z JUL 09"
    final Date date = dateFormat.parse(dateStr);

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MINUTE, 0);

    return calendar.getTime();
  }

  private static Header headerFor(final WordHelper helper,
      final SimpleDateFormat dateFormat) throws ParseException
  {
    final String dateText = helper.getHeaderCell(0).trim();

    // ok. we've encountered a document where the DTG was 220001, not 220000
    // this resulted in one minute being added to all resulting times.

    final String platform = helper.getHeaderCell(4);
    final Date date = tidyDate(dateText, dateFormat);
    final Header header = new Header(date, platform);
    return header;
  }

  private static void logError(final int status, final String msg,
      final Exception e)
  {
    logThisError(status, msg, e);
  }

  private static void logThisError(final int status, final String msg,
      final Exception e)
  {
    Application.logError3(status, msg, e, true);
  }

  /**
   * keep track of which track-source combinations we've asked about
   * 
   */
  private final List<String> askedAbout = new ArrayList<String>();

  /**
   * flag for if we've informed user that we couldn't find host track
   * 
   */
  private boolean _declaredNoHostFound = false;

  /**
   * where we write our data
   * 
   */
  private final Layers _layers;

  /**
   * keep track of track names that we have matched
   * 
   */
  Map<String, String> nameMatches = new HashMap<String, String>();

  private final SimpleDateFormat DATE_FORMAT;

  private final SimpleDateFormat TIME_FORMAT;

  private final TrackDataProvider _trackProvider;

  /**
   * 
   * @param target
   *          where to dump the data
   * @param trackProvider
   *          how to find the primary track
   */
  public ImportRiderNarrativeDocument(final Layers target,
      final TrackDataProvider trackProvider)
  {
    _layers = target;
    _trackProvider = trackProvider;

    DATE_FORMAT = new GMTDateFormat(DATE_FORMAT_STR);
    TIME_FORMAT = new GMTDateFormat("HH:mm:ss");

    if (SkipNames == null)
    {
      SkipNames = new ArrayList<String>();
      SkipNames.add("HMS");
      SkipNames.add("Hms");
      SkipNames.add("USS");
      SkipNames.add("RNAS");
      SkipNames.add("HNLMS");
    }
  }

  private void addCut(final RiderEntry thisN, final String platform)
      throws NoHostPlatformException
  {
    String hisTrack = trackFor(platform, platform);

    // did we find a track? Don't worry if we didn't just use the raw text
    if (hisTrack == null)
    {
      hisTrack = platform;
    }

    // can we find the parent?
    final TrackWrapper parent = (TrackWrapper) _layers.findLayer(hisTrack);

    if (parent != null)
    {
      // find our sensor
      final SensorWrapper ourSensor = findOurSensor(NARRATIVE_CUTS_SENSOR,
          parent, true);

      final HiResDate theDate = new HiResDate(thisN.date.getTime());

      final Double ambigBearing = thisN.ambig != null ? new Double(thisN.ambig)
          : null;

      final SensorContactWrapper cut = new SensorContactWrapper(parent
          .getName(), theDate, null, new Double(thisN.bearing), ambigBearing,
          null, null, DebriefColors.RED, "NARRATIVE", 0, hisTrack);

      ourSensor.add(cut);
    }
  }

  private void addEntry(final RiderEntry thisN, final String platform,
      final NarrativeWrapper narrative) throws NoHostPlatformException
  {
    String hisTrack = trackFor(platform, platform);

    // did we find a track? Don't worry if we didn't just use the raw text
    if (hisTrack == null)
    {
      hisTrack = platform;
    }

    final String textBit = thisN.toString();

    // sort out the time
    final long correctedDTG = thisN.date.getTime();

    final NarrativeEntry ne = new NarrativeEntry(hisTrack, RIDER_SOURCE,
        new HiResDate(correctedDTG), textBit);

    // shade all rider's narratives black
    ne.setColor(DebriefColors.BLACK);

    // and store it
    narrative.add(ne);
  }

  private NarrativeWrapper getNarrativeLayer()
  {
    NarrativeWrapper nw = (NarrativeWrapper) _layers.findLayer(
        ImportReplay.NARRATIVE_LAYER);

    if (nw == null)
    {
      nw = new NarrativeWrapper(ImportReplay.NARRATIVE_LAYER);
      _layers.addThisLayer(nw);
    }

    return nw;
  }

  public void handleImport(final String fileName, final InputStream inputStream)
  {
    // ok, read it into a document
    HWPFDocument doc = null;

    try
    {
      doc = new HWPFDocument(inputStream);
    }
    catch (final OfficeXmlFileException xw)
    {
      logError(ErrorLogger.WARNING,
          ".Doc file appears to contain .Docx data. Switching to other importer",
          xw);
      // ok, it's .docx data in a .doc file
      try
      {
        final FileInputStream is2 = new FileInputStream(fileName);
        handleImportX(fileName, is2);
      }
      catch (final FileNotFoundException e)
      {
        e.printStackTrace();
      }
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }

    if (doc != null)
    {
      // ok, now have a see if it's a rider's narrative
      final Range range = doc.getRange();
      final TableIterator tIter = new TableIterator(range);

      final boolean isRider = tIter.hasNext() && canImport(new DocHelper(tIter
          .next()));

      if (isRider)
      {
        final TableBreakdown data = this.importFromWord(doc);
        this.processThese(data);
      }
      else
      {
        final ImportNarrativeDocument iw = new ImportNarrativeDocument(_layers);
        final ArrayList<String> strings = iw.importFromWord(doc);
        iw.processThese(strings);
      }
    }
  }

  public void handleImportX(final String fileName,
      final InputStream inputStream)
  {
    // ok, read it into a document
    XWPFDocument doc = null;

    try
    {
      doc = new XWPFDocument(inputStream);
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }

    // ok, now have a see if it's a rider's narrative
    final List<XWPFTable> tables = doc.getTables();
    final boolean isRider = (tables != null && tables.size() > 0 && canImport(
        new DocXHelper(tables.get(0))));

    if (isRider)
    {
      final TableBreakdown data = this.importFromWordX(doc);
      this.processThese(data);
    }
    else
    {
      final ImportNarrativeDocument iw = new ImportNarrativeDocument(_layers);
      final ArrayList<String> strings = iw.importFromWordX(doc);
      iw.processThese(strings);
    }
  }

  private TableBreakdown importFromWord(final HWPFDocument doc)
  {
    TableBreakdown data = null;

    try
    {
      final Range range = doc.getRange();

      final TableIterator tIter = new TableIterator(range);

      final Table table = tIter.next();

      final DocHelper helper = new DocHelper(table);

      final Header header = headerFor(helper, DATE_FORMAT);
      final List<RiderEntry> entries = entriesFor(helper, header, TIME_FORMAT);
      data = new TableBreakdown(header, entries);

    }
    catch (final ParseException e)
    {
      e.printStackTrace();
    }

    return data;
  }

  private TableBreakdown importFromWordX(final XWPFDocument doc)
  {
    TableBreakdown data = null;

    try
    {
      final List<XWPFTable> tables = doc.getTables();

      if (tables.size() > 1)
      {
        throw new IllegalArgumentException("Wrongly formatted document");
      }

      final XWPFTable myTable = tables.get(0);

      final DocXHelper helper = new DocXHelper(myTable);

      final Header header = headerFor(helper, DATE_FORMAT);
      final List<RiderEntry> entries = entriesFor(helper, header, TIME_FORMAT);
      data = new TableBreakdown(header, entries);
    }
    catch (final ParseException e)
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if (doc != null)
        {
          doc.close();
        }
      }
      catch (final IOException e)
      {
        e.printStackTrace();
      }
    }

    return data;
  }

  /**
   * parse a list of strings
   * 
   * @param data
   */
  private void processThese(final TableBreakdown data)
  {
    if (data == null)
    {
      return;
    }

    // keep track of if we've added anything
    boolean dataAdded = false;

    try
    {
      NarrativeWrapper narrative = null;

      // ok, now we can loop through the strings
      for (final RiderEntry thisN : data.entries)
      {
        // does it have a bearing?
        if (thisN.bearing != null)
        {
          addCut(thisN, data.header.platform);
        }

        if (narrative == null)
        {
          // note, we defer getting/creating the narrative layer
          // until we get here, since if we can't find a parent
          // track to use, we won't load any data
          narrative = getNarrativeLayer();
        }

        // add a narrative entry
        addEntry(thisN, data.header.platform, narrative);

        // ok, take note that we've added something
        dataAdded = true;
      }

      if (dataAdded)
      {
        _layers.fireModified(getNarrativeLayer());
      }
    }
    catch (final NoHostPlatformException ne)
    {
      // ok, user should have been warned. We're done.
    }
  }

  /**
   * is there a single visible track present?
   * 
   * @param layers
   * @param narrativeName
   * @return
   */
  private TrackWrapper singleTrackPresent(final Layers layers,
      final String narrativeName)
  {
    final TrackWrapper res;
    TrackWrapper candidate = null;
    boolean singleCandidate = false;

    // loop through the layers, see if there is a single track present
    final int ctr = layers.size();
    for (int i = 0; i < ctr; i++)
    {
      final Layer thisL = layers.elementAt(i);
      if (thisL.getVisible() && thisL instanceof TrackWrapper)
      {
        // have we already asked about this platform
        final String thisPerm = thisL.getName() + narrativeName;
        if (!askedAbout.contains(thisPerm))
        {
          // nope, go for it

          // ok, have we found one already?
          if (candidate != null)
          {
            // bugger, more than one track. don't bother
            singleCandidate = false;
            break;
          }
          else
          {
            // hey, it's a maybe
            candidate = (TrackWrapper) thisL;

            // remember we've found one
            singleCandidate = true;
          }
        }
      }
    }

    if (singleCandidate)
    {
      res = candidate;
    }
    else
    {
      res = null;
    }

    return res;
  }

  private String trackFor(final String originalName, String name)
      throws NoHostPlatformException
  {
    if (name == null)
    {
      name = originalName;
    }

    final String platform = name.trim();
    String match = nameMatches.get(platform);
    if (match == null)
    {
      // search the layers
      final Layer theL = _layers.findLayer(platform);
      if (theL != null)
      {
        match = theL.getName();
        nameMatches.put(originalName, match);
      }
      else
      {
        // try skipping then names
        final Iterator<String> nameIter = SkipNames.iterator();
        while (nameIter.hasNext() && match == null)
        {
          final String thisSkip = nameIter.next();
          if (platform.startsWith(thisSkip))
          {
            final String subStr = platform.substring(thisSkip.length()).trim();
            match = trackFor(originalName, subStr);
          }
        }

        if (match == null)
        {
          // ok, if there is just one track present, invite the user to use that
          final TrackWrapper singleTrack = singleTrackPresent(_layers, name);

          // did we find one?
          if (singleTrack != null)
          {
            // ok, just use the name of this single track
            match = singleTrack.getName();
            nameMatches.put(originalName, match);
          }
          else
          {
            // we can't find a host track.

            // is there a primary allocated?
            if (_trackProvider != null)
            {
              final WatchableList parent = _trackProvider.getPrimaryTrack();
              if (parent != null)
              {
                // cool, use it
                match = parent.getName();
                nameMatches.put(originalName, match);
              }
            }

            // have we already told the user?
            if (match == null && !_declaredNoHostFound)
            {
              // ok, stop it appearing again
              _declaredNoHostFound = true;

              // tell the user
              MessageProvider.Base.Provider.show("Import Rider's Narrative",
                  "Platform in Rider's narrative doesn't match any loaded tracks.\nPlease assign a primary, and the cuts will be added to that platform.",
                  MessageProvider.ERROR);

              throw new NoHostPlatformException();
            }
          }
        }
      }
    }

    return match;
  }
}
