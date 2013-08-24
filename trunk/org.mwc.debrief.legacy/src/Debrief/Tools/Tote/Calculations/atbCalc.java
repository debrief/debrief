package Debrief.Tools.Tote.Calculations;

import java.text.DecimalFormat;

import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

public class atbCalc extends relBearingCalc {
	public atbCalc()
	{
		super(new DecimalFormat("000.0"), "ATB", "degs");
	}

	@Override
	public double calculate(final Watchable primary, final Watchable secondary,
			final HiResDate thisTime) {
		// switch the items around
		return super.calculate(secondary, primary, thisTime);
	}

	@Override
	public String update(final Watchable primary, final Watchable secondary, final HiResDate time) {
		// switch the items around
		return super.update( primary,secondary, time);
	}
	
	
}
