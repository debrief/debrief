/*
 * Description:
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 1:11:09 PM
 */
package ASSET.Scenario.Observers;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

import ASSET.ScenarioType;
import MWC.Algorithms.LiveData.Attribute;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.GeneralFormat;

abstract public class CoreObserver implements ScenarioObserver, Editable
{
  /***************************************************************
   *  member variables
   ***************************************************************/
  /**
   * the name of this observer
   */
  private String _myName = "Un-named observer";

  /**
   * whether this observer is currently active
   */
  private boolean _isActive = true;

  /**
   * remember the scenario
   */
  protected ScenarioType _myScenario;

  /**
   * the editor for this data type
   */
  protected Editable.EditorType _myEditor = null;
  
  /** our property support
   * 
   */
  protected PropertyChangeSupport _pSupport;
  
  /** attribute helper support, just in case we want it
   * 
   */
  private Attribute.AttributeHelper _myAttributeHelper;
  
	
	/** our multi scenario observer helper
	 * 
	 */
	private BatchListenerHelper _batchListener = null;

  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */

  public CoreObserver(String myName, boolean isActive)
  {
    _myName = myName;
    _isActive = isActive;
    
    // create the prop support, mostly used for the IAttribute listen-able stats hierarchy
		_pSupport = new PropertyChangeSupport(this);
  }


  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */

  /** convenience class, largely helping with attribute watchers
   * 
   */
  protected Attribute.AttributeHelper getAttributeHelper()
  {
  	if(_myAttributeHelper == null)
  		_myAttributeHelper = new Attribute.AttributeHelper(_pSupport);
  	
  	return _myAttributeHelper;
  }
  
  protected BatchListenerHelper getListenerHelper()
  {
  	if(_batchListener == null)
  		_batchListener = new BatchListenerHelper();
  	
  	return _batchListener;
  }
  
  /** somebody cares about us, aaah
   * 
   * @param listener that loving soul
   */
  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
  	_pSupport.addPropertyChangeListener(listener);
  }
  
  /** somebody doesn't care about us
   * 
   * @param listener not worth mentioning
   */
  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
  	_pSupport.removePropertyChangeListener(listener);
  }
  
  public void restart(ScenarioType scenario)
  {
    // remember the scenario - since we will probably forget it when we teardown
    final ScenarioType tmpScen = _myScenario;

    // mark end of processing
    tearDown(tmpScen);

    // set ourselves up again
    setup(tmpScen);
  }

  /**
   * get the string
   */
  public String toString()
  {
    return getName();
  }


  /**
   * get the name of this observer
   */
  public String getName()
  {
    return _myName;
  }
  

  /** do the comparison
   * 
   * @param arg0
   * @return
   */
	public int compareTo(Plottable arg0)
	{
		CoreObserver other = (CoreObserver) arg0;
		return getName().compareTo(other.getName());
	}  

  /**
   * set the name of this observer
   */
  public void setName(final String val)
  {
    _myName = val;
  }

  /**
   * whether this observer is currently active
   */
  public boolean isActive()
  {
    return _isActive;
  }

  /**
   * whether this observer is currently active
   */
  public void setActive(final boolean active)
  {
    this._isActive = active;
  }


  /**
   * get a string recording the current date and version of ASSET
   *
   * @return string
   */
  public static String getHeaderInfo()
  {
    String res = "ASSET Version:" + ASSET.GUI.VersionInfo.getVersion() + GeneralFormat.LINE_SEPARATOR;
    res += "File saved:" + MWC.Utilities.TextFormatting.FullFormatDateTime.toString(new Date().getTime()) + GeneralFormat.LINE_SEPARATOR;
    return res;
  }

  /***************************************************************
   *  scenario parameters
   ***************************************************************/
  /**
   * configure observer to listen to this scenario
   */
  final public void setup(final ScenarioType scenario)
  {
    if (isActive())
    {
      _myScenario = scenario;

      // also add any listeners we're interested in
      addListeners(scenario);

      // and the specific processing for this type
      performSetupProcessing(scenario);
    }
  }


  /**
   * inform observer that scenario is complete, to remove listeners, etc
   * It's ok to clear the score (if applicable) since it will have been
   * retrieved before this point
   */
  final public void tearDown(ScenarioType scenario)
  {
    if (isActive())
    {
      performCloseProcessing(scenario);

      // ok, remove any listeners
      removeListeners(scenario);
    }
  }


  /**
   * we're getting up and running.  The observers have been created and we've remembered
   * the scenario
   *
   * @param scenario the new scenario we're looking at
   */
  abstract protected void performSetupProcessing(ScenarioType scenario);

  /**
   * right, the scenario is about to close.  We haven't removed the listeners
   * or forgotten the scenario (yet).
   *
   * @param scenario the scenario we're closing from
   */
  abstract protected void performCloseProcessing(ScenarioType scenario);

  /**
   * add any applicable listeners
   * @param scenario TODO
   */
  abstract protected void addListeners(ScenarioType scenario);


  /**
   * remove any listeners
   * @param scenario TODO
   */
  abstract protected void removeListeners(ScenarioType scenario);

  /**********************************************************************
   * editable parameters
   *********************************************************************/

  /**
   * find the data area occupied by this item
   */
  public WorldArea getBounds()
  {
    return null;
  }

  /**
   * it this item currently visible?
   */
  public boolean getVisible()
  {
    return isActive();
  }

  /**
   * paint this object to the specified canvas
   */
  public void paint(CanvasType dest)
  {
  }

  /**
   * Determine how far away we are from this point.
   * or return INVALID_RANGE if it can't be calculated
   */
  public double rangeFrom(WorldLocation other)
  {
    return 0;
  }

  /**
   * set the visibility of this item
   */
  public void setVisible(boolean val)
  {
    setActive(val);
  }
}
