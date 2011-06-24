/**
 * 
 */
package org.mwc.cmap.core.ui_support;

import org.eclipse.swt.widgets.Display;


/** queue of events, where the event only gets run if another doesn't get placed on 
 * top of it before a specified time period passes
 * @author ianmayo
 *
 */
public class EventStack
{

	public static class DelayedEventRunner extends Thread
	{
	
		private final int delay;
		private Runnable nextEvent = null;
		private Long eventReceivedTime = null;
	
		public DelayedEventRunner(int delay)
		{
			this.delay = delay;
			setDaemon(true);
		}
	
		synchronized public void setNextEvent(Runnable runnable)
		{
			// store the time we received this
			eventReceivedTime = System.currentTimeMillis();
	
			// remember this event (over-writing any previous events)
			nextEvent = runnable;
		}
	
		public void run()
		{
			while (true)
			{
				synchronized (this)
				{
					if (nextEvent != null)
					{
						boolean isDelayUp = false;
	
						final long currentTime = System.currentTimeMillis();
	
						// how long since we last received an event?
						long elapsedTime = currentTime - eventReceivedTime;
	
						// have we passed the required waiting time?
						isDelayUp = elapsedTime > delay;
	
						if (isDelayUp)
						{
							Display.getDefault().asyncExec(nextEvent);
							// and clear the queue
							nextEvent = null;
						}
					}
				}
				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e)
				{
					// we can not do anything about it, let's ignore
				}
			}
		}
	
	}

	/** a queue of events, where only the last one gets run - ideally placed
	 * for screen updates when changing lots of properties.  Normally changing the 
	 * property of hundreds of items will trigger hundreds of screen updates, this
	 * allows us to just fire the last one
	 * 
	 * @param delay how (millis) long we should wait for another before running the current operation
	 */
	public EventStack(int delay)
	{
		_delay = delay;
		if (delay <= 0)
		{
			throw new RuntimeException("Delay has to be positive");
		}
	}

	public void addEvent(Runnable event)
	{
		if (event == null)
		{
			throw new RuntimeException("Event cannot be null");
		}
		getEventRunner().setNextEvent(event);
	}

	// implementation details

	private final int _delay;

	private DelayedEventRunner eventRunner = null;

	private DelayedEventRunner getEventRunner()
	{
		if (eventRunner == null)
		{
			eventRunner = new DelayedEventRunner(_delay);
			eventRunner.start();
		}
		return eventRunner;
	}

}