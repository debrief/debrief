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
 * --------------
 * TickUnits.java
 * --------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TickUnits.java,v 1.1.1.1 2003/07/17 10:06:28 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 23-Nov-2001 : Version 1 (DG);
 * 18-Feb-2002 : Fixed bug in getNearestTickUnit (thanks to Mario Inchiosa for reporting this,
 *               SourceForge bug id 518073) (DG);
 * 25-Feb-2002 : Moved createStandardTickUnits() method from NumberAxis, and added
 *               createIntegerTickUnits() method (DG);
 * 01-May-2002 : Updated for changes to the TickUnit class (DG);
 * 18-Sep-2002 : Added standardTickUnit methods which take a Locale instance (AS);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.List;

/**
 * A collection of tick units.
 * <P>
 * Used by the DateAxis and NumberAxis classes.
 *
 * @author DG
 */
public class TickUnits {

    /** Storage for the tick units. */
    @SuppressWarnings("unchecked")
		private List units;

    /**
     * Constructs a new collection of tick units.
     */
    @SuppressWarnings("unchecked")
		public TickUnits() {
        this.units = new java.util.ArrayList();
    }

    /**
     * Adds a tick unit to the collection.
     * <P>
     * The tick units are maintained in ascending order.
     *
     * @param unit  the tick unit to add.
     */
    @SuppressWarnings("unchecked")
		public void add(TickUnit unit) {

        units.add(unit);
        Collections.sort(units);

    }

    /**
     * Returns a tick unit that is larger than the supplied unit.
     *
     * @param unit   the unit.
     *
     * @return a tick unit that is larger than the supplied unit.
     */
    @SuppressWarnings("unchecked")
		public TickUnit getLargerTickUnit(TickUnit unit) {

        int index = Collections.binarySearch(units, unit);
        if (index >= 0) {
            index = index + 1;
        }
        else {
            index = -index;
        }

        return (TickUnit) units.get(Math.min(index, units.size() - 1));

    }

    /**
     * Returns the tick unit in the collection that is greater than or equal
     * to (in size) the specified unit.
     *
     * @param unit  the unit.
     *
     * @return a unit from the collection.
     */
    @SuppressWarnings("unchecked")
		public TickUnit getCeilingTickUnit(TickUnit unit) {

        int index = Collections.binarySearch(units, unit);
        if (index >= 0) {
            return (TickUnit) units.get(index);
        }
        else {
            index = -(index + 1);
            return (TickUnit) units.get(Math.min(index, units.size() - 1));
        }

    }

    /**
     * Returns the tick unit in the collection that is greater than or equal
     * to the specified size.
     *
     * @param size  the size.
     *
     * @return a unit from the collection.
     */
    public TickUnit getCeilingTickUnit(double size) {

        return getCeilingTickUnit(new NumberTickUnit(size, null));

    }

    /**
     * Creates the standard tick units.
     * <P>
     * If you don't like these defaults, create your own instance of TickUnits
     * and then pass it to the setStandardTickUnits(...) method in the
     * NumberAxis class.
     *
     * @return the standard tick units.
     *
     * @deprecated this method has been moved to the NumberAxis class.
     */
    public static TickUnits createStandardTickUnits() {

        TickUnits units = new TickUnits();

        // we can add the units in any order, the TickUnits collection will sort them...
        units.add(new NumberTickUnit(0.0000001,    new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(0.000001,     new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(0.00001,      new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(0.0001,       new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(0.001,        new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(0.01,         new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(0.1,          new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(1,            new DecimalFormat("0")));
        units.add(new NumberTickUnit(10,           new DecimalFormat("0")));
        units.add(new NumberTickUnit(100,          new DecimalFormat("0")));
        units.add(new NumberTickUnit(1000,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(100000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(1000000,      new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(10000000,     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(100000000,    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(1000000000,   new DecimalFormat("#,###,###,##0")));

        units.add(new NumberTickUnit(0.00000025,   new DecimalFormat("0.00000000")));
        units.add(new NumberTickUnit(0.0000025,    new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(0.000025,     new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(0.00025,      new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(0.0025,       new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(0.025,        new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(0.25,         new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(2.5,          new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(25,           new DecimalFormat("0")));
        units.add(new NumberTickUnit(250,          new DecimalFormat("0")));
        units.add(new NumberTickUnit(2500,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(25000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(250000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2500000,      new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(25000000,     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(250000000,    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(2500000000.0,   new DecimalFormat("#,###,###,##0")));

        units.add(new NumberTickUnit(0.0000005,    new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(0.000005,     new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(0.00005,      new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(0.0005,       new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(0.005,        new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(0.05,         new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(0.5,          new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(5L,           new DecimalFormat("0")));
        units.add(new NumberTickUnit(50L,          new DecimalFormat("0")));
        units.add(new NumberTickUnit(500L,         new DecimalFormat("0")));
        units.add(new NumberTickUnit(5000L,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(50000L,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(500000L,      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5000000L,     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(50000000L,    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(500000000L,   new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(5000000000L,  new DecimalFormat("#,###,###,##0")));

        return units;

    }

    /**
     * Returns a collection of tick units for integer values.
     *
     * @return a collection of tick units for integer values.
     *
     * @deprecated this method has been moved to the NumberAxis class.
     */
    public static TickUnits createIntegerTickUnits() {

        TickUnits units = new TickUnits();

        units.add(new NumberTickUnit(1,              new DecimalFormat("0")));
        units.add(new NumberTickUnit(2,              new DecimalFormat("0")));
        units.add(new NumberTickUnit(5,              new DecimalFormat("0")));
        units.add(new NumberTickUnit(10,             new DecimalFormat("0")));
        units.add(new NumberTickUnit(20,             new DecimalFormat("0")));
        units.add(new NumberTickUnit(50,             new DecimalFormat("0")));
        units.add(new NumberTickUnit(100,            new DecimalFormat("0")));
        units.add(new NumberTickUnit(200,            new DecimalFormat("0")));
        units.add(new NumberTickUnit(500,            new DecimalFormat("0")));
        units.add(new NumberTickUnit(1000,           new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2000,           new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5000,           new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10000,          new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(20000,          new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(50000,          new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(100000,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(200000,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(500000,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(1000000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2000000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5000000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10000000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(20000000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(50000000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(100000000,      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(200000000,      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(500000000,      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(1000000000,     new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2000000000,     new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5000000000.0,   new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10000000000.0,  new DecimalFormat("#,##0")));

        return units;

    }

    /**
     * Creates the standard tick units, and uses a given Locale to create the DecimalFormats
     * <P>
     * If you don't like these defaults, create your own instance of TickUnits
     * and then pass it to the setStandardTickUnits(...) method in the
     * NumberAxis class.
     *
     * @param locale the locale to use to represent Numbers.
     *
     * @return the standard tick units.
     *
     * @deprecated this method has been moved to the NumberAxis class.
     */
    public static TickUnits createStandardTickUnits(Locale locale) {

        TickUnits units = new TickUnits();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

        // we can add the units in any order, the TickUnits collection will sort them...
        units.add(new NumberTickUnit(0.0000001,    numberFormat));
        units.add(new NumberTickUnit(0.000001,     numberFormat));
        units.add(new NumberTickUnit(0.00001,      numberFormat));
        units.add(new NumberTickUnit(0.0001,       numberFormat));
        units.add(new NumberTickUnit(0.001,        numberFormat));
        units.add(new NumberTickUnit(0.01,         numberFormat));
        units.add(new NumberTickUnit(0.1,          numberFormat));
        units.add(new NumberTickUnit(1,            numberFormat));
        units.add(new NumberTickUnit(10,           numberFormat));
        units.add(new NumberTickUnit(100,          numberFormat));
        units.add(new NumberTickUnit(1000,         numberFormat));
        units.add(new NumberTickUnit(10000,        numberFormat));
        units.add(new NumberTickUnit(100000,       numberFormat));
        units.add(new NumberTickUnit(1000000,      numberFormat));
        units.add(new NumberTickUnit(10000000,     numberFormat));
        units.add(new NumberTickUnit(100000000,    numberFormat));
        units.add(new NumberTickUnit(1000000000,   numberFormat));

        units.add(new NumberTickUnit(0.00000025,   numberFormat));
        units.add(new NumberTickUnit(0.0000025,    numberFormat));
        units.add(new NumberTickUnit(0.000025,     numberFormat));
        units.add(new NumberTickUnit(0.00025,      numberFormat));
        units.add(new NumberTickUnit(0.0025,       numberFormat));
        units.add(new NumberTickUnit(0.025,        numberFormat));
        units.add(new NumberTickUnit(0.25,         numberFormat));
        units.add(new NumberTickUnit(2.5,          numberFormat));
        units.add(new NumberTickUnit(25,           numberFormat));
        units.add(new NumberTickUnit(250,          numberFormat));
        units.add(new NumberTickUnit(2500,         numberFormat));
        units.add(new NumberTickUnit(25000,        numberFormat));
        units.add(new NumberTickUnit(250000,       numberFormat));
        units.add(new NumberTickUnit(2500000,      numberFormat));
        units.add(new NumberTickUnit(25000000,     numberFormat));
        units.add(new NumberTickUnit(250000000,    numberFormat));
        units.add(new NumberTickUnit(2500000000.0,   numberFormat));

        units.add(new NumberTickUnit(0.0000005,    numberFormat));
        units.add(new NumberTickUnit(0.000005,     numberFormat));
        units.add(new NumberTickUnit(0.00005,      numberFormat));
        units.add(new NumberTickUnit(0.0005,       numberFormat));
        units.add(new NumberTickUnit(0.005,        numberFormat));
        units.add(new NumberTickUnit(0.05,         numberFormat));
        units.add(new NumberTickUnit(0.5,          numberFormat));
        units.add(new NumberTickUnit(5L,           numberFormat));
        units.add(new NumberTickUnit(50L,          numberFormat));
        units.add(new NumberTickUnit(500L,         numberFormat));
        units.add(new NumberTickUnit(5000L,        numberFormat));
        units.add(new NumberTickUnit(50000L,       numberFormat));
        units.add(new NumberTickUnit(500000L,      numberFormat));
        units.add(new NumberTickUnit(5000000L,     numberFormat));
        units.add(new NumberTickUnit(50000000L,    numberFormat));
        units.add(new NumberTickUnit(500000000L,   numberFormat));
        units.add(new NumberTickUnit(5000000000L,  numberFormat));

        return units;

    }

    /**
     * Returns a collection of tick units for integer values.
     * Uses a given Locale to create the DecimalFormats.
     *
     * @param locale the locale to use to represent Numbers.
     *
     * @return a collection of tick units for integer values.
     *
     * @deprecated this method has been moved to the NumberAxis class.
     */
    public static TickUnits createIntegerTickUnits(Locale locale) {

        TickUnits units = new TickUnits();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

        units.add(new NumberTickUnit(1,              numberFormat));
        units.add(new NumberTickUnit(2,              numberFormat));
        units.add(new NumberTickUnit(5,              numberFormat));
        units.add(new NumberTickUnit(10,             numberFormat));
        units.add(new NumberTickUnit(20,             numberFormat));
        units.add(new NumberTickUnit(50,             numberFormat));
        units.add(new NumberTickUnit(100,            numberFormat));
        units.add(new NumberTickUnit(200,            numberFormat));
        units.add(new NumberTickUnit(500,            numberFormat));
        units.add(new NumberTickUnit(1000,           numberFormat));
        units.add(new NumberTickUnit(2000,           numberFormat));
        units.add(new NumberTickUnit(5000,           numberFormat));
        units.add(new NumberTickUnit(10000,          numberFormat));
        units.add(new NumberTickUnit(20000,          numberFormat));
        units.add(new NumberTickUnit(50000,          numberFormat));
        units.add(new NumberTickUnit(100000,         numberFormat));
        units.add(new NumberTickUnit(200000,         numberFormat));
        units.add(new NumberTickUnit(500000,         numberFormat));
        units.add(new NumberTickUnit(1000000,        numberFormat));
        units.add(new NumberTickUnit(2000000,        numberFormat));
        units.add(new NumberTickUnit(5000000,        numberFormat));
        units.add(new NumberTickUnit(10000000,       numberFormat));
        units.add(new NumberTickUnit(20000000,       numberFormat));
        units.add(new NumberTickUnit(50000000,       numberFormat));
        units.add(new NumberTickUnit(100000000,      numberFormat));
        units.add(new NumberTickUnit(200000000,      numberFormat));
        units.add(new NumberTickUnit(500000000,      numberFormat));
        units.add(new NumberTickUnit(1000000000,     numberFormat));
        units.add(new NumberTickUnit(2000000000,     numberFormat));
        units.add(new NumberTickUnit(5000000000.0,   numberFormat));
        units.add(new NumberTickUnit(10000000000.0,  numberFormat));

        return units;

    }

}
