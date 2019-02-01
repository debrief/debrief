/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
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
import org.eclipse.jface.dialogs.TrayDialog;
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
import org.mwc.debrief.core.wizards.core.NewContactWizard;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.temporal.TimeProvider;

/**
 * @author ian.mayo
 * 
 */
public class GenerateNewSensorContact implements RightClickContextItemGenerator
{

	private static class AddSensorCut extends CMAPOperation
	{

		final private Layers _layers;
		final private SensorWrapper _sensorWrapper;
		final private SensorContactWrapper _contact;

		public AddSensorCut(final Layers layers, final SensorWrapper sensorWrapper,
				final SensorContactWrapper contact)
		{
			super("Create sensor cut");
			_sensorWrapper = sensorWrapper;
			_contact = contact;
			_layers = layers;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			_sensorWrapper.add(_contact);

			// sorted, do the update
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

		@Override
		public boolean canRedo()
		{
			return true;
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			_sensorWrapper.removeElement(_contact);
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
			final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			if(win != null)
			{
				final IWorkbenchPage page = win.getActivePage();
				final IEditorPart editor = page.getActiveEditor();
				if (editor != null)
				{
					final Object objTime = editor.getAdapter(TimeProvider.class);
					if (objTime != null)
					{
						final TimeProvider timer = (TimeProvider)objTime;
						final HiResDate tNow = timer.getTime();

						// ok, do I know how to create a TMA segment from this?
						final Editable onlyOne = subjects[0];
						if (onlyOne instanceof SensorWrapper)
						{
							final SensorWrapper tw = (SensorWrapper) onlyOne;
							// cool wrap it in an action.
							_myAction = new Action("Generate contact for this sensor")
							{
								@Override
								public void run()
								{
									// get the supporting data
									final NewContactWizard wizard = new NewContactWizard(tNow, null, tw);
									runOperation(theLayers, wizard);
								}
							};
						}
					}

					// go for it, or not...
					if (_myAction != null)
						parent.add(_myAction);
				}
			}
		}

	}

	/**
	 * ok, do the wizard
	 * 
	 * @param theLayers
	 * @param wizard
	 * @param helpContext
	 */
	private void runOperation(final Layers theLayers, final NewContactWizard wizard)
	{
		final WizardDialog dialog = new WizardDialog(Display.getCurrent()
				.getActiveShell(), wizard);
		TrayDialog.setDialogHelpAvailable(true);
		dialog.setHelpAvailable(true);
		dialog.create();
		dialog.open();

		// did it work?
		if (dialog.getReturnCode() == WizardDialog.OK)
		{
			final SensorWrapper senWrapper = wizard.getSensorWrapper();

			final SensorContactWrapper newCut = wizard.getContact();
			// ok, go for it.
			// sort it out as an operation
			final IUndoableOperation addTheCut = new AddSensorCut(theLayers, senWrapper,
					newCut);

			// ok, stick it on the buffer
			runIt(addTheCut);
		}
	}

	/**
	 * put the operation firer onto the undo history. We've refactored this into a
	 * separate method so testing classes don't have to simulate the CorePlugin
	 * 
	 * @param operation
	 */
	protected void runIt(final IUndoableOperation operation)
	{
		CorePlugin.run(operation);
	}
}
