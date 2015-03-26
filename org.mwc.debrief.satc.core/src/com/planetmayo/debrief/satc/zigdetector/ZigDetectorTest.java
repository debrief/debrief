package com.planetmayo.debrief.satc.zigdetector;
/**
 * Created by Bill on 1/23/2015.
 *A project to determine the Linear regression for maritime analytic using java
 * Modules such as apache commons maths libraries and Jfreechart are used for analysis and visualization
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import flanagan.math.Minimisation;

public class ZigDetectorTest
{
	private static final String OPTIMISER_THRESHOLD_STR = "Optim Thresh";

	private static final String RMS_ZIG_RATIO_STR = "RMS Zig Ratio";

	private static final String NOISE_SD_STR = "Noise SD";
	// how much RMS error we require on the Atan Curve before we
	// bother trying to slice the target leg
	private static double RMS_ZIG_RATIO = 0.6;
	// private static double RMS_ZIG_THRESHOLD = 0.005;

	// when to let the optimiser relax
	private static double OPTIMISER_TOLERANCE = 1e-6;

	// the error we add on
	private static double BRG_ERROR_SD = 0.0;

	final static Long timeEnd = null; // osL1end;

	final static SimpleDateFormat dateF = new SimpleDateFormat("HH:mm:ss");

	final static DecimalFormat numF = new DecimalFormat(
			" 0000.0000000;-0000.0000000");

	public static void main3(final String[] args) throws IOException
	{
		final ZigDetector detector = new ZigDetector();
		final OwnshipLegDetector legDetector = new OwnshipLegDetector();
	
		final String name = "Scen5";
	
		// load the data
		final Track ownshipTrack = Track.read("data/" + name + "_Ownship.csv");
		final Sensor sensor = new Sensor("data/" + name + "_Sensor.csv");
	
		// find the ownship legs
		final List<LegOfData> ownshipLegs = legDetector.identifyOwnshipLegs(
				ownshipTrack.getDates(), ownshipTrack.getSpeeds(), ownshipTrack.getCourses(), 5);
		// data.ownshipLegs = data.ownshipLegs.subList(2, 3);
	
		// get ready to store the new legs
		final ILegStorer legStorer = new ILegStorer()
		{
			@Override
			public void storeLeg(final String scenarioName, final long tStart,
					final long tEnd, final Sensor sensor, final double rms)
			{
				System.out.println("Leg identified from " + new Date(tStart) + " to "
						+ new Date(tEnd));
			}
		};
	
		// get ready to store the results runs
		final TimeSeriesCollection legResults = new TimeSeriesCollection();
	
		// ok, work through the legs. In the absence of a Discrete
		// Optimisation algorithm we're taking a brue force approach.
		// Hopefully we can find an optimised alternative to this.
		for (final Iterator<LegOfData> iterator2 = ownshipLegs.iterator(); iterator2
				.hasNext();)
		{
			final LegOfData thisLeg = iterator2.next();
	
			// ok, slice the data for this leg
			long legStart = thisLeg.getStart();
			long legEnd = thisLeg.getEnd();
	
			// trim the start/end to the sensor data
			legStart = Math.max(legStart, sensor.getTimes()[0]);
			legEnd = Math
					.min(legEnd, sensor.getTimes()[sensor.getTimes().length - 1]);
	
			final List<Double> thisLegBearings = sensor.extractBearings(legStart,
					legEnd);
			final List<Long> thisLegTimes = sensor.extractTimes(legStart, legEnd);

			detector.sliceThis(name, legStart, legEnd, sensor, legStorer,
					RMS_ZIG_RATIO, OPTIMISER_TOLERANCE, thisLegTimes, thisLegBearings);
	
			// create a placeholder for the overall score for this leg
			final TimeSeries atanBar = new TimeSeries("ATan " + thisLeg.getName());
			legResults.addSeries(atanBar);
	
			// create a placeholder for the individual time slice experiments
			final TimeSeries thisSeries = new TimeSeries(thisLeg.getName()
					+ " Slices");
			legResults.addSeries(thisSeries);
		}
	
	}


	public static void main(final String[] args) throws Exception
	{
	
		final ZigDetectorTest detectorTest = new ZigDetectorTest();
		final ZigDetector detector = new ZigDetector();
		final OwnshipLegDetector legDetector = new OwnshipLegDetector();
	
		// capture the start time (used for time elapsed at the end)
		final long startTime = System.currentTimeMillis();
	
		// create a holder for the data
		final JFrame frame = createFrame();
		frame.setLocation(600, 50);
		final Container container = frame.getContentPane();
	
		// ok, insert a grid
		final JPanel inGrid = new JPanel();
		container.add(inGrid);
		final GridLayout grid = new GridLayout(0, 2);
		inGrid.setLayout(grid);
	
		final HashMap<String, ScenDataset> datasets = new HashMap<String, ScenDataset>();
	
		final ArrayList<String> scenarios = new ArrayList<String>();
		// scenarios.add("Scen1");
		// scenarios.add("Scen2a");
		// scenarios.add("Scen2b");
		// scenarios.add("Scen3");
		// scenarios.add("Scen4");
		scenarios.add("Scen6");
	
		// handler for slider changes
		final NewValueListener newL = new NewValueListener()
		{
	
			@Override
			public void newValue(final String name, final double val)
			{
				switch (name)
				{
				case NOISE_SD_STR:
					BRG_ERROR_SD = val;
					break;
				case RMS_ZIG_RATIO_STR:
					RMS_ZIG_RATIO = val;
					break;
				case OPTIMISER_THRESHOLD_STR:
					OPTIMISER_TOLERANCE = val;
					break;
				default:
					// don't worry - we make it empty to force a refresh
				}
	
				// create the ownship & target course data
				final Iterator<ScenDataset> iterator = datasets.values().iterator();
				while (iterator.hasNext())
				{
					// create somewhere to store it.
					final ScenDataset data = iterator.next();
	
					// clear the identified legs - to show progress
					Plotting.clearLegMarkers(data.targetPlot, data.bearingPlot);
	
					// update the title
					final NumberFormat numF = new DecimalFormat("0.000");
					final NumberFormat expF = new DecimalFormat("0.###E0");
					final String title = data._name + " Noise:"
							+ numF.format(BRG_ERROR_SD) + " Conv:"
							+ expF.format(OPTIMISER_TOLERANCE) + " Zig:"
							+ numF.format(RMS_ZIG_RATIO);
					data.chartPanel.getChart().setTitle(title);
					data.turnMarkers = new ArrayList<Long>();
	
					// get ready to store the new legs
					data.legStorer = new LegStorer();
	
					// apply the error to the sensor data
					data.sensor.applyError(BRG_ERROR_SD);
	
					// get ready to store the results runs
					final TimeSeriesCollection legResults = new TimeSeriesCollection();
	
					final TimeSeries rmsScores = new TimeSeries("RMS Errors");
					data.legStorer.setRMSScores(rmsScores);
					data.legStorer.setLegList(new ArrayList<LegOfData>());
	
					// ok, work through the legs. In the absence of a Discrete
					// Optimisation
					// algorithm we're taking a brue force approach.
					// Hopefully Craig can find an optimised alternative to this.
					for (final Iterator<LegOfData> iterator2 = data.ownshipLegs
							.iterator(); iterator2.hasNext();)
					{
						final LegOfData thisLeg = iterator2.next();
	
						// ok, slice the data for this leg
						long legStart = thisLeg.getStart();
						long legEnd = thisLeg.getEnd();
	
						// trim the start/end to the sensor data
						legStart = Math.max(legStart, data.sensor.getTimes()[0]);
						legEnd = Math.min(legEnd,
								data.sensor.getTimes()[data.sensor.getTimes().length - 1]);
	
						final List<Double> thisLegBearings = data.sensor.extractBearings(legStart,
								legEnd);
						final List<Long> thisLegTimes = data.sensor.extractTimes(legStart, legEnd);

						detector.sliceThis(name, legStart, legEnd, data.sensor, data.legStorer,
								RMS_ZIG_RATIO, OPTIMISER_TOLERANCE, thisLegTimes, thisLegBearings);
	
						// create a placeholder for the overall score for this leg
						final TimeSeries atanBar = new TimeSeries("ATan "
								+ thisLeg.getName());
						legResults.addSeries(atanBar);
	
						// create a placeholder for the individual time slice experiments
						final TimeSeries thisSeries = new TimeSeries(thisLeg.getName()
								+ " Slices");
						legResults.addSeries(thisSeries);
					}
	
					// plot the bearings
					Plotting.showBearings(data.bearingPlot, data.sensor.getTimes(),
							data.sensor.getBearings(), data.legStorer._rmsScores,
							data.legStorer.getLegs());
	
					// ok, output the results
					Plotting.plotLegPeriods(data.targetPlot, data.tgtTransColor,
							data.legStorer._legList);
				}
			}
		};
	
		// - ok insert the grid controls
		inGrid.add(detectorTest.createControls(newL));
	
		// create the placeholders
		for (final Iterator<String> iterator = scenarios.iterator(); iterator
				.hasNext();)
		{
			final String name = iterator.next();
	
			// create somewhere to store it.
			final ScenDataset data = new ScenDataset(name);
	
			// store it
			datasets.put(name, data);
	
			// ok - create the placeholder
			final CombinedDomainXYPlot combinedPlot = Plotting.createPlot();
			data._plot = combinedPlot;
	
			data.chartPanel = new ChartPanel(new JFreeChart("Results for " + name
					+ " Tol:" + OPTIMISER_TOLERANCE, JFreeChart.DEFAULT_TITLE_FONT,
					combinedPlot, true))
			{
	
				/**
						 * 
						 */
				private static final long serialVersionUID = 1L;
	
				@Override
				public Dimension getPreferredSize()
				{
					return new Dimension(700, 500);
				}
	
			};
			inGrid.add(data.chartPanel, BorderLayout.CENTER);
		}
	
		// create the ownship & target course data
		final Iterator<ScenDataset> iterator = datasets.values().iterator();
		while (iterator.hasNext())
		{
			// create somewhere to store it.
			final ScenDataset data = iterator.next();
	
			// load the data
			data.ownshipTrack = Track.read("data/" + data._name + "_Ownship.csv");
			data.targetTrack = Track.read("data/" + data._name + "_Target.csv");
			data.sensor = new Sensor("data/" + data._name + "_Sensor.csv");
	
			// find the ownship legs
			data.ownshipLegs = legDetector.identifyOwnshipLegs(
					data.ownshipTrack.getDates(), data.ownshipTrack.getSpeeds(), data.ownshipTrack.getCourses(), 9);
			// data.ownshipLegs = data.ownshipLegs.subList(2, 3);
	
			// ok, now for the ownship data
			data.oShipColor = new Color(0f, 0f, 1.0f);
			data.oShipTransColor = new Color(0f, 0f, 1.0f, 0.2f);
			data.ownshipPlot = Plotting.plotSingleVesselData(data._plot, "O/S",
					data.ownshipTrack, data.oShipColor, null, timeEnd);
	
			// ok, now for the ownship legs
			Plotting.plotLegPeriods(data.ownshipPlot, data.oShipTransColor,
					data.ownshipLegs);
	
			// try to plot the moving average
			// switch the courses to an n-term moving average
//			Plotting.addAverageCourse(data.ownshipPlot,
//					data.ownshipTrack.averageCourses, data.ownshipTrack.averageSpeeds,
//					data.ownshipTrack.getDates());
	
			// and the target plot
			data.tgtColor = new Color(1.0f, 0f, 0f);
			data.tgtTransColor = new Color(1.0f, 0f, 0f, 0.2f);
			// data.targetPlot = Plotting.plotSingleVesselData(data._plot, "Tgt",
			// data.targetTrack, data.tgtColor, null, timeEnd);
	
			// insert a bearing plot
			data.bearingPlot = Plotting.createBearingPlot(data._plot);
		}
	
		if (inGrid.getComponentCount() == 1)
		{
			grid.setColumns(1);
		}
	
		frame.pack();
	
		// ok, we should probably initialise it
		newL.newValue("", 0);
	
		final long elapsed = System.currentTimeMillis() - startTime;
		System.out.println("Elapsed:" + elapsed / 1000 + " secs");
	
	}


	/**
	 * local instance of leg storer, also collects some other performance data
	 * 
	 * @author ian
	 * 
	 */
	public static class LegStorer implements ILegStorer
	{
		private ArrayList<LegOfData> _legList;
		private TimeSeries _rmsScores;

		public List<LegOfData> getLegs()
		{
			return _legList;
		}

		public void setLegList(final ArrayList<LegOfData> legList)
		{
			_legList = legList;
		}

		public void setRMSScores(final TimeSeries series)
		{
			_rmsScores = series;
		}

		@Override
		public void storeLeg(final String scenarioName, final long tStart,
				final long tEnd, final Sensor sensor, final double rms)
		{
			System.out.println("Storing " + scenarioName + " : "
					+ dateF.format(new Date(tStart)) + " - "
					+ dateF.format(new Date(tEnd)));

			_legList.add(new LegOfData("Leg-" + (_legList.size() + 1), tStart, tEnd));

			// store some RMS error scores
			if (sensor != null)
			{
				final List<Long> times = sensor.extractTimes(tStart, tEnd);
				for (final Iterator<Long> iterator = times.iterator(); iterator
						.hasNext();)
				{
					final Long long1 = iterator.next();
					_rmsScores.add(new FixedMillisecond(long1), rms);
				}
			}
		}
	}
	
	/**
	 * @return a frame to contain the results
	 */
	private static JFrame createFrame()
	{
		final JFrame frame = new JFrame("Results");
		frame.pack();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(final WindowEvent e)
			{
				System.out.println("Closed");
				e.getWindow().dispose();
			}
		});
		frame.setLayout(new BorderLayout());

		return frame;
	}

	public static String out(final Minimisation res)
	{
		final double[] key = res.getParamValues();
		final String out = " B:" + numF.format(key[0]) + " P:"
				+ numF.format(key[1]) + " Q:" + numF.format(key[2]) + " Sum:"
				+ numF.format(res.getMinimum());

		return out;
	}

	protected JPanel createControls(final NewValueListener newListener)
	{
		final JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));

		final NumberFormat decFormat = new DecimalFormat("0.000");
		final NumberFormat expFormat = new DecimalFormat("0.000E00");

		// panel.add(createItem(NOISE_SD_STR, new double[]
		// { 0d, 0.1d, 0.2d, 0.25d, 0.3d, 0.5, 2d }, newListener, decFormat,
		// BRG_ERROR_SD));
		panel.add(createItem(OPTIMISER_THRESHOLD_STR, new double[]
		{ 1e-4, 1e-5, 1e-6, 1e-7, 1e-8 }, newListener, expFormat,
				OPTIMISER_TOLERANCE));
		panel.add(createItem(RMS_ZIG_RATIO_STR, new double[]
		{ 1.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3 }, newListener, decFormat,
				RMS_ZIG_RATIO));

		return panel;
	}

	protected JPanel createItem(final String label, final double[] values,
			final NewValueListener listener, final NumberFormat numberFormat,
			final double startValue)
	{

		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel(label), BorderLayout.WEST);

		final JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 0));
		final ButtonGroup bg = new ButtonGroup();
		for (int i = 0; i < values.length; i++)
		{
			final JRadioButton newB = new JRadioButton();
			final double thisD = values[i];
			newB.setText(numberFormat.format(values[i]));
			if (thisD == startValue)
			{
				newB.setSelected(true);
			}
			newB.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(final ActionEvent e)
				{
					System.out.println(label + " changed to:" + e.toString());

					if (newB.isSelected())
					{
						listener.newValue(label, thisD);
					}
				}
			});
			buttons.add(newB);
			bg.add(newB);
		}

		panel.add(buttons, BorderLayout.EAST);

		return panel;
	}
	

	protected static interface NewValueListener
	{
		public void newValue(String attribute, double val);
	}

	public static class ScenDataset
	{
		public XYPlot bearingPlot;
		public ChartPanel chartPanel;
		public Color tgtTransColor;
		public Color tgtColor;
		public Color oShipColor;
		public Color oShipTransColor;
		protected ArrayList<Long> turnMarkers;
		protected LegStorer legStorer;
		public XYPlot targetPlot;
		public XYPlot ownshipPlot;
		public List<LegOfData> ownshipLegs;
		private final String _name;
		CombinedDomainXYPlot _plot;
		public Track ownshipTrack;
		public Track targetTrack;
		public Sensor sensor;

		public ScenDataset(final String name)
		{
			_name = name;
		}

	}

	protected static interface ValConverter
	{
		public double convert(int input);

		int unConvert(double val);
	}
}
