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
			/**
			 * In this Test we are taking the measures every 2 seconds. In each measure, and
			 * in every measure we are increasing 10 units.
			 */
			final int INCREMENT = 10;
			final int MEASURE_DELTA = 10;
			// Every 10 seconds we increase 10, it is naturally 1 increment by second
			final double EXPECTED_INCREMENT = 1.0;
			// Average when all values are the same is the same value.
			final double EXPECTED_AVERAGE = 1.0;
			// Since the average is not changing, we have a 0 delta rate rate.
			final double EXPECTED_DELTA_RATE_RATE = .0;

			final String initialTime = "22/04/09 17:59:58";
			final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			final int deltaInMilli = MEASURE_DELTA * 1000;
			final HiResDate[] times = new HiResDate[15];
			final Date initialDate = format.parse(initialTime);
			for (int i = 0; i < times.length; i++) {
				times[i] = new HiResDate(initialDate.getTime() + deltaInMilli * i);
			}

			final double[] measures = new double[15];
			for (int i = 0; i < measures.length; i++) {
				measures[i] = INCREMENT * i;
			}

			final double[] rate = DeltaRateToteCalcImplementation.calculateRate(measures, times);

			for (int i = 1; i < rate.length; i++) {
				assertEquals("Delta Rate Tote Calculation Rate", EXPECTED_INCREMENT, rate[i], 1e-8);
			}

			final double[] average = DeltaRateToteCalcImplementation.caculateAverageRate(times, TIME_WINDOW, rate);

			for (int i = 1; i < average.length; i++) {
				assertEquals("Delta Rate Tote Calculation Average", EXPECTED_AVERAGE, average[i], 1e-8);
			}

			final double[] deltaRateRate = DeltaRateToteCalcImplementation.calculateDeltaRateRate(times, rate);

			for (int i = 1; i < deltaRateRate.length; i++) {
				assertEquals("Delta Rate Tote Calculation Average", EXPECTED_DELTA_RATE_RATE, deltaRateRate[i], 1e-8);
			}
		};

		public void testCalculation2() throws ParseException {
			/**
			 * In this Test we are taking the measures every 2 seconds. In each measure, and
			 * in every measure we are increasing 10 units.
			 */
			final int INCREMENT = -10;
			final int MEASURE_DELTA = 20;
			/**
			 * Every 10 seconds we decrease 20, it is a decrement of .5, resulting as a
			 * change of an absolute value of .5.
			 */
			final double EXPECTED_INCREMENT = .5;
			// Average when all values are the same is the same value.
			final double EXPECTED_AVERAGE = .5;
			// Since the average is not changing, we have a 0 delta rate rate.
			final double EXPECTED_DELTA_RATE_RATE = .0;

			final String initialTime = "22/04/09 17:59:58";
			final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			final int deltaInMilli = MEASURE_DELTA * 1000;
			final HiResDate[] times = new HiResDate[15];
			final Date initialDate = format.parse(initialTime);
			for (int i = 0; i < times.length; i++) {
				times[i] = new HiResDate(initialDate.getTime() + deltaInMilli * i);
			}

			final double[] measures = new double[15];
			for (int i = 0; i < measures.length; i++) {
				measures[i] = INCREMENT * i;
			}

			final double[] rate = DeltaRateToteCalcImplementation.calculateRate(measures, times);

			for (int i = 1; i < rate.length; i++) {
				assertEquals("Delta Rate Tote Calculation Rate", EXPECTED_INCREMENT, rate[i], 1e-8);
			}

			final double[] average = DeltaRateToteCalcImplementation.caculateAverageRate(times, TIME_WINDOW, rate);

			for (int i = 1; i < average.length; i++) {
				assertEquals("Delta Rate Tote Calculation Average", EXPECTED_AVERAGE, average[i], 1e-8);
			}

			final double[] deltaRateRate = DeltaRateToteCalcImplementation.calculateDeltaRateRate(times, rate);

			for (int i = 1; i < deltaRateRate.length; i++) {
				assertEquals("Delta Rate Tote Calculation Average", EXPECTED_DELTA_RATE_RATE, deltaRateRate[i], 1e-8);
			}
		};

		public void testCalculation3() throws ParseException {
			/**
			 * In this Test we are taking the measures every 2 seconds. In each measure, and
			 * in every measure we are increasing 10 units.
			 */
			final int INCREMENT = -10;
			final int MEASURE_DELTA = 20;
			// Since the average is not changing, we have a 0 delta rate rate.
			final double EXPECTED_DELTA_RATE_RATE = -.5;

			final String initialTime = "22/04/09 17:59:58";
			final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			final int deltaInMilli = MEASURE_DELTA * 1000;
			final HiResDate[] times = new HiResDate[15];
			final Date initialDate = format.parse(initialTime);
			for (int i = 0; i < times.length; i++) {
				times[i] = new HiResDate(initialDate.getTime() + deltaInMilli * i);
			}

			final double[] rate = new double[15];
			for (int i = 0; i < rate.length; i++) {
				rate[i] = INCREMENT * i;
			}

			final double[] deltaRateRate = DeltaRateToteCalcImplementation.calculateDeltaRateRate(times, rate);

			for (int i = 2; i < deltaRateRate.length; i++) {
				assertEquals("Delta Rate Tote Calculation Average", EXPECTED_DELTA_RATE_RATE, deltaRateRate[i], 1e-8);
			}
		};
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
