package org.mwc.cmap.NarrativeViewer2;

import java.io.Serializable;

import org.eclipse.swt.graphics.Color;

public interface INatEntry extends Serializable
{
  public abstract String getTime();

  public abstract String getName();

  public abstract String getType();

  public abstract String getLog();
  
  public abstract Color getColor();

}