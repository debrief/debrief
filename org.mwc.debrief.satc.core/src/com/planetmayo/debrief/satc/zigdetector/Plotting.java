package com.planetmayo.debrief.satc.zigdetector;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;

public class Plotting
{

	public static void addAverageCourse(final XYPlot ownshipPlot,
			final double[] averageCourses, final double[] averageSpeeds,
			final long[] times)
	{
		// TODO Auto-generated method stub
		final TimeSeriesCollection dataCrse = new TimeSeriesCollection();
		final TimeSeriesCollection dataSpeed = new TimeSeriesCollection();

		final TimeSeries data1 = new TimeSeries("Avg Course");
		final TimeSeries data2 = new TimeSeries("Avg Speed");

		// obtain the data for the points
		for (int i = 0; i < times.length; i++)
		{
			final long thisTime = times[i];
			data1.add(new FixedMillisecond(thisTime), averageCourses[i]);
			data2.add(new FixedMillisecond(thisTime), averageSpeeds[i]);
		}
		dataCrse.addSeries(data1);
		dataSpeed.addSeries(data2);
		ownshipPlot.setDataset(2, dataCrse);
		ownshipPlot.setDataset(3, dataSpeed);

		final XYLineAndShapeRenderer lineRenderer1 = new XYLineAndShapeRenderer(
				true, true);
		lineRenderer1.setSeriesPaint(0, Color.green);
		lineRenderer1.setSeriesShape(0, ShapeUtilities.createDownTriangle(2f));
		ownshipPlot.setRenderer(2, lineRenderer1);

		final XYLineAndShapeRenderer lineRenderer2 = new XYLineAndShapeRenderer(
				true, true);
		lineRenderer2.setSeriesPaint(0, Color.green);
		lineRenderer2.setSeriesShape(0, ShapeUtilities.createUpTriangle(2f));
		ownshipPlot.setRenderer(3, lineRenderer2);

		ownshipPlot.mapDatasetToRangeAxis(3, 1);

	}

	public static void addLegResults(final CombinedDomainXYPlot parent,
			final TimeSeriesCollection errorValues, final List<Long> valueMarkers)
	{

		final JFreeChart chart = ChartFactory.createTimeSeriesChart("Leg Results", // String
				// title,
				"Time", // String timeAxisLabel
				"Error", // String valueAxisLabel,
				errorValues, // XYDataset dataset,
				true, // include legend
				true, // tooltips
				false); // urls

		final XYPlot xyPlot = (XYPlot) chart.getPlot();
		xyPlot.setDomainCrosshairVisible(true);
		xyPlot.setRangeCrosshairVisible(true);
		final DateAxis axis = (DateAxis) xyPlot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));

		final NumberAxis rangeAxis = new LogarithmicAxis("Log(error)");
		xyPlot.setRangeAxis(rangeAxis);

		final XYLineAndShapeRenderer lineRenderer1 = new XYLineAndShapeRenderer(
				true, true);
		xyPlot.setRenderer(0, lineRenderer1);

		// let's try the shading
		if (valueMarkers != null)
		{
			plotMarkers(xyPlot, valueMarkers);
		}

		xyPlot.getRenderer().setBaseSeriesVisibleInLegend(true);

		parent.add(xyPlot);
	}

	public static void clearLegMarkers(final XYPlot xyPlot, final XYPlot bearingPlot)
	{
		if (xyPlot != null)
		{
			xyPlot.clearDomainMarkers();
		}
		if (bearingPlot != null)
		{
			bearingPlot.clearDomainMarkers();
		}
	}

	public static XYPlot createBearingPlot(final CombinedDomainXYPlot parent)
	{

		final JFreeChart chart = ChartFactory.createTimeSeriesChart("Bearing Data", // String
				// title,
				"Time", // String timeAxisLabel
				"Bearing", // String valueAxisLabel,
				null, // XYDataset dataset,
				true, // include legend
				true, // tooltips
				false); // urls

		parent.add(chart.getXYPlot());

		return chart.getXYPlot();
	}

	public static CombinedDomainXYPlot createPlot()
	{
		final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(
				"Domain"));
		plot.setGap(10.0);
		final DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
		return plot;
	}

	public static void plotCombinedVesselData(final CombinedDomainXYPlot parent,
			final Track ownshipTrack, final List<LegOfData> ownshipLegs,
			final Color ownshipCol, final Track tgtTrack,
			final List<LegOfData> tgtLegs, final Color tgtCol,
			final List<Long> turnEstimates, final Long endTime)
	{

		final TimeSeriesCollection courseColl = new TimeSeriesCollection();
		final TimeSeriesCollection speedColl = new TimeSeriesCollection();

		final TimeSeries oCourse = new TimeSeries("O/S Course");
		final TimeSeries oSpeed = new TimeSeries("O/S Speed");
		final TimeSeries tCourse = new TimeSeries("Tgt Course");
		final TimeSeries tSpeed = new TimeSeries("Tgt Speed");

		final double[] oCourses = ownshipTrack.getCourses();
		final double[] oSpeeds = ownshipTrack.getSpeeds();
		final long[] oTimes = ownshipTrack.getDates();
		final double[] tCourses = tgtTrack.getCourses();
		final double[] tSpeeds = tgtTrack.getSpeeds();

		// obtain the data for the points
		for (int i = 0; i < oTimes.length; i++)
		{
			final long thisTime = oTimes[i];
			if (endTime == null || thisTime <= endTime)
			{
				oCourse.add(new FixedMillisecond(thisTime), oCourses[i]);
				oSpeed.add(new FixedMillisecond(thisTime), oSpeeds[i]);
				tCourse.add(new FixedMillisecond(thisTime), tCourses[i]);
				tSpeed.add(new FixedMillisecond(thisTime), tSpeeds[i]);
			}
		}
		courseColl.addSeries(oCourse);
		speedColl.addSeries(oSpeed);
		courseColl.addSeries(tCourse);
		speedColl.addSeries(tSpeed);

		final JFreeChart chart = ChartFactory.createTimeSeriesChart(null, // String
				"Time", // String timeAxisLabel
				"Course", // String valueAxisLabel,
				courseColl, // XYDataset dataset,
				true, // include legend
				true, // tooltips
				false); // urls

		final XYPlot xyPlot = (XYPlot) chart.getPlot();
		xyPlot.setDomainCrosshairVisible(true);
		xyPlot.setRangeCrosshairVisible(true);
		final DateAxis axis = (DateAxis) xyPlot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));

		final NumberAxis axis2 = new NumberAxis("Speed");
		xyPlot.setRangeAxis(1, axis2);
		xyPlot.setDataset(1, speedColl);
		xyPlot.mapDatasetToRangeAxis(1, 1);

		final XYLineAndShapeRenderer lineRenderer1 = new XYLineAndShapeRenderer(
				true, true);
		lineRenderer1.setSeriesPaint(0, Color.blue);
		lineRenderer1.setSeriesPaint(1, Color.red);
		lineRenderer1.setSeriesShape(0, ShapeUtilities.createUpTriangle(2f));
		lineRenderer1.setSeriesShape(1, ShapeUtilities.createDownTriangle(2f));
		final XYLineAndShapeRenderer lineRenderer2 = new XYLineAndShapeRenderer(
				true, true);
		lineRenderer2.setSeriesPaint(0, Color.blue);
		lineRenderer2.setSeriesPaint(1, Color.red);
		lineRenderer2.setSeriesShape(0, ShapeUtilities.createDownTriangle(2f));
		lineRenderer2.setSeriesShape(1, ShapeUtilities.createUpTriangle(2f));

		// ok, and store them
		xyPlot.setRenderer(0, lineRenderer1);
		xyPlot.setRenderer(1, lineRenderer2);

		// let's try the shading
		if (ownshipLegs != null)
		{
			plotLegPeriods(xyPlot, ownshipCol, ownshipLegs);
		}

		if (tgtLegs != null)
		{
			plotLegPeriods(xyPlot, tgtCol, tgtLegs);
		}

		// let's try the shading
		if (turnEstimates != null)
		{
			plotMarkers(xyPlot, turnEstimates);
		}

		parent.add(xyPlot);
	}

	/**
	 * @param xyPlot
	 * @param transColor
	 * @param ownshipLegs
	 */
	public static void plotLegPeriods(final XYPlot xyPlot,
			final Color transColor, final List<LegOfData> ownshipLegs)
	{
		if (xyPlot == null)
		{
			return;
		}

		// clear any domain markesr
		xyPlot.clearDomainMarkers();

		final Iterator<LegOfData> iter = ownshipLegs.iterator();
		while (iter.hasNext())
		{
			final LegOfData leg = iter.next();
			final Marker bst = new IntervalMarker(leg.getStart(), leg.getEnd(),
					transColor, new BasicStroke(2.0f), null, null, 1.0f);
			bst.setLabel(leg.getName());
			bst.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
			bst.setLabelFont(new Font("SansSerif", Font.ITALIC + Font.BOLD, 10));
			bst.setLabelTextAnchor(TextAnchor.BASELINE_RIGHT);
			xyPlot.addDomainMarker(bst, Layer.BACKGROUND);
		}
	}

	/**
	 * Plot a series of vertical markers
	 * 
	 * @param xyPlot
	 * @param valueMarkers
	 */
	public static void plotMarkers(final XYPlot xyPlot,
			final List<Long> valueMarkers)
	{
		final Iterator<Long> iter = valueMarkers.iterator();
		while (iter.hasNext())
		{
			final Long leg = iter.next();
			final Marker bst = new ValueMarker(leg, Color.gray,
					new BasicStroke(3.0f), null, null, 1.0f);
			bst.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
			bst.setLabelFont(new Font("SansSerif", Font.ITALIC + Font.BOLD, 10));
			bst.setLabelTextAnchor(TextAnchor.BASELINE_RIGHT);
			xyPlot.addDomainMarker(bst, Layer.BACKGROUND);
		}
	}

	public static XYPlot plotPQData(final CombinedDomainXYPlot parent,
			final String title, final TimeSeriesCollection calculated,
			final TimeSeriesCollection fitted)
	{

		final JFreeChart chart = ChartFactory.createTimeSeriesChart(title, // String
				"Time", // String timeAxisLabel
				title, // String valueAxisLabel,
				calculated, // XYDataset dataset,
				true, // include legend
				false, // tooltips
				false); // urls

		final XYPlot xyPlot = (XYPlot) chart.getPlot();
		xyPlot.setDomainCrosshairVisible(true);
		xyPlot.setRangeCrosshairVisible(true);
		final DateAxis axis = (DateAxis) xyPlot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));

		// final NumberAxis axis2 = new NumberAxis(title + "Speed");
		// xyPlot.setRangeAxis(1, axis2);
		// xyPlot.setDataset(1, speedColl);
		// xyPlot.mapDatasetToRangeAxis(1, 1);

		final XYLineAndShapeRenderer lineRenderer1 = new XYLineAndShapeRenderer(
				true, true);
		xyPlot.setRenderer(lineRenderer1);

		parent.add(xyPlot);

		return xyPlot;
	}

	public static void plotSensorData(final CombinedDomainXYPlot parent,
			final long[] times, final double[] bearings, final TimeSeries rmsScores)
	{

		final TimeSeriesCollection scores = new TimeSeriesCollection();
		scores.addSeries(rmsScores);

		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries bSeries = new TimeSeries("Bearings");
		dataset.addSeries(bSeries);
		for (int i = 0; i < bearings.length; i++)
		{
			bSeries.add(new FixedMillisecond(times[i]), bearings[i]);
		}

		final JFreeChart chart = ChartFactory.createTimeSeriesChart("Leg Results", // String
				// title,
				"Time", // String timeAxisLabel
				"Bearing", // String valueAxisLabel,
				dataset, // XYDataset dataset,
				true, // include legend
				true, // tooltips
				false); // urls

		final XYPlot xyPlot = (XYPlot) chart.getPlot();
		xyPlot.setDomainCrosshairVisible(true);
		xyPlot.setRangeCrosshairVisible(true);
		final DateAxis axis = (DateAxis) xyPlot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));

		// final NumberAxis rangeAxis = new LogarithmicAxis("Log(error)");
		// xyPlot.setRangeAxis(rangeAxis);
		final NumberAxis axis2 = new LogarithmicAxis("RMS Error");
		xyPlot.setRangeAxis(1, axis2);
		xyPlot.setDataset(1, scores);
		xyPlot.mapDatasetToRangeAxis(1, 1);

		final XYLineAndShapeRenderer lineRenderer2 = new XYLineAndShapeRenderer(
				false, true);
		xyPlot.setRenderer(1, lineRenderer2);
		xyPlot.getRenderer().setBaseSeriesVisibleInLegend(false);
		lineRenderer2.setSeriesPaint(0, Color.green);

		// final NumberAxis rangeAxis = new LogarithmicAxis("Log(error)");
		// xyPlot.setRangeAxis(rangeAxis);

		final XYLineAndShapeRenderer lineRenderer1 = new XYLineAndShapeRenderer(
				true, true);
		xyPlot.setRenderer(0, lineRenderer1);
		xyPlot.getRenderer().setBaseSeriesVisibleInLegend(true);

		parent.add(xyPlot);
	}

	public static XYPlot plotSingleVesselData(final CombinedDomainXYPlot parent,
			final String title, final Track ownshipTrack, final Color color,
			final List<Long> valueMarkers, final Long endTime)
	{

		final TimeSeriesCollection dataCrse = new TimeSeriesCollection();
		final TimeSeriesCollection dataSpeed = new TimeSeriesCollection();

		final TimeSeries data1 = new TimeSeries(title + "Course");
		final TimeSeries data2 = new TimeSeries(title + "Speed");

		if (ownshipTrack != null)
		{
			final double[] courses = ownshipTrack.getCourses();
			final double[] speeds = ownshipTrack.getSpeeds();
			final long[] times = ownshipTrack.getDates();

			// obtain the data for the points
			for (int i = 0; i < times.length; i++)
			{
				final long thisTime = times[i];
				if (endTime == null || thisTime <= endTime)
				{
					data1.add(new FixedMillisecond(thisTime), courses[i]);
					data2.add(new FixedMillisecond(thisTime), speeds[i]);
				}
			}
			dataCrse.addSeries(data1);
			dataSpeed.addSeries(data2);
		}

		final JFreeChart chart = ChartFactory.createTimeSeriesChart(title, // String
				"Time", // String timeAxisLabel
				title + "Course", // String valueAxisLabel,
				dataCrse, // XYDataset dataset,
				true, // include legend
				true, // tooltips
				false); // urls

		final XYPlot xyPlot = (XYPlot) chart.getPlot();
		xyPlot.setDomainCrosshairVisible(true);
		xyPlot.setRangeCrosshairVisible(true);
		final DateAxis axis = (DateAxis) xyPlot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));

		final NumberAxis axis2 = new NumberAxis(title + "Speed");
		xyPlot.setRangeAxis(1, axis2);
		xyPlot.setDataset(1, dataSpeed);
		xyPlot.mapDatasetToRangeAxis(1, 1);

		final XYLineAndShapeRenderer lineRenderer1 = new XYLineAndShapeRenderer(
				true, true);
		lineRenderer1.setSeriesPaint(0, color);
		lineRenderer1.setSeriesShape(0, ShapeUtilities.createUpTriangle(2f));

		final XYLineAndShapeRenderer lineRenderer2 = new XYLineAndShapeRenderer(
				true, true);
		lineRenderer2.setSeriesPaint(0, color);
		lineRenderer2.setSeriesShape(0, ShapeUtilities.createDownTriangle(2f));

		// ok, and store them
		xyPlot.setRenderer(0, lineRenderer1);
		xyPlot.setRenderer(1, lineRenderer2);

		parent.add(xyPlot);

		return xyPlot;
	}

	public static void showBearings(final XYPlot xyPlot, final long[] times,
			final double[] bearings, final TimeSeries rmsScores, List<LegOfData> legList)
	{

		if (xyPlot == null)
		{
			return;
		}
		
		// clear the datasets
		xyPlot.setDataset(null);

		final TimeSeriesCollection scores = new TimeSeriesCollection();
		if (rmsScores != null)
		{
			scores.addSeries(rmsScores);
		}

		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries bSeries = new TimeSeries("Bearings");
		dataset.addSeries(bSeries);
		for (int i = 0; i < bearings.length; i++)
		{
			bSeries.add(new FixedMillisecond(times[i]), bearings[i]);
		}

		// store the data
		xyPlot.setDataset(dataset);
		xyPlot.setDomainCrosshairVisible(true);
		xyPlot.setRangeCrosshairVisible(true);
		// final DateAxis axis = (DateAxis) xyPlot.getDomainAxis();
		// axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));

		final NumberAxis axis2 = new NumberAxis("RMS Error (%)");
		xyPlot.setRangeAxis(1, axis2);
		xyPlot.setDataset(1, scores);
		xyPlot.mapDatasetToRangeAxis(1, 1);
		axis2.setAutoRange(false);
		axis2.setAutoRange(true);

		final XYLineAndShapeRenderer lineRenderer2 = new XYLineAndShapeRenderer(
				false, true);
		xyPlot.setRenderer(1, lineRenderer2);
		xyPlot.getRenderer().setBaseSeriesVisibleInLegend(false);
		lineRenderer2.setSeriesPaint(0, Color.green);

		final XYLineAndShapeRenderer lineRenderer1 = new XYLineAndShapeRenderer(
				true, true);
		xyPlot.setRenderer(0, lineRenderer1);
		xyPlot.getRenderer().setBaseSeriesVisibleInLegend(true);
		
		// and the leg markers
		final Iterator<LegOfData> iter = legList.iterator();
		while (iter.hasNext())
		{
			final LegOfData leg = iter.next();
			final Marker bst = new IntervalMarker(leg.getStart(), leg.getEnd(),
					new Color(1.0f, 0f, 0f, 0.2f), new BasicStroke(2.0f), null, null, 1.0f);
			bst.setLabel(leg.getName());
			bst.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
			bst.setLabelFont(new Font("SansSerif", Font.ITALIC + Font.BOLD, 10));
			bst.setLabelTextAnchor(TextAnchor.BASELINE_RIGHT);
			xyPlot.addDomainMarker(bst, Layer.BACKGROUND);
		}
	}

}
