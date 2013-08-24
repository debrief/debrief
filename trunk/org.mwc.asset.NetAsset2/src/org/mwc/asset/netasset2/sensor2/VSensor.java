package org.mwc.asset.netasset2.sensor2;

import java.awt.Color;
import java.awt.Frame;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.mwc.asset.netasset2.time.IVTime;

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
import MWC.GUI.JFreeChart.DateAxisEditor;
import MWC.GUI.JFreeChart.RelativeDateAxis;

public class VSensor extends Composite implements ParticipantDetectedListener,
		IVTime
{

	private final XYPlot _thePlot;
	private final JFreeChart _thePlotArea;
	private final ChartPanel _chartInPanel;
	private TimeSeriesCollection dataList;
	private Integer _visibleTimePeriod = new Integer(5 * 60);
	private final RelativeDateAxis _dateAxis;
	private long _timeNow = -1;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VSensor(final Composite parent, final int style)
	{
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		final ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(BorderLayout.NORTH);

		// ToolItem testBtn = new ToolItem(toolBar, SWT.NONE);
		// testBtn.setText("Test 1");
		// testBtn.addSelectionListener(new SelectionAdapter()
		// {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e)
		// {
		// doTest();
		// }
		// });

		final ToolItem tltmDropdownItem = new ToolItem(toolBar, SWT.DROP_DOWN);
		tltmDropdownItem.setText("Visible period");
		final DropdownSelectionListener drops = new DropdownSelectionListener(
				tltmDropdownItem);
		drops.add("5 Mins", 5 * 60);
		drops.add("15 Mins", 15 * 60);
		drops.add("60 Mins", 60 * 60);
		drops.add("All data", 0);
		tltmDropdownItem.addSelectionListener(drops);

		final Composite sashForm = new Composite(this, SWT.EMBEDDED);

		// now we need a Swing object to put our chart into
		final Frame _plotControl = SWT_AWT.new_Frame(sashForm);

		// the y axis is common to hi & lo res. Format it here
		final NumberAxis yAxis = new NumberAxis("Degs");
	//	yAxis.setRange(0, 360);
		yAxis.setAutoRange(true);
		yAxis.setTickUnit(new NumberTickUnit(45));

		// create a date-formatting axis
		_dateAxis = new RelativeDateAxis();
		_dateAxis.setStandardTickUnits(DateAxisEditor
				.createStandardDateTickUnitsAsTickUnits());
		_dateAxis.setAutoRange(true);

		final XYItemRenderer theRenderer = new XYShapeRenderer();

		_thePlot = new XYPlot(null, _dateAxis, yAxis, theRenderer);
		_thePlot.setOrientation(PlotOrientation.HORIZONTAL);
		_thePlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
		_thePlot.setBackgroundPaint(Color.BLACK);
		theRenderer.setPlot(_thePlot);

		_dateAxis.setLabelPaint(Color.GREEN);
		_dateAxis.setTickLabelPaint(Color.GREEN);
		_dateAxis.setAxisLinePaint(Color.GREEN);

		yAxis.setLabelPaint(Color.GREEN);
		yAxis.setTickLabelPaint(Color.GREEN);

		_thePlotArea = new JFreeChart(null, _thePlot);
		_thePlotArea.setBackgroundPaint(Color.BLACK);
		_thePlotArea.setBorderPaint(Color.BLACK);

		// set the color of the area surrounding the plot
		// - naah, don't bother. leave it in the application background color.

		// ////////////////////////////////////////////////
		// put the holder into one of our special items
		// ////////////////////////////////////////////////
		_chartInPanel = new ChartPanel(_thePlotArea, true);

		_plotControl.add(_chartInPanel);

	}

	class DropdownSelectionListener extends SelectionAdapter
	{
		private final ToolItem dropdown;

		private final Menu menu;

		public DropdownSelectionListener(final ToolItem dropdown)
		{
			this.dropdown = dropdown;
			menu = new Menu(dropdown.getParent().getShell());
		}

		public void add(final String item, final int secs)
		{
			final MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(item);
			menuItem.setData(secs);
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(final SelectionEvent event)
				{
					final MenuItem selected = (MenuItem) event.widget;
					dropdown.setText(selected.getText());
					dropdown.setData(selected.getData());
					setTimePeriod((Integer) dropdown.getData());
				}
			});
		}

		public void widgetSelected(final SelectionEvent event)
		{
			if (event.detail == SWT.ARROW)
			{
				final ToolItem item = (ToolItem) event.widget;
				final Rectangle rect = item.getBounds();
				final Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
				menu.setLocation(pt.x, pt.y + rect.height);
				menu.setVisible(true);
			}
			else
			{
				setTimePeriod((Integer) dropdown.getData());
			}
		}
	}

	// specify time period displayed
	protected void setTimePeriod(final Integer secs)
	{
		_visibleTimePeriod = secs;

		if (_timeNow != 0)
		{
			if (secs == 0)
			{
				_dateAxis.setAutoRange(true);
			}
			else
			{
				final long startTime = _timeNow - (secs * 1000);
				final Range newR = new Range(startTime, _timeNow);
				_dateAxis.setRange(newR, true, true);
			}

		}
	}

	private static long lastTime = new Date().getTime();;

	protected void doTest()
	{
		final DetectionList dets = new DetectionList();

		lastTime += (int) (1 + Math.random() * 21) * 5d * 1000;

		newTime(lastTime);

		final int numE = (int) (Math.random() * 5d);
		for (int i = 0; i < numE; i++)
		{
			final float thisBrg = (float) (Math.random() * 360d);
			final SensorType st = new OpticSensor(i);
			final int partId = 100 + i;
			final NetworkParticipant np = new NetworkParticipant()
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
					return partId;
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

			final DetectionEvent de = new DetectionEvent(lastTime, 12, null, st, null,
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
		if (detections.size() > 0)
		{
			final Iterator<DetectionEvent> iter = detections.iterator();
			while (iter.hasNext())
			{
				final DetectionEvent thisD = iter.next();
				processThis(thisD);
			}
		}

	}

	private void processThis(final DetectionEvent thisD)
	{
		// keep track of if we need to add the time series to the plot
		boolean addDataset = false;

		if (dataList == null)
		{
			dataList = new TimeSeriesCollection();
			addDataset = true;
		}

		final String seriesId = "s" + thisD.getSensor() + "t" + thisD.getTarget();

		TimeSeries thisSeries = dataList.getSeries(seriesId);

		// keep track of if we should be adding this series to the dataset
		boolean seriesAddPending = false;

		if (thisSeries == null)
		{
			thisSeries = new TimeSeries(seriesId);
			// don't actually add the series until it contains some data
			seriesAddPending = true;
		}

		float bearing = thisD.getBearing();
		if (bearing < 0)
			bearing += 360;
		final long newTime = thisD.getTime();
		final FixedMillisecond time = new FixedMillisecond(newTime);

		try
		{
			thisSeries.add(time, bearing);
		}
		catch (final Exception e)
		{
			System.err.println("BUGGER");
		}

		if (seriesAddPending)
			dataList.addSeries(thisSeries);

		if (addDataset)
			_thePlot.setDataset(dataList);

	}

	@Override
	public void restart(final ScenarioType scenario)
	{
	}

	@Override
	public void newTime(final long newTime)
	{
		_timeNow = newTime;
		// check we're showing the correct period
		setTimePeriod(_visibleTimePeriod);

	}

}
