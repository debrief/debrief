package org.mwc.asset.netasset2.part;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import ASSET.Participants.Status;
import MWC.GenericData.WorldSpeed;

public class PartRCPView extends ViewPart
{

	public static interface NewDemStatus
	{
		public void demanded(double course, double speed, double depth);
	}

	public static final String ID = "org.mwc.asset.NetAsset2.PartView";

	private IVPart _view;

	private NewDemStatus _listener;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		_view = new VPart(parent, SWT.NONE);
		_view.setSubmitListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (_listener != null)
				{
					_listener.demanded(Double.valueOf(_view.getDemCourse()),
							Double.valueOf(_view.getDemSpeed()),
							Double.valueOf(_view.getDemDepth()));
				}
			}
		});
	}

	public void setDemStatusListener(NewDemStatus listener)
	{
		_listener = listener;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}

	public void setEnabled(boolean val)
	{
		_view.setEnabled(val);
	}

	DecimalFormat df2 = new DecimalFormat("0.00");
	DecimalFormat df0 = new DecimalFormat("0");
	
	public void updateStatus(Status newStatus)
	{
		final String crse =  df0.format(newStatus.getCourse());
		final String spd = df2.format(newStatus.getSpeed().getValueIn(WorldSpeed.Kts));
		final String depth = df2.format(newStatus.getLocation().getDepth());
		
		Display.getDefault().asyncExec(new Runnable(){

			@Override
			public void run()
			{
				_view.setActCourse(crse + "\u00B0");
				_view.setActSpeed(spd + "kts");
				_view.setActDepth(depth + "m");
			}});
		
	}

	public void setParticipant(String name)
	{
		_view.setParticipant(name);
	}

}