/**
 * 
 */
package org.mwc.debrief.core.ContextOperations;

import java.util.Iterator;
import java.util.Vector;

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

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class GroupTracks implements RightClickContextItemGenerator
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
		boolean goForIt = false;

		Vector<TrackWrapper> tracks = new Vector<TrackWrapper>();
		
		// we're only going to work with two or more items, and we only put them into a track wrapper
		if (subjects.length > 1)
		{
			// are they tracks, or track segments
			for (int i = 0; i < subjects.length; i++)
			{
				Editable thisE = subjects[i];
				if (thisE instanceof TrackWrapper)
				{
					goForIt = true;
					tracks.add((TrackWrapper) thisE);
				}
				else if (thisE instanceof TrackSegment)
				{
					goForIt = true;
				}
				
				if(!goForIt)
				{
					CorePlugin.logError(Status.INFO, "Not allowing merge, there's a non-compliant entry", null);
					// may as well drop out - this item wasn't compliant
					continue;
				}
			}
		}

		// check we got some
		if(tracks.size() == 0)
			goForIt = false;
		
		// ok, is it worth going for?
		if (goForIt)
		{
			// right,stick in a separator
			parent.add(new Separator());

			for (Iterator<TrackWrapper> iterator = tracks.iterator(); iterator.hasNext();)
			{
				final TrackWrapper editable = iterator.next();
				final String title = "Group tracks into " + editable.getName();
				// create this operation
				Action doMerge = new Action(title){
					public void run()
					{
						IUndoableOperation theAction = new GroupTracksOperation(title, editable, theLayers, parentLayers, subjects);							
						CorePlugin.run(theAction );
					}};
				parent.add(doMerge);
			}
		}
	}

	private static class GroupTracksOperation extends CMAPOperation
	{

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final Layer[] _parents;
		private final Editable[] _subjects;
		private TrackWrapper wrapper;


		public GroupTracksOperation(String title, TrackWrapper receiver, Layers theLayers, Layer[] parentLayers,
				Editable[] subjects)
		{
			super(title);
			wrapper = receiver;
			_layers = theLayers;
			_parents = parentLayers;
			_subjects = subjects;
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			TrackWrapper.groupTracks(wrapper, _layers, _parents, _subjects);
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
			CorePlugin.logError(Status.INFO, "Undo not permitted for merge operation", null);
			return null;
		}
	}
}
