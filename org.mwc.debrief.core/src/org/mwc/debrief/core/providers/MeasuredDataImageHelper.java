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
package org.mwc.debrief.core.providers;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider.ViewLabelImageHelper;
import org.mwc.debrief.core.providers.measured_data.DatasetWrapper;
import org.mwc.debrief.core.providers.measured_data.FolderWrapper;

import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeries2Double;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDouble;
import MWC.GUI.Editable;

public class MeasuredDataImageHelper implements ViewLabelImageHelper
{

	public ImageDescriptor getImageFor(final Editable editable)
	{
		final ImageDescriptor res;
		
		if (editable instanceof FolderWrapper)
		{
			res = CorePlugin.getImageDescriptor("icons/16/folder.png");
		}
		else if (editable instanceof DatasetWrapper)
		{
		  // sort out the correct number of dimensions
		  DatasetWrapper sw = (DatasetWrapper) editable;
		  TimeSeriesCore ds = sw.getDataset();
		  if(ds instanceof TimeSeries2Double)
		  {
	      res = CorePlugin.getImageDescriptor("icons/16/dataset_2.png");
		  }
		  else if(ds instanceof TimeSeriesDouble)
      {
        res = CorePlugin.getImageDescriptor("icons/16/dataset_1.png");
      }
		  else
		  {
		    res = null;
		  }
		}
		else
		{
		  res = null;
		}
		return res;
	}

}
