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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.BoundedStatesListener;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class SpatialView extends Composite implements BoundedStatesListener
{

	@UiField(provided = true)
	SimplePlot plot = new SimplePlot(new PlotModel(), new PlotOptions());

	interface SpatialViewUiBinder extends UiBinder<Widget, SpatialView>
	{
	}

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
		plotOptions.getGridOptions().setMinBorderMargin(5);

		// support for zoom
		plotOptions.setZoomOptions(new ZoomOptions().setInteractive(true))
				.setPanOptions(new PanOptions().setInteractive(true));

		// format xAxis date display
		plot.setHeight("300px");
		plot.setWidth("100%");

	}

	@Override
	public void debugStatesBounded(Collection<BoundedState> newStates)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void statesBounded(Collection<BoundedState> newStates)
	{
		// clear the plot
		clearPlot();

		if (newStates == null)
			return;

		// ok, go for it. display the states
		Iterator<BoundedState> iter = newStates.iterator();
		while (iter.hasNext())
		{
			BoundedState boundedState = (BoundedState) iter.next();
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

	private void clearPlot()
	{
		plot.getModel().removeAllSeries();
	}

	@Override
	public void incompatibleStatesIdentified(BaseContribution contribution, IncompatibleStateException e)
	{
		// TODO Auto-generated method stub

	}

}
