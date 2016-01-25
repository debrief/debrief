package org.mwc.debrief.dis.listeners.impl;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.interfaces.IControllableViewport;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;

abstract public class DISContext
{
  Layers _myLayers = null;

  short _currentEx = -1;

  /**
   * whether a new plot should be created for each new replication
   * 
   * @return
   */
  abstract public boolean getUseNewPlot();

  /**
   * whether a UI should update on each new data item
   * 
   * @return
   */
  abstract public boolean getLiveUpdates();

  public Layers getLayersFor(final short exerciseId)
  {
    // check if this is our existing exercise
    if (_currentEx != exerciseId)
    {
      // ok, new exercise - do we need a new plot?
      if (getUseNewPlot())
      {

        // ok, create a new plot
        System.out.println("== CREATING NEW PLOT FOR NEW EXERCISE ==");

        Display.getDefault().syncExec(new Runnable()
        {

          @Override
          public void run()
          {
            // create a new plot

            IEditorInput input = new DISInput("DIS Exercise: " + exerciseId);
            String editorId = "org.mwc.debrief.TrackEditor";
            try
            {
              IWorkbenchWindow window =
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow();
              IWorkbenchPage page = window.getActivePage();
              IEditorPart newP = page.openEditor(input, editorId);

              // and get the new layers object
              _myLayers = (Layers) newP.getAdapter(Layers.class);
            }
            catch (PartInitException e)
            {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        });

      }
      else
      {
        // ok, reuse the current one - no action required
      }

      _currentEx = exerciseId;
    }

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
          if (editor != null)
          {
            _myLayers = (Layers) editor.getAdapter(Layers.class);
          }
        }
      });

    }
    return _myLayers;
  }

  // TODO: produc
  public class DISInput implements IEditorInput
  {

    final private String _name;

    public DISInput(String name)
    {
      _name = name;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter)
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public boolean exists()
    {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public ImageDescriptor getImageDescriptor()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String getName()
    {
      return _name;
    }

    @Override
    public IPersistableElement getPersistable()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String getToolTipText()
    {
      return "New DIS Session";
    }

  }

  abstract public boolean getFitToData();

  public void fitToWindow()
  {
    Display.getDefault().syncExec(new Runnable()
    {

      @Override
      public void run()
      {
        IWorkbenchWindow window =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        IEditorPart editor = page.getActiveEditor();
        IControllableViewport icv =
            (IControllableViewport) editor
                .getAdapter(IControllableViewport.class);
        if (icv != null)
        {
          icv.rescale();
        }
      }
    });
  }

  private boolean updating = false;

  public void
      fireUpdate(final Plottable newItem, final TrackWrapper finalTrack)
  {
    if (updating)
    {
      System.out.println("SKIP UPDATE");
    }
    else
    {
      updating = true;
      // pass the new item to the extended method in order to display it in
      // the layer manager
      // newItem = fw;
      Display.getDefault().asyncExec(new Runnable()
      {

        @Override
        public void run()
        {
          _myLayers.fireExtended(newItem, finalTrack);

          // hmm, do we need to resize?
          if (getFitToData())
          {
            fitToWindow();
          }
        }
      });
      updating = false;
    }
  }
}
