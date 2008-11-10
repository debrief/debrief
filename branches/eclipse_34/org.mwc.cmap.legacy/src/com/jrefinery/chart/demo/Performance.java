/* ===============
 * JFreeChart Demo
 * ===============
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
 * Performance.java
 * ----------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited).
 * Contributor(s):   -;
 *
 * $Id: Performance.java,v 1.1.1.1 2003/07/17 10:06:35 Ian.Mayo Exp $
 *
 * Changes (since 11-Oct-2002)
 * ---------------------------
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */
package com.jrefinery.chart.demo;

import java.awt.geom.Line2D;
import java.util.Date;

/**
 * A basic performance test for a couple of common operations.
 *
 * @author DG
 */
public class Performance {

    /** The value. */
    private double value = 2.0;

    /** The number. */
    private Double number = new Double(value);

    /**
     * Default constructor.
     */
    public Performance() {
    }

    /**
     * Creates lines in a loop.
     *
     * @param count  the number of lines to create.
     */
    public void createLines(int count) {

        for (int i = 0; i < count; i++) {
            Line2D line = new Line2D.Double(1.0, 1.0, 1.0, 1.0);
        }

    }

    /**
     * Creates one line, then repeatedly calls the setLine method.
     *
     * @param count  the number of times to call the setLine method.
     */
    public void setLines(int count) {

        Line2D line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);
        for (int i = 0; i < count; i++) {
            line.setLine(1.0, 1.0, 1.0, 1.0);
        }

    }

    /**
     * Repeatedly grabs a value from a Number instance.
     *
     * @param count  the number of times to call doubleValue().
     */
    public void getNumber(int count) {

        for (int i = 0; i < count; i++) {
            double d = this.number.doubleValue();
        }

    }

    /**
     * Repeatedly grabs a value from a double.
     *
     * @param count  the number of times to fetch the value.
     */
    public void getValue(int count) {

        for (int i = 0; i < count; i++) {
            double d = this.value;
        }

    }

    /**
     * Writes the current time to the console.
     *
     * @param text  the prefix.
     * @param time  the time.
     */
    public void writeTime(String text, Date time) {

        System.out.println(text + " : " + time.getTime());

    }

    /**
     * Starting point for the application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        Performance p = new Performance();
        System.out.println("Simple performance tests.");

        Date start1 = new Date();
        p.createLines(100000);
        Date end1 = new Date();

        Date start2 = new Date();
        p.setLines(100000);
        Date end2 = new Date();

        p.writeTime("Start create lines", start1);
        p.writeTime("Finish create lines", end1);
        p.writeTime("Start set lines", start2);
        p.writeTime("Finish set lines", end2);

        Date start3 = new Date();
        p.getNumber(1000000);
        Date end3 = new Date();

        Date start4 = new Date();
        p.getValue(1000000);
        Date end4 = new Date();

        p.writeTime("Start get number", start3);
        p.writeTime("Finish get number", end3);
        p.writeTime("Start get value", start4);
        p.writeTime("Finish get value", end4);


    }

}
