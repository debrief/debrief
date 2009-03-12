package ASSET.Scenario;

/**
 * Title:  MultiForceScenario
 * Description:  Scenario class which handles multiple forces (such as in
 *    a FEAST/SuperSearch type application where participants do not see
 *    targets in their own force).
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Ian Mayo
 * @version 1.0
 * @deprecated
 */

import ASSET.ParticipantType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class MultiForceScenario extends CoreScenario
{

  /**
   * keep track of the blue force, the CoreScenario stores
   * the red force
   */
  private HashMap<Integer, ParticipantType> _blueForce;

  /**
   * keep track of the "full" participants, from both sides
   * (which we use for the Blue stepping forward)
   */
  private HashMap<Integer, ParticipantType> _fullForce;
  

  /**
   * the name of this scenario type
   */
  public static final String TYPE = "SuperSearch";


  /**
   * the list of blue participants changed listeners
   */
  private Vector<ParticipantsChangedListener> _blueParticipantListeners;

  /**
   * our blue wrapper, to make this look like a normal scenario for red
   * participants
   */
  private ScenarioBlueWrapper _blueWrapper = null;

  /**
   * our red wrapper, to make this look like a normal scenario for red
   * participants
   */
  private ScenarioRedWrapper _redWrapper = null;

  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */

  public MultiForceScenario()
  {
    _fullForce = new HashMap<Integer, ParticipantType>();
  }

  /////////////////////////////////////////////////////
  // manage our blue listeners
  /////////////////////////////////////////////////////
  public void addBlueParticipantsChangedListener(final ParticipantsChangedListener list)
  {
    if (_blueParticipantListeners == null)
      _blueParticipantListeners = new Vector<ParticipantsChangedListener>(1, 2);

    _blueParticipantListeners.add(list);
  }

  public void removeBlueParticipantsChangedListener(final ParticipantsChangedListener list)
  {
    _blueParticipantListeners.remove(list);
  }

  private void fireBlueParticipantChanged(final int index, final boolean added)
  {
    if (_blueParticipantListeners != null)
    {
      final Iterator<ParticipantsChangedListener> it = _blueParticipantListeners.iterator();
      while (it.hasNext())
      {
        final ParticipantsChangedListener pcl = (ParticipantsChangedListener) it.next();
        if (added)
          pcl.newParticipant(index);
        else
          pcl.participantRemoved(index);
      }
    }
  }

  /***********************************************************************
   *
   ***********************************************************************/


  /**
   * create the red force using the characteristics provided
   */
  public void createRedForce(final String file,
                             final String variance)
  {
    final ASSET.Scenario.SuperSearch.SuperSearch generator = new ASSET.Scenario.SuperSearch.SuperSearch();
    generator.setTemplate(file);
    generator.setVariance(variance);
    generator.setIdStart(1000);

    final ASSET.ParticipantType[] lst = generator.build();

    // store the time step
    this.setScenarioStepTime(generator.getTimeStep());

    // now add these participants to the red force
    for (int i = 0; i < lst.length; i++)
    {
      final ASSET.ParticipantType thisP = lst[i];
      thisP.setName(thisP.getName() + ":" + i);

      // initialise the time in the new participant
      thisP.getStatus().setTime(this.getTime());
      addParticipant(thisP.getId(), thisP);

    }
  }


  /**
   * back door, for reloading a scenario from file)
   */
  public void addParticipant(int index, final ParticipantType participant)
  {
    if (index == 0)
      index = ASSET.Util.IdNumber.generateInt();

    // and add it to our "full" listing
    _fullForce.put(new Integer(index), participant);

    // add the particicpant to the parents (red) listing
    super.addParticipant(index, participant);
  }

  /**
   * method to add a participant to the "blue" force
   */
  public void addBlueParticipant(int index, final ASSET.ParticipantType participant)
  {

    if (index == 0)
      index = ASSET.Util.IdNumber.generateInt();

    // do we have list?
    if (_blueForce == null)
    {
      _blueForce = new java.util.HashMap<Integer, ParticipantType>();
    }

    // store it
    _blueForce.put(new Integer(index), participant);

    // also add it to the "full force" listing
    _fullForce.put(new Integer(index), participant);

    // fire new scenario event
    this.fireBlueParticipantChanged(index, true);
  }

  ////////////////////////////////////////////////////////
  // participant-related
  ////////////////////////////////////////////////////////
  /**
   * Return a particular Participant - so that the Participant can be controlled directly.  Listeners added/removed. Participants added/removed, etc.
   */
  public ParticipantType getThisParticipant(final int id)
  {
    ParticipantType res = null;
    if (_fullForce != null)
      res = (ParticipantType) _fullForce.get(new Integer(id));

    return res;
  }

  /**
   * remove the indicated participant
   */
  public void removeParticipant(final int index)
  {
    // try to do it from the parent
    super.removeParticipant(index);

    // and try to do it from our full force

    // now remove it (try the full force first)
    final Integer bigI = new Integer(index);
    _fullForce.remove(bigI);

    // now remove it (try the blue force)
    final Object res2 = _blueForce.remove(bigI);
    if (res2 != null)
    {
      // fire new scenario event
      this.fireBlueParticipantChanged(index, false);
    }
  }

  /**
   * Provide a list of id numbers of Participant we contain
   *
   * @return list of ids of Participant we contain
   */
  protected Integer[] getListOfBlueParticipants()
  {
    Integer[] res = new Integer[0];

    if (_blueForce != null)
    {
      final java.util.Collection<Integer> vals = _blueForce.keySet();
      res = vals.toArray(res);
    }

    return res;
  }


  /**
   * Provide a list of id numbers of Participant we contain
   *
   * @return list of ids of Participant we contain
   */
  public Integer[] getListOfParticipants()
  {
    Integer[] res = new Integer[_fullForce.size()];

    if (_blueForce != null)
    {
      final java.util.Collection<Integer> vals = _fullForce.keySet();
      res =  vals.toArray(res);
    }

    return res;
  }

  /**
   * Provide a list of id numbers of Participant we contain
   *
   * @return list of ids of Participant we contain
   */
  protected Integer[] getListOfRedParticipants()
  {
    return super.getListOfParticipants();
  }

  /**
   * Return a particular Participant - so that the Participant can be controlled directly.  Listeners added/removed. Participants added/removed, etc.
   */
  public ParticipantType getThisRedParticipant(final int id)
  {
    return super.getThisParticipant(id);
  }


  /**
   * Move the scenario through a single step
   */
  public void step()
  {

    if (_blueWrapper == null)
    {
      _blueWrapper = new ScenarioBlueWrapper(this);
      _redWrapper = new ScenarioRedWrapper(this);
    }

    long oldTime = _myTime;

    // move time forward
    _myTime += _myScenarioStepTime;

    // move the blue participants forward
    if (_blueForce != null)
    {
      //////////////////////////////////////////////////
      // first the decision
      //////////////////////////////////////////////////
      java.util.Iterator<ParticipantType> iter = _blueForce.values().iterator();
      while (iter.hasNext())
      {
        final ParticipantType pt = iter.next();
        // pass it the parent's listof participants
        pt.doDecision(oldTime, _myTime, _blueWrapper);
      }

      //////////////////////////////////////////////////
      // now the movement
      //////////////////////////////////////////////////
      iter = _blueForce.values().iterator();
      while (iter.hasNext())
      {
        final ParticipantType pt = (ParticipantType) iter.next();
        // pass it the parent's listof participants
        pt.doMovement(oldTime, _myTime, _blueWrapper);
      }

      //////////////////////////////////////////////////
      // now the detection
      //////////////////////////////////////////////////
      iter = _blueForce.values().iterator();
      while (iter.hasNext())
      {
        final ParticipantType pt = (ParticipantType) iter.next();
        // pass it the parent's listof participants
        pt.doDetection(oldTime, _myTime, _blueWrapper);
      }
    }

    // now move the red participants forward
    if (_myVisibleParticipants != null)
    {
      //////////////////////////////////////////////////
      // first the decision
      //////////////////////////////////////////////////
      java.util.Iterator<ParticipantType> iter = _myVisibleParticipants.values().iterator();
      while (iter.hasNext())
      {
        final ParticipantType pt = (ParticipantType) iter.next();
        pt.doDecision(oldTime, _myTime, _redWrapper);
      }

      //////////////////////////////////////////////////
      // now the movement
      //////////////////////////////////////////////////
      iter = _myVisibleParticipants.values().iterator();
      while (iter.hasNext())
      {
        final ParticipantType pt = (ParticipantType) iter.next();
        pt.doMovement(oldTime, _myTime, _redWrapper);
      }

      //////////////////////////////////////////////////
      // now the detection
      //////////////////////////////////////////////////
      iter = _myVisibleParticipants.values().iterator();
      while (iter.hasNext())
      {
        final ParticipantType pt = (ParticipantType) iter.next();
        pt.doDetection(oldTime, _myTime, _redWrapper);
      }
    }

    // fire messages
    this.fireScenarioStepped(_myTime);
  }

  /**
   * ****************************************************************
   * wrapper to make MultiForce act like normal Scenario
   * ****************************************************************
   */
  private class ScenarioBlueWrapper extends MultiForceScenario
  {
    private MultiForceScenario _scenario;

    public ScenarioBlueWrapper(final MultiForceScenario scenario)
    {
      _scenario = scenario;
    }

    /**
     * Return a particular Participant - so that the Participant can be controlled directly.  Listeners added/removed. Participants added/removed, etc.
     */
    public ParticipantType getThisParticipant(final int id)
    {
      return _scenario.getThisParticipant(id);
    }

    /**
     * Provide a list of id numbers of Participant we contain
     *
     * @return list of ids of Participant we contain
     */
    public Integer[] getListOfParticipants()
    {
      return _scenario.getListOfParticipants();
    }
  }

  /**
   * ****************************************************************
   * wrapper to make MultiForce act like normal Scenario
   * ****************************************************************
   */
  private class ScenarioRedWrapper extends MultiForceScenario
  {
    private MultiForceScenario _scenario;

    public ScenarioRedWrapper(final MultiForceScenario scenario)
    {
      _scenario = scenario;
    }

    /**
     * Return a particular Participant - so that the Participant can be controlled directly.  Listeners added/removed. Participants added/removed, etc.
     */
    public ParticipantType getThisParticipant(final int id)
    {
      return _scenario.getThisParticipant(id);
    }

    /**
     * Provide a list of id numbers of Participant we contain
     *
     * @return list of ids of Participant we contain
     */
    public Integer[] getListOfParticipants()
    {
      return _scenario.getListOfBlueParticipants();
    }
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class MFScenarioTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public MFScenarioTest(final String val)
    {
      super(val);
    }

    protected int stepCounter = 0;
    protected long lastTime = 0;
    protected Boolean lastStartState = null;
    protected int createdCounter = 0;
    protected int destroyedCounter = 0;


    protected class createdListener implements ParticipantsChangedListener
    {
      /**
       * the indicated participant has been added to the scenario
       */
      public void newParticipant(int index)
      {
        createdCounter++;
      }

      /**
       * the indicated participant has been removed from the scenario
       */
      public void participantRemoved(int index)
      {
        destroyedCounter++;
      }

      /**
       * the scenario has restarted
       */
      public void restart()
      {
      }

    }

    int newStepTime;

    protected class startStopListener implements ScenarioRunningListener
    {
      public void started()
      {
        lastStartState = new Boolean(true);
      }

      /**
       * the scenario has stopped running on auto
       */
      public void paused()
      {
        // let's not worry about this little thing
      }


      public void finished(long elapsedTime, String reason)
      {
        lastStartState = new Boolean(false);
      }

      public void newScenarioStepTime(final int val)
      {
        newStepTime = val;
      };
      public void newStepTime(final int val)
      {
        newStepTime = val;
      };

      /**
       * the scenario has restarted
       */
      public void restart()
      {
      }

    }

    protected class stepListener implements ScenarioSteppedListener
    {
      public void step(final long newTime)
      {
        lastTime = newTime;
        stepCounter++;
      }

      /**
       * the scenario has restarted
       */
      public void restart()
      {
      }

    }

    public void testScenarioParticipants()
    {

      //
      //      // create server
      //      final MultiForceScenario srv = new MultiForceScenario();
      //
      //      // add as listener
      //      final createdListener cl = new createdListener();
      //      srv.addParticipantsChangedListener(cl);
      //
      //      SSMovementCharacteristics ssm = new SSMovementCharacteristics("blank",
      //                                                                    1, 1, 0.0, 20, 1, 300, 1, 1, 100, 1);
      //      SurfaceMovementCharacteristics sur = new SurfaceMovementCharacteristics("surf",
      //                                                                              1, 1, 0.0, 22, 1, 399);
      //
      //      // create the red participants
      //      final ASSET.Participants.Status stat = new ASSET.Participants.Status(2, 12);
      //      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(1, 1, 1);
      //      stat.setLocation(origin);
      //      stat.setCourse(0);
      //      stat.setSpeed(new WorldSpeed(3, WorldSpeed.M_sec));
      //      final ASSET.ParticipantType ssn = new ASSET.Models.Vessels.SSN(2, stat, null, "SSN");
      //      ssn.setMovementChars(ssm);
      //      final ASSET.ParticipantType frig = new ASSET.Models.Vessels.Surface(3, stat, null, "Frigate");
      //      frig.setMovementChars(sur);
      //
      //      // create the blue participants
      //      final ASSET.ParticipantType ssk1 = new ASSET.Models.Vessels.SSK(11, stat, null, "SSK1");
      //      ssk1.setMovementChars(ssm);
      //      final ASSET.ParticipantType ssk2 = new ASSET.Models.Vessels.SSK(12, stat, null, "SSK2");
      //      ssk2.setMovementChars(ssm);
      //      final ASSET.ParticipantType ssk3 = new ASSET.Models.Vessels.SSK(13, stat, null, "SSK3");
      //      ssk3.setMovementChars(ssm);
      //
      //      // add the blues
      //      srv.addBlueParticipant(ssn.getId(), ssn);
      //      srv.addBlueParticipant(frig.getId(), frig);
      //
      //      // give the ssn a sensor
      //      final ASSET.Models.Sensor.CoreSensor sensor = new ASSET.Models.Sensor.Initial.BroadbandSensor(22);
      //      ssn.addSensor(sensor);
      //
      //      // give the ssn a sensor
      //      ssk1.addSensor(sensor);
      //
      //      // add the blues
      //      srv.addParticipant(ssk1.getId(), ssk1);
      //      srv.addParticipant(ssk2.getId(), ssk2);
      //      srv.addParticipant(ssk3.getId(), ssk3);
      //
      //      // check the sizes
      //      final Integer[] blues = srv.getListOfBlueParticipants();
      //      assertEquals("blue participant size", 2, blues.length);
      //
      //      final Integer[] reds = srv.getListOfRedParticipants();
      //      assertEquals("red participant size", 3, reds.length);
      //
      //      // try to remove a blue
      //      srv.removeParticipant(ssk1.getId());
      //      assertEquals("red participant size", 2, srv.getListOfRedParticipants().length);
      //
      //      final int lenBefore = srv.getListOfBlueParticipants().length;
      //      srv.removeParticipant(ssn.getId());
      //      assertEquals("blue participant size", lenBefore - 1, srv.getListOfBlueParticipants().length);
      //
      //
      //      // check stepping through
      //      srv.step();

      // todo: reinstate tests
    }

  }

}