/**
 * 
 */
package org.mwc.debrief.core.ContextOperations;

import java.util.*;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Tools.Tote.Watchable;
import Debrief.Wrappers.*;
import MWC.GUI.*;
import MWC.GUI.Properties.TimeStepPropertyEditor;
import MWC.GenericData.*;
import MWC.TacticalData.*;

/**
 * @author ian.mayo
 */
public class InterpolateTrack implements RightClickContextItemGenerator
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

		// we're only going to work with one item
		if (subjects.length == 1)
		{
			// is it a track?
			Editable thisE = subjects[0];
			if (thisE instanceof TrackWrapper)
			{
				goForIt = true;
			}
		}

		// ok, is it worth going for?
		if (goForIt)
		{
			final String title = "Interpolate tracks";

			// right,stick in a separator
			parent.add(new Separator());

			// and the new drop-down list of interpolation frequencies
			MenuManager newMenu = new MenuManager("Interpolate tracks");
			parent.add(newMenu);

			// ok, loop through the time steps, creating an
			// action for each one
			final TimeStepPropertyEditor pe = new TimeStepPropertyEditor();
			String[] tags = pe.getTags();
			for (int i = 0; i < tags.length; i++)
			{
				final String thisLabel = tags[i];
				pe.setAsText(thisLabel);
				Long thisIntLong = (Long) pe.getValue();
				final long thisIntervalMillis = thisIntLong.longValue();

				// yes, create the action
				Action convertToTrack = new Action("At " + thisLabel + " interval")
				{
					public void run()
					{
						// ok, go for it.
						// sort it out as an operation
						IUndoableOperation convertToTrack1 = new InterpolateTrackOperation(title,
								theLayers, (TrackWrapper) subjects[0], thisLabel, thisIntervalMillis);

						// ok, stick it on the buffer
						CorePlugin.run(convertToTrack1);
					}
				};

				newMenu.add(convertToTrack);
			}
		}

	}

	private static class InterpolateTrackOperation extends CMAPOperation
	{

		/**
		 * the parent to update on completion
		 */
		private Layers _layers;

		/**
		 * list of new fixes we're creating
		 */
		private Vector<FixWrapper> _newFixes;

		/**
		 * the track we're interpolating
		 */
		private TrackWrapper _track;

		/**
		 * the step to interpolate against
		 */
		private long _thisIntervalMicros;

		public InterpolateTrackOperation(String title, Layers layers, TrackWrapper track,
				String thisLabel, long thisIntervalMicros)
		{
			super("At " + thisLabel + " interval");
			_layers = layers;
			_track = track;
			_thisIntervalMicros = thisIntervalMicros;
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			long startTime = _track.getStartDTG().getMicros();
			long endTime = _track.getEndDTG().getMicros();

			// switch on track interpolation
			_track.setInterpolatePoints(true);

			for (long thisTime = (startTime + _thisIntervalMicros); thisTime < endTime; thisTime += _thisIntervalMicros)
			{
				// ok, generate the point at this interval
				if (_newFixes == null)
					_newFixes = new Vector<FixWrapper>(0, 1);

				Watchable[] matches = _track.getNearestTo(new HiResDate(0, thisTime));
				if (matches.length > 0)
				{
					FixWrapper interpFix = (FixWrapper) matches[0];

					// make it an normal FixWrapper, not an interpolated one
					FixWrapper newFix = new FixWrapper(interpFix.getFix());
					
					// tidy the interpolated fix name
					newFix.resetName();

					_newFixes.add(newFix);
				}
			}

			if(_newFixes != null)
			{
				// right, now add the fixes
				for (Iterator<FixWrapper> iter = _newFixes.iterator(); iter.hasNext();)
				{
					FixWrapper fix = (FixWrapper) iter.next();
					_track.add(fix);
				}
			}

			// ok, switch off interpolation
			_track.setInterpolatePoints(false);

			// sorted, do the update
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// forget about the new tracks
			for (Iterator<FixWrapper> iter = _newFixes.iterator(); iter.hasNext();)
			{
				FixWrapper trk = (FixWrapper) iter.next();
				_track.removeElement(trk);
			}

			// and clear the new tracks item
			_newFixes.removeAllElements();
			_newFixes = null;

			_layers.fireModified(_track);

			return Status.OK_STATUS;
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
			Layers theLayers = new Layers();
			TrackWrapper track = new TrackWrapper();
			Track trk = new Track();
			track.setTrack(trk);
			track.setName("Trk");
			theLayers.addThisLayer(track);
			
			for(int i=0;i<3;i++)
			{
				WorldLocation thisLoc = new WorldLocation(0,i,'N',0,0,'W', 0);
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(0);
				cal.set(2005, 6, 6, 12, i * 5,0);			
				Fix newFix = new Fix(new HiResDate(cal.getTime()), thisLoc, 0, 0);
			
				FixWrapper sw = new FixWrapper(newFix);
				track.add(sw);
			}
			
			// ok, now do the interpolation
			InterpolateTrackOperation ct = new InterpolateTrackOperation("convert it", theLayers, track,
					"1 min", 60 * 1000 * 1000);
			
			// check we're starting with the right number of items
			assertEquals("starting with right number", 3, track.numFixes());
			
			
			try
			{
				ct.execute(null, null);
			}
			catch (ExecutionException e)
			{
				fail("Exception thrown");
			}
			
			 // check we've got the right number of fixes
			assertEquals("right num of fixes generated", track.numFixes(), 11);

		}
	}	
}
