package MWC.GUI.Java3d.Tactical;

import MWC.GUI.BaseLayer;

import javax.media.j3d.Switch;
import javax.media.j3d.Group;
import javax.media.j3d.BranchGroup;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Mar 27, 2003
 * Time: 9:28:43 AM
 * To change this template use Options | File Templates.
 */
public class Layer3D extends Switch implements java.beans.PropertyChangeListener
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  private final BaseLayer _theLayer;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /** constructor
   *
   * @param theLayer
   */
  public Layer3D(BaseLayer theLayer)
  {
    // store the layer
    this._theLayer = theLayer;

    // set the capabilities
    setCapability(Switch.ALLOW_SWITCH_WRITE);
    setCapability(Group.ALLOW_CHILDREN_READ);
    setCapability(Group.ALLOW_CHILDREN_EXTEND);

    // set the visibility
    this.setVisible(_theLayer.getVisible());

    // listen out for the vis change event
    theLayer.addPropertyChangeListener(this, BaseLayer.VISIBILITY_CHANGE);

    // and finally store the user data
    this.setUserData(theLayer);
  }

  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////


  /** change the visibility of this layer
   *
   * @param isVisible
   */
  public void setVisible(boolean isVisible)
  {
    if(isVisible)
    {
      //set visible
      setWhichChild(Switch.CHILD_ALL);
    }
    else
    {
      // set invisible
      setWhichChild(Switch.CHILD_NONE);
    }
  }

  /**
   * This method gets called when a bound property is changed.
   * @param evt A PropertyChangeEvent object describing the event source
   *   	and the property that has changed.
   */
  public void propertyChange(PropertyChangeEvent evt)
  {
    if(evt.getPropertyName() == BaseLayer.VISIBILITY_CHANGE)
    {
      Boolean val = (Boolean)evt.getNewValue();
      setVisible(val.booleanValue());
    }
  }

}
