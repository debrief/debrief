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

import static ASSET.Scenario.Observers.ProportionDetectedObserver.*;
import ASSET.Scenario.Observers.ProportionDetectedObserver.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

/**
 * ************************************************************ a gui public class to
 * show progress of this monitor
 * *************************************************************
 */

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class ProportionDetectedObserverTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final MWC.GUI.Editable ed = new ProportionDetectedObserver(null, null, "how many", new Integer(2), true);
		return ed;
	}
}
