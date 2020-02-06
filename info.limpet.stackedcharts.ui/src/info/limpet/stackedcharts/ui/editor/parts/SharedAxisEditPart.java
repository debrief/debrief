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

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;
import info.limpet.stackedcharts.ui.editor.figures.ArrowFigure;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalIconLabel;

/**
 * Represents the shared (independent) axis of a {@link ChartSet} object
 */
public class SharedAxisEditPart extends AbstractGraphicalEditPart {

	public class AxisAdapter implements Adapter {

		@Override
		public Notifier getTarget() {
			return getAxis();
		}

		@Override
		public boolean isAdapterForType(final Object type) {
			return type.equals(IndependentAxis.class);
		}

		@Override
		public void notifyChanged(final Notification notification) {
			final int featureId = notification.getFeatureID(StackedchartsPackage.class);
			switch (featureId) {
			case StackedchartsPackage.INDEPENDENT_AXIS__NAME:
				refreshVisuals();
			}
		}

		@Override
		public void setTarget(final Notifier newTarget) {
		}
	}

	public class ChartSetAdapter implements Adapter {

		private Notifier target;

		void attachTo(final Notifier newTarget) {
			if (this.target != null) {
				this.target.eAdapters().remove(this);
			}
			setTarget(newTarget);
			if (this.target != null) {
				this.target.eAdapters().add(this);
			}
		}

		@Override
		public Notifier getTarget() {
			return target;
		}

		@Override
		public boolean isAdapterForType(final Object type) {
			return type.equals(ChartSet.class);
		}

		@Override
		public void notifyChanged(final Notification notification) {
			final int featureId = notification.getFeatureID(StackedchartsPackage.class);
			switch (featureId) {
			case StackedchartsPackage.CHART_SET__ORIENTATION:
				refreshVisuals();
			}
		}

		@Override
		public void setTarget(final Notifier newTarget) {
			this.target = newTarget;
		}
	}

	private static volatile Font boldFont;

	private final AxisAdapter adapter = new AxisAdapter();

	private final ChartSetAdapter chartSetAdapter = new ChartSetAdapter();

	private DirectionalIconLabel axisNameLabel;

	private ArrowFigure arrowFigure;

	@Override
	public void activate() {
		super.activate();
		getAxis().eAdapters().add(adapter);
		chartSetAdapter.attachTo((ChartSet) getParent().getModel());
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
	}

	@Override
	protected IFigure createFigure() {
		final RectangleFigure rectangle = new RectangleFigure();
		rectangle.setOutline(false);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		rectangle.setLayoutManager(gridLayout);

		arrowFigure = new ArrowFigure(true);
		rectangle.add(arrowFigure);

		// and the text label
		axisNameLabel = new DirectionalIconLabel(StackedchartsImages.getImage(StackedchartsImages.DESC_AXIS));
		axisNameLabel.getLabel().setTextAlignment(PositionConstants.TOP);
		rectangle.add(axisNameLabel);

		return rectangle;
	}

	@Override
	public void deactivate() {
		getAxis().eAdapters().remove(adapter);
		chartSetAdapter.attachTo(null);
		super.deactivate();
	}

	protected IndependentAxis getAxis() {
		return (IndependentAxis) getModel();
	}

	@Override
	protected void refreshVisuals() {
		String name = getAxis().getName();
		if (name == null) {
			name = "<unnamed>";
		}
		axisNameLabel.getLabel().setText("Shared axis: " + name);

		if (boldFont == null) {
			final FontData fontData = axisNameLabel.getFont().getFontData()[0];
			boldFont = new Font(Display.getCurrent(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		}
		axisNameLabel.getLabel().setFont(boldFont);

		final GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;

		final EditPart parent = getParent();
		((GraphicalEditPart) parent).setLayoutConstraint(this, figure, gridData);

		final boolean horizontal = ((ChartSet) parent.getModel()).getOrientation() == Orientation.HORIZONTAL;

		final GridLayout layoutManager = (GridLayout) getFigure().getLayoutManager();
		if (horizontal) {
			arrowFigure.setHorizontal(false);
			axisNameLabel.setVertical(true);

			layoutManager.setConstraint(arrowFigure, new GridData(GridData.CENTER, GridData.FILL, false, true));
			layoutManager.setConstraint(axisNameLabel, new GridData(GridData.CENTER, GridData.FILL, false, true));
			layoutManager.numColumns = getFigure().getChildren().size();
		} else {
			arrowFigure.setHorizontal(true);
			axisNameLabel.setVertical(false);

			layoutManager.setConstraint(arrowFigure, new GridData(GridData.FILL, GridData.CENTER, true, false));
			layoutManager.setConstraint(axisNameLabel, new GridData(GridData.FILL, GridData.CENTER, true, false));
			layoutManager.numColumns = 1;
		}

		layoutManager.invalidate();
		getFigure().invalidate();
	}
}
