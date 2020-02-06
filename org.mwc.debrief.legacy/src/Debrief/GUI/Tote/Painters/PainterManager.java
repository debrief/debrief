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

package Debrief.GUI.Tote.Painters;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorSupport;
import java.util.Enumeration;
import java.util.Vector;

import Debrief.GUI.Tote.StepControl;
import MWC.GUI.Editable;
import MWC.GUI.StepperListener;
import MWC.GenericData.HiResDate;

public final class PainterManager implements StepperListener, Editable {
	////////////////////////////////////////////////////////////
	// nested class describing how to edit this class
	////////////////////////////////////////////////////////////
	public static final class PainterManagerInfo extends Editable.EditorType {

		public PainterManagerInfo(final PainterManager data) {
			super(data, "Tote Painter", "");
		}

		@Override
		public final PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = {
						longProp("Display", "the current display mode", TagListEditor.class), };
				return res;
			} catch (final Exception e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}

		}

	}

	public static final class TagListEditor extends PropertyEditorSupport {
		// the working copy we are editing
		String current;

		@Override
		public final String getAsText() {
			return current;
		}

		/**
		 * return a tag list of the current editors
		 */
		@Override
		public final String[] getTags() {
			return getListeners();
		}

		@Override
		public final Object getValue() {
			return current;
		}

		@Override
		public final void setAsText(final String p1) {
			current = p1;
		}

		@Override
		public final void setValue(final Object p1) {
			if (p1 instanceof String) {
				final String val = (String) p1;
				setAsText(val);
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val) {
			super(val);
		}

		public final void testMyParams() {
			final StepControl stepper = new Debrief.GUI.Tote.Swing.SwingStepControl(null, null, null, null, null, null);
			Editable ed = new PainterManager(stepper);
			Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

	///////////////////////////////////
	// member variables
	//////////////////////////////////
	private static Vector<StepperListener> _thePainters;

	public static String[] getListeners() {
		String[] strings = null;
		final Vector<String> res = new Vector<String>(0, 1);
		final Enumeration<StepperListener> iter = _thePainters.elements();
		while (iter.hasMoreElements()) {
			final StepperListener l = iter.nextElement();
			res.addElement(l.toString());
		}

		// are there any results?
		if (res.size() > 0) {
			strings = new String[res.size()];
			res.copyInto(strings);
		}

		return strings;
	}

	///////////////////////////////////
	// member functions
	//////////////////////////////////

	private StepperListener _current;

	private java.beans.PropertyChangeSupport _pSupport;

	transient private MWC.GUI.Editable.EditorType _myEditor = null;

	///////////////////////////////////
	// constructor
	//////////////////////////////////
	public PainterManager(final StepControl stepper) {
		_thePainters = new Vector<StepperListener>(0, 1);

		_pSupport = new PropertyChangeSupport(this);

		// register with the stepper
		stepper.addStepperListener(this);
	}

	public final void addPainter(final StepperListener listener) {
		_thePainters.addElement(listener);
		firePropertyChange();
	}

	public final void addPropertyChangeListener(final java.beans.PropertyChangeListener listener) {
		_pSupport.addPropertyChangeListener(listener);
	}

	/**
	 * provide method to clear out local data
	 *
	 */
	public final void closeMe() {
		_current = null;
		_pSupport = null;
		_thePainters.removeAllElements();
		_myEditor = null;
	}

	private void firePropertyChange() {
		_pSupport.firePropertyChange("Painter Change", null, null);
	}

	public final StepperListener getCurrentPainterObject() {
		return _current;
	}

	public final String getDisplay() {
		final String res;
		if (_current != null) {
			res = _current.toString();
		} else
			res = null;

		return res;
	}

	@Override
	public final MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new PainterManagerInfo(this);

		return _myEditor;
	}

	@Override
	public final String getName() {
		return "Painter Manager";
	}

	@Override
	public final boolean hasEditor() {
		return true;
	}

	@Override
	public final void newTime(final HiResDate oldDTG, final HiResDate newDTG, final MWC.GUI.CanvasType canvas) {
		if (_current != null)
			_current.newTime(oldDTG, newDTG, canvas);
	}

	public final void removePainter(final StepperListener listener) {
		_thePainters.removeElement(listener);
		firePropertyChange();
	}

	////////////////////////////////////////////////////////////
	// 'editable' methods
	////////////////////////////////////////////////////////////

	public final void removePropertyChangeListener(final java.beans.PropertyChangeListener listener) {
		_pSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void reset() {
		// don't worry about it, ignore
	}

	public final void setCurrentListener(final StepperListener listener) {
		// tell the current one it is now 'off'
		if (_current != null)
			_current.steppingModeChanged(false);

		// and assign the new one
		_current = listener;

		// tell the new one it is now on
		_current.steppingModeChanged(true);

		firePropertyChange();

	}

	public final void setDisplay(final String listener) {
		final Enumeration<StepperListener> iter = _thePainters.elements();
		while (iter.hasMoreElements()) {
			final StepperListener l = iter.nextElement();
			if (l.toString().equals(listener)) {
				setCurrentListener(l);
				break;
			}
		}
	}

	////////////////////////////////////////////////////////////
	// property editor to return the painters as a combo box
	////////////////////////////////////////////////////////////

	///////////////////////////////////
	// stepper listener classes
	//////////////////////////////////
	@Override
	public final void steppingModeChanged(final boolean on) {
		if (_current != null)
			_current.steppingModeChanged(on);
	}

	@Override
	public final String toString() {
		return "Painter Manager";
	}
}
