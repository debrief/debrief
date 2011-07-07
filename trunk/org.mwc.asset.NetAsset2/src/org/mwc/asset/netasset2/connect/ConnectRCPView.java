package org.mwc.asset.netasset2.connect;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class ConnectRCPView extends ViewPart
{
	public static final String ID = "org.mwc.asset.NetAsset2.ConnectView";
	private IVConnect controlV;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		controlV = new VConnect(parent, SWT.NONE);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == IVConnect.class)
		{
			res = controlV;
		}

		if (res == null)
			res = super.getAdapter(adapter);

		return res;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}

}