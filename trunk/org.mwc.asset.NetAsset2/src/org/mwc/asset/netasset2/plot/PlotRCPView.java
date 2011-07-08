package org.mwc.asset.netasset2.plot;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.netasset2.part.IVPartUpdate;
import org.mwc.asset.netasset2.time.IVTime;

public class PlotRCPView extends ViewPart implements IAdaptable
{
	public static final String ID = "org.mwc.asset.NetAsset2.PlotView";

	private IVPartUpdate _view;
	private IVTime _timer;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		VPlot plot  = new VPlot(parent, SWT.NONE);
		_view = plot;
		_timer =plot;
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
		}else if(adapter == IVTime.class)
		{
			return _timer;
		}

		if (res == null)
			res = super.getAdapter(adapter);

		return res;
	}

}