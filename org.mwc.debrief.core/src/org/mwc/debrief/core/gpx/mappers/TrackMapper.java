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
package org.mwc.debrief.core.gpx.mappers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.IStatus;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.gpx.TrackExtensionType;
import org.mwc.debrief.core.loaders.DebriefJaxbContextAware;
import org.w3c.dom.Node;

import com.topografix.gpx.v10.Gpx;
import com.topografix.gpx.v10.Gpx.Trk;
import com.topografix.gpx.v10.Gpx.Trk.Trkseg;
import com.topografix.gpx.v10.Gpx.Trk.Trkseg.Trkpt;
import com.topografix.gpx.v10.ObjectFactory;
import com.topografix.gpx.v11.ExtensionsType;
import com.topografix.gpx.v11.GpxType;
import com.topografix.gpx.v11.TrkType;
import com.topografix.gpx.v11.TrksegType;
import com.topografix.gpx.v11.WptType;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Properties.LocationPropertyEditor;
import junit.framework.TestCase;

/**
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 *
 *           <pre>
 * /plot/session/layers/track										/gpx/trk								Debrief.Wrappers.TrackWrapper
 * /plot/session/layers/track/TrackSegment 			/gpx/trk/trkseg 				Debrief.Wrappers.Track.TrackSegment
 * /plot/session/layers/track/TrackSegment/fix 	/gpx/trk/trkseg/trkpt 	Debrief.Wrappers.FixWrapper
 *           </pre>
 */
public class TrackMapper implements DebriefJaxbContextAware
{
  public static class TestGPXExport extends TestCase
  {
    private static TrackWrapper getData(final String name)
        throws FileNotFoundException
    {
      // get our sample data-file
      final ImportReplay importer = new ImportReplay();
      final Layers theLayers = new Layers();
      final String fName =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/" + name;
      final File inFile = new File(fName);
      assertTrue("input file exists", inFile.exists());
      final FileInputStream is = new FileInputStream(fName);
      importer.importThis(fName, is, theLayers);

      // sort out the sensors
      importer.storePendingSensors();

      // get the sensor track
      final TrackWrapper track = (TrackWrapper) theLayers.elements()
          .nextElement();

      return track;
    }

    public void testTrackExport() throws FileNotFoundException
    {
      final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();

      final TrackMapper mapper = new TrackMapper();

      List<Trk> res = mapper.toGpx10(tracks);

      assertEquals("empty list", 0, res.size());

      final TrackWrapper trk1 = getData("boat1.rep");

      tracks.add(trk1);

      res = mapper.toGpx10(tracks);

      assertEquals("has track", 1, res.size());

      final TrackWrapper trk2 = getData("boat2.rep");

      tracks.add(trk2);

      res = mapper.toGpx10(tracks);

      assertEquals("has track", 2, res.size());

      // check the data
      final Trk firstTrack = res.get(0);
      final Trkseg seg = firstTrack.getTrkseg().iterator().next();
      final Iterator<Trkpt> tIter = seg.getTrkpt().iterator();
      final Trkpt fix1 = tIter.next();

      assertEquals("correct lat", 22.186286d, fix1.getLat().doubleValue(),
          0.000001);
      assertEquals("correct lon", -21.697880, fix1.getLon().doubleValue(),
          0.000001);
      assertEquals("correct course", 269.7d, MWC.Algorithms.Conversions
          .Rads2Degs(fix1.getCourse().doubleValue()), 0.1);
      assertEquals("correct speed", 2d, MWC.Algorithms.Conversions.Mps2Kts(fix1
          .getSpeed().doubleValue()), 0.001);
      assertEquals("correct time", "1995-12-12T05:00:00Z", fix1.getTime()
          .toString());

      final Trkpt fix2 = tIter.next();

      assertEquals("correct lat", 22.186272d, fix2.getLat().doubleValue(),
          0.000001);
      assertEquals("correct lon", -21.700827, fix2.getLon().doubleValue(),
          0.000001);
      assertEquals("correct course", 269.7d, MWC.Algorithms.Conversions
          .Rads2Degs(fix2.getCourse().doubleValue()), 0.1);
      assertEquals("correct speed", 2d, MWC.Algorithms.Conversions.Mps2Kts(fix2
          .getSpeed().doubleValue()), 0.001);
      assertEquals("correct time", "1995-12-12T05:01:00Z", fix2.getTime()
          .toString());

    }
  }

  private static final ObjectFactory GPX_1_0_OBJ_FACTORY = new ObjectFactory();
  private final TrackSegmentMapper segmentMapper = new TrackSegmentMapper();
  private final FixMapper fixMapper = new FixMapper();

  private JAXBContext debriefContext;

  /**
   * @category gpx10
   */
  private void exportFixes(final TrackSegment seg, final Trkseg gpxSeg)
  {
    final Collection<Editable> pts = seg.getData();
    for (final Iterator<Editable> iterator = pts.iterator(); iterator
        .hasNext();)
    {
      final FixWrapper fix = (FixWrapper) iterator.next();
      gpxSeg.getTrkpt().add(fixMapper.toGpx10(fix));
    }
  }

  /**
   * @category gpx10
   */
  private void exportSegment(final Trk gpxTrack, final Editable nextElement)
  {
    final TrackSegment seg = (TrackSegment) nextElement;
    final Trkseg gpxSeg = GPX_1_0_OBJ_FACTORY.createGpxTrkTrkseg();
    gpxTrack.getTrkseg().add(gpxSeg);
    exportFixes(seg, gpxSeg);
  }

  /**
   * @category gpx11
   */
  public List<TrackWrapper> fromGpx(final GpxType gpx)
  {
    final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>(gpx.getTrk()
        .size());

    for (final TrkType gpxTrack : gpx.getTrk())
    {
      final TrackWrapper track = new TrackWrapper();

      mapGpxTrack(gpxTrack, track);

      for (final TrksegType gpxSegment : gpxTrack.getTrkseg())
      {
        final TrackSegment segment = segmentMapper.fromGpx(gpxSegment);
        track.add(segment);

        // keep track of the previous fix, in case we wish to calculate course
        // and speed
        FixWrapper previousFix = null;

        for (final WptType waypointType : gpxSegment.getTrkpt())
        {
          fixMapper.setJaxbContext(debriefContext);
          final FixWrapper fix = fixMapper.fromGpx(waypointType, previousFix);
          segment.add(fix);

          previousFix = fix;
        }
      }
      tracks.add(track);
    }
    return tracks;
  }

  /**
   * @category gpx10
   */
  public List<TrackWrapper> fromGpx10(final Gpx gpx)
  {
    final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>(gpx.getTrk()
        .size());

    for (final Gpx.Trk gpxTrack : gpx.getTrk())
    {
      final TrackWrapper track = new TrackWrapper();

      mapGpx10Track(gpxTrack, track);

      for (final Gpx.Trk.Trkseg gpxSegment : gpxTrack.getTrkseg())
      {
        final TrackSegment segment = segmentMapper.fromGpx10(gpxSegment);
        track.add(segment);

        // keep track of the previous fix, in case we wish to calculate course
        // and speed
        FixWrapper previousFix = null;

        for (final Gpx.Trk.Trkseg.Trkpt waypointType : gpxSegment.getTrkpt())
        {
          fixMapper.setJaxbContext(debriefContext);
          final FixWrapper fix = fixMapper.fromGpx10(waypointType, previousFix);
          segment.add(fix);

          previousFix = fix;
        }
      }
      tracks.add(track);
    }
    return tracks;
  }

  /**
   * @category gpx10
   */
  private void mapGpx10Track(final Trk gpxTrack, final TrackWrapper track)
  {
    track.setName(gpxTrack.getName());
    // Ignore handling of debrief extensions as they are not required for now
  }

  /**
   * @category gpx11
   */
  private void mapGpxTrack(final TrkType gpxTrack, final TrackWrapper track)
  {
    track.setName(gpxTrack.getName());

    try
    {
      final ExtensionsType extensions = gpxTrack.getExtensions();
      if (extensions != null)
      {
        final List<Object> any = extensions.getAny();

        final Unmarshaller unmarshaller = debriefContext.createUnmarshaller();
        final Object object = unmarshaller.unmarshal((Node) any.get(0));
        final TrackExtensionType trackExtension =
            (TrackExtensionType) JAXBIntrospector.getValue(object);

        track.setNameAtStart(trackExtension.isNameAtStart());
        track.setLineThickness(trackExtension.getLineThickness().intValue());
        track.setInterpolatePoints(trackExtension.isInterpolatePoints());
        track.setLinkPositions(trackExtension.isLinkPositions());
        track.setLineStyle(trackExtension.getLineStyle().intValue());
        final LocationPropertyEditor nameLocationConverter =
            new LocationPropertyEditor();
        nameLocationConverter.setAsText(trackExtension.getNameLocation());
        track.setNameLocation(((Integer) nameLocationConverter.getValue())
            .intValue());
        track.getSensors().setVisible(trackExtension.isSensorsVisible());
        track.getSolutions().setVisible(trackExtension.isSolutionsVisible());
        track.setNameVisible(trackExtension.isNameVisible());
        track.setPlotArrayCentre(trackExtension.isPlotArrayCentre());
        track.setPositionsVisible(trackExtension.isPositionsVisible());
        track.setLinkPositions(trackExtension.isLinkPositions());
        track.setVisible(trackExtension.isVisible());
        track.setSymbolType(trackExtension.getSymbol());
      }
    }
    catch (final JAXBException e)
    {
      CorePlugin.logError(IStatus.ERROR, "Error while mapping Track from GPX",
          e);
    }
  }

  @Override
  public void setJaxbContext(final JAXBContext ctx)
  {
    debriefContext = ctx;
  }

  public List<Trk> toGpx10(final List<TrackWrapper> tracks)
  {
    final List<Trk> gpxTracks = new ArrayList<Trk>(tracks.size());
    for (final TrackWrapper track : tracks)
    {
      final Trk gpxTrack = GPX_1_0_OBJ_FACTORY.createGpxTrk();
      gpxTrack.setName(track.getName());

      final Enumeration<Editable> segs = track.getSegments().elements();
      while (segs.hasMoreElements())
      {
        final Editable nextElement = segs.nextElement();

        if (nextElement instanceof TrackSegment)
        {
          exportSegment(gpxTrack, nextElement);
        }
        else
        {
          CorePlugin.logError(IStatus.INFO, "Ignoring " + nextElement
              + " while marshalling Track GPX as it is not a Fix", null);
        }
      }
      gpxTracks.add(gpxTrack);
    }
    return gpxTracks;
  }
}
