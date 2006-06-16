package org.mwc.cmap.tote.calculations;

import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.preferences.PrefsPage;

import Debrief.Tools.Tote.Watchable;
import Debrief.Tools.Tote.Calculations.rangeCalc;
import MWC.GenericData.HiResDate;

public class SlantRangeCalc extends rangeCalc
{

	private static final String SLANT_RANGE_MARKER = " \\";

	/** override our paren't calculate method so we can choose whether to show slant range
	 * 
	 * @param primary
	 * @param secondary
	 * @param thisTime
	 * @return
	 */
	public double calculate(Watchable primary, Watchable secondary, HiResDate thisTime)
	{
		double range = 0.0;
		double theRng = 0.0;
		if ((primary != null) && (secondary != null) && (primary != secondary))
		{
			range = primary.getLocation().rangeFrom(secondary.getLocation());

			// ok, are we after slant range?
			if (doSlant())
			{
				// right, we've got to change the distance calculation to a slant
				// separation

				// what's the depth separation in metres
				double depthSep = Math.abs(primary.getLocation().getDepth()
						- secondary.getLocation().getDepth());

				// convert to degs
				double sep = MWC.Algorithms.Conversions.m2Degs(depthSep);

				// now calc the slant range
				double range2 = Math.sqrt(sep * sep + range * range);

				range = range2;
			}

			// we output the range value according to the currently selected range
			// units
			String theUnits = getUnits();

			theRng = convertRange(range, theUnits);

		}
		return theRng;
	}

	/**
	 * @return
	 */
	private boolean doSlant()
	{
		String calcSlantStr = CorePlugin.getToolParent().getProperty(
				PrefsPage.PreferenceConstants.CALC_SLANT_RANGE);
		boolean slantStr = Boolean.parseBoolean(calcSlantStr);
		return slantStr;
	}

	/** override our parent's update method so we can annotate the range if we're actually calculating the slant range.
	 * 
	 * @param primary
	 * @param secondary
	 * @param time
	 * @return
	 */
	public String update(Watchable primary, Watchable secondary, HiResDate time)
	{
		String res = super.update(primary, secondary, time);

		// are we calculating slant range?
		if (doSlant())
		{
			// do we have anything to show?
			if (!res.equals(rangeCalc.NOT_APPLICABLE))
			{
					// yes, include marker against value
					res = res + SLANT_RANGE_MARKER;
			}
		}

		return res;

	}

	public String getTitle()
	{
		String res = super.getTitle();
		if(doSlant())
		{
			res = "Slant " + res;
		}
		return res;
	}
}
