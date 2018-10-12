package Debrief.Tools.Tote.Calculations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.PolygonWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.EarthModel;
import MWC.Algorithms.EarthModels.CompletelyFlatEarth;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import junit.framework.TestCase;

public class DistanceToFeature
{
  public static interface DataStorer
  {
    void store(long time, WorldDistance distance);
  }

  public static class TestFeatureDistance extends TestCase
  {

    private final static String ownship_track =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/Polyline_Measurement.rep";

    public void testSetPolygon() throws FileNotFoundException
    {
      final EarthModel oldModel = WorldLocation.getModel();

      // force a flat earth model
      WorldLocation.setModel(new CompletelyFlatEarth());

      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, tLayers);

      assertEquals("read in data", 3, tLayers.size());

      final TrackWrapper track = (TrackWrapper) tLayers.elementAt(1);
      assertEquals("track name", "TRACK", track.getName());
      final Layer shapes = tLayers.elementAt(2);
      final PolygonWrapper polyline = (PolygonWrapper) shapes.elements()
          .nextElement();

      final ArrayList<Long> times = new ArrayList<Long>();
      final ArrayList<WorldDistance> dist = new ArrayList<WorldDistance>();

      final DataStorer storer = new DataStorer()
      {
        @Override
        public void store(final long time, final WorldDistance distance)
        {
          System.out.println(new Date(time) + " - " + distance.getValueIn(
              WorldDistance.METRES));
          times.add(time);
          dist.add(distance);
        }
      };
      calculateDistanceBetween(polyline, track, storer);

      // restore the earth model
      WorldLocation.setModel(oldModel);
    }
  }

  public static void calculateDistanceBetween(final PolygonWrapper polyline,
      final WatchableList track, final DataStorer storer)
  {
    // go through track

    final Collection<Editable> points = track.getItemsBetween(new HiResDate(0),
        new HiResDate(new Date().getTime()));

    if (points != null)
    {

      final Iterator<Editable> pIter = points.iterator();

      while (pIter.hasNext())
      {
        final Editable next = pIter.next();
        if (next instanceof FixWrapper)
        {
          final FixWrapper fix = (FixWrapper) next;
          final WorldDistance dist = distanceFrom(polyline.getPoly(), fix
              .getLocation());

          if (dist != null)
          {
            storer.store(fix.getDTG().getDate().getTime(), dist);
          }
        }
      }
    }
  }

  private static WorldDistance distanceFrom(final PolygonShape polyline,
      final WorldLocation worldLocation)
  {
    // loop through the legs
    final Enumeration<Editable> points = polyline.elements();

    WorldLocation last = null;
    WorldDistance shortest = null;
    while (points.hasMoreElements())
    {
      final WorldLocation thisL = ((PolygonNode) points.nextElement())
          .getLocation();
      if (last != null)
      {
        final WorldDistance dist = worldLocation.rangeFrom(last, thisL);

        if (shortest == null)
        {
          shortest = dist;
        }
        else if (shortest.greaterThan(dist))
        {
          shortest = dist;
        }
      }
      last = thisL;
    }
    return shortest;
  }

}
