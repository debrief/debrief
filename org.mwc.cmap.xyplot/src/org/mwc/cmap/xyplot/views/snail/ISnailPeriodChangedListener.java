package org.mwc.cmap.xyplot.views.snail;

public interface ISnailPeriodChangedListener 
{
	/**
	 * Notifies that the time period for snail mode has changed.
	 *
	 * @param period period in seconds
	 */
	 public void snailPeriodChanged(final long period);
	
}
