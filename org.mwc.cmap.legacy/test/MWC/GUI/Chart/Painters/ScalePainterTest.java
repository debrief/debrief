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

package MWC.GUI.Chart.Painters;

import static MWC.GUI.Chart.Painters.ScalePainter.*;
import MWC.GUI.Chart.Painters.ScalePainter.*;
import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import MWC.GUI.CanvasType;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.DiagonalLocationPropertyEditor;
import MWC.GUI.Properties.UnitsPropertyEditor;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class ScalePainterTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testMyParams() {
		MWC.GUI.Editable ed = new ScalePainter();
		MWC.GUI.Editable.editableTesterSupport.testParams(ed);
		ed = null;
	}
}
