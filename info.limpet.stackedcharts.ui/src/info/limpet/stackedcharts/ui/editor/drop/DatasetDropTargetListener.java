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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;

/**
 * base for classes supporting the drop process, including establishing if the
 * target is valid
 *
 * @author ian
 *
 */
abstract public class DatasetDropTargetListener extends CoreDropTargetListener {

	protected static boolean canDropDataset(final Chart chart, final Dataset dataset) {
		boolean possible = true;

		// check the axis
		final Iterator<DependentAxis> minIter = chart.getMinAxes().iterator();
		final Iterator<DependentAxis> maxIter = chart.getMaxAxes().iterator();

		if (datasetAlreadyExistsOnTheseAxes(minIter, dataset.getName())
				|| datasetAlreadyExistsOnTheseAxes(maxIter, dataset.getName())) {
			possible = false;
		}

		return possible;
	}

	protected static boolean datasetAlreadyExistsOnTheseAxes(final Iterator<DependentAxis> axes, final String name) {
		boolean exists = false;

		while (axes.hasNext()) {
			final DependentAxis dAxis = axes.next();
			final Iterator<Dataset> dIter = dAxis.getDatasets().iterator();
			while (dIter.hasNext()) {
				final Dataset thisD = dIter.next();
				if (name.equals(thisD.getName())) {
					// ok, we can't add it
					System.err.println("Not adding dataset - duplicate name");
					exists = true;
					break;
				}
			}
		}

		return exists;
	}

	protected AbstractGraphicalEditPart feedback;

	protected DatasetDropTargetListener(final GraphicalViewer viewer) {
		super(viewer);
	}

	/**
	 * wrap up the data change for the drop event
	 *
	 * @param axis
	 * @param datasets
	 * @return
	 */
	abstract protected Command createCommand(AbstractGraphicalEditPart axis, List<Dataset> datasets);

	@Override
	public void drop(final DropTargetEvent event) {
		if (LocalSelectionTransfer.getTransfer().isSupportedType(event.currentDataType)) {
			final StructuredSelection sel = (StructuredSelection) LocalSelectionTransfer.getTransfer().getSelection();
			if (sel.isEmpty()) {
				event.detail = DND.DROP_NONE;
				return;
			}
			final List<Dataset> objects = convertSelectionToDataset(sel);
			final EditPart target = findPart(event);

			final AbstractGraphicalEditPart editPart = (AbstractGraphicalEditPart) target;
			final List<Dataset> datasets = new ArrayList<Dataset>(objects.size());
			for (final Object o : objects) {
				if (o instanceof Dataset) {
					datasets.add((Dataset) o);
				} else if (o instanceof List<?>) {
					final List<?> list = (List<?>) o;
					for (final Iterator<?> iter = list.iterator(); iter.hasNext();) {
						final Object item = iter.next();
						if (item instanceof Dataset) {
							datasets.add((Dataset) item);
						}
					}
				}
			}

			if (datasets.size() > 0) {
				final Command command = createCommand(editPart, datasets);
				getCommandStack().execute(command);
			}
		}

		feedback = null;
	}

}
