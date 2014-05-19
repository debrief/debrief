/**
 * 
 */
package org.mwc.cmap.core.preferences;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

final public class SelectionHelper implements ISelectionProvider
{
	private Vector<ISelectionChangedListener> _selectionListeners;

	public void addSelectionChangedListener(final ISelectionChangedListener listener)
	{
		if (_selectionListeners == null)
			_selectionListeners = new Vector<ISelectionChangedListener>(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}

	public ISelection getSelection()
	{
		return null;
	}

	public void removeSelectionChangedListener(
			final ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	public void setSelection(final ISelection selection)
	{
	}

	public void fireNewSelection(final ISelection data)
	{
		final SelectionChangedEvent sEvent = new SelectionChangedEvent(this, data);
		for (final Iterator<ISelectionChangedListener> stepper = _selectionListeners
				.iterator(); stepper.hasNext();)
		{
			final ISelectionChangedListener thisL = (ISelectionChangedListener) stepper
					.next();
			if (thisL != null)
			{
				thisL.selectionChanged(sEvent);
			}
		}
	}
}