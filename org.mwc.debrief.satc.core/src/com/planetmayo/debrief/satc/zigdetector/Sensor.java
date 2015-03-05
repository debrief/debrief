package com.planetmayo.debrief.satc.zigdetector;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import au.com.bytecode.opencsv.CSVReader;

public class Sensor
{

	private long[] times;
	private double[] bearings;
	private double[] rawBearings;
	private final Random genny = new Random();

	public Sensor(final String path) throws IOException
	{

		final DateTimeFormatter formatter = DateTimeFormat
				.forPattern("yyMMdd HHmmss");

		// schema
		// 100112 120000,12000.00 ,24000.00 ,0.00 ,-153.43

		// Reading the csv file but ignoring the first column since it contains
		// headings the result is stored in a list
		CSVReader csvReader = null;

		try
		{
			csvReader = new CSVReader(new FileReader(path), ',', '\'', 1);
			final List<String[]> content = csvReader.readAll();
			// variable to hold each row of the List while iterating through it
			String[] row = null;

			// counter used to populate the variables with data from the csv
			// file
			int counter = 0;

			// initializing the variables to hold data in the csv file
			bearings = new double[content.size()];
			rawBearings = new double[content.size()];
			times = new long[content.size()];

			for (final Object object : content)
			{
				row = (String[]) object;
				/* parsing data from the list to the variables */
				final String thisDate = row[0].toString();
				times[counter] = formatter.parseDateTime(thisDate).getMillis();
				bearings[counter] = (Double.parseDouble(row[4].toString()));
				rawBearings[counter] = (Double.parseDouble(row[4].toString()));
				counter++;
			}
		}
		finally
		{
			if (csvReader != null)
			{
				csvReader.close();
			}
		}
	}

	/**
	 * generate a new set of bearings by applying the provided SD to the raw
	 * bearings
	 * 
	 * @param sd
	 */
	public void applyError(final double sd)
	{
		// loop through the bearings
		for (int i = 0; i < rawBearings.length; i++)
		{
			final double thisB = rawBearings[i];
			// calc a new error
			final double thisBearing = thisB + genny.nextGaussian() * sd;

			// and store it
			bearings[i] = thisBearing;

		}
	}

	public List<Double> extractBearings(final Long start, final Long end)
	{
		final List<Double> thisBearings = new ArrayList<Double>();
		// ok, loop through our data
		for (int i = 0; i < times.length; i++)
		{
			final long thisT = times[i];
			if ((thisT >= start) && (thisT <= end))
			{
				thisBearings.add(bearings[i]);
			}

		}
		return thisBearings;
	}

	public List<Long> extractTimes(final Long start, final Long end)
	{
		final List<Long> thisTimes = new ArrayList<Long>();
		// ok, loop through our data
		for (int i = 0; i < times.length; i++)
		{
			final long thisT = times[i];
			if ((thisT >= start) && (thisT <= end))
			{
				thisTimes.add(times[i]);
			}

		}
		return thisTimes;
	}

	public double[] getBearings()
	{
		return bearings;
	}

	public long[] getTimes()
	{
		return times;
	}

}
