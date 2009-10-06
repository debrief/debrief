package org.mwc.asset.SimulationController.views;

import java.util.HashSet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class SelectionProvider implements ISelectionProvider {

	private ISelection mySelection;

	private HashSet<ISelectionChangedListener> myListeners = new HashSet<ISelectionChangedListener>();

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		myListeners.add(listener);
	}

	public ISelection getSelection() {
		return mySelection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		myListeners.remove(listener);
	}

	public void setSelection(ISelection selection) {
		mySelection = selection;
		for (ISelectionChangedListener listener : myListeners.toArray(new ISelectionChangedListener[0])) {
			listener.selectionChanged(new SelectionChangedEvent(this, mySelection));
		}
	}
}
