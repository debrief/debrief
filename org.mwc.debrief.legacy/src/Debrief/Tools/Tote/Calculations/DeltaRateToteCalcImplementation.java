package Debrief.Tools.Tote.Calculations;

import static org.junit.Assert.assertArrayEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import MWC.GenericData.HiResDate;
import junit.framework.TestCase;

public class DeltaRateToteCalcImplementation {

	public static class DeltaRateToteCalcImplementationTest extends TestCase {

		public final static long TIME_WINDOW = 1000L * 60; // 60 seconds;
		
		public void testDummyTest(){
			
		}
	}

	/**
	 * Calculate the average of the rate given. Please, be aware that the first
	 * value is expected to be always zero, because the measure at index -1 is
	 * undefined.
	 * 
	 * @param time             Time when the measures were taken.
	 * @param windowSizeMillis Window Size to calculate the average
	 * @param deltaRate        Rate of the Measures. (First value will be assumed as
	 *                         0).
	 * @return Average of the delta of the rates
	 */
	public static double[] caculateAverageRate(final HiResDate[] time, final long windowSizeMillis,
			final double[] deltaRate) {
		// Let's calculate the delta rate in a period.
		final double[] deltaInPeriod = new double[time.length];
		final int[] countInPeriod = new int[time.length];
		double currentSum = 0;

		/**
		 * IMPORTANT NOTE, in case that you want to include the first element in the
		 * calculation, change the following indexes to 0.
		 */

		int minSumIndex = 1;
		int maxSumIndex = 1;
		for (int i = 1; i < deltaInPeriod.length; i++) {
			final long earlest = time[i].getMicros() - windowSizeMillis * 1000;
			final long latest = time[i].getMicros() + windowSizeMillis * 1000;
			while (minSumIndex < deltaRate.length && time[minSumIndex].getMicros() <= earlest) {
				if (minSumIndex != maxSumIndex) {
					currentSum -= deltaRate[minSumIndex];
				}
				++minSumIndex;

				// In case the minimum pass the latest
				maxSumIndex = Math.max(maxSumIndex, minSumIndex);
			}

			while (maxSumIndex < deltaRate.length && time[maxSumIndex].getMicros() < latest) {
				currentSum += deltaRate[maxSumIndex];
				++maxSumIndex;
			}
			deltaInPeriod[i] = currentSum;
			countInPeriod[i] = maxSumIndex - minSumIndex;
		}

		// Let's calculate the average
		final double[] average = new double[time.length];
		for (int i = 1; i < average.length; i++) {
			average[i] = deltaInPeriod[i] / countInPeriod[i];
		}
		return average;
	}

	public static double[] calculateDeltaRateRate(final HiResDate[] time, final double[] average) {
		// Let's calculate the delta rate
		final double[] deltaRateRate = new double[time.length];
		for (int i = 0; i < deltaRateRate.length - 2; i++) {
			deltaRateRate[i + 2] = (average[i + 2] - average[i + 1])
					/ ((time[i + 2].getMicros() - time[i + 1].getMicros()) / 1000.0 / 1000.0);
		}
		return deltaRateRate;
	}

	public static double[] calculateRate(final double[] measure, final HiResDate[] time) {
		if (measure.length > 2 && time.length == measure.length) {
			// Let's calculate the delta rate
			final double[] deltaRate = new double[measure.length];
			for (int i = 1; i < deltaRate.length; i++) {
				deltaRate[i] = Math.abs((measure[i] - measure[i - 1])
						/ ((time[i].getMicros() - time[i - 1].getMicros()) / 1000.0 / 1000.0));
			}
			return deltaRate;
		}
		return new double[] {};
	}

}
