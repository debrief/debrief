package org.mwc.debrief.dis.listeners.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PerformanceQueue {

	final Queue<Long> myQue;
	final String myName;

	private final long _period;

	public PerformanceQueue(final long period, final String name) {
		myQue = new ConcurrentLinkedQueue<Long>();
		myName = name;
		_period = period;
	}

	public void add(final long timeNow) {
		myQue.add(timeNow);

		flush(timeNow);
	}

	public void clear() {
		myQue.clear();
	}

	private void flush(final long timeNow) {
		final long _startTime = timeNow - _period;

		// remove any that are too old
		while (!myQue.isEmpty() && myQue.peek() <= _startTime) {
			myQue.remove();
		}
	}

	public double freqAt(final long time) {
		final double res;

		if (myQue.isEmpty())
			return 0;

		// what's the elapsed period:
		final long period = time - myQue.peek();

		if (period == 0) {
			res = 0;
		} else {
			res = myQue.size() / (double) period;
		}

		// throw in a flush
		flush(time);

		return res;
	}

	public void main(final String args[]) {

	}

	public Object size() {
		return myQue.size();
	}
}
