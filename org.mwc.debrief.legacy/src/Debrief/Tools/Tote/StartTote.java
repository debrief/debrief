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

package Debrief.Tools.Tote;

import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;

public final class StartTote extends PlainTool {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	private final PlainChart _theChart;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	public StartTote(final ToolParent theParent, final PlainChart theChart) {
		super(theParent, "Step Forward", null);
		_theChart = theChart;
	}

	@Override
	public final void execute() {
		_theChart.update();
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////
	@Override
	public final Action getData() {
		// return the product
		return null;
	}

}
