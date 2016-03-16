package org.mwc.debrief.dis.listeners.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
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
   * the replication that we're currently watching
   * 
   */
  long _replicationCounter = -1;

  /**
   * flag for if this scenario is complete - so we don't add new data
   * 
   */
  private boolean _scenarioComplete;

  private long _currentReplication;

  /**
   * constructor, handle some internal initialisation
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
    final Runnable theR = new Runnable()
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
    };
    if(Display.getCurrent() != null)
    {
      theR.run();
    }
    else
    {
      Display.getDefault().syncExec(theR);
    }
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
    // do we need to stop listening to an editor
    if (_myEditor != null)
    {
      // we want to know about screen updates, to
      // keep track of rendering performance
      Object suProvider =
          _myEditor.getAdapter(CanvasType.ScreenUpdateProvider.class);
      if (suProvider != null)
      {
        ScreenUpdateProvider matched =
            (CanvasType.ScreenUpdateProvider) suProvider;
        matched.removeScreenUpdateListener(this);
      }

    }

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
    // flag for if we try to match existing editors when we open a new plot
    final int matchState;

    if (_myEditor != null)
    {
      matchState = IWorkbenchPage.MATCH_NONE;
    }
    else
    {
      matchState = IWorkbenchPage.MATCH_INPUT;

      final IWorkbenchWindow window =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      if (window == null)
      {
        // handle case where application is closing
        return null;
      }
      final IWorkbenchPage page = window.getActivePage();
      final IEditorPart editor = page.getActiveEditor();

      // do we have an editor?
      if (editor != null)
      {
        _myLayers = (Layers) editor.getAdapter(Layers.class);
        if (_myLayers != null)
        {
          // ok, it's suitable
          _myEditor = editor;

          // ok, start listening to it
          ScreenUpdateProvider listener =
              (ScreenUpdateProvider) _myEditor
                  .getAdapter(CanvasType.ScreenUpdateProvider.class);
          if (listener != null)
          {
            listener.addScreenUpdateListener(this);
          }
        }
      }
    }

    if (forceNew || _myEditor == null)
    {
      IEditorInput input = null;

      // hmm, should we drop the old one?
      if (_myEditor != null)
      {
        // ok, start listening to it
        ScreenUpdateProvider listener =
            (ScreenUpdateProvider) _myEditor
                .getAdapter(CanvasType.ScreenUpdateProvider.class);
        if (listener != null)
        {
          listener.removeScreenUpdateListener(this);
        }

        // note - we prevent editor matching if we already have an
        // editor input, so a new instance is created, we
        // don't just re-activate the previous one
        IEditorInput thisInput = _myEditor.getEditorInput();
        if (thisInput instanceof IFileEditorInput)
        {
          IFileEditorInput fi = (IFileEditorInput) thisInput;
          FileEditorInput newF = new FileEditorInput(fi.getFile());
          input = newF;
        }

        // and clear the pointer
        _myEditor = null;
      }

      // ok, we'll have to create one
      if (input == null)
      {
        input = new DISInput("DIS Exercise: " + exerciseId);
      }
      String editorId = "org.mwc.debrief.TrackEditor";
      try
      {
        IWorkbenchWindow window =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();

        // note: we use MATCH_NONE so that we open a fresh instance of the current editor,
        // and not just re-open the current one
        _myEditor = page.openEditor(input, editorId, true, matchState);

        // ok, start listening to it
        ScreenUpdateProvider listener =
            (ScreenUpdateProvider) _myEditor
                .getAdapter(CanvasType.ScreenUpdateProvider.class);
        if (listener != null)
        {
          listener.addScreenUpdateListener(this);
        }
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
  private Layers getLayersFor(final short exerciseId,
      final long replicationCounter)
  {

    // check if this is our existing exercise
    if (_currentEx != exerciseId || _replicationCounter != replicationCounter)
    {
      // nope. stop adding data to the current exercise
      _scenarioComplete = true;
    }

    if (_scenarioComplete)
    {
      // ok, new exercise - do we need a new plot?
      if (getUseNewPlot())
      {
        // ok, create a new plot
        final Runnable theR = new Runnable()
        {
          @Override
          public void run()
          {
            // create a new plot
            getEditor(true, exerciseId);
          }
        };
        if(Display.getCurrent() != null)
        {
          theR.run();
        }
        else
        {
          Display.getDefault().syncExec(theR);
        }
      }
      else
      {
        // no, we can re-use the old one
        final Runnable theR = new Runnable()
        {
          @Override
          public void run()
          {
            if (_myLayers != null)
            {
              clearLayers();
            }
          }
        };
        if(Display.getCurrent() != null)
        {
          theR.run();
        }
        else
        {
          Display.getDefault().syncExec(theR);
        }
      }

      // and remember the exercise id
      _currentEx = exerciseId;
      _replicationCounter = replicationCounter;
    }

    _scenarioComplete = false;

    // have we managed to find some layers?
    if (_myLayers == null)
    {
      final Runnable theR = new Runnable()
      {
        @Override
        public void run()
        {
          // create a new plot
          getEditor(false, exerciseId);
        }
      };
      if(Display.getCurrent() != null)
      {
        theR.run();
      }
      else
      {
        Display.getDefault().syncExec(theR);
      }
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

    public String toString()
    {
      return _name;
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
    final Runnable theR = new Runnable()
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
    };
    if(Display.getCurrent() != null)
    {
      theR.run();
    }
    else
    {
      Display.getDefault().syncExec(theR);
    }
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
    if(_myLayers != null)
    {
    _myLayers.fireExtended(newItem, layer);
    }
    else
    {
      System.err.println("Not updating, no layers object");
    }
  }

  @Override
  public void setReplicationId(long replicationCounter)
  {
    _currentReplication = replicationCounter;
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
    Layers tgt = getLayersFor(exerciseId, _currentReplication);

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

  @Override
  public void scenarioComplete()
  {
    // ok, remember the fact that the current scenario has completed
    _scenarioComplete = true;
  }

}
