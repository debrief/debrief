package Debrief.ReaderWriter.powerPoint;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import Debrief.ReaderWriter.powerPoint.model.ExportNarrativeEntry;
import Debrief.ReaderWriter.powerPoint.model.Track;
import Debrief.ReaderWriter.powerPoint.model.TrackData;
import Debrief.ReaderWriter.powerPoint.model.TrackPoint;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class TrackParser
{
  private static final TrackParser instance = new TrackParser();

  public static TrackParser getInstance()
  {
    return instance;
  }

  private TrackParser()
  {

  }

  /**
   * It parses the given xml and returns a Track instance of it
   *
   * @param xml
   *          Track File as String
   * @return TrackData instance
   */
  public TrackData parse(final String xml)
  {
    final TrackData trackData = new TrackData();

    final Document soup = Jsoup.parse(xml, "", Parser.xmlParser());
    parseBasicInfo(trackData, soup);
    parseNarratives(trackData, soup);
    try
    {
      parseTracks(trackData, soup);
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }

    return trackData;
  }

  /**
   * Given a Soup Document and a Track, it retrieves the the dimensions, interval and name
   * 
   * @param trackData
   *          TrackData instance where we are going to insert the info
   * @param soup
   *          Soup file
   */
  private void parseBasicInfo(final TrackData trackData, final Document soup)
  {
    // We get the dimensions
    final Element dimensions = soup.selectFirst("dimensions");
    trackData.setWidth(Integer.parseInt(dimensions.attr("width")));
    trackData.setHeight(Integer.parseInt(dimensions.attr("height")));

    // We get the intervals
    final Elements interval = soup.select("interval");
    if (interval.isEmpty())
    {
      trackData.setIntervals(100);
    }
    else
    {
      trackData.setIntervals(Integer.parseInt(interval.get(0).attr("millis")));
    }

    final Elements name = soup.select("name");
    if (!name.isEmpty())
    {
      trackData.setName(name.get(0).text());
    }
  }

  /**
   * Given a Soup Document and a Track, it retrieves the the narratives
   * 
   * @param trackData
   *          TrackData instance where we are going to insert the info
   * @param soup
   *          Soup file
   */
  private void parseNarratives(final TrackData trackData, final Document soup)
  {
    final Elements narrativeEntries = soup.select("NarrativeEntries");

    if (!narrativeEntries.isEmpty())
    {
      final Elements entries = narrativeEntries.select("Entry");

      for (final Element entry : entries)
      {
        final ExportNarrativeEntry entryInstance = new ExportNarrativeEntry(
            entry.attr("Text"), entry.attr("dateStr"), entry.attr("elapsed"),
            null);
        trackData.getNarrativeEntries().add(entryInstance);
      }
    }
  }

  /**
   * Given a Soup Document and a Track, it retrieves the the narratives
   * 
   * @param trackData
   *          TrackData instance where we are going to insert the info
   * @param soup
   *          Soup file
   * @throws ParseException
   */
  private void parseTracks(final TrackData trackData, final Document soup)
      throws ParseException
  {
    final Elements tracks = soup.select("trk");
    for (final Element track : tracks)
    {
      final Track currentTrack = new Track(track.selectFirst("name").text(),
          track.selectFirst("color").text(), 0);

      for (final Element coordinate : track.select("trkpt"))
      {
        final TrackPoint point = new TrackPoint();
        point.setLongitude(Float.parseFloat(coordinate.attr("lon")));
        point.setLatitude(Float.parseFloat(coordinate.attr("lat")));

        final DateFormat dateTimeFormatter = new GMTDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'");
        final Date dateTime = dateTimeFormatter.parse(coordinate.selectFirst(
            "time").text());
        point.setTime(dateTime);

        point.setCourse(Float.parseFloat(coordinate.selectFirst("course")
            .text()));
        point.setElevation(Float.parseFloat(coordinate.selectFirst("ele")
            .text()));
        point.setSpeed(Float.parseFloat(coordinate.selectFirst("speed")
            .text()));

        point.setFormattedTime(coordinate.selectFirst("time").text());

        currentTrack.getSegments().add(point);
      }
      trackData.getTracks().add(currentTrack);
    }
  }
}
