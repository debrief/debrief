package org.debrief.limpet_integration.adapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.ITemporalObjectCollection;
import info.limpet.ITemporalQuantityCollection;

import java.awt.geom.Point2D;

import javax.measure.Measurable;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;

import org.junit.Test;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Test;

public class LimpetTrackTest
{

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
    ITemporalObjectCollection<?> subject = (ITemporalObjectCollection<?>) group
        .get(LimpetTrack.LOCATION);
    assertNotNull("found locations", subject);
    assertEquals("correct length", 5, subject.getValuesCount());

    Point2D firstLoc = (Point2D) subject.getValues().get(0);
    Long firstTime = subject.getTimes().get(0);

    assertEquals("correct start time", 110L, firstTime.longValue());
    assertEquals("correct start lat", 8.99E-4, firstLoc.getX(), 0.001);
    assertEquals("correct start long", -1.19E-5, firstLoc.getY(), 0.001);

    subject = (ITemporalObjectCollection<?>) group.get(LimpetTrack.SPEED);
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

  }

}
