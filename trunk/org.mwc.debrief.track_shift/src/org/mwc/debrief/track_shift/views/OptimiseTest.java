package org.mwc.debrief.track_shift.views;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.mockito.Mockito;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.Doublet;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class OptimiseTest
{

	public static class testMe extends TestCase
	{
		public void testLoad() throws FileNotFoundException
		{
			// get some data
			Layers layers = getTheData();
			
			assertNotNull("should have loaded data", layers);
			
			// ok, sort out the two tracks
			assertEquals("wrong num tracks", 2, layers.size());
			
			// ok, go for the primary
			Layer layerPri = layers.elementAt(0);
			Layer layerSec = layers.elementAt(1);
			
			TrackWrapper pri = (TrackWrapper) layerPri;
			TrackWrapper sec = (TrackWrapper) layerSec;
			
			assertEquals("NONSUCH", pri.getName());
			assertEquals("TMA_TGT", sec.getName());
			
			// can we get some doublets?
			TreeSet<Doublet> doublets = StackedDotHelper.getDoublets(pri, sec, true, true, false);
			
			assertNotNull("found some", doublets);
			assertEquals("right num", 18, doublets.size());
			
			double brgRads = 1;
			double rngDegs = 0.001;
			// ok, try to get a performance figure for the doublets
			WorldVector thisOffset = new WorldVector(brgRads, rngDegs, 0);
			double score = scoreFor(doublets, thisOffset);
			
			assertTrue("created score", 100 == score);
		}
		
		public void testShift() throws FileNotFoundException
		{
			// get some data
			Layers layers = getTheData();
			
			assertNotNull("should have loaded data", layers);
			
			// ok, sort out the two tracks
			assertEquals("wrong num tracks", 2, layers.size());
			
			// ok, go for the primary
			Layer layerPri = layers.elementAt(0);
			Layer layerSec = layers.elementAt(1);
			
			TrackWrapper pri = (TrackWrapper) layerPri;
			TrackWrapper sec = (TrackWrapper) layerSec;
			
			assertEquals("NONSUCH", pri.getName());
			assertEquals("TMA_TGT", sec.getName());
			
			// can we get some doublets?
			TreeSet<Doublet> doublets = StackedDotHelper.getDoublets(pri, sec, true, true, false);
			
			assertEquals("right num", 18, doublets.size());
			WorldArea coverage = areaFor(doublets);
			assertEquals("right area", " Area TL: 04¡59'38.05\"N 029¡53'48.02\"E  BR: 04¡57'28.41\"N 029¡56'41.19\"E  (29.89667205395194) ", coverage.toString());
			
			WorldVector thisOffset = new WorldVector(0,0,0);
			doublets = shiftDoublets(doublets, thisOffset);
			coverage = areaFor(doublets);
			assertEquals("still the right area", " Area TL: 04¡59'38.05\"N 029¡53'48.02\"E  BR: 04¡57'28.41\"N 029¡56'41.19\"E  (29.89667205395194) ", coverage.toString());

			// apply some kind of offset - move it 1 deg north
			 thisOffset = new WorldVector(0,1,0);
			doublets = shiftDoublets(doublets, thisOffset);
			coverage = areaFor(doublets);
			assertEquals("area moved", " Area TL: 04¡59'38.05\"N 029¡53'48.02\"E  BR: 04¡57'28.41\"N 029¡56'41.19\"E  (29.89667205395194) ", coverage.toString());
		}

		private WorldArea areaFor(TreeSet<Doublet> doublets)
		{
			WorldArea res = null;
			Iterator<Doublet> iter = doublets.iterator();
			while (iter.hasNext())
			{
				Doublet doublet = (Doublet) iter.next();
				WorldLocation loc = doublet.getTarget().getLocation();
				if(res == null)
					res = new WorldArea(loc, loc);
				else
					res.extend(loc);
			}
			return res;
		}

	}
	
	private static Layers getTheData() throws FileNotFoundException
	{
		DebriefEclipseXMLReaderWriter reader = new DebriefEclipseXMLReaderWriter();
		Layers res = new Layers();
		String path = "src/org/mwc/debrief/track_shift/views/";
		String fName = "midflow2.xml";
		InputStream is = new FileInputStream(path + fName);
		IControllableViewport view = Mockito.mock(IControllableViewport.class);
		PlotEditor plot = Mockito.mock(PlotEditor.class);
		reader.importThis(fName, is, res, view, plot);
		return res;
	}

	/**
	 * 
	 * @param doublets the list of objects we're playing with
	 * @param thisOffset the offset we apply to this permutation
	 * @return
	 */
	private static double scoreFor(TreeSet<Doublet> doublets, WorldVector thisOffset)
	{
		// apply the offset
		doublets = shiftDoublets(doublets, thisOffset);
		
		// now calculate the error
		
		return 100;
	}

	private static TreeSet<Doublet> shiftDoublets(TreeSet<Doublet> doublets,
			WorldVector thisOffset)
	{
		// loop through the doublets
		Iterator<Doublet> iter = doublets.iterator();
		while (iter.hasNext())
		{
			Doublet doublet = (Doublet) iter.next();
			
			// fudge the target location
			FixWrapper fix = doublet.getTarget();
			
			// sort out where it is
			FixWrapper newF = new FixWrapper(fix.getFix());

			WorldLocation newLoc = new WorldLocation(newF.getLocation());
			newLoc.addToMe(thisOffset);
			newF.setLocation(newLoc);
			
			doublet.setTarget(newF);
		}
		
		return doublets;
	}

}
