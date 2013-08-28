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
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class MergeTracks implements RightClickContextItemGenerator
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
				if (thisE instanceof TrackWrapper)
				{
					goForIt = true;
				}
				else if (thisE instanceof TrackSegment)
				{
					goForIt = true;
				}

				if (goForIt)
				{
					validItems++;
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

			final Editable editable = subjects[0];
			final String title = "Merge tracks into " + editable.getName();
			// create this operation
			final Action doMerge = new Action(title)
			{
				public void run()
				{
					final IUndoableOperation theAction = new MergeTracksOperation(title,
							editable, theLayers, parentLayers, subjects);

					CorePlugin.run(theAction);
				}
			};
			parent.add(doMerge);
		}
		else
		{
			// aah, see if this a single-segment leg
			if (subjects.length == 1)
			{
				final Editable item = subjects[0];

				CoreTMASegment seg = null;

				// is it a track?
				if (item instanceof TrackWrapper)
				{
					final TrackWrapper tw = (TrackWrapper) item;
					final SegmentList segs = tw.getSegments();
					if (segs.size() == 1)
					{
						final TrackSegment thisSeg = (TrackSegment) segs.first();
						if (thisSeg instanceof CoreTMASegment)
						{
							seg = (CoreTMASegment) thisSeg;
						}
					}
				}
				else if (item instanceof CoreTMASegment)
				{
					seg = (CoreTMASegment) item;
				}

				if (seg != null)
				{
					// right,stick in a separator
					parent.add(new Separator());

					final String title = "Convert " + seg.getName() + " into standalone track";
					final CoreTMASegment target = seg;
					// create this operation
					final Action doMerge = new Action(title)
					{
						public void run()
						{
							final IUndoableOperation theAction = new ConvertTrackOperation(title,
									target, theLayers);

							CorePlugin.run(theAction);
						}
					};
					parent.add(doMerge);

				}

			}

		}
	}

	private static class MergeTracksOperation extends CMAPOperation
	{

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final Layer[] _parents;
		private final Editable[] _subjects;
		private final Editable _target;

		public MergeTracksOperation(final String title, final Editable editable,
				final Layers theLayers, final Layer[] parentLayers, final Editable[] subjects)
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
			final int res = TrackWrapper.mergeTracks(_target, _layers, _parents, _subjects);
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
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			CorePlugin.logError(Status.INFO,
					"Undo not permitted for merge operation", null);
			return null;
		}
	}

	private static class ConvertTrackOperation extends CMAPOperation
	{

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final CoreTMASegment _target;

		public ConvertTrackOperation(final String title, final CoreTMASegment segment,
				final Layers theLayers)
		{
			super(title);
			_target = segment;
			_layers = theLayers;
		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			// create a non-TMA track
			final TrackSegment newSegment = new TrackSegment(_target);

			// now do some fancy footwork to remove the target from the wrapper,
			// and
			// replace it with our new segment
			newSegment.getWrapper().removeElement(_target);
			newSegment.getWrapper().add(newSegment);
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
}
