package org.mwc.debrief.dis.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.dis.DisActivator;
import org.mwc.debrief.dis.ui.views.DisListenerView;

public class DISRunInSimulatorHandler extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    DisListenerView view = null;
    try {
      IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
      Object firstElement = selection.getFirstElement();
      if (firstElement instanceof IResource) {
        IResource resource = (IResource) firstElement;
        if (resource.getFileExtension().equals("inp")) {
          String filePath = resource.getLocation().toOSString();
          view = (DisListenerView) window.getActivePage().showView(CorePlugin.DIS_LISTENER_VIEW);
          view.doLaunch(filePath);
        }
      }
    } catch (PartInitException e) {
      DisActivator.log(e);
    }
    return view;
  }

}
