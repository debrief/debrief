/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package org.mwc.debrief.track_shift.zig_detector.ownship;
import java.text.SimpleDateFormat;
import java.util.Date;

import MWC.Utilities.TextFormatting.GMTDateFormat;

/**
 * class to store the time period representing a leg of ownship data
 * 
 * @author ian
 * 
 */
public class LegOfData
{
	long tStart = Long.MAX_VALUE;
	long tEnd = Long.MIN_VALUE;

	final private String _myName;

	public LegOfData(final String name)
	{
		_myName = name;
	}

	public LegOfData(final String name, final long tStart2, final long tEnd2)
	{
		this(name);
		tStart = tStart2;
		tEnd = tEnd2;
	}

	public void add(final long time)
	{
		tStart = Math.min(tStart, time);
		tEnd = Math.max(tEnd, time);
	}

	public Long getEnd()
	{
		return tEnd;
	}

	public String getName()
	{
		return _myName;
	}

	public Long getStart()
	{
		return tStart;
	}

	public boolean initialised()
	{
		return tStart != Long.MAX_VALUE;
	}

	@Override
	public String toString()
	{
		final SimpleDateFormat sdf = new GMTDateFormat("hh:mm:ss");
		return getName() + " " + sdf.format(new Date(tStart)) + "-"
				+ sdf.format(new Date(tEnd));
	}

}