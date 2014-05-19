/*
 * Desciption:
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 8:38:10 AM
 */
package ASSET.Scenario.Observers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Scenario.LiveScenario.ISimulation;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;
import MWC.GUI.Editable;


/** controller observer that stops running after indicated period
 * 
 * @author ianmayo
 *
 */
public class ScenarioStatusObserver extends
  CoreObserver implements ASSET.Scenario.ScenarioSteppedListener, IAttribute
{
  private HashMap<ScenarioType, ScenarioRunningListener> _runnerList;

	/***************************************************************
   *  member variables
   ***************************************************************/

  /***************************************************************
   *  constructor
   ***************************************************************/
  /**
   * default constructor - doesn't need much
   *
   */
  public ScenarioStatusObserver()
  {
    super("Status", true);
    
    _runnerList = new HashMap<ScenarioType, ScenarioRunningListener>();
  }


  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */

  /**
   * we're getting up and running.  The observers have been created and we've remembered
   * the scenario
   *
   * @param scenario the new scenario we're looking at
   */
  protected void performSetupProcessing(final ScenarioType scenario)
  {

  	// create a fresh runner for each scenario
  	ScenarioRunningListener runner = new ScenarioRunningListener(){

			public void finished(long elapsedTime, String reason)
			{
				getAttributeHelper().newData(scenario, elapsedTime, ISimulation.COMPLETE);
			}

			public void newScenarioStepTime(int val)
			{
			}

			public void newStepTime(int val)
			{
			}

			public void paused()
			{
			}

			public void restart(ScenarioType scenario)
			{
			}

			public void started()
			{
				System.out.println("STARTED");
				getAttributeHelper().newData(scenario, scenario.getTime(), ISimulation.RUNNING);

			}};
			
			_runnerList.put(scenario, runner);
  	
  	scenario.addScenarioRunningListener(runner);
  	
  	// initialise
  	getAttributeHelper().newData(scenario, scenario.getTime(), ISimulation.WAITING);
  }

  /**
   * right, the scenario is about to close.  We haven't removed the listeners
   * or forgotten the scenario (yet).
   *
   * @param scenario the scenario we're closing from
   */
  protected void performCloseProcessing(ScenarioType scenario)
  {
  	ScenarioRunningListener runner = _runnerList.get(scenario);
  	scenario.removeScenarioRunningListener(runner);
  }

  /**
   * add any applicable listeners
   */
  protected void addListeners(ScenarioType scenario)
  {
    // and become a listener
    _myScenario.addScenarioSteppedListener(this);
  }

  /**
   * remove any listeners
   */
  protected void removeListeners(ScenarioType scenario)
  {
    // remove ourselves as a listener
    _myScenario.removeScenarioSteppedListener(this);
  }

  /**
   * the scenario has stepped forward
   */
  public void step(ScenarioType scenario, final long newTime)
  {
 // 	getAttributeHelper().newData(scenario, newTime, newTime);
  }


  //////////////////////////////////////////////////
  // property editing
  //////////////////////////////////////////////////

  private EditorType _myEditor1;

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
  public EditorType getInfo()
  {
    if (_myEditor1 == null)
      _myEditor1 = new StatusObserverInfo(this);

    return _myEditor1;
  }

  //////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////
  static public class StatusObserverInfo extends EditorType
  {


    /**
     * constructor for editable details
     *
     * @param data the object we're going to edit
     */
    public StatusObserverInfo(final ScenarioStatusObserver data)
    {
      super(data, data.getName(), "Edit");
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("Name", "the name of this observer"),
        };
        return res;
      }
      catch (IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }
  }

  //////////////////////////////////////////////////
  // property testing
  //////////////////////////////////////////////////
  public static class TimeObsTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new ScenarioStatusObserver();
    }
  }

  public DataDoublet getCurrent(Object index)
	{
  	// do we know about this object yet?
  	Object val = getAttributeHelper().getCurrent(index);
  	if(val == null)
  	{
  		// aah. we probably haven't heard of it yet. set it up
  		ScenarioType scen = (ScenarioType) index;
  		getAttributeHelper().newData(index, scen.getTime(), ISimulation.WAITING);
  	}
  	
		return getAttributeHelper().getCurrent(index);
	}

  public Vector<DataDoublet> getHistoricValues(Object index)
	{
		return getAttributeHelper().getValuesFor(index);
	}

	public boolean isSignificant()
	{
		return true;
	}


	public String getUnits()
	{
		
		return "n/a";
	}


}
