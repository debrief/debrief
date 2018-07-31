package Debrief.ReaderWriter.powerPoint.test;

import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.junit.Test;

import Debrief.ReaderWriter.powerPoint.TrackParser;
import Debrief.ReaderWriter.powerPoint.model.NarrativeEntry;
import Debrief.ReaderWriter.powerPoint.model.Track;
import Debrief.ReaderWriter.powerPoint.model.TrackData;
import Debrief.ReaderWriter.powerPoint.model.TrackPoint;

public class TrackParserTest
{

  public TrackParserTest()
  {

  }

  final String sampleTrack = Utils.testFolder + File.separator + "TrackParser"
      + File.separator + "SampleTrack.txt";

  @Test
  public void testParse() throws IOException
  {
    final byte[] encoded = Files.readAllBytes(Paths.get(sampleTrack));
    final String trackXml = new String(encoded);
    TrackData result = TrackParser.getInstance().parse(trackXml);
    TrackData expectedResult = new TrackData();
    expectedResult.setHeight(634);
    expectedResult.setIntervals(200);
    expectedResult.setWidth(690);
    expectedResult.setName("Exported DebriefNG tracks");
    expectedResult.getNarrativeEntries().addAll(Arrays.asList(
        new NarrativeEntry[]
        {new NarrativeEntry("COMEX. Rule amendment Charlie 3", "120500.00",
            "0"), new NarrativeEntry("CONFIRMED. OBTAIN SOLUTION", "121003.00",
                "12120")}));
    Track track1 = new Track("COLLINGWOOD", new Color(0, 100, 189));

    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH:mm:ss'Z'");
    TrackPoint trackPoint = new TrackPoint();
    trackPoint.setCourse((float) 358.5);
    trackPoint.setElevation((float) .0);
    trackPoint.setLatitude((float) 56.0);
    trackPoint.setLongitude((float) 511.0);
    trackPoint.setSpeed((float) 1.8006);
    LocalDateTime dateTime = LocalDateTime.from(dateTimeFormatter.parse(
        "1995-12-12T05:05:00Z"));
    trackPoint.setTime(dateTime);
    track1.getSegments().add(trackPoint);
    expectedResult.getTracks().add(track1);

    Track track2 = new Track("NELSON", new Color(224, 28, 62));
    trackPoint = new TrackPoint();
    trackPoint.setCourse((float) 269.4);
    trackPoint.setElevation((float) .0);
    trackPoint.setLatitude((float) 585.0);
    trackPoint.setLongitude((float) 304.0);
    trackPoint.setSpeed((float) 1.0289);
    dateTime = LocalDateTime.from(dateTimeFormatter.parse(
        "1995-12-12T05:05:00Z"));
    trackPoint.setTime(dateTime);
    track2.getSegments().add(trackPoint);
    expectedResult.getTracks().add(track2);

    assertTrue(result.equals(expectedResult));
  }

}
