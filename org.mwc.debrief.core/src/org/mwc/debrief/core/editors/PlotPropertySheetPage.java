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

public class PlotPropertySheetPage extends PropertySheetPage
{

	private PlotEditor _plotEditor;
	private Composite _base;
	private Label context;

	public PlotPropertySheetPage(PlotEditor plotEditor)
	{
		this._plotEditor = plotEditor;
		
	}
	@Override
	public void createControl(Composite parent)
	{
	  _base = new Composite(parent, SWT.NONE);
	  //clean layout
	  GridLayout layout = new GridLayout();
	  layout.marginTop = 0;
	  layout.marginBottom = 0;
	  layout.marginRight = 0;
	  layout.marginLeft = 0;
	  layout.verticalSpacing = 5;
	  layout.marginWidth=0;
	  layout.marginHeight=0;
    _base.setLayout(layout);
	  context = new Label(_base,SWT.NONE); 
	  FontData fontData = context.getFont().getFontData()[0];
	  final Font font = new Font(parent.getDisplay(), new FontData(fontData.getName(), fontData
	      .getHeight(), SWT.ITALIC));
	  context.setFont(font);
	  context.addDisposeListener(new DisposeListener()
    {
      
      @Override
      public void widgetDisposed(DisposeEvent e)
      {
        font.dispose();
        
      }
    });

	  context.setText("<selection>");
	 
	  super.createControl(_base);
	  context.setLayoutData(new GridData(GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL));
	  super.getControl().setLayoutData(new GridData(GridData.FILL_BOTH|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL));

	   
	}
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
	  super.selectionChanged(part, selection);
	  if(selection instanceof StructuredSelection)
	  {
	    StructuredSelection treeSelection =(StructuredSelection) selection;
	    
	    Object[] objs = treeSelection.toArray();
	    if(objs.length>0)
	    {
	      StringBuilder builder = new StringBuilder();
	      boolean addSep = false;
	      final int MAX_SIZE = 5;
	      for(int ctr = 0; ctr < objs.length && ctr < MAX_SIZE; ctr++)
	      {
	        Object object = objs[ctr];
	        if(object instanceof EditableWrapper)
	        {
	          if(addSep)
	          {
	            builder.append(", ");
	          }
	          EditableWrapper wrapper = (EditableWrapper) object;
	          builder.append(wrapper.getEditable().toString());
	          
	          addSep = true;
	        }
        }
	      
	      // see if we need to append ...
	      if(objs.length > MAX_SIZE)
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
	
	
	
	@Override
	public Control getControl()
	{
	  return _base;
	}
	

	@Override
	public void setActionBars(IActionBars actionBars)
	{
		super.setActionBars(actionBars);
		actionBars.setGlobalActionHandler(
				ActionFactory.UNDO.getId(), _plotEditor.getUndoAction());
		actionBars.setGlobalActionHandler(
				ActionFactory.REDO.getId(), _plotEditor.getRedoAction());
		actionBars.updateActionBars();
	}

}
