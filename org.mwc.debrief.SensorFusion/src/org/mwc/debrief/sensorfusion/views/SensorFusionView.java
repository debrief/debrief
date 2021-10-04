/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.sensorfusion.views;

import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.swt.ChartComposite;
import org.jfree.data.time.TimeSeriesCollection;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.preferences.SelectionHelper;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.sensorfusion.Activator;
import org.mwc.debrief.sensorfusion.views.DataSupport.SensorSeries;
import org.mwc.debrief.sensorfusion.views.DataSupport.TacticalSeries;
import org.mwc.debrief.sensorfusion.views.FusionPlotRenderer.FusionHelper;

import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.SplittableLayer;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GenericData.WatchableList;

public class SensorFusionView extends ViewPart implements ISelectionProvider, FusionHelper {

	private static class SplitSegmentsOperation extends CMAPOperation {

		final private Layers _layers;
		private final SplittableLayer _youSplitter;

		public SplitSegmentsOperation(final SplittableLayer youSplitter, final Layers currentLayers) {
			super("Auto-split sensor datasets");
			_youSplitter = youSplitter;
			_layers = currentLayers;
		}

		@Override
		public boolean canRedo() {
			return false;
		}

		@Override
		public boolean canUndo() {
			return false;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			_youSplitter.AutoSplitTracks();

			// ok, fire off a layers extended event to share the good news
			if (_layers != null)
				_layers.fireExtended();

			final IStatus res = new Status(IStatus.OK, Activator.PLUGIN_ID, "split sensor tracks successful", null);
			return res;
		}

		@Override
		public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			// register success
			return new Status(IStatus.OK, Activator.PLUGIN_ID, "Undo trim sensors", null);
		}
	}

	private static class TrimToSubjectOperation extends CMAPOperation {

		final private TrackWrapper _primary;
		final private WatchableList[] _secondaries;
		final private Layers _layers;
		private ArrayList<SensorWrapper> _deletedSensors;

		public TrimToSubjectOperation(final TrackWrapper primary, final WatchableList[] secondaries,
				final Layers _currentLayers) {
			super("Trim to sensors that match secondary tracks");
			_primary = primary;
			_secondaries = secondaries;
			_layers = _currentLayers;

		}

		@Override
		public boolean canRedo() {
			return true;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			_deletedSensors = DataSupport.trimToSensorNearSubjectTracks(_primary, _secondaries);

			final Iterator<SensorWrapper> iter = _deletedSensors.iterator();
			while (iter.hasNext()) {
				final SensorWrapper thisSensor = iter.next();
				_primary.removeElement(thisSensor);
			}

			// ok, fire off a layers extended event to share the good news
			if (_layers != null && !_deletedSensors.isEmpty()) {
				_layers.fireExtended();
			}

			final IStatus res = new Status(IStatus.OK, Activator.PLUGIN_ID, "trim sensors successful", null);
			return res;
		}

		@Override
		public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			// ok, restore the items
			final Iterator<SensorWrapper> iter = _deletedSensors.iterator();
			while (iter.hasNext()) {
				final SensorWrapper thisSensor = iter.next();
				_primary.add(thisSensor);
			}

			// ok, fire off a layers extended event to share the good news
			if (_layers != null && !_deletedSensors.isEmpty()) {
				_layers.fireExtended();
				_deletedSensors.clear();
			}

			// register success
			return new Status(IStatus.OK, Activator.PLUGIN_ID, "Undo trim sensors", null);
		}
	}

	private static class TrimToTrackPeriodOperation extends CMAPOperation {

		final private TrackWrapper _primary;
		final private Layers _layers;
		private ArrayList<SensorWrapper> _deletedSensors;

		public TrimToTrackPeriodOperation(final TrackWrapper primary, final Layers _currentLayers) {
			super("Trim sensors outside primary track period");
			_primary = primary;
			_layers = _currentLayers;

		}

		@Override
		public boolean canRedo() {
			return true;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			_deletedSensors = DataSupport.trimToTrackPeriod(_primary);

			final Iterator<SensorWrapper> iter = _deletedSensors.iterator();
			while (iter.hasNext()) {
				final SensorWrapper thisSensor = iter.next();
				_primary.removeElement(thisSensor);
			}

			// ok, fire off a layers extended event to share the good news
			if (_layers != null && !_deletedSensors.isEmpty()) {
				_layers.fireExtended();
			}

			final IStatus res = new Status(IStatus.OK, Activator.PLUGIN_ID, "trim sensors successful", null);
			return res;
		}

		@Override
		public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			// ok, restore the items
			final Iterator<SensorWrapper> iter = _deletedSensors.iterator();
			while (iter.hasNext()) {
				final SensorWrapper thisSensor = iter.next();
				_primary.add(thisSensor);
			}

			// ok, fire off a layers extended event to share the good news
			if (_layers != null && !_deletedSensors.isEmpty()) {
				_layers.fireExtended();
				_deletedSensors.clear();
			}

			// register success
			return new Status(IStatus.OK, Activator.PLUGIN_ID, "Undo trim sensors", null);
		}
	}

	private static final String CHART_NAME = "Bearing data";

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.mwc.debrief.SensorFusion";

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor = null;

	protected TrackManager _trackData;

	private ChartComposite _myChartFrame;

	private Action _useOriginalColors;

	final private XYLineAndShapeRenderer _plotRenderer;

	final private Vector<SensorSeries> _selectedTracks;
	final private HashMap<SensorWrapper, SensorSeries> _trackIndex;

	private Layers _currentLayers;

	/**
	 * helper - handle the selection a little better
	 */
	private SelectionHelper _selectionHelper;

	/**
	 * listen out for new data being added or removed
	 *
	 */
	protected DataListener _layerListener;

	protected ISelectionChangedListener _selectionChangeListener;

	protected ISelectionProvider _currentProvider;

	private Action _doSplit;

	private Action _trimToTrack;

	private Action _trimToSubjectTracks;

	/**
	 * The constructor.
	 */
	public SensorFusionView() {
		_selectedTracks = new Vector<SensorSeries>(0, 1);
		_plotRenderer = new FusionPlotRenderer(this);
		_trackIndex = new HashMap<SensorWrapper, SensorSeries>();
	}

	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		_selectionHelper.addSelectionChangedListener(listener);
	}

	private void contributeToActionBars() {
		final IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(_useOriginalColors);
		bars.getToolBarManager().add(new Separator());
		bars.getToolBarManager().add(_doSplit);
		bars.getToolBarManager().add(_trimToTrack);
		bars.getToolBarManager().add(_trimToSubjectTracks);
		// and the help link
		bars.getToolBarManager().add(new Separator());
		bars.getToolBarManager()
				.add(CorePlugin.createOpenHelpAction("org.mwc.debrief.help.BulkSensorData", null, this));

		final IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		menu.add(_useOriginalColors);
		bars.getToolBarManager().add(new Separator());
		menu.add(_doSplit);
		menu.add(_trimToTrack);
		menu.add(_trimToSubjectTracks);
		menu.add(new Separator());
		menu.add(CorePlugin.createOpenHelpAction("org.mwc.debrief.help.BulkSensorData", null, this));

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		makeActions();
		contributeToActionBars();

		// and the selection provider bits
		_selectionHelper = new SelectionHelper();
		getSite().setSelectionProvider(_selectionHelper);

		// declare our context sensitive help
		CorePlugin.declareContextHelp(parent, "org.mwc.debrief.help.BulkSensorData");

		parent.setLayout(new FillLayout());

		// ok, let's mockup the UI
		final JFreeChart myChart = DataSupport.createChart(null);
		myChart.setTitle(CHART_NAME);
		myChart.getXYPlot().setRenderer(_plotRenderer);

		// and the chart frame
		_myChartFrame = new ChartComposite(parent, SWT.NONE, myChart, 400, 600, 300, 200, 1800, 1800, true, true, true,
				true, true, true) {

			@Override
			protected Menu createPopupMenu(final boolean arg0, final boolean arg1, final boolean arg2,
					final boolean arg3) {
				// prevent the JFreeChart menu from opening
				return null;
			}

			@Override
			public void mouseUp(final MouseEvent event) {
				super.mouseUp(event);
				final JFreeChart c = getChart();
				if (c != null) {
					c.setNotify(true); // force redraw
				}
			}
		};
		_myChartFrame.setDisplayToolTips(false);
		_myChartFrame.setHorizontalAxisTrace(false);
		_myChartFrame.setVerticalAxisTrace(false);

		_myChartFrame.addChartMouseListener(new ChartMouseListener() {

			@Override
			public void chartMouseClicked(final ChartMouseEvent event) {
				final ChartEntity entity = event.getEntity();
				if (entity instanceof XYItemEntity) {
					final XYItemEntity xyi = (XYItemEntity) entity;
					final TimeSeriesCollection coll = (TimeSeriesCollection) xyi.getDataset();
					final TacticalSeries ts = (TacticalSeries) coll.getSeries(((XYItemEntity) entity).getSeriesIndex());
					if (ts instanceof SensorSeries) {
						final SensorSeries ss = (SensorSeries) ts;

						// check which mouse it si
						if (event.getTrigger().getButton() == 3) {
							// get the sensor itself
							final SensorWrapper sensor = ss.getSensor();

							// get the parent layer
							final TrackWrapper parentLayer = (TrackWrapper) _trackData.getPrimaryTrack();

							// ok, create ourselves a menu
							final MenuManager mmgr = new MenuManager();

							// insert the relevant menu items
							RightClickSupport.getDropdownListFor(mmgr, new Editable[] { sensor }, null,
									new Layer[] { parentLayer }, _currentLayers, false);

							// get the SWT menu object for it
							final Menu thisM = mmgr.createContextMenu(_myChartFrame);

							// and display it
							thisM.setVisible(true);
						} else {
							// right, is ctrl-key pressed
							final int mods = event.getTrigger().getModifiers();
							if ((mods & InputEvent.CTRL_MASK) == 0) {
								_selectedTracks.removeAllElements();
								_selectedTracks.add(ss);
							} else {
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
				} else {
					_selectedTracks.removeAllElements();

					// and update the UI
					updatedSelection();
					// ok, we need to redraw
					redrawPlot();
				}
			}

			@Override
			public void chartMouseMoved(final ChartMouseEvent event) {
			}
		});

		_selectionChangeListener = new ISelectionChangedListener() {

			@Override
			@SuppressWarnings({ "rawtypes" })
			public void selectionChanged(final SelectionChangedEvent event) {
				// right, see what it is
				final ISelection sel = event.getSelection();
				if (sel instanceof StructuredSelection) {
					final StructuredSelection ss = (StructuredSelection) sel;
					final Iterator eles = ss.iterator();
					boolean processingThese = false;
					while (eles.hasNext()) {
						final Object datum = eles.next();
						if (datum instanceof EditableWrapper) {
							final EditableWrapper pw = (EditableWrapper) datum;
							final Editable ed = pw.getEditable();
							if (ed instanceof SensorWrapper) {
								if (!processingThese) {
									processingThese = true;
									_selectedTracks.removeAllElements();

								}
								final SensorSeries thisSeries = _trackIndex.get(ed);
								_selectedTracks.add(thisSeries);
							}
						}
					}
					if (processingThese)
						redrawPlot();
				}
			}
		};

		// and sort out the listeners
		setupListeners();

	}

	@Override
	public void dispose() {
		stopListeningTo(_currentProvider);
		stopListeningTo(_currentLayers);
		_myPartMonitor.ditch();

		super.dispose();
	}

	@Override
	public HashMap<SensorWrapper, SensorSeries> getIndex() {
		return _trackIndex;
	}

	private SensorFusionView getMe() {
		return this;
	}

	@Override
	public Vector<SensorSeries> getSelectedItems() {
		return _selectedTracks;
	}

	@Override
	public ISelection getSelection() {
		return _selectionHelper.getSelection();
	}

	private void makeActions() {
		_useOriginalColors = new Action("Plot tracks using original colors", SWT.TOGGLE) {
			@Override
			public void run() {
				// we don't need to do any fancy processing. If we trigger redraw,
				// it will pick up the new value
				redrawPlot();
			}
		};
		_useOriginalColors.setImageDescriptor(Activator.getImageDescriptor("icons/ColorPalette.png"));

		_doSplit = new Action("Auto-split sensor segments", SWT.NONE) {
			@Override
			public void run() {
				// we don't need to do any fancy processing. If we trigger redraw,
				// it will pick up the new value
				splitTracks();
			}

		};
		_doSplit.setImageDescriptor(Activator.getImageDescriptor("icons/handaxe.png"));

		_trimToTrack = new Action("Trim to track period", SWT.NONE) {
			@Override
			public void run() {
				trimToTrack();
			}

		};
		_trimToTrack.setImageDescriptor(Activator.getImageDescriptor("icons/hourglass.png"));

		_trimToSubjectTracks = new Action("Remove sensor data unrelated to Secondary Tracks", SWT.NONE) {
			@Override
			public void run() {
				trimToSubjectTracks();
			}

		};
		_trimToSubjectTracks.setImageDescriptor(Activator.getImageDescriptor("icons/scissors.png"));
	}

	private void recalculateData() {

		// which is the primary?
		final WatchableList primary = _trackData.getPrimaryTrack();

		if (primary == null) {
			_myChartFrame.getChart().setTitle("Primary track missing");
			_myChartFrame.getChart().getXYPlot().setDataset(null);
			return;
		} else
			_myChartFrame.getChart().setTitle(CHART_NAME);

		// check it's a track
		if (!(primary instanceof TrackWrapper)) {
			CorePlugin.logError(IStatus.WARNING, "Primary track not suitable for watching", null);
		} else {
			final TrackWrapper _primary = (TrackWrapper) primary;
			// and which are the secondaries?
			final WatchableList[] secondaries = _trackData.getSecondaryTracks();

			// sort out the bearing tracks
			final TimeSeriesCollection newData = new TimeSeriesCollection();
			DataSupport.tracksFor(_primary, secondaries, newData);

			DataSupport.sensorDataFor(_primary, newData, _trackIndex);

			// and now the sensor data
			_myChartFrame.getChart().getXYPlot().setDataset(newData);
		}

	}

	protected void redrawPlot() {
		if (_plotRenderer != null) {
			_plotRenderer.setSeriesShapesVisible(0, true);
		}
	}

	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		_selectionHelper.removeSelectionChangedListener(listener);
	}

	protected void resetPlot() {
		if (!_myChartFrame.isDisposed()) {
			_myChartFrame.getChart().getXYPlot().setDataset(null);
			_myChartFrame.getChart().setTitle("Pending");
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
	}

	@Override
	public void setSelection(final ISelection selection) {
		_selectionHelper.fireNewSelection(selection);
	}

	protected void setupListeners() {
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());
		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.ACTIVATED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				// aah, just check it's not us
				if (part != getMe()) {
					if (_currentProvider != null)
						stopListeningTo(_currentProvider);

					startListeningTo((ISelectionProvider) part);
				}
			}
		});
		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.DEACTIVATED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				// aah, just check it's not is
				if (part != getMe()) {
					stopListeningTo((ISelectionProvider) part);
				}
			}
		});

		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.ACTIVATED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				final TrackManager provider = (TrackManager) part;

				// is this different to our current one?
				if (provider != _trackData)
					storeDetails(provider, parentPart);
			}
		});

		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.CLOSED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				if (part == _trackData) {
					_trackData = null;
					resetPlot();
				}
			}
		});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {

				// is this different to our current one?
				if (part != _currentLayers) {
					if (_layerListener == null)
						_layerListener = new DataListener() {

							@Override
							public void dataExtended(final Layers theData) {
								// redo the data
								recalculateData();
							}

							@Override
							public void dataModified(final Layers theData, final Layer changedLayer) {
								// redo the data
								recalculateData();
							}

							@Override
							public void dataReformatted(final Layers theData, final Layer changedLayer) {
								// redo the presentation
								redrawPlot();
							}
						};

					// ok, stop listening to the current one
					if (_currentLayers != null)
						stopListeningTo(_currentLayers);

					// and start listening to the new one
					startListeningTo((Layers) part);
				}
			}
		});

		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				if (part == _currentLayers) {
					stopListeningTo((Layers) part);
				}
			}
		});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());

	}

	protected void splitTracks() {
		// ok, go get the primary track
		if (_trackData != null) {
			final TrackWrapper primary = (TrackWrapper) _trackData.getPrimaryTrack();
			final BaseLayer sensors = primary.getSensors();
			if (sensors instanceof SplittableLayer) {
				final SplittableLayer youSplitter = (SplittableLayer) sensors;
				final IUndoableOperation op = new SplitSegmentsOperation(youSplitter, _currentLayers);
				CorePlugin.run(op);
			}
		}
	}

	protected void startListeningTo(final ISelectionProvider part) {
		_currentProvider = part;
		_currentProvider.addSelectionChangedListener(_selectionChangeListener);
	}

	// protected void resetData()
	// {
	//
	// final long _start = _primary.getStartDTG().getDate().getTime();
	// final long _end = _primary.getEndDTG().getDate().getTime();
	//
	// int ctr = 0;
	//
	// int MAX_SENSORS = 110;
	// for (int i = 0; i < MAX_SENSORS; i++)
	// {
	// final long _step = DataSupport.stepInterval();
	// final long _thisStart = _start + DataSupport.delay();
	// long _thisEnd = _thisStart + DataSupport.duration();
	// _thisEnd = Math.min(_thisEnd, _end);
	// long _this = _thisStart;
	//
	// SensorWrapper sw = new SensorWrapper("sensor 3:" + i);
	// sw.setColor(null);
	// double theVal = Math.random() * 360;
	// while (_this < _thisEnd)
	// {
	// theVal = theVal - 1 + (Math.random() * 2);
	// SensorContactWrapper scw = new SensorContactWrapper(_primary.getName(),
	// new HiResDate(_this), null, theVal, null, null, null, null,
	// "some label:", 0, sw.getName());
	// sw.add(scw);
	// ctr++;
	// _this += _step;
	// }
	//
	// _primary.add(sw);
	//
	// }
	// System.out.println("created " + ctr + " cuts");
	// }

	protected void startListeningTo(final Layers part) {
		_currentLayers = part;
		_currentLayers.addDataModifiedListener(_layerListener);
		_currentLayers.addDataReformattedListener(_layerListener);
		_currentLayers.addDataExtendedListener(_layerListener);
	}

	protected void stopListeningTo(final ISelectionProvider part) {
		if (_currentProvider != null)
			_currentProvider.removeSelectionChangedListener(_selectionChangeListener);
		_currentProvider = null;
	}

	protected void stopListeningTo(final Layers part) {
		if (_currentLayers != null) {
			_currentLayers.removeDataModifiedListener(_layerListener);
			_currentLayers.removeDataReformattedListener(_layerListener);
			_currentLayers.removeDataExtendedListener(_layerListener);
		}
		_currentLayers = null;
	}

	protected void storeDetails(final TrackManager provider, final IWorkbenchPart parentPart) {
		// ok, we've got a new plot to watch. better watch it...
		_trackData = provider;

		// clear our list
		_selectedTracks.removeAllElements();

		recalculateData();

	}

	protected void trimToSubjectTracks() {
		// ok, go get the primary track
		if (_trackData != null) {
			final TrackWrapper primary = (TrackWrapper) _trackData.getPrimaryTrack();
			final WatchableList[] secondaries = _trackData.getSecondaryTracks();

			final IUndoableOperation op = new TrimToSubjectOperation(primary, secondaries, _currentLayers);
			CorePlugin.run(op);
		}
	}

	protected void trimToTrack() {
		// ok, go get the primary track
		if (_trackData != null) {
			final TrackWrapper primary = (TrackWrapper) _trackData.getPrimaryTrack();

			final IUndoableOperation op = new TrimToTrackPeriodOperation(primary, _currentLayers);
			CorePlugin.run(op);
		}
	}

	protected void updatedSelection() {
		final Vector<EditableWrapper> wrappers = new Vector<EditableWrapper>(0, 1);
		final Iterator<SensorSeries> it = _selectedTracks.iterator();
		while (it.hasNext()) {
			final SensorSeries ss = it.next();
			final SensorWrapper sw = ss.getSensor();
			final EditableWrapper ed = new EditableWrapper(sw);
			wrappers.add(ed);
		}

		if (!wrappers.isEmpty()) {
			// and provide the selection object
			final StructuredSelection trackSelection = new StructuredSelection(wrappers);
			setSelection(trackSelection);
		}
	}

	@Override
	public boolean useOriginalColors() {
		return _useOriginalColors.isChecked();
	}
}