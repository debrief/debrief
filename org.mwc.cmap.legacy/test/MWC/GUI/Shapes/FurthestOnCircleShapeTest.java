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

package MWC.GUI.Shapes;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GUI.Shapes.FurthestOnCircleShape.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GUI.Shapes.FurthestOnCircleShape.*;
import static MWC.GUI.Properties.LabelLocationPropertyEditor.ALL;
import static MWC.GUI.Properties.LabelLocationPropertyEditor.BOTTOM;
import static MWC.GUI.Properties.LabelLocationPropertyEditor.LEFT;
import static MWC.GUI.Properties.LabelLocationPropertyEditor.RIGHT;
import static MWC.GUI.Properties.LabelLocationPropertyEditor.TOP;
import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GUI.Properties.LabelLocationPropertyEditor;
import MWC.GUI.Properties.TacticalFrequencyPropertyEditor;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class FurthestOnCircleShapeTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testMyParams() {
		MWC.GUI.Editable ed = new FurthestOnCircleShape(new WorldLocation(2d, 2d, 2d), 3,
				new WorldSpeed(2, WorldSpeed.Kts), 5000, 45, 180);
		MWC.GUI.Editable.editableTesterSupport.testParams(ed);
		ed = null;
	}

	@Test
	public void testWidthCalc() {
		final FurthestOnCircleShape ed = new FurthestOnCircleShape(new WorldLocation(2d, 2d, 2d), 3,
				new WorldSpeed(60, WorldSpeed.Kts), 60 * 60 * 1000, 45, 180);
		final WorldDistance dist = ed.getRingWidth();
		assertEquals(1d, dist.getValueIn(WorldDistance.DEGS), "the width");
	}
}
