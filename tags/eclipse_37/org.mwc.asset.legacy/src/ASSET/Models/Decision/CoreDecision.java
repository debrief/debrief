package ASSET.Models.Decision;

import ASSET.Models.DecisionType;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 17-Aug-2004
 * Time: 14:47:13
 * To change this template use File | Settings | File Templates.
 */
abstract public class CoreDecision implements DecisionType
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  /**
   * the name of this decision model
   */
  private String _myName;

  /**
   * whether we're active or not
   */
  private boolean _isActive = true;

  /**
   * the last thing we were doing
   */
  private String _lastActivity = null;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * constructor for the core decision type.
   *
   * @param name - the name for this activity
   */
  protected CoreDecision(final String name)
  {
    setActive(true);
    setName(name);
  }

  //////////////////////////////////////////////////
  // accessors
  //////////////////////////////////////////////////

  /** use a local stringbuffer to create the activity string.  We do it loads of times,
   * this local buffer will reduce object creation
   */
  private StringBuffer _activityBuffer = new StringBuffer();


  /**
   * return a string representing what we are currently up to
   */
  public final String getActivity()
  {
    // clear our buffer.
    _activityBuffer.setLength(0);
    _activityBuffer.append(getName());
    _activityBuffer.append(":");
    _activityBuffer.append(_lastActivity);
    return _activityBuffer.toString();
  }

  /**
   * store the most recent activity
   */
  protected void setLastActivity(String activity)
  {
    _lastActivity = activity;
  }

  /**
   * return the name of this detection model
   *
   * @return the name
   */
  public final String getName()
  {
    return _myName;
  }

  /**
   * the name of this detection model
   *
   * @param val the name to use
   */
  public final void setName(final String val)
  {
    _myName = val;
  }


  /**
   * toString
   *
   * @return the returned String
   */
  final public String toString()
  {
    return getName();
  }

  /**
   * find out whether this behaviour is active or not.
   *
   * @return yes/no
   */
  public final boolean isActive()
  {
    return _isActive;
  }

  /**
   * se twhether this behaviour is active or not.
   *
   * @param isActive yes/no
   */
  public final void setActive(final boolean isActive)
  {
    _isActive = isActive;
  }


  ////////////////////////////////////////////////////////////////////////////
  //  embedded class, used for editing the object
  ////////////////////////////////////////////////////////////////////////////
  /**
   * the definition of what is editable about this object
   */
  public class CoreDecisionInfo extends MWC.GUI.Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public CoreDecisionInfo(final CoreDecision data)
    {
      super(data, data.getName(), "Edit", "images/icons/BehaviourModel.gif");
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
          prop("Name", "the current location of this participant"),
        };

        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

}
