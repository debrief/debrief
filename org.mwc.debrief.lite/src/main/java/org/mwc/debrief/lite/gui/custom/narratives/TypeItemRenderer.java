/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class TypeItemRenderer extends JCheckBox implements
    ListCellRenderer<String>
{

  /**
   *
   */
  private static final long serialVersionUID = -2797827350350391146L;

  @Override
  public Component getListCellRendererComponent(
      final JList<? extends String> list, final String value, final int index,
      final boolean isSelected, final boolean cellHasFocus)
  {
    setName(value);
    return this;
  }

}
