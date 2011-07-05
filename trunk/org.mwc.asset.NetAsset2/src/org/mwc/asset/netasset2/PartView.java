package org.mwc.asset.netasset2;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.netasset2.view.IVPart;
import org.mwc.asset.netasset2.view.VPart;

import ASSET.Participants.Status;

public class PartView extends ViewPart
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
		_view.addSubmitListener(new SelectionAdapter()
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
	
	public void updateStatus(Status newStatus)
	{
		String crse =  df2.format(newStatus.getCourse());
		String spd = df2.format(newStatus.getSpeed());
		String depth = df2.format(newStatus.getLocation().getDepth());
		
		_view.setActCourse(crse);
		_view.setActSpeed(spd);
		_view.setActSpeed(depth);
	}

	public void setParticipant(String name)
	{
		_view.setParticipant(name);
	}

}