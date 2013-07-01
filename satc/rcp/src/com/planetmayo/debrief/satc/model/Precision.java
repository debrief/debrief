package com.planetmayo.debrief.satc.model;

public enum Precision
{
	LOW("Low", 600), MEDIUM("Medium", 300), HIGH("High", 100);

	private final String label;
	private final double meters;

	private Precision(String label, double meters)
	{
		this.label = label;
		this.meters = meters;
	}

	public String getLabel()
	{
		return label;
	}
	
	public double toMeters() 
	{
		return meters;
	}
}
