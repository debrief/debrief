/* =======================================
* JFreeChart : a Java Chart Class Library
* =======================================
*
* Project Info:  http://www.jrefinery.com/jfreechart
* Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
*
* This file...
* $Id: DateUnit.java,v 1.2 2004/05/25 15:36:01 Ian.Mayo Exp $
*
* Original Author:  David Gilbert;
* Contributor(s):   -;
*
* (C) Copyright 2000, 2001, Simba Management Limited;
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
* Changes (from 18-Sep-2001)
* --------------------------
* 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
*
*/


package MWC.GUI.ptplot.jfreeChart;

import java.util.*;

/**
 * Represents an amount of time - used to represent the tick units on a MWCDateAxis.
 */
public class DateUnit {

  /** The field (see java.util.Calendar) that the DateUnit is defined in terms of. */
  protected int field;

  /** The number of units (years, months, days, hours, minutes, seconds or milliseconds) in a
   * single DateUnit. */
  protected int count;

  /**
   * Builds a DateUnit.
   */
  public DateUnit(int field, int count) {
    this.field = field;
    this.count = count;
  }

  /**
   * Returns the field used for this DateUnit.
   */
  public int getField() {
    return this.field;
  }

  /**
   * Returns the number of units.
   */
  public int getCount() {
    return this.count;
  }

  /**
   * Adds this unit to the specified Date and returns the new Date.
   */
  public Date addToDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(this.field, this.count);
    return calendar.getTime();
  }

}
