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
package ASSET.Models.Sensor.Initial;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.NetworkParticipant;
import ASSET.ScenarioType;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;


/**
 * base implementation of a sensor
 */
abstract public class InitialSensor extends CoreSensor
{

  ////////////////////////////////////////////////////
  // member bariables
  ////////////////////////////////////////////////////


  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

  
  /** whether this sensor returns ambiguous bearings
   * 
   */
  private boolean _isAmbiguous = false;
  
  /** whether this sensor can produce a range value
   * 
   */
  private boolean _canProduceRange = true;

	/**
   * *************************************************
   * constructor
   * *************************************************
   */
  public InitialSensor(final int id, final String defaultName)
  {
    super(id, 0, defaultName);
    // todo: we're not managing time between detection opportunities for these initial sensors
  }


  // what is the detection strength for this target?
  protected ASSET.Models.Detection.DetectionEvent detectThis(
    final ASSET.Models.Environment.EnvironmentType environment,
    final ASSET.ParticipantType host,
    final ASSET.ParticipantType target,
    final long time, ScenarioType scenario)
  {
    ASSET.Models.Detection.DetectionEvent res = null;

    // take copies of the locations
    final WorldLocation hostLocation = getHostLocationFor(host);
    final WorldLocation targetLocation = getLocationFor(target);
    final WorldVector wv = targetLocation.subtract(hostLocation);

    // useful values
    final WorldDistance rng = new WorldDistance(wv.getRange(), WorldDistance.DEGS);
    final double brg = MWC.Algorithms.Conversions.Rads2Degs(wv.getBearing());
    final double crse = host.getStatus().getCourse();

    // components of the sensor equation
    final double loss = getLoss(environment, targetLocation, hostLocation);
    final double osNoise = getOSNoise(host, brg);
    final double tgtNoise = getTgtNoise(target, brg);
    final double bkNoise = getBkgndNoise(environment, targetLocation, brg);
    final double rd = getRD(host, target);
    final double di = getDI(crse, brg);

    // ok, go for the SE.
    final double fom = tgtNoise - (osNoise - di) - rd - bkNoise;
    final double se = fom - loss;

    if (se > 0)
    //    if(rng < THRESHOLD)
    {
      // collate the data
      Float Brg = this.canProduceBearing() ? new Float(brg) : null;
      final Float RelBrg = this.canProduceBearing() ? new Float(this.relativeBearing(host.getStatus().getCourse(), brg)) : null;

      final WorldDistance Rng = canProduceRange() ? rng : null;
      final Float tgtSpeed = this.hasTgtSpeed() ? new Float(target.getStatus().getSpeed().getValueIn(WorldSpeed.Kts)) :  null;
      final Float tgtCourse = this.hasTgtCourse() ? new Float(target.getStatus().getCourse()) : null;
      final Float Str = new Float(se);
      final WorldDistance estimatedRange = getEstimatedRange();

      //   if(this.canIdentifyTarget())
      //    identifiedTarget = target; // @@ this is in case we want to start
      // indicating that the target is unidentified for a
      // detection. We currently always pass the target name

      if (target.getCategory() == null)
        System.out.println("no category for:" + target);

      while(Brg < 0)
      {
        Brg += 360;
      }
      
      res = new DetectionEvent(time,
                               host.getId(),
                               hostLocation,
                               this,
                               Rng,
                               estimatedRange,
                               Brg,
                               RelBrg, Str,
                               target.getCategory(),
                               tgtSpeed,
                               tgtCourse,
                               target);
      
      // hmm, do we produce ambiguous bearings?
      if(isAmbiguous())
      {
        handleAmbiguousBearing(res, crse, Brg, RelBrg);
      }
    }

    // ok, just see if there are any pSupport listners
    if (_pSupport != null)
    {
      if (_pSupport.hasListeners(SENSOR_COMPONENT_EVENT))
      {
        // create the event
        InitialSensorComponentsEvent sev =
          new InitialSensorComponentsEvent(time, loss, bkNoise, osNoise, tgtNoise, rd, di, se, target.getName());

        // and fire it!
        _pSupport.firePropertyChange(SENSOR_COMPONENT_EVENT,
                                     null, sev);
      }
    }

    return res;

  }


  private void handleAmbiguousBearing(
      final DetectionEvent detection, final double crse, final Float Brg,
      final Float RelBrg)
  {
    float ambigBrg = (float) (crse - RelBrg);
    while(ambigBrg < 0)
    {
      ambigBrg += 360;
    }
    while(ambigBrg > 360)
    {
      ambigBrg -= 360;
    }
    
    // ok, we'll make the port bearing hte first bearing, and the stbd one
    // the other one. Otherwise the first baering is always true
    if(RelBrg < crse)
    {
      // ok, this is port. leave it as is
      // the stbd is ambiguous
      detection.setAmbiguousBearing(ambigBrg);
    }
    else
    {
      // ok, this is stbd, swap them around
      // put the ambig bearing into port
      detection.setBearing(ambigBrg);
      // and the real one into stbd
      detection.setAmbiguousBearing(Brg);
    }
  }

  /**
   * does this sensor return the course of the target?
   */
  boolean hasTgtCourse()
  {
    return false;
  }
  
  

  @Override
	public boolean canProduceRange()
	{
  	return _canProduceRange;
	}

  /** specify is this sensor can produce a range value
   * 
   * @param canProduceRange
   */
  public void setCanProduceRange(boolean canProduceRange)
  {
  	_canProduceRange = canProduceRange;
  }

	/**
   * does this sensor return the speed of the target?
   */
  boolean hasTgtSpeed()
  {
    return false;
  }

  abstract protected double getLoss(ASSET.Models.Environment.EnvironmentType environment,
                                    WorldLocation target,
                                    WorldLocation host);

  abstract protected double getOSNoise(ASSET.ParticipantType ownship,
                                       double absBearingDegs);

  abstract protected double getTgtNoise(ASSET.ParticipantType target,
                                        double absBearingDegs);

  /**
   * get the background noise on this particular bearing from the target location (to give us the noise behind the target)
   *
   * @param environment
   * @param host           the host location we are looking at
   * @param absBearingDegs
   * @return
   */
  abstract protected double getBkgndNoise(ASSET.Models.Environment.EnvironmentType environment,
                                          WorldLocation host,
                                          double absBearingDegs);

  abstract protected double getRD(NetworkParticipant host,
                                  NetworkParticipant target);

  abstract protected double getDI(double courseDegs,
                                  double absBearingDegs);


  /**
   * if this sensor has a dynamic behaviour, update it according to the demanded status
   *
   * @param myDemandedStatus
   * @param myStatus
   * @param newTime
   */
  public void update(DemandedStatus myDemandedStatus,
                     Status myStatus,
                     long newTime)
  {
    // don't bother.  let classes over-ride as necessary
  }



  /** indicate whether this sensor should return ambiguous bearings
   * 
   * @param isAmbiguous
   */
	public void setAmbiguous(boolean isAmbiguous)
	{
		_isAmbiguous = isAmbiguous;
	}
  
  /** whether this is an ambiguous bearing
   * 
   * @return
   */
  public boolean isAmbiguous()
  {
  	return _isAmbiguous;
  }
  
  ////////////////////////////////////////////////////
  // embedded class for event fired after each detection step
  ////////////////////////////////////////////////////
  public static class InitialSensorComponentsEvent
  {
    ////////////////////////////////////////////////////
    // member objects
    ////////////////////////////////////////////////////
    public double _loss;
    final private double _bkNoise;
    final private double _osNoise;
    final private double _tgtNoise;
    final private double _rd;
    final private double _di;
    final private double _se;
    final private String _tgtName;
    final private long _time;


    /**
     * constructor
     */
    public InitialSensorComponentsEvent(long time, double loss, double bkNoise,
                                        double osNoise, double tgtNoise,
                                        double rd, double di, double se, String tgtName)
    {
      _loss = loss;
      _bkNoise = bkNoise;
      _osNoise = osNoise;
      _tgtNoise = tgtNoise;
      _rd = rd;
      _di = di;
      _tgtName = tgtName;
      _se = se;
      _time = time;
    }

    public double getSE()
    {
      return _se;
    }

    public long getTime()
    {
    	return _time;
    }
    
    public String getTgtName()
    {
      return _tgtName;
    }

    public double getLoss()
    {
      return _loss;
    }

    public double getBkNoise()
    {
      return _bkNoise;
    }

    public double getOsNoise()
    {
      return _osNoise;
    }

    public double getTgtNoise()
    {
      return _tgtNoise;
    }

    public double getRd()
    {
      return _rd;
    }

    public double getDi()
    {
      return _di;
    }

    public String toString()
    {
      String res;

      res = (int) _loss + " " + (int) _bkNoise + " " + (int) _osNoise + " " + (int) _tgtNoise
        + " " + (int) _rd + " " + (int) _di + " " + (int) _se + " " + _tgtName;

      return res;
    }
  }

}