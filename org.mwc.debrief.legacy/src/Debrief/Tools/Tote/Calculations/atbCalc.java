/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
