package com.planetmayo.debrief.satc_rcp.ui.converters.units;

public class UnitConverter
{
	public static final UnitConverter SPEED_KTS = new UnitConverter(new MSecToKts(), new KtsToMSec());
	public static final UnitConverter ANGLE_DEG = new UnitConverter(new RadsToDeg(), new DegToRads());
	
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
	
	public static UnitConverter scale(UnitConverter converter, double scale)
	{
		return new UnitConverter(
				new ScaleConverter(converter.getModelToUI(), scale), 
				new ScaleConverter(converter.getUIToModel(), 1 / scale)
		);
	}
	
}
