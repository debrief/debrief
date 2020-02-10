/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

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
public class MockSimulation extends Simulation {
	public static class MockTimerTask extends TimerTask {
		private final Vector<IAttribute> _attrs;
		private final Simulation _parent;

		/**
		 * create a task that performs some random updates
		 *
		 * @param subject
		 * @param attrs   the mock object's attributes (not the parents, since we won't
		 *                be randomly varying the state)
		 */
		public MockTimerTask(final Vector<IAttribute> attrs, final Simulation parent) {
			_attrs = attrs;
			_parent = parent;
		}

		@Override
		public void run() {

			// is our sim still running?
			if (!_parent.isRunning()) {
				super.cancel();
				return;
			}

			// do some attribute variation
			for (final Iterator<IAttribute> iterator = _attrs.iterator(); iterator.hasNext();) {
				final Attribute type = (Attribute) iterator.next();

				// 20% chance of change
				if (Math.random() >= 0.8) {
					// yup, exceeded the threshold, make some noddy change
					type.fireUpdate(_parent, _parent.getTime(), new Integer((int) (Math.random() * 100)));
				}
			}

			// move it forward
			_parent.step();
		}

	}

	private static final int STEP_INTERVAL = 100;

	/**
	 * convenience class
	 *
	 * @param name
	 * @param runTime
	 * @return
	 */
	public static MockSimulation createLong(final String name, final long runTime) {
		final Vector<IAttribute> attrs = new Vector<IAttribute>();
		final IAttribute att1 = new Attribute("Height", "m", true);
		attrs.add(att1);
		final IAttribute att2 = new Attribute("Speed", "kts", false);
		attrs.add(att2);
		final IAttribute att3 = new Attribute("Distance", "yds", true);
		attrs.add(att3);
		final IAttribute att4 = new Attribute("Fuel", "%", false);
		attrs.add(att4);
		final IAttribute att5 = new Attribute("Range", "m", true);
		attrs.add(att5);
		final IAttribute att6 = new Attribute("Acceleration", "m/s/s", false);
		attrs.add(att6);
		final IAttribute att7 = new Attribute("Water", "ddegs", false);
		attrs.add(att7);
		final IAttribute att8 = new Attribute("Temperature", "c", false);
		attrs.add(att8);

		final MockSimulation res = new MockSimulation(name, runTime, attrs);
		return res;
	}

	/**
	 * convenience class
	 *
	 * @param name
	 * @param runTime
	 * @return
	 */
	public static MockSimulation createShort(final String name, final long runTime) {
		final Vector<IAttribute> attrs = new Vector<IAttribute>();
		final IAttribute att1 = new Attribute("Height", "m", true);
		attrs.add(att1);
		final IAttribute att2 = new Attribute("Speed", "kts", false);
		attrs.add(att2);

		final MockSimulation res = new MockSimulation(name, runTime, attrs);
		return res;
	}

	/**
	 * our own timer
	 *
	 */
	Timer _myTimer;

	/**
	 * the utility object that handles the task stepping
	 *
	 */
	private final MockTimerTask _updateCycle;

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
	 * @param name    title for this simulation
	 * @param runTime how long to run for
	 * @param attrs   things that can be listened to
	 */
	public MockSimulation(final String name, final long runTime, final Vector<IAttribute> attrs) {
		super(name);

		// randomize the time a little
		_timeLimit = runTime;// (long) (runTime * Math.random() * TIME_VARIANCE);

		// create the object that looks after the task stepping
		_updateCycle = new MockTimerTask(attrs, this);
	}

	@Override
	protected void complete() {
		// update the parent status
		super.complete();

		// stop the process
		_myTimer.cancel();

		// cancel any pending calls
		_updateCycle.cancel();

	}

	@Override
	public long getTime() {
		return System.currentTimeMillis() - timeStart;
	}

	@Override
	public void pause() {
		stop();
	}

	@Override
	public void start() {

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

	/**
	 * move the scenario forward
	 *
	 */
	@Override
	public void step() {
		// are we finished yet?
		if (getTime() > _timeLimit) {
			this.complete();
		}

		// fire the time update
		super._time.fireUpdate(this, getTime(), getTime());
	}

	@Override
	public void stop() {
		// update the parent status
		super.stop();

		// stop the process
		_myTimer.cancel();

		// cancel any pending calls
		_updateCycle.cancel();
	}

}
