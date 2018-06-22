/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.operations.spatial;

import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.SI.SECOND;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.CollectionComplianceTests.TimePeriod;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;

import org.eclipse.january.dataset.IDataset;

public class DopplerShiftBetweenTracksOperation implements IOperation
{
  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  public static class DopplerShiftOperation extends AbstractCommand
  {

    private static final String SOUND_SPEED = "SOUND_SPEED";
    private static final String LOC = "LOC";
    private static final String SPEED = "SPEED";
    private static final String COURSE = "COURSE";
    private static final String FREQ = "FREQ";
    private static final String TX = "TX_";

    /**
     * let the class organise a tidy set of data, to collate the assorted datasets
     * 
     */
    private transient HashMap<String, IStoreItem> _data;

    /**
     * keep track of which output document relates to which input one
     * 
     */
    private HashMap<TrackProvider, NumberDocument> _outputMap;

    /**
     * nominated transmitter
     * 
     */
    private final StoreGroup _tx;

    /**
     * nominated receivers
     * 
     */
    private List<TrackProvider> _allTracks;

    private final CollectionComplianceTests aTests =
        new CollectionComplianceTests();

    // final ICommand newC =
    // new DopplerShiftOperation(thisG, destination,
    // "Doppler between tracks (from " + thisG.getName() + ")",
    // "Calculate doppler between two tracks", selection, context);

    public DopplerShiftOperation(final StoreGroup transmitter,
        final IStoreGroup store, final String title, final String description,
        final List<IStoreItem> selection, IContext context)
    {
      super(title, description, store, false, false, selection, context);

      _tx = transmitter;
    }

    /**
     * find any selection items that we can use as tracks
     * 
     * @param ignoreMe
     * @param selection
     * @param aTests
     * @return
     */
    public static List<TrackProvider> getTracks(IStoreGroup ignoreMe,
        List<IStoreItem> selection, CollectionComplianceTests aTests)
    {
      List<TrackProvider> res = new ArrayList<TrackProvider>();

      Iterator<IStoreItem> iter = selection.iterator();
      while (iter.hasNext())
      {
        IStoreItem item = iter.next();
        if (item != ignoreMe)
        {
          if (item instanceof LocationDocument)
          {
            LocationDocument loc = (LocationDocument) item;
            res.add(new SingletonWrapper(loc.getName(), loc));
          }
          else if (item instanceof IStoreGroup)
          {
            // CHECK IF IT'S SUITABLE AS A TRACK. IF NOT, SEE IF IT JUST
            // CONTAINS LOCATIONS
            // - THEN ADD THEM ALL
            //
            IStoreGroup grp = (IStoreGroup) item;

            // see if this is a composite track
            // or, is this a conventional track
            if (aTests.isATrack(grp))
            {
              res.add(new CompositeTrackWrapper(grp, grp.getName()));
            }
            else
            {
              // see if this is a group of non-temporal locations
              Iterator<IStoreItem> iter2 = grp.iterator();
              while (iter2.hasNext())
              {
                IStoreItem iStoreItem = (IStoreItem) iter2.next();
                if (iStoreItem instanceof IDocument)
                {
                  Document<?> coll = (Document<?>) iStoreItem;
                  if (coll.size() == 1)
                  {
                    if (coll instanceof LocationDocument)
                    {
                      final LocationDocument loc = (LocationDocument) coll;
                      res.add(new SingletonWrapper(coll.getName(), loc));
                    }
                  }
                }
              }
            }
          }
        }
      }
      return res;
    }

    /**
     * way to make singleton locations (that don't have course/speed) look like tracks
     * 
     * @author ian
     * 
     */
    public interface TrackProvider
    {
      Point2D getLocationAt(long time);

      double getCourseAt(long time);

      double getSpeedAt(long time);

      String getName();
      
      IGeoCalculator getCalculator();

      /**
       * @param dsOperation
       */
      void addDependent(ICommand dsOperation);

    }

    protected static class SingletonWrapper implements TrackProvider
    {

      private final String _name;
      private LocationDocument _dataset;

      public SingletonWrapper(String name, LocationDocument loc)
      {
        _name = name;
        _dataset = loc;
      }

      @Override
      public Point2D getLocationAt(long time)
      {
        return _dataset.getLocationIterator().next();
      }

      @Override
      public double getCourseAt(long time)
      {
        return 0;
      }

      @Override
      public double getSpeedAt(long time)
      {
        return 0;
      }

      @Override
      public String getName()
      {
        return _name;
      }

      /*
       * (non-Javadoc)
       * 
       * @see info.limpet.data.operations.spatial.DopplerShiftBetweenTracksOperation
       * .DSOperation.TrackProvider#addDependent(info.limpet.IOperation)
       */
      @Override
      public void addDependent(ICommand operation)
      {
        _dataset.addDependent(operation);
      }

      @Override
      public IGeoCalculator getCalculator()
      {
        return _dataset.getCalculator();
      }

    }

    protected static class CompositeTrackWrapper implements TrackProvider
    {

      private final String _name;

      private CollectionComplianceTests aTests =
          new CollectionComplianceTests();
      private NumberDocument _course;
      private NumberDocument _speed;
      private LocationDocument _location;

      public CompositeTrackWrapper(IStoreGroup track, String name)
      {
        _name = name;

        // assign the components
        _course =
            aTests.findCollectionWith(track, Angle.UNIT.getDimension(), false);
        _speed =
            aTests.findCollectionWith(track, Velocity.UNIT.getDimension(),
                false);

        Iterator<IStoreItem> iter = track.iterator();
        while (iter.hasNext())
        {
          IStoreItem iStoreItem = (IStoreItem) iter.next();
          if (iStoreItem instanceof LocationDocument)
          {
            _location = (LocationDocument) iStoreItem;
          }
        }
      }

      /*
       * (non-Javadoc)
       * 
       * @see info.limpet.data.operations.spatial.DopplerShiftBetweenTracksOperation
       * .DSOperation.TrackProvider#addDependent(info.limpet.IOperation)
       */
      @Override
      public void addDependent(ICommand operation)
      {
        _course.addDependent(operation);
        _speed.addDependent(operation);
        _location.addDependent(operation);
      }

      @Override
      public Point2D getLocationAt(long time)
      {
        return _location.locationAt(time);
      }

      @Override
      public double getCourseAt(long time)
      {
        return aTests.valueAt(_course, time, RADIAN.asType(Angle.class));
      }

      @Override
      public double getSpeedAt(long time)
      {
        return aTests.valueAt(_speed, time, METRE.divide(SECOND).asType(
            Velocity.class));
      }

      @Override
      public String getName()
      {
        return _name;
      }

      @Override
      public IGeoCalculator getCalculator()
      {
        return _location.getCalculator();
      }

    }

    @Override
    public void execute()
    {
      // ok, sort out the data
      organiseData();

      // tell the inputs that we're a dependent
      for (IStoreItem c : _data.values())
      {
        IDocument<?> doc = (IDocument<?>) c;
        doc.addDependent(this);
      }

      // and the tracks
      for (TrackProvider c : _allTracks)
      {
        c.addDependent(this);
      }

      // do the caculation (it handles the outputs)
      recalculate(null);

      // put the outputs into the target
      for (IDocument<?> o : getOutputs())
      {
        getStore().add(o);
      }
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates
     * 
     * @param unit
     */
    private void performCalc()
    {
      // just check we've been organised (if we've been loaded from persistent
      // storage)
      organiseData();

      // and the bounding period
      final TimePeriod period = aTests.getBoundingRange(_data.values());

      // check it's valid
      if (period.invalid())
      {
        System.err.println("Insufficient coverage for datasets");
        return;
      }

      // ok, let's start by finding our time sync
      final IDocument<?> times = aTests.getOptimalIndex(period, _data.values());

      // check we were able to find some times
      if (times == null)
      {
        System.err.println("Unable to find time source dataset");
        return;
      }
            
      final IGeoCalculator calc = _allTracks.get(0).getCalculator();

      // ok, now loop through the receivers
      Iterator<TrackProvider> rIter = _allTracks.iterator();
      while (rIter.hasNext())
      {
        TrackProvider trackProvider = (TrackProvider) rIter.next();

        // find the relevant outputs dataset
        String thisOutName =
            getOutputNameFor(_tx.getName(), trackProvider.getName());

        NumberDocumentBuilder builder =
            new NumberDocumentBuilder(thisOutName, HERTZ
                .asType(Frequency.class), this, SampleData.MILLIS);

        // and now we can start looping through
        final Iterator<Double> tIter = times.getIndexIterator();
        while (tIter.hasNext())
        {
          final double thisTime = tIter.next();

          if (thisTime >= period.getStartTime()
              && thisTime <= period.getEndTime())
          {
            // ok, now collate our data
            final LocationDocument txLocDoc =
                (LocationDocument) _data.get(TX + LOC);
            final Point2D txLoc = txLocDoc.locationAt(thisTime);

            final double txCourseRads =
                aTests.valueAt((IDocument<?>) _data.get(TX + COURSE),
                    (long) thisTime, SI.RADIAN);

            final double txSpeedMSec =
                aTests.valueAt((IDocument<?>) _data.get(TX + SPEED),
                    (long) thisTime, SI.METERS_PER_SECOND);

            final double freq =
                aTests.valueAt((IDocument<?>) _data.get(TX + FREQ),
                    (long) thisTime, SI.HERTZ);

            final double soundSpeed =
                aTests.valueAt((IDocument<?>) _data.get(SOUND_SPEED),
                    (long) thisTime, SI.METERS_PER_SECOND);

            final Point2D rxLoc = trackProvider.getLocationAt((long) thisTime);
            final double rxCourseRads =
                trackProvider.getCourseAt((long) thisTime);
            final double rxSpeedMSec =
                trackProvider.getSpeedAt((long) thisTime);

            // check we have locations. During some property editing we receive
            // recalc call
            // after old value is removed, and before new value is added.
            if (txLoc != null && rxLoc != null)
            {
              // now find the bearing between them
              double angleDegs = calc.getAngleBetween(txLoc, rxLoc);

              if (angleDegs < 0)
              {
                angleDegs += 360;
              }

              final double angleRads = Math.toRadians(angleDegs);

              // ok, and the calculation
              final double shifted =
                  calcPredictedFreqSI(soundSpeed, txCourseRads, rxCourseRads,
                      txSpeedMSec, rxSpeedMSec, angleRads, freq);

              // store the result
              builder.add(thisTime, shifted);
            }
          }
        }

        // capture the output
        NumberDocument outputData = builder.toDocument();

        if (outputData == null)
        {
          throw new RuntimeException("Didn't generate any data");
        }

        NumberDocument targetDoc = _outputMap.get(trackProvider);
        if (targetDoc == null)
        {
          // ok, we need to create it
          _outputMap.put(trackProvider, outputData);

          // and store it as an output
          getOutputs().add(outputData);
        }
        else
        {
          // capture the name, so we don't overwrite it
          IDataset ds = outputData.getDataset();
          ds.setName(targetDoc.getName());

          // ok, just repalce teh contents
          targetDoc.setDataset(outputData.getDataset());
        }
      }
    }

    /**
     * 
     * @param speedOfSound
     * @param osHeadingRads
     * @param tgtHeadingRads
     * @param osSpeed
     * @param tgtSpeed
     * @param bearing
     * @param fNought
     * @return
     */
    private static double calcPredictedFreqSI(final double speedOfSound,
        final double osHeadingRads, final double tgtHeadingRads,
        final double osSpeed, final double tgtSpeed, final double bearing,
        final double fNought)
    {
      final double relB = bearing - osHeadingRads;

      // note - contrary to some publications TSL uses the
      // angle along the bearing, not the angle back down the bearing (ATB).
      final double angleOffTheOtherB = tgtHeadingRads - bearing;

      final double valOSL = Math.cos(relB) * osSpeed;
      final double valTSL = Math.cos(angleOffTheOtherB) * tgtSpeed;

      final double freq =
          fNought * (speedOfSound + valOSL) / (speedOfSound + valTSL);

      return freq;
    }

    private String getOutputNameFor(String tx, String rx)
    {
      return "Doppler shift between " + tx + " and " + rx;
    }

    private void organiseData()
    {
      if (_data == null)
      {
        // create the list of non-tx tracks
        _allTracks = getTracks(_tx, getInputs(), aTests);

        // ok, we need to collate the data
        _data = new HashMap<String, IStoreItem>();

        // and somewhere to store the results
        _outputMap = new HashMap<TrackProvider, NumberDocument>();

        final CollectionComplianceTests tests = new CollectionComplianceTests();

        // ok, transmitter data
        _data.put(TX + FREQ, tests.findCollectionWith(_tx, Frequency.UNIT
            .getDimension(), true));
        _data.put(TX + COURSE, tests.findCollectionWith(_tx, SI.RADIAN
            .getDimension(), true));
        _data.put(TX + SPEED, tests.findCollectionWith(_tx, METRE
            .divide(SECOND).getDimension(), true));
        _data.put(TX + LOC, tests.someHaveLocation(_tx));

        // and the sound speed
        _data.put(SOUND_SPEED, tests.findCollectionWith(getInputs(), METRE
            .divide(SECOND).getDimension(), false));
      }
    }

    @Override
    protected void recalculate(final IStoreItem subject)
    {
      // clear out the lists, first
      performCalc();

      // share the good news
      for (Document<?> o : getOutputs())
      {
        o.fireDataChanged();
      }
    }
  }

  protected boolean appliesTo(final List<IStoreItem> selection)
  {
    // ok, check we have two collections
    final boolean allTracks = aTests.getNumberOfTracks(selection) >= 2;
    final boolean someHaveFreq =
        aTests.findCollectionWith(selection, Frequency.UNIT.getDimension(),
            true) != null;
    final boolean topLevelSoundSpeed =
        aTests.findCollectionWith(selection, METRE.divide(SECOND)
            .getDimension(), false) != null;

    return allTracks && someHaveFreq && topLevelSoundSpeed;
  }

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  /**
   * utility operation to extract the location datasets from the selection (walking down into groups
   * as necessary)
   * 
   * @param selection
   * @return
   */
  protected List<IStoreItem> getLocationDatasets(
      final List<IStoreItem> selection)
  {
    final List<IStoreItem> collatedTracks = new ArrayList<IStoreItem>();

    // hmm, they may be composite tracks - extract the location data
    final Iterator<IStoreItem> sIter = selection.iterator();
    while (sIter.hasNext())
    {
      final IStoreItem iStoreItem = sIter.next();
      if (iStoreItem instanceof IStoreGroup)
      {
        final IStoreGroup group = (IStoreGroup) iStoreItem;
        final Iterator<IStoreItem> kids = group.iterator();
        while (kids.hasNext())
        {
          final IStoreItem thisItem = kids.next();
          if (thisItem instanceof LocationDocument)
          {
            final IStoreItem thisI = thisItem;
            collatedTracks.add(thisI);
          }
        }
      }
      else if (iStoreItem instanceof LocationDocument)
      {
        collatedTracks.add(iStoreItem);
      }
    }
    return collatedTracks;
  }

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      // get the list of tracks
      ArrayList<IStoreGroup> trackList = aTests.getChildTrackGroups(selection);

      // ok, loop through them
      Iterator<IStoreGroup> iter = trackList.iterator();
      while (iter.hasNext())
      {
        StoreGroup thisG = (StoreGroup) iter.next();
        final boolean hasFrequency =
            aTests.findCollectionWith(thisG, Frequency.UNIT.getDimension(),
                true) != null;
        if (hasFrequency)
        {
          final ICommand newC =
              new DopplerShiftOperation(thisG, destination,
                  "Doppler between tracks (from " + thisG.getName() + ")",
                  "Calculate doppler between two tracks", selection, context);
          res.add(newC);
        }
      }
    }

    return res;
  }
}
