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
package info.limpet.stackedcharts.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;

class StackedPane extends Composite {
	private final StackLayout _stackLayout;
	private final Map<Integer, Control> _panes = new HashMap<Integer, Control>();
	private final List<Control> _pages = new ArrayList<Control>();
	private final List<SelectionListener> listeners = new ArrayList<>(1);
	private int _activePane = -1;

	public StackedPane(final Composite parent) {
		super(parent, SWT.NO_FOCUS);
		_stackLayout = new StackLayout();
		setLayout(_stackLayout);
		_stackLayout.marginHeight = 0;
		_stackLayout.marginWidth = 0;
	}

	public StackedPane(final Composite parent, final int style) {
		super(parent, style);
		_stackLayout = new StackLayout();
		setLayout(_stackLayout);
	}

	public void add(final int key, final Control control) {
		if (_stackLayout.topControl == null) {
			_stackLayout.topControl = control;
		}
		_panes.put(key, control);
		_pages.add(control);
	}

	public void addSelectionListener(final SelectionListener listener) {
		listeners.add(listener);
	}

	void completeSelection() {
		final Control control = _panes.get(_activePane);

		if (control != null) {

			_stackLayout.topControl = control;
		}

		layout(true);

		if (control instanceof Composite) {
			((Composite) control).layout(true);
		}
	}

	void fireSelection(final Control c) {
		for (final SelectionListener listener : new ArrayList<>(listeners)) {
			final Event e = new Event();
			e.item = c;
			e.widget = c;
			listener.widgetSelected(new SelectionEvent(e));
		}
	}

	public Control getActiveControl() {
		return _stackLayout.topControl;
	}

	public int getActiveControlKey() {

		return _activePane;
	}

	public Control getControl(final int key) {
		return _panes.get(key);
	}

	@Override
	public StackLayout getLayout() {
		return (StackLayout) super.getLayout();
	}

	public void remove(final int key) {
		final Control control = _panes.get(key);
		if (control != null) {
			final int indexOf = _pages.indexOf(control) - 1;
			_panes.remove(key);
			_pages.remove(control);

			control.dispose();
			if (indexOf > 0 && indexOf < _pages.size()) {
				_stackLayout.topControl = _pages.get(indexOf);
				layout(true);
			} else if (_pages.size() > 0) {
				_stackLayout.topControl = _pages.get(0);
				layout(true);
			}
		}
	}

	public void removeSelectionListener(final SelectionListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setLayout(final Layout layout) {
		if (!(layout instanceof StackLayout)) {
			throw new IllegalArgumentException("Only support StackLayout");
		}
		super.setLayout(layout);
	}

	public void showPane(final int pane) {
		showPane(pane, true);

	}

	public void showPane(final int pane, final boolean fireEvent) {

		if (getActiveControlKey() == pane) {
			return;
		}
		_activePane = pane;
		final Control control = _panes.get(pane);
		control.setSize(getSize());

		if (fireEvent)
			fireSelection(control);
		// fix for work around on mac
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0
				|| System.getProperty("os.name").toLowerCase().indexOf("nux") >= 0) {
			completeSelection();
		}

	}
}
