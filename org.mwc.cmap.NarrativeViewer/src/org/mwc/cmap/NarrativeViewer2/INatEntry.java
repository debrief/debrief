package org.mwc.cmap.NarrativeViewer2;

import java.io.Serializable;

import org.eclipse.swt.graphics.Color;

public interface INatEntry extends Serializable
{
  public abstract Color getColor();

  public abstract String getLog();

  public abstract String getName();

  public abstract String getTime();

  public abstract String getType();

}