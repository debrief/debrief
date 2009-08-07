package Debrief.Tools.Tote.Calculations;

import java.text.DecimalFormat;

import Debrief.Tools.Tote.Watchable;
import MWC.GenericData.HiResDate;

public class atbCalc extends relBearingCalc {
	public atbCalc()
	{
		super(new DecimalFormat("000.0"), "ATB", "degs");
	}

	@Override
	public double calculate(Watchable primary, Watchable secondary,
			HiResDate thisTime) {
		// switch the items around
		return super.calculate(secondary, primary, thisTime);
	}

	@Override
	public String update(Watchable primary, Watchable secondary, HiResDate time) {
		// switch the items around
		return super.update( primary,secondary, time);
	}
	
	
}
