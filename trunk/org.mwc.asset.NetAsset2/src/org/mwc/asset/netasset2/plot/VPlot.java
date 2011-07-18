package org.mwc.asset.netasset2.plot;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.mwc.asset.netasset2.part.IVPartMovement;
import org.mwc.asset.netasset2.time.IVTime;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.debrief.core.editors.painters.SnailHighlighter;

import swing2swt.layout.BorderLayout;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.Status;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection.RelativeProjectionParent;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;

public class VPlot extends Composite implements IVPartMovement, IVTime,
		ParticipantDetectedListener
{

	/**
	 * the chart we store/manager
	 */
	protected SWTChart _myChart = null;

	/**
	 * the graphic data we know about
	 */
	protected Layers _myLayers;

	private TrackWrapper myTrack;

	private int _numUpdates = 0;

	private SnailHighlighter _snailPainter;

	protected HiResDate tNow;

	protected Status _recentStatus;

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VPlot(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		ToolBar toolBar = new ToolBar(this, SWT.PUSH);
		toolBar.setLayoutData(BorderLayout.NORTH);

		_snailPainter = new SnailHighlighter(null);

		_myLayers = new Layers();
		_myChart = new SWTChart(_myLayers, this)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void chartFireSelectionChanged(ISelection sel)
			{
			}

			@Override
			protected void paintThisLayer(Layer thisLayer, CanvasType dest)
			{
				if (tNow != null)
					_snailPainter.paintThisLayer(thisLayer, dest, tNow);

			}

		};
		_myChart.getCanvasControl().setLayoutData(BorderLayout.CENTER);
		_myChart.getCanvas().getProjection().setRelativeMode(true, false);
		_myChart.getCanvas().getProjection()
				.setRelativeProjectionParent(new RelativeProjectionParent()
				{

					@Override
					public double getHeading()
					{
						double res = 0;
						if (_recentStatus != null)
							res = _recentStatus.getCourse();

						return res;
					}

					@Override
					public WorldLocation getLocation()
					{
						WorldLocation res = null;
						if (_recentStatus != null)
							res = _recentStatus.getLocation();
						return res;
					}
				});

		// specify a snail painter

		ToolItem btnZoomOut = new ToolItem(toolBar, SWT.NONE);
		btnZoomOut.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				zoomOut();
			}
		});
		btnZoomOut.setText("Zoom out");

		ToolItem btnFitToWin = new ToolItem(toolBar, SWT.NONE);
		btnFitToWin.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				fitToWin();
			}
		});
		btnFitToWin.setText("Fit to win");

		final ToolItem followTrack = new ToolItem(toolBar, SWT.CHECK);
		followTrack.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				_myChart.getCanvas().getProjection()
						.setRelativeMode(followTrack.getSelection(), false);
			}

		});
		followTrack.setText("O/S Centred");
		followTrack.setSelection(true);

	}

	protected void zoomOut()
	{
		_myChart.getCanvas().getProjection().zoom(2.0);
		_myChart.update();
	}

	protected void fitToWin()
	{
		_myChart.rescale();
		_myChart.update();
	}

	@Override
	public void setParticipant(final String name)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				// ok, clear out...
				_myLayers.clear();

				// create new participant painter
				myTrack = new TrackWrapper();
				myTrack.setName(name);
				myTrack.setColor(Color.blue);
				_myLayers.addThisLayer(myTrack);

			}
		});
	}

	@Override
	public void newTime(long newTime)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				_myChart.update();
			}
		});
	}

	@Override
	public void moved(final Status status)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{

				// remember the status as most recent
				_recentStatus = status;

				Fix theFix = new Fix();
				tNow = new HiResDate(status.getTime());
				theFix.setTime(tNow);
				theFix.setLocation(status.getLocation());
				theFix.setCourse(MWC.Algorithms.Conversions.Degs2Rads(status
						.getCourse()));
				theFix.setSpeed(status.getSpeed().getValueIn(WorldSpeed.ft_sec / 3));
				FixWrapper fw = new FixWrapper(theFix);
				myTrack.addFix(fw);
				_numUpdates++;

				if (_numUpdates == 2)
					_myChart.rescale();

			}
		});
	}

	// find hte sensor
	private SensorWrapper getSensor(String name)
	{
		Enumeration<Editable> iter = myTrack.getSensors().elements();
		SensorWrapper res = null;
		while (iter.hasMoreElements())
		{
			SensorWrapper sensor = (SensorWrapper) iter.nextElement();
			if (sensor.getName().equals(name))
			{
				res = sensor;
				break;
			}
		}

		// did we find one?
		if (res == null)
		{
			res = new SensorWrapper(name);
			res.setVisible(true);
			res.setWormInHole(false);
			res.setColor(Color.yellow);
			myTrack.add(res);
		}
		return res;
	}

	@Override
	public void newDetections(DetectionList detections)
	{
		Iterator<DetectionEvent> iter = detections.iterator();
		while (iter.hasNext())
		{
			DetectionEvent det = iter.next();
			String name = "Sensor_" + det.getSensor();
			SensorWrapper sw = getSensor(name);
			SensorContactWrapper scw = new SensorContactWrapper();

			// sort out the time
			HiResDate dtg = new HiResDate(det.getTime());
			scw.setDTG(dtg);

			// do we have bearing?
			Float brg = det.getBearing();
			if (brg != null)
				scw.setBearing(brg);

			// do we have range?
			WorldDistance rng = det.getRange();
			if (rng != null)
			{
				scw.setRange(rng);
			}

			// and store it
			sw.add(scw);

		}
	}

	@Override
	public void restart(ScenarioType scenario)
	{
		// TODO Auto-generated method stub

	}
}
