package org.mwc.asset.netasset;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.comms.restlet.data.DecisionResource.DecidedEvent;
import org.mwc.asset.comms.restlet.data.DetectionResource.DetectionEvent;
import org.mwc.asset.comms.restlet.host.ASSETGuest;
import org.mwc.asset.comms.restlet.test.MockHost;
import org.mwc.asset.netasset.model.RestGuest;
import org.mwc.asset.netasset.model.RestHost;
import org.mwc.asset.netasset.view.HolderPane;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.debrief.core.editors.painters.SnailHighlighter;

import ASSET.Participants.Status;
import Debrief.GUI.Tote.Painters.SnailPainter;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FullFormatDateTime;

public class NetAssetView extends ViewPart implements ASSETGuest
{
	public static final String ID = "org.mwc.asset.NetAsset.NetAssetView";

	private HolderPane _control;

	private RestGuest _myModel;
	private RestHost _myHosting;

	private Layers _myLayers;

	private TrackWrapper _myTrack;

	private HiResDate _time;

	private SWTChart _myChart;

	private SnailHighlighter _myHighlighter;

	public NetAssetView()
	{
		_myModel = new RestGuest(this);
		_myHosting = new RestHost();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		_control = new HolderPane(parent, SWT.NONE);
		_control.setActCourse("12.3");
		_control.setActSpeed("2.3");
		_control.setActDepth("1.3");

		_control.logEvent(new Date().getTime(), "Event", "Start");

		doPlot(_control.getPlotContainer());

		_control.addConnectListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				super.widgetSelected(e);
				Button btn = (Button) e.widget;
				final String btnName = btn.getText();
				final boolean doConnect = (btnName == HolderPane.CONNECT);
				new Thread()
				{

					@Override
					public void run()
					{
						if (doConnect)
							doConnect();
						else
							doDisconnect();
					}
				}.run();
			}
		});

		_control.addSelfHostListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Button btn = (Button) e.widget;
				if (btn.getSelection())
					_myHosting.startHosting(new MockHost());
				else
					_myHosting.stopHosting();
			}
		});

		_control.addTakeControlListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				new Thread()
				{
					@Override
					public void run()
					{
						Button btn = (Button) e.widget;
						doTakeControl(btn.getSelection());
					}
				}.run();

			}
		});
		_control.addSubmitListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				super.widgetSelected(e);
				new Thread()
				{
					@Override
					public void run()
					{
						doSubmit(_control.getDemCourse(), _control.getDemSpeed(), _control
								.getDemDepth());
					}
				}.run();
			}

		});
		_control.addTimeListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Button widget = (Button) e.widget;
				doPlay(widget.getSelection());
			}
		});
		_control.addTimeSpeedListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Button btn = (Button) e.widget;

				doGoFaster(btn.getText().equals("++"));
				super.widgetSelected(e);
			}
		});

	}

	private void doPlot(Composite plotContainer)
	{
		_myLayers = new Layers();
		_myTrack = new TrackWrapper();
		_myTrack.setName("Ian");
		_myLayers.addThisLayer(_myTrack);

		final TrackDataProvider provider = new TrackDataProvider()
		{
			public void removeTrackShiftListener(TrackShiftListener listener)
			{
			}

			public void removeTrackDataListener(TrackDataListener listener)
			{
			}

			public WatchableList[] getSecondaryTracks()
			{
				return new WatchableList[]
				{};
			}

			public WatchableList getPrimaryTrack()
			{
				return _myTrack;
			}

			public void fireTracksChanged()
			{
			}

			public void fireTrackShift(TrackWrapper target)
			{
			}

			public void addTrackShiftListener(TrackShiftListener listener)
			{
			}

			public void addTrackDataListener(TrackDataListener listener)
			{
			}
		};
		_myHighlighter = new SnailHighlighter(provider);

		_myChart = new SWTChart(_myLayers, plotContainer)
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void chartFireSelectionChanged(ISelection sel)
			{
			}

			/**
			 * @param thisLayer
			 * @param dest
			 */
			protected void paintThisLayer(Layer thisLayer, CanvasType dest)
			{
				try
				{
					// get the current time
					HiResDate tNow = _time;

					// do we know the time?
					// if (tNow != null)
					if (true)
					{
						// yes. cool, get plotting
						_myHighlighter.paintThisLayer(thisLayer, dest, tNow);

						// ok, now sort out the highlight

						// right, what are the watchables
						final Vector<Plottable> watchables = SnailPainter
								.getWatchables(thisLayer);

						// cycle through them
						final Enumeration<Plottable> watches = watchables.elements();
						while (watches.hasMoreElements())
						{
							final WatchableList list = (WatchableList) watches.nextElement();
							// is the primary an instance of layer (with it's
							// own line
							// thickness?)
							if (list instanceof Layer)
							{
								final Layer ly = (Layer) list;
								int thickness = ly.getLineThickness();
								dest.setLineWidth(thickness);
							}

							// ok, clear the nearest items
							if (tNow != null)
							{
								Watchable[] wList = list.getNearestTo(tNow);
								Watchable watch = null;
								if (wList.length > 0)
									watch = wList[0];

								if (watch != null)
								{
									// // aah, is this the primary?
									// boolean isPrimary = (list == provider
									// .getPrimaryTrack());

									// plot it
									// _layerPainterManager.getCurrentHighlighter().highlightIt(
									// dest.getProjection(), dest, list, watch, isPrimary);
								}
							} // whether we have a current time...
						}
					}
				}
				catch (Exception e)
				{
				}

			}
		};

		Layer misc = new BaseLayer();
		misc.setName("Misc");
		_myLayers.addThisLayer(misc);
		GridPainter grid = new GridPainter();
		grid.setName("1Nm Grid");
		grid.setDelta(new WorldDistance(1, WorldDistance.NM));

	}

	protected void doTakeControl(boolean take)
	{
		if (take)
			_myModel.doTakeControl();
		else
			_myModel.doReleaseControl();

		_control.setStateEnabled(take);

	}

	protected void doGoFaster(boolean faster)
	{
		_myModel.doGoFaster(faster);
	}

	protected void doPlay(boolean play)
	{
		_myModel.play(play);
	}

	protected void doSubmit(String courseTxt, String speedTxt, String depthTxt)
	{
		double courseDegs = Double.parseDouble(courseTxt);
		double speedKts = Double.parseDouble(speedTxt);
		double depthM = Double.parseDouble(depthTxt);
		_myModel.doDemStatus(courseDegs, speedKts, depthM);
	}

	protected void doConnect()
	{
		boolean worked = _myModel.doConnect();
		if (worked)
			_control.setConnectPhrase(HolderPane.DISCONNECT);
		_control.setTimeEnabled(worked);
	}

	protected void doDisconnect()
	{
		_myModel.doDisconnect();
		_control.setTimeEnabled(false);
		_control.setStateEnabled(false);
		_control.setConnectPhrase(HolderPane.CONNECT);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}

	@Override
	public void newParticipantDecision(int scenarioId, int participantId,
			DecidedEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void newParticipantDetection(int scenarioId, int participantId,
			DetectionEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void newParticipantState(int scenarioId, int participantId,
			final Status newState)
	{
		Display dThread = Display.getDefault();
		if (dThread != null)
			dThread.asyncExec(new Runnable()
			{
				public void run()
				{
					_control.setActCourse("" + ((int) newState.getCourse()));
					_control.setActSpeed(""
							+ (int) (newState.getSpeed().getValueIn(WorldSpeed.Kts)));
					_control.setActDepth("" + ((int) newState.getLocation().getDepth()));

					Fix theFix = new Fix(new HiResDate(newState.getTime()), newState
							.getLocation(), MWC.Algorithms.Conversions.Degs2Rads(newState
							.getCourse()), newState.getSpeed().getValueIn(
							WorldSpeed.ft_sec / 3));
					FixWrapper newFix = new FixWrapper(theFix);
					_myTrack.add(newFix);
					_myLayers.fireExtended(newFix, _myTrack);
					_myChart.rescale();
				}
			});
	}

	@Override
	public void newScenarioEvent(final long time, final String eventName,
			final String description)
	{
		if (time != 0)
		{
			_time = new HiResDate(time);
			Display dThread = Display.getDefault();
			if (dThread != null)
				dThread.asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						if (eventName.equals("Step"))
							_control.setTime(FullFormatDateTime.toString(time));
						else
							_control.logEvent(time, eventName, description);
					}
				});
			else
			{
				System.out.println("dThread missing");
			}
		}
	}
}
