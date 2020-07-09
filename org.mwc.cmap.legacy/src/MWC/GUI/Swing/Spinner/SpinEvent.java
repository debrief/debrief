
package MWC.GUI.Swing.Spinner;

import java.util.EventObject;

import javax.swing.JComponent;

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
public class SpinEvent extends EventObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final String command;
	private final JComponent component;

	/**
	 * Create a new SpinEvent.
	 *
	 * @param src  The source of the event.
	 * @param cmd  The action command of the Spinner causing the event.
	 * @param comp The component that the Spinner is 'spinning'.
	 */
	public SpinEvent(final Object src, final String cmd, final JComponent comp) {
		super(src);
		this.command = cmd;
		this.component = comp;
	}

	/**
	 * Get the action command of this Spinner that caused this SpinEvent.
	 *
	 * @return The action command of this SpinEvent.
	 */
	public String getActionCommand() {
		return this.command;
	}

	/**
	 * Get the component that was 'spun' by the Spinner that caused this event.
	 *
	 * @return The component that was 'spun'.
	 */
	public JComponent getComponent() {
		return this.component;
	}
}
