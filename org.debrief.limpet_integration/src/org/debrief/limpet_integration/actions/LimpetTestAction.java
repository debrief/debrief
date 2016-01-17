package org.debrief.limpet_integration.actions;

import info.limpet.IStoreItem;
import info.limpet.data.store.InMemoryStore;

import java.util.Enumeration;

import org.debrief.limpet_integration.TopLevelTarget;
import org.debrief.limpet_integration.adapters.DebriefLimpetAdapterFactory;
import org.debrief.limpet_integration.data.StoreWrapper;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class LimpetTestAction implements IWorkbenchWindowActionDelegate
{
  private IWorkbenchWindow _window;

  /**
   * The constructor.
   */
  public LimpetTestAction()
  {
  }

  /**
   * The action has been activated. The argument of the method represents the
   * 'real' action sitting in the workbench UI.
   * 
   * @see IWorkbenchWindowActionDelegate#run
   */
  public void run(IAction action)
  {
    System.out.println("ACTION IS ABOUT TO RUN");

    // ok, get the editor
    IEditorPart editor = _window.getActivePage().getActiveEditor();
    if (editor != null)
    {
      Layers layers = (Layers) editor.getAdapter(Layers.class);
      if (layers != null)
      {
        Enumeration<Editable> iter = layers.elements();
        while (iter.hasMoreElements())
        {
          Editable layer = (Editable) iter.nextElement();

          // can we wrap this?
          IStoreItem limpetItem = (IStoreItem) new DebriefLimpetAdapterFactory()
              .getAdapter(layer, IStoreItem.class);

          if (limpetItem == null)
          {
            // hmm, do we need to walk this item?
            if (layer instanceof Layer)
            {
              Layer bl = (Layer) layer;
              Enumeration<Editable> elems = bl.elements();
              while (elems.hasMoreElements())
              {
                Editable thisE = (Editable) elems.nextElement();

                IStoreItem thislimpetItem = (IStoreItem) new DebriefLimpetAdapterFactory()
                    .getAdapter(layer, IStoreItem.class);

                if (thislimpetItem != null)
                {
                  handleThis(thisE, thislimpetItem, layers, bl);
                }
              }
            }
          }

          if (limpetItem != null)
          {
            handleThis(layer, limpetItem, layers, (Layer) layer);
          }
        }
      }
      // also try to add the top level entity
      TopLevelTarget topLevel = new TopLevelTarget(layers);
      topLevel.setRealised(true);
      layers.addThisLayer(topLevel);
    }
  }

  /**
   * @param thisE
   * @param thislimpetItem
   */
  private void handleThis(Editable thisE, IStoreItem limpetItem, Layers layers,
      Layer subjectLayer)
  {
    if (thisE instanceof TrackWrapper)
    {
      InMemoryStore store = new InMemoryStore();// createData();

      StoreWrapper data = new StoreWrapper(store);
      TrackWrapper track = (TrackWrapper) thisE;
      track.add(data);

      // also add a wrapped version of the track
      store.add(limpetItem);

      layers.fireExtended(data, subjectLayer);

    }

  }

  /**
   * Selection in the workbench has been changed. We can change the state of the
   * 'real' action here if we want, but this can only happen after the delegate
   * has been created.
   * 
   * @see IWorkbenchWindowActionDelegate#selectionChanged
   */
  public void selectionChanged(IAction action, ISelection selection)
  {
  }

  /**
   * We can use this method to dispose of any system resources we previously
   * allocated.
   * 
   * @see IWorkbenchWindowActionDelegate#dispose
   */
  public void dispose()
  {
  }

  /**
   * We will cache window object in order to be able to provide parent shell for
   * the message dialog.
   * 
   * @see IWorkbenchWindowActionDelegate#init
   */
  public void init(IWorkbenchWindow window)
  {
    _window = window;
  }

}