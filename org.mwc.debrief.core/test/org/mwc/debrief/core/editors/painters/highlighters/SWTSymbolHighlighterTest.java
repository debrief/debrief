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

package org.mwc.debrief.core.editors.painters.highlighters;

import static org.mwc.debrief.core.editors.painters.highlighters.SWTSymbolHighlighter.*;
import org.mwc.debrief.core.editors.painters.highlighters.SWTSymbolHighlighter.*;
import java.awt.Color;
import java.awt.Point;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class SWTSymbolHighlighterTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testMyParams() {
		Editable ed = new SWTSymbolHighlighter();
		Editable.editableTesterSupport.testParams(ed);
		ed = null;
	}
}
