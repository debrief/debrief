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
 * ----------------------------
 * StandardPieURLGenerator.java
 * ----------------------------
 * (C) Copyright 2002, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributors:     David Gilbert (for Simba Management Limited);
 *
 * $Id: StandardPieURLGenerator.java,v 1.1.1.1 2003/07/17 10:06:48 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */
package com.jrefinery.legacy.chart.urls;

import com.jrefinery.legacy.data.PieDataset;

/**
 * A URL generator.
 *
 * @author RA
 */
public class StandardPieURLGenerator implements PieURLGenerator {

    /** The prefix. */
    private String prefix = "index.html";

    /** The category parameter name. */
    private String categoryParameterName = "category";

    /**
     * Default constructor.
     */
    public StandardPieURLGenerator() {
    }

    /**
     * Creates a new generator.
     *
     * @param prefix  the prefix.
     */
    public StandardPieURLGenerator(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Creates a new generator.
     *
     * @param prefix  the prefix.
     * @param categoryParameterName  the category parameter name.
     */
    public StandardPieURLGenerator(String prefix, String categoryParameterName) {
        this.prefix = prefix;
        this.categoryParameterName = categoryParameterName;
    }

    /**
     * Generates a URL.
     *
     * @param data  the dataset.
     * @param category  the category.
     *
     * @return a string containing the generated URL.
     */
    public String generateURL(PieDataset data, Object category) {
        String url = prefix;
        if (url.indexOf("?") > -1) {
            url += "&" + this.categoryParameterName + "=" + category.toString();
        }
        else {
            url += "?" + this.categoryParameterName + "=" + category.toString();
        }
        return url;
    }
}
