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
package MWC.GUI;

import java.util.Enumeration;

/**
 * specialised type of layer that is able to plot dynamic children, that-is
 * elements that are time-sensitive.
 *
 * @author ian
 *
 */
public class DynamicLayer extends BaseLayer implements DynamicPlottable {

	private static final long serialVersionUID = 1L;

	@Override
	public void add(final Editable thePlottable) {
		// SPECIAL HANDLING. We can't allow DynamicPlottables to be added to normal
		// layers, since normal layers don't provide the time integration.
		// We only allow them to be pasted into a DynamicLayer like this.
		super.getData().add(thePlottable);
	}

	@Override
	public void paint(final CanvasType dest) {
		// do nothing
	}

	@Override
	public void paint(final CanvasType dest, final long time) {
		final Enumeration<Editable> elements = elements();
		while (elements.hasMoreElements()) {
			final Editable element = elements.nextElement();
			if (element instanceof DynamicPlottable) {
				((DynamicPlottable) element).paint(dest, time);
			}
		}
	}

}
