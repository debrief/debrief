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
  private final static String DEAD_TYPE = "PlotTheDead";
  private Boolean _plotTheDead = null;
  

  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  public RemoveDetectedObserverHandler()
  {
    super(_myNewType);
    
    addAttributeHandler(new HandleBooleanAttribute(DEAD_TYPE)
    {
      public void setValue(String name, final boolean val)
      {
        _plotTheDead = new Boolean(val);
      }
    });

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
  	RemoveDetectedObserver res =  new RemoveDetectedObserver(watch, target, name, detectionLevel,isActive);
  	if(_plotTheDead != null)
  		res.setPlotTheDead(_plotTheDead.booleanValue());
  	
  	_plotTheDead = null;
  	
  	return res;
  }

}
