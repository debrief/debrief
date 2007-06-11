package ASSET.Models.Mediums;

import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class Optic implements ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium,
  java.io.Serializable,
  MWC.GUI.Editable
{
  //////////////////////////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////////////////////////

  /**
   * the cross-sectional area of the target (m squared)
   */
  private double _xSectArea;

  /**
   * the height of the target (not altitude, but how high is it?) (m)
   */
  private WorldDistance _height;

  /**
   * an invalid height object, for when we don't know
   */
  public final static double INVALID_HEIGHT = -1;

  /**
   * the default height to use
   */
  private final static double DEFAULT_HEIGHT = 20;

  /**
   * my editor
   */
  private MWC.GUI.Editable.EditorType _myEditor;

  //////////////////////////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////////////////////////

  public Optic(final double baseNoiseLevel, final WorldDistance height)
  {
    _xSectArea = baseNoiseLevel;

    // handle a missing default height
    if (height.getValueIn(WorldDistance.METRES) != INVALID_HEIGHT)
    {
      _height = height;
    }
    else
      _height = new WorldDistance(DEFAULT_HEIGHT, WorldDistance.METRES);
  }

  //////////////////////////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////////////////////////

  public double radiatedEnergyFor(final ASSET.Participants.Status status, double absBearingDegs)
  {
    double res = 0;
    // are we too deep?
    if (status.getLocation().getDepth() > 20)
    {
      // todo: replace hard-coded 20m value above

      // yes, we not radiating this
      res = 0;
    }
    else
    {
      // we are shallow enough to be seen return our energy
      res = getXSectAreaFor(status);
    }

    return res;
  }

  public double reflectedEnergyFor(ASSET.Participants.Status status, double absBearingDegs)
  {
    final double res = 0;
    return res;
  }

  private double getXSectAreaFor(ASSET.Participants.Status status)
  {
    return _xSectArea;
  }

  public double getXSectArea()
  {
    return _xSectArea;
  }

  public void setXSectArea(final double val)
  {
    _xSectArea = val;
  }

  public WorldDistance getHeight()
  {
    return _height;
  }

  public void setHeight(WorldDistance height)
  {
    _height = height;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: Optic.java,v $
   * Revision 1.1  2006/08/08 14:21:45  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:53  Ian.Mayo
   * First versions
   *
   * Revision 1.8  2004/08/31 09:36:38  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   *
   * Revision 1.7  2004/08/26 17:05:32  Ian.Mayo
   * Implement more editable properties
   * <p/>
   * Revision 1.6  2004/08/26 16:47:22  Ian.Mayo
   * Implement more editable properties, add Acceleration property editor
   * <p/>
   * Revision 1.5  2004/08/26 16:27:15  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.4  2004/05/24 15:10:30  Ian.Mayo
   * Commit changes conducted at home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:53  ian
   * no message
   * <p/>
   * Revision 1.3  2003/11/05 09:19:30  Ian.Mayo
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
      _myEditor = new OpticInfo(this);

    return _myEditor;
  }


  /**
   * the name of this object
   *
   * @return the name of this editable object
   */
  public String getName()
  {
    return "Optic";
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

  /**
   * *************************************************
   * editor info
   * *************************************************
   */

  static public class OpticInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public OpticInfo(final Optic data)
    {
      super(data, data.getName(), "Optic");
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
          prop("XSectArea", "the cross-sectional area of this participant (m2)"),
          prop("Height", "the height of this participant (m)"),
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
  public static class OpticTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new Optic(12, new WorldDistance(12, WorldDistance.NM));
    }
  }

}