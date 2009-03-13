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
 * -----------------
 * ChartTiming1.java
 * -----------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: ChartTiming1.java,v 1.1.1.1 2003/07/17 10:06:32 Ian.Mayo Exp $
 *
 * Changes (from 24-Apr-2002)
 * --------------------------
 * 24-Apr-2002 : Added standard header (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.data.DefaultPieDataset;

/**
 * Draws a pie chart over and over for 10 seconds.  Reports on how many redraws were achieved.
 *
 * @author DG
 */
public class ChartTiming1 {

    /** A flag that indicates when time is up. */
    private boolean finished;

    /**
     * Creates a new application.
     */
    public ChartTiming1() {

        finished = false;

        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("One", new Double(10.3));
        data.setValue("Two", new Double(8.5));
        data.setValue("Three", new Double(3.9));
        data.setValue("Four", new Double(3.9));
        data.setValue("Five", new Double(3.9));
        data.setValue("Six", new Double(3.9));

        // create a pie chart...
        boolean withLegend = true;
        JFreeChart chart = ChartFactory.createPieChart("ToolTip Example", data, withLegend);

        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        Rectangle2D chartArea = new Rectangle2D.Double(0, 0, 400, 300);

        TimerThread timer = new TimerThread(10000L, this);
        int count = 0;
        timer.start();
        while (!finished) {
            chart.draw(g2, chartArea, null);
            System.out.println("Charts drawn..." + count);
            if (!finished) {
                count++;
            }
        }
        System.out.println("DONE");

    }

    /**
     * Sets the flag that signals time up.
     *
     * @param flag  the flag.
     */
    public void setFinished(boolean flag) {
        this.finished = flag;
    }

    /**
     * Starting point for the application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

    }
}

/**
 * A simple timer thread.
 *
 * @author DG
 */
class TimerThread extends Thread {

    /** The time counter. */
    private long millis;

    /** The application. */
    private ChartTiming1 application;

    /**
     * Creates a new timer thread.
     *
     * @param millis  the number of milliseconds.
     * @param application  the application.
     */
    public TimerThread(long millis, ChartTiming1 application) {
        this.millis = millis;
        this.application = application;
    }

    /**
     * Starts the timer thread running.
     */
    public void run() {
        try {
            sleep(millis);
            application.setFinished(true);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
