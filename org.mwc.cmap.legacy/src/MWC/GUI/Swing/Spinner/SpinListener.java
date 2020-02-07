
package MWC.GUI.Swing.Spinner;

import java.util.EventListener;

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
public interface SpinListener extends EventListener {
	/**
	 * If this listener is registered with a Spinner, this method will be called
	 * when the control is 'spun' up.
	 *
	 * @param event The SpinEvent providing information about the 'spin'.
	 */
	public void spinnerSpunDown(SpinEvent event);

	/**
	 * If this listener is registered with a Spinner, this method will be called
	 * when the control is 'spun' up.
	 *
	 * @param event The SpinEvent providing information about the 'spin'.
	 */
	public void spinnerSpunUp(SpinEvent event);
}
