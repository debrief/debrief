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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.mwc.debrief.core.dialogs.CreateProjectDialog;

public class CreateDebriefProject
{

	public void createStartProject()
	{
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		if (projects.length > 0)
		{
			return;
		}
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				final WorkbenchWindow window = (WorkbenchWindow) PlatformUI
						.getWorkbench().getActiveWorkbenchWindow();
				if (window == null)
				{
					return;
				}
				final IWorkbenchPage page = window.getActivePage();
				// first start
				if (page.findView(DebriefPlugin.INTROVIEW) != null
						&& !window.getCoolBarVisible()
						&& !window.getPerspectiveBarVisible())
				{
					IViewReference viewRef = page.findViewReference(DebriefPlugin.INTROVIEW);
					if (page.getPartState(viewRef) == IWorkbenchPage.STATE_MAXIMIZED)
					{
						window.addPropertyChangeListener(new IPropertyChangeListener()
						{

							@Override
							public void propertyChange(PropertyChangeEvent event)
							{
								String property = event.getProperty();
								if (WorkbenchWindow.PROP_COOLBAR_VISIBLE.equals(property)
										|| WorkbenchWindow.PROP_PERSPECTIVEBAR_VISIBLE.equals(property))
								{
									Object newValue = event.getNewValue();
									if (newValue instanceof Boolean
											&& ((Boolean) newValue).booleanValue())
									{
										createProject();
										window.removePropertyChangeListener(this);
									}
								}
							}

						});
					}
					else
					{
						createProject();
					}
				}
				else
				{
					createProject();
				}
			}
		});

	}

	private void createProject()
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				Shell shell = PlatformUI.getWorkbench().getModalDialogShellProvider()
						.getShell();
				CreateProjectDialog dialog = new CreateProjectDialog(shell, true);
				dialog.open();

			}
		});
	}

}
