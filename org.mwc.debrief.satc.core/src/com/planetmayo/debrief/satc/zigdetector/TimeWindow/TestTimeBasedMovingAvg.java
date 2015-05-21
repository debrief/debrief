package com.planetmayo.debrief.satc.zigdetector.TimeWindow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.zigdetector.TimeWindow.average.TimeBasedMovingAverage;

public class TestTimeBasedMovingAvg
{

	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(
			"dd/MMM/yyyy HH:mm:ss");

	public static void main(String[] args)
	{

		Long[] durations = { 30L * 1000 };

		for (Long duration : durations)
		{

			final Set<DataPoint> thisData = dataPointsTest2;

			// we now wish to have the data in an array
			long times[] = new long[thisData.size()];
			double[] values = new double[thisData.size()];

			final DataPoint[] dataArray = thisData.toArray(new DataPoint[] { null });

			for (int i = 0; i < dataArray.length; i++)
			{
				times[i] = dataArray[i].getTimestamp().getTimeInMillis();
				values[i] = dataArray[i].getValue();
			}

			// ok, now for the moving average
			final TimeBasedMovingAverage movingAverage = new TimeBasedMovingAverage(
					duration);

			for (DataPoint pt : dataPointsTest2)
			{
				final long thisTime = pt.getTimestamp().getTimeInMillis();
				printAvg(movingAverage, thisTime, times, values);
			}
		}
	}

	public static void printAvg(TimeBasedMovingAverage movingAverage,
			long thisTime, long[] times, double[] values)
	{
		Double avg = movingAverage.average(thisTime, times, values);
		String msg = String.format(
				"The centered moving average for %d with period %s is %f", thisTime,
				movingAverage.getDuration(), avg);
		System.out.println(msg);
	}

	public static Calendar parse(String dateTime)
	{
		final Calendar calendar = Calendar.getInstance();
		try
		{
			calendar.setTime(FORMATTER.parse(dateTime));

		}
		catch (ParseException e)
		{
			System.out.print(e);
		}
		return calendar;
	}

	public static DataPoint parse(String dateTime, Double value)
	{
		return new DataPoint(parse(dateTime), value);
	}

	@SuppressWarnings("serial")
	static final Set<DataPoint> dataPointsTest1 = new TreeSet<DataPoint>()
	{
		{
			add(parse("07/Aug/2000 00:00:00", 001d));
			add(parse("07/Aug/2000 00:00:01", 002d));
			add(parse("07/Aug/2000 00:00:02", 003d));
			add(parse("07/Aug/2000 00:00:03", 004d));
			add(parse("07/Aug/2000 00:00:04", 005d));
			add(parse("07/Aug/2000 00:00:10", 006d));
			add(parse("07/Aug/2000 00:00:20", 007d));
			add(parse("07/Aug/2000 00:00:30", 008d));
			add(parse("07/Aug/2000 00:00:40", 009d));
			add(parse("07/Aug/2000 00:00:50", 010d));
		}
	};

	@SuppressWarnings("serial")
	static final Set<DataPoint> dataPointsTest2 = new TreeSet<DataPoint>()
	{
		{
			add(parse("07/Aug/2000 00:00:00", 001d));
			add(parse("07/Aug/2000 00:00:01", 002d));
			add(parse("07/Aug/2000 00:00:02", 003d));
			add(parse("07/Aug/2000 00:00:03", 004d));
			add(parse("07/Aug/2000 00:00:04", 005d));
			add(parse("07/Aug/2000 00:00:10", 006d));
			add(parse("07/Aug/2000 00:00:20", 007d));
			add(parse("07/Aug/2000 00:00:30", 008d));
			add(parse("07/Aug/2000 00:00:40", 009d));
			add(parse("07/Aug/2000 00:00:50", 010d));
		}
	};

	@SuppressWarnings("serial")
	static final Set<DataPoint> dataPoints = new TreeSet<DataPoint>()
	{
		{

			add(parse("07/Aug/2000 00:00:14", 70.5));
			add(parse("07/Aug/2000 00:00:22", 70.7));
			add(parse("07/Aug/2000 00:00:30", 70.8));
			add(parse("07/Aug/2000 00:00:36", 71.0));
			add(parse("07/Aug/2000 00:00:44", 71.2));
			add(parse("07/Aug/2000 00:00:52", 71.5));
			add(parse("07/Aug/2000 00:00:58", 71.7));
			add(parse("07/Aug/2000 00:01:06", 71.7));
			add(parse("07/Aug/2000 00:01:12", 71.6));
			add(parse("07/Aug/2000 00:01:20", 71.3));
			add(parse("07/Aug/2000 00:01:26", 71.1));
			add(parse("07/Aug/2000 00:01:28", 71.0));
			add(parse("07/Aug/2000 00:01:34", 70.9));
			add(parse("07/Aug/2000 00:01:38", 70.8));
			add(parse("07/Aug/2000 00:01:44", 70.6));
			add(parse("07/Aug/2000 00:01:48", 70.6));
			add(parse("07/Aug/2000 00:01:54", 70.5));
			add(parse("07/Aug/2000 00:02:04", 70.3));
			add(parse("07/Aug/2000 00:02:08", 70.2));
			add(parse("07/Aug/2000 00:02:12", 70.1));
			add(parse("07/Aug/2000 00:02:16", 70.0));
			add(parse("07/Aug/2000 00:02:20", 69.9));
			add(parse("07/Aug/2000 00:02:24", 69.9));
			add(parse("07/Aug/2000 00:02:28", 69.8));
			add(parse("07/Aug/2000 00:02:32", 69.7));
			add(parse("07/Aug/2000 00:02:34", 69.6));
			add(parse("07/Aug/2000 00:02:36", 69.6));
			add(parse("07/Aug/2000 00:02:40", 69.5));
			add(parse("07/Aug/2000 00:02:42", 69.5));
			add(parse("07/Aug/2000 00:02:44", 69.4));
			add(parse("07/Aug/2000 00:02:46", 69.3));
			add(parse("07/Aug/2000 00:02:48", 69.3));
			add(parse("07/Aug/2000 00:02:50", 69.3));
			add(parse("07/Aug/2000 00:02:52", 69.2));
			add(parse("07/Aug/2000 00:02:54", 69.2));
			add(parse("07/Aug/2000 00:02:56", 69.1));
			add(parse("07/Aug/2000 00:03:00", 69.0));
			add(parse("07/Aug/2000 00:03:02", 69.0));
			add(parse("07/Aug/2000 00:03:04", 68.9));
			add(parse("07/Aug/2000 00:03:06", 68.9));
			add(parse("07/Aug/2000 00:03:18", 68.5));
			add(parse("07/Aug/2000 00:03:32", 68.4));
			add(parse("07/Aug/2000 00:03:48", 68.7));
			add(parse("07/Aug/2000 00:04:02", 69.1));
			add(parse("07/Aug/2000 00:04:14", 69.6));
			add(parse("07/Aug/2000 00:04:32", 70.4));
			add(parse("07/Aug/2000 00:04:52", 71.5));
			add(parse("07/Aug/2000 00:05:08", 71.9));
			add(parse("07/Aug/2000 00:05:30", 71.3));
			add(parse("07/Aug/2000 00:06:02", 70.4));
			add(parse("07/Aug/2000 00:06:30", 69.8));
			add(parse("07/Aug/2000 00:07:00", 69.2));
			add(parse("07/Aug/2000 00:07:30", 69.2));
			add(parse("07/Aug/2000 00:08:06", 70.9));
			add(parse("07/Aug/2000 00:08:56", 69.7));
			add(parse("07/Aug/2000 00:09:24", 68.1));
			add(parse("07/Aug/2000 00:09:52", 69.4));
			add(parse("07/Aug/2000 00:10:14", 70.9));
			add(parse("07/Aug/2000 00:10:42", 70.8));
			add(parse("07/Aug/2000 00:11:00", 69.9));
			add(parse("07/Aug/2000 00:11:20", 69.2));
			add(parse("07/Aug/2000 00:11:32", 69.1));
			add(parse("07/Aug/2000 00:11:44", 69.5));
			add(parse("07/Aug/2000 00:11:58", 70.1));
			add(parse("07/Aug/2000 00:12:12", 70.9));
			add(parse("07/Aug/2000 00:12:28", 71.6));
			add(parse("07/Aug/2000 00:12:58", 70.2));
			add(parse("07/Aug/2000 00:13:14", 69.9));
			add(parse("07/Aug/2000 00:13:46", 70.4));
			add(parse("07/Aug/2000 00:14:06", 71.0));
			add(parse("07/Aug/2000 00:14:34", 71.2));
			add(parse("07/Aug/2000 00:14:54", 70.5));
			add(parse("07/Aug/2000 00:15:12", 69.6));
			add(parse("07/Aug/2000 00:15:32", 68.9));
			add(parse("07/Aug/2000 00:15:44", 68.8));
			add(parse("07/Aug/2000 00:16:00", 69.0));
			add(parse("07/Aug/2000 00:16:18", 69.7));
			add(parse("07/Aug/2000 00:16:30", 70.1));
			add(parse("07/Aug/2000 00:16:54", 70.8));
			add(parse("07/Aug/2000 00:17:14", 70.5));
			add(parse("07/Aug/2000 00:17:40", 69.7));
			add(parse("07/Aug/2000 00:17:58", 69.6));
			add(parse("07/Aug/2000 00:18:24", 70.2));
			add(parse("07/Aug/2000 00:18:48", 71.3));
			add(parse("07/Aug/2000 00:19:00", 71.7));
			add(parse("07/Aug/2000 00:19:16", 71.0));
			add(parse("07/Aug/2000 00:19:26", 70.3));
			add(parse("07/Aug/2000 00:19:42", 69.4));
			add(parse("07/Aug/2000 00:20:00", 68.9));
			add(parse("07/Aug/2000 00:20:14", 69.1));
			add(parse("07/Aug/2000 00:20:26", 69.8));
			add(parse("07/Aug/2000 00:20:44", 70.7));
			add(parse("07/Aug/2000 00:21:02", 71.1));
			add(parse("07/Aug/2000 00:21:18", 70.4));
			add(parse("07/Aug/2000 00:21:34", 69.4));
			add(parse("07/Aug/2000 00:21:44", 69.0));
			add(parse("07/Aug/2000 00:21:52", 68.7));
			add(parse("07/Aug/2000 00:21:56", 68.7));
			add(parse("07/Aug/2000 00:22:02", 68.8));
			add(parse("07/Aug/2000 00:22:10", 69.0));
			add(parse("07/Aug/2000 00:22:16", 69.1));
			add(parse("07/Aug/2000 00:22:24", 69.4));
			add(parse("07/Aug/2000 00:22:34", 69.8));
			add(parse("07/Aug/2000 00:22:42", 70.2));
			add(parse("07/Aug/2000 00:22:50", 70.6));
			add(parse("07/Aug/2000 00:22:52", 70.7));
			add(parse("07/Aug/2000 00:22:56", 70.9));
			add(parse("07/Aug/2000 00:22:58", 71.0));
			add(parse("07/Aug/2000 00:23:00", 71.2));
			add(parse("07/Aug/2000 00:23:30", 71.5));
			add(parse("07/Aug/2000 00:23:58", 70.4));
			add(parse("07/Aug/2000 00:24:46", 68.8));
			add(parse("07/Aug/2000 00:25:20", 70.4));
			add(parse("07/Aug/2000 00:26:06", 70.2));
			add(parse("07/Aug/2000 00:26:34", 69.3));
			add(parse("07/Aug/2000 00:27:18", 70.2));
			add(parse("07/Aug/2000 00:27:56", 71.1));
			add(parse("07/Aug/2000 00:28:54", 69.1));
			add(parse("07/Aug/2000 00:29:32", 70.1));
			add(parse("07/Aug/2000 00:30:30", 69.8));
			add(parse("07/Aug/2000 00:31:30", 61.0));
			add(parse("07/Aug/2000 00:32:10", 50.7));
			add(parse("07/Aug/2000 00:33:12", 29.2));
			add(parse("07/Aug/2000 00:34:10", 19.6));
			add(parse("07/Aug/2000 00:35:00", 19.6));
			add(parse("07/Aug/2000 00:35:44", 20.5));
			add(parse("07/Aug/2000 00:36:06", 20.5));
			add(parse("07/Aug/2000 00:36:38", 20.0));
			add(parse("07/Aug/2000 00:37:08", 19.0));
			add(parse("07/Aug/2000 00:37:34", 19.3));
			add(parse("07/Aug/2000 00:38:04", 20.2));
			add(parse("07/Aug/2000 00:38:32", 20.1));
			add(parse("07/Aug/2000 00:38:58", 19.8));
			add(parse("07/Aug/2000 00:39:24", 19.4));
			add(parse("07/Aug/2000 00:39:52", 19.0));
			add(parse("07/Aug/2000 00:40:18", 19.5));
			add(parse("07/Aug/2000 00:40:44", 19.9));
			add(parse("07/Aug/2000 00:41:06", 20.1));
			add(parse("07/Aug/2000 00:41:28", 20.2));
			add(parse("07/Aug/2000 00:41:42", 20.2));
			add(parse("07/Aug/2000 00:41:52", 20.2));
			add(parse("07/Aug/2000 00:41:58", 20.2));
			add(parse("07/Aug/2000 00:42:04", 20.1));
			add(parse("07/Aug/2000 00:42:08", 20.1));
			add(parse("07/Aug/2000 00:42:12", 20.0));
			add(parse("07/Aug/2000 00:42:14", 20.0));
			add(parse("07/Aug/2000 00:42:16", 20.0));
			add(parse("07/Aug/2000 00:42:52", 19.3));
			add(parse("07/Aug/2000 00:43:24", 19.5));
			add(parse("07/Aug/2000 00:44:22", 20.5));
			add(parse("07/Aug/2000 00:45:00", 20.2));
			add(parse("07/Aug/2000 00:45:56", 20.4));
			add(parse("07/Aug/2000 00:46:28", 19.8));
			add(parse("07/Aug/2000 00:47:32", 19.7));
			add(parse("07/Aug/2000 00:48:46", 20.5));
			add(parse("07/Aug/2000 00:49:32", 18.8));
			add(parse("07/Aug/2000 00:50:40", 21.6));
			add(parse("07/Aug/2000 00:51:50", 19.6));
		}
	};

	public static class DataPoint implements Comparable<DataPoint>
	{

		private final Calendar timestamp;

		private final Double value;

		private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		public DataPoint(Calendar timestamp, Double value)
		{
			this.timestamp = timestamp;
			this.value = value;
		}

		@Override
		public String toString()
		{
			return "DataPoint{" + "timestamp=" + sdf.format(timestamp.getTime())
					+ ", value=" + value + '}';
		}

		public Calendar getTimestamp()
		{
			return timestamp;
		}

		public Double getValue()
		{
			return value;
		}

		@Override
		public int compareTo(DataPoint o)
		{
			return this.getTimestamp().compareTo(o.getTimestamp());
		}
	}

}