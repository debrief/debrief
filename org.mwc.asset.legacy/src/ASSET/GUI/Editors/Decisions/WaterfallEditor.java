
package ASSET.GUI.Editors.Decisions;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

import ASSET.Models.DecisionType;

public class WaterfallEditor extends MWC.GUI.Properties.Swing.SwingCustomEditor
		implements java.beans.PropertyChangeListener, DropTargetListener {

	//////////////////////////////////////////////////////////////////////
	// GUI components
	//////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final JList modelList = new JList();
	private final BorderLayout mainBorder = new BorderLayout();
	private final JPanel dirBtnHolder = new JPanel();
	private final JButton upBtn = new JButton();
	private final JButton downBtn = new JButton();
	private final JButton deleteBtn = new JButton();

	//////////////////////////////////////////////////////////////////////
	// drag and drop components
	//////////////////////////////////////////////////////////////////////

	private ASSET.Models.Decision.BehaviourList _myList;

	public WaterfallEditor() {

	}

	void deleteBtn_actionPerformed(final ActionEvent e) {
		final DecisionType cur = getCurrent();

		// remove it
		_myList.getModels().remove(cur);

		// and update
		updateForm();
	}

	void downBtn_actionPerformed(final ActionEvent e) {
		final DecisionType cur = getCurrent();
		if (cur != null) {
			final int index = _myList.getModels().indexOf(cur);
			if (index != _myList.getModels().size() - 1) {
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

	@Override
	public void dragEnter(final DropTargetDragEvent dtde) {
	}

	@Override
	public void dragExit(final DropTargetEvent dte) {
	}

	@Override
	public void dragOver(final DropTargetDragEvent dtde) {
	}

	@Override
	public void drop(final DropTargetDropEvent dtde) {
		try {
			if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				final Transferable tr = dtde.getTransferable();
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				final String s = (String) tr.getTransferData(DataFlavor.stringFlavor);
				System.out.println("accepted:" + s);
				ASSET.Util.XML.ASSETReaderWriter.importThis(_myList, s, new java.io.FileInputStream(s));
				updateForm();
				dtde.dropComplete(true);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dropActionChanged(final DropTargetDragEvent dtde) {
	}

	private ASSET.Models.DecisionType getCurrent() {
		return (ASSET.Models.DecisionType) modelList.getSelectedValue();
	}

	void itemSelected() {
		final ASSET.Models.DecisionType sel = getCurrent();
		if (sel == null)
			return;

		if (sel.hasEditor()) {
			super.getPanel().addEditor(sel.getInfo(), null);
		}
	}

	private void jbInit() {
		this.setLayout(mainBorder);
		upBtn.setText("Up");
		upBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				upBtn_actionPerformed(e);
			}
		});
		downBtn.setText("Down");
		downBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				downBtn_actionPerformed(e);
			}
		});
		deleteBtn.setText("Delete");
		deleteBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
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
		highP.setHorizontalAlignment(SwingConstants.RIGHT);
		final JLabel lowP = new javax.swing.JLabel("Low Priority");
		lowP.setHorizontalAlignment(SwingConstants.RIGHT);
		modHolder.add(highP, BorderLayout.NORTH);
		modHolder.add(lowP, BorderLayout.SOUTH);

		this.add(modHolder, BorderLayout.CENTER);

		modelList.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(final java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					itemSelected();
				}
			}
		});
	}

	@Override
	public void propertyChange(final java.beans.PropertyChangeEvent pe) {
		final String type = pe.getPropertyName();
		if (type == ASSET.Models.Decision.BehaviourList.UPDATED) {
			// highlight the active model
			final ASSET.Models.DecisionType dec = (ASSET.Models.DecisionType) pe.getNewValue();

			// and select it
			modelList.setSelectedValue(dec, true);

		}
	}

	private void selectThis(final ASSET.Models.DecisionType val) {
		modelList.setSelectedValue(val, true);
	}

	@Override
	public void setObject(final Object value) {
		setValue(value);
	}

	private void setValue(final Object value) {
		//
		if (value instanceof ASSET.Models.Decision.BehaviourList) {
			_myList = (ASSET.Models.Decision.BehaviourList) value;

			_myList.addListener(ASSET.Models.Decision.BehaviourList.UPDATED, this);

			updateForm();

			jbInit();
		}
	}

	public boolean supportsCustomEditor() {
		return true;
	}

	void upBtn_actionPerformed(final ActionEvent e) {
		final DecisionType cur = getCurrent();
		if (cur != null) {
			final int index = _myList.getModels().indexOf(cur);
			if (index != 0) {
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

	private void updateForm() {
		modelList.setListData(_myList.getModels());
	}

}