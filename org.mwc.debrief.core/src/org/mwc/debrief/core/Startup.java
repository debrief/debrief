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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class Startup implements IStartup
{

	private static final String RESET_PERSPECTIVE = ".resetPerspective";
	private static final String INTROVIEW = "org.eclipse.ui.internal.introview";

	@Override
	public void earlyStartup()
	{
		removePerspective();
		removePreferencePages();
		testResetPerspective();
		createStartProject();
	}

	private void createStartProject()
	{
		// TODO Auto-generated method stub
		
	}

	private void testResetPerspective()
	{
		Location installLocation = Platform.getInstallLocation();
		if (installLocation == null)
		{
			setResetPerspectiveTimestamp();
			return;
		}
		String installFileStr = installLocation.getURL().getFile();
		if (installFileStr == null)
		{
			setResetPerspectiveTimestamp();
			return;
		}
		try
		{
			File installDir = new File(installFileStr);
			if (installDir.isDirectory())
			{
				File installFile = new File(installDir, RESET_PERSPECTIVE);
				if (installFile.isFile())
				{
					final String info = readFile(installFile);
					final long timestamp = installFile.lastModified();
					final long resetPerspectivePreference = DebriefPlugin.getDefault()
							.getResetPerspectiveTimestamp();
					if (timestamp > resetPerspectivePreference)
					{
							Display.getDefault().asyncExec(new Runnable()
							{
								
								@Override
								public void run()
								{
									IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
									if (window == null) {
										return;
									}
									final IWorkbenchPage page = window.getActivePage();
									// first start
									if ( !(resetPerspectivePreference == 0 && page.findView(INTROVIEW) != null)) {
										page.resetPerspective();
										showDialog(window.getShell(), info);
									}
								}
							});
					}
				}
			}
		}
		finally
		{
			setResetPerspectiveTimestamp();
		}
	}

	private String readFile(File file)
	{
		String info = null;
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null)
			{
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			info = stringBuilder.toString();
		}
		catch (Exception e)
		{
			// ignore; return default message
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}

		if (info == null || info.isEmpty())
		{
			info = "Your user interface has been reset to include new Debrief features";
		}
		return info;
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

	private void setResetPerspectiveTimestamp()
	{
		String value = new Long(System.currentTimeMillis()).toString();
		DebriefPlugin.getDefault().getPreferenceStore().putValue(DebriefPlugin.RESET_PERSPECTIVE_TIMESTAMP, value);
	}

	private void removePreferencePages()
	{
		PreferenceManager preferenceManager = PlatformUI.getWorkbench()
				.getPreferenceManager();
		if (preferenceManager == null) {
			return;
		}
		preferenceManager.remove("org.eclipse.debug.ui.DebugPreferencePage");
		preferenceManager.remove("org.eclipse.debug.ui.LaunchingPreferencePage");
		preferenceManager
				.remove("org.eclipse.debug.ui.ViewManagementPreferencePage");
		preferenceManager.remove("org.eclipse.debug.ui.ConsolePreferencePage");
		preferenceManager
				.remove("org.eclipse.debug.ui.StringVariablePreferencePage");
		preferenceManager.remove("org.eclipse.debug.ui.PerspectivePreferencePage");
		preferenceManager.remove("org.eclipse.debug.ui.LaunchConfigurations");
		preferenceManager.remove("org.eclipse.debug.ui.LaunchDelegatesPreferencePage");
		preferenceManager.remove("org.eclipse.team.ui.TeamPreferences");

	}

	private void removePerspective()
	{
		IPerspectiveRegistry registry = PlatformUI.getWorkbench().getPerspectiveRegistry();
		if (registry == null)
		{
			return;
		}
		List<IPerspectiveDescriptor> descriptors = new ArrayList<IPerspectiveDescriptor>();

		addDescriptor(registry, descriptors, "org.eclipse.debug.ui.DebugPerspective");
		addDescriptor(registry, descriptors, "org.eclipse.team.ui.TeamSynchronizingPerspective");

		// FIXME this method doesn't work on Eclipse E4 (Juno/Kepler)
		if (registry instanceof IExtensionChangeHandler && !descriptors.isEmpty())
		{
			IExtensionChangeHandler handler = (IExtensionChangeHandler) registry;
			handler.removeExtension(null, descriptors.toArray());
		}
	}

	private void addDescriptor(IPerspectiveRegistry registry,
			List<IPerspectiveDescriptor> descriptors, String id)
	{
		IPerspectiveDescriptor perspectiveDescriptor = registry.findPerspectiveWithId(id);
		if (perspectiveDescriptor != null)
		{
			descriptors.add(perspectiveDescriptor);
		}
	}

}
