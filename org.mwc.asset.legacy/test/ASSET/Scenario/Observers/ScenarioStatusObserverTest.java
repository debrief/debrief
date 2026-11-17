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

import static ASSET.Scenario.Observers.ScenarioStatusObserver.*;
import ASSET.Scenario.Observers.ScenarioStatusObserver.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Vector;
import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Scenario.LiveScenario.ISimulation;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;
import MWC.GUI.Editable;

/***************************************************************
 * member variables
 ***************************************************************/

//////////////////////////////////////////////////
// property testing
//////////////////////////////////////////////////
public class ScenarioStatusObserverTest extends ASSET.Util.EditableTesting {
	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new ScenarioStatusObserver();
	}
}
