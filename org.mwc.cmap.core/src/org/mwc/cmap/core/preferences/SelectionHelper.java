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