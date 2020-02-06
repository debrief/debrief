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
package info.limpet.stackedcharts.ui.editor.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.swt.graphics.Image;

import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

public class StylingEditPart extends AbstractGraphicalEditPart {

	/**
	 * Standard Eclipse icon, source:
	 * http://eclipse-icons.i24.cc/eclipse-icons-07.html
	 */
	private static final Image IMAGE = StackedchartsImages.getImage(StackedchartsImages.DESC_PAINT);

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
	}

	@Override
	protected IFigure createFigure() {
		final Label label = new Label(IMAGE);
		label.setToolTip(new Label("Click to view style properties"));
		return label;
	}

}
