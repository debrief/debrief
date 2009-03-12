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
 * -----------------------
 * JFreeChartDemoBase.java
 * -----------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Matthew Wright;
 *                   Serge V. Grachov;
 *                   Bill Kelemen;
 *                   Achilleus Mantzios;
 *                   Bryan Scott;
 *
 * $Id: JFreeChartDemoBase.java,v 1.2 2004/10/16 16:11:54 ian Exp $
 *
 * Changes
 * -------
 * 27-Jul-2002 : Created (BRS);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.*;
import com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm;
import com.jrefinery.chart.data.PlotFit;
import com.jrefinery.chart.tooltips.HighLowToolTipGenerator;
import com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator;
import com.jrefinery.data.*;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

/**
 * A simple class that allows the swing and servlet chart demostrations
 * to share chart generating code.
 * <p/>
 * If you would like to add a chart to the swing and/or servlet demo do so here.
 *
 * @author BRS/DG
 */
public class JFreeChartDemoBase
{

  /**
   * CHART_COMMANDS holds information on charts that can be created
   * Format is
   * Name, Creation Method, Resource file prefix
   * <p/>
   * Steps To add a chart
   * 1) Create a createChart method which returns a JFreeChart
   * 2) Append details to CHART_COMMANDS
   * 3) Append details to DemoResources
   */
  public static final String[][] CHART_COMMANDS = {
    {"HORIZONTAL_BAR_CHART", "createHorizontalBarChart", "chart1"},
    {"HORIZONTAL_STACKED_BAR_CHART", "createStackedHorizontalBarChart", "chart2"},
    {"VERTICAL_BAR_CHART", "createVerticalBarChart", "chart3"},
    {"VERTICAL_3D_BAR_CHART", "createVertical3DBarChart", "chart4"},
    {"VERTICAL_STACKED_BAR_CHART", "createVerticalStackedBarChart", "chart5"},
    {"VERTICAL_STACKED_3D_BAR_CHART", "createVerticalStacked3DBarChart", "chart6"},
    {"PIE_CHART_1", "createPieChartOne", "chart7"},
    {"PIE_CHART_2", "createPieChartTwo", "chart8"},
    {"XY_PLOT", "createXYPlot", "chart9"},
    {"TIME_SERIES_1_CHART", "createTimeSeries1Chart", "chart10"},
    {"TIME_SERIES_2_CHART", "createTimeSeries2Chart", "chart11"},
    {"TIME_SERIES_WITH_MA_CHART", "createTimeSeriesWithMAChart", "chart12"},
    {"HIGH_LOW_CHART", "createHighLowChart", "chart13"},
    {"CANDLESTICK_CHART", "createCandlestickChart", "chart14"},
    {"SIGNAL_CHART", "createSignalChart", "chart15"},
    {"WIND_PLOT", "createWindPlot", "chart16"},
    {"SCATTER_PLOT", "createScatterPlot", "chart17"},
    {"LINE_CHART", "createLineChart", "chart18"},
    {"VERTICAL_XY_BAR_CHART", "createVerticalXYBarChart", "chart19"},
    {"XY_PLOT_NULL", "createNullXYPlot", "chart20"},
    {"XY_PLOT_ZERO", "createXYPlotZeroData", "chart21"},
    {"TIME_SERIES_CHART_SCROLL", "createTimeSeriesChartInScrollPane", "chart22"},
    {"SINGLE_SERIES_BAR_CHART", "createSingleSeriesBarChart", "chart23"},
    {"DYNAMIC_CHART", "createDynamicXYChart", "chart24"},
    {"OVERLAID_CHART", "createOverlaidChart", "chart25"},
    {"HORIZONTALLY_COMBINED_CHART", "createHorizontallyCombinedChart", "chart26"},
    {"VERTICALLY_COMBINED_CHART", "createVerticallyCombinedChart", "chart27"},
    {"COMBINED_OVERLAID_CHART", "createCombinedAndOverlaidChart1", "chart28"},
    {"COMBINED_OVERLAID_DYNAMIC_CHART", "createCombinedAndOverlaidDynamicXYChart", "chart29"},
    {"THERMOMETER_CHART", "createThermometerChart", "chart30"},
    {"METER_CHART", "createMeterChartCircle", "chart31"},
    {"GANTT_CHART", "createGanttChart", "chart32"},
    {"METER_CHART2", "createMeterChartPie", "chart33"},
    {"METER_CHART3", "createMeterChartChord", "chart34"},
    {"COMPASS_CHART", "createCompassChart", "chart35"},
  };

  /**
   * Base class name for localised resources.
   */
  public static final String BASE_RESOURCE_CLASS
    = "com.jrefinery.chart.demo.resources.DemoResources";

  /**
   * Localised resources.
   */
  private ResourceBundle resources;

  /**
   * An array of charts.
   */
  private JFreeChart[] chart = new JFreeChart[CHART_COMMANDS.length];

  /**
   * Default constructor.
   */
  public JFreeChartDemoBase()
  {
    this.resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
  }

  /**
   * Returns a chart.
   *
   * @param i the chart index.
   * @return a chart.
   */
  @SuppressWarnings("unchecked")
	public JFreeChart getChart(int i)
  {

    if ((i < 0) && (i >= chart.length))
    {
      i = 0;
    }

    if (chart[i] == null)
    {
      /// Utilise reflection to invoke method to create new chart if required.
      try
      {
        Class[] nullClass = null;
        Method method = getClass().getDeclaredMethod(CHART_COMMANDS[i][1], nullClass);
        Object[] nullObject = null;
        chart[i] = (JFreeChart) method.invoke(this, nullObject);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    return chart[i];
  }

  /**
   * This makes the resources bundle available.  Basically an optimisation so
   * the demo servlet can access the same resource file.
   *
   * @return the resources bundle.
   */
  public ResourceBundle getResources()
  {
    return this.resources;
  }

  /**
   * Create a horizontal bar chart.
   *
   * @return a horizontal bar chart.
   */
  public JFreeChart createHorizontalBarChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("bar.horizontal.title");
    String domain = resources.getString("bar.horizontal.domain");
    String range = resources.getString("bar.horizontal.range");

    CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
    JFreeChart chart1 = ChartFactory.createHorizontalBarChart(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.orange));
    CategoryPlot plot = chart1.getCategoryPlot();
    NumberAxis axis = (NumberAxis) plot.getRangeAxis();
    axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    axis.setCrosshairVisible(false);
    axis.setInverted(true);

    return chart1;

  }

  /**
   * Creates and returns a sample stacked horizontal bar chart.
   *
   * @return a sample stacked horizontal bar chart.
   */
  public JFreeChart createStackedHorizontalBarChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("bar.horizontal-stacked.title");
    String domain = resources.getString("bar.horizontal-stacked.domain");
    String range = resources.getString("bar.horizontal-stacked.range");

    CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
    JFreeChart chart1 = ChartFactory.createStackedHorizontalBarChart(title, domain, range,
      data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
    return chart1;

  }

  /**
   * Creates and returns a sample vertical bar chart.
   *
   * @return a sample vertical bar chart.
   */
  public JFreeChart createVerticalBarChart()
  {

    String title = resources.getString("bar.vertical.title");
    String domain = resources.getString("bar.vertical.domain");
    String range = resources.getString("bar.vertical.range");

    CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
    JFreeChart chart1 = ChartFactory.createVerticalBarChart(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
    CategoryPlot plot = (CategoryPlot) chart1.getPlot();
    plot.setForegroundAlpha(0.9f);
    NumberAxis verticalAxis = (NumberAxis) plot.getRangeAxis();
    verticalAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    return chart1;
  }

  /**
   * Creates and returns a sample vertical 3D bar chart.
   *
   * @return a sample vertical 3D bar chart.
   */
  public JFreeChart createVertical3DBarChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("bar.vertical3D.title");
    String domain = resources.getString("bar.vertical3D.domain");
    String range = resources.getString("bar.vertical3D.range");

    CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
    JFreeChart chart1 = ChartFactory.createVerticalBarChart3D(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
    CategoryPlot plot = (CategoryPlot) chart1.getPlot();
    plot.setForegroundAlpha(0.75f);
    return chart1;

  }

  /**
   * Creates and returns a sample stacked vertical bar chart.
   *
   * @return a sample stacked vertical bar chart.
   */
  public JFreeChart createVerticalStackedBarChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("bar.vertical-stacked.title");
    String domain = resources.getString("bar.vertical-stacked.domain");
    String range = resources.getString("bar.vertical-stacked.range");

    CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
    JFreeChart chart1
      = ChartFactory.createStackedVerticalBarChart(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
    return chart1;

  }

  /**
   * Creates and returns a sample stacked vertical 3D bar chart.
   *
   * @return a sample stacked vertical 3D bar chart.
   */
  public JFreeChart createVerticalStacked3DBarChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("bar.vertical-stacked3D.title");
    String domain = resources.getString("bar.vertical-stacked3D.domain");
    String range = resources.getString("bar.vertical-stacked3D.range");
    CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
    JFreeChart chart1
      = ChartFactory.createStackedVerticalBarChart3D(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
    return chart1;

  }

  /**
   * Creates and returns a sample pie chart.
   *
   * @return a sample pie chart.
   */
  public JFreeChart createPieChartOne()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("pie.pie1.title");
    CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
    PieDataset extracted = DatasetUtilities.createPieDataset(data, 0);
    JFreeChart chart1 = ChartFactory.createPieChart(title, extracted, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.orange));
    PiePlot plot = (PiePlot) chart1.getPlot();
    plot.setCircular(false);
    // make section 1 explode by 100%...
    plot.setRadiusPercent(0.60);
    plot.setExplodePercent(1, 1.00);
    return chart1;

  }

  /**
   * Creates and returns a sample pie chart.
   *
   * @return a sample pie chart.
   */
  public JFreeChart createPieChartTwo()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("pie.pie2.title");
    CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
    Object category = data.getCategories().get(1);
    PieDataset extracted = DatasetUtilities.createPieDataset(data, category);
    JFreeChart chart1 = ChartFactory.createPieChart(title, extracted, true);

    // then customise it a little...
    chart1.setBackgroundPaint(Color.lightGray);
    PiePlot pie = (PiePlot) chart1.getPlot();
    pie.setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
    pie.setBackgroundImage(JFreeChart.INFO.getLogo());
    pie.setBackgroundPaint(Color.white);
    pie.setBackgroundAlpha(0.6f);
    pie.setForegroundAlpha(0.75f);
    return chart1;

  }

  /**
   * Creates and returns a sample XY plot.
   *
   * @return a sample XY plot.
   */
  @SuppressWarnings("deprecation")
	public JFreeChart createXYPlot()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("xyplot.sample1.title");
    String domain = resources.getString("xyplot.sample1.domain");
    String range = resources.getString("xyplot.sample1.range");
    XYDataset data = DemoDatasetFactory.createSampleXYDataset();
    JFreeChart chart1 = ChartFactory.createXYChart(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));
    return chart1;

  }

  /**
   * Creates and returns a sample time series chart.
   *
   * @return a sample time series chart.
   */
  public JFreeChart createTimeSeries1Chart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("timeseries.sample1.title");
    String subtitle = resources.getString("timeseries.sample1.subtitle");
    String domain = resources.getString("timeseries.sample1.domain");
    String range = resources.getString("timeseries.sample1.range");
    String copyrightStr = resources.getString("timeseries.sample1.copyright");
    XYDataset data = DemoDatasetFactory.createTimeSeriesCollection3();
    JFreeChart chart1 = ChartFactory.createTimeSeriesChart(title, domain, range, data, true);

    // then customise it a little...
    TextTitle title2 = new TextTitle(subtitle, new Font("SansSerif", Font.PLAIN, 12));
    title2.setSpacer(new Spacer(Spacer.RELATIVE, 0.05, 0.05, 0.05, 0.0));
    chart1.addTitle(title2);

    TextTitle copyright = new TextTitle(copyrightStr, new Font("SansSerif", Font.PLAIN, 9));
    copyright.setPosition(TextTitle.BOTTOM);
    copyright.setHorizontalAlignment(TextTitle.RIGHT);
    chart1.addTitle(copyright);

    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    XYPlot plot = chart1.getXYPlot();
    HorizontalDateAxis axis = (HorizontalDateAxis) plot.getDomainAxis();
    axis.setVerticalTickLabels(true);
    return chart1;

  }

  /**
   * Creates and returns a sample time series chart.
   *
   * @return a sample time series chart.
   */
  public JFreeChart createTimeSeries2Chart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("timeseries.sample2.title");
    String subtitleStr = resources.getString("timeseries.sample2.subtitle");
    String domain = resources.getString("timeseries.sample2.domain");
    String range = resources.getString("timeseries.sample2.range");
    XYDataset data = DemoDatasetFactory.createTimeSeriesCollection4();
    JFreeChart chart1 = ChartFactory.createTimeSeriesChart(title, domain, range, data, true);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    XYPlot plot = chart1.getXYPlot();
    VerticalLogarithmicAxis vla = new VerticalLogarithmicAxis(range);
    plot.setRangeAxis(vla);
    return chart1;

  }

  /**
   * Creates and returns a sample time series chart.
   *
   * @return a sample time series chart.
   */
  public JFreeChart createTimeSeriesWithMAChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("timeseries.sample3.title");
    String domain = resources.getString("timeseries.sample3.domain");
    String range = resources.getString("timeseries.sample3.range");
    String subtitleStr = resources.getString("timeseries.sample3.subtitle");
    XYDataset data = DemoDatasetFactory.createTimeSeriesCollection2();
    MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
    mavg.setPeriod(30);
    PlotFit pf = new PlotFit(data, mavg);
    data = pf.getFit();
    JFreeChart chart1 = ChartFactory.createTimeSeriesChart(title, domain, range, data, true);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    return chart1;

  }

  /**
   * Displays a vertical bar chart in its own frame.
   *
   * @return a high low chart.
   */
  public JFreeChart createHighLowChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("timeseries.highlow.title");
    String domain = resources.getString("timeseries.highlow.domain");
    String range = resources.getString("timeseries.highlow.range");
    String subtitleStr = resources.getString("timeseries.highlow.subtitle");
    HighLowDataset data = DemoDatasetFactory.createSampleHighLowDataset();
    JFreeChart chart1 = ChartFactory.createHighLowChart(title, domain, range, data, true);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.magenta));
    return chart1;

  }

  /**
   * Creates a candlestick chart.
   *
   * @return a candlestick chart.
   */
  public JFreeChart createCandlestickChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("timeseries.candlestick.title");
    String domain = resources.getString("timeseries.candlestick.domain");
    String range = resources.getString("timeseries.candlestick.range");
    String subtitleStr = resources.getString("timeseries.candlestick.subtitle");
    HighLowDataset data = DemoDatasetFactory.createSampleHighLowDataset();
    JFreeChart chart1 = ChartFactory.createCandlestickChart(title, domain, range, data,
      false);

    chart1.getPlot().setSeriesPaint(new Paint[]{Color.blue});

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));
    return chart1;

  }

  /**
   * Creates and returns a sample signal chart.
   *
   * @return a sample chart.
   */
  public JFreeChart createSignalChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("timeseries.signal.title");
    String domain = resources.getString("timeseries.signal.domain");
    String range = resources.getString("timeseries.signal.range");
    String subtitleStr = resources.getString("timeseries.signal.subtitle");
    SignalsDataset data = DemoDatasetFactory.createSampleSignalDataset();
    JFreeChart chart1 = ChartFactory.createSignalChart(title, domain, range, data, true);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    return chart1;

  }

  /**
   * Creates and returns a sample thermometer chart.
   *
   * @return a sample thermometer chart.
   */
  public JFreeChart createThermometerChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("meter.thermo.title");
    String subtitleStr = resources.getString("meter.thermo.subtitle");
    String units = resources.getString("meter.thermo.units");

    DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();
    data.setValue(new Double(34.0));
    ThermometerPlot plot = new ThermometerPlot(data);
    plot.setUnits(units);
    JFreeChart chart1 = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    return chart1;

  }

  /**
   * Creates and returns a sample meter chart.
   *
   * @return a meter chart.
   */
  public JFreeChart createMeterChartCircle()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("meter.meter.title");
    String subtitleStr = resources.getString("meter.meter.subtitle");
    String units = resources.getString("meter.meter.units");
    DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();

    data.setUnits(units);
    MeterPlot plot = new MeterPlot(data);
    plot.setMeterAngle(270);
    plot.setDialType(1);
    JFreeChart chart1 = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
      plot, false);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    return chart1;
  }

  /**
   * Creates and returns a sample meter chart.
   *
   * @return a meter chart.
   */
  public JFreeChart createMeterChartPie()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("meter.meter.title");
    String subtitleStr = resources.getString("meter.meter.subtitle");
    String units = resources.getString("meter.meter.units");
    DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();

    data.setUnits(units);
    MeterPlot plot = new MeterPlot(data);
    plot.setMeterAngle(270);
    plot.setDialType(0);
    JFreeChart chart1 = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
      plot, false);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    return chart1;
  }

  /**
   * Creates and returns a sample meter chart.
   *
   * @return the meter chart.
   */
  public JFreeChart createMeterChartChord()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("meter.meter.title");
    String subtitleStr = resources.getString("meter.meter.subtitle");
    String units = resources.getString("meter.meter.units");
    DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();

    data.setUnits(units);
    MeterPlot plot = new MeterPlot(data);
    plot.setMeterAngle(270);
    plot.setDialType(2);
    JFreeChart chart1 = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
      plot, false);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    return chart1;
  }

  /**
   * Creates a compass chart.
   *
   * @return a compass chart.
   */
  public JFreeChart createCompassChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("meter.compass.title");
    String subtitleStr = resources.getString("meter.compass.subtitle");
    DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();

    Plot plot = new CompassPlot(data);
    JFreeChart chart1 = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
      plot, false);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    return chart1;
  }

  /**
   * Creates and returns a sample wind plot.
   *
   * @return a sample wind plot.
   */
  public JFreeChart createWindPlot()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("other.wind.title");
    String domain = resources.getString("other.wind.domain");
    String range = resources.getString("other.wind.range");
    WindDataset data = DemoDatasetFactory.createWindDataset1();
    JFreeChart chart1 = ChartFactory.createWindPlot(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));
    return chart1;

  }

  /**
   * Creates and returns a sample scatter plot.
   *
   * @return a sample scatter plot.
   */
  public JFreeChart createScatterPlot()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("other.scatter.title");
    String domain = resources.getString("other.scatter.domain");
    String range = resources.getString("other.scatter.range");
    XYDataset data = new SampleXYDataset2();
    JFreeChart chart1 = ChartFactory.createScatterPlot(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));

    XYPlot plot = chart1.getXYPlot();
    ValueAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCrosshairVisible(true);
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setCrosshairVisible(true);
    rangeAxis.setAutoRangeIncludesZero(false);
    return chart1;

  }

  /**
   * Creates and returns a sample line chart.
   *
   * @return a line chart.
   */
  public JFreeChart createLineChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("other.line.title");
    String domain = resources.getString("other.line.domain");
    String range = resources.getString("other.line.range");
    CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
    JFreeChart chart1 = ChartFactory.createLineChart(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundImage(JFreeChart.INFO.getLogo());
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));

    CategoryPlot plot = (CategoryPlot) chart1.getPlot();
    plot.setBackgroundAlpha(0.65f);
    HorizontalCategoryAxis axis = (HorizontalCategoryAxis) plot.getDomainAxis();
    axis.setVerticalCategoryLabels(true);
    return chart1;
  }

  /**
   * Creates and returns a sample vertical XY bar chart.
   *
   * @return a sample vertical XY bar chart.
   */
  public JFreeChart createVerticalXYBarChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("other.xybar.title");
    String domain = resources.getString("other.xybar.domain");
    String range = resources.getString("other.xybar.range");
    IntervalXYDataset data = DemoDatasetFactory.createTimeSeriesCollection1();
    JFreeChart chart1 = ChartFactory.createVerticalXYBarChart(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));

    XYItemRenderer renderer = chart1.getXYPlot().getRenderer();
    renderer.setToolTipGenerator(new TimeSeriesToolTipGenerator());
    return chart1;
  }

  /**
   * Creates and returns a sample XY chart with null data.
   *
   * @return a chart.
   */
  @SuppressWarnings({ "deprecation", "deprecation" })
	public JFreeChart createNullXYPlot()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("test.null.title");
    String domain = resources.getString("test.null.domain");
    String range = resources.getString("test.null.range");
    XYDataset data = null;
    JFreeChart chart1 = ChartFactory.createXYChart(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
    return chart1;

  }

  /**
   * Creates a sample XY plot with an empty dataset.
   *
   * @return a sample XY plot with an empty dataset.
   */
  @SuppressWarnings("deprecation")
	public JFreeChart createXYPlotZeroData()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("test.zero.title");
    String domain = resources.getString("test.zero.domain");
    String range = resources.getString("test.zero.range");
    XYDataset data = new EmptyXYDataset();
    JFreeChart chart1 = ChartFactory.createXYChart(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
    return chart1;
  }

  /**
   * Creates and returns a sample time series chart that will be displayed in a scroll pane.
   *
   * @return a sample time series chart.
   */
  public JFreeChart createTimeSeriesChartInScrollPane()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("test.scroll.title");
    String domain = resources.getString("test.scroll.domain");
    String range = resources.getString("test.scroll.range");
    String subtitleStr = resources.getString("test.scroll.subtitle");
    XYDataset data = DemoDatasetFactory.createTimeSeriesCollection2();
    JFreeChart chart1 = ChartFactory.createTimeSeriesChart(title, domain, range, data, true);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.gray));
    return chart1;

  }

  /**
   * Creates and returns a sample bar chart with just one series.
   *
   * @return a sample bar chart.
   */
  public JFreeChart createSingleSeriesBarChart()
  {

    // create a default chart based on some sample data...
    String title = resources.getString("test.single.title");
    String domain = resources.getString("test.single.domain");
    String range = resources.getString("test.single.range");
    String subtitle1Str = resources.getString("test.single.subtitle1");
    String subtitle2Str = resources.getString("test.single.subtitle2");

    CategoryDataset data = DemoDatasetFactory.createSingleSeriesCategoryDataset();

    JFreeChart chart1 = ChartFactory.createHorizontalBarChart(title, domain, range, data, true);
    chart1.addTitle(new TextTitle(subtitle1Str));
    chart1.addTitle(new TextTitle(subtitle2Str));
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
    return chart1;

  }

  /**
   * Displays an XY chart that is periodically updated by a background thread.  This is to
   * demonstrate the event notification system that automatically updates charts as required.
   *
   * @return a chart.
   */
  @SuppressWarnings("deprecation")
	public JFreeChart createDynamicXYChart()
  {

    String title = resources.getString("test.dynamic.title");
    String domain = resources.getString("test.dynamic.domain");
    String range = resources.getString("test.dynamic.range");

    SampleXYDataset data = new SampleXYDataset();
    JFreeChart chart1 = ChartFactory.createXYChart(title, domain, range, data, true);
    SampleXYDatasetThread update = new SampleXYDatasetThread(data);

    Thread thread = new Thread(update);
    thread.start();

    return chart1;

  }

  /**
   * Creates and returns a sample overlaid chart.
   *
   * @return an overlaid chart.
   */
  public JFreeChart createOverlaidChart()
  {

    // create a default chart based on some sample data...
    String title = this.resources.getString("combined.overlaid.title");
    String subtitleStr = this.resources.getString("combined.overlaid.subtitle");
    // create high-low and moving average dataset
    HighLowDataset highLowData = DemoDatasetFactory.createSampleHighLowDataset();
    MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
    mavg.setPeriod(5);
    PlotFit pf = new PlotFit(highLowData, mavg);
    XYDataset maData = pf.getFit();

    // make an overlaid CombinedPlot
    OverlaidXYPlot overlaidPlot = new OverlaidXYPlot("Date", "Price");
    overlaidPlot.setDomainAxis(new HorizontalDateAxis("Date"));

    // create and add subplot 1...
    XYItemRenderer renderer1 = new HighLowRenderer(new HighLowToolTipGenerator());
    XYPlot subplot1 = new XYPlot(highLowData, null, null, renderer1);
    subplot1.setSeriesPaint(new Paint[]{Color.red});
    overlaidPlot.add(subplot1);

    XYDataset data2 = new SubSeriesDataset(maData, 1); // MA data
    XYPlot subplot2 = new XYPlot(data2, null, null);
    subplot2.setSeriesPaint(new Paint[]{Color.blue});
    subplot2.getRenderer().setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0.00"));
    overlaidPlot.add(subplot2);

    // make the top level JFreeChart object
    JFreeChart chart1 = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
      overlaidPlot, true);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));

    return chart1;

  }

  /**
   * Creates a horizontally combined chart.
   *
   * @return a horizontally combined chart.
   */
  public JFreeChart createHorizontallyCombinedChart()
  {

    // create a default chart based on some sample data...
    String title = this.resources.getString("combined.horizontal.title");
    String subtitleStr = this.resources.getString("combined.horizontal.subtitle");
    String range = this.resources.getString("combined.horizontal.range");

    // calculate Time Series and Moving Average Dataset
    MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
    mavg.setPeriod(30);
    PlotFit pf = new PlotFit(DemoDatasetFactory.createTimeSeriesCollection2(), mavg);
    XYDataset tempDataset = pf.getFit();

    // create master dataset
    CombinedDataset data = new CombinedDataset();
    data.add(tempDataset);                // time series + MA

    // test SubSeriesDataset and CombinedDataset operations

    // decompose data into its two dataset series
    XYDataset series0 = new SubSeriesDataset(data, 0);
    JFreeChart chart1 = null;

    // make a common vertical axis for all the sub-plots
    NumberAxis valueAxis = new VerticalNumberAxis(range);
    valueAxis.setAutoRangeIncludesZero(false);  // override default
    valueAxis.setCrosshairVisible(false);

    // make a horizontally combined plot
    CombinedXYPlot multiPlot = new CombinedXYPlot(valueAxis, CombinedXYPlot.HORIZONTAL);

    int[] weight = {1, 1, 1}; // control horizontal space assigned to each subplot

    // add subplot 1...
    XYPlot subplot1 = new XYPlot(series0, new HorizontalDateAxis("Date"), null);
    subplot1.setSeriesPaint(new Paint[]{Color.red});
    multiPlot.add(subplot1, weight[0]);

    // add subplot 2...
    XYPlot subplot2 = new XYPlot(data, new HorizontalDateAxis("Date"), null);
    subplot2.setSeriesPaint(new Paint[]{Color.green, Color.yellow});
    multiPlot.add(subplot2, weight[1]);

    // add subplot 3...
    XYPlot subplot3 = new XYPlot(series0, new HorizontalDateAxis("Date"),
      null, new VerticalXYBarRenderer(0.20));
    subplot3.setSeriesPaint(new Paint[]{Color.blue});
    multiPlot.add(subplot3, weight[2]);

    // now make the top level JFreeChart
    chart1 = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, multiPlot, true);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    return chart1;

  }

  /**
   * Creates and returns a sample vertically combined chart.
   *
   * @return a sample vertically combined chart.
   */
  public JFreeChart createVerticallyCombinedChart()
  {

    // create a default chart based on some sample data...
    String title = this.resources.getString("combined.vertical.title");
    String subtitleStr = this.resources.getString("combined.vertical.subtitle");
    String domain = this.resources.getString("combined.vertical.domain");
    // calculate Time Series and Moving Average Dataset
    MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
    mavg.setPeriod(30);
    PlotFit pf = new PlotFit(DemoDatasetFactory.createTimeSeriesCollection2(), mavg);
    XYDataset tempDataset = pf.getFit();

    // create master dataset
    CombinedDataset data = new CombinedDataset();
    data.add(tempDataset);                // time series + MA
    data.add(DemoDatasetFactory.createSampleHighLowDataset()); // high-low data

    // test SubSeriesDataset and CombinedDataset operations

    // decompose data into its two dataset series
    SeriesDataset series0 = new SubSeriesDataset(data, 0);
    SeriesDataset series1 = new SubSeriesDataset(data, 1);
    SeriesDataset series2 = new SubSeriesDataset(data, 2);

    // compose datasets for each sub-plot
    CombinedDataset data0 = new CombinedDataset(new SeriesDataset[]{series0});
    CombinedDataset data1 = new CombinedDataset(new SeriesDataset[]{series0, series1});
    CombinedDataset data2 = new CombinedDataset(new SeriesDataset[]{series2});

    // make one shared horizontal axis
    ValueAxis timeAxis = new HorizontalDateAxis(domain);
    timeAxis.setCrosshairVisible(false);

    // make a vertically CombinedPlot that will contain the sub-plots
    CombinedXYPlot multiPlot = new CombinedXYPlot(timeAxis, CombinedXYPlot.VERTICAL);

    int[] weight = {1, 1, 1, 1}; // control vertical space allocated to each sub-plot

    // add subplot1...
    XYPlot subplot1 = new XYPlot(data0, null, new VerticalNumberAxis("Value"));
    NumberAxis range1 = (NumberAxis) subplot1.getRangeAxis();
    range1.setAutoRangeIncludesZero(false);
    subplot1.setSeriesPaint(new Paint[]{Color.red});
    multiPlot.add(subplot1, weight[0]);

    // add subplot2...
    XYPlot subplot2 = new XYPlot(data1, null, new VerticalNumberAxis("Value"));
    NumberAxis range2 = (NumberAxis) subplot2.getRangeAxis();
    range2.setAutoRangeIncludesZero(false);
    subplot2.setSeriesPaint(new Paint[]{Color.green, Color.yellow});
    multiPlot.add(subplot2, weight[1]);

    // add subplot3...
    XYPlot subplot3 = new XYPlot(data2, null, new VerticalNumberAxis("Value"));
    XYItemRenderer renderer3 = new HighLowRenderer();
    subplot3.setRenderer(renderer3);
    NumberAxis range3 = (NumberAxis) subplot3.getRangeAxis();
    range3.setAutoRangeIncludesZero(false);
    subplot3.setSeriesPaint(new Paint[]{Color.blue});
    multiPlot.add(subplot3, weight[2]);

    // add subplot4...
    XYPlot subplot4 = new XYPlot(data0, null, new VerticalNumberAxis("Value"));
    XYItemRenderer renderer4 = new VerticalXYBarRenderer();
    subplot4.setRenderer(renderer4);
    NumberAxis range4 = (NumberAxis) subplot4.getRangeAxis();
    range4.setAutoRangeIncludesZero(false);
    subplot4.setSeriesPaint(new Paint[]{Color.lightGray});
    multiPlot.add(subplot4, weight[3]);

    // now make the top level JFreeChart that contains the CombinedPlot
    JFreeChart chart1 = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, multiPlot, true);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    return chart1;

  }

  /**
   * Creates a combined and overlaid chart.
   *
   * @return a combined and overlaid chart.
   */
  public JFreeChart createCombinedAndOverlaidChart1()
  {

    // create a default chart based on some sample data...
    String title = this.resources.getString("combined.combined-overlaid.title");
    String subtitleStr = this.resources.getString("combined.combined-overlaid.subtitle");
    String domain = this.resources.getString("combined.combined-overlaid.domain");
    String[] ranges = this.resources.getStringArray("combined.combined-overlaid.ranges");

    HighLowDataset highLowData = DemoDatasetFactory.createSampleHighLowDataset();
    XYDataset timeSeriesData = DemoDatasetFactory.createTimeSeriesCollection2();

    // calculate Moving Average of High-Low Dataset
    MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
    mavg.setPeriod(5);
    PlotFit pf = new PlotFit(highLowData, mavg);
    XYDataset highLowMAData = pf.getFit();

    // calculate Moving Average of Time Series
    mavg = new MovingAveragePlotFitAlgorithm();
    mavg.setPeriod(30);
    pf = new PlotFit(timeSeriesData, mavg);
    XYDataset timeSeriesMAData = pf.getFit();

    // create master Dataset
    CombinedDataset data = new CombinedDataset();
    data.add(timeSeriesData);         // time series
    data.add(timeSeriesMAData, 1);    // time series MA (series #1 of dataset)
    data.add(highLowData);            // high-low series
    data.add(highLowMAData, 1);       // high-low MA (series #1 of dataset)

    // test XYSubDataset and CombinedDataset operations

    // decompose data into its two dataset series
    XYDataset series0 = new SubSeriesDataset(data, 0); // time series
    XYDataset series1 = new SubSeriesDataset(data, 1); // time series MA
    XYDataset series2 = new SubSeriesDataset(data, 2); // high-low series
    XYDataset series3 = new SubSeriesDataset(data, 3); // high-low MA

    // compose datasets for each sub-plot
    CombinedDataset data0 = new CombinedDataset(new SeriesDataset[]{series0});
    // make one vertical axis for each (vertical) chart
    NumberAxis[] valueAxis = new NumberAxis[3];
    for (int i = 0; i < valueAxis.length; i++)
    {
      valueAxis[i] = new VerticalNumberAxis(ranges[i]);
      valueAxis[i].setCrosshairVisible(false);
      if (i <= 1)
      {
        valueAxis[i].setAutoRangeIncludesZero(false);  // override default
      }
    }

    // create CombinedPlot...
    CombinedXYPlot multiPlot = new CombinedXYPlot(new HorizontalDateAxis(domain),
      CombinedXYPlot.VERTICAL);

    int[] weight = {1, 2, 2};

    // add subplot1...
    XYPlot subplot1 = new XYPlot(data0, null, new VerticalNumberAxis(ranges[0]));
    NumberAxis axis1 = (NumberAxis) subplot1.getRangeAxis();
    axis1.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
    axis1.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
    axis1.setAutoRangeIncludesZero(false);
    multiPlot.add(subplot1, weight[0]);

    // add subplot2 (an overlaid plot)...
    OverlaidXYPlot subplot2 = new OverlaidXYPlot(null, new VerticalNumberAxis(ranges[1]));
    NumberAxis axis2 = (NumberAxis) subplot2.getRangeAxis();
    axis2.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
    axis2.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
    axis2.setAutoRangeIncludesZero(false);
    XYPlot p1 = new XYPlot(series0, null, null);
    subplot2.add(p1);
    XYPlot p2 = new XYPlot(series1, null, null);
    subplot2.add(p2);

    multiPlot.add(subplot2, weight[1]);

    // add subplot3 (an overlaid plot)...
    OverlaidXYPlot subplot3 = new OverlaidXYPlot(null, new VerticalNumberAxis(ranges[2]));
    NumberAxis axis3 = (NumberAxis) subplot3.getRangeAxis();
    axis3.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
    axis3.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
    axis3.setAutoRangeIncludesZero(false);

    XYItemRenderer renderer3 = new HighLowRenderer();
    XYPlot p3 = new XYPlot(series2, null, null, renderer3);
    subplot3.add(p3);
    XYPlot p4 = new XYPlot(series3, null, null);
    subplot3.add(p4);

    multiPlot.add(subplot3, weight[2]);

    // now create the master JFreeChart object
    JFreeChart chart1 = new JFreeChart(title,
      new Font("SansSerif", Font.BOLD, 12),
      multiPlot, true);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 10));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
    return chart1;

  }

  /**
   * Displays an XY chart that is periodically updated by a background thread.  This is to
   * demonstrate the event notification system that automatically updates charts as required.
   *
   * @return a chart.
   */
  public JFreeChart createCombinedAndOverlaidDynamicXYChart()
  {

    // chart title and axis labels...
    String title = this.resources.getString("combined.dynamic.title");
    String subtitleStr = this.resources.getString("combined.dynamic.subtitle");
    String domain = this.resources.getString("combined.dynamic.domain");
    String[] ranges = this.resources.getStringArray("combined.dynamic.ranges");

    // setup sample base 2-series dataset
    SampleXYDataset data = new SampleXYDataset();

    // create some SubSeriesDatasets and CombinedDatasets to test events
    XYDataset series0 = new SubSeriesDataset(data, 0);
    XYDataset series1 = new SubSeriesDataset(data, 1);

    CombinedDataset combinedData = new CombinedDataset();
    combinedData.add(series0);
    combinedData.add(series1);

    // create common time axis
    NumberAxis timeAxis = new HorizontalNumberAxis(domain);
    timeAxis.setTickMarksVisible(true);
    timeAxis.setAutoRangeIncludesZero(false);
    timeAxis.setCrosshairVisible(false);

    // make one vertical axis for each (vertical) chart
    NumberAxis[] valueAxis = new NumberAxis[4];
    for (int i = 0; i < valueAxis.length; i++)
    {
      valueAxis[i] = new VerticalNumberAxis(ranges[i]);
      valueAxis[i].setAutoRangeIncludesZero(false);
      valueAxis[i].setCrosshairVisible(false);
    }

    CombinedXYPlot plot = new CombinedXYPlot(timeAxis, CombinedXYPlot.VERTICAL);

    // add subplot1...
    XYPlot subplot0 = new XYPlot(series0, null, valueAxis[0]);
    plot.add(subplot0, 1);

    // add subplot2...
    XYPlot subplot1 = new XYPlot(series1, null, valueAxis[1]);
    plot.add(subplot1, 1);

    // add subplot3...
    OverlaidXYPlot subplot2 = new OverlaidXYPlot(null, valueAxis[2]);

    // add two overlaid XY charts (share both axes)
    XYPlot p1 = new XYPlot(series0, null, null);
    subplot2.add(p1);
    XYPlot p2 = new XYPlot(series1, null, null);
    subplot2.add(p2);
    plot.add(subplot2, 1);

    // add subplot4...
    XYPlot subplot3 = new XYPlot(data, null, valueAxis[3]);
    plot.add(subplot3, 1);
    //combinedPlot.adjustPlots();

    JFreeChart chart1 = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

    // then customise it a little...
    TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
    chart1.addTitle(subtitle);
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.cyan));

    // setup thread to update base Dataset
    SampleXYDatasetThread update = new SampleXYDatasetThread(data);
    Thread thread = new Thread(update);
    thread.start();

    return chart1;

  }

  /**
   * Creates a gantt chart.
   *
   * @return a gantt chart.
   */
  public JFreeChart createGanttChart()
  {

    String title = resources.getString("gantt.task.title");
    String domain = resources.getString("gantt.task.domain");
    String range = resources.getString("gantt.task.range");

    IntervalCategoryDataset data = DemoDatasetFactory.createSampleGanttDataset();

    JFreeChart chart1 = ChartFactory.createGanttChart(title, domain, range, data, true);

    // then customise it a little...
    chart1.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
    return chart1;

  }

}
