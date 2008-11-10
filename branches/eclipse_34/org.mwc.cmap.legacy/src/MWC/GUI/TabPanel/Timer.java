package MWC.GUI.TabPanel;


import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.awt.event.ActionListener;
import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;

//	05/30/97	LAB	Updated to support Java 1.1
//	06/06/97	LAB	Removed deprecated (unusable in 1.1) functions:
//					public void setEventType(int type)
//					public int getEventType()
//					public void setTarget(Component t)
//					public Component getTarget()
//	06/24/97	LAB	Changed the behavior of the start and stop
//					methods to actually start and stop the thread
//					instead of suspending and resuming it.  Added
//					pause() and resume() for this purpose.
//  07/23/97    CAR implemented readObject
//	08/19/97	LAB	Fixed a bug where Timer would keep going even if repeat was set to false.
//					Fixed resume to only resume if currently paused.  Changed property
//					names used by firePropertyChange to follow the naming conventions set
//					forth in the Bean Spec. (lowwer case first letter, unless second letter
//					is capitalized too).  Made package level data protected.

/**
 *
 * Sets a timer to wait before an action event is posted to a component.
 * The caller can specify the target component, the event to send to the
 * component, and the time delay.
 *
 * The timer is implemented as a thread.  The one of the start(...) methods should
 * be called to start the thread.
 *
 *
 * @version 1.0, Nov 26, 1996
 *
 * @author	Symantec
 *
 */
public class Timer implements Runnable, java.io.Serializable
{
    /**
     * Creates a timer with the default delay.
     * After 1000 miliseconds this timer will fire an ActionEvent.
	 * It will not repeat.
     */
    public Timer()
    {
		this(1000, false);
    }

    /**
     * Creates a timer with specified delay.
     * After the specified delay this timer will fire an ActionEvent.
	 * It will not repeat.
     * @param d the delay in milliseconds
     */
    public Timer(int d)
    {
		this(d, false);
    }

    /**
     * Creates a timer with specified repeat setting and the default delay.
     * After 1000 miliseconds this timer will fire an ActionEvent.
	 * It may repeat, depending on r.
     * @param r if true, reset and repeat after generating the event
     */
    public Timer(boolean r)
    {
		this(1000, r);
    }

    /**
     * Creates a timer with specified delay and repeat setting.
     * After the specified delay this timer will fire an ActionEvent.
	 * It may repeat, depending on r.
     * @param d the delay in milliseconds
     * @param r if true, reset and repeat after generating the event
     */
    public Timer(int d, boolean r)
    {
        delay     = d;
        repeat    = r;
        execute   = false;
        thread    = new Thread(this);
    }

    /**
	 * @deprecated
	 * @see symantec.itools.util.Timer#Timer(int, boolean)
     */
	public Timer(Component t)
    {
        this(1000);
    }

    /**
	 * @deprecated
	 * @see symantec.itools.util.Timer#Timer(int, boolean)
     */
    public Timer(Component t, int d)
    {
        this(d, false);
    }

    /**
	 * @deprecated
	 * @see symantec.itools.util.Timer#Timer(int, boolean)
     */
    public Timer(Component t, int d, boolean r)
    {
        this(d, r);
    }

    /**
	 * @deprecated
	 * @see symantec.itools.util.Timer#Timer(int, boolean)
     */
    public Timer(Component t, int d, boolean r, int e)
    {
		this(d, r);
	}

    /**
     * Sets the delay time for this timer.
     * @param d the delay in milliseconds.  This delay will be used starting
     *          after the current delay elapses
     *
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #getDelay()
     */
    public void setDelay(int d) throws PropertyVetoException
    {
    	Integer newValue = new Integer(d);
    	Integer oldValue = new Integer(delay);

		vetos.fireVetoableChange("delay", oldValue, newValue);

        delay = d;

		changes.firePropertyChange("delay", oldValue, newValue);
    }

    /**
     * Obtains the delay time setting for this timer.
     * @return the current delay setting for this timer, in milliseconds
     * @see #setDelay(int)
     */
    public int getDelay()
    {
        return delay;
    }


    /**
     * Changes the repeat setting of the timer.
     * If the repeat setting is false a single event will be generated.  When
     * set to true the timer produces a series of events.
     *
     * @param f reset and repeat after generating the event
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #isRepeat
     */
    public void setRepeat(boolean f) throws PropertyVetoException
    {
    	Boolean newValue = new Boolean(f);
    	Boolean oldValue = new Boolean(repeat);

		vetos.fireVetoableChange("repeat", oldValue, newValue);

		repeat = f;

		changes.firePropertyChange("repeat", oldValue, newValue);
    }

    /**
     * Obtains the repeat setting of the timer.
     * @return true if this timer is set to repeat, false if this timer does not repeat
     * @see #setRepeat
     */
    public boolean isRepeat()
    {
        return repeat;
    }

    /**
	 * @deprecated
	 * @see #isRepeat()
     */
    public boolean getRepeat()
    {
        return isRepeat();
    }

    /**
     * Pauses the timer.
     * Differs from stop in that the timer
     * is continued from whatever state it was in before
     * pausing.
     * <p>
     * start() and stop() overrule this function.
     * @see #resume
     * @see #start
     * @see #stop
     */
    public void pause()
    {
    	execute = false;
    }

    /**
     * Resumes the timer.
     * Differs from start in that the timer
     * is continued from whatever state it was in before
     * pausing.
     * <p>
     * start() and stop() overrule this function
     * @see #pause
     * @see #start
     * @see #stop
     */
    public void resume()
    {
    	if(execute != true)
    	{
	    	execute = true;
		    thread.resume();
		}
	}

    /**
     * Starts the timer with existing settings.
     * @see #start(int)
     * @see #start(boolean)
     * @see #start(int, boolean)
     * @see #stop
     * @see #run
     */
    public void start()
    {
	    execute = true;
	    live	= true;
	    if(thread.isAlive())
	    {
	    	thread.resume();
	    }
	    else
		{
	    	thread	= new Thread(this);
		    thread.start();
		}
    }

    /**
     * Starts the timer using the specified delay.
     * @param d the delay in milliseconds
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #start()
     * @see #start(boolean)
     * @see #start(int, boolean)
     * @see #stop
     * @see #run
     */
    public void start(int d) throws PropertyVetoException
    {
	    setDelay(d);

	    start();
    }

    /**
     * Starts the timer using the specified repeat setting.
     * @param r reset and repeat after generating the event
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #start()
     * @see #start(int)
     * @see #start(int, boolean)
     * @see #stop
     * @see #run
     */
    public void start(boolean r) throws PropertyVetoException
    {
	    setRepeat(r);

	    start();
    }

    /**
     * Starts the timer using the specified delay and repeat settings.
     * @param d the delay in milliseconds
     * @param r reset and repeat after generating the event
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #start()
     * @see #start(int)
     * @see #start(boolean)
     * @see #stop
     * @see #run
     */
    public void start(int d, boolean r) throws PropertyVetoException
    {
	    setDelay(d);
	    setRepeat(r);

	    start();
    }

    /**
     * Stops the timer.  After return the timer will generate no more events.
     * @see #start
     */
    public void stop()
    {
	    execute		= false;
	    repeating	= false;
	    live		= false;
	    thread.resume();
    }

    /**
     * The thread body.  This method is called by the Java virtual machine in response to a
     * start call by the user.
     * @see #start()
     * @see #start(int)
     * @see #start(boolean)
     * @see #start(int, boolean)
     * @see #stop
     */
    public void run()
    {
        if(!execute) thread.suspend();
		try
		{
            while(live)
            {
                do
                {
					repeating = repeat;
                    thread.sleep(delay);
                    if (execute)
                    {
                    	sourceActionEvent();
                    }
                }
                while (repeating && live);

                if((!execute && live) || !repeating)
                	thread.suspend();
            }
	   }
        catch (InterruptedException e)
        {
        }
    }

    /**
     * Sets the command name of the action event fired by this button.
     * @param command Tthe name of the action event command fired by this button
     * @see #getActionCommand
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     */
    public void setActionCommand(String command) throws PropertyVetoException
    {
    	String oldValue = actionCommand;

		vetos.fireVetoableChange("actionCommand", oldValue, command);
        actionCommand = command;
		changes.firePropertyChange("actionCommand", oldValue, command);
    }

    /**
     * Returns the command name of the action event fired by this button.
     * @see #setActionCommand
     */
    public String getActionCommand()
    {
        return actionCommand;
    }

    /**
     * Adds the specified action listener to receive action events
     * from this button.
     * @param l the action listener
     */
	public void addActionListener(ActionListener l)
	{
		actionListener = AWTEventMulticaster.add(actionListener, l);
	}

    /**
     * Removes the specified action listener so it no longer receives
     * action events from this button.
     * @param l the action listener
     */
	public void removeActionListener(ActionListener l)
	{
		actionListener = AWTEventMulticaster.remove(actionListener, l);
	}

    /**
     * Fires an action event to the listeners.
     * @see #setActionCommand
     */
	public void sourceActionEvent()
	{
		if (actionListener != null)
			actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand));
	}

    /**
     * Adds a listener for all property change events.
     * @param listener the listener to add
     * @see #removePropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
    	changes.addPropertyChangeListener(listener);
    }

    /**
     * Removes a listener for all property change events.
     * @param listener the listener to remove
     * @see #addPropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
    	changes.removePropertyChangeListener(listener);
    }

    /**
     * Adds a listener for all vetoable property change events.
     * @param listener the listener to add
     * @see #removeVetoableChangeListener
     */
    public void addVetoableChangeListener(VetoableChangeListener listener)
    {
		vetos.addVetoableChangeListener(listener);
    }

    /**
     * Removes a listener for all vetoable property change events.
     * @param listener the listener to remove
     * @see #addVetoableChangeListener
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {
    	vetos.removeVetoableChangeListener(listener);
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();

        execute   = false;
        thread    = new Thread(this);
    }

    protected Component  target;
    protected int        eventType;
   	protected boolean    repeat;
    protected boolean    repeating;
    protected boolean    execute;
    protected boolean	   live;
    protected int        delay;
    protected String actionCommand;
	protected ActionListener actionListener = null;
    transient protected Thread thread;
	private VetoableChangeSupport vetos = new VetoableChangeSupport(this);
    private PropertyChangeSupport changes = new PropertyChangeSupport(this);
}
