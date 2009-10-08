package ASSET.Scenario.Observers;

import java.util.HashMap;

import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioSteppedListener;

public class BatchListenerHelper
{

	/**
	 * help for monitoring steps
	 * 
	 */
	private HashMap<ScenarioType, ScenarioSteppedListener> _stepListeners;

	public void registerStep(ScenarioType scenario,
			ScenarioSteppedListener listener)
	{

	}
}
