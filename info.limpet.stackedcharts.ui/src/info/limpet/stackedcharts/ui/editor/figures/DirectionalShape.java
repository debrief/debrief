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
package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.RectangleFigure;

/**
 * A Draw2D container {@link RectangleFigure} that orders its children in row or
 * in a column based on the {@link #isVertical()} property. It uses internal
 * layout, clients should not attempt to set new {@link LayoutManager}.
 */
public class DirectionalShape extends RectangleFigure {

	private boolean vertical;

	public DirectionalShape() {
		final DirectionFlowLayout manager = new DirectionFlowLayout();
		manager.setStretchMinorAxis(true);
		manager.setMinorAlignment(OrderedLayout.ALIGN_CENTER);
		manager.setMajorAlignment(OrderedLayout.ALIGN_CENTER);
		super.setLayoutManager(manager);
		setOutline(false);
	}

	/**
	 * @see #setVertical(boolean)
	 * @return
	 */
	public boolean isVertical() {
		return vertical;
	}

	/**
	 * Not intended to be called
	 */
	@Override
	public void setLayoutManager(final LayoutManager manager) {
		throw new UnsupportedOperationException("Layout manager is read-only");
	}

	/**
	 * @param vertical when <code>true</code> children will be laid out from bottom
	 *                 to top
	 */
	public void setVertical(final boolean vertical) {
		this.vertical = vertical;
		((FlowLayout) getLayoutManager()).setHorizontal(!vertical);
	}
}
