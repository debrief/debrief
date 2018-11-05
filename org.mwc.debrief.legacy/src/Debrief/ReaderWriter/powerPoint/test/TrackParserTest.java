package Debrief.ReaderWriter.powerPoint.test;

import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import Debrief.ReaderWriter.powerPoint.TrackParser;
import Debrief.ReaderWriter.powerPoint.model.ExportNarrativeEntry;
import Debrief.ReaderWriter.powerPoint.model.Track;
import Debrief.ReaderWriter.powerPoint.model.TrackData;
import Debrief.ReaderWriter.powerPoint.model.TrackPoint;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class TrackParserTest
{
  private static final String sampleTrack = Utils.testFolder + File.separator
      + "TrackParser" + File.separator + "SampleTrack.txt";

  @Test
  public void testParse() throws IOException, ParseException
  {
    final byte[] encoded = Files.readAllBytes(Paths.get(sampleTrack));
    final String trackXml = new String(encoded);
    final TrackData result = TrackParser.getInstance().parse(trackXml);
    final TrackData expectedResult = new TrackData();
    expectedResult.setHeight(634);
    expectedResult.setIntervals(200);
    expectedResult.setWidth(690);
    expectedResult.setName("Exported DebriefNG tracks");
    expectedResult.getNarrativeEntries().addAll(Arrays.asList(
        new ExportNarrativeEntry[]
        {new ExportNarrativeEntry("COMEX. Rule amendment Charlie 3",
            "120500.00", "0", null), new ExportNarrativeEntry(
                "CONFIRMED. OBTAIN SOLUTION", "121003.00", "12120", null)}));
    final Track track1 = new Track("COLLINGWOOD", new Color(0, 100, 189), 0);

    final DateFormat dateTimeFormatter = new GMTDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss'Z'");
    Date dateTime = dateTimeFormatter.parse("1995-12-12T05:05:00Z");
    TrackPoint trackPoint = new TrackPoint((float) 56.0, (float) 511.0,
        (float) .0, dateTime, "1995-12-12T05:05:00Z");
    track1.getPoints().add(trackPoint);
    expectedResult.getTracks().add(track1);

    final Track track2 = new Track("NELSON", new Color(224, 28, 62), 0);
    dateTime = dateTimeFormatter.parse("1995-12-12T05:05:00Z");
    trackPoint = new TrackPoint((float) 585.0, (float) 304.0, (float) .0,
        dateTime, "1995-12-12T05:05:00Z");
    track2.getPoints().add(trackPoint);
    expectedResult.getTracks().add(track2);

    assertTrue(result.equals(expectedResult));
  }

}
