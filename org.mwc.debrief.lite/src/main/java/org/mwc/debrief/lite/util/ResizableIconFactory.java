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

package org.mwc.debrief.lite.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import org.pushingpixels.neon.api.icon.ResizableIcon;


/**
 * @author Ayesha
 *
 */
public class ResizableIconFactory implements ResizableIcon {

	public static Factory factory(final ResizableIcon icon) {
		return () -> new ResizableIconFactory(icon);
	}

	private final ResizableIcon delegate;

	public ResizableIconFactory(final ResizableIcon icon) {
		delegate = icon;
	}

	@Override
	public int getIconHeight() {
		return delegate.getIconHeight();
	}

	@Override
	public int getIconWidth() {
		return delegate.getIconWidth();
	}

	@Override
	public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
		delegate.paintIcon(c, g, x, y);
	}

	@Override
	public void setDimension(final Dimension arg0) {
		delegate.setDimension(arg0);

	}

}
