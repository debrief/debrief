/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
