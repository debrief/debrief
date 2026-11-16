package MWC.GenericData;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GenericData.WorldPath.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import MWC.GenericData.WorldPath.*;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class WorldPathTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	private void showPath(final WorldPath path) {
		final Vector<WorldLocation> pts = path._myPoints;
		for (int i = 0; i < pts.size(); i++) {
			final WorldLocation location = pts.elementAt(i);
			System.out.println("" + location.getLong() + ", " + location.getLat() + ", " + location.getDepth());
		}
	}

	@Test
	public final void testBreakPath() {
		final WorldLocation loc1 = new WorldLocation(0, 0, 0);
		final WorldLocation loc2 = new WorldLocation(0, 2, 0);
		final WorldLocation loc3 = new WorldLocation(0, 0, 4);
		final WorldLocation loc4 = new WorldLocation(0, 0, 8);
		final WorldLocation loc5 = new WorldLocation(0, 3, 4);

		final WorldPath newPath = new WorldPath(new WorldLocation[] { loc1, loc2, loc3, loc4, loc5 });
		final WorldPath brokenDownPath = newPath.breakIntoStraightSections(2);
		assertEquals(2, brokenDownPath.size(),
				0, "broken down into wrong number of sections - is it implemented even?");

	}

	@Test
	public void testBreakSections() {
		final WorldPath thePath = new WorldPath(
				new WorldLocation[] { new WorldLocation(0, 0, 0), new WorldLocation(2, 1, 0),
						new WorldLocation(3, 4, 0), new WorldLocation(2, 5, 0), new WorldLocation(0, 6, 0) });
		final int PATH_LENGTH = 8;
		final WorldPath brokenPath = thePath.breakIntoStraightSections(PATH_LENGTH);

		// get some checks in
		assertEquals(PATH_LENGTH, brokenPath.size(), "path not of correct length");
		assertEquals(thePath._myPoints.lastElement(), brokenPath._myPoints.lastElement(),
				"doesn't finish at correct point");

		showPath(thePath);
		showPath(brokenPath);
	}

	@Test
	public void testBreakSections2() {
		final WorldPath thePath = new WorldPath(new WorldLocation[] {
				new WorldLocation.LocalLocation(new WorldDistance(0, WorldDistance.NM),
						new WorldDistance(0, WorldDistance.NM), 0),
				new WorldLocation.LocalLocation(new WorldDistance(20, WorldDistance.NM),
						new WorldDistance(25, WorldDistance.NM), 0),
				new WorldLocation.LocalLocation(new WorldDistance(40, WorldDistance.NM),
						new WorldDistance(30, WorldDistance.NM), 0),
				new WorldLocation.LocalLocation(new WorldDistance(60, WorldDistance.NM),
						new WorldDistance(40, WorldDistance.NM), 0)

		});
		final int PATH_LENGTH = 8;
		final WorldPath brokenPath = thePath.breakIntoStraightSections(PATH_LENGTH);

		// get some checks in
		assertEquals(PATH_LENGTH, brokenPath.size(), "path not of correct length");
		assertEquals(thePath._myPoints.lastElement(), brokenPath._myPoints.lastElement(),
				"doesn't finish at correct point");

		showPath(thePath);
		showPath(brokenPath);
	}

	@Test
	public final void testMovement() {
		final WorldLocation w1 = new WorldLocation(1, 1, 1);
		final WorldLocation w2 = new WorldLocation(2, 2, 2);
		final WorldLocation w3 = new WorldLocation(3, 3, 3);
		final WorldLocation w4 = new WorldLocation(4, 4, 4);
		final WorldLocation w5 = new WorldLocation(5, 5, 5);

		final WorldPath path = new WorldPath();
		path.addPoint(w1);

		// check the area
		WorldArea ar = path.getBounds();
		assertTrue(ar.equals(new WorldArea(w1, w1)), "area updated");

		path.addPoint(w2);

		assertTrue(!(ar.equals(path.getBounds())), "area changed");

		path.addPoint(w3);
		path.addPoint(w4);

		// add duff point
		path.addPoint(null);

		// check they're all there
		assertEquals(4, path._myPoints.size(), "all points loaded");

		// check the area
		ar = path.getBounds();

		assertTrue(ar.equals(new WorldArea(w1, w4)), "full area contained");
		assertEquals(w4, path.getLocationAt(3), "found fourth item");

		// move up
		path.moveUpward(w4);
		assertEquals(w3, path.getLocationAt(3), "found third in fourth slot item");

		// move invalid upwards
		path.moveUpward(w5);

		// move first upwards
		path.moveUpward(w1);
		assertEquals(w1, path.getLocationAt(0), "first item still in slot");

		// move last downwards
		path.moveDownward(w3);
		assertEquals(w3, path.getLocationAt(3), "last item still in slot");

		// move invalid downwards
		path.moveDownward(w5);

		// remove invalid
		path.remove(w5);
		assertEquals(4, path.size(), "still contains 4");

		// remove others
		path.remove(w3);
		assertEquals(3, path.size(), "dropped to 3");

		// move first upwards
		path.moveUpward(w1);
		assertEquals(w1, path.getLocationAt(0), "first item still in slot");

		// move last downwards
		path.moveDownward(w4);
		assertEquals(w4, path.getLocationAt(2), "last item still in slot");

		path.remove(w2);
		path.remove(w1);

		path.moveUpward(w4);
		path.moveDownward(w4);

		path.remove(w4);

		path.moveUpward(w4);

		ar = path.getBounds();
		assertEquals(null, ar, "empty area with no points");
	}

}
