package ASSET.GUI.CommandLine;

import ASSET.ScenarioType;

/** listener class for things that want to learn about new scenarios being selected
 * - particularly during a multi-scenario simulation
 * @author ianmayo
 *
 */
public interface NewScenarioListener
{
	public void newScenario(ScenarioType oldScenario, ScenarioType newScenario);

}
