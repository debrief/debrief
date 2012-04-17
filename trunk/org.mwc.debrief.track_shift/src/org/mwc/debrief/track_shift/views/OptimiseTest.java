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
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.Doublet;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;

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
			TreeSet<Doublet> doublets = StackedDotHelper.getDoublets(pri, sec, true,
					true, false);

			assertNotNull("found some", doublets);
			assertEquals("right num", 18, doublets.size());
			
			
			// Create instance of Minimisation
			Minimisation min = new Minimisation();
			MinimisationFunction funct = new TryOffsetFunction(doublets);

			// initial estimates
			double[] start =
			{ 0, 0 };

			// initial step sizes
			double[] step =
			{20, 400 };

			// convergence tolerance
			double ftol = 1e-8;

			// set the min/max bearing
			min.addConstraint(0, -1, 0d);
			min.addConstraint(0, 1, 360d);
			
			// set the min/max ranges
			min.addConstraint(1, -1, 0d);
			min.addConstraint(1, 1, 6000d);

			// Nelder and Mead minimisation procedure
			min.nelderMead(funct, start, step, ftol, 500);

			// get the results out
			double[] param = min.getParamValues();

			double bearing = param[0];
			double range = param[1];
			
			System.err.println("answer is:" + bearing + " degs" + range + "m");
		}
		
		public void testPermutations()
		{
			
			SensorWrapper sensor = new SensorWrapper("nane");
			SensorContactWrapper sensorCut = new SensorContactWrapper("name", null,null, 100d,  new WorldLocation(1d,1d,1d), null, null,1, null);
			sensor.add(sensorCut);
			TrackSegment parent = null;
			FixWrapper hostFix = null;
			WorldLocation theLoc = new WorldLocation(0, 0, 0);
			Fix newFix = new Fix(new HiResDate(1000), theLoc, 0, 0);
			FixWrapper targetFix = new FixWrapper(newFix);
			Doublet dt = new Doublet(sensorCut, targetFix, parent, hostFix);

			final TreeSet<Doublet> doublets = new TreeSet<Doublet>();
			doublets.add(dt);
			
			// Create instance of Minimisation
			Minimisation min = new Minimisation();
			MinimisationFunction funct = new TryOffsetFunction(doublets);

			// initial estimates
			double[] start =
			{ 0, 0 };

			// initial step sizes
			double[] step =
			{20, 400 };

			// convergence tolerance
			double ftol = 1e-8;

			// set the min/max bearing
			min.addConstraint(0, -1, 0d);
			min.addConstraint(0, 1, 360d);
			
			// set the min/max ranges
			min.addConstraint(1, -1, 0d);
			min.addConstraint(1, 1, 6000d);

			// Nelder and Mead minimisation procedure
			min.nelderMead(funct, start, step, ftol, 500);

			// get the results out
			double[] param = min.getParamValues();

			double bearing = param[0];
			double range = param[1];
			
			System.err.println("answer is:" + bearing + " degs" + range + "m");
			
	//		assertEquals("wrong bearing", Math.PI/2, bearing / 180 *  Math.PI, 0.001 );
	//		assertEquals("wrong range", 0.001, MWC.Algorithms.Conversions.Degs2m( range), 0.001 );
		}
		
		public void testDummyPermutations()
		{
			// Create instance of Minimisation
			Minimisation min = new Minimisation();
			MinimisationFunction funct = new DummyOffsetFunction(100, 2000);

			// initial estimates
			double[] start =
			{ 0, 0 };

			// initial step sizes
			double[] step =
			{20, 400 };

			// convergence tolerance
			double ftol = 1e-8;

			// set the min/max bearing
			min.addConstraint(0, -1, 0d);
			min.addConstraint(0, 1, 360d);
			
			// set the min/max ranges
			min.addConstraint(1, -1, 0d);
			min.addConstraint(1, 1, 6000d);

			// Nelder and Mead minimisation procedure
			min.nelderMead(funct, start, step, ftol, 500);

			// get the results out
			double[] param = min.getParamValues();

			double bearing = param[0];
			double range = param[1];
			
			System.err.println("answer is:" + bearing + " degs" + range + "m");
			
			assertEquals("wrong bearing", 100, bearing, 0.0001 );
			assertEquals("wrong range", 2000, range, 0.0001 );
		}

		public void testShiftSingle() throws FileNotFoundException
		{
			SensorContactWrapper sensor = null;
			TrackSegment parent = null;
			FixWrapper hostFix = null;
			WorldLocation theLoc = new WorldLocation(0, 0, 0);
			Fix newFix = new Fix(new HiResDate(1000), theLoc, 0, 0);
			FixWrapper targetFix = new FixWrapper(newFix);
			Doublet dt = new Doublet(sensor, targetFix, parent, hostFix);

			final TreeSet<Doublet> doublets = new TreeSet<Doublet>();
			doublets.add(dt);

			assertEquals("right num", 1, doublets.size());
			WorldArea coverage = areaFor(doublets);
			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);
			
			TryOffsetFunction func = new TryOffsetFunction(doublets);

			WorldVector thisOffset = new WorldVector(0, 0, 0);
			TreeSet<Doublet> res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);

			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);

			// apply some kind of offset - move it 1 deg north
			thisOffset = new WorldVector(0, 1, 0);
			res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);
			assertEquals("right tl lat", 1.0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);

			// apply some kind of offset - move it 1 deg north
			thisOffset = new WorldVector(Math.PI / 2, 1, 0);
			res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);
			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 1, coverage.getTopLeft().getLong(), 0.001);
		}

		public void testShiftMultiple() throws FileNotFoundException
		{
			SensorContactWrapper sensor = null;
			TrackSegment parent = null;
			FixWrapper hostFix1 = new FixWrapper(new Fix(new HiResDate(1000), null,
					0, 0));

			WorldLocation theLoc = new WorldLocation(0, 0, 0);
			Fix newFix = new Fix(new HiResDate(1000), theLoc, 0, 0);
			FixWrapper targetFix = new FixWrapper(newFix);
			WorldLocation theLoc2 = new WorldLocation(0, 0, 0);
			Fix newFix2 = new Fix(new HiResDate(2000), theLoc2, 0, 0);
			FixWrapper targetFix2 = new FixWrapper(newFix2);
			Doublet dt = new Doublet(sensor, targetFix, parent, hostFix1);
			Doublet dt2 = new Doublet(sensor, targetFix2, parent, hostFix1);

			final TreeSet<Doublet> doublets = new TreeSet<Doublet>();
			doublets.add(dt);
			doublets.add(dt2);

			assertEquals("right num", 1, doublets.size());
			WorldArea coverage = areaFor(doublets);
			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);

			WorldVector thisOffset = new WorldVector(0, 0, 0);
			TryOffsetFunction func = new TryOffsetFunction(doublets);
			TreeSet<Doublet> res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);

			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);

			// apply some kind of offset - move it 1 deg north
			thisOffset = new WorldVector(0, 1, 0);
			res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);
			assertEquals("right tl lat", 1.0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);

			// apply some kind of offset - move it 1 deg north
			thisOffset = new WorldVector(Math.PI / 2, 1, 0);
			res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);
			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 1, coverage.getTopLeft().getLong(), 0.001);
		}

		private WorldArea areaFor(TreeSet<Doublet> doublets)
		{
			WorldArea res = null;
			Iterator<Doublet> iter = doublets.iterator();
			while (iter.hasNext())
			{
				Doublet doublet = (Doublet) iter.next();
				WorldLocation loc = doublet.getTarget().getLocation();
				if (res == null)
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

	public static class TryOffsetFunction implements MinimisationFunction
	{
		private final TreeSet<Doublet> _doublets;

		public TryOffsetFunction(TreeSet<Doublet> doublets)
		{
			_doublets = doublets;
		}

		@Override
		public double function(double[] param)
		{
			// ok, generate bearing
			double brgDegs = param[0];
			double brgRads = MWC.Algorithms.Conversions.Degs2Rads(brgDegs);
			double rngM = param[1];
			double rngDegs = MWC.Algorithms.Conversions.m2Degs(rngM);
			

			// and the world vector
			WorldVector offset = new WorldVector(
					brgRads, rngDegs, 0);
			
			// get shifting
			TreeSet<Doublet> newD = shiftDoublets( offset);
			
			// do the math
			double res = 0;
			for (Iterator<Doublet> iterator = newD.iterator(); iterator.hasNext();)
			{
				Doublet thisD = iterator.next();
				double measuredBearing = thisD.getMeasuredBearing();
				double calculatedBearing = thisD.getCalculatedBearing(null,
						null);
				final double thisError = thisD.calculateBearingError(measuredBearing,
						calculatedBearing);
				res += thisError * thisError;
			}
			// do the calc
			return res;
		}

		/** produce a new set of positions by applying an offset to ourst
		 * 
		 * @param thisOffset
		 * @return
		 */
		private TreeSet<Doublet> shiftDoublets( WorldVector thisOffset)
		{
			TreeSet<Doublet> res = new TreeSet<Doublet>();
		
			// loop through the doublets
			Iterator<Doublet> iter = _doublets.iterator();
			while (iter.hasNext())
			{
				Doublet doublet = (Doublet) iter.next();
		
				// get this fix
				FixWrapper fix = doublet.getTarget();
		
				// clone it
				FixWrapper newF = new FixWrapper(fix.getFix().makeCopy());
		
				// move it
				WorldLocation newLoc = new WorldLocation(newF.getLocation());
				newLoc.addToMe(thisOffset);
				newF.setLocation(newLoc);
		
				// create a new doublet
				Doublet newD = new Doublet(doublet.getSensorCut(), newF,
						doublet.getTargetTrack(), doublet.getHost());
		
				res.add(newD);
			}
		
			return res;
		}

	}

	public static class DummyOffsetFunction implements MinimisationFunction
	{
		
		private double _val1;
		private double _val2;

		public DummyOffsetFunction(double val1, double val2)
		{
			_val1 = val1;
			_val2 = val2;
		}

		@Override
		public double function(double[] param)
		{
			// ok, generate bearing
			double brgDegs = param[0];
			double rngM = param[1];
			
			double res = (Math.abs(_val1-brgDegs)) + Math.abs(_val2-rngM);
			
			// do the calc
			return res;
		}

	}
}
