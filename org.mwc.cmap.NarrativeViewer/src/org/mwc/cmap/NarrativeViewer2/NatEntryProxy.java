package org.mwc.cmap.NarrativeViewer2;

import org.eclipse.swt.graphics.Color;
import org.mwc.cmap.core.property_support.ColorHelper;

import MWC.TacticalData.NarrativeEntry;

public class NatEntryProxy implements INatEntry
{
  final NarrativeEntry entry;

  public NatEntryProxy(NarrativeEntry entry)
  {
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
    return entry.getDTGString();
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
}
