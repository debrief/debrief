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

package Debrief.GUI.Tote.Painters;

import static Debrief.GUI.Tote.Painters.SnailDrawFix2.*;
import Debrief.GUI.Tote.Painters.SnailDrawFix2.*;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyDescriptor;
import Debrief.GUI.Tote.AnalysisTote;
import Debrief.GUI.Tote.Painters.SnailPainter2.ColorFadeCalculator;
import Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter;
import Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter.RectangleHighlight;
import Debrief.GUI.Tote.Painters.Highlighters.SymbolHighlighter;
import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GUI.Properties.FractionPropertyEditor;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class SnailDrawFix2Test {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testMyParams() {
		Editable ed = new SnailDrawFix2("testing", null);
		Editable.editableTesterSupport.testParams(ed);
		ed = null;
	}
}
