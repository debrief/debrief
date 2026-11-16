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

package ASSET.Util.MonteCarlo;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Util.MonteCarlo.LocationOffset.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ASSET.Util.MonteCarlo.LocationOffset.*;
import java.text.ParseException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

//////////////////////////////////////////////////
// property testing
//////////////////////////////////////////////////
public class LocationOffsetTest extends SupportTesting {

	@Test
	public void testArea() {

		final WorldDistance myDist = new WorldDistance(1, WorldDistance.DEGS);
		final WorldLocation myOrigin = new WorldLocation(8, 0, 0);
		final int myModel = RandomGenerator.NORMAL;
		final LocationOffset la = new LocationOffset(myOrigin, myDist, myModel);

		// and get the locations
		final int limit = 100;
		for (int i = 0; i < limit; i++) {
			la.newPermutation();
			final WorldLocation thisLoc = la.thisPermutation();
			final WorldVector error = thisLoc.subtract(myOrigin);
			final double rngDegs = error.getRange();
			assertTrue(rngDegs < 1, "generated location isn't inside indicated area");
		}
	}

}
