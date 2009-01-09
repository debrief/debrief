package MWC.GUI.Properties.Swing;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SwingWorldPathPropertyEditor extends MWC.GUI.Properties.WorldPathPropertyEditor implements java.beans.PropertyChangeListener
{
  javax.swing.JLabel _myLabel;

  public SwingWorldPathPropertyEditor() {
  }


  public java.awt.Component getCustomEditor()
  {
    JButton editBtn =new JButton("Edit");
    editBtn.setToolTipText("Click to edit the points in the " + getMyType());
    editBtn.setMargin(new Insets(0,0,0,0));
    editBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        openEditor();
      }
    });


    if(_myLabel == null)
        _myLabel = new javax.swing.JLabel("blank");

    JPanel thePanel = new JPanel();
    thePanel.setLayout(new BorderLayout());
    thePanel.add("East", editBtn);
    thePanel.add("Center", _myLabel);

    return thePanel;
  }

  protected String getMyType()
  {
    return "Path";
  }

  protected void openEditor()
  {
    WorldPathDetailEditor wpd  = new WorldPathDetailEditor(super._myPath, super._myChart, super._thePanel, super._theParent, getMyType());
    wpd.addPropertyChangeListener(this);
    super._thePanel.add(wpd);
  }

  protected void resetData()
  {
    if(_myLabel == null)
        _myLabel = new javax.swing.JLabel("blank");

    _myLabel.setText(getMyType() + " with:" + _myPath.size() + " points");
  }

  /**
   * This method gets called when a bound property is changed.
   * @param evt A PropertyChangeEvent object describing the event source
   *   	and the property that has changed.
   */

  public void propertyChange(PropertyChangeEvent evt)
  {
    // update the label
    resetData();
  }

  public void doClose()
  {
  }

}