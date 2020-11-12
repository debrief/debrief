/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package MWC.GUI;

import MWC.Utilities.Errors.Trace;

/**
 * @author Ayesha
 *
 */
public class RestrictedBaseLayer<T extends Editable> extends BaseLayer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public RestrictedBaseLayer(final boolean orderedChildren) {
		super(orderedChildren);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(final Editable thePlottable) {
		try {
			addElement((T) thePlottable);
		} catch (final ClassCastException ce) {
			Trace.trace("Can't add :" + thePlottable.getName() + " to :" + this.getName(), true);
			MessageProvider.Base.show("add", "Can't add :" + thePlottable.getName() + " to :" + this.getName(),
					MessageProvider.ERROR);
		}
	}

	private void addElement(final T item) {
		super.add(item);
	}

}