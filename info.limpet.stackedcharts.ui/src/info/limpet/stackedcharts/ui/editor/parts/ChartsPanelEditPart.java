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

import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.ui.editor.parts.ChartSetEditPart.ChartsWrapper;

/**
 * Represents the list of the charts contained in a {@link ChartSet}
 */
public class ChartsPanelEditPart extends AbstractGraphicalEditPart {

	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected IFigure createFigure() {
		final RectangleFigure rectangle = new RectangleFigure();
		rectangle.setOutline(false);
		final GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		rectangle.setLayoutManager(layout);
		rectangle.setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		return rectangle;
	}

	@Override
	public ChartsWrapper getModel() {
		return (ChartsWrapper) super.getModel();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected List getModelChildren() {
		return (getModel()).getCharts();
	}

	@Override
	protected void refreshVisuals() {
		final GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;

		final GraphicalEditPart parent = (GraphicalEditPart) getParent();
		parent.setLayoutConstraint(this, figure, gridData);

		final GridLayout layoutManager = (GridLayout) getFigure().getLayoutManager();
		layoutManager.numColumns = ((ChartSet) parent.getModel()).getOrientation() == Orientation.HORIZONTAL
				? getModelChildren().size()
				: 1;
		layoutManager.invalidate();
	}
}
