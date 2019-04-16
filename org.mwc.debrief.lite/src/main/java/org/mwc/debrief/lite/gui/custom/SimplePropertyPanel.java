package org.mwc.debrief.lite.gui.custom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import MWC.GUI.Editable.EditorType;
import MWC.GUI.Layer;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Undo.UndoBuffer;

public class SimplePropertyPanel extends JPanel implements PropertiesPanel
{

  public SimplePropertyPanel()
  {
    super();
    setLayout(new FlowLayout());
  }
  
  @Override
  public void addEditor(EditorType info, Layer parentLayer)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void addConstructor(EditorType info, Layer parentLayer)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public UndoBuffer getBuffer()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Component add(Component thePanel)
  {
    return super.add(thePanel);
  }

  @Override
  public void remove(Component theComponent)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void remove(Object theObject)
  {
    // TODO Auto-generated method stub

  }

}
