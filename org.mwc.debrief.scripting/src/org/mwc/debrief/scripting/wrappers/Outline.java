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

/**
 * Outline class that exposes operations related with the Outline plugin.
 * 
 * @see org.eclipse.ui.views.contentoutline.IContentOutlinePage
 * @author Ian Mayo
 *
 */
public class Outline
{
  private final IContentOutlinePage outline;

  /**
   * Constructor that receives a reference of the outline.
   * 
   * @param _outline
   */
  public Outline(final IContentOutlinePage _outline)
  {
    this.outline = _outline;
  }

  /**
   * Return Current selection in the Outline.
   * 
   * @see org.eclipse.jface.viewers.ISelection
   * @return Currently selected object in the Outline
   */
  private ISelection getISelection()
  {
    final ISelection[] answer = new ISelection[1];
    Display.getDefault().syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        answer[0] = outline.getSelection();
      }
    });
    return answer[0];
  }

  /**
   * Return Current objects selected in the Outline.
   * 
   * @see MWC.GUI.Editable
   * @return Currently selected objects in the Outline <br />
   *         // @type MWC.GUI.Editable
   * 
   */
  public Editable[] getSelection()
  {
    final ISelection iSelection = getISelection();

    final ArrayList<Editable> editables = new ArrayList<>();

    if (iSelection != null && !iSelection.isEmpty()
        && iSelection instanceof IStructuredSelection)
    {
      final IStructuredSelection structuredSelection =
          (IStructuredSelection) iSelection;
      final Iterator<?> i = structuredSelection.iterator();
      while (i.hasNext())
      {
        final Object currentItem = i.next();
        if (currentItem instanceof Editable)
        {
          editables.add((Editable) currentItem);
        }
      }
    }

    return editables.toArray(new Editable[0]);
  }

  /**
   * Method that selects the object specified in the outline
   * 
   * @param structuredSelection
   *          Object to be selected in the outline
   */
  private void setISelection(final StructuredSelection structuredSelection)
  {
    Display.getDefault().syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        outline.setSelection(structuredSelection);
      }
    });
  }

  /**
   * Method that selected the objects specified in the outline.
   * 
   * @param toSelect
   *          Objects to be selected in the outline
   */
  public void setSelection(final Editable[] toSelect)
  {
    setISelection(new StructuredSelection(toSelect));
  }
}
