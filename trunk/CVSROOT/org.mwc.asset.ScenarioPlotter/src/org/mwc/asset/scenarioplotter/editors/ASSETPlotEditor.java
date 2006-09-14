package org.mwc.asset.scenarioplotter.editors;

import java.io.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.mwc.asset.core.ASSETActivator;
import org.mwc.cmap.plotViewer.editors.CorePlotEditor;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import ASSET.*;
import ASSET.GUI.Workbench.Plotters.ScenarioLayer;
import ASSET.Participants.*;
import ASSET.Participants.Status;
import ASSET.Scenario.*;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.*;

public class ASSETPlotEditor extends CorePlotEditor
{
	protected ScenarioType _myScenario;

	protected ScenarioSteppedListener _myStepListener;

	protected ParticipantsChangedListener _myChangeListener;

	/**
	 * use our own layers object - not the one in the parent, silly.
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

		// hey, get the layers out
		Object newLayers = input.getAdapter(Layers.class);
		if (newLayers != null)
		{
			storeLayers(newLayers);
		}

		// hmm, do we have a scenario
		Object tryScenario = input.getAdapter(ScenarioType.class);
		if (tryScenario != null)
		{
			listenToTheScenario((ScenarioType) tryScenario);

			// update our title
			setPartName(_myScenario.getName());
		}

		// and the scenario layers object (it's the one we update when time moves
		// forward
		Object tryScenarioLayers = input.getAdapter(ScenarioLayer.class);
		if (tryScenarioLayers != null)
		{
			ScenarioLayer scenario = (ScenarioLayer) tryScenarioLayers;
			_theScenarioLayers = scenario;
		}
		else
		{
			ASSETActivator.logError(org.eclipse.core.runtime.Status.WARNING,
					"Our init message isn't providing us with the scenario layers", null);
		}

		// ok, create some actions
		// Create Action instances
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void listenToTheScenario(ScenarioType scenario)
	{
		_myScenario = scenario;
		scenario.addScenarioSteppedListener(new ScenarioSteppedListener()
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
		_myScenario.addParticipantsChangedListener(new ParticipantsChangedListener(){
			public void newParticipant(int index)
			{			
				// fire modified
				fireDirty();
				
				// listen to this participant
				ParticipantType cp = _myScenario.getThisParticipant(index);
				
				if(_participantMovedListener == null)
					_participantMovedListener= new ParticipantMovedListener(){
						public void moved(Status newStatus)
						{
							fireDirty();
						}

						public void restart()
						{
							fireDirty();
						}};
				
				cp.addParticipantMovedListener(_participantMovedListener);
			}
			public void participantRemoved(int index)
			{		
				// stop listening to this participant
				ParticipantType cp = _myScenario.getThisParticipant(index);
				cp.removeParticipantMovedListener(_participantMovedListener);
				
				// fire modified
				fireDirty();
			}
			public void restart()
			{			}});
		
	}
	
	ParticipantMovedListener _participantMovedListener = null;

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

	/**
	 * @see org.eclipse.ui.IEditorPart#doSave(IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{

		IEditorInput input = getEditorInput();

		if (input.exists())
		{
			// is this the correct type of file?
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
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
			Layer theDecs = _assetLayers.findLayer(Layers.CHART_FEATURES);
			
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
			ASSETActivator.logError(org.eclipse.core.runtime.Status.ERROR, "Unable to identify source file for plot",
					null);
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
					ASSETActivator.logError(IStatus.ERROR,
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
}
