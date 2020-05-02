package Debrief.Tools.Tote.Calculations;

import static org.junit.Assert.assertArrayEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import MWC.GenericData.HiResDate;
import junit.framework.TestCase;

public class DeltaRateToteCalcImplementation {

	public static class DeltaRateToteCalcImplementationTest extends TestCase {

		public void testCalculation() throws ParseException {
			final long timeWindow = 1000L * 60; // 60 seconds;
			final String timeText = "22/04/09 17:59:58," + "22/04/09 18:00:06," + "22/04/09 18:00:14,"
					+ "22/04/09 18:00:22," + "22/04/09 18:00:30," + "22/04/09 18:00:37," + "22/04/09 18:00:46,"
					+ "22/04/09 18:00:54," + "22/04/09 18:01:02," + "22/04/09 18:01:10," + "22/04/09 18:01:18,"
					+ "22/04/09 18:01:26," + "22/04/09 18:01:34";
			final double[] measures = new double[] { 289.78, 289.61, 289.37, 289.18, 289, 289, 289.08, 289.38, 289.52,
					289.6, 289.7, 289.73, 289.69 };
			final double[] expectedAnswer = new double[] { 0.000226253464349, 0.000222222222222, 6.61375661376134E-05,
					-6.29154795821912E-05, 0, 0, -1.40996569567961E-05, 0.000298735655878, 0.000501339929911,
					0.000499370118418, 0.000598702443941, };

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

			final double[] deltaRateRate = DeltaRateToteCalcImplementation.calculate(measures, time, timeWindow);

			assertArrayEquals("Delta Rate Tote Calculation Rate", expectedAnswer, deltaRateRate, 1e-5);
		};
	}

	public static double[] calculate(final double[] measure, final HiResDate[] time, final long windowSizeMillis) {
		if (measure.length > 2 && time.length == measure.length) {

			// Let's calculate the delta rate
			final double[] deltaRate = new double[measure.length];
			for (int i = 1; i < deltaRate.length; i++) {
				deltaRate[i] = (measure[i] - measure[i - 1]) / ((time[i].getMicros() - time[i - 1].getMicros()) / 1000.0 / 1000.0 - 1);
			}

			// Let's calculate the delta rate in a period.
			final double[] deltaInPeriod = new double[measure.length];
			final int[] countInPeriod = new int[measure.length];
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
			final double[] average = new double[measure.length];
			for (int i = 1; i < average.length; i++) {
				average[i] = deltaInPeriod[i] / countInPeriod[i];
			}

			// Let's calculate the delta rate
			final double[] deltaRateRate = new double[measure.length - 2];
			for (int i = 0; i < deltaRateRate.length; i++) {
				deltaRateRate[i] = (average[i + 2] - average[i + 1])
						/ ((time[i + 2].getMicros() - time[i + 1].getMicros()) / 1000.0 / 1000.0 - 1);
			}
			return deltaRateRate;
		}
		return new double[] {};
	}

}
