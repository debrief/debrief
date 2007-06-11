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
 * ---------------
 * ChartFrame.java
 * ---------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: ChartFrame.java,v 1.1.1.1 2003/07/17 10:06:21 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 22-Nov-2001 : Version 1 (DG);
 * 08-Jan-2001 : Added chartPanel attribute (DG);
 * 24-May-2002 : Renamed JFreeChartFrame --> ChartFrame (DG);
 *
 */

package com.jrefinery.chart;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * A frame for displaying a chart.
 *
 * @author DG
 */
public class ChartFrame extends JFrame {

    /** The chart panel. */
    private ChartPanel chartPanel;

    /**
     * Constructs a frame for a chart.
     *
     * @param title  the frame title.
     * @param chart  the chart.
     */
    public ChartFrame(String title, JFreeChart chart) {
        this(title, chart, false);
    }

    /**
     * Constructs a frame for a chart.
     *
     * @param title  the frame title.
     * @param chart  the chart.
     * @param scrollPane  iIf <code>true</code>, put the Chart(Panel) into a JScrollPane.
     */
    public ChartFrame(String title, JFreeChart chart, boolean scrollPane) {

        super(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartPanel = new ChartPanel(chart);
        if (scrollPane) {
            setContentPane(new JScrollPane(chartPanel));
        }
        else {
            setContentPane(chartPanel);
        }

    }

    /**
     * Returns the chart panel for the frame.
     *
     * @return the chart panel.
     */
    public ChartPanel getChartPanel() {
        return this.chartPanel;
    }

}
