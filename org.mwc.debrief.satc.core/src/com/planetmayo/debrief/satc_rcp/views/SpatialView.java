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
package com.planetmayo.debrief.satc_rcp.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.AbstractGroupMarker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.generator.exceptions.GenerationException;
import com.planetmayo.debrief.satc.model.generator.impl.bf.IBruteForceSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.impl.bf.LegWithRoutes;
import com.planetmayo.debrief.satc.model.generator.impl.ga.IGASolutionsListener;
import com.planetmayo.debrief.satc.model.legs.AlteringRoute;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc.model.manager.ISolversManagerListener;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.planetmayo.debrief.satc_rcp.model.SpatialViewSettings;
import com.planetmayo.debrief.satc_rcp.model.SpatialViewSettings.SpatialSettingsListener;
import com.planetmayo.debrief.satc_rcp.ui.UIListener;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class SpatialView extends ViewPart implements IConstrainSpaceListener, 
				IGASolutionsListener, IBruteForceSolutionsListener
{
	private JFreeChart _chart;
	private ChartComposite _chartComposite;

	private XYPlot _plot;
	private XYLineAndShapeRenderer _renderer;	
	private XYSeriesCollection _myData;	
	
	private Action _debugMode;	
	private Action _resizeButton;
	private Action _showLegend;	
	private Action _saveCoursePlot;
	private Action _saveSpeedPlot;
	
	/**
	 * keep track of how many sets of series that we've plotted
	 * 
	 */
	int _numCycles = 0;
	
	/**
	 * how many routes to display
	 * 
	 */
	private int _numRoutes = Integer.MAX_VALUE;

	/**
	 * the last set of states we plotted
	 * 
	 */
	private Collection<BoundedState> _lastStates = null;
	private List<LegWithRoutes> _lastSetOfScoredLegs;
	private CompositeRoute[] _lastSetOfSolutions;
	private List<CompositeRoute> _currentTopRoutes;
	private HashMap<Integer, ArrayList<String>> _scoredRouteLabels = new HashMap<Integer, ArrayList<String>>();
	private List<State> _targetSolution;

	private SpatialViewSettings _settings;
	private ISolversManager _solversManager;	
	private ISolver _activeSolver;	

	final private SimpleDateFormat _legendDateFormat = new SimpleDateFormat("HH:mm:ss");

	private ISolversManagerListener solversManagerListener;
	private IConstrainSpaceListener constrainSpaceListener;
	private IGenerateSolutionsListener generateSolutionsListener;
	private SpatialSettingsListener spatialSettingsListener;	

	public void clear(String title)
	{
		_myData.removeAllSeries();

		if (title != null)
			_chart.setTitle(new TextTitle(title, new java.awt.Font("SansSerif",
					java.awt.Font.BOLD, 8)));
		else
			_chart.setTitle(title);
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
		_solversManager = SATC_Activator.getDefault().getService(ISolversManager.class, true);
		_settings = SATC_Activator.getDefault().getService(SpatialViewSettings.class, true);		
		
		// get the data ready
		_myData = new XYSeriesCollection();

		JFreeChart chart = createChart(_myData);
		_chartComposite = new ChartComposite(parent, SWT.NONE, chart, true);

		makeActions();

		IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(_saveSpeedPlot);
		bars.getToolBarManager().add(_saveCoursePlot);
		bars.getToolBarManager().add(new Separator());
		bars.getToolBarManager().add(_showLegend);
		bars.getToolBarManager().add(_debugMode);
		bars.getToolBarManager().add(_resizeButton);
		

		bars.getMenuManager().add(new AbstractGroupMarker("num")
		{
		});
		bars.getMenuManager().appendToGroup("num", new RouteNumSelector(10));
		bars.getMenuManager().appendToGroup("num", new RouteNumSelector(50));
		bars.getMenuManager().appendToGroup("num", new RouteNumSelector(100));
		bars.getMenuManager().appendToGroup("num", new RouteNumSelector());

		// add some handlers to sort out how many routes to shw
		initListener(parent.getDisplay());
		_solversManager.addSolversManagerListener(solversManagerListener);
		_settings.addListener(spatialSettingsListener);
		setActiveSolver(_solversManager.getActiveSolver());
		
		_targetSolution = new TestSupport().loadSolutionTrack();
	}

	@Override
	public void dispose()
	{
		if (_activeSolver != null)
		{
			_activeSolver.getBoundsManager().removeConstrainSpaceListener(
					constrainSpaceListener);
			_activeSolver.getSolutionGenerator()
				.removeReadyListener(generateSolutionsListener);
		}
		_solversManager.removeSolverManagerListener(solversManagerListener);
		_settings.removeListener(spatialSettingsListener);
		super.dispose();
	}

	private void setNumRoutes(int _myNum)
	{
		_numRoutes = _myNum;

		redoChart();
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
		
		_saveSpeedPlot = new Action("Save speed plot", SWT.NONE)
		{

			@Override
			public void run()
			{
				if (_lastSetOfSolutions != null)
				{
					try 
					{
						savePlot(false);
					}
					catch (IOException ex) { }
				}
			}			
		};
		_saveCoursePlot = new Action("Save course plot", SWT.NONE)
		{

			@Override
			public void run()
			{
				if (_lastSetOfSolutions != null)
				{
					try 
					{
						savePlot(true);
					}
					catch (IOException ex) { }
				}
			}			
		};		
	}
	
	
	protected void savePlot(boolean course) throws IOException 
	{
		FileDialog dialog = new FileDialog(getViewSite().getShell(), SWT.SAVE);
		dialog.setFilterExtensions(new String[]	{ "*.txt" });
		String filename = dialog.open();
		if (filename == null) 
		{
			return;
		}
		
		CompositeRoute route = _lastSetOfSolutions[0];
		List<CoreRoute> parts = new ArrayList<CoreRoute>(route.getLegs());
		int i = 0;
		CoreRoute currentRoute = parts.get(0);
		
		PrintWriter writer = new PrintWriter(filename);
		DecimalFormat format = new DecimalFormat("0.0000");
		
		long startTime = parts.get(0).getStartTime().getTime();
		long endTime = parts.get(parts.size() - 1).getEndTime().getTime();
	
		for (long time = startTime; time < endTime; time += 1000) 
		{
			double t = (time - startTime) / 1000.0;
			Date currentDate = new Date(time);
			double value = course ? currentRoute.getCourse(currentDate) 
					: currentRoute.getSpeed(currentDate);
			while (value == -1) 
			{
				i++;
				currentRoute = parts.get(i);
				value = course ? currentRoute.getCourse(currentDate) 
						: currentRoute.getSpeed(currentDate);
			}
			writer.println(format.format(t) + "   " + format.format(value));
		}
		writer.close();		
	}

	@Override
	public void statesBounded(IBoundsManager boundsManager)
	{
		// we have to clear the other values when this happens, since
		// they're no longer valid
		_lastSetOfScoredLegs = null;
		_lastSetOfSolutions = null;

		_lastStates = _activeSolver.getProblemSpace().states();

		redoChart();
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
	
	private void initListener(Display display)
	{
		solversManagerListener = new ISolversManagerListener()
		{
			
			@Override
			public void solverCreated(ISolver solver)
			{
			}
			
			@Override
			public void activeSolverChanged(ISolver activeSolver)
			{
				setActiveSolver(activeSolver);
			}
		};
		solversManagerListener = UIListener.wrap(display, 
				ISolversManagerListener.class, solversManagerListener);
		constrainSpaceListener = UIListener.wrap(display,
				IConstrainSpaceListener.class, this);
		generateSolutionsListener = UIListener.wrap(display,
				new Class<?>[]
				{ IBruteForceSolutionsListener.class, IGASolutionsListener.class },
				this, new UIListener.MinimumDelay("iterationComputed", 150));
		spatialSettingsListener = new SpatialSettingsListener()
		{
			
			@Override
			public void onSettingsChanged()
			{
				redoChart();
			}
		};
		spatialSettingsListener = UIListener.wrap(display, SpatialSettingsListener.class, 
				spatialSettingsListener);
		
	}
	
	private void setActiveSolver(ISolver activeSolver) 
	{
		if (_activeSolver != null)
		{
			_activeSolver.getBoundsManager().removeConstrainSpaceListener(constrainSpaceListener);
			_activeSolver.getSolutionGenerator().removeReadyListener(generateSolutionsListener);			
		}
		_activeSolver = activeSolver;
		clear("");
		if (_activeSolver != null)
		{
			_activeSolver.getBoundsManager().addConstrainSpaceListener(constrainSpaceListener);
			_activeSolver.getSolutionGenerator().addReadyListener(generateSolutionsListener);
		}
	}

	private void showBoundedStates(Collection<BoundedState> newStates)
	{
		if (newStates.isEmpty())
		{
			return;
		}

		// just double-check that we're showing any states
		if (!_settings.isShowLegEndBounds() && !_settings.isShowAllBounds())
			return;

		String lastSeries = "UNSET";
		BoundedState lastState = null;
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

			// ok, color code the series
			String thisSeries = thisS.getMemberOf();

			if (loc != null)
			{
				boolean showThisState = false;

				// ok, what about the name?
				String legName = "";

				if (thisSeries != lastSeries)
				{
					// are we storing leg ends?
					if (_settings.isShowLegEndBounds() || (lastSeries == null))
					{
						showThisState = true;

						if (thisSeries != null)
							legName = thisSeries;
						else
							legName = "Turn " + turnCounter++;
					}

					colourCounter++;

					// and remember the new series
					lastSeries = thisSeries;

				}

				// are we adding this leg?
				// if (!showThisState)
				// {
				// no, but are we showing mid=leg states?
				if (_settings.isShowAllBounds())
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
					// right, but do we show it for the new, or old state
					if (thisS.getMemberOf() == null)
					{
						// right, we're already in a turn. use the last one
						if (lastState != null)
							loc = lastState.getLocation();
					}
					else
					{
						// right this is the first point in a new leg. use this one
						loc = thisS.getLocation();
					}

					// check we've found a shape
					if (loc != null)
					{
						// ok, we've got a new series
						XYSeries series = new XYSeries(legName, false);

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

//						final float dash1[] =
//						{ 10f, 10f };
//						final BasicStroke dashed = new BasicStroke(1.0f ,
//						 BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1,
//						 0.0f);
						final BasicStroke dashed = new BasicStroke(1f);

						_renderer.setSeriesStroke(seriesIndex, dashed);

						// _renderer.setSeriesStroke(seriesIndex, new BasicStroke());
						_renderer.setSeriesLinesVisible(seriesIndex, true);
						_renderer.setSeriesShapesVisible(seriesIndex, false);

					}
				}
			}

			// and move on
			lastState = thisS;
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

	private int plotTheseCoordsAsALine(String title, Coordinate[] coords)
	{
		int num = addSeries(title, coords);

		final float dash1[] =
		{ 10f, 10f };
		final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

		_renderer.setSeriesStroke(num, dashed);
		_renderer.setSeriesLinesVisible(num, true);
		_renderer.setSeriesShapesVisible(num, false);
		_renderer.setSeriesVisibleInLegend(num, false);

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

	private void plotTheseCoordsAsAPoints(String name,
			ArrayList<ArrayList<Point>> points, boolean largePoints)
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
			int num = addSeries(name + _numCycles++, coords.toArray(demo));

			listOfIndexes.add(num);

			_chart.setNotify(false);
			_renderer.setSeriesShapesVisible(num, true);
			_renderer.setSeriesLinesVisible(num, false);
			_renderer.setSeriesVisibleInLegend(num, false);

			int offset = largePoints ? -1 : 0;
			int size = largePoints ? 3 : 1;

			Shape triangle = new Rectangle(offset, offset, size, size);
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
			showBoundedStates(_activeSolver.getProblemSpace().states());
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
		if(_targetSolution != null)
			plotTargetSolution(_targetSolution);
		if (_currentTopRoutes != null) 
			plotCurrentTopRoutes(_currentTopRoutes);
	}
	
	private void plotCurrentTopRoutes(List<CompositeRoute> _currentRoutes) 
	{
		float width = 1.0f;
		for (CompositeRoute route : _currentRoutes)
		{
			Color currentColor = Color.BLACK;
			for (CoreRoute routePart : route.getLegs())
			{
				Point startP = routePart.getStartPoint();
				Point endP = routePart.getEndPoint();

				XYSeries series = new XYSeries("" + (_numCycles++), false);
				series.add(new XYDataItem(startP.getY(), startP.getX()));
				series.add(new XYDataItem(endP.getY(), endP.getX()));

				// get the shape
				_myData.addSeries(series);

				// get the series num
				int num = _myData.getSeriesCount() - 1;
				_renderer.setSeriesPaint(num, currentColor);
				_renderer.setSeriesStroke(num, new BasicStroke(width, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 1.0f, new float[] {4.f, width}, 0.0f), false);
				_renderer.setSeriesLinesVisible(num, true);
				_renderer.setSeriesShapesVisible(num, false);
			}
		}
	}

	private void plotTargetSolution(List<State> solution)
	{
		// ok, get the target solution data
		if(_settings.isShowTargetSolution())
		{
			Coordinate [] coords = new Coordinate[solution.size()];
			int ctr = 0;
			for (Iterator<State> iterator = solution.iterator(); iterator.hasNext();)
			{
				State thisState = (State) iterator.next();
				Coordinate coord = new Coordinate(thisState.getLocation().getY(), thisState.getLocation().getX());
				coords[ctr++] = coord;
			}
			plotTheseCoordsAsALine("Solution", coords, Color.ORANGE);
			
			// get the series num
			int num = _myData.getSeriesCount() - 1;
			_renderer.setSeriesStroke(num, new BasicStroke(3));
			
		}
	}

	private void showTopSolutions(CompositeRoute[] lastSetOfSolutions2)
	{
		// just draw in these solutions
		if (_settings.isShowRecommendedSolutions())
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
	public void solutionsReady(CompositeRoute[] routes)
	{
		LogFactory.getLog().info("Last error score: " + routes[0].getScore());
		_lastSetOfSolutions = routes;
		_currentTopRoutes = null;

		redoChart();
	}

	/**
	 * store the collection of a line with its name plus score value
	 * 
	 * @author Ian
	 * 
	 */
	private static class ScoredRoute
	{
		private final LineString theRoute;
		private final double theScore;
		@SuppressWarnings("unused")
		private final String theName;
		private CoreRoute rawRoute;

		public ScoredRoute(LineString route, String name, double score,
				CoreRoute rawRoute)
		{
			theRoute = route;
			theScore = score;
			theName = name;
			this.rawRoute = rawRoute;
		}
	}

	@Override
	public void legsScored(List<LegWithRoutes> theLegs)
	{
		// forget the solutions, they're no longer valid
		_lastSetOfSolutions = null;

		_lastSetOfScoredLegs = theLegs;

		redoChart();
	}

	private void showLegsWithScores(List<LegWithRoutes> theLegs)
	{
		_lastSetOfScoredLegs = theLegs;

		// hey, are we showing points?
		if (_settings.isShowPoints() || _settings.isShowAchievablePoints() || _settings.isShowRoutes()
				|| _settings.isShowRoutesWithScores())
		{
			ArrayList<ArrayList<Point>> allPoints = new ArrayList<ArrayList<Point>>();
			ArrayList<ArrayList<Point>> allPossiblePoints = new ArrayList<ArrayList<Point>>();
			ArrayList<ArrayList<LineString>> allPossibleRoutes = new ArrayList<ArrayList<LineString>>();
			HashMap<LegWithRoutes, ArrayList<ScoredRoute>> scoredRoutes = new HashMap<LegWithRoutes, ArrayList<ScoredRoute>>();

			// ok, loop trough
			for (Iterator<LegWithRoutes> iterator = theLegs.iterator(); iterator.hasNext();)
			{
				final LegWithRoutes thisLeg = iterator.next();

				// start off with the ponts
				if (_settings.isShowPoints() || _settings.isShowAchievablePoints())
				{
					ArrayList<Point> points = new ArrayList<Point>();
					ArrayList<Point> possiblePoints = new ArrayList<Point>();

					// ok, we need to look at all of the routes to sort out which points
					// are achievable					
					CoreRoute[][] routes = thisLeg.getRoutes();

					// go through the start points
					if (routes != null)
					{
						int numStart = routes.length;
						int numEnd = routes[0].length;

						// sort out the start points first
						for (int i = 0; i < numStart; i++)
						{
							CoreRoute[] thisStart = routes[i];
							
							// ok, are we showing all?
							CoreRoute firstRoute = findFirstValidRoute(thisStart);
							if (firstRoute != null)
							{
								Point startPoint = firstRoute.getStartPoint();
								if (_settings.isShowPoints())
								{
									// ok, just add it to the list
									points.add(startPoint);
								}
								// ok - do we need to check which ones have any valid points?
								if (_settings.isShowAchievablePoints())
								{
									boolean isPossible = false;

									for (int j = 0; j < numEnd; j++)
									{
										CoreRoute thisRoute = thisStart[j];

										if (thisRoute != null)
										{
											if (thisRoute.isPossible())
											{
												isPossible = true;
												break;
											}
										}
									}
									// ok, add it to the list
									if (isPossible)
									{
										possiblePoints.add(startPoint);
									}
								}
							}
						}
					}
					allPoints.add(points);
					allPossiblePoints.add(possiblePoints);

				}

				// and now for the routes
				if (_settings.isShowRoutes() || _settings.isShowRoutesWithScores())
				{

					ArrayList<LineString> possibleRoutes = new ArrayList<LineString>();

					// we're only currently going to draw lines for straight legs
					if (thisLeg.getLeg().getType() == LegType.STRAIGHT)
					{
						int routeCounter = 0;
						for (CoreRoute[] routes : thisLeg.getRoutes()) 
						{
							if (routeCounter > _numRoutes)
								break;							
							for (CoreRoute route : routes) 
							{
								// only bother if it's an actual route
								if(route == null)
									continue;
								
								// only display the route if it's achievable
								if(!route.isPossible())
									continue;
								
								routeCounter++;
								if (routeCounter > _numRoutes)
									break;
								
								Coordinate[] coords = new Coordinate[]
								{ route.getStartPoint().getCoordinate(),
										route.getEndPoint().getCoordinate() };
								LineString newR = GeoSupport.getFactory().createLineString(
										coords);

								if (_settings.isShowRoutes())
									possibleRoutes.add(newR);
								
								if (_settings.isShowRoutesWithScores())
								{
									// do we have a collection for this leg
									ArrayList<ScoredRoute> thisLegRes = scoredRoutes.get(thisLeg);
									if (thisLegRes == null)
									{
										// nope, better create one then
										thisLegRes = new ArrayList<ScoredRoute>();
										scoredRoutes.put(thisLeg, thisLegRes);
									}

									thisLegRes.add(new ScoredRoute(newR, route.getName(),
											route.getScore(), route));

								}
							}
						}
					}
					allPossibleRoutes.add(possibleRoutes);

				}
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

	private CoreRoute findFirstValidRoute(CoreRoute[] thisStart)
	{
		for (int i = 0; i < thisStart.length; i++)
		{
			CoreRoute coreRoute = thisStart[i];
			if (coreRoute != null)
				return coreRoute;
		}
		return null;
	}

	private void plotRoutesWithScores(
			HashMap<LegWithRoutes, ArrayList<ScoredRoute>> legRoutes)
	{
		if (legRoutes.size() == 0)
			return;

		// we need to store the point labels. get ready to store them
		_scoredRouteLabels.clear();
		final DateFormat labelTimeFormat = new SimpleDateFormat("mm:ss");

		// work through the legs
		Iterator<LegWithRoutes> lIter = legRoutes.keySet().iterator();
		while (lIter.hasNext())
		{
			final LegWithRoutes thisL = lIter.next();
			final ArrayList<ScoredRoute> scoredRoutes = legRoutes.get(thisL);

			double max = 0, min = Double.MAX_VALUE;
			for (Iterator<ScoredRoute> iterator = scoredRoutes.iterator(); iterator
					.hasNext();)
			{
				ScoredRoute route = iterator.next();
				// Ensure thisScore is between 0-100
				double thisScore = route.theScore;

				thisScore = Math.log(thisScore);

				if (max < thisScore)
				{
					max = thisScore;
				}
				if (min > thisScore)
				{
					min = thisScore;
				}
			}

			System.out.println(" for leg: " + thisL.getClass().getName() + " min:" + min
					+ " max:" + max);

			for (Iterator<ScoredRoute> iterator = scoredRoutes.iterator(); iterator
					.hasNext();)
			{
				ScoredRoute route = iterator.next();

				Point startP = route.theRoute.getStartPoint();
				Point endP = route.theRoute.getEndPoint();

				// Ensure thisScore is between 0-100
				double thisScore = route.theScore;
				thisScore = Math.log(thisScore);

				double thisColorScore = (thisScore - min) / (max - min);

				// System.out.println("this s:" + (int) thisScore + " was:"
				// + route.theScore);

				XYSeries series = new XYSeries("" + (_numCycles++), false);
				series.add(new XYDataItem(startP.getY(), startP.getX()));
				series.add(new XYDataItem(endP.getY(), endP.getX()));

				// get the shape
				_myData.addSeries(series);

				// get the series num
				int num = _myData.getSeriesCount() - 1;
				_renderer.setSeriesPaint(num, getHeatMapColorFor(thisColorScore));

				// make the line width inversely proportional to the score, with a max
				// width of 2 pixels
				final float width = (float) (2f - 2 * thisColorScore);

				// make the top score solid, and worse scores increasingly sparse
				final float dash[];
				if (thisScore == min)
				{
					dash = null;
				}
				else
				{
					float thisWid = (float) (1f + Math.exp(thisScore - min) / 3);
					float[] tmpDash =
					{ 4, thisWid };
					dash = tmpDash;
				}

				// and put this line thickness, dashing into a stroke object
				BasicStroke stroke = new BasicStroke(width, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 1.0f, dash, 0.0f);

				_renderer.setSeriesStroke(num, stroke, false);
				_renderer.setSeriesLinesVisible(num, true);
				_renderer.setSeriesShapesVisible(num, false);
				_renderer.setSeriesVisibleInLegend(num, false);

				// ok, we'll also show the route points
				XYSeries series2 = new XYSeries("" + (_numCycles++), false);

				ArrayList<String> theseLabels = new ArrayList<String>();

				// loop through the points
				Iterator<State> stIter = route.rawRoute.getStates().iterator();
				while (stIter.hasNext())
				{
					State state = (State) stIter.next();
					Point loc = state.getLocation();
					XYDataItem newPt = new XYDataItem(loc.getY(), loc.getX());
					series2.add(newPt);
					// and store the label for this point
					theseLabels.add(labelTimeFormat.format(state.getTime()));
				}

				// get the shape
				_myData.addSeries(series2);
				//
				// // get the series num
				num = _myData.getSeriesCount() - 1;

				// ok, we now need to put hte series into the right slot
				_scoredRouteLabels.put(num, theseLabels);

				if (_settings.isShowRoutePointLabels())
				{
					_renderer.setSeriesItemLabelGenerator(num, new XYItemLabelGenerator()
					{

						@Override
						public String generateLabel(XYDataset arg0, int arg1, int arg2)
						{
							String res = null;
							ArrayList<String> thisList = _scoredRouteLabels.get(arg1);
							if (thisList != null)
							{
								res = thisList.get(arg2);
							}
							return res;
						}
					});
					_renderer.setSeriesItemLabelPaint(num,
							getHeatMapColorFor(thisColorScore));
				}

				// _renderer.setSeriesPaint(num, getHeatMapColorFor(thisColorScore));
				_renderer.setSeriesItemLabelsVisible(num, _settings.isShowRoutePointLabels());
				_renderer.setSeriesLinesVisible(num, false);
				_renderer.setSeriesPaint(num, getHeatMapColorFor(thisColorScore));
				_renderer.setSeriesShapesVisible(num, _settings.isShowRoutePoints());
				_renderer.setSeriesVisibleInLegend(num, false);
			}
		}
	}

	private void plotTopRoutes(Collection<CoreRoute> scoredRoutes)
	{
		XYSeries series = new XYSeries("" + (_numCycles++), false);
		for (Iterator<CoreRoute> iterator = scoredRoutes.iterator(); iterator
				.hasNext();)
		{
			CoreRoute route = iterator.next();

			if (route.getType() == LegType.STRAIGHT)
			{
				Point startP = route.getStartPoint();
				Point endP = route.getEndPoint();			
				series.add(new XYDataItem(startP.getY(), startP.getX()));
				series.add(new XYDataItem(endP.getY(), endP.getX()));
			}
			else
			{
				AlteringRoute altering = (AlteringRoute) route;
				Point start = route.getStartPoint();
				Point end = route.getEndPoint();
				Point[] controls = altering.getBezierControlPoints();
				for (double t = 0; t <= 1; t += 0.05) 
				{
					Point p = MathUtils.calculateBezier(t, start, end, controls);
					series.add(new XYDataItem(p.getY(), p.getX()));
				}
			}
		}
		// get the shape
		_myData.addSeries(series);

		// get the series num
		int num = _myData.getSeriesCount() - 1;
		_renderer.setSeriesPaint(num, Color.black);
		_renderer.setSeriesStroke(num, new BasicStroke(5), false);
		_renderer.setSeriesLinesVisible(num, true);
		_renderer.setSeriesShapesVisible(num, false);
	}

	/*
	 * produce a heat map color score for this value
	 * 
	 * @param thisScore value between 0 & 1
	 */
	private Color getHeatMapColorFor(double thisScore)
	{
		
		final float range = 0.8f;
		final float offset = 0.2f;

		float red = (float) (1f - 0.8 * thisScore);
		float green = (float) (thisScore * 0.7);
		float blue = (float) (offset + range * thisScore);

		return new Color(red, green, blue);
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

	@Override
	public void startingGeneration()
	{
		// don't worry about it.
	}

	@Override
	public void finishedGeneration(Throwable error)
	{
		if (error instanceof GenerationException) 
		{
			GenerationException ex = (GenerationException) error;
			MessageBox messageBox = new MessageBox(_chartComposite.getShell(), SWT.ICON_WARNING | SWT.OK);
			messageBox.setMessage(ex.getMessage());
			messageBox.setText("Error during generation");
			messageBox.open();
		}
	}
	
	@Override
	public void iterationComputed(List<CompositeRoute> topRoutes, double topScore)
	{
		_currentTopRoutes = null;
		if (_settings.isShowIntermediateGASolutions())
		{
			_currentTopRoutes = topRoutes;
		
			redoChart();
		}
	}

	/**
	 * utility class to allow us to specify how many routes to be displayed
	 * 
	 * @author Ian
	 * 
	 */
	private class RouteNumSelector extends Action
	{

		private int _myNum;

		private RouteNumSelector(int num)
		{
			super(num + " points");
			_myNum = num;
		}

		protected RouteNumSelector()
		{
			super("All points");
			_myNum = Integer.MAX_VALUE;
		}

		@Override
		public void run()
		{
			setNumRoutes(_myNum);
		}

	}
}