package org.mwc.cmap.grideditor.table.actons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.mwc.cmap.grideditor.GridEditorActionContext;
import org.mwc.cmap.grideditor.GridEditorPlugin;
import org.mwc.cmap.grideditor.command.CompositeOperation;
import org.mwc.cmap.grideditor.command.OperationEnvironment;
import org.mwc.cmap.grideditor.command.SetDescriptorValueOperation;
import org.mwc.cmap.grideditor.interpolation.ItemsInterpolator;
import org.mwc.cmap.grideditor.interpolation.ItemsInterpolatorFactory;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;


public class InterpolateAction extends AbstractViewerAction {

	private static final String DEFAULT_ACTION_TEXT = "Interpolate";

	public InterpolateAction() {
		setText(DEFAULT_ACTION_TEXT);
		setToolTipText(getText());
		setImageDescriptor(loadImageDescriptor(GridEditorPlugin.IMG_INTERPOLATE_CALCULATOR));
	}

	@Override
	protected void updateActionAppearance(IUndoableOperation operation) {
		super.updateActionAppearance(operation);
		setText(operation == null ? DEFAULT_ACTION_TEXT : DEFAULT_ACTION_TEXT + " " + ((InterpolateOperation) operation).getDescriptor().getTitle());
		setToolTipText(getText());
	}

	@Override
	public IUndoableOperation createUndoableOperation(GridEditorActionContext actionContext) {
		final IUndoContext undoContext = actionContext.getUndoSupport().getUndoContext();
		final GriddableSeries series = actionContext.getTableInput();
		if (series == null) {
			return null;
		}

		final GriddableItemDescriptor descriptor = actionContext.getChartInputDescriptor();
		if (!isKnownDescriptor(series, descriptor)) {
			return null;
		}
		//contains the set of items that need to be changed (that is, that are in between base points)
		LinkedList<TimeStampedDataItem> operationSet = new LinkedList<TimeStampedDataItem>();
		//consider user have selected start at first, then end and finally middle, 
		//we have to reorder
		List<TimeStampedDataItem> reordered = new ArrayList<TimeStampedDataItem>(actionContext.getStructuredSelection().size());
		orderAccordingTheSeries(filterSelection(actionContext.getStructuredSelection()), series, reordered, operationSet);
		if (operationSet.isEmpty()) {
			return null;
		}

		ItemsInterpolatorFactory interpolatorFactory = (ItemsInterpolatorFactory) Platform.getAdapterManager().getAdapter(descriptor, ItemsInterpolatorFactory.class);
		if (interpolatorFactory == null) {
			interpolatorFactory = ItemsInterpolatorFactory.DEFAULT;
		}
		ItemsInterpolator interpolator = interpolatorFactory.createItemsInterpolator(descriptor, reordered.toArray(new TimeStampedDataItem[reordered.size()]));
		if (interpolator == null) {
			return null;
		}

		InterpolateOperation result = new InterpolateOperation(undoContext, descriptor);
		for (TimeStampedDataItem next : operationSet) {
			if (!interpolator.canInterpolate(next)) {
				return null;
			}
			Object nextValue = interpolator.getInterpolatedValue(next);
			OperationEnvironment nextContext = new OperationEnvironment(undoContext, series, next, descriptor);
			result.add(new SetDescriptorValueOperation(nextContext, nextValue));
		}
		return result;
	}

	private boolean isKnownDescriptor(GriddableSeries series, GriddableItemDescriptor descriptor) {
		if (series == null || descriptor == null) {
			return false;
		}
		return Arrays.asList(series.getAttributes()).contains(descriptor);
	}

	private static List<TimeStampedDataItem> filterSelection(IStructuredSelection selection) {
		List<TimeStampedDataItem> result = new ArrayList<TimeStampedDataItem>(selection.size());
		for (Object next : selection.toList()) {
			if (next instanceof TimeStampedDataItem) {
				result.add((TimeStampedDataItem) next);
			}
		}
		return result;
	}

	private static void orderAccordingTheSeries(List<TimeStampedDataItem> inputList, GriddableSeries series, List<TimeStampedDataItem> reorderedResult, List<TimeStampedDataItem> inBetweenOutput) {
		reorderedResult.clear();

		//we are using IdentityHashMap as a set
		final Object SOMETHING = 42;
		IdentityHashMap<TimeStampedDataItem, Object> remainings = new IdentityHashMap<TimeStampedDataItem, Object>();
		for (TimeStampedDataItem next : inputList) {
			remainings.put(next, SOMETHING);
		}
		for (TimeStampedDataItem next : series.getItems()) {
			if (remainings.containsKey(next)) {
				reorderedResult.add(next);
				remainings.remove(next);
				if (remainings.isEmpty()) {
					break;
				}
			} else if (!reorderedResult.isEmpty() && inBetweenOutput != null) {
				//this element is not an interpolation point, and its after the first interpolation point, 
				//so, its in between
				inBetweenOutput.add(next);
			}
		}
	}

	private static class InterpolateOperation extends CompositeOperation {

		private final GriddableItemDescriptor myDescriptor;

		public InterpolateOperation(IUndoContext wholeOperationUndoContext, GriddableItemDescriptor descriptor) {
			super("Interpolating " + descriptor.getTitle(), wholeOperationUndoContext);
			myDescriptor = descriptor;
		}

		public GriddableItemDescriptor getDescriptor() {
			return myDescriptor;
		}
	}

}
