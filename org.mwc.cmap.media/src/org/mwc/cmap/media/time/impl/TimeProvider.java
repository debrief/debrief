package org.mwc.cmap.media.time.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.mwc.cmap.media.time.ITimeListener;
import org.mwc.cmap.media.time.ITimeProvider;

public class TimeProvider implements ITimeProvider {
	
	Map<ITimeListener, ITimeListener> listeners = new WeakHashMap<ITimeListener, ITimeListener>();

	public TimeProvider() {

	}

	@Override
	public synchronized void addListener(ITimeListener listener) {
		listeners.put(listener, listener);		
	}

	@Override
	public synchronized void removeListener(ITimeListener listener) {
		listeners.remove(listener);		
	}

	@Override
	public void fireNewTime(Object src, long millis) {
		Map<ITimeListener, ITimeListener> fireListeners = new HashMap<ITimeListener, ITimeListener>(listeners);
		synchronized (this) { 
			fireListeners.putAll(listeners);
		}
		for (ITimeListener listener : fireListeners.keySet()) {
			listener.newTime(src, millis);
		}
	}
}
