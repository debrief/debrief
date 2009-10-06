/*
 * Desciption:
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 8:38:10 AM
 */
package ASSET.Scenario.Observers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioRunningListener;
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
  private ScenarioRunningListener _runner;

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
  protected void performSetupProcessing(ScenarioType scenario)
  {
  	if(_runner == null)
  	_runner = new ScenarioRunningListener(){

			@Override
			public void finished(long elapsedTime, String reason)
			{
				getHelper().newData(elapsedTime, "FINISHED");
			}

			@Override
			public void newScenarioStepTime(int val)
			{
			}

			@Override
			public void newStepTime(int val)
			{
			}

			@Override
			public void paused()
			{
			}

			@Override
			public void restart()
			{
			}

			@Override
			public void started()
			{
			}};
  	
  	scenario.addScenarioRunningListener(_runner);
  	
  	// initialise
  	getHelper().newData(scenario.getTime(), "WAITING");
  }

  /**
   * right, the scenario is about to close.  We haven't removed the listeners
   * or forgotten the scenario (yet).
   *
   * @param scenario the scenario we're closing from
   */
  protected void performCloseProcessing(ScenarioType scenario)
  {
  	scenario.removeScenarioRunningListener(_runner);
  }

  /**
   * add any applicable listeners
   */
  protected void addListeners()
  {
    // and become a listener
    _myScenario.addScenarioSteppedListener(this);
  }

  /**
   * remove any listeners
   */
  protected void removeListeners()
  {
    // remove ourselves as a listener
    _myScenario.removeScenarioSteppedListener(this);
  }

  /**
   * the scenario has stepped forward
   */
  public void step(final long newTime)
  {
  	getHelper().newData(newTime, newTime);
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

  @Override
	public DataDoublet getCurrent()
	{
		return getHelper().getCurrent();
	}

  @Override
	public Vector<DataDoublet> getHistoricValues()
	{
		return getHelper().getHistoricValues();
	}

	@Override
	public boolean isSignificant()
	{
		return true;
	}


}
