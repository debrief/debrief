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
package org.mwc.debrief.satc_interface.actions;

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
import org.mwc.debrief.satc_interface.data.SATC_Solution;
import org.mwc.debrief.satc_interface.data.wrappers.StraightLegWrapper;

import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class MergeLegs implements RightClickContextItemGenerator
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
		if (subjects.length >= 1)
		{
			// are they tracks, or track segments
			for (int i = 0; i < subjects.length; i++)
			{
				boolean goForIt = false;
				final Editable thisE = subjects[i];
				if (thisE instanceof StraightLegWrapper)
				{
					goForIt = true;
					validItems++;
				}

				if (goForIt)
				{
				}
				else
				{
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

			final SATC_Solution solution = (SATC_Solution) parentLayers[0];

			// get the first item
			final Editable editable = subjects[0];

			final String mergeTitle = "Merge Straight Leg Forecasts";
			// ok, and let us do it in place
			final Action doMergeInPlace = new Action(mergeTitle)
			{
				public void run()
				{
					final IUndoableOperation theAction = new MergeLegsOperation(
							mergeTitle, editable, theLayers, solution, subjects);

					CorePlugin.run(theAction);
				}
			};
			parent.add(doMergeInPlace);
		}
	}

	private static class MergeLegsOperation extends CMAPOperation
	{

		/**
		 * the parent to update on completion
		 */
		protected final Layers _layers;
		protected final SATC_Solution _parent;
		protected final Editable[] _subjects;
		protected final Editable _target;

		public MergeLegsOperation(final String title, final Editable target,
				final Layers theLayers, final SATC_Solution parentLayer,
				final Editable[] subjects)
		{
			super(title);
			_target = target;
			_layers = theLayers;
			_parent = parentLayer;
			_subjects = subjects;
		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			// ok, loop through the legs
			StraightLegForecastContribution firstC = null;
			for (int i = 0; i < _subjects.length; i++)
			{
				Editable thisE = _subjects[i];
				StraightLegWrapper thisS = (StraightLegWrapper) thisE;
				StraightLegForecastContribution thisC = (StraightLegForecastContribution) thisS
						.getContribution();

				if (i == 0)
				{
					firstC = thisC;
				}
				else
				{
					// first remove it, so we don't get a re-calculate
					_parent.removeElement(thisS);

					// ok, extend the first cont with these times
					firstC.setFinishDate(thisC.getFinishDate());
				}
			}

			// // ok, get the first leg
			// StraightLegWrapper slwA = (StraightLegWrapper) _subjects[0];
			// StraightLegForecastContribution contA = slwA.getContribution();
			//
			// // ok, get the other legs
			// for()
			// StraightLegWrapper slwA = (StraightLegWrapper) _subjects[0];
			// StraightLegForecastContribution contA = slwA.getContribution();
			//
			// final int res = TrackWrapper.mergeTracks((TrackWrapper) _target,
			// _layers, _subjects);

			// ok, we can also hide the parent
			//
			// if (res == IStatus.OK)
			// {
			// // it worked, so switch off the composite track
			// TrackWrapper oldParent = (TrackWrapper) _parents[0];
			// oldParent.setVisible(false);
			//
			// // and talk about the UI update
			// fireModified();
			// }
			//
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
			CorePlugin.logError(Status.INFO,
					"Undo not permitted for merge operation", null);
			return null;
		}
	}
}
