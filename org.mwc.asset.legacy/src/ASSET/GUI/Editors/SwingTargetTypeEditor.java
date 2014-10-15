/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.GUI.Editors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SwingTargetTypeEditor extends TargetTypeEditor{

  private JLabel _holder;

  public Component getCustomEditor()
  {
    // create the holder
    _holder = new JLabel("---");

    // create the panel
    final JPanel _panel = new JPanel();
    _panel.setLayout(new BorderLayout());

    // create the button
    final JButton editBtn = new JButton("Edit");
    editBtn.setMargin(new Insets(0,0,0,0));
    editBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        _myType = TargetTypeEditorFrame.doEdit(_myType);
        resetData();
      }
    });

    _panel.add("West", _holder);
    _panel.add("Center", editBtn);

    resetData();
    return _panel;
  }

  protected void setText(String val)
  {
    if(_holder != null)
    {
      if(val == null)
      {
         val = "Blank";
      }
      else if(val.length() == 0)
      {
        val = "Blank";
      }

      _holder.setText(val);
    }
  }


}