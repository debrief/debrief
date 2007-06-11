package ASSET.Scenario.Observers;

import java.io.File;

/**
 * Interface implemented by observers which keep track of data across a number of scenario runs
 * Log:
 *  $Log: IntraScenarioObserverType.java,v $
 *  Revision 1.1  2006/08/08 14:22:12  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:26:20  Ian.Mayo
 *  First versions
 *
 *  Revision 1.2  2004/05/24 16:07:14  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:56  ian
 *  no message
 *
 *  Revision 1.1  2003/11/06 13:25:57  Ian.Mayo
 *  Initial version looks fine
 *
 *
 */
public interface IntraScenarioObserverType extends ScenarioObserver
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
