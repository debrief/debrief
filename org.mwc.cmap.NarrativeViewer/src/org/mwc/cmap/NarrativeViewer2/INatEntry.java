package org.mwc.cmap.NarrativeViewer2;

import org.eclipse.swt.graphics.Color;

public interface INatEntry
{

  public abstract String getDate();

  public abstract String getTime();

  public abstract String getName();

  public abstract String getType();

  public abstract String getLog();

  public abstract String toString();
  
  public abstract Color getColor();

}