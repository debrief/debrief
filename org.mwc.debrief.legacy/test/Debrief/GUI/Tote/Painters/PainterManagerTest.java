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

import static Debrief.GUI.Tote.Painters.PainterManager.*;
import Debrief.GUI.Tote.Painters.PainterManager.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorSupport;
import java.util.Enumeration;
import java.util.Vector;
import Debrief.GUI.Tote.StepControl;
import MWC.GUI.Editable;
import MWC.GUI.StepperListener;
import MWC.GenericData.HiResDate;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class PainterManagerTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testMyParams() {
		final StepControl stepper = new Debrief.GUI.Tote.Swing.SwingStepControl(null, null, null, null, null, null);
		Editable ed = new PainterManager(stepper);
		Editable.editableTesterSupport.testParams(ed);
		ed = null;
	}
}
