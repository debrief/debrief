// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: View3dPlot.java,v $
// @author $Author$
// @version $Revision$
// $Log: View3dPlot.java,v $
// Revision 1.1  2006/08/08 14:21:21  Ian.Mayo
// Second import
//
// Revision 1.1  2006/08/07 12:25:30  Ian.Mayo
// First versions
//
// Revision 1.4  2004/11/25 14:29:28  Ian.Mayo
// Handle switch to hi-res dates
//
// Revision 1.3  2004/09/08 09:06:08  ian
// Idea tidying, remove link to unused ETOPO class
//
// Revision 1.2  2004/05/24 15:39:30  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:30:50  ian
// no message
//
// Revision 1.1.1.1  2003/07/25 09:58:07  Ian.Mayo
// Repository rebuild.
//
// Revision 1.4  2003/06/11 15:03:09  ian_mayo
// Find time anchor for current dataset
//
// Revision 1.3  2002-10-29 16:25:53+00  ian_mayo
// general improvements (supporting intercept, etc)
//
// Revision 1.2  2002-10-10 16:12:25+01  ian_mayo
// general mods, mostly because of IntelliJ Idea code inspections
//
// Revision 1.1  2002-09-17 11:24:34+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-05-28 09:25:08+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:52+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-23 13:08:19+01  ian
// Reflect class name changes
//
// Revision 1.3  2002-05-07 16:09:30+01  ian_mayo
// Reflect participant name changes
//
// Revision 1.2  2002-05-07 08:45:24+01  ian_mayo
// Restructure how we listen out for layers changing
//
// Revision 1.1  2002-05-01 15:46:43+01  ian
// Add support for listening out for tracks being added/removed
//
// Revision 1.0  2002-04-30 09:14:54+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:06+01  ian_mayo
// Initial revision
//
// Revision 1.3  2001-11-20 11:23:17+00  administrator
// changed method signature
//
// Revision 1.2  2001-08-21 12:14:23+01  administrator
// Remove file drop support
//
// Revision 1.1  2001-08-17 08:05:14+01  administrator
// Try to clear up memory leaks
//
// Revision 1.0  2001-07-17 08:41:20+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:31+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:48:27  ianmayo
// initial import of files
//
// Revision 1.6  2000-10-10 13:05:29+01  ian_mayo
// Update to reflect new FileDrag/Drop code
//
// Revision 1.5  2000-10-10 12:20:31+01  ian_mayo
// location of drag drop has changed
//
// Revision 1.4  2000-10-09 13:37:29+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.3  2000-06-19 15:05:36+01  ian_mayo
// moved the code which puts the tracks into the plot into the TimedFrame3d class
//
// Revision 1.2  2000-06-15 18:06:33+01  ian_mayo
// replaced DropFile support, and recognised sub-classing of Frame3d to TimedFrame3d
//
// Revision 1.1  2000-06-15 13:45:49+01  ian_mayo
// Initial revision
//

package ASSET.GUI.Tools;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.media.j3d.Group;
import javax.swing.JFrame;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.GUI.Tools.Plot3D.ASSETParticipant3D;
import ASSET.Scenario.ScenarioSteppedListener;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.StepperListener;
import MWC.GUI.ToolParent;
import MWC.GUI.ETOPO.BathyProvider;
import MWC.GUI.Java3d.MouseWheelWorldHolder;
import MWC.GUI.Java3d.WorldHolder;
import MWC.GUI.Java3d.Tactical.Participant3D;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.HiResDate;

/**
 * command to import a file (initially just Replay) into Debrief.
 * The data used to implement the command is stored as a command,
 * so that it may be added to an undo buffer.
 */
public class View3dPlot extends PlainTool // implements Layers.DataListener
{ //implements FileDropSupport.FileDropListener {

  ///////////////////////////////////////////////////////////////
  // member variables
  ///////////////////////////////////////////////////////////////

  /**
   * the properties panel to put ourselves into
   */
  private PropertiesPanel _thePanel;

  /**
   * the scenario the 3d viewer listens to
   */
  private ScenarioType _theScenario;

  /**
   * the set of layers were are plotting
   */
  private Layers _theLayers;

  /**
   * make an adapter for the scenario, so it appears like a
   * stepper controller
   */
  private StepperControllerAdapter _stepperAdapter;

  /**
   * the class which provides bathy data
   */
  private BathyProvider _bathyProvider;

  /**
   * the name of the application producing this view
   */
  final private String _appName;

  ///////////////////////////////////////////////////////////////
  // constructor
  ///////////////////////////////////////////////////////////////
  /**
   * constructor, taking information ready for when the button
   * gets pressed
   *
   * @param theParent the ToolParent window which we control the cursor of
   * @param appName   the application calling this view (name placed in title bar)
   */
  public View3dPlot(final ToolParent theParent,
                    final PropertiesPanel thePanel,
                    final Layers theData,
                    final ScenarioType theScenario,
                    final String appName)
  {
    super(theParent, "View in 3d", "images/view3d.gif");
    _appName = appName;
    _theScenario = theScenario;
    // store the Session
    _thePanel = thePanel;
    _theLayers = theData;

    // wrap the step control
    _stepperAdapter = new StepperControllerAdapter(theScenario);
  }


  public Action getData()
  {
    return null;
  }


  public void execute()
  {

    try
    {
      WorldHolder tmpHolder = null;

      // prepare the bathy data
      //      ETOPOPainter painter = (ETOPOPainter) CreateETOPO.loadBathyData(null);
      //      ETOPOWrapper _bathyProvider = painter.getETOPO();

      // SPECIAL PROCESSING, open up a mouse-less view if we are running jdk1.3
      try
      {
        tmpHolder = new MouseWheelWorldHolder(_thePanel, _stepperAdapter, _theLayers, _bathyProvider, true)
        {
          /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void dataExtended(final Layers theData)
          {
            //
            doDataExtended(theData, this);
          }

          public void dataModified(final Layers theData, Layer changedLayer)
          {
            // just pass it on to the extended method
            dataExtended(theData);
          }

          public void dataReformatted(final Layers theData, final Layer changedLayer)
          {
            //
            doDataReformatted(theData, changedLayer, this);
          }
        };
      }
      catch (java.lang.NoClassDefFoundError er)
      {
        tmpHolder = new WorldHolder(_thePanel, _stepperAdapter, _theLayers, _bathyProvider, true)
        {
          /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void dataExtended(final Layers theData)
          {
            doDataExtended(theData, this);
          }

          public void dataModified(final Layers theData, Layer changedLayer)
          {
            // just pass it on to the extended method
            dataExtended(theData);
          }

          public void dataReformatted(final Layers theData, final Layer changedLayer)
          {
            // see if one of our tracks has changed colour or symbol
            doDataReformatted(theData, changedLayer, this);

          }
        };
      }

      final WorldHolder worldHolder = tmpHolder;

      // we pass in a dummy branch group
//      final BranchGroup tra = new BranchGroup();

      // add the tracks
      final Collection<ParticipantType> tracks = getParticipants(_theScenario);

      for (final Iterator<ParticipantType> thisT = tracks.iterator(); thisT.hasNext();)
      {
        final ParticipantType thisParticipant = thisT.next();

        // check this track contains data
        final ASSETParticipant3D t3 = new ASSETParticipant3D(worldHolder.getWorld().getWorldPlottingOptions(),
                                                             worldHolder.getWorld(),
                                                             thisParticipant,
                                                             _theScenario);
        // store the track
        worldHolder.addTrack(t3);

      }
      // add the buoyfields


      // done
      worldHolder.finish();



      // and put it into a frame
      final JFrame worldF = new JFrame(_appName + " 3D View");
      worldF.setSize(600, 400);
      worldF.getContentPane().setLayout(new BorderLayout());
      worldF.getContentPane().add(worldHolder);
      worldF.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      worldF.addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          worldHolder.doClose();
        }
      });
      worldF.setVisible(true);

    }
    catch (NoClassDefFoundError e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
      final String msg = "3D Java libraries not found, please check installation";
      MWC.GUI.Dialogs.DialogFactory.showMessage("View 3d",
                                                msg);
    }

  }


  /**
   * **************************************************************
   * layers support listening
   * **************************************************************
   */
  void doDataReformatted(Layers theData, Layer changedLayer, WorldHolder theHolder)
  {

  }

  @SuppressWarnings("rawtypes")
	void doDataExtended(Layers theData, final WorldHolder theHolder)
  {

    // get the new list of tracks
    final Collection<ParticipantType> theParticipants = getParticipants(_theScenario);

    // create somewhere to store our existing children
    final Group myTracks = theHolder.getWorld().getTracks();

    // see if we hold all of the tracks
    final Enumeration it = myTracks.getAllChildren();
    while (it.hasMoreElements())
    {
      final Participant3D thisP = (Participant3D) it.nextElement();
      // get the track
      final Object thisTrack = thisP.getUserData();

      // see if this participant is still in the tracks
      if (theParticipants.contains(thisTrack))
      {
        // ok, we're all safe
      }
      else
      {
        // remove this from the world and our toolbar
        theHolder.removeThisTrack(thisP);

      } // whether this track is in the new list

      // remove this track from the list of those found - since we've handled it
      theParticipants.remove(thisTrack);

    } // stepping through the current tracks

    // now see if there are any tracks in the new list which we don't have
    if (theParticipants.size() > 0)
    {
      final Iterator<ParticipantType> iter = theParticipants.iterator();
      while (iter.hasNext())
      {
        // create a 3d version of this track
        final ParticipantType rw = iter.next();

        // we receive data extended message twice.  Once when the new layer is added (but is still empty),
        // and again once the file has been read in (and the track contains data)

        // check this track contains data
        final ASSETParticipant3D t3 = new ASSETParticipant3D(theHolder.getWorld().getWorldPlottingOptions(),
                                                             theHolder.getWorld(),
                                                             rw,
                                                             _theScenario);
        // store the track
        theHolder.addTrack(t3);
      }
    }

  }

  private static Collection<ParticipantType> getParticipants(final ScenarioType theScenario)
  {
    final Vector<ParticipantType> vec = new Vector<ParticipantType>(0, 1);

    // step through the layers
    final Integer[] list = theScenario.getListOfParticipants();
    for (int i = 0; i < list.length; i++)
    {
      final Integer thisOne = list[i];
      final ParticipantType thisPart = theScenario.getThisParticipant(thisOne.intValue());
      vec.add(thisPart);
    }
    return vec;
  }


  /**
   * *************************************************
   * adapter class to make a scenario look like a stepper
   * *************************************************
   */
  public static class StepperControllerAdapter implements StepperListener.StepperController,
    ScenarioSteppedListener
  {

    /****************************************************
     * member variables
     ***************************************************/
    /**
     * the scenario we listen to
     */
    private ScenarioType _myScenario;

    /**
     * the list of stepper listeners
     */
    private Vector<StepperListener> _theListeners = null;


    /****************************************************
     * constructor
     ***************************************************/
    /**
     * constructor
     */
    public StepperControllerAdapter(final ScenarioType theScenario)
    {
      this._myScenario = theScenario;
      _theListeners = new Vector<StepperListener>(0, 1);
    }


    /****************************************************
     * member methods
     ***************************************************/
    /**
     * add a new listener
     */
    public void addStepperListener(final StepperListener listener)
    {
      // are we currently listening?
      if (_theListeners.size() == 0)
      {
        // no, let's start
        _myScenario.addScenarioSteppedListener(this);
      }

      _theListeners.add(listener);

    }

    /**
     * remove a listener
     */
    public void removeStepperListener(final StepperListener listener)
    {
      _theListeners.remove(listener);

      // is it time to stop listening?
      if (_theListeners.size() == 0)
      {
        _myScenario.removeScenarioSteppedListener(this);
      }
    }

    /**
     * instruct the stepper to move forwards, backwards
     */
    public void doStep(boolean forward, boolean large_step)
    {
      // ignore the time step request
      System.out.println("IGNORED");
    }

    /**
     * find out what the current time is
     */
    public HiResDate getCurrentTime()
    {
      return new HiResDate(_myScenario.getTime());
    }


    /****************************************************
     * scenario stepping support
     ***************************************************/
    /**
     * the scenario has stepped forward
     */
    public void step(ScenarioType scenario, final long newTime)
    {
      // pass this through the listeners
      final Iterator<StepperListener> it = _theListeners.iterator();
      while (it.hasNext())
      {
        final StepperListener sl = it.next();
        sl.newTime(null, new HiResDate(newTime), null);
      }
    }

    /**
     * the scenario has restarted, reset
     */
    public void restart(ScenarioType scenario)
    {
      // hey, just ignore it!
    }

    /**
     * determine the zero-time or anchor for the current dataset
     */
    public HiResDate getTimeZero()
    {
      return null;
    }
  }


}
