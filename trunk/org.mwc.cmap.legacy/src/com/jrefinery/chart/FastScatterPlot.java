package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Iterator;
import java.util.Date;
import com.jrefinery.chart.annotations.Annotation;
import com.jrefinery.chart.annotations.XYAnnotation;

/**
 * A fast scatter plot.
 *
 * @author DG
 */
public class FastScatterPlot extends Plot {

    /** The data. */
    private float[][] data;

    /** The domain axis (used for the x-values). */
    private ValueAxis domainAxis;

    /** The range axis (used for the y-values). */
    private ValueAxis rangeAxis;

    /** A list of markers (optional) for the domain axis. */
    private List domainMarkers;

    /** A list of markers (optional) for the range axis. */
    private List rangeMarkers;

    /** A list of annotations (optional) for the plot. */
    private List annotations;

    /**
     * Creates a new fast scatter plot.
     * <P>
     * The data is an array of x, y values:  data[0][i] = x, data[1][i] = y.
     *
     * @param data  the data.
     * @param domainAxis  the domain (x) axis.
     * @param rangeAxis  the range (y) axis.
     */
    public FastScatterPlot(float[][] data, ValueAxis domainAxis, ValueAxis rangeAxis) {

        super(null,
              Plot.DEFAULT_INSETS,
              Plot.DEFAULT_BACKGROUND_PAINT,
              null,  // background image
              Plot.DEFAULT_BACKGROUND_ALPHA,
              Plot.DEFAULT_OUTLINE_STROKE,
              Plot.DEFAULT_OUTLINE_PAINT,
              Plot.DEFAULT_FOREGROUND_ALPHA);

        this.data = data;
        this.domainAxis = domainAxis;
        this.rangeAxis = rangeAxis;

    }

    /**
     * Returns the domain axis for the plot.  If the domain axis for this plot
     * is null, then the method will return the parent plot's domain axis (if
     * there is a parent plot).
     *
     * @return the domain axis.
     */
    public ValueAxis getDomainAxis() {

        return this.domainAxis;

    }

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * null, then the method will return the parent plot's range axis (if
     * there is a parent plot).
     *
     * @return the range axis.
     */
    public ValueAxis getRangeAxis() {

        return this.rangeAxis;

    }

    /**
     * Draws the fast scatter plot on a Java 2D graphics device (such as the screen or
     * a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea   the area within which the plot (including axis labels)
     *                   should be drawn.
     * @param info  collects chart drawing information (null permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // set up info collection...
        if (info != null) {
            info.setPlotArea(plotArea);

        }

        // adjust the drawing area for plot insets (if any)...
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
                             plotArea.getY() + insets.top,
                             plotArea.getWidth() - insets.left - insets.right,
                             plotArea.getHeight() - insets.top - insets.bottom);
        }

        // estimate the area required for drawing the axes...
        double hAxisAreaHeight = 0;

        if (this.domainAxis != null) {
            HorizontalAxis hAxis = (HorizontalAxis) this.domainAxis;
            hAxisAreaHeight = hAxis.reserveHeight(g2, this, plotArea);
        }

        double vAxisWidth = 0;
        if (this.rangeAxis != null) {
            VerticalAxis vAxis = (VerticalAxis) this.rangeAxis;
            vAxisWidth = vAxis.reserveAxisArea(g2, this, plotArea, hAxisAreaHeight).getWidth();
        }

        // ...and therefore what is left for the plot itself...
        Rectangle2D dataArea = new Rectangle2D.Double(plotArea.getX() + vAxisWidth,
                                                      plotArea.getY(),
                                                      plotArea.getWidth() - vAxisWidth,
                                                      plotArea.getHeight() - hAxisAreaHeight);

        if (info != null) {
            info.setDataArea(dataArea);
        }

        // draw the plot background and axes...
        drawOutlineAndBackground(g2, dataArea);

        if (this.domainAxis != null) {
            this.domainAxis.draw(g2, plotArea, dataArea);
        }
        if (this.rangeAxis != null) {
            this.rangeAxis.draw(g2, plotArea, dataArea);
        }

        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   this.foregroundAlpha));

        if (this.domainMarkers != null) {
            Iterator iterator = this.domainMarkers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                //renderer.drawDomainMarker(g2, this, getDomainAxis(), marker, dataArea);
            }
        }

        if (this.rangeMarkers != null) {
            Iterator iterator = this.rangeMarkers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                //renderer.drawRangeMarker(g2, this, getRangeAxis(), marker, dataArea);
            }
        }

        render(g2, dataArea, info, null);

        // draw the annotations...
        if (this.annotations != null) {
            Iterator iterator = this.annotations.iterator();
            while (iterator.hasNext()) {
                Annotation annotation = (Annotation) iterator.next();
                if (annotation instanceof XYAnnotation) {
                    XYAnnotation xya = (XYAnnotation) annotation;
                    // get the annotation to draw itself...
                    xya.draw(g2, dataArea, getDomainAxis(), getRangeAxis());
                }
            }
        }

        g2.setClip(originalClip);
        g2.setComposite(originalComposite);

    }

    /**
     * Draws a representation of the data within the dataArea region.
     * <P>
     * The <code>info</code> and <code>crosshairInfo</code> arguments may be <code>null</code>.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     * @param crosshairInfo  an optional object for collecting crosshair info.
     */
    public void render(Graphics2D g2, Rectangle2D dataArea,
                       ChartRenderingInfo info, CrosshairInfo crosshairInfo) {

        Date start = new Date();
        System.out.println("Start: "+start.toString());
        g2.setPaint(Color.red);
        if (this.data != null) {

            ValueAxis domainAxis = getDomainAxis();
            ValueAxis rangeAxis = getRangeAxis();
            for (int i = 0; i < data[0].length; i++) {
                float x = data[0][i];
                float y = data[1][i];
                int transX = (int) domainAxis.translateValueToJava2D(x, dataArea);
                int transY = (int) rangeAxis.translateValueToJava2D(y, dataArea);
                g2.drawRect(transX, transY, 1, 1);
            }


        }
        Date finish = new Date();
        System.out.println("Finish: "+finish.toString());

    }

    /**
     * Returns a short string describing the plot type.
     *
     * @return a short string describing the plot type.
     */
    public String getPlotType() {
        return "Fast Scatter Plot";
    }

}
