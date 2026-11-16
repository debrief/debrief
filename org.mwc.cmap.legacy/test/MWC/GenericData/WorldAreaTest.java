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

package MWC.GenericData;

import static MWC.GenericData.WorldArea.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.GenericData.WorldArea.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class WorldAreaTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";
	WorldLocation w1;
	WorldLocation w2;
	WorldLocation w3;
	WorldLocation w4;
	WorldLocation w5;
	WorldArea wa1;
	WorldArea wa2;
	WorldArea wa3;
	WorldArea wa4;
	private WorldArea wa5;


	@BeforeEach
	public final void setUp() {
		// set the earth model we are expecting
		MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.FlatEarth());

		w1 = new WorldLocation(12, 12, 0);
		w2 = new WorldLocation(10, 10, 100);
		w3 = new WorldLocation(9, 9, 110);
		w4 = new WorldLocation(58, 12, 0);
		w5 = new WorldLocation(62, 10, 100);
		wa1 = new WorldArea(w1, w2);
		wa2 = new WorldArea(w1, w2);
		wa3 = new WorldArea(w1, w3);
		wa4 = new WorldArea(w4, w5);
		wa5 = new WorldArea(w2, w5);
	}

	@AfterEach
	public final void tearDown() {
		w1 = null;
		w2 = null;
		w3 = null;
		wa1 = null;
		wa2 = null;
		wa3 = null;
	}

	@Test
	public final void testAreaCalcs() {
		w4 = new WorldLocation(58, 12, 0);
		w5 = new WorldLocation(62, 10, 100);
		wa4 = new WorldArea(w4, w5);
		assertTrue(wa4.getBottomRight().equals(new WorldLocation(58, 12, 0)));
		assertTrue(wa4.getTopLeft().equals(new WorldLocation(62, 10, 100)));
		assertTrue(wa4.getTopRight().equals(new WorldLocation(62, 12, 100)));
		assertTrue(wa4.getBottomLeft().equals(new WorldLocation(58, 10, 0)));
		final WorldLocation first = wa4.getCentre();
		final WorldLocation other = new WorldLocation(60, 11, 50);
		assertTrue(first.equals(other));
		assertEquals(wa4.getDepthRange(), 100, 0d, "Check depth range of area");
		assertEquals(wa4.getHeight(), 4.0, 0d, "Check height of area");
		assertEquals(wa4.getWidth(), 1.0, 0d, "Check width of area");
	}

	@Test
	public void testChangeCentre() {
		final WorldLocation wa = new WorldLocation(15, 13, 0);
		final WorldLocation wb = new WorldLocation(13, 15, 0);
		final WorldLocation wcenter = new WorldLocation(14, 14, 0);
		final WorldLocation wcenter_b = new WorldLocation(1, 1, 0);
		final WorldArea w_a = new WorldArea(wa, wb);
		// check the centre
		assertEquals(w_a.getCentre(), wcenter, "original centre is right");

		// shift it
		w_a.setCentre(wcenter_b);

		// check the centre
		assertEquals(w_a.getCentre(), wcenter_b, "new centre is right");

		// shift it
		w_a.setCentre(wcenter);

		// check the centre
		assertEquals(w_a.getCentre(), wcenter, "new centre is right");

		// and the corners
		assertEquals(wa.getLat(), w_a.getTopLeft().getLat(), 0.03, "new TL");
		assertEquals(wa.getLong(), w_a.getTopLeft().getLong(), 0.03, "new TL");
		assertEquals(wb.getLat(), w_a.getBottomRight().getLat(), 0.03, "new BR");
		assertEquals(wb.getLong(), w_a.getBottomRight().getLong(), 0.03, "new BR");

		final WorldLocation currentTL = new WorldLocation(w_a.getTopLeft());

		// go through the cycle once again
		// shift it
		w_a.setCentre(wcenter_b);

		// check the centre
		assertEquals(w_a.getCentre(), wcenter_b, "new centre is right");

		// shift it
		w_a.setCentre(wcenter);

		// check the centre
		assertEquals(w_a.getCentre(), wcenter, "new centre is right");

		// and the updated top-left
		assertEquals(currentTL, w_a.getTopLeft(), "tl same as last time");

	}

	@Test
	public final void testConstructor() {
		final WorldArea ww1 = new WorldArea(w1, w2);
		final WorldArea ww2 = new WorldArea(wa1);
		assertEquals(ww1.equals(wa1), true, "constructor worked");
		assertEquals(ww2.equals(wa1), true, "constructor worked");
	}

	@Test
	public final void testContains() {
		// assuming the area is TL: 12, 9, 110 and BR: 9, 12, 0
		//
		final WorldLocation offLeft = new WorldLocation(10, 8, 0);
		final WorldLocation offRight = new WorldLocation(10, 13, 0);
		final WorldLocation offTop = new WorldLocation(14, 9, 0);
		final WorldLocation offBottom = new WorldLocation(7, 9, 0);
		final WorldLocation tooShallow = new WorldLocation(9, 9, -5);
		final WorldLocation tooDeep = new WorldLocation(9, 9, 200);
		assertTrue(wa3.contains(w1), "w1 in wa3");
		assertTrue(wa3.contains(w2), "w2 in wa3");
		assertTrue(wa3.contains(w3), "w3 in wa3");
		assertTrue(!wa2.contains(offLeft), "offLeft outside wa2");
		assertTrue(!wa2.contains(offRight), "offRight outside wa2");
		assertTrue(!wa2.contains(offTop), "offTop outside wa2");
		assertTrue(!wa2.contains(offBottom), "offBottom outside wa2");
		assertTrue(!wa2.contains(tooShallow), "tooShallow outside wa2");
		assertTrue(!wa2.contains(tooDeep), "tooDeep outside wa2");
	}

	@Test
	public final void testDiffRangeFromEdge() {
		final WorldArea was = new WorldArea(new WorldLocation(0.5, 4.05, 0), new WorldLocation(0, 4.05, 0));
		final WorldLocation tgt = new WorldLocation(0.127, 0.5, 0);
		final double dist = was.rangeFrom(tgt);
		assertTrue(dist > 1);
	}

	@Test
	public void testDistribution() {
		final WorldArea theArea = new WorldArea(new WorldLocation(2, 2, 0), new WorldLocation(3, 3, 0));
		final WorldLocation first = theArea.getDistributedLocation(0, 10);

		assertEquals(2, first.getLat(), 0, "first point correct");
		assertEquals(2, first.getLong(), 0, "first point correct");

	}

	@Test
	public final void testEquals() {
		final WorldArea ww = new WorldArea(w2, w1);
		assertTrue(ww.equals(wa1), "Identical areas");
		final WorldArea ww2 = new WorldArea(w3, w1);
		assertTrue(!ww2.equals(wa1), "Different areas");
	}

	@Test
	public final void testExtend() {
		final WorldArea ww4 = new WorldArea(wa1);
		final WorldArea ww5 = new WorldArea(wa1);
		ww4.extend(w3);
		assertTrue(ww4.equals(wa3), "Extending using location");
		ww5.extend(wa3);
		assertTrue(ww5.equals(wa3), "Extending using area");
	}

	@Test
	public final void testExtend2() {
		final WorldArea ww4 = new WorldArea(wa1);
		final WorldArea ww5 = new WorldArea(wa1);

		assertNull(extend(null, null), "Null test OK for static extend");
		assertTrue(ww4.equals(extend(null, ww4)), "First Null for static extend.");
		assertTrue(ww4.equals(extend(ww4, null)), "Second Null for static extend.");
		assertTrue(wa3.equals(extend(ww5, wa3)), "Extending using area static extend");
	}

	@Test
	public final void testGrow() {
		final WorldLocation a1a = new WorldLocation(6, 1, 0);
		final WorldLocation a1b = new WorldLocation(4, 3, 0);
		final WorldLocation newTL = new WorldLocation(7, 0, 100);
		final WorldLocation newBR = new WorldLocation(3, 4, -100);
		final WorldArea a1 = new WorldArea(a1a, a1b);
		a1.grow(1, 100);
		assertEquals(newTL, a1.getTopLeft(), "Checking new top left");
		assertEquals(newBR, a1.getBottomRight(), "Checking new bottom right");
	}

	@Test
	public final void testMissingDepthData() {
		w4 = new WorldLocation(58, 12, 0);
		w5 = new WorldLocation(62, 10, 100);
		final WorldLocation w6 = new WorldLocation(64, 9, Double.NaN);
		wa4 = new WorldArea(w4, w5);

		// ok, try to extend it
		assertEquals(wa4.getBottomRight().getDepth(), 0d, 0.1d, "shallow depth valid");
		assertEquals(wa4.getTopLeft().getDepth(), 100d, 0.1d, "deep depth valid");

		wa4.extend(w6);

		// ok, try to extend it
		assertEquals(wa4.getBottomRight().getDepth(), 0d, 0.1d, "shallow depth valid");
		assertEquals(wa4.getTopLeft().getDepth(), 100d, 0.1d, "deep depth valid");
		assertEquals(wa4.getTopLeft().getLat(), 64d, 0.1d, "lat updated");
		assertEquals(wa4.getBottomRight().getLat(), 58, 0.1d, "lat updated");
		assertEquals(wa4.getTopLeft().getLong(), 9d, 0.1d, "long updated");
		assertEquals(wa4.getBottomRight().getLong(), 12d, 0.1d, "long updated");

	}

	@Test
	public final void testNormalise() {
		final WorldArea ww1 = new WorldArea(w1, w3);
		final WorldArea ww2 = new WorldArea(w3, w1);
		assertTrue(ww1.equals(ww2), "Checking normalise");
	}

	@Test
	public final void testOverlap() {
		final WorldLocation aa = new WorldLocation(4, 3, 0);
		final WorldLocation ab = new WorldLocation(2, 5, 0);
		final WorldLocation a1a = new WorldLocation(6, 1, 0);
		final WorldLocation a1b = new WorldLocation(4, 2.9, 0);
		final WorldLocation a2a = new WorldLocation(6, 5.1, 0);
		final WorldLocation a2b = new WorldLocation(4, 7, 0);
		final WorldLocation a3a = new WorldLocation(5, 2, 0);
		final WorldLocation a3b = new WorldLocation(3, 4, 0);
		final WorldLocation a4a = new WorldLocation(5, 4, 0);
		final WorldLocation a4b = new WorldLocation(3, 6, 0);
		final WorldLocation a5a = new WorldLocation(3.5, 3.5, 0);
		final WorldLocation a5b = new WorldLocation(4.5, 4.5, 0);
		final WorldLocation a6a = new WorldLocation(3, 2, 0);
		final WorldLocation a6b = new WorldLocation(1, 4, 0);
		final WorldLocation a7a = new WorldLocation(3, 4, 0);
		final WorldLocation a7b = new WorldLocation(1, 6, 0);
		final WorldLocation a8a = new WorldLocation(2, 1, 0);
		final WorldLocation a8b = new WorldLocation(0, 2.9, 0);
		final WorldLocation a9a = new WorldLocation(2, 5.1, 0);
		final WorldLocation a9b = new WorldLocation(0, 7, 0);

		final WorldArea a1 = new WorldArea(a1a, a1b);
		final WorldArea a2 = new WorldArea(a2a, a2b);
		final WorldArea a3 = new WorldArea(a3a, a3b);
		final WorldArea a4 = new WorldArea(a4a, a4b);
		final WorldArea a5 = new WorldArea(a5a, a5b);
		final WorldArea a6 = new WorldArea(a6a, a6b);
		final WorldArea a7 = new WorldArea(a7a, a7b);
		final WorldArea a8 = new WorldArea(a8a, a8b);
		final WorldArea a9 = new WorldArea(a9a, a9b);

		final WorldArea waa = new WorldArea(aa, ab);
		assertTrue(!waa.overlaps(a1), "a1");
		assertTrue(!waa.overlaps(a2), "a2");
		assertTrue(waa.overlaps(a3), "a3");
		assertTrue(waa.overlaps(a4), "a4");
		assertTrue(waa.overlaps(a5), "a5");
		assertTrue(waa.overlaps(a6), "a6");
		assertTrue(waa.overlaps(a7), "a7");
		assertTrue(!waa.overlaps(a8), "a8");
		assertTrue(!waa.overlaps(a9), "a9");
	}

	@Test
	public final void testRangeFrom() {
		final WorldLocation a1a = new WorldLocation(6, 1, 0);
		final WorldLocation a1b = new WorldLocation(4, 3, 0);
		final WorldLocation a2a = new WorldLocation(6, 5, 0);
		final WorldLocation a2b = new WorldLocation(4, 7, 0);
		final WorldArea a1 = new WorldArea(a1a, a1b);
		final double r1 = a1.rangeFrom(a2a);
		final double r2 = a1.rangeFrom(a2b);
		final double r3 = a2a.rangeFrom(new WorldLocation(6, 3, 0));
		final double r4 = new WorldLocation(4, 3, 0d).rangeFrom(a2b);
		assertEquals(r3, r1, 0d, "Checking range A from");
		assertEquals(r4, r2, 0d, "Checking range B from");
	}

	@Test
	public final void testRangeFrom2() {
		final WorldLocation a1a = new WorldLocation(0, 0, 0);
		final WorldLocation a1b = new WorldLocation(8, 4, 0);
		final WorldLocation a2a = new WorldLocation(0, 2, 0);
		final WorldLocation a2b = new WorldLocation(8, 2, 0);
		final WorldArea a1 = new WorldArea(a1a, a1b);
		final double r1 = a1.rangeFrom(a2a);
		final double r2 = a1.rangeFrom(a2b);
		// double r3 = a2a.rangeFrom(new WorldLocation(6, 3, 0));
		// double r4 = new WorldLocation(4, 3, 0d).rangeFrom(a2b);
		assertEquals(2, r1, 0d, "Checking range A from");
		assertEquals(2, r2, 0.1d, "Checking range B from");
	}

	@Test
	public final void testRangeFromEdge() {
		double dist1 = wa1.rangeFromEdge(new WorldLocation(13, 11, 0));
		assertEquals(1d, dist1, 0.01, "correct range");
		dist1 = wa1.rangeFromEdge(new WorldLocation(11, 11, 0));
		assertEquals(0d, dist1, 0.01, "correct range");
		dist1 = wa1.rangeFromEdge(new WorldLocation(9, 10.4, 0));
		assertEquals(1d, dist1, 0.01, "correct range");
		dist1 = wa1.rangeFromEdge(new WorldLocation(9, 11, 0));
		assertEquals(1d, dist1, 0.01, "correct range");
		dist1 = wa1.rangeFromEdge(new WorldLocation(11, 13, 0));
		assertEquals(1d, dist1, 0.02, "correct range");
		dist1 = wa1.rangeFromEdge(new WorldLocation(11.2, 13, 0));
		assertEquals(1d, dist1, 0.02, "correct range");
		dist1 = wa1.rangeFromEdge(new WorldLocation(11, 9, 0));
		assertEquals(1d, dist1, 0.02, "correct range");
		dist1 = wa5.rangeFromEdge(new WorldLocation(10, 11, 100));
		assertEquals(1d, dist1, 0.02, "correct range");

	}
}
