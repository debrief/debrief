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
public class SimpleEditablePropertyPanel extends JPanel implements PropertiesPanel
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
  public void addEditor(EditorType info, Layer parentLayer)
  {
    ToolbarOwner owner = null;
    ToolParent parent = null;
    
    /*ToolParent parent = parentLayer;
    if (parent instanceof ToolbarOwner)
    {
      owner = (ToolbarOwner) parent;
    }*/

    PropertiesDialog dialog = new PropertiesDialog(info, null,
        null, parent, owner);
    dialog.setSize(400, 500);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
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
    super.removeAll();
    
    if ( thePanel != null )
    {
      super.add(thePanel, BorderLayout.CENTER);
    }
    
    super.revalidate();
    super.repaint();
    return thePanel;
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
