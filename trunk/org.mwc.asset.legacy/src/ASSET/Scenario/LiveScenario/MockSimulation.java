package ASSET.Scenario.LiveScenario;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import MWC.Algorithms.LiveData.Attribute;
import MWC.Algorithms.LiveData.IAttribute;

/**
 * mocked simulation object that is able to run for specified period of time
 * 
 * @author ianmayo
 * 
 */
public class MockSimulation extends Simulation
{
	private static final int STEP_INTERVAL = 100;

	/**
	 * the local set of attributes for this simulation
	 * 
	 */
	private Vector<IAttribute> _attrs;

	/**
	 * our own timer
	 * 
	 */
	Timer _myTimer;

	/**
	 * the utility object that handles the task stepping
	 * 
	 */
	private MockTimerTask _updateCycle;

	/**
	 * the time we started, so we can deduct it from current time
	 * 
	 */
	private long timeStart;

	/**
	 * how long to run for
	 * 
	 */
	private final long _timeLimit;

	/**
	 * constructor, provides all needed to run for a while.
	 * 
	 * @param name
	 *          title for this simulation
	 * @param runTime
	 *          how long to run for
	 * @param attrs
	 *          things that can be listened to
	 */
	public MockSimulation(String name, long runTime, Vector<IAttribute> attrs)
	{
		super(name);

		// randomize the time a little
		_timeLimit = runTime;// (long) (runTime * Math.random() * TIME_VARIANCE);

		// create the object that looks after the task stepping
		_updateCycle = new MockTimerTask(attrs, this);

		// remember the attributes
		_attrs = attrs;
	}

	@Override
	public Vector<IAttribute> getAttributes()
	{
		// create results container
		Vector<IAttribute> res = new Vector<IAttribute>();

		// add the parent attributes
		res.addAll(super.getAttributes());

		// and add our attributes
		res.addAll(_attrs);

		// done.
		return res;
	}

	@Override
	public long getTime()
	{
		return System.currentTimeMillis() - timeStart;
	}

	/**
	 * move the scenario forward
	 * 
	 */
	public void step()
	{
		// are we finished yet?
		if (getTime() > _timeLimit)
		{
			this.complete();
		}
		
		// fire the time update
		super._time.fireUpdate(this, getTime(), getTime());
	}

	/**
	 * convenience class
	 * 
	 * @param name
	 * @param runTime
	 * @return
	 */
	public static MockSimulation createShort(String name, long runTime)
	{
		Vector<IAttribute> attrs = new Vector<IAttribute>();
		IAttribute att1 = new Attribute("Height","m", true);
		attrs.add(att1);
		IAttribute att2 = new Attribute("Speed", "kts", false);
		attrs.add(att2);

		MockSimulation res = new MockSimulation(name, runTime, attrs);
		return res;
	}

	/**
	 * convenience class
	 * 
	 * @param name
	 * @param runTime
	 * @return
	 */
	public static MockSimulation createLong(String name, long runTime)
	{
		Vector<IAttribute> attrs = new Vector<IAttribute>();
		IAttribute att1 = new Attribute("Height","m",  true);
		attrs.add(att1);
		IAttribute att2 = new Attribute("Speed", "kts", false);
		attrs.add(att2);
		IAttribute att3 = new Attribute("Distance","yds",  true);
		attrs.add(att3);
		IAttribute att4 = new Attribute("Fuel", "%", false);
		attrs.add(att4);
		IAttribute att5 = new Attribute("Range","m",  true);
		attrs.add(att5);
		IAttribute att6 = new Attribute("Acceleration","m/s/s",  false);
		attrs.add(att6);
		IAttribute att7 = new Attribute("Water","ddegs",  false);
		attrs.add(att7);
		IAttribute att8 = new Attribute("Temperature","c",  false);
		attrs.add(att8);

		MockSimulation res = new MockSimulation(name, runTime, attrs);
		return res;
	}

	@Override
	public void start()
	{
		
		System.out.println("starting " + getName());
		
		// update the parent status
		super.start();

		// remember what time we started at
		timeStart = System.currentTimeMillis();

		// create our timer, if necessary
		_myTimer = new Timer();

		// and start the timer
		_myTimer.scheduleAtFixedRate(_updateCycle, 5, STEP_INTERVAL);

	}

	@Override
	public void stop()
	{
		// update the parent status
		super.stop();

		// stop the process
		_myTimer.cancel();

		// cancel any pending calls
		_updateCycle.cancel();
	}

	@Override
	protected void complete()
	{
		// update the parent status
		super.complete();

		// stop the process
		_myTimer.cancel();

		// cancel any pending calls
		_updateCycle.cancel();

	}

	public static class MockTimerTask extends TimerTask
	{
		private Vector<IAttribute> _attrs;
		private Simulation _parent;

		/**
		 * create a task that performs some random updates
		 * 
		 * @param subject
		 * @param attrs
		 *          the mock object's attributes (not the parents, since we won't be
		 *          randomly varying the state)
		 */
		public MockTimerTask(Vector<IAttribute> attrs, Simulation parent)
		{
			_attrs = attrs;
			_parent = parent;
		}

		@Override
		public void run()
		{

			// is our sim still running?
			if (_parent.getState().getCurrent(_parent).getValue() == Simulation.COMPLETE)
			{
				super.cancel();
				return;
			}

			// do some attribute variation
			for (Iterator<IAttribute> iterator = _attrs.iterator(); iterator
					.hasNext();)
			{
				Attribute type = (Attribute) iterator.next();

				// 20% chance of change
				if (Math.random() >= 0.8)
				{
					// yup, exceeded the threshold, make some noddy change
					type.fireUpdate(this, _parent.getTime(), new Integer(
							(int) (Math.random() * 100)));
				}
			}

			// move it forward
			_parent.step();
		}

	}

}
