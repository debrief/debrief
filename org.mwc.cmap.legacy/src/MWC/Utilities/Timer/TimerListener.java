

package MWC.Utilities.Timer;

/** The TimerListener interface must be implemented by
* a class that wants to be notified about time events.
*
* @version  1.00, Jul 20, 1998
*/
public interface TimerListener extends java.util.EventListener {

  /** Called when a new timer event occurs */
  public void onTime (java.awt.event.ActionEvent event);

}

