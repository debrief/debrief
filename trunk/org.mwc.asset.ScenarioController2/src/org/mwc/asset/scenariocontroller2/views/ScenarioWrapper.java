package org.mwc.asset.scenariocontroller2.views;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.GUI.Workbench.Plotters.ScenarioLayer;
import ASSET.Scenario.Observers.ScenarioObserver;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * view of a complete simulation (scenario and controllers)
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
	private ControllerWrapper _theController;
	private ScenarioLayer _scenLayer;

	public ScenarioWrapper(ScenarioControllerView scenarioController)
	{
		this(scenarioController, new ScenarioLayer());
	}

	/**
	 * convenience method for when we have our own scenario layer
	 * 
	 * @param scenarioController
	 * @param layer
	 */
	public ScenarioWrapper(ScenarioControllerView scenarioController,
			ScenarioLayer layer)
	{
		_theCont = scenarioController;
		_theController = new ControllerWrapper();
		_scenLayer = layer;
		this.addThisLayer(_scenLayer);
		this.addThisLayer(_theController);
	}

	/**
	 * convenience method to ditch any layers other than the scenario and
	 * controller
	 * 
	 */
	public void ditchChartFeatures()
	{

		Enumeration<Editable> layers = this.elements();
		while (layers.hasMoreElements())
		{
			Layer thisLayer = (Layer) layers.nextElement();
			if ((thisLayer == _scenLayer) || (thisLayer == _theController))
			{
				// ok, leave it
			}
			else
			{
				// aaah, one last check - just check it's not an instance of the
				// scenario wrapper
				if (thisLayer instanceof ScenarioLayer)
				{

				}
				else
					this.removeThisLayer(thisLayer);
			}
		}
	}

	public ScenarioType getScenario()
	{
		return _scenLayer.getScenario();
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

	/**
	 * layout-manager compliant wrapper around a scenario control file
	 * 
	 * @author Administrator
	 * 
	 */
	private class ControllerWrapper extends BaseLayer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BaseLayer _layerObs;
		private BaseLayer _layerGenny;

		public ControllerWrapper()
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
