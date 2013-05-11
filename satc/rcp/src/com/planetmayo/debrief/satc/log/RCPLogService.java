package com.planetmayo.debrief.satc.log;

import org.eclipse.core.runtime.IStatus;

import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class RCPLogService implements LogService
{
	
	@Override
	public void error(String message)
	{
		SATC_Activator.log(IStatus.ERROR, message, null);
	}

	@Override
	public void error(String message, Exception ex)
	{
		SATC_Activator.log(IStatus.ERROR, message, ex);
	}

	@Override
	public void info(String message)
	{
		SATC_Activator.log(IStatus.INFO, message, null);
	}

	@Override
	public void info(String message, Exception ex)
	{
		SATC_Activator.log(IStatus.INFO, message, ex);
	}

	@Override
	public void warn(String message)
	{
		SATC_Activator.log(IStatus.WARNING, message, null);
	}

	@Override
	public void warn(String message, Exception ex)
	{
		SATC_Activator.log(IStatus.WARNING, message, ex);
	}
}
