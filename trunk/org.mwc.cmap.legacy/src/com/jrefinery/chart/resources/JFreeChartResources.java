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
 * ------------------------
 * JFreeChartResources.java
 * ------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartResources.java,v 1.1.1.1 2003/07/17 10:06:43 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 01-Oct-2002 : Version 1 (DG);
 * 16-Oct-2002 : Changed version number to 0.9.4 (DG);
 *
 */
package com.jrefinery.chart.resources;

import java.util.ListResourceBundle;

/**
 * Localised resources for JFreeChart.
 *
 * @author DG
 */
public class JFreeChartResources extends ListResourceBundle {

    /**
     * Returns the array of strings in the resource bundle.
     *
     * @return the array of strings in the resource bundle.
     */
    public Object[][] getContents() {
        return CONTENTS;
    }

    /** The resources to be localised. */
    private static final Object[][] CONTENTS = {

        {"project.name",      "JFreeChart"},
        {"project.version",   "0.9.4"},
        {"project.info",      "http://www.object-refinery.com/jfreechart/index.html"},
        {"project.copyright", "(C)opyright 2000-2002, by Simba Management Limited and"
                            + " Contributors"}

    };

}
