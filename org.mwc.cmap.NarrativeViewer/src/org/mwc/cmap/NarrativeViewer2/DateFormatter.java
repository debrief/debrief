/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package org.mwc.cmap.NarrativeViewer2;

import java.util.WeakHashMap;

import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;

import MWC.GenericData.HiResDate;

public class DateFormatter
{

  static final TimeFormatter DEFAULT_TIME = new TimeFormatter()
  {
    @Override
    public String format(final HiResDate time)
    {
      return time.toString();
    }
  };

  private TimeFormatter formatter = DEFAULT_TIME;

  private final WeakHashMap<Object, String> formattedDateCache =
      new WeakHashMap<Object, String>();

  public void clearCache()
  {
    formattedDateCache.clear();
  }

  public String get(final HiResDate dtg)
  {

    String format = formattedDateCache.get(dtg);

    if (format == null)
    {
      format = formatter.format(dtg);
      formattedDateCache.put(dtg, format);
    }

    return format;

  }

  public void setFormatter(final TimeFormatter formatter)
  {
    this.formatter = formatter;
    clearCache();
  }

}
