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

package com.borlander.rac525791.dashboard;

import com.borlander.rac525791.dashboard.layout.ControlUISuite.ControlAccess;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;

public class ScaledControlPointersLayer extends ControlPointersLayer {
	private final AutoScaler myScaler;

	public ScaledControlPointersLayer(final Factory factory, final DashboardUIModel uiModel,
			final ControlAccess control) {
		super(factory, uiModel, control);
		myScaler = new AutoScaler();
	}

	public int getMultiplier() {
		return myScaler.getMultiplier();
	}

	@Override
	public void setActualValue(final int value) {
		myScaler.setActual(value);
		final int scaled = myScaler.getScaledActual();
		super.setActualValue(scaled);
	}

	@Override
	public void setDemandedValue(final int value) {
		myScaler.setDemanded(value);
		final int scaled = myScaler.getScaledDemanded();
		super.setDemandedValue(scaled);
	}

}
