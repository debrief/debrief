package org.mwc.cmap.NarrativeViewer2;

import org.eclipse.swt.graphics.Color;
import org.mwc.cmap.core.property_support.ColorHelper;

import MWC.TacticalData.NarrativeEntry;

public class NatEntryProxy implements INatEntry
{
  final DateFormatter dateFormatter;
  final NarrativeEntry entry;

  public NatEntryProxy(DateFormatter dateFormatter,NarrativeEntry entry)
  {
    this.dateFormatter = dateFormatter;
    this.entry = entry;
  }

  @Override
  public String getDate()
  {
    return entry.getDTGString();
  }

  @Override
  public String getTime()
  {
    return dateFormatter.get(entry.getDTG());
  }

  @Override
  public String getName()
  {
    return entry.getTrackName();
  }

  @Override
  public String getType()
  {
    return entry.getType();
  }

  @Override
  public String getLog()
  {
    return entry.getEntry();
  }

  @Override
  public String toString()
  {
    return entry.toString();
  }

  @Override
  public Color getColor()
  {
    // convert to SWT color
    return ColorHelper.getColor(entry.getColor());
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((entry == null) ? 0 : entry.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NatEntryProxy other = (NatEntryProxy) obj;
    if (entry == null)
    {
      if (other.entry != null)
        return false;
    }
    else if (!entry.equals(other.entry))
      return false;
    return true;
  }
  
  
}
