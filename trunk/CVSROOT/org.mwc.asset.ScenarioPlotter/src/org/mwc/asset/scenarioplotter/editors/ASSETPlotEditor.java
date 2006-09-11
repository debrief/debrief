package org.mwc.asset.scenarioplotter.editors;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.mwc.asset.core.ASSETActivator;
import org.mwc.cmap.plotViewer.editors.CorePlotEditor;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import ASSET.ScenarioType;
import ASSET.GUI.Workbench.Plotters.ScenarioLayer;
import ASSET.Scenario.*;
import MWC.GUI.Layers;

public class ASSETPlotEditor extends CorePlotEditor
{
	protected ScenarioType _myScenario;

	protected ScenarioSteppedListener _myStepListener;

	protected ParticipantsChangedListener _myChangeListener;
	
	
	/** use our own layers object - not the one in the parent, silly.
	 * 
	 */
	private Layers _assetLayers;

	private ScenarioLayer _theScenarioLayers;
	
	public ASSETPlotEditor()
	{
		super();

	}

	public void dispose()
	{

		super.dispose();
	}

	
	/**
	 * create the chart we're after
	 * 
	 * @param parent
	 *          the parent object to stick it into
	 */
	protected SWTChart createTheChart(Composite parent)
	{
		SWTChart res = new SWTChart(_assetLayers, parent){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void chartFireSelectionChanged(ISelection sel)
			{
				// TODO Auto-generated method stub
				fireSelectionChanged(sel);
			}};
		return res;
	}	
	
	
	public void createPartControl(Composite parent)
	{
		// do the parent bits...
		super.createPartControl(parent);

		// override the layers bit
	//	getChart().setLayers(_myLayers);
		
		// sort out the part monitor
	//	_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());
	}

	// /////////////////////////////////////////////////////////
	// core plot editor member methods
	// ////////////////////////////////////////////////////////
	public void loadingComplete(Object source)
	{
	}

	public void doSave(IProgressMonitor monitor)
	{
	}

	public void doSaveAs()
	{
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInputWithNotify(input);
		
		// hey, get the layers out
		Object newLayers = input.getAdapter(Layers.class);
		if(newLayers != null)
		{
			storeLayers(newLayers);
		}
		
		// hmm, do we have a scenario
		Object tryScenario = input.getAdapter(ScenarioType.class);
		if(tryScenario != null)
		{
			ScenarioType scenario = (ScenarioType) tryScenario;
			listenToTheScenario(scenario);
			
			// update our title
			setPartName(scenario.getName());
		}
		
		// and the scenario layers object (it's the one we update when time moves forward
		Object tryScenarioLayers = input.getAdapter(ScenarioLayer.class);
		if(tryScenarioLayers != null)
		{
			ScenarioLayer scenario = (ScenarioLayer) tryScenarioLayers;
			_theScenarioLayers = scenario;
		}
		else
		{
			ASSETActivator.logError(Status.WARNING, "Our init message isn't providing us with the scenario layers", null);
		}
		
		
		// ok, create some actions
		// Create Action instances
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void listenToTheScenario(ScenarioType scenario)
	{
		scenario.addScenarioSteppedListener(new ScenarioSteppedListener(){
			public void restart()
			{
				doUpdate();
			}

			public void step(long newTime)
			{
				doUpdate();
			}});
		
	}
	protected void doUpdate()
	{
			_assetLayers.fireModified(_theScenarioLayers);	
	}

	/**
	 * @param tryLayers
	 */
	private void storeLayers(Object tryLayers)
	{
		_assetLayers = (Layers) tryLayers;
		
		_myLayers.addDataExtendedListener(_listenForMods);
		_myLayers.addDataModifiedListener(_listenForMods);
		_myLayers.addDataReformattedListener(_listenForMods);		
	}

	private void contributeToActionBars()
	{
	}

	private void hookContextMenu()
	{
	}

	private void makeActions()
	{
	}

	public boolean isSaveAsAllowed()
	{

		return false;
	}

	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == Layers.class)
		{
			res = _assetLayers;
		}

		if (res == null)
		{
			res = super.getAdapter(adapter);
		}
		return res;
	}

}
