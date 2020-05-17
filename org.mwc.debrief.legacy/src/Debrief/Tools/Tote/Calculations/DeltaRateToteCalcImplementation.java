package Debrief.Tools.Tote.Calculations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import MWC.GenericData.HiResDate;
import junit.framework.TestCase;

public class DeltaRateToteCalcImplementation {

	public static class DeltaRateToteCalcImplementationTest extends TestCase {

		public final static long TIME_WINDOW = 1000L * 60; // 60 seconds;

		public void testCalculation() throws ParseException {
			final String timeText = "22/04/09 17:59:58," + "22/04/09 18:00:06," + "22/04/09 18:00:14,"
					+ "22/04/09 18:00:22," + "22/04/09 18:00:30," + "22/04/09 18:00:37," + "22/04/09 18:00:46,"
					+ "22/04/09 18:00:54," + "22/04/09 18:01:02," + "22/04/09 18:01:10," + "22/04/09 18:01:18,"
					+ "22/04/09 18:01:26," + "22/04/09 18:01:34";
			final double[] measures = new double[] { 289.78, 289.61, 289.37, 289.18, 289, 289, 289.08, 289.38, 289.52,
					289.6, 289.7, 289.73, 289.69 };
			final double[] expectedDeltaRateAnswer = new double[] { 0, 0, 0.000226253464349, 0.000222222222222,
					6.61375661376134E-05, -6.29154795821912E-05, 0, 0, -1.40996569567961E-05, 0.000298735655878,
					0.000501339929911, 0.000499370118418, 0.000598702443941, };

			final double[] expectedRate = new double[] { 0, -0.024285714285709, -0.034285714285716, -0.027142857142857,
					-0.025714285714287, 0, 0.0099999999999, 0.042857142857145, 0.019999999999998, 0.011428571428577,
					0.01428571428571, 0.004285714285719, -0.005714285714289 };

			final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

			final String[] timeTokenized = timeText.split(",");
			final Date[] timeDate = new Date[timeTokenized.length];
			for (int i = 0; i < timeTokenized.length; i++) {
				timeDate[i] = format.parse(timeTokenized[i]);
			}

			final HiResDate[] time = new HiResDate[timeDate.length];
			for (int i = 0; i < time.length; i++) {
				time[i] = new HiResDate(timeDate[i]);
			}

			final double[] rate = DeltaRateToteCalcImplementation.calculateRate(measures, time);

			// TODO: reinstate test
			// assertArrayEquals("Delta Rate Tote Calculation Rate", expectedRate, rate,
			// 1e-5);

			final double[] deltaRateRate = DeltaRateToteCalcImplementation.calculateDeltaRateRate(time, rate);

			// TODO: reinstate test
			// assertArrayEquals("Delta Rate Tote Calculation Rate",
			// expectedDeltaRateAnswer, deltaRateRate, 1e-5);
		};
	}

	public static double[] caculateAverageRate(final HiResDate[] time, final long windowSizeMillis,
			final double[] deltaRate) {
		// Let's calculate the delta rate in a period.
		final double[] deltaInPeriod = new double[time.length];
		final int[] countInPeriod = new int[time.length];
		double currentSum = 0;
		int minSumIndex = 0;
		int maxSumIndex = 0;
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
					/ ((time[i + 2].getMicros() - time[i + 1].getMicros()) / 1000.0 / 1000.0 - 1);
		}
		return deltaRateRate;
	}

	public static double[] calculateRate(final double[] measure, final HiResDate[] time) {
		if (measure.length > 2 && time.length == measure.length) {
			// Let's calculate the delta rate
			final double[] deltaRate = new double[measure.length];
			for (int i = 1; i < deltaRate.length; i++) {
				deltaRate[i] = Math.abs((measure[i] - measure[i - 1])
						/ ((time[i].getMicros() - time[i - 1].getMicros()) / 1000.0 / 1000.0 - 1));
			}
			return deltaRate;
		}
		return new double[] {};
	}

}
