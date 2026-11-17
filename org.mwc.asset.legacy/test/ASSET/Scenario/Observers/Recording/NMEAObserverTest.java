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

package ASSET.Scenario.Observers.Recording;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Scenario.Observers.Recording.NMEAObserver.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ASSET.Scenario.Observers.Recording.NMEAObserver.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Models.SensorType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

public class NMEAObserverTest {
	@Test
	public static void testPack() {
		assertEquals("0000.0000,N", pack(0, true), "valid string");
		assertEquals("00000.0000,E", pack(0, false), "valid string");

		assertEquals("4530.0000,N", pack(45.5, true), "valid string");
		assertEquals("04530.0000,E", pack(45.5, false), "valid string");

		assertEquals("0000.6000,S", pack(-0.01, true), "valid string");
		assertEquals("00000.6000,W", pack(-0.01, false), "valid string");

		assertEquals("4530.0000,S", pack(-45.5, true), "valid string");
		assertEquals("04530.0000,W", pack(-45.5, false), "valid string");
	}
}
