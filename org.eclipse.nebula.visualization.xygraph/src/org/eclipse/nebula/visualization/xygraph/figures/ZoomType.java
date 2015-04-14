/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.figures;

import org.eclipse.nebula.visualization.xygraph.Messages;
import org.eclipse.nebula.visualization.xygraph.util.SingleSourceHelper;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * The type of zoom on XYGraph.
 * 
 * @author Xihui Chen
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public enum ZoomType {
	/** Interactive Rubberband zoom */
	RUBBERBAND_ZOOM(Messages.Zoom_Rubberband, XYGraphMediaFactory.getInstance().getImage("images/RubberbandZoom.png"),
			XYGraphMediaFactory.getInstance().getImage("images/RubberbandZoomCursor.png"), XYGraphMediaFactory
					.getInstance().getImage("images/HorizontalZoomCursorOnXAxis.png"), XYGraphMediaFactory
					.getInstance().getImage("images/VerticalZoomCursorOnYAxis.png"), XYGraphFlags.COMBINED_ZOOM
					| XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_CROSS),

	/** Zoom via 'cursors' for horizontal start/end position */
	HORIZONTAL_ZOOM(Messages.Zoom_Horiz, XYGraphMediaFactory.getInstance().getImage("images/HorizontalZoom.png"),
			XYGraphMediaFactory.getInstance().getImage("images/HorizontalZoomCursor.png"), XYGraphMediaFactory
					.getInstance().getImage("images/HorizontalZoomCursorOnXAxis.png"), null, XYGraphFlags.COMBINED_ZOOM
					| XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_SIZEWE),

	/** Zoom via 'cursors' for vertical start/end position */
	VERTICAL_ZOOM(Messages.Zoom_Vert, XYGraphMediaFactory.getInstance().getImage("images/VerticalZoom.png"),
			XYGraphMediaFactory.getInstance().getImage("images/VerticalZoomCursor.png"), null, XYGraphMediaFactory
					.getInstance().getImage("images/VerticalZoomCursorOnYAxis.png"), XYGraphFlags.COMBINED_ZOOM
					| XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_SIZENS),

	/** Zoom 'in' around mouse pointer */
	ZOOM_IN(Messages.Zoom_In, XYGraphMediaFactory.getInstance().getImage("images/ZoomIn.png"), XYGraphMediaFactory
			.getInstance().getImage("images/ZoomInCursor.png"), XYGraphMediaFactory.getInstance().getImage(
			"images/ZoomInCursorOnXAxis.png"), XYGraphMediaFactory.getInstance().getImage(
			"images/ZoomInCursorOnYAxis.png"), XYGraphFlags.COMBINED_ZOOM, SWT.CURSOR_HAND),

	/** Zoom 'out' around mouse pointer */
	ZOOM_OUT(Messages.Zoom_Out, XYGraphMediaFactory.getInstance().getImage("images/ZoomOut.png"), XYGraphMediaFactory
			.getInstance().getImage("images/ZoomOutCursor.png"), XYGraphMediaFactory.getInstance().getImage(
			"images/ZoomOutCursorOnXAxis.png"), XYGraphMediaFactory.getInstance().getImage(
			"images/ZoomOutCursorOnYAxis.png"), XYGraphFlags.COMBINED_ZOOM, SWT.CURSOR_HAND),

	/** Zoom 'in' around mouse pointer along horizontal axis */
	ZOOM_IN_HORIZONTALLY(Messages.Zoom_InHoriz, XYGraphMediaFactory.getInstance().getImage("images/ZoomInHoriz.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomInHorizCursor.png"), XYGraphMediaFactory
					.getInstance().getImage("images/ZoomInCursorOnXAxis.png"), null, XYGraphFlags.SEPARATE_ZOOM,
			SWT.CURSOR_SIZEWE),

	/** Zoom 'out' around mouse pointer along horizontal axis */
	ZOOM_OUT_HORIZONTALLY(Messages.Zoom_OutHoriz,
			XYGraphMediaFactory.getInstance().getImage("images/ZoomOutHoriz.png"), XYGraphMediaFactory.getInstance()
					.getImage("images/ZoomOutHorizCursor.png"), XYGraphMediaFactory.getInstance().getImage(
					"images/ZoomOutCursorOnXAxis.png"), null, XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_SIZEWE),

	/** Zoom 'in' around mouse pointer along vertical axis */
	ZOOM_IN_VERTICALLY(Messages.Zoom_InVert, XYGraphMediaFactory.getInstance().getImage("images/ZoomInVert.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomInVertCursor.png"), null, XYGraphMediaFactory
					.getInstance().getImage("images/ZoomInCursorOnYAxis.png"), XYGraphFlags.SEPARATE_ZOOM,
			SWT.CURSOR_SIZENS),

	/** Zoom 'out' around mouse pointer along vertical axes */
	ZOOM_OUT_VERTICALLY(Messages.Zoom_OutVert, XYGraphMediaFactory.getInstance().getImage("images/ZoomOutVert.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomOutVertCursor.png"), null, XYGraphMediaFactory
					.getInstance().getImage("images/ZoomOutCursorOnYAxis.png"), XYGraphFlags.SEPARATE_ZOOM,
			SWT.CURSOR_SIZENS),

	/** Zoom 'out' around mouse pointer */
	PANNING(Messages.Zoom_Pan, XYGraphMediaFactory.getInstance().getImage("images/Panning.png"), XYGraphMediaFactory
			.getInstance().getImage("images/PanningCursor.png"), XYGraphMediaFactory.getInstance().getImage(
			"images/PanningCursorOnAxis.png"), XYGraphMediaFactory.getInstance().getImage(
			"images/PanningCursorOnAxis.png"), XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM,
			SWT.CURSOR_SIZEALL),

	/** Disarm zoom behavior */
	NONE(Messages.Zoom_None, XYGraphMediaFactory.getInstance().getImage("images/MouseArrow.png"), null, null, null,
			XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_ARROW);

	final private Image iconImage;
	final private String description;
	final private Cursor cursor;
	final private Cursor cursorOnXAxis;
	final private Cursor cursorOnYAxis;

	final private int flags;

	/**
	 * Initialize
	 * 
	 * @param description
	 *            Description used for tool tip
	 * @param iconImage
	 *            Button icon
	 * @param cursorImage
	 *            Cursor when zoom type is selected
	 * @param flags
	 *            Bitwise 'or' of flags that specify in which zoom
	 *            configurations this zoom type should be included
	 * @see XYGraphFlags#COMBINED_ZOOM
	 * @see XYGraphFlags#SEPARATE_ZOOM
	 */
	private ZoomType(final String description, final Image iconImage, final Image cursorImage,
			final Image cursorImageOnXAxis, final Image cursorImageOnYAxis, final int flags,
			final int backUpSWTCursorType) {
		this.description = description;
		this.iconImage = iconImage;
		if (cursorImage == null)
			cursor = Display.getDefault().getSystemCursor(SWT.CURSOR_ARROW);
		else
			cursor = SingleSourceHelper.createCursor(Display.getDefault(), cursorImage.getImageData(), 8, 8,
					backUpSWTCursorType);
		if (cursorImageOnXAxis == null) {
			cursorOnXAxis = cursor;
		} else {
			cursorOnXAxis = SingleSourceHelper.createCursor(Display.getDefault(), cursorImageOnXAxis.getImageData(), 8,
					8, backUpSWTCursorType);
		}
		if (cursorImageOnYAxis == null) {
			cursorOnYAxis = cursor;
		} else {
			cursorOnYAxis = SingleSourceHelper.createCursor(Display.getDefault(), cursorImageOnYAxis.getImageData(), 8,
					8, backUpSWTCursorType);
		}

		this.flags = flags;
	}

	/**
	 * @return the iconImageData
	 */
	public Image getIconImage() {
		return iconImage;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the cursor
	 */
	public Cursor getCursor() {
		return cursor;
	}

	/**
	 * @return the cursor on axis.
	 */
	public Cursor getCursorOnAxis(boolean horizontalAxis) {
		if (horizontalAxis)
			return cursorOnXAxis;
		else
			return cursorOnYAxis;
	}

	/**
	 * Check if this zoom mode should be offered when a graph was created with
	 * given flags
	 * 
	 * @param flags
	 *            Flags of the XYGraph tool bar
	 * @return <code>true</code> if this zoom type applies
	 */
	public boolean useWithFlags(final int flags) {
		return (this.flags & flags) > 0;
	}

	@Override
	public String toString() {
		return description;
	}
}
