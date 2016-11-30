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

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class BooleanHelper extends EditorHelper
{

  public BooleanHelper()
  {
    super(Boolean.class);
  }

  public CellEditor getCellEditorFor(final Composite parent)
  {
    final CellEditor res = new CustomCheckboxCellEditor(parent);
    return res;
  }

  @SuppressWarnings(
  {"rawtypes"})
  public boolean editsThis(final Class target)
  {
    return ((target == Boolean.class) || (target == boolean.class));
  }

  public Object translateToSWT(final Object value)
  {
    return value;
  }

  public Object translateFromSWT(final Object value)
  {
    return value;
  }

  public ILabelProvider getLabelFor(final Object currentValue)
  {
    final ILabelProvider label1 = new LabelProvider()
    {
      public String getText(final Object element)
      {
        String res = null;
        final Boolean val = (Boolean) element;
        String name = null;
        if (val.booleanValue())
        {
          name = "Yes";
        }
        else
        {
          name = "No";
        }
        res = name;
        return res;
        // return null;
      }

      public Image getImage(final Object element)
      {
        return null;
        //
        // Image res = null;
        // Boolean val = (Boolean) element;
        // String name = null;
        // if(val.booleanValue())
        // {
        // name = "checked.gif";
        // }
        // else
        // {
        // name = "unchecked.gif";
        // }
        // res = CorePlugin.getImageFromRegistry(name);
        // return res;
      }

    };
    return label1;
  }

  @Override
  public Control getEditorControlFor(final Composite parent,
      final IDebriefProperty property)
  {
    final Button myCheckbox = new Button(parent, SWT.CHECK);
    myCheckbox.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(final SelectionEvent e)
      {
        final Boolean val = new Boolean(myCheckbox.getSelection());
        property.setValue(val);
      }
    });
    return myCheckbox;
  }

  public class CustomCheckboxCellEditor extends CellEditor
  {

    boolean value = false;

    private Button check;
    private static final int defaultStyle = SWT.NONE;

    public CustomCheckboxCellEditor()
    {
      setStyle(defaultStyle);
    }

    public CustomCheckboxCellEditor(Composite parent)
    {
      this(parent, defaultStyle);
    }

    public CustomCheckboxCellEditor(Composite parent, int style)
    {
      super(parent, style);
    }

    protected Control createControl(Composite parent)
    {
      if (check == null || check.isDisposed())
      {
        check = new Button(parent, SWT.CHECK);
        check.setBackground(Display.getCurrent()
            .getSystemColor(SWT.COLOR_WHITE));
        check.setSelection(value);
        check.addSelectionListener(new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            value = !value;
            fireApplyEditorValue();
          }
        });

      }
      return check;
    }

    @Override
    public void activate()
    {
      super.activate();
    }

    protected Object doGetValue()
    {
      return value ? Boolean.TRUE : Boolean.FALSE;
    }

    /*
     * (non-Javadoc) Method declared on CellEditor.
     */
    protected void doSetFocus()
    {
      // Ignore
    }

    protected void doSetValue(Object value)
    {
      Assert.isTrue(value instanceof Boolean);
      this.value = ((Boolean) value).booleanValue();
    }

    public void activate(ColumnViewerEditorActivationEvent activationEvent)
    {
      if (activationEvent.eventType != ColumnViewerEditorActivationEvent.TRAVERSAL)
      {
        super.activate(activationEvent);
      }
    }
  }

}