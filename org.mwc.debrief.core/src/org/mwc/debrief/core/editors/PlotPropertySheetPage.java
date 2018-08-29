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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.mwc.cmap.core.property_support.EditableWrapper;

import MWC.GUI.Editable;
import MWC.GUI.Editable.EditorType;

public class PlotPropertySheetPage extends PropertySheetPage
{

  private final PlotEditor _plotEditor;
  private Composite _base;
  private Label context;
  
  /** listen out for property changes on the selected
   * items
   */
  final private PropertyChangeListener _propListener;
  
  /** remember the current selection, so we can
   * remove listeners on a new selection
   */
  private Object[] _curSelection;

  public PlotPropertySheetPage(final PlotEditor plotEditor)
  {
    this._plotEditor = plotEditor;
    _propListener = new PropertyChangeListener()
    {
      @Override
      public void propertyChange(PropertyChangeEvent evt)
      {
        // ok, refresh.
        refresh();
      }
    };
  }

  @Override
  public void createControl(final Composite parent)
  {
    _base = new Composite(parent, SWT.NONE);
    // clean layout
    final GridLayout layout = new GridLayout();
    layout.marginTop = 0;
    layout.marginBottom = 0;
    layout.marginRight = 0;
    layout.marginLeft = 0;
    layout.verticalSpacing = 5;
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    _base.setLayout(layout);
    context = new Label(_base, SWT.NONE);
    final FontData fontData = context.getFont().getFontData()[0];
    final Font font =
        new Font(parent.getDisplay(), new FontData(fontData.getName(), fontData
            .getHeight(), SWT.ITALIC));
    context.setFont(font);
    context.addDisposeListener(new DisposeListener()
    {

      @Override
      public void widgetDisposed(final DisposeEvent e)
      {
        font.dispose();

      }
    });

    context.setText("<selection>");

    super.createControl(_base);
    context.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
        | GridData.GRAB_HORIZONTAL));
    super.getControl().setLayoutData(
        new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
            | GridData.GRAB_VERTICAL));

  }

  @Override
  public Control getControl()
  {
    return _base;
  }
  
  

  @Override
  public void dispose()
  {
    // de-register any existing listeners
    removePropertyChangeListenersFor(_curSelection, _propListener);
    //fix bug - 2998, reset the cached propertysheetpage in editor.
    _plotEditor.disposePlotPropertySheetPage();
    super.dispose();
  }

  @Override
  public void selectionChanged(final IWorkbenchPart part,
      final ISelection selection)
  {
    super.selectionChanged(part, selection);
    if (selection instanceof StructuredSelection)
    {
      final StructuredSelection treeSelection = (StructuredSelection) selection;

      final Object[] objs = treeSelection.toArray();
      if (objs.length > 0)
      {
        
        // do we have a previous selection?
        removePropertyChangeListenersFor(_curSelection, _propListener);
        
        // store the new selection
        _curSelection = objs;
        
        final StringBuilder builder = new StringBuilder();
        boolean addSep = false;
        final int MAX_SIZE = 5;
        for (int ctr = 0; ctr < objs.length && ctr < MAX_SIZE; ctr++)
        {
          final Object object = objs[ctr];
          if (object instanceof EditableWrapper)
          {
            if (addSep)
            {
              builder.append(", ");
            }
            final EditableWrapper wrapper = (EditableWrapper) object;
            builder.append(wrapper.getEditable().toString());
            
            addSep = true;
            
            // listen for any property changes
            final Editable editable = wrapper.getEditable();
            if (editable != null)
            {
              if (editable.hasEditor())
              {
                final EditorType info = editable.getInfo();
                info.addPropertyChangeListener(_propListener);
              }
            }
          }
        }

        // see if we need to append ...
        if (objs.length > MAX_SIZE)
        {
          builder.append(", ...");
        }

        context.setText(builder.toString());
        context.setToolTipText(builder.toString());
        return;
      }
    }
    context.setText("<pending>");
    context.setToolTipText("");
  }

  private static void removePropertyChangeListenersFor(Object[] curSelection, PropertyChangeListener propListener)
  {
    if(curSelection != null)
    {
      for(Object obj: curSelection)
      {
        // was it an editable?
        if (obj instanceof EditableWrapper)
        {
          EditableWrapper ed = (EditableWrapper) obj;
          // ok, de-register
          final Editable editable = ed.getEditable();
          if (editable.hasEditor())
          {
            editable.getInfo().removePropertyChangeListener(propListener);
          }
        }
      }
    }
  }

  @Override
  public void setActionBars(final IActionBars actionBars)
  {
    super.setActionBars(actionBars);
    actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), _plotEditor
        .getUndoAction());
    actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), _plotEditor
        .getRedoAction());
    actionBars.updateActionBars();
  }

}
