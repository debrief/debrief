/**
 * 
 */
package org.mwc.debrief.core.ContextOperations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

/**
 * @author ian.mayo
 */
public class TrimTrack implements RightClickContextItemGenerator
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
		ArrayList<TrackWrapper> tracks = null;

		// we're only going to work with one item
		for (int i = 0; i < subjects.length; i++)
		{
			Editable editable = subjects[i];
			if (editable instanceof TrackWrapper)
			{
				if (tracks == null)
					tracks = new ArrayList<TrackWrapper>();

				tracks.add((TrackWrapper) editable);
			}
		}

		// ok, is it worth going for?
		if (tracks != null)
		{
			final String title;
			if (tracks.size() == 1)
				title = "Trim track to Time Period";
			else
				title = "Trim tracks to Time Period";

			// get a final version
			final ArrayList<TrackWrapper> finalTracks = tracks;

			// right,stick in a separator
			parent.add(new Separator());

			Action trimAction = new Action(title)
			{

				@Override
				public void run()
				{
					// ok, go for it.
					// sort it out as an operation
					final IUndoableOperation doTrim = new TrimTrackOperation(title,
							theLayers, finalTracks);

					// ok, stick it on the buffer
					CorePlugin.run(doTrim);
				}
			};
			trimAction.setImageDescriptor(CorePlugin
					.getImageDescriptor("icons/clock.png"));
			parent.add(trimAction);

		}

	}

	private static class TrimTrackOperation extends CMAPOperation
	{

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;

		/**
		 * the track we're interpolating
		 */
		private final ArrayList<TrackWrapper> _tracks;

		public TrimTrackOperation(final String title, final Layers layers,
				final ArrayList<TrackWrapper> tracks)
		{
			super("Trim track(s)");
			_layers = layers;
			_tracks = tracks;
		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			// check the time controller is open
			final IViewPart part = CorePlugin.findView(CorePlugin.TIME_CONTROLLER);
			if (part == null)
			{
				CorePlugin.errorDialog("Trim Tracks",
						"The Time Controller must be used to specify the trimmed time period.\n"
								+ "Please open the Time Controller View.");
			}
			else
			{
				// ok, try to get the time
				TimePeriod period = (TimePeriod) part.getAdapter(TimePeriod.class);

				if (period != null)

				{
					Iterator<TrackWrapper> iter = _tracks.iterator();
					while (iter.hasNext())
					{
						TrackWrapper thisT = iter.next();
			 		  thisT.trimTo(period);
					}

					CorePlugin
							.errorDialog(
									"Trim Tracks",
									"This has been a destructive change, data has been deleted.\nPlease use Save-As " +
									"if you don't wish to overwrite the orginal (full-extent) datafile");
				}
			}

			// sorted, do the update
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

		@Override
		public boolean canUndo()
		{
			return false;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// TODO Auto-generated method stub
			return null;
		}

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}

		public final void testInterpolate()
		{
			final Layers theLayers = new Layers();
			final ArrayList<TrackWrapper> tracks = new ArrayList<TrackWrapper>();
			TrackWrapper track = new TrackWrapper();
			track.setName("Trk");
			theLayers.addThisLayer(track);
			tracks.add(track);

			for (int i = 0; i < 3; i++)
			{
				final WorldLocation thisLoc = new WorldLocation(0, i, 0, 'N', 0, 0, 0,
						'W', 0);
				final Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(0);
				cal.set(2005, 6, 6, 12, i * 5, 0);
				final Fix newFix = new Fix(new HiResDate(cal.getTime()), thisLoc, 0, 0);

				final FixWrapper sw = new FixWrapper(newFix);
				track.add(sw);
			}

			// ok, now do the interpolation
			final TrimTrackOperation ct = new TrimTrackOperation("convert it",
					theLayers, tracks);

			// check we're starting with the right number of items
			assertEquals("starting with right number", 3, track.numFixes());

			try
			{
				ct.execute(null, null);
			}
			catch (final ExecutionException e)
			{
				fail("Exception thrown");
			}

			// check we've got the right number of fixes
			assertEquals("right num of fixes generated", track.numFixes(), 11);

		}
	}
}
