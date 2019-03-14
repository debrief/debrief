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
package Debrief.Tools.Palette;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class ListLayersDialog extends JDialog
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String[] _layersList;
  private JList<String> _list;
  private String selectedItem;
  public ListLayersDialog(final String[] layersList)
  {
    _layersList = layersList;
    setTitle("Adding a new drawing feature");
    initForm();
  }
  public void initForm() {
    final JButton okButton = new JButton("OK");
    okButton.setEnabled(false);

    JLabel lblMessage = new JLabel();
    lblMessage.setText("Please select the destination layer for new feature");
    _list = new JList<>(_layersList);
    _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    _list.setBorder(new CompoundBorder(new EmptyBorder(4,4,4,4),new LineBorder(Color.BLACK,1)));
    _list.setVisibleRowCount(-1);
    _list.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        okButton.setEnabled(!_list.isSelectionEmpty());
      }});
    JScrollPane listScroller = new JScrollPane();
    listScroller.setViewportView(_list);
    listScroller.setPreferredSize(new Dimension(250, 80));
    add(lblMessage,BorderLayout.NORTH);
    add(listScroller,BorderLayout.CENTER);
    JPanel southPanel = new JPanel();
    southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    okButton.addActionListener(new ActionListener()
    {
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        selectedItem = _list.getSelectedValue();
        setVisible(false);
        dispose();
      }
    });
    southPanel.add(okButton);
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener()
    {
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        setVisible(false);
        dispose();
      }
    });
    southPanel.add(cancelButton);
    add(southPanel,BorderLayout.SOUTH);
  }
  
  

  
  public String getSelectedItem() {
    return selectedItem;
  }

}
