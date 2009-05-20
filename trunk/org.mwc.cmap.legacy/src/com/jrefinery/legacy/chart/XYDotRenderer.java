package com.jrefinery.legacy.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import com.jrefinery.legacy.data.XYDataset;

public class XYDotRenderer extends AbstractXYItemRenderer
                           implements XYItemRenderer {

    /**
     * Constructs a new renderer.
     */
    public XYDotRenderer() {

    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param data  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param crosshairInfo  information about crosshairs on a plot.
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset data,
                         int series,
                         int item,
                         CrosshairInfo crosshairInfo) {

        Paint seriesPaint = plot.getSeriesPaint(series);
        Stroke seriesStroke = plot.getSeriesStroke(series);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        Number xn = data.getXValue(series, item);
        Number yn = data.getYValue(series, item);
        if (yn != null) {
            double x = xn.doubleValue();
            double y = yn.doubleValue();
            double transX = domainAxis.translateValueToJava2D(x, dataArea);
            double transY = rangeAxis.translateValueToJava2D(y, dataArea);

            g2.drawRect((int) transX, (int) transY, 1, 1);

            // do we need to update the crosshair values?
            if (domainAxis.isCrosshairLockedOnData()) {
                if (rangeAxis.isCrosshairLockedOnData()) {
                    // both axes
                    crosshairInfo.updateCrosshairPoint(x, y);
                }
                else {
                    // just the horizontal axis...
                    crosshairInfo.updateCrosshairX(x);
                }
            }
            else {
                if (rangeAxis.isCrosshairLockedOnData()) {
                    // just the vertical axis...
                    crosshairInfo.updateCrosshairY(y);
                }
            }
        }

    }

}
