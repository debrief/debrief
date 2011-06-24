package org.mwc.cmap.grideditor.chart;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;

public class Value2ValueManager implements ChartDataManager {

	private final GriddableItemDescriptor myDescriptor;

	private final String myTitle;

	private final GriddableItemChartComponent myXComponent;

	private final GriddableItemChartComponent myYComponent;

	private final ScatteredXYSeries mySeries;

	private final XYSeriesCollection myDataSet;

	private GriddableSeries myInput;

	public Value2ValueManager(GriddableItemDescriptor descriptor, GriddableItemChartComponent xComponent, GriddableItemChartComponent yComponent) {
		myDescriptor = descriptor;
		myXComponent = xComponent;
		myYComponent = yComponent;
		myTitle = descriptor.getTitle();

		mySeries = new ScatteredXYSeries("the-only-series");
		myDataSet = new XYSeriesCollection(mySeries);
	}
	
	protected GriddableSeries getInput() {
		return myInput;
	}

	public ValueAxis createXAxis() {
		return createNumberAxis();
	}

	public ValueAxis createYAxis() {
		return createNumberAxis();
	}

	private NumberAxis createNumberAxis() {
		NumberAxis result = new NumberAxis();
		result.setAutoRangeIncludesZero(false);
		return result;
	}

	public String getChartTitle() {
		return myTitle;
	}

	public GriddableItemDescriptor getDescriptor() {
		return myDescriptor;
	}

	public XYDataset getXYDataSet() {
		return myDataSet;
	}

	public void handleItemAdded(int index, TimeStampedDataItem addedItem) {
		mySeries.insertAt(index, createChartItem(addedItem));
	}

	public void handleItemChanged(TimeStampedDataItem changedItem) {
		int index = myInput.getItems().indexOf(changedItem);
		if (index < 0) {
			return;
		}

		double currentXValue = myXComponent.getDoubleValue(changedItem);
		BackedXYDataItem chartItem = (BackedXYDataItem) mySeries.getDataItem(index);
		if (chartItem.getDomainItem() != changedItem) {
			int shouldBeAt = myInput.getItems().indexOf(chartItem.getDomainItem());
			throw new IllegalStateException("domain position for element: " + changedItem + //
					" is " + index + //
					", but chart series contains " + chartItem.getDomainItem() + //
					" which should be at position: " + shouldBeAt);
		}
		if (currentXValue == chartItem.getXValue()) {
			mySeries.updateByIndex(index, myYComponent.getDoubleValue(changedItem));
		} else {
			handleItemDeleted(index, changedItem);
			handleItemAdded(index, changedItem);
		}
	}

	public void handleItemDeleted(int oldIndex, TimeStampedDataItem deletedItem) {
		mySeries.remove(oldIndex);
	}

	public void setInput(GriddableSeries input) {
		myInput = input;
		int index = 0;
		for (TimeStampedDataItem nextItem : input.getItems()) {
			handleItemAdded(index, nextItem);
			index++;
		}
	}

	public void attach(JFreeChartComposite chartPanel) {
		//
	}

	public void detach(JFreeChartComposite chartPanel) {
		//
	}

	private BackedXYDataItem createChartItem(TimeStampedDataItem domainItem) {
		double xValue = myXComponent.getDoubleValue(domainItem);
		double yValue = myYComponent.getDoubleValue(domainItem);
		return new BackedXYDataItem(xValue, yValue, domainItem);
	}

}
