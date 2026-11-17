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

public class ScenarioLayerLayerListenerTest extends ASSET.Util.EditableTesting {
	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final SSN ssn = new SSN(12);
		final Editable listener = new ScenarioParticipantWrapper(ssn, null);
		return listener;
	}
}
