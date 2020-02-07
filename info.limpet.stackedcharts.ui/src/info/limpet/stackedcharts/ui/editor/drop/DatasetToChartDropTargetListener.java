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

import info.limpet.stackedcharts.model.AxisType;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.NumberAxis;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart;

public class DatasetToChartDropTargetListener extends DatasetDropTargetListener {
	public DatasetToChartDropTargetListener(final GraphicalViewer viewer) {
		super(viewer);
	}

	@Override
	public boolean appliesTo(final DropTargetEvent event) {
		final EditPart findObjectAt = findPart(event);
		return findObjectAt instanceof ChartEditPart;
	}

	private DependentAxis checkThisAxis(final DependentAxis axis, final String units) {
		DependentAxis res = null;
		final AxisType aType = axis.getAxisType();
		if (aType != null && aType instanceof NumberAxis) {
			final NumberAxis na = (NumberAxis) aType;
			final String axisUnits = na.getUnits();
			if (axisUnits != null && units != null && axisUnits.equals(units)) {
				res = axis;
			}
		}
		return res;
	}

	@Override
	protected Command createCommand(final AbstractGraphicalEditPart editPart, final List<Dataset> datasets) {

		// just check what we've received
		if (!(editPart instanceof ChartEditPart)) {
			System.err.println(this.toString() + " received wrong type of edit part");
		}

		CompoundCommand res = null;
		final Chart chart = ((ChartEditPart) editPart).getModel();

		for (final Dataset dataset : datasets) {
			// find a dataset to dump this dataset into
			final String units = dataset.getUnits();

			DependentAxis targetAxis = null;

			if (targetAxis == null) {
				// ok, we have to go in at the chart level
				targetAxis = findAxisFor(chart.getMinAxes(), dataset.getUnits());

				if (targetAxis == null) {
					targetAxis = findAxisFor(chart.getMinAxes(), dataset.getUnits());
				}

				if (targetAxis == null) {
					// ok, just create a new one
					final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();
					targetAxis = factory.createDependentAxis();
					targetAxis.setName(units);
					final NumberAxis aType = factory.createNumberAxis();
					aType.setUnits(units);
					targetAxis.setAxisType(aType);
					chart.getMinAxes().add(targetAxis);
				}
			}

			// ok, create the command
			final AddDatasetsToAxisCommand command = new AddDatasetsToAxisCommand(targetAxis, dataset);
			if (res == null) {
				res = new CompoundCommand();
			}
			res.add(command);
		}

		return res;
	}

	private DependentAxis findAxisFor(final EList<DependentAxis> axes, final String units) {
		DependentAxis res = null;
		for (final DependentAxis axis : axes) {
			res = checkThisAxis(axis, units);
			if (res != null) {
				break;
			}
		}
		return res;
	}
}