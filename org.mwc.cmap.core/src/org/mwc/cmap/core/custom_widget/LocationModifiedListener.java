package org.mwc.cmap.core.custom_widget;

import java.util.EventListener;

public interface LocationModifiedListener extends EventListener{
  public void modifyValue(LocationModifiedEvent e);
}