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
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.ui.views.properties.IPropertySource;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.model.Styling;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;
import info.limpet.stackedcharts.ui.editor.commands.DeleteDatasetsFromAxisCommand;
import info.limpet.stackedcharts.ui.editor.figures.DatasetFigure;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalShape;

/**
 * An {@link GraphicalEditPart} to represent datasets
 */
public class DatasetEditPart extends AbstractGraphicalEditPart implements ActionListener, IPropertySourceProvider {

	public class DatasetAdapter implements Adapter {

		@Override
		public Notifier getTarget() {
			return getDataset();
		}

		@Override
		public boolean isAdapterForType(final Object type) {
			return type.equals(Dataset.class);
		}

		@Override
		public void notifyChanged(final Notification notification) {
			final int featureId = notification.getFeatureID(StackedchartsPackage.class);
			switch (featureId) {
			case StackedchartsPackage.DATASET__STYLING:
				refreshChildren();
				break;
			case StackedchartsPackage.DATASET__NAME:
				refreshVisuals();
				break;
			}
		}

		@Override
		public void setTarget(final Notifier newTarget) {
		}
	}

	private DatasetFigure contentPane;

	private final DatasetAdapter adapter = new DatasetAdapter();

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
		getDataset().eAdapters().add(adapter);
	}

	@Override
	protected void addChildVisual(final EditPart childEditPart, final int index) {
		super.addChildVisual(childEditPart, getContentPane().getChildren().size());
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			@Override
			protected Command createDeleteCommand(final GroupRequest deleteRequest) {
				final Dataset dataset = (Dataset) getHost().getModel();
				final DependentAxis parent = (DependentAxis) getHost().getParent().getModel();
				final DeleteDatasetsFromAxisCommand cmd = new DeleteDatasetsFromAxisCommand(parent, dataset);
				return cmd;
			}
		});
	}

	@Override
	protected IFigure createFigure() {
		final DirectionalShape figure = new DirectionalShape();

		contentPane = new DatasetFigure();
		figure.add(contentPane);

		final Button button = new Button(StackedchartsImages.getImage(StackedchartsImages.DESC_DELETE));
		button.setToolTip(new Label("Remove the dataset from this axis"));
		button.addActionListener(this);
		figure.add(button);
		return figure;
	}

	@Override
	public void deactivate() {
		getDataset().eAdapters().remove(adapter);
		super.deactivate();
	}

	@Override
	public IFigure getContentPane() {
		return contentPane;
	}

	protected Dataset getDataset() {
		return (Dataset) getModel();
	}

	@Override
	public IPropertySource getPropertySource() {
		final Dataset axis = getDataset();
		final Styling axisType = getDataset().getStyling();

		// Proxy two objects in to one
		return new CombinedProperty(axis, axisType, "Styling");
	}

	@Override
	protected void refreshVisuals() {
		contentPane.setName(getDataset().getName());

		final ChartSet parent = ((Chart) getParent().getParent().getParent().getModel()).getParent();

		final boolean horizontal = parent.getOrientation() == Orientation.HORIZONTAL;
		((DirectionalShape) getFigure()).setVertical(!horizontal);

		if (horizontal) {
			contentPane.setVertical(false);
			setLayoutConstraint(this, getFigure(), new GridData(GridData.FILL, GridData.FILL, true, false));

		} else {
			contentPane.setVertical(true);
			setLayoutConstraint(this, getFigure(), new GridData(GridData.CENTER, GridData.FILL, false, true));

		}
	}
}
