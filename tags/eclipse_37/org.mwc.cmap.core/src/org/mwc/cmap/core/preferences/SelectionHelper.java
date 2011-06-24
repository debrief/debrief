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

	public void addSelectionChangedListener(ISelectionChangedListener listener)
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
			ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection)
	{
	}

	public void fireNewSelection(ISelection data)
	{
		SelectionChangedEvent sEvent = new SelectionChangedEvent(this, data);
		for (Iterator<ISelectionChangedListener> stepper = _selectionListeners
				.iterator(); stepper.hasNext();)
		{
			ISelectionChangedListener thisL = (ISelectionChangedListener) stepper
					.next();
			if (thisL != null)
			{
				thisL.selectionChanged(sEvent);
			}
		}
	}
}