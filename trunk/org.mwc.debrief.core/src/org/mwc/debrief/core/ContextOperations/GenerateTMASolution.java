/**
 * 
 */
package org.mwc.debrief.core.ContextOperations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.wizards.core.NewSolutionWizard;

import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;

/**
 * @author ian.mayo
 * 
 */
public class GenerateTMASolution implements RightClickContextItemGenerator
{

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}

		public final void testIWork()
		{

		}
	}

	private static class AddSolution extends CMAPOperation
	{

		private Layers _layers;
		private TrackWrapper _track;
		private TMAWrapper _solutionWrapper;
		private TMAContactWrapper _solution;

		public AddSolution(Layers layers, TrackWrapper track, TMAWrapper solutionWrapper, TMAContactWrapper solution)
				{
			super("Create TMA ellipse");
			_track = track;
			_solutionWrapper = solutionWrapper;
			_solution = solution;
			_layers = layers;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// do we know the parent track?
			if(_track != null)
			{
				// ok, add the solution to the track
				_track.add(_solutionWrapper);
			}
			
			// add the solution to the wrapper
			_solutionWrapper.add(_solution);

			// sorted, do the update
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// do we know the parent track?
			if(_track != null)
			{
				// ok, add the solution to the track
				_track.removeElement(_solutionWrapper);
			}
			
			_solutionWrapper.removeElement(_solution);
			
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

	}

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(final IMenuManager parent, final Layers theLayers,
			final Layer[] parentLayers, final Editable[] subjects)
	{
		//
		Action _myAction = null;

		// so, see if it's something we can do business with
		if (subjects.length == 1)
		{
			// sort out the current time
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = win.getActivePage();
			IEditorPart editor = page.getActiveEditor();
			TimeProvider timer = (TimeProvider) editor
					.getAdapter(TimeProvider.class);
			final HiResDate tNow = timer.getTime();

			// ok, do I know how to create a TMA segment from this?
			Editable onlyOne = subjects[0];
			if (onlyOne instanceof TrackWrapper)
			{
				final TrackWrapper tw = (TrackWrapper) onlyOne;
				// cool wrap it in an action.
				_myAction = new Action("Generate TMA Ellipse for this track")
				{
					@Override
					public void run()
					{
						// get the supporting data
						NewSolutionWizard wizard = new NewSolutionWizard(tNow, tw, null);
						runOperation(theLayers, wizard);
					}
				};
			}
			else if(onlyOne instanceof TMAWrapper)
			{
				final TMAWrapper tw = (TMAWrapper) onlyOne;
				// cool wrap it in an action.
				_myAction = new Action("Generate TMA Ellipse within this group")
				{
					@Override
					public void run()
					{
						// get the supporting data
						NewSolutionWizard wizard = new NewSolutionWizard(tNow, null, tw);
						runOperation(theLayers, wizard);
					}
				};
			}
		}

		// go for it, or not...
		if (_myAction != null)
			parent.add(_myAction);

	}

	/** ok, do the wizard
	 * 
	 * @param theLayers
	 * @param wizard
	 */
	private void runOperation(final Layers theLayers,
			NewSolutionWizard wizard)
	{
		WizardDialog dialog = new WizardDialog(Display.getCurrent()
				.getActiveShell(), wizard);
		dialog.create();
		dialog.open();

		// did it work?
		if (dialog.getReturnCode() == WizardDialog.OK)
		{
			TMAWrapper newSolWrapper = wizard.getSolutionWrapper();;
			TMAContactWrapper newSol = wizard.getSolution();
			TrackWrapper theTrack = wizard.getTrack();
			// ok, go for it.
			// sort it out as an operation
			IUndoableOperation convertToTrack1 = new AddSolution(theLayers, theTrack , newSolWrapper, newSol);

			// ok, stick it on the buffer
			runIt(convertToTrack1);
		}
	}


	/**
	 * put the operation firer onto the undo history. We've refactored this into a
	 * separate method so testing classes don't have to simulate the CorePlugin
	 * 
	 * @param operation
	 */
	protected void runIt(IUndoableOperation operation)
	{
		CorePlugin.run(operation);
	}
}
