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
package org.mwc.debrief.lite.properties;

import java.awt.BorderLayout;
import java.net.URL;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import MWC.GUI.Editable;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Properties.Swing.SwingPropertyEditor2;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI.ToolbarOwner;
import MWC.GUI.Undo.UndoBuffer;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class PropertiesDialog extends JDialog
{

  // in order to overwride the close method, so that the dialog is closed and disposed
  private static class PropsEditor extends SwingPropertyEditor2
  {

    public PropsEditor(final EditorType info, final SwingPropertiesPanel parent,
        final Layers theLayers, final ToolParent toolParent,
        final Layer parentLayer, final SwingPropertiesPanel propsPanel)
    {
      super(info, parent, theLayers, toolParent, parentLayer, propsPanel);
    }

    @Override
    public void close()
    {
      super.close();
      if (!dialogs.isEmpty())
      {
        final PropertiesDialog lastDialog = dialogs.pop();
        lastDialog.dispose();
      }
    }

  }

  private static Stack<PropertiesDialog> dialogs = new Stack<>();
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private final Editable _editableProperty;

  private final Layers _theLayers;
  /**
   * the toolparent we supply to any new panels
   */
  MWC.GUI.ToolParent _theToolParent;

  /**
   * the name of the session we read from, for when the toolbar floats
   */
  private final MyMetalToolBarUI.ToolbarOwner _owner;

  private final UndoBuffer _undoBuffer;

  public PropertiesDialog(final Editable editableProperty, final Layers layers,
      final UndoBuffer undoBuffer, final ToolParent toolParent,
      final ToolbarOwner owner)
  {
    _editableProperty = editableProperty;
    _theLayers = layers;
    _owner = owner;
    _theToolParent = toolParent;
    _undoBuffer = undoBuffer;
    dialogs.add(this);
    initForm();
    setModal(true);
    setTitle("Edit Properties");
    final URL iconURL = getClass().getClassLoader().getResource(
        "images/icon.png");
    if (iconURL != null)
    {
      final ImageIcon myIcon = new ImageIcon(iconURL);
      if (myIcon != null)
        setIconImage(myIcon.getImage());
    }
  }

  protected void initForm()
  {
    final SwingPropertiesPanel propsPanel = new SwingPropertiesPanel(_theLayers,
        _undoBuffer, _theToolParent, _owner);
    propsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    final PropsEditor ap = new PropsEditor(_editableProperty.getInfo(),
        propsPanel, _theLayers, _theToolParent, null, propsPanel);
    final JPanel thePanel = (JPanel) ap.getPanel();
    thePanel.setName(_editableProperty.getInfo().getDisplayName());
    // now, listen out for the name of the panel changing - we are removed as listener by the
    // SwingPropertyEditor
    // in it's close operation
    _editableProperty.getInfo().addPropertyChangeListener(propsPanel);
    propsPanel.add(thePanel);
    add(propsPanel, BorderLayout.CENTER);
  }

}