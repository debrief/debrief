package info.limpet.stackedcharts.ui.editor.drop;

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

/**
 * base for classes supporting the drop process, including establishing if the
 * target is valid
 * 
 * @author ian
 * 
 */
abstract public class CoreDropTargetListener implements
		TransferDropTargetListener {

	private final GraphicalViewer viewer;
	protected AbstractGraphicalEditPart feedback;

	protected CoreDropTargetListener(GraphicalViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * whether this listener applies to this event
	 * 
	 * @param event
	 * @return
	 */
	abstract boolean appliesTo(DropTargetEvent event);

	/**
	 * find the object being passed over
	 * 
	 * @param event
	 *            the event
	 * @param viewer
	 *            our figure
	 * @return the nearest edit part
	 */
	final protected EditPart findPart(DropTargetEvent event) {
		org.eclipse.swt.graphics.Point cP = viewer.getControl().toControl(
				event.x, event.y);
		EditPart findObjectAt = viewer.findObjectAt(new Point(cP.x, cP.y));
		return findObjectAt;
	}

	protected void addFeedback(AbstractGraphicalEditPart figure) {
		if (figure != null) {
			figure.getFigure().setBackgroundColor(ColorConstants.lightGray);
		}
	}

	protected void removeFeedback(AbstractGraphicalEditPart figure) {
		if (figure != null) {
			figure.getFigure()
					.setBackgroundColor(AxisEditPart.BACKGROUND_COLOR);
		}
	}

	protected CommandStack getCommandStack() {
		return viewer.getEditDomain().getCommandStack();
	}

	protected List<Dataset> convertSelectionToDataset(
			StructuredSelection selection) {
		IStackedDatasetAdapter adapter = new AdapterRegistry();
		List<Dataset> element = new ArrayList<Dataset>();
		for (Object object : selection.toArray()) {
			if (adapter.canConvertToDataset(object)) {
				final List<Dataset> converted = adapter
						.convertToDataset(object);
				if (converted != null) {
					element.addAll(converted);
				}
			}
		}

		return element;
	}

	protected List<ScatterSet> convertSelectionToScatterSet(
			StructuredSelection selection) {
		IStackedScatterSetAdapter adapter = new AdapterRegistry();
		List<ScatterSet> element = new ArrayList<ScatterSet>();
		for (Object object : selection.toArray()) {
			if (adapter.canConvertToScatterSet(object)) {
				final List<ScatterSet> converted = adapter
						.convertToScatterSet(object);
				if (converted != null) {
					element.addAll(converted);
				}
			}
		}

		return element;
	}

	protected static boolean canDropSelection(final Chart chart,
			ISelection selection) {
		boolean canDrop = true;

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

	@Override
	public final void dragOver(DropTargetEvent event) {
		EditPart target = findPart(event);

		if (feedback == target) {
			// drop out, we're still passing over the same object
			return;
		}

		if (LocalSelectionTransfer.getTransfer().isSupportedType(
				event.currentDataType)) {
			// get the chart model
			Chart chart = null;

			AbstractGraphicalEditPart editPart = (AbstractGraphicalEditPart) target;
			if (editPart instanceof ChartPaneEditPart) {
				ChartPaneEditPart chartPanel = (ChartPaneEditPart) editPart;
				chart = (Chart) chartPanel.getModel();
			} else if (editPart instanceof AxisEditPart) {
				AxisEditPart axis = (AxisEditPart) editPart;
				ChartPaneEditPart chartEditPane = (ChartPaneEditPart) axis
						.getParent();
				ChartEditPart chartPane = (ChartEditPart) chartEditPane
						.getParent();
				chart = (Chart) chartPane.getModel();
			} else if (editPart instanceof ScatterSetContainerEditPart) {
				ScatterSetContainerEditPart scatter= (ScatterSetContainerEditPart) editPart;
				chart = (Chart) scatter.getParent().getModel();
			}

			if (canDropSelection(chart, LocalSelectionTransfer.getTransfer()
					.getSelection())) {
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
	final public void dragOperationChanged(DropTargetEvent event) {
	}

	@Override
	final public void dragLeave(DropTargetEvent event) {
		removeFeedback(feedback);
		feedback = null;
	}

	@Override
	final public void dragEnter(DropTargetEvent event) {
	}

	@Override
	final public boolean isEnabled(DropTargetEvent event) {
		return LocalSelectionTransfer.getTransfer().isSupportedType(
				event.currentDataType);
	}

	@Override
	final public Transfer getTransfer() {
		return LocalSelectionTransfer.getTransfer();
	}

	final public void reset() {
		removeFeedback(feedback);
		feedback = null;
	}

	@Override
	final public void dropAccept(DropTargetEvent event) {
	}

}
