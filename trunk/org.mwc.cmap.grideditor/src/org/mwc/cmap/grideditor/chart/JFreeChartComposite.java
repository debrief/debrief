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
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.swt.SWTUtils;
import org.mwc.cmap.grideditor.GridEditorActionContext;


public class JFreeChartComposite extends FixedChartComposite {

	private final GridEditorActionContext myActionContext;

	private EventListenerList myListenerExtensions;

	private ChartDataManager myInput;

	public JFreeChartComposite(Composite parent, GridEditorActionContext actionContext) {
		super(parent, SWT.BORDER);
		myActionContext = actionContext;
	}
	
	public GridEditorActionContext getActionContext() {
		return myActionContext;
	}

	@Override
	public void addChartMouseListener(ChartMouseListener listener) {
		super.addChartMouseListener(listener);
		if (listener instanceof ChartMouseListenerExtension) {
			if (myListenerExtensions == null) {
				myListenerExtensions = new EventListenerList();
			}
			myListenerExtensions.add(ChartMouseListenerExtension.class, (ChartMouseListenerExtension) listener);
		}
	}

	public void setInput(ChartDataManager input) {
		if (myInput != null) {
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
	public void mouseUp(MouseEvent event) {
		Object[] listeners = myListenerExtensions.getListeners(ChartMouseListenerExtension.class);
		if (listeners.length != 0) {
			// pass mouse down event if some ChartMouseListener are listening
			java.awt.event.MouseEvent awtEvent = SWTUtils.toAwtMouseEvent(event);
			ChartMouseEvent chartEvent = new ChartMouseEvent(getChart(), awtEvent, null);
			for (int i = listeners.length - 1; i >= 0; i -= 1) {
				((ChartMouseListenerExtension) listeners[i]).chartMouseReleased(chartEvent);
			}
			if (awtEvent.isConsumed()) {
				forgetZoomPoints();
				return;
			}
		}
		super.mouseUp(event);
	}

	@Override
	public void mouseDoubleClick(MouseEvent event) {
		Rectangle scaledDataArea = getScreenDataArea(event.x, event.y);
		if (scaledDataArea == null)
			return;
		int x = (int) ((event.x - getClientArea().x) / getScaleX());
		int y = (int) ((event.y - getClientArea().y) / getScaleY());
		x = (int) ((event.x - getClientArea().x));
		y = (int) ((event.y - getClientArea().y));

		if (this.getChartRenderingInfo() != null) {
			EntityCollection entities = this.getChartRenderingInfo().getEntityCollection();
			if (entities != null) {
				System.err.println("Searching for : x: " + x + ", y: " + y);
				for (Object next : entities.getEntities()) {
					ChartEntity nextEntity = (ChartEntity) next;
					if (false == nextEntity instanceof XYItemEntity) {
						continue;
					}
					XYItemEntity xyEntity = (XYItemEntity) nextEntity;
					System.err.println("nextEntity: " + nextEntity);
					@SuppressWarnings("unchecked")
					Comparable seriesKey = xyEntity.getDataset().getSeriesKey(xyEntity.getSeriesIndex());
					BackedChartItem backedChartItem = null;
					if (xyEntity.getDataset() instanceof XYSeriesCollection) {
						XYSeries series = ((XYSeriesCollection) xyEntity.getDataset()).getSeries(seriesKey);
						XYDataItem dataItem = series.getDataItem(xyEntity.getItem());
						System.err.println("nextItem: " + dataItem);
						if (dataItem instanceof BackedChartItem) {
							backedChartItem = (BackedChartItem) dataItem;
						}
					} else if (xyEntity.getDataset() instanceof TimeSeriesCollection) {
						TimeSeries series = ((TimeSeriesCollection) xyEntity.getDataset()).getSeries(seriesKey);
						TimeSeriesDataItem dataItem = series.getDataItem(xyEntity.getItem());
						System.err.println("nextItem: period: " + dataItem.getPeriod() + ", value: " + dataItem.getValue());
						if (dataItem instanceof BackedChartItem) {
							backedChartItem = (BackedChartItem) dataItem;
						}
					}

					if (backedChartItem != null) {
						System.err.println("\tdomainItem: " + backedChartItem.getDomainItem());
					}

					System.err.println("area:" + nextEntity.getArea());
					System.err.println("area-bounds:" + nextEntity.getArea().getBounds());
					System.err.println("contains :" + nextEntity.getArea().contains(x, y));
					if (nextEntity.getArea().contains(x, y)) {
						System.err.println("FOUND!");
						break;
					}
				}
			}
		}
	}
}
