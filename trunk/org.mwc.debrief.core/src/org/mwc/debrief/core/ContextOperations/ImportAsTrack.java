/**
 * 
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
				Editable thisE = subjects[i];
				if (thisE instanceof GTrack)
				{
					goForIt = true;
					if(title == null)
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
			Action doMerge = new Action(theTitle)
			{
				public void run()
				{
					IUndoableOperation theAction = new ImportAsTrackOperation(theTitle,
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
		private final Layer[] _parents;
		private final Editable[] _subjects;
		private Editable _target;

		public ImportAsTrackOperation(String title, Editable editable,
				Layers theLayers, Layer[] parentLayers, Editable[] subjects)
		{
			super(title);
			_target = editable;
			_layers = theLayers;
			_parents = parentLayers;
			_subjects = subjects;
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			for (int i = 0; i < _subjects.length; i++)
			{
				Editable ed = _subjects[i];
				if(ed instanceof GPackage)
				{
					GPackage gp = (GPackage) ed;
					Enumeration<Editable> enumer = gp.elements();
					while (enumer.hasMoreElements())
					{
						GTrack gt = (GTrack) enumer.nextElement();
						TrackWrapper tw = getTrackFor(gt);
						_layers.addThisLayer(tw);
					}
				}
				else
				{
					GTrack gt = (GTrack) ed;
					TrackWrapper tw = getTrackFor(gt);
					_layers.addThisLayer(tw);
				}
			}
			int res = TrackWrapper.mergeTracks(_target, _layers, _parents, _subjects);
			if (res == IStatus.OK)
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
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			CorePlugin.logError(Status.INFO,
					"Undo not permitted for merge operation", null);
			return null;
		}
	}

	public static TrackWrapper getTrackFor(GTrack gt)
	{
		gt.setVisible(false);
		
		TrackWrapper tw = new TrackWrapper();
		tw.setName(gt.getName());
		tw.setColor(gt.getColor());
		
		// loop through the points
		int len = gt.size();
		for(int i=0;i<len;i++)
		{
			Fix f = gt.getFixAt(i);
			FixWrapper fw = new FixWrapper(f);
			tw.addFix(fw);
		}
		
			
		return tw;
	}
}
