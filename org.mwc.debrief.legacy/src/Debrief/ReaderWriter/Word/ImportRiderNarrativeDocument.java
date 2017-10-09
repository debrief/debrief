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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
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
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeEntry;

public class ImportRiderNarrativeDocument
{

  private static class Header
  {
    Date startDate;

    String platform;

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
    final Date date;
    final Integer bearing;
    String text;

    public RiderEntry(final Date date, final Integer bearing, final String text)
    {
      this.date = date;
      this.bearing = bearing;
      this.text = text;
    }
  }

  private static class TableBreakdown
  {
    Header header;

    List<RiderEntry> entries;

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

    public void testImportRiderNarrative() throws FileNotFoundException,
        InterruptedException
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
      final TableBreakdown data = importer.importFromWordX(testFile, is);

      SimpleDateFormat dateF = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
      dateF.setTimeZone(TimeZone.getTimeZone("GMT"));
      
      assertNotNull(data);
      assertNotNull(data.header);
      assertNotNull(data.header.startDate);
      assertNotNull(data.entries);
      assertEquals(7, data.entries.size());
      assertEquals("NONSUCH", data.header.platform);
      assertEquals("2017/06/12 12:00:00", dateF.format(data.header.startDate));

      final RiderEntry entry5 = data.entries.get(5);
      assertEquals("2017/06/12 06:07:22", dateF.format(entry5.date));
      assertEquals(22, entry5.bearing.intValue());
      assertEquals("Lorem ipsum 6", entry5.text);

      importer.processThese(data);

      //
      //
      // // hmmm, how many tracks
      // assertEquals("got new tracks", 8, tLayers.size());
      //
      // final NarrativeWrapper narrLayer =
      // (NarrativeWrapper) tLayers.elementAt(1);
      // // correct final count
      // assertEquals("Got num lines", 371, narrLayer.size());
      //
      // // hey, let's have a look them
      // TrackWrapper tw = (TrackWrapper) tLayers.elementAt(4);
      // assertEquals("correct name", "M01_AAAA AAAA AAA (BBBB)", tw.getName());
      // assertEquals("got fixes", 3, tw.numFixes());
      //
      // // hey, let's have a look them
      // tw = (TrackWrapper) tLayers.elementAt(6);
      // assertEquals("correct name", "025_AAAA AAAA AAA (AAAA)", tw.getName());
      // assertEquals("got fixes", 5, tw.numFixes());
      //
      // // we need to introduce a 500ms delay, so we don't use
      // // the cahced visible period
      // Thread.sleep(550);
      //
      // final TimePeriod bounds = tw.getVisiblePeriod();
      // // in our sample data we have several FCSs at the same time,
      // // so we have to increment the DTG (seconds) on successive points.
      // // so,the dataset should end at 08:11:01 - since the last point
      // // had a second added.
      // assertEquals("correct bounds:", "Period:951212 080800 to 951212 081400",
      // bounds.toString());
      //
      // // hey, let's have a look tthem
      // tw = (TrackWrapper) tLayers.elementAt(7);
      // assertEquals("correct name", "027_AAAA AAAA AAA (AAAA)", tw.getName());
      // assertEquals("got fixes", 3, tw.numFixes());

    }

  }

  private static interface WordHelper
  {
    public String getHeaderCell(int num);

    public boolean hasMoreEntries();

    public String getCell(int i);
  }

  private static class DocXHelper implements WordHelper
  {
    final private XWPFTable _table;

    private DocXHelper(XWPFTable table)
    {
      _table = table;
    }

    @Override
    public String getHeaderCell(int num)
    {
      final XWPFTableRow headerRow = _table.getRow(1);
      final XWPFTableCell cell = headerRow.getCell(num);
      return cell.getText();
    }

    @Override
    public boolean hasMoreEntries()
    {
      int rows = _table.getRows().size();
      return ++rowCount < rows;
    }

    int rowCount = 2;

    @Override
    public String getCell(int i)
    {
      XWPFTableRow row = _table.getRow(rowCount);
      return row.getCell(i).getText();
    }

  }

  private static class DocHelper implements WordHelper
  {
    final private Table _table;

    private DocHelper(Table table)
    {
      _table = table;
    }

    @Override
    public String getHeaderCell(int num)
    {
      final TableRow headerRow = _table.getRow(1);
      final TableCell cell = headerRow.getCell(0);
      return cell.text();
    }

    @Override
    public boolean hasMoreEntries()
    {
      int rows = _table.numRows();
      return ++rowCount < rows;
    }

    int rowCount = 2;

    @Override
    public String getCell(int i)
    {
      TableRow row = _table.getRow(rowCount);
      return row.getCell(i).text();
    }

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

  private static List<String> SkipNames = null;

  private static QuestionHelper questionHelper;

  public static void setQuestionHelper(final QuestionHelper helper)
  {
    questionHelper = helper;
  }

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

    DATE_FORMAT = new SimpleDateFormat(
        "dd/MMM/yyyy");
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    TIME_FORMAT =
        new SimpleDateFormat("HHmmss:SS");
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

    final String textBit;
    if (thisN.bearing != null)
    {
      textBit = "[" + thisN.bearing + "]" + thisN.text;
    }
    else
    {
      textBit = thisN.text;
    }

    // sort out the time
    final long correctedDTG = header.startDate.getTime() + thisN.date.getTime();

    final NarrativeEntry ne =
        new NarrativeEntry(hisTrack, "Rider", new HiResDate(correctedDTG),
            textBit);

    // try to color the entry
    final Layer host = _layers.findLayer(trackFor(header.platform));
    if (host instanceof TrackWrapper)
    {
      final TrackWrapper tw = (TrackWrapper) host;
      ne.setColor(tw.getColor());
    }

    // and store it
    nw.add(ne);
  }

  private List<RiderEntry> entriesFor(final WordHelper helper, Header header,
      final SimpleDateFormat timeFormat) throws ParseException
  {
    final List<RiderEntry> res = new ArrayList<RiderEntry>();

    while (helper.hasMoreEntries())
    {
      // ok, parse this row
      final String dateStr = helper.getCell(0);
      final String bearingStr = helper.getCell(1);
      @SuppressWarnings("unused")
      final String nextStr = helper.getCell(2);
      final String text = helper.getCell(3);

      final Date date = timeFormat.parse(dateStr);
      Integer bearing;
      if (bearingStr.toUpperCase().equals("NA"))
      {
        bearing = null;
      }
      else
      {
        bearing = Integer.parseInt(bearingStr);
      }

      final Date newDate =
          new Date(header.startDate.getTime() + date.getTime());

      final RiderEntry entry = new RiderEntry(newDate, bearing, text);
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

  public TableBreakdown
      importFromWord(final String fName, final InputStream is)
  {
    TableBreakdown data = null;

    try
    {
      final HWPFDocument doc = new HWPFDocument(is);

      Range range = doc.getRange();

      TableIterator tIter = new TableIterator(range);

      Table table = tIter.next();

      DocHelper helper = new DocHelper(table);

      final Header header = headerFor(helper, DATE_FORMAT);
      final List<RiderEntry> entries = entriesFor(helper, header, TIME_FORMAT);
      data = new TableBreakdown(header, entries);

    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }
    catch (ParseException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return data;
  }

  public TableBreakdown
      importFromWordX(final String fName, final InputStream is)
  {
    TableBreakdown data = null;

    XWPFDocument doc = null;
    try
    {
      doc = new XWPFDocument(is);

      final List<XWPFTable> tables = doc.getTables();

      if (tables.size() > 1)
      {
        throw new IllegalArgumentException("Wrongly formatted document");
      }

      final XWPFTable myTable = tables.get(0);

      DocXHelper helper = new DocXHelper(myTable);

      final Header header = headerFor(helper, DATE_FORMAT);
      final List<RiderEntry> entries = entriesFor(helper, header, TIME_FORMAT);
      data = new TableBreakdown(header, entries);
    }
    catch (final IOException e)
    {
      e.printStackTrace();
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

    // find the outer time period - we only load data into the current time period
    TimePeriod outerPeriod = null;
    final Enumeration<Editable> layers = _layers.elements();
    while (layers.hasMoreElements())
    {
      final Layer thisL = (Layer) layers.nextElement();
      if (thisL instanceof WatchableList)
      {
        final WatchableList wl = (WatchableList) thisL;
        if (wl.getStartDTG() != null && wl.getEndDTG() != null)
        {
          final TimePeriod thisP =
              new TimePeriod.BaseTimePeriod(wl.getStartDTG(), wl.getEndDTG());
          if (outerPeriod == null)
          {
            outerPeriod = thisP;
          }
          else
          {
            outerPeriod.extend(wl.getStartDTG());
            outerPeriod.extend(wl.getEndDTG());
          }
        }
      }
    }

    // ok, now we can loop through the strings
    for (final RiderEntry thisN : data.entries)
    {
      // also remove any other control chars that may throw MS Word
      thisN.text = removeBadChars(thisN.text);

      final long combinedDate =
          data.header.startDate.getTime() + thisN.date.getTime();
      final HiResDate theDate = new HiResDate(combinedDate);

      // do we know the outer time period?
      if (outerPeriod != null && theDate != null)
      {
        // check it's in the currently loaded time period
        if (!outerPeriod.contains(theDate))
        {
          // ok, it's not in our period - jump to the next row
          continue;
        }
      }

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
   * do some pre-processing of text, to protect robustness of data written to file
   * 
   * @param raw_text
   * @return text with some control chars removed
   */
  private String removeBadChars(final String raw_text)
  {
    // swap soft returns for hard ones
    String res = raw_text.replace('\u000B', '\n');

    // we learned that whilst MS Word includes the following
    // control chars, and we can persist them via XML, we
    // can't restore them via SAX. So, swap them for
    // spaces
    res = res.replace((char) 1, (char) 32);
    res = res.replace((char) 19, (char) 32);
    res = res.replace((char) 20, (char) 32);
    res = res.replace((char) 21, (char) 32);
    res = res.replace((char) 5, (char) 32); // MS Word comment marker

    // done.
    return res;
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

  private String trackFor(final String originalName)
  {
    return trackFor(originalName, null);
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
