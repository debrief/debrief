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
package ui;

import info.limpet.rcp.editors.LimpetLabelProvider;

import org.debrief.limpet_integration.data.LimpetHolder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider.ViewLabelImageHelper;

import MWC.GUI.Editable;

public class LimpetImageHelper implements ViewLabelImageHelper
{

  private LimpetLabelProvider prov;

  public ImageDescriptor getImageFor(final Editable subject)
  {

    if (prov == null)
    {
      prov = new LimpetLabelProvider();
    }

    final ImageDescriptor res;
    if (subject instanceof LimpetHolder)
    {
      LimpetHolder lw = (LimpetHolder) subject;

      res = prov.getImageDescriptor(lw.getItem());
    }
    else
    {
      res = null;
    }

    return res;
  }

}
