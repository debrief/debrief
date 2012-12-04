package com.planetmayo.debrief.satc_rcp.ui.converters.units;

public enum UnitConverter
{
	SPEED_KTS(new MSecToKts(), new KtsToMSec()),
	ANGLE_DEG(new RadsToDeg(), new DegToRads());
	
	private final AbstractUnitConverter modelToUI;
	private final AbstractUnitConverter uiToModel;
	
	UnitConverter(AbstractUnitConverter modelToUI, AbstractUnitConverter uiToModel) {
		this.modelToUI = modelToUI;
		this.uiToModel = uiToModel;
	}

	public AbstractUnitConverter getModelToUI()
	{
		return modelToUI;
	}

	public AbstractUnitConverter getUIToModel()
	{
		return uiToModel;
	}
	
}
