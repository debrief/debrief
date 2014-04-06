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

public class MinMaxLimitObservable extends AbstractObservableValue
{
	private IObservableValue minObservable;
	private IObservableValue maxObservable;
	private IConverter converter;
	private PrivateInterface privateInterface;
	private Object cachedValue;
	private boolean updating;
	private boolean intervalMode;

	private class PrivateInterface implements IChangeListener, IStaleListener,
			IDisposeListener
	{
		public void handleDispose(DisposeEvent staleEvent)
		{
			dispose();
		}

		public void handleChange(ChangeEvent event)
		{
			if (!isDisposed() && !updating)
				notifyIfChanged();
		}

		public void handleStale(StaleEvent staleEvent)
		{
			if (!isDisposed())
				fireStale();
		}
	}

	public MinMaxLimitObservable(IObservableValue minObservable,
			IObservableValue maxObservable)
	{
		this(minObservable, maxObservable, null);
	}
	
	public MinMaxLimitObservable(IObservableValue minObservable, 
			IObservableValue maxObservable, IConverter converter) 
	{
		super(minObservable.getRealm());
		this.minObservable = minObservable;
		this.maxObservable = maxObservable;
		this.converter = converter;
		this.intervalMode = minObservable != null && maxObservable != null; 
				
		privateInterface = new PrivateInterface();
		minObservable.addDisposeListener(privateInterface);
		maxObservable.addDisposeListener(privateInterface);
	}

	public Object getValueType()
	{
		return String.class;
	}

	protected void firstListenerAdded()
	{
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

	protected void lastListenerRemoved()
	{
		if (minObservable != null && !minObservable.isDisposed())
		{
			minObservable.removeChangeListener(privateInterface);
			minObservable.removeStaleListener(privateInterface);
		}

		if (maxObservable != null && !maxObservable.isDisposed())
		{
			maxObservable.removeChangeListener(privateInterface);
			maxObservable.removeStaleListener(privateInterface);
		}

		cachedValue = null;
	}

	private void notifyIfChanged()
	{
		if (hasListeners())
		{
			Object oldValue = cachedValue;
			Object newValue = cachedValue = doGetValue();
			if (!ObjectUtils.safeEquals(oldValue, newValue))
			{
				fireValueChange(Diffs.createValueDiff(oldValue, newValue));
			}
		}
	}

	protected Object doGetValue()
	{
		String minString = "";
		String maxString = "";
		Object minValue = minObservable == null ? null : minObservable.getValue();
		Object maxValue = maxObservable == null ? null : maxObservable.getValue();
		
		if (minValue == null && maxValue == null) 
		{
			return null; 
		}
		if (minValue != null) 
		{
			if (converter != null)
			{
				minString = converter.convert(minValue).toString();
			}
			else 
			{
				minString = "" + ((Number) minValue).intValue();
			}
		}
		if (maxValue != null) 
		{
			if (converter != null)
			{
				maxString = converter.convert(maxValue).toString();
			}
			else 
			{
				maxString = "" + ((Number) maxValue).intValue();
			}
		}
		if (! intervalMode) 
		{
			return minString + maxString;
		}
		if (minString.isEmpty()) 
		{
			return "< " + maxString;
		}
		if (maxString.isEmpty()) 
		{
			return "> " + minString;
		}		
		return minString + (maxString.equals(minString) ? "" : " - " + maxString);
	}

	protected void doSetValue(Object value)
	{
		throw new IllegalStateException("Hard contraints are read-only!");
	}

	public boolean isStale()
	{
		ObservableTracker.getterCalled(this);
		boolean minStale = minObservable != null ? minObservable.isStale() : false;
		boolean maxStale = maxObservable != null ? maxObservable.isStale() : false;
		return minStale || maxStale;
	}

	public synchronized void dispose()
	{
		checkRealm();
		if (!isDisposed())
		{
			if (minObservable != null && !minObservable.isDisposed())
			{
				minObservable.removeDisposeListener(privateInterface);
				minObservable.removeChangeListener(privateInterface);
				minObservable.removeStaleListener(privateInterface);
			}
			if (maxObservable != null && !maxObservable.isDisposed())
			{
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
}
