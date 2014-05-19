package org.mwc.cmap.media.time;

public interface ITimeProvider {
	
	void addListener(ITimeListener listener);
	
	void removeListener(ITimeListener listener);
	
	void fireNewTime(Object src, long millis);
}
