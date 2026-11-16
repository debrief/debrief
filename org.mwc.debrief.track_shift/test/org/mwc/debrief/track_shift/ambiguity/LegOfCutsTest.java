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

package org.mwc.debrief.track_shift.ambiguity;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.debrief.track_shift.ambiguity.LegOfCuts.*;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.junit.jupiter.api.Test;
import Debrief.Wrappers.SensorContactWrapper;
import MWC.GenericData.HiResDate;

public class LegOfCutsTest {

	@Test
	public static void testProcessCuts() throws FileNotFoundException {
		List<WeightedObservedPoint> obs = new ArrayList<WeightedObservedPoint>();
		obs.add(new WeightedObservedPoint(1, 80d, 260d));
		obs.add(new WeightedObservedPoint(1, 90, 280d));
		obs.add(new WeightedObservedPoint(1, 100, 300d));
		obs.add(new WeightedObservedPoint(1, 110, 320d));
		obs.add(new WeightedObservedPoint(1, 120, 340d));
		obs.add(new WeightedObservedPoint(1, 130, 0d));
		obs.add(new WeightedObservedPoint(1, 140, 20d));

		List<WeightedObservedPoint> res = LegOfCuts.putObsInCorrectDomain(obs);
		assertEquals(380d, res.get(res.size() - 1).getY(), 0.001, "correct last score");

		obs = new ArrayList<WeightedObservedPoint>();
		obs.add(new WeightedObservedPoint(1, 80, 160d));
		obs.add(new WeightedObservedPoint(1, 90, 140d));
		obs.add(new WeightedObservedPoint(1, 100, 120d));
		obs.add(new WeightedObservedPoint(1, 110, 80d));
		obs.add(new WeightedObservedPoint(1, 120, 30d));
		obs.add(new WeightedObservedPoint(1, 130, 340d));
		obs.add(new WeightedObservedPoint(1, 140, 320d));

		res = LegOfCuts.putObsInCorrectDomain(obs);
		assertEquals(-40d, res.get(res.size() - 1).getY(), 0.001, "correct last score");

		obs = new ArrayList<WeightedObservedPoint>();
		obs.add(new WeightedObservedPoint(1, 80, -160d));
		obs.add(new WeightedObservedPoint(1, 90, -140d));
		obs.add(new WeightedObservedPoint(1, 100, -120d));
		obs.add(new WeightedObservedPoint(1, 110, -80d));
		obs.add(new WeightedObservedPoint(1, 120, -30d));
		obs.add(new WeightedObservedPoint(1, 130, 20d));
		obs.add(new WeightedObservedPoint(1, 140, 40d));

		res = LegOfCuts.putObsInCorrectRange(obs);
		assertEquals(200d, res.get(0).getY(), 0.001, "correct last score");
		assertEquals(40d, res.get(res.size() - 1).getY(), 0.001, "correct last score");
	}

}
