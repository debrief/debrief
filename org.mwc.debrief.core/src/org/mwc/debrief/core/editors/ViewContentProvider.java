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
package org.mwc.debrief.core.editors;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.Extensions.AdditionalData;
import Debrief.Wrappers.Extensions.AdditionalProvider;
import Debrief.Wrappers.Extensions.AdditionalProvider.ExistingChildrenMayNeedToBeWrapped;
import Debrief.Wrappers.Extensions.AdditionalProviderWrapper;
import Debrief.Wrappers.Extensions.ExtensionContentProvider;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottables;

/*
 * The content provider class is responsible for providing objects to the view.
 * It can wrap existing objects in adapters or simply return objects as-is.
 * These objects may be sensitive to the current input of the view, or ignore it
 * and always show the same content (like Task List, for example).
 */
public class ViewContentProvider implements IStructuredContentProvider,
    ITreeContentProvider
{
  /**
   * set a limit on the limit for which we allow a layer to be expanded
   */
  private static final int MAX_ITEMS = 10000;
  private ArrayList<ExtensionContentProvider> _contentProviders;

  /**
   * @param view
   */
  public ViewContentProvider()
  {

  }

  public void inputChanged(final Viewer v, final Object oldInput,
      final Object newInput)
  {
  }

  public void dispose()
  {
  }

  public Object[] getElements(final Object parent)
  {
    Object[] res = null;
    if (parent instanceof Layers)
    {
      // cool - run through the layers
      final Vector<EditableWrapper> list = new Vector<EditableWrapper>(0, 1);
      final Layers theLayers = (Layers) parent;
      final Enumeration<Editable> numer = theLayers.elements();
      while (numer.hasMoreElements())
      {
        final Layer thisL = (Layer) numer.nextElement();
        final EditableWrapper wrapper =
            new EditableWrapper(thisL, null, theLayers);
        list.add(wrapper);
      }
      res = list.toArray();
    }
    return res;
  }

  public Object getParent(final Object child)
  {
    Object res = null;
    if (child instanceof EditableWrapper)
    {
      final EditableWrapper thisP = (EditableWrapper) child;
      final EditableWrapper parent = thisP.getParent();
      res = parent;
    }
    return res;
  }

  public Object[] getChildren(final Object parent)
  {
    Object[] res = new Object[0];
    if (parent instanceof EditableWrapper)
    {
      final EditableWrapper pl = (EditableWrapper) parent;
      if (pl.hasChildren())
      {
        final Vector<EditableWrapper> list = new Vector<EditableWrapper>(0, 1);

        final HasEditables thisL = (HasEditables) pl.getEditable();

        final boolean childrenHaveBeenWrapped;

        // is this a data provider?
        if (thisL instanceof AdditionalProvider)
        {
          AdditionalProvider container = (AdditionalProvider) thisL;
          AdditionalData additionalData = container.getAdditionalData();

          // ok, check if it implements the interface, AND it wants them wrapped
          final boolean childrenShouldBeWrapped =
              container instanceof AdditionalProvider.ExistingChildrenMayNeedToBeWrapped
                  && ((AdditionalProvider.ExistingChildrenMayNeedToBeWrapped) container)
                      .childrenNeedWrapping();

          // and is there any additional data?
          if (childrenShouldBeWrapped && additionalData.size() > 0)
          {
            // ok, do the children need to be wrapped?
            ExistingChildrenMayNeedToBeWrapped ecw =
                (ExistingChildrenMayNeedToBeWrapped) container;

            // ok, get the collective name
            final String collectiveName = ecw.getItemsName();

            // ok. are there other child items? If so, we need to demote them
            final Enumeration<Editable> numer = thisL.elements();
            if (numer.hasMoreElements())
            {
              // yes, there are children present
              Layer wrappedElements =
                  new ChildDataWrapper(collectiveName, numer, thisL
                      .hasOrderedChildren(), (Layer) thisL);

              // ok, wrap it.
              final EditableWrapper pw =
                  new EditableWrapper.OrderedEditableWrapper(wrappedElements,
                      pl, pl.getLayers(), 0);
              list.add(pw);

              // children handled. remember that
              childrenHaveBeenWrapped = true;
            }
            else
            {
              childrenHaveBeenWrapped = false;
            }
          }
          else
          {
            // don't need wrapping
            childrenHaveBeenWrapped = false;
          }

          // ok, we still need to wrap this container
          Editable addData =
              new AdditionalProviderWrapper(additionalData,
                  getContentProviderExtensions());
          EditableWrapper pw =
              new EditableWrapper.OrderedEditableWrapper(addData, pl, pl
                  .getLayers(), 1);
          list.add(pw);
        }
        else
        {
          // no additional data present
          childrenHaveBeenWrapped = false;
        }

        // have the children been wrapped?
        if (!childrenHaveBeenWrapped)
        {
          // no, let's do it

          // right, do they have their own order?
          if (thisL.hasOrderedChildren())
          {
            int index = 0;
            final Enumeration<Editable> numer = thisL.elements();
            while (numer.hasMoreElements())
            {
              final Editable thisP = (Editable) numer.nextElement();
              final EditableWrapper pw =
                  new EditableWrapper.OrderedEditableWrapper(thisP, pl, pl
                      .getLayers(), index);
              list.add(pw);
              index++;
            }
          }
          else
          {
            final Enumeration<Editable> numer = thisL.elements();
            if (numer != null)
            {
              while (numer.hasMoreElements())
              {
                final Editable thisP = (Editable) numer.nextElement();
                final EditableWrapper pw =
                    new EditableWrapper(thisP, pl, pl.getLayers());
                list.add(pw);
              }
            }
          }
        }

        // ok, done.
        res = list.toArray();
      }
    }
    return res;
  }

  public boolean hasChildren(final Object parent)
  {
    boolean res = false;
    if (parent instanceof EditableWrapper)
    {
      final EditableWrapper pw = (EditableWrapper) parent;

      // special case - only allow the layer to open if it has less than max-items
      Editable ed = pw.getEditable();
      if (ed instanceof Plottables)
      {
        // get the object as a list
        Plottables pl = (Plottables) ed;

        // check if it's a reasonable size
        res = pl.size() < MAX_ITEMS;
      }
      else
        res = pw.hasChildren();
    }
    return res;
  }

  private static class ChildDataWrapper extends BaseLayer
  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final String collectiveName;
    private final Enumeration<Editable> numer;
    private final boolean hasOrderedChildren;
    private final Layer parent;

    public ChildDataWrapper(final String collName,
        final Enumeration<Editable> enumerator, final boolean orderedChildren, Layer parent)
    {
      collectiveName = collName;
      numer = enumerator;
      hasOrderedChildren = orderedChildren;
      this.parent = parent;
    }

    @Override
    public String toString()
    {
      return collectiveName;
    }

    @Override
    public Enumeration<Editable> elements()
    {
      return numer;
    }

    @Override
    public boolean hasOrderedChildren()
    {
      return hasOrderedChildren;
    }

    @Override
    public void add(Editable thePlottable)
    {
      parent.add(thePlottable);
    }

    @Override
    public boolean hasEditor()
    {
      return parent.hasEditor();
    }

    @Override
    public EditorType getInfo()
    {
      return parent.getInfo();
    }

    @Override
    public void setVisible(boolean visible)
    {
      parent.setVisible(visible);
    }

    @Override
    public boolean getVisible()
    {
      return parent.getVisible();
    }
    
    

  }

  private List<ExtensionContentProvider> getContentProviderExtensions()
  {
    if (_contentProviders == null)
    {
      _contentProviders = new ArrayList<ExtensionContentProvider>();

      IExtensionRegistry registry = Platform.getExtensionRegistry();

      if (registry != null)
      {

        final IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint(
                DebriefPlugin.PLUGIN_NAME,
                DebriefPlugin.CONTENT_PROVIDER_EXTENSION_POINT_ID);

        final IExtension[] extensions = point.getExtensions();
        for (int i = 0; i < extensions.length; i++)
        {
          final IExtension iExtension = extensions[i];
          final IConfigurationElement[] confE =
              iExtension.getConfigurationElements();
          for (int j = 0; j < confE.length; j++)
          {
            final IConfigurationElement iConfigurationElement = confE[j];
            ExtensionContentProvider newInstance;
            try
            {
              newInstance =
                  (ExtensionContentProvider) iConfigurationElement
                      .createExecutableExtension("contentProvider");
              _contentProviders.add(newInstance);
            }
            catch (final CoreException e)
            {
              CorePlugin.logError(Status.ERROR,
                  "Trouble whilst loading REP import extensions", e);
            }
          }
        }
      }
    }
    return _contentProviders;
  }
}