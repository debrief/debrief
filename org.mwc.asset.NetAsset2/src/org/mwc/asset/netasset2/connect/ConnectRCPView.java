/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.netasset2.connect;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.netasset2.connect.IVConnect.StringProvider;

public class ConnectRCPView extends ViewPart
{
	public static final String ID = "org.mwc.asset.NetAsset2.ConnectView";
	private static final String LAST_IP = "LAST_IP_ADDRESS";
	private IVConnect controlV;
	private String _lastIPAddress = "127.0.0.1";

	public ConnectRCPView()
	{
	}
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent)
	{
		final StringProvider stringProvider = new StringProvider()
		{

			@Override
			public String getString(final String title, final String message)
			{
				String res = null;
				final InputDialog dialog = new InputDialog(Display.getDefault()
						.getActiveShell(), title, message, _lastIPAddress, null); // new
																																			// input
																																			// dialog
				if (dialog.open() == IStatus.OK)
				{
					res = _lastIPAddress = dialog.getValue();
				}
				return res;
			}
		};

		controlV = new VConnect(parent, SWT.NONE, stringProvider);
	}

	@Override
	public void init(final IViewSite site, final IMemento memento) throws PartInitException
	{
		// do the parent bit
		super.init(site, memento);

		if (memento != null)
		{
			// now restore my state
			final String lastA = memento.getString(LAST_IP);
			if (lastA != null)
				_lastIPAddress = lastA;
		}
	}

	@Override
	public void saveState(final IMemento memento)
	{
		super.saveState(memento);

		memento.putString(LAST_IP, _lastIPAddress);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter)
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