package Debrief.Tools.Tote.Calculations;

import java.text.DecimalFormat;

import MWC.Algorithms.Conversions;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.Tools.Tote.TimeWindowRateCalculation;

public class courseDeltaAverageCalc extends courseRateCalc implements TimeWindowRateCalculation {

	private long windowSizeInMilli;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	public courseDeltaAverageCalc() {
		super(new DecimalFormat("000.0"), "Absolute Course Delta Rate", "degs/sec");
		windowSizeInMilli = DeltaRateToteCalcImplementation.DeltaRateToteCalcImplementationTest.TIME_WINDOW;
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	public courseDeltaAverageCalc(DecimalFormat decimalFormat, String name, String unit) {
		super(decimalFormat, name, unit);
		windowSizeInMilli = DeltaRateToteCalcImplementation.DeltaRateToteCalcImplementationTest.TIME_WINDOW;
	}

	@Override
	public double[] calculate(final Watchable[] primary, final HiResDate[] thisTime, final long windowSizeMillis) {
		final double[] measure = calculateMeasure(primary);
		final double[] deltaRate = super.calculate(primary, thisTime, windowSizeMillis);
		if (windowSizeMillis == 0) {
			return deltaRate; // We don't want to do the average;
		}
		return DeltaRateToteCalcImplementation.caculateAverageRate(measure, thisTime, windowSizeMillis, deltaRate);
	}

	@Override
	public long getWindowSizeMillis() {
		return windowSizeInMilli;
	}

	@Override
	public void setWindowSizeMillis(long newWindowSize) {
		this.windowSizeInMilli = newWindowSize;
	}
}
