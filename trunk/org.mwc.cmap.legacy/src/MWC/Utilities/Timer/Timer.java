/** Timer.java
* 
* Copyright (C) 1998  NetBeans, Inc.
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
*/

package MWC.Utilities.Timer;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Enumeration;
import java.util.Vector;

/** The Timer JavaBean is a nonvisual component that sends an ActionEvent
* to the registered TimerListeners every "delay" property milliseconds.
* It can either send that event only once, or it can cycle (according to
* the "onceOnly" property).
*
* @version  1.01, Sep 02, 1998
*/
public class Timer extends Object
implements java.io.Serializable {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PROP_ONCE_ONLY = "onceOnly";
  public static final String PROP_DELAY = "delay";

  public static final long DEFAULT_DELAY = 1000;
  public static final boolean DEFAULT_ONLY_ONCE = false;

  /** Creates a new Timer */
  public Timer () {
    delay = DEFAULT_DELAY;
    onceOnly = DEFAULT_ONLY_ONCE;
    propertySupport = new PropertyChangeSupport (this);
    // [IM] don't fire the timer at generation - always start it as a conscious decision
    //  start ();
  }

  public synchronized void start () {
    if (running) return;
    timerThread = new TimerThread ();
    running = true;
    timerThread.start ();
  }

  public synchronized void stop () {
    if (!running) return;
    timerThread.interrupt();
    timerThread = null;
    running = false;
  }

	/** Getter method for if the timer is running or not
	 * @return boolean for Is Running
	 */
	public boolean isRunning(){
		return running;
	}
	
  /** Getter method for the delay property.
  * @return Current delay value
  */
  public long getDelay () {
    return delay;
  }

  /** Setter method for the delay property.
  * @param value New delay value
  */
  public void setDelay (long value) {
    if (delay == value) return;
    long oldValue = delay;
    delay = value;
    propertySupport.firePropertyChange (PROP_DELAY,
                                        new Long (oldValue),
                                        new Long (delay));
  }

  /** Getter method for the onceOnly property.
  * @return Current onceOnly value
  */
  public boolean getOnceOnly () {
    return onceOnly;
  }

  /** Setter method for the onceOnly property.
  * @param value New onceOnly value
  */
  public void setOnceOnly (boolean value) {
    if (onceOnly == value) return;
    onceOnly = value;
    propertySupport.firePropertyChange (PROP_ONCE_ONLY,
                                        new Boolean (!onceOnly),
                                        new Boolean (onceOnly));
  }

  public void addPropertyChangeListener (PropertyChangeListener l) {
    propertySupport.addPropertyChangeListener (l);
  }

  public void removePropertyChangeListener (PropertyChangeListener l) {
    propertySupport.removePropertyChangeListener (l);
  }

  public void addTimerListener (TimerListener l) {
    if (listeners == null)
      listeners = new Vector<TimerListener>();

    listeners.addElement (l);
  }

  public void removeTimerListener (TimerListener l) {
    if (listeners == null)
      return;
    listeners.removeElement (l);
  }

  @SuppressWarnings("unchecked") void fireTimerEvent () {
    if (listeners == null) return;
    Vector<TimerListener> l;
    synchronized (this) {
      l = (Vector<TimerListener>) listeners.clone ();
    }

    for (Enumeration e = l.elements (); e.hasMoreElements ();) {
      TimerListener tl = (TimerListener) e.nextElement ();
      tl.onTime (new ActionEvent (this, ActionEvent.ACTION_PERFORMED, "onTime"));
    }

  }

  class TimerThread extends Thread {
  	
		public void run() {
      while (running) {
        try 
        {
          sleep(delay);
        } 
        catch (InterruptedException e) 
        {
        	
        }
        catch (Exception e) 
        {
        	System.err.println("Exception in time-stepper");
        	e.printStackTrace();
        }
        fireTimerEvent();
        if (onceOnly) break;
      }
    }
  }

  transient private TimerThread timerThread;

  /** The timer listeners */
  transient private Vector<TimerListener> listeners;

  /** The support for firing property changes */
  private PropertyChangeSupport propertySupport;

  /** The flag indicating whether the timer is running */
  boolean running;

  /** If true, the timer stops after firing the first onTime, if false
  * it keeps ticking until stopped */
  boolean onceOnly;

  /** Delay in milliseconds */
  long delay;
}

