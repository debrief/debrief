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

package ASSET.Scenario.Observers;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Scenario.Observers.WriteToCloudObserver.*;
import ASSET.Scenario.Observers.WriteToCloudObserver.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.TacticalData.Track;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class WriteToCloudObserverTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final WriteToCloudObserver tpo = new WriteToCloudObserver("a", "test observer", true);
		return tpo;
	}

	// TODO FIX-TEST
	public void NtestWrite() {

		final String directoryName = "./test_reports/";
		final String fileName = "res.png";

		final WriteToCloudObserver tpo = new WriteToCloudObserver(directoryName, "test observer", true);
		final CoreScenario cs = new CoreScenario();
		tpo.setup(cs);

		WorldLocation loc = new WorldLocation(1, 2, 3);
		final Status stat = new Status(12, 0);
		stat.setCourse(21);
		stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		ParticipantType ssn = new CoreParticipant(12);
		ssn.setName("Bingo");
		ssn.setCategory(
				new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));

		tpo.processTheseDetails(loc, stat, ssn);

		// move location
		loc = new WorldLocation(loc.add(new WorldVector(0.101, 0.01, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(0.201, 0.05, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(0.101, 0.03, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(0.011, 0.05, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		ssn = new CoreParticipant(14);
		ssn.setName("Spooner");
		ssn.setCategory(new Category(Category.Force.RED, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));

		loc = new WorldLocation(loc.add(new WorldVector(2.701, 0.12, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		// move location
		loc = new WorldLocation(loc.add(new WorldVector(2.201, 0.12, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(2.101, 0.05, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(1.301, 0.02, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(2.031, 0.04, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		tpo.tearDown(cs);

		// check file exists
		final File file = new File(directoryName + fileName);
		assertTrue(file.exists(), "file got created");
		System.out.println("file size is:" + file.length());
		assertEquals(7540, file.length(), 400, "file is of correct size");
	}
}
