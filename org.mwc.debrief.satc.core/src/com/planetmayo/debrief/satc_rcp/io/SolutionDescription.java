package com.planetmayo.debrief.satc_rcp.io;

import java.util.List;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("solution")
public class SolutionDescription
{	
	private VehicleType vehicleType;
	
	private Precision precision;
	
	@XStreamAlias("contributions")
	private List<BaseContribution> contributions;
	
	@XStreamAsAttribute
	private int version;
	
	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public VehicleType getVehicleType()
	{
		return vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType)
	{
		this.vehicleType = vehicleType;
	}

	public Precision getPrecision()
	{
		return precision;
	}

	public void setPrecision(Precision precision)
	{
		this.precision = precision;
	}

	public List<BaseContribution> getContributions()
	{
		return contributions;
	}

	public void setContributions(List<BaseContribution> contributions)
	{
		this.contributions = contributions;
	}	
}
