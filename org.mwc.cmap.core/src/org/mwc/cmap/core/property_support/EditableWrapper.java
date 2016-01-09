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
package org.mwc.cmap.core.property_support;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.Editable;
import MWC.GUI.Editable.DeprecatedPropertyDescriptor;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.Griddable;
import MWC.GUI.Griddable.NonBeanPropertyDescriptor;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * embedded class which wraps a plottable object alongside some useful other
 * bits
 */
public class EditableWrapper implements IPropertySource
{
  /**
   * the data object we are wrapping
   */
  protected final Editable _editable;

  /**
   * the editable properties of this object
   */
  IPropertyDescriptor[] _myDescriptors;

  /**
   * the editable properties of this object
   */
  IPropertyDescriptor[] _myGridDescriptors;

  /**
   * the layers we're looking at
   */
  protected final Layers _theLayers;

  /**
   * the tags we use for the boolean editor
   */
  static String[] _booleanTags = new String[]
  { "Yes", "No" };

  /**
   * the parent of this object
   */
  private final EditableWrapper _parent;

  /**
   * constructor - ok, lets get going
   * 
   * @param plottable
   * @param theLayers
   */
  public EditableWrapper(final Editable plottable,
      final EditableWrapper parent, final Layers theLayers)
  {
    _editable = plottable;
    _theLayers = theLayers;
    _parent = parent;
  }

  /**
   * constructor - ok, lets get going
   * 
   * @param plottable
   * @param theLayers
   */
  public EditableWrapper(final Editable plottable, final Layers theLayers)
  {
    this(plottable, null, theLayers);
  }

  /**
   * constructor - ok, lets get going
   * 
   * @param plottable
   * @param theLayers
   */
  public EditableWrapper(final Editable plottable)
  {
    this(plottable, null, null);
  }

  public Layer getTopLevelLayer()
  {
    Layer res = null;
    // ok. we may just be changing a single layer
    // head back up the tree to the base layer
    EditableWrapper parent = getParent();

    // just see if we are a top-level layer
    if (parent == null)
    {
      // get the parent then
      final Editable parentE = getEditable();

      // right, is it an editable?
      if (parentE instanceof Layer)
        res = (Layer) parentE;
    }
    else
    {
      EditableWrapper parentParent = parent;
      while (parent != null)
      {
        parent = parent.getParent();
        if (parent != null)
        {
          parentParent = parent;
        }
      }

      // sorted. previous parent should be the top-level layer
      res = (Layer) parentParent.getEditable();
    }
    return res;
  }

  /**
   * hey, where's the thing we're dealing with?
   * 
   * @return
   */
  public Editable getEditable()
  {
    return _editable;
  }

  public boolean equals(final Object arg0)
  {
    Editable targetEditable = null;
    boolean res = false;
    if (arg0 instanceof EditableWrapper)
    {
      final EditableWrapper pw = (EditableWrapper) arg0;
      targetEditable = pw.getEditable();
    }

    // right, have we found something to match?
    if (targetEditable != null)
    {
      res = (targetEditable == _editable);
    }

    return res;
  }

  @Override
  public int hashCode()
  {
    return _editable.hashCode();
  }

  public Object getEditableValue()
  {
    return _editable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
   */
  final public IPropertyDescriptor[] getPropertyDescriptors()
  {
    // right, does this object have dynamic descriptors?
    if (_editable.getInfo() instanceof Editable.DynamicDescriptors)
    {
      // yes - reset our list, we'll regenerate them
      _myDescriptors = null;
    }


    if (_myDescriptors == null)
    {
      final IPropertyDescriptor[] res = new IPropertyDescriptor[]
      { null };

      final Vector<IPropertyDescriptor> list = new Vector<IPropertyDescriptor>(
          0, 1);

      if (_editable instanceof IPropertySource)
      {
        // TODO: we should be able to use the Limpet property descriptors here
        // also check if it's capable of returning RCP property editors
        IPropertySource pSource = (IPropertySource) _editable;
        IPropertyDescriptor[] tmpDescriptors = pSource.getPropertyDescriptors();
        
        for (int i = 0; i < tmpDescriptors.length; i++)
        {
          IPropertyDescriptor thisS = tmpDescriptors[i];
          DebriefIProperty wrapped = new DebriefIProperty(thisS, _editable, null);
          list.add(wrapped);
          // ok, wrap it in a new class that makes an IPropertyDescriptor
          // look like an IDebriefProperty
        }
        
      }
      else
      {
        final Editable.EditorType editor = _editable.getInfo();
        if (editor != null)
        {
          final PropertyDescriptor[] properties = editor
              .getPropertyDescriptors();
          // _myDescriptors = new IPropertyDescriptor[properties.length];

          if (properties != null)
          {
            for (int i = 0; i < properties.length; i++)
            {
              final PropertyDescriptor thisProp = properties[i];

              // hmm, is it a legacy property?
              if (thisProp instanceof DeprecatedPropertyDescriptor)
              {
                // right, just give it a stiff ignoring, it's deprecated
              }
              else
              {
                // ok, wrap it, and add it to our list.
                final IPropertyDescriptor newProp = new DebriefProperty(
                    thisProp, (Editable) editor.getData(), null);
                list.add(newProp);
              }
            }
          }

          // hmm, are there any "supplemental" editors?
          final BeanInfo[] others = editor.getAdditionalBeanInfo();
          if (others != null)
          {
            // adding more editors
            for (int i = 0; i < others.length; i++)
            {
              final BeanInfo bn = others[i];
              if (bn instanceof MWC.GUI.Editable.EditorType)
              {
                final Editable.EditorType et = (Editable.EditorType) bn;
                final Editable obj = (Editable) et.getData();
                final PropertyDescriptor[] pds = et.getPropertyDescriptors();
                if (pds != null)
                {
                  for (int j = 0; j < pds.length; j++)
                  {
                    final PropertyDescriptor pd = pds[j];

                    // is this an 'expert' property which
                    // should not appear in here as an additional?
                    if (pd.isExpert())
                    {
                      // do nothing, we don't want to show this
                    }
                    else
                    {
                      // ok, add this editor
                      final IPropertyDescriptor newProp = new DebriefProperty(
                          pd, obj, null);

                      list.add(newProp);
                    }
                  }
                }
              }
            }
          }
        }

      }
      // hmm, did we find any
      if (list.size() > 0)
      {
        _myDescriptors = (IPropertyDescriptor[]) list.toArray(res);
      }
    }

    // just make sure we aren't returning a null...
    if (_myDescriptors == null)
      _myDescriptors = new IPropertyDescriptor[]
      {};

    return _myDescriptors;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
   */
  final public IPropertyDescriptor[] getGriddablePropertyDescriptors()
  {

    _myGridDescriptors = null;
    if (_myGridDescriptors == null)
    {
      final Vector<IPropertyDescriptor> list = new Vector<IPropertyDescriptor>(
          0, 1);
      final IPropertyDescriptor[] res = new IPropertyDescriptor[]
      { null };
      if (_editable != null)
      {
        // is it griddable
        if (_editable instanceof GriddableSeriesMarker)
        {
          final GriddableSeriesMarker series = (GriddableSeriesMarker) _editable;
          final Editable.EditorType editor = _editable.getInfo();
          final Editable sample = series.getSampleGriddable();
          // just check we managed to get some sample data
          if (sample == null)
            return _myGridDescriptors;

          final Griddable grid = (Griddable) sample.getInfo();

          final PropertyDescriptor[] properties = grid
              .getGriddablePropertyDescriptors();

          if (properties != null)
          {
            for (int i = 0; i < properties.length; i++)
            {
              final PropertyDescriptor thisProp = properties[i];

              // hmm, is it a legacy property?
              if (thisProp instanceof DeprecatedPropertyDescriptor)
              {
                // right, just give it a stiff ignoring, it's deprecated
              }
              else
              {
                // ok, wrap it, and add it to our list.
                final IPropertyDescriptor newProp = new DebriefProperty(
                    thisProp, (Editable) sample, null);
                list.add(newProp);
              }
            }
          }
          else
          {
            final NonBeanPropertyDescriptor[] nonBean = grid
                .getNonBeanGriddableDescriptors();
            if (nonBean != null)
            {
              for (int i = 0; i < nonBean.length; i++)
              {
                final NonBeanPropertyDescriptor nb = nonBean[i];
                final IPropertyDescriptor newP = new DebriefNonBeanProperty(nb,
                    null, _editable);
                list.add(newP);
              }
            }
          }

          // hmm, are there any "supplemental" editors?
          if (editor != null)
          {
            final BeanInfo[] others = editor.getAdditionalBeanInfo();
            if (others != null)
            {
              // adding more editors
              for (int i = 0; i < others.length; i++)
              {
                final BeanInfo bn = others[i];
                if (bn instanceof MWC.GUI.Editable.EditorType)
                {
                  final Editable.EditorType et = (Editable.EditorType) bn;
                  final Editable obj = (Editable) et.getData();
                  final PropertyDescriptor[] pds = et.getPropertyDescriptors();
                  if (pds != null)
                  {
                    for (int j = 0; j < pds.length; j++)
                    {
                      final PropertyDescriptor pd = pds[j];

                      // is this an 'expert' property which
                      // should not appear in here as an additional?
                      if (pd.isExpert())
                      {
                        // do nothing, we don't want to show this
                      }
                      else
                      {
                        // ok, add this editor
                        final IPropertyDescriptor newProp = new DebriefProperty(
                            pd, obj, null);

                        list.add(newProp);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      // hmm, did we find any
      if (list.size() > 0)
      {
        _myGridDescriptors = (IPropertyDescriptor[]) list.toArray(res);
      }

    }
    return _myGridDescriptors;
  }

  /**
   * using the supplied display name value, find our matching property
   * descriptor
   * 
   * @param id
   *          the string to look for
   * @return the matching property descriptor
   */
  final private IDebriefProperty getDescriptorFor(final String id)
  {

    IDebriefProperty res = null;
    // right, the id we're getting is the string display name.
    // pass through our descriptors to find the matching one
    for (int i = 0; i < _myDescriptors.length; i++)
    {
      final IPropertyDescriptor thisDescriptor = _myDescriptors[i];
      if (thisDescriptor.getDisplayName().equals(id))
      {
        res = (IDebriefProperty) thisDescriptor;
        break;
      }
    }
    return res;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang
   * .Object)
   */
  final public Object getPropertyValue(final Object id)
  {
    Object res = null;

    // convert the id back to a string
    final String thisName = (String) id;

    // ok. now find the matching descriptor
    final IDebriefProperty thisProp = getDescriptorFor(thisName);

    // get the value, if it worked
    res = thisProp.getValue();

    // done. for better or for worse..
    return res;
  }

  final public boolean isPropertySet(final Object id)
  {
    return true;
  }

  final public void resetPropertyValue(final Object id)
  {

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang
   * .Object, java.lang.Object)
   */
  final public void setPropertyValue(final Object id, final Object value)
  {

    // convert the id back to a string
    final String thisName = (String) id;

    // ok. now find the matching descriptor
    final IDebriefProperty thisProp = getDescriptorFor(thisName);

    // and find the existing value
    final Object oldVal = thisProp.getValue();

    // ok, create the action
    final PropertyChangeAction pca = new PropertyChangeAction(oldVal, value,
        thisProp, getEditable().getName(), getLayers(), getTopLevelLayer());

    // and sort it out with the history
    CorePlugin.run(pca);
  }

  final public Layers getLayers()
  {
    return _theLayers;
  }

  final public boolean hasChildren()
  {
    return ((_editable instanceof Layer) && (!(_editable instanceof Editable.DoNoInspectChildren)));
  }

  @SuppressWarnings("rawtypes")
  final protected static Class getPropertyClass(
      final PropertyDescriptor thisProp)
  {

    Class res = null;
    try
    {
      // find out the type of the editor
      final Method m = thisProp.getReadMethod();
      res = m.getReturnType();
    }
    catch (final Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }

    return res;
  }

  public EditableWrapper getParent()
  {
    return _parent;
  }

  /**
   * embedded class which stores a property change in an undoable operation
   * 
   * @author ian.mayo
   */
  final protected static class PropertyChangeAction extends AbstractOperation
  {
    private final Object _oldValue;

    private final Object _newValue;

    private final IDebriefProperty _property;

    private final Layers _wholeLayers;

    /**
     * the parent plottable for this item - the layer that we fire an update for
     * after a change
     */
    private final Layer _topLevelLayer;

    /**
     * setup the change details
     * 
     * @param oldValue
     *          old value (to undo to)
     * @param newValue
     *          new value
     * @param subject
     *          the item being edited
     * @param wholeLayers
     *          the complete set of layers to redraw
     * @param parentLayer
     *          the parent layer - the one to be updated following a change
     * @param wholeLayers
     *          the layers object we inform about the change
     */
    public PropertyChangeAction(final Object oldValue, final Object newValue,
        final IDebriefProperty subject, final String name,
        final Layers wholeLayers, final Layer topLevelLayer)
    {
      super(name + " " + subject.getDisplayName());

      if (CorePlugin.getUndoContext() != null)
      {
        this.addContext(CorePlugin.getUndoContext());
      }

      _oldValue = oldValue;
      _newValue = newValue;
      _property = subject;
      _wholeLayers = wholeLayers;
      _topLevelLayer = topLevelLayer;
    }

    public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // get the value, if it worked
      _property.setValue(_newValue);

      // ok, and tell any listeners that want to know
      // - the only listener I can think of is the Java3d properties viewer
      _property.getEditable().getInfo().fireChanged(_property.getEditable(),
          _property.getDisplayName(), _oldValue, _newValue);

      // fire the reformatted event for the parent layer
      // - note, we may not have the layers object if this editable isn't a plot
      // object
      // (it could be an xy plot)
      if (_wholeLayers != null)
      {
        // right, we can fire a change if we like. have a look
        final Annotation[] ann = _property.getAnnotationsForSetter();
        if ((ann != null) && (ann.length > 0))
        {
          for (int i = 0; i < ann.length; i++)
          {
            final Annotation thisA = ann[i];
            if (thisA.annotationType().equals(FireExtended.class))
            {
              _wholeLayers.fireExtended(null, _topLevelLayer);
            }
            else if (thisA.annotationType().equals(FireReformatted.class))
            {
              _wholeLayers.fireReformatted(_topLevelLayer);
            }
          }

        }
        else
          _wholeLayers.fireModified(_topLevelLayer);

      }

      return Status.OK_STATUS;
    }

    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      return execute(monitor, info);
    }

    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // get the value, if it worked
      _property.setValue(_oldValue);

      // ok, and tell any listeners that want to know...
      _property.getEditable().getInfo().fireChanged(_property.getValue(),
          _property.getDisplayName(), _newValue, _oldValue);

      // right, we can fire a change if we like. have a look
      final Annotation[] ann = _property.getAnnotationsForSetter();
      if ((ann != null) && (ann.length > 0))
      {
        for (int i = 0; i < ann.length; i++)
        {
          final Annotation thisA = ann[i];
          if (thisA.annotationType().equals(FireExtended.class))
          {
            _wholeLayers.fireExtended(null, _topLevelLayer);
          }
          else if (thisA.annotationType().equals(FireReformatted.class))
          {
            _wholeLayers.fireModified(_topLevelLayer);
          }
        }
      }
      else
        _wholeLayers.fireReformatted(_topLevelLayer);

      // and return the status
      return Status.OK_STATUS;
    }

  }

  public static class OrderedEditableWrapper extends EditableWrapper implements
      Comparable<OrderedEditableWrapper>
  {
    private final int _myIndex;

    public OrderedEditableWrapper(final Editable plottable,
        final EditableWrapper parent, final Layers theLayers, final int myIndex)
    {
      super(plottable, parent, theLayers);
      _myIndex = myIndex;
    }

    public int compareTo(final OrderedEditableWrapper o)
    {
      final OrderedEditableWrapper other = (OrderedEditableWrapper) o;
      final int hisIndex = other._myIndex;
      int res;

      if (_myIndex < hisIndex)
        res = -1;
      else if (_myIndex > hisIndex)
        res = 1;
      else
        res = 0;

      return res;
    }

  }
}