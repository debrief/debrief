/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.multipath2.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.multipath2.model.MultiPathModel.*;
import org.mwc.debrief.multipath2.model.MultiPathModel.*;
import java.awt.Color;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.multipath2.model.TimeDeltas.Observation;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;
import org.junit.jupiter.api.Test;

// /////////////////////////////////////////////////
// and the testing goes here
// /////////////////////////////////////////////////
public class MultiPathModelTest {
	public static class MyFunc implements MinimisationFunction {

		public MyFunc(final WatchableList primary, final WatchableList secondary, final SVP svp,
				final TimeDeltas times) {

		}

		@Override
		public double function(final double[] param) {
			// now the error function
			final double res = Math.abs(100d - param[0]);

			// done
			System.out.println("returned " + res + " from:" + param[0]);

			return res;
		}

	}

	private static TimeDeltas getDeltas() {
		final TimeDeltas deltas = new TimeDeltas();
		try {
			deltas.load(TimeDeltasTest.TEST_TIMES_FILE);
		} catch (final ParseException e) {
			e.printStackTrace();
			fail("number format problem");
		} catch (final IOException e) {
			e.printStackTrace();
			fail("file read problem");
		} catch (final DataFormatException e) {
			e.printStackTrace();
			fail("bad data problem");
		}
		return deltas;
	}

	private SVP getSVP() {
		final SVP svp = new SVP();
		try {
			svp.load(SVPTest.SVP_FILE);
		} catch (final ParseException e) {
			e.printStackTrace();
			fail("number format");
		} catch (final IOException e) {
			e.printStackTrace();
			fail("file read problem");
		} catch (final DataFormatException e) {
			e.printStackTrace();
			fail("bad data problem");
		}
		return svp;
	};

	private SVP getSVP2() {
		final SVP svp = new SVP();
		try {
			svp.load(SVPTest.SVP_FILE2);
		} catch (final ParseException e) {
			e.printStackTrace();
			fail("number format");
		} catch (final IOException e) {
			e.printStackTrace();
			fail("file read problem");
		} catch (final DataFormatException e) {
			e.printStackTrace();
			fail("bad data problem");
		}
		return svp;
	}

	// TODO FIX-TEST
	public void NtestCalc2() {
		final SVP svp = getSVP2();

		final MultiPathModel model = new MultiPathModel();

		for (int i = 1140; i < 1348; i++) {

			final double zS = 1340;
			final double zR = i;

			// now sort out the sound speeds
			final double cD = svp.getMeanSpeedBetween(zS, zR);
			final double cS = svp.getMeanSpeedBetween(0, zS);
			final double cR = svp.getMeanSpeedBetween(0, zR);

			// do the actual calculation
			final double time_delay = model.calculateDelay(1200, zR, zS, cD, cS, cR);

			System.out.println("cd:" + (int) cD + " cs:" + (int) cS + " cr:" + (int) cR + " delay at " + i + " is:"
					+ time_delay);

		}

	}

	// TODO FIX-TEST
	public void NtestMagic() {
		// load the data
		final SVP svp = new SVP();
		final TimeDeltas times = new TimeDeltas();

		try {
			svp.load(SVPTest.SVP_FILE);
			times.load(TimeDeltasTest.TEST_TIMES_FILE);
		} catch (final ParseException e) {
			e.printStackTrace();
			fail();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		} catch (final DataFormatException e) {
			e.printStackTrace();
			fail();
		}

		// ok, what else do we need?

		// Create instance of Minimisation
		final Minimisation min = new Minimisation();

		// Create instace of class holding function to be minimised
		final WatchableList primary = null;
		final WatchableList secondary = null;
		final MinimisationFunction funct = new MyFunc(primary, secondary, svp, times);

		// initial estimates
		final double[] start = { 30 };

		// initial step sizes
		final double[] step = { 5 };

		// convergence tolerance
		final double ftol = 1e-2;

		// specify the maximum depth
		double maxDepth = svp.getMaxDepth();
		min.addConstraint(0, +1, maxDepth);

		// Nelder and Mead minimisation procedure
		min.nelderMead(funct, start, step, ftol, 100);

		// get values of y and z at minimum
		double[] param = min.getParamValues();

		// Output the results to screen
		System.out.println("Minimum = " + min.getMinimum());
		System.out.println("Value of x at the minimum = " + param[0]);
		assertEquals(59, param[0], 1, "valid depth");

		// specify the shallower maximum depth
		min.removeConstraints();
		maxDepth = 40;
		min.addConstraint(0, +1, maxDepth);

		// Nelder and Mead minimisation procedure
		min.nelderMead(funct, start, step, ftol, 100);

		// get values of y and z at minimum
		param = min.getParamValues();

		// Output the results to screen
		System.out.println("Minimum = " + min.getMinimum());
		System.out.println("Value of x at the minimum = " + param[0]);

		assertEquals(40, param[0], 1, "valid depth");

	}

	// TODO FIX-TEST
	public void NtestMe() {
		final WorldLocation loc = new WorldLocation(2, 2, 30);
		final LabelWrapper primary = new LabelWrapper("Sensor", loc, Color.red);

		final TrackWrapper secondary = new TrackWrapper();

		final TimeDeltas deltas = getDeltas();

		final HiResDate start = deltas.getStartTime();
		final HiResDate end = deltas.getEndTime();
		final long interval = 10000;

		WorldLocation startLoc = loc.add(new WorldVector(Math.PI, 0.02, 0));

		for (long thisT = start.getDate().getTime(); thisT <= end.getDate().getTime()
				+ interval; thisT += interval) {
			final HiResDate thisD = new HiResDate(thisT);
			final WorldLocation newLoc = new WorldLocation(startLoc.add(new WorldVector(Math.PI * 1.5, 0.005, 0)));
			final Fix newF = new Fix(thisD, newLoc, 0, 0);
			final FixWrapper newFw = new FixWrapper(newF);
			secondary.addFix(newFw);

			startLoc = newLoc;
		}

		final SVP svp = getSVP();

		// ok, go for the calc
		final MultiPathModel model = new MultiPathModel();
		final TimeSeries calc = model.getCalculatedProfileFor(primary, secondary, svp, deltas, 44);

		assertNotNull(calc, "got some results");
		assertEquals(379, calc.getItemCount(), "correct num return");
	}

	// TODO FIX-TEST
	public void NtestNonMatchTimes() {
		final WorldLocation loc = new WorldLocation(2, 2, 30);
		final LabelWrapper primary = new LabelWrapper("Sensor", loc, Color.red);

		final TrackWrapper secondary = new TrackWrapper();

		final TimeDeltas deltas = getDeltas();

		final HiResDate start = deltas.getStartTime();
		final HiResDate end = deltas.getEndTime();
		final long interval = 10000;

		WorldLocation startLoc = loc.add(new WorldVector(Math.PI, 0.02, 0));

		for (long thisT = start.getDate().getTime() + 92000; thisT <= end.getDate().getTime()
				+ interval; thisT += interval) {
			final HiResDate thisD = new HiResDate(thisT);
			final WorldLocation newLoc = new WorldLocation(startLoc.add(new WorldVector(Math.PI * 1.5, 0.005, 0)));
			final Fix newF = new Fix(thisD, newLoc, 0, 0);
			final FixWrapper newFw = new FixWrapper(newF);
			secondary.addFix(newFw);

			startLoc = newLoc;
		}

		final SVP svp = getSVP();

		// ok, go for the calc
		final MultiPathModel model = new MultiPathModel();
		final TimeSeries calc = model.getCalculatedProfileFor(primary, secondary, svp, deltas, 44);

		assertNotNull(calc, "got some results");
		assertEquals(369, calc.getItemCount(), "correct num return");

	}

	@Test
	public void testCalc() {
		// ok, go for the calc
		final MultiPathModel model = new MultiPathModel();

		double sepM = 2549.11;
		final double zR = 30;
		final double zS = 50;
		final double cD = 1500.760;
		final double cS = 1501.628;
		final double cR = 1502.207;
		final double delay = model.calculateDelay(sepM, zR, zS, cD, cS, cR);

		assertEquals(-0.00044, delay, 0.001, "wrong calc");

		sepM = 2072.33;
		assertEquals(-0.000034, delay, 0.001, "wrong calc");

	}
}
