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
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;

public class ImportRiderNarrativeDocument
{

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

  /**
   * helper that can ask the user a question
   * 
   */
  public static interface QuestionHelper
  {
    boolean askYes(String title, String message);
  }

  private static class RiderEntry
  {
    private final Date date;
    private final Integer bearing;
    private final Integer beam;
    private final String text;

    public RiderEntry(final Date date, final Integer bearing,
        final Integer beam, final String text)
    {
      this.date = date;
      this.bearing = bearing;
      this.beam = beam;
      this.text = ImportNarrativeDocument.removeBadChars(text);
      ;
    }

    @Override
    public String toString()
    {
      // first the bearing bits
      final String brgStr = bearing == null ? "" : "" + bearing;
      final String beamStr = beam == null ? "" : "" + beam;
      return "[" + brgStr + "/" + beamStr + "] " + text;
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
    private final static String valid_doc_path =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/RiderNarrative.docx";

    private final static String test_doc_root =
        "../org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Word/test_docs";

    private final static String bad_date_1 = test_doc_root + "/"
        + "BadDate1.docx";
    private final static String old_doc_format = test_doc_root + "/"
        + "RiderNarrative.doc";

    private final static String ownship_track =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";

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
      final ImportRiderNarrativeDocument im =
          new ImportRiderNarrativeDocument(null);

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

      final DateFormat df = new SimpleDateFormat("ddHHmm'Z' MMM yy");
      df.setTimeZone(TimeZone.getTimeZone("GMT"));
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

      final String testFile = old_doc_format;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);
      assertEquals("layers empty", 1, tLayers.size());

      final ImportRiderNarrativeDocument importer =
          new ImportRiderNarrativeDocument(tLayers);
      final HWPFDocument doc = new HWPFDocument(is);

      final Range range = doc.getRange();
      final Table table = new TableIterator(range).next();
      assertTrue(canImport(new DocHelper(table)));

      final TableBreakdown data = importer.importFromWord(doc);

      final SimpleDateFormat dateF =
          new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
      dateF.setTimeZone(TimeZone.getTimeZone("GMT"));

      assertNotNull(data);
      assertNotNull(data.header);
      assertNotNull(data.header.startDate);
      assertNotNull(data.entries);
      assertEquals(10, data.entries.size());
      assertEquals("HMS Nonsuch", data.header.platform);
      assertEquals("2017/07/09 12:00:00", dateF.format(data.header.startDate));

      final RiderEntry entry4 = data.entries.get(4);
      assertEquals("2017/07/09 12:04:16", dateF.format(entry4.date));
      assertEquals(null, entry4.bearing);
      assertEquals("Lorem ipsum 5", entry4.text);
      assertEquals(21, entry4.beam.intValue());
      assertEquals("[/21] Lorem ipsum 5", entry4.toString());

      final RiderEntry entry5 = data.entries.get(5);
      assertEquals("2017/07/09 12:05:17", dateF.format(entry5.date));
      assertEquals(22, entry5.bearing.intValue());
      assertEquals("Lorem ipsum 6", entry5.text);
      assertEquals(null, entry5.beam);
      assertEquals("[22/] Lorem ipsum 6", entry5.toString());

      final RiderEntry entry8 = data.entries.get(7);
      assertEquals("2017/07/09 12:07:19", dateF.format(entry8.date));
      assertEquals(null, entry8.bearing);
      assertTrue("Contains newline", entry8.text.contains("\n"));
      assertEquals(null, entry8.beam);
      assertEquals("[/] Lorem ipsum 8\rAnd more \nAnd more", entry8.toString());

      // ok, now store them
      importer.processThese(data);

      // hmmm, how many tracks
      assertEquals("got new tracks", 2, tLayers.size());

      final NarrativeWrapper narrLayer =
          (NarrativeWrapper) tLayers.elementAt(1);
      // correct final count
      assertEquals("Got num lines", 10, narrLayer.size());
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

      final String testFile = valid_doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final ImportRiderNarrativeDocument importer =
          new ImportRiderNarrativeDocument(tLayers);
      assertEquals("layers empty", 1, tLayers.size());

      final XWPFDocument doc = new XWPFDocument(is);
      final TableBreakdown data = importer.importFromWordX(doc);

      final SimpleDateFormat dateF =
          new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
      dateF.setTimeZone(TimeZone.getTimeZone("GMT"));

      assertNotNull(data);
      assertNotNull(data.header);
      assertNotNull(data.header.startDate);
      assertNotNull(data.entries);
      assertEquals(10, data.entries.size());
      assertEquals("HMS Nonsuch", data.header.platform);
      assertEquals("2017/07/09 12:00:00", dateF.format(data.header.startDate));

      final RiderEntry entry4 = data.entries.get(4);
      assertEquals("2017/07/09 12:04:16", dateF.format(entry4.date));
      assertEquals(null, entry4.bearing);
      assertEquals("Lorem ipsum 5", entry4.text);
      assertEquals(21, entry4.beam.intValue());
      assertEquals("[/21] Lorem ipsum 5", entry4.toString());

      final RiderEntry entry5 = data.entries.get(5);
      assertEquals("2017/07/09 12:05:17", dateF.format(entry5.date));
      assertEquals(22, entry5.bearing.intValue());
      assertEquals("Lorem ipsum 6", entry5.text);
      assertEquals(null, entry5.beam);
      assertEquals("[22/] Lorem ipsum 6", entry5.toString());

      final RiderEntry entry8 = data.entries.get(7);
      assertEquals("2017/07/09 12:07:19", dateF.format(entry8.date));
      assertEquals(null, entry8.bearing);
      assertTrue("Contains newline", entry8.text.contains("\n"));
      assertEquals(null, entry8.beam);
      assertEquals("[/] Lorem ipsum 8And more \nAnd more", entry8.toString());

      // ok, now store them
      importer.processThese(data);

      // hmmm, how many tracks
      assertEquals("got new tracks", 2, tLayers.size());

      final NarrativeWrapper narrLayer =
          (NarrativeWrapper) tLayers.elementAt(1);
      // correct final count
      assertEquals("Got num lines", 10, narrLayer.size());
    }

    @SuppressWarnings("deprecation")
    public void testTimeImport() throws ParseException
    {
      final String string2 = "02:01:34";
      final DateFormat df = new SimpleDateFormat("HH:mm:ss");
      df.setTimeZone(TimeZone.getTimeZone("GMT"));
      assertEquals("correct time", "1 Jan 1970 02:01:34 GMT", df.parse(string2)
          .toGMTString());
    }

  }

  private static interface WordHelper
  {
    public String getCell(int i);

    public String getFirstCell();

    public String getHeaderCell(int num);

    public boolean hasMoreEntries();
  }

  private static List<String> SkipNames = null;

  private static QuestionHelper questionHelper;

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

  private static Header headerFor(final WordHelper helper,
      final SimpleDateFormat dateFormat) throws ParseException
  {
    final String dateText = helper.getHeaderCell(0);
    final String platform = helper.getHeaderCell(4);
    final Date date = dateFormat.parse(dateText);
    final Header header = new Header(date, platform);
    return header;
  }

  public static void logThisError(final int status, final String msg,
      final Exception e)
  {
    Application.logError3(status, msg, e, true);
  }

  public static void setQuestionHelper(final QuestionHelper helper)
  {
    questionHelper = helper;
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

  public ImportRiderNarrativeDocument(final Layers target)
  {
    _layers = target;

    DATE_FORMAT = new SimpleDateFormat("ddHHmm'Z' MMM yy");
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));

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

  private void addEntry(final RiderEntry thisN, final Header header)
  {
    final NarrativeWrapper nw = getNarrativeLayer();
    String hisTrack = trackFor(header.platform, header.platform);

    // did we find a track? Don't worry if we didn't just use the raw text
    if (hisTrack == null)
    {
      hisTrack = header.platform;
    }

    final String textBit = thisN.toString();

    // sort out the time
    final long correctedDTG = thisN.date.getTime();

    final NarrativeEntry ne =
        new NarrativeEntry(hisTrack, "Rider", new HiResDate(correctedDTG),
            textBit);

    // shade all rider's narratives black
    ne.setColor(DebriefColors.BLACK);

    // and store it
    nw.add(ne);
  }

  private List<RiderEntry> entriesFor(final WordHelper helper,
      final Header header, final SimpleDateFormat timeFormat)
      throws ParseException
  {
    final List<RiderEntry> res = new ArrayList<RiderEntry>();

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

      final Date date = timeFormat.parse(dateStr);
      Integer bearing;
      if (bearingStr.isEmpty() || bearingStr.toUpperCase().equals("NO B"))
      {
        bearing = null;
      }
      else
      {
        bearing = Integer.parseInt(bearingStr);
      }

      final Integer beam;
      if (beamStr.isEmpty() || beamStr.toUpperCase().equals("NO BM"))
      {
        beam = null;
      }
      else
      {
        beam = Integer.parseInt(beamStr);
      }

      final Date newDate = new Date(dateFor(header.startDate, date));

      final RiderEntry entry = new RiderEntry(newDate, bearing, beam, text);
      res.add(entry);

    }
    return res;
  }

  private NarrativeWrapper getNarrativeLayer()
  {
    NarrativeWrapper nw =
        (NarrativeWrapper) _layers.findLayer(ImportReplay.NARRATIVE_LAYER);

    if (nw == null)
    {
      nw = new NarrativeWrapper(ImportReplay.NARRATIVE_LAYER);
      _layers.addThisLayer(nw);
    }

    return nw;
  }

  public void
      handleImport(final String fileName, final InputStream inputStream)
  {
    // ok, read it into a document
    HWPFDocument doc = null;

    try
    {
      doc = new HWPFDocument(inputStream);
    }
    catch (final IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // ok, now have a see if it's a rider's narrative
    final Range range = doc.getRange();
    final TableIterator tIter = new TableIterator(range);

    final boolean isRider =
        tIter.hasNext() && canImport(new DocHelper(tIter.next()));

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

  public void
      handleImportX(final String fileName, final InputStream inputStream)
  {
    // ok, read it into a document
    XWPFDocument doc = null;

    try
    {
      doc = new XWPFDocument(inputStream);
    }
    catch (final IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // ok, now have a see if it's a rider's narrative
    final List<XWPFTable> tables = doc.getTables();
    final boolean isRider =
        (tables != null && tables.size() > 0 && canImport(new DocXHelper(tables
            .get(0))));

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

  public ArrayList<String> importFromPdf(final String fileName,
      final InputStream inputStream)
  {
    final ArrayList<String> strings = new ArrayList<String>();

    try
    {
      final PDDocument document = PDDocument.load(inputStream);

      final PDFTextStripper textStripper = new PDFTextStripper();
      final PDPageTree pages = document.getPages();
      for (int i = 1; i <= pages.getCount(); i++)
      {
        textStripper.setStartPage(i);
        textStripper.setEndPage(i);
        final String pageText = textStripper.getText(document);
        final String[] split = pageText.split(textStripper.getLineSeparator());
        strings.addAll(Arrays.asList(split));

      }
      document.close();
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }

    return strings;
  }

  public TableBreakdown importFromWord(final HWPFDocument doc)
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return data;
  }

  public TableBreakdown importFromWordX(final XWPFDocument doc)
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

  public void logError(final int status, final String msg, final Exception e)
  {
    logThisError(status, msg, e);
  }

  /**
   * parse a list of strings
   * 
   * @param data
   */
  public void processThese(final TableBreakdown data)
  {
    if (data == null)
    {
      return;
    }

    // keep track of if we've added anything
    boolean dataAdded = false;

    // ok, now we can loop through the strings
    for (final RiderEntry thisN : data.entries)
    {
      // add a narrative entry
      addEntry(thisN, data.header);

      // ok, take note that we've added something
      dataAdded = true;
    }

    if (dataAdded)
    {
      _layers.fireModified(getNarrativeLayer());
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
            // ok, ask the user if he wants to change the subject track to this track's name
            if (questionHelper != null)
            {

              final boolean wantsTo =
                  questionHelper.askYes("Change track name",
                      "Host platform not found for narrative entries.\nDo you want to rename track ["
                          + singleTrack.getName() + "] to [" + name + "]");

              // remember that we've asked about it
              askedAbout.add(singleTrack.getName() + name);

              if (wantsTo)
              {
                singleTrack.setName(name);
                match = name;
              }
            }
          }
          else
          {
            // we can't find a host track.

            // have we already told the user?
            if (!_declaredNoHostFound)
            {
              // ok, stop it appearing again
              _declaredNoHostFound = true;

              // tell the user
              MessageProvider.Base.Provider.show("Import Narrative",
                  "Narrative entries will be imported, but we won't be creating FCSs "
                      + "since we couldn't determine the host track for: "
                      + originalName + ".", MessageProvider.WARNING);

            }
          }
        }
      }
    }

    return match;
  }
}
