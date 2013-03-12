package com.planetmayo.debrief.satc_rcp.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import org.jfree.util.ShapeUtilities;

import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager.IShowBoundProblemSpaceDiagnostics;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager.IShowGenerateSolutionsDiagnostics;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class SpatialView extends ViewPart implements IConstrainSpaceListener,
		GeoSupport.GeoPlotter, IShowBoundProblemSpaceDiagnostics,
		IShowGenerateSolutionsDiagnostics, IGenerateSolutionsListener
{
	private static JFreeChart _chart;
	private static ChartComposite _chartComposite;

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

	/**
	 * level of diagnostics for user
	 * 
	 * @see IBoundsManager.IShowBoundProblemSpaceDiagnostics
	 */
	private boolean _showLegEndBounds;

	/**
	 * level of diagnostics for user
	 * 
	 * @see IBoundsManager.IShowBoundProblemSpaceDiagnostics
	 */
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
	 * level of diagnostics for user
	 * 
	 * @see IBoundsManager.IShowGenerateSolutionsDiagnostics
	 */
	private boolean _showRecommendedSolutions;

	/**
	 * the last set of states we plotted
	 * 
	 */
	private Collection<BoundedState> _lastStates = null;

	private ISolutionGenerator solutionGenerator;

	private ArrayList<CoreLeg> _lastSetOfScoredLegs;

	private CompositeRoute[] _lastSetOfSolutions;

	final private SimpleDateFormat _legendDateFormat = new SimpleDateFormat(
			"hh:mm:ss");

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
		showBoundedStates(_lastStates);
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
		_chartComposite = new ChartComposite(parent, SWT.NONE, chart, true);

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
				_chartComposite.restoreAutoBounds();
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

	private void showBoundedStates(Collection<BoundedState> newStates)
	{
		if (newStates.isEmpty())
		{
			return;
		}

		// just double-check that we're showing any states
		if (!_showLegEndBounds && !_showAllBounds)
			return;

		String lastSeries = "UNSET";
		int turnCounter = 1;
		int colourCounter = 0;

		@SuppressWarnings("rawtypes")
		HashMap<Comparable, Integer> keyToLegTypeMapping = new HashMap<Comparable, Integer>();

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
				String legName = "";

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

					colourCounter++;

					// ok, use new color

					// DONE: generate a new color. We should prob allow up to 20 colors, I
					// welcome
					// a strategy for generateNewColor()

					// and remember the new series
					lastSeries = thisSeries;

				}

				// are we adding this leg?
				// if (!showThisState)
				// {
				// no, but are we showing mid=leg states?
				if (_showAllBounds)
				{
					// yes - we do want mid-way stats, better add it.
					showThisState = true;
					if (legName.length() > 0)
						legName = "(" + legName + ") ";
					legName += _legendDateFormat.format(thisS.getTime());
				}
				// }

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

					keyToLegTypeMapping.put(series.getKey(), colourCounter);

					_myData.addSeries(series);

					int seriesIndex = _myData.getSeriesCount() - 1;

					_renderer.setSeriesShapesVisible(seriesIndex, false);
					_renderer.setSeriesLinesVisible(seriesIndex, true);

					_renderer.setSeriesStroke(seriesIndex, new BasicStroke());

				}
			}
		}

		Color[] colorsList = getDifferentColors(colourCounter);

		// paint each series with color depending on leg type.
		for (Comparable<?> key : keyToLegTypeMapping.keySet())
		{
			_renderer.setSeriesPaint(_myData.getSeriesIndex(key),
					colorsList[keyToLegTypeMapping.get(key) - 1]);

		}
	}

	public static Color[] getDifferentColors(int n)
	{
		Color[] cols = new Color[n];
		for (int i = 0; i < n; i++)
			cols[i] = Color.getHSBColor((float) (n - i) / n, 1, 1);
		// return cols;
		return randomizeArray(cols);
	}

	public static Color[] randomizeArray(Color[] array)
	{
		for (int i = array.length - 1; i > array.length / 2; i--)
		{
			Color temp = array[i];
			array[i] = array[array.length - 1 - i];
			array[array.length - 1 - i] = temp;
		}
		return array;
	}

	@Override
	public void showGeometry(String title, Coordinate[] coords)
	{

		// are we in debug mode?
		if (!_debugMode.isChecked())
			return;

		plotTheseCoordsAsALine(title, coords);

	}

	private int plotTheseCoordsAsALine(String title, Coordinate[] coords)
	{
		int num = addSeries(title, coords);

		_renderer.setSeriesStroke(num, new BasicStroke(0.0f));

		return num;
	}

	private void plotTheseCoordsAsALine(String title, Coordinate[] coords,
			Color color)
	{
		int index = plotTheseCoordsAsALine(title, coords);
		_renderer.setSeriesPaint(index, color);
		
		_renderer.setSeriesShapesVisible(index, false);
		_renderer.setSeriesLinesVisible(index, true);

	}

	private void plotTheseCoordsAsAPoints(String name, ArrayList<ArrayList<Point>> points,
			boolean largePoints)
	{
		List<Integer> listOfIndexes = new ArrayList<Integer>();

		for (ArrayList<Point> pointsList : points)
		{
			Collection<Coordinate> coords = new ArrayList<Coordinate>();

			for (Point point : pointsList)
			{
				coords.add(point.getCoordinate());
			}
			Coordinate[] demo = new Coordinate[]
			{};

			// create the data series, get the index number
			int num = addSeries(name	 + _numCycles++, coords.toArray(demo));

			listOfIndexes.add(num);

			_chart.setNotify(false);
			_renderer.setSeriesShapesVisible(num, true);
			_renderer.setSeriesLinesVisible(num, false);

			Shape triangle = ShapeUtilities.createRegularCross(largePoints ? 5 : 2,
					largePoints ? 5 : 2);
			_renderer.setSeriesShape(num, triangle);

			_chart.setNotify(true);

		}

		Color[] colorsList = getDifferentColors(listOfIndexes.size());

		for (int i = 0; i < listOfIndexes.size(); i++)
		{
			_renderer.setSeriesPaint(listOfIndexes.get(i), colorsList[i]);
		}

	}

	private int addSeries(String title, Coordinate[] coords)
	{
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
		return num - 1;
	}

	@Override
	public void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps)
	{
		if (_debugMode.isChecked())
			showBoundedStates(boundsManager.getSpace().states());
	}

	@Override
	public void setShowAllBounds(boolean onOff)
	{
		_showAllBounds = onOff;

		redoChart();
	}

	@Override
	public void setShowLegEndBounds(boolean onOff)
	{
		_showLegEndBounds = onOff;

		redoChart();
	}

	@Override
	public void setShowRecommendedSolutions(boolean onOff)
	{
		_showRecommendedSolutions = onOff;

		redoChart();
	}

	private void redoChart()
	{
		// clear the UI
		clear(null);

		// and replot
		if (_lastStates != null)
			showBoundedStates(_lastStates);
		if (_lastSetOfScoredLegs != null)
			showLegsWithScores(_lastSetOfScoredLegs);
		if (_lastSetOfSolutions != null)
			showTopSolutions(_lastSetOfSolutions);
	}

	private void showTopSolutions(CompositeRoute[] lastSetOfSolutions2)
	{
		// just draw in these solutions
		if (_showRecommendedSolutions)
		{
			for (int i = 0; i < lastSetOfSolutions2.length; i++)
			{
				CompositeRoute compositeRoute = lastSetOfSolutions2[i];
				// ok, loop through the legs
				Collection<CoreRoute> legs = compositeRoute.getLegs();
				plotTopRoutes(legs);
			}
		}
	}

	@Override
	public void setShowPoints(boolean onOff)
	{
		_showPoints = onOff;

		redoChart();
	}

	@Override
	public void setShowAchievablePoints(boolean onOff)
	{
		_showAchievablePoints = onOff;
		redoChart();
	}

	@Override
	public void setShowRoutes(boolean onOff)
	{
		_showRoutes = onOff;
		redoChart();
	}

	@Override
	public void setShowRoutesWithScores(boolean onOff)
	{
		_showRoutesWithScores = onOff;
		redoChart();
	}

	@Override
	public void solutionsReady(CompositeRoute[] routes)
	{
		_lastSetOfSolutions = routes;

		redoChart();
	}

	private static class ScoredRoute
	{
		private LineString theRoute;
		private double theScore;

		public ScoredRoute(LineString route, double score)
		{
			theRoute = route;
			theScore = score;
		}
	}

	@Override
	public void legsScored(ArrayList<CoreLeg> theLegs)
	{
		_lastSetOfScoredLegs = theLegs;

		redoChart();
	}

	private void showLegsWithScores(ArrayList<CoreLeg> theLegs)
	{
		_lastSetOfScoredLegs = theLegs;

		// hey, are we showing points?
		if (_showPoints || _showAchievablePoints || _showRoutes
				|| _showRoutesWithScores)
		{
			ArrayList<ArrayList<Point>> allPoints = new ArrayList<ArrayList<Point>>();
			ArrayList<ArrayList<Point>> allPossiblePoints = new ArrayList<ArrayList<Point>>();
			ArrayList<ArrayList<LineString>> allPossibleRoutes = new ArrayList<ArrayList<LineString>>();
			Collection<ScoredRoute> scoredRoutes = new ArrayList<ScoredRoute>();

			// ok, loop trough
			for (Iterator<CoreLeg> iterator = theLegs.iterator(); iterator.hasNext();)
			{
				ArrayList<Point> points = new ArrayList<Point>();
				ArrayList<Point> possiblePoints = new ArrayList<Point>();
				ArrayList<LineString> possibleRoutes = new ArrayList<LineString>();
				CoreLeg thisLeg = iterator.next();

				// ok, get the points
				CoreRoute[][] routes = thisLeg.getRoutes();

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
						points.add(startPoint);
					}

					// ok - do we need to check which ones have any valid points?
					if (_showAchievablePoints || _showRoutes || _showRoutesWithScores)
					{
						boolean isPossible = false;

						for (int j = 0; j < numEnd; j++)
						{
							CoreRoute thisRoute = thisStart[j];

							if (thisRoute.isPossible())
							{
								isPossible = true;

								// we're only currently going to draw lines for straight legs
								if (thisLeg.getType() == LegType.STRAIGHT)
								{
									if (_showRoutes || _showRoutesWithScores)
									{
										Coordinate[] coords = new Coordinate[]
										{ thisRoute.getStartPoint().getCoordinate(),
												thisRoute.getEndPoing().getCoordinate() };
										LineString newR = GeoSupport.getFactory().createLineString(
												coords);

										if (_showRoutes)
											possibleRoutes.add(newR);

										if (_showRoutesWithScores)
											scoredRoutes.add(new ScoredRoute(newR, thisRoute
													.getScore()));
									}
								}
							}
						}

						// ok, add it to the list
						if (isPossible)
						{

							if (_showAchievablePoints)
								possiblePoints.add(startPoint);

						}
					}

				}
				allPoints.add(points);
				allPossiblePoints.add(possiblePoints);
				allPossibleRoutes.add(possibleRoutes);
			}

			// System.out.println("num all points:" + allPoints.size());
			// System.out.println("num achievable points:" +
			// allPossiblePoints.size());

			plotTheseCoordsAsAPoints("all_", allPoints, false);
			plotTheseCoordsAsAPoints("poss_", allPossiblePoints, true);
			plotPossibleRoutes(allPossibleRoutes);
			plotRoutesWithScores(scoredRoutes);

		}

	}

	private void plotRoutesWithScores(Collection<ScoredRoute> scoredRoutes)
	{
		double max = 0, min = Double.MAX_VALUE;
		for (Iterator<ScoredRoute> iterator = scoredRoutes.iterator(); iterator
				.hasNext();)
		{
			ScoredRoute route = iterator.next();
			// Ensure thisScore is between 0-100
			double thisScore = route.theScore;
			if (max < thisScore)
			{
				max = thisScore;
			}
			if (min > thisScore)
			{
				min = thisScore;
			}

		}

		System.out.println("min:" + (int) min + " max:" + (int) max);

		for (Iterator<ScoredRoute> iterator = scoredRoutes.iterator(); iterator
				.hasNext();)
		{
			ScoredRoute route = iterator.next();

			Point startP = route.theRoute.getStartPoint();
			Point endP = route.theRoute.getEndPoint();

			// Ensure thisScore is between 0-100
			double thisScore = (route.theScore - min) / (max - min) * 100;

			XYSeries series = new XYSeries("" + (_numCycles++), false);
			series.add(new XYDataItem(startP.getY(), startP.getX()));
			series.add(new XYDataItem(endP.getY(), endP.getX()));

			// get the shape
			_myData.addSeries(series);

			// get the series num
			int num = _myData.getSeriesCount() - 1;
			_renderer.setSeriesPaint(num, getHeatMapColorFor(thisScore));
			_renderer.setSeriesStroke(num, new BasicStroke(), false);

		}

	}

	private void plotTopRoutes(Collection<CoreRoute> scoredRoutes)
	{
		for (Iterator<CoreRoute> iterator = scoredRoutes.iterator(); iterator
				.hasNext();)
		{
			CoreRoute route = iterator.next();

			Point startP = route.getStartPoint();
			Point endP = route.getEndPoing();

			XYSeries series = new XYSeries("" + (_numCycles++), false);
			series.add(new XYDataItem(startP.getY(), startP.getX()));
			series.add(new XYDataItem(endP.getY(), endP.getX()));

			// get the shape
			_myData.addSeries(series);

			// get the series num
			int num = _myData.getSeriesCount() - 1;
			_renderer.setSeriesPaint(num, Color.green);
			_renderer.setSeriesStroke(num, new BasicStroke(3), false);
		}

	}

	/*
	 * Ensure thisScore is between 0-100
	 */
	private Color getHeatMapColorFor(double thisScore)
	{
		// put the score into the 50-100 domain, to make it more feint
		thisScore = 50 + thisScore / 2;

		float red = (float) (thisScore / 100);
		float blue = (float) ((100 - thisScore) / 100);

		return new Color(red, 0, blue);
	}

	private void plotPossibleRoutes(
			ArrayList<ArrayList<LineString>> allPossibleRoutes)
	{

		Color[] list = getDifferentColors(allPossibleRoutes.size());

		for (int i = 0; i < allPossibleRoutes.size(); i++)
		{
			for (LineString lineString : allPossibleRoutes.get(i))
			{
				plotTheseCoordsAsALine("" + (_numCycles++),
						lineString.getCoordinates(), list[i]);
			}

		}

	}
}