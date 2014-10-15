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

import java.util.*;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

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
			final Editable thisE = subjects[0];
			if (thisE instanceof TrackWrapper)
			{
				goForIt = true;
			}
		}

		// ok, is it worth going for?
		if (goForIt)
		{
			final String title = "Resample position data";

			// right,stick in a separator
			parent.add(new Separator());

			// and the new drop-down list of interpolation frequencies
			final MenuManager newMenu = new MenuManager(title);
			parent.add(newMenu);

			// ok, loop through the time steps, creating an
			// action for each one
			final TimeStepPropertyEditor pe = new TimeStepPropertyEditor();
			final String[] tags = pe.getTags();
			for (int i = 0; i < tags.length; i++)
			{
				final String thisLabel = tags[i];
				pe.setAsText(thisLabel);
				final Long thisIntLong = (Long) pe.getValue();
				final long thisIntervalMillis = thisIntLong.longValue();

				// yes, create the action
				final Action convertToTrack = new Action("At " + thisLabel + " interval")
				{
					public void run()
					{
						// ok, go for it.
						// sort it out as an operation
						final IUndoableOperation convertToTrack1 = new InterpolateTrackOperation(title,
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
		private final Layers _layers;

		/**
		 * list of new fixes we're creating
		 */
		private Vector<FixWrapper> _newFixes;

		/**
		 * the track we're interpolating
		 */
		private final TrackWrapper _track;

		/**
		 * the step to interpolate against
		 */
		private final long _thisIntervalMicros;

		public InterpolateTrackOperation(final String title, final Layers layers, final TrackWrapper track,
				final String thisLabel, final long thisIntervalMicros)
		{
			super("At " + thisLabel + " interval");
			_layers = layers;
			_track = track;
			_thisIntervalMicros = thisIntervalMicros;
		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			final long startTime = _track.getStartDTG().getMicros();
			final long endTime = _track.getEndDTG().getMicros();

			// switch on track interpolation
			_track.setInterpolatePoints(true);

			for (long thisTime = (startTime + _thisIntervalMicros); thisTime < endTime; thisTime += _thisIntervalMicros)
			{
				// ok, generate the point at this interval
				if (_newFixes == null)
					_newFixes = new Vector<FixWrapper>(0, 1);

				final Watchable[] matches = _track.getNearestTo(new HiResDate(0, thisTime));
				if (matches.length > 0)
				{
					final FixWrapper interpFix = (FixWrapper) matches[0];

					// make it an normal FixWrapper, not an interpolated one
					final FixWrapper newFix = new FixWrapper(interpFix.getFix());
					
					// tidy the interpolated fix name
					newFix.resetName();

					_newFixes.add(newFix);
				}
			}

			if(_newFixes != null)
			{
				// cool, it worked. clear them all out
				// TODO:  re-instate this function if we use this class
		//		_track.clearPositions();
				
				// right, now add the fixes
				for (final Iterator<FixWrapper> iter = _newFixes.iterator(); iter.hasNext();)
				{
					final FixWrapper fix = (FixWrapper) iter.next();
					_track.add(fix);
				}
			}

			// ok, switch off interpolation
			_track.setInterpolatePoints(false);

			// sorted, do the update
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			// forget about the new tracks
			for (final Iterator<FixWrapper> iter = _newFixes.iterator(); iter.hasNext();)
			{
				final FixWrapper trk = (FixWrapper) iter.next();
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
			final Layers theLayers = new Layers();
			final TrackWrapper track = new TrackWrapper();
			track.setName("Trk");
			theLayers.addThisLayer(track);
			
			for(int i=0;i<3;i++)
			{
				final WorldLocation thisLoc = new WorldLocation(0,i,0,'N',0,0,0,'W', 0);
				final Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(0);
				cal.set(2005, 6, 6, 12, i * 5,0);			
				final Fix newFix = new Fix(new HiResDate(cal.getTime()), thisLoc, 0, 0);
			
				final FixWrapper sw = new FixWrapper(newFix);
				track.add(sw);
			}
			
			// ok, now do the interpolation
			final InterpolateTrackOperation ct = new InterpolateTrackOperation("convert it", theLayers, track,
					"1 min", 60 * 1000 * 1000);
			
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
