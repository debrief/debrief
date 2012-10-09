package com.planetmayo.debrief.satc.views;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.planetmayo.debrief.satc.model.generator.BoundedStatesListener;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

public class SpatialView extends ViewPart implements BoundedStatesListener
{

	private static XYPlot _plot;
	private static XYLineAndShapeRenderer _renderer;
	private Action _debugMode;
	private Action _resizeButton;
	private XYSeriesCollection _myData;

	/**
	 * keep track of how many sets of series that we've plotted
	 * 
	 */
	int _numCycles = 0;
	private TrackGenerator _generator;

	public void createPartControl(Composite parent)
	{
		// get the data ready
		_myData = new XYSeriesCollection();

		JFreeChart chart = createChart(_myData);
		new ChartComposite(parent, SWT.NONE, chart, true);

		makeActions();

		IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(_debugMode);
		bars.getToolBarManager().add(_resizeButton);

		// start listening to data
		_generator = MockMaintainContributionsView.getGenerator();
		if (_generator != null)
		{
			_generator.addBoundedStateListener(this);
		}

	}

	@Override
	public void dispose()
	{
		if (_generator != null)
			_generator.removeBoundedStateListener(this);
		super.dispose();
	}

	private void makeActions()
	{
		_debugMode = new Action("Debug Mode", SWT.TOGGLE)
		{
		};
		_debugMode.setText("Debug Mode");
		_debugMode.setChecked(true);
		_debugMode
				.setToolTipText("Track all states (including application of each Contribution)");

		_resizeButton = new Action("Resize", SWT.NONE)
		{

			@Override
			public void run()
			{

				// TODO: resize the plot
			}

		};
		_resizeButton
				.setToolTipText("Track all states (including application of each Contribution)");
	}

	public void setFocus()
	{
	}

	/**
	 * Creates the Chart based on a dataset
	 * 
	 * @param _myData2
	 */

	private static JFreeChart createChart(XYDataset _myData2)
	{
		// tell it to draw joined series
		_renderer = new XYLineAndShapeRenderer(true, false);

		// NumberAxis rangeAx = new NumberAxis("Title on Log");
		// NumberAxis domainAx = new NumberAxis("Title on Lat");
		// SquaredXYPlot plot = new SquaredXYPlot(_myData2, rangeAx, domainAx,
		// renderer);
		// plot.setRenderer(renderer);
		// JFreeChart chart = new JFreeChart(plot);

		JFreeChart chart = ChartFactory.createScatterPlot("States", "Lat", "Lon",
				_myData2, PlotOrientation.HORIZONTAL, false, false, false);
		_plot = (XYPlot) chart.getPlot();
		_plot.setNoDataMessage("No data available");
		_plot.setRenderer(_renderer);

		return chart;
	}

	@Override
	public void debugStatesBounded(Collection<BoundedState> newStates)
	{
		if (_debugMode.isChecked())
			statesBounded(newStates);
	}

	private int _oldSeries = 0;

	private void showData(Collection<BoundedState> newStates)
	{
		// ditch the old series
		for (int i = 0; i < _oldSeries; i++)
		{
			_myData.removeSeries(i);
		}

		// mark the remaining series as lapsed
		// int len = _myData.getSeriesCount();
		// for(int i=0;i<len;i++)
		// {
		// _renderer.setSeriesStroke(i, new DStroke())
		// }

		// do we need to demote any shapes?
		Iterator<BoundedState> iter = newStates.iterator();
		while (iter.hasNext())
		{
			BoundedState thisS = (BoundedState) iter.next();
			// get the poly
			LocationRange loc = thisS.getLocation();
			if (loc != null)
			{
				// ok, we've got a new series
				XYSeries series = new XYSeries(thisS.getTime().toString() + "_"
						+ _numCycles++, false);

				// get the shape
				Polygon poly = loc.getPolygon();
				Coordinate[] boundary = poly.getCoordinates();
				for (int i = 0; i < boundary.length; i++)
				{
					Coordinate coordinate = boundary[i];
					series.add(new XYDataItem(coordinate.y, coordinate.x));
				}
				_myData.addSeries(series);
			}
		}
	}

	@Override
	public void statesBounded(Collection<BoundedState> newStates)
	{
		if ((newStates != null) && (newStates.size() > 0))
		{
			// hey, we've got data. show it
			showData(newStates);
		}
		else
		{
			_myData.removeAllSeries();
		}
	}

	@Override
	public void incompatibleStatesIdentified(IncompatibleStateException e)
	{
		_myData.removeAllSeries();
	}

}