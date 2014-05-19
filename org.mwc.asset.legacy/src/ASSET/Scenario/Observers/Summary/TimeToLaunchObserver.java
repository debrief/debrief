/*
 * Desciption:
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 8:45:34 AM
 */
package ASSET.Scenario.Observers.Summary;

import ASSET.Models.Decision.TargetType;
import ASSET.NetworkParticipant;
import ASSET.Participants.Category;
import ASSET.Scenario.Genetic.ScenarioRunner;
import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.ParticipantsChangedListener;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.ScenarioType;
import MWC.GenericData.Duration;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.text.NumberFormat;

/**
 * listener which looks out for a particular type of participant being created (launched)
 */
public class TimeToLaunchObserver extends CoreObserver implements ScenarioObserver.ScenarioReferee, ParticipantsChangedListener,
  ScenarioSteppedListener
{

  /**
   * the time we started (to calculate the elapsed time)
   */
  private long _startTime;

  /**
   * the current recorded time
   */
  private long _currentTime;

  /**
   * running tally of fitness score
   */
  private double _myScore = -1;

  /**
   * the target type for target vessels for the target we're watching
   */
  private TargetType _targetType = null;

  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  public TimeToLaunchObserver(final TargetType targetType, String name, final boolean isActive)
  {
    super(name, isActive);
    // remember the target types
    _targetType = targetType;
  }

  /***************************************************************
   *  member methods
   ***************************************************************/


  /**
   * the scenario has restarted
   */
  public void restart(ScenarioType scenario)
  {
    super.restart(scenario);

    _myScore = -1;
  }

  /**
   * get the types of vessel whose proximity we are checking for (targets)
   */
  public TargetType getTargetType()
  {
    return _targetType;
  }


  /**
   * we're getting up and running.  The observers have been created and we've remembered
   * the scenario
   *
   * @param scenario the new scenario we're looking at
   */
  protected void performSetupProcessing(ScenarioType scenario)
  {
    _myScenario = scenario;

    // store the current time
    _startTime = _myScenario.getTime();
    _currentTime = _myScenario.getTime();
  }


  /**
   * right, the scenario is about to close.  We haven't removed the listeners
   * or forgotten the scenario (yet).
   *
   * @param scenario the scenario we're closing from
   */
  protected void performCloseProcessing(ScenarioType scenario)
  {
    // reset the score
    _myScore = -1;
  }

  /**
   * add any applicable listeners
   */
  protected void addListeners(ScenarioType scenario)
  {
    // and become a listener
    _myScenario.addScenarioSteppedListener(this);

    // listen to the participants being crated
    _myScenario.addParticipantsChangedListener(this);
  }

  /**
   * remove any listeners
   */
  protected void removeListeners(ScenarioType scenario)
  {
    // remove ourselves as a listener
    _myScenario.removeScenarioSteppedListener(this);
    _myScenario.removeParticipantsChangedListener(this);
  }


  /**
   * return how well this scenario performed, according to this referee
   */
  public ScenarioRunner.ScenarioOutcome getOutcome()
  {
    ScenarioRunner.ScenarioOutcome res = null;

    // did we decide the outcome?
    if (_myScore != -1)
    {
      res = new ScenarioRunner.ScenarioOutcome();
      Duration elapsedD = new Duration(_myScore, Duration.MILLISECONDS);
      res.summary = "Launched after " + elapsedD;
      res.score = _myScore;
    }

    return res;
  }

  /** return a description of the performance of this run
   *
   */
  /**
   * get a text description of the outcome
   */
  public String getSummary()
  {
    // set the attributes
    Duration duration = new Duration((long) _myScore, Duration.MILLISECONDS);

    // and get value
    double value = duration.getValueIn(Duration.HOURS);
    NumberFormat nf = new java.text.DecimalFormat("0.0");
    String res = nf.format(value);


    return "Launch after:" + res + " hours";
  }


  /**
   * the scenario has stepped forward
   */
  public void step(ScenarioType scenario, long newTime)
  {
    _currentTime = newTime;
  }

  /**
   * the indicated participant has been added to the scenario
   */
  public void newParticipant(int index)
  {
    // get the participant
    NetworkParticipant pt = _myScenario.getThisParticipant(index);

    // get it's type
    Category theCat = pt.getCategory();

    // does it match?
    if (_targetType.matches(theCat))
    {
      // we've won!
      _myScore = _currentTime - _startTime;

      // and finish!
      _myScenario.stop("Stopped on launch:" + getName());
    }
  }

  /**
   * the indicated participant has been removed from the scenario
   */
  public void participantRemoved(int index)
  {
    // hey, don't worry
  }

  //////////////////////////////////////////////////
  // property editing
  //////////////////////////////////////////////////

  private EditorType _myEditor;

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
    if (_myEditor == null)
      _myEditor = new TimeToLaunchObserverInfo(this);

    return _myEditor;
  }

  //////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////
  static public class TimeToLaunchObserverInfo extends EditorType
  {


    /**
     * constructor for editable details
     *
     * @param data the object we're going to edit
     */
    public TimeToLaunchObserverInfo(final TimeToLaunchObserver data)
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
          prop("TargetType", "the type of participant to look out for"),
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


}
