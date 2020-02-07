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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsPackage;

public class ChartSetEditPart extends AbstractGraphicalEditPart {
	public class ChartSetAdapter implements Adapter {

		@Override
		public Notifier getTarget() {
			return getModel();
		}

		@Override
		public boolean isAdapterForType(final Object type) {
			return type.equals(ChartSet.class);
		}

		@Override
		public void notifyChanged(final Notification notification) {
			final int featureId = notification.getFeatureID(StackedchartsPackage.class);
			switch (featureId) {
			case StackedchartsPackage.CHART_SET__CHARTS:
				refreshChildren();
				break;
			case StackedchartsPackage.CHART_SET__ORIENTATION:
				refresh();
				break;
			}
		}

		@Override
		public void setTarget(final Notifier newTarget) {
			// Do nothing.
		}
	}

	public static class ChartSetWrapper {
		private final ChartSet charts;

		public ChartSetWrapper(final ChartSet charts) {
			this.charts = charts;
		}

		public ChartSet getcChartSet() {
			return charts;
		}
	}

	/**
	 * Wraps the charts, so that they are displayed in a separate container and not
	 * together with the shared axis.
	 */
	public static class ChartsWrapper {
		private final List<Chart> charts;

		public ChartsWrapper(final List<Chart> charts) {
			this.charts = charts;
		}

		public List<Chart> getCharts() {
			return charts;
		}
	}

	private final ChartSetAdapter adapter = new ChartSetAdapter();

	@Override
	public void activate() {
		super.activate();
		getChartSet().eAdapters().add(adapter);
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected IFigure createFigure() {
		final RectangleFigure rectangle = new RectangleFigure();
		rectangle.setOutline(false);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		rectangle.setLayoutManager(gridLayout);
		rectangle.setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

		return rectangle;
	}

	@Override
	public void deactivate() {
		getChartSet().eAdapters().remove(adapter);
		super.deactivate();
	}

	ChartSet getChartSet() {
		return getModel();
	}

	@Override
	public ChartSet getModel() {
		return (ChartSet) super.getModel();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List getModelChildren() {
		// 2 model children - the charts, displayed in a separate container and the
		// shared (independent
		// axis) shown on the bottom
		final List modelChildren = new ArrayList<>();
		final ChartSet chartSet = getModel();
		modelChildren.add(new ChartSetWrapper(chartSet));
		modelChildren.add(new ChartsWrapper(chartSet.getCharts()));

		final boolean horizontal = chartSet.getOrientation() == Orientation.HORIZONTAL;
		modelChildren.add(horizontal ? 1 : modelChildren.size(), chartSet.getSharedAxis());
		return modelChildren;
	}

	@Override
	protected void refreshVisuals() {
		final GridLayout layoutManager = (GridLayout) getFigure().getLayoutManager();

		layoutManager.numColumns = getChartSet().getOrientation() == Orientation.HORIZONTAL ? getModelChildren().size()
				: 1;
		layoutManager.invalidate();
	}
}
