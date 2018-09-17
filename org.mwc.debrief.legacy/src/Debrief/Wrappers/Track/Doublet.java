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
package Debrief.Wrappers.Track;

import java.awt.Color;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.Algorithms.Conversions;
import MWC.Algorithms.FrequencyCalcs;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

public class Doublet implements Comparable<Doublet>
{
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testCalc extends junit.framework.TestCase
  {

    private double convertAndTest(final double myCrseDegs,
        final double bearingDegs, final double mySpeedKts,
        final double observedFreq)
    {
      final double myCrseRads = Conversions.Degs2Rads(myCrseDegs);
      final double bearingRads = Conversions.Degs2Rads(bearingDegs);
      return FrequencyCalcs.calcDopplerComponent(bearingRads, myCrseRads,
          mySpeedKts, observedFreq);
    }
    
    public void testTwoWay()
    {
      SensorWrapper rxSensor = new SensorWrapper("Receiver_Sensor");
      SensorContactWrapper receiver = new SensorContactWrapper("host",new HiResDate(1000), null,90d,null,Color.red, "label", 0, "Receiver");
      receiver.setOrigin(new WorldLocation(0.1,0.1,0d));
      receiver.setSensor(rxSensor);
      double targetCourse = 120;
      double targetSpeed = 15;
      Fix targetFix = new Fix(new HiResDate(2000), new WorldLocation(2,2,2d), targetCourse, targetSpeed);
      final FixWrapper targetFixW = new FixWrapper(targetFix);
      WorldLocation targetLocation = new WorldLocation(0,0,0d);
      HiResDate startTime = new HiResDate(1000);
      HiResDate endTime = new HiResDate(1000000);
      TrackSegment parent = new AbsoluteTMASegment(34, new WorldSpeed(12, WorldSpeed.Kts), targetLocation, startTime, endTime);
      double hostCourse = 90d;
      double hostSpeed = 10d;
      Fix hostFix = new Fix(new HiResDate(4000), new WorldLocation(1,1,1d), hostCourse, hostSpeed );
      FixWrapper hostFixW = new FixWrapper(hostFix);
      double speed_of_sound = FrequencyCalcs.SpeedOfSoundKts;
      SensorWrapper transmitter = new SensorWrapper("Transmitter");
      transmitter.setBaseFrequency(300d);
      Doublet dub = new Doublet(receiver, targetFixW, parent, hostFixW)
          {

            @Override
            protected FixWrapper getTransmitterFix(SensorWrapper source)
            {
              return targetFixW;
            }
        
          };
      double freq = dub.getPredictedMultistaticFrequency(speed_of_sound, transmitter);
      assertEquals("correct freq", 297.914, freq, 0.01);
    }

    public void testCorrected()
    {
      double res = convertAndTest(320, 28, 8, 300);
      assertEquals("right freq", 0.304, res, 0.1);

      res = convertAndTest(320, 328, 8, 300);
      assertEquals("right freq", 0.805, res, 0.1);

      res = convertAndTest(320, 158, 8, 300);
      assertEquals("right freq", -0.7734, res, 0.01);

      res = convertAndTest(320, 158, 9, 300);
      assertEquals("right freq", -0.870, res, 0.01);

      res = convertAndTest(150, 158, 9, 300);
      assertEquals("right freq", 0.906, res, 0.01);
    }
  }

  public static final double INVALID_BASE_FREQUENCY = Double.NaN;

  /**
   * Calculate the current doppler shift between the two positons
   * 
   * @param SpeedOfSound
   *          the speed of sound to use, m/sec
   * @return
   */
  public static double getDopplerShift(final double SpeedOfSound,
      final double fNought, final Fix host, final Fix tgt)
  {
    final double osKts = Conversions.Yps2Kts(host.getSpeed());
    final double tgtKts = Conversions.Yps2Kts(tgt.getSpeed());

    final double osSpeed = MWC.Algorithms.Conversions.Kts2Mps(osKts);
    final double tgtSpeed = MWC.Algorithms.Conversions.Kts2Mps(tgtKts);
    final double osHeadingRads = host.getCourse();
    final double tgtHeadingRads = tgt.getCourse();

    // produce dLat, dLong at the correct point on the earth
    final WorldVector offset = tgt.getLocation().subtract(host.getLocation());

    // done, go for it.
    return FrequencyCalcs.calcPredictedFreqSI(SpeedOfSound, osHeadingRads,
        tgtHeadingRads, osSpeed, tgtSpeed, offset.getBearing(), fNought);
  }

  private final SensorContactWrapper _sensor;

  private final FixWrapper _targetFix;

  private final FixWrapper _hostFix;

  private final TrackSegment _targetTrack;

  // ////////////////////////////////////////////////
  // working variables to help us along.
  // ////////////////////////////////////////////////
  private static final WorldLocation _workingSensorLocation = new WorldLocation(
      0.0, 0.0, 0.0);

  private static final WorldLocation _workingTargetLocation = new WorldLocation(
      0.0, 0.0, 0.0);

  // ////////////////////////////////////////////////
  // constructor
  // ////////////////////////////////////////////////
  public Doublet(final SensorContactWrapper sensor, final FixWrapper targetFix,
      final TrackSegment parent, final FixWrapper hostFix)
  {
    _sensor = sensor;
    _targetFix = targetFix;
    _targetTrack = parent;
    _hostFix = hostFix;
  }

  /**
   * ok find bearing error (wrapped to -..360)
   * 
   * @param measuredValue
   * @param calcValue
   * @return
   */
  public double calculateBearingError(final double measuredValue,
      final double calcValue)
  {
    double theError = measuredValue - calcValue;

    while (theError > 180)
      theError -= 360.0;

    while (theError < -180)
      theError += 360.0;

    return theError;
  }

  /**
   * ok find frequency error
   * 
   * @param measuredValue
   * @param calcValue
   * @return
   */
  public double calculateFreqError(final double measuredValue,
      final double calcValue)
  {
    return measuredValue - calcValue;
  }

  @Override
  public int compareTo(final Doublet o)
  {
    final int res;

    // hmm, do they have the same host DTG?
    if (_hostFix == null || o._hostFix == null || _hostFix.getDTG().equals(
        o._hostFix.getDTG()))
    {
      // yes, then lets compare the target fixes. This happens
      // when the sensor cuts are more frequent than the
      // sensor platform positions

      // do we have a target fix
      if (_targetFix != null && o._targetFix != null)
      {
        // are the times unequal?
        final int timeDiff = _targetFix.getDTG().compareTo(o._targetFix
            .getDTG());
        if (timeDiff != 0)
        {
          res = timeDiff;
        }
        else
        {
          final int sensorDiff = _sensor.getSensor().compareTo(o._sensor
              .getSensor());

          if (sensorDiff == 0)
          {
            // same sensor, compare times

            // nope, we'll have to compare the sensor fix
            res = _sensor.getDTG().compareTo(o._sensor.getDTG());
          }
          else
          {
            // different sensor. job done
            res = sensorDiff;
          }
        }
      }
      else
      {
        // target fix missing. Compare sensor, then time
        final int sensorDiff = _sensor.getSensor().compareTo(o._sensor
            .getSensor());

        if (sensorDiff == 0)
        {
          // same sensor, compare times

          // nope, we'll have to compare the sensor fix
          res = _sensor.getDTG().compareTo(o._sensor.getDTG());
        }
        else
        {
          // different sensor. job done
          res = sensorDiff;
        }
      }
    }
    else if (_hostFix != null && o._hostFix != null)
    {
      // ok, we have both host fixes
      res = _hostFix.getDTG().compareTo(o._hostFix.getDTG());
    }
    else
    {
      // nope, we'll have to compare the sensor fix
      res = _sensor.getDTG().compareTo(o._sensor.getDTG());
    }

    return res;
  }

  public double getAmbiguousMeasuredBearing()
  {
    return _sensor.getAmbiguousBearing();
  }

  /**
   * get the base frequency for this measured tonal, if there is one
   * 
   * @return
   */
  public double getBaseFrequency()
  {
    return _sensor.getSensor().getBaseFrequency();
  }

  public double getCalculatedBearing(final WorldVector sensorOffset,
      final WorldVector targetOffset, WorldLocation rxLoc,
      WorldLocation txLoc)
  {
    // copy our locations
    _workingSensorLocation.copy(rxLoc);
    _workingTargetLocation.copy(txLoc);

    // apply the offsets
    if (sensorOffset != null)
      _workingSensorLocation.addToMe(sensorOffset);
    if (targetOffset != null)
      _workingTargetLocation.addToMe(targetOffset);

    // calculate the current bearing
    final WorldVector error = _workingTargetLocation.subtract(
        _workingSensorLocation);
    double calcBearing = error.getBearing();
    calcBearing = Conversions.Rads2Degs(calcBearing);

    if (calcBearing < 0)
      calcBearing += 360;

    return calcBearing;
  }

  public double getCalculatedBearing(final WorldVector sensorOffset,
      final WorldVector targetOffset)
  {
    final WorldLocation sensorOrigin = _sensor
        .getCalculatedOrigin(null);
    return getCalculatedBearing(sensorOffset, targetOffset, sensorOrigin, _targetFix.getLocation());
  }

  /**
   * get the colour of this sensor fix
   */
  public Color getColor()
  {
    return _sensor.getColor();
  }

  /**
   * calculate the corrected frequency (take out ownship doppler)
   * 
   * @return
   */
  public double getCorrectedFrequency()
  {
    double correctedFreq = 0;

    final double theBearingDegs = getCalculatedBearing(null, null);
    final double theBearingRads = Conversions.Degs2Rads(theBearingDegs);
    final double myCourseRads = _hostFix.getCourse();

    final double mySpeedKts = _hostFix.getSpeed();
    final double observedFreq = _sensor.getFrequency();
    final double dopplerComponent = FrequencyCalcs.calcDopplerComponent(
        theBearingRads, myCourseRads, mySpeedKts, observedFreq);

    correctedFreq = observedFreq + dopplerComponent;

    return correctedFreq;
  }

  // ////////////////////////////////////////////////
  // member methods
  // ////////////////////////////////////////////////
  /**
   * get the DTG of this contact
   * 
   * @return the DTG
   */
  public HiResDate getDTG()
  {
    return _sensor.getDTG();
  }

  public boolean getHasBeenResolved()
  {
    return !_sensor.getHasAmbiguousBearing();
  }

  public FixWrapper getHost()
  {
    return _hostFix;
  }

  public double getMeasuredBearing()
  {
    return _sensor.getBearing();
  }

  public double getMeasuredFrequency()
  {
    return _sensor.getFrequency();
  }

  /**
   * calculate what the frequency of the target should be (base freq plus both dopplers)
   * 
   * @return
   */
  public double getPredictedFrequency(final double speedOfSoundKts)
  {
    double predictedFreq = Double.NaN;

    if (_targetTrack instanceof CoreTMASegment)
    {
      final double baseFreq = getBaseFrequency();

      final double theBearingDegs = getCalculatedBearing(null, null);

      final double myCourseDegs = _hostFix.getCourseDegs();
      final double hisCourseDegs = _targetFix.getCourseDegs();

      final double mySpeedKts = _hostFix.getSpeed();
      final double hisSpeedKts = _targetFix.getSpeed();

      predictedFreq = FrequencyCalcs.getPredictedFreq(baseFreq, speedOfSoundKts,
          mySpeedKts, myCourseDegs, hisSpeedKts, hisCourseDegs, theBearingDegs);
    }
    return predictedFreq;
  }

  protected FixWrapper getTransmitterFix(SensorWrapper source)
  {
    Watchable[] nearest = source.getHost().getNearestTo(_hostFix
        .getDateTimeGroup(), false);
    final Watchable res;
    if (nearest != null && nearest.length > 0)
    {
      res = nearest[0];
    }
    else
    {
      res = null;
    }

    return (FixWrapper) res;
  }

  public double getPredictedMultistaticFrequency(final double speedOfSoundKts,
      final SensorWrapper source)
  {
    double predictedFreq = Double.NaN;

    if (_targetTrack instanceof CoreTMASegment)
    {
      // ok, freq drop from source to target
      final FixWrapper sourceFix = getTransmitterFix(source);

      final double baseFreq = source.getBaseFrequency();

      final WorldLocation txLocation = _targetFix.getLocation();
      final WorldLocation rxLocation = sourceFix.getLocation();
      double theBearingDegs = getCalculatedBearing(null, null, txLocation, rxLocation);

      double rxCourseDegs = _targetFix.getCourseDegs();
      double txCourseDegs = sourceFix.getCourseDegs();

      double rxSpeedKts = _targetFix.getSpeed();
      double txSpeedKts = sourceFix.getSpeed();

      predictedFreq = FrequencyCalcs.getPredictedFreq(baseFreq, speedOfSoundKts,
          rxSpeedKts, rxCourseDegs, txSpeedKts, txCourseDegs, theBearingDegs);

      // ok, now from the target to the receiver
      theBearingDegs = getCalculatedBearing(null, null);

      rxCourseDegs = _hostFix.getCourseDegs();
      txCourseDegs = _targetFix.getCourseDegs();

      rxSpeedKts = _hostFix.getSpeed();
      txSpeedKts = _targetFix.getSpeed();

      predictedFreq = FrequencyCalcs.getPredictedFreq(predictedFreq,
          speedOfSoundKts, rxSpeedKts, rxCourseDegs, txSpeedKts, txCourseDegs,
          theBearingDegs);

    }
    return predictedFreq;
  }

  public SensorContactWrapper getSensorCut()
  {
    return _sensor;
  }

  public FixWrapper getTarget()
  {
    return _targetFix;
  }

  public TrackSegment getTargetTrack()
  {
    return _targetTrack;
  }

}