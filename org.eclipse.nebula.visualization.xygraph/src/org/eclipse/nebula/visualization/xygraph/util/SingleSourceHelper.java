/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.util;

import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public abstract class SingleSourceHelper {

	private static final SingleSourceHelper IMPL;

	static {
		IMPL = (SingleSourceHelper) ImplementationLoader.newInstance(SingleSourceHelper.class);
	}

	/**
	 * @param display
	 * @param imageData
	 * @param width
	 * @param height
	 * @param backUpSWTCursorStyle
	 * @return a cursor. The cursor will be automatically disposed when display
	 *         disposed, so please don't dispose it externally.
	 */
	public static Cursor createCursor(Display display, ImageData imageData, int width, int height,
			int backUpSWTCursorStyle) {
		return IMPL.createInternalCursor(display, imageData, width, height, backUpSWTCursorStyle);
	}

	public static Image createVerticalTextImage(String text, Font font, RGB color, boolean upToDown) {
		return IMPL.createInternalVerticalTextImage(text, font, color, upToDown);
	}

	public static Image getXYGraphSnapShot(XYGraph xyGraph) {
		return IMPL.getInternalXYGraphSnapShot(xyGraph);
	}

	public static String getImageSavePath() {
		return IMPL.getInternalImageSavePath();
	}

	public static GC getImageGC(final Image image) {
		if (IMPL == null)
			return null;
		return IMPL.internalGetImageGC(image);
	}

	protected abstract GC internalGetImageGC(final Image image);

	protected abstract String getInternalImageSavePath();

	protected abstract Cursor createInternalCursor(Display display, ImageData imageData, int width, int height,
			int backUpSWTCursorStyle);

	protected abstract Image createInternalVerticalTextImage(String text, Font font, RGB color, boolean upToDown);

	protected abstract Image getInternalXYGraphSnapShot(XYGraph xyGraph);

}
