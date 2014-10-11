/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class MergeContacts implements RightClickContextItemGenerator
{

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(final IMenuManager parent, final Layers theLayers,
			final Layer[] parentLayers, final Editable[] subjects)
	{
		int validItems = 0;
		
		// we're only going to work with two or more items
		if (subjects.length > 1)
		{
			// are they tracks, or track segments
			for (int i = 0; i < subjects.length; i++)
			{
				boolean goForIt = false;
				final Editable thisE = subjects[i];
				if (thisE instanceof SensorWrapper)
				{
					goForIt = true;					
				}
				
				if(goForIt)
				{
					validItems++;
				}
				
				
				else
				{
					// NOTE: we're no longer logging this instance - it was just of value for developing
					// the merge operation
//					CorePlugin.logError(Status.INFO, "Not allowing merge, there's a non-compliant entry", null);
	
					// may as well drop out - this item wasn't compliant
					continue;
				}
			}
		}

		// ok, is it worth going for?
		if (validItems >= 2)
		{

			// right,stick in a separator
			parent.add(new Separator());

			final Editable editable = subjects[0];
			final String title = "Merge sensors into " + editable.getName();
			// create this operation
			final Action doMerge = new Action(title){
				public void run()
				{
					final IUndoableOperation theAction = new MergeSensorsOperation(title, editable, theLayers, parentLayers, subjects);
						
					CorePlugin.run(theAction );
				}};
			parent.add(doMerge);
		}
	}

	private static class MergeSensorsOperation extends CMAPOperation
	{

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final Layer[] _parents;
		private final Editable[] _subjects;
		private final Editable _target;


		public MergeSensorsOperation(final String title, final Editable editable, final Layers theLayers, final Layer[] parentLayers,
				final Editable[] subjects)
		{
			super(title);
			_target = editable;
			_layers = theLayers;
			_parents = parentLayers;
			_subjects = subjects;
		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			final int res = SensorWrapper.mergeSensors(_target, _layers, _parents[0], _subjects);
			if(res == IStatus.OK)
				fireModified();
			return Status.OK_STATUS;
		}

		
		
		@Override
		public boolean canRedo()
		{
			return false;
		}

		@Override
		public boolean canUndo()
		{
			return false;
		}
		
		private void fireModified()
		{
			_layers.fireExtended();
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			CorePlugin.logError(Status.INFO, "Undo not permitted for merge operation", null);
			return null;
		}
	}
}
