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

import static Debrief.Wrappers.LabelWrapper.*;
import Debrief.Wrappers.LabelWrapper.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanPlotFaded;
import MWC.GUI.CanvasType;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Chart.Swing.SwingChart;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class LabelWrapperTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testMyParams() {
		MWC.GUI.Editable ed = new LabelWrapper(null, null, null);
		MWC.GUI.Editable.editableTesterSupport.testParams(ed);
		ed = null;
	}

	@Test
	public final void testWatchables() {
		final WatchableList.TestWatchables tw = new WatchableList.TestWatchables() {
			/**
			 * get an example of this kind of list with both dates set
			 *
			 * @return
			 */
			@Override
			public WatchableList getBothDates(final HiResDate startDate, final HiResDate endDate) {
				return new LabelWrapper("both", null, Color.red, startDate, endDate) {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

				};
			}

			/**
			 * get an example of this kind of list with no dates set
			 *
			 * @return
			 */
			@Override
			public WatchableList getNullDates() {
				return new LabelWrapper("both", null, Color.red, null, null) {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;
				};
			}

			/**
			 * get an example of this kind of list with only start date set
			 *
			 * @return
			 */
			@Override
			public WatchableList getStartDateOnly(final HiResDate startDate) {
				return new LabelWrapper("both", null, Color.red, startDate, null) {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

				};
			}
		};

		tw.doTest();
	}
}
