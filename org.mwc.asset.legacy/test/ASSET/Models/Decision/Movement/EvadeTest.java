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

package ASSET.Models.Decision.Movement;

import static ASSET.Models.Decision.Movement.Evade.*;
import ASSET.Models.Decision.Movement.Evade.*;
import ASSET.NetworkParticipant;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.Conversions;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * ASSET.Models.Decision.Movement.Evade.UserControlInfo
 */

// ////////////////////////////////////////////////
// testing
// ////////////////////////////////////////////////
public class EvadeTest extends ASSET.Util.EditableTesting {
	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new Evade(null, new WorldSpeed(12, WorldSpeed.M_sec), new WorldDistance(12, WorldDistance.METRES),
				null);
	}

}
