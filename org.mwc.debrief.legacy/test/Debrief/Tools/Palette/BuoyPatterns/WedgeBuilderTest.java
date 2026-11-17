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

package Debrief.Tools.Palette.BuoyPatterns;

import static Debrief.Tools.Palette.BuoyPatterns.WedgeBuilder.*;
import Debrief.Tools.Palette.BuoyPatterns.WedgeBuilder.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////
// Member variables
//////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class WedgeBuilderTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testMyParams() {
		MWC.GUI.Editable ed = new WedgeBuilder(null, null, null);
		MWC.GUI.Editable.editableTesterSupport.testParams(ed);
		ed = null;
	}
}
