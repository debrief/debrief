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

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.graphics.Image;

import info.limpet.stackedcharts.ui.editor.Activator;

/**
 * A {@link DirectionalShape} that has a {@link Label} and an icon on the left.
 *
 */
public class DirectionalIconLabel extends DirectionalShape {
	private final DirectionalLabel label;

	public DirectionalIconLabel(final Image icon) {
		add(new Label(icon));
		this.label = new DirectionalLabel(Activator.FONT_8);
		this.label.setTextAlignment(PositionConstants.TOP);
		add(getLabel());
	}

	public DirectionalLabel getLabel() {
		return label;
	}

	@Override
	public void setVertical(final boolean vertical) {
		super.setVertical(vertical);
		label.setVertical(vertical);
	}

}
