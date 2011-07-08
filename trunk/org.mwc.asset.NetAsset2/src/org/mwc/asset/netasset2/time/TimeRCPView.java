package org.mwc.asset.netasset2.time;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TimeRCPView extends ViewPart implements IAdaptable
{
	public static final String ID = "org.mwc.asset.NetAsset2.TimeView";

	private IVTime _view;
	private IVTimeControl _control;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		VTime time = new VTime(parent, SWT.NONE);
		_control = time;
		_view = time;
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
			res = _view;
		}
		else if(adapter == IVTimeControl.class)
		{
			res = _control;
		}

		if (res == null)
			res = super.getAdapter(adapter);

		return res;
	}

	public void setEnabled(boolean val)
	{
		_control.setEnabled(val);
	}

}