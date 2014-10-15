/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.grideditor.chart;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.graphics.Rectangle;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

public abstract class DataPointsDragTracker implements
		ChartMouseListenerExtension
{

	private final JFreeChartComposite myChartPanel;

	private final DragSubject myDragSubject;

	private final boolean myAllowVerticalMovesOnly;

	protected abstract void dragCompleted(BackedChartItem item, double finalX,
			double finalY);

	public DataPointsDragTracker(final JFreeChartComposite chartPanel,
			final boolean allowYOnly)
	{
		myChartPanel = chartPanel;
		myAllowVerticalMovesOnly = allowYOnly;
		myDragSubject = new DragSubject();
	}

	public void chartMouseClicked(final ChartMouseEvent event)
	{
		myDragSubject.setSubject(event.getEntity());
		myChartPanel.redrawCanvas();
	}

	public void chartMouseMoved(final ChartMouseEvent event)
	{
		if (!myDragSubject.isEmpty())
		{
			myChartPanel.forgetZoomPoints();

			// Rectangle clientArea = myChartPanel.getClientArea();
			// int screenX = event.getTrigger().getX() - clientArea.x;
			// int screenY = event.getTrigger().getY() - clientArea.y;

			// [IM] don't bother with sorting out the client area offset
			// - we've stopped using it in the FixedChartComposite calling method
			final int screenX = event.getTrigger().getX();
			final int screenY = event.getTrigger().getY();

			// deliberately switch axes for following line, now that we've switched
			// the axes to put time
			// down the LH side.
			final Point2D point2d = new Point2D.Double(screenY, screenX);
			final XYPlot xyplot = myChartPanel.getChart().getXYPlot();
			final ChartRenderingInfo renderingInfo = myChartPanel.getChartRenderingInfo();
			Rectangle2D dataArea = renderingInfo.getPlotInfo().getDataArea();

			// WORKAROUND: when the grid graph gets really wide, the labels on the
			// y-axis get stretched.
			// but, the dataArea value doesn't reflect this.
			// So, get the width values from the getScreenDataArea method - which
			// does reflect the scaling applied to the y axis.
			// - and all works well now.
			final Rectangle dataArea2 = myChartPanel.getScreenDataArea();
			dataArea = new Rectangle2D.Double(dataArea2.x, dataArea.getY(),
					dataArea2.width, dataArea.getHeight());

			final ValueAxis domainAxis = xyplot.getDomainAxis();
			final RectangleEdge domainEdge = xyplot.getDomainAxisEdge();
			final ValueAxis valueAxis = xyplot.getRangeAxis();
			final RectangleEdge valueEdge = xyplot.getRangeAxisEdge();
			double domainX = domainAxis.java2DToValue(point2d.getX(), dataArea,
					domainEdge);
			final double domainY = valueAxis.java2DToValue(point2d.getY(), dataArea,
					valueEdge);

			if (myAllowVerticalMovesOnly)
			{
				domainX = myDragSubject.getDraggedItem().getXValue();
			}

			if (!myDragSubject.isEmpty())
				myDragSubject.setProposedValues(domainX, domainY);
			myChartPanel.redrawCanvas();
		}
	}

	public void chartMouseReleased(final ChartMouseEvent event)
	{
		if (!myDragSubject.isEmpty() && myDragSubject.getLastDomainPoint() != null)
		{
			try
			{
				final Point2D finalPoint = myDragSubject.getLastDomainPoint();
				dragCompleted(myDragSubject.getDraggedItem(), finalPoint.getX(),
						finalPoint.getY());
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}
		if (!myDragSubject.isEmpty())
		{
			myDragSubject.setSubject(null);
			myChartPanel.redrawCanvas();
		}
	}

	private RendererWithDynamicFeedback getFeedbackRenderer()
	{
		if (myChartPanel.getChart() == null)
		{
			return null;
		}
		final XYItemRenderer renderer = myChartPanel.getChart().getXYPlot()
				.getRenderer(0);
		return renderer instanceof RendererWithDynamicFeedback ? (RendererWithDynamicFeedback) renderer
				: null;
	}

	private class DragSubject
	{

		private BackedChartItem myDraggedItem;

		private XYItemEntity myDraggedEntity;

		private Point2D.Double myLastDomainPoint;

		public void setSubject(final ChartEntity chartEntity)
		{
			clear();
			if (chartEntity instanceof XYItemEntity)
			{
				myDraggedEntity = (XYItemEntity) chartEntity;
				myDraggedItem = extractBackedChartItem(myDraggedEntity);
				if (myDraggedItem == null)
				{
					clear();
				}
			}

			final RendererWithDynamicFeedback renderer = getFeedbackRenderer();
			if (renderer != null)
			{
				renderer.setFeedbackSubject(myDraggedEntity);
				renderer.setFeedBackValue(null);
			}
		}

		public void setProposedValues(final double x, final double y)
		{
			if (isEmpty())
			{
				return;
			}

			final RendererWithDynamicFeedback renderer = getFeedbackRenderer();
			if (renderer == null)
			{
				return;
			}
			if (myLastDomainPoint == null)
			{
				myLastDomainPoint = new Point2D.Double();
			}
			myLastDomainPoint.setLocation(x, y);
			renderer.setFeedBackValue((Point2D.Double) myLastDomainPoint.clone());
		}

		public BackedChartItem getDraggedItem()
		{
			return myDraggedItem;
		}

		@SuppressWarnings("unused")
		public XYItemEntity getDraggedEntity()
		{
			return myDraggedEntity;
		}

		public boolean isEmpty()
		{
			return myDraggedEntity == null;
		}

		private void clear()
		{
			myDraggedEntity = null;
			myDraggedItem = null;
			myLastDomainPoint = null;
		}

		public Point2D.Double getLastDomainPoint()
		{
			return myLastDomainPoint;
		}

		@SuppressWarnings("rawtypes")
		private BackedChartItem extractBackedChartItem(final XYItemEntity xyEntity)
		{
			final Comparable seriesKey = xyEntity.getDataset().getSeriesKey(
					xyEntity.getSeriesIndex());
			if (xyEntity.getDataset() instanceof XYSeriesCollection)
			{
				final XYSeries series = ((XYSeriesCollection) xyEntity.getDataset())
						.getSeries(seriesKey);
				final XYDataItem dataItem = series.getDataItem(xyEntity.getItem());
				if (dataItem instanceof BackedChartItem)
				{
					return (BackedChartItem) dataItem;
				}
			}
			else if (xyEntity.getDataset() instanceof TimeSeriesCollection)
			{
				final TimeSeries series = ((TimeSeriesCollection) xyEntity.getDataset())
						.getSeries(seriesKey);
				final TimeSeriesDataItem dataItem = series.getDataItem(xyEntity.getItem());
				if (dataItem instanceof BackedChartItem)
				{
					return (BackedChartItem) dataItem;
				}
			}
			return null;
		}

	}

}
