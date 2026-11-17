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

package Debrief.Wrappers;

import static Debrief.Wrappers.PolygonWrapper.*;
import Debrief.Wrappers.PolygonWrapper.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.Vector;
import MWC.GUI.Editable;
import MWC.GUI.ExportLayerAsSingleItem;
import MWC.GUI.Exportable;
import MWC.GUI.FireExtended;
import MWC.GUI.Layer;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

public class PolygonWrapperTest {

	@Test
	public final void testMyParams() {
		final Vector<PolygonNode> nodes = new Vector<PolygonNode>();
		nodes.add(new PolygonNode("aa", new WorldLocation(2d, 3d, 0d), null));
		final PolygonShape shape = new PolygonShape(nodes);
		PolygonWrapper ed = new PolygonWrapper("aa", shape, MWC.GUI.Properties.DebriefColors.RED, new HiResDate(0));
		editableTesterSupport.testParams(ed);
		ed = null;
	}
}
