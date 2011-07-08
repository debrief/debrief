package org.mwc.asset.netasset2.plot;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.netasset2.part.IVPartUpdate;

public class PlotRCPView extends ViewPart implements IAdaptable
{
	public static final String ID = "org.mwc.asset.NetAsset2.PlotView";

	private IVPartUpdate _view;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		_view = new VPlot(parent, SWT.NONE);
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

		if (adapter == IVPartUpdate.class)
		{
			res = _view;
		}

		if (res == null)
			res = super.getAdapter(adapter);

		return res;
	}

	public void setEnabled(boolean val)
	{
		_view.setEnabled(val);
	}

}