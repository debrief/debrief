
package ASSET.GUI.Editors;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

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

import ASSET.Models.Decision.TargetType;
import ASSET.Participants.Category;

public class TargetTypeEditorFrame extends javax.swing.JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	static public TargetType doEdit(final TargetType val) {
		final JFrame parent = new JFrame("scrap");
		// work with a copy of the original value
		TargetType res = new TargetType(val);
		final TargetTypeEditorFrame aw = new TargetTypeEditorFrame(res, parent);
		aw.setVisible(true);
		res = new TargetType(aw.getResult());
		aw.dispose();
		parent.dispose();
		return res;
	}

	/**
	 * ************************************************************ member variables
	 * *************************************************************
	 */
	private JList forceList;
	private JList envList;

	private JList typeList;

	TargetType _myType;

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	private TargetTypeEditorFrame(final TargetType theType, final Frame theFrame) {
		super(theFrame, true);

		_myType = theType;

		this.setName("Edit Target Types");
		this.setSize(360, 260);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		initForm();
	}

	void doClose() {
		setVisible(false);
	}

	/**
	 * return the current value of the field
	 */
	private TargetType getResult() {
		return _myType;
	}

	Object getValue() {
		final TargetType res = new TargetType();

		// are there any types in force list?
		if (forceList.getSelectedIndices().length > 0) {
			final Object[] list = forceList.getSelectedValues();
			for (int i = 0; i < list.length; i++) {
				final String thisVal = (String) list[i];
				res.addTargetType(ASSET.Participants.Category.checkForce(thisVal));
			}
		}

		// are there any types in env list?
		if (envList.getSelectedIndices().length > 0) {
			final Object[] list = envList.getSelectedValues();
			for (int i = 0; i < list.length; i++) {
				final String thisVal = (String) list[i];
				res.addTargetType(ASSET.Participants.Category.checkEnv(thisVal));
			}
		}

		// are there any types in env list?
		if (typeList.getSelectedIndices().length > 0) {
			final Object[] list = typeList.getSelectedValues();
			for (int i = 0; i < list.length; i++) {
				final String thisVal = (String) list[i];
				res.addTargetType(ASSET.Participants.Category.checkType(thisVal));
			}
		}

		return res;
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	private void initForm() {
		final JPanel holder = new JPanel();
		holder.setLayout(new java.awt.GridLayout(1, 0));

		final JLabel forceL = new JLabel("Force");
		final JLabel envL = new JLabel("Env");
		final JLabel typeL = new JLabel("Type");

		forceList = new JList(Category.getForces());
		forceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		forceList.setVisibleRowCount(3);
		envList = new JList(Category.getEnvironments());
		envList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		envList.setVisibleRowCount(3);
		typeList = new JList(Category.getTypes());
		typeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		typeList.setVisibleRowCount(3);

		final JPanel forceHolder = new JPanel();
		forceHolder.setLayout(new BorderLayout());
		forceHolder.add("North", forceL);
		forceHolder.add("Center", forceList);
		holder.add(forceHolder);

		final JPanel envHolder = new JPanel();
		envHolder.setLayout(new BorderLayout());
		envHolder.add("North", envL);
		envHolder.add("Center", envList);
		holder.add(envHolder);

		final JPanel typeHolder = new JPanel();
		typeHolder.setLayout(new BorderLayout());
		typeHolder.add("North", typeL);
		typeHolder.add("Center", typeList);
		holder.add(typeHolder);

		// setup the data, if we know our type
		if (_myType != null)
			resetData();

		this.getContentPane().setLayout(new BorderLayout());
		final JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				// update the object
				_myType = (TargetType) getValue();
				doClose();
			}
		});

		this.getContentPane().add("Center", holder);
		this.getContentPane().add("South", close);

	}

	private void resetData() {
		if (_myType == null)
			return;

		// get our categories
		final Collection<String> coll = _myType.getTargets();

		if (coll == null)
			return;

		final Iterator<String> it = coll.iterator();

		final Vector<String> forces = new Vector<String>(0, 1);
		final Vector<String> envs = new Vector<String>(0, 1);
		final Vector<String> types = new Vector<String>(0, 1);

		while (it.hasNext()) {
			final String thisCat = it.next();

			// which list is this in?
			if (ASSET.Participants.Category.getForces().contains(thisCat)) {
				forces.add(thisCat);
			} else if (ASSET.Participants.Category.getEnvironments().contains(thisCat)) {
				envs.add(thisCat);
			} else if (ASSET.Participants.Category.getTypes().contains(thisCat)) {
				types.add(thisCat);
			}

		}

		// now set the selected items in each list
		setThisList(envList, envs);
		setThisList(typeList, types);
		setThisList(forceList, forces);

	}

	private void setThisList(final JList list, final Vector<String> items) {
		final Vector<Integer> indices = new Vector<Integer>(0, 1);
		int[] results = { 1, 2, 3 };

		// step through the items in the list
		final ListModel lm = list.getModel();

		for (int i = 0; i < lm.getSize(); i++) {
			final String thisItem = (String) lm.getElementAt(i);

			// is this in our vector?
			final Iterator<String> it = items.iterator();
			while (it.hasNext()) {
				final String val = it.next();
				if (val.equals(thisItem))
					indices.add(new Integer(i));
			}
		}

		// now produce the list of indices
		results = new int[indices.size()];

		for (int j = 0; j < indices.size(); j++) {
			final Integer thisI = indices.elementAt(j);
			results[j] = thisI.intValue();
		}

		list.setSelectedIndices(results);

	}

	public void setValue(final Object p1) {
		if (p1 instanceof TargetType) {
			_myType = (TargetType) p1;

			if (envList != null)
				resetData();
		} else
			return;
	}

	public boolean supportsCustomEditor() {
		return true;
	}

	/***************************************************************
	 * main method, for testing
	 ***************************************************************/

}