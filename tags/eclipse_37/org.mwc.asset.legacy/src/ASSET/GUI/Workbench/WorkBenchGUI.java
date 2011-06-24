/*
 * Desciption:
 * User: administrator
 * Date: Nov 8, 2001
 * Time: 11:08:03 AM
 */
package ASSET.GUI.Workbench;

import ASSET.GUI.Core.CoreGUISwing;
import ASSET.GUI.MonteCarlo.Loader;
import ASSET.GUI.Painters.ScenarioNoiseLevelPainter;
import ASSET.GUI.Painters.ScenarioNoiseLevelPainter.ParticipantStatus;
import ASSET.GUI.Tools.View3dPlot;
import ASSET.GUI.Util.FileList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.DragDrop.FileDropLocationSupport;
import MWC.GUI.DragDrop.FileDropSupport;
import MWC.GUI.Layer;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.MenuItemInfo;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Vector;

public class WorkBenchGUI extends CoreGUISwing implements ScenarioNoiseLevelPainter.StatusProvider
{
  /***************************************************************
   *  member variables
   ***************************************************************/

  /**
   * the name of this application
   */
  private final static String _MyName = "ASSET Workbench";

  /**
   * support for dropping participants into the chart
   */
  private FileDropLocationSupport _participantDropper = null;

  /**
   * support for dropping scenarios into the props panel
   */
  private FileDropSupport _scenarioDropper = null;

  /**
   * the object which handles plotting the scenario data onto the plot
   */
  ASSET.GUI.Workbench.Plotters.ScenarioLayer _scenarioPlotter = null;

  /**
   * container for the Monte Carlo loader, if required.
   */
  static JComponent _loaderHolder = null;
  private static final String USER_QUIT_MESSAGE = "User quit";

  /***************************************************************
   *  constructor
   ***************************************************************/
  /**
   * constructor, of course
   *
   * @param theParent the toolparent = where we control the cursor from
   */
  WorkBenchGUI(final CoreGUISwing.ASSETParent theParent)
  {
    super(theParent);

    // "throttle-back" the scenario, so the GUI's can listen
    super._theScenario.setStepTime(100);

  }


  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */


  void participantDropped(final Vector<File> files, final Point point)
  {
    final Iterator<File> ii = files.iterator();
    while (ii.hasNext())
    {
      final File file = (File) ii.next();
      // read in this file

      try
      {
        final ASSET.ParticipantType newPart = ASSETReaderWriter.importParticipant(file.getName(), new java.io.FileInputStream(file));

        // ok, now set the location
        final WorldLocation loc = new WorldLocation(getChart().getCanvas().getProjection().toWorld(point));
        if (loc != null)
          newPart.getStatus().setLocation(loc);

        // now set the time
        newPart.getStatus().setTime(_theScenario.getTime());

        // and store it in the scenario
        if (newPart != null)
          this._theScenario.addParticipant(ASSET.ScenarioType.INVALID_ID, newPart);
      }
      catch (java.io.FileNotFoundException fe)
      {
        MWC.Utilities.Errors.Trace.trace(fe, "Reading in dragged participant file");
      }

    }
  }

  public void scenarioDropped(final Vector<File> files) throws FileNotFoundException
  {
    final Iterator<File> ii = files.iterator();
    while (ii.hasNext())
    {
      final File file = (File) ii.next();
      // read in this file
      ASSETReaderWriter.importThis(_theScenario, file.getName(), new java.io.FileInputStream(file));
      super._theParent.setTitle(_MyName + " - " + _theScenario.getName());
    }
  }

  /**
   * extra call to allow any CoreGUI-level components the be initialised before we
   * start creating the GUI components
   */
  protected void constructorSteps()
  {
    _theScenario.setScenarioStepTime(60000);
    _theScenario.setTime(new java.util.Date().getTime());

    _participantDropper = new FileDropLocationSupport();
    _participantDropper.setFileDropListener(new FileDropLocationSupport.FileDropLocationListener()
    {
      public void FilesReceived(final Vector<File> files, final Point point)
      {
        participantDropped(files, point);
      }
    }, ".A_P");

    _scenarioDropper = new FileDropSupport();
    _scenarioDropper.setFileDropListener(new FileDropSupport.FileDropListener()
    {
      public void FilesReceived(final Vector<File> files)
      {
        try
        {
          scenarioDropped(files);
        }
        catch (FileNotFoundException e)
        {
          MWC.Utilities.Errors.Trace.trace("Sorry one of the files to load was not found", true);
          MWC.Utilities.Errors.Trace.trace(e);
        }
      }
    }, ".XML");

  }


  /**
   * get the current list of vessel statuses, put into an array of structures
   */
  public Vector<ParticipantStatus> getStatuses(final int theMedium)
  {
    final Vector<ScenarioNoiseLevelPainter.ParticipantStatus> res = new Vector<ParticipantStatus>(0, 1);
    final Integer[] list = _theScenario.getListOfParticipants();
    for (int j = 0; j < list.length; j++)
    {
      final ParticipantType pt = _theScenario.getThisParticipant(list[j].intValue());
      final ScenarioNoiseLevelPainter.ParticipantStatus ps = new ScenarioNoiseLevelPainter.ParticipantStatus();
      ps.location = pt.getStatus().getLocation();
      ps.sourceLevel = pt.getRadiatedNoiseFor(theMedium, 0);
      res.add(ps);
    }
    return res;
  }

  /**
   * listen out for status changes
   */
  public void addScenarioChangeListener(final ScenarioSteppedListener listener)
  {
    super._theScenario.addScenarioSteppedListener(listener);
  }

  /**
   * stop listening for status changes
   */
  public void removeScenarioChangeListener(final ScenarioSteppedListener listener)
  {
    super._theScenario.removeScenarioSteppedListener(listener);
  }

  /**
   * allow our child classes to add new gui components
   */
  protected void addUniqueComponents(final CoreGUISwing.MyToolbarHolder assetHolder)
  {

    getChart().getCanvas().getProjection().setDataArea(new MWC.GenericData.WorldArea(new MWC.GenericData.WorldLocation(0, 0, 0),
                                                                                     new MWC.GenericData.WorldLocation(2, 2, 2)));

    //    WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

    getChart().getCanvas().setBackgroundColor(new Color(108, 150, 184));

    // create panes to put our library into
    _behavioursHolder = new JTabbedPane();
    _behavioursHolder.setName("Library");

    // create the library objects
    FileList flBehaviour = new FileList("Behaviours", "Behaviours", null);
    assetHolder.add(flBehaviour);
    final FileList flVessels = new FileList("Vessels", "Vessels", "a_p");
    assetHolder.add(flVessels);

    // add the file droppers
    _participantDropper.addComponent(getChart().getPanel());
    _scenarioDropper.addComponent((SwingPropertiesPanel) _theProperties);


    getChart().getCanvas().getProjection().setDataArea(new WorldArea(new MWC.GenericData.WorldLocation(0, 0, 0),
                                                                     new MWC.GenericData.WorldLocation(2, 2, 2)));

    // add the chart plotter
    _scenarioPlotter = new
      ASSET.GUI.Workbench.Plotters.ScenarioLayer();
    _scenarioPlotter.setScenario(_theScenario);
    _theData.addThisLayer(_scenarioPlotter);


    final Layers localData = _theData;
    
    // listen out for new participants
    _theScenario.addParticipantsChangedListener(new ASSET.Scenario.ParticipantsChangedListener()
    {
      public void newParticipant(int index)
      {
      	localData.fireModified(null);
      }

      public void participantRemoved(int index)
      {
      	localData.fireModified(null);
      }

      public void restart(ScenarioType scenario)
      {
      }
    });

    // listen to the time stepping
    _theScenario.addScenarioSteppedListener(new ASSET.Scenario.ScenarioSteppedListener()
    {
      public void step(ScenarioType scenario, final long time)
      {
        // tell the data it's been modified
        //  _theData.fireModified(null);
      	localData.fireModified(_scenarioPlotter);
        // update the clock
        setTime(time);
      }

      public void restart(ScenarioType scenario)
      {
      }
    });

    _theScenario.addScenarioRunningListener(new ScenarioRunningListener()
    {
      public void started()
      {
      }

      public void paused()
      {
      }

      public void finished(long elapsedTime, String reason)
      {
        // did we cause it?
        if (reason.equals(USER_QUIT_MESSAGE))
        {
          // don't bother telling the user - he triggered it anyway
        }
        else
        {
          // tell the user why the scenario has stopped
          MWC.GUI.Dialogs.DialogFactory.showMessage("Scenario Stop", "Reason:" + reason);
        }
      }

      public void newScenarioStepTime(int val)
      {
      }

      public void newStepTime(int val)
      {
      }

      public void restart(ScenarioType scenario)
      {
      }
    });

    setupComms();

    // temporarily add the misc layer
    Layer misc = _theData.findLayer("Misc");
    if (misc == null)
    {
      misc = new BaseLayer();
      misc.setName("Misc");
      _theData.addThisLayer(misc);
    }
    misc.add(new ASSET.GUI.Painters.Detections.ScenarioDetectionPainter(_theScenario));

  }

  /**
   * set up the RMI server code
   */
  private void setupComms()
  {
    //    // first set up the RMI comms part
    //    try
    //    {
    //      ServerImpl serRMI = new ServerImpl(this._theServer);
    //    }
    //    catch (RemoteException e)
    //    {
    //      e.printStackTrace();
    //    }

    // provide listener support for discovery of our servce
  }

  /**
   * update the time displayed
   */
  void setTime(final long time)
  {
    final String newT = MWC.Utilities.TextFormatting.FullFormatDateTime.toString(time);
    _theTime.setText(newT);

  }

  /**
   * build the list of ASSET-related tools
   */
  protected void addUniqueTools()
  {
  	final CoreScenario localScenario = _theScenario;
  	final PlainChart localChart = getChart();
  	
    _theTools.addElement(new MenuItemInfo("ASSET", null, "Step",
                                          new PlainTool(_theParent, "Step", "images/VCRForward.gif")
                                          {
                                            public Action getData()
                                            {
                                            	localScenario.step();
                                              return null;
                                            }
                                          }, null, 't'));

    _theTools.addElement(new MenuItemInfo("ASSET", "StartStop", "Start",
                                          new PlainTool(_theParent, "Start", "images/VCRFastForward.gif")
                                          {
                                            public Action getData()
                                            {
                                            	localScenario.start();
                                              return null;
                                            }
                                          }, null, 'a'));


    _theTools.addElement(new MenuItemInfo("ASSET", "StartStop", "Pause",
                                          new PlainTool(_theParent, "Pause", "images/VCRPause.gif")
                                          {
                                            public void execute()
                                            {
                                            	localScenario.pause();
                                            }

                                            public Action getData()
                                            {
                                              return null;
                                            }
                                          }, null, 'o'));

    _theTools.addElement(new MenuItemInfo("ASSET", null, "Restart",
                                          new PlainTool(_theParent, "Restart", "images/VCRBegin.gif")
                                          {
                                            public Action getData()
                                            {
                                              // get the scenario to go back to the start
                                            	localScenario.restart();

                                              // and redraw the plot
                                            	localChart.update();
                                              return null;
                                            }
                                          }, null, 'o'));

    _theTools.addElement(new MenuItemInfo("ASSET", null, "Exit",
                                          new PlainTool(_theParent, "Exit", "images/VCRStop.gif")
                                          {
                                            public Action getData()
                                            {
                                              // there's a good chance that errors will be thrown whilst
                                              // quitting.  Wrap the finish steps so that we know that
                                              // the user can get out
                                              try
                                              {
                                                // tell the scenario to stop after this cycle
                                              	localScenario.stop(USER_QUIT_MESSAGE);

                                                // trigger another cycle so that the scenario can find out that
                                                // we've stopped it
                                              	localScenario.step();
                                              }
                                              catch (Exception e)
                                              {
                                                MWC.Utilities.Errors.Trace.trace(e);
                                              }

                                              // and quit.
                                              System.exit(0);
                                              return null;
                                            }
                                          }, null, 'x'));

    _theTools.addElement(new MenuItemInfo("File", null, "Save",
                                          new ASSET.Util.XML.Tools.SaveScenarioXML(_theParent, _theScenario)
                                          , null, 'S'));

    _theTools.addElement(new MenuItemInfo("File", null, "SaveAs",
                                          new ASSET.Util.XML.Tools.SaveScenarioAsXML(_theParent, _theScenario)
                                          , null, 'A'));

    _theTools.addElement(new MenuItemInfo("File", null, "Monte Carlo importer",
                                          new ASSET.GUI.Tools.OpenMonteCarloPanel(_theParent,
                                                                                  (SwingPropertiesPanel) _theProperties, getChart(),
                                                                                  _theScenario)
                                          , null, 'M'));
    _theTools.addElement(new MenuItemInfo("Noise", null, "Noise Levels",
                                          new ASSET.GUI.Tools.CreateNoiseLevel(_theParent,
                                                                               _theProperties,
                                                                               null,
                                                                               _theData,
                                                                               getChart(),
                                                                               _theScenario.getEnvironment(),
                                                                               this,
                                                                               EnvironmentType.BROADBAND_PASSIVE)
                                          , null, 'N'));

    _theTools.addElement(new MenuItemInfo("Noise", null, "Noise Source",
                                          new ASSET.GUI.Tools.CreateNoiseSource(_theParent,
                                                                                _theProperties,
                                                                                null,
                                                                                _theData,
                                                                                getChart(),
                                                                                _theScenario.getEnvironment(),
                                                                                EnvironmentType.BROADBAND_PASSIVE)
                                          , null, 'A'));

    _theTools.addElement(new MenuItemInfo("Noise", null, "Noise Excess",
                                          new ASSET.GUI.Tools.CreateNoiseExcess(_theParent,
                                                                                _theProperties,
                                                                                null,
                                                                                _theData,
                                                                                getChart())
                                          , null, 'A'));


    // NOTE: wrap this next creator, in case we haven't got the right files avaialble
    try
    {
      _theTools.addElement(new MenuItemInfo("View", null, "View in 3d",
                                            new View3dPlot(_theParent, _theProperties, _theData, _theScenario, "ASSET"), null, ' '));
    }
    catch (java.lang.NoClassDefFoundError e)
    {
      System.err.println("3D Viewer not provided, classes not found");
      //e.printStackTrace();
    }

  }

  public static void main(String[] args)
  {
    //    try{
    //    UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
    //    }catch(UnsupportedLookAndFeelException e)
    //    {
    //      e.printStackTrace();
    //    }

    // create the interface
    final JFrame parent = new JFrame(_MyName);

    parent.setSize(1100, 600);
    parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // open the splash screen
    CoreGUISwing.showSplash(parent, "images/WorkBenchLogo.gif");

    // create the tool-parent
    final CoreGUISwing.ASSETParent pr = new CoreGUISwing.ASSETParent(parent);

    // create the workbench
    final WorkBenchGUI viewer = new WorkBenchGUI(pr);

    // collate the parent
    parent.getContentPane().setLayout(new BorderLayout());
    parent.getContentPane().add("Center", viewer.getPanel());
    parent.doLayout();

    parent.setVisible(true);

    // have we received any files to load?
    if (args.length == 1)
    {
      // one file has been dropped in, load it
      try
      {
        Vector<File> theFiles = new Vector<File>();
        for (int i = 0; i < args.length; i++)
        {
          String thisFile = args[i];
          // put the datafile into a vector
          theFiles.add(new File(thisFile));
        }

        // load the data
        viewer.scenarioDropped(theFiles);

        // trigger a fit-to-win
        viewer.FitToWin();
      }
      catch (FileNotFoundException e)
      {
        MWC.GUI.Dialogs.DialogFactory.showMessage("Load files", "Sorry one of the files to load was not found" + e.getMessage());
        MWC.Utilities.Errors.Trace.trace(e.getLocalizedMessage(), false);
      }
    }
    else if (args.length == 2)
    {
      // two files have been dropped in, configure a Monte Carlo run
      String scenarioFile = args[0];
      String controlFile = args[1];

      // lastly, experimentally, add the Monte Carlo loader
      final Loader theLoader = new Loader((CoreScenario) viewer.getScenario(), scenarioFile, controlFile,
                                          viewer.getChart(), viewer.getParent(), viewer.getProperties())
      {
        /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				/**
         * user has tried to close the loader panel = we'll just hide it
         */
        public void doClose()
        {
          viewer.getProperties().remove(_loaderHolder);
        }
      };

      SwingPropertiesPanel spp = (SwingPropertiesPanel) viewer.getProperties();
      _loaderHolder = spp.addThisPanel(theLoader);
    }
  }

}
