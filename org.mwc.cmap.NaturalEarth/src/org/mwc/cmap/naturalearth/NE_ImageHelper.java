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
package org.mwc.cmap.naturalearth;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider.ViewLabelImageHelper;
import org.mwc.cmap.naturalearth.wrapper.NELayer;

import MWC.GUI.Editable;

public class NE_ImageHelper implements ViewLabelImageHelper
{

	public ImageDescriptor getImageFor(final Editable editable)
	{
		ImageDescriptor res = null;

		if (editable instanceof NELayer)
			res = Activator.getImageDescriptor("icons/16/NaturalEarth.png");
		
		return res;
	}

}
