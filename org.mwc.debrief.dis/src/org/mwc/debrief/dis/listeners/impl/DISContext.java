package org.mwc.debrief.dis.listeners.impl;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import MWC.GUI.Layers;

public class DISContext
{
  Layers _myLayers = null;

  public Layers getLayers()
  {
    if (_myLayers == null)
    {

      Display.getDefault().syncExec(new Runnable()
      {
        @Override
        public void run()
        {
          IWorkbenchWindow iw =
              PlatformUI.getWorkbench().getActiveWorkbenchWindow();
          IWorkbenchPage activePage = iw.getActivePage();
          IEditorPart editor = activePage.getActiveEditor();
          _myLayers = (Layers) editor.getAdapter(Layers.class);
        }
      });

    }
    return _myLayers;
  }

}
