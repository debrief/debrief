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
package info.limpet.impl;

import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.SI.SECOND;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.operations.arithmetic.simple.AddQuantityOperation;
import info.limpet.operations.arithmetic.simple.MultiplyQuantityOperation;
import info.limpet.operations.spatial.GeoSupport;
import info.limpet.operations.spatial.IGeoCalculator;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.StringDataset;

public class SampleData
{
  public static final Unit<?> MILLIS = SI.MILLI(SECOND).asType(
      Duration.class);

  public static final String SPEED_DATA_FOLDER = "Speed data";
  public static final String SPEED_THREE_LONGER = "Speed Three (longer)";
  public static final String SPEED_IRREGULAR2 = "Speed two irregular time";
  public static final String TIME_INTERVALS = "Time intervals";
  public static final String TIME_STAMPS_1 = "Time stamps (early)";
  public static final String TIME_STAMPS_2 = "Time stamps (late)";
  public static final String STRING_TWO = "String two";
  public static final String STRING_ONE = "String one";
  public static final String LENGTH_SINGLETON = "Length Singleton";
  public static final String LENGTH_TWO = "Length Two non-Time";
  public static final String LENGTH_ONE = "Length One non-Time";
  public static final String ANGLE_ONE = "Angle One Time";
  public static final String FREQ_ONE = "Freq One";
  public static final String SPEED_ONE = "Speed One Time";
  public static final String SPEED_TWO = "Speed Two Time";
  public static final String SPEED_FOUR = "Speed Four Time";
  public static final String SPEED_ONE_RAD = "Speed One Rad Noise time";
  public static final String RAD_NOISE_LOOKUP = "Radiated Noise Lookup";
  public static final String TRACK_ONE = "Track One Time";
  public static final String COMPOSITE_ONE = "Composite Track One";
  public static final String TRACK_TWO = "Track Two Time";
  public static final String TRACK_2D_ONE = "Track Two 2D Time";
  public static final String TRACK_2D_TWO = "Track Two 2D Time";
  public static final String SPEED_EARLY = "Speed Two Time (earlier)";
  public static final String RANGED_SPEED_SINGLETON = "Ranged Speed Singleton";
  public static final String FLOATING_POINT_FACTOR = "Floating point factor";
  public static final String SINGLETON_LOC_1 = "Single Track One";
  public static final String SINGLETON_LOC_2 = "Single Track Two";

  private static class ObjectColl extends ArrayList<String>
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    final private String _name;
    final private ICommand _predecessor;

    public ObjectColl(String name, ICommand predecessor)
    {
      _name = name;
      _predecessor = predecessor;
    }

    public IStoreItem toDocument()
    {
      StringDataset dataset =
          (StringDataset) DatasetFactory.createFromObject(this);
      dataset.setName(_name);
      IDocument<?> res = new StringDocument(dataset, _predecessor);
      return res;
    }
  }

  public static final Unit<?> DEGREE_ANGLE = RADIAN.times(Math.PI / 180d);

  public StoreGroup getData(long count)
  {
    final StoreGroup list = new StoreGroup("Sample Data");
    IContext context = new MockContext();
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    // // collate our data series
    NumberDocumentBuilder freq1 =
        new NumberDocumentBuilder(FREQ_ONE, HERTZ.asType(Frequency.class),
            null, null);
    NumberDocumentBuilder angle1 =
        new NumberDocumentBuilder(ANGLE_ONE, DEGREE_ANGLE.asType(Angle.class),
            null, SampleData.MILLIS);
    NumberDocumentBuilder speedSeries1b =
        new NumberDocumentBuilder(SPEED_ONE, METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    NumberDocumentBuilder speedSeries2b =
        new NumberDocumentBuilder(SPEED_TWO, METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    NumberDocumentBuilder speedSeries3 =
        new NumberDocumentBuilder(SPEED_THREE_LONGER, METRE.divide(SECOND)
            .asType(Velocity.class), null, SampleData.MILLIS);
    NumberDocumentBuilder speedEarly1 =
        new NumberDocumentBuilder(SPEED_EARLY, METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    NumberDocumentBuilder speedIrregularB =
        new NumberDocumentBuilder(SPEED_IRREGULAR2, METRE.divide(SECOND)
            .asType(Velocity.class), null, SampleData.MILLIS);
    NumberDocumentBuilder speedSeries4 =
        new NumberDocumentBuilder(SPEED_FOUR, METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    NumberDocumentBuilder speedRadNoise =
        new NumberDocumentBuilder(SPEED_ONE_RAD, NonSI.DECIBEL, null,
            SampleData.MILLIS);
    NumberDocumentBuilder radNoiseLookup =
        new NumberDocumentBuilder(RAD_NOISE_LOOKUP, NonSI.DECIBEL, null, METRE
            .divide(SECOND).asType(Velocity.class));
    NumberDocumentBuilder length1 =
        new NumberDocumentBuilder(LENGTH_ONE, METRE.asType(Length.class), null,
            null);
    NumberDocumentBuilder length2 =
        new NumberDocumentBuilder(LENGTH_TWO, METRE.asType(Length.class), null,
            null);
    ObjectColl string1 = new ObjectColl(STRING_ONE, null);
    ObjectColl string2 = new ObjectColl(STRING_TWO, null);
    NumberDocumentBuilder singleton1 =
        new NumberDocumentBuilder(FLOATING_POINT_FACTOR, Dimensionless.UNIT,
            null, null);
    NumberDocumentBuilder singletonRange1b =
        new NumberDocumentBuilder(RANGED_SPEED_SINGLETON, METRE.divide(SECOND)
            .asType(Velocity.class), null, null);
    NumberDocumentBuilder singletonLength =
        new NumberDocumentBuilder(LENGTH_SINGLETON, METRE.asType(Length.class),
            null, null);
    NumberDocumentBuilder timeIntervals =
        new NumberDocumentBuilder(TIME_INTERVALS,
            SECOND.asType(Duration.class), null, SampleData.MILLIS);
    NumberDocumentBuilder timeStamps_1 =
        new NumberDocumentBuilder(TIME_STAMPS_1, SECOND.asType(Duration.class),
            null, null);
    NumberDocumentBuilder timeStamps_2 =
        new NumberDocumentBuilder(TIME_STAMPS_2, SECOND.asType(Duration.class),
            null, null);
    LocationDocumentBuilder track1 =
        new LocationDocumentBuilder(TRACK_ONE, null, SampleData.MILLIS);
    LocationDocumentBuilder track2 =
        new LocationDocumentBuilder(TRACK_TWO, null, SampleData.MILLIS);
    LocationDocumentBuilder singleLoc1 =
        new LocationDocumentBuilder(SINGLETON_LOC_1, null, null);
    LocationDocumentBuilder singleLoc2 =
        new LocationDocumentBuilder(SINGLETON_LOC_2, null, null);
    LocationDocumentBuilder track1_2D =
        new LocationDocumentBuilder(TRACK_2D_ONE, null, SampleData.MILLIS,
            SI.METER);
    LocationDocumentBuilder track2_2D =
        new LocationDocumentBuilder(TRACK_2D_TWO, null, SampleData.MILLIS,
            SI.METER);

    long thisTime = 0;

    // get ready for the track generation
    final IGeoCalculator calc = GeoSupport.getCalculatorWGS84();
    Point2D pos1 = calc.createPoint(-4, 55.8);
    Point2D pos2 = calc.createPoint(-4.2, 54.9);

    final long interval = 500L * 60;

    for (int i = 1; i <= count; i++)
    {
      thisTime = new Date().getTime() + i * interval;

      final long earlyTime = thisTime - (1000 * 60 * 60 * 24 * 365 * 20);

      angle1.add(thisTime, 90 + 1.1 * Math.toDegrees(Math.sin(Math
          .toRadians(i * 52.5))));
      final double thisSpeedOne = 6 + 4d * Math.sin(Math.toRadians(i));
      final double thisRadNoise = Math.pow(thisSpeedOne, 2.1);
      speedSeries1b.add(thisTime, thisSpeedOne);
      speedSeries2b.add(thisTime, 7 + 2 * Math.sin(i));
      speedRadNoise.add(thisTime, thisRadNoise);

      // we want the irregular series to only have occasional
      if (i % 3 == 0)
      {
        speedIrregularB.add(thisTime + 500 * 45, 7 + 2 * Math.sin(i + 1));
      }
      else
      {
        if (i % 7 > 4)
        {
          speedIrregularB.add(thisTime + 500 * 25 * 2, 7 + 2 * Math.sin(i - 1));
        }
      }

      speedSeries3.add(thisTime, 3d * Math.cos(i));
      speedSeries4.add(thisTime, 3 + 2d * Math.cos(i / 10));
      speedEarly1.add(earlyTime, Math.sin(i));
      length1.add((double) i % 3);
      length2.add((double) i % 5);
      string1.add("item " + i);
      string2.add("item " + (i % 3));
      timeIntervals.add(thisTime, (4 + Math.sin(Math.toRadians(i) + 3.4
          * Math.random())));

      if (i < ((double) count) * 0.4)
      {
        if (i % 6 > 2)
        {
          timeStamps_1.add(thisTime - interval
              + (interval * 2d * Math.random()));
        }
      }
      if (i > ((double) count) * 0.6)
      {
        if (i % 6 > 2)
        {
          timeStamps_2.add(thisTime - interval
              + (interval * 2d * Math.random()));
        }
      }

      // sort out the tracks
      Point2D p1 = calc.calculatePoint(pos1, Math.toRadians(77 - (i * 4)), 554);

      Point2D p2 = calc.calculatePoint(pos2, Math.toRadians(54 + (i * 5)), 133);

      track1.add(thisTime, p1);
      track2.add(thisTime, p2);

      track1_2D.add(thisTime, p1);
      track2_2D.add(thisTime, p2);
    }

    // add an extra item to speedSeries3
    speedSeries3.add(thisTime + 12 * 500 * 60, 12d);

    // ok, generate the radiated noise lookup table
    for (int i = 0; i < 30; i += 5)
    {
      radNoiseLookup.add(i, Math.pow(i, 2.05));
    }

    // give the singletons a value
    singleton1.add(4d);
    singletonRange1b.add(998d);
    double minR = 940d;
    double maxR = 1050d;
    Range speedRange = new Range(minR, maxR);
    singletonRange1b.setRange(speedRange);
    freq1.add(77d);
    singleLoc1.add(calc.createPoint(12, 13));
    singleLoc2.add(calc.createPoint(7, 7));
    singletonLength.add(12d);

    final NumberDocument speedSeries1 = speedSeries1b.toDocument();
    final NumberDocument speedSeries2 = speedSeries2b.toDocument();
    final NumberDocument speedIrregular = speedIrregularB.toDocument();

    StoreGroup group1 = new StoreGroup(SPEED_DATA_FOLDER);
    group1.add(speedSeries1);
    group1.add(speedIrregular);
    group1.add(speedEarly1.toDocument());
    group1.add(speedSeries2);
    group1.add(speedRadNoise.toDocument());
    list.add(group1);

    IStoreGroup factors = new StoreGroup("Factors");

    NumberDocumentBuilder singletonRange2b =
        new NumberDocumentBuilder(RANGED_SPEED_SINGLETON, METRE.divide(SECOND)
            .asType(Velocity.class), null, null);
    double minR1 = 940d;
    double maxR1 = 1050d;
    Range speedRange1 = new Range(minR1, maxR1);
    singletonRange2b.setRange(speedRange1);
    singletonRange2b.add(998d);

    NumberDocument singletonRange2 = singletonRange2b.toDocument();

    NumberDocumentBuilder singletonSpeedb =
        new NumberDocumentBuilder("Ranged Speed", Velocity.UNIT, null, null);
    double min = 0d;
    double max = 100d;
    singletonSpeedb.add(54d);
    singletonSpeedb.setRange(new Range(min, max));
    NumberDocument singletonSpeed = singletonSpeedb.toDocument();

    NumberDocumentBuilder singletonLength1b =
        new NumberDocumentBuilder("Ranged Speed", Length.UNIT, null, null);
    singletonLength1b.add(134d);
    singletonLength1b.setRange(new Range(40d, 150d));

    NumberDocument singletonLength1 = singletonLength1b.toDocument();

    factors.add(singletonRange2);
    factors.add(singletonSpeed);
    factors.add(singletonLength1);
    //
    list.add(factors);

    IStoreGroup compositeTrack = new StoreGroup(COMPOSITE_ONE);
    compositeTrack.add(angle1.toDocument());
    final LocationDocument track2d = track2.toDocument();
    compositeTrack.add(track2d);
    compositeTrack.add(freq1.toDocument());
    compositeTrack.add(speedSeries4.toDocument());

    list.add(compositeTrack);

    list.add(length1.toDocument());
    list.add(length2.toDocument());
    list.add(string1.toDocument());
    list.add(string2.toDocument());
    list.add(singleton1.toDocument());
    list.add(singletonRange1b.toDocument());
    list.add(singletonLength.toDocument());
    list.add(timeIntervals.toDocument());
    list.add(timeStamps_1.toDocument());
    list.add(timeStamps_2.toDocument());
    list.add(track1.toDocument());
    list.add(track2d);
    list.add(singleLoc1.toDocument());
    list.add(singleLoc2.toDocument());
    list.add(track1_2D.toDocument());
    list.add(track2_2D.toDocument());
    list.add(speedSeries3.toDocument());
    list.add(radNoiseLookup.toDocument());

    // res.addAll(list);

    // perform an operation, so we have some audit trail
    selection.add(speedSeries1);
    selection.add(speedSeries2);
    Collection<ICommand> actionsA =
        new AddQuantityOperation().actionsFor(selection, list, context);
    Iterator<ICommand> addIterA = actionsA.iterator();
    // addIter.next();
    ICommand addActionA = addIterA.next();
    addActionA.execute();

    // perform an operation, so we have some audit trail
    selection.clear();
    selection.add(speedSeries1);
    selection.add(speedSeries2);
    selection.add(speedIrregular);
    Collection<ICommand> actions =
        new AddQuantityOperation().actionsFor(selection, list, context);
    Iterator<ICommand> addIter = actions.iterator();
    // addIter.next();
    ICommand addAction = addIter.next();
    addAction.execute();

    // and an operation using our speed factor
    selection.clear();
    selection.add(speedSeries1);
    selection.add(singleton1.toDocument());
    Collection<ICommand> actions2 =
        new MultiplyQuantityOperation().actionsFor(selection, list, context);
    addAction = actions2.iterator().next();
    addAction.execute();

    // calculate the distance travelled
    selection.clear();
    selection.add(timeIntervals.toDocument());
    selection.add(singletonRange1b.toDocument());
    Collection<ICommand> actions3 =
        new MultiplyQuantityOperation().actionsFor(selection, list, context);
    addAction = actions3.iterator().next();
    addAction.execute();

    return list;
  }
}
