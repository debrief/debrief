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

package Debrief.Tools.Tote.Calculations;


import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Test;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;

public final class speedRateCalc extends plainCalc {
	
	static public final class testCalc extends junit.framework.TestCase {

		public void testSpeedRate_MultiTrack() {
			TrackWrapper trackA = new TrackWrapper();
			trackA.setName("Alpha");
			TrackWrapper trackB = new TrackWrapper();
			trackB.setName("bravo");
			FixWrapper fixAA =  TrackWrapper_Test.createFix4(2000, 10, 20, 30, MWC.Algorithms.Conversions.Kts2Yps(40));
			fixAA.setTrackWrapper(trackA);
			FixWrapper fixBA =  TrackWrapper_Test.createFix4(4000, 10, 20, 30, MWC.Algorithms.Conversions.Kts2Yps(30));
			fixBA.setTrackWrapper(trackA);
			FixWrapper fixCA =  TrackWrapper_Test.createFix4(2000, 10, 20, 30, MWC.Algorithms.Conversions.Kts2Yps(30));
			fixCA.setTrackWrapper(trackA);
			FixWrapper fixAB =  TrackWrapper_Test.createFix4(2000, 10, 20, 30, MWC.Algorithms.Conversions.Kts2Yps(80));
			fixAB.setTrackWrapper(trackB);
			FixWrapper fixBB =  TrackWrapper_Test.createFix4(4000, 10, 20, 30, MWC.Algorithms.Conversions.Kts2Yps(60));
			fixBB.setTrackWrapper(trackB);
			FixWrapper fixCB =  TrackWrapper_Test.createFix4(2000, 10, 20, 30, MWC.Algorithms.Conversions.Kts2Yps(40));
			fixCB.setTrackWrapper(trackB);
			speedRateCalc calc = new speedRateCalc();
			double answerA = calc.calculate(fixAA, null, new HiResDate(10000));
			assertEquals("Should not return", 0d, answerA);
			assertEquals("Should have map", calc.prevPrimary.size(), 1);
			double answerAB = calc.calculate(fixAB, null, new HiResDate(10000));
			assertEquals("Should not return", 0d, answerAB);
			assertEquals("Should have map", calc.prevPrimary.size(), 2);
			double answerB = calc.calculate(fixBA, null, new HiResDate(10000));
			assertEquals("Should return result", -5d, answerB);
			double answerBB = calc.calculate(fixBB, null, new HiResDate(10000));
			assertEquals("Should return result", -10d, answerBB);
			double answerC = calc.calculate(fixCA, null, new HiResDate(10000));
			assertEquals("Should return result", 0d, answerC);
			double answerCB = calc.calculate(fixCB, null, new HiResDate(10000));
			assertEquals("Should return result", 0d, answerCB);
		}
		
		public void testSpeedRate_SingleTrack() {
			TrackWrapper trackA = new TrackWrapper();
			trackA.setName("Alpha");
			FixWrapper fixA =  TrackWrapper_Test.createFix4(2000, 10, 20, 30, MWC.Algorithms.Conversions.Kts2Yps(40));
			fixA.setTrackWrapper(trackA);
			FixWrapper fixB =  TrackWrapper_Test.createFix4(4000, 10, 20, 30, MWC.Algorithms.Conversions.Kts2Yps(30));
			fixB.setTrackWrapper(trackA);
			FixWrapper fixC =  TrackWrapper_Test.createFix4(2000, 10, 20, 30, MWC.Algorithms.Conversions.Kts2Yps(30));
			fixC.setTrackWrapper(trackA);
			speedRateCalc calc = new speedRateCalc();
			double answerA = calc.calculate(fixA, null, new HiResDate(10000));
			assertEquals("Should not return", 0d, answerA);
			assertEquals("Should have map", calc.prevPrimary.size(), 1);
			double answerB = calc.calculate(fixB, null, new HiResDate(10000));
			assertEquals("Should return result", -5d, answerB);
			double answerC = calc.calculate(fixC, null, new HiResDate(10000));
			assertEquals("Should return result", 0d, answerC);
		}
	}
		
	public speedRateCalc() {
		super(new DecimalFormat("00.00"), "Speed Rate", "Kts/second");
	}

	Map<WatchableList, Watchable> prevPrimary = new HashMap<WatchableList, Watchable>();
	
	@Override
	public final double calculate(final Watchable primary, final Watchable secondary, final HiResDate thisTime) {
		final double res;
		if(primary instanceof FixWrapper) {
			FixWrapper fix = (FixWrapper) primary;
			WatchableList parent = fix.getTrackWrapper();
			
			// do we have a previous value?
			Watchable prev = prevPrimary.get(parent);
			
			if(prev != null) {
				System.out.println("Time:" + fix.getTime().getDate() + " Prev:" + prev.getTime().getDate());
				

				// check it's newer than us
				HiResDate time = prev.getTime();
				if(time.greaterThan(primary.getTime())) {
					// ok, we can only have jumped forward in time if we're restarting. clear the list
					prevPrimary.remove(parent);
					res = 0d;
					System.err.println("CLEARING PREVIOUS LIST");
				} else if(primary.getTime().greaterThan(prev.getTime())) {
					// ok, we can do the calculation
					double speedDelta = primary.getSpeed() - prev.getSpeed();
					long timeDelta = primary.getTime().getDate().getTime() - prev.getTime().getDate().getTime();
					res = speedDelta / (timeDelta / 1000);
				} else {
					res = 0d;
				}
			} else {
				res = 0d;
			}
			
			// and remember the value
			prevPrimary.put(parent,  primary);
		} else {
			res = 0d;
		}
		System.out.println("Res:" + res);
		return res;
	}

	/**
	 * does this calculation require special bearing handling (prevent wrapping
	 * through 360 degs)
	 *
	 */
	@Override
	public final boolean isWrappableData() {
		return false;
	}

	@Override
	public final String update(final Watchable primary, final Watchable secondary, final HiResDate time) {
		// check we have data
		if (primary == null)
			return NOT_APPLICABLE;

		return _myPattern.format(calculate(primary, secondary, time));
	}

}
