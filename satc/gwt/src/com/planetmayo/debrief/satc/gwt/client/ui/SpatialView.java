package com.planetmayo.debrief.satc.gwt.client.ui;

import java.util.Collection;
import java.util.Iterator;

import ca.nanometrics.gflot.client.DataPoint;
import ca.nanometrics.gflot.client.PlotModel;
import ca.nanometrics.gflot.client.SeriesHandler;
import ca.nanometrics.gflot.client.SimplePlot;
import ca.nanometrics.gflot.client.options.GlobalSeriesOptions;
import ca.nanometrics.gflot.client.options.GridOptions;
import ca.nanometrics.gflot.client.options.LegendOptions;
import ca.nanometrics.gflot.client.options.LegendOptions.LegendPosition;
import ca.nanometrics.gflot.client.options.LineSeriesOptions;
import ca.nanometrics.gflot.client.options.PanOptions;
import ca.nanometrics.gflot.client.options.PlotOptions;
import ca.nanometrics.gflot.client.options.PointsSeriesOptions;
import ca.nanometrics.gflot.client.options.ZoomOptions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class SpatialView extends Composite implements IConstrainSpaceListener
{

	interface SpatialViewUiBinder extends UiBinder<Widget, SpatialView>
	{
	}

	@UiField(provided = true)
	SimplePlot plot = new SimplePlot(new PlotModel(), new PlotOptions());

	@UiField InlineLabel debug;
	@UiField InlineLabel clear;
	
	@UiHandler("clear")
	void clearClick(ClickEvent e)
	{
		clearPlot();
	}
	
	@UiHandler("debug")
	void clearDebug(ClickEvent e)
	{
		if(debug.getStyleName().contains("clicked"))
		{
			_inDebug = false;
			debug.removeStyleName("clicked");
		}
		else
		{
			_inDebug = true;
			debug.addStyleName("clicked");
		}
	}
	
	
	// TODO: Akash - this will actually be a GWT uiField, a toggle button
	Boolean _inDebug = false;

	private static SpatialViewUiBinder uiBinder = GWT
			.create(SpatialViewUiBinder.class);

	public SpatialView()
	{
		initWidget(uiBinder.createAndBindUi(this));

		// format the plot
		PlotOptions plotOptions = plot.getPlotOptions();

		// add point series option to show dot (small circle) on point
		plotOptions.setGlobalSeriesOptions(new GlobalSeriesOptions()
				.setLineSeriesOptions(
						new LineSeriesOptions().setLineWidth(1).setShow(true))
				.setPointsOptions(new PointsSeriesOptions().setRadius(2).setShow(true))
				.setShadowSize(2d));

		plotOptions.setGridOptions(new GridOptions().setHoverable(true));
		plotOptions.setLegendOptions(new LegendOptions().setPosition(
				LegendPosition.NORTH_EAST).setNumOfColumns(5));
		plotOptions.getLegendOptions().setShow(false);

		// plotOptions.getGridOptions().clearMinBorderMargin();
		plotOptions.getGridOptions().setBorderWidth(1);
		plotOptions.getGridOptions().setBorderColor("#CCC");
		plotOptions.getGridOptions().setMinBorderMargin(5);

		// support for zoom
		plotOptions.setZoomOptions(new ZoomOptions().setInteractive(true))
				.setPanOptions(new PanOptions().setInteractive(true));

		// format xAxis date display
		plot.setHeight("30em");
		plot.setWidth("100%");

	}

	private void clearPlot()
	{
		plot.getModel().removeAllSeries();
	}

	
	@Override
	public void statesBounded(IBoundsManager boundsManager)
	{
		plotThis(boundsManager.getSpace().states());		
	}

	@Override
	public void restarted(IBoundsManager boundsManager)
	{
		// ok, do a clear, so we redraw afresh
		clearPlot();
		
		plotThis(boundsManager.getSpace().states());		
	}

	@Override
	public void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps)
	{
		if (_inDebug)
			plotThis(boundsManager.getSpace().states());		
	}

	@Override
	public void error(IBoundsManager boundsManager, IncompatibleStateException ex)
	{
	}

	private void plotThis(Collection<BoundedState> newStates)
	{
		if (newStates == null)
		{
			clearPlot();
			return;
		}

		// ok, go for it. display the states
		Iterator<BoundedState> iter = newStates.iterator();
		while (iter.hasNext())
		{
			BoundedState boundedState = iter.next();
			LocationRange loc = boundedState.getLocation();
			if (loc != null)
			{
				// ok, we've got a new series
				SeriesHandler ser = plot.getModel().addSeries(
						boundedState.getTime().toString());

				// get the shape
				double[][] coords = GeoSupport.getCoordsFor(loc);
				for (int i = 0; i < coords.length; i++)
				{
					DataPoint dp = new DataPoint(coords[i][0], coords[i][1]);
					ser.add(dp);
				}
			}
		}

		plot.redraw();
	}

}
