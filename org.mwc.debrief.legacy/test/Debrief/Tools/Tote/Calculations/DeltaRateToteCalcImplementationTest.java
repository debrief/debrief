package Debrief.Tools.Tote.Calculations;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.Tools.Tote.Calculations.DeltaRateToteCalcImplementation.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.Tools.Tote.Calculations.DeltaRateToteCalcImplementation.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import MWC.GenericData.HiResDate;
import org.junit.jupiter.api.Test;

public class DeltaRateToteCalcImplementationTest {


	@Test
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
			assertEquals(EXPECTED_INCREMENT, rate[i], 1e-8, "Delta Rate Tote Calculation Rate");
		}

		final double[] average = DeltaRateToteCalcImplementation.caculateAverageRate(times, TIME_WINDOW, rate);

		for (int i = 1; i < average.length; i++) {
			assertEquals(EXPECTED_AVERAGE, average[i], 1e-8, "Delta Rate Tote Calculation Average");
		}

		final double[] deltaRateRate = DeltaRateToteCalcImplementation.calculateDeltaRateRate(times, rate);

		for (int i = 1; i < deltaRateRate.length; i++) {
			assertEquals(EXPECTED_DELTA_RATE_RATE, deltaRateRate[i], 1e-8, "Delta Rate Tote Calculation Average");
		}
	};

	@Test
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
			assertEquals(EXPECTED_INCREMENT, rate[i], 1e-8, "Delta Rate Tote Calculation Rate");
		}

		final double[] average = DeltaRateToteCalcImplementation.caculateAverageRate(times, TIME_WINDOW, rate);

		for (int i = 1; i < average.length; i++) {
			assertEquals(EXPECTED_AVERAGE, average[i], 1e-8, "Delta Rate Tote Calculation Average");
		}

		final double[] deltaRateRate = DeltaRateToteCalcImplementation.calculateDeltaRateRate(times, rate);

		for (int i = 1; i < deltaRateRate.length; i++) {
			assertEquals(EXPECTED_DELTA_RATE_RATE, deltaRateRate[i], 1e-8, "Delta Rate Tote Calculation Average");
		}
	};

	@Test
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
			assertEquals(EXPECTED_DELTA_RATE_RATE, deltaRateRate[i], 1e-8, "Delta Rate Tote Calculation Average");
		}
	};
}
