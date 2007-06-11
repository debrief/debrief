/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ---------------
 * ChartPanel.java
 * ---------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Søren Caspersen;
 *                   Jonathan Nash;
 *                   Hans-Jurgen Greiner;
 *                   Andreas Schneider;
 *                   Daniel van Enckevort;
 *
 * $Id: ChartPanel.java,v 1.2 2007/01/03 15:14:32 ian.mayo Exp $
 *
 * Changes (from 28-Jun-2001)
 * --------------------------
 * 28-Jun-2001 : Integrated buffering code contributed by Søren Caspersen (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 22-Nov-2001 : Added scaling to improve display of charts in small sizes (DG);
 * 26-Nov-2001 : Added property editing, saving and printing (DG);
 * 11-Dec-2001 : Transferred saveChartAsPNG method to new ChartUtilities class (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash.
 *               Renamed the tooltips class (DG);
 * 23-Jan-2002 : Implemented zooming based on code by Hans-Jurgen Greiner (DG);
 * 05-Feb-2002 : Improved tooltips setup.  Renamed method attemptSaveAs()-->doSaveAs() and made
 *               it public rather than private (DG);
 * 28-Mar-2002 : Added a new constructor (DG);
 * 09-Apr-2002 : Changed initialisation of tooltip generation, as suggested by Hans-Jurgen
 *               Greiner (DG);
 * 27-May-2002 : New interactive zooming methods based on code by Hans-Jurgen Greiner. Renamed
 *               JFreeChartPanel --> ChartPanel, moved constants to ChartPanelConstants
 *               interface (DG);
 * 31-May-2002 : Fixed a bug with interactive zooming and added a way to control if the
 *               zoom rectangle is filled in or drawn as an outline. A mouse drag
 *               gesture towards the top left now causes an autoRangeBoth() and is
 *               a way to undo zooms (AS);
 * 11-Jun-2002 : Reinstated handleClick method call in mouseClicked(...) to get crosshairs
 *               working again (DG);
 * 13-Jun-2002 : Added check for null popup menu in mouseDragged method (DG);
 * 18-Jun-2002 : Added get/set methods for minimum and maximum chart dimensions (DG);
 * 25-Jun-2002 : Removed redundant code (DG);
 * 27-Aug-2002 : Added get/set methods for popup menu (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import com.jrefinery.chart.event.ChartChangeEvent;
import com.jrefinery.chart.event.ChartChangeListener;
import com.jrefinery.chart.ui.ChartPropertyEditPanel;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.ChartEntity;
import com.jrefinery.ui.ExtensionFileFilter;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A Swing GUI component for displaying a JFreeChart.
 * <P>
 * The panel registers with the chart to receive notification of changes to any component of the
 * chart.  The chart is redrawn automatically whenever this notification is received.
 *
 * @author DG
 */
public class ChartPanel extends JPanel implements ChartPanelConstants,
                                                  ChartChangeListener,
                                                  ActionListener,
                                                  MouseListener,
                                                  MouseMotionListener,
                                                  Printable {

    /** The chart that is displayed in the panel. */
    private JFreeChart chart;

    /** Storage for registered (chart) mouse listeners. */
    private List chartMouseListeners;

    /** A flag that controls whether or not the off-screen buffer is used. */
    private boolean useBuffer;

    /** A flag that indicates that the buffer should be refreshed. */
    private boolean refreshBuffer;

    /** A buffer for the rendered chart. */
    private Image chartBuffer;

    /** The height of the chart buffer. */
    private int chartBufferHeight;

    /** The width of the chart buffer. */
    private int chartBufferWidth;

    /** The minimum width for drawing a chart (uses scaling for smaller widths). */
    private int minimumDrawWidth;

    /** The minimum height for drawing a chart (uses scaling for smaller
     *  heights).
     */
    private int minimumDrawHeight;

    /** The maximum width for drawing a chart (uses scaling for bigger widths). */
    private int maximumDrawWidth;

    /** The maximum height for drawing a chart (uses scaling for bigger heights). */
    private int maximumDrawHeight;

    /** The popup menu for the frame. */
    private JPopupMenu popup;

    /** The drawing info collected the last time the chart was drawn. */
    private ChartRenderingInfo info;

    /** The scale factor used to draw the chart. */
    private double scaleX;

    /** The scale factor used to draw the chart. */
    private double scaleY;

    /** The zoom rectangle (selected by the user with the mouse). */
    private Rectangle2D zoomRectangle = null;

    /** The zoom rectangle starting point (selected by the user with a mouse
     *  click)
     */
    private Point2D zoomPoint = null;

    /** Controls if the zoom rectangle is drawn as an outline or filled. */
    private boolean fillZoomRectangle = false;

    /** A flag that controls whether or not horizontal zooming is enabled. */
    private boolean horizontalZoom = false;

    /** A flag that controls whether or not vertical zooming is enabled. */
    private boolean verticalZoom = false;

    /** A flag that controls whether or not horizontal tracing is enabled. */
    private boolean horizontalAxisTrace = false;

    /** A flag that controls whether or not vertical tracing is enabled. */
    private boolean verticalAxisTrace = false;

    /** Menu item for zooming in on a chart (both axes). */
    private JMenuItem zoomInBothAxesMenuItem;

    /** Menu item for zooming in on a chart (horizontal axis). */
    private JMenuItem zoomInHorizontalMenuItem;

    /** Menu item for zooming in on a chart (vertical axis). */
    private JMenuItem zoomInVerticalMenuItem;

    /** Menu item for zooming out on a chart. */
    private JMenuItem zoomOutBothMenuItem;

    /** Menu item for zooming out on a chart (horizontal axis). */
    private JMenuItem zoomOutHorizontalMenuItem;

    /** Menu item for zooming out on a chart (vertical axis). */
    private JMenuItem zoomOutVerticalMenuItem;

    /** Menu item for resetting the zoom (both axes). */
    private JMenuItem autoRangeBothMenuItem;

    /** Menu item for resetting the zoom (horizontal axis only). */
    private JMenuItem autoRangeHorizontalMenuItem;

    /** Menu item for resetting the zoom (vertical axis only). */
    private JMenuItem autoRangeVerticalMenuItem;

    /** A vertical trace line. */
    private Line2D verticalTraceLine;

    /** A horizontal trace line. */
    private Line2D horizontalTraceLine;

    /**
     * Constructs a JFreeChart panel.
     *
     * @param chart  the chart.
     */
    public ChartPanel(JFreeChart chart) {

        this(chart,
             DEFAULT_WIDTH,
             DEFAULT_HEIGHT,
             DEFAULT_MINIMUM_DRAW_WIDTH,
             DEFAULT_MINIMUM_DRAW_HEIGHT,
             DEFAULT_MAXIMUM_DRAW_WIDTH,
             DEFAULT_MAXIMUM_DRAW_HEIGHT,
             DEFAULT_BUFFER_USED,
             true,  // properties
             true,  // save
             true,  // print
             true,  // zoom
             true   // tooltips
             );

    }

    /**
     * Constructs a panel containing a chart.
     *
     * @param chart  the chart.
     * @param useBuffer  a flag controlling whether or not an off-screen buffer is used.
     */
    public ChartPanel(JFreeChart chart, boolean useBuffer) {

        this(chart,
             DEFAULT_WIDTH,
             DEFAULT_HEIGHT,
             DEFAULT_MINIMUM_DRAW_WIDTH,
             DEFAULT_MINIMUM_DRAW_HEIGHT,
             DEFAULT_MAXIMUM_DRAW_WIDTH,
             DEFAULT_MAXIMUM_DRAW_HEIGHT,
             useBuffer,
             true,  // properties
             true,  // save
             false,  // print - Dec 06 - was true. Print was intermittent, so disable.
             true,  // zoom
             true   // tooltips
             );

    }

    /**
     * Constructs a JFreeChart panel.
     *
     * @param chart  the chart.
     * @param properties  a flag indicating whether or not the chart property
     *                    editor should be available via the popup menu.
     * @param save  a flag indicating whether or not save options should be
     *              available via the popup menu.
     * @param print  a flag indicating whether or not the print option
     *               should be available via the popup menu.
     * @param zoom  a flag indicating whether or not zoom options should
     *              be added to the popup menu.
     * @param tooltips  a flag indicating whether or not tooltips should be
     *                  enabled for the chart.
     */
    public ChartPanel(JFreeChart chart,
                      boolean properties,
                      boolean save,
                      boolean print,
                      boolean zoom,
                      boolean tooltips) {

        this(chart,
             DEFAULT_WIDTH,
             DEFAULT_HEIGHT,
             DEFAULT_MINIMUM_DRAW_WIDTH,
             DEFAULT_MINIMUM_DRAW_HEIGHT,
             DEFAULT_MAXIMUM_DRAW_WIDTH,
             DEFAULT_MAXIMUM_DRAW_HEIGHT,
//             DEFAULT_BUFFER_USED,
             true,
             properties,
             save,
             print,
             zoom,
             tooltips
             );

    }

    /**
     * Constructs a JFreeChart panel.
     *
     * @param chart  the chart.
     * @param width  the preferred width of the panel.
     * @param height  the preferred height of the panel.
     * @param minimumDrawWidth  the minimum drawing width.
     * @param minimumDrawHeight  the minimum drawing height.
     * @param maximumDrawWidth  the maximum drawing width.
     * @param maximumDrawHeight  the maximum drawing height.
     * @param useBuffer  a flag that indicates whether to use the off-screen
     *                   buffer to improve performance (at the expense of memory).
     * @param properties  a flag indicating whether or not the chart property
     *                    editor should be available via the popup menu.
     * @param save  a flag indicating whether or not save options should be
     *              available via the popup menu.
     * @param print  a flag indicating whether or not the print option
     *               should be available via the popup menu.
     * @param zoom  a flag indicating whether or not zoom options should be added to the
     *              popup menu.
     * @param tooltips  a flag indicating whether or not tooltips should be enabled for the chart.
     */
    public ChartPanel(JFreeChart chart,
                      int width,
                      int height,
                      int minimumDrawWidth,
                      int minimumDrawHeight,
                      int maximumDrawWidth,
                      int maximumDrawHeight,
                      boolean useBuffer,
                      boolean properties,
                      boolean save,
                      boolean print,
                      boolean zoom,
                      boolean tooltips) {

        this.chart = chart;
        this.chartMouseListeners = new java.util.ArrayList();
        this.info = new ChartRenderingInfo();
        setPreferredSize(new Dimension(width, height));
        this.useBuffer = useBuffer;
        this.refreshBuffer = false;
        this.chart.addChangeListener(this);
        this.minimumDrawWidth = minimumDrawWidth;
        this.minimumDrawHeight = minimumDrawHeight;
        this.maximumDrawWidth = maximumDrawWidth;
        this.maximumDrawHeight = maximumDrawHeight;

        // set up popup menu...
        this.popup = null;
        if (properties || save || print || zoom) {
            popup = this.createPopupMenu(properties, save, print, zoom);
        }

        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setGenerateToolTips(tooltips);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        //this.setOpaque(true);

    }

    /**
     * Returns the chart contained in the panel.
     *
     * @return The chart.
     */
    public JFreeChart getChart() {
        return chart;
    }

    /**
     * Sets the chart that is displayed in the panel.
     *
     * @param chart  The chart.
     */
    public void setChart(JFreeChart chart) {

        // stop listening for changes to the existing chart...
        if (this.chart != null) {
            this.chart.removeChangeListener(this);
        }

        // add the new chart...
        this.chart = chart;
        this.chart.addChangeListener(this);
        if (this.useBuffer) {
            this.refreshBuffer = true;
        }
        Plot plot = chart.getPlot();
        this.horizontalZoom = this.horizontalZoom && (plot instanceof HorizontalValuePlot);
        this.verticalZoom = this.verticalZoom && (plot instanceof VerticalValuePlot);
        repaint();

    }

    /**
     * Returns the minimum drawing width for charts.
     * <P>
     * If the width available on the panel is less than this, then the chart is
     * drawn at the minimum width then scaled down to fit.
     *
     * @return The minimum drawing width.
     */
    public int getMinimumDrawWidth() {
        return this.minimumDrawWidth;
    }

    /**
     * Sets the minimum drawing width for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available width is
     * less than this amount, the chart will be drawn using the minimum width
     * then scaled down to fit the available space.
     *
     * @param width  The width.
     */
    public void setMinimumDrawWidth(int width) {
        this.minimumDrawWidth = width;
    }

    /**
     * Returns the maximum drawing width for charts.
     * <P>
     * If the width available on the panel is greater than this, then the chart
     * is drawn at the maximum width then scaled up to fit.
     *
     * @return The maximum drawing width.
     */
    public int getMaximumDrawWidth() {
        return this.maximumDrawWidth;
    }

    /**
     * Sets the maximum drawing width for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available width is
     * greater than this amount, the chart will be drawn using the maximum
     * width then scaled up to fit the available space.
     *
     * @param width  The width.
     */
    public void setMaximumDrawWidth(int width) {
        this.maximumDrawWidth = width;
    }

    /**
     * Sets the minimum drawing height for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available height is
     * less than this amount, the chart will be drawn using the minimum height
     * then scaled down to fit the available space.
     *
     * @param height  The height.
     */
    public void setMinimumDrawHeight(int height) {
        this.minimumDrawHeight = height;
    }

    /**
     * Returns the minimum drawing height for charts.
     * <P>
     * If the height available on the panel is less than this, then the chart
     * is drawn at the minimum height then scaled down to fit.
     *
     * @return  The minimum drawing height.
     */
    public int getMinimumDrawHeight() {
        return this.minimumDrawHeight;
    }

    /**
     * Returns the maximum drawing height for charts.
     * <P>
     * If the height available on the panel is greater than this, then the
     * chart is drawn at the maximum height then scaled up to fit.
     *
     * @return  The maximum drawing height.
     */
    public int getMaximumDrawHeight() {
        return this.maximumDrawHeight;
    }

    /**
     * Sets the maximum drawing height for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available height is
     * greater than this amount, the chart will be drawn using the maximum
     * height then scaled up to fit the available space.
     *
     * @param height  The height.
     */
    public void setMaximumDrawHeight(int height) {
        this.maximumDrawHeight = height;
    }

    /**
     * Returns the popup menu.
     *
     * @return the popup menu.
     */
    public JPopupMenu getPopupMenu() {
        return this.popup;
    }

    /**
     * Sets the popup menu for the panel.
     *
     * @param popup  the new popup menu.
     */
    public void setPopupMenu(JPopupMenu popup) {
        this.popup = popup;
    }

    /**
     * Returns the chart rendering info from the most recent chart redraw.
     *
     * @return the chart rendering info.
     */
    public ChartRenderingInfo getChartRenderingInfo() {
        return this.info;
    }

    /**
     * A flag that controls mouse-based zooming.
     *
     * @param flag  <code>true</code> enables zooming and rectangle fill on zoom.
     */
    public void setMouseZoomable(boolean flag) {
        this.setMouseZoomable(flag, true);
    }

    /**
     * Controls mouse zooming and how the zoom rectangle is displayed
     *
     * @param flag  <code>true</code> if zooming enabled
     * @param fillRectangle  <code>true</code> if zoom rectangle is filled,
     *                       false if rectangle is shown as outline only.
     */
    public void setMouseZoomable(boolean flag, boolean fillRectangle) {
        this.setHorizontalZoom(flag);
        this.setVerticalZoom(flag);
        this.setFillZoomRectangle(fillRectangle);
    }

    /**
     * A flag that controls mouse-based zooming on the horizontal axis.
     *
     * @param flag  <code>true</code> enables zooming on HorizontalValuePlots.
     */
    public void setHorizontalZoom(boolean flag) {
        this.horizontalZoom = flag && (chart.getPlot() instanceof HorizontalValuePlot);
    }

    /**
     * A flag that controls how the zoom rectangle is drawn.
     *
     * @param flag  <code>true</code> instructs to fill the rectangle on
     *              zoom, otherwise it will be outlined.
     */
    public void setFillZoomRectangle(boolean flag) {
        this.fillZoomRectangle = flag;
    }

    /**
     * A flag that controls mouse-based zooming on the vertical axis.
     *
     * @param flag  <code>true</code> enables zooming on VerticalValuePlots.
     */
    public void setVerticalZoom(boolean flag) {
        this.verticalZoom = flag && (chart.getPlot() instanceof VerticalValuePlot);
    }

    /**
     * A flag that controls trace lines on the horizontal axis.
     *
     * @param flag  <code>true</code> enables trace lines for the mouse
     *      pointer on the horizontal axis.
     */
    public void setHorizontalAxisTrace(boolean flag) {
        this.horizontalAxisTrace = flag;
    }

    /**
     * A flag that controls trace lines on the vertical axis.
     *
     * @param flag  <code>true</code> enables trace lines for the mouse
     *              pointer on the vertical axis.
     */
    public void setVerticalAxisTrace(boolean flag) {
        this.verticalAxisTrace = flag;
    }


    /**
     * Sets a flag that controls whether or not tool tips are displayed.
     *
     * @param flag  the flag.
     *
     * @deprecated use setDisplayToolTips.
     */
    public void setGenerateToolTips(boolean flag) {
        setDisplayToolTips(flag);
    }

    /**
     * Switches chart tooltip generation on or off.
     *
     * @param flag  the flag.
     */
    public void setDisplayToolTips(boolean flag) {

        if (flag == true) {
            ToolTipManager.sharedInstance().registerComponent(this);
        }
        else {
            ToolTipManager.sharedInstance().unregisterComponent(this);
        }

    }

    /**
     * Returns a string for the tooltip.
     *
     * @param e  the mouse event.
     *
     * @return a tool tip or <code>null</code> if no tooltip is available.
     */
    public String getToolTipText(MouseEvent e) {

        String result = null;

        EntityCollection entities = this.info.getEntityCollection();
        if (entities != null) {
            Insets insets = this.getInsets();
            ChartEntity entity = entities.getEntity((int) ((e.getX() - insets.left) / scaleX),
                                                    (int) ((e.getY() - insets.top) / scaleY));
            if (entity != null) {
                result = entity.getToolTipText();
            }
        }

        return result;

    }

    /**
     * Returns the chart entity at a given point.
     * <P>
     * This method will return null if there is (a) no entity at the given point, or
     * (b) no entity collection has been generated.
     *
     * @param viewX  the x-coordinate.
     * @param viewY  the y-coordinate.
     *
     * @return the chart entity (possibly null).
     */
    public ChartEntity getEntityForPoint(int viewX, int viewY) {

        Insets insets = getInsets();
        double x = (viewX - insets.left) / scaleX;
        double y = (viewY - insets.top) / scaleY;
        EntityCollection entities = this.info.getEntityCollection();
        return entities != null ? entities.getEntity(x, y) : null;

    }

    /**
     * Sets the refresh buffer flag.
     *
     * @param flag  <code>true</code> indicate, that the buffer should be refreshed.
     */
    public void setRefreshBuffer(boolean flag) {
        this.refreshBuffer = flag;
    }

    /** Working storage for available panel area after deducting insets. */
    private Rectangle2D available = new Rectangle2D.Double();

    /** Working storage for the chart area. */
    private Rectangle2D chartArea = new Rectangle2D.Double();

    /**
     * Paints the component by drawing the chart to fill the entire component,
     * but allowing for the insets (which will be non-zero if a border has been
     * set for this component).  To increase performance (at the expense of
     * memory), an off-screen buffer image can be used.
     *
     * @param g  the graphics device for drawing on.
     */
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // first determine the size of the chart rendering area...
        Dimension size = getSize();
        Insets insets = getInsets();
        available.setRect(insets.left, insets.top,
                          size.getWidth() - insets.left - insets.right,
                          size.getHeight() - insets.top - insets.bottom);

        // work out if scaling is required...
        boolean scale = false;
        double drawWidth = available.getWidth();
        double drawHeight = available.getHeight();
        this.scaleX = 1.0;
        this.scaleY = 1.0;

        if (drawWidth < this.minimumDrawWidth) {
            scaleX = drawWidth / minimumDrawWidth;
            drawWidth = minimumDrawWidth;
            scale = true;
        }
        else if (drawWidth > this.maximumDrawWidth) {
            scaleX = drawWidth / maximumDrawWidth;
            drawWidth = maximumDrawWidth;
            scale = true;
        }

        if (drawHeight < this.minimumDrawHeight) {
            scaleY = drawHeight / minimumDrawHeight;
            drawHeight = minimumDrawHeight;
            scale = true;
        }
        else if (drawHeight > this.maximumDrawHeight) {
            scaleY = drawHeight / maximumDrawHeight;
            drawHeight = maximumDrawHeight;
            scale = true;
        }

        chartArea.setRect(0.0, 0.0, drawWidth, drawHeight);

        // are we using the chart buffer?
        if (useBuffer) {

            // do we need to resize the buffer?
            if ((chartBuffer == null) || (chartBufferWidth != available.getWidth())
                                      || (chartBufferHeight != available.getHeight())) {

                chartBufferWidth = (int) available.getWidth();
                chartBufferHeight = (int) available.getHeight();
                chartBuffer = createImage(chartBufferWidth, chartBufferHeight);
                refreshBuffer = true;

            }

            // do we need to redraw the buffer?
            if (refreshBuffer) {

                Rectangle2D bufferArea =
                    new Rectangle2D.Double(0, 0, chartBufferWidth, chartBufferHeight);

                Graphics2D bufferG2 = (Graphics2D) chartBuffer.getGraphics();
                if (scale) {
                    AffineTransform saved = bufferG2.getTransform();
                    AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
                    bufferG2.transform(st);
                    chart.draw(bufferG2, chartArea, this.info);
                    bufferG2.setTransform(saved);
                }
                else {
                    chart.draw(bufferG2, bufferArea, this.info);
                }

                refreshBuffer = false;

            }

            // zap the buffer onto the panel...
            g2.drawImage(chartBuffer, insets.left, insets.right, this);

        }

        // or redrawing the chart every time...
        else {

            AffineTransform saved = g2.getTransform();
            g2.translate(insets.left, insets.right);
            if (scale) {
                AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
                g2.transform(st);
            }
            chart.draw(g2, chartArea, this.info);
            g2.setTransform(saved);

        }

        this.verticalTraceLine = null;
        this.horizontalTraceLine = null;

    }


  /**
   * Paints the component by drawing the chart to fill the entire component,
   * but allowing for the insets (which will be non-zero if a border has been
   * set for this component).  To increase performance (at the expense of
   * memory), an off-screen buffer image can be used.
   *
   * @param g  the graphics device for drawing on.
   */
  public void paintWMFComponent(Graphics g) {

      Graphics2D g2 = (Graphics2D) g;

      // first determine the size of the chart rendering area...
      Dimension size = getSize();
      Insets insets = getInsets();
      available.setRect(insets.left, insets.top,
                        size.getWidth() - insets.left - insets.right,
                        size.getHeight() - insets.top - insets.bottom);

      // work out if scaling is required...
      boolean scale = false;
      double drawWidth = available.getWidth();
      double drawHeight = available.getHeight();
      this.scaleX = 1.0;
      this.scaleY = 1.0;

      if (drawWidth < this.minimumDrawWidth) {
          scaleX = drawWidth / minimumDrawWidth;
          drawWidth = minimumDrawWidth;
          scale = true;
      }
      else if (drawWidth > this.maximumDrawWidth) {
          scaleX = drawWidth / maximumDrawWidth;
          drawWidth = maximumDrawWidth;
          scale = true;
      }

      if (drawHeight < this.minimumDrawHeight) {
          scaleY = drawHeight / minimumDrawHeight;
          drawHeight = minimumDrawHeight;
          scale = true;
      }
      else if (drawHeight > this.maximumDrawHeight) {
          scaleY = drawHeight / maximumDrawHeight;
          drawHeight = maximumDrawHeight;
          scale = true;
      }

      chartArea.setRect(0.0, 0.0, drawWidth, drawHeight);

      AffineTransform saved = g2.getTransform();
      g2.translate(insets.left, insets.right);
      if (scale) {
          AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
          g2.transform(st);
      }
      chart.draw(g2, chartArea, this.info);
      g2.setTransform(saved);


      this.verticalTraceLine = null;
      this.horizontalTraceLine = null;

  }





    /**
     * Receives notification of changes to the chart, and redraws the chart.
     *
     * @param event  details of the chart change event.
     */
    public void chartChanged(ChartChangeEvent event) {

        this.refreshBuffer = true;
        repaint();

    }

    /**
     * Handles action events generated by the popup menu.
     *
     * @param event  the event.
     */
    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();

        if (command.equals(PROPERTIES_ACTION_COMMAND)) {
            this.attemptEditChartProperties();
        }
        else if (command.equals(SAVE_ACTION_COMMAND)) {
            try {
                this.doSaveAs();
            }
            catch (IOException e) {
                System.err.println("ChartPanel.doSaveAs: i/o exception = " + e.getMessage());
            }
        }
        else if (command.equals(PRINT_ACTION_COMMAND)) {
            this.createChartPrintJob();
        }
        else if (command.equals(ZOOM_IN_BOTH_ACTION_COMMAND)) {
            zoomInBoth(this.zoomPoint.getX(), this.zoomPoint.getY());
        }
        else if (command.equals(ZOOM_IN_HORIZONTAL_ACTION_COMMAND)) {
            zoomInHorizontal(this.zoomPoint.getX());
        }
        else if (command.equals(ZOOM_IN_VERTICAL_ACTION_COMMAND)) {
            zoomInVertical(this.zoomPoint.getY());
        }
        else if (command.equals(ZOOM_OUT_BOTH_ACTION_COMMAND)) {
            zoomOutBoth(this.zoomPoint.getX(), this.zoomPoint.getY());
        }
        else if (command.equals(ZOOM_OUT_HORIZONTAL_ACTION_COMMAND)) {
            zoomOutHorizontal(this.zoomPoint.getX());
        }
        else if (command.equals(ZOOM_OUT_VERTICAL_ACTION_COMMAND)) {
            zoomOutVertical(this.zoomPoint.getY());
        }
        else if (command.equals(AUTO_RANGE_BOTH_ACTION_COMMAND)) {
            autoRangeBoth();
        }
        else if (command.equals(AUTO_RANGE_HORIZONTAL_ACTION_COMMAND)) {
            autoRangeHorizontal();
        }
        else if (command.equals(AUTO_RANGE_VERTICAL_ACTION_COMMAND)) {
            autoRangeVertical();
        }

    }

    /**
     * Handles a 'mouse entered' event.
     * <P>
     * This method does nothing, but is required for implementation of the MouseListener
     * interface.
     *
     * @param e  the mouse event.
     */
    public void mouseEntered(MouseEvent e) {
        // do nothing
    }

    /**
     * Handles a 'mouse exited' event.
     * <P>
     * This method does nothing, but is required for implementation of the MouseListener
     * interface.
     *
     * @param e  the mouse event.
     */
    public void mouseExited(MouseEvent e) {
        // do nothing
    }

    /**
     * Handles a 'mouse pressed' event.
     * <P>
     * This event is the popup trigger on Unix/Linux.  For Windows, the popup
     * trigger is the 'mouse released' event.
     *
     * @param e  The mouse event.
     */
    public void mousePressed(MouseEvent e) {

        if (zoomRectangle == null) {

            this.zoomPoint = RefineryUtilities.getPointInRectangle(e.getX(), e.getY(),
                                                                   getScaledDataArea());

            // check for popup trigger...
            if (e.isPopupTrigger()) {
                if (popup != null) {
                    displayPopupMenu(e.getX(), e.getY());
                }
            }
        }

    }

    /**
     * Handles a 'mouse released' event.
     * <P>
     * On Windows, we need to check if this is a popup trigger, but only if we
     * haven't already been tracking a zoom rectangle.
     *
     * @param e  Information about the event.
     */
    public void mouseReleased(MouseEvent e) {

        if (zoomRectangle != null) {

            if (Math.abs(e.getX() - zoomPoint.getX()) >= MINIMUM_DRAG_ZOOM_SIZE) {
                if (e.getX() < zoomPoint.getX() || e.getY() < zoomPoint.getY()) {
                    autoRangeBoth();
                }
                else {
                    double x, y, w, h;
                    Rectangle2D scaledDataArea = this.getScaledDataArea();
                    //for a mouseReleased event, (horizontalZoom || verticalZoom)
                    //will be true, so we can just test for either being false;
                    //otherwise both are true
                    if (verticalZoom == false) {
                        x = zoomPoint.getX();
                        y = scaledDataArea.getMinY();
                        w = Math.min(zoomRectangle.getWidth(),
                                     scaledDataArea.getMaxX() - zoomPoint.getX());
                        h = scaledDataArea.getHeight();
                    }
                    else if (horizontalZoom == false) {
                        x = scaledDataArea.getMinX();
                        y = zoomPoint.getY();
                        w = scaledDataArea.getWidth();
                        h = Math.min(zoomRectangle.getHeight(),
                                     scaledDataArea.getMaxY() - zoomPoint.getY());
                    }
                    else {
                        x = zoomPoint.getX();
                        y = zoomPoint.getY();
                        w = Math.min(zoomRectangle.getWidth(),
                                     scaledDataArea.getMaxX() - zoomPoint.getX());
                        h = Math.min(zoomRectangle.getHeight(),
                                     scaledDataArea.getMaxY() - zoomPoint.getY());
                    }
                    Rectangle2D zoomArea = new Rectangle2D.Double(x, y, w, h);
                    zoom(zoomArea);
                }
                this.zoomPoint = null;
                this.zoomRectangle = null;
            }
            else {
                Graphics2D g2 = (Graphics2D) getGraphics();
                g2.setXORMode(java.awt.Color.gray);
                if (fillZoomRectangle == true) {
                    g2.fill(zoomRectangle);
                }
                else {
                    g2.draw(zoomRectangle);
                }
                g2.dispose();
                this.zoomRectangle = null;
            }

        }

        else if (e.isPopupTrigger()) {
            if (popup != null) {
                displayPopupMenu(e.getX(), e.getY());
            }
        }

    }

    /**
     * Receives notification of mouse clicks on the panel... these are
     * translated and passed on to any registered chart mouse click listeners.
     *
     * @param event  Information about the mouse event.
     */
    public void mouseClicked(MouseEvent event) {

        Insets insets = getInsets();
        int x = (int) ((event.getX() - insets.left) / scaleX);
        int y = (int) ((event.getY() - insets.top) / scaleY);

        // old 'handle click' code...
        chart.handleClick(x, y, this.info);

        // new entity code...
        if (this.chartMouseListeners.isEmpty()) {
            return;
        }

        ChartEntity entity = this.info.getEntityCollection().getEntity(x, y);
        ChartMouseEvent chartEvent = new ChartMouseEvent(event, entity);

        Iterator iterator = chartMouseListeners.iterator();
        while (iterator.hasNext()) {
            ChartMouseListener listener = (ChartMouseListener) iterator.next();
            listener.chartMouseClicked(chartEvent);
        }

    }

    /**
     * Implementation of the MouseMotionListener's method
     *
     * @param e  the event.
     */
    public void mouseMoved(MouseEvent e) {

        if (this.horizontalAxisTrace) {
            drawHorizontalAxisTrace(e.getX());
        }

        if (this.verticalAxisTrace) {
            drawVerticalAxisTrace(e.getY());
        }

        if (this.chartMouseListeners.isEmpty()) {
            return;
        }

        Insets insets = getInsets();
        int x = (int) ((e.getX() - insets.left) / scaleX);
        int y = (int) ((e.getY() - insets.top) / scaleY);

        ChartEntity entity = this.info.getEntityCollection().getEntity(x, y);
        ChartMouseEvent event = new ChartMouseEvent(e, entity);

        Iterator iterator = chartMouseListeners.iterator();
        while (iterator.hasNext()) {
            ChartMouseListener listener = (ChartMouseListener) iterator.next();
            listener.chartMouseMoved(event);
        }

    }

    /**
     * Handles a 'mouse dragged' event.
     *
     * @param e  the mouse event.
     */
    public void mouseDragged(MouseEvent e) {

        // if the popup menu has already been triggered, then ignore dragging...
        if (popup != null && popup.isShowing()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) getGraphics();

        // use XOR to erase the previous zoom rectangle (if any)...
        g2.setXORMode(java.awt.Color.gray);
        if (zoomRectangle != null) {
            if (fillZoomRectangle == true) {
                g2.fill(zoomRectangle);
            }
            else {
                g2.draw(zoomRectangle);
            }
        }

        Rectangle2D scaledDataArea = this.getScaledDataArea();
        if (this.horizontalZoom && this.verticalZoom) {
            // selected rectangle shouldn't extend outside the data area...
            double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
            double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());
            zoomRectangle = new Rectangle2D.Double(zoomPoint.getX(), zoomPoint.getY(),
                                                   xmax - zoomPoint.getX(),
                                                   ymax - zoomPoint.getY());
        }
        else if (this.horizontalZoom) {
            double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
            zoomRectangle = new Rectangle2D.Double(zoomPoint.getX(), scaledDataArea.getMinY(),
                                                   xmax - zoomPoint.getX(),
                                                   scaledDataArea.getHeight());
        }
        else if (this.verticalZoom) {
            double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());
            zoomRectangle = new Rectangle2D.Double(scaledDataArea.getMinX(), zoomPoint.getY(),
                                                   scaledDataArea.getWidth(),
                                                   ymax - zoomPoint.getY());
        }

        if (zoomRectangle != null) {
            // use XOR to draw the new zoom rectangle...
            if (fillZoomRectangle == true) {
                g2.fill(zoomRectangle);
            }
            else {
                g2.draw(zoomRectangle);
            }
        }
        g2.dispose();

    }

    /**
     * Zooms in on an anchor point (measured in Java2D coordinates).
     *
     * @param x  The x value.
     * @param y  The y value.
     */
    public void zoomInBoth(double x, double y) {

        zoomInHorizontal(x);
        zoomInVertical(y);

    }

    /**
     * Decreases the range on the horizontal axis, centered about a Java2D
     * x coordinate.
     * <P>
     * The range on the x axis is halved.
     *
     * @param x  The x coordinate in Java2D space.
     */
    public void zoomInHorizontal(double x) {

        if (chart.getPlot() instanceof HorizontalValuePlot) {
            HorizontalValuePlot hvp = (HorizontalValuePlot) chart.getPlot();
            ValueAxis axis = hvp.getHorizontalValueAxis();
            double value = axis.translateJava2DtoValue((float) x, this.info.getDataArea());
            axis.resizeRange(0.5, value);
        }

    }

    /**
     * Decreases the range on the vertical axis, centered about a Java2D
     * y coordinate.
     * <P>
     * The range on the y axis is halved.
     *
     * @param y  The y coordinate in Java2D space.
     */
    public void zoomInVertical(double y) {

        if (chart.getPlot() instanceof VerticalValuePlot) {
            VerticalValuePlot vvp = (VerticalValuePlot) chart.getPlot();
            ValueAxis axis = vvp.getVerticalValueAxis();
            double value = axis.translateJava2DtoValue((float) y, this.info.getDataArea());
            axis.resizeRange(0.5, value);
        }

    }

    /**
     * Zooms out on an anchor point (measured in Java2D coordinates).
     *
     * @param x  The x value.
     * @param y  The y value.
     */
    public void zoomOutBoth(double x, double y) {

        zoomOutHorizontal(x);
        zoomOutVertical(y);

    }

    /**
     * Increases the range on the horizontal axis, centered about a Java2D
     * x coordinate.
     * <P>
     * The range on the x axis is doubled.
     *
     * @param x  The x coordinate in Java2D space.
     */
    public void zoomOutHorizontal(double x) {

        if (chart.getPlot() instanceof HorizontalValuePlot) {
            HorizontalValuePlot hvp = (HorizontalValuePlot) chart.getPlot();
            ValueAxis axis = hvp.getHorizontalValueAxis();
            double value = axis.translateJava2DtoValue((float) x, this.info.getDataArea());
            axis.resizeRange(2.0, value);
        }

    }

    /**
     * Increases the range on the vertical axis, centered about a Java2D y coordinate.
     * <P>
     * The range on the y axis is doubled.
     *
     * @param y  the y coordinate in Java2D space.
     */
    public void zoomOutVertical(double y) {

        if (chart.getPlot() instanceof VerticalValuePlot) {
            VerticalValuePlot vvp = (VerticalValuePlot) chart.getPlot();
            ValueAxis axis = vvp.getVerticalValueAxis();
            double value = axis.translateJava2DtoValue((float) y, this.info.getDataArea());
            axis.resizeRange(2.0, value);
        }

    }

    /**
     * Zooms in on a selected region.
     *
     * @param selection  the selected region.
     */
    public void zoom(Rectangle2D selection) {

        if ((selection.getHeight() > 0) && (selection.getWidth() > 0)) {

            Rectangle2D scaledDataArea = this.getScaledDataArea();
            if (chart.getPlot() instanceof HorizontalValuePlot) {
                HorizontalValuePlot hvp = (HorizontalValuePlot) chart.getPlot();
                ValueAxis axis = hvp.getHorizontalValueAxis();
                double lower = axis.translateJava2DtoValue((float) selection.getX(),
                                                           scaledDataArea);
                double upper = axis.translateJava2DtoValue((float) selection.getMaxX(),
                                                           scaledDataArea);
                axis.setRange(lower, upper);
            }

            if (chart.getPlot() instanceof VerticalValuePlot) {
                VerticalValuePlot vvp = (VerticalValuePlot) chart.getPlot();
                ValueAxis axis = vvp.getVerticalValueAxis();
                double lower = axis.translateJava2DtoValue((float) selection.getMaxY(),
                                                           scaledDataArea);
                double upper = axis.translateJava2DtoValue((float) selection.getY(),
                                                           scaledDataArea);
                axis.setRange(lower, upper);
            }
        }

    }

    /**
     * Restores the auto-range calculation on both axes.
     */
    public void autoRangeBoth() {
        autoRangeHorizontal();
        autoRangeVertical();
    }

    /**
     * Restores the auto-range calculation on the horizontal axis.
     */
    public void autoRangeHorizontal() {
        if (chart.getPlot() instanceof HorizontalValuePlot) {
            HorizontalValuePlot hvp = (HorizontalValuePlot) chart.getPlot();
            ValueAxis axis = hvp.getHorizontalValueAxis();
            axis.setAutoRange(true);
        }
    }

    /**
     * Restores the auto-range calculation on the vertical axis.
     */
    public void autoRangeVertical() {

        Plot p = chart.getPlot();
        if (p instanceof VerticalValuePlot) {
            VerticalValuePlot plot = (VerticalValuePlot) p;
            ValueAxis axis = plot.getVerticalValueAxis();
            axis.setAutoRange(true);
        }

    }

    /**
     * Returns the data area for the chart (the area inside the axes) with the
     * current scaling applied.
     *
     * @return the scaled data area.
     */
    public Rectangle2D getScaledDataArea() {
        Rectangle2D dataArea = this.info.getDataArea();
        Insets insets = this.getInsets();
        double x = dataArea.getX() * scaleX + insets.left;
        double y = dataArea.getY() * scaleY + insets.top;
        double w = dataArea.getWidth() * scaleX;
        double h = dataArea.getHeight() * scaleY;
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Draws a vertical line used to trace the mouse position to the horizontal axis.
     *
     * @param x  the x-coordinate of the trace line.
     */
    private void drawHorizontalAxisTrace(int x) {

        Graphics2D g2 = (Graphics2D) getGraphics();
        Rectangle2D dataArea = this.getScaledDataArea();

        g2.setXORMode(java.awt.Color.orange);
        if (((int) dataArea.getMinX() < x) && (x < (int) dataArea.getMaxX())) {

            if (verticalTraceLine != null) {
                g2.draw(verticalTraceLine);
                verticalTraceLine.setLine(x, (int) dataArea.getMinY(),
                                          x, (int) dataArea.getMaxY());
            }
            else {
                verticalTraceLine = new Line2D.Float(x, (int) dataArea.getMinY(),
                                                     x, (int) dataArea.getMaxY());
            }
            g2.draw(verticalTraceLine);
        }

    }

    /**
     * Draws a horizontal line used to trace the mouse position to the vertical axis.
     *
     * @param y  the y-coordinate of the trace line.
     */
    private void drawVerticalAxisTrace(int y) {

        Graphics2D g2 = (Graphics2D) getGraphics();
        Rectangle2D dataArea = this.getScaledDataArea();

        g2.setXORMode(java.awt.Color.orange);
        if (((int) dataArea.getMinY() < y) && (y < (int) dataArea.getMaxY())) {

            if (horizontalTraceLine != null) {
                g2.draw(horizontalTraceLine);
                horizontalTraceLine.setLine((int) dataArea.getMinX(), y,
                                            (int) dataArea.getMaxX(), y);
            }
            else {
                horizontalTraceLine = new Line2D.Float((int) dataArea.getMinX(), y,
                                                       (int) dataArea.getMaxX(), y);
            }
            g2.draw(horizontalTraceLine);
        }

    }

    /**
     * Displays a dialog that allows the user to edit the properties for the
     * current chart.
     */
    private void attemptEditChartProperties() {

        ChartPropertyEditPanel panel = new ChartPropertyEditPanel(chart);
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Chart Properties", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            panel.updateChartProperties(chart);
        }

    }

    /**
     * Opens a file chooser and gives the user an opportunity to save the chart
     * in PNG format.
     *
     * @throws IOException if there is an I/O error.
     */
    public void doSaveAs() throws IOException {

        JFileChooser fileChooser = new JFileChooser();
        ExtensionFileFilter filter = new ExtensionFileFilter("PNG Image Files", ".png");
        fileChooser.addChoosableFileFilter(filter);

        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            ChartUtilities.saveChartAsPNG(fileChooser.getSelectedFile(),
                                          this.chart, getWidth(), getHeight());
        }

    }

    /**
     * Creates a print job for the chart.
     */
    public void createChartPrintJob() {

        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf = job.defaultPage();
        pf = job.pageDialog(pf);
        job.setPrintable(this, pf);
        if (job.printDialog()) {
            try {
                job.print();
            }
            catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, e);
            }
        }

    }

    /**
     * Prints the chart on a single page.
     *
     * @param g  the graphics context.
     * @param pf  the page format to use.
     * @param pageIndex  the index of the page. If not <code>0</code>, nothing gets print.
     *
     * @return the result of printing.
     */
    public int print(Graphics g, PageFormat pf, int pageIndex) {

        if (pageIndex != 0) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2 = (Graphics2D) g;
        double x = pf.getImageableX();
        double y = pf.getImageableY();
        double w = pf.getImageableWidth();
        double h = pf.getImageableHeight();
        chart.draw(g2, new Rectangle2D.Double(x, y, w, h), null);
        return PAGE_EXISTS;

    }

    /**
     * Adds a listener to the list of objects listening for chart mouse events.
     *
     * @param listener  the listener.
     */
    public void addChartMouseListener(ChartMouseListener listener) {
        this.chartMouseListeners.add(listener);
    }

    /**
     * Removes a listener from the list of objects listening for chart mouse events.
     *
     * @param listener  the listener.
     */
    public void removeChartMouseListener(ChartMouseListener listener) {
        this.chartMouseListeners.remove(listener);
    }

    /**
     * Creates a popup menu for the panel.
     *
     * @param properties    Include a menu item for the chart property editor.
     * @param save      Include a menu item for saving the chart.
     * @param print     Include a menu item for printing the chart.
     * @param zoom      Include menu items for zooming.
     *
     * @return The popup menu.
     */
    protected JPopupMenu createPopupMenu(boolean properties, boolean save, boolean print,
                                         boolean zoom) {

        JPopupMenu result = new JPopupMenu("Chart:");
        boolean separator = false;

        if (properties) {
            JMenuItem propertiesItem = new JMenuItem("Properties...");
            propertiesItem.setActionCommand(PROPERTIES_ACTION_COMMAND);
            propertiesItem.addActionListener(this);
            result.add(propertiesItem);
            separator = true;
        }

        if (save) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }
            JMenuItem saveItem = new JMenuItem("Save as...");
            saveItem.setActionCommand(SAVE_ACTION_COMMAND);
            saveItem.addActionListener(this);
            result.add(saveItem);
            separator = true;
        }

        if (print) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }
            JMenuItem printItem = new JMenuItem("Print...");
            printItem.setActionCommand(PRINT_ACTION_COMMAND);
            printItem.addActionListener(this);
            result.add(printItem);
            separator = true;
        }

        if (zoom) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }

            JMenu zoomInMenu = new JMenu("Zoom In");

            JMenuItem zoomInBothAxesMenuItem = new JMenuItem("Both Axes");
            zoomInBothAxesMenuItem.setActionCommand(ZOOM_IN_BOTH_ACTION_COMMAND);
            zoomInBothAxesMenuItem.addActionListener(this);
            zoomInMenu.add(zoomInBothAxesMenuItem);

            zoomInMenu.addSeparator();

            zoomInHorizontalMenuItem = new JMenuItem("Horizontal Axis");
            zoomInHorizontalMenuItem.setActionCommand(ZOOM_IN_HORIZONTAL_ACTION_COMMAND);
            zoomInHorizontalMenuItem.addActionListener(this);
            zoomInMenu.add(zoomInHorizontalMenuItem);

            zoomInVerticalMenuItem = new JMenuItem("Vertical Axis");
            zoomInVerticalMenuItem.setActionCommand(ZOOM_IN_VERTICAL_ACTION_COMMAND);
            zoomInVerticalMenuItem.addActionListener(this);
            zoomInMenu.add(zoomInVerticalMenuItem);

            result.add(zoomInMenu);

            JMenu zoomOutMenu = new JMenu("Zoom Out");

            JMenuItem zoomOutBothMenuItem = new JMenuItem("Both Axes");
            zoomOutBothMenuItem.setActionCommand(ZOOM_OUT_BOTH_ACTION_COMMAND);
            zoomOutBothMenuItem.addActionListener(this);
            zoomOutMenu.add(zoomOutBothMenuItem);

            zoomOutMenu.addSeparator();

            zoomOutHorizontalMenuItem = new JMenuItem("Horizontal Axis");
            zoomOutHorizontalMenuItem.setActionCommand(ZOOM_OUT_HORIZONTAL_ACTION_COMMAND);
            zoomOutHorizontalMenuItem.addActionListener(this);
            zoomOutMenu.add(zoomOutHorizontalMenuItem);

            zoomOutVerticalMenuItem = new JMenuItem("Vertical Axis");
            zoomOutVerticalMenuItem.setActionCommand(ZOOM_OUT_VERTICAL_ACTION_COMMAND);
            zoomOutVerticalMenuItem.addActionListener(this);
            zoomOutMenu.add(zoomOutVerticalMenuItem);

            result.add(zoomOutMenu);

            JMenu autoRangeMenu = new JMenu("Auto Range");

            autoRangeBothMenuItem = new JMenuItem("Both Axes");
            autoRangeBothMenuItem.setActionCommand(AUTO_RANGE_BOTH_ACTION_COMMAND);
            autoRangeBothMenuItem.addActionListener(this);
            autoRangeMenu.add(autoRangeBothMenuItem);

            autoRangeMenu.addSeparator();
            autoRangeHorizontalMenuItem = new JMenuItem("Horizontal Axis");
            autoRangeHorizontalMenuItem.setActionCommand(AUTO_RANGE_HORIZONTAL_ACTION_COMMAND);
            autoRangeHorizontalMenuItem.addActionListener(this);
            autoRangeMenu.add(autoRangeHorizontalMenuItem);

            autoRangeVerticalMenuItem = new JMenuItem("Vertical Axis");
            autoRangeVerticalMenuItem.setActionCommand(AUTO_RANGE_VERTICAL_ACTION_COMMAND);
            autoRangeVerticalMenuItem.addActionListener(this);
            autoRangeMenu.add(autoRangeVerticalMenuItem);

            result.addSeparator();
            result.add(autoRangeMenu);

        }

        return result;

    }

    /**
     * Incomplete method - the idea is to modify the zooming options depending
     * on the type of chart being displayed by the panel.
     *
     * @param x  horizontal position of the popup.
     * @param y  vertical position of the popup.
     */
    protected void displayPopupMenu(int x, int y) {

        if (popup != null) {

            // go through each zoom menu item and doDecide whether or not to
            // enable it...
            Plot plot = this.chart.getPlot();
            if (plot instanceof HorizontalValuePlot) {
                //HorizontalValuePlot hvp = (HorizontalValuePlot)plot;
                //ValueAxis hAxis = hvp.getHorizontalValueAxis();
                // to be completed...
            }
            popup.show(this, x, y);
        }

    }

}
