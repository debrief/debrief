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
