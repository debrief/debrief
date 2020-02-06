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

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**
 * An edit part for Scatter Set object
 */
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;
import info.limpet.stackedcharts.ui.editor.commands.DeleteScatterSetCommand;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalIconLabel;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalShape;

public class ScatterSetEditPart extends AbstractGraphicalEditPart implements ActionListener {

	public class ScatterSetAdapter implements Adapter {

		@Override
		public Notifier getTarget() {
			return getModel();
		}

		@Override
		public boolean isAdapterForType(final Object type) {
			return type.equals(ScatterSet.class);
		}

		@Override
		public void notifyChanged(final Notification notification) {
			final int featureId = notification.getFeatureID(StackedchartsPackage.class);
			switch (featureId) {
			case StackedchartsPackage.SCATTER_SET__NAME:
				refreshVisuals();
				break;
			}
		}

		@Override
		public void setTarget(final Notifier newTarget) {
			// Do nothing.
		}
	}

	private final ScatterSetAdapter adapter = new ScatterSetAdapter();

	private DirectionalIconLabel scatterSetNameLabel;

	@Override
	public void actionPerformed(final ActionEvent event) {
		final Command deleteCommand = getCommand(new GroupRequest(REQ_DELETE));
		if (deleteCommand != null) {
			final CommandStack commandStack = getViewer().getEditDomain().getCommandStack();
			commandStack.execute(deleteCommand);
		}
	}

	@Override
	public void activate() {
		super.activate();
		getModel().eAdapters().add(adapter);
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());

		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			@Override
			protected Command createDeleteCommand(final GroupRequest deleteRequest) {
				// TODO: implement
				// 1. do not use this scatter set in the current chart
				// 2. if scatter set used only here, then delete scatter set from shared axis
				return new DeleteScatterSetCommand(getModel(), getChart());
			}
		});
	}

	@Override
	protected IFigure createFigure() {
		final DirectionalShape figure = new DirectionalShape();
		scatterSetNameLabel = new DirectionalIconLabel(StackedchartsImages.getImage(StackedchartsImages.DESC_DATASET));
		figure.add(scatterSetNameLabel);
		final Button button = new Button(StackedchartsImages.getImage(StackedchartsImages.DESC_DELETE));
		button.setToolTip(new Label("Remove scatter set"));
		button.addActionListener(this);
		figure.add(button);

		return figure;
	}

	@Override
	public void deactivate() {
		getModel().eAdapters().remove(adapter);
		super.deactivate();
	}

	public Chart getChart() {
		return (Chart) getParent().getParent().getModel();
	}

	@Override
	public ScatterSet getModel() {
		return (ScatterSet) super.getModel();
	}

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		final ScatterSet scatterSet = getModel();
		final String name = scatterSet.getName();
		scatterSetNameLabel.getLabel().setText(name != null ? name : "<unnamed>");

		final ChartSet chartSet = getChart().getParent();
		final boolean vertical = chartSet.getOrientation() == Orientation.VERTICAL;
		((DirectionalShape) getFigure()).setVertical(!vertical);
		scatterSetNameLabel.setVertical(!vertical);
	}

}
