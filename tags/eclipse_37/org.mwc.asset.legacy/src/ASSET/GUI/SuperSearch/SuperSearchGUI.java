/*
 * Desciption:
 * User: administrator
 * Date: Nov 8, 2001
 * Time: 11:23:14 AM
 */
package ASSET.GUI.SuperSearch;

import java.awt.Color;

import javax.swing.JFrame;

import ASSET.ScenarioType;
import ASSET.GUI.Core.CoreGUISwing;
import MWC.GUI.Layers;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.MenuItemInfo;
import MWC.GUI.Tools.PlainTool;

public class SuperSearchGUI extends CoreGUISwing
{
  /***************************************************************
   *  member variables
   ***************************************************************/

  /**
   * our super search object
   */
  CoreSuperSearch _searcher;

  /***************************************************************
   *  constructor
   ***************************************************************/
  /**
   * constructor, of course
   *
   * @param theParent the toolparent = where we control the cursor from
   */
  private SuperSearchGUI(final CoreGUISwing.ASSETParent theParent)
  {
    super(theParent);

  }
  /***************************************************************
   *  member methods
   ***************************************************************/
  /**
   * allow our child classes to add new gui components
   */
  protected void addUniqueComponents(CoreGUISwing.MyToolbarHolder assetHolder)
  {
    /////////////////////////////////////////////////
    // ASSET bits
    /////////////////////////////////////////////////
    final SSBuilderSwing ssb = new SSBuilderSwing(_searcher._myScenario, super._theData);
    _theProperties.add(ssb);

  }

  /**
   * extra call to allow any CoreGUI-level components the be initialised before we
   * start creating the GUI components
   */
  protected void constructorSteps()
  {
    _searcher = new CoreSuperSearch();

    setupScenario();
  }

  /**
   * setup the scenario
   */
  private void setupScenario()
  {
    _searcher._myScenario.setTime(0);
    _searcher._myScenario.setScenarioStepTime(60000);
    _searcher._myScenario.setStepTime(0);
    
    final Layers localData = _theData;

    // connect the layers to the scenario
    _searcher.addScenarioSteppedListener(new ASSET.Scenario.ScenarioSteppedListener()
    {
      @SuppressWarnings("synthetic-access")
			public void step(ScenarioType scenario, final long time)
      {
        if (_searcher != null)
        {
        	localData.fireModified(_searcher.getDataLayer());
        }
        else
        	localData.fireModified(null);

        // update the clock
        setTime(MWC.Utilities.TextFormatting.FullFormatDateTime.toString(time));
      }

      @SuppressWarnings("synthetic-access")
			public void restart(ScenarioType scenario)
      {
        // reset the data
      	localData.fireModified(null);

        // and reset the time
        setTime("------");
      }
    });


  }


  /**
   * build the list of ASSET-related tools
   */
  protected void addUniqueTools()
  {

    _theTools.addElement(new MenuItemInfo("ASSET", null, "Step",
        new PlainTool(_theParent, "Step", "images/VCRForward.gif")
        {
          public Action getData()
          {
            _searcher._myScenario.step();
            return null;
          }
        }, null, 't'));

    _theTools.addElement(new MenuItemInfo("ASSET", "StartStop", "Start",
        new PlainTool(_theParent, "Start", "images/VCRFastForward.gif")
        {
          public Action getData()
          {
            _searcher._myScenario.start();
            return null;
          }
        }, null, 'a'));


    _theTools.addElement(new MenuItemInfo("ASSET", "StartStop", "Stop",
        new PlainTool(_theParent, "Stop", "images/VCRPause.gif")
        {
          public Action getData()
          {
            _searcher._myScenario.stop("User triggered");
            return null;
          }
        }, null, 'o'));

    _theTools.addElement(new MenuItemInfo("ASSET", null, "Exit",
        new PlainTool(_theParent, "Exit", "images/VCRStop.gif")
        {
          public Action getData()
          {
            System.exit(0);
            return null;
          }
        }, null, 'x'));
    // start with the chart
    //   final MWC.GUI.Chart.Painters.GridPainter grid = new MWC.GUI.Chart.Painters.GridPainter();
//    grid.setDelta(new MWC.GenericData.WorldDistance(15, WorldDistance.NM));
//    final MWC.GUI.Layer decs = _theData.findLayer("Decs");
//    decs.add(grid);

    // give the chart an area
//    getChart().getCanvas().getProjection().setDataArea(new WorldArea(new WorldLocation(2,2,0),
    //                                                                   new WorldLocation(0,0,0)));

    // add the chart plotter
    _theData.addThisLayer(_searcher._guiSupport);


    getChart().getCanvas().setBackgroundColor(new Color(108, 150, 184));

    // connect the scenario stepping to the chart repainting
//    _searcher.addScenarioSteppedListener(new ASSET.Scenario.ScenarioSteppedListener()
//    {public void step(long newTime)
//      {
//        getChart().update();
//      }
//    public void restart(){;}});

    // have a go at our "removed" listener
 //   final TargetType watch = new TargetType(Category.Force.BLUE);
  //  final TargetType target = new TargetType(Category.Force.RED);

  }


  public static void main(String[] args)
  {
//    try{
//    UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
//    }catch(javax.swing.UnsupportedLookAndFeelException e)
//    {
//      e.printStackTrace();
//    }


    // create the interface
    final JFrame parent = new JFrame("ASSET SuperSearch");

    // open the splash screen
    CoreGUISwing.showSplash(parent, "images/SuperSearchLogo.gif");

    // initialise the interface
    parent.setSize(900, 500);
    parent.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

    // and create the other parent
    final CoreGUISwing.ASSETParent pr = new CoreGUISwing.ASSETParent(parent);

    final SuperSearchGUI viewer = new SuperSearchGUI(pr);

    parent.getContentPane().setLayout(new java.awt.BorderLayout());
    parent.getContentPane().add("Center", viewer.getPanel());

    parent.setVisible(true);

  }

}
