/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.kupzog.ktable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;

/**
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public class KTableActionHandler {

	static final char TAB = '\t';
	static final String PlatformLineDelimiter = System.getProperty("line.separator");

	public KTableCopyAction m_CopyAction;
	public KTableCopyAllAction m_CopyAllAction;
	public KTableCutAction m_CutAction;
	public KTablePasteAction m_PasteAction;
	public KTableSelectAllAction m_SelectAllAction;

	protected KTable m_table;
	protected MenuManager m_contextMenuManager;

	/**
	 * 
	 */
	public KTableActionHandler(KTable table) {
		m_table = table;
		createActions();
		registerActionUpdater();

		// add actions to context menu:
		m_contextMenuManager = new MenuManager("#PopupMenu");
		m_contextMenuManager.setRemoveAllWhenShown(true);
		m_contextMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = m_contextMenuManager.createContextMenu(m_table);
		m_table.setMenu(menu);
	}

	/**
	 * @return Returns the menu manager used to build the context menu of the
	 *         table.
	 *         <p>
	 *         The purpose for this is normally the registering of context menus
	 *         in the workbench.
	 * @see org.eclipse.ui.IWorkbenchPartSite#registerContextMenu(org.eclipse.jface.action.MenuManager,
	 *      org.eclipse.jface.viewers.ISelectionProvider)
	 */
	public MenuManager getMenuManager() {
		return m_contextMenuManager;
	}

	protected void createActions() {
		m_CopyAction = new KTableCopyAction();
		m_CopyAllAction = new KTableCopyAllAction();
		m_PasteAction = new KTablePasteAction();
		m_CutAction = new KTableCutAction();
		m_SelectAllAction = new KTableSelectAllAction();
	}

	protected void fillContextMenu(IMenuManager menumanager) {
		menumanager.add(m_CopyAction);
		menumanager.add(m_CutAction);
		menumanager.add(m_PasteAction);
		menumanager.add(new Separator());
		menumanager.add(m_CopyAllAction);
		menumanager.add(m_SelectAllAction);
		menumanager.add(new Separator());
		// Other plug-ins can contribute their actions here
		menumanager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * Registers the cut, copy, paste and select_all actions for global use at
	 * the IActionBar given.
	 * <p>
	 * Currently does not set up the UNDO and REDO actions because they will be
	 * implemented in another way.
	 * 
	 * @param actionBar
	 *            The IActionBars that allows global action registration.
	 *            Normally you can get that with getViewerSite().getActionBars()
	 *            or getEditorSite().getActionBars().
	 */
	public void registerGlobalActions(IActionBars actionBar) {
		actionBar.setGlobalActionHandler(ActionFactory.CUT.getId(), this.m_CutAction);
		actionBar.setGlobalActionHandler(ActionFactory.COPY.getId(), this.m_CopyAction);
		actionBar.setGlobalActionHandler(ActionFactory.PASTE.getId(), this.m_PasteAction);
		actionBar.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), this.m_SelectAllAction);
		actionBar.updateActionBars();
	}

	protected class KTableCopyAction extends Action {
		protected KTableCopyAction() {
			setId("KTableCopyActionHandler");//$NON-NLS-1$
			setEnabled(false);
			setText("Kopieren");
		}

		public void run() {
			if (m_table != null && !m_table.isDisposed()) {
				setClipboardContent(m_table.getCellSelection());
			}
		}

		public void updateEnabledState() {
			if (m_table != null && !m_table.isDisposed()) {
				Point[] selection = m_table.getCellSelection();
				setEnabled(selection != null && selection.length > 0);
			} else
				setEnabled(false);
		}
	}

	protected class KTableCopyAllAction extends Action {
		protected KTableCopyAllAction() {
			setId("KTableCopyAllActionHandler");//$NON-NLS-1$
			setEnabled(false);
			setText("Ganze Tabelle kopieren");
		}

		public void run() {
			if (m_table != null && !m_table.isDisposed()) {
				setClipboardContent(getAllTableCells());
			}
		}

		public void updateEnabledState() {
			if (m_table != null && !m_table.isDisposed()) {
				setEnabled(true);
			} else
				setEnabled(false);
		}

		private Point[] getAllTableCells() {
			KTableModel model = m_table.getModel();
			if (model == null)
				return new Point[] {};
			Vector<Point> cells = new Vector<Point>(model.getColumnCount() * model.getRowCount());
			for (int row = 0; row < model.getRowCount(); row++) {
				for (int col = 0; col < model.getColumnCount(); col++) {
					Point valid = model.belongsToCell(col, row);
					if (valid.y == row && valid.x == col)
						cells.add(valid);
				}
			}
			return cells.toArray(new Point[] {});
		}
	}

	protected class KTableCutAction extends Action {
		protected KTableCutAction() {
			setId("KTableCutActionHandler");//$NON-NLS-1$
			setEnabled(false);
			setText("Ausschneiden");
		}

		public void run() {
			if (m_table != null && !m_table.isDisposed()) {
				Point[] selection = m_table.getCellSelection();
				setClipboardContent(selection);
				removeContentAt(selection);
			}
		}

		public void updateEnabledState() {
			if (m_table != null && !m_table.isDisposed()) {
				Point[] selection = m_table.getCellSelection();
				setEnabled(selection != null && selection.length > 0);
			} else
				setEnabled(false);
		}

		protected void removeContentAt(Point[] selection) {
			KTableModel model = m_table.getModel();
			if (model == null)
				return;
			boolean updateSeperateCells = selection.length > 4 ? false : true;
			try {
				if (!updateSeperateCells)
					m_table.setRedraw(false);
				for (int i = 0; i < selection.length; i++) {
					model.setContentAt(selection[i].x, selection[i].y, "");
					if (updateSeperateCells)
						m_table.updateCell(selection[i].x, selection[i].y);
				}
			} finally {
				if (!updateSeperateCells)
					m_table.setRedraw(true);
			}
		}
	}

	protected class KTableSelectAllAction extends Action {
		protected KTableSelectAllAction() {
			setId("KTableSelectAllActionHandler");//$NON-NLS-1$
			setEnabled(false);
			setText("Alles Markieren");
		}

		public void run() {
			if (m_table != null && !m_table.isDisposed()) {
				KTableModel model = m_table.getModel();
				if (model != null)
					selectAll(model);
			}
		}

		public void updateEnabledState() {
			if (m_table != null && !m_table.isDisposed() && m_table.isMultiSelectMode()) {
				setEnabled(true);
			} else
				setEnabled(false);
		}

		protected void selectAll(KTableModel model) {
			Vector<Point> sel = new Vector<Point>();
			for (int row = model.getFixedHeaderRowCount(); row < model.getRowCount(); row++)
				for (int col = model.getFixedHeaderColumnCount(); col < model.getColumnCount(); col++) {
					Point cell = model.belongsToCell(col, row);
					if (cell.x == col && cell.y == row)
						sel.add(cell);
				}
			try {
				m_table.setRedraw(false);
				m_table.setSelection(new Point[] {}, false);
				m_table.setSelection(sel.toArray(new Point[] {}), false);
			} finally {
				m_table.setRedraw(true);
			}
		}
	}

	protected class KTablePasteAction extends Action {
		protected KTablePasteAction() {
			setId("KTablePasteActionHandler");//$NON-NLS-1$
			setEnabled(false);
			setText("Einfügen");
		}

		public void run() {
			if (m_table != null && !m_table.isDisposed()) {
				pasteToSelection(getTextFromClipboard(), m_table.getCellSelection());
			}
		}

		protected String getTextFromClipboard() {
			Clipboard clipboard = new Clipboard(m_table.getDisplay());
			try {
				return clipboard.getContents(TextTransfer.getInstance()).toString();
			} catch (Exception ex) {
				return "";
			} finally {
				clipboard.dispose();
			}
		}

		protected void pasteToSelection(String text, Point[] selection) {
			if (selection == null || selection.length == 0)
				return;
			KTableModel model = m_table.getModel();
			if (model == null)
				return;

			try {
				m_table.setRedraw(false);
				m_table.setSelection(new Point[] {}, false);
				Vector<Point> sel = new Vector<Point>();

				String[][] cellTexts = parseCellTexts(text);
				for (int row = 0; row < cellTexts.length; row++)
					for (int col = 0; col < cellTexts[row].length; col++) {
						model.setContentAt(col + selection[0].x, row + selection[0].y, cellTexts[row][col]);
						sel.add(new Point(col + selection[0].x, row + selection[0].y));
					}
				m_table.setSelection(sel.toArray(new Point[] {}), false);
			} finally {
				m_table.setRedraw(true);
			}
		}

		protected String[][] parseCellTexts(String text) {
			if (!m_table.isMultiSelectMode()) {
				return new String[][] { { text } };
			} else {
				String[] lines = text.split(PlatformLineDelimiter);
				String[][] cellText = new String[lines.length][];
				for (int line = 0; line < lines.length; line++)
					cellText[line] = lines[line].split(TAB + "");
				return cellText;
			}
		}

		public void updateEnabledState() {
			if (m_table != null && !m_table.isDisposed()) {
				Point[] selection = m_table.getCellSelection();
				if (selection == null)
					setEnabled(false);
				else if (selection.length > 1) // &&
					// !m_table.isMultiSelectMode())
					setEnabled(false);
				else
					setEnabled(true);
			} else
				setEnabled(false);
		}
	}

	/**
	 * Copies the specified text range to the clipboard. The table will be
	 * placed in the clipboard in plain text format and RTF format.
	 * 
	 * @param selection
	 *            The list of cell indices thats content should be set to the
	 *            clipboard.
	 * 
	 * @exception SWTError,
	 *                see Clipboard.setContents
	 * @see org.eclipse.swt.dnd.Clipboard.setContents
	 */
	protected void setClipboardContent(Point[] selection) throws SWTError {
		// RTFTransfer rtfTransfer = RTFTransfer.getInstance();
		TextTransfer plainTextTransfer = TextTransfer.getInstance();
		HTMLTransfer htmlTransfer = HTMLTransfer.getInstance();

		// String rtfText = getRTFForSelection(selection);
		String plainText = getTextForSelection(selection);
		String htmlText = getHTMLForSelection(selection);

		Clipboard clipboard = new Clipboard(m_table.getDisplay());
		try {
			clipboard.setContents(new String[] { plainText, htmlText }, // rtfText
					new Transfer[] { plainTextTransfer, htmlTransfer }); // rtfTransfer
		} catch (SWTError error) {
			// Copy to clipboard failed. This happens when another application
			// is accessing the clipboard while we copy. Ignore the error.
			// Rethrow all other errors.
			if (error.code != DND.ERROR_CANNOT_SET_CLIPBOARD) {
				throw error;
			}
		} finally {
			clipboard.dispose();
		}
	}

	private Point[] findTableDimensions(Point[] selection) {
		Point topLeft = new Point(-1, -1);
		Point bottomRight = new Point(-1, -1);

		for (int i = 0; i < selection.length; i++) {
			Point cell = selection[i];
			if (topLeft.x < 0)
				topLeft.x = cell.x;
			else if (topLeft.x > cell.x)
				topLeft.x = cell.x;
			if (bottomRight.x < 0)
				bottomRight.x = cell.x;
			else if (bottomRight.x < cell.x)
				bottomRight.x = cell.x;

			if (topLeft.y < 0)
				topLeft.y = cell.y;
			else if (topLeft.y > cell.y)
				topLeft.y = cell.y;
			if (bottomRight.y < 0)
				bottomRight.y = cell.y;
			else if (bottomRight.y < cell.y)
				bottomRight.y = cell.y;
		}
		return new Point[] { topLeft, bottomRight };
	}

	private Point findCellSpanning(int col, int row, KTableModel model) {
		Point spanning = new Point(1, 1);
		Point cell = new Point(col, row);
		while (model.belongsToCell(col + spanning.x, row).equals(cell))
			spanning.x++;

		while (model.belongsToCell(col, row + spanning.y).equals(cell))
			spanning.y++;

		return spanning;
	}

	protected String getHTMLForSelection(Point[] selection) {
		StringBuffer html = new StringBuffer();
		sortSelectedCells(selection);

		Point[] dimensions = findTableDimensions(selection);
		Point topLeft = dimensions[0];
		Point bottomRight = dimensions[1];

		KTableModel model = m_table.getModel();
		if (model == null)
			return "";
		// add header:
		html.append("Version:1.0\n");
		html.append("StartHTML:0000000000\n");
		html.append("EndHTML:0000000000\n");
		html.append("StartFragment:0000000000\n");
		html.append("EndFragment:0000000000\n");
		html.append("<html><body><table>");

		Point nextValidCell = selection[0];
		int selCounter = 1;
		for (int row = topLeft.y; row <= bottomRight.y; row++) {
			html.append("<tr>");
			for (int col = topLeft.x; col <= bottomRight.x; col++) {
				// may skip the cell when it is spanned by another one.
				if (model.belongsToCell(col, row).equals(new Point(col, row))) {

					if (nextValidCell.x == col && nextValidCell.y == row) {
						html.append("<td");
						Point spanning = findCellSpanning(col, row, model);
						if (spanning.x > 1)
							html.append(" colspan=\"" + spanning.x + "\"");
						if (spanning.y > 1)
							html.append(" rowspan=\"" + spanning.y + "\"");
						html.append(">");

						Object content = model.getContentAt(col, row);
						html.append(maskHtmlChars(content.toString()));
						if (selCounter < selection.length) {
							nextValidCell = selection[selCounter];
							selCounter++;
						}
					} else
						html.append("<td>");

					html.append("</td>");
				}
			}
			html.append("</tr>");
		}
		html.append("</table></body></html>");

		return html.toString();
	}

	private String maskHtmlChars(String text) {
		text = text.replaceAll("&", "&amp;");
		text = text.replaceAll("ä", "&auml;");
		text = text.replaceAll("Ä", "&Auml;");
		text = text.replaceAll("ö", "&ouml;");
		text = text.replaceAll("Ö", "&Ouml;");
		text = text.replaceAll("ü", "&uuml;");
		text = text.replaceAll("Ü", "&Uuml;");
		text = text.replaceAll("ß", "&szlig;");
		text = text.replaceAll("\"", "&quot;");
		text = text.replaceAll("<", "&lt");
		text = text.replaceAll(">", "&gt");
		text = text.replaceAll("€", "&euro;");
		return text;
	}

	protected String getTextForSelection(Point[] selection) {
		StringBuffer text = new StringBuffer();
		Point topLeft = sortSelectedCells(selection);
		KTableModel model = m_table.getModel();
		if (model == null)
			return "";

		int currentCol = topLeft.x;
		for (int i = 0; i < selection.length; i++) {
			for (; currentCol < selection[i].x; currentCol++)
				text.append(TAB);

			Object content = model.getContentAt(selection[i].x, selection[i].y);
			text.append(content.toString());

			if (i + 1 < selection.length) {
				for (int row = selection[i].y; row < selection[i + 1].y; row++)
					text.append(PlatformLineDelimiter);
				if (selection[i].y != selection[i + 1].y)
					currentCol = topLeft.x;
			}
		}
		return text.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Point sortSelectedCells(Point[] selection) {
		Arrays.sort(selection, new Comparator() {

			public int compare(Object o1, Object o2) {
				Point p1 = (Point) o1;
				Point p2 = (Point) o2;
				if (p1.y < p2.y)
					return -1;
				if (p1.y > p2.y)
					return +1;
				if (p1.x < p2.x)
					return -1;
				if (p1.x > p2.x)
					return +1;
				return 0;
			}

		});

		int minCol = selection[0].x;
		for (int i = 1; i < selection.length; i++)
			if (selection[i].x < minCol)
				minCol = selection[i].x;
		return new Point(minCol, selection[0].y);
	}

	protected String getRTFForSelection(Point[] selection) {
		return getTextForSelection(selection);
	}

	protected void registerActionUpdater() {
		m_table.addCellSelectionListener(new KTableCellSelectionListener() {

			public void cellSelected(int col, int row, int statemask) {
				updateActions();
			}

			public void fixedCellSelected(int col, int row, int statemask) {
				updateActions();
			}
		});
	}

	protected void updateActions() {
		m_CopyAction.updateEnabledState();
		m_CopyAllAction.updateEnabledState();
		m_CutAction.updateEnabledState();
		m_PasteAction.updateEnabledState();
		m_SelectAllAction.updateEnabledState();
	}
}
