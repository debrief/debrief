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

public class NetAssetView extends ViewPart implements ASSETGuest {
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
	public void createPartControl(Composite parent) {
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
				Display.getCurrent().asyncExec(new Runnable(){

					@Override
					public void run()
					{
						doConnect();
					}});
			}
		});
		_control.addSubmitListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				super.widgetSelected(e);
				doSubmit();
			}
		});
		_control.addTimeListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Button widget = (Button) e.widget;
				doPlay(widget.getSelection());
			}});
		


	}

	
	protected void doPlay(boolean play)
	{
		if(play)
			System.out.println("playing");
		else
			System.out.println("stopping");
	}


	protected void doSubmit()
	{
	}


	protected void doConnect()
	{
		boolean worked = _myModel.doConnect();
//		if(worked)
//			_control.setEnabled(true);
//		else
//			_control.setEnabled(false);
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
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
			Status newState)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void newScenarioEvent(long time, String eventName, String description)
	{
		// TODO Auto-generated method stub
		
	}

}