/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.scenarioplotter.editors;

import java.awt.Color;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.SubActionBars2;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.mwc.asset.core.ASSETPlugin;
import org.mwc.asset.scenariocontroller2.views.ScenarioWrapper;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.gt2plot.proj.GtProjection;
import org.mwc.cmap.plotViewer.PlotViewerPlugin;
import org.mwc.cmap.plotViewer.actions.ExportWMF;
import org.mwc.cmap.plotViewer.editors.CorePlotEditor;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioSteppedListener;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener2;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;

public class ASSETPlotEditor extends CorePlotEditor
{

	// //////////////////////////////
	// member data
	// //////////////////////////////

	/**
	 * the scenario we're listening to
	 * 
	 */
	protected ScenarioType _myScenario = null;

	private ScenarioSteppedListener _stepListener;

	protected ISelectionChangedListener _selectionChangeListener;

	private final GtProjection _myProjection;

	// //////////////////////////////
	// constructor
	// //////////////////////////////

	public ASSETPlotEditor()
	{
		super();

		_myProjection = new GtProjection();

		_selectionChangeListener = new ISelectionChangedListener()
		{
			public void selectionChanged(final SelectionChangedEvent event)
			{
				// right, see what it is
				final ISelection sel = event.getSelection();
				newSelection(sel);

			}

		};

		_listenForMods = new DataListener2()
		{
			public void dataModified(final Layers theData, final Layer changedLayer)
			{
				fireDirty();
			}

			public void dataExtended(final Layers theData)
			{
				fireDirty();
			}

			public void dataReformatted(final Layers theData, final Layer changedLayer)
			{
				fireDirty();
			}

			@Override
			public void dataExtended(final Layers theData, final Plottable newItem, final Layer parent)
			{
				fireDirty();
			}
		};

	}

	private void newSelection(final ISelection sel)
	{
		if (sel instanceof StructuredSelection)
		{
			final StructuredSelection ss = (StructuredSelection) sel;
			final Object datum = ss.getFirstElement();
			if (datum instanceof EditableWrapper)
			{
				final EditableWrapper pw = (EditableWrapper) datum;
				final Editable edd = pw.getEditable();
				if (edd instanceof ScenarioWrapper)
				{
					final ScenarioWrapper sw = (ScenarioWrapper) edd;

					// also sort out the scenario component
					final ScenarioType scen = sw.getScenario();

					if (scen != _myScenario)
					{
						updateScenario(sw, scen);
					}
				}

			}
		}
	}

	protected void updateScenario(final ScenarioWrapper scenarioLayers,
			final ScenarioType scenario)
	{
		// are we already listening to it?
		if (_myScenario != null)
		{
			stopListeningToThis(_myLayers);
			stopListeningToThis(_myScenario);
		}

		// give it to the chart
		_myChart.setLayers(scenarioLayers);
		//
		// // ok, let's start listening to it
		startListeningTo(scenarioLayers);
		startListeningTo(scenario);

		_myChart.update();

	}

	private void startListeningTo(final Layers layers)
	{

		// are we already listening to it?
		if (_myLayers != null)
			stopListeningToThis(_myLayers);

		// and remember it
		_myLayers = layers;

		// give it to the chart
		_myChart.setLayers(_myLayers);

		// start listening
		layers.addDataExtendedListener(_listenForMods);
		layers.addDataModifiedListener(_listenForMods);
		layers.addDataReformattedListener(_listenForMods);

		// make sure we can see the data
		_myChart.rescale();

		// and ask for a refresh
		fireDirty();
	}

	private void stopListeningToThis(final Layers layers)
	{
		layers.removeDataExtendedListener(_listenForMods);
		layers.removeDataModifiedListener(_listenForMods);
		layers.removeDataReformattedListener(_listenForMods);

		// and forget it in the chart
		_myChart.setLayers(null);
	}

	private void startListeningTo(final ScenarioType scenario)
	{
		_myScenario = scenario;

		if (_stepListener == null)
			_stepListener = new ScenarioSteppedListener()
			{
				public void restart(final ScenarioType scenario)
				{
					update();
				}

				public void step(final ScenarioType scenario, final long newTime)
				{
					update();
				}
			};

		scenario.addScenarioSteppedListener(_stepListener);

		// give it something to look at
		update();

	}

	private void stopListeningToThis(final ScenarioType scenario)
	{
		if (scenario != null)
			scenario.removeScenarioSteppedListener(_stepListener);
	}

	public void dispose()
	{
		super.dispose();

		// do we have a scenario?
		if (_myScenario != null)
			stopListeningToThis(_myScenario);

		// do we have layers?
		if (_myLayers != null)
			stopListeningToThis(_myLayers);

		_myScenario = null;
		_myLayers = null;

		// empty the part monitor
		if (_myPartMonitor != null)
			_myPartMonitor.ditch();
		_myPartMonitor = null;
	}

	public void createPartControl(final Composite parent)
	{
		super.createPartControl(parent);

		// tell the plotter not to defer paint events
		getChart().setDeferPaints(true);
		
		// try to set the background color
		getChart().getCanvas().setBackgroundColor(Color.black); 

		// and over-ride the undo button
		final IAction undoAction = new UndoActionHandler(getEditorSite(),
				ASSETPlugin.ASSET_CONTEXT);
		final IAction redoAction = new RedoActionHandler(getEditorSite(),
				ASSETPlugin.ASSET_CONTEXT);

		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.UNDO.getId(), undoAction);
		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.REDO.getId(), redoAction);

		// put in the plot-copy support
		final IAction _copyClipboardAction = new Action()
		{
			public void runWithEvent(final Event event)
			{
				final ExportWMF ew = new ExportWMF(true, false);
				ew.run(null);
			}
		};

		final IActionBars actionBars = getEditorSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
				_copyClipboardAction);

		// and start listening
		setupListeners();

		// see if there's anything to show
		lookForData();

	}

	private void lookForData()
	{
		// ok, cycle through the open views and check if we're after any of them
		@SuppressWarnings("deprecation")
		final
		IViewPart[] views = getSite().getWorkbenchWindow().getActivePage()
				.getViews();
		for (int i = 0; i < views.length; i++)
		{
			final IViewPart iViewPart = views[i];

			final Object obj = iViewPart.getAdapter(ISelectionProvider.class);
			if (obj != null)
			{
				final ISelectionProvider iS = (ISelectionProvider) obj;
				// have a look at the selection
				final ISelection sel = iS.getSelection();
				if (sel != null)
					newSelection(sel);
			}

			_myPartMonitor.partActivated(iViewPart);
		}
	}

	/**
	 * create the chart we're after
	 * 
	 * @param parent
	 *          the parent object to stick it into
	 */
	protected SWTChart createTheChart(final Composite parent)
	{
		final SWTChart res = new SWTChart(_myLayers, parent, _myProjection)
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void chartFireSelectionChanged(final ISelection sel)
			{
				//
			}
		};
		return res;
	}

	public void setFocus()
	{
		// just put some kind of blank object into the properties window
		// putBackdropIntoProperties();

		// ok, set the drag mode to whatever our common "mode" is.
		// - start off by getting the current mode
		final PlotMouseDragger curMode = PlotViewerPlugin.getCurrentMode();

		// has one been set?
		if (curMode != null)
		{
			// yup, better observe it then
			_myChart.setDragMode(curMode);
		}

	}

	public WorldArea getViewport()
	{
		return getChart().getCanvas().getProjection().getDataArea();
	}

	public void setViewport(final WorldArea target)
	{
		getChart().getCanvas().getProjection().setDataArea(target);
	}

	public PlainProjection getProjection()
	{
		return getChart().getCanvas().getProjection();
	}

	public void setProjection(final PlainProjection proj)
	{
		// yes, just update it.
		_myChart.getCanvas().setProjection(proj);
	}

	public SWTChart getChart()
	{
		return _myChart;
	}

	/**
	 * Returns the ActionbarContributor for the Editor.
	 * 
	 * @return the ActionbarContributor for the Editor.
	 */
	public SubActionBars2 getActionbar()
	{
		return (SubActionBars2) getEditorSite().getActionBars();
	}

	/**
	 * hmm, are we dirty?
	 * 
	 * @return
	 */
	public boolean isDirty()
	{
		return false;
	}

	public void update()
	{
		// call a chart update - so it removes it's cached data
		if (_myChart != null)
			_myChart.update();
	}

	/**
	 * get the chart to fit to window
	 * 
	 */
	public void rescale()
	{
		_myChart.rescale();
	}

	public void doSave(final IProgressMonitor monitor)
	{
	}

	public void doSaveAs()
	{
	}

	public void init(final IEditorSite site, final IEditorInput input)
			throws PartInitException
	{
		// initialise importand stuff
		setSite(site);
		setInputWithNotify(input);
	}

	public boolean isSaveAsAllowed()
	{
		return false;
	}

	private void setupListeners()
	{
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// aah, just check it's not us
						if (part != this)
						{
							final ISelectionProvider iS = (ISelectionProvider) part;
							iS.addSelectionChangedListener(_selectionChangeListener);
						}
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// aah, just check it's not is
						if (part != this)
						{
							final ISelectionProvider iS = (ISelectionProvider) part;
							iS.removeSelectionChangedListener(_selectionChangeListener);
						}
					}
				});
	}

}
