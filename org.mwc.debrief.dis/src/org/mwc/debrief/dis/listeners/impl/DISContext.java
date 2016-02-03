package org.mwc.debrief.dis.listeners.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.Plottable;

abstract public class DISContext implements IDISContext
{
  /**
   * the current layers object (for the current exercise)
   * 
   */
  private Layers _myLayers = null;

  /**
   * keep track of layers created for this exercise
   * 
   */
  final private List<Layer> _newLayers = new ArrayList<Layer>();

  /**
   * the exercise that we're currently playing
   * 
   */
  short _currentEx = -1;

  /**
   * whether we are currently processing an update
   * 
   */
  private boolean updating = false;

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.impl.IDISContext#getUseNewPlot()
   */
  @Override
  abstract public boolean getUseNewPlot();

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.impl.IDISContext#getLiveUpdates()
   */
  @Override
  abstract public boolean getLiveUpdates();

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.impl.IDISContext#addThisLayer(MWC.GUI.Layer)
   */
  @Override
  public void addThisLayer(final Layer layer)
  {
    Display.getDefault().syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        // remember this top-level data
        _newLayers.add(layer);

        // and put it into the screen
        if (_myLayers != null)
        {
          _myLayers.addThisLayerDoNotResize(layer);
        }
      }
    });
  }

  /**
   * get the layers object for this exercise (creating a new plot, if necessary)
   * 
   * @param exerciseId
   *          the exercise that's being played
   * @return the layers object for this exercise
   */
  private Layers getLayersFor(final short exerciseId)
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
        Display.getDefault().syncExec(new Runnable()
        {
          @Override
          public void run()
          {
            if (_myLayers != null)
            {
              // and clear the new layers
              Iterator<Layer> lIter = _newLayers.iterator();
              while (lIter.hasNext())
              {
                Layer thisL = (Layer) lIter.next();
                _myLayers.removeThisLayer(thisL);
              }
            }
          }
        });
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

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.impl.IDISContext#getFitToData()
   */
  @Override
  abstract public boolean getFitToData();

  /**
   * direct the plot to resize to show all visible data
   * 
   */
  protected void fitToWindow()
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
  
  @Override
  public Iterator<INewItemListener> getNewItemListeners()
  {
    return _myLayers.getNewItemListeners().iterator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.impl.IDISContext#fireUpdate(MWC.GUI.Plottable,
   * MWC.GUI.Layer)
   */
  @Override
  public void fireUpdate(final Plottable newItem, final Layer layer)
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
          _myLayers.fireExtended(newItem, layer);

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

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.impl.IDISContext#findLayer(short, java.lang.String)
   */
  @Override
  public TrackWrapper findLayer(short exerciseId, String theName)
  {
    TrackWrapper res = null;

    /*
     * get the layers, creating a new plot if necessary
     */
    Layers tgt = getLayersFor(exerciseId);

    if (tgt != null)
    {
      res = (TrackWrapper) tgt.findLayer(theName);
    }

    return res;
  }
}
