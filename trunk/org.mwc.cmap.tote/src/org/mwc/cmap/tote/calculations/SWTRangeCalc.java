/**
 * 
 */
package org.mwc.cmap.tote.calculations;

import java.text.NumberFormat;

import Debrief.Tools.Tote.*;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * @author ian.mayo
 *
 */
public class SWTRangeCalc implements toteCalculation
{

	public String update(Watchable primary, Watchable secondary, HiResDate thisTime)
	{
		return "updated at:" + DebriefFormatDateTime.toStringHiRes(thisTime);
	}

	public double calculate(Watchable primary, Watchable secondary, HiResDate thisTime)
	{
		return -1;
	}

	public void setPattern(NumberFormat format)
	{
	
	}

	public String getTitle()
	{
		return "calc test:range";
	}

	public String getUnits()
	{
		// TODO Auto-generated method stub
		return "n/a";
	}

	public boolean isWrappableData()
	{
		return false;
	}

}
