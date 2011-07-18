package org.mwc.asset.netasset2.sensor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.netasset2.time.IVTime;

import ASSET.Participants.ParticipantDetectedListener;

public class SensorRCPView extends ViewPart implements IAdaptable
{
	public static final String ID = "org.mwc.asset.NetAsset2.SensorView";

	private IVTime _timer;
	private ParticipantDetectedListener _detector;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		VSensor view = new VSensor(parent, SWT.NONE);
		_timer = view;
		_detector = view;
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == IVTime.class)
		{
			res = _timer;
		}
		else if(adapter == ParticipantDetectedListener.class)
		{
			res = _detector;
		}

		if (res == null)
			res = super.getAdapter(adapter);

		return res;
	}

	public void setEnabled(boolean val)
	{
	}

}