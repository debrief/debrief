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
 * -------------
 * TickUnit.java
 * -------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: NumberTickUnit.java,v 1.1.1.1 2003/07/17 10:06:25 Ian.Mayo Exp $
 *
 * Changes (from 19-Dec-2001)
 * --------------------------
 * 19-Dec-2001 : Added standard header (DG);
 * 01-May-2002 : Updated for changed to TickUnit class (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.text.NumberFormat;

/**
 * A numerical tick unit.
 *
 * @author DG
 */
public class NumberTickUnit extends TickUnit {

    /** A formatter for the tick unit. */
    private NumberFormat formatter;

    /**
     * Creates a new number tick unit.
     *
     * @param size  the size of the tick unit.
     * @param formatter  a number formatter for the tick unit.
     */
    public NumberTickUnit(double size, NumberFormat formatter) {
        super(size);
        this.formatter = formatter;
    }

    /**
     * Converts a value to a string.
     *
     * @param value  the value.
     *
     * @return the formatted string.
     */
    public String valueToString(double value) {
        return formatter.format(value);
    }

}
