package MWC.GUI.JFreeChart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: Feb 5, 2003 Time: 10:59:40 AM
 * To change this template use Options | File Templates.
 */
// ////////////////////////////////////////////////
// custom renderer, which uses the specified color for the data series
// ////////////////////////////////////////////////
public final class ColourStandardXYItemRenderer extends DefaultXYItemRenderer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** A working line (to save creating thousands of instances). */
	// private Line2D workingLine = new Line2D.Double(0.0, 0.0, 0.0, 0.0);

	/**
	 * the plot whose data we're plotting
	 * 
	 */
	private XYPlot _myPlot;

	/**
	 * Constructs a new renderer.
	 * <p>
	 * To specify the type of renderer, use one of the constants: SHAPES, LINES or
	 * SHAPES_AND_LINES.
	 * 
	 * @param type
	 *          the type of renderer.
	 * @param toolTipGenerator
	 *          the tooltip generator.
	 * @param urlGenerator
	 *          the URL generator.
	 * @param plot
	 */
	public ColourStandardXYItemRenderer(
			final XYToolTipGenerator toolTipGenerator,
			final XYURLGenerator urlGenerator, XYPlot plot)
	{
		super();
		this.setBaseToolTipGenerator(toolTipGenerator);
		this.setURLGenerator(urlGenerator);
		_myPlot = plot;
	}

	public void setPlot(XYPlot thePlot)
	{
		super.setPlot(thePlot);
		_myPlot = thePlot;
	}

	@Override
	public Paint getItemPaint(int row, int column)
	{
		Color theColor = null;

		final XYDataset data = _myPlot.getDataset();

		Paint res = null;

		if (data instanceof TimeSeriesCollection)
		{
			TimeSeriesCollection tsc = (TimeSeriesCollection) data;
			// get the data series
			TimeSeries bts = tsc.getSeries(row);
			TimeSeriesDataItem tsdp = bts.getDataItem(column);
			if (tsdp instanceof AttractiveDataItem)
			{
				AttractiveDataItem cdi = (AttractiveDataItem) tsdp;
				theColor = cdi.getColor();
			}
		}

		if (theColor != null)
			res = theColor;
		else
			res = super.getItemPaint(row, column);

		return res;
	}

	/**
	 * Returns a legend item for a series.
	 * 
	 * @param series
	 *          the series (zero-based index).
	 * 
	 * @return a legend item for the series.
	 */
	public LegendItem getLegendItem(int series)
	{

		XYPlot plot = this.getPlot();

		XYDataset dataset = plot.getDataset();
		String label = (String) dataset.getSeriesKey(series);
		String description = label;
		Shape shape = null;
		Paint paint = this.getSeriesPaint(series);
		Paint outlinePaint = paint;
		Stroke stroke = plot.getRenderer().getSeriesStroke(series);

		return new LegendItem(label, description, null, null, shape, paint, stroke,
				outlinePaint);
	}

	/**
	 * accessor method to find out if we should connect this point to the previous
	 * one
	 * 
	 * @param plot
	 *          the plot (can be used to obtain standard color information etc).
	 * @param series
	 *          the series index.
	 * @param item
	 *          the item index.
	 * @return yes/no
	 */
	protected boolean connectToPrevious(XYPlot plot, final int series,
			final int item)
	{

		final XYDataset data = plot.getDataset();

		boolean res = true;

		if (data instanceof TimeSeriesCollection)
		{
			TimeSeriesCollection tsc = (TimeSeriesCollection) data;
			// get the data series
			TimeSeries bts;
			try
			{
				bts = tsc.getSeries(series);

				TimeSeriesDataItem tsdp;
				tsdp = bts.getDataItem(item);
				if (tsdp instanceof AttractiveDataItem)
				{
					AttractiveDataItem cdi = (AttractiveDataItem) tsdp;
					res = cdi.connectToPrevious();
				}
			}
			catch (IndexOutOfBoundsException ee)
			{
			//	ee.printStackTrace();
				res = false;
			}
			catch (IllegalArgumentException ee)
			{
			//	ee.printStackTrace();
				res = false;
			}
		}

		return res;
	}

	/**
	 * Draws the visual representation of a single data item.
	 * 
	 * @param g2
	 *          the graphics device.
	 * @param state
	 *          the renderer state.
	 * @param dataArea
	 *          the area within which the data is being drawn.
	 * @param info
	 *          collects information about the drawing.
	 * @param plot
	 *          the plot (can be used to obtain standard color information etc).
	 * @param domainAxis
	 *          the domain axis.
	 * @param rangeAxis
	 *          the range axis.
	 * @param dataset
	 *          the dataset.
	 * @param series
	 *          the series index (zero-based).
	 * @param item
	 *          the item index (zero-based).
	 * @param crosshairState
	 *          crosshair information for the plot (<code>null</code> permitted).
	 * @param pass
	 *          the pass index.
	 */
	public void drawItem(Graphics2D g2, XYItemRendererState state,
			Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series,
			int item, CrosshairState crosshairState, int pass)
	{

		// do nothing if item is not visible
		if (!getItemVisible(series, item))
		{
			return;
		}

		boolean connectToPrev = connectToPrevious(_myPlot, series, item);

		// first pass draws the background (lines, for instance)
		if (isLinePass(pass))
		{
			if (connectToPrev)
			{
				if (this.getDrawSeriesLineAsPath())
				{
					drawPrimaryLineAsPath(state, g2, plot, dataset, pass, series, item,
							domainAxis, rangeAxis, dataArea);
				}
				else
				{
					drawPrimaryLine(state, g2, plot, dataset, pass, series, item,
							domainAxis, rangeAxis, dataArea);
				}
			}
		}
		// second pass adds shapes where the items are ..
		else if (isItemPass(pass))
		{

			// setup for collecting optional entity info...
			EntityCollection entities = null;
			if (info != null)
			{
				entities = info.getOwner().getEntityCollection();
			}

			drawSecondaryPass(g2, plot, dataset, pass, series, item, domainAxis,
					dataArea, rangeAxis, crosshairState, entities);
		}
	}

	/**
	 * Draws the visual representation of a single data item.
	 * 
	 * @param g2
	 *          the graphics device.
	 * @param dataArea
	 *          the area within which the data is being drawn.
	 * @param info
	 *          collects information about the drawing.
	 * @param plot
	 *          the plot (can be used to obtain standard color information etc).
	 * @param domainAxis
	 *          the domain (horizontal) axis.
	 * @param rangeAxis
	 *          the range (vertical) axis.
	 * @param data
	 *          the dataset.
	 * @param series
	 *          the series index.
	 * @param item
	 *          the item index.
	 * @param crosshairInfo
	 *          information about crosshairs on a plot.
	 */
	// public void drawItem(Graphics2D g2,
	// Rectangle2D dataArea,
	// ChartRenderingInfo info,
	// XYPlot plot,
	// ValueAxis domainAxis,
	// ValueAxis rangeAxis,
	// XYDataset data,
	// int series,
	// int item,
	// CrosshairInfo crosshairInfo) {
	//
	// // setup for collecting optional entity info...
	// Shape entityArea = null;
	// EntityCollection entities = null;
	// if (info != null) {
	// entities = info.getEntityCollection();
	// }
	//
	// Paint seriesPaint = plot.getRenderer().getSeriesPaint(series);
	// Stroke seriesStroke = plot.getRenderer().getSeriesStroke(series);
	// g2.setPaint(seriesPaint);
	// g2.setStroke(seriesStroke);
	//
	// // get the data point...
	// Number x1n = data.getXValue(series, item);
	// Number y1n = data.getYValue(series, item);
	// if (y1n != null) {
	// double x1 = x1n.doubleValue();
	// double y1 = y1n.doubleValue();
	// double transX1 = domainAxis.valueToJava2D(x1, dataArea);
	// double transY1 = rangeAxis.valueToJava2D(y1, dataArea);
	//
	// Paint paint = getPaint(plot, series, item, transX1, transY1);
	// if (paint != null) {
	// g2.setPaint(paint);
	// }
	//
	// if (true) {
	// if (item > 0) {
	//
	// // find out if we're going to connect this line
	// boolean connectToPrevious = connectToPrevious(plot, series, item, transX1,
	// transY1);
	//
	// if(connectToPrevious)
	// {
	// // get the previous data point...
	// Number x0n = data.getXValue(series, item - 1);
	// Number y0n = data.getYValue(series, item - 1);
	// if (y0n != null) {
	// double x0 = x0n.doubleValue();
	// double y0 = y0n.doubleValue();
	//
	// double transX0 = domainAxis.translateValueToJava2D(x0, dataArea);
	// double transY0 = rangeAxis.translateValueToJava2D(y0, dataArea);
	//
	// workingLine.setLine(transX0, transY0, transX1, transY1);
	// if (workingLine.intersects(dataArea)) {
	// g2.draw(workingLine);
	// }
	// }
	// }
	// }
	// }
	//
	// // if (this.plotShapes) {
	// if(this.getPlotShapes())
	// {
	// double scale = getShapeScale(plot, series, item, transX1, transY1);
	// Shape shape = getShape(plot, series, item, transX1, transY1, scale);
	// if (shape.intersects(dataArea)) {
	// if (isShapeFilled(plot, series, item, transX1, transY1)) {
	// g2.fill(shape);
	// }
	// else {
	// g2.draw(shape);
	// }
	// }
	// entityArea = shape;
	//
	// }
	//
	// if (false) {
	// // if (this.plotImages) {
	// // use shape scale with transform??
	// double scale = getShapeScale(plot, series, item, transX1, transY1);
	// Image image = this.getImage(plot, series, item, transX1, transY1);
	// if (image != null) {
	// Point hotspot = getImageHotspot(plot, series, item, transX1, transY1,
	// image);
	// g2.drawImage(image,
	// (int) (transX1 - hotspot.getX()),
	// (int) (transY1 - hotspot.getY()), (ImageObserver) null);
	// }
	// // tooltipArea = image; not sure how to handle this yet
	// }
	//
	// // add an entity for the item...
	// if (entities != null) {
	// if (entityArea == null) {
	// entityArea = new Rectangle2D.Double(transX1 - 2, transY1 - 2, 4, 4);
	// }
	// String tip = "";
	// if (getToolTipGenerator() != null) {
	// tip = getToolTipGenerator().generateToolTip(data, series, item);
	// }
	// String url = null;
	// if (getURLGenerator() != null) {
	// url = getURLGenerator().generateURL(data, series, item);
	// }
	// XYItemEntity entity = new XYItemEntity(entityArea, tip, url, series, item);
	// entities.addEntity(entity);
	// }
	//
	// // do we need to update the crosshair values?
	// if (domainAxis.isCrosshairLockedOnData()) {
	// if (rangeAxis.isCrosshairLockedOnData()) {
	// // both axes
	// crosshairInfo.updateCrosshairPoint(x1, y1);
	// }
	// else {
	// // just the horizontal axis...
	// crosshairInfo.updateCrosshairX(x1);
	// }
	// }
	// else {
	// if (rangeAxis.isCrosshairLockedOnData()) {
	// // just the vertical axis...
	// crosshairInfo.updateCrosshairY(y1);
	// }
	// }
	// }
	// }

}
