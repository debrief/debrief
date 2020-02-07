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
package info.limpet.stackedcharts.ui.editor.drop;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.dnd.DropTargetEvent;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;

public class DatasetToAxisDropTargetListener extends DatasetDropTargetListener {
	public DatasetToAxisDropTargetListener(final GraphicalViewer viewer) {
		super(viewer);
	}

	@Override
	public boolean appliesTo(final DropTargetEvent event) {
		final EditPart findObjectAt = findPart(event);
		return findObjectAt instanceof AxisEditPart;
	}

	@Override
	protected Command createCommand(final AbstractGraphicalEditPart axis, final List<Dataset> datasets) {
		final AddDatasetsToAxisCommand addDatasetsToAxisCommand = new AddDatasetsToAxisCommand(
				(DependentAxis) axis.getModel(), datasets.toArray(new Dataset[datasets.size()]));
		return addDatasetsToAxisCommand;
	}

}