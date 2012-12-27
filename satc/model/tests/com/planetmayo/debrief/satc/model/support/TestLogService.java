package com.planetmayo.debrief.satc.model.support;

import com.planetmayo.debrief.satc.support.LogService;

public class TestLogService implements LogService
{

	@Override
	public void error(String message)
	{
		System.err.println("ERROR: " + message);
	}

	@Override
	public void error(String message, Exception ex)
	{
		System.err.println("ERROR: " + message);
		ex.printStackTrace();		
	}

	@Override
	public void info(String message)
	{
		System.out.println(message);
	}

	@Override
	public void info(String message, Exception ex)
	{
		System.out.println(message);
		ex.printStackTrace();
	}

	@Override
	public void warn(String message)
	{
		System.err.println("WARNING: " + message);		
	}

	@Override
	public void warn(String message, Exception ex)
	{
		System.err.println("WARNING: " + message);
		ex.printStackTrace();
	}	
}
