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
 * DateUnit.java
 * -------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DateUnit.java,v 1.1.1.1 2003/07/17 10:06:22 Ian.Mayo Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 13-Mar-2002 : Updated Javadoc comments (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.util.Date;
import java.util.Calendar;

/**
 * Represents a fixed unit of time, used to represent the tick units on a DateAxis.
 *
 * @author DG
 *
 * @deprecated use DateTickUnit instead.
 */
public class DateUnit {

    /** The field (see java.util.Calendar) used to define the DateUnit. */
    private int field;

    /** The number of units. */
    private int count;

    /**
     * Constructs a DateUnit.
     * <p>
     * The DateUnit is specified using one of the following fields from the
     * java.util.Calendar class:  YEAR, MONTH, DATE or DAY_OF_MONTH,
     * HOUR_OF_DAY (not HOUR!), MINUTE, SECOND or MILLISECOND.
     * <p>
     * You also specify the number of units (for example, you might specify
     * 3 months if you have quarterly data, or 7 days, or 1 hour).
     * <p>
     *
     * @param field  the date field.
     * @param count  the number of units.
     */
    public DateUnit(int field, int count) {
        this.field = field;
        this.count = count;
    }

    /**
     * Returns the field used to define the DateUnit.  This should be one of
     * the following constants defined in the java.util.Calendar class:  YEAR,
     * MONTH, DATE or DAY_OF_MONTH, HOUR_OF_DAY (not HOUR!), MINUTE, SECOND or
     * MILLISECOND.
     *
     * @return the field used to define the DateUnit.
     */
    public int getField() {
        return this.field;
    }

    /**
     * Returns the number of units.
     *
     * @return the number of units.
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Calculates a new date by adding this DateUnit to the base date.
     *
     * @param base  the base date.
     *
     * @return a new date one DateUnit after the base date.
     */
    public Date addToDate(Date base) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);
        calendar.add(this.field, this.count);
        return calendar.getTime();

    }

}
