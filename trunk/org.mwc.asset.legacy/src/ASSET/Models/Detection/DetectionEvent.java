/**
 * ASSET.Models.Detection.DetectionEvent
 */

package ASSET.Models.Detection;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.io.Serializable;

import ASSET.Models.SensorType;
import ASSET.ParticipantType;
import ASSET.Participants.Category;
import MWC.GUI.Properties.AbstractPropertyEditor;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class DetectionEvent implements java.util.Comparator<DetectionEvent>, Serializable
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////
  // set of (ascending) detection states
  ////////////////////////////////////////////////////////////

  /**
   * we're not in contact with this participant (and
   * there won't be a detection - but lets keep it in here anyway)
   */
  public static final int UNDETECTED = 0;

  /**
   * we've only just about detected this participant,
   * but we've no idea what it is
   */
  public static final int DETECTED = 1;

  /**
   * we know what type of target it is - the category
   */
  public static final int CLASSIFIED = 2;

  /**
   * we know exactly what it is!
   */
  public static final int IDENTIFIED = 3;


  /**
   * the current detection state for this target
   */
  private int _detectionState;

  /**
   * store the sensor location for this detection.  This has been
   * introduced mainly to support remote sensors. A helo could
   * hear be informed of a bearing detection from a sonar buoy
   * but needs to know the sonar buoy location find out where
   * the bearing line spans from
   */
  private WorldLocation _sensorLocation;

  /**
   * time of this detection
   */
  private long _time;

  /**
   * sensor which made detection
   */
  private int _sensorId;

  /**
   * range of detection (yds)
   */
  private WorldDistance _range;

  /**
   * the estimated range, where the actual range isn't available
   */
  private WorldDistance _estimatedRange;

  /**
   * relative bearing to target (degs)
   */
  protected Float _relBearing;

  /**
   * bearing of detection (degs)
   */
  private Float _bearing;

  /**
   * strength of detection (%)
   */
  private Float _strength;

  /**
   * the type of this target
   */
  private Category _target_type;

  /**
   * the course of the target (degs)
   */
  private Float _course;

  /**
   * the speed of the target (knots)
   */
  private Float _speed;

  /**
   * the host vessel for this detection
   */
  private int _hostId;

  /**
   * a textual message desribing this detection
   */
  private String _myMessage;

  /**
   * the id of the target
   */
  private Integer _targetId;


  ////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////


  /**
   * Constructor
   *
   * @param time     the time this detection was made
   * @param sensor   sensor which made detection
   * @param range    range in yards
   * @param bearing  bearing in degrees
   * @param strength strength 1..100
   */
  public DetectionEvent(final long time,
                        final int host,
                        final WorldLocation sensorLocation,
                        final SensorType sensor,
                        final WorldDistance range,
                        final WorldDistance estimatedRange,
                        final Float bearing,
                        final Float relBearing,
                        final Float strength,
                        final Category target_type,
                        final Float speed,
                        final Float course,
                        final ParticipantType target)
  {
    _time = time;
    _range = range;
    _estimatedRange = estimatedRange;
    _bearing = bearing;
    _relBearing = relBearing;
    _strength = strength;
    _target_type = target_type;
    _course = course;
    _speed = speed;
    _sensorId = sensor.getId();
    _hostId = host;
    _sensorLocation = sensorLocation;

    if (target != null)
      _targetId = new Integer(target.getId());
    else
      _targetId = null;

    // write the message
    _myMessage = target.getName() + " held on " + sensor.getName();
  }

  /**
   * Constructor
   *
   * @param time     the time this detection was made
   * @param sensor   sensor which made detection
   * @param range    range in yards
   * @param bearing  bearing in degrees
   * @param strength strength 1..100
   */
  public DetectionEvent(final long time,
                        final int host,
                        final WorldLocation sensorLocation,
                        final SensorType sensor,
                        final WorldDistance range,
                        final WorldDistance estimatedRange,
                        final Float bearing,
                        final Float relBearing,
                        final Float strength,
                        final Category target_type,
                        final WorldSpeed speed,
                        final Float course,
                        final ParticipantType target,
                        final int detectionState)
  {
    _time = time;
    _range = range;
    _estimatedRange = estimatedRange;
    _bearing = bearing;
    _relBearing = relBearing;
    _strength = strength;
    _target_type = target_type;
    _course = course;
    _speed = new Float(speed.getValueIn(WorldSpeed.M_sec));
    _sensorId = sensor.getId();
    _hostId = host;
    _sensorLocation = sensorLocation;
    _detectionState = detectionState;

    if (target != null)
      _targetId = new Integer(target.getId());
    else
      _targetId = null;

    // write the message
    _myMessage = target.getName() + " held on " + sensor.getName();
  }

  /**
   * set the detection state for this detection
   *
   * @see DetectionEvent.UNDETECTED
   */
  public void setDetectionState(int state)
  {
    _detectionState = state;
  }

  /**
   * get the detection state for this detection
   *
   * @see DetectionEvent.UNDETECTED
   */
  public int getDetectionState()
  {
    return _detectionState;
  }


  /**
   * get the bearing (degs)
   */
  public Float getBearing()
  {
    return _bearing;
  }

  /**
   * get the range (yds)
   */
  public WorldDistance getRange()
  {
    return _range;
  }

  /**
   * get the estimated range (WorldDistance) for when
   * actual range isn't known
   */
  public WorldDistance getEstimatedRange()
  {
    WorldDistance res;
    if (_estimatedRange == null)
      res = _range;
    else
      res = _estimatedRange;

    return res;
  }

  /**
   * get the target course (Degs)
   */
  public Float getCourse()
  {
    return _course;
  }

  /**
   * get the target speed (kts)
   */
  public Float getSpeed()
  {
    return _speed;
  }

  public Category getTargetType()
  {
    return _target_type;
  }

  public String toString()
  {
    return _myMessage;
  }

  public int getHost()
  {
    return _hostId;
  }

  public int getTarget()
  {
    return _targetId.intValue();
  }

  /**
   * set the id of the target we're looking at
   *
   * @param val the new id
   */
  public void setTarget(int val)
  {
    _targetId = new Integer(val);
  }

  public long getTime()
  {
    return _time;
  }

  public int getSensor()
  {
    return _sensorId;
  }

  public Float getStrength()
  {
    return _strength;
  }

  public WorldLocation getSensorLocation()
  {
    return _sensorLocation;
  }

  /**
   * ******************************************************************
   * support for comparator interface
   * ******************************************************************
   */

  public int compare(final DetectionEvent d1,
                     final DetectionEvent d2)
  {
    int res = 0;

    if (d1.getTime() < d2.getTime())
      res = -1;
    else if (d1.getTime() > d2.getTime())
      res = 1;

    return res;
  }

  public boolean equals(final Object obj)
  {
    final DetectionEvent d2 = (DetectionEvent) obj;

    return (this == d2);

  }

  public static class DetectionStatePropertyEditor extends AbstractPropertyEditor
  {
    ////////////////////////////////////////////////////
    // member objects
    ////////////////////////////////////////////////////
    private String _stringTags[] =
      {
        "Undetected",
        "Detected",
        "Classified",
        "Identified",
      };

    ////////////////////////////////////////////////////
    // member methods
    ////////////////////////////////////////////////////
    public String[] getTags()
    {
      return _stringTags;
    }


  }
}