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

package MWC.Algorithms;

final public class Conversions {
	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	static public class ConversionsTest extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "CONV";

		public ConversionsTest(final String val) {
			super(val);
		}

		public void testValues() {
			final double degs = 1;
			assertEquals("degs 2 nm", 60, Conversions.Degs2Nm(degs), 0.01);
			assertEquals("degs 2 km", 111.12, Conversions.Degs2Km(degs), 0.01);
			assertEquals("degs 2 m", 111120.000, Conversions.Degs2m(degs), 0.01);
			assertEquals("degs 2 yds", 121522.31, Conversions.Degs2Yds(degs), 0.01);
			assertEquals("ft 2 m", 0.3048, Conversions.ft2m(1), 0.01);
			assertEquals("m 2 ft", 1, Conversions.m2ft(0.3048), 0.01);
			/**
			 * note, we're not using Excel value for degs2yds, but RN approximation (6080/3)
			 */
		}

		public void testNormaliseDegs() {
			assertEquals(0d, Conversions.normaliseDegs(0), 1e-9);
			assertEquals(0d, Conversions.normaliseDegs(360), 1e-9);
			assertEquals(350d, Conversions.normaliseDegs(-10), 1e-9);
			assertEquals(10d, Conversions.normaliseDegs(730), 1e-9);
			assertEquals(180d, Conversions.normaliseDegs(-540), 1e-9);
			assertEquals(359.5, Conversions.normaliseDegs(-0.5), 1e-9);
		}

		public void testSignedDegs() {
			assertEquals(-10d, Conversions.signedDegs(350), 1e-9);
			assertEquals(180d, Conversions.signedDegs(180), 1e-9);
			assertEquals(180d, Conversions.signedDegs(-180), 1e-9);
			assertEquals(-170d, Conversions.signedDegs(190), 1e-9);
			assertEquals(10d, Conversions.signedDegs(730), 1e-9);
			assertEquals(-10d, Conversions.signedDegs(-730), 1e-9);
			assertEquals(170d, Conversions.signedDegs(-550), 1e-9);
		}

		public void testDegsDifference() {
			// from 350 to 010 is +20, not -340
			assertEquals(20d, Conversions.degsDifference(350, 10), 1e-9);
			assertEquals(-20d, Conversions.degsDifference(10, 350), 1e-9);
			assertEquals(-10d, Conversions.degsDifference(-175, 175), 1e-9);
			assertEquals(0d, Conversions.degsDifference(45, 405), 1e-9);
		}

		public void testRadiansHelpers() {
			assertEquals(Math.PI * 1.5, Conversions.normaliseRads(-Math.PI / 2), 1e-9);
			assertEquals(0d, Conversions.normaliseRads(2 * Math.PI), 1e-9);
			assertEquals(-Math.PI / 2, Conversions.signedRads(Math.PI * 1.5), 1e-9);
			assertEquals(Math.PI, Conversions.signedRads(-Math.PI), 1e-9);
			assertEquals(Degs2Rads(20), Conversions.radsDifference(Degs2Rads(350), Degs2Rads(10)), 1e-9);
		}
	}

	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	final static private double RADIAN_CONV = 180 / Math.PI;
	final static private double NM_M_CONV = 1852d;
	final static private double DEGS_KM_CONV = 60d * NM_M_CONV / 1000d;
	final static private double DEGS_M_CONV = 60d * NM_M_CONV;
	final static private double KTS_MPS_CONV = 1852d / 3600;
	final static private double MILES_M_CONV = 1609.344;
	final static private double FT_M_CONV = 0.3048;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	/**
	 * Note method of expressing this conversion using existing, exact constants as
	 * supplied by Daniel Thibault, Dec 09
	 */
	final static private double YDS_NM_CONV = NM_M_CONV / (3d * FT_M_CONV);

	/**
	 * normalise an angle (degrees) to the range [0, 360)
	 *
	 * @param degs angle in degrees
	 * @return equivalent angle in [0, 360)
	 */
	final public static double normaliseDegs(final double degs) {
		if (degs >= 0d && degs < 360d) {
			// already in range, don't introduce rounding errors
			return degs;
		}
		double res = degs % 360d;
		if (res < 0) {
			res += 360d;
		}
		// guard against -0.0 and rounding up to exactly 360
		if (res >= 360d || res == 0d) {
			res = 0d;
		}
		return res;
	}

	/**
	 * fold an angle (or an angular difference) in degrees into the range (-180,
	 * 180]
	 *
	 * @param degs angle in degrees
	 * @return equivalent signed angle in (-180, 180]
	 */
	final public static double signedDegs(final double degs) {
		if (degs > -180d && degs <= 180d) {
			// already in range, don't introduce rounding errors
			return degs;
		}
		final double res = normaliseDegs(degs);
		return res > 180d ? res - 360d : res;
	}

	/**
	 * the shortest signed turn (degrees) from one angle to another, so the change
	 * from 350 to 010 is +20, not -340
	 *
	 * @param fromDegs start angle (degrees)
	 * @param toDegs   end angle (degrees)
	 * @return signed difference (toDegs - fromDegs) in (-180, 180]
	 */
	final public static double degsDifference(final double fromDegs, final double toDegs) {
		return signedDegs(toDegs - fromDegs);
	}

	/**
	 * normalise an angle (radians) to the range [0, 2 PI)
	 *
	 * @param rads angle in radians
	 * @return equivalent angle in [0, 2 PI)
	 */
	final public static double normaliseRads(final double rads) {
		if (rads >= 0d && rads < 2d * Math.PI) {
			// already in range, don't introduce rounding errors
			return rads;
		}
		final double twoPi = 2d * Math.PI;
		double res = rads % twoPi;
		if (res < 0) {
			res += twoPi;
		}
		if (res >= twoPi || res == 0d) {
			res = 0d;
		}
		return res;
	}

	/**
	 * fold an angle (or an angular difference) in radians into the range (-PI,
	 * PI]
	 *
	 * @param rads angle in radians
	 * @return equivalent signed angle in (-PI, PI]
	 */
	final public static double signedRads(final double rads) {
		if (rads > -Math.PI && rads <= Math.PI) {
			// already in range, don't introduce rounding errors
			return rads;
		}
		final double res = normaliseRads(rads);
		return res > Math.PI ? res - 2d * Math.PI : res;
	}

	/**
	 * the shortest signed turn (radians) from one angle to another
	 *
	 * @param fromRads start angle (radians)
	 * @param toRads   end angle (radians)
	 * @return signed difference (toRads - fromRads) in (-PI, PI]
	 */
	final public static double radsDifference(final double fromRads, final double toRads) {
		return signedRads(toRads - fromRads);
	}

	final public static double clipRadians(final double val) {
		double theVal = val;
		while (theVal > 2 * Math.PI)
			theVal = theVal - 2 * Math.PI;
		while (theVal < 0)
			theVal = theVal + 2 * Math.PI;

		return theVal;
	}

	/**
	 * convert the range to the supplied units
	 *
	 * @param range    range (in degrees)
	 * @param theUnits target units
	 * @return converted value
	 */
	public static final double convertRange(final double range, final String theUnits) {
		double theRng = 0;
		// do the units conversion
		if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS)
				|| theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.OLD_YDS_UNITS)) {
			theRng = MWC.Algorithms.Conversions.Degs2Yds(range);
		} else if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.KYD_UNITS)) {
			theRng = MWC.Algorithms.Conversions.Degs2Yds(range) / 1000.0;
		} else if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.METRES_UNITS)) {
			theRng = MWC.Algorithms.Conversions.Degs2m(range);
		} else if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.KM_UNITS)) {
			theRng = MWC.Algorithms.Conversions.Degs2Km(range);
		} else if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.NM_UNITS)) {
			theRng = MWC.Algorithms.Conversions.Degs2Nm(range);
		} else {
			MWC.Utilities.Errors.Trace.trace("Range/Bearing units in properties file may be corrupt");
		}
		return theRng;
	}

	final public static double Degs2Km(final double val) {
		return val * DEGS_KM_CONV;
	}

	final public static double Degs2m(final double val) {
		return val * DEGS_M_CONV;
	}

	final public static double Degs2Nm(final double val) {
		return val * 60;
	}

	final public static double Degs2Rads(final double val) {
		return val / RADIAN_CONV;
	}

	final public static double Degs2Yds(final double val) {
		return Nm2Yds(Degs2Nm(val));
	}

	final public static double ft2m(final double val) {
		return val * FT_M_CONV;
	}

	final public static double Kts2Mps(final double val) {
		return val * KTS_MPS_CONV;
	}

	final public static double Kts2Yps(final double val) {
		return val * YDS_NM_CONV / 3600;
	}

	final public static double m2Degs(final double val) {
		return val / DEGS_M_CONV;
	}

	final public static double m2ft(final double val) {
		return val / FT_M_CONV;
	}

	final public static double miles2metres(final double val) {
		return val * MILES_M_CONV;
	}

	final public static double Mps2Kts(final double val) {
		return val / KTS_MPS_CONV;
	}

	final public static double Nm2Degs(final double val) {
		return val / 60;
	}

	final public static double Nm2Yds(final double val) {
		return val * YDS_NM_CONV;
	}

	final public static double Rads2Degs(final double val) {
		return val * RADIAN_CONV;
	}

	final public static double Yds2Degs(final double val) {
		return Nm2Degs(Yds2Nm(val));
	}

	final public static double Yds2Nm(final double val) {
		return val / YDS_NM_CONV;
	}

	/*
	 * public static String long2Date(long val){ Date d = new Date(val); String res
	 * = d.toString(); return res; } ditch this
	 */

	final public static double Yps2Kts(final double val) {
		return val / YDS_NM_CONV * 3600;
	}
}
