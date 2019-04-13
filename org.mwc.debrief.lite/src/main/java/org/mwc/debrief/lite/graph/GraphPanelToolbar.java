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
package org.mwc.debrief.lite.graph;

import java.awt.FlowLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PlainPropertyEditor;

public class GraphPanelToolbar extends JPanel implements
    PlainPropertyEditor.EditorUsesToolParent
{

  /**
   * 
   */
  private static final long serialVersionUID = 8529947841065977007L;

  /**
   * Busy cursor
   */
  private ToolParent _theParent;

  public GraphPanelToolbar()
  {
    super(new FlowLayout(FlowLayout.LEFT));
    init();
  }

  protected void init()
  {

    final JComboBox<String> operationComboBox = new JComboBox<>(new String[]
    {"Depth", "Course", "Speed", "Range", "Bearing", "Bearing Rate Calculation",
        "Rel Brg", "ATB"});
    operationComboBox.setSize(50, 20);

    final JComboBox<String> selectTrackComboBox = new JComboBox<>(new String[]
    {"Boat 1", "Boat 2", "Boat 3", "Boat 1", "Boat 2", "Boat 3"});
    // editButton.setEnabled(false);
    
    final JButton fixToWindowsButton = createCommandButton("Scale the graph to show all data", "icons/16/fit_to_win.png");
    final JButton viewGridButton = createCommandButton("Switch axes", "icons/16/swap_axis.png");
    final JButton viewTimeButton = createCommandButton("Show symbols", "icons/16/open.png");
    final JButton hideCrosshair = createCommandButton("Hide the crosshair from the graph (for printing)", "icons/16/fix.png");
    final JButton expandButton = createCommandButton("Expand Period covered in sync with scenario time", "icons/16/clock.png");
    final JButton wmfButton = createCommandButton("Produce a WMF file of the graph", "icons/16/ex_2word_256_1.png");
    final JButton placeBitmapButton = createCommandButton("Place a bitmap image of the graph on the clipboard", "icons/16/copy_to_clipboard.png");
    final JButton copyGraph = createCommandButton("Copies the graph as a text matrix to the clipboard", "icons/16/export.png");
    final JButton propertiesButton = createCommandButton("Change editable properties for this chart", "icons/16/properties.png");
    final JToggleButton autosyncButton = createJToggleButton("Auto-sync with calculated track data", "icons/16/direction.png");

    add(operationComboBox);
    add(selectTrackComboBox);
    add(fixToWindowsButton);
    add(viewGridButton);
    add(viewTimeButton);
    add(hideCrosshair);
    add(expandButton);
    add(wmfButton);
    add(placeBitmapButton);
    add(copyGraph);
    add(propertiesButton);
    add(autosyncButton);
  }

  private JToggleButton createJToggleButton(String command, String image)
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
    final JToggleButton button = new JToggleButton(icon);
    button.setToolTipText(command);
    return button;
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

  @Override
  public void setParent(ToolParent theParent)
  {
    _theParent = theParent;
  }

}
