package com.planetmayo.debrief.satc.zigdetector;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		return getName() + " " + sdf.format(new Date(tStart)) + "-"
				+ sdf.format(new Date(tEnd));
	}

}