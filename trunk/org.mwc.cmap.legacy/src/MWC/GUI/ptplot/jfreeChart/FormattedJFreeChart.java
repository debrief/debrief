/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Jan 13, 2003
 * Time: 11:24:43 AM
 * To change this template use Options | File Templates.
 */
package MWC.GUI.ptplot.jfreeChart;

//import com.jrefinery.chart.*;

import MWC.GUI.Editable;
import MWC.GUI.StepperListener;
import MWC.GUI.ptplot.jfreeChart.Utils.ColouredDataItem;
import MWC.GenericData.HiResDate;
import com.jrefinery.chart.*;

import java.awt.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.List;

/**
 * *******************************************************************
 * embedded class for plot for which we can control some of the formatting
 * (line width, axis steps/sizes, labels
 * *******************************************************************
 */
public class FormattedJFreeChart extends JFreeChart implements MWC.GUI.Editable
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  /**
   * the width of the data-line
   */
  private int _dataLineWidth = 2;

  /**
   * our editable details
   */
  transient private Editable.EditorType _myEditor = null;

  /**
   * the interval & format of the date axis
   */
  private DateAxisEditor.MWCDateTickUnitWrapper _theDateTick =
    new DateAxisEditor.MWCDateTickUnitWrapper(1, Calendar.MINUTE, "hh:mm");

  /**
   * the time offset supplier
   */
  private SwitchableTimeOffsetProvider _provider = null;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * Constructs a chart.
   * <P>
   * Note that the ChartFactory class contains static methods that will
   * return a ready-made chart.
   *
   * @param title        the main chart title.
   * @param titleFont    the font for displaying the chart title.
   * @param plot         controller of the visual representation of the data.
   * @param createLegend a flag indicating whether or not a legend should
   *                     be created for the chart.
   */
  public FormattedJFreeChart(String title,
                             Font titleFont,
                             Plot plot,
                             boolean createLegend)
  {
    super(title, titleFont, plot, createLegend);

    // update the line width's we're using
    this.setDataLineWidth(_dataLineWidth);

    // let's not show symbols by default, eh?
    this.setShowSymbols(false);
  }

  /**
   * Constructs a chart.
   * <P>
   * Note that the ChartFactory class contains static methods that will
   * return a ready-made chart.
   *
   * @param title        the main chart title.
   * @param titleFont    the font for displaying the chart title.
   * @param plot         controller of the visual representation of the data.
   * @param createLegend a flag indicating whether or not a legend should
   *                     be created for the chart.
   * @param stepper      the provider of the time offset
   */
  public FormattedJFreeChart(String title,
                             Font titleFont,
                             Plot plot,
                             boolean createLegend,
                             StepperListener.StepperController stepper)
  {

    this(title, titleFont, plot, createLegend);

    _provider = new SwitchableTimeOffsetProvider(stepper);
  }



  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  /**
   * the width of the data line
   *
   * @return width in pixels
   */
  public int getDataLineWidth()
  {
    return _dataLineWidth;
  }

  /**
   * set the width of the data line
   *
   * @param dataLineWidth width in pixels
   */
  public void setDataLineWidth(int dataLineWidth)
  {
    this._dataLineWidth = dataLineWidth;

    // and update the data
    Plot thePlot = getPlot();
    Stroke[] theStrokes = new Stroke[]{new BasicStroke(_dataLineWidth)};
    thePlot.setSeriesStroke(theStrokes);
  }


  /**
   * Returns the Stroke used to draw any shapes for a series.
   *
   * @param index the series (zero-based index).
   * @return the Stroke used to draw any shapes for a series.
   */
  public Stroke getSeriesStroke(int index)
  {
    Stroke res = getPlot().getSeriesStroke(index);

    res = new BasicStroke(_dataLineWidth);

    return res;
  }

  /**
   * the title of this plot
   *
   * @return
   */
  public String getTitle()
  {
    // get the titles for the plot
    java.util.List theTitles = this.getTitles();
    TextTitle tt = (TextTitle) theTitles.get(0);
    return tt.getText();
  }

  /**
   * the title of this plot
   *
   * @param title the new title to use
   */
  public void setTitle(String title)
  {
    // add the chart title, if one has been specified...
    ArrayList newTitles = new ArrayList();
    if (title != null)
    {
      TextTitle textTitle = new TextTitle(title, JFreeChart.DEFAULT_TITLE_FONT);
      textTitle.addChangeListener(this);
      newTitles.add(textTitle);
    }
    super.setTitles(newTitles);
  }

  /**
   * the title of this plot
   *
   * @param title the new title to use
   */
  public void setTitleFont(Font titleFont)
  {
    // add the chart title, if one has been specified...
    List theTitles = super.getTitles();
    if (theTitles != null)
    {
    	// find the title
    	for (Iterator iter = theTitles.iterator(); iter.hasNext();)
			{
				TextTitle thisT = (TextTitle) iter.next();
				thisT.setFont(titleFont);
			}
    }
  }

  /**
   * the title of this plot
   *
   * @param title the new title to use
   */
  public Font getTitleFont()
  {
  	Font res = null;
  	 // add the chart title, if one has been specified...
    List theTitles = super.getTitles();
    if (theTitles != null)
    {
    	// find the title
    	for (Iterator iter = theTitles.iterator(); iter.hasNext();)
			{
				TextTitle thisT = (TextTitle) iter.next();
				res = thisT.getFont();
				break;
			}
    }
    return res;
  }
  
  /**
   * the title of this plot
   *
   * @param title the new title to use
   */
  public void setAxisFont(Font axisFont)
  {
    Axis axis = (Axis) super.getXYPlot().getVerticalAxis();
    axis.setLabelFont(axisFont);
    axis = (Axis) super.getXYPlot().getHorizontalAxis();
    axis.setLabelFont(axisFont);
  }

  /**
   * the title of this plot
   *
   * @param title the new title to use
   */
  public Font getAxisFont()
  {
  	Font res = null;
    Axis axis = (Axis) super.getXYPlot().getVerticalAxis();
    res = axis.getLabelFont();  	

    return res;
  }
  

  /**
   * the title of this plot
   *
   * @param title the new title to use
   */
  public Font getTickFont()
  {
  	Font res = null;
    Axis axis = (Axis) super.getXYPlot().getVerticalAxis();
    res = axis.getTickLabelFont();
    return res;
  }  

  /**
   * the title of this plot
   *
   * @param title the new title to use
   */
  public void setTickFont(Font tickFont)
  {
    Axis axis = (Axis) super.getXYPlot().getVerticalAxis();
    axis.setTickLabelFont(tickFont);
    axis = (Axis) super.getXYPlot().getHorizontalAxis();
    axis.setTickLabelFont(tickFont);
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


  public boolean isShowSymbols()
  {
    StandardXYItemRenderer sx = (StandardXYItemRenderer) getXYPlot().getRenderer();
    return sx.getPlotShapes();
  }

  public void setShowSymbols(boolean showSymbols)
  {
    StandardXYItemRenderer sx = (StandardXYItemRenderer) getXYPlot().getRenderer();
    sx.setPlotShapes(showSymbols);

    this.fireChartChanged();
  }

  public String getY_AxisTitle()
  {
    return getXYPlot().getRangeAxis().getLabel();
  }

  public void setY_AxisTitle(String yTitle)
  {
    this.getXYPlot().getRangeAxis().setLabel(yTitle);
  }

  public String getX_AxisTitle()
  {
    return getXYPlot().getDomainAxis().getLabel();
  }

  public void setX_AxisTitle(String xTitle)
  {
    this.getXYPlot().getDomainAxis().setLabel(xTitle);
  }

  public DateAxisEditor.MWCDateTickUnitWrapper getDateTickUnits()
  {
    return _theDateTick;
  }

  public void setDateTickUnits(DateAxisEditor.MWCDateTickUnitWrapper theDateTick)
  {
    ValueAxis hd = this.getXYPlot().getDomainAxis();

    // store the current tick
    _theDateTick = theDateTick;

    if (theDateTick.isAutoScale())
    {
      hd.setAutoTickUnitSelection(true);
    }
    else
    {
      // cancel auto calc
      hd.setAutoTickUnitSelection(false);

      // get the date axis
      ValueAxis va = this.getXYPlot().getDomainAxis();
      com.jrefinery.chart.HorizontalDateAxis da = (com.jrefinery.chart.HorizontalDateAxis) va;

      // and set the tick
      da.setTickUnit(_theDateTick.getUnit());

    }
  }

  //////////////////////////////////////////////////
  // editable methods
  //////////////////////////////////////////////////

  /**
   * the editable details for this track
   *
   * @return the details
   */
  public final Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new PlotInfo(this);

    return _myEditor;
  }

  /**
   * the name of this object
   *
   * @return the name of this editable object
   */
  public String getName()
  {
    return getTitle();
  }

  /**
   * whether there is any edit information for this item
   * this is a convenience function to save creating the EditorType data
   * first
   *
   * @return yes/no
   */
  public boolean hasEditor()
  {
    return true;
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
   * update whether we're in relative time plotting mode
   *
   * @param val
   */
  public void setRelativeTimes(boolean val)
  {
    if (_provider != null)
    {
      // update whether it's active
      _provider.setApplied(val);

      // get the date axis
      ValueAxis va = this.getXYPlot().getDomainAxis();
      com.jrefinery.chart.HorizontalDateAxis da = (com.jrefinery.chart.HorizontalDateAxis) va;

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

  /**
   * *******************************************************************
   * embedded class which optionally applies an offset to the time value received
   * *******************************************************************
   */
  public static class SwitchableTimeOffsetProvider implements ColouredDataItem.OffsetProvider
  {
    /**
     * whether we are active
     */
    private boolean _applied = false;

    /**
     * the time controller
     */
    private StepperListener.StepperController _stepper;

    //////////////////////////////////////////////////
    // constructor
    //////////////////////////////////////////////////

    /**
     * create a time offset provider
     *
     * @param stepper
     */
    public SwitchableTimeOffsetProvider(StepperListener.StepperController stepper)
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
     * change whether we are active
     *
     * @param applied
     */
    public void setApplied(boolean applied)
    {
      _applied = applied;
    }


    /**
     * offset the provided time by the desired amount
     *
     * @param val the actual time value
     * @return the processed time value
     */
    public long offsetTimeFor(long val)
    {
      long res = val;

      if (_applied)
      {
        if (_stepper != null)
        {
          HiResDate dt = _stepper.getTimeZero();
          if (dt != null)
            res -= dt.getMicros();
        }
      }
      return res;
    }
  }


  /**
   * *******************************************************************
   * class containing editable details of this plot
   * *******************************************************************
   */
  public final class PlotInfo extends Editable.EditorType
  {

    /**
     * constructor for this editor, takes the actual track as a parameter
     *
     * @param data track being edited
     */
    public PlotInfo(FormattedJFreeChart data)
    {
      super(data, data.getName(), "");
    }

    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        PropertyDescriptor[] res = {
          longProp("DataLineWidth", "the width to draw the data lines", 
          		MWC.GUI.Properties.LineWidthPropertyEditor.class),
          prop("Title", "the title of this plot"),
          prop("X_AxisTitle", "the x axis title of this plot"),
          prop("Y_AxisTitle", "the y axis title of this plot"),
          prop("RelativeTimes", "whether to plot times relative to an anchor value (tZero)", EditorType.TEMPORAL),
          longProp("DateTickUnits", "the minutes separation to use on the grid (or -1)", DateAxisEditor.class, EditorType.TEMPORAL),
          prop("ShowSymbols", "whether to show symbols at the data points", EditorType.VISIBILITY),
          prop("TitleFont", "font to use for the plot title", EditorType.FORMAT),
          prop("AxisFont", "font to use for the plot axis titles", EditorType.FORMAT),
          prop("TickFont", "font to use for the plot axis tick mark labels", EditorType.FORMAT),
        };
        return res;
      }
      catch (IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }

}
