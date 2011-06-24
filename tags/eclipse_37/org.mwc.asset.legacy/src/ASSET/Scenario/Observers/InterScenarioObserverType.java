package ASSET.Scenario.Observers;

import java.io.File;

/**
 * Interface implemented by observers which keep track of data across a number of scenario runs
 *
 */
public interface InterScenarioObserverType extends ScenarioObserver
{

  /** initialise the observer, let it create it's output file
   *
   * @param outputDirectory where to place the output file
   */
  public void initialise(File outputDirectory);

  /** indicate that all scenario runs are now complete
   *
   */
  public void finish();

}
