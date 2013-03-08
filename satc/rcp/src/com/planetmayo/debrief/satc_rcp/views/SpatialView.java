package com.planetmayo.debrief.satc_rcp.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
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
import com.planetmayo.debrief.satc.model.generator.IBoundsManager.IShowBoundProblemSpaceDiagnostics;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager.IShowGenerateSolutionsDiagnostics;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class SpatialView extends ViewPart implements IConstrainSpaceListener,
		GeoSupport.GeoPlotter, IShowBoundProblemSpaceDiagnostics,
		IShowGenerateSolutionsDiagnostics, IGenerateSolutionsListener
{
	private static JFreeChart _chart;

	private static XYPlot _plot;
	private static XYLineAndShapeRenderer _renderer;
	private Action _debugMode;

	private XYSeriesCollection _myData;
	/**
	 * keep track of how many sets of series that we've plotted
	 * 
	 */
	int _numCycles = 0;
	private Action _resizeButton;

	private Action _showLegend;

	private IBoundsManager boundsManager;

	private boolean _showLegEndBounds;

	private boolean _showAllBounds;

	/**
	 * level of diagnostics for user
	 * 
	 * @see IBoundsManager.IShowGenerateSolutionsDiagnostics
	 */
	private boolean _showPoints;

	/**
	 * level of diagnostics for user
	 * 
	 * @see IBoundsManager.IShowGenerateSolutionsDiagnostics
	 */
	private boolean _showAchievablePoints;

	/**
	 * level of diagnostics for user
	 * 
	 * @see IBoundsManager.IShowGenerateSolutionsDiagnostics
	 */
	private boolean _showRoutes;

	/**
	 * level of diagnostics for user
	 * 
	 * @see IBoundsManager.IShowGenerateSolutionsDiagnostics
	 */
	private boolean _showRoutesWithScores;

	/**
	 * the last set of states we plotted
	 * 
	 */
	private Collection<BoundedState> _lastStates = null;

	private ISolutionGenerator solutionGenerator;

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
	public void statesBounded(IBoundsManager boundsManager)
	{
		_lastStates = boundsManager.getSpace().states();
		showData(_lastStates);
	}

	/**
	 * Creates the Chart based on a dataset
	 * 
	 * @param _myData2
	 */

	private JFreeChart createChart(XYDataset _myData2)
	{
		// tell it to draw joined series
		_renderer = new XYLineAndShapeRenderer(true, false);

		_chart = ChartFactory.createScatterPlot("States", "Lat", "Lon", _myData2,
				PlotOrientation.HORIZONTAL, true, false, false);
		_plot = (XYPlot) _chart.getPlot();
		_plot.setBackgroundPaint(Color.WHITE);
		_plot.setDomainCrosshairPaint(Color.LIGHT_GRAY);
		_plot.setRangeCrosshairPaint(Color.LIGHT_GRAY);
		_plot.setNoDataMessage("No data available");
		_plot.setRenderer(_renderer);
		_chart.getLegend().setVisible(false);

		return _chart;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		boundsManager = SATC_Activator.getDefault().getService(
				IBoundsManager.class, true);
		solutionGenerator = SATC_Activator.getDefault().getService(
				ISolutionGenerator.class, true);
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
		GeoSupport.setPlotter(this, this, this);
		boundsManager.addBoundStatesListener(this);
		solutionGenerator.addReadyListener(this);
	}

	@Override
	public void dispose()
	{
		boundsManager.removeSteppingListener(this);
		super.dispose();
	}

	@Override
	public void error(IBoundsManager boundsManager, IncompatibleStateException ex)
	{
		String theCont = boundsManager.getCurrentContribution().getName();
		clear("In contribution:[" + theCont + "] problem is: ["
				+ ex.getLocalizedMessage() + "]");
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
		_showLegend.setToolTipText("Show the legend");

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
	public void restarted(IBoundsManager boundsManager)
	{
		clear(null);
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

		// just double-check that we're showing any states
		if (!_showLegEndBounds && !_showAllBounds)
			return;

		String lastSeries = "UNSET";
		Color thisColor = null;
		int turnCounter = 1;

		// and plot the new data
		Iterator<BoundedState> iter = newStates.iterator();
		while (iter.hasNext())
		{
			BoundedState thisS = iter.next();
			// get the poly
			LocationRange loc = thisS.getLocation();
			if (loc != null)
			{
				boolean showThisState = false;

				// ok, color code the series
				String thisSeries = thisS.getMemberOf();

				// ok, what about the name?
				String legName = null;

				if (thisSeries != lastSeries)
				{
					// right this is the start of a new leg

					// are we storing leg ends?
					if (_showLegEndBounds)
					{
						showThisState = true;

						if (thisSeries != null)
							legName = thisSeries;
						else
							legName = "Turn " + turnCounter++;

					}

					// ok, use new color

					// TODO: generate a new color. We should prob allow up to 20 colors, I
					// welcome
					// a strategy for generateNewColor()
					thisColor = generateNewColor(thisSeries);

					// and remember the new series
					lastSeries = thisSeries;

				}

				// are we adding this leg?
				if (!showThisState)
				{
					// no, but are we showing mid=leg states?
					if (_showAllBounds)
					{
						// yes - we do want mid-way stats, better add it.
						showThisState = true;
						legName = thisS.getTime().toString();
					}
				}

				// right then do we create a shape (series) for this one?
				if (showThisState)
				{
					// ok, we've got a new series
					XYSeries series = new XYSeries(legName, false);

					// get the shape
					Geometry geometry = loc.getGeometry();
					Coordinate[] boundary = geometry.getCoordinates();
					for (int i = 0; i < boundary.length; i++)
					{
						Coordinate coordinate = boundary[i];
						series.add(new XYDataItem(coordinate.y, coordinate.x));
					}
					_myData.addSeries(series);

					// TODO: Akash - we have to do some fancy JFreeChart to set the color
					// for this data series.
					// I think we may have to retrieve the series index, get the renderer,
					// then set the color
					// (or something like that)
				}
			}
		}
	}

	/**
	 * create a colour for this leg
	 * 
	 * @param legName
	 * @return
	 */
	private Color generateNewColor(String legName)
	{
		// TODO: Akash - generate color for this leg: maybe use hash of legName?
		// i.e. have list of 20 colours. then used hash code mod 20 to give
		// a number from 1 to 20, use that as the index?

		return Color.red;
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

	@Override
	public void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps)
	{
		if (_debugMode.isChecked())
			showData(boundsManager.getSpace().states());
	}

	@Override
	public void setShowAllBounds(boolean onOff)
	{
		_showAllBounds = onOff;

		// clear the UI
		clear(null);

		// and replot
		if (_lastStates != null)
			showData(_lastStates);
	}

	@Override
	public void setShowLegEndBounds(boolean onOff)
	{
		_showLegEndBounds = onOff;

		// clear the UI
		clear(null);

		// and replot
		if (_lastStates != null)
			showData(_lastStates);
	}

	@Override
	public void setShowPoints(boolean onOff)
	{
		_showPoints = onOff;
	}

	@Override
	public void setShowAchievablePoints(boolean onOff)
	{
		_showAchievablePoints = onOff;
	}

	@Override
	public void setShowRoutes(boolean onOff)
	{
		_showRoutes = onOff;
	}

	@Override
	public void setShowRoutesWithScores(boolean onOff)
	{
		_showRoutesWithScores = onOff;
	}

	@Override
	public void solutionsReady(CompositeRoute[] routes)
	{
		// TODO: IAN - HIGH process this
	}

	@Override
	public void legsGenerated(ArrayList<CoreLeg> theLegs)
	{
		// hey, are we showing points?
		if (_showPoints || _showAchievablePoints)
		{
			Collection<Point> res = new ArrayList<Point>();

			// ok, loop trough
			for (Iterator<CoreLeg> iterator = theLegs.iterator(); iterator.hasNext();)
			{
				CoreLeg coreLeg = (CoreLeg) iterator.next();

				// ok, get the points
				CoreRoute[][] routes = coreLeg.getRoutes();

				// go through the start points
				int numStart = routes.length;
				int numEnd = routes[0].length;

				// sort out the start points first
				for (int i = 0; i < numStart; i++)
				{
					CoreRoute[] thisStart = routes[i];

					// ok, are we showing all?
					Point startPoint = thisStart[0].getStartPoint();
					if (_showPoints)
					{
						// ok, just add it to the list
						res.add(startPoint);
					}
					else
					{
						if (_showAchievablePoints)
						{
							boolean isPossible = true;

							for (int j = 0; j < numEnd; j++)
							{
								CoreRoute thisRoute = thisStart[j];

								if (!thisRoute.isPossible())
								{
									isPossible = false;
									break;
								}
							}

							// ok, add it to the list
							if (isPossible)
								res.add(startPoint);

						}
					}
				}
			}
		}
	}


	@Override
	public void legsScored(ArrayList<CoreLeg> theLegs)
	{
		// TODO: IAN - HIGH process this
	}

}