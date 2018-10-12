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
package org.mwc.cmap.media.actions;

import java.io.File;
import java.net.URI;
import java.util.Date;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.media.Activator;
import org.mwc.cmap.media.dialog.VideoPlayerStartTimeDialog;
import org.mwc.cmap.media.views.VideoPlayerView;

public class OpenVideoPlayerAction extends AbstractHandler
{

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    ISelection sel = HandlerUtil.getCurrentSelectionChecked(event);
    if (sel instanceof IStructuredSelection)
    {
      IStructuredSelection selection = (IStructuredSelection) sel;
      Object object = selection.getFirstElement();
      if (object instanceof IFile)
      {
        IFile file = (IFile) object;
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
          VideoPlayerView emptyVpv = null;
          VideoPlayerView foundVpv = null;
          String fileName = javaFile.getAbsolutePath();
          for (IViewReference viewReference : viewReferences)
          {
            IViewPart view = viewReference.getView(false);
            if (view instanceof VideoPlayerView)
            {
              VideoPlayerView vpv = (VideoPlayerView) view;
              if (fileName.equals(vpv.getSelected()))
              {
                foundVpv = vpv;
                break;
              }
              if (vpv.getSelected() == null)
              {
                emptyVpv = vpv;
              }
            }
          }
          
          // see if we can get a start time for the current plot
          Date startTime = null;
          IEditorPart editor = CorePlugin.getActivePage().getActiveEditor();
          if(editor != null)
          {
            TimeProvider timeC = (TimeProvider) editor.getAdapter(TimeProvider.class);
            if(timeC != null)
            {
              startTime = timeC.getTime().getDate();
            }
          }
          
          if(startTime == null)
          {
            VideoPlayerStartTimeDialog dialog = new VideoPlayerStartTimeDialog();
            dialog.setStartTime(new Date());
            dialog.setBlockOnOpen(true);
            if(dialog.open()==Window.OK) {
              startTime = dialog.getStartTime();
            }
            else
            {
              // ok, cancelled
              return null;
            }
          }
          
          if (foundVpv != null)
          {
            page.activate(foundVpv);
          }
          else if (emptyVpv != null)
          {
            page.activate(emptyVpv);
            emptyVpv.open(fileName, startTime);
          }
          else
          {
            Object o = new NewVideoPlayerAction().execute(null);
            if (o instanceof VideoPlayerView)
            {
              ((VideoPlayerView) o).open(fileName, startTime);
            }
          }
        }
        catch (CoreException e)
        {
          Activator.log(e);
        }
      }
    }
    return null;
  }

}
