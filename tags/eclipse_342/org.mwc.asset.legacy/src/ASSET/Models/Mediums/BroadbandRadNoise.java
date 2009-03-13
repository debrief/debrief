package ASSET.Models.Mediums;

import ASSET.Util.SupportTesting;
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

public class BroadbandRadNoise implements ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium,
  java.io.Serializable, MWC.GUI.Editable
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
  MWC.GUI.Editable.EditorType _myEditor;

  //////////////////////////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////////////////////////

  public BroadbandRadNoise(final double baseNoiseLevel)
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
   * $Log: BroadbandRadNoise.java,v $
   * Revision 1.1  2006/08/08 14:21:44  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:53  Ian.Mayo
   * First versions
   *
   * Revision 1.6  2004/08/31 09:36:37  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   *
   * Revision 1.5  2004/08/26 16:27:13  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.4  2004/05/24 15:10:28  Ian.Mayo
   * Commit changes conducted at home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:53  ian
   * no message
   * <p/>
   * Revision 1.3  2003/11/05 09:19:29  Ian.Mayo
   * Include MWC Model support
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
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new BroadbandInfo(this);

    return _myEditor;
  }


  /**
   * the name of this object
   *
   * @return the name of this editable object
   */
  public String getName()
  {
    return "BroadbandSensor";
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
  static public class BroadbandInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public BroadbandInfo(final BroadbandRadNoise data)
    {
      super(data, data.getName(), "BroadbandSensor");
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
          prop("BaseNoiseLevel", "the base level of broadband radiated noise (dB)"),
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
  public static class BBRadNoiseTest extends SupportTesting.EditableTesting
  {

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new BroadbandRadNoise(12);
    }
  }

}