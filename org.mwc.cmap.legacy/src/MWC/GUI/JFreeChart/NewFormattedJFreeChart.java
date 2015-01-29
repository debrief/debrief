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
import java.awt.Font;
import java.awt.Stroke;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Calendar;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;

import MWC.GUI.Editable;
import MWC.GUI.StepperListener;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;

/**
 * ******************************************************************* embedded
 * class for plot for which we can control some of the formatting (line width,
 * axis steps/sizes, labels
 * *******************************************************************
 */
public class NewFormattedJFreeChart extends JFreeChart implements
		MWC.GUI.Editable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////
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
	private DateAxisEditor.MWCDateTickUnitWrapper _theDateTick = new DateAxisEditor.MWCDateTickUnitWrapper(
			DateTickUnitType.MINUTE, Calendar.MINUTE, "HH:mm");

	/**
	 * the time offset supplier
	 */
	private SwitchableTimeOffsetProvider _provider = null;

	private Duration _fixedDuration;

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	/**
	 * Constructs a chart.
	 * <P>
	 * Note that the ChartFactory class contains static methods that will return a
	 * ready-made chart.
	 * 
	 * @param title
	 *          the main chart title.
	 * @param titleFont
	 *          the font for displaying the chart title.
	 * @param plot
	 *          controller of the visual representation of the data.
	 * @param createLegend
	 *          a flag indicating whether or not a legend should be created for
	 *          the chart.
	 */
	public NewFormattedJFreeChart(final String title, final Font titleFont, final Plot plot,
			final boolean createLegend)
	{
		super(title, titleFont, plot, createLegend);
		
		_fixedDuration = new Duration(3, Duration.HOURS);

		// update the line width's we're using
		this.setDataLineWidth(_dataLineWidth);
		
		// let's not show symbols by default, eh?
		this.setShowSymbols(false);
	}

	/**
	 * Constructs a chart.
	 * <P>
	 * Note that the ChartFactory class contains static methods that will return a
	 * ready-made chart.
	 * 
	 * @param title
	 *          the main chart title.
	 * @param titleFont
	 *          the font for displaying the chart title.
	 * @param plot
	 *          controller of the visual representation of the data.
	 * @param createLegend
	 *          a flag indicating whether or not a legend should be created for
	 *          the chart.
	 * @param stepper
	 *          the provider of the time offset
	 */
	public NewFormattedJFreeChart(final String title, final Font titleFont, final Plot plot,
			final boolean createLegend, final StepperListener.StepperController stepper)
	{

		this(title, titleFont, plot, createLegend);

		_provider = new SwitchableTimeOffsetProvider(stepper);
	}

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

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
	 * @param dataLineWidth
	 *          width in pixels
	 */
	public void setDataLineWidth(final int dataLineWidth)
	{
		this._dataLineWidth = dataLineWidth;

		// and update the data
		final XYPlot thePlot = (XYPlot) getPlot();
		final Stroke[] theStrokes = new Stroke[]
		{ new BasicStroke(_dataLineWidth) };
		for (int i = 0; i < theStrokes.length; i++)
		{
			final Stroke stroke = theStrokes[i];
			thePlot.getRenderer().setSeriesStroke(i, stroke);
		}
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

	/**
	 * the title of this plot
	 * 
	 * @return
	 */
	public String getTitleText()
	{
		return this.getTitle().getText();
	}

	public void setTitleText(final String text)
	{
		this.getTitle().setText(text);
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

	public Font getTickFont()
	{
		return this.getXYPlot().getRangeAxis().getTickLabelFont();
	}

	public void setTickFont(final Font tickFont)
	{
		this.getXYPlot().getRangeAxis().setTickLabelFont(tickFont);
		this.getXYPlot().getDomainAxis().setTickLabelFont(tickFont);
	}

	public Font getAxisFont()
	{
		return this.getXYPlot().getRangeAxis().getLabelFont();
	}

	public void setAxisFont(final Font axisFont)
	{
		this.getXYPlot().getRangeAxis().setLabelFont(axisFont);
		this.getXYPlot().getDomainAxis().setLabelFont(axisFont);
	}

	public void setFixedDuration(final Duration dur)
	{
		_fixedDuration = dur;
		
		// right, we've remembered the value, but if the plot is already
		// set to display fixed duration we need to fire the data to the
		// plot
		if(getDisplayFixedDuration())
		{
			// yes, we're displaying fixed duration, remind
			// everybody what's happening
			setDisplayFixedDuration(true);
		}
	}
	
	public Duration getFixedDuration()
	{
		return _fixedDuration;
	}
	
	public void setDisplayFixedDuration(final boolean val)
	{
		final XYPlot xp = this.getXYPlot();
		if(xp instanceof StepperXYPlot)
		{
	    final StepperXYPlot	stp = (StepperXYPlot) xp;
	    if(val)
	    stp.setFixedDuration(_fixedDuration);
	    else
	    	stp.setFixedDuration(null);
		}
		
		this.fireChartChanged();
	}
	
	public boolean getDisplayFixedDuration()
	{
		boolean res = false;
		final XYPlot xp = this.getXYPlot();
		if(xp instanceof StepperXYPlot)
		{
	    final StepperXYPlot	stp = (StepperXYPlot) xp;
	    res = (stp.getFixedDuration() != null);
		}
		
		return res;
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
		final DefaultXYItemRenderer sx = (DefaultXYItemRenderer) getXYPlot()
				.getRenderer();
		return sx.getBaseShapesVisible();
	}

	public void setShowSymbols(final boolean showSymbols)
	{
		final DefaultXYItemRenderer sx = (DefaultXYItemRenderer) getXYPlot()
				.getRenderer();
		sx.setBaseShapesVisible(showSymbols);

		this.fireChartChanged();
	}

	public String getY_AxisTitle()
	{
		return getXYPlot().getRangeAxis().getLabel();
	}

	public void setY_AxisTitle(final String yTitle)
	{
		this.getXYPlot().getRangeAxis().setLabel(yTitle);
	}

	public String getX_AxisTitle()
	{
		return getXYPlot().getDomainAxis().getLabel();
	}

	public void setX_AxisTitle(final String xTitle)
	{
		this.getXYPlot().getDomainAxis().setLabel(xTitle);
	}

	public DateAxisEditor.MWCDateTickUnitWrapper getDateTickUnits()
	{
		return _theDateTick;
	}

	public void setDateTickUnits(final DateAxisEditor.MWCDateTickUnitWrapper theDateTick)
	{
		final ValueAxis hd = this.getXYPlot().getDomainAxis();

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
			final ValueAxis va = this.getXYPlot().getDomainAxis();
			final DateAxis da = (DateAxis) va;

			// and set the tick
			da.setTickUnit(_theDateTick.getUnit());

		}
	}

	// ////////////////////////////////////////////////
	// editable methods
	// ////////////////////////////////////////////////

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
		return getTitleText();
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
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

	/**
	 * *******************************************************************
	 * embedded class which optionally applies an offset to the time value
	 * received
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
		 * change whether we are active
		 * 
		 * @param applied
		 */
		public void setApplied(final boolean applied)
		{
			_applied = applied;
		}

		/**
		 * offset the provided time by the desired amount
		 * 
		 * @param val
		 *          the actual time value
		 * @return the processed time value
		 */
		public long offsetTimeFor(final long val)
		{
			long res = val;

			if (_applied)
			{
				if (_stepper != null)
				{
					final HiResDate dt = _stepper.getTimeZero();
					if (dt != null)
						res -= dt.getMicros();
				}
			}
			return res;
		}
	}

	/**
	 * ******************************************************************* class
	 * containing editable details of this plot
	 * *******************************************************************
	 */
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

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{

						 longProp("DataLineWidth", "the width to draw the data lines",
						 MWC.GUI.Properties.LineWidthPropertyEditor.class),
						 prop("TitleText", "the title of this plot"),
						 prop("FixedDuration", "How long a time-span to display", EditorType.TEMPORAL),
						 prop("DisplayFixedDuration", "Whether to show a limited time period (in Grow mode)", EditorType.TEMPORAL),
						 prop("X_AxisTitle", "the x axis title of this plot"),
						 prop("Y_AxisTitle", "the y axis title of this plot"),
						 prop("RelativeTimes",
						 "whether to plot times relative to an anchor value (tZero)",
						 EditorType.TEMPORAL),
						longProp("DateTickUnits", "the minutes separation to the axis",
								DateAxisEditor.class, EditorType.TEMPORAL),
						prop("ShowSymbols", "whether to show symbols at the data points",
								EditorType.VISIBILITY),
						prop("TitleFont", "font to use for the plot title",
								EditorType.FORMAT),
						prop("AxisFont", "font to use for the plot axis titles",
								EditorType.FORMAT),
						prop("TickFont", "font to use for the plot axis tick mark labels",
								EditorType.FORMAT)
				};
				return res;
			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

	}

}
