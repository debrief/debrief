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
package org.mwc.cmap.media.actions;

import java.io.File;
import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mwc.cmap.media.Activator;
import org.mwc.cmap.media.views.ImagesView;

public class OpenImagesViewAction extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection sel = HandlerUtil.getCurrentSelectionChecked(event);
		if (sel instanceof IStructuredSelection)
		{
			IStructuredSelection selection = (IStructuredSelection) sel;
			Object object = selection.getFirstElement();
			if (object instanceof IFolder)
			{
				IFolder file = (IFolder) object;
				if (!file.exists())
				{
					return null;
				}
				URI uri = file.getLocationURI();
				if (file.isLinked())
				{
					uri = file.getRawLocationURI();
				}
				try
				{
					File javaFile = EFS.getStore(uri).toLocalFile(0,
							new NullProgressMonitor());
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					IViewReference[] viewReferences = page.getViewReferences();
					ImagesView emptyIv = null;
					ImagesView foundIv = null;
					String fileName = javaFile.getAbsolutePath();
					for (IViewReference viewReference : viewReferences)
					{
						IViewPart view = viewReference.getView(false);
						if (view instanceof ImagesView)
						{
							ImagesView iv = (ImagesView) view;
							if (fileName.equals(iv.getOpenedFolder()))
							{
								foundIv = iv;
								break;
							}
							if (iv.getOpenedFolder() == null)
							{
								emptyIv = iv;
							}
						}
					}
					if (foundIv != null)
					{
						page.activate(foundIv);
					} else if (emptyIv != null)
					{
						page.activate(emptyIv);
						emptyIv.openFolder(fileName);
					} else
					{
						Object o = new NewImagesViewAction().execute(null);
						if (o instanceof ImagesView)
						{
							((ImagesView) o).openFolder(fileName);
						}
					}
				} catch (CoreException e)
				{
					Activator.log(e);
				}
			}
		}
		return null;
	}

}
