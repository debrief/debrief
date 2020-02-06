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
package info.limpet.stackedcharts.ui.editor.policies;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.commands.DeleteDatasetsFromAxisCommand;
import info.limpet.stackedcharts.ui.editor.commands.MoveAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;
import info.limpet.stackedcharts.ui.editor.parts.DatasetEditPart;

public class AxisContainerEditPolicy extends ContainerEditPolicy implements EditPolicy {

	@Override
	public void eraseTargetFeedback(final Request request) {
		// remove the highlight
		if (REQ_ADD.equals(request.getType())) {
			final AxisEditPart axisEditPart = (AxisEditPart) getHost();
			final IFigure figure = axisEditPart.getFigure();
			figure.setBackgroundColor(AxisEditPart.BACKGROUND_COLOR);
		}
	}

	@Override
	protected Command getAddCommand(final GroupRequest request) {
		@SuppressWarnings("rawtypes")
		final List toAdd = request.getEditParts();

		final Command res;

		// have a peek, to see if it's a dataset, or an axis
		if (toAdd.size() == 0) {
			// nothing selected. don't bother
			res = null;
		} else {
			final Object first = toAdd.get(0);
			if (first instanceof DatasetEditPart) {
				final Dataset[] datasets = new Dataset[toAdd.size()];
				int i = 0;
				for (final Object o : toAdd) {
					datasets[i++] = (Dataset) ((DatasetEditPart) o).getModel();
				}
				res = new AddDatasetsToAxisCommand((DependentAxis) getHost().getModel(), datasets);
			} else if (first instanceof AxisEditPart) {
				final CompoundCommand compoundCommand = new CompoundCommand();
				res = compoundCommand;
				// find the listing we belong to
				final DependentAxis axis = (DependentAxis) getHost().getModel();

				// find out which list (min/max) this axis is currently on
				final EList<DependentAxis> destination = MoveAxisCommand.getHostListFor(axis);

				int indexOfHost = destination.indexOf(axis);
				int newindex = indexOfHost--;
				if (newindex < 0)
					newindex = 0;

				// ok, did we find it?
				if (destination != null) {
					for (final Object o : toAdd) {
						if (o instanceof AxisEditPart) {
							compoundCommand.add(new MoveAxisCommand(destination,
									(DependentAxis) ((AxisEditPart) o).getModel(), newindex));
							newindex++;
						}
					}
				}
			} else {
				// it's not a type that we're interested in. don't bother
				res = null;
			}
		}

		return res;
	}

	@Override
	protected Command getCreateCommand(final CreateRequest request) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	protected Command getOrphanChildrenCommand(final GroupRequest request) {
		@SuppressWarnings("rawtypes")
		final List toRemove = request.getEditParts();
		final Dataset[] datasets = new Dataset[toRemove.size()];
		int i = 0;
		for (final Object o : toRemove) {
			datasets[i++] = (Dataset) ((DatasetEditPart) o).getModel();
		}
		return new DeleteDatasetsFromAxisCommand((DependentAxis) getHost().getModel(), datasets);
	}

	@Override
	public EditPart getTargetEditPart(final Request request) {
		if (REQ_ADD.equals(request.getType())) {
			return getHost();
		}
		return super.getTargetEditPart(request);
	}

	@Override
	public void showTargetFeedback(final Request request) {
		// highlight the Axis when user is about to drop a dataset on it
		if (REQ_ADD.equals(request.getType())) {
			final AxisEditPart axisEditPart = (AxisEditPart) getHost();
			final IFigure figure = axisEditPart.getFigure();
			figure.setBackgroundColor(ColorConstants.lightGray);
		}
	}

}
