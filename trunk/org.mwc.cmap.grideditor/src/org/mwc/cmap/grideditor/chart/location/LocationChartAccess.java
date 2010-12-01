package org.mwc.cmap.grideditor.chart.location;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.mwc.cmap.grideditor.GridEditorUndoSupport;
import org.mwc.cmap.grideditor.chart.BackedChartItem;
import org.mwc.cmap.grideditor.chart.ChartDataManager;
import org.mwc.cmap.grideditor.chart.DataPointsDragTracker;
import org.mwc.cmap.grideditor.chart.GriddableItemChartComponent;
import org.mwc.cmap.grideditor.chart.JFreeChartComposite;
import org.mwc.cmap.grideditor.chart.Value2ValueManager;
import org.mwc.cmap.grideditor.command.BeanUtil;
import org.mwc.cmap.grideditor.command.OperationEnvironment;
import org.mwc.cmap.grideditor.command.SetDescriptorValueOperation;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptorExtension;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.WorldLocation;


public class LocationChartAccess implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (false == adaptableObject instanceof GriddableItemDescriptorExtension) {
			return null;
		}
		if (!ChartDataManager.class.isAssignableFrom(adapterType)) {
			return null;
		}
		GriddableItemDescriptorExtension descriptor = (GriddableItemDescriptorExtension) adaptableObject;
		if (WorldLocation.class.isAssignableFrom(descriptor.getType())) {
			return createLocationChart(descriptor);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { ChartDataManager.class };
	}

	private ChartDataManager createLocationChart(final GriddableItemDescriptor descriptor) {
		return new Longitude2LatitudeChartManager(descriptor);
	}

	private static class Longitude2LatitudeChartManager extends Value2ValueManager {

		private DataPointsDragTracker myDragTracker;

		public Longitude2LatitudeChartManager(GriddableItemDescriptor descriptor) {
			super(descriptor, createLatitudeAccessor(descriptor), createLongitudeAccessor(descriptor));
		}

		@Override
		public void attach(JFreeChartComposite chartPanel) {
			super.attach(chartPanel);
			final GridEditorUndoSupport undoSupport = chartPanel.getActionContext().getUndoSupport();
			if (undoSupport != null) {
				myDragTracker = new DataPointsDragTracker(chartPanel, false) {

					@Override
					protected void dragCompleted(BackedChartItem item, double finalX, double finalY) {
						OperationEnvironment environment = new OperationEnvironment(undoSupport.getUndoContext(), getInput(), item.getDomainItem(), getDescriptor());
						WorldLocation location = new WorldLocation(finalX, finalY,0);
						SetDescriptorValueOperation setLocation = new SetDescriptorValueOperation(environment, location);
						try {
							undoSupport.getOperationHistory().execute(setLocation, null, null);
						} catch (ExecutionException e) {
							throw new RuntimeException("Can't set the location of :" + location + //
									" for item " + item.getDomainItem(), e);
						}
					}
				};
				chartPanel.addChartMouseListener(myDragTracker);
			}
		}

		@Override
		public void detach(JFreeChartComposite chartPanel) {
			if (myDragTracker != null) {
				chartPanel.removeChartMouseListener(myDragTracker);
				myDragTracker = null;
			}
		}

		private static GriddableItemChartComponent createLatitudeAccessor(final GriddableItemDescriptor descriptor) {
			return new GriddableItemChartComponent() {

				public double getDoubleValue(TimeStampedDataItem dataItem) {
					WorldLocation location = BeanUtil.getItemValue(dataItem, descriptor, WorldLocation.class);
					return location == null ? 0 : location.getLat();
				}
			};
		}

		private static GriddableItemChartComponent createLongitudeAccessor(final GriddableItemDescriptor descriptor) {
			return new GriddableItemChartComponent() {

				public double getDoubleValue(TimeStampedDataItem dataItem) {
					WorldLocation location = BeanUtil.getItemValue(dataItem, descriptor, WorldLocation.class);
					return location == null ? 0 : location.getLong();
				}
			};
		}
	}
}
