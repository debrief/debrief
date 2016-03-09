package Debrief.ReaderWriter.Word;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
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
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
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

    Range r = doc.getRange();
    // StyleSheet styleSheet = doc.getStyleSheet();

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
      NarrEntry thisN;
      try
      {
        thisN = new NarrEntry(text);

        switch (thisN.type)
        {
        case "FCS":
        {
          // add a narrative entry
          NarrativeWrapper nw = getNarrativeLayer();
          String hisTrack = trackFor(thisN.platform, thisN.platform);
          NarrativeEntry ne =
              new NarrativeEntry(hisTrack, new HiResDate(thisN.dtg), thisN.text);
          nw.add(ne);

          // create track for this
          break;
        }
        default:
        {
          // add a plain narrative entry
          break;
        }
        }

        System.out.println("date:" + thisN.dtg + " content:" + thisN.text);

      }
      catch (ParseException e)
      {
        Application.logError2(1, "Failed whilst parsing Word Document,at line:"
            + x, e);
      }
    }
  }

  Map<String, String> nameMatches = new HashMap<String, String>();

  private String trackFor(String originalName, String name)
  {
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

  private static class NarrEntry
  {
    Date dtg;
    String type;
    String platform;
    String text;

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

        dtg = new Date(datePart.getTime() + timePart.getTime());

        // ok, and the message part
        int ind = entry.indexOf(platform);
        text = entry.substring(ind + platform.length() + 2);
      }

    }
  }

  public static class TestImportAIS extends TestCase
  {
    public void testFullImport() throws Exception
    {
      testImport("src/2003_2007.doc", 6);
    }

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

    private void testImport(final String testFile, final int len)
        throws Exception
    {
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final Layers tLayers = new Layers();

      final ImportWord importer = new ImportWord(tLayers);
      importer.importThis(testFile, is);

      // hmmm, how many tracks
      assertEquals("got new tracks", len, tLayers.size());

      final TrackWrapper thisT = (TrackWrapper) tLayers.findLayer("BW LIONESS");
      final Enumeration<Editable> fixes = thisT.getPositions();
      while (fixes.hasMoreElements())
      {
        final FixWrapper thisF = (FixWrapper) fixes.nextElement();
        System.out.println(thisF.getDateTimeGroup().getDate() + " COG:"
            + (int) Math.toDegrees(thisF.getCourse()) + " SOG:"
            + (int) thisF.getSpeed());

      }
    }

  }
}
