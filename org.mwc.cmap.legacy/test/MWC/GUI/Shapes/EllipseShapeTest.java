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

import static MWC.GUI.Shapes.EllipseShape.*;
import MWC.GUI.Shapes.EllipseShape.*;
import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Vector;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.Layer;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class EllipseShapeTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testMyParams() {
		MWC.GUI.Editable ed = new EllipseShape(new WorldLocation(2d, 2d, 2d), 12d,
				new WorldDistance(12d, WorldDistance.DEGS), new WorldDistance(12d, WorldDistance.DEGS));
		MWC.GUI.Editable.editableTesterSupport.testParams(ed);
		ed = null;
	}
}
