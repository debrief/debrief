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

package Debrief.GUI.Tote;

import static Debrief.GUI.Tote.StepControl.*;
import Debrief.GUI.Tote.StepControl.*;
import java.awt.Color;
import java.beans.BeanInfo;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorSupport;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter;
import MWC.GUI.Editable;
import MWC.GUI.StepperListener;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class StepControlTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testMyParams() {
		StepControl ed = new StepControl(null, null) {
			@Override
			protected void doEditPainter() {
			}

			@Override
			protected void formatTimeText() {
			}

			@Override
			protected PropertiesPanel getPropertiesPanel() {
				return null;
			}

			@Override
			public HiResDate getToolboxEndTime() {
				return null;
			}

			@Override
			public HiResDate getToolboxStartTime() {
				return null;
			}

			@Override
			protected void initForm() {
			}

			@Override
			protected void painterIsDefined() {
			}

			@Override
			public void setToolboxEndTime(final HiResDate val) {
			}

			@Override
			public void setToolboxStartTime(final HiResDate val) {
			}

			@Override
			protected void updateForm(final HiResDate DTG) {
			}
		};

		editableTesterSupport.testParams(ed);
		ed = null;
	}
}
