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

import static Debrief.GUI.Tote.Painters.RelativePainter.*;
import Debrief.GUI.Tote.Painters.RelativePainter.*;
import Debrief.GUI.Tote.AnalysisTote;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import org.junit.jupiter.api.Test;

///////////////////////////////////
	// member variables
	//////////////////////////////////

	/**
	 * NON-STANDARD implementation, we are returning the editor for our snail
	 * plotter object, not ourself
	 */
//  public Editable.EditorType getInfo()
//  {
//    return new RelativePainterInfo(this);
//  }

	/////////////////////////////////////////
	// accessors for the beaninfo
	////////////////////////////////////////
	//////////////////////////////////////////////////////////
	// accessors for editable parameters
	/////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	// nested class describing how to edit this class
	////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	public final class RelativePainterTest {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";


		@Test
		public final void testMyParams() {
			Editable ed = new SnailPainter(null, null, null);
			Editable.editableTesterSupport.testParams(ed);
			ed = null;
		}
	}
