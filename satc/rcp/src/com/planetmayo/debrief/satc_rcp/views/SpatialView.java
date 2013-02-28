package com.planetmayo.debrief.satc_rcp.views;

import java.awt.BasicStroke;
import java.awt.Color;
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
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.ISteppingListener;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class SpatialView extends ViewPart implements ISteppingListener,
		GeoSupport.GeoPlotter
{
	private IBoundsManager boundsManager;

	private static XYPlot _plot;
	private static XYLineAndShapeRenderer _renderer;
	private static JFreeChart _chart;

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

		_chart = ChartFactory.createScatterPlot("States", "Lat", "Lon", _myData2,
				PlotOrientation.HORIZONTAL, true, false, false);
		_plot = (XYPlot) _chart.getPlot();
		_plot.setBackgroundPaint(Color.WHITE);
		_plot.setDomainCrosshairPaint(Color.LIGHT_GRAY);
		_plot.setRangeCrosshairPaint(Color.LIGHT_GRAY);
		_plot.setNoDataMessage("No data available");
		_plot.setRenderer(_renderer);
		

		return _chart;
	}

	private Action _debugMode;
	private Action _resizeButton;

	private XYSeriesCollection _myData;

	/**
	 * keep track of how many sets of series that we've plotted
	 * 
	 */
	int _numCycles = 0;

	private Action _showLegend;

	@Override
	public void clear(String title)
	{
		_myData.removeAllSeries();

		if (title != null)
			_chart.setTitle(new TextTitle(title, new java.awt.Font("SansSerif",
					java.awt.Font.BOLD, 8)));
		else
			_chart.setTitle(title);
	}

	@Override
	public void createPartControl(Composite parent)
	{
		boundsManager = SATC_Activator.getDefault().getService(
				IBoundsManager.class, true);
		// get the data ready
		_myData = new XYSeriesCollection();

		JFreeChart chart = createChart(_myData);
		new ChartComposite(parent, SWT.NONE, chart, true);

		makeActions();

		IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(_showLegend);
		bars.getToolBarManager().add(_debugMode);
		bars.getToolBarManager().add(_resizeButton);

		// tell the GeoSupport about us
		GeoSupport.setPlotter(this);
		boundsManager.addSteppingListener(this);
	}

	@Override
	public void dispose()
	{
		boundsManager.removeSteppingListener(this);
		super.dispose();
	}

	@Override
	public void complete(IBoundsManager boundsManager)
	{
		showData(boundsManager.getSpace().states());
	}

	@Override
	public void restarted(IBoundsManager boundsManager)
	{
		clear(null);
	}

	@Override
	public void error(IBoundsManager boundsManager, IncompatibleStateException ex)
	{
		String theCont = boundsManager.getCurrentContribution().getName();
		clear("In contribution:[" + theCont + "] problem is: ["
				+ ex.getLocalizedMessage() + "]");
	}

	@Override
	public void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps)
	{
		if (_debugMode.isChecked())
			showData(boundsManager.getSpace().states());
	}

	private void makeActions()
	{
		_debugMode = new Action("Debug Mode", SWT.TOGGLE)
		{
		};
		_debugMode.setText("Debug Mode");
		_debugMode.setChecked(false);
		_debugMode
				.setToolTipText("Track all states (including application of each Contribution)");

		_showLegend = new Action("Show Legend", SWT.TOGGLE)
		{
			public void run()
			{
				super.run();
				_chart.getLegend(0).setVisible(_showLegend.isChecked());
			}
		};
		_showLegend.setText("Show Legend");
		_showLegend.setChecked(false);
		_showLegend
				.setToolTipText("Show the legend");

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

	@Override
	public void setFocus()
	{
	}

	private void showData(Collection<BoundedState> newStates)
	{
		if (newStates.isEmpty())
		{
			return;
		}
		// clear the data
		// _myData.removeAllSeries();

		// and plot the new data
		Iterator<BoundedState> iter = newStates.iterator();
		while (iter.hasNext())
		{
			BoundedState thisS = iter.next();
			// get the poly
			LocationRange loc = thisS.getLocation();
			if (loc != null)
			{
				// ok, we've got a new series
				XYSeries series = new XYSeries(thisS.getTime().toString() + "_"
						+ _numCycles++, false);

				// get the shape
				Geometry geometry = loc.getGeometry();
				Coordinate[] boundary = geometry.getCoordinates();
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
	public void showGeometry(String title, Coordinate[] coords)
	{

		// are we in debug mode?
		if (!_debugMode.isChecked())
			return;

		// ok, we've got a new series
		XYSeries series = new XYSeries(title, false);

		// get the shape
		for (int i = 0; i < coords.length; i++)
		{
			Coordinate coordinate = coords[i];
			series.add(new XYDataItem(coordinate.y, coordinate.x));
		}
		_myData.addSeries(series);

		// get the series num
		int num = _myData.getSeriesCount();

		_renderer.setSeriesStroke(num, new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND, 1.0f, new float[]
				{ 10.0f, 6.0f }, 0.0f));

	}
}