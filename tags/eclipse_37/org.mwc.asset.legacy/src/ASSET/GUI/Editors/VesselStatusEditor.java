package ASSET.GUI.Editors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Movement.SSMovementCharacteristics;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class VesselStatusEditor implements Editable
{

  //////////////////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////////////////

  private ASSET.ParticipantType _myParticipant;
  private Editable.EditorType _myEditor = null;

  //////////////////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////////////////
  public VesselStatusEditor(final ASSET.ParticipantType participant)
  {
    // wrap the participant
    _myParticipant = participant;
  }

  //////////////////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////////////////

  public String toString()
  {
    return "Status";
  }

  public String getName()
  {
    return toString();
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
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new VesselStatusInfo(this);

    return _myEditor;
  }
  //////////////////////////////////////////////////////////////////////
  // editable getter/setter
  //////////////////////////////////////////////////////////////////////
  public BoundedInteger getSpeed()
  {
    return new BoundedInteger((int) _myParticipant.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec),
                              0,
                              (int) (_myParticipant.getMovementChars().getMaxSpeed().getValueIn(WorldDistance.METRES)));
  }

  public void setSpeed(final BoundedInteger val)
  {
    _myParticipant.getStatus().setSpeed(new WorldSpeed(val.getCurrent(), WorldSpeed.M_sec));
  }

  public BoundedInteger getCourse()
  {
    // trim the course
    double crseVal = MWC.Algorithms.Conversions.clipRadians(MWC.Algorithms.Conversions.Degs2Rads(_myParticipant.getStatus().getCourse()));
    crseVal = MWC.Algorithms.Conversions.Rads2Degs(crseVal);
    return new BoundedInteger((int) crseVal,
                              0,
                              360);
  }

  public void setCourse(final BoundedInteger val)
  {
    _myParticipant.getStatus().setCourse(val.getCurrent());
  }

  public double getFuelLevel()
  {
    return _myParticipant.getStatus().getFuelLevel();
  }

  public void setFuelLevel(final double val)
  {
    _myParticipant.getStatus().setFuelLevel(val);
  }

  public MWC.GenericData.WorldLocation getLocation()
  {
    return _myParticipant.getStatus().getLocation();
  }

  public void setLocation(final MWC.GenericData.WorldLocation val)
  {
    _myParticipant.getStatus().setLocation(val);
  }

  //////////////////////////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////////////////////////

  static public class VesselStatusInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public VesselStatusInfo(final VesselStatusEditor data)
    {
      super(data, data.getName(), "Edit");
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
          prop("Location", "the current location of this participant"),
          prop("Course", "the current course of this participant (degs)"),
          prop("Speed", "the current speed of this participant (kts)"),
          prop("FuelLevel", "the fuel level for this participant"),
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
  // add testing code
  //////////////////////////////////////////////////
  public static class EditorTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      SSN theSSN = new SSN(12);
      Status stat = new Status(12, 12);
      stat.setLocation(new WorldLocation(12, 12, 0));
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
      theSSN.setMovementChars(SSMovementCharacteristics.getSampleSSChars());
      theSSN.setStatus(stat);
      VesselStatusEditor res = new VesselStatusEditor(theSSN);

      return res;
    }
  }
}