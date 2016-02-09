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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.core.ui_support.PartMonitor.ICallback;

import MWC.GUI.CanvasType;
import MWC.GUI.CanvasType.ScreenUpdateProvider;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.Plottable;

abstract public class DISContext implements IDISContext,
    CanvasType.ScreenUpdateListener
{
  /**
   * the current editor
   * 
   */
  private IEditorPart _myEditor = null;

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

  /**
   * construcutor, handle some internal initialisation
   * 
   */
  public DISContext(final PartMonitor pm)
  {
    // ok, sort out the editor closing functionality
    pm.addPartListener(IEditorPart.class, PartMonitor.CLOSED, new ICallback()
    {
      @Override
      public void eventTriggered(String type, Object instance,
          IWorkbenchPart parentPart)
      {
        if (instance == _myEditor)
        {
          stopListeningTo((IEditorPart) instance);
        }
      }
    });

  }

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

  protected void stopListeningTo(IEditorPart editor)
  {
    // ok, stop listening to updates
    ScreenUpdateProvider se = (ScreenUpdateProvider) _myEditor;
    se.removeScreenUpdateListener(this);

    // clear some pointers
    _myEditor = null;
    _myLayers = null;
  }

  protected void listenTo(IEditorPart editor)
  {
    if (editor != null)
    {
      // we want to know about screen updates, to
      // keep track of rendering performance
      Object suProvider =
          editor.getAdapter(CanvasType.ScreenUpdateProvider.class);
      if (suProvider != null)
      {
        ScreenUpdateProvider matched =
            (CanvasType.ScreenUpdateProvider) suProvider;
        matched.addScreenUpdateListener(this);
      }

      // ok, remember this editor
      _myEditor = editor;
    }
  }

  /**
   * create the new editor, as a place to store our data
   * 
   * @param exerciseId
   * @return
   */
  private IEditorPart getEditor(final boolean forceNew, final short exerciseId)
  {
    if(_myEditor == null)
    {
      // we may have just opened. have a look at any existing editors
      Display.getDefault().syncExec(new Runnable()
      {
        @Override
        public void run()
        {
          IWorkbenchWindow window =
              PlatformUI.getWorkbench().getActiveWorkbenchWindow();
          IWorkbenchPage page = window.getActivePage();
          IEditorPart editor = page.getActiveEditor();
          _myLayers = (Layers) editor.getAdapter(Layers.class);
          if(_myLayers != null)
          {
            // ok, it's suitable
            _myEditor = editor;
          }
        }
      });

    }
    
    if (forceNew || _myEditor == null)
    {
      // ok, we'll have to create one
      IEditorInput input = new DISInput("DIS Exercise: " + exerciseId);
      String editorId = "org.mwc.debrief.TrackEditor";
      try
      {
        IWorkbenchWindow window =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        _myEditor = page.openEditor(input, editorId);
      }
      catch (PartInitException e)
      {
        e.printStackTrace();
      }
      listenTo(_myEditor);

      // and get the new layers object
      _myLayers = (Layers) _myEditor.getAdapter(Layers.class);
    }

    // ok, done.
    return _myEditor;
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
        Display.getDefault().syncExec(new Runnable()
        {

          @Override
          public void run()
          {
            // create a new plot
            getEditor(true, exerciseId);
          }

        });
      }
      else
      {
        // no, we can re-use the old one
        Display.getDefault().syncExec(new Runnable()
        {
          @Override
          public void run()
          {
            if (_myLayers != null)
            {
              clearLayers();
            }
          }
        });
      }

      // and remember the exercise id
      _currentEx = exerciseId;
    }

    // have we managed to find some layers?
    if (_myLayers == null)
    {
      Display.getDefault().syncExec(new Runnable()
      {
        @Override
        public void run()
        {
          // create a new plot
          getEditor(false, exerciseId);
        }
      });
    }
    return _myLayers;
  }

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
      return null;
    }

    @Override
    public boolean exists()
    {
      // we indicate that this exists, so that Debrief can start the save process
      return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor()
    {
      return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.debrief.dis",
          "icons/16px/dis_icon.png");
    }

    @Override
    public String getName()
    {
      return _name;
    }

    @Override
    public IPersistableElement getPersistable()
    {
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
    final Iterator<INewItemListener> res;
    if (_myLayers != null)
    {
      res = _myLayers.getNewItemListeners().iterator();
    }
    else
    {
      res = null;
    }

    return res;
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
      // System.out.println("SKIP UPDATE");
    }
    else
    {
      updating = true;
      // pass the new item to the extended method in order to display it in
      // the layer manager
      // newItem = fw;
      Display.getDefault().syncExec(new Runnable()
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
          updating = false;
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.impl.IDISContext#findLayer(short, java.lang.String)
   */
  @Override
  public Layer findLayer(short exerciseId, String theName)
  {
    Layer res = null;

    /*
     * get the layers, creating a new plot if necessary
     */
    Layers tgt = getLayersFor(exerciseId);

    if (tgt != null)
    {
      res = tgt.findLayer(theName);
    }

    return res;
  }

  /**
   * forget about any new layers that have been loaded
   * 
   */
  private void clearLayers()
  {
    // and clear the new layers
    Iterator<Layer> lIter = _newLayers.iterator();
    while (lIter.hasNext())
    {
      Layer thisL = (Layer) lIter.next();
      _myLayers.removeThisLayer(thisL);
    }

    // also, we have to restart any formatters in that layer
    Iterator<INewItemListener> iter = getNewItemListeners();
    while (iter.hasNext())
    {
      Layers.INewItemListener thisI = (Layers.INewItemListener) iter.next();
      thisI.reset();
    }
  }
}
