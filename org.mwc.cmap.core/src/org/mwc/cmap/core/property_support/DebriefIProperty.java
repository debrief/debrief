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

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import MWC.GUI.Editable;
import MWC.GUI.Editable.CategorisedPropertyDescriptor;
import MWC.GUI.Editable2;

public class DebriefIProperty implements IPropertyDescriptor, IDebriefProperty
{
  final IPropertyDescriptor _thisProp;

  final Editable _subject;

  EditorHelper _myHelper = null;

  static Vector<EditorHelper> _myHelperList;

  static Control _theControl;

  public DebriefIProperty(final IPropertyDescriptor prop,
      final Editable subject, final Control theControl)
  {
    _thisProp = prop;
    _subject = subject;
    _theControl = theControl;

    initialiseHelpers();

    _myHelper = findHelperFor(prop, subject);
  }

  @SuppressWarnings(
  {})
  private EditorHelper findHelperFor(final IPropertyDescriptor prop,
      final Editable subject)
  {
    return null;
    // EditorHelper res = null;
    //
    // // is there an explicit editor specified?
    // final Class specificEditor = prop...getPropertyEditorClass();
    //
    // // did we find one?
    // if (specificEditor != null)
    // {
    // Object theEditor = null;
    // try
    // {
    // theEditor = specificEditor.newInstance();
    // }
    // catch (final Exception e)
    // {
    // CorePlugin.logError(Status.ERROR, "whilst finding helper", e);
    // }
    //
    // if (theEditor instanceof java.beans.PropertyEditor)
    // {
    // final java.beans.PropertyEditor propEditor = (java.beans.PropertyEditor)
    // theEditor;
    // // ok. wrap it.
    // if (!(propEditor instanceof DoNotUseTagEditorInPropertiesView))
    // {
    // if (propEditor.getTags() != null)
    // {
    // // ok - do one of the combo-box editor types
    // final String[] theTags = propEditor.getTags();
    // res = new TagListHelper(theTags, propEditor);
    //
    // }
    // }
    // }
    // }
    //
    // if (res == null)
    // {
    //
    // // ok, find the type of object we're working with
    // final Class rawClass = EditableWrapper.getPropertyClass(_thisProp);
    //
    // for (final Iterator iter = _myHelperList.iterator(); iter.hasNext();)
    // {
    // final EditorHelper thisHelper = (EditorHelper) iter.next();
    // if (thisHelper.editsThis(rawClass))
    // {
    // res = thisHelper;
    // break;
    // }
    // }
    //
    // if (res == null)
    // {
    // // ok, log the error
    // final String msg = "editor not found for:"
    // + EditableWrapper.getPropertyClass(prop) + "("
    // + prop.getDisplayName() + ")";
    // System.out.println(msg);
    // }
    //
    // }
    // return res;
  }

  public static void addSupplementalHelpers(
      final Vector<EditorHelper> newHelpers)
  {
    // make sure our starter list is created
    initialiseHelpers();

    // now add the new ones
    for (final Iterator<EditorHelper> iter = newHelpers.iterator(); iter
        .hasNext();)
    {
      final EditorHelper thisHelper = (EditorHelper) iter.next();
      _myHelperList.add(thisHelper);
    }
  }

  private synchronized static void initialiseHelpers()
  {
    if (_myHelperList == null)
    {
      _myHelperList = new Vector<EditorHelper>(0, 1);
      _myHelperList.add(new ColorHelper(_theControl));
      _myHelperList.add(new BoundedIntegerHelper());
      _myHelperList
          .add(new BoundedIntegerHelper.SteppingBoundedIntegerHelper());
      _myHelperList.add(new EditorHelper(String.class)
      {
        public CellEditor getCellEditorFor(final Composite parent)
        {
          return new TextCellEditor(parent, SWT.MULTI | SWT.V_SCROLL);
        }
      });
      _myHelperList.add(new EditorHelper(Long.class)
      {

        public CellEditor getCellEditorFor(final Composite parent)
        {
          return new TextCellEditor(parent);
        }

        public Object translateToSWT(final Object value)
        {
          String res = " ";
          final Long val = (Long) value;
          if (val != null)
          {
            final int thisInt = val.intValue();
            res = "" + thisInt;
          }
          return res;
        }

        public Object translateFromSWT(final Object value)
        {
          final String val = (String) value;
          Long res = null;
          res = new Long(val);
          return res;
        }

      });
      _myHelperList.add(new EditorHelper(Integer.class)
      {

        public CellEditor getCellEditorFor(final Composite parent)
        {
          return new TextCellEditor(parent);
        }

        public Object translateToSWT(final Object value)
        {
          String res = " ";
          final Integer val = (Integer) value;
          if (val != null)
          {
            final int thisInt = val.intValue();
            res = "" + thisInt;
          }
          return res;
        }

        public Object translateFromSWT(final Object value)
        {
          final String val = (String) value;
          Integer res = null;
          res = new Integer(val);
          return res;
        }

      });
      _myHelperList.add(new BooleanHelper());
      _myHelperList.add(new FontHelper());
      _myHelperList.add(new DTGHelper());
      _myHelperList.add(new LatLongHelper());
      _myHelperList.add(new ArrayLengthHelper());
      _myHelperList.add(new DistanceWithUnitsHelper());
      _myHelperList.add(new WorldSpeedHelper());
      _myHelperList.add(new WorldAccelerationHelper());
      _myHelperList.add(new WorldDistanceHelper());
      _myHelperList.add(new WorldPathHelper());
      _myHelperList.add(new DurationHelper());
      _myHelperList.add(new DoubleHelper());
      _myHelperList.add(new FileNameHelper());

    }
  }

  public CellEditor createPropertyEditor(final Composite parent)
  {
    CellEditor res = null;
    if (_myHelper != null)
    {
      res = _myHelper.getCellEditorFor(parent);
    }
    if (res == null)
    {
      res = _thisProp.createPropertyEditor(parent);
    }
    return res;
  }

  public Control createEditor(final Composite parent)
  {
    Control res = null;
    if (_myHelper != null)
    {
      res = _myHelper.getEditorControlFor(parent, this);
    }
    return res;
  }

  public String getCategory()
  {
    String res = null;
    if (_thisProp instanceof Editable.CategorisedPropertyDescriptor)
    {
      final Editable.CategorisedPropertyDescriptor desc = (CategorisedPropertyDescriptor) _thisProp;
      res = desc.getCategory();
    }
    return res;
  }

  public String getDescription()
  {
    return _thisProp.getDescription();
  }

  public String getName()
  {
    return _thisProp.getDisplayName();
  }

  public String getDisplayName()
  {
    return _thisProp.getDisplayName();
  }

  public String[] getFilterFlags()
  {
    return null;
  }

  public Object getHelpContextIds()
  {
    return null;
  }

  public Object getId()
  {
    return _thisProp.getDisplayName();
  }

  public ILabelProvider getLabelProvider()
  {
    ILabelProvider res = null;
    if (_myHelper != null)
    {
      res = _myHelper.getLabelFor(_thisProp);
    }
    return res;
  }

  public EditorHelper getHelper()
  {
    return _myHelper;
  }

  public boolean isCompatibleWith(final IPropertyDescriptor anotherProperty)
  {
    // the name properties aren't compatible.
    boolean res = true;
    if (this.getDisplayName().equals("Name"))
      res = false;
    if (this.getDisplayName().equals("DateTimeGroup"))
      res = false;
    if (this.getDisplayName().equals("FixLocation"))
      res = false;
    return res;
  }

  public Object getRawValue()
  {
    Object res = null;

    if (_subject instanceof Editable2)
    {
      Editable2 e2 = (Editable2) _subject;
      res = e2.getValue(_thisProp.getId());
    }

    return res;
  }

  public Object getValue()
  {
    Object res = null;

    // get the raw value for this object
    res = getRawValue();

    if (_myHelper != null)
    {
      if (res != null)
        res = _myHelper.translateToSWT(res);
    }

    return res;
  }

  public Annotation[] getAnnotationsForSetter()
  {
    // // find out the type of the editor
    // final Method write = _thisProp..getWriteMethod();
    // return write.getAnnotations();
    return null;
  }

  public Editable getEditable()
  {
    return _subject;
  }

  public void setValue(final Object value)
  {
    Object theValue = value;
    if (_myHelper != null)
    {
      theValue = _myHelper.translateFromSWT(theValue);
    }

    if (_subject instanceof Editable2)
    {
      Editable2 e2 = (Editable2) _subject;
      e2.setValue(_thisProp.getId(), theValue);
    }
  }

}