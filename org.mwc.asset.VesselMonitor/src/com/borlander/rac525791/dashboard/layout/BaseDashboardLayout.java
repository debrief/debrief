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

package com.borlander.rac525791.dashboard.layout;

import java.util.List;

import org.eclipse.draw2d.AbstractHintLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class BaseDashboardLayout extends AbstractHintLayout {
	protected static class Cache {
		private final Rectangle myCachedBounds = new Rectangle();
		private final Point myCachedTopLeft = new Point();

		public void cacheTopLeft(final Rectangle actualBounds, final Point point) {
			myCachedBounds.setLocation(actualBounds.x, actualBounds.y);
			myCachedBounds.setSize(actualBounds.width, actualBounds.height);
			myCachedTopLeft.setLocation(point.x, point.y);
		}

		public boolean getCachedTopLeft(final Rectangle actualBounds, final Point output) {
			boolean cacheOk = false;
			if (myCachedBounds.equals(actualBounds)) {
				output.setLocation(myCachedTopLeft);
				cacheOk = true;
			}
			return cacheOk;
		}

	}

	private static final Rectangle TEMP_RECT = new Rectangle();
	private static final Point TEMP = new Point();

	/**
	 * Optimisation -- all our layouts will probably called with same changed
	 * bounds.
	 */
	private static final Cache ourCache = new Cache();

	private final DashboardUIModel myUiModel;

	public BaseDashboardLayout(final DashboardUIModel uiModel) {
		myUiModel = uiModel;
	}

	@Override
	protected Dimension calculatePreferredSize(final IFigure container, final int wHint, final int hHint) {
		final int width = (wHint > 0) ? wHint : container.getBounds().width;
		final int height = (hHint > 0) ? hHint : container.getBounds().height;
		return myUiModel.getUISuite(width, height).getPreferredSize();
	}

	protected final ControlUISuite getSuite(final IFigure container) {
		final Rectangle actualBounds = container.getBounds();
		return myUiModel.getUISuite(actualBounds.width, actualBounds.height);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void layout(final IFigure container) {
		final ControlUISuite suite = getSuite(container);
		final List children = container.getChildren();
		for (int i = 0; i < children.size(); i++) {
			final IFigure next = (IFigure) children.get(i);
			TEMP_RECT.setSize(suite.getPreferredSizeRO());
			placeAtTopLeft(container, TEMP_RECT);
			next.setBounds(TEMP_RECT);
		}
	}

	protected void placeAtTopLeft(final IFigure container, final Point output) {
		final Rectangle availableBounds = container.getBounds();
		if (ourCache.getCachedTopLeft(availableBounds, output)) {
			return;
		}

		final Dimension actualSize = getSuite(container).getPreferredSizeRO();
		final int x = availableBounds.x + (availableBounds.width - actualSize.width) / 2;
		final int y = availableBounds.y + (availableBounds.height - actualSize.height) / 2;
		output.setLocation(x, y);
		ourCache.cacheTopLeft(availableBounds, output);
	}

	protected void placeAtTopLeft(final IFigure container, final Rectangle rectangle) {
		placeAtTopLeft(container, TEMP);
		rectangle.setLocation(TEMP);
	}

}
