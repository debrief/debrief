package Debrief.Tools.Tote.Calculations;

import java.text.DecimalFormat;

import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.Tools.Tote.TimeWindowRateCalculation;

public class speedDeltaAverageCalc extends speedRateCalc implements TimeWindowRateCalculation{
	
	private long windowSizeInMilli;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	public speedDeltaAverageCalc() {
		super(new DecimalFormat("00.00"), "Average Speed Delta Rate (abs)", "Knots/secs");
		windowSizeInMilli = DeltaRateToteCalcImplementation.DeltaRateToteCalcImplementationTest.TIME_WINDOW;
	}
	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	public speedDeltaAverageCalc(DecimalFormat decimalFormat, String description, String units) {
		super(decimalFormat, description, units);
		windowSizeInMilli = DeltaRateToteCalcImplementation.DeltaRateToteCalcImplementationTest.TIME_WINDOW;
	}

	@Override
	public final double calculate(final Watchable primary, final Watchable secondary, final HiResDate thisTime) {
		double res = 0.0;
		if (primary != null) {
			res = primary.getSpeed();
		}
		return res;
	}

	public double[] calculate(final Watchable[] primary, final HiResDate[] thisTime, final long windowSizeMillis) {
		final double[] measure = calculateMeasure(primary);
		final double[] deltaRate = super.calculate(primary, thisTime, windowSizeMillis);
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
