package org.mwc.cmap.gt2plot.calc;

import interfaces.IEarthModelProvider;
import MWC.Algorithms.EarthModel;

/**
 * Provides GeoTools Calculator adapter as Earth model;
 * Is used in the Extension of EarthModelProvider Extension Point.
 */
public class GeoCalculatorProvider implements IEarthModelProvider {

	@Override
	public EarthModel getEarthModel() {
		return new GeoCalculatorAdapter();
	}

}
