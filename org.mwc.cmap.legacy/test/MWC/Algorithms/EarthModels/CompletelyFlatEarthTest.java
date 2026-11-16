package MWC.Algorithms.EarthModels;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.Algorithms.EarthModels.CompletelyFlatEarth.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.Algorithms.EarthModels.CompletelyFlatEarth.*;
import MWC.Algorithms.EarthModel;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class CompletelyFlatEarthTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testIt() {

		final CompletelyFlatEarth cf = new CompletelyFlatEarth();
		final FlatEarth fe = new FlatEarth();
		final WorldLocation w1 = new WorldLocation(5.1, 1, 0);
		final WorldLocation w2 = new WorldLocation(5, 0.4, 1);

		WorldLocation.setModel(cf);

		final WorldVector res = cf.subtract(w1, w2);
		final WorldVector res2 = fe.subtract(w1, w2);
		final WorldVector res3 = w2.subtract(w1);
		System.out.println("res is:" + res.toString());
		System.out.println("res2 is:" + res2.toString());
		System.out.println("res3 is:" + res3.toString());

		// try adding them back in
		final WorldLocation w4 = cf.add(w1, res);
		final WorldLocation w5 = fe.add(w1, res2);
		assertEquals(w4, w2, "Completely flat");
		assertEquals(w5.getLat(), w2.getLat(), 0.0001, "locally flat lat");
		assertEquals(w5.getLong(), w2.getLong(), 0.0001, "locally flat long");

	}

	@Test
	public void testAntimeridian() {
		final WorldLocation east = new WorldLocation(0, 179.9, 0);
		final WorldLocation west = new WorldLocation(0, -179.9, 0);

		final EarthModel[] models = new EarthModel[] { new FlatEarth(), new CompletelyFlatEarth() };
		for (final EarthModel model : models) {
			final String name = model.getClass().getSimpleName();
			// going east across the date line
			final WorldVector res = model.subtract(east, west);
			assertEquals(0.2, res.getRange(), 0.0001, name + " short range");
			assertEquals(90d, MWC.Algorithms.Conversions.Rads2Degs(res.getBearing()), 0.0001,
					name + " heading east");

			// and back west
			final WorldVector back = model.subtract(west, east);
			assertEquals(0.2, back.getRange(), 0.0001, name + " short range back");
			assertEquals(270d, MWC.Algorithms.Conversions.normaliseDegs(MWC.Algorithms.Conversions.Rads2Degs(back.getBearing())),
					0.0001,
					name + " heading west");

			// adding the vector should land us in the western hemisphere
			final WorldLocation sum = model.add(east, res);
			assertEquals(-179.9, sum.getLong(), 0.0001, name + " wrapped long");
			assertEquals(0, sum.getLat(), 0.0001, name + " lat");
		}
	}

	@Test
	public void testNotWrappedAtBoundary() {
		// a location exactly on the date line isn't wrapped
		final FlatEarth fe = new FlatEarth();
		final WorldLocation start = new WorldLocation(0, 179, 0);
		final WorldLocation res = fe.add(start, new WorldVector(Math.PI / 2, 1, 0));
		assertEquals(180, res.getLong(), 0.0001, "still at 180");
	}

}
