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

//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996, 1996 Borland International, Inc. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package MWC.GUI.SplitPanel;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

//import borland.jbcl.util.*;
//import borland.jbcl.model.*;

/**
 * Convenient Panel to use as a superclass for JavaBean views and controls -
 * subdispatches focus, key and mouse events - manages action listeners -
 * manages tab/focus awareness
 */
public class BeanPanel extends Panel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected transient ActionMulticaster actionAdapter;

	protected boolean focusAware = true;

	protected int focusState = ItemPainter.INACTIVE;

	public BeanPanel() {
		enableEvents(AWTEvent.FOCUS_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK
				| AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

	public BeanPanel(final LayoutManager layout) {
		super.setLayout(layout);
		enableEvents(AWTEvent.FOCUS_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK
				| AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

	/**
	 * protected implementation of action event registration methods that a subclass
	 * can expose as public methods if it sources action events
	 */
	public synchronized void addActionListener(final ActionListener l) {
		if (actionAdapter == null)
			actionAdapter = new ActionMulticaster();
		actionAdapter.add(l);
	}

	// General events

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	// Action events

	/**
	 *
	 */
	@Override
	public boolean isFocusable() {
//System.err.println("BeanPanel.isFocusTraversable this=" + this + ",focusAware=" + focusAware);
		return focusAware;
	}

	// Key events

	/**
	 * protected support for a focusAware property that a subclass can expose as
	 * public if it has the potential for accepting focus
	 */
	protected boolean isFocusAware() {
		return focusAware;
	}

	protected void processActionEvent(final ActionEvent e) {
		if (actionAdapter != null)
			actionAdapter.dispatch(e);
	}

	@Override
	protected void processEvent(final AWTEvent e) {
		if (e instanceof ActionEvent)
			processActionEvent((ActionEvent) e);
		else
			super.processEvent(e);
	}

	@Override
	protected void processFocusEvent(final FocusEvent e) {
		// System.err.println("BeanPanel.processFocusEvent e=" + e + " focusState=" +
		// focusState + " focusAware=" + focusAware);
		if (focusAware) {
			switch (e.getID()) {
			case FocusEvent.FOCUS_GAINED:
				focusState = ItemPainter.FOCUSED;
				/* showFocusOwner(); */ break;
			case FocusEvent.FOCUS_LOST:
				focusState = ItemPainter.INACTIVE;
				/* showFocusOwner(); */ break;
			}
			// repaint();
		}
		super.processFocusEvent(e);
	}

	// Mouse events

	@Override
	protected void processKeyEvent(final KeyEvent e) {
		// System.err.println("processKeyEvent(" + e + ")");
//    if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F6 && e.isAltDown()) {
//      System.err.println("Consuming alt-f6!");
//      e.consume();
//    }
		switch (e.getID()) {
		case KeyEvent.KEY_PRESSED:
			processKeyPressed(e);
			break;
		case KeyEvent.KEY_TYPED:
			processKeyTyped(e);
			break;
		case KeyEvent.KEY_RELEASED:
			processKeyReleased(e);
			break;
		}
		super.processKeyEvent(e);
	}

	protected void processKeyPressed(final KeyEvent e) {
	}

	/*
	 * void showFocusOwner() { for (Container p = getParent(); p != null; p =
	 * p.getParent()) if (p instanceof Window) { Component fo =
	 * ((Window)p).getFocusOwner(); System.err.println("focusOwner=" + fo); return;
	 * } }
	 */

	protected void processKeyReleased(final KeyEvent e) {
	}

	protected void processKeyTyped(final KeyEvent e) {
	}

	protected void processMouseClicked(final MouseEvent e) {
	}

	protected void processMouseDragged(final MouseEvent e) {
	}

	protected void processMouseEntered(final MouseEvent e) {
	}

	@Override
	protected void processMouseEvent(final MouseEvent e) {
		// System.err.println("processMouseEvent(" + e + ")");
		switch (e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			processMousePressed(e);
			break;
		case MouseEvent.MOUSE_RELEASED:
			processMouseReleased(e);
			break;
		case MouseEvent.MOUSE_CLICKED:
			processMouseClicked(e);
			break;
		case MouseEvent.MOUSE_ENTERED:
			processMouseEntered(e);
			break;
		case MouseEvent.MOUSE_EXITED:
			processMouseExited(e);
			break;
		}
		super.processMouseEvent(e);
	}

	protected void processMouseExited(final MouseEvent e) {
	}

	// Focus events

	@Override
	protected void processMouseMotionEvent(final MouseEvent e) {
		switch (e.getID()) {
		case MouseEvent.MOUSE_MOVED:
			processMouseMoved(e);
			break;
		case MouseEvent.MOUSE_DRAGGED:
			processMouseDragged(e);
			break;
		}
		super.processMouseMotionEvent(e);
	}

	protected void processMouseMoved(final MouseEvent e) {
	}

	protected void processMousePressed(final MouseEvent e) {
		if (e.getClickCount() == 1 && isFocusable()) {
			// System.err.println("processMousePress(" + e + ") requesting focus");
			requestFocus();
			// showFocusOwner();
		}
	}

	protected void processMouseReleased(final MouseEvent e) {
	}

	public synchronized void removeActionListener(final ActionListener l) {
		if (actionAdapter != null)
			actionAdapter.remove(l);
	}

	protected void setFocusAware(final boolean aware) {
		focusAware = aware;
	}
}
