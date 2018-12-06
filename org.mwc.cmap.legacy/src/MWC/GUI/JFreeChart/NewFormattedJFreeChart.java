/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package MWC.GUI.JFreeChart;

//import com.jrefinery.chart.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.text.DateFormat;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.StepperListener;
import MWC.GUI.ToolParent;
import MWC.GUI.JFreeChart.DateAxisEditor.DatedRNFormatter;
import MWC.GUI.Properties.GraphicSizePropertyEditor;
import MWC.GUI.Properties.LineWidthPropertyEditor;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.GMTDateFormat;

/**
 * ******************************************************************* embedded class for plot for
 * which we can control some of the formatting (line width, axis steps/sizes, labels
 * *******************************************************************
 */
public class NewFormattedJFreeChart extends JFreeChart implements
    MWC.GUI.Editable
{

  public static class ChartDateFormatPropertyEditor extends
      MWC.GUI.Properties.DateFormatPropertyEditor
  {
    static public final String AUTO_VALUE = "Auto";

    static private final String[] stringTags =
    {AUTO_VALUE, "mm:ss.SSS", "HHmm.ss", "HHmm", "HHmm // ddHHmm", "ddHHmm",
        "ddHHmm.ss", "yy/MM/dd HH:mm",};

    private int getMyIndexOf(final String val)
    {
      int res = INVALID_INDEX;

      // cycle through the tags until we get a matching one
      for (int i = 0; i < getTags().length; i++)
      {
        final String thisTag = getTags()[i];
        if (thisTag.equals(val))
        {
          res = i;
          break;
        }

      }
      return res;
    }

    @Override
    public final String[] getTags()
    {
      return stringTags;
    }

    @Override
    public void setAsText(final String val)
    {
      _myFormat = getMyIndexOf(val);
    }

  }

  public final class PlotInfo extends Editable.EditorType
  {

    /**
     * constructor for this editor, takes the actual track as a parameter
     *
     * @param data
     *          track being edited
     */
    public PlotInfo(final NewFormattedJFreeChart data)
    {
      super(data, data.getName(), "");
    }

    @Override
    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {displayExpertLongProp("DataLineWidth", "Data line width",
            "the width to draw the data lines", EditorType.FORMAT,
            LineWidthPropertyEditor.class), displayProp("TitleText",
                "Title text", "the title of this plot"), displayProp(
                    "FixedDuration", "Fixed duration",
                    "How long a time-span to display", EditorType.TEMPORAL),
            displayProp("DisplayFixedDuration", "Display fixed duration",
                "Whether to show a limited time period (in Grow mode)",
                EditorType.TEMPORAL), displayProp("X_AxisTitle", "X axis title",
                    "the x axis title of this plot"), displayProp("Y_AxisTitle",
                        "Y axis title", "the y axis title of this plot"),
            displayProp("RelativeTimes", "Relative times",
                "whether to plot times relative to an anchor value (tZero)",
                EditorType.TEMPORAL),

            displayProp("ShowSymbols", "Show symbols",
                "whether to show symbols at the data points",
                EditorType.VISIBILITY), displayExpertLongProp("SymbolSize",
                    "Symbol size", "whether to show S/M/L symbols",
                    EditorType.FORMAT, GraphicSizePropertyEditor.class),
            displayProp("TitleFont", "Title font",
                "font to use for the plot title", EditorType.FORMAT),
            displayProp("AxisFont", "Axis font",
                "font to use for the plot axis titles", EditorType.FORMAT),
            displayProp("TickFont", "Tick font",
                "font to use for the plot axis tick mark labels",
                EditorType.FORMAT), displayProp("LegendFont", "Legend font",
                    "font to use for the legend", EditorType.FORMAT),
            displayProp("ShowLegend", "Show legend", "whether to show legend",
                EditorType.VISIBILITY), displayLongProp("LabelFormat",
                    "Label format",
                    "the time format of the label, or N/A to leave as-is",
                    ChartDateFormatPropertyEditor.class, FORMAT)};
        return res;
      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }

      // NOTE: we deprecated the tick units, since auto-tick units
      // works well
      // displayLongProp("DateTickUnits", "Date tick units",
      // "the minutes separation to the axis", DateAxisEditor.class,
      // EditorType.TEMPORAL),
    }

  }

  /**
   * ******************************************************************* embedded class which
   * optionally applies an offset to the time value received
   * *******************************************************************
   */
  public static class SwitchableTimeOffsetProvider implements
      ColouredDataItem.OffsetProvider
  {
    /**
     * whether we are active
     */
    private boolean _applied = false;

    /**
     * the time controller
     */
    private final StepperListener.StepperController _stepper;

    // ////////////////////////////////////////////////
    // constructor
    // ////////////////////////////////////////////////

    /**
     * create a time offset provider
     *
     * @param stepper
     */
    public SwitchableTimeOffsetProvider(
        final StepperListener.StepperController stepper)
    {
      this._stepper = stepper;
    }

    /**
     * whether we are active
     *
     * @return
     */
    public boolean isApplied()
    {
      return _applied;
    }

    /**
     * offset the provided time by the desired amount
     *
     * @param val
     *          the actual time value
     * @return the processed time value
     */
    @Override
    public long offsetTimeFor(final long val)
    {
      long res = val;

      if (_applied)
      {
        if (_stepper != null)
        {
          final HiResDate dt = _stepper.getTimeZero();
          if (dt != null)
          {
            res -= dt.getMicros();
          }
        }
      }
      return res;
    }

    /**
     * change whether we are active
     *
     * @param applied
     */
    public void setApplied(final boolean applied)
    {
      _applied = applied;
    }
  }

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public static final String CHART_LABEL_FORMAT = "ChartLabelFormat";

  private static ToolParent _toolParent;

  public static void initialise(final ToolParent toolParent)
  {
    _toolParent = toolParent;
  }

  // ////////////////////////////////////////////////
  // member variables
  // ////////////////////////////////////////////////
  /**
   * the width of the data-line
   */
  private int _dataLineWidth = 3;

  // ////////////////////////////////////////////////
  // constructor
  // ////////////////////////////////////////////////

  /**
   * our editable details
   */
  transient private Editable.EditorType _myEditor = null;

  /**
   * the time offset supplier
   */
  private SwitchableTimeOffsetProvider _provider = null;

  private Duration _fixedDuration;

  // ////////////////////////////////////////////////
  // member methods
  // ////////////////////////////////////////////////

  private String _labelFormat;

  /**
   * Constructs a chart.
   * <P>
   * Note that the ChartFactory class contains static methods that will return a ready-made chart.
   *
   * @param title
   *          the main chart title.
   * @param titleFont
   *          the font for displaying the chart title.
   * @param plot
   *          controller of the visual representation of the data.
   * @param createLegend
   *          a flag indicating whether or not a legend should be created for the chart.
   */
  public NewFormattedJFreeChart(final String title, final Font titleFont,
      final Plot plot, final boolean createLegend)
  {
    super(title, titleFont, plot, createLegend);

    _fixedDuration = new Duration(3, Duration.HOURS);

    // update the line width's we're using
    this.setDataLineWidth(_dataLineWidth);

    // let's not show symbols by default, eh?
    this.setShowSymbols(false);

    // initialise the time format - do we know the previous one?
    final String prefFormat = _toolParent.getProperty(CHART_LABEL_FORMAT);
    final String theFormat;
    if (prefFormat != null && prefFormat.length() > 0)
    {
      theFormat = prefFormat;
    }
    else
    {
      theFormat = "HHmm.ss";
    }
    setLabelFormat(theFormat);
  }

  /**
   * Constructs a chart.
   * <P>
   * Note that the ChartFactory class contains static methods that will return a ready-made chart.
   *
   * @param title
   *          the main chart title.
   * @param titleFont
   *          the font for displaying the chart title.
   * @param plot
   *          controller of the visual representation of the data.
   * @param createLegend
   *          a flag indicating whether or not a legend should be created for the chart.
   * @param stepper
   *          the provider of the time offset
   */
  public NewFormattedJFreeChart(final String title, final Font titleFont,
      final Plot plot, final boolean createLegend,
      final StepperListener.StepperController stepper)
  {

    this(title, titleFont, plot, createLegend);

    _provider = new SwitchableTimeOffsetProvider(stepper);
  }

  public Font getAxisFont()
  {
    return this.getXYPlot().getRangeAxis().getLabelFont();
  }

  /**
   * the width of the data line
   *
   * @return width in pixels
   */
  public int getDataLineWidth()
  {
    return _dataLineWidth;
  }

  public boolean getDisplayFixedDuration()
  {
    boolean res = false;
    final XYPlot xp = this.getXYPlot();
    if (xp instanceof StepperXYPlot)
    {
      final StepperXYPlot stp = (StepperXYPlot) xp;
      res = (stp.getFixedDuration() != null);
    }

    return res;
  }

  public Duration getFixedDuration()
  {
    return _fixedDuration;
  }

  /**
   * the editable details for this track
   *
   * @return the details
   */
  @Override
  public final Editable.EditorType getInfo()
  {
    if (_myEditor == null)
    {
      _myEditor = new PlotInfo(this);
    }

    return _myEditor;
  }

  public final String getLabelFormat()
  {
    return _labelFormat;
  }

  public Font getLegendFont()
  {
    final LegendTitle legend = this.getLegend();
    final Font res;
    if (legend != null)
    {
      res = legend.getItemFont();
    }
    else
    {
      res = LegendTitle.DEFAULT_ITEM_FONT;
    }
    return res;
  }

  /**
   * the name of this object
   *
   * @return the name of this editable object
   */
  @Override
  public String getName()
  {
    return getTitleText();
  }

  /**
   * find out if we're in relative time plotting mode
   *
   * @return
   */
  public boolean getRelativeTimes()
  {
    boolean res = false;

    // do we have a provider?
    if (_provider != null)
    {
      // is it active?
      res = _provider.isApplied();
    }
    return res;
  }

  /**
   * Returns the Stroke used to draw any shapes for a series.
   *
   * @param index
   *          the series (zero-based index).
   * @return the Stroke used to draw any shapes for a series.
   */
  public Stroke getSeriesStroke(final int index)
  {
    final XYPlot plot = (XYPlot) getPlot();
    Stroke res = plot.getRenderer().getSeriesStroke(index);

    res = new BasicStroke(_dataLineWidth);

    return res;
  }

  public double getSymbolSize()
  {
    final ColourStandardXYItemRenderer renderer =
        (ColourStandardXYItemRenderer) this.getXYPlot().getRenderer();
    return renderer.getSymbolSize();
  }

  public Font getTickFont()
  {
    return this.getXYPlot().getRangeAxis().getTickLabelFont();
  }

  /**
   * accessor to get hold of the time offset provider
   *
   * @return
   */
  public SwitchableTimeOffsetProvider getTimeOffsetProvider()
  {
    return _provider;
  }

  /**
   * the title of this plot
   *
   * @param title
   *          the new title to use
   */
  public Font getTitleFont()
  {
    return this.getTitle().getFont();
  }

  /**
   * the title of this plot
   *
   * @return
   */
  public String getTitleText()
  {
    return this.getTitle().getText();
  }

  public String getX_AxisTitle()
  {
    return getXYPlot().getDomainAxis().getLabel();
  }

  public String getY_AxisTitle()
  {
    return getXYPlot().getRangeAxis().getLabel();
  }

  /**
   * whether there is any edit information for this item this is a convenience function to save
   * creating the EditorType data first
   *
   * @return yes/no
   */
  @Override
  public boolean hasEditor()
  {
    return true;
  }

  public boolean isShowLegend()
  {
    return getLegend() != null;
  }

  public boolean isShowSymbols()
  {
    final DefaultXYItemRenderer sx = (DefaultXYItemRenderer) getXYPlot()
        .getRenderer();
    return sx.getBaseShapesVisible();
  }

  public void setAxisFont(final Font axisFont)
  {
    this.getXYPlot().getRangeAxis().setLabelFont(axisFont);
    this.getXYPlot().getDomainAxis().setLabelFont(axisFont);
  }

  /**
   * set the width of the data line
   *
   * @param dataLineWidth
   *          width in pixels
   */
  public void setDataLineWidth(final int dataLineWidth)
  {
    this._dataLineWidth = dataLineWidth;

    // and update the data
    final XYPlot thePlot = (XYPlot) getPlot();
    final Stroke[] theStrokes = new Stroke[]
    {new BasicStroke(_dataLineWidth)};
    for (int i = 0; i < theStrokes.length; i++)
    {
      final Stroke stroke = theStrokes[i];
      thePlot.getRenderer().setSeriesStroke(i, stroke);
    }
  }

  public void setDisplayFixedDuration(final boolean val)
  {
    final XYPlot xp = this.getXYPlot();
    if (xp instanceof StepperXYPlot)
    {
      final StepperXYPlot stp = (StepperXYPlot) xp;
      if (val)
      {
        stp.setFixedDuration(_fixedDuration);
      }
      else
      {
        stp.setFixedDuration(null);
      }
    }

    this.fireChartChanged();
  }

  public void setFixedDuration(final Duration dur)
  {
    _fixedDuration = dur;

    // right, we've remembered the value, but if the plot is already
    // set to display fixed duration we need to fire the data to the
    // plot
    if (getDisplayFixedDuration())
    {
      // yes, we're displaying fixed duration, remind
      // everybody what's happening
      setDisplayFixedDuration(true);
    }
  }

  @FireReformatted
  public final void setLabelFormat(final String format)
  {
    _labelFormat = format;

    // get the date axis
    final ValueAxis va = this.getXYPlot().getDomainAxis();
    final DateAxis da = (DateAxis) va;

    // special case, if it's AUTO format
    final DateFormat target;
    if (ChartDateFormatPropertyEditor.AUTO_VALUE.equals(format))
    {
      target = null;
    }
    else
    {
      // see if it's a special format
      if (format.contains("//"))
      {
        final String[] items = format.split("//");
        target = new DatedRNFormatter(items[0], items[1]);
      }
      else
      {
        target = new GMTDateFormat(format);
      }
    }

    // and set the tick
    if(da != null)
    {
      da.setDateFormatOverride(target);
    }

    // also store it as a pref, for the next format
    _toolParent.setProperty(CHART_LABEL_FORMAT, format);
  }

  public void setLegendFont(final Font legendFont)
  {
    final LegendTitle legend = this.getLegend();
    if (legend != null)
    {
      legend.setItemFont(legendFont);
    }
  }

  /**
   * update whether we're in relative time plotting mode
   *
   * @param val
   */
  public void setRelativeTimes(final boolean val)
  {
    if (_provider != null)
    {
      // update whether it's active
      _provider.setApplied(val);

      // get the date axis
      final ValueAxis va = this.getXYPlot().getDomainAxis();
      final RelativeDateAxis da = (RelativeDateAxis) va;

      // do rescale
      da.configure();

      // and tell it what we're doing
      da.setRelativeTimes(val);

      // and trigger repaint
      this.fireChartChanged();

      // hey, do the rescale here, it works better
      getXYPlot().getRangeAxis().setAutoRange(true);
      da.setAutoRange(true);
    }

  }

  // ////////////////////////////////////////////////
  // editable methods
  // ////////////////////////////////////////////////

  public void setShowLegend(final boolean showLegend)
  {
    if (showLegend)
    {
      if (!isShowLegend())
      {
        final LegendTitle legend = new LegendTitle(getPlot());
        legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
        legend.setFrame(new LineBorder());
        legend.setBackgroundPaint(Color.white);
        legend.setPosition(RectangleEdge.BOTTOM);
        addLegend(legend);
      }
    }
    else
    {
      if (isShowLegend())
      {
        removeLegend();
      }
    }

    this.fireChartChanged();
  }

  public void setShowSymbols(final boolean showSymbols)
  {
    final DefaultXYItemRenderer sx = (DefaultXYItemRenderer) getXYPlot()
        .getRenderer();
    sx.setBaseShapesVisible(showSymbols);

    this.fireChartChanged();
  }

  public void setSymbolSize(final double size)
  {
    final ColourStandardXYItemRenderer renderer =
        (ColourStandardXYItemRenderer) this.getXYPlot().getRenderer();
    renderer.setSymbolSize(size);

    // ok, trigger redraw
    this.fireChartChanged();
  }

  public void setTickFont(final Font tickFont)
  {
    this.getXYPlot().getRangeAxis().setTickLabelFont(tickFont);
    this.getXYPlot().getDomainAxis().setTickLabelFont(tickFont);
  }

  /**
   * the title of this plot
   *
   * @param title
   *          the new title to use
   */
  public void setTitleFont(final Font titleFont)
  {
    this.getTitle().setFont(titleFont);
  }

  public void setTitleText(final String text)
  {
    this.getTitle().setText(text);
  }

  public void setX_AxisTitle(final String xTitle)
  {
    this.getXYPlot().getDomainAxis().setLabel(xTitle);
  }

  public void setY_AxisTitle(final String yTitle)
  {
    this.getXYPlot().getRangeAxis().setLabel(yTitle);
  }

}
