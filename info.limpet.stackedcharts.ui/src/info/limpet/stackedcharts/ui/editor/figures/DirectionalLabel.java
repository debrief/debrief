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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageUtilities;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Image;

import info.limpet.stackedcharts.ui.editor.Activator;

/**
 * The label is a standard horizontal label by default and it can change
 * orientation to vertical
 *
 */
public class DirectionalLabel extends Label {

	private boolean vertical;

	public DirectionalLabel(final String fontName) {
		setFont(Activator.getDefault().getFont(fontName));
	}

	@Override
	protected Dimension calculateLabelSize(final Dimension txtSize) {
		Dimension labelSize = super.calculateLabelSize(txtSize);
		if (vertical) {
			labelSize = labelSize.transpose();
		}
		return labelSize;
	}

	@Override
	protected void paintFigure(final Graphics graphics) {
		if (vertical) {

			final String subStringText = getSubStringText();
			if (!subStringText.isEmpty()) {
				final Image image = ImageUtilities.createRotatedImageOfString(subStringText, getFont(),
						getForegroundColor(), getBackgroundColor());
				graphics.drawImage(image, new Point(getTextLocation()).translate(getLocation()));
				image.dispose();
			}
		} else {
			super.paintFigure(graphics);
		}
	}

	public void setVertical(final boolean vertical) {
		this.vertical = vertical;
		repaint();
		invalidate();
	}

}
