package org.mwc.asset.netasset2.part;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class PartRCPView extends ViewPart
{


	public static final String ID = "org.mwc.asset.NetAsset2.PartView";

	private IVPartControl _view;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		_view = new VPart(parent, SWT.NONE);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == IVPartControl.class)
		{
			res = _view;
		}
		else if(adapter == IVPartUpdate.class)
		{
			res = _view;
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