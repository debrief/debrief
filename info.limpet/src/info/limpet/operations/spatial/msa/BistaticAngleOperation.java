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
package info.limpet.operations.spatial.msa;

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
import info.limpet.operations.spatial.IGeoCalculator;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

public class BistaticAngleOperation implements IOperation
{

  public abstract class BistaticAngleCommand extends AbstractCommand
  {
    private final IDocument<?> _timeProvider;
    final private NumberDocumentBuilder _azimuthBuilder;
    final protected NumberDocumentBuilder _bistaticBuilder;
    final protected NumberDocumentBuilder _bistaticAspectBuilder;
    final private Unit<?> _outputUnits;
    final private LocationDocument _transmitter;
    final private LocationDocument _receiver;
    final private IStoreGroup _target;

    public BistaticAngleCommand(final List<IStoreItem> selection,
        final IStoreGroup target, final LocationDocument tx,
        final LocationDocument rx, final IStoreGroup store,
        final IDocument<?> timeProvider, final IContext context)
    {
      super("Bistatic angle at:" + target.getName() + " from " + tx.getName()
          + " to:" + rx.getName(), "Calculate bistatic angles", store, false,
          false, selection, context);

      // special processing.
      // we generate our own list of inputs, so clear the existing list
      getInputs().clear();

      _timeProvider = timeProvider;
      _transmitter = tx;
      _receiver = rx;
      _target = target;
      _outputUnits = SampleData.DEGREE_ANGLE;
      final Unit<?> indexUnits =
          _timeProvider == null ? null : SampleData.MILLIS;

      final String elements =
          "[" + _transmitter + "] > [" + _target.getName() + "] > ["
              + _receiver + "]";

      _azimuthBuilder =
          new NumberDocumentBuilder("Azimuth Angle for " + elements,
              _outputUnits, this, indexUnits);
      _bistaticBuilder =
          new NumberDocumentBuilder("Bistatic Angle for " + elements,
              _outputUnits, this, indexUnits);
      _bistaticAspectBuilder =
          new NumberDocumentBuilder("Bistatic Aspect Angle for " + elements,
              _outputUnits, this, indexUnits);
    }

    @Override
    public void execute()
    {
      // perform the calculation
      performCalc();

      // get the output documents
      final NumberDocument biDataset = _bistaticBuilder.toDocument();
      final NumberDocument biADataset = _bistaticAspectBuilder.toDocument();
      final NumberDocument azDataset = _azimuthBuilder.toDocument();

      // now create the output dataset

      // store the output
      super.addOutput(biDataset);
      super.addOutput(biADataset);
      super.addOutput(azDataset);

      // tell each series that we're a dependent
      for (final IStoreItem doc : getInputs())
      {
        final IDocument<?> idoc = (IDocument<?>) doc;
        idoc.addDependent(this);
      }

      // ok, done
      getStore().add(biDataset);
      getStore().add(biADataset);
      getStore().add(azDataset);

      // tell the output it's been updated (by now it should
      // have a full set of listeners
      biDataset.fireDataChanged();
      biADataset.fireDataChanged();
      azDataset.fireDataChanged();
    }

    /**
     * produce a name for the output document
     * 
     * @return
     */
    protected String getOutputName()
    {
      return "Bistatic angle sets at " + getInputs().get(1).getName();
    }

    /**
     * reset the builder
     * 
     */
    private void init()
    {
      _bistaticBuilder.clear();
      _bistaticAspectBuilder.clear();
      _azimuthBuilder.clear();
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates
     * 
     * @param unit
     */
    private void performCalc()
    {
      // clear the output data
      init();

      // get the tracks
      final IStoreGroup target = _target;

      // get the location datasets
      final List<IStoreItem> tgtDocs = new ArrayList<IStoreItem>();
      tgtDocs.addAll(target);
      final NumberDocument tgt_hdg =
          aTests.findCollectionWith(tgtDocs, SI.RADIAN.getDimension(), true);
      final LocationDocument tx_track = _transmitter;
      final LocationDocument tgt_track = aTests.getFirstLocation(target);
      final LocationDocument rx_track = _receiver;

      // ok, if this is the first pass, store these documents as the inputs
      if (getInputs().size() == 0)
      {
        getInputs().add(tgt_hdg);
        getInputs().add(tx_track);
        getInputs().add(rx_track);
        getInputs().add(tgt_track);
      }

      // get a calculator to use
      final IGeoCalculator calc = tx_track.getCalculator();
      final IDocument<?> times;

      if (_timeProvider == null)
      {
        return;
      }

      // and the bounding period
      final Collection<IStoreItem> selection = new ArrayList<IStoreItem>();
      selection.add(tx_track);
      selection.add(rx_track);
      selection.add(tgt_track);
      selection.add(tgt_hdg);

      final TimePeriod period = aTests.getBoundingRange(selection);

      // check it's valid
      if (period.invalid())
      {
        throw new IllegalArgumentException("Insufficient coverage for datasets");
      }

      // ok, let's start by finding our time sync
      times = aTests.getOptimalIndex(period, selection);

      // check we were able to find some times
      if (times == null)
      {
        throw new IllegalArgumentException("Unable to find time source dataset");
      }

      // reduce the times data to the correct intervals & period
      final Document<?> trimmed_times = trimTheTimes(times, period);

      // ok, produce the sets of intepolated positions, at the specified times
      final LocationDocument interp_tx =
          locationsFor(tx_track, trimmed_times, period);
      final LocationDocument interp_tgt =
          locationsFor(tgt_track, trimmed_times, period);
      final LocationDocument interp_rx =
          locationsFor(rx_track, trimmed_times, period);
      final NumberDocument interp_headings =
          numbersFor(tgt_hdg, (Document<?>) times, period);

      // ok, get ready to walk through them
      final Iterator<Point2D> txIter = interp_tx.getLocationIterator();
      final Iterator<Point2D> tgtIter = interp_tgt.getLocationIterator();
      final Iterator<Point2D> rxIter = interp_rx.getLocationIterator();
      final Iterator<Double> hdgIter = interp_headings.getIterator();
      final Iterator<Double> timeIter = trimmed_times.getIndexIterator();

      while (timeIter.hasNext())
      {
        final Point2D txP = txIter.next();
        final Point2D targetP = tgtIter.next();
        final Point2D rxP = rxIter.next();
        final double heading = hdgIter.next();
        final Double time = timeIter.next();
        calcAndStore(calc, txP, targetP, rxP, heading, time, _bistaticBuilder,
            _bistaticAspectBuilder, _azimuthBuilder);
      }
    }

    @Override
    protected void recalculate(final IStoreItem subject)
    {
      // clear out the lists, first
      performCalc();

      // get the output documents
      final NumberDocument biDataset = _bistaticBuilder.toDocument();
      final NumberDocument biADataset = _bistaticAspectBuilder.toDocument();

      // get the existing outputs
      final NumberDocument realBi = (NumberDocument) getOutputs().get(0);
      final NumberDocument realBiA = (NumberDocument) getOutputs().get(1);

      realBi.copy(biDataset);
      realBiA.copy(biADataset);

      // and fire updates
      realBi.fireDataChanged();
      realBiA.fireDataChanged();
    }

    private Document<?> trimTheTimes(final IDocument<?> times,
        final TimePeriod period)
    {
      final Document<?> trimmed_times;
      if (times instanceof LocationDocument)
      {
        trimmed_times =
            locationsFor((LocationDocument) times, (Document<?>) times, period);
      }
      else if (times instanceof NumberDocument)
      {
        trimmed_times =
            numbersFor((NumberDocument) times, (Document<?>) times, period);
      }
      else
      {
        trimmed_times = null;
      }
      return trimmed_times;
    }
  }

  /**
   * make this method more visible, for testing
   * 
   * @param calc
   *          utility calculator
   * @param tx
   *          location of transmitter
   * @param target
   *          location of target
   * @param rx
   *          location of receiver
   * @param heading
   *          heading of target
   * @param time
   *          time of observation
   * @param bistaticBuilder
   *          where to store the bistatic angle
   * @param bistaticAspectBuilder
   *          where to store the bistatic aspect angle
   * @param azimuthBuilder
   */
  public static void calcAndStore(final IGeoCalculator calc, final Point2D tx,
      final Point2D target, final Point2D rx, final Double heading,
      final Double time, final NumberDocumentBuilder bistaticBuilder,
      final NumberDocumentBuilder bistaticAspectBuilder,
      final NumberDocumentBuilder azimuthBuilder)
  {
    // ok start with two angles
    final double toSource = calc.getAngleBetween(target, tx);
    final double toReceiver = calc.getAngleBetween(target, rx);

    // make them relative
    double relToSource = toSource - heading;
    final double relToReceiver = toReceiver - heading;

    // and the bistatic angle
    final double biAngle = Math.abs(relToReceiver - relToSource);

    // which angle to we add the bisector to?
    final double baseAngle = Math.min(relToSource, relToReceiver);

    double biAspectAngle = baseAngle + biAngle / 2d;

    if (biAspectAngle < -180d)
    {
      biAspectAngle += 360d;
    }

    // make sure they're positive
    if (relToSource < 0)
    {
      relToSource += 360d;
    }

    // and make sure it's positive
    biAspectAngle = Math.abs(biAspectAngle);

    bistaticBuilder.add(time, biAngle);
    bistaticAspectBuilder.add(time, biAspectAngle);
    azimuthBuilder.add(time, relToSource);
  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> rawSelection,
      final IStoreGroup destination, final IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();

    // check we have three items selected, for the three tracks
    if (rawSelection.size() == 3)
    {
      // find which ones have heading
      final List<IStoreGroup> tracksWithHeading =
          getSuitableTracks(rawSelection, true);
      
      for(final IStoreGroup thisTarget: tracksWithHeading)
      {
        // ok, get the location
        final LocationDocument targetTrack =
            aTests.getFirstLocation(thisTarget);

        // now the heading
        final NumberDocument heading =
            aTests.findCollectionWith(thisTarget, SampleData.DEGREE_ANGLE
                .getDimension(), true);

        final TimePeriod tracksPeriod = trackIntersectionFor(rawSelection);

        // check it's indexed
        if (heading.isIndexed())
        {
          // ok, check it's in the relevant time period
          final TimePeriod hdgBounds = aTests.getBoundsFor(heading);

          //
          if (!tracksPeriod.overlaps(hdgBounds))
          {
            return res;
          }
          else
          {
            // ok, we can create a command for this permutation

            // loop through all the tracks, to find the rx/tx
            List<LocationDocument> subjects = new ArrayList<LocationDocument>();
            for(final IStoreItem item: rawSelection)
            {
              // check it's not us.
              if (!item.equals(thisTarget))
              {
                if (item instanceof StoreGroup)
                {
                  final StoreGroup group = (StoreGroup) item;
                  final LocationDocument firstLoc =
                      aTests.getFirstLocation(group);
                  if (firstLoc != null)
                  {
                    subjects.add(firstLoc);
                  }
                }
                else if (item instanceof LocationDocument)
                {
                  // ok, it's one of the others, add it
                  subjects.add((LocationDocument) item);
                }
              }
            }

            if (subjects.size() != 2)
            {
              return res;
            }
            final LocationDocument tx1 = subjects.get(0);
            final LocationDocument rx1 = subjects.get(1);

            final LocationDocument tx2 = subjects.get(1);
            final LocationDocument rx2 = subjects.get(0);

            // ok, and the command
            res.add(new BistaticAngleCommand(rawSelection, thisTarget, tx1,
                rx1, destination, targetTrack, context)
            {

              @Override
              protected String getOutputName()
              {
                return getContext().getInput("Generate bearing",
                    NEW_DATASET_MESSAGE,
                    "Bearing between " + super.getSubjectList());
              }

            });
            // ok, and the command
            res.add(new BistaticAngleCommand(rawSelection, thisTarget, tx2,
                rx2, destination, targetTrack, context)
            {

              @Override
              protected String getOutputName()
              {
                return getContext().getInput("Generate bearing",
                    NEW_DATASET_MESSAGE,
                    "Bearing between " + super.getSubjectList());
              }

            });
          }
        }
        else
        {
          // ok, carry on with the next one
          continue;
        }
      }
    }
    return res;
  }

  @SuppressWarnings("unused")
  private boolean appliesTo(final List<IStoreItem> datasets)
  {
    // ok, now check they overlap
    final boolean nonEmpty = getATests().nonEmpty(datasets);
    final boolean equalLength = getATests().allEqualLengthOrSingleton(datasets);
    final boolean canInterpolate =
        getATests().suitableForIndexedInterpolation(datasets);
    final boolean atLeast4 = datasets.size() >= 4;
    final boolean hasContents = getATests().allHaveData(datasets);
    final boolean equalOrInterp = equalLength || canInterpolate;

    return nonEmpty && equalOrInterp && atLeast4 && hasContents;
  }

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  /**
   * utility operation to extract the tracks from the selection (walking down into groups as
   * necessary)
   * 
   * @param selection
   * @return
   */
  private List<IStoreGroup> getSuitableTracks(final List<IStoreItem> selection,
      final boolean needCourse)
  {
    final List<IStoreGroup> collatedTracks = new ArrayList<IStoreGroup>();

    // hmm, they may be composite tracks - extract the location data
    final Iterator<IStoreItem> sIter = selection.iterator();
    while (sIter.hasNext())
    {
      final IStoreItem iStoreItem = sIter.next();
      if (iStoreItem instanceof IStoreGroup)
      {
        final IStoreGroup group = (IStoreGroup) iStoreItem;
        if (aTests.isATrack(group, false, needCourse))
        {
          collatedTracks.add(group);
        }
      }
    }
    return collatedTracks;
  }

  private TimePeriod trackIntersectionFor(final List<IStoreItem> allTracks)
  {
    final List<IStoreItem> tracks = new ArrayList<IStoreItem>();
    final Iterator<IStoreItem> iter = allTracks.iterator();
    while (iter.hasNext())
    {
      final IStoreItem next = iter.next();
      if (next instanceof StoreGroup)
      {
        final IStoreGroup track = (IStoreGroup) next;
        // ok, get the tarck
        final LocationDocument doc = aTests.getFirstLocation(track);
        tracks.add(doc);
      }
      else if (next instanceof LocationDocument)
      {
        tracks.add(next);
      }
    }
    final TimePeriod period = aTests.getBoundingRange(tracks);
    return period;
  }

}
