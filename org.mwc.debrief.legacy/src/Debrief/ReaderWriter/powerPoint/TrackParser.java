/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
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
        final DateFormat dateTimeFormatter = new GMTDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'");
        final Date dateTime = dateTimeFormatter.parse(coordinate.selectFirst(
            "time").text());
        final TrackPoint point = new TrackPoint(Float.parseFloat(coordinate
            .attr("lat")), Float.parseFloat(coordinate.attr("lon")), Float
                .parseFloat(coordinate.selectFirst("ele").text()), dateTime,
            coordinate.selectFirst("time").text());

        currentTrack.getPoints().add(point);
      }
      trackData.getTracks().add(currentTrack);
    }
  }
}
