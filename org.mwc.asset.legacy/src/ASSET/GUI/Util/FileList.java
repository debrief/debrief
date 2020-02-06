
package ASSET.GUI.Util;

import java.awt.BorderLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.File;
import java.io.FilenameFilter;

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
import javax.swing.JList;
import javax.swing.JPanel;

public class FileList extends JPanel implements FilenameFilter, DragGestureListener, DragSourceListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	static private final String file_separator = System.getProperty("file.separator");
	private final JList _myList = new JList();

	// private DragSource dragSource = new DragSource();
	// private DragGestureRecognizer recogniser =
	// dragSource.createDefaultDragGestureRecognizer(_myList,
	// DnDConstants.ACTION_COPY_OR_MOVE, this);

	private final String _myDirectory;

	final private String _mySuffix;

	public FileList(final String directory, final String title, final String suffix) {
		// store the directory
		_myDirectory = directory;

		if (suffix != null)
			_mySuffix = suffix;
		else
			_mySuffix = "xml";

		// set the name of the tab
		setName(title);

		// initialise the form
		initForm();

		// and refresh the list
		refreshForm();
	}

	@Override
	public boolean accept(final File dir, final String name) {
		return name.toLowerCase().endsWith(_mySuffix);
	}

	@Override
	public void dragDropEnd(final DragSourceDropEvent dsde) {
	}

	@Override
	public void dragEnter(final DragSourceDragEvent dsde) {
	}

	@Override
	public void dragExit(final DragSourceEvent dse) {
	}

	@Override
	public void dragGestureRecognized(final DragGestureEvent dge) {
		String sel = getCurrentItem() + "." + _mySuffix;
		// append our file suffix
		sel = _myDirectory + file_separator + sel;

		dge.startDrag(DragSource.DefaultCopyDrop, new StringSelection(sel), this);
	}

	@Override
	public void dragOver(final DragSourceDragEvent dsde) {
	}

	@Override
	public void dropActionChanged(final DragSourceDragEvent dsde) {
	}

	private String getCurrentItem() {
		return (String) _myList.getSelectedValue();
	}

	private void initForm() {
		// create list object
		this.setLayout(new BorderLayout());
		this.add(_myList, BorderLayout.CENTER);
	}

	private void refreshForm() {
		// create the file
		final File _myDir = new File(_myDirectory);

		final String[] fList = _myDir.list(this);

		_myList.removeAll();

		final java.util.Vector<String> vec = new java.util.Vector<String>(0, 1);

		// check we've received data
		if (fList != null) {

			// step through, but removing suffix
			for (int i = 0; i < fList.length; i++) {
				String str = fList[i];
				str = str.substring(0, str.length() - 4);
				vec.addElement(str);
			}

			_myList.setListData(vec);
		}
	}

}
