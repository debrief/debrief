/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 
 Author: Friederich Kupzog  
 fkmk@kupzog.de
 www.kupzog.de/fkmk
 */

package de.kupzog.ktable.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;

public class KTableCellEditorMultilineWrapText extends KTableCellEditor {
	private Text m_Text;

	private KeyAdapter keyListener = new KeyAdapter() {
		@SuppressWarnings("synthetic-access")
		public void keyPressed(KeyEvent e) {
			try {
				onKeyPressed(e);
			} catch (Exception ex) {
				// Do nothing
			}
		}
	};

	private TraverseListener travListener = new TraverseListener() {
		@SuppressWarnings("synthetic-access")
		public void keyTraversed(TraverseEvent e) {
			onTraverse(e);
		}
	};

	public void open(KTable table, int col, int row, Rectangle rect) {
		super.open(table, col, row, rect);
		m_Text.setText(m_Model.getContentAt(m_Col, m_Row).toString());
		m_Text.selectAll();
		m_Text.setVisible(true);
		m_Text.setFocus();
	}

	public void close(boolean save) {
		if (save)
			m_Model.setContentAt(m_Col, m_Row, m_Text.getText());
		m_Text.removeKeyListener(keyListener);
		m_Text.removeTraverseListener(travListener);
		m_Text = null;
		super.close(save);
	}

	protected Control createControl() {
		m_Text = new Text(m_Table, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		m_Text.addKeyListener(keyListener);
		m_Text.addTraverseListener(travListener);
		return m_Text;
	}

	/*
	 * overridden from superclass
	 */
	public void setBounds(Rectangle rect) {
		super.setBounds(new Rectangle(rect.x, rect.y, rect.width, rect.height));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kupzog.ktable.KTableCellEditor#setContent(java.lang.Object)
	 */
	public void setContent(Object content) {
		m_Text.setText(content.toString());
		m_Text.setSelection(content.toString().length());
	}

}