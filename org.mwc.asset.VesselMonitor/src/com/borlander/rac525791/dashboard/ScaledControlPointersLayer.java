/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.borlander.rac525791.dashboard;

import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.layout.ControlUISuite.ControlAccess;

public class ScaledControlPointersLayer extends ControlPointersLayer {
	private final AutoScaler myScaler;
	
	public ScaledControlPointersLayer(Factory factory, DashboardUIModel uiModel, ControlAccess control) {
		super(factory, uiModel, control);
		myScaler = new AutoScaler();
	}
	
	public int getMultiplier(){
		return myScaler.getMultiplier();
	}
	
	@Override
	public void setActualValue(int value) {
		myScaler.setActual(value);
		int scaled = myScaler.getScaledActual(); 
		super.setActualValue(scaled);
	}
	
	@Override
	public void setDemandedValue(int value) {
		myScaler.setDemanded(value);
		int scaled = myScaler.getScaledDemanded();
		super.setDemandedValue(scaled);
	}

}
