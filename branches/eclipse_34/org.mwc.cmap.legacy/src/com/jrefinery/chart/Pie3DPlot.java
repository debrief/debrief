/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * --------------
 * PiePlot3D.java
 * --------------
 * (C) Copyright 2000-2002, by Simba Management and Contributors.
 *
 * Original Author:  Tomer Peretz;
 * Contributor(s):   Richard Atkinson;
 *                   David Gilbert (for Simba Management Limited);
 *                   Xun Kang;
 *
 * $Id: Pie3DPlot.java,v 1.1.1.1 2003/07/17 10:06:26 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 21-Jun-2002 : Version 1;
 * 31-Jul-2002 : Modified to use startAngle and direction, drawing modified so that charts
 *               render with foreground alpha < 1.0 (DG);
 * 05-Aug-2002 : Small modification to draw method to support URLs for HTML image maps (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 18-Oct-2002 : Added drawing bug fix sent in by Xun Kang, and made a couple of other related
 *               fixes (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Area;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import com.jrefinery.chart.tooltips.StandardPieToolTipGenerator;
import com.jrefinery.chart.entity.PieSectionEntity;
import com.jrefinery.data.PieDataset;
import com.jrefinery.data.DatasetUtilities;

/**
 * A plot that displays data in the form of a 3D pie chart, using data from
 * any class that implements the CategoryDataset interface.
 *
 * @see Plot
 *
 * @author TP
 */
public class Pie3DPlot extends PiePlot {

    /** The factor of the depth of the pie from the plot height */
    private double depthFactor = 0.2;

    /**
     * Creates a 3D pie chart with default attributes.
     *
     * @param data  the data for the chart.
     */
    public Pie3DPlot(PieDataset data) {
        super(data);
        setCircularAttribute(false);
    }

    /**
     * Sets the factor of the pie depth from the plot height.
     *
     * @param newDepthFactor  the new depth factor.
     */
    public void setDepthFactor(double newDepthFactor) {
        this.depthFactor = newDepthFactor;
    }

    /**
     * The depth factor for the chart.
     *
     * @return  the current depth factor.
     */
    public double getDepthFactor () {
        return depthFactor;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot should be drawn.
     * @param info  collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        Shape savedClip = g2.getClip();
        Rectangle2D clipArea = savedClip != null
            ? savedClip.getBounds2D().createIntersection(plotArea)
            : plotArea;

        // adjust for insets...
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
                             plotArea.getY() + insets.top,
                             plotArea.getWidth() - insets.left - insets.right,
                             plotArea.getHeight() - insets.top - insets.bottom);
        }

        if (info != null) {
            info.setPlotArea(plotArea);
            info.setDataArea(plotArea);
        }

        // adjust the plot area by the interior spacing value
        double gapPercent = getInteriorGapPercent();
        double gapHorizontal = plotArea.getWidth() * gapPercent;
        double gapVertical = plotArea.getHeight() * gapPercent;

        double pieX = plotArea.getX() + gapHorizontal / 2;
        double pieY = plotArea.getY() + gapVertical / 2;
        double pieW = plotArea.getWidth() - gapHorizontal;
        double pieH = plotArea.getHeight() - gapVertical;

        if (isCircular()) {
            double min = Math.min(pieW, pieH) / 2;
            pieX = (pieX + pieX + pieW) / 2 - min;
            pieY = (pieY + pieY + pieH) / 2 - min;
            pieW = 2 * min;
            pieH = 2 * min;
        }

        Rectangle2D explodedPieArea = new Rectangle2D.Double(pieX, pieY, pieW, pieH);
        double radiusPercent = getRadiusPercent();
        double explodeHorizontal = (1 - radiusPercent) * pieW;
        double explodeVertical = (1 - radiusPercent) * pieH;
        Rectangle2D pieArea = new Rectangle2D.Double(pieX + explodeHorizontal / 2,
                                                     pieY + explodeVertical / 2,
                                                     pieW - explodeHorizontal,
                                                     pieH - explodeVertical);

        // draw the outline and background
        drawOutlineAndBackground(g2, plotArea);
        // get the data source - return if null;
        PieDataset data = getPieDataset();
        if (data == null) {
            return;
        }
        // if too any elements
        if (data.getCategories().size() > plotArea.getWidth()) {
            String text = "Too many elements";
            Font sfont = new Font("dialog", Font.BOLD, 10);
            g2.setFont(sfont);
            int stringWidth
                = (int) sfont.getStringBounds(text, g2.getFontRenderContext()).getWidth();

            g2.drawString(text,
                          (int) (plotArea.getX() + (plotArea.getWidth() - stringWidth) / 2),
                          (int) (plotArea.getY() + (plotArea.getHeight() / 2)));
            return;
        }
        // if we are drawing a perfect circle, we need to readjust the top left
        // coordinates of the drawing area for the arcs to arrive at this
        // effect.
        if (isCircular()) {
            double min = Math.min(plotArea.getWidth(), plotArea.getHeight()) / 2;
            plotArea = new Rectangle2D.Double(plotArea.getCenterX() - min,
                                              plotArea.getCenterY() - min, 2 * min, 2 * min);
        }
        // get a list of categories...
        List categories = data.getCategories();

        if (categories.size() == 0) {
            return;
        }

        // establish the coordinates of the top left corner of the drawing area
        double arcX = pieArea.getX();
        double arcY = pieArea.getY();

        g2.clip(clipArea);
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.foregroundAlpha));

        double totalValue = DatasetUtilities.getPieDatasetTotal(data);
        double runningTotal = 0;
        int depth = (int) (pieArea.getHeight() * depthFactor);
        if (depth < 0) {
            return;  // if depth is negative don't draw anything
        }

        ArrayList arcList = new ArrayList();
        Arc2D.Double arc;
        Paint paint;
        Paint outlinePaint;

        boolean hasElement = false;
        Iterator iterator = categories.iterator();
        while (iterator.hasNext()) {

            Object current = iterator.next();
            Number dataValue = data.getValue(current);
            double value = dataValue.doubleValue();
            if (value <= 0) {
                arcList.add(null);
                continue;
            }
            hasElement = true;
            double startAngle = getStartAngle();
            int direction = getDirection();
            double angle1 = startAngle + (direction * (runningTotal * 360)) / totalValue;
            double angle2 = startAngle + (direction * (runningTotal + value) * 360) / totalValue;
            arcList.add(new Arc2D.Double(arcX,
                                         arcY + depth,
                                         pieArea.getWidth(),
                                         pieArea.getHeight() - depth,
                                         angle1,
                                         angle2 - angle1,
                                         Arc2D.PIE));
            runningTotal += value;
        }

        Shape oldClip = g2.getClip();

        Ellipse2D top = new Ellipse2D.Double(pieArea.getX(),
                                             pieArea.getY(),
                                             pieArea.getWidth(),
                                             pieArea.getHeight() - depth);

        Ellipse2D bottom = new Ellipse2D.Double(pieArea.getX(),
                                                pieArea.getY() + depth,
                                                pieArea.getWidth(),
                                                pieArea.getHeight() - depth);

        Rectangle2D lower = new Rectangle2D.Double(top.getX(),
                                                   top.getCenterY(),
                                                   pieArea.getWidth(),
                                                   bottom.getMaxY() - top.getCenterY());

        Rectangle2D upper = new Rectangle2D.Double(pieArea.getX(),
                                                   top.getY(),
                                                   pieArea.getWidth(),
                                                   bottom.getCenterY() - top.getY());

        Area a = new Area(top);
        a.add(new Area(lower));
        Area b = new Area(bottom);
        b.add(new Area(upper));
        Area pie = new Area(a);
        pie.intersect(b);

        Area front = new Area(pie);
        front.subtract(new Area(top));

        Area back = new Area(pie);
        back.subtract(new Area(bottom));

        // draw the bottom circle
        int xs[];
        int ys[];
        outlinePaint = getSeriesOutlinePaint(0);
        arc = new Arc2D.Double(arcX,
                               arcY + depth,
                               pieArea.getWidth(),
                               pieArea.getHeight() - depth,
                               0, 360, Arc2D.PIE);

        int categoryCount = arcList.size();
        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            arc = (Arc2D.Double) arcList.get(categoryIndex);
            paint = getSeriesPaint(categoryIndex);
            outlinePaint = getSeriesOutlinePaint(categoryIndex);

            g2.setPaint(paint);
            g2.fill(arc);
            g2.setPaint(outlinePaint);
            g2.draw(arc);
            g2.setPaint(paint);

            Point2D p1 = arc.getStartPoint();

            double x0 = arc.getCenterX();
            double y0 = arc.getCenterY();

            // draw the height
            xs = new int[] {(int) arc.getCenterX(), (int) arc.getCenterX(),
                            (int) p1.getX(), (int) p1.getX() };
            ys = new int[] {(int) arc.getCenterY(), (int) arc.getCenterY() - depth,
                            (int) p1.getY() - depth, (int) p1.getY() };
            Polygon polygon = new Polygon(xs, ys, 4);
            g2.setPaint(java.awt.Color.lightGray);
            g2.fill(polygon);
            g2.setPaint(outlinePaint);
            g2.draw(polygon);
            g2.setPaint(paint);

        }

        g2.setPaint(Color.gray);
        g2.fill(back);
        g2.fill(front);
        int cat = 0;
        iterator = arcList.iterator();
        while (iterator.hasNext()) {
            Arc2D segment = (Arc2D) iterator.next();
            if (segment != null) {
                paint = getSeriesPaint(cat);
                drawSide(g2, pieArea, segment, front, back, paint);
            }
            cat++;
        }

        g2.setClip(oldClip);

        // draw the sections at the top of the pie (and set up tooltips)...
        Arc2D upperArc;
        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            arc = (Arc2D.Double) arcList.get(categoryIndex);
            if (arc == null) {
                continue;
            }
            upperArc = new Arc2D.Double(arcX, arcY,
                                        pieArea.getWidth(),
                                        pieArea.getHeight() - depth,
                                        arc.getAngleStart(),
                                        arc.getAngleExtent(),
                                        Arc2D.PIE);
            paint = this.getSeriesPaint(categoryIndex);
            outlinePaint = this.getSeriesOutlinePaint(categoryIndex);

            g2.setPaint(paint);
            g2.fill(upperArc);
            g2.setStroke(new BasicStroke());
            g2.setPaint(outlinePaint);
            g2.draw(upperArc);

           // add a tooltip for the section...
            Object current = categories.get(categoryIndex);
            if (info != null) {
                if (getToolTipGenerator() == null) {
                    setToolTipGenerator(new StandardPieToolTipGenerator());
                }
                String tip = getToolTipGenerator().generateToolTip(data, current);
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(data, current);
                }
                PieSectionEntity entity =
                    new PieSectionEntity(upperArc, tip, url, current, categoryIndex);
                info.getEntityCollection().addEntity(entity);
            }

            // then draw the label...
            if (getSectionLabelType() != NO_LABELS) {
                this.drawLabel(g2, pieArea, explodedPieArea, data,
                               data.getValue(current).doubleValue(),
                               categoryIndex, arc.getAngleStart(), arc.getAngleExtent());
            }
        }

        g2.clip(savedClip);
        g2.setComposite(originalComposite);

    }

    /**
     * Draws the side of a pie section.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param arc  the arc.
     * @param front  the front of the pie.
     * @param back  the back of the pie.
     * @param paint  the color.
     */
    public void drawSide(Graphics2D g2,
                         Rectangle2D plotArea, Arc2D arc, Area front, Area back, Paint paint) {

        double start = arc.getAngleStart();
        double extent = arc.getAngleExtent();
        double end = start + extent;

        // for CLOCKWISE charts, the extent will be negative...
        if (extent < 0.0) {

            if (isAngleAtFront(start)) {  // start at front

                if (!isAngleAtBack(end)) {

                    if (extent > -180.0) {  // the segment is entirely at the front of the chart
                        Area side = new Area(
                            new Rectangle2D.Double(arc.getEndPoint().getX(), plotArea.getY(),
                                                   arc.getStartPoint().getX()
                                                   - arc.getEndPoint().getX(),
                                                   plotArea.getHeight()));
                        side.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side);
                        g2.setPaint(Color.lightGray);
                        g2.draw(side);
                    }
                    else {  // the segment starts at the front, and wraps all the way around
                            // the back and finishes at the front again
                        Area side1 = new Area(
                            new Rectangle2D.Double(plotArea.getX(), plotArea.getY(),
                                                   arc.getStartPoint().getX() - plotArea.getX(),
                                                   plotArea.getHeight()));
                        side1.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.fill(back);
                        Area side2 = new Area(
                            new Rectangle2D.Double(arc.getEndPoint().getX(),
                                                   plotArea.getY(),
                                                   plotArea.getMaxX() - arc.getEndPoint().getX(),
                                                   plotArea.getHeight()));
                        g2.fill(side2);
                        g2.setPaint(Color.lightGray);
                        g2.draw(side1);
                        g2.draw(back);
                        g2.draw(side2);
                    }
                }
                else {  // starts at the front, finishes at the back (going around the left side)
                    Area side2 = new Area(
                        new Rectangle2D.Double(plotArea.getX(), plotArea.getY(),
                                               arc.getEndPoint().getX() - plotArea.getX(),
                                               plotArea.getHeight()));
                    side2.intersect(back);
                    g2.setPaint(paint);
                    g2.fill(side2);
                    g2.setPaint(Color.lightGray);
                    g2.draw(side2);

                    Area side1 = new Area(
                        new Rectangle2D.Double(plotArea.getX(), plotArea.getY(),
                                               arc.getStartPoint().getX() - plotArea.getX(),
                                               plotArea.getHeight()));
                    side1.intersect(front);
                    g2.setPaint(paint);
                    g2.fill(side1);
                    g2.setPaint(Color.lightGray);
                    g2.draw(side1);
                }
            }
            else {  // the segment starts at the back (still extending CLOCKWISE)

                if (!isAngleAtFront(end)) {
                    if (extent > -180.0) {  // whole segment stays at the back
                        Area side = new Area(
                            new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(),
                                                   arc.getEndPoint().getX()
                                                   - arc.getStartPoint().getX(),
                                                   plotArea.getHeight()));
                        side.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side);
                        g2.setPaint(Color.lightGray);
                        g2.draw(side);
                    }
                    else {  // starts at the back, wraps around front, and finishes at back again
                        Area side1 = new Area(
                            new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(),
                                                   plotArea.getMaxX()
                                                   - arc.getStartPoint().getX(),
                                                   plotArea.getHeight()));
                        side1.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.fill(front);
                        Area side2 = new Area(
                            new Rectangle2D.Double(plotArea.getX(),
                                                   plotArea.getY(),
                                                   arc.getEndPoint().getX() - plotArea.getX(),
                                                   plotArea.getHeight()));
                        side2.intersect(back);
                        g2.fill(side2);
                        g2.setPaint(Color.lightGray);
                        g2.draw(side1);
                        g2.draw(back);
                        g2.draw(side2);
                    }
                }
                else {  // starts at back, finishes at front (CLOCKWISE)
                    Area side1 = new Area(
                        new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(),
                                               plotArea.getMaxX() - arc.getStartPoint().getX(),
                                               plotArea.getHeight()));
                    side1.intersect(back);
                    g2.setPaint(paint);
                    g2.fill(side1);
                    g2.setPaint(Color.lightGray);
                    g2.draw(side1);

                    Area side2 = new Area(
                        new Rectangle2D.Double(arc.getEndPoint().getX(), plotArea.getY(),
                                               plotArea.getMaxX() - arc.getEndPoint().getX(),
                                               plotArea.getHeight()));
                    side2.intersect(front);
                    g2.setPaint(paint);
                    g2.fill(side2);
                    g2.setPaint(Color.lightGray);
                    g2.draw(side2);

                }
            }
        }
        else if (extent > 0.0) {  // the pie sections are arranged ANTICLOCKWISE

            if (isAngleAtFront(start)) {  // segment starts at the front

                if (!isAngleAtBack(end)) {  // and finishes at the front

                    if (extent < 180.0) {  // segment only occupies the front
                        Area side = new Area(
                            new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(),
                                                   arc.getEndPoint().getX()
                                                   - arc.getStartPoint().getX(),
                                                   plotArea.getHeight()));
                        side.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side);
                        g2.setPaint(Color.lightGray);
                        g2.draw(side);
                    }
                    else {  // segments wraps right around the back...
                        Area side1 = new Area(
                            new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(),
                                                   plotArea.getMaxX() - arc.getStartPoint().getX(),
                                                   plotArea.getHeight()));
                        side1.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.fill(back);
                        Area side2 = new Area(
                            new Rectangle2D.Double(plotArea.getX(),
                                                   plotArea.getY(),
                                                   arc.getEndPoint().getX() - plotArea.getMaxX(),
                                                   plotArea.getHeight()));
                        side2.intersect(front);
                        g2.fill(side2);
                        g2.setPaint(Color.lightGray);
                        g2.draw(side1);
                        g2.draw(back);
                        g2.draw(side2);
                    }
                }
                else {  // segments starts at front and finishes at back...
                    Area side2 = new Area(
                        new Rectangle2D.Double(arc.getEndPoint().getX(), plotArea.getY(),
                                               plotArea.getMaxX() - arc.getEndPoint().getX(),
                                               plotArea.getHeight()));
                    side2.intersect(back);
                    g2.setPaint(paint);
                    g2.fill(side2);
                    g2.setPaint(Color.lightGray);
                    g2.draw(side2);

                    Area side1 = new Area(
                        new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(),
                                               plotArea.getMaxX() - arc.getStartPoint().getX(),
                                               plotArea.getHeight()));
                    side1.intersect(front);
                    g2.setPaint(paint);
                    g2.fill(side1);
                    g2.setPaint(Color.lightGray);
                    g2.draw(side1);
                }
            }
            else {  // segment starts at back

                if (!isAngleAtFront(end)) {
                    if (extent < 180.0) {  // and finishes at back
                        Area side = new Area(
                            new Rectangle2D.Double(arc.getEndPoint().getX(), plotArea.getY(),
                                                   arc.getStartPoint().getX()
                                                   - arc.getEndPoint().getX(),
                                                   plotArea.getHeight()));
                        side.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side);
                        g2.setPaint(Color.lightGray);
                        g2.draw(side);
                    }
                    else {  // starts at back and wraps right around to the back again
                        Area side1 = new Area(
                            new Rectangle2D.Double(arc.getStartPoint().getX(), plotArea.getY(),
                                                   plotArea.getX() - arc.getStartPoint().getX(),
                                                   plotArea.getHeight()));
                        side1.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.fill(front);
                        Area side2 = new Area(
                            new Rectangle2D.Double(arc.getEndPoint().getX(),
                                                   plotArea.getY(),
                                                   plotArea.getMaxX() - arc.getEndPoint().getX(),
                                                   plotArea.getHeight()));
                        side2.intersect(back);
                        g2.fill(side2);
                        g2.setPaint(Color.lightGray);
                        g2.draw(side1);
                        g2.draw(back);
                        g2.draw(side2);
                    }
                }
                else {  // starts at the back and finishes at the front (wrapping the left side)
                    Area side1 = new Area(
                        new Rectangle2D.Double(plotArea.getX(), plotArea.getY(),
                                               arc.getStartPoint().getX() - plotArea.getX(),
                                               plotArea.getHeight()));
                    side1.intersect(back);
                    g2.setPaint(paint);
                    g2.fill(side1);
                    g2.setPaint(Color.lightGray);
                    g2.draw(side1);

                    Area side2 = new Area(
                        new Rectangle2D.Double(plotArea.getX(), plotArea.getY(),
                                               arc.getEndPoint().getX() - plotArea.getX(),
                                               plotArea.getHeight()));
                    side2.intersect(front);
                    g2.setPaint(paint);
                    g2.fill(side2);
                    g2.setPaint(Color.lightGray);
                    g2.draw(side2);

                }
            }

        }

    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return <i>Pie 3D Plot</i>.
     */
    public String getPlotType () {
        return "Pie 3D Plot";
    }

    /**
     * A utility method that returns true if the angle represents a point at the front of the
     * 3D pie chart.  0 - 180 degrees is the back, 180 - 360 is the front.
     *
     * @param angle  the angle.
     *
     * @return true if the angle is at the front of the pie.
     */
    private boolean isAngleAtFront(double angle) {

        return (Math.sin(Math.toRadians(angle)) < 0.0);

    }

    /**
     * A utility method that returns true if the angle represents a point at the back of the
     * 3D pie chart.  0 - 180 degrees is the back, 180 - 360 is the front.
     *
     * @param angle  the angle.
     *
     * @return true if the angle is at the back of the pie.
     */
    private boolean isAngleAtBack(double angle) {

        return (Math.sin(Math.toRadians(angle)) > 0.0);

    }

}
