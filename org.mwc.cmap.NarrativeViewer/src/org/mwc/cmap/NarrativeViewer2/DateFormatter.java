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
