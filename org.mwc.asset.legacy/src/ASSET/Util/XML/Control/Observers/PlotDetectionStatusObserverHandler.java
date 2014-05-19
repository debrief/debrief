/*
 * Desciption:
 * User: administrator
 * Date: Nov 11, 2001
 * Time: 2:11:41 PM
 */
package ASSET.Util.XML.Control.Observers;

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.DetectionObserver;
import ASSET.Scenario.Observers.Plotting.PlotDetectionStatusObserver;

abstract class PlotDetectionStatusObserverHandler extends DetectionObserverHandler
{
  /**
   * ************************************************************
   * member variables
   * *************************************************************
   */
  private final static String _myNewType = "PlotDetectionStatusObserver";

  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  public PlotDetectionStatusObserverHandler()
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
    return new PlotDetectionStatusObserver(watch, target, name, detectionLevel,isActive);
  }

}
