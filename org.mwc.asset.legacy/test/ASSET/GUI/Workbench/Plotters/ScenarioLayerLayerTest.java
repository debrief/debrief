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

package ASSET.GUI.Workbench.Plotters;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.GUI.Workbench.Plotters.ScenarioLayer.*;
import ASSET.GUI.Workbench.Plotters.ScenarioLayer.*;
import java.util.Enumeration;
import java.util.HashMap;
import ASSET.ScenarioType;
import ASSET.Models.Vessels.SSN;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.Duration;

//////////////////////////////////////////////////
// testing support
//////////////////////////////////////////////////
public class ScenarioLayerLayerTest extends ASSET.Util.EditableTesting {
	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final ScenarioLayer layer = new ScenarioLayer();
		final CoreScenario cs = new CoreScenario();
		if (cs.getName() == null) {
			cs.setName("Scenario");
		}
		layer.setScenario(cs);
		layer.setStepTime(new Duration(12, Duration.SECONDS));
		layer.setScenarioStepTime(new Duration(12, Duration.SECONDS));
		return layer;
	}

	@Test
	public void testSetScenario() {
		final ScenarioType scen1 = new CoreScenario();
		scen1.setName("test scenario");
		scen1.createNewParticipant(ASSET.Participants.Category.Type.SUBMARINE);
		scen1.createNewParticipant(ASSET.Participants.Category.Type.FRIGATE);

		assertEquals(2, scen1.getListOfParticipants().length, "Wrong number of participants");

		// check it doesn't have any listeners

		// sort out the listeners
		final ScenarioLayer sl = new ScenarioLayer();
		sl.setScenario(scen1);

		// check the listeners are assigned
		assertEquals(2, sl.getData().size(), "Wrong number of part listeners");

		// and try another
		final ScenarioType scen2 = new CoreScenario();
		scen2.setName("test scenario 2");
		scen2.createNewParticipant(ASSET.Participants.Category.Type.SUBMARINE);
		scen2.createNewParticipant(ASSET.Participants.Category.Type.FRIGATE);
		scen2.createNewParticipant(ASSET.Participants.Category.Type.FRIGATE);

		sl.setScenario(scen2);

		// check there are no listeners to the old sceanrio
		assertEquals(3, sl.getData().size(), "Wrong number of part listeners");

	}
}
