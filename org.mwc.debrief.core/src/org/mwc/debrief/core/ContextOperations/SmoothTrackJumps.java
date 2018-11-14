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
package org.mwc.debrief.core.ContextOperations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import junit.framework.TestCase;

/**
 * @author ian.mayo
 */
public class SmoothTrackJumps implements RightClickContextItemGenerator
{
  public static class TestMe extends TestCase
  {
    public void testFindLegs()
    {
      fail("failed");
    }
    
    private static boolean isOnTrackDegs(final double existingDegs, final double newDegs)
    {
      return isOnTrack(MWC.Algorithms.Conversions.Degs2Rads(existingDegs), MWC.Algorithms.Conversions.Degs2Rads(newDegs));
    }
    
    public static void testOnTrack()
    {
      assertTrue("is on track", isOnTrackDegs(90, 90));
      assertTrue("is on track", isOnTrackDegs(90, 1));
      assertTrue("is on track", isOnTrackDegs(90, 179));

      assertFalse("not on track", isOnTrackDegs(90, 359));
      assertFalse("not on track", isOnTrackDegs(90, 270));
      assertFalse("not on track", isOnTrackDegs(90, 181));
      assertFalse("not on track", isOnTrackDegs(90, -90));
      assertFalse("not on track", isOnTrackDegs(90, -1));
      
      assertTrue("is on track", isOnTrackDegs(-90, -90));
      assertTrue("is on track", isOnTrackDegs(-90, 270));
      assertTrue("is on track", isOnTrackDegs(-90, -1));
      assertTrue("is on track", isOnTrackDegs(-90, -179));
      assertTrue("is on track", isOnTrackDegs(-90, 181));
      assertTrue("is on track", isOnTrackDegs(-90, 359));

      assertFalse("is on track", isOnTrackDegs(-90, 90));
      assertFalse("is on track", isOnTrackDegs(-90, -270));
      assertFalse("is on track", isOnTrackDegs(-90, 1));
      assertFalse("is on track", isOnTrackDegs(-90, 179));
      assertFalse("is on track", isOnTrackDegs(-90, -181));
      assertFalse("is on track", isOnTrackDegs(-90, -359));

      assertTrue("is on track", isOnTrackDegs(0, 89));
      assertTrue("is on track", isOnTrackDegs(0, 1));
      assertTrue("is on track", isOnTrackDegs(0, -89));
      assertTrue("is on track", isOnTrackDegs(0, -1));
      assertTrue("is on track", isOnTrackDegs(0, 0));

      assertFalse("not on track", isOnTrackDegs(0, 159));
      assertFalse("not on track", isOnTrackDegs(0, 260));
      assertFalse("not on track", isOnTrackDegs(0, 181));
      assertFalse("not on track", isOnTrackDegs(0, -91));
      assertFalse("not on track", isOnTrackDegs(0, -260));

      assertTrue("is on track", isOnTrackDegs(180, 159));
      assertTrue("is on track", isOnTrackDegs(180, 260));
      assertTrue("is on track", isOnTrackDegs(180, 181));
      assertTrue("is on track", isOnTrackDegs(180, -91));
      assertTrue("is on track", isOnTrackDegs(-180, 181));
      assertTrue("is on track", isOnTrackDegs(-180, -91));
      assertTrue("is on track", isOnTrackDegs(180, -260));

      assertFalse("not on track", isOnTrackDegs(180, 89));
      assertFalse("not on track", isOnTrackDegs(180, 1));
      assertFalse("not on track", isOnTrackDegs(180, -89));
      assertFalse("not on track", isOnTrackDegs(180, -1));
      assertFalse("not on track", isOnTrackDegs(180, 0));
    }
  }

	/**
	 * @param parent
	 *          menu
	 * @param theLayers
	 *          the whole layers
	 * @param parentLayers
	 *          the parent layers for the selected items
	 * @param subjects
	 *          the selected items
	 */
	public void generate(final IMenuManager parent, final Layers theLayers,
			final Layer[] parentLayers, final Editable[] subjects)
	{
		Collection<Editable> points = null;
		String tmpTitle = null;
		TrackWrapper track = null;
		
		// check something was selected
		if(subjects.length == 0)
			return;

		// how many items are selected?
		if (subjects.length == 1)
		{
			// is it a track?
			final Editable thisE = subjects[0];
			if (thisE instanceof TrackSegment)
			{
				TrackSegment ts = (TrackSegment) thisE;
				points = ts.getData();
				tmpTitle = "Smooth back-tracking jumps for selected track";
				
        if (ts.getWrapper() instanceof TrackWrapper)
        {
          track = ts.getWrapper();
        }
			}
			else if(thisE instanceof TrackWrapper)
			{
			  TrackWrapper tw = (TrackWrapper) thisE;
			  SegmentList segs = tw.getSegments();
			  if(segs.size() == 1)
			  {
			    TrackSegment seg = (TrackSegment) segs.elements().nextElement();
			    points = seg.getData();
			    track = tw;
	        tmpTitle = "Smooth back-tracking jumps for selected track";
			  }
			}
		}
		else
		{
			// see if it's a series of points
			final Editable thisE = subjects[0];
			if (thisE instanceof FixWrapper)
			{
				// collate the points into a collection
				points = new Vector<Editable>();

				// loop through the data
				for (int i = 0; i < subjects.length; i++)
				{
					Editable editable = subjects[i];
					
					if(track == null)
					{
					  FixWrapper fix = (FixWrapper) editable;
		        if (fix.getTrackWrapper() instanceof TrackWrapper)
		        {
		          track = (TrackWrapper) fix.getTrackWrapper();
		        }
					}
					
					points.add(editable);
				}

				tmpTitle = "Smooth back-tracking jumps in selected positions";
				
			}
		}

		// ok, is it worth going for?
		if (points != null && track != null)
		{
			final String title = tmpTitle;
			final Collection<Editable> permPoints = points;
			
			final TrackWrapper lTrack = track;

			// right,stick in a separator
			parent.add(new Separator());

			// and the new drop-down list of interpolation frequencies

			// yes, create the action
			final Action convertToTrack = new Action(title)
			{
				public void run()
				{
					// ok, go for it.
					// sort it out as an operation
					final IUndoableOperation removeJumps = new SmoothJumps(title,
							theLayers, lTrack, permPoints);

					// ok, stick it on the buffer
					CorePlugin.run(removeJumps);
				}

			};
			parent.add(convertToTrack);
		}

	}
	
	private static boolean isOnTrack(final double existingRads, final double newRads)
	{
	  double delta = Math.abs(newRads - existingRads);
	  
	  // we're having troubles where large numbers aren't recognised as multiple circles.
	  delta += 0.00001;
	  
	  while(delta > Math.PI)
	  {
	    delta -= Math.PI * 2;
	  }

	  return Math.abs(delta) <= Math.PI / 2;
	}

	/** find the legs in the block of data
	 * 
	 * @param points
	 * @return
	 */
	static HashMap<FixWrapper, WorldLocation> getLegs(final Collection<Editable> points)
	{
		// prepare to store the legs
	  final HashMap<FixWrapper, WorldLocation> res = new HashMap<FixWrapper, WorldLocation>();

		// get the points
		final Iterator<Editable> iter = points.iterator();

		// ok, here's the logic.  We establish the direction of travel from n-3 to n-2.
		// if n-1 doesnt' follow that general trend, but n does, then
		// n-1 is replaced by it's interpolated equivalent.

		// remember the previous position (which we use to "plot on" the last point)
    FixWrapper n_minus_3 = null;
		FixWrapper n_minus_2 = null;
		FixWrapper n_minus_1 = null;

		while (iter.hasNext())
		{
			final FixWrapper subject = (FixWrapper) iter.next();

			// do we have all our positions?
			if (n_minus_3 != null)
			{
			  // establish the direction for first two
			  final double coreVector = n_minus_2.getLocation().bearingFrom(n_minus_3.getLocation());
			  
			  // and the direction for next two
			  final double testVector = n_minus_1.getLocation().bearingFrom(n_minus_2.getLocation());
			  
			  // ok, does it appear to back-track?
			  if(!isOnTrack(coreVector, testVector))
			  {
			    // ok, compare with the next leg
	        final double nextVector = subject.getLocation().bearingFrom(n_minus_2.getLocation());
	        
	        // do the test for if it's inline
	        final boolean inline = isOnTrack(coreVector, nextVector);
	        
	        if(inline)
	        {
	          // ok, we have to generate the interpolated fix location for the n_minus_1
	          FixWrapper newF = FixWrapper.interpolateFix(n_minus_2, subject, n_minus_1.getDateTimeGroup());
	          
	          res.put(n_minus_1, new WorldLocation(newF.getLocation()));
	        }
			  }

			}
			
			// ok, now move along the bed
      n_minus_3 = n_minus_2;
			n_minus_2 = n_minus_1;
			n_minus_1 = subject;
		}

		return res;
	}

	/**
	 * which leg is this position in?
	 * 
	 * @param thisP
	 *          the position
	 * @param legs
	 *          the lsit of legs
	 * @return
	 */
	static Leg findLegFor(FixWrapper thisP, ArrayList<Leg> legs)
	{
		HiResDate theTime = thisP.getTime();

		Leg res = null;

		// loop through the legs
		for (Iterator<Leg> iterator = legs.iterator(); iterator.hasNext();)
		{
			Leg leg = (Leg) iterator.next();

			if (leg.contains(theTime.getDate().getTime()))
			{
				res = leg;
				break;
			}
		}
		return res;
	}

	protected void showMessage(String title, String txt)
	{
		CorePlugin.showMessage(title, txt);
	}

	static void applyOffsets(ArrayList<Leg> legs, Collection<Editable> points,
			HashMap<FixWrapper, WorldLocation> fixes)
	{
		// now pass through again, applying the offset
		Iterator<Editable> iter = points.iterator();
		while (iter.hasNext())
		{
			FixWrapper thisP = (FixWrapper) iter.next();

			// find the correct leg
			Leg relevantLeg = findLegFor(thisP, legs);

			// is it in a leg?
			if (relevantLeg != null)
			{
				// ok, find the offset vector
				WorldVector offset = relevantLeg.offsetFor(thisP.getTime().getDate()
						.getTime());

				if (offset != null)
				{
					// store the old existing location
					fixes.put(thisP, thisP.getLocation());

					// and apply the new offset
					thisP.setLocation(thisP.getLocation().add(offset));
				}
			}
		}
	}

	class SmoothJumps extends CMAPOperation
	{

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;

		/**
		 * list of new fixes we're creating
		 */
		private Collection<Editable> _points;

		/**
		 * the track we're interpolating
		 */
		private final TrackWrapper _track;

		private HashMap<FixWrapper, WorldLocation> _newFixes;

		public SmoothJumps(String title, Layers theLayers, TrackWrapper lTrack,
				Collection<Editable> points)
		{
			super(title);
			_layers = theLayers;
			_track = lTrack;
			_points = points;
		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
		  // fina what needs fixing
			_newFixes = getLegs(_points);

			// did we find any?
			if (_newFixes == null || _newFixes.size() == 0)
			{
				showMessage("Smooth jumps",
						"No back-tracking jumps were detected in the track segment\n\n"
								+ "A jump is detected when n+2 doesn't loosely follow the direction\n"
								+ "from n to n+1, but n+3 remains on that route.");
				
				// ok, return cancel - since this isn't an operation that we can undo
				return Status.CANCEL_STATUS;
			}
			else
			{
				applyFixes(_newFixes);

				// sorted, do the update
				if (_layers != null)
					_layers.fireModified(_track);
				return Status.OK_STATUS;
			}
		}

    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      applyFixes(_newFixes);
      if (_layers != null)
        _layers.fireModified(_track);

      return Status.OK_STATUS;
    }

		
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
		  applyFixes(_newFixes);
      if (_layers != null)
        _layers.fireModified(_track);

			return Status.OK_STATUS;
		}
	}

	/**
	 * store the limits of a leg
	 * 
	 * @author ian
	 * 
	 */
	static class Leg
	{
		/**
		 * the start of the leg
		 * 
		 */
		final long startTime;

		/**
		 * the end of the leg
		 * 
		 */
		final long endTime;

		/**
		 * the offset from where the last point should be, to where it is measured
		 * as
		 * 
		 */
		final WorldVector offset;

		/**
		 * define a leg, used for removing jumps
		 * 
		 * @param startP
		 *          the start-point (presumed to be a GPS fix)
		 * @param prevP
		 *          the point immediately before the jump point
		 * @param jumpP
		 *          the point before the jump
		 * @param lockP
		 *          the point after the jump (presumed to be a GPS fix)
		 */
		public Leg(FixWrapper startP, FixWrapper prevP, FixWrapper jumpP,
				FixWrapper lockP)
		{
			startTime = startP.getTime().getDate().getTime();
			endTime = lockP.getTime().getDate().getTime();
			long jumpTime = jumpP.getTime().getDate().getTime();
			long prevTime = prevP.getTime().getDate().getTime();

			// ok, calculate the offset to the previous location
			WorldVector lastStep = jumpP.getLocation().subtract(prevP.getLocation());

			// now calculate the proportional time step to get the new location
			double nextStepProp = (endTime - jumpTime)
					/ (double) (jumpTime - prevTime);

			WorldVector newOffset = new WorldVector(lastStep.getBearing(),
					nextStepProp * lastStep.getRange(), 0);

			offset = lockP.getLocation().subtract(jumpP.getLocation().add(newOffset));
		}

		/**
		 * is this time in our time period. Note, we deliberately exclude the first
		 * & last times, since those positions don't have an offset applied
		 * 
		 * @param theTime
		 *          the time we're testing against
		 * @return yes/no
		 */
		public boolean contains(long theTime)
		{
			return ((theTime > startTime) && (theTime < endTime));
		}

		/**
		 * calculate the offset to apply to the supplied position
		 * 
		 * @param time
		 * @return
		 */
		public WorldVector offsetFor(final long time)
		{
			WorldVector res = null;

			// just check this isn't the start or end time
			if ((time != startTime) && (time != endTime))
			{
				// ok, how far along time period are we
				double tDelta = time - startTime;
				double proportion = tDelta / (endTime - startTime);
				double newDistance = offset.getRange() * proportion;

				// generate the offset
				res = new WorldVector(offset.getBearing(), newDistance, 0);
			}

			return res;
		}
	}

  private static void applyFixes(final HashMap<FixWrapper, WorldLocation> newFixes)
  {
    for(final FixWrapper fix: newFixes.keySet())
    {
      // what's the new location?
      final WorldLocation newLoc = new WorldLocation(newFixes.get(fix));
      
      // what's the current location
      final WorldLocation oldLoc = new WorldLocation(fix.getLocation());
      
      // store the new location
      fix.setLocation(newLoc);
      
      // and remember the old value
      newFixes.put(fix, oldLoc);
      
    }
  }

}
