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

package Debrief.Tools.Palette;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Manually select layer
 *
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class AutoSelectTarget extends AbstractAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static boolean _ticked = false;

	public static boolean getAutoSelectTarget() {
		return _ticked;
	}

	public static void setAutoSelectTarget(final boolean yes) {
		_ticked = yes;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		_ticked = !_ticked;
	}

}
