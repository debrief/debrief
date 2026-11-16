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

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GenericData.Duration.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GenericData.Duration.*;
import java.io.Serializable;
import java.text.ParseException;
import java.util.StringTokenizer;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class DurationTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testToString() throws ParseException {
		Duration dd = new Duration(12, MINUTES);
		String res = dd.toString();
		assertEquals(res, "12.0 minutes", "Minutes correct");
		dd = new Duration(1.25, MINUTES);
		res = dd.toString();
		assertEquals(res, "75.0 seconds", "Minutes correct");

		// and now convert back
		final Duration d2 = Duration.fromString(res);
		assertEquals(dd, d2, "converted back");

	}

	@Test
	public final void testWorldDistanceUnits() {
		final Duration w1 = new Duration(1, Duration.MILLISECONDS);
		assertEquals(w1.getValueIn(Duration.MILLISECONDS), 1d, 0.000001, "millis correct");

		final Duration w2 = new Duration(2 / 1000d, Duration.SECONDS);
		assertEquals(w2.getValueIn(Duration.MILLISECONDS), 2d, 0.000001, "seconds correct");

		final Duration w3 = new Duration(5, Duration.MINUTES);
		assertEquals(w3.getValueIn(Duration.MILLISECONDS), 300000d, 0.000001, "mins correct");

		final Duration w4 = new Duration(5 / 1000d / 60d / 60d, Duration.HOURS);
		assertEquals(w4.getValueIn(Duration.MILLISECONDS), 5d, 0.000001, "hours correct");

		assertEquals(w2.getValueIn(Duration.SECONDS), 2 / 1000d, 0.000001, "Back to Metres correct");

		final Duration w5 = new Duration(w4);
		assertEquals(w5.getValueIn(Duration.MILLISECONDS), 5d, 0.000001, "Copy constructor");

		assertEquals(Duration.getLabelFor(Duration.MINUTES), "minutes", "retrieve mins label");
		assertEquals(Duration.getLabelFor(Duration.DAYS), "days", "retrieve days label");

	}

}
