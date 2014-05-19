package ASSET.Models.Mediums;

import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class NarrowbandRadNoise implements ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium,
  java.io.Serializable, Editable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////////////////////////
  private double _baseNoiseLevel;

  /**
   * my editor
   */
  EditorType _myEditor;

  //////////////////////////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////////////////////////

  public NarrowbandRadNoise(final double baseNoiseLevel)
  {
    _baseNoiseLevel = baseNoiseLevel;
  }

  //////////////////////////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////////////////////////

  public double radiatedEnergyFor(final ASSET.Participants.Status status, double absBearingDegs)
  {
    final double Speed = status.getSpeed().getValueIn(WorldSpeed.M_sec);
    final double res = 0.0000252 * Math.pow(Speed, 5) -
      0.001456 * Math.pow(Speed, 4) +
      0.02165 * Math.pow(Speed, 3) +
      0.04 * Math.pow(Speed, 2) -
      0.66 * Speed + getBaseNoiseLevelFor(status);
    return res;
  }

  public double reflectedEnergyFor(ASSET.Participants.Status status, double absBearingDegs)
  {
    final double res = 0;
    return res;
  }

  double getBaseNoiseLevelFor(ASSET.Participants.Status status)
  {
    return _baseNoiseLevel;
  }

  public double getBaseNoiseLevel()
  {
    return _baseNoiseLevel;
  }


  public void setBaseNoiseLevel(final double val)
  {
    _baseNoiseLevel = val;
  }


  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: NarrowbandRadNoise.java,v $
   * Revision 1.1  2006/08/08 14:21:45  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:53  Ian.Mayo
   * First versions
   *
   * Revision 1.1  2004/10/18 14:58:10  Ian.Mayo
   * First version.  Working fine
   *
   * Revision 1.6  2004/08/31 09:36:37  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  /****************************************************
   * editor support
   ***************************************************/
  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new NarrowbandInfo(this);

    return _myEditor;
  }


  /**
   * the name of this object
   *
   * @return the name of this editable object
   */
  public String getName()
  {
    return "NarrowbandSensor";
  }

  public String toString()
  {
    return getName();
  }

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

  //////////////////////////////////////////////////
  // editable info
  //////////////////////////////////////////////////
  static public class NarrowbandInfo extends EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public NarrowbandInfo(final NarrowbandRadNoise data)
    {
      super(data, data.getName(), "NarrowbandSensor");
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res = {
          prop("BaseNoiseLevel", "the base level of narrowband radiated noise (dB)"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  //////////////////////////////////////////////////
  // testing
  //////////////////////////////////////////////////
  public static class BBRadNoiseTest extends ASSET.Util.SupportTesting.EditableTesting
  {

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new NarrowbandRadNoise(12);
    }
  }

}