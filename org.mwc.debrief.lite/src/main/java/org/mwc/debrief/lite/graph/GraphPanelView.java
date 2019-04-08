package org.mwc.debrief.lite.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import MWC.GUI.LayerManager.Swing.SwingLayerManager;

public class GraphPanelView extends SwingLayerManager
{

  /**
   * 
   */
  private static final long serialVersionUID = 8529947841065977007L;

  @Override
  protected void initForm()
  {
    super.initForm();
    
    JPanel commandBar = new JPanel();
    commandBar.setBackground(Color.BLACK);
    commandBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
    
    final JButton editButton = createCommandButton("Edit",
        "icons/24/edit.png");
    editButton.setEnabled(false);
    commandBar.add(editButton);

    add(commandBar, BorderLayout.NORTH);
  }
  


  private JButton createCommandButton(String command, String image)
  {
    URL imageIcon = getClass().getClassLoader().getResource(image);
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon(imageIcon);
    }
    catch (Exception e)
    {
      System.err.println("Failed to find icon:" + image);
      e.printStackTrace();
    }
    final JButton button = new JButton(icon);
    button.setToolTipText(command);
    return button;
  }
  
}
