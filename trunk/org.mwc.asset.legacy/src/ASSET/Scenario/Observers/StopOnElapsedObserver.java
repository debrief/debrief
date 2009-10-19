/*
 * Desciption:
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 8:38:10 AM
 */
package ASSET.Scenario.Observers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

import ASSET.ScenarioType;
import ASSET.Scenario.Genetic.ScenarioRunner;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;


/** controller observer that stops running after indicated period
 * 
 * @author ianmayo
 *
 */
public class StopOnElapsedObserver extends
  CoreObserver implements ASSET.Scenario.ScenarioSteppedListener,
  ScenarioObserver.ScenarioReferee, Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/***************************************************************
   *  member variables
   ***************************************************************/
  /**
   * the period of time elapsed so far
   */
  private Duration _elapsedTime = null;

  /**
   * a record of the start time for the scenario
   */
  private long _startTime = -1;

  /**
   * did we actually stop it?
   */
  private boolean _weStoppedIt = false;

  /***************************************************************
   *  constructor
   ***************************************************************/
  /**
   * default constructor, taking the elapsed time after which the
   * scenario should be stopped
   *
   * @param elapsed elapsed milliseconds since scenario start time
   */
  public StopOnElapsedObserver(Duration elapsed, String name, final boolean isActive)
  {
    super(name, isActive);
    _elapsedTime = elapsed;
  }

  /**
   * convenience constructor
   *
   * @param days  number of elapsed days
   * @param hours number of elapsed hours
   * @param mins  number of elapsed minutes
   * @param secs  number of elapsed seconds
   */
  public StopOnElapsedObserver(final int days, final int hours, final int mins, final int secs,
                      String name, final boolean isActive)
  {
    this(new Duration((days * 24 * 60 * 60 * 1000) +
                      (hours * 60 * 60 * 1000) +
                      (mins * 60 * 1000) +
                      (secs * 1000), Duration.MILLISECONDS), name, isActive);
  }


  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */

  public Duration getElapsed()
  {
    return _elapsedTime;
  }

  public void setElapsed(Duration val)
  {
    _elapsedTime = val;
  }

  /**
   * we're getting up and running.  The observers have been created and we've remembered
   * the scenario
   *
   * @param scenario the new scenario we're looking at
   */
  protected void performSetupProcessing(ScenarioType scenario)
  {
    // take record of the start time
    _startTime = scenario.getTime();


    _weStoppedIt = false;
  }

  /**
   * right, the scenario is about to close.  We haven't removed the listeners
   * or forgotten the scenario (yet).
   *
   * @param scenario the scenario we're closing from
   */
  protected void performCloseProcessing(ScenarioType scenario)
  {
    // reset the data
    _startTime = -1;

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
    // find out start
    if (_startTime == -1)
    {
      _startTime = _myScenario.getTime();
    }

    // find out elapsed
    final long elapsed = newTime - _startTime;

    if (elapsed >= _elapsedTime.getValueIn(Duration.MILLISECONDS))
    {
      _weStoppedIt = true;

      _myScenario.stop("Stopped on elapsed time:" + getName());
    }
  }

  /**
   * return how well this scenario performed, according to this referee.
   */
  public ScenarioRunner.ScenarioOutcome getOutcome()
  {
    ScenarioRunner.ScenarioOutcome res = null;
    if (_weStoppedIt)
    {
      res = new ScenarioRunner.ScenarioOutcome();
      res.summary = "Stopped:" + getName();
    }

    return res;
  }

  /**
   * accessor function to indicate if this observer stopped the scenario
   *
   * @return yes/no for if we stopped it
   */
  public boolean hasStopped()
  {
    return _weStoppedIt;
  }

  //////////////////////////////////////////////////
  // property editing
  //////////////////////////////////////////////////

  private transient EditorType _myEditor1;

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
      _myEditor1 = new TimeObserverInfo(this);

    return _myEditor1;
  }

  //////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////
  static public class TimeObserverInfo extends EditorType
  {


    /**
     * constructor for editable details
     *
     * @param data the object we're going to edit
     */
    public TimeObserverInfo(final StopOnElapsedObserver data)
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
          prop("Elapsed", "period after which scenario should be stopped")
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
      return new StopOnElapsedObserver(12, 12, 12, 12, "", true);
    }
  }


}
