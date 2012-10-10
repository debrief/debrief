package com.planetmayo.debrief.satc.gwt.client.services;

import com.google.gwt.core.client.GWT;
import com.planetmayo.debrief.satc.support.LogService;

public class GWTLogService implements LogService
{

	@Override
	public void info(String message)
	{
		GWT.log(message);
	}

	@Override
	public void info(String message, Exception ex)
	{
		GWT.log(message, ex);		
	}

	@Override
	public void warn(String message)
	{
		GWT.log("WARN! " + message);		
	}

	@Override
	public void warn(String message, Exception ex)
	{
		GWT.log("WARN! " + message, ex);		
	}

	@Override
	public void error(String message)
	{
		GWT.log("ERROR!!! " + message, null);		
	}

	@Override
	public void error(String message, Exception ex)
	{
		GWT.log("ERROR!!! " + message, ex);		
	}
	
}
