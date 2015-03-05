/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ResetPerspective
{
	private static final String RESET_PERSPECTIVE = "";
	private static final long resetPerspective = 2;

	public void resetPerspective()
	{
		try
		{
			final long resetPerspectivePreference = DebriefPlugin.getDefault()
					.getResetPerspectivePreference();
			if (resetPerspective > resetPerspectivePreference)
			{
				Display.getDefault().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						IWorkbenchWindow window = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow();
						if (window == null)
						{
							return;
						}
						final IWorkbenchPage page = window.getActivePage();
						// first start
						if (!(resetPerspectivePreference == 0 && page.findView(DebriefPlugin.INTROVIEW) != null))
						{
							page.resetPerspective();
							String info;
							if (RESET_PERSPECTIVE == null || RESET_PERSPECTIVE.isEmpty()) {
								info = "Your Debrief layout has been reset to provide you with updated features";
							} else {
								info = RESET_PERSPECTIVE;
							}
							showDialog(window.getShell(), info);
						}
					}
				});
			}

		}
		finally
		{
			setResetPerspective();
		}
	}
	
	private void setResetPerspective()
	{
		String value = new Long(resetPerspective).toString();
		DebriefPlugin.getDefault().getPreferenceStore().putValue(DebriefPlugin.RESET_PERSPECTIVE, value);
	}
	
	protected void showDialog(Shell shell, final String info)
	{
		final PopupDialog dialog = new PopupDialog(shell, PopupDialog.HOVER_SHELLSTYLE,
				true, false, false, false, false, null, 
				null) {

			@Override
			protected Control createDialogArea(Composite parent)
			{
				GridData gd = new GridData(GridData.FILL_BOTH);
				StyledText text = new StyledText(parent, SWT.MULTI | SWT.READ_ONLY
						| SWT.WRAP);
				text.setLayoutData(gd);
				text.setForeground(parent.getDisplay().getSystemColor(
						SWT.COLOR_INFO_FOREGROUND));
				text.setBackground(parent.getDisplay().getSystemColor(
						SWT.COLOR_INFO_BACKGROUND));
				text.setText(info);
				text.setEditable(false);
				return text;
			}
		};
		dialog.open();
	}



}
