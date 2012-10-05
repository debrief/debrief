package com.planetmayo.debrief.satc;

import com.planetmayo.debrief.satc.model.generator.TrackGenerator;

public class MockEngine
{
	private final TrackGenerator _generator = new TrackGenerator();

	public MockEngine()
	{
	}
	
	public TrackGenerator getGenerator()
	{
		return _generator;
	}
}
