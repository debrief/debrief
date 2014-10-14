/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

public class InterpolateAction extends AbstractViewerAction
{

	private static final String DEFAULT_ACTION_TEXT = "Interpolate";

	public InterpolateAction()
	{
		setText(DEFAULT_ACTION_TEXT);
		setToolTipText(getText());
		setImageDescriptor(loadImageDescriptor(GridEditorPlugin.IMG_INTERPOLATE_CALCULATOR));
	}

	@Override
	protected void updateActionAppearance(final IUndoableOperation operation)
	{
		super.updateActionAppearance(operation);
		setText(operation == null ? DEFAULT_ACTION_TEXT : DEFAULT_ACTION_TEXT + " "
				+ ((InterpolateOperation) operation).getDescriptor().getTitle());
		setToolTipText(getText());
	}

	@Override
	public IUndoableOperation createUndoableOperation(
			final GridEditorActionContext actionContext)
	{
		final IUndoContext undoContext = actionContext.getUndoSupport()
				.getUndoContext();
		final GriddableSeries series = actionContext.getTableInput();
		if (series == null)
		{
			return null;
		}

		final GriddableItemDescriptor descriptor = actionContext
				.getChartInputDescriptor();
		if (!isKnownDescriptor(series, descriptor))
		{
			return null;
		}
		// contains the set of items that need to be changed (that is, that are in
		// between base points)
		final LinkedList<TimeStampedDataItem> operationSet = new LinkedList<TimeStampedDataItem>();
		// consider user have selected start at first, then end and finally middle,
		// we have to reorder
		final List<TimeStampedDataItem> reordered = new ArrayList<TimeStampedDataItem>(
				actionContext.getStructuredSelection().size());
		orderAccordingTheSeries(filterSelection(actionContext
				.getStructuredSelection()), series, reordered, operationSet);
		if (operationSet.isEmpty())
		{
			return null;
		}

		ItemsInterpolatorFactory interpolatorFactory = (ItemsInterpolatorFactory) Platform
				.getAdapterManager().getAdapter(descriptor,
						ItemsInterpolatorFactory.class);
		if (interpolatorFactory == null)
		{
			interpolatorFactory = ItemsInterpolatorFactory.DEFAULT;
		}
		final ItemsInterpolator interpolator = interpolatorFactory
				.createItemsInterpolator(descriptor, reordered
						.toArray(new TimeStampedDataItem[reordered.size()]));
		if (interpolator == null)
		{
			return null;
		}

		final InterpolateOperation result = new InterpolateOperation(undoContext,
				descriptor);
		for (final TimeStampedDataItem next : operationSet)
		{
			if (!interpolator.canInterpolate(next))
			{
				return null;
			}
			final Object nextValue = interpolator.getInterpolatedValue(next);
			final OperationEnvironment nextContext = new OperationEnvironment(undoContext,
					series, next, descriptor);
			result.add(new SetDescriptorValueOperation(nextContext, nextValue));
		}
		return result;
	}

	private boolean isKnownDescriptor(final GriddableSeries series,
			final GriddableItemDescriptor descriptor)
	{
		if (series == null || descriptor == null)
		{
			return false;
		}
		return Arrays.asList(series.getAttributes()).contains(descriptor);
	}

	private static List<TimeStampedDataItem> filterSelection(
			final IStructuredSelection selection)
	{
		final List<TimeStampedDataItem> result = new ArrayList<TimeStampedDataItem>(
				selection.size());
		for (final Object next : selection.toList())
		{
			if (next instanceof TimeStampedDataItem)
			{
				result.add((TimeStampedDataItem) next);
			}
		}
		return result;
	}

	private static void orderAccordingTheSeries(
			final List<TimeStampedDataItem> inputList, final GriddableSeries series,
			final List<TimeStampedDataItem> reorderedResult,
			final List<TimeStampedDataItem> inBetweenOutput)
	{
		reorderedResult.clear();

		// we are using IdentityHashMap as a set
		final Object SOMETHING = 42;
		final IdentityHashMap<TimeStampedDataItem, Object> remainings = new IdentityHashMap<TimeStampedDataItem, Object>();
		for (final TimeStampedDataItem next : inputList)
		{
			remainings.put(next, SOMETHING);
		}
		for (final TimeStampedDataItem next : series.getItems())
		{
			if (remainings.containsKey(next))
			{
				reorderedResult.add(next);
				remainings.remove(next);
				if (remainings.isEmpty())
				{
					break;
				}
			}
			else if (!reorderedResult.isEmpty() && inBetweenOutput != null)
			{
				// this element is not an interpolation point, and its after the first
				// interpolation point,
				// so, its in between
				inBetweenOutput.add(next);
			}
		}
	}

	private static class InterpolateOperation extends CompositeOperation
	{

		private final GriddableItemDescriptor myDescriptor;

		public InterpolateOperation(final IUndoContext wholeOperationUndoContext,
				final GriddableItemDescriptor descriptor)
		{
			super("Interpolating " + descriptor.getTitle(), wholeOperationUndoContext);
			myDescriptor = descriptor;
		}

		public GriddableItemDescriptor getDescriptor()
		{
			return myDescriptor;
		}
	}

}
