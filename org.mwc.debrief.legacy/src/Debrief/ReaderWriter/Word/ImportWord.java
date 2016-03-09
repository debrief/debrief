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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;

public class ImportWord
{

  private static List<String> SkipNames = null;

  /**
   * where we write our data
   * 
   */
  private final Layers _layers;

  public ImportWord(final Layers target)
  {
    _layers = target;

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

  public void importThis(final String fName, final InputStream is)
  {
    HWPFDocument doc = null;
    try
    {
      doc = new HWPFDocument(is);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    if (doc == null)
      return;

    // keep track of if we've added anything
    boolean dataAdded = false;

    Range r = doc.getRange();

    int lenParagraph = r.numParagraphs();
    for (int x = 0; x < lenParagraph; x++)
    {
      Paragraph p = r.getParagraph(x);
      String text = p.text();
      if (text.trim().length() == 0)
      {
        continue;
      }

      // ok, get the narrative type
      NarrEntry thisN = NarrEntry.create(text, x);

      if (thisN == null)
      {
        logError("Unable to parse line:" + text, null);
        continue;
      }

      switch (thisN.type)
      {
      case "FCS":
      {
        // add a narrative entry
        addEntry(thisN);

        // create track for this
        addFCS(thisN);

        // ok, take note that we've added something
        dataAdded = true;

        break;
      }
      default:
      {
        // ok, just add a narrative entry for anything not recognised
        
        // add a narrative entry
        addEntry(thisN);

        // ok, take note that we've added something
        dataAdded = true;

        break;

      }
      }
    }

    if (dataAdded)
    {
      _layers.fireModified(getNarrativeLayer());
    }
  }

  private void addFCS(NarrEntry thisN)
  {
    // ok, parse the message
    FCSEntry fe = new FCSEntry(thisN.text);

    // find the host
    TrackWrapper host =
        (TrackWrapper) _layers.findLayer(trackFor(thisN.platform));
    if (host != null)
    {
      // find the fix nearest this time
      Watchable[] nearest = host.getNearestTo(thisN.dtg);
      if (nearest != null && nearest.length > 0)
      {
        Watchable fix = nearest[0];
        // apply the offset
        WorldVector vec =
            new WorldVector(Math.toRadians(fe.brgDegs), new WorldDistance(
                fe.rangYds, WorldDistance.YARDS), new WorldDistance(0,
                WorldDistance.METRES));
        WorldLocation loc = fix.getLocation().add(vec);
        Fix tgtF = new Fix(thisN.dtg, loc, 0, 0);
        FixWrapper newF = new FixWrapper(tgtF);
        newF.resetName();

        // and the target track
        TrackWrapper tgt_track = (TrackWrapper) _layers.findLayer(fe.tgtName);

        if (tgt_track == null)
        {
          tgt_track = new TrackWrapper();
          tgt_track.setName(fe.tgtName);
          _layers.addThisLayer(tgt_track);
        }

        tgt_track.add(newF);
      }
      else
      {
        logError("Host fix not present for FCS at:" + thisN.dtg.getDate(), null);
      }
    }
  }

  public static void logThisError(String msg, Exception e)
  {
    Application.logError2(Application.WARNING, msg, e);
  }

  public void logError(String msg, Exception e)
  {
    logThisError(msg, e);
  }

  private void addEntry(NarrEntry thisN)
  {
    NarrativeWrapper nw = getNarrativeLayer();
    String hisTrack = trackFor(thisN.platform, thisN.platform);
    NarrativeEntry ne =
        new NarrativeEntry(hisTrack, thisN.type, new HiResDate(thisN.dtg),
            thisN.text);

    // try to color the entry
    Layer host = _layers.findLayer(trackFor(thisN.platform));
    if (host instanceof TrackWrapper)
    {
      TrackWrapper tw = (TrackWrapper) host;
      ne.setColor(tw.getColor());
    }

    // and store it
    nw.add(ne);
  }

  Map<String, String> nameMatches = new HashMap<String, String>();

  private String trackFor(String originalName)
  {
    return trackFor(originalName, null);
  }

  private String trackFor(String originalName, String name)
  {
    if (name == null)
    {
      name = originalName;
    }

    String platform = name.trim();
    String match = nameMatches.get(platform);
    if (match == null)
    {
      // search the layers
      Layer theL = _layers.findLayer(platform);
      if (theL != null)
      {
        match = theL.getName();
        nameMatches.put(originalName, match);
      }
      else
      {
        // try skipping then names
        Iterator<String> nameIter = SkipNames.iterator();
        while (nameIter.hasNext() && match == null)
        {
          String thisSkip = (String) nameIter.next();
          if (platform.startsWith(thisSkip))
          {
            String subStr = platform.substring(thisSkip.length()).trim();
            match = trackFor(originalName, subStr);
          }
        }
      }
    }

    return match;
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

  private static class FCSEntry
  {
    final double brgDegs;
    final double rangYds;
    final String tgtName;

    public FCSEntry(String msg)
    {
      String[] items = msg.trim().split(" ");
      String bT = items[0].split(":")[1];
      String rT = items[1].split(":")[1];
      tgtName = msg.substring(msg.lastIndexOf(":") + 1).trim();

      brgDegs = Double.parseDouble(bT);
      rangYds = Double.parseDouble(rT);
    }
  }

  private static class NarrEntry
  {
    HiResDate dtg;
    String type;
    String platform;
    String text;

    static public NarrEntry create(String msg, int lineNum)
    {
      NarrEntry res = null;
      try
      {
        res = new NarrEntry(msg);
        
        // just check it's valid
        boolean valid = (res.dtg != null) && (res.type != null) && (res.platform != null) && (res.text != null);
        if(!valid)
          res = null;
      }
      catch (ParseException e)
      {
        logThisError("Failed whilst parsing Word Document, at line:" + lineNum,
            e);
      }

      return res;
    }

    public NarrEntry(String entry) throws ParseException
    {
      DateFormat dateF = new SimpleDateFormat("HH:mm:ss");
      dateF.setTimeZone(TimeZone.getTimeZone("GMT"));

      String[] parts = entry.split(",");
      int ctr = 0;
      if (parts.length > 5)
      {
        String yrStr = parts[ctr++];
        String monStr = parts[ctr++];
        String dayStr = parts[ctr++];
        String timeStr = parts[ctr++];
        type = parts[ctr++].trim();
        platform = parts[ctr++].trim();

        @SuppressWarnings("deprecation")
        Date datePart =
            new Date(Integer.parseInt(yrStr) - 1900,
                Integer.parseInt(monStr) - 1, Integer.parseInt(dayStr));
        Date timePart = dateF.parse(timeStr);

        dtg = new HiResDate(new Date(datePart.getTime() + timePart.getTime()));

        // ok, and the message part
        int ind = entry.indexOf(platform);
        text = entry.substring(ind + platform.length() + 2).trim();
      }

    }
  }

  public static class TestImportAIS extends TestCase
  {
    private final static String doc_path =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/narrative.doc";

    public void testNameHandler()
    {
      Layers layers = new Layers();
      TrackWrapper track = new TrackWrapper();
      track.setName("Nelson");
      layers.addThisLayer(track);
      TrackWrapper track2 = new TrackWrapper();
      track2.setName("Iron Duck");
      layers.addThisLayer(track2);
      ImportWord iw = new ImportWord(layers);
      String match = iw.trackFor("HMS Boat", "HMS Boat");
      assertNull("not found match", match);
      match = iw.trackFor("HMS Nelson", "HMS Nelson");
      assertNotNull("found match", match);
      match = iw.trackFor("Hms Nelson", "Hms Nelson");
      assertNotNull("found match", match);
      match = iw.trackFor("RNAS Nelson", "RNAS Nelson");
      assertNotNull("found match", match);

      // check we've created new entries
      assertEquals("name matches", 3, iw.nameMatches.size());

      // and the two word name
      match = iw.trackFor("Hms Iron Duck", "Hms Iron Duck");
      assertNotNull("found match", match);

      // check we've created new entries
      assertEquals("name matches", 4, iw.nameMatches.size());

    }

    public void testParseFCS()
    {
      FCSEntry fe = new FCSEntry(" B:124 R:23434 Track:T_NAME");
      assertEquals("got range:", 23434d, fe.rangYds);
      assertEquals("got brg:", 124d, fe.brgDegs);
      assertEquals("got name:", "T_NAME", fe.tgtName);

      fe = new FCSEntry(" B:124.44 R:23434.2 Track:T NAME");
      assertEquals("got range:", 23434.2, fe.rangYds);
      assertEquals("got brg:", 124.44, fe.brgDegs);
      assertEquals("got name:", "T NAME", fe.tgtName);

    }

    public void testImportEmptyLayers() throws FileNotFoundException
    {
      String testFile = doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final Layers tLayers = new Layers();

      final ImportWord importer = new ImportWord(tLayers);
      importer.importThis(testFile, is);

      // hmmm, how many tracks
      assertEquals("got new tracks", 1, tLayers.size());
    }

    List<String> tstMessages = new ArrayList<String>();

    public void testImportHostPresentNoFixes() throws FileNotFoundException
    {
      tstMessages.clear();
      String testFile = doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final Layers tLayers = new Layers();

      TrackWrapper track = new TrackWrapper();
      track.setName("Nelson");
      tLayers.addThisLayer(track);

      final ImportWord importer = new ImportWord(tLayers)
      {

        @Override
        public void logError(String msg, Exception e)
        {
          tstMessages.add(msg);
        }

      };
      importer.importThis(testFile, is);

      // hmmm, how many tracks
      assertEquals("got new tracks", 2, tLayers.size());

      assertEquals("received messages", 19, tstMessages.size());
    }

    private FixWrapper createF(HiResDate dtg)
    {

      WorldLocation loc = new WorldLocation(2, 2, 2);
      Fix newF = new Fix(dtg, loc, 0, 0);
      FixWrapper fw = new FixWrapper(newF);
      return fw;
    }

    public void testImportHostPresentWithFixes() throws FileNotFoundException,
        ParseException
    {
      tstMessages.clear();
      String testFile = doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      DateFormat df = new SimpleDateFormat("yyyy,MM,dd,HH:mm:ss");

      final InputStream is = new FileInputStream(testI);

      final Layers tLayers = new Layers();

      TrackWrapper track = new TrackWrapper();
      track.setName("Nelson");
      tLayers.addThisLayer(track);

      track.add(createF(new HiResDate(df.parse("1995,12,12,06:21:32"))));
      track.add(createF(new HiResDate(df.parse("1995,12,12,06:34:32"))));
      track.add(createF(new HiResDate(df.parse("1995,12,12,06:56:32"))));

      final ImportWord importer = new ImportWord(tLayers)
      {

        @Override
        public void logError(String msg, Exception e)
        {
          tstMessages.add(msg);
        }

      };
      importer.importThis(testFile, is);

      // hmmm, how many tracks
      assertEquals("got new tracks", 4, tLayers.size());

      assertEquals("received messages", 2, tstMessages.size());
    }

  }
}
