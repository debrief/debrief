
package org.mwc.cmap.naturalearth.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.wrapper.NELayer;

import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class InsertNaturalEarth extends AbstractHandler
{

  public Layers getLayers()
  {
    final Layers[] answer = new Layers[1];
    Display.getDefault().syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        // nope, better generate it
        final IWorkbench wb = PlatformUI.getWorkbench();
        final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        final IWorkbenchPage page = win.getActivePage();
        IEditorPart editor = page.getActiveEditor();
        answer[0] = (Layers) editor.getAdapter(Layers.class);
      }
    });
    return answer[0];
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    // check if we have a data path, and check it exists
    if (!NELayer.hasGoodPath())
    {
      System.err.println("Don't have good path assigned");
    }
    else
    {
      Layers layers = getLayers();
      if (layers != null)
      {
        //
        NELayer ne = new NELayer(Activator.getDefault().getDefaultStyleSet());
        layers.addThisLayer(ne);
      }
    }

    return null;
  }
}
