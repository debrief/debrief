package org.mwc.cmap.grideditor.chart;

import java.util.Date;

import org.eclipse.core.commands.ExecutionException;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataset;
import org.mwc.cmap.grideditor.GridEditorUndoSupport;
import org.mwc.cmap.grideditor.command.CompositeOperation;
import org.mwc.cmap.grideditor.command.OperationEnvironment;
import org.mwc.cmap.grideditor.command.SetDescriptorValueOperation;
import org.mwc.cmap.grideditor.command.SetTimeStampOperation;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;


public class Date2ValueManager implements ChartDataManager {

	private final String myTitle;

	private final GriddableItemDescriptor myDescriptor;

	private final GriddableItemChartComponent myValuesComponent;

	private final TimeSeriesWithDuplicates myDuplicatesWorkaround;

	private GriddableSeries myInput;

	private DataPointsDragTracker myDragTracker;

	public Date2ValueManager(GriddableItemDescriptor descriptor, GriddableItemChartComponent valuesComponent) {
		myDescriptor = descriptor;
		myValuesComponent = valuesComponent;
		myTitle = descriptor.getTitle();
		myDuplicatesWorkaround = new TimeSeriesWithDuplicates("the-only");
	}

	public XYDataset getXYDataSet() {
		return myDuplicatesWorkaround.getDataSet();
	}

	public GriddableItemDescriptor getDescriptor() {
		return myDescriptor;
	}

	public String getChartTitle() {
		return myTitle;
	}

	public void setInput(GriddableSeries input) {
		myInput = input;
		for (TimeStampedDataItem nextItem : input.getItems()) {
			double nextValue = myValuesComponent.getDoubleValue(nextItem);
			myDuplicatesWorkaround.addDomainItem(nextItem, nextValue);
		}
	}

	public ValueAxis createXAxis() {
		DateAxis result = new DateAxis(null);
		result.setTimeZone(TimeSeriesWithDuplicates.getDefaultTimeZone());
		return result;
	}

	public ValueAxis createYAxis() {
		NumberAxis result = new NumberAxis(null);
		result.setAutoRangeIncludesZero(false);
		return result;
	}

	public void handleItemAdded(int index, TimeStampedDataItem addedItem) {
		myDuplicatesWorkaround.addDomainItem(addedItem, myValuesComponent.getDoubleValue(addedItem));
	}

	public void handleItemChanged(TimeStampedDataItem changedItem) {
		myDuplicatesWorkaround.updateDomainItem(changedItem, myValuesComponent.getDoubleValue(changedItem));
	}

	public void handleItemDeleted(int oldIndex, TimeStampedDataItem deletedItem) {
		myDuplicatesWorkaround.removeDomainItem(deletedItem);
	}

	public void attach(JFreeChartComposite chartPanel) {
		final GridEditorUndoSupport undoSupport = chartPanel.getActionContext().getUndoSupport();
		if (undoSupport != null) {
			myDragTracker = new DataPointsDragTracker(chartPanel, true) {

				@Override
				protected void dragCompleted(BackedChartItem item, double finalX, double finalY) {
					OperationEnvironment environment = new OperationEnvironment(undoSupport.getUndoContext(), myInput, item.getDomainItem(), myDescriptor);
					Date finalTime = new Date((long) finalX);
					SetTimeStampOperation setTime = new SetTimeStampOperation(environment, new HiResDate(finalTime));
					SetDescriptorValueOperation setValue = new SetDescriptorValueOperation(environment, finalY);
					CompositeOperation update = new CompositeOperation("Applying changes from chart", undoSupport.getUndoContext());
					update.add(setTime);
					update.add(setValue);
					try {
						undoSupport.getOperationHistory().execute(update, null, null);
					} catch (ExecutionException e) {
						throw new RuntimeException("[Chart]Can't set the timestamp of :" + finalTime + //
								" and/or value: " + finalY + //
								" for item " + item.getDomainItem(), e);
					}
				}
			};
			chartPanel.addChartMouseListener(myDragTracker);
		}
	}

	public void detach(JFreeChartComposite chartPanel) {
		if (myDragTracker != null) {
			chartPanel.removeChartMouseListener(myDragTracker);
			myDragTracker = null;
		}
	}

}
