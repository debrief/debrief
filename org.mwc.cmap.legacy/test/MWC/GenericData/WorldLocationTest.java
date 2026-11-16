package MWC.GenericData;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GenericData.WorldLocation.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import MWC.GenericData.WorldLocation.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.math.BigDecimal;
import MWC.Algorithms.Conversions;
import MWC.Algorithms.EarthModels.CompletelyFlatEarth;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class WorldLocationTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";
	private WorldLocation w1;
	private WorldLocation w2;
	private WorldVector wv1;
	private WorldLocation w4;
	private WorldLocation w5;


	public final void setSubtract() {
		final WorldVector wvv = w4.subtract(w1);
		assertTrue(wvv.equals(wv1));
	}

	@BeforeEach
	public final void setUp() {
		// set the earth model we are expecting
		MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.FlatEarth());

		w1 = new WorldLocation(12.3, 12.4, 12.5);
		w2 = new WorldLocation(12.3, 12.4, 12.5);
		wv1 = new WorldVector(0, 1, 0);
		w4 = new WorldLocation(13.3, 12.4, 12.5);
		w5 = new WorldLocation(12.225, 12.275, 12.5);
	}

	public final void setXXX() {
		final double dep = w4.getDepth();
		final double dLat = w4.getLat();
		final double dLong = w4.getLong();
		w1.setDepth(dep);
		w1.setLat(dLat);
		w1.setLong(dLong);
		assertTrue(w1.equals(w4));
	}

	@AfterEach
	public final void tearDown() {
		w1 = null;
		w2 = null;
		wv1 = null;
		w4 = null;
		w5 = null;
	}

	@Test
	public final void testAdd() {
		final WorldLocation ww = w1.add(wv1);
		assertTrue(w4.equals(ww));
	}

	@Test
	public final void testAddToMe() {
		w1.addToMe(wv1);
		assertTrue(w1.equals(w4));
	}

	@Test
	public final void testBearingFrom() {
		final double brg = w4.bearingFrom(w1);
		assertTrue(brg == 0d);
	}

	@Test
	public final void testConstructor() {
		final WorldLocation v1 = new WorldLocation(12.225, 12.275, 12.5);
		final WorldLocation v2 = new WorldLocation(12, 13.5, 0, 'N', 12, 16.5, 0, 'E', 12.5);
		final WorldLocation v3 = new WorldLocation(12, 13, 30, 'N', 12, 16, 30, 'E', 12.5);
		final WorldLocation v4 = new WorldLocation(w5);

		assertTrue(v1.equals(w5), "v1 (d,d,d)");
		assertTrue(v2.equals(w5), "v2 (i,d,c,i,d,c,d)");
		assertTrue(v3.equals(w5), "v3 (i,i,d,c,i,i,d,c,d)");
		assertTrue(v4.equals(w5), "v4 (WorldLoc)");
	}

	@Test
	public final void testCopy() {
		final WorldLocation w3 = new WorldLocation(0, 0, 0);
		w3.copy(w2);
		assertTrue(w2.equals(w3));
	}

	@Test
	public void testDecimalConstructor() {
		// test the secs component
		WorldLocation worldLoc = new WorldLocation(22, 0, 45, 'N', 22, 0, 1.45, 'W', 0);
		assertEquals(22.0125, worldLoc.getLat(), 0.001, "right lat");
		assertEquals(-22.000402, worldLoc.getLong(), 0.00001, "right long");

		// now the mins component
		worldLoc = new WorldLocation(22, 0.5, 00, 'N', 14, 0.5, 0, 'W', 0);
		assertEquals(22.008, worldLoc.getLat(), 0.01, "right lat");
		assertEquals(-14.008333, worldLoc.getLong(), 0.00001, "right long");

		// now the degs component
		worldLoc = new WorldLocation(22.5, 0, 00, 'N', 14.5, 0, 0, 'W', 0);
		assertEquals(22.5, worldLoc.getLat(), 0.01, "right lat");
		assertEquals(-14.5, worldLoc.getLong(), 0.00001, "right long");

		//
		// NOW LET'S REVERSE THE HEMISPHERE
		// test the secs component
		worldLoc = new WorldLocation(22, 0, 45, 'S', 22, 0, 1.45, 'E', 0);
		assertEquals(-22.0125, worldLoc.getLat(), 0.001, "right lat");
		assertEquals(22.000402, worldLoc.getLong(), 0.00001, "right long");

		// now the mins component
		worldLoc = new WorldLocation(22, 0.5, 00, 'S', 14, 0.5, 0, 'E', 0);
		assertEquals(-22.008, worldLoc.getLat(), 0.01, "right lat");
		assertEquals(14.008333, worldLoc.getLong(), 0.00001, "right long");

		// now the degs component
		worldLoc = new WorldLocation(22.5, 0, 00, 'S', 14.5, 0, 0, 'E', 0);
		assertEquals(-22.5, worldLoc.getLat(), 0.01, "right lat");
		assertEquals(14.5, worldLoc.getLong(), 0.00001, "right long");

	}

	@Test
	public final void testEquals() {
		assertTrue(w1.equals(w2));
		assertTrue(!w1.equals(w5));
	}

	@Test
	public final void testGetDepth() {
		assertEquals(w1.getDepth(), 12.5, 0d);
	}

	@Test
	public final void testGetLat() {
		assertEquals(w1.getLat(), 12.3, 0d);
	}

	@Test
	public final void testGetLong() {
		assertEquals(w1.getLong(), 12.4, 0d);
	}

	@Test
	public final void testPerpDistanceFrom() {
		WorldLocation.setModel(new CompletelyFlatEarth());

		final WorldLocation me = new WorldLocation(7, 0, 0);
		final WorldLocation p1 = new WorldLocation(4, 4, 0);
		WorldLocation p2 = new WorldLocation(12, 4, 0);

		WorldDistance res = me.perpendicularDistanceBetween(p1, p2);
		assertEquals(4.0, res.getValueIn(WorldDistance.DEGS), 0.001, "off-track error is correct");

		p2 = new WorldLocation(9, 2, 0);
		res = me.perpendicularDistanceBetween(p1, p2);
		assertEquals(2.5997, res.getValueIn(WorldDistance.DEGS), 0.001, "off-track error is correct");

		p2 = new WorldLocation(-4, -4, 0);
		res = me.perpendicularDistanceBetween(p1, p2);
		assertEquals(4.9497, res.getValueIn(WorldDistance.DEGS), 0.001, "off-track error is correct");

		res = me.rangeFrom(p1, p2);
		assertEquals(4.9497, res.getValueIn(WorldDistance.DEGS),
				0.001, "off-track error is correct (using range from operator)");

	}

	@Test
	public final void testPerpDistanceAtHighLatitude() {
		// use the default (locally flat) earth model
		WorldLocation.setModel(new MWC.Algorithms.EarthModels.FlatEarth());

		// N-S leg at 0 deg long, point 1 deg of longitude east of it, at 60N
		final WorldLocation me = new WorldLocation(60, 1, 0);
		final WorldLocation p1 = new WorldLocation(59, 0, 0);
		final WorldLocation p2 = new WorldLocation(61, 0, 0);

		final double expected = me.rangeFrom(new WorldLocation(60, 0, 0));
		assertEquals(30d, Conversions.Degs2Nm(expected), 0.01, "check expected is 30nm");

		final WorldDistance res = me.perpendicularDistanceBetween(p1, p2);
		assertEquals(30d, res.getValueIn(WorldDistance.NM), 0.1, "cross track honours cos(lat)");

		// and nearest point beyond the end of the segment
		final WorldLocation p3 = new WorldLocation(60, -2, 0);
		final WorldLocation p4 = new WorldLocation(60, 0, 0);
		final WorldDistance res2 = me.perpendicularDistanceBetween(p3, p4);
		assertEquals(30d, res2.getValueIn(WorldDistance.NM), 0.01, "off end of line");

		// and across the date line
		final WorldLocation dl = new WorldLocation(60, -179.5, 0);
		final WorldLocation d1 = new WorldLocation(59, 179.5, 0);
		final WorldLocation d2 = new WorldLocation(61, 179.5, 0);
		assertEquals(30d, dl.perpendicularDistanceBetween(d1, d2).getValueIn(WorldDistance.NM), 0.1,
				"across date line");
	}

	@Test
	public final void testRangeFrom() {
		final double rng = w4.rangeFrom(w1);
		assertEquals(rng, 1.0, 0.0001d);
	}

}
