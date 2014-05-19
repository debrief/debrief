package ASSET.GUI.Tools;

import ASSET.GUI.Core.CoreGUISwing;
import ASSET.GUI.MonteCarlo.Loader;
import ASSET.Scenario.CoreScenario;
import MWC.GUI.PlainChart;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;

import javax.swing.*;

/**
 * Tool capable of displaying our loader panel.
 */
public class OpenMonteCarloPanel extends PlainTool
{
  /** the loader panel we manage
   *
   */
  Loader _myLoader;

  /** the scenario we load the data into
   *
   */
  CoreScenario _myScenario;

  /** the properties we display the loader in
   *
   */
  SwingPropertiesPanel _myProperties;

  /** the floating component which the loader gets inserted into by the Properties Panel
   *
   */
  JPanel _loaderContainer;

  /** the chart our Monte Carlo loader will ask to update on completion
   *
   */
  private PlainChart _theChart;

  /** constructor - store all of the bits
   *
   * @param parent
   * @param properties
   * @param scenario
   */
  public OpenMonteCarloPanel(CoreGUISwing.ASSETParent parent,
                             SwingPropertiesPanel properties,
                             PlainChart theChart,
                             CoreScenario scenario)
  {
    super(parent, "Open Monte Carlo loader", "images/MonteCarlo.gif");

    _myScenario = scenario;
    _myProperties = properties;
    _theChart = theChart;
  }


  /**
   * the job of the execute function is to collate
   * the data necessary for the command to take place, then
   * call the function specific command in the 'Action'
   * object
   */
  public void execute()
  {
    super.execute();    //To change body of overridden methods use File | Settings | File Templates.

    // ok, do we still need to create the loader
    if(_myLoader == null)
    {
      _myLoader = new Loader(_myScenario, _theChart, super.getParent(), _myProperties)
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
          _myProperties.remove(_loaderContainer);
        }
      };

      // and stick it into the properties window
      _loaderContainer = _myProperties.addThisPanel(_myLoader);

    }
    else
    {
      // hmm, already been created.  Better just show it
      _myProperties.show(_loaderContainer);
    }
  }

  /**
   * abstract definition - this is where the class retrieves the data
   * necessary for the operation
   */
  public Action getData()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
