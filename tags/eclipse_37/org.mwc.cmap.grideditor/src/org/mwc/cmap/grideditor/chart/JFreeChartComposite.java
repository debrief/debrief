package org.mwc.cmap.grideditor.chart;

import javax.swing.event.EventListenerList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.swt.SWTUtils;
import org.mwc.cmap.grideditor.GridEditorActionContext;
import org.mwc.cmap.grideditor.table.GridEditorTable;

public class JFreeChartComposite extends FixedChartComposite
{

	private final GridEditorActionContext myActionContext;

	private EventListenerList myListenerExtensions;

	private ChartDataManager myInput;

	private final GridEditorTable _dataGrid;

	public JFreeChartComposite(Composite parent,
			GridEditorActionContext actionContext, GridEditorTable dataGrid)
	{
		// next: double-buffer the chart, so when we switch back to Debrief from another app we don't have 
		// to wait for it to get redrawn
		super(parent, SWT.BORDER, null, true);
		myActionContext = actionContext;
		_dataGrid = dataGrid;
	}

	public GridEditorActionContext getActionContext()
	{
		return myActionContext;
	}

	@Override
	public void addChartMouseListener(ChartMouseListener listener)
	{
		super.addChartMouseListener(listener);
		if (listener instanceof ChartMouseListenerExtension)
		{
			if (myListenerExtensions == null)
			{
				myListenerExtensions = new EventListenerList();
			}
			myListenerExtensions.add(ChartMouseListenerExtension.class,
					(ChartMouseListenerExtension) listener);
		}
	}

	public void setInput(ChartDataManager input)
	{
		if (myInput != null)
		{
			myInput.detach(this);
			myInput = null;
		}
		myInput = input;
		myInput.attach(this);

		ChartBuilder builder = new ChartBuilder(input);
		JFreeChart chart = builder.buildChart();
		setChart(chart);
		redraw();
		myActionContext.setChartInput(input);
	}

	@Override
	public void mouseUp(MouseEvent event)
	{
		Object[] listeners = myListenerExtensions
				.getListeners(ChartMouseListenerExtension.class);
		if (listeners.length != 0)
		{
			// pass mouse down event if some ChartMouseListener are listening
			java.awt.event.MouseEvent awtEvent = SWTUtils.toAwtMouseEvent(event);
			ChartMouseEvent chartEvent = new ChartMouseEvent(getChart(), awtEvent,
					null);
			for (int i = listeners.length - 1; i >= 0; i -= 1)
			{
				((ChartMouseListenerExtension) listeners[i])
						.chartMouseReleased(chartEvent);
			}
			if (awtEvent.isConsumed())
			{
				forgetZoomPoints();
				return;
			}
		}
		super.mouseUp(event);
	}

	@Override
	public void mouseDoubleClick(MouseEvent event)
	{
		Rectangle scaledDataArea = getScreenDataArea(event.x, event.y);
		if (scaledDataArea == null)
			return;
		int x = (int) ((event.x - getClientArea().x) / getScaleX());
		int y = (int) ((event.y - getClientArea().y) / getScaleY());
		x = (int) ((event.x - getClientArea().x));
		y = (int) ((event.y - getClientArea().y));

		if (this.getChartRenderingInfo() != null)
		{
			EntityCollection entities = this.getChartRenderingInfo()
					.getEntityCollection();
			if (entities != null)
			{
				for (Object next : entities.getEntities())
				{
					ChartEntity nextEntity = (ChartEntity) next;
					if (false == nextEntity instanceof XYItemEntity)
					{
						continue;
					}

					if (nextEntity.getArea().contains(x, y))
					{
						// sort out it's details
						XYItemEntity xyEntity = (XYItemEntity) nextEntity;
						int theIndex = 0;
						if (xyEntity.getDataset() instanceof XYSeriesCollection)
						{
							theIndex = xyEntity.getItem();


//							BackedChartItem backedChartItem;
//							XYSeries series = ((XYSeriesCollection) xyEntity.getDataset())
//									.getSeries(seriesKey);
//							XYDataItem dataItem = series.getDataItem(xyEntity.getItem());
//							if (dataItem instanceof BackedChartItem)
//							{
//								backedChartItem = (BackedChartItem) dataItem;
//							}
						}
						else if (xyEntity.getDataset() instanceof TimeSeriesCollection)
						{
							TimeSeriesCollection theDataset = (TimeSeriesCollection) xyEntity
									.getDataset();
							TimeSeries theSeries = theDataset.getSeries(xyEntity
									.getSeriesIndex());
							theIndex = xyEntity.getItem();
							int itemCount = theSeries.getItemCount();
							// the items are in reverse order. reverse the index
							theIndex = itemCount - (theIndex + 1);

							// TimeSeries series = ((TimeSeriesCollection)
							// xyEntity.getDataset())
							// .getSeries(seriesKey);
							// TimeSeriesDataItem dataItem = series
							// .getDataItem(xyEntity.getItem());
							// if (dataItem instanceof BackedChartItem)
							// {
							// backedChartItem = (BackedChartItem) dataItem;
							// }
						}


						// clear the selection, as long as ctrl isn't selected
						if ((event.stateMask & SWT.CTRL) != 0)
						{
							// control is selected, so we don't want to clear the selection
						}
						else if ((event.stateMask & SWT.SHIFT) != 0)
						{
							// shift is selected, so we want to extend the selection
						}
						else
						{
							// we're not extending the selection, clear it,
							_dataGrid.getTableViewer().getTable().deselectAll();
						}

						// try to select it
						_dataGrid.getTableViewer().getTable().select(theIndex);

						// and make sure it's visible
						_dataGrid.getTableViewer().getTable().showSelection();

						// and tell the action buttons what's happened
						_dataGrid.getActionContext().setSelection(
								_dataGrid.getTableViewer().getSelection());

						break;
					}
				}
			}
		}
	}
}
