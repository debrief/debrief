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

package org.mwc.debrief.core.ContextOperations;

import java.util.Enumeration;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.wizards.core.NewSolutionWizard;

import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.temporal.TimeProvider;

/**
 * @author ian.mayo
 *
 */
public class GenerateTUASolution implements RightClickContextItemGenerator {

	private static class AddSolution extends CMAPOperation {

		private final Layers _layers;
		private TrackWrapper _track;
		private TMAWrapper _solutionWrapper;
		private final TMAContactWrapper _solution;

		public AddSolution(final Layers layers, final TrackWrapper track, final TMAWrapper solutionWrapper,
				final TMAContactWrapper solution) {
			super("Create TMA ellipse");
			_track = track;
			_solutionWrapper = solutionWrapper;
			_solution = solution;
			_layers = layers;
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
			// do we know the parent track?
			if (_track != null) {
				// right, just see if a solution with this name already exists
				final BaseLayer solLayer = _track.getSolutions();

				if (solLayer != null) {
					final Enumeration<Editable> theSols = solLayer.elements();
					while(theSols.hasMoreElements()) {
						final Editable editable = theSols.nextElement();
						final TMAWrapper sw = (TMAWrapper) editable;
						if (sw.getName().equals(_solutionWrapper.getName())) {
							// remember this solution
							_solutionWrapper = sw;

							// and forget about the track
							_track = null;
							break;
						}
					}
				}

				// did we find an existing solution?
				if (_track != null) {
					// nope, in that case add our solution to the track
					_track.add(_solutionWrapper);
				}
			}

			// add the solution to the wrapper
			_solutionWrapper.add(_solution);

			// sorted, do the update
			_layers.fireExtended(_solutionWrapper, _track);

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			return null;
		}

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val) {
			super(val);
		}

		public final void testIWork() {

		}
	}

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	@Override
	public void generate(final IMenuManager parent, final Layers theLayers, final Layer[] parentLayers,
			final Editable[] subjects) {
		//
		Action _myAction = null;

		// so, see if it's something we can do business with
		if (subjects.length == 1) {
			// sort out the current time
			final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			if (win != null) {
				final IWorkbenchPage page = win.getActivePage();
				final IEditorPart editor = page.getActiveEditor();
				if (editor != null) {
					final TimeProvider timer = editor.getAdapter(TimeProvider.class);
					if (timer != null) {
						final HiResDate tNow = timer.getTime();

						// ok, do I know how to create a TMA segment from this?
						final Editable onlyOne = subjects[0];
						if (onlyOne instanceof TrackWrapper) {
							final TrackWrapper tw = (TrackWrapper) onlyOne;
							// cool wrap it in an action.
							_myAction = new Action("Generate TUA Ellipse for this track") {
								@Override
								public void run() {
									// get the supporting data
									final NewSolutionWizard wizard = new NewSolutionWizard(tNow, tw, null);
									runOperation(theLayers, wizard);
								}
							};
						} else if (onlyOne instanceof TMAWrapper) {
							final TMAWrapper tw = (TMAWrapper) onlyOne;
							// cool wrap it in an action.
							_myAction = new Action("Generate TUA Ellipse within this group") {
								@Override
								public void run() {
									// get the supporting data
									final NewSolutionWizard wizard = new NewSolutionWizard(tNow, null, tw);
									runOperation(theLayers, wizard);
								}
							};
						}
					}
				}
			}
		}

		// go for it, or not...
		if (_myAction != null)
			parent.add(_myAction);

	}

	/**
	 * put the operation firer onto the undo history. We've refactored this into a
	 * separate method so testing classes don't have to simulate the CorePlugin
	 *
	 * @param operation
	 */
	protected void runIt(final IUndoableOperation operation) {
		CorePlugin.run(operation);
	}

	/**
	 * ok, do the wizard
	 *
	 * @param theLayers
	 * @param wizard
	 * @param helpContext
	 */
	private void runOperation(final Layers theLayers, final NewSolutionWizard wizard) {
		final WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		TrayDialog.setDialogHelpAvailable(true);
		dialog.setHelpAvailable(true);
		dialog.create();
		dialog.open();

		// did it work?
		if (dialog.getReturnCode() == Window.OK) {
			final TMAWrapper newSolWrapper = wizard.getSolutionWrapper();
			;
			final TMAContactWrapper newSol = wizard.getSolution();
			final TrackWrapper theTrack = wizard.getTrack();
			// ok, go for it.
			// sort it out as an operation
			final IUndoableOperation convertToTrack1 = new AddSolution(theLayers, theTrack, newSolWrapper, newSol);

			// ok, stick it on the buffer
			runIt(convertToTrack1);
		}
	}
}
