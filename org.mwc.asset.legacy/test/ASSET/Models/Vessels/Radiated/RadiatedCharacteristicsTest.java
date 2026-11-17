package ASSET.Models.Vessels.Radiated;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.*;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import ASSET.Models.MWCModel;
import ASSET.Models.Environment.CoreEnvironment;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

//////////////////////////////////////////////////////////////////////
// constructor
//////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////
// testing for this public class
////////////////////////////////////////////////////
public class RadiatedCharacteristicsTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new RadiatedCharacteristics("h334");
	}

	/**
	 * test the list management
	 */
	@Test
	public void testBearingSet() {
		// create a few noise components
		final NoiseComponent nc = new NoiseComponent(0, 4, 100);
		final NoiseComponent n2 = new NoiseComponent(0, 8, 200);
		final NoiseComponent n3 = new NoiseComponent(0, 16, 300);
		final NoiseComponent n4 = new NoiseComponent(0, 24, 600);

		final BearingSet bs = new BearingSet(22);
		bs.add(nc);
		bs.add(n2);
		bs.add(n3);
		bs.add(n4);

		double res = bs.valueFor(8);
		assertEquals(200, res, 0.001, "found correct value");
		res = bs.valueFor(4);
		assertEquals(100, res, 0.001, "found correct value");
		res = bs.valueFor(2);
		assertEquals(100, res, 0.001, "found correct value");
		res = bs.valueFor(16);
		assertEquals(300, res, 0.001, "found correct value");
		res = bs.valueFor(20);
		assertEquals(450, res, 0.001, "found correct value");
		res = bs.valueFor(24);
		assertEquals(600, res, 0.001, "found correct value");
		res = bs.valueFor(28);
		assertEquals(600, res, 0.001, "found correct value");
	}

	/**
	 * test the string swapping
	 */
	@Test
	public void testInterpolation() {
		final NoiseComponent nc = new NoiseComponent(0, 4, 100);
		final NoiseComponent n2 = new NoiseComponent(0, 8, 200);
		double res = BearingSet.interpolateBetween(nc, n2, 6);
		assertEquals(res, 150d, 0.001, "correct interpolated value");
		res = BearingSet.interpolateBetween(nc, n2, 7);
		assertEquals(res, 175d, 0.001, "correct interpolated value");
		res = BearingSet.interpolateBetween(nc, n2, 4);
		assertEquals(res, 100d, 0.001, "correct interpolated value");
		res = BearingSet.interpolateBetween(nc, n2, 8);
		assertEquals(res, 200d, 0.001, "correct interpolated value");
		res = BearingSet.interpolateBetween(nc, nc, 2);
		assertEquals(res, 100d, 0.001, "correct interpolated value");
		res = BearingSet.interpolateBetween(n2, n2, 10);
		assertEquals(res, 200d, 0.001, "correct interpolated value");
	}

	@Test
	public void testNoiseSignature() {
		final NoiseSignature ns = new NoiseSignature();

		// create a few noise components
		final NoiseComponent nc = new NoiseComponent(0, 4, 100);
		final NoiseComponent n2 = new NoiseComponent(0, 8, 200);
		final NoiseComponent n3 = new NoiseComponent(0, 16, 300);
		final NoiseComponent n4 = new NoiseComponent(0, 24, 500);

		final NoiseComponent pc = new NoiseComponent(90, 4, 1000);
		final NoiseComponent p2 = new NoiseComponent(90, 8, 2000);
		final NoiseComponent p3 = new NoiseComponent(90, 16, 3000);
		final NoiseComponent p4 = new NoiseComponent(90, 24, 5000);

		final NoiseComponent qc = new NoiseComponent(160, 4, 10000);
		final NoiseComponent q2 = new NoiseComponent(160, 8, 20000);
		final NoiseComponent q3 = new NoiseComponent(160, 16, 30000);
		final NoiseComponent q4 = new NoiseComponent(160, 24, 50000);

		ns.addComponent(nc);
		ns.addComponent(n2);
		ns.addComponent(n3);
		ns.addComponent(n4);
		ns.addComponent(pc);
		ns.addComponent(p2);
		ns.addComponent(p3);
		ns.addComponent(p4);
		ns.addComponent(qc);
		ns.addComponent(q2);
		ns.addComponent(q3);
		ns.addComponent(q4);

		// see how we get on
		double res = ns.getValueFor(90, 16);
		assertEquals(res, 3000, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(90, 2);
		assertEquals(res, 1000, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(90, 24);
		assertEquals(res, 5000, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(90, 28);
		assertEquals(res, 5000, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(0, 2);
		assertEquals(res, 100, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(0, 28);
		assertEquals(res, 500, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(160, 2);
		assertEquals(res, 10000, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(160, 28);
		assertEquals(res, 50000, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(180, 2);
		assertEquals(res, 10000, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(180, 28);
		assertEquals(res, 50000, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(30, 2);
		assertEquals(res, 100, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(30, 24);
		assertEquals(res, 500, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(60, 2);
		assertEquals(res, 1000, 0.001, "found speed for existing bearing");
		res = ns.getValueFor(60, 24);
		assertEquals(res, 5000, 0.001, "found speed for existing bearing");

	}
}
