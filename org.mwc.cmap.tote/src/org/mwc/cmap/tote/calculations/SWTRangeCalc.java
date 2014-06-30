/**
 * 
 */
package org.mwc.cmap.tote.calculations;

import java.text.NumberFormat;

import Debrief.Tools.Tote.*;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * @author ian.mayo
 *
 */
public class SWTRangeCalc implements toteCalculation
{

	public String update(final Watchable primary, final Watchable secondary, final HiResDate thisTime)
	{
		return "updated at:" + DebriefFormatDateTime.toStringHiRes(thisTime);
	}

	public double calculate(final Watchable primary, final Watchable secondary, final HiResDate thisTime)
	{
		return -1;
	}

	public void setPattern(final NumberFormat format)
	{
	
	}

	public String getTitle()
	{
		return "calc test:range";
	}

	public String getUnits()
	{
		return "n/a";
	}

	public boolean isWrappableData()
	{
		return false;
	}

}
