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
 * ----------------
 * ChartEntity.java
 * ----------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: ChartEntity.java,v 1.1.1.1 2003/07/17 10:06:40 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 23-May-2002 : Version 1 (DG);
 * 12-Jun-2002 : Added Javadoc comments (DG);
 * 26-Jun-2002 : Added methods for image maps (DG);
 * 05-Aug-2002 : Added constructor and accessors for URL support in image maps
 *               Added getImageMapAreaTag() - previously in subclasses (RA);
 * 05-Sep-2002 : Added getImageMapAreaTag(boolean) to support OverLIB for tooltips
 *               http://www.bosrup.com/web/overlib (RA);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 08-Oct-2002 : Changed getImageMapAreaTag to use title instead of alt attribute so HTML
 *               image maps now work in Mozilla and Opera as well as Internet Explorer (RA);
 *
 */

package com.jrefinery.chart.entity;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.PathIterator;

/**
 * A class that captures information about some component of a chart (a bar, line etc).
 *
 * @author DG
 */
public class ChartEntity {

    /** The area occupied by the entity (in Java 2D space). */
    private Shape area;

    /** The tool tip text for the entity. */
    private String toolTipText;

    /** The URL text for the entity. */
    private String urlText;

    /**
     * Creates a new entity.
     *
     * @param area  the area.
     * @param toolTipText  the tool tip text (if any).
     */
    public ChartEntity(Shape area, String toolTipText) {
        this(area, toolTipText, null);
    }

    /**
     * Creates a new entity.
     *
     * @param area  the area.
     * @param toolTipText  the tool tip text (if any).
     * @param urlText  the URL text for HTML image maps (if any).
     */
    public ChartEntity(Shape area, String toolTipText, String urlText) {
        this.area = area;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }

    /**
     * Returns the area occupied by the entity (in Java 2D space).
     *
     * @return the area.
     */
    public Shape getArea() {
        return this.area;
    }

    /**
     * Sets the area for the entity.
     * <P>
     * This class conveys information about chart entities back to a client.
     * Setting this area doesn't change the entity (which has already been
     * drawn).
     *
     * @param area  the area.
     */
    public void setArea(Shape area) {
        this.area = area;
    }

    /**
     * Returns the tool tip text for the entity.
     *
     * @return the tool tip text (if any).
     */
    public String getToolTipText() {
        return this.toolTipText;
    }

    /**
     * Sets the tool tip text.
     *
     * @param text  the text.
     */
    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    /**
     * Returns a string describing the entity area.  This string is intended
     * for use in an AREA tag when generating an image map.
     *
     * @return the shape type.
     */
    public String getShapeType() {
        if (this.area instanceof Rectangle2D) {
            return "RECT";
        }
        else {
            return "POLY";
        }
    }

    /**
     * Returns the shape coordinates as a string.
     *
     * @return the shape coordinates.
     */
    public String getShapeCoords() {
        if (this.area instanceof Rectangle2D) {
            return getRectCoords((Rectangle2D) this.area);
        }
        else {
            return getPolyCoords(this.area);
        }
    }

    /**
     * Returns a string containing the coordinates (x1, y1, x2, y2) for a given
     * rectangle.  This string is intended for use in an image map.
     *
     * @param rectangle  the rectangle.
     *
     * @return upper left and lower right corner of a rectangle.
     */
    private String getRectCoords(Rectangle2D rectangle) {

        int x1 = (int) rectangle.getX();
        int y1 = (int) rectangle.getY();
        int x2 = x1 + (int) rectangle.getWidth();
        int y2 = y1 + (int) rectangle.getHeight();
        return x1 + "," + y1 + "," + x2 + "," + y2;
    }

    /**
     * Returns a string containing the coordinates for a given shape.  This
     * string is intended for use in an image map.
     *
     * @param shape  the shape.
     *
     * @return the coordinates for a given shape as string.
     */
    private String getPolyCoords(Shape shape) {

        String result = "";
        boolean first = true;
        float[] coords = new float[6];
        PathIterator pi = shape.getPathIterator(null, 1.0);
        while (pi.isDone() == false) {
            pi.currentSegment(coords);
            if (first) {
                first = false;
                result = result + (int) coords[0] + "," + (int) coords[1];
            }
            else {
                result = result + "," + (int) coords[0] + "," + (int) coords[1];
            }
            pi.next();
        }
        return result;
    }

    /**
     * Returns an HTML image map tag tag for this entity.
     *
     * @return the HTML tag
     */
    public String getImageMapAreaTag() {
        return this.getImageMapAreaTag(false);
    }

    /**
     * Returns an HTML image map tag tag for this entity.
     *
     * @param useOverLibForToolTips Whether to use OverLIB for tooltips
     *        (http://www.bosrup.com/web/overlib/).
     *
     * @return The HTML tag
     */
    public String getImageMapAreaTag(boolean useOverLibForToolTips) {
        String tag = "<AREA SHAPE=\"" + getShapeType() + "\""
                   + " COORDS=\"" + getShapeCoords() + "\"";
        if (this.urlText == null ? false : !this.urlText.equals("")) {
            tag += " href=\"" + this.urlText + "\"";
        }
        if (this.toolTipText == null ? false : !this.toolTipText.equals("")) {
            if (useOverLibForToolTips) {
                tag += " onmouseover=\"return overlib('" + this.toolTipText
                        + "');\" onmouseout=\"return nd();\"";
            }
            else {
                tag += " title=\"" + this.toolTipText + "\"";
            }
        }
        tag += ">";
        return tag;
    }

    /**
     * Returns the URL text for the entity.
     *
     * @return the URL text (if any).
     */
    public String getURLText() {
        return this.urlText;
    }

    /**
     * Sets the URL text.
     *
     * @param text the text.
     */
    public void setURLText(String text) {
        this.urlText = text;
    }

}
