package ASSET.GUI.Editors.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.DecisionType;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;

public class WaterfallEditor extends MWC.GUI.Properties.Swing.SwingCustomEditor
  implements java.beans.PropertyChangeListener, DropTargetListener
{

  //////////////////////////////////////////////////////////////////////
  // GUI components
  //////////////////////////////////////////////////////////////////////

  private JList modelList = new JList();
  private BorderLayout mainBorder = new BorderLayout();
  private JPanel dirBtnHolder = new JPanel();
  private JButton upBtn = new JButton();
  private JButton downBtn = new JButton();
  private JButton deleteBtn = new JButton();

  //////////////////////////////////////////////////////////////////////
  // drag and drop components
  //////////////////////////////////////////////////////////////////////

  private DropTarget dropTarget = new DropTarget(modelList, DnDConstants.ACTION_COPY_OR_MOVE, this);


  private ASSET.Models.Decision.BehaviourList _myList;

  public WaterfallEditor()
  {

  }

  public boolean supportsCustomEditor()
  {
    return true;
  }

  public void setObject(final Object value)
  {
    setValue(value);
  }

  private void setValue(final Object value)
  {
    //
    if (value instanceof ASSET.Models.Decision.BehaviourList)
    {
      _myList = (ASSET.Models.Decision.BehaviourList) value;

      _myList.addListener(ASSET.Models.Decision.BehaviourList.UPDATED, this);

      updateForm();

      jbInit();
    }
  }


  private void jbInit()
  {
    this.setLayout(mainBorder);
    upBtn.setText("Up");
    upBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        upBtn_actionPerformed(e);
      }
    });
    downBtn.setText("Down");
    downBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        downBtn_actionPerformed(e);
      }
    });
    deleteBtn.setText("Delete");
    deleteBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        deleteBtn_actionPerformed(e);
      }
    });
    this.add(new JLabel(_myList.getBehaviourName() + " Decision Model"), BorderLayout.NORTH);
    this.add(dirBtnHolder, BorderLayout.EAST);
    dirBtnHolder.add(upBtn, null);
    dirBtnHolder.add(downBtn, null);
    dirBtnHolder.add(deleteBtn, null);
    dirBtnHolder.setLayout(new GridLayout(0, 1));

    final JPanel modHolder = new JPanel();
    modHolder.setLayout(new BorderLayout());
    modHolder.add(new JScrollPane(modelList), BorderLayout.CENTER);
    final JLabel highP = new javax.swing.JLabel("High Priority");
    highP.setHorizontalAlignment(JLabel.RIGHT);
    final JLabel lowP = new javax.swing.JLabel("Low Priority");
    lowP.setHorizontalAlignment(JLabel.RIGHT);
    modHolder.add(highP, BorderLayout.NORTH);
    modHolder.add(lowP, BorderLayout.SOUTH);

    this.add(modHolder, BorderLayout.CENTER);

    modelList.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(final java.awt.event.MouseEvent e)
      {
        if (e.getClickCount() == 2)
        {
          itemSelected();
        }
      }
    });
  }

  private void itemSelected()
  {
    final ASSET.Models.DecisionType sel = getCurrent();
    if (sel == null)
      return;

    if (sel.hasEditor())
    {
      super.getPanel().addEditor(sel.getInfo(), null);
    }
  }

  private void updateForm()
  {
    modelList.setListData(_myList.getModels());
  }

  private void selectThis(final ASSET.Models.DecisionType val)
  {
    modelList.setSelectedValue(val, true);
  }

  private ASSET.Models.DecisionType getCurrent()
  {
    return (ASSET.Models.DecisionType) modelList.getSelectedValue();
  }

  private void upBtn_actionPerformed(ActionEvent e)
  {
    final DecisionType cur = getCurrent();
    if (cur != null)
    {
      final int index = _myList.getModels().indexOf(cur);
      if (index != 0)
      {
        // remove it from it's existing location
        _myList.getModels().removeElement(cur);
        // and insert it one place up
        _myList.getModels().insertElementAt(cur, index - 1);
        // and update the form
        updateForm();

        // and select our current item
        selectThis(cur);

      }
    }
  }

  private void downBtn_actionPerformed(ActionEvent e)
  {
    final DecisionType cur = getCurrent();
    if (cur != null)
    {
      final int index = _myList.getModels().indexOf(cur);
      if (index != _myList.getModels().size() - 1)
      {
        // remove it from it's existing location
        _myList.getModels().removeElement(cur);
        // and insert it one place up
        _myList.getModels().insertElementAt(cur, index + 1);
        // and update the form
        updateForm();

        // and select our current item
        selectThis(cur);
      }
    }
  }

  private void deleteBtn_actionPerformed(ActionEvent e)
  {
    final DecisionType cur = getCurrent();

    // remove it
    _myList.getModels().remove(cur);

    // and update
    updateForm();
  }

  public void propertyChange(final java.beans.PropertyChangeEvent pe)
  {
    final String type = pe.getPropertyName();
    if (type == ASSET.Models.Decision.BehaviourList.UPDATED)
    {
      // highlight the active model
      final ASSET.Models.DecisionType dec = (ASSET.Models.DecisionType) pe.getNewValue();

      // and select it
      modelList.setSelectedValue(dec, true);

    }
  }

  public void drop(final DropTargetDropEvent dtde)
  {
    try
    {
      if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor))
      {
        final Transferable tr = dtde.getTransferable();
        dtde.acceptDrop(DnDConstants.ACTION_COPY);
        final String s = (String) tr.getTransferData(DataFlavor.stringFlavor);
        System.out.println("accepted:" + s);
        ASSET.Util.XML.ASSETReaderWriter.importThis(_myList, s, new java.io.FileInputStream(s));
        updateForm();
        dtde.dropComplete(true);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void dropActionChanged(DropTargetDragEvent dtde)
  {
  }

  public void dragEnter(DropTargetDragEvent dtde)
  {
  }

  public void dragExit(DropTargetEvent dte)
  {
  }

  public void dragOver(DropTargetDragEvent dtde)
  {
  }


}