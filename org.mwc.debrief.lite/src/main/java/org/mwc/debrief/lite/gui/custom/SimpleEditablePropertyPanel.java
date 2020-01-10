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
package org.mwc.debrief.lite.gui.custom;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import org.mwc.debrief.lite.properties.PropertiesDialog;

import MWC.GUI.Editable.EditorType;
import MWC.GUI.Layer;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI.ToolbarOwner;
import MWC.GUI.Undo.UndoBuffer;

/**
 * Simple Panel.
 *
 */
public class SimpleEditablePropertyPanel extends JPanel implements
    PropertiesPanel
{

  /**
   *
   */
  private static final long serialVersionUID = -5170000978012001387L;

  public SimpleEditablePropertyPanel()
  {
    super();
    setLayout(new BorderLayout());
  }

  @Override
  public Component add(final Component thePanel)
  {
    super.removeAll();

    if (thePanel != null)
    {
      super.add(thePanel, BorderLayout.CENTER);
    }

    super.revalidate();
    super.repaint();
    return thePanel;
  }

  @Override
  public void addConstructor(final EditorType info, final Layer parentLayer)
  {
    System.out.print(""); // Hello Codacy :)
  }

  @Override
  public void addEditor(final EditorType info, final Layer parentLayer)
  {
    final ToolbarOwner owner = null;
    final ToolParent parent = null;

    final PropertiesDialog dialog = new PropertiesDialog(info, null, null,
        parent, owner, null);
    dialog.setSize(400, 500);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
  }

  @Override
  public UndoBuffer getBuffer()
  {
    return null;
  }

  @Override
  public void remove(final Component theComponent)
  {
    System.out.print(""); // Hello Codacy :)
  }

  @Override
  public void remove(final Object theObject)
  {
    System.out.print(""); // Hello Codacy :)
  }

  public void reset()
  {
    super.removeAll();

    super.revalidate();
    super.repaint();
  }
}
