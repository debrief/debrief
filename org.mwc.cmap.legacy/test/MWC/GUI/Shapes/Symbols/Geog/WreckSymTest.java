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

package MWC.GUI.Shapes.Symbols.Geog;

import static MWC.GUI.Shapes.Symbols.Geog.WreckSym.*;
import MWC.GUI.Shapes.Symbols.Geog.WreckSym.*;
import java.beans.PropertyDescriptor;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class WreckSymTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testMyParams() {
		MWC.GUI.Editable ed = new WreckSym();
		editableTesterSupport.testParams(ed);
		ed = null;
	}
}
