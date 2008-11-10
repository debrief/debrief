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
 * -----------------
 * JThermometer.java
 * -----------------
 * A plot that displays a single value in a thermometer type display.
 *
 * (C) Copyright 2000-2002, Australian Antarctic Division and Contributors.
 *
 * Original Author:  Bryan Scott.
 * Contributor(s):   David Gilbert (for Simba Management Limited).
 *
 * Changes (from 17-Sep-2002)
 * --------------------------
 * 17-Sep-2002 : Reviewed with Checkstyle utility (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Insets;
import java.awt.CardLayout;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import com.jrefinery.data.DefaultMeterDataset;

/**
 * An initial quick and dirty.  The concept behind this class would be to
 * generate a gui bean that could be used within JBuilder, Netbeans etc...
 *
 * @copyright Copyright (c) 2002
 * @company Australian Antarctic Division
 * @author BRS
 *
 */
public class JThermometer extends JPanel {

    /** The dataset. */
    private DefaultMeterDataset data;

    /** The chart. */
    private JFreeChart chart;

    /** The chart panel. */
    private ChartPanel panel;

    /** The thermometer plot. */
    private ThermometerPlot plot = new ThermometerPlot();

    /**
     * Default constructor.
     */
    public JThermometer() {
        super(new CardLayout());
        plot.setInsets(new Insets(5, 5, 5, 5));
        data = new DefaultMeterDataset();
        data.setRange(new Double(-60000), new Double(60000));
        plot.setData(data);
        chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        panel = new ChartPanel(chart);
        this.add(panel, "Panel");
        setBackground(getBackground());
    }

    /**
     * Adds a title to the chart.
     *
     * @param title  the title.
     */
    public void addTitle(AbstractTitle title) {
        chart.addTitle(title);
    }

    /**
     * Adds a title to the chart.
     *
     * @param title  the title.
     */
    public void addTitle(String title) {
        chart.addTitle(new TextTitle(title));
    }

    /**
     * Adds a title to the chart.
     *
     * @param title  the title.
     * @param font  the title font.
     */
    public void addTitle(String title, Font font) {
        chart.addTitle(new TextTitle(title, font));
    }

    /**
     * Sets the value format for the thermometer.
     *
     * @param df  the formatter.
     */
    public void setValueFormat(DecimalFormat df) {
        plot.setValueFormat(df);
    }

    /**
     * Sets the lower and upper bounds for the thermometer.
     *
     * @param lower  the lower bound.
     * @param upper  the upper bound.
     */
    public void setRange(double lower, double upper) {
        plot.setRange(lower, upper);
    }

    /**
     * Sets the range.
     *
     * @param range  the range type.
     * @param displayLow  the low value.
     * @param displayHigh  the high value.
     */
    public void setSubrangeInfo(int range, double displayLow, double displayHigh) {
        plot.setSubrangeInfo(range, displayLow, displayHigh);
    }

    /**
     * Sets the range.
     *
     * @param range  the range type.
     * @param rangeLow  the low value for the range.
     * @param rangeHigh  the high value for the range.
     * @param displayLow  the low value for display.
     * @param displayHigh  the high value for display.
     */
    public void setSubrangeInfo(int range,
                             double rangeLow, double rangeHigh,
                             double displayLow, double displayHigh) {

        plot.setSubrangeInfo(range, rangeLow, rangeHigh, displayLow, displayHigh);

    }

    /**
     * Sets the location at which the temperature value is displayed.
     *
     * @param loc  the location.
     */
    public void setValueLocation(int loc) {
        plot.setValueLocation(loc);
        panel.repaint();
    }

    /**
     * Returns the value of the thermometer.
     *
     * @return the value.
     */
    public Number getValue() {
        if (data != null) {
            return data.getValue();
        }
        else {
            return null;
        }
    }

    /**
     * Sets the value of the thermometer.
     *
     * @param value  the value.
     */
    public void setValue(double value) {
        setValue(new Double(value));
    }

    /**
     * Sets the value of the thermometer.
     *
     * @param value  the value.
     */
    public void setValue(Number value) {
        if (data != null) {
            data.setValue(value);
        }
    }

    /**
     * Sets the unit type.
     *
     * @param i  the unit type.
     */
    public void setUnits(int i) {
        if (plot != null) {
            plot.setUnits(i);
        }
    }

    /**
     * Sets the outline paint.
     *
     * @param p  the paint.
     */
    public void setOutlinePaint(Paint p) {
        if (plot != null) {
            plot.setOutlinePaint(p);
        }
    }

    /**
     * Sets the foreground color.
     *
     * @param fg  the foreground color.
     */
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (plot != null) {
            plot.setThermometerPaint(fg);
        }
    }

    /**
     * Sets the background color.
     *
     * @param bg  the background color.
     */
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (plot != null) {
            plot.setBackgroundPaint(bg);
        }
        if (chart != null) {
            chart.setBackgroundPaint(bg);
        }
        if (panel != null) {
            panel.setBackground(bg);
        }
    }

    /**
     * Sets the value font.
     *
     * @param f  the font.
     */
    public void setValueFont(Font f) {
        if (plot != null) {
            plot.setValueFont(f);
        }
    }

    /**
     * Sets the flag that controls whether or not the display range follows the data value.
     *
     * @param flag  the new value of the flag.
     */
    public void setFollowDataInSubranges(boolean flag) {
        plot.setFollowDataInSubranges(flag);
    }

    /**
     * Sets the flag that controls whether or not value lines are displayed.
     *
     * @param b  the new flag value.
     */
    public void setShowValueLines(boolean b) {
        plot.setShowValueLines(b);
    }

}
