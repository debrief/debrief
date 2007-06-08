/*
 * Created by Ian Mayo, PlanetMayo Ltd.
 * User: Ian.Mayo
 * Date: 30-Oct-2002
 * Time: 09:47:22
 */
package ASSET.GUI.Workbench;

import ASSET.GUI.Core.CoreGUISwing;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

public class LoadedWorkbench
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////
  //  static private final String MY_SCENARIO = "D:\\Dev\\Asset\\src\\test_data\\force_prot_scenario.xml";
  static private final String MY_SCENARIO = "c:\\temp\\andy_tactic\\ssn_run1.xml";

  static private final String MY_OBSERVERS = "c:\\temp\\andy_tactic\\ssn_observers.xml";


  ////////////////////////////////////////////////////
  // member constructor
  ////////////////////////////////////////////////////
  
  ////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////


  public static void main(String[] args)
  {
    //    try{
    //    UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
    //    }catch(UnsupportedLookAndFeelException e)
    //    {
    //      e.printStackTrace();
    //    }

    // create the interface
    final JFrame parent = new JFrame("Loaded session");

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

    // finally load the data

    // put the datafile into a vector
    Vector theScenarios = new Vector();
    theScenarios.add(new File(MY_SCENARIO));


    Vector theControls = new Vector();
    theControls.add(new File(MY_OBSERVERS));

    // load the data
    try
    {
      viewer.scenarioDropped(theScenarios);
      viewer.observerDropped(theControls);

    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    // trigger a fit-to-win
    viewer.FitToWin();

  }


}
