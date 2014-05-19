package ASSET.GUI.SuperSearch;

import ASSET.ScenarioType;
import ASSET.GUI.SuperSearch.Plotters.SSGuiSupport;
import ASSET.Scenario.ScenarioSteppedListener;
import MWC.GUI.Layer;

/**
 * Title:     CoreSuperSearch
 * Description:  core code to fulfil GUI requirements of SuperSearch
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Ian Mayo
 * @version 1.0
 */


/**
 * Structure of SuperSearch:
 * CoreSuperSearch:
 *   non-gui framework which handles:
 *     - creation of server, scenario, participants
 *     - listening to participant creation, movement, detections
 *     - maintaining stats of detections, movement, performance
 *     - format of presentation of results
 *
 */

public class CoreSuperSearch implements ASSET.Scenario.ScenarioSteppedListener {


  /***********************************************************************
   * local variables
   ***********************************************************************/

  /** our local server
   *
   */
  private ASSET.ServerType _myServer;

   /** our local scenario
    *
    */
  ASSET.Scenario.MultiForceScenario _myScenario;

  /** the object keeping track of participant statuses
   *
   */
  final SSGuiSupport _guiSupport = new SSGuiSupport();


  /** the list of stepping listeners
   *
   */
  private java.util.Vector<ScenarioSteppedListener> _stepListeners;

  /***********************************************************************
   * constructor
   ***********************************************************************/
  public CoreSuperSearch()
  {
    create();
  }
  /***********************************************************************
   * member methods
   ***********************************************************************/

  public Layer getDataLayer()
  {
    return _guiSupport;
  }

  /** create the components
   *
   */
  private void create()
  {
    // create the server
    _myServer = new ASSET.Server.CoreServer();

    // create and store the scenario
    final int index = _myServer.createNewScenario(ASSET.Scenario.MultiForceScenario.TYPE);
    _myScenario = (ASSET.Scenario.MultiForceScenario) _myServer.getThisScenario(index);

    // setup the gui listener
    _guiSupport.setScenario(_myScenario);

    // and listen for the scenario stepping
    _myScenario.addScenarioSteppedListener(this);


  }

  /***********************************************************************
   * listen out for scenario step
   ***********************************************************************/
  /** the scenario has stepped forward
   *
   */
  public void step(ScenarioType scenario, final long newTime)
  {
    fireScenarioStepped(scenario, newTime);
  }


  /** handle the restart event
   *
   */
  public void restart(ScenarioType scenario)
  {
    if(_stepListeners != null)
    {
      final java.util.Iterator<ScenarioSteppedListener> it = _stepListeners.iterator();
      while(it.hasNext())
      {
        final ASSET.Scenario.ScenarioSteppedListener pcl = (ASSET.Scenario.ScenarioSteppedListener)it.next();
        pcl.restart(scenario);
      }
    }
  }

  private void fireScenarioStepped(ScenarioType scenario, final long time)
  {
    if(_stepListeners != null)
    {
      final java.util.Iterator<ScenarioSteppedListener> it = _stepListeners.iterator();
      while(it.hasNext())
      {
        final ASSET.Scenario.ScenarioSteppedListener pcl = (ASSET.Scenario.ScenarioSteppedListener)it.next();
        pcl.step(scenario, time);
      }
    }
  }

  public void addScenarioSteppedListener(final ASSET.Scenario.ScenarioSteppedListener listener)
  {
    if(_stepListeners == null)
      _stepListeners = new java.util.Vector<ScenarioSteppedListener>(1,2);

    _stepListeners.add(listener);
  }


  public void removeScenarioSteppedListener(final ASSET.Scenario.ScenarioSteppedListener listener)
  {
    _stepListeners.remove(listener);
  }

  /***********************************************************************
   * testing code
   ***********************************************************************/
//  public static class UNtestSuperSearch extends junit.framework.TestCase
//  {
//    // todo: reinstate this test - using the correct monte carlo generator schema files
//
//
//    static public final String TEST_ALL_TEST_TYPE  = "UNTIE";
//    public UNtestSuperSearch(final String val)
//    {
//      super(val);
//    }
//    public void testCreate()
//    {
//      final int per_side = 2;
//
//      // create object
//      final CoreSuperSearch css = new CoreSuperSearch();
//      assertNotNull("created SuperSearch", css);
//
//      // create servers
//      css.create();
//      assertNotNull("created server", css._myServer);
//      assertNotNull("created scenario", css._myScenario);
//      assertNotNull("created monitor", css._guiSupport);
//
//      // set the parameters
//      final MWC.GenericData.WorldArea coverage = new MWC.GenericData.WorldArea(new MWC.GenericData.WorldLocation(0,0,0),
//                                                                         new MWC.GenericData.WorldLocation(1,1,0));
//
//
//      // build scenario
//      String root = System.getProperty("TEST_ROOT");
//      if(root == null)
//      {
//        root = "d:\\dev\\asset2_out\\";
//      }
//
//      // check files exist
//      String s1 = root + "SuperSearch\\ss1_ssk.xml";
//      String s2 = root + "SuperSearch\\ss1_control.xml";
//      java.io.File f1 = new File(s1);
//      java.io.File f2 = new File(s2);
//      assertTrue("SSK file not found", f1.exists());
//      assertTrue("control file not found", f2.exists());
//
//      css._myScenario.createRedForce(s1, s2);
//      assertEquals("gui has heard all new creations", css._guiSupport.getVector().size(), 100);
//
//      // add the blue vessel
//      final ASSET.Participants.Status stat = new ASSET.Participants.Status(2, 12);
//      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(0.1,0.1,1);
//      stat.setLocation(origin);
//      stat.setCourse(0);
//      stat.setSpeed(new WorldSpeed(3, WorldSpeed.M_sec));
//
//      final ASSET.ParticipantType ssn = new ASSET.Models.Vessels.SSN(20, stat, new SimpleDemandedStatus(0, stat), "BLUE_SSN");
//      ssn.getCategory().setForce(ASSET.Participants.Category.Force.BLUE);
//
//      css._myScenario.addBlueParticipant(20, ssn);
//
//      // step forward, see if gui updated
//      css._myScenario.setScenarioStepTime(300000);
//      css._myScenario.setTime(0);
//      css._myScenario.step();
//      final SSGuiSupport.ParticipantListener pl = (SSGuiSupport.ParticipantListener) css._guiSupport.getVector().lastElement();
//
//      // check location
//      final MWC.GenericData.WorldLocation newLocation = new MWC.GenericData.WorldLocation(pl.getLocation());
////      assertEquals("listener has correct location", newLocation.rangeFrom(new MWC.GenericData.WorldLocation(0,0,0)), 0, 0.005 );
//
//      // move, and see if it has moved
//      css._myScenario.step();
//      super.assertTrue("We've moved", (pl.getLocation() != newLocation));
//      System.out.println("range:" + MWC.Algorithms.Conversions.Degs2Yds(pl.getLocation().rangeFrom(newLocation)));
//
//
//
//    }
//  }
//
//
//
//  public static void main(String[] args) {
//    final UNtestSuperSearch ss = new UNtestSuperSearch("dummy");
//    ss.testCreate();
//  }
}