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
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartPaneEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ScatterSetContainerEditPart;
import info.limpet.stackedcharts.ui.view.adapter.AdapterRegistry;
import info.limpet.stackedcharts.ui.view.adapter.IStackedDatasetAdapter;
import info.limpet.stackedcharts.ui.view.adapter.IStackedScatterSetAdapter;

/**
 * base for classes supporting the drop process, including establishing if the
 * target is valid
 *
 * @author ian
 *
 */
abstract public class CoreDropTargetListener implements TransferDropTargetListener {

	protected static boolean canDropSelection(final Chart chart, final ISelection selection) {
		final boolean canDrop = true;

		// NOTE: this section has been deliberately commented out.
		// The Debrief convert() method sometimes opens a dialog to
		// ask the user which data to display (for a tree-view item
		// that is actually several collections. So, we can't call
		// convert() for drag-over operations, we can only call
		// it after a drop
		//
		// We'll have to reconsider the logic here.

		// AdapterRegistry adapter = new AdapterRegistry();
		// if (selection instanceof StructuredSelection)
		// {
		// // check the selection
		// for (Object obj : ((StructuredSelection) selection).toArray())
		// {
		// if (adapter.canConvert(obj))
		// {
		// List<Dataset> convert = adapter.convert(obj);
		// if (convert.size() == 0)
		// {
		// continue;
		// }
		// for (Dataset dataset : convert)
		// {
		// if (!canDropDataset(chart, dataset))
		// {
		// canDrop = false;
		// break;
		// }
		// }
		// }
		// }
		// }

		return canDrop;
	}

	private final GraphicalViewer viewer;

	protected AbstractGraphicalEditPart feedback;

	protected CoreDropTargetListener(final GraphicalViewer viewer) {
		this.viewer = viewer;
	}

	protected void addFeedback(final AbstractGraphicalEditPart figure) {
		if (figure != null) {
			figure.getFigure().setBackgroundColor(ColorConstants.lightGray);
		}
	}

	/**
	 * whether this listener applies to this event
	 *
	 * @param event
	 * @return
	 */
	abstract boolean appliesTo(DropTargetEvent event);

	protected List<Dataset> convertSelectionToDataset(final StructuredSelection selection) {
		final IStackedDatasetAdapter adapter = new AdapterRegistry();
		final List<Dataset> element = new ArrayList<Dataset>();
		for (final Object object : selection.toArray()) {
			if (adapter.canConvertToDataset(object)) {
				final List<Dataset> converted = adapter.convertToDataset(object);
				if (converted != null) {
					element.addAll(converted);
				}
			}
		}

		return element;
	}

	protected List<ScatterSet> convertSelectionToScatterSet(final StructuredSelection selection) {
		final IStackedScatterSetAdapter adapter = new AdapterRegistry();
		final List<ScatterSet> element = new ArrayList<ScatterSet>();
		for (final Object object : selection.toArray()) {
			if (adapter.canConvertToScatterSet(object)) {
				final List<ScatterSet> converted = adapter.convertToScatterSet(object);
				if (converted != null) {
					element.addAll(converted);
				}
			}
		}

		return element;
	}

	@Override
	final public void dragEnter(final DropTargetEvent event) {
	}

	@Override
	final public void dragLeave(final DropTargetEvent event) {
		removeFeedback(feedback);
		feedback = null;
	}

	@Override
	final public void dragOperationChanged(final DropTargetEvent event) {
	}

	@Override
	public final void dragOver(final DropTargetEvent event) {
		final EditPart target = findPart(event);

		if (feedback == target) {
			// drop out, we're still passing over the same object
			return;
		}

		if (LocalSelectionTransfer.getTransfer().isSupportedType(event.currentDataType)) {
			// get the chart model
			Chart chart = null;

			final AbstractGraphicalEditPart editPart = (AbstractGraphicalEditPart) target;
			if (editPart instanceof ChartPaneEditPart) {
				final ChartPaneEditPart chartPanel = (ChartPaneEditPart) editPart;
				chart = (Chart) chartPanel.getModel();
			} else if (editPart instanceof AxisEditPart) {
				final AxisEditPart axis = (AxisEditPart) editPart;
				final ChartPaneEditPart chartEditPane = (ChartPaneEditPart) axis.getParent();
				final ChartEditPart chartPane = (ChartEditPart) chartEditPane.getParent();
				chart = chartPane.getModel();
			} else if (editPart instanceof ScatterSetContainerEditPart) {
				final ScatterSetContainerEditPart scatter = (ScatterSetContainerEditPart) editPart;
				chart = (Chart) scatter.getParent().getModel();
			}

			if (canDropSelection(chart, LocalSelectionTransfer.getTransfer().getSelection())) {
				removeFeedback(feedback);
				feedback = (AbstractGraphicalEditPart) target;
				addFeedback(feedback);
				event.detail = DND.DROP_COPY;
			} else {
				removeFeedback(feedback);
				feedback = null;
				event.detail = DND.DROP_NONE;
			}
		} else {
			removeFeedback(feedback);
			feedback = null;
			event.detail = DND.DROP_NONE;
		}

	}

	@Override
	final public void dropAccept(final DropTargetEvent event) {
	}

	/**
	 * find the object being passed over
	 *
	 * @param event  the event
	 * @param viewer our figure
	 * @return the nearest edit part
	 */
	final protected EditPart findPart(final DropTargetEvent event) {
		final org.eclipse.swt.graphics.Point cP = viewer.getControl().toControl(event.x, event.y);
		final EditPart findObjectAt = viewer.findObjectAt(new Point(cP.x, cP.y));
		return findObjectAt;
	}

	protected CommandStack getCommandStack() {
		return viewer.getEditDomain().getCommandStack();
	}

	@Override
	final public Transfer getTransfer() {
		return LocalSelectionTransfer.getTransfer();
	}

	@Override
	final public boolean isEnabled(final DropTargetEvent event) {
		return LocalSelectionTransfer.getTransfer().isSupportedType(event.currentDataType);
	}

	protected void removeFeedback(final AbstractGraphicalEditPart figure) {
		if (figure != null) {
			figure.getFigure().setBackgroundColor(AxisEditPart.BACKGROUND_COLOR);
		}
	}

	final public void reset() {
		removeFeedback(feedback);
		feedback = null;
	}

}
