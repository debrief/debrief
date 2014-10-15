/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
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
