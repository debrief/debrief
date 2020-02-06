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

package ASSET.GUI.Workbench.Plotters;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import ASSET.Models.DecisionType;
import ASSET.Models.Decision.BehaviourList;
import MWC.GUI.Editable;

public class BehavioursPlottable extends BasePlottable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public BehavioursPlottable(final DecisionType decisionModel, final ScenarioLayer parent) {
		super(decisionModel, parent);
	}

	@Override
	public Enumeration<Editable> elements() {
		final Vector<Editable> list = new Vector<Editable>(0, 1);

		// hmm, do we have child behaviours?
		if (getModel() instanceof BehaviourList) {
			final BehaviourList bl = (BehaviourList) getModel();
			final Vector<DecisionType> theModels = bl.getModels();
			final Iterator<DecisionType> iter = theModels.iterator();
			while (iter.hasNext()) {
				list.add(iter.next());
			}
		}

		return list.elements();
	}

	public DecisionType getDecisionModel() {
		return (DecisionType) getModel();
	}

	@Override
	public boolean hasOrderedChildren() {
		return true;
	}

}
