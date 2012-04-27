package org.mwc.asset.scenarioplotter.editors;

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
import MWC.GUI.Layers.DataListener;
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

	private GtProjection _myProjection;

	// //////////////////////////////
	// constructor
	// //////////////////////////////

	public ASSETPlotEditor()
	{
		super();

		_myProjection = new GtProjection();
		
		_selectionChangeListener = new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				// right, see what it is
				ISelection sel = event.getSelection();
				newSelection(sel);

			}

		};

		_listenForMods = new DataListener()
		{
			public void dataModified(Layers theData, Layer changedLayer)
			{
				fireDirty();
			}

			public void dataExtended(Layers theData)
			{
				fireDirty();
			}

			public void dataReformatted(Layers theData, Layer changedLayer)
			{
				fireDirty();
			}
		};

	}

	private void newSelection(ISelection sel)
	{
		if (sel instanceof StructuredSelection)
		{
			StructuredSelection ss = (StructuredSelection) sel;
			Object datum = ss.getFirstElement();
			if (datum instanceof EditableWrapper)
			{
				EditableWrapper pw = (EditableWrapper) datum;
				Editable edd = pw.getEditable();
				if (edd instanceof ScenarioWrapper)
				{
					ScenarioWrapper sw = (ScenarioWrapper) edd;

					// also sort out the scenario component
					ScenarioType scen = sw.getScenario();

					if (scen != _myScenario)
					{
						updateScenario(sw, scen);
					}
				}

			}
		}
	}

	protected void updateScenario(ScenarioWrapper scenarioLayers,
			ScenarioType scenario)
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

	private void startListeningTo(Layers layers)
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

	private void stopListeningToThis(Layers layers)
	{
		layers.removeDataExtendedListener(_listenForMods);
		layers.removeDataModifiedListener(_listenForMods);
		layers.removeDataReformattedListener(_listenForMods);

		// and forget it in the chart
		_myChart.setLayers(null);
	}

	private void startListeningTo(ScenarioType scenario)
	{
		_myScenario = scenario;

		if (_stepListener == null)
			_stepListener = new ScenarioSteppedListener()
			{
				public void restart(ScenarioType scenario)
				{
					update();
				}

				public void step(ScenarioType scenario, long newTime)
				{
					update();
				}
			};

		scenario.addScenarioSteppedListener(_stepListener);

		// give it something to look at
		update();

	}

	private void stopListeningToThis(ScenarioType scenario)
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

	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);

		// tell the plotter not to defer paint events
		getChart().setDeferPaints(false);

		// and over-ride the undo button
		IAction undoAction = new UndoActionHandler(getEditorSite(),
				ASSETPlugin.ASSET_CONTEXT);
		IAction redoAction = new RedoActionHandler(getEditorSite(),
				ASSETPlugin.ASSET_CONTEXT);

		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.UNDO.getId(), undoAction);
		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.REDO.getId(), redoAction);

		// put in the plot-copy support
		IAction _copyClipboardAction = new Action()
		{
			public void runWithEvent(Event event)
			{
				ExportWMF ew = new ExportWMF(true, false);
				ew.run(null);
			}
		};

		IActionBars actionBars = getEditorSite().getActionBars();
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
		IViewPart[] views = getSite().getWorkbenchWindow().getActivePage()
				.getViews();
		for (int i = 0; i < views.length; i++)
		{
			IViewPart iViewPart = views[i];

			Object obj = iViewPart.getAdapter(ISelectionProvider.class);
			if (obj != null)
			{
				ISelectionProvider iS = (ISelectionProvider) obj;
				// have a look at the selection
				ISelection sel = iS.getSelection();
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
	protected SWTChart createTheChart(Composite parent)
	{
		SWTChart res = new SWTChart(_myLayers, parent, _myProjection)
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void chartFireSelectionChanged(ISelection sel)
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
		PlotMouseDragger curMode = PlotViewerPlugin.getCurrentMode();

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

	public void setViewport(WorldArea target)
	{
		getChart().getCanvas().getProjection().setDataArea(target);
	}

	public PlainProjection getProjection()
	{
		return getChart().getCanvas().getProjection();
	}

	public void setProjection(PlainProjection proj)
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

	public void doSave(IProgressMonitor monitor)
	{
	}

	public void doSaveAs()
	{
	}

	public void init(IEditorSite site, IEditorInput input)
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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// aah, just check it's not us
						if (part != this)
						{
							ISelectionProvider iS = (ISelectionProvider) part;
							iS.addSelectionChangedListener(_selectionChangeListener);
						}
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// aah, just check it's not is
						if (part != this)
						{
							ISelectionProvider iS = (ISelectionProvider) part;
							iS.removeSelectionChangedListener(_selectionChangeListener);
						}
					}
				});
	}

}
