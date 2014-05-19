package com.planetmayo.debrief.satc.model.generator.exceptions;

public class GenerationException extends InterruptedException
{
	private static final long serialVersionUID = 1L;

	public GenerationException()
	{
		super();
	}

	public GenerationException(String s)
	{
		super(s);
	}
}
