/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.scripting.wrappers;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import MWC.GUI.Editable;

public class Outline
{
  private final IContentOutlinePage outline;

  public Outline(IContentOutlinePage _outline)
  {
    this.outline = _outline;
  }

  public Editable[] getSelection()
  {
    final ISelection iSelection = getISelection();

    ArrayList<Editable> editables = new ArrayList<>();

    if (iSelection != null && !iSelection.isEmpty()
        && iSelection instanceof IStructuredSelection)
    {
      IStructuredSelection structuredSelection = (IStructuredSelection) iSelection;
      final Iterator<?> i = structuredSelection.iterator();
      while (i.hasNext())
      {
        Object currentItem = i.next();
        if ( currentItem instanceof Editable )
        {
          editables.add((Editable)currentItem);
        }
      }
    }

    return editables.toArray(new Editable[0]);
  }

  private ISelection getISelection()
  {
    final ISelection[] answer = new ISelection[1];
    Display.getDefault().syncExec(new Runnable()
    {
      public void run()
      {
        answer[0] = outline.getSelection();
      }
    });
    return answer[0];
  }
  
  public void setSelection(Editable[] toSelect)
  {
    setISelection(new StructuredSelection(toSelect));
  }

  private void setISelection(StructuredSelection structuredSelection)
  {
    Display.getDefault().syncExec(new Runnable()
    {
      public void run()
      {
        outline.setSelection(structuredSelection);
      }
    });
  }
}
