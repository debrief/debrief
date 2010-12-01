package org.mwc.cmap.grideditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.mwc.cmap.grideditor.chart.ChartDataManager;
import org.mwc.cmap.grideditor.chart.Date2ValueManager;
import org.mwc.cmap.grideditor.chart.GriddableItemChartComponent;
import org.mwc.cmap.grideditor.chart.JFreeChartComposite;
import org.mwc.cmap.grideditor.data.ChartComponentFactory;
import org.mwc.cmap.grideditor.table.GridEditorTable;
import org.mwc.cmap.grideditor.table.TableModel;
import org.mwc.cmap.grideditor.table.actons.GridEditorActionGroup;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;

public class GridEditorUI extends Composite
{

	private final SashForm mySashForm;

	private final GridEditorTable myTable;

	private final JFreeChartComposite myChart;

	private GriddableSeries myInput;

	private ChartRefresher myChartRefresher;

	public GridEditorUI(Composite parent, GridEditorActionGroup actionGroup)
	{
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		mySashForm = new SashForm(this, SWT.VERTICAL);

		myTable = new GridEditorTable(mySashForm, actionGroup);
		myChart = new JFreeChartComposite(mySashForm, actionGroup.getContext(),
				myTable);

		myTable.setColumnHeaderSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (e.widget instanceof TableColumn)
				{
					updateChart((TableColumn) e.widget);
				}
			}
		});

		myChart.setVisible(false);
		myTable.setVisible(false);

		mySashForm.setWeights(new int[]
		{ 50, 50 });
	}

	public void forceTableFocus()
	{
		myTable.getTableViewer().getTable().forceFocus();
	}

	public GridEditorTable getTable()
	{
		return myTable;
	}

	public JFreeChartComposite getChart()
	{
		return myChart;
	}

	public void inputSeriesChanged(GriddableSeries input)
	{
		if (myInput == input)
		{
			return;
		}
		if (!myTable.isTrackingSelection())
		{
			return;
		}

		disposeRefresher();

		myInput = input;
		myTable.setInput(input);
		myTable.setVisible(input != null);
		myChart.setVisible(false);

		refreshSashForm();
	}

	private void updateChart(TableColumn tableColumn)
	{
		boolean shown = showChart(tableColumn);
		myChart.setVisible(shown);
		refreshSashForm();
		myChart.forceRedraw();
	}

	private void refreshSashForm()
	{
		mySashForm.layout();
		mySashForm.update();
		mySashForm.redraw();
	}

	private boolean showChart(TableColumn tableColumn)
	{
		if (myInput == null)
		{
			return false;
		}
		TableModel.ColumnBase column = myTable.getTableModel().findColumnData(
				tableColumn);
		if (column == null)
		{
			return false;
		}
		GriddableItemDescriptor descriptor = column.getDescriptor();
		if (descriptor == null)
		{
			return false;
		}
		ChartDataManager chartDataManager = createChartDataManager(descriptor);
		if (chartDataManager == null)
		{
			return false;
		}
		disposeRefresher();
		chartDataManager.setInput(myInput);
		myChartRefresher = new ChartRefresher(myInput, chartDataManager);
		myChart.setInput(chartDataManager);
		return true;
	}

	public ChartDataManager createChartDataManager(
			GriddableItemDescriptor descriptor)
	{
		// first try adapter manager - allows redefinition for some descriptor types
		ChartDataManager adapatee = (ChartDataManager) Platform.getAdapterManager()
				.getAdapter(descriptor, ChartDataManager.class);
		if (adapatee != null)
		{
			return adapatee;
		}
		if (ChartComponentFactory.isChartable(descriptor))
		{
			GriddableItemChartComponent chartable = ChartComponentFactory
					.newChartComponent(descriptor);
			return new Date2ValueManager(descriptor, chartable);
		}
		return null;
	}

	private void disposeRefresher()
	{
		if (myChartRefresher != null)
		{
			myChartRefresher.dispose();
			myChartRefresher = null;
		}
	}

	private static class ChartRefresher implements PropertyChangeListener
	{

		private ChartDataManager myChartInput;

		private GriddableSeries myData;

		public ChartRefresher(GriddableSeries data, ChartDataManager chartInput)
		{
			myData = data;
			myChartInput = chartInput;
			myData.addPropertyChangeListener(this);
		}

		public void dispose()
		{
			if (myData != null)
			{
				myData.removePropertyChangeListener(this);
				myData = null;
				myChartInput = null;
			}
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if (myChartInput == null)
			{
				return;
			}
			if (GriddableSeries.PROPERTY_DELETED.equals(evt.getPropertyName()))
			{
				myChartInput.handleItemDeleted((Integer) evt.getOldValue(),
						(TimeStampedDataItem) evt.getNewValue());
			}
			else if (GriddableSeries.PROPERTY_CHANGED.equals(evt.getPropertyName()))
			{
				myChartInput.handleItemChanged((TimeStampedDataItem) evt.getNewValue());
			}
			else if (GriddableSeries.PROPERTY_ADDED.equals(evt.getPropertyName()))
			{
				myChartInput.handleItemAdded((Integer) evt.getOldValue(),
						(TimeStampedDataItem) evt.getNewValue());
			}
		}

	}

}
