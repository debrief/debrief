package org.mwc.asset.netasset2.sensor2;

import java.awt.Color;
import java.awt.Frame;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import swing2swt.layout.BorderLayout;
import ASSET.NetworkParticipant;
import ASSET.ScenarioType;
import ASSET.Models.SensorType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.Initial.OpticSensor;
import ASSET.Participants.Category;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.Status;
import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.DateAxisEditor;
import MWC.GUI.JFreeChart.DatedToolTipGenerator;
import MWC.GUI.JFreeChart.NewFormattedJFreeChart;
import MWC.GUI.JFreeChart.RelativeDateAxis;

public class VSensor extends Composite implements ParticipantDetectedListener
{

	private XYPlot _thePlot;
	private NewFormattedJFreeChart _thePlotArea;
	private ChartPanel _chartInPanel;
	private TimeSeriesCollection dataList;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VSensor(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(BorderLayout.NORTH);

		ToolItem testBtn = new ToolItem(toolBar, SWT.NONE);
		testBtn.setText("Test 1");
		testBtn.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doTest();
			}
		});

		ToolItem fitToWin = new ToolItem(toolBar, SWT.NONE);
		fitToWin.setText("Fit");

		ToolItem tltmDropdownItem = new ToolItem(toolBar, SWT.DROP_DOWN);
		tltmDropdownItem.setText("Time");

		Composite sashForm = new Composite(this, SWT.EMBEDDED);

		// now we need a Swing object to put our chart into
		Frame _plotControl = SWT_AWT.new_Frame(sashForm);

		// the y axis is common to hi & lo res. Format it here
		NumberAxis yAxis = new NumberAxis("Degs");
		// create a date-formatting axis
		final DateAxis dAxis = new RelativeDateAxis();
		dAxis.setStandardTickUnits(DateAxisEditor
				.createStandardDateTickUnitsAsTickUnits());

		// also create the date-knowledgable tooltip writer
		DatedToolTipGenerator tooltipGenerator = new DatedToolTipGenerator();

		ColourStandardXYItemRenderer theRenderer = new ColourStandardXYItemRenderer(
				tooltipGenerator, null, null);
		_thePlot = new XYPlot(null, (RelativeDateAxis) dAxis, yAxis, theRenderer);
		theRenderer.setPlot(_thePlot);

		_thePlotArea = new NewFormattedJFreeChart("Sensor plot", null, _thePlot,
				true, null);

		// set the color of the area surrounding the plot
		// - naah, don't bother. leave it in the application background color.
		_thePlotArea.setBackgroundPaint(Color.white);

		// ////////////////////////////////////////////////
		// put the holder into one of our special items
		// ////////////////////////////////////////////////
		_chartInPanel = new ChartPanel(_thePlotArea, true);
		
		_plotControl.add(_chartInPanel);

		dataList = new TimeSeriesCollection();

	}

	private static long lastTime = new Date().getTime();;

	protected void doTest()
	{
		DetectionList dets = new DetectionList();

		lastTime += (int) (Math.random() * 21) * 5d * 1000;

		int numE = (int) (Math.random() * 5d);
		for (int i = 0; i < numE; i++)
		{
			float thisBrg = (float) (Math.random() * 360d);
			SensorType st = new OpticSensor(12);
			final int sensorID = i;
			NetworkParticipant np = new NetworkParticipant()
			{

				@Override
				public Status getStatus()
				{
					return null;
				}

				@Override
				public String getName()
				{
					return "scrap name";
				}

				@Override
				public int getId()
				{
					return sensorID;
				}

				@Override
				public Category getCategory()
				{
					return null;
				}

				@Override
				public String getActivity()
				{
					return null;
				}
			};

			DetectionEvent de = new DetectionEvent(lastTime, 12, null, st, null,
					null, thisBrg, null, null, null, null, null, np);
			dets.add(de);
		}

		newDetections(dets);
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void newDetections(final DetectionList detections)
	{
		Iterator<DetectionEvent> iter = detections.iterator();
		while (iter.hasNext())
		{
			DetectionEvent thisD = iter.next();
			processThis(thisD);
		}
	}

	private void processThis(DetectionEvent thisD)
	{
		int sensorId = thisD.getSensor();
		TimeSeries thisSeries = dataList.getSeries("" + sensorId);
		
		if(thisSeries == null)
		{
			thisSeries = new TimeSeries("" + sensorId);
			dataList.addSeries(thisSeries);
		}
		
		float bearing = thisD.getBearing();
		long newTime = thisD.getTime();
		System.out.println("adding " + bearing + " at " + newTime + " to " + sensorId);
		FixedMillisecond time = new FixedMillisecond(newTime);
		
		thisSeries.addOrUpdate(time, bearing);
	}

	@Override
	public void restart(ScenarioType scenario)
	{
	}

}
