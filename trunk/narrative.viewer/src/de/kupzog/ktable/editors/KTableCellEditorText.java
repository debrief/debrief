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

/**
 * A simple cell editor that simply creates a text widget that allows the user
 * to type in one line of text.
 * <p>
 * This class is very similar to <code>KTableCellEditorText2</code> that
 * additionally allows the user to navigate within the text widget using
 * ARROW_LEFT and ARROW_RIGHT keys.
 * 
 * @see de.kupzog.ktable.editors.KTableCellEditorText2
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public class KTableCellEditorText extends KTableCellEditor {
	protected Text m_Text;

	private KeyAdapter keyListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			try {
				onKeyPressed(e);
			} catch (Exception ex) {
				// Do nothing
			}
		}
	};

	private TraverseListener travListener = new TraverseListener() {
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
		if (m_Text != null) {
			if (save)
				m_Model.setContentAt(m_Col, m_Row, m_Text.getText());
			m_Text.removeKeyListener(keyListener);
			m_Text.removeTraverseListener(travListener);
		}
		super.close(save);
		if (m_Text != null) {
			m_Text.dispose();
			m_Text = null;
		}
	}

	protected Control createControl() {
		m_Text = new Text(m_Table, SWT.NONE);
		m_Text.addKeyListener(keyListener);
		m_Text.addTraverseListener(travListener);
		return m_Text;
	}

	/*
	 * overridden from superclass
	 */
	// public void setBounds(Rectangle rect)
	// {
	// super.setBounds(new Rectangle(rect.x, rect.y+(rect.height - 15)/2+1,
	// rect.width, 15));
	// }
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