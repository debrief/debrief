
package com.visutools.nav.bislider;

import java.util.Vector;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public class ColorisationSupport implements java.io.Serializable {
	static final long serialVersionUID = -1497023291489642695L;

	private final Vector<ColorisationListener> colorisationListeners = new Vector<ColorisationListener>();

	/**
	 * Adds a feature to the ColorisationListener attribute of the
	 * ColorisationSupport object
	 *
	 * @param l The feature to be added to the ColorisationListener attribute
	 */
	public synchronized void addColorisationListener(final ColorisationListener l) {
		// add a listener if it is not already registered
		if (!colorisationListeners.contains(l))
			colorisationListeners.addElement(l);
	}// addColorisationListener()

	/**
	 * Create a ColorisationEvent event
	 *
	 * @param Source_Arg     Source of the event. Should be the BiSlider
	 * @param ColorArray_Arg Array describing the color correspondances.
	 * @return A colorizer object for the given parameters
	 */
	public Colorizer createColorisationEvent(final Object Source_Arg, final double[][] ColorArray_Arg) {
		return new ColorisationEvent(Source_Arg, ColorArray_Arg);
	}// createColorisationEvent()

	/**
	 * fire the evcent asynchronously
	 *
	 * @param Source_Arg     Source of the event. Should be the BiSlider
	 * @param ColorArray_Arg Array describing the color correspondances.
	 */
	public void fireAsyncNewColors(final Object Source_Arg, final double[][] ColorArray_Arg) {
		final Thread Thread1 = new Thread() {
			@Override
			public void run() {
				internFireAsyncNewColors(Source_Arg, ColorArray_Arg);
			}
		};
		Thread1.start();
	}// fireAsyncNewColors()

	/**
	 * Description of the Method
	 *
	 * @param Source_Arg     Source of the event. Should be the BiSlider
	 * @param ColorArray_Arg Array describing the color correspondances.
	 */
	@SuppressWarnings("unchecked")
	public void fireNewColors(final Object Source_Arg, final double[][] ColorArray_Arg) {
		// Make a copy of the listener object vector so that
		// it cannot be changed while we are firing events.
		Vector<ColorisationListener> v;
		synchronized (this) {
			v = (Vector<ColorisationListener>) colorisationListeners.clone();
		}

		// Fire the event to all listeners.
		final int count = v.size();
		for (int i = 0; i < count; i++) {
			final ColorisationListener listener = v.elementAt(i);
			listener.newColors(new ColorisationEvent(Source_Arg, ColorArray_Arg));
		}
	}// fireNewColors()

	/**
	 * fire the event asynchronously
	 *
	 * @param Source_Arg     Source of the event. Should be the BiSlider
	 * @param ColorArray_Arg Array describing the color correspondances.
	 */
	@SuppressWarnings("unchecked")
	void internFireAsyncNewColors(final Object Source_Arg, final double[][] ColorArray_Arg) {
		Vector<ColorisationListener> v;
		synchronized (this) {
			v = (Vector<ColorisationListener>) colorisationListeners.clone();
		}

		// Fire the event to all listeners.
		final int count = v.size();
		for (int i = 0; i < count; i++) {
			final ColorisationListener listener = v.elementAt(i);
			listener.newColors(new ColorisationEvent(Source_Arg, ColorArray_Arg));
		}
	}// internFireAsyncNewColors()

	/**
	 * Description of the Method
	 *
	 * @param l Description of the Parameter
	 */
	public synchronized void removeColorisationListener(final ColorisationListener l) {
		// remove it if it is registered
		if (colorisationListeners.contains(l))
			colorisationListeners.removeElement(l);
	}// removeColorisationListener()
}
