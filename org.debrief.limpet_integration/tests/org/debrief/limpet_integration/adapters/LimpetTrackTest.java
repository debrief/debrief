package org.debrief.limpet_integration.adapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import info.limpet.IChangeListener;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.ITemporalObjectCollection;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Location;

import java.awt.Color;
import java.awt.geom.Point2D;

import javax.measure.Measurable;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;

import org.junit.Test;

import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Test;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class LimpetTrackTest
{

  @Test
  public void testCreateSingleton()
  {
    final String labelName = "TEST_LABEL_NAME";

    LabelWrapper label = new LabelWrapper(labelName, new WorldLocation(12d,
        14d, 0d), Color.red);

    DebriefLimpetAdapterFactory adapter = new DebriefLimpetAdapterFactory();

    IStoreItem limpet = (IStoreItem) adapter
        .getAdapter(label, IStoreItem.class);

    assertNotNull("result created");

    assertEquals("correct name", labelName, limpet.getName());

    // have a look at teh data
    IStoreGroup coll = (IStoreGroup) limpet;
    assertEquals("size", 4, coll.size());

    // get the locations
    NonTemporal.Location loc = (Location) coll.get(CoreLimpetTrack.LOCATION);
    assertNotNull("found location", loc);
    assertEquals("size", 1, loc.getValuesCount());

    // ok, check updates work
    hasUpdated1 = false;
    hasUpdated2 = false;

    coll.addChangeListener(new IChangeListener()
    {

      @Override
      public void metadataChanged(IStoreItem subject)
      {
      }

      @Override
      public void dataChanged(IStoreItem subject)
      {
        hasUpdated1 = true;
      }

      @Override
      public void collectionDeleted(IStoreItem subject)
      {
      }
    });

    loc.addChangeListener(new IChangeListener()
    {
      @Override
      public void metadataChanged(IStoreItem subject)
      {
      }

      @Override
      public void dataChanged(IStoreItem subject)
      {
        hasUpdated2 = true;
      }

      @Override
      public void collectionDeleted(IStoreItem subject)
      {
      }
    });

    // ok, move hte location
    label.setLocation(new WorldLocation(label.getLocation().add(
        new WorldVector(12, 13, 14))));
    assertTrue("update fired", hasUpdated1);
    assertTrue("update fired", hasUpdated2);

  }

  private static boolean hasUpdated1 = false;
  private static boolean hasUpdated2 = false;
  @SuppressWarnings("unused")
  private static boolean hasUpdated3 = false;

  @SuppressWarnings("unchecked")
  @Test
  public void testCreate()
  {
    TrackWrapper track = TrackWrapper_Test.getDummyTrack();

    DebriefLimpetAdapterFactory adapter = new DebriefLimpetAdapterFactory();

    IStoreItem limpet = (IStoreItem) adapter
        .getAdapter(track, IStoreItem.class);

    assertNotNull("result created");

    assertEquals("correct name", TrackWrapper_Test.TRACK_NAME, limpet.getName());

    IStoreGroup group = (IStoreGroup) limpet;
    assertNotNull("correct type", group);

    assertEquals("correct size", 4, group.size());
    assertTrue("no parent", group.getParent() == null);

    // check locations
    ITemporalObjectCollection<?> locColl = (ITemporalObjectCollection<?>) group
        .get(LimpetTrack.LOCATION);
    assertNotNull("found locations", locColl);
    assertEquals("correct length", 5, locColl.getValuesCount());

    Point2D firstLoc = (Point2D) locColl.getValues().get(0);
    Long firstTime = locColl.getTimes().get(0);

    assertEquals("correct start time", 110L, firstTime.longValue());
    assertEquals("correct start lat", 8.99E-4, firstLoc.getX(), 0.001);
    assertEquals("correct start long", -1.19E-5, firstLoc.getY(), 0.001);

    ITemporalObjectCollection<?> subject = (ITemporalObjectCollection<?>) group
        .get(LimpetTrack.SPEED);
    assertNotNull("found locations", subject);
    assertEquals("correct length", 5, subject.getValuesCount());

    ITemporalQuantityCollection<Velocity> qCol = (ITemporalQuantityCollection<Velocity>) subject;
    Measurable<Velocity> firstVal = qCol.getValues().get(0);

    firstTime = subject.getTimes().get(0);
    assertEquals("correct start time", 110L, firstTime.longValue());
    assertEquals("correct start lat", 100.584,
        firstVal.doubleValue(qCol.getUnits()), 0.001);

    subject = (ITemporalObjectCollection<?>) group.get(LimpetTrack.COURSE);
    assertNotNull("found locations", subject);
    assertEquals("correct length", 5, subject.getValuesCount());

    ITemporalQuantityCollection<Angle> qCourse = (ITemporalQuantityCollection<Angle>) subject;
    Measurable<Angle> firstAngle = qCourse.getValues().get(0);

    firstTime = subject.getTimes().get(0);
    assertEquals("correct start time", 110L, firstTime.longValue());
    assertEquals("correct start lat", 10.0,
        firstAngle.doubleValue(qCourse.getUnits()), 0.001);

    subject = (ITemporalObjectCollection<?>) group.get(LimpetTrack.DEPTH);
    assertNotNull("found locations", subject);
    assertEquals("correct length", 5, subject.getValuesCount());

    ITemporalQuantityCollection<Length> qLen = (ITemporalQuantityCollection<Length>) subject;
    Measurable<Length> firstLen = qLen.getValues().get(0);

    firstTime = subject.getTimes().get(0);
    assertEquals("correct start time", 110L, firstTime.longValue());
    assertEquals("correct start lat", 0.0,
        firstLen.doubleValue(qLen.getUnits()), 0.001);

    // did it move?
    hasUpdated1 = false;
    locColl.addChangeListener(new IChangeListener()
    {
      @Override
      public void metadataChanged(IStoreItem subject)
      {
      }

      @Override
      public void dataChanged(IStoreItem subject)
      {
        hasUpdated1 = true;
      }

      @Override
      public void collectionDeleted(IStoreItem subject)
      {
      }
    });

    // move the track
    track.shift(new WorldVector(1d, 2d, 0d));

    assertTrue("has moved", hasUpdated1);

  }

}
