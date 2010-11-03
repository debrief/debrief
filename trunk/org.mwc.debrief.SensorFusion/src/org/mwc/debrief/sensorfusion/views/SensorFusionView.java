package org.mwc.debrief.sensorfusion.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.InputEvent;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.preferences.SelectionHelper;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.sensorfusion.views.DataSupport.SensorSeries;
import org.mwc.debrief.sensorfusion.views.DataSupport.TacticalSeries;

import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.WatchableList;

public class SensorFusionView extends ViewPart implements ISelectionProvider
{

	private static final String CHART_NAME = "Bearing data";

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.mwc.debrief.SensorFusion";

	private Action resetData;
	private Action clearPlot;

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor = null;

	protected TrackManager _trackData;

	private TrackWrapper _primary;

	private JFreeChart _myChart;

	private ChartComposite _myChartFrame;

	private Action showSymbols;

	private XYLineAndShapeRenderer _plotRenderer;

	private Vector<SensorSeries> _selectedTracks;

	private Action mergeTracks;

	private TimeSeriesCollection _currentData;

	private Vector<ISelectionChangedListener> _selectionListeners;

	/**
	 * helper - handle the selection a little better
	 */
	private SelectionHelper _selectionHelper;

	/**
	 * The constructor.
	 */
	public SensorFusionView()
	{

		_selectedTracks = new Vector<SensorSeries>(0, 1);
	}

	protected void setupListeners()
	{
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						TrackManager provider = (TrackManager) part;

						// is this different to our current one?
						if (provider != _trackData)
							storeDetails(provider, parentPart);
					}
				});

		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						if (part == _trackData)
						{
							_trackData = null;
							clearUI();
						}
					}
				});
	}

	protected void clearUI()
	{
		// and clear the plot
	}

	protected void storeDetails(TrackManager provider, IWorkbenchPart parentPart)
	{
		// ok, we've got a new plot to watch. better watch it...
		_trackData = provider;

		// which is the primary?
		WatchableList primary = provider.getPrimaryTrack();

		// check it's a track
		if (!(primary instanceof TrackWrapper))
		{
			CorePlugin.logError(Status.WARNING,
					"Primary track not suitable for watching", null);
		}
		else
		{
			_primary = (TrackWrapper) primary;
		}

		// and which are the secondaries?
		WatchableList[] secondaries = provider.getSecondaryTracks();

		// sort out the bearing tracks

		// and now the sensor data

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		setupListeners();

		// and the selection provider bits
		_selectionHelper = new SelectionHelper();
		getSite().setSelectionProvider(_selectionHelper);

		parent.setLayout(new FillLayout());

		// ok, let's mockup the UI
		_myChart = DataSupport.createChart(null);

		// resetData();

		_myChartFrame = new ChartComposite(parent, SWT.NONE, _myChart, true);
		_myChartFrame.setDisplayToolTips(true);
		_myChartFrame.setHorizontalAxisTrace(false);
		_myChartFrame.setVerticalAxisTrace(false);
		_myChart.setTitle(CHART_NAME);

		_myChartFrame.addChartMouseListener(new ChartMouseListener()
		{

			public void chartMouseClicked(ChartMouseEvent event)
			{
				ChartEntity entity = event.getEntity();
				if (entity instanceof XYItemEntity)
				{
					XYItemEntity xyi = (XYItemEntity) entity;
					TimeSeriesCollection coll = (TimeSeriesCollection) xyi.getDataset();
					TacticalSeries ts = (TacticalSeries) coll
							.getSeries(((XYItemEntity) entity).getSeriesIndex());
					if (ts instanceof SensorSeries)
					{
						SensorSeries ss = (SensorSeries) ts;

						// right, is ctrl-key pressed
						int mods = event.getTrigger().getModifiers();
						if ((mods & InputEvent.CTRL_MASK) != 2)
						{
							_selectedTracks.removeAllElements();

							_selectedTracks.add(ss);
						}
						else
						{
							if (_selectedTracks.contains(ts))
								_selectedTracks.remove(ts);
							else
								_selectedTracks.add(ss);

						}

						// and update the UI
						updatedSelection();

						// ok, we need to redraw
						redrawPlot();
					}
				}
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent event)
			{
			}
		});

	}

	protected void updatedSelection()
	{
		//
		mergeTracks.setEnabled(_selectedTracks.size() > 1);

		// and provide the selection object
		StructuredSelection trackSelection = new StructuredSelection(
				_selectedTracks);
		setSelection(trackSelection);
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				SensorFusionView.this.fillContextMenu(manager);
			}
		});
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(resetData);
		manager.add(new Separator());
		manager.add(clearPlot);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(resetData);
		manager.add(clearPlot);
		manager.add(showSymbols);
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(resetData);
		manager.add(clearPlot);
		manager.add(showSymbols);
		manager.add(mergeTracks);
	}

	private void makeActions()
	{
		resetData = new Action()
		{
			public void run()
			{
				_myChart.setTitle(CHART_NAME + "(Loading)");
				Runnable doIt = new Runnable()
				{

					@Override
					public void run()
					{
						resetData();
			//			_myChart.setTitle(CHART_NAME);
					}
				};
				Display.getCurrent().asyncExec(doIt);
			}
		};
		resetData.setText("Reset data");

		clearPlot = new Action()
		{
			public void run()
			{
				Runnable doIt = new Runnable()
				{

					@Override
					public void run()
					{
						resetPlot();
					}
				};
				Display.getCurrent().asyncExec(doIt);

			}
		};
		clearPlot.setText("clearData");
		showSymbols = new Action("Show symbols", SWT.TOGGLE)
		{

			@Override
			public void run()
			{
				redrawPlot();
			}
		};
		mergeTracks = new Action("Merge tracks")
		{

			@Override
			public void run()
			{
				// combine the currently selected tracks
				mergeSelectedTracks();
			}
		};
		mergeTracks.setEnabled(false);
	}

	protected void mergeSelectedTracks()
	{
		// combine the selecion
		SensorSeries newSeries = new SensorSeries(_selectedTracks.firstElement()
				.toString()
				+ "(Merged)", "Sensor");
		Iterator<SensorSeries> iter = _selectedTracks.iterator();
		while (iter.hasNext())
		{
			SensorSeries thisS = iter.next();
			newSeries.addAndOrUpdate(thisS);
		}

		// ok, remove the selection
		Iterator<SensorSeries> iter3 = _selectedTracks.iterator();
		while (iter3.hasNext())
		{
			_currentData.removeSeries(iter3.next());
		}

		// clear our list
		_selectedTracks.removeAllElements();

		// store our new one
		_selectedTracks.add(newSeries);

		// replace the selection
		_currentData.addSeries(newSeries);

		// and force a redraw
		// redrawPlot();
	}

	protected void redrawPlot()
	{
		if (_plotRenderer != null)
		{
			_plotRenderer.setSeriesShapesVisible(0, true);
		}
	}

	protected void resetPlot()
	{
		_myChart.getXYPlot().setDataset(null);
	}

	protected void resetData()
	{
		_currentData = DataSupport.createDataset();
		_myChart.getXYPlot().setDataset(_currentData);

		XYPlot plot = _myChart.getXYPlot();
		_plotRenderer = new XYLineAndShapeRenderer()
		{

			private BasicStroke thickStroke;
			int _lastSeriesNum = -1;
			TacticalSeries _lastSeries = null;
			boolean _isSensor;
			private boolean _isSelected;

			@Override
			public XYItemRendererState initialise(Graphics2D g2,
					Rectangle2D dataArea, XYPlot plot, XYDataset data,
					PlotRenderingInfo info)
			{
				// reset our local copy
				_lastSeriesNum = -1;

				return super.initialise(g2, dataArea, plot, data, info);
			}

			@Override
			public Boolean getSeriesShapesFilled(int series)
			{
				return super.getSeriesShapesFilled(series);
			}

			@Override
			public Boolean getSeriesShapesVisible(int series)
			{
				return _lastSeries.getVisible();
			}

			@Override
			public Paint getItemPaint(int row, int column)
			{
				Paint res;
				if (_isSelected)
					res = Color.BLACK;
				else
					res = super.getItemPaint(row, column);
				return res;
			}

			@Override
			public Stroke getItemStroke(int row, int column)
			{
				Stroke res;
				if (!_isSensor)
				{
					if (thickStroke == null)
						thickStroke = new BasicStroke(4);
					res = thickStroke;
				}
				else
					res = super.getItemStroke(row, column);
				return res;
			}

			@Override
			public void drawItem(Graphics2D g2, XYItemRendererState state,
					Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
					ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
					int series, int item, CrosshairState crosshairState, int pass)
			{
				if (series != _lastSeriesNum)
				{
					TimeSeriesCollection tData = (TimeSeriesCollection) dataset;
					_lastSeriesNum = series;
					_lastSeries = (TacticalSeries) tData.getSeries(series);
					_isSensor = (_lastSeries instanceof SensorSeries);
					_isSelected = _selectedTracks.contains(_lastSeries);

				}

				super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis,
						dataset, series, item, crosshairState, pass);
			}

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		plot.setRenderer(_plotRenderer);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		_selectionHelper.addSelectionChangedListener(listener);
	}

	@Override
	public ISelection getSelection()
	{
		return _selectionHelper.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		_selectionHelper.removeSelectionChangedListener(listener);
	}

	@Override
	public void setSelection(ISelection selection)
	{
		_selectionHelper.fireNewSelection(selection);
	}
}