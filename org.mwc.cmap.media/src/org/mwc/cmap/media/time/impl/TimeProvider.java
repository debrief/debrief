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
	public synchronized void addListener(final ITimeListener listener) {
		listeners.put(listener, listener);
	}

	@Override
	public void fireNewTime(final Object src, final long millis) {
		final Map<ITimeListener, ITimeListener> fireListeners = new HashMap<ITimeListener, ITimeListener>(listeners);
		synchronized (this) {
			fireListeners.putAll(listeners);
		}
		for (final ITimeListener listener : fireListeners.keySet()) {
			listener.newTime(src, millis);
		}
	}

	@Override
	public synchronized void removeListener(final ITimeListener listener) {
		listeners.remove(listener);
	}
}
