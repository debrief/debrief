package com.planetmayo.debrief.satc.model;

public enum Precision
{
	LOW("Low"), MEDIUM("Medium"), HIGH("High");

	private final String label;

	private Precision(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return label;
	}

}
