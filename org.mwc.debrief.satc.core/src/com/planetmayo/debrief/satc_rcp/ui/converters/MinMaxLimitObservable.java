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

package com.planetmayo.debrief.satc_rcp.ui.converters;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.DisposeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IDisposeListener;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.StaleEvent;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;

import com.planetmayo.debrief.satc.util.ObjectUtils;

public class MinMaxLimitObservable extends AbstractObservableValue {
	private class PrivateInterface implements IChangeListener, IStaleListener, IDisposeListener {
		@Override
		public void handleChange(final ChangeEvent event) {
			if (!isDisposed() && !updating)
				notifyIfChanged();
		}

		@Override
		public void handleDispose(final DisposeEvent staleEvent) {
			dispose();
		}

		@Override
		public void handleStale(final StaleEvent staleEvent) {
			if (!isDisposed())
				fireStale();
		}
	}

	private IObservableValue minObservable;
	private IObservableValue maxObservable;
	private final IConverter converter;
	private PrivateInterface privateInterface;
	private Object cachedValue;
	private boolean updating;
	private final boolean intervalMode;

	private final String suffix;

	public MinMaxLimitObservable(final IObservableValue minObservable, final IObservableValue maxObservable) {
		this(minObservable, maxObservable, null, null);
	}

	public MinMaxLimitObservable(final IObservableValue minObservable, final IObservableValue maxObservable,
			final IConverter converter) {
		this(minObservable, maxObservable, converter, null);
	}

	public MinMaxLimitObservable(final IObservableValue minObservable, final IObservableValue maxObservable,
			final IConverter converter, final String suffix) {
		super(minObservable.getRealm());
		this.minObservable = minObservable;
		this.maxObservable = maxObservable;
		this.converter = converter;
		this.intervalMode = minObservable != null && maxObservable != null;
		this.suffix = suffix == null ? "" : suffix;

		privateInterface = new PrivateInterface();
		if (minObservable != null)
			minObservable.addDisposeListener(privateInterface);
		if (maxObservable != null)
			maxObservable.addDisposeListener(privateInterface);
	}

	@Override
	public synchronized void dispose() {
		checkRealm();
		if (!isDisposed()) {
			if (minObservable != null && !minObservable.isDisposed()) {
				minObservable.removeDisposeListener(privateInterface);
				minObservable.removeChangeListener(privateInterface);
				minObservable.removeStaleListener(privateInterface);
			}
			if (maxObservable != null && !maxObservable.isDisposed()) {
				maxObservable.removeDisposeListener(privateInterface);
				maxObservable.removeChangeListener(privateInterface);
				maxObservable.removeStaleListener(privateInterface);
			}
			minObservable = null;
			maxObservable = null;
			privateInterface = null;
			cachedValue = null;
		}
		super.dispose();
	}

	@Override
	protected Object doGetValue() {
		String minString = "";
		String maxString = "";
		final Object minValue = minObservable == null ? null : minObservable.getValue();
		final Object maxValue = maxObservable == null ? null : maxObservable.getValue();

		if (minValue == null && maxValue == null) {
			return null;
		}
		if (minValue != null) {
			if (converter != null) {
				minString = converter.convert(minValue).toString();
			} else {
				minString = "" + ((Number) minValue).intValue();
			}
		}
		if (maxValue != null) {
			if (converter != null) {
				maxString = converter.convert(maxValue).toString();
			} else {
				maxString = "" + ((Number) maxValue).intValue();
			}
		}
		if (!intervalMode) {
			return minString + maxString;
		}
		if (minString.isEmpty()) {
			return "< " + maxString;
		}
		if (maxString.isEmpty()) {
			return "> " + minString;
		}
		return minString + (maxString.equals(minString) ? "" : " - " + maxString) + suffix;
	}

	@Override
	protected void doSetValue(final Object value) {
		throw new IllegalStateException("Hard contraints are read-only!");
	}

	@Override
	protected void firstListenerAdded() {
		cachedValue = doGetValue();

		if (minObservable != null) {
			minObservable.addChangeListener(privateInterface);
			minObservable.addStaleListener(privateInterface);
		}

		if (maxObservable != null) {
			maxObservable.addChangeListener(privateInterface);
			maxObservable.addStaleListener(privateInterface);
		}
	}

	@Override
	public Object getValueType() {
		return String.class;
	}

	@Override
	public boolean isStale() {
		ObservableTracker.getterCalled(this);
		final boolean minStale = minObservable != null ? minObservable.isStale() : false;
		final boolean maxStale = maxObservable != null ? maxObservable.isStale() : false;
		return minStale || maxStale;
	}

	@Override
	protected void lastListenerRemoved() {
		if (minObservable != null && !minObservable.isDisposed()) {
			minObservable.removeChangeListener(privateInterface);
			minObservable.removeStaleListener(privateInterface);
		}

		if (maxObservable != null && !maxObservable.isDisposed()) {
			maxObservable.removeChangeListener(privateInterface);
			maxObservable.removeStaleListener(privateInterface);
		}

		cachedValue = null;
	}

	private void notifyIfChanged() {
		if (hasListeners()) {
			final Object oldValue = cachedValue;
			final Object newValue = cachedValue = doGetValue();
			if (!ObjectUtils.safeEquals(oldValue, newValue)) {
				fireValueChange(Diffs.createValueDiff(oldValue, newValue));
			}
		}
	}
}
