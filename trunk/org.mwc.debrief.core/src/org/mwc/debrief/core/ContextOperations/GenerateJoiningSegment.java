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
public class GenerateJoiningSegment implements RightClickContextItemGenerator
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
		// we're only going to work with two or more items
		if (subjects.length > 1)
		{
			// track the parents
			Layer firstParent = parentLayers[0];

			// is it a track?
			if (firstParent instanceof TrackWrapper)
			{
				final TrackWrapper parentTrack = (TrackWrapper) firstParent;

				// do they have the same parent layer?
				if (parentLayers[1] == parentLayers[0])
				{
					// ok, is it worth going for?
					if ((subjects[0] instanceof TrackSegment)
							&& (subjects[1] instanceof TrackSegment))
					{
						final String title = "Generate infill segment";
						// create this operation
						Action doMerge = new Action(title)
						{
							public void run()
							{
								IUndoableOperation theAction = new JoinTracksOperation(title,
										(TrackSegment) subjects[0], (TrackSegment) subjects[1],
										theLayers, parentTrack);

								CorePlugin.run(theAction);
							}
						};
						parent.add(new Separator());
						parent.add(doMerge);
					}
				}
			}
		}

	}

	private static class JoinTracksOperation extends CMAPOperation
	{

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final Layer _parentTrack;
		private final TrackSegment _trackOne;
		private final TrackSegment _trackTwo;
		private TrackSegment _infill;

		public JoinTracksOperation(String title, TrackSegment trackOne,
				TrackSegment trackTwo, Layers theLayers, Layer parentTrack)
		{
			super(title);
			_trackOne = trackOne;
			_trackTwo = trackTwo;
			_layers = theLayers;
			_parentTrack = parentTrack;
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{

			// now do the more detailed checks
			if (_trackOne.endDTG().greaterThan(_trackTwo.startDTG()))
			{
				// fail, they overlap
				CorePlugin.showMessage("Generate infill segment", "Sorry, this operation cannot be performed for overlapping track sections\nPlease delete overlapping data points and try again");
				return new Status(IStatus.ERROR, null, "Overlapping data points", null);
			}

			// cool, go for it
			// generate the new track segment
			_infill = new TrackSegment(_trackOne, _trackTwo);
			
			// add the track segment to the parent track
			_parentTrack.add(_infill);

			fireModified();
			return new Status(IStatus.OK, null, "generate infill successful", null);
		}

		@Override
		public boolean canRedo()
		{
			return false;
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		private void fireModified()
		{
			_layers.fireExtended();
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// right, just delete our new track segment
			_parentTrack.removeElement(_infill);
			
			// cool, tell everyone
			fireModified();
			
			// register success
			return new Status(IStatus.OK, null, "ditch infill successful", null);
		}
	}
}
