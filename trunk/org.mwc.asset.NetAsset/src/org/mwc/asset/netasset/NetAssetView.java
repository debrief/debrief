package org.mwc.asset.netasset;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.netasset.view.HolderPane;

public class NetAssetView extends ViewPart {
	public static final String ID = "org.mwc.asset.NetAsset.view";

	private HolderPane _control;

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
				doConnect();
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
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

}