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

package com.borlander.rac525791.dashboard.text;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

import com.borlander.rac525791.draw2d.ext.InvisibleRectangle;

public abstract class AbstractTextLayer extends InvisibleRectangle {
	private final GCProxy myGCFactory = new GCProxy();

	private void drawTexts(final Graphics g) {
		try {
			for (final TextDrawer next : getTextDrawers()) {
				next.drawText(myGCFactory, g);
			}
		} finally {
			myGCFactory.dispose();
		}
	}

	@Override
	protected void fillShape(final Graphics g) {
		g.pushState();
		g.setTextAntialias(SWT.ON);
		final Rectangle localBounds = getBounds();
		g.translate(localBounds.x, localBounds.y);
		drawTexts(g);
		g.translate(-localBounds.x, -localBounds.y);
		g.popState();
	}

	protected abstract TextDrawer[] getTextDrawers();

	@Override
	public void paint(final Graphics graphics) {
		if (getLocalBackgroundColor() != null) {
			graphics.setBackgroundColor(getLocalBackgroundColor());
		}
		if (getLocalForegroundColor() != null) {
			graphics.setForegroundColor(getLocalForegroundColor());
		}
		if (getFont() != null) {
			graphics.setFont(getFont());
		}

		graphics.pushState();
		try {
			// inverting order to show text over the children
			paintClientArea(graphics);
			graphics.restoreState();
			paintFigure(graphics);
			graphics.restoreState();
			paintBorder(graphics);
		} finally {
			graphics.popState();
		}
	}

}
