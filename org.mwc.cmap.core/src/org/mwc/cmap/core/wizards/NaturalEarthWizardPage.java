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
package org.mwc.cmap.core.wizards;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.Editable;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class NaturalEarthWizardPage extends CoreEditableWizardPage
{

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NaturalEarthWizardPage(final ISelection selection)
	{
		super(selection, "nePage", "Add Vectored background data",
				"Use this page to add the Natural Earth dataset to your plot.",
				"images/ne_wizard.png", null);
	}

	@Override
	protected Editable createMe()
	{
		if (_editable == null)
		{
			IHandlerService handlerService = getHandlerService();
			if (handlerService == null)
			{
				CorePlugin.logError(IStatus.WARNING, "IHandlerService is null", null);
				return null;
			}
			try
			{
				_editable = (Editable) handlerService.executeCommand("org.mwc.cmap.naturalearth.ui.CreateNELayer", null);
			}
			catch (Exception ex)
			{
				CorePlugin.logError(IStatus.WARNING, "org.mwc.cmap.naturalearth.ui.CreateNELayer not found", null);
				return null;
			}
		}
		return _editable;
	}

	private IHandlerService getHandlerService()
	{
		return (IHandlerService)PlatformUI.getWorkbench().getService(IHandlerService.class);
	}

	/**
	 * @return
	 */
	@Override
	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		return new PropertyDescriptor[0];
	}

	@Override
	protected void addComponents(Composite container)
	{
		Link link1 = new Link(container, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 3;
		link1.setLayoutData(gd);
		link1.setText("Natural Earth data trimmed to suit Debrief's Mercator projection is available online from here:\n"
				+ "<a>https://github.com/debrief/NaturalEarth</a>");
		link1.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				try
				{
					IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
					IWebBrowser browser = support.getExternalBrowser();
					browser.openURL(new URL("https://github.com/debrief/NaturalEarth"));
				}
				catch (Exception e)
				{
					CorePlugin.logError(IStatus.WARNING, "Cannot open an external browser", e);
				}
			}
		});
		String path = getDataFolder();
		if (path == null || path.isEmpty() || !(new File(path).isDirectory()))
		{
			final Link link = new Link(container, SWT.BORDER);
			gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			gd.horizontalSpan = 3;
			link.setLayoutData(gd);
			link.setText("Natural Earth data folder isn't set. Debrief will use the default folder.\n"
					+ "You can set a data folder by clicking <a>Window>Preferences>Maritime analysis>Natural Earth</a>.");
			link.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent event)
				{
					PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(link.getShell(), "org.mwc.cmap.naturalearth.preferences.NaturalEarhPrefs", null, null);
					dialog.open();
				}
			});
		}
	}

	private String getDataFolder()
	{
		return Platform.getPreferencesService().getString(
				"org.mwc.cmap.NaturalEarth", "dataFolder", null, null);
	}

}