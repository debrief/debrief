/*
 * Desciption:
 * User: administrator
 * Date: Nov 11, 2001
 * Time: 2:11:41 PM
 */
package ASSET.Util.XML.Control.Observers;

import ASSET.GUI.SuperSearch.Observers.RemoveDetectedObserver;
import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.DetectionObserver;

abstract class RemoveDetectedObserverHandler extends DetectionObserverHandler
{
  /**
   * ************************************************************
   * member variables
   * *************************************************************
   */
  private final static String _myNewType = "RemoveDetectedObserver";

  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  public RemoveDetectedObserverHandler()
  {
    super(_myNewType);
  }

  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */
  static public String getType()
  {
    return _myNewType;
  }

  protected DetectionObserver getObserver(final TargetType watch, final TargetType target, final String name, final Integer detectionLevel,boolean isActive)
  {
    return new RemoveDetectedObserver(watch, target, name, detectionLevel,isActive);
  }

}
