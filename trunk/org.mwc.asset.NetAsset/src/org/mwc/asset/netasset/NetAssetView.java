package org.mwc.asset.netasset;

import java.util.Date;

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
import org.mwc.asset.netasset.model.RestSupport;
import org.mwc.asset.netasset.view.HolderPane;

import ASSET.Participants.Status;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.TextFormatting.FullFormatDateTime;

public class NetAssetView extends ViewPart implements ASSETGuest
{
	public static final String ID = "org.mwc.asset.NetAsset.NetAssetView";

	private HolderPane _control;

	private RestSupport _myModel;

	public NetAssetView()
	{
		_myModel = new RestSupport(this);
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

		_control.addConnectListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				super.widgetSelected(e);
				Button btn = (Button) e.widget;
				final boolean doIt = btn.getSelection();
				new Thread()
				{

					@Override
					public void run()
					{
						if (doIt)
							doConnect();
						else
							doDisconnect();
					}
				}.run();
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
		_control.setTimeEnabled(worked);
	}

	protected void doDisconnect()
	{
		_myModel.doDisconnect();
		_control.setTimeEnabled(false);
		_control.setStateEnabled(false);
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
					_control.setActCourse("" + ((int)newState.getCourse()));
					_control.setActSpeed("" + (int)(newState.getSpeed().getValueIn(WorldSpeed.Kts)));
					_control.setActDepth("" + ((int)newState.getLocation().getDepth()));
				}
			});
	}

	@Override
	public void newScenarioEvent(final long time, final String eventName,
			final String description)
	{
		System.out.println("message at:" + time + " type:" + eventName + " desc:"
				+ description);
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