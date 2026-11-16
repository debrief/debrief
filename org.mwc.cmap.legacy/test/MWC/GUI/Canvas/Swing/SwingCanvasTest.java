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

package MWC.GUI.Canvas.Swing;

import static MWC.GUI.Canvas.Swing.SwingCanvas.*;
import MWC.GUI.Canvas.Swing.SwingCanvas.*;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.Vector;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class SwingCanvasTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testMyParams() {
		final Editable ed = new SwingCanvas(null);
		Editable.editableTesterSupport.testParams(ed);
	}
}
