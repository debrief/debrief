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
 * ------------------
 * ImageMapDemo3.java
 * ------------------
 * (C) Copyright 2002, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: ImageMapDemo3.java,v 1.1.1.1 2003/07/17 10:06:33 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 18-Jul-2002 : Version 1 (RA);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */
package com.jrefinery.chart.demo;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartUtilities;
import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.NumberAxis;
import com.jrefinery.chart.StandardXYItemRenderer;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.entity.StandardEntityCollection;
import com.jrefinery.chart.tooltips.CustomXYToolTipGenerator;
import com.jrefinery.chart.urls.StandardXYURLGenerator;
import com.jrefinery.data.XYSeries;
import com.jrefinery.data.XYSeriesCollection;

/**
 * A demo showing the construction of HTML image maps with JFreeChart.
 *
 * @author RA
 */
public class ImageMapDemo3 {

    /**
     * Default constructor.
     */
    public ImageMapDemo3() {
        super();
    }

    /**
     * Starting point for the demo.
     *
     * @param args  ignored.
     *
     * @throws ParseException if there is a problem parsing dates.
     */
    public static void main(String[] args) throws ParseException {

        //  Create a sample dataset
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        XYSeries dataSeries = new XYSeries("Curve data");
        ArrayList<String> toolTips = new ArrayList<String>();
        dataSeries.add(sdf.parse("01-Jul-2002").getTime(), 5.22);
        toolTips.add("1D - 5.22");
        dataSeries.add(sdf.parse("02-Jul-2002").getTime(), 5.18);
        toolTips.add("2D - 5.18");
        dataSeries.add(sdf.parse("03-Jul-2002").getTime(), 5.23);
        toolTips.add("3D - 5.23");
        dataSeries.add(sdf.parse("04-Jul-2002").getTime(), 5.15);
        toolTips.add("4D - 5.15");
        dataSeries.add(sdf.parse("05-Jul-2002").getTime(), 5.22);
        toolTips.add("5D - 5.22");
        dataSeries.add(sdf.parse("06-Jul-2002").getTime(), 5.25);
        toolTips.add("6D - 5.25");
        dataSeries.add(sdf.parse("07-Jul-2002").getTime(), 5.31);
        toolTips.add("7D - 5.31");
        dataSeries.add(sdf.parse("08-Jul-2002").getTime(), 5.36);
        toolTips.add("8D - 5.36");
        XYSeriesCollection xyDataset = new XYSeriesCollection(dataSeries);
        CustomXYToolTipGenerator ttg = new CustomXYToolTipGenerator();
        ttg.addToolTipSeries(toolTips);

        //  Create the chart
        StandardXYURLGenerator urlg = new StandardXYURLGenerator("xy_details.jsp");
        ValueAxis timeAxis = new HorizontalDateAxis("");
        NumberAxis valueAxis = new VerticalNumberAxis("");
        valueAxis.setAutoRangeIncludesZero(false);  // override default
        XYPlot plot = new XYPlot(xyDataset, timeAxis, valueAxis);
        StandardXYItemRenderer sxyir = new StandardXYItemRenderer(
            StandardXYItemRenderer.LINES + StandardXYItemRenderer.SHAPES,
            ttg, urlg);
        plot.setRenderer(sxyir);
        JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        chart.setBackgroundPaint(java.awt.Color.white);

        // save it to an image
        try {
            ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
            File file1 = new File("xychart100.png");
            ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);

            // write an HTML page incorporating the image with an image map
            File file2 = new File("xychart100.html");
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file2));
            PrintWriter writer = new PrintWriter(out);
            writer.println("<HTML>");
            writer.println("<HEAD><TITLE>JFreeChart Image Map Demo</TITLE></HEAD>");
            writer.println("<BODY>");
            ChartUtilities.writeImageMap(writer, "chart", info);
            writer.println("<IMG SRC=\"xychart100.png\" "
                           + "WIDTH=\"600\" HEIGHT=\"400\" BORDER=\"0\" USEMAP=\"#chart\">");
            writer.println("</BODY>");
            writer.println("</HTML>");
            writer.close();

        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
        return;
    }
}
