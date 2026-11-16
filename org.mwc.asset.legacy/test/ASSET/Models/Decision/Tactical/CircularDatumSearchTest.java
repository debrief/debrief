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

package ASSET.Models.Decision.Tactical;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Tactical.CircularDatumSearch.*;
import ASSET.Models.Decision.Tactical.CircularDatumSearch.*;
import ASSET.Models.Decision.Composite;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Decision.Conditions.ElapsedTime;
import ASSET.Models.Decision.Movement.Move;
import ASSET.Models.Decision.Movement.Trail;
import ASSET.Models.Decision.Movement.Transit;
import ASSET.Models.Decision.Movement.Wander;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

//////////////////////////////////////////////////
// member variables
//////////////////////////////////////////////////

/**
 * ********************************************************************** test
 * this public class
 * **********************************************************************
 */

public class CircularDatumSearchTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final MWC.GUI.Editable ed = new Move();
		return ed;
	}

	@Test
	public void testCreateBuoys() {
		// ok, create the search
		final CircularDatumSearch cs = new CircularDatumSearch(null, new WorldDistance(1, WorldDistance.KM), 6,
				null, null, null, null);

		final Sequence field = cs.createBuoyField(null, null);

		// check we created the correct amount (one more than number dropped, since it
		// includes the
		// transit)
		assertEquals(field.getModels().size(), 12, "correct num buoys created");

	}
}
