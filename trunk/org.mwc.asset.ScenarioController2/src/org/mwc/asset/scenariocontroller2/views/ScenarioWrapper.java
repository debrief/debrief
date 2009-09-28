package org.mwc.asset.scenariocontroller2.views;

import java.util.Iterator;
import java.util.Vector;

import ASSET.GUI.Workbench.Plotters.ScenarioLayer;
import ASSET.Scenario.Observers.ScenarioObserver;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layers;

/** view of a complete simulation (scenario and controls)
 * 
 * @author Administrator
 *
 */
public class ScenarioWrapper extends Layers
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ScenarioControllerView _theCont;
	private ContWrapper _theController;
	private ScenarioLayer _scenLayer;

	public ScenarioWrapper(ScenarioControllerView scenarioController)
	{
		_theCont = scenarioController;
		_theController = new ContWrapper();
		_scenLayer = new ScenarioLayer();
		this.addThisLayer(_scenLayer);
		this.addThisLayer(_theController);
	}

	public void fireNewScenario()
	{
		_scenLayer.setScenario(_theCont.getScenario());

		this.fireExtended();
	}

	public void fireNewController()
	{
		_theController.setScenario(_theCont.getObservers());
		this.fireExtended();
	}

	/** layout-manager compliant wrapper around a scenario control file
	 * 
	 * @author Administrator
	 *
	 */
	private class ContWrapper extends BaseLayer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BaseLayer _layerObs;
		private BaseLayer _layerGenny;

		public ContWrapper()
		{
			this.setName("Generator Pending");
		}

		public void setScenario(Vector<ScenarioObserver> observers)
		{
			// clear out
			this.removeAllElements();

			this.setName("Generator");

			// and add our layers
			_layerGenny = new BaseLayer();
			_layerGenny.setName("Generator");
			this.add(_layerGenny);
			_layerObs = new BaseLayer();
			_layerObs.setName("Observers");
			this.add(_layerObs);

			// ok, now load the observers themselves
			Iterator<ScenarioObserver> iter = observers.iterator();
			while (iter.hasNext())
			{
				ScenarioObserver thisS = iter.next();
				_layerObs.add(thisS);
			}

		}

	}

}
