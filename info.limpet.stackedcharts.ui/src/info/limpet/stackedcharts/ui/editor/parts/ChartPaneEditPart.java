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

import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart.ChartPanePosition;

public class ChartPaneEditPart extends AbstractGraphicalEditPart {

	public static class AxisLandingPad {
		final Chart chart;
		final ChartEditPart.ChartPanePosition pos;

		public AxisLandingPad(final Chart chart, final ChartEditPart.ChartPanePosition pos) {
			this.chart = chart;
			this.pos = pos;
		}

		public Chart getChart() {
			return chart;
		}

		public ChartEditPart.ChartPanePosition getPos() {
			return pos;
		}
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected IFigure createFigure() {
		final RectangleFigure figure = new RectangleFigure();
		figure.setOutline(false);
		final GridLayout layoutManager = new GridLayout();
		// zero margin, in order to connect the dependent axes to the shared one
		layoutManager.marginHeight = 0;
		layoutManager.marginWidth = 0;
		figure.setLayoutManager(layoutManager);
		return figure;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected List getModelChildren() {
		final Chart chart = (Chart) getParent().getModel();

		final ChartEditPart.ChartPanePosition pos = (ChartPanePosition) getModel();
		switch (pos) {
		case MIN:
			return chart.getMinAxes().size() == 0 ? Arrays.asList(new AxisLandingPad(chart, pos)) : chart.getMinAxes();

		case MAX:
			return chart.getMaxAxes().size() == 0 ? Arrays.asList(new AxisLandingPad(chart, pos)) : chart.getMaxAxes();
		}

		return Arrays.asList();
	}

	@Override
	protected void refreshVisuals() {
		final ChartEditPart.ChartPanePosition pos = (ChartPanePosition) getModel();
		final IFigure figure = getFigure();

		final ChartSet chartSet = ((Chart) getParent().getModel()).getParent();
		final boolean vertical = chartSet.getOrientation() == Orientation.VERTICAL;

		if (pos == ChartPanePosition.MIN) {
			((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
					vertical ? BorderLayout.LEFT : BorderLayout.BOTTOM);
		} else {
			((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
					vertical ? BorderLayout.RIGHT : BorderLayout.TOP);
		}

		((GridLayout) getFigure().getLayoutManager()).numColumns = vertical ? getModelChildren().size() : 1;
	}
}
