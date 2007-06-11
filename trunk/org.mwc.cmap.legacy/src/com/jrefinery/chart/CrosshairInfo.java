/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * ------------------
 * CrosshairInfo.java
 * ------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: CrosshairInfo.java,v 1.1.1.1 2003/07/17 10:06:22 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 24-Jan-2002 : Version 1 (DG);
 * 05-Mar-2002 : Added Javadoc comments (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

/**
 * Maintains information about crosshairs on a plot.
 *
 * @author DG
 */
public class CrosshairInfo {

    /** The x-value for the anchor point. */
    private double anchorX;

    /** The y-value for the anchor point. */
    private double anchorY;

    /** The x-value for the crosshair point. */
    private double crosshairX;

    /** The y-value for the crosshair point. */
    private double crosshairY;

    /** The smallest distance so far between the anchor point and a data point. */
    private double distance;

    /**
     * Default constructor.
     */
    public CrosshairInfo() {
    }

    /**
     * Sets the distance.
     *
     * @param distance  the distance.
     */
    public void setCrosshairDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Evaluates a data point and if it is the closest to the anchor point it
     * becomes the new crosshair point.
     * <P>
     * To understand this method, you need to know the context in which it will
     * be called.  An instance of this class is passed to an XYItemRenderer as
     * each data point is plotted.  As the point is plotted, it is passed to
     * this method to see if it should be the new crosshair point.
     *
     * @param candidateX  x position of candidate for the new crosshair point.
     * @param candidateY  y position of candidate for the new crosshair point.
     */
    public void updateCrosshairPoint(double candidateX, double candidateY) {

        double d = (candidateX - anchorX) * (candidateX - anchorX)
                 + (candidateY - anchorY) * (candidateY - anchorY);

        if (d < distance) {
            crosshairX = candidateX;
            crosshairY = candidateY;
            distance = d;
        }

    }

    /**
     * Evaluates an x-value and if it is the closest to the anchor point it
     * becomes the new crosshair point.
     * <P>
     * Used in cases where only the x-axis is numerical.
     *
     * @param candidateX  x position of the candidate for the new crosshair point.
     */
    public void updateCrosshairX(double candidateX) {

        double d = Math.abs(candidateX - anchorX);
        if (d < distance) {
            crosshairX = candidateX;
            distance = d;
        }

    }

    /**
     * Evaluates a y-value and if it is the closest to the anchor point it
     * becomes the new crosshair point.
     * <P>
     * Used in cases where only the y-axis is numerical.
     *
     * @param candidateY  y position of the candidate for the new crosshair point.
     */
    public void updateCrosshairY(double candidateY) {

        double d = Math.abs(candidateY - anchorY);
        if (d < distance) {
            crosshairY = candidateY;
            distance = d;
        }

    }

    /**
     * Set the x-value for the anchor point.
     *
     * @param x  the x position.
     */
    public void setAnchorX(double x) {
        this.anchorX = x;
    }

    /**
     * Set the y-value for the anchor point.
     *
     * @param y  the y position.
     */
    public void setAnchorY(double y) {
        this.anchorY = y;
    }

    /**
     * Get the x-value for the crosshair point.
     *
     * @return the x position of the crosshair point.
     */
    public double getCrosshairX() {
        return this.crosshairX;
    }

    /**
     * Get the y-value for the crosshair point.
     *
     * @return the y position of the crosshair point.
     */
    public double getCrosshairY() {
        return this.crosshairY;
    }

}
