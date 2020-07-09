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

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.dnd.DropTargetEvent;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.editor.commands.AddAxisToChartCommand;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.AxisLandingPadEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart.ChartPanePosition;
import info.limpet.stackedcharts.ui.editor.parts.ChartPaneEditPart;

public class DatasetToAxisLandingDropTargetListener extends DatasetDropTargetListener {
	public DatasetToAxisLandingDropTargetListener(final GraphicalViewer viewer) {
		super(viewer);
	}

	@Override
	public boolean appliesTo(final DropTargetEvent event) {
		final EditPart findObjectAt = findPart(event);
		return findObjectAt instanceof AxisLandingPadEditPart;
	}

	@Override
	protected Command createCommand(final AbstractGraphicalEditPart axis, final List<Dataset> datasets) {
		final CompoundCommand compoundCommand = new CompoundCommand();

		// get the dimensions of the first dataset
		final String units;
		if (datasets != null && datasets.size() > 0 && datasets.get(0).getUnits() != null) {
			final Dataset dataset = datasets.get(0);
			units = dataset.getUnits();
		} else {
			units = "[dimensionless]";
		}

		final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();
		final DependentAxis newAxis = factory.createDependentAxis();
		newAxis.setName(units);
		newAxis.setAxisType(factory.createNumberAxis());

		final ChartPaneEditPart.AxisLandingPad pad = (ChartPaneEditPart.AxisLandingPad) axis.getModel();
		// find out which list (min/max) this axis is currently on
		final EList<DependentAxis> destination = pad.getPos() == ChartPanePosition.MIN ? pad.getChart().getMinAxes()
				: pad.getChart().getMaxAxes();

		compoundCommand.add(new AddAxisToChartCommand(destination, newAxis));

		final AddDatasetsToAxisCommand addDatasetsToAxisCommand = new AddDatasetsToAxisCommand(newAxis,
				datasets.toArray(new Dataset[datasets.size()]));
		compoundCommand.add(addDatasetsToAxisCommand);
		return compoundCommand;
	}

}
