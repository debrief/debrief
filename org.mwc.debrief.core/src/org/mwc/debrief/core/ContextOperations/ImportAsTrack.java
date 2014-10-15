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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.TacticalData.Fix;
import MWC.TacticalData.GND.GPackage;
import MWC.TacticalData.GND.GTrack;

/**
 * @author ian.mayo
 */
public class ImportAsTrack implements RightClickContextItemGenerator
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
		String title = null;

		// we're only going to work with two or more items
		if (subjects.length >= 1)
		{
			// are they tracks, or track segments
			for (int i = 0; i < subjects.length; i++)
			{
				boolean goForIt = false;
				final Editable thisE = subjects[i];
				if (thisE instanceof GTrack)
				{
					goForIt = true;
					if (title == null)
						title = "Import as Debrief track";
					else
						title = "Import as Debrief tracks";
				}
				else if (thisE instanceof GPackage)
				{
					title = "Import as Debrief tracks";
					goForIt = true;
				}

				if (goForIt)
				{
					validItems++;
				}
			}
		}

		// ok, is it worth going for?
		if (validItems >= 1)
		{

			// right,stick in a separator
			parent.add(new Separator());

			final Editable editable = subjects[0];
			final String theTitle = title;

			// create this operation
			final Action doMerge = new Action(theTitle)
			{
				public void run()
				{
					final IUndoableOperation theAction = new ImportAsTrackOperation(theTitle,
							editable, theLayers, parentLayers, subjects);

					CorePlugin.run(theAction);
				}
			};
			parent.add(doMerge);
		}
	}

	private static class ImportAsTrackOperation extends CMAPOperation
	{

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final Editable[] _subjects;

		public ImportAsTrackOperation(final String title, final Editable editable,
				final Layers theLayers, final Layer[] parentLayers, final Editable[] subjects)
		{
			super(title);
			_layers = theLayers;
			_subjects = subjects;
		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			for (int i = 0; i < _subjects.length; i++)
			{
				final Editable ed = _subjects[i];
				if (ed instanceof GPackage)
				{
					final GPackage gp = (GPackage) ed;
					final Enumeration<Editable> enumer = gp.elements();
					while (enumer.hasMoreElements())
					{
						final GTrack gt = (GTrack) enumer.nextElement();
						final TrackWrapper tw = getTrackFor(gt);
						_layers.addThisLayer(tw);
					}
				}
				else
				{
					final GTrack gt = (GTrack) ed;
					final TrackWrapper tw = getTrackFor(gt);
					_layers.addThisLayer(tw);
				}
			}
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
			CorePlugin.logError(Status.INFO,
					"Undo not permitted for merge operation", null);
			return null;
		}
	}

	public static TrackWrapper getTrackFor(final GTrack gt)
	{
		gt.setVisible(false);

		final TrackWrapper tw = new TrackWrapper();
		tw.setName(gt.getName());
		tw.setColor(gt.getColor());

		// loop through the points
		final int len = gt.size();
		for (int i = 0; i < len; i++)
		{
			final Fix f = gt.getFixAt(i);
			final FixWrapper fw = new FixWrapper(f);
			tw.addFix(fw);
		}

		return tw;
	}
}
