/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.asset.netasset2.plot;

import java.awt.Color;
import java.util.Date;
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
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.gt2plot.proj.GtProjection;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.editors.painters.PlainHighlighter;
import org.mwc.debrief.core.editors.painters.SnailHighlighter;
import org.mwc.debrief.core.editors.painters.TemporalLayerPainter;

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
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GenericData.Duration;
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

	private PlainHighlighter _plainPainter;

	protected TemporalLayerPainter _myPainter;

	private GtProjection _myProjection;

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
	public VPlot(final Composite parent, final int style)
	{
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		final ToolBar toolBar = new ToolBar(this, SWT.PUSH);
		toolBar.setLayoutData(BorderLayout.NORTH);

		_snailPainter = new SnailHighlighter(null);
		_plainPainter = new PlainHighlighter();
		_myPainter = _snailPainter;
		_snailPainter.getSnailProperties().setTrailLength(new Duration(30, Duration.MINUTES));

		_myProjection = new GtProjection();
		
		_myLayers = new Layers();

		_myChart = new SWTChart(_myLayers, this, _myProjection)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void chartFireSelectionChanged(final ISelection sel)
			{
			}

			@Override
			protected void paintThisLayer(final Layer thisLayer, final CanvasType dest)
			{
				if (tNow != null)
					_myPainter.paintThisLayer(thisLayer, dest, tNow);
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

		final ToolItem btnZoomOut = new ToolItem(toolBar, SWT.NONE);
		btnZoomOut.setText("Zoom out");
		btnZoomOut.setImage(CorePlugin.getImageFromRegistry(DebriefPlugin.getImageDescriptor("icons/zoomout.gif")));
		btnZoomOut.setToolTipText("Zoom out");
		btnZoomOut.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				zoomOut();
			}
		});

		final ToolItem btnFitToWin = new ToolItem(toolBar, SWT.NONE);
		btnFitToWin.setToolTipText("Fit to window");
		btnFitToWin.setImage(CorePlugin.getImageFromRegistry(DebriefPlugin.getImageDescriptor("icons/fit_to_win.gif")));
		btnFitToWin.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				fitToWin();
			}
		});
		btnFitToWin.setText("Fit to win");
		new ToolItem(toolBar, SWT.SEPARATOR); // Signals end of group

		final ToolItem followTrack = new ToolItem(toolBar, SWT.CHECK);
		followTrack.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				_myChart.getCanvas().getProjection()
						.setRelativeMode(followTrack.getSelection(), false);
			}

		});
		followTrack.setText("O/S Centred");
		followTrack.setImage(CorePlugin.getImageFromRegistry(DebriefPlugin.getImageDescriptor("icons/filter.gif")));
		followTrack.setSelection(true);
		new ToolItem(toolBar, SWT.SEPARATOR); // Signals end of group
		
		final ToolItem snail = new ToolItem(toolBar, SWT.RADIO);
		snail.setText("Snail");
		snail.setImage(CorePlugin.getImageFromRegistry(DebriefPlugin.getImageDescriptor("icons/snail.gif")));
		snail.setSelection(true);
		snail.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(final SelectionEvent e)
			{
				_myPainter = _snailPainter;
				_myChart.update();
			}});
		final ToolItem plain = new ToolItem(toolBar, SWT.RADIO);
		plain.setText("Plain");
		plain.setImage(CorePlugin.getImageFromRegistry(DebriefPlugin.getImageDescriptor("icons/normal.gif")));
		plain.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(final SelectionEvent e)
			{
				_myPainter = _plainPainter;
				_myChart.update();
			}});
		new ToolItem(toolBar, SWT.SEPARATOR); // Signals end of group
		
	}

	private void insertBackData(final Layers layers)
	{
		final BaseLayer base = new BaseLayer();
		base.setName("General");
		final GridPainter gp = new GridPainter();
		gp.setName("Grid");
		gp.setColor(new Color(30,30,30));
		gp.setDelta(new WorldDistance(1, WorldDistance.NM));
		gp.setVisible(true);
		base.add(gp);
		layers.addThisLayer(base);
		base.setVisible(true);
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

				// and sort out the background data
				insertBackData(_myLayers);


			}
		});
	}

	@Override
	public void newTime(final long newTime)
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

			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{

				// remember the status as most recent
				_recentStatus = status;

				final Fix theFix = new Fix();
				tNow = new HiResDate(status.getTime());
				theFix.setTime(tNow);
				theFix.setLocation(status.getLocation());
				theFix.setCourse(MWC.Algorithms.Conversions.Degs2Rads(status
						.getCourse()));
				theFix.setSpeed(status.getSpeed().getValueIn(WorldSpeed.ft_sec / 3));
				final FixWrapper fw = new FixWrapper(theFix);
				myTrack.addFix(fw);
				
				// hmm, should we plot a minute marker?
				final Date dt = tNow.getDate();
				final int mins = dt.getMinutes();
				final int secs = dt.getSeconds();
				
				// hmm, should we plot a 10 minute marker?
				if(mins % 10 == 0)
				{
					fw.setLabelShowing(true);
				}
				if(secs == 0)
				{
					fw.setSymbolShowing(true);
				}
				
				
				_numUpdates++;

				if (_numUpdates == 2)
					_myChart.rescale();

			}
		});
	}

	// find hte sensor
	@SuppressWarnings("deprecation")
  private SensorWrapper getSensor(final String name)
	{
		final Enumeration<Editable> iter = myTrack.getSensors().elements();
		SensorWrapper res = null;
		while (iter.hasMoreElements())
		{
			final SensorWrapper sensor = (SensorWrapper) iter.nextElement();
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
	public void newDetections(final DetectionList detections)
	{
		final Iterator<DetectionEvent> iter = detections.iterator();
		while (iter.hasNext())
		{
			final DetectionEvent det = iter.next();
			final String name = "Sensor_" + det.getSensor();
			final SensorWrapper sw = getSensor(name);
			final SensorContactWrapper scw = new SensorContactWrapper();

			// sort out the time
			final HiResDate dtg = new HiResDate(det.getTime());
			scw.setDTG(dtg);

			// do we have bearing?
			final Float brg = det.getBearing();
			if (brg != null)
				scw.setBearing(brg);

			// do we have range?
			final WorldDistance rng = det.getRange();
			if (rng != null)
			{
				scw.setRange(rng);
			}

			// and store it
			sw.add(scw);

		}
	}

	@Override
	public void restart(final ScenarioType scenario)
	{

	}
}
