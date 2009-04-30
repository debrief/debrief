/**
 * 
 */
package org.mwc.debrief.core.ContextOperations;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
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

import Debrief.Tools.Tote.Watchable;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.TrackWrapper_Support.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

/**
 * @author ian.mayo
 */
public class MergeTrack implements RightClickContextItemGenerator
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

		// we're only going to work with two or more items
		if (subjects.length > 1)
		{
			// are they tracks, or track segments
			for (int i = 0; i < subjects.length; i++)
			{
				Editable thisE = subjects[0];
				if (thisE instanceof TrackWrapper)
				{
					goForIt = true;
				}
				else if (thisE instanceof TrackSegment)
				{
					goForIt = true;
				}
				
				if(!goForIt)
				{
					System.err.println("not doing it, they're not compliant!");
					// may as well drop out - this item wasn't compliant
					continue;
				}
			}
		}

		// ok, is it worth going for?
		if (goForIt)
		{
			final String title = "Merge tracks";

			// right,stick in a separator
			parent.add(new Separator());

			// and the new drop-down list of interpolation frequencies
			Action doMerge = new Action(title){
				public void run()
				{
					IUndoableOperation theAction = new MergeTracksOperation(theLayers, parentLayers, subjects);
						
					CorePlugin.run(theAction );
				}};
			parent.add(doMerge);
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
//			InterpolateTrackOperation ct = new InterpolateTrackOperation("convert it", theLayers, track,
//					"1 min", 60 * 1000 * 1000);
			
			// check we're starting with the right number of items
//			assertEquals("starting with right number", 3, track.numFixes());
//			
//			
//			try
//			{
//				ct.execute(null, null);
//			}
//			catch (ExecutionException e)
//			{
//				fail("Exception thrown");
//			}
			
			 // check we've got the right number of fixes
			assertEquals("right num of fixes generated", track.numFixes(), 11);

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


		public MergeTracksOperation(Layers theLayers, Layer[] parentLayers,
				Editable[] subjects)
		{
			super("Merge tracks");
			_layers = theLayers;
			_parents = parentLayers;
			_subjects = subjects;
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			fireModified();
			System.err.println("ran merge");
			return Status.OK_STATUS;
		}

		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			fireModified();
			System.err.println("ran undo");
			return Status.OK_STATUS;
		}
		
		private void fireModified()
		{
			_layers.fireExtended();
		}
	}
}
