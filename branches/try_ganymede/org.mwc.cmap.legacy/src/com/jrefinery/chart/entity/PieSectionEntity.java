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
 * ---------------------
 * PieSectionEntity.java
 * ---------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: PieSectionEntity.java,v 1.1.1.1 2003/07/17 10:06:40 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 23-May-2002 : Version 1 (DG);
 * 12-Jun-2002 : Added Javadoc comments (DG);
 * 26-Jun-2002 : Added method to generate AREA tag for image map generation (DG);
 * 05-Aug-2002 : Added new constructor to populate URLText
 *               Moved getImageMapAreaTag() to ChartEntity (superclass) (RA);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.entity;

import java.awt.Shape;

/**
 * A chart entity that represents one section within a pie plot.
 *
 * @author DG
 */
public class PieSectionEntity extends ChartEntity {

    /** The category. */
    private Object category;

    /** The category index. */
    private int categoryIndex;

    /**
     * Creates a new pie section entity.
     *
     * @param area  the area.
     * @param toolTipText  the tool tip text.
     * @param category  the category.
     * @param categoryIndex  the category index.
     */
    public PieSectionEntity(Shape area, String toolTipText, Object category, int categoryIndex) {
        super(area, toolTipText);
        this.category = category;
        this.categoryIndex = categoryIndex;
    }

    /**
     * Creates a new pie section entity.
     *
     * @param area  the area.
     * @param toolTipText  the tool tip text.
     * @param urlText  the URL text for HTML image maps.
     * @param category  the category.
     * @param categoryIndex  the category index.
     */
    public PieSectionEntity(Shape area, String toolTipText, String urlText,
                            Object category, int categoryIndex) {
        super(area, toolTipText, urlText);
        this.category = category;
        this.categoryIndex = categoryIndex;
    }

    /**
     * Returns the category.
     *
     * @return the category.
     */
    public Object getCategory() {
        return this.category;
    }

    /**
     * Sets the category.
     *
     * @param category  the category.
     */
    public void setCategory(Object category) {
        this.category = category;
    }

    /**
     * Returns the category index.
     *
     * @return the category index.
     */
    public int getCategoryIndex() {
        return this.categoryIndex;
    }

    /**
     * Sets the category index.
     *
     * @param index  the category index.
     */
    public void setCategoryIndex(int index) {
        this.categoryIndex = index;
    }

    /**
     * Returns a string representing the entity.
     *
     * @return a string representing the entity.
     */
    public String toString() {
        return "Pie section: " + category.toString();
    }

}
