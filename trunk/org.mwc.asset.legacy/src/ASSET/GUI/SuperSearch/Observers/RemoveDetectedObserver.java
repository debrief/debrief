/*
 * Desciption:
 * User: administrator
 * Date: Nov 11, 2001
 * Time: 12:29:16 PM
 */
package ASSET.GUI.SuperSearch.Observers;

import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class RemoveDetectedObserver extends ASSET.Scenario.Observers.DetectionObserver
{
  /***************************************************************
   *  member variables
   ***************************************************************/



  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  public RemoveDetectedObserver(final TargetType watchVessel, final TargetType targetVessel,
                                final String myName, final Integer detectionLevel, final boolean isActive)
  {
    super(watchVessel, targetVessel, myName, detectionLevel, isActive);

  }
  /***************************************************************
   *  member methods
   ***************************************************************/

  /**
   * valid detection happened, process it
   */
  protected void validDetection(final DetectionEvent detection)
  {
  	// let the parent do it's stuff
  	super.validDetection(detection);
  	
    // remove this target
    final int tgt = detection.getTarget();

    getScenario().removeParticipant(tgt);

  }


  /***************************************************************
   *  plottable properties
   ***************************************************************/
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
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new RemoverInfo(this, getName());

    return _myEditor;
  }

  //////////////////////////////////////////////////////
  // bean info for this class
  /////////////////////////////////////////////////////
  public class RemoverInfo extends Editable.EditorType
  {

    public RemoverInfo(final RemoveDetectedObserver data, final String name)
    {
      super(data, name, "");
    }

    public String getName()
    {
      return RemoveDetectedObserver.this.getName();
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("Name", "the name of this observer"),
          prop("NumDetected", "the number of targets detected"),
          prop("Active", "whether this listener is active"),
        };
        return res;
      }
      catch (IntrospectionException e)
      {
        System.out.println("::" + e.getMessage());
        return super.getPropertyDescriptors();
      }
    }
  }

  /**
   * ************************************************************
   * a gui class to show progress of this monitor
   * *************************************************************
   */

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class RemDetectedTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public RemDetectedTest(final String val)
    {
      super(val);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      MWC.GUI.Editable ed = new RemoveDetectedObserver(null, null, "how many", new Integer(2), true);
      return ed;
    }
  }
}
