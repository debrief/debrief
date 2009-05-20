/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * ------------------
 * DemoResources.java
 * ------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DemoResources.java,v 1.1.1.1 2003/07/17 10:06:39 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 15-Mar-2002 : Version 1 (DG);
 * 26-Mar-2002 : Changed name from JFreeChartDemoResources.java --> DemoResources.java (DG);
 * 02-Jul-2002 : Added the tabs.X to define tabs in the demo. (BRS)
 * 02-Jul-2002 : Added the chartX.tab to define which tab an example should be
 *               displayed on. (BRS)
 * 02-Jul-2002 : Added the chartX.usage to define where an example should is
 *               applicable (All, Servlet, Swing). (BRS)
 * 02-Jul-2002 : Added Gantt chart resources.
 *
 */

package com.jrefinery.legacy.chart.demo.resources;

import java.util.ListResourceBundle;

/**
 * A resource bundle that stores all the user interface items that might need localisation.
 */
public class DemoResources extends ListResourceBundle {

    /**
     * Returns the array of strings in the resource bundle.
     */
    public Object[][] getContents() {
        return contents;
    }

    /** The resources to be localised. */
    static final Object[][] contents = {

        // about frame...
        { "about.title", "About..."},
        { "about.version.label", "Version"},

        // menu labels...
        { "menu.file", "File"},
        { "menu.file.mnemonic", new Character('F') },

        { "menu.file.exit", "Exit"},
        { "menu.file.exit.mnemonic", new Character('x') },

        { "menu.help", "Help"},
        { "menu.help.mnemonic", new Character('H')},

        { "menu.help.about", "About..."},
        { "menu.help.about.mnemonic", new Character('A')},

        // dialog messages...
        { "dialog.exit.title", "Confirm exit..."},
        { "dialog.exit.message", "Are you sure you want to exit?"},

        // labels for the tabs in the main window...
        // Maximum of Twenty (20) charts per page.
        {"tabs.1", "Bar Charts"},
        {"tabs.2", "Pie Charts"},
        {"tabs.3", "XY Charts"},
        {"tabs.4", "Time Series Charts"},
        {"tabs.5", "Meter Charts"},
        {"tabs.6", "Other Charts"},
        {"tabs.7", "Test Charts"},
        {"tabs.8", "Combined Charts"},

        //{"usage.0","All Applications"},
        //{"usage.1","Swing Only"},
        //{"usage.2","Servlet Only"},

        // sample chart descriptions...
        {"chart1.title",       "Horizontal Bar Chart: "},
        {"chart1.tab",         "1"},
        {"chart1.usage",       "All"},
        {"chart1.description", "Displays horizontal bars, representing data from a "
                              +"CategoryDataset.  Notice that the numerical axis is inverted."},
        {"chart1.zoom",        "false"},

        {"chart2.title",       "Horizontal Stacked Bar Chart: "},
        {"chart2.tab",         "1"},
        {"chart2.usage",       "All"},
        {"chart2.description", "Displays horizontal stacked bars, representing data from a "
                              +"CategoryDataset."},
        {"chart2.zoom",        "false"},

        {"chart3.title",       "Vertical Bar Chart: "},
        {"chart3.tab",         "1"},
        {"chart3.usage",       "All"},
        {"chart3.description", "Displays vertical bars, representing data from a CategoryDataset."},
        {"chart3.zoom",        "false"},

        {"chart4.title",       "Vertical 3D Bar Chart: "},
        {"chart4.tab",         "1"},
        {"chart4.usage",       "All"},
        {"chart4.description", "Displays vertical bars with a 3D effect, representing data from a "
                              +"CategoryDataset."},
        {"chart4.zoom",        "false"},

        {"chart5.title",       "Vertical Stacked Bar Chart: "},
        {"chart5.tab",         "1"},
        {"chart5.usage",       "All"},
        {"chart5.description", "Displays vertical stacked bars, representing data from a "
                              +"CategoryDataset."},
        {"chart5.zoom",        "false"},

        {"chart6.title",       "Vertical Stacked 3D Bar Chart: "},
        {"chart6.tab",         "1"},
        {"chart6.usage",       "All"},
        {"chart6.description", "Displays vertical stacked bars with a 3D effect, representing "
                              +"data from a CategoryDataset."},
        {"chart6.zoom",        "false"},

        {"chart7.title",       "Pie Chart 1: "},
        {"chart7.tab",         "2"},
        {"chart7.usage",       "All"},
        {"chart7.description", "A pie chart showing one section exploded."},
        {"chart7.zoom",        "false"},

        {"chart8.title",       "Pie Chart 2: "},
        {"chart8.tab",         "2"},
        {"chart8.usage",       "All"},
        {"chart8.description", "A pie chart showing percentages on the category labels.  Also, "
                              +"this plot has a background image."},
        {"chart8.zoom",        "false"},

        {"chart9.title",       "XY Plot: "},
        {"chart9.tab",         "3"},
        {"chart9.usage",       "All"},
        {"chart9.zoom",        "true"},
        {"chart9.description", "A line chart using data from an XYDataset.  Both axes are "
                              +"numerical."},
        {"chart9.zoom",        "true"},

        {"chart10.title",       "Time Series 1: "},
        {"chart10.tab",         "4"},
        {"chart10.usage",       "All"},
        {"chart10.description", "A time series chart, representing data from an XYDataset.  This "
                               +"chart also demonstrates the use of multiple chart titles."},
        {"chart10.zoom",        "false"},

        {"chart11.title",       "Time Series 2: "},
        {"chart11.tab",         "4"},
        {"chart11.usage",       "All"},
        {"chart11.description", "A time series chart, representing data from an XYDataset.  The "
                               +"vertical axis has a logarithmic scale."},
        {"chart11.zoom",        "false"},

        {"chart12.title",       "Time Series 3: "},
        {"chart12.tab",         "4"},
        {"chart12.usage",       "All"},
        {"chart12.description", "A time series chart with a moving average."},
        {"chart12.zoom",        "false"},

        {"chart13.title",       "High/Low/Open/Close Chart: "},
        {"chart13.tab",         "6"},
        {"chart13.usage",       "All"},
        {"chart13.description", "A high/low/open/close chart based on data in a HighLowDataset."},
        {"chart13.zoom",        "false"},

        {"chart14.title",       "Candlestick Chart: "},
        {"chart14.tab",         "6"},
        {"chart14.usage",       "All"},
        {"chart14.description", "A candlestick chart based on data in a HighLowDataset."},
        {"chart14.zoom",        "false"},

        {"chart15.title",       "Signal Chart: "},
        {"chart15.tab",         "6"},
        {"chart15.usage",       "All"},
        {"chart15.description", "A signal chart based on data in a SignalDataset."},
        {"chart15.zoom",        "false"},

        {"chart16.title",       "Wind Plot: "},
        {"chart16.tab",         "6"},
        {"chart16.usage",       "All"},
        {"chart16.description", "A wind plot, represents wind direction and intensity (supplied "
                               +"via a WindDataset)."},
        {"chart16.zoom",        "false"},

        {"chart17.title",       "Scatter Plot: "},
        {"chart17.tab",         "3"},
        {"chart17.usage",       "All"},
        {"chart17.description", "A scatter plot, representing data in an XYDataset."},
        {"chart17.zoom",        "false"},

        {"chart18.title",       "Line Chart: "},
        {"chart18.tab",         "6"},
        {"chart18.usage",       "All"},
        {"chart18.description", "A chart displaying lines and or shapes, representing data in a "
                               +"CategoryDataset.  This plot also illustrates the use of a "
                               +"background image on the chart, and alpha-transparency on the "
                               +"plot."},
        {"chart18.zoom",        "false"},

        {"chart19.title",       "Vertical XY Bar Chart: "},
        {"chart19.tab",         "3"},
        {"chart19.usage",       "All"},
        {"chart19.description", "A chart showing vertical bars, based on data in an "
                               +"IntervalXYDataset."},
        {"chart19.zoom",        "false"},

        {"chart20.title",       "Null Data: "},
        {"chart20.tab",         "7"},
        {"chart20.usage",       "All"},
        {"chart20.description", "A chart with a null dataset."},
        {"chart20.zoom",        "false"},

        {"chart21.title",       "Zero Data: "},
        {"chart21.tab",         "7"},
        {"chart21.usage",       "All"},
        {"chart21.description", "A chart with a dataset containing zero series."},
        {"chart21.zoom",        "false"},

        {"chart22.title",       "Chart in JScrollPane: "},
        {"chart22.tab",         "7"},
        {"chart22.usage",       "All"},
        {"chart22.description", "A chart embedded in a JScrollPane."},
        {"chart22.zoom",        "false"},

        {"chart23.title",       "Single Series Bar Chart: "},
        {"chart23.tab",         "7"},
        {"chart23.usage",       "All"},
        {"chart23.description", "A single series bar chart.  This chart also illustrates the use "
                               +"of a border around a ChartPanel."},
        {"chart23.zoom",        "false"},

        {"chart24.title",       "Dynamic Chart: "},
        {"chart24.tab",         "7"},
        {"chart24.usage",       "Swing"},
        {"chart24.description", "A dynamic chart, to test the event notification mechanism."},
        {"chart24.zoom",        "false"},

        {"chart25.title",       "Overlaid Chart: "},
        {"chart25.tab",         "8"},
        {"chart25.usage",       "All"},
        {"chart25.description", "Displays an overlaid chart with high/low/open/close and moving "
                               +"average plots."},
        {"chart25.zoom",        "false"},

        {"chart26.title",       "Horizontally Combined Chart: "},
        {"chart26.tab",         "8"},
        {"chart26.usage",       "All"},
        {"chart26.description", "Displays a horizontally combined chart of time series and XY bar "
                               +"plots."},
        {"chart26.zoom",        "false"},

        {"chart27.title",       "Vertically Combined Chart: "},
        {"chart27.tab",         "8"},
        {"chart27.usage",       "All"},
        {"chart27.description", "Displays a vertically combined chart of XY, TimeSeries and "
                               +"VerticalXYBar plots."},
        {"chart27.zoom",        "false"},

        {"chart28.title",       "Combined and Overlaid Chart: "},
        {"chart28.tab",         "8"},
        {"chart28.usage",       "All"},
        {"chart28.description", "A combined chart of a XY, overlaid TimeSeries and an overlaid "
                               +"HighLow & TimeSeries plots."},
        {"chart28.zoom",        "false"},

        {"chart29.title",       "Combined and Overlaid Dynamic Chart: "},
        {"chart29.tab",         "8"},
        {"chart29.usage",       "Swing"},
        {"chart29.description", "Displays a dynamic combined and  overlaid chart, to test the "
                               +"event notification mechanism."},
        {"chart29.zoom",        "false"},

        {"chart30.title",       "Thermometer Chart: "},
        {"chart30.tab",         "5"},
        {"chart30.usage",       "All"},
        {"chart30.description", "Displays a thermometer chart."},
        {"chart30.zoom",        "false"},

        {"chart31.title",       "Meter Dial Chart: "},
        {"chart31.tab",         "5"},
        {"chart31.usage",       "All"},
        {"chart31.description", "Displays a speedo chart, with a full circle."},
        {"chart31.zoom",        "false"},

        {"chart32.title",       "Gantt Chart: "},
        {"chart32.tab",         "6"},
        {"chart32.usage",       "All"},
        {"chart32.description", "Displays a gantt chart."},

        {"chart33.title",       "Meter Dial Chart: "},
        {"chart33.tab",         "5"},
        {"chart33.usage",       "All"},
        {"chart33.description", "Displays a speedo chart, with a pie ending."},

        {"chart34.title",       "Meter Dial Chart: "},
        {"chart34.tab",         "5"},
        {"chart34.usage",       "All"},
        {"chart34.description", "Displays a speedo chart, with a chord ending."},

        {"chart35.title",       "Compass Chart: "},
        {"chart35.tab",         "5"},
        {"chart35.usage",       "All"},
        {"chart35.description", "Displays a compass chart."},

        {"charts.display", "Display"},

        // chart titles and labels...
        {"bar.horizontal.title",  "Horizontal Bar Chart"},
        {"bar.horizontal.domain", "Categories"},
        {"bar.horizontal.range",  "Value"},

        {"bar.horizontal-stacked.title",  "Horizontal Stacked Bar Chart"},
        {"bar.horizontal-stacked.domain", "Categories"},
        {"bar.horizontal-stacked.range",  "Value"},

        {"bar.vertical.title",  "Vertical Bar Chart"},
        {"bar.vertical.domain", "Categories"},
        {"bar.vertical.range",  "Value"},

        {"bar.vertical3D.title",  "Vertical 3D Bar Chart"},
        {"bar.vertical3D.domain", "Categories"},
        {"bar.vertical3D.range",  "Value"},

        {"bar.vertical-stacked.title",  "Vertical Stacked Bar Chart"},
        {"bar.vertical-stacked.domain", "Categories"},
        {"bar.vertical-stacked.range",  "Value"},

        {"bar.vertical-stacked3D.title",  "Vertical Stacked 3D Bar Chart"},
        {"bar.vertical-stacked3D.domain", "Categories"},
        {"bar.vertical-stacked3D.range",  "Value"},

        {"pie.pie1.title", "Pie Chart 1"},

        {"pie.pie2.title", "Pie Chart 2"},

        {"xyplot.sample1.title",  "XY Plot"},
        {"xyplot.sample1.domain", "X Values"},
        {"xyplot.sample1.range",  "Y Values"},

        {"timeseries.sample1.title",     "Time Series Chart 1"},
        {"timeseries.sample1.subtitle",  "Value of GBP in JPY"},
        {"timeseries.sample1.domain",    "Date"},
        {"timeseries.sample1.range",     "CCY per GBP"},
        {"timeseries.sample1.copyright", "(C)opyright 2002, by Simba Management Limited"},

        {"timeseries.sample2.title",    "Time Series Chart 2"},
        {"timeseries.sample2.domain",   "Millisecond"},
        {"timeseries.sample2.range",    "Log Axis"},
        {"timeseries.sample2.subtitle", "Milliseconds"},

        {"timeseries.sample3.title",    "Time Series Chart with Moving Average"},
        {"timeseries.sample3.domain",   "Date"},
        {"timeseries.sample3.range",    "CCY per GBP"},
        {"timeseries.sample3.subtitle", "30 day moving average of GBP"},

        {"timeseries.highlow.title",    "High/Low/Open/Close Chart"},
        {"timeseries.highlow.domain",   "Date"},
        {"timeseries.highlow.range",    "Price ($ per share)"},
        {"timeseries.highlow.subtitle", "IBM Stock Price"},

        {"timeseries.candlestick.title",    "CandleStick Chart"},
        {"timeseries.candlestick.domain",   "Date"},
        {"timeseries.candlestick.range",    "Price ($ per share)"},
        {"timeseries.candlestick.subtitle", "IBM Stock Price"},

        {"timeseries.signal.title",    "Signal Chart"},
        {"timeseries.signal.domain",   "Date"},
        {"timeseries.signal.range",    "Price ($ per share)"},
        {"timeseries.signal.subtitle", "IBM Stock Price"},

        {"meter.meter.title", "Speed"},
        {"meter.meter.subtitle", "Km"},
        {"meter.meter.units", "Km"},

        {"meter.thermo.title", "Hobart - Tasmania"},
        {"meter.thermo.subtitle", "Temperature"},
        {"meter.thermo.units", "°C"},

        {"other.wind.title",  "Wind Plot"},
        {"other.wind.domain", "X-Axis"},
        {"other.wind.range",  "Y-Axis"},

        {"other.scatter.title",  "Scatter Plot"},
        {"other.scatter.domain", "X-Axis"},
        {"other.scatter.range",  "Y-Axis"},

        {"other.line.title",  "Line Plot"},
        {"other.line.domain", "Category"},
        {"other.line.range",  "Value"},

        {"other.xybar.title",  "Time Series Bar Chart"},
        {"other.xybar.domain", "Date"},
        {"other.xybar.range",  "Value"},

        {"test.null.title",  "XY Plot (null data)"},
        {"test.null.domain", "X"},
        {"test.null.range",  "Y"},

        {"test.zero.title",  "XY Plot (zero data)"},
        {"test.zero.domain", "X axis"},
        {"test.zero.range",  "Y axis"},

        {"test.scroll.title",    "Time Series"},
        {"test.scroll.subtitle", "Value of GBP"},
        {"test.scroll.domain",   "Date"},
        {"test.scroll.range",    "Value"},

        {"test.single.title",     "Single Series Bar Chart"},
        {"test.single.subtitle1", "Subtitle 1"},
        {"test.single.subtitle2", "Subtitle 2"},
        {"test.single.domain",    "Date"},
        {"test.single.range",     "Value"},

        {"test.dynamic.title",  "Dynamic Chart"},
        {"test.dynamic.domain", "Domain"},
        {"test.dynamic.range",  "Range"},

        {"combined.overlaid.title",     "Overlaid Chart"},
        {"combined.overlaid.subtitle",  "High/Low/Open/Close plus Moving Average"},
        {"combined.overlaid.domain",    "Date" },
        {"combined.overlaid.range",     "IBM"},

        {"combined.horizontal.title",     "Horizontal Combined Chart"},
        {"combined.horizontal.subtitle",  "Time Series and XY Bar Charts"},
        {"combined.horizontal.domains",   new String[] {"Date 1", "Date 2", "Date 3"} },
        {"combined.horizontal.range",     "CCY per GBP"},

        {"combined.vertical.title",     "Vertical Combined Chart"},
        {"combined.vertical.subtitle",  "Four charts in one"},
        {"combined.vertical.domain",    "Date"},
        {"combined.vertical.ranges",    new String[] {"CCY per GBP", "Pounds", "IBM", "Bars"} },

        {"combined.combined-overlaid.title",     "Combined and Overlaid Chart"},
        {"combined.combined-overlaid.subtitle",  "XY, Overlaid (two TimeSeries) and Overlaid "
                                                +"(HighLow and TimeSeries)"},
        {"combined.combined-overlaid.domain",    "Date"},
        {"combined.combined-overlaid.ranges",    new String[] {"CCY per GBP", "Pounds", "IBM"} },

        {"combined.dynamic.title",     "Dynamic Combined Chart"},
        {"combined.dynamic.subtitle",  "XY (series 0), XY (series 1), Overlaid (both series) "
                                      +"and XY (both series)"},
        {"combined.dynamic.domain",    "X" },
        {"combined.dynamic.ranges",    new String[] {"Y1", "Y2", "Y3", "Y4"} },

        {"meter.compass.title", "Compass Plot"},
        {"meter.compass.subtitle", "subtitle"},

        {"gantt.task.title","Gant Chart"},
        {"gantt.task.domain","Task"},
        {"gantt.task.range","Time"},
    };

}
