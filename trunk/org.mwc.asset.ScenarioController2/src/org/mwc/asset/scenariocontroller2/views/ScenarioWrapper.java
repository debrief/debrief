package org.mwc.asset.scenariocontroller2.views;

import ASSET.ScenarioType;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layers;

public class ScenarioWrapper extends Layers
{

	private class ScenWrapper extends BaseLayer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ScenWrapper()
		{
			this.setName("Pending");
		}

		public void init()
		{
			// ok, fresh start
			this.removeAllElements();
			
			ScenarioType theScen = (ScenarioType) _theCont
					.getAdapter(ScenarioType.class);
			if (theScen.getName() != null)
			{
				this.setName(theScen.getName());

				BaseLayer theParts = new BaseLayer();
				theParts.setName("Participants");
				this.add(theParts);
				BaseLayer theEnv = new BaseLayer();
				theEnv.setName("Environment");
				this.add(theEnv);
			}
		}
	}

	private class ContWrapper extends BaseLayer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ContWrapper()
		{
			this.setName("Pending");
			BaseLayer theParts = new BaseLayer();
			theParts.setName("Generator");
			this.add(theParts);
			BaseLayer theEnv = new BaseLayer();
			theEnv.setName("Observers");
			this.add(theEnv);
		}
		
		protected void loadObservers()
		{
			// check we have a controller
			ScenarioController controller = (ScenarioController) _theCont.getAdapter(ScenarioController.class);
			if(controller != null)
			{
				// get the observers
		//		controller.			
			}
		}
		
		public void init()
		{
			
		}
	}

	private ScenarioController _theCont;
	private ScenWrapper _theScenario;
	private ContWrapper _theController;

	public ScenarioWrapper(ScenarioController scenarioController)
	{
		_theCont = scenarioController;		
		_theScenario = new ScenWrapper();
		_theController = new ContWrapper();
		this.addThisLayer(_theScenario);
		this.addThisLayer(_theController);
	}
	
	public void fireNewScenario()
	{
		_theScenario.init();
		this.fireExtended();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
