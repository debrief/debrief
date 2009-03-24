package ASSET.Models.Movement;

import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 13-Aug-2003
 * Time: 15:14:31
 * To change this template use Options | File Templates.
 */
public abstract class ThreeDimMovementCharacteristics extends MovementCharacteristics
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * min Height at which we can travel
   */
  protected WorldDistance _minHeight;

  /**
   * max Height at which we travel
   */
  protected WorldDistance _maxHeight;

  protected WorldSpeed _defaultClimbRate;

  protected WorldSpeed _defaultDiveRate;

  public ThreeDimMovementCharacteristics(String myName, WorldAcceleration accelRate,
                                         WorldAcceleration decelRate, double fuel_usage_rate,
                                         WorldSpeed maxSpeed, WorldSpeed minSpeed, WorldSpeed defaultClimbRate,
                                         WorldSpeed defaultDiveRate, WorldDistance maxHeight,
                                         WorldDistance minHeight)
  {
    super(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed);
    this._defaultClimbRate = defaultClimbRate;
    this._defaultDiveRate = defaultDiveRate;
    this._maxHeight = maxHeight;
    this._minHeight = minHeight;
  }


  public WorldSpeed getDefaultClimbRate()
  {
    return _defaultClimbRate;
  }

  public void setDefaultClimbRate(WorldSpeed _defaultClimbRate)
  {
    this._defaultClimbRate = _defaultClimbRate;
  }

  public WorldSpeed getDefaultDiveRate()
  {
    return _defaultDiveRate;
  }

  public void setDefaultDiveRate(WorldSpeed _defaultDiveRate)
  {
    this._defaultDiveRate = _defaultDiveRate;
  }

  public WorldSpeed getClimbRate()
  {
    return _defaultClimbRate;
  }

  public WorldSpeed getDiveRate()
  {
    return _defaultDiveRate;
  }


  public WorldDistance getMaxHeight()
  {
    return _maxHeight;
  }

  public void setMaxHeight(WorldDistance _maxHeight)
  {
    this._maxHeight = _maxHeight;
  }

  public WorldDistance getMinHeight()
  {
    return _minHeight;
  }

  public void setMinHeight(WorldDistance _minHeight)
  {
    this._minHeight = _minHeight;
  }

  //////////////////////////////////////////////////
  // property editing
  //////////////////////////////////////////////////

  private EditorType _myEditor;

  /**
   * whether there is any edit information for this item
   * this is a convenience function to save creating the EditorType data
   * first
   *
   * @return yes/no
   */
  public boolean hasEditor()
  {
    return true;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new ThreeDimMovementCharacteristicsInfo(this);

    return _myEditor;
  }

  /** get a set of movement chars
   * 
   * @param i the turning circle to use.
   * @return
   */
  public static MovementCharacteristics getSampleChars(final float i)
  {
    return new ThreeDimMovementCharacteristics("the moves",
                                       new WorldAcceleration(1, WorldAcceleration.Kts_sec),
                                       new WorldAcceleration(1, WorldAcceleration.Kts_sec),
                                       0.0000001,
                                       new WorldSpeed(30, WorldSpeed.Kts),
                                       new WorldSpeed(1, WorldSpeed.Kts),
                                       new WorldSpeed(1, WorldSpeed.M_sec),
                                       new WorldSpeed(1, WorldSpeed.M_sec),
                                       new WorldDistance(300, WorldSpeed.M_sec),
                                       new WorldDistance(1, WorldSpeed.M_sec)
                                       )
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
       * get the turning circle diameter (m) at this speed (in m/sec)
       */
      public double getTurningCircleDiameter(double m_sec)
      {
        return i;
      }
    };  }

  //////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////
  static public class ThreeDimMovementCharacteristicsInfo extends EditorType
  {


    /**
     * constructor for editable details
     *
     * @param data the object we're going to edit
     */
    public ThreeDimMovementCharacteristicsInfo(final ThreeDimMovementCharacteristics data)
    {
      super(data, data.getName(), "Edit");
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("AccelRate", "the rate of acceleration for this vessel (kts/sec)"),
          prop("DecelRate", "the rate of acceleration for this vessel (kts/sec)"),
          prop("FuelUsageRate", "the rate of fuel usage for this vessel (%/kt/sec)"),
          prop("MaxHeight", "the maximum Height which this vessel travels to (m)"),
          prop("MaxSpeed", "the rate of acceleration for this vessel (kts/sec)"),
          prop("DefaultClimbRate", "the normal rate at which the participant climbs"),
          prop("DefaultDiveRate", "the normal rate at which the participant dives"),
          prop("Name", "the name of this set of movement characteristics"),
        };
        return res;
      }
      catch (IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }
  }

  //////////////////////////////////////////////////
  // property testing
  //////////////////////////////////////////////////
  public static class ThreeDMoveCharsTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new ThreeDimMovementCharacteristics("", null, null, 12, null, null, null, null, null, null)
      {
        /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				/**
         * get the turning circle diameter (m) at this speed (in m/sec)
         */
        public double getTurningCircleDiameter(double m_sec)
        {
          return 12;
        }
      };
    }
  }


}
