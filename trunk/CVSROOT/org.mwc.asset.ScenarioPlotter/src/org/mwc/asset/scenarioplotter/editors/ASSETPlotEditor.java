package org.mwc.asset.scenarioplotter.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.mwc.cmap.plotViewer.editors.CorePlotEditor;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import ASSET.ScenarioType;
import ASSET.Scenario.*;
import MWC.GUI.Layers;

public class ASSETPlotEditor extends CorePlotEditor
{
//	private PartMonitor _myPartMonitor;

	private Action _fitToWin;

	private Action _zoomOut;

	protected ScenarioType _myScenario;

	protected ScenarioSteppedListener _myStepListener;

	protected ParticipantsChangedListener _myChangeListener;
	
	/** use our own layers object - not the one in the parent, silly.
	 * 
	 */
	private Layers _assetLayers;
	
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

		// and start listening
		listenToMyParts();
	}

	/**
	 * listen out for part changes
	 */
	private void listenToMyParts()
	{
//		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
//				new PartMonitor.ICallback()
//				{
//					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
//					{
//						Layers newLayers = (Layers) part;
//						if (newLayers != _myLayers)
//						{
//							// stop listening to the old layesr
//							_myLayers.removeDataExtendedListener(_listenForMods);
//							_myLayers.removeDataModifiedListener(_listenForMods);
//							_myLayers.removeDataReformattedListener(_listenForMods);
//
//							_myLayers = newLayers;
//
//							// start listening to the new layers
//							_myLayers.addDataExtendedListener(_listenForMods);
//							_myLayers.addDataModifiedListener(_listenForMods);
//							_myLayers.addDataReformattedListener(_listenForMods);
//
//							// tell the chart about the new layers
//							getChart().setLayers(_myLayers);
//
//						}
//					}
//
//				});
//
//		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
//				new PartMonitor.ICallback()
//				{
//					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
//					{
//						if (part == _myLayers)
//						{
//							// ok, stop listening out.
//							_myLayers.removeDataExtendedListener(_listenForMods);
//							_myLayers.removeDataModifiedListener(_listenForMods);
//							_myLayers.removeDataReformattedListener(_listenForMods);
//							_myLayers = null;
//
//							getChart().setLayers(null);
//
//							// and do chart update
//							getChart().update();
//
//						}
//					}
//				});
//		
//		_myPartMonitor.addPartListener(ScenarioType.class, PartMonitor.ACTIVATED,
//				new PartMonitor.ICallback()
//				{
//					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
//					{
//						if(part != _myScenario)
//						{
//							// sort out our listeners
//							if(_myStepListener == null)
//							{
//								_myStepListener = new ScenarioSteppedListener(){
//
//									public void restart()
//									{
//									}
//
//									public void step(long newTime)
//									{
//										_myLayers.fireModified(_theScenarioLayer);
//									}};
//							}
//							if(_myChangeListener == null)
//							{
//								_myStepListener = new ScenarioSteppedListener(){
//									public void restart()
//									{
//									}
//
//									public void step(long newTime)
//									{
//										_myLayers.fireModified(_theScenarioLayer);
//									}};
//							}
//							
//							// stop listening to my scenario
//							_myScenario.removeScenarioSteppedListener(_myStepListener);
//							_myScenario.removeParticipantsChangedListener(_myChangeListener);
//							
//							// ok, forget about that one.
//							_myScenario = null;
//							
//							_myScenario = (ScenarioType) part;
//							_myScenario.addScenarioSteppedListener(_myStepListener);
//							_myScenario.addParticipantsChangedListener(_myChangeListener);
//						}
//					}
//				});		
//		_myPartMonitor.addPartListener(ScenarioType.class, PartMonitor.CLOSED,
//				new PartMonitor.ICallback()
//				{
//					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
//					{
//						if(part == _myScenario)
//						{
//							// stop listening to my scenario
//							_myScenario.removeScenarioSteppedListener(_myStepListener);
//							_myScenario.removeParticipantsChangedListener(_myChangeListener);
//							
//							// ok, forget about that one.
//							_myScenario = null;
//						}
//					}
//				});		
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
		
		// ok, create some actions
		// Create Action instances
		makeActions();
		hookContextMenu();
		contributeToActionBars();
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
		IActionBars bars = getEditorSite().getActionBars();  
		bars.getToolBarManager().add(_fitToWin);
		bars.getToolBarManager().add(_zoomOut);
	}

	private void hookContextMenu()
	{
	}

	private void makeActions()
	{
		_fitToWin = new Action(){
			public void run()
			{
				getChart().getCanvas().getProjection().zoom(0.0);
				getChart().update();
			}};
		_fitToWin.setText("Fit");
		_zoomOut = new Action(){
			public void run()
			{
				getChart().getCanvas().getProjection().zoom(2.0);
				getChart().update();
			}};
		_zoomOut.setText("Zoom out");
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
