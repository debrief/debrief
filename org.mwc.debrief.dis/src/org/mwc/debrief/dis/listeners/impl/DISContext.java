package org.mwc.debrief.dis.listeners.impl;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import MWC.GUI.Layers;

abstract public class DISContext
{
  Layers _myLayers = null;

  /** whether a UI should update on each new data item
   * 
   * @return
   */
  abstract public boolean getLiveUpdates();
  
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
