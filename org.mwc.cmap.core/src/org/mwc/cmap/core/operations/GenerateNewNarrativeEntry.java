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
package org.mwc.cmap.core.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.cmap.core.wizards.NewNarrativeEntryWizard;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;

/**
 * @author ian.mayo
 * 
 */
public class GenerateNewNarrativeEntry implements
		RightClickContextItemGenerator
{

	private static final String WIZARD_TITLE = "Generate new narrative entry";

public static class AddNarrativeEntry extends CMAPOperation
	{

		final private Layers _layers;
		final private NarrativeEntry _entry;
		private NarrativeWrapper _parent;

		public AddNarrativeEntry(final Layers layers, final NarrativeWrapper parent,
				final NarrativeEntry entry)
		{
			super("Create narrative entry");
			_layers = layers;
			_parent = parent;
			_entry = entry;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			// do we have a parent?
			if (_parent == null)
			{
				// see if it already exists
				_parent = (NarrativeWrapper) _layers.findLayer(LayerHandler.NARRATIVE_LAYER);
				if (_parent == null)
				{
					_parent = new NarrativeWrapper(LayerHandler.NARRATIVE_LAYER);
					_layers.addThisLayer(_parent);
				}
			}

			_parent.add(_entry);

			// sorted, do the update
			_layers.fireExtended(_entry, _parent);

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
			_parent.removeElement(_entry);
			_layers.fireExtended(null, _parent);
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

		boolean goForIt = false;
		NarrativeWrapper narrative = null;

		// we're only going to work with two or more items
		if (subjects.length == 0)
		{
			goForIt = true;
		}

		if (subjects.length == 1)
		{
			if (subjects[0] instanceof NarrativeWrapper)
			{
				goForIt = true;
				narrative = (NarrativeWrapper) subjects[0];
			}
		}

		if (goForIt)
		{
		  // try to get the current plot date
	    // ok, populate the data
	    final IEditorPart curEditor = PlatformUI.getWorkbench()
	        .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	    final HiResDate date;
	    if (curEditor instanceof IAdaptable)
	    {
	      TimeProvider prov = (TimeProvider) curEditor.getAdapter(TimeProvider.class);
	      if(prov != null)
	      {
	        date = prov.getTime();
	      }
	      else
	      {
	        date = null;
	      }
	    }
	    else
	    {
	      date = null;
	    }
	    
			// right,stick in a separator
			parent.add(new Separator());

			final NarrativeWrapper theNarrative = narrative;

			// create this operation
			final Action addEntry = new Action(WIZARD_TITLE)
			{
				public void run()
				{
					// get the supporting data
					final NewNarrativeEntryWizard wizard = new NewNarrativeEntryWizard(date);
					runOperation(theLayers, theNarrative, wizard);
				}
			};
			
			// ok - set the image descriptor
			addEntry.setImageDescriptor(CorePlugin
					.getImageDescriptor("icons/16/narrative_entry.png"));

			
			parent.add(addEntry);
		}

	}

	/**
	 * ok, do the wizard
	 * 
	 * @param theLayers
	 * @param theNarrative
	 * @param wizard
	 * @param helpContext
	 */
	private void runOperation(final Layers theLayers,
			final NarrativeWrapper theNarrative, final NewNarrativeEntryWizard wizard)
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
			final NarrativeEntry ne = wizard.getEntry();
			// ok, go for it.
			// sort it out as an operation
			final IUndoableOperation addTheCut = new AddNarrativeEntry(theLayers,
					theNarrative, ne);

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
