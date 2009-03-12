/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 
 Authors: Friederich Kupzog
 Lorenz Maierhofer
 fkmk@kupzog.de
 www.kupzog.de/fkmk
 */
package de.kupzog.ktable.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;

/**
 * A combobox cell editor.
 * 
 * @author Friederich Kupzog
 */
public class KTableCellEditorCombo extends KTableCellEditor {
	private CCombo m_Combo;
	private String m_Items[];
	private Cursor m_ArrowCursor = new Cursor(Display.getDefault(), SWT.CURSOR_ARROW);

	private KeyAdapter keyListener = new KeyAdapter() {
		@SuppressWarnings("synthetic-access")
		public void keyPressed(KeyEvent e) {
			try {
				onKeyPressed(e);
			} catch (Exception ex) {
				// Do nothing
			}
			e.doit = false;
		}
	};

	private TraverseListener travListener = new TraverseListener() {
		public void keyTraversed(TraverseEvent e) {
			onTraverse(e);
		}
	};

	public void open(KTable table, int row, int col, Rectangle rect) {
		super.open(table, row, col, rect);
		String content = m_Model.getContentAt(m_Col, m_Row).toString();
		m_Combo.setText(content);
		m_Combo.setSelection(new Point(0, content.length()));
	}

	public void close(boolean save) {
		if (save)
			m_Model.setContentAt(m_Col, m_Row, m_Combo.getText());
		m_Combo.removeKeyListener(keyListener);
		m_Combo.removeTraverseListener(travListener);
		super.close(save);
		m_Combo = null;
		m_ArrowCursor.dispose();
	}

	protected Control createControl() {
		m_Combo = new CCombo(m_Table, SWT.READ_ONLY);
		m_Combo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		if (m_Items != null)
			m_Combo.setItems(m_Items);

		m_Combo.addKeyListener(keyListener);
		m_Combo.addTraverseListener(travListener);

		m_Combo.setCursor(m_ArrowCursor);
		return m_Combo;
	}

	/**
	 * Overwrite the onTraverse method to ignore arrowup and arrowdown events so
	 * that they get interpreted by the editor control.
	 * <p>
	 * Comment that out if you want the up and down keys move the editor.<br>
	 * Hint by David Sciamma.
	 */
	protected void onTraverse(TraverseEvent e) {
		// set selection to the appropriate next element:
		switch (e.keyCode) {
		case SWT.ARROW_UP: // Go to previous item
		case SWT.ARROW_DOWN: // Go to next item
		{
			// Just don't treat the event
			break;
		}
		default: {
			super.onTraverse(e);
			break;
		}
		}
	}

	public void setBounds(Rectangle rect) {
		super.setBounds(new Rectangle(rect.x, rect.y + 1, rect.width, rect.height - 2));
	}

	public void setItems(String items[]) {
		m_Items = items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kupzog.ktable.KTableCellEditor#setContent(java.lang.String)
	 */
	public void setContent(Object content) {
		if (content instanceof Integer) {
			m_Combo.select(((Integer) content).intValue());
		} else if (content instanceof String) {
			setSelectionToClosestMatch((String) content);
		} else {
			setSelectionToClosestMatch(content.toString());
		}
	}

	private void setSelectionToClosestMatch(String content) {
		content = content.toLowerCase();

		String[] citems = m_Combo.getItems();
		String[] items = new String[citems.length];
		for (int i = 0; i < citems.length; i++)
			items[i] = citems[i].toLowerCase();

		for (int length = content.length(); length >= 0; length--) {
			String part = content.substring(0, length);
			for (int i = 0; i < items.length; i++)
				if (items[i].startsWith(part)) {
					m_Combo.select(i);
					return;
				}
		}
	}
}
