package org.mwc.asset.scenarioplotter.editors;

import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.mwc.asset.core.ASSETPlugin;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.narrative.BaseNarrativeProvider;
import org.mwc.cmap.plotViewer.editors.CorePlotEditor;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import ASSET.ScenarioType;
import ASSET.GUI.Workbench.Plotters.ScenarioLayer;
import ASSET.Scenario.*;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.*;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.NarrativeEntry;

public class ASSETPlotEditor extends CorePlotEditor
{
	/**
	 * the type of message we send when loading/managing the scenario
	 */
	public static final String SCENARIO_CONFIG = "SCENARIO_CONFIG";

	protected final CoreScenario _myScenario;

	protected ScenarioSteppedListener _myStepListener;

	protected ParticipantsChangedListener _myChangeListener;

	/**
	 * narrative utility support
	 */
	private BaseNarrativeProvider _myNarrativeProvider;

	/**
	 * use our own layers object - not the one in the parent, silly.
	 */
	private Layers _assetLayers;

	private ScenarioLayer _myScenarioLayer;
	
	
	/** store our observers in a layer
	 * 
	 * @author ian
	 *
	 */
	private class ControlLayer extends BaseLayer
	{
		/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /** and the observers themselves
     * 
     */
    private Vector<Editable> _observers;

		public ControlLayer()
		{
			this.setName("Controls");
		}
		
		/** present ourselves as a list
		 * 
		 */
		public Enumeration<Editable> elements()
		{
		  IteratorWrapper res = null;
		  if(_observers != null)
		    res = new IteratorWrapper(_observers.iterator()); 
			return res;
		}

		/** store some more observers
		 * 
		 * @param observerList
		 */
    public void addObservers(Vector<ScenarioObserver> observerList)
    {
      // do we have our list yet?
      if(_observers == null)
        _observers = new Vector<Editable>(1,1);
      
      // ok, add the new items
      _observers.addAll(observerList);
    }
	}
	
	private ControlLayer _controller;


	public ASSETPlotEditor()
	{
		super();

		_myNarrativeProvider = new BaseNarrativeProvider();
		_assetLayers = new Layers();

		_myScenario = new CoreScenario();
		listenToTheScenario();
		
		// and the controller
		_controller = new ControlLayer();
		_assetLayers.addThisLayer(_controller);

		// add the chart plotter
		_myScenarioLayer = new ASSET.GUI.Workbench.Plotters.ScenarioLayer();
		_myScenarioLayer.setScenario(_myScenario);
		_assetLayers.addThisLayer(_myScenarioLayer);
	
		/** and listen out for changes to the layers (mostly property edits)
		 * 
		 */
		listenToLayerChanges();

	}

	public void dispose()
	{
    // clear out the observers
    if (_controller != null)
    {
    	java.util.Enumeration<Editable> enumer = _controller.elements();
    	while(enumer.hasMoreElements())
    	{
        ScenarioObserver observer = (ScenarioObserver) enumer.nextElement();
        observer.tearDown(_myScenario);
    	}
    }		
		
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
		SWTChart res = new SWTChart(_assetLayers, parent)
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void chartFireSelectionChanged(ISelection sel)
			{
				// TODO Auto-generated method stub
				fireSelectionChanged(sel);
			}
		};
		return res;
	}

	public void createPartControl(Composite parent)
	{
		// do the parent bits...
		super.createPartControl(parent);

		// override the layers bit
		// getChart().setLayers(_myLayers);

		// sort out the part monitor
		// _myPartMonitor = new
		// PartMonitor(getSite().getWorkbenchWindow().getPartService());
	}

	// /////////////////////////////////////////////////////////
	// core plot editor member methods
	// ////////////////////////////////////////////////////////

	public void loadingComplete(Object source)
	{
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInputWithNotify(input);

		// right, is this a file editor input?
		if (input instanceof IFileEditorInput)
		{
			try
			{
				IFileEditorInput ife = (IFileEditorInput) input;
				InputStream is;
				is = ife.getFile().getContents();
				// ok, load the file.
				ASSETReaderWriter.importThis(_myScenario, ife.getName(), is);

				// ok, tell everybody we've got some new participants
				fireMessage(SCENARIO_CONFIG, new HiResDate(), "Scenario loaded from:"
						+ ife.getName());

				// right, does it have a backdrop?
				if (_myScenario.getBackdrop() != null)
				{
					_assetLayers.removeThisLayer(_assetLayers.findLayer(Layers.CHART_FEATURES));
					_assetLayers.addThisLayer(_myScenario.getBackdrop());
				}

				// fire the layers change for new scenario data
				_assetLayers.fireExtended(null, _myScenarioLayer);

				// update our title
				setPartName(_myScenario.getName());
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// ok, create some actions
		// Create Action instances
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void listenToTheScenario()
	{
		_myScenario.addScenarioSteppedListener(new ScenarioSteppedListener()
		{
			public void restart()
			{
				doUpdate();
				// fire modified
				fireDirty();
			}

			public void step(long newTime)
			{
				doUpdate();
				// fire modified
				fireDirty();
			}
		});

		// also listen for the scenario being modified
		_myScenario.addParticipantsChangedListener(new ParticipantsChangedListener()
		{
			public void newParticipant(int index)
			{
				// fire modified
				fireDirty();
			}

			public void participantRemoved(int index)
			{
				// fire modified
				fireDirty();
			}

			public void restart()
			{
				// fire modified
				fireDirty();
			}
		});

	}

	protected void doUpdate()
	{
		_assetLayers.fireModified(_myScenarioLayer);
	}

	/**
	 * @param tryLayers
	 */
	private void listenToLayerChanges()
	{
		_assetLayers.addDataExtendedListener(_listenForMods);
		_assetLayers.addDataModifiedListener(_listenForMods);
		_assetLayers.addDataReformattedListener(_listenForMods);
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

	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == Layers.class)
		{
			res = _assetLayers;
		}
		else if(adapter == ScenarioType.class)
		{
			res = _myScenario;
		}

		if (res == null)
		{
			res = super.getAdapter(adapter);
		}
		return res;
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#doSave(IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{
		IEditorInput input = getEditorInput();

		if (input.exists())
		{
			IFile file = null;

			// is this the correct type of file?
			if (input instanceof IFileEditorInput)
			{
				file = ((IFileEditorInput) input).getFile();
			}

			if (file != null)
				doSaveTo(file, monitor);
		}
	}

	/**
	 * save our plot to the indicated location
	 * 
	 * @param destination
	 *          where to save plot to
	 * @param monitor
	 *          somebody/something to be informed about progress
	 */
	private void doSaveTo(IFile destination, IProgressMonitor monitor)
	{
		boolean itWorked = false;

		if (destination != null)
		{
			// hey, get the decorations layer
			BaseLayer theDecs = (BaseLayer) _assetLayers.findLayer(Layers.CHART_FEATURES);

			// put the decs into the scenario
			_myScenario.setBackdrop(theDecs);

			// ok, now write to the file
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ASSETReaderWriter.exportThis(_myScenario, theDecs, bos);

			// now convert to String
			byte[] output = bos.toByteArray();
			InputStream is = new ByteArrayInputStream(output);

			try
			{
				destination.setContents(is, true, false, monitor);

				itWorked = true;
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}

			// ok, lastly indicate that the save worked (if it did!)
			_plotIsDirty = !itWorked;
			firePropertyChange(PROP_DIRTY);
		}
		else
		{
			ASSETPlugin.logError(org.eclipse.core.runtime.Status.ERROR,
					"Unable to identify source file for plot", null);
		}

	}

	public void doSaveAs()
	{
		String message = "Save as";
		SaveAsDialog dialog = new SaveAsDialog(getEditorSite().getShell());
		dialog.setTitle("Save Plot As");
		if (getEditorInput() instanceof FileEditorInput)
		{
			IFile oldFile = ((FileEditorInput) getEditorInput()).getFile();
			// dialog.setOriginalFile(oldFile);

			IPath oldPath = oldFile.getFullPath();
			IPath newStart = oldPath.removeFileExtension();
			IPath newPath = newStart.addFileExtension("xml");
			File asFile = newPath.toFile();
			String newName = asFile.getName();
			// dialog.setOriginalFile(newName);
			dialog.setOriginalName(newName);
		}
		dialog.create();
		if (message != null)
			dialog.setMessage(message, IMessageProvider.WARNING);
		else
			dialog.setMessage("Save file to another location.");
		dialog.open();
		IPath path = dialog.getResult();

		if (path == null)
		{
			return;
		}
		else
		{
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (!file.exists())
				try
				{
					System.out.println("creating:" + file.getName());
					file.create(new ByteArrayInputStream(new byte[] {}), false, null);
				}
				catch (CoreException e)
				{
					ASSETPlugin.logError(IStatus.ERROR,
							"Failed trying to create new file for save-as", e);
					return;
				}

			// ok, write to the file
			doSaveTo(file, new NullProgressMonitor());

			// also make this new file our input
			IFileEditorInput newInput = new FileEditorInput(file);
			setInputWithNotify(newInput);
		}

		_plotIsDirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	public boolean isSaveAsAllowed()
	{
		return true;
	}

	/**
	 * fire off the message to any listeners
	 * 
	 * @param type
	 *          the type of message we're sending out
	 * @param dtg
	 *          the DTG of the message
	 * @param message
	 */
	protected void fireMessage(String type, HiResDate dtg, String message)
	{
		NarrativeEntry newEntry = new NarrativeEntry("Controller", type, dtg, message);
		_myNarrativeProvider.fireEntry(newEntry);
	}


	@Override
	protected void filesDropped(String[] fileNames)
	{
		super.filesDropped(fileNames);
		
		// ok. is it a control file?
		
		String control = fileNames[0];
		try
		{
			InputStream controlStream;
			controlStream = new FileInputStream(control);
	    // now get the control data
	    ASSETReaderWriter.ResultsContainer controller = 
	    	ASSET.Util.XML.ASSETReaderWriter.importThisControlFile(control,
	        controlStream);
	                                                                                                           
	    // and do our stuff with the observers (tell them about our scenario)
	    configureObservers(controller.observerList, controller.outputDirectory);
	    
	    // inform our controllers object about the new observers
	    _controller.addObservers(controller.observerList);

	    // and setup the random number seed
	    _myScenario.setSeed(controller.randomSeed);				
		} 
		catch (FileNotFoundException e)
		{
			CorePlugin.logError(Status.ERROR,"Whilst reading in control file", e);
		}
		
	}
	

  public void configureObservers(Vector<ScenarioObserver> observers, File outputPath)
  {
    Iterator<ScenarioObserver> iter = observers.iterator();
    while (iter.hasNext())
    {
      ScenarioObserver observer = (ScenarioObserver) iter.next();

      // is this an observer which is interested in the output path
      if (observer instanceof RecordToFileObserverType)
      {
        RecordToFileObserverType obs = (RecordToFileObserverType) observer;
        obs.setDirectory(outputPath);
      }

      // ok, let it set itself up
      observer.setup(_myScenario);

      // and remember it for when we finish
      _controller.add(observer);
    }
  }		
}
