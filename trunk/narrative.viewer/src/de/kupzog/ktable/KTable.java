/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 
 Authors: 
 Friederich Kupzog,  fkmk@kupzog.de, www.kupzog.de/fkmk
 Lorenz Maierhofer, lorenz.maierhofer@logicmindguide.com

 */

package de.kupzog.ktable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import de.kupzog.ktable.renderers.TextCellRenderer;

/**
 * Custom drawn tabel widget for SWT GUIs.
 * <p>
 * The idea of KTable is to have a flexible grid of cells to display data in it.
 * The class focuses on displaying data and not on collecting the data to
 * display. The latter is done by the <code>KTableModel</code> which has to be
 * implemented for each specific case. Some default tasks are done by a base
 * implementation called <code>KTableDefaultModel</code>. Look also into
 * <code>KTableSortedModel</code> that provides a transparent sorting of
 * cells.<br>
 * The table asks the table model for the amount of columns and rows, the sizes
 * of columns and rows and for the content of the cells which are currently
 * drawn. Even if the table has a million rows, it won�t get slower because it
 * only requests those cells it currently draws. Only a bad table model can
 * influence the drawing speed negatively.
 * <p>
 * When drawing a cell, the table calls a <code>KTableCellRenderer</code> to
 * do this work. The table model determines which cell renderer is used for
 * which cell. A default renderer is available (<code>KTableCellRenderer.defaultRenderer</code>),
 * but the creation of self-written renderers for specific purposes is assumed.
 * Some default renderers are available in the package
 * <code>de.kupzog.ktable.cellrenderers.*</code>.
 * <p>
 * KTable allows to F columns and rows. Each column can have an individual size
 * while the rows are all of the same height except the first row. Multiple
 * column and row headers are possible. These "fixed" cells will not be scrolled
 * out of sight. The column and row count always starts in the upper left corner
 * with 0, independent of the number of column headers or row headers.
 * <p>
 * It is also possible to span cells over several rows and/or columns. The
 * KTable asks the model do provide this information via
 * <code>belongsToCell(col, row)</code>. This method must return the cell the
 * given cell should be merged with.
 * <p>
 * Changing of model values is possible by implementations of
 * <code>KTableCellEditor</code>. Again the KTable asks the model to provide
 * an implementation. Note that there are multiple celleditors available in the
 * package <code>de.kupzog.ktable.editors</code>!
 * 
 * @author Friederich Kupzog
 * @see de.kupzog.ktable.KTableModel
 * @see de.kupzog.ktable.KTableDefaultModel
 * @see de.kupzog.ktable.KTableSortedModel
 * @see de.kupzog.ktable.KTableCellRenderer
 * @see de.kupzog.ktable.KTableCellEditor
 * @see de.kupzog.ktable.KTableCellSelectionListener
 * 
 */
public class KTable extends Canvas {

	// Data and data editing facilities:
	protected KTableModel m_Model;
	protected KTableCellEditor m_CellEditor;

	// current visible:
	protected int m_TopRow;
	protected int m_LeftColumn;

	// Selection
	protected HashMap m_Selection;
	protected int m_FocusRow;
	protected int m_FocusCol;
	protected int m_MainFocusRow;
	protected int m_MainFocusCol;
	protected int m_ClickColumnIndex;
	protected int m_ClickRowIndex;
	private int m_Style = SWT.NONE;

	// important measures
	protected int m_RowsVisible;
	protected int m_RowsFullyVisible;
	protected int m_ColumnsVisible;
	protected int m_ColumnsFullyVisible;

	// column sizes
	protected int m_ResizeColumnIndex;
	protected int m_ResizeColumnLeft;
	protected int m_ResizeRowIndex;
	protected int m_ResizeRowTop;
	protected int m_NewRowSize;
	protected int m_NewColumnSize;
	protected boolean m_Capture = false;
	protected Image m_LineRestore;
	protected int m_LineX;
	protected int m_LineY;

	// resize area
	protected int m_ResizeAreaSize = 10;

	// diverse
	protected Display m_Display;
	protected ArrayList cellSelectionListeners;
	protected ArrayList cellDoubleClickListeners;
	protected ArrayList cellResizeListeners;
	protected Cursor m_defaultCursor;
	protected Point m_defaultCursorSize;
	protected Cursor m_defaultRowResizeCursor;
	protected Cursor m_defaultColumnResizeCursor;
	protected String m_nativTooltip;

	// ////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR & DISPOSE
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new KTable.
	 * 
	 * possible styles:
	 * <ul>
	 * <li><b>SWT.V_SCROLL</b> - show vertical scrollbar and allow vertical
	 * scrolling by arrow keys</li>
	 * <li><b>SWT.H_SCROLL</b> - show horizontal scrollbar and allow
	 * horizontal scrolling by arrow keys</li>
	 * <li><b>SWTX.AUTO_SCROLL</b> - Dynamically shows vertical and horizontal
	 * scrollbars when they are necessary.</li>
	 * <li><b>SWTX.FILL_WITH_LASTCOL</b> - Makes the table enlarge the last
	 * column to always fill all space.</li>
	 * <li><b>SWTX.FILL_WITH_DUMMYCOL</b> - Makes the table fill any remaining
	 * space with dummy columns to fill all space.</li>
	 * <li><b>SWT.FLAT</b> - Does not paint a dark outer border line.</li>
	 * <li><b>SWT.MULTI</b> - Sets the "Multi Selection Mode". In this mode,
	 * more than one cell or row can be selected. The user can achieve this by
	 * shift-click and ctrl-click. The selected cells/rows can be scattored ofer
	 * the complete table. If you pass false, only a single cell or row can be
	 * selected. This mode can be combined with the "Row Selection Mode".</li>
	 * <li><b>SWT.FULL_SELECTION</b> - Sets the "Full Selection Mode". In the
	 * "Full Selection Mode", the table always selects a complete row.
	 * Otherwise, each individual cell can be selected. This mode can be
	 * combined with the "Multi Selection Mode".</li>
	 * <li><b>SWTX.EDIT_ON_KEY</b> - Activates a possibly present cell editor
	 * on every keystroke. (Default: only ENTER). However, note that editors can
	 * specify which events they accept.</li>
	 * <li><b>SWTX.MARK_FOCUS_HEADERS</b> - Makes KTable draw left and top
	 * header cells in a different style when the focused cell is in their
	 * row/column. This mimics the MS Excel behavior that helps find the
	 * currently selected cell(s).</li>
	 * <li><b>SWT.HIDE_SELECTION</b> - Hides the selected cells when the
	 * KTable looses focus.</li>
	 * After creation a table model should be added using setModel().
	 */
	public KTable(Composite parent, int style) {
		// Initialize canvas to draw on.
		super(parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | style);

		m_Style = style;

		// inits
		m_Display = Display.getCurrent();
		m_Selection = new HashMap();
		m_CellEditor = null;

		m_TopRow = 0;
		m_LeftColumn = 0;
		m_FocusRow = 0;
		m_FocusCol = 0;
		m_RowsVisible = 0;
		m_RowsFullyVisible = 0;
		m_ColumnsVisible = 0;
		m_ColumnsFullyVisible = 0;
		m_ResizeColumnIndex = -1;
		m_ResizeRowIndex = -1;
		m_ResizeRowTop = -1;
		m_NewRowSize = -1;
		m_NewColumnSize = -1;
		m_ResizeColumnLeft = -1;
		m_ClickColumnIndex = -1;
		m_ClickRowIndex = -1;

		m_LineRestore = null;
		m_LineX = 0;
		m_LineY = 0;

		cellSelectionListeners = new ArrayList(10);
		cellDoubleClickListeners = new ArrayList(10);
		cellResizeListeners = new ArrayList(10);

		// Listener creation
		createListeners();

		// handle tooltip initialization:
		m_nativTooltip = super.getToolTipText();
		super.setToolTipText("");

		// apply various style bits:
		if ((style & SWTX.AUTO_SCROLL) == SWTX.AUTO_SCROLL) {
			addListener(SWT.Resize, new Listener() {
				public void handleEvent(Event event) {
					updateScrollbarVisibility();
				}
			});
			addCellResizeListener(new KTableCellResizeListener() {
				public void rowResized(int row, int newHeight) {
					updateScrollbarVisibility();
				}

				public void columnResized(int col, int newWidth) {
					updateScrollbarVisibility();
				}
			});
		}
	}

	public void dispose() {

		if (m_defaultCursor != null)
			m_defaultCursor.dispose();

		if (m_defaultRowResizeCursor != null)
			m_defaultRowResizeCursor.dispose();

		if (m_defaultColumnResizeCursor != null)
			m_defaultColumnResizeCursor.dispose();

		super.dispose();

	}

	protected void createListeners() {

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				onPaint(event);
			}
		});

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				redraw();
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				onMouseDown(e);
			}

			public void mouseUp(MouseEvent e) {
				onMouseUp(e);
			}

			public void mouseDoubleClick(MouseEvent e) {
				onMouseDoubleClick(e);
			}
		});

		addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				onMouseMove(e);
			}
		});

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				onKeyDown(e);
			}
		});

		addCellSelectionListener(new KTableCellSelectionAdapter() {

			private Point[] oldSelections;

			public void cellSelected(int col, int row, int statemask) {
				if (isHighlightSelectionInHeader() && (statemask & SWT.SHIFT) == 0) {
					Point[] selections = getCellSelection();
					GC gc = new GC(KTable.this);

					repaintRelevantCells(gc, oldSelections);
					repaintRelevantCells(gc, selections);

					gc.dispose();
					oldSelections = selections;
				}
			}

			private void repaintRelevantCells(GC gc, Point[] selections) {
				if (selections == null)
					return;
				Rectangle bounds = getClientArea();
				Rectangle oldClipping = gc.getClipping();
				int fixedWidth = 0;
				int fixedHeight = 0;
				for (int k = 0; k < m_Model.getFixedHeaderColumnCount(); k++)
					fixedWidth += getCellRectIgnoreSpan(k, 0).width + 1;
				for (int k = 0; k < m_Model.getFixedHeaderRowCount(); k++)
					fixedHeight += getCellRectIgnoreSpan(0, k).height + 1;

				for (int i = 0; i < selections.length; i++) {
					int col = selections[i].x;
					int row = selections[i].y;
					for (int j = 0; j < getModel().getFixedHeaderColumnCount(); j++) {
						Point valid = getValidCell(j, row);
						// allow painting of GC only on columns, not on rows:
						Rectangle rowClip = new Rectangle(1, 1 + fixedHeight, fixedWidth, bounds.height - 1 - fixedHeight);
						rowClip.intersect(oldClipping);
						gc.setClipping(rowClip);
						drawCell(gc, valid.x, valid.y);
					}
					for (int j = 0; j < getModel().getFixedHeaderRowCount(); j++) {
						Point valid = getValidCell(col, j);
						// allow painting of GC only on rows, not on cols:
						Rectangle rowClip = new Rectangle(1 + fixedWidth, 1, bounds.width - 1 - fixedWidth, fixedHeight);

						rowClip.intersect(oldClipping);
						gc.setClipping(rowClip);
						drawCell(gc, valid.x, valid.y);
					}
					gc.setClipping(oldClipping);
				}
			}
		});

		addFocusListener(new FocusListener() {

			private Point[] oldSelection;

			public void focusGained(FocusEvent e) {
				if (!isShowSelectionWithoutFocus() && oldSelection != null) {
					setSelection(oldSelection, false);
					for (int i = 0; i < oldSelection.length; i++)
						updateCell(oldSelection[i].x, oldSelection[i].y);
					oldSelection = null;
				}
			}

			public void focusLost(FocusEvent e) {
				if (!isShowSelectionWithoutFocus()) {
					oldSelection = getCellSelection();
					clearSelection();
					if (oldSelection != null)
						for (int i = 0; i < oldSelection.length; i++)
							updateCell(oldSelection[i].x, oldSelection[i].y);
				}
			}

		});

		TooltipListener tooltipListener = new TooltipListener();
		addListener(SWT.Dispose, tooltipListener);
		addListener(SWT.KeyDown, tooltipListener);
		addListener(SWT.MouseDown, tooltipListener);
		addListener(SWT.MouseDoubleClick, tooltipListener);
		addListener(SWT.MouseMove, tooltipListener);
		addListener(SWT.MouseHover, tooltipListener);
		addListener(SWT.MouseExit, tooltipListener);

		if (getVerticalBar() != null) {
			getVerticalBar().addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int oldTopRow = m_TopRow;
					m_TopRow = getVerticalBar().getSelection();
					if (oldTopRow != m_TopRow)
						redraw();
				}

			});
			getVerticalBar().addListener(SWT.Selection, tooltipListener);
		}

		if (getHorizontalBar() != null) {
			getHorizontalBar().addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int oldLeftCol = m_LeftColumn;
					m_LeftColumn = getHorizontalBar().getSelection();
					if (oldLeftCol != m_LeftColumn)
						redraw();
				}
			});
			getHorizontalBar().addListener(SWT.Selection, tooltipListener);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////
	// CALCULATIONS
	// ////////////////////////////////////////////////////////////////////////////

	protected int getFixedWidth() {
		int width = 0;
		for (int i = 0; i < getFixedColumnCount(); i++)
			width += getColumnWidth(i);
		return width;
	}

	protected int getHeaderWidth() {
		int width = 0;
		for (int i = 0; i < m_Model.getFixedHeaderColumnCount(); i++)
			width += getColumnWidth(i);
		return width;
	}

	protected int getColumnLeft(int index) {
		if (index < getFixedColumnCount()) {
			int x = 0;
			for (int i = 0; i < index; i++) {
				x += getColumnWidth(i);
			}
			return x;
		}
		// regular data area:
		if (index < m_LeftColumn || index > m_LeftColumn + m_ColumnsVisible)
			return -1;
		int x = getFixedWidth();
		for (int i = m_LeftColumn; i < index; i++) {
			x += getColumnWidth(i);
		}
		return x;
	}

	protected int getColumnRight(int index) {
		if (index < 0)
			return 0;
		return getColumnLeft(index) + getColumnWidth(index);
	}

	protected int getRowBottom(int index) {

		if (index < 0)
			return 0;

		int y = getFixedHeight();
		for (int i = m_TopRow; i <= index; i++) {
			y += m_Model.getRowHeight(i);
		}

		return y;

	}

	private int getFixedHeight() {
		int height = 1;
		for (int i = 0; i < getFixedRowCount(); i++)
			height += m_Model.getRowHeight(i);
		return height;
	}

	/**
	 * @return Returns the number of visible rows in the table. This always
	 *         contains the fixed rows, and adds the number of rows that fit in
	 *         the rest based on the currently shown top-row.
	 */
	public int getVisibleRowCount() {
		if (m_Model == null)
			return 0;
		return getFixedRowCount() + getFullyVisibleRowCount(getFixedHeight());
	}

	private int getFullyVisibleRowCount(int fixedHeight) {
		Rectangle rect = getClientArea();
		ScrollBar sb = getHorizontalBar();
		if (sb != null)
			rect.height -= sb.getSize().y;

		int count = 0;
		int heightSum = fixedHeight;
		for (int i = m_TopRow; heightSum < rect.height; i++) {
			count++;
			heightSum += m_Model.getRowHeight(i);
		}
		return count;
	}

	protected void doCalculations() {
		if (m_Model == null) {
			ScrollBar sb = getHorizontalBar();
			if (sb != null) {
				sb.setMinimum(0);
				sb.setMaximum(1);
				sb.setPageIncrement(1);
				sb.setThumb(1);
				sb.setSelection(1);
			}
			sb = getVerticalBar();
			if (sb != null) {
				sb.setMinimum(0);
				sb.setMaximum(1);
				sb.setPageIncrement(1);
				sb.setThumb(1);
				sb.setSelection(1);
			}
			return;
		}

		Rectangle rect = getClientArea();
		if (m_LeftColumn < getFixedColumnCount()) {
			m_LeftColumn = getFixedColumnCount();
		}
		if (m_LeftColumn > m_Model.getColumnCount())
			m_LeftColumn = 0;

		if (m_TopRow < getFixedRowCount()) {
			m_TopRow = getFixedRowCount();
		}
		if (m_TopRow > m_Model.getRowCount())
			m_TopRow = 0;

		int fixedHeight = getFixedHeight();
		m_ColumnsVisible = 0;
		m_ColumnsFullyVisible = 0;

		if (m_Model.getColumnCount() > getFixedColumnCount()) {
			int runningWidth = getColumnLeft(m_LeftColumn);
			for (int col = m_LeftColumn; col < m_Model.getColumnCount(); col++) {
				if (runningWidth < rect.width + rect.x)
					m_ColumnsVisible++;
				runningWidth += getColumnWidth(col);
				if (runningWidth < rect.width + rect.x)
					m_ColumnsFullyVisible++;
				else
					break;
			}
		}

		ScrollBar sb = getHorizontalBar();
		if (sb != null) {
			if (m_Model.getColumnCount() <= getFixedColumnCount()) {
				sb.setMinimum(0);
				sb.setMaximum(1);
				sb.setPageIncrement(1);
				sb.setThumb(1);
				sb.setSelection(1);
			} else {
				sb.setMinimum(getFixedColumnCount());
				sb.setMaximum(m_Model.getColumnCount());
				sb.setIncrement(1);
				sb.setPageIncrement(1);
				sb.setThumb(m_ColumnsFullyVisible);
				sb.setSelection(m_LeftColumn);
			}
		}

		m_RowsFullyVisible = getFullyVisibleRowCount(fixedHeight);
		m_RowsFullyVisible = Math.min(m_RowsFullyVisible, m_Model.getRowCount() - getFixedRowCount());
		m_RowsFullyVisible = Math.max(0, m_RowsFullyVisible);

		m_RowsVisible = m_RowsFullyVisible + 1;

		if (m_TopRow + m_RowsFullyVisible > m_Model.getRowCount()) {
			m_TopRow = Math.max(getFixedRowCount(), m_Model.getRowCount() - m_RowsFullyVisible);
		}

		if (m_TopRow + m_RowsFullyVisible >= m_Model.getRowCount()) {
			m_RowsVisible--;
		}

		sb = getVerticalBar();
		if (sb != null) {
			if (m_Model.getRowCount() <= getFixedRowCount()) {
				sb.setMinimum(0);
				sb.setMaximum(1);
				sb.setPageIncrement(1);
				sb.setThumb(1);
				sb.setSelection(1);
			} else {
				sb.setMinimum(getFixedRowCount());
				sb.setMaximum(m_Model.getRowCount());
				sb.setPageIncrement(m_RowsVisible - getFixedRowCount());
				sb.setIncrement(1);
				sb.setThumb(m_RowsFullyVisible);
				sb.setSelection(m_TopRow);
			}
		}
	}

	/**
	 * Returns the area that is occupied by the given cell. Does not take into
	 * account any cell span.
	 * 
	 * @param col
	 * @param row
	 * @return Rectangle
	 */
	protected Rectangle getCellRectIgnoreSpan(int col, int row) {
		return getCellRectIgnoreSpan(col, row, getColumnLeft(col) + 1);
	}

	/**
	 * Returns the area that is occupied by the given cell. Does not take into
	 * account any cell span.
	 * <p>
	 * This version is an optimization if the x value is known. Use the version
	 * with just col and row as parameter if this is not known.
	 * 
	 * @param col
	 *            The column index
	 * @param row
	 *            The row index
	 * @param x_startValue
	 *            The right horizontal start value - if this is not known, there
	 *            is a version without this parameter! Note that this is the end
	 *            value of the last cell + 1 px border.
	 * @return Returns the area of a cell
	 */
	protected Rectangle getCellRectIgnoreSpan(int col, int row, int x_startValue) {
		if ((col < 0) || (col >= m_Model.getColumnCount()))
			return new Rectangle(-1, -1, 0, 0);

		int x = x_startValue;
		int y;

		y = getYforRow(row);
		if (row >= getFixedRowCount() && row < m_TopRow) {
			for (int i = row; i < m_TopRow; i++)
				y -= m_Model.getRowHeight(i);
		}
		// take into account 1px bottom and right border that
		// belongs to the cell and is rendered by the cellrenderer
		// TODO: Make lines seperately configured and seperately drawn items
		int width = getColumnWidth(col) - 1;
		int height = m_Model.getRowHeight(row) - 1;

		return new Rectangle(x, y, width, height);
	}

	private int getRowForY(int y) {
		int rowSum = 1;
		for (int i = 0; i < getFixedRowCount(); i++) {
			int height = m_Model.getRowHeight(i);
			if (rowSum < y && rowSum + height >= y)
				return i;
			rowSum += height;
		}
		for (int i = m_TopRow; i < m_Model.getRowCount(); i++) {
			int height = m_Model.getRowHeight(i);
			if (rowSum < y && rowSum + height >= y)
				return i;
			rowSum += height;
		}
		return -1;
	}

	private int getYforRow(int row) {
		if (row == 0)
			return 1;

		int y = 1 + m_Model.getRowHeight(0);
		if (row < m_Model.getFixedHeaderRowCount()) {
			for (int i = 1; i < row; i++)
				y += m_Model.getRowHeight(row);

		} else {
			y = getFixedHeight();
			if (row < 1)
				return 1;
			for (int i = m_TopRow; i < row; i++)
				y += m_Model.getRowHeight(i);
		}
		return y;
	}

	private Rectangle getCellRect(int col, int row, int left_X) {
		Point valid = getValidCell(col, row);

		Rectangle bound = getCellRectIgnoreSpan(col, row, left_X);
		// determine the cells that are contained:
		int spanRow = 0, spanCol = 0;
		// move over columns and add width:
		while (getValidCell(col + spanCol + 1, row).equals(valid) && col + spanCol + 1 < m_Model.getColumnCount()) {
			int width = getColumnWidth(col + spanCol + 1);
			bound.width += width;
			spanCol++;
		}
		while (getValidCell(col, row + spanRow + 1).equals(valid)) {
			bound.height += m_Model.getRowHeight(row + spanRow + 1);
			spanRow++;
		}
		return bound;
	}

	/**
	 * Returns the area that is occupied by the given cell. Respects cell span.
	 * 
	 * @param col
	 *            the column index
	 * @param row
	 *            the row index
	 * @return returns the area the cell content is drawn at.
	 * @throws IllegalArgumentException
	 *             if the given cell is not a cell that is visible, but
	 *             overlapped by a spanning cell. Call getValidCell() first to
	 *             ensure it is a visible cell.
	 */
	public Rectangle getCellRect(int col, int row) {
		checkWidget();

		// be sure that it is a valid cell that is not overlapped by some other:
		Point valid = getValidCell(col, row);
		if (valid.x != col || row != valid.y)
			return new Rectangle(0, 0, 0, 0);
		// throw new IllegalArgumentException("The cell bounds of an overlapped,
		// " +
		// "non-visible cell were requested: "+col+", "+row);

		Rectangle bound = getCellRectIgnoreSpan(col, row);
		// determine the cells that are contained:
		int spanRow = 0, spanCol = 0;
		// move over columns and add width:
		while (getValidCell(col + spanCol + 1, row).equals(valid) && col + spanCol + 1 < m_Model.getColumnCount()) {
			int width = getColumnWidth(col + spanCol + 1);
			bound.width += width;
			spanCol++;
		}

		while (getValidCell(col, row + spanRow + 1).equals(valid)) {
			bound.height += m_Model.getRowHeight(row + spanRow + 1);
			spanRow++;
		}
		return bound;
	}

	protected boolean canDrawCell(Rectangle r, Rectangle clipRect) {
		if (r.height == 0 || r.width == 0)
			return false;
		if (r.y + r.height < clipRect.y)
			return false;
		if (r.y > clipRect.y + clipRect.height)
			return false;
		if (r.x + r.width < clipRect.x)
			return false;
		if (r.x > clipRect.x + clipRect.width)
			return false;
		return true;
	}

	// ////////////////////////////////////////////////////////////////////////////
	// PAINTING & MORE
	// ////////////////////////////////////////////////////////////////////////////

	// Paint-Result
	protected void onPaint(PaintEvent event) {

		Rectangle rect = getClientArea();
		GC gc = event.gc;

		doCalculations();

		if (m_Model != null) {

			// be sure that clipping cuts oft all the unneccessary part of a
			// spanned cell:
			Rectangle oldClipping = setContentAreaClipping(gc);
			drawCells(gc, oldClipping, m_LeftColumn, m_Model.getColumnCount(), m_TopRow, m_TopRow + m_RowsVisible);
			// content has been drawn, so set back clipping:

			setTopAreaClipping(gc, oldClipping);
			drawCells(gc, gc.getClipping(), 0, getFixedColumnCount(), m_TopRow, m_TopRow + m_RowsVisible);
			setLeftAreaClipping(gc, oldClipping);
			drawCells(gc, gc.getClipping(), m_LeftColumn, m_Model.getColumnCount(), 0, getFixedRowCount());
			gc.setClipping(oldClipping);
			drawCells(gc, gc.getClipping(), 0, getFixedColumnCount(), 0, getFixedRowCount());

			drawBottomSpace(gc);
		} else {
			gc.fillRectangle(rect);
		}
	}

	private void setTopAreaClipping(GC gc, Rectangle oldClipping) {
		Rectangle contentClip = getClientArea();

		contentClip.x = 1;
		contentClip.y = 1;
		contentClip.width -= 1;
		contentClip.height -= 1;

		for (int i = 0; i < getFixedRowCount(); i++) {
			int height = getCellRectIgnoreSpan(0, i).height;
			contentClip.y += height + 1;
			contentClip.height -= height + 1;
		}

		contentClip.intersect(oldClipping);
		gc.setClipping(contentClip);
	}

	private void setLeftAreaClipping(GC gc, Rectangle oldClipping) {
		Rectangle contentClip = getClientArea();

		contentClip.x = 1;
		contentClip.y = 1;
		contentClip.width -= 1;
		contentClip.height -= 1;

		for (int i = 0; i < getFixedColumnCount(); i++) {
			int width = getCellRectIgnoreSpan(i, 0).width;
			contentClip.x += width + 1;
			contentClip.width -= width + 1;
		}

		contentClip.intersect(oldClipping);
		gc.setClipping(contentClip);
	}

	/**
	 * This sets the clipping area of the GC to the content area of the table,
	 * ignoring all header cells. This has the result that fixed cells are not
	 * drawn by this gc!
	 * 
	 * @param gc
	 *            The gc to manipulate.
	 * @return The old clipping area. It you want to paint fixed cells with this
	 *         GC, re-set this with gc.setClipping();
	 */
	private Rectangle setContentAreaClipping(GC gc) {
		Rectangle oldClipping = gc.getClipping();
		Rectangle contentClip = getClientArea();

		contentClip.x = 1;
		contentClip.y = 1;
		contentClip.width -= 1;
		contentClip.height -= 1;

		for (int i = 0; i < getFixedColumnCount(); i++) {
			int width = getCellRectIgnoreSpan(i, 0).width;
			contentClip.x += width + 1;
			contentClip.width -= width + 1;
		}

		for (int i = 0; i < getFixedRowCount(); i++) {
			int height = getCellRectIgnoreSpan(0, i).height;
			contentClip.y += height + 1;
			contentClip.height -= height + 1;
		}

		contentClip.intersect(oldClipping);
		gc.setClipping(contentClip);
		return oldClipping;
	}

	// Bottom-Space
	protected void drawBottomSpace(GC gc) {
		Rectangle r = getClientArea();
		if (m_Model.getRowCount() > 0) {
			r.y = getFixedHeight();
			for (int i = 0; i < m_RowsVisible; i++) {
				r.y += m_Model.getRowHeight(i + m_TopRow);
			}
		}

		int lastColRight = getColumnRight(Math.min(m_LeftColumn + m_ColumnsVisible, m_Model.getColumnCount() - 1));

		// implement the behavior that we fill the remaining space with an
		// additional empty column.
		if ((getStyle() & SWTX.FILL_WITH_DUMMYCOL) != 0) {
			lastColRight--;
			int lastCol = m_Model.getColumnCount() - 1;

			// int defaultrowheight = m_Model.getRowHeight()-1;
			int ystart = 1;
			for (int row = 0; row < getFixedRowCount(); row++) {
				Point last = getValidCell(lastCol, row);
				KTableCellRenderer fixedRenderer = m_Model.getCellRenderer(last.x, last.y);

				int rowheight = m_Model.getRowHeight(row);
				fixedRenderer.drawCell(gc, new Rectangle(lastColRight + 2, ystart, r.width - 1, rowheight - 1), -1, -1, "", false, true, false, m_Model);

				ystart += rowheight;
			}
			TextCellRenderer defaultRenderer = new TextCellRenderer(SWT.NONE);
			for (int row = m_TopRow; row < m_TopRow + m_RowsVisible + 1; row++) {
				int rowHeight = m_Model.getRowHeight(row);
				defaultRenderer.drawCell(gc, new Rectangle(lastColRight + 2, ystart, r.width - 1, rowHeight - 1), -1, -1, "", false, false, false, m_Model);
				ystart += rowHeight;
			}

			// draw bottom areas
			gc.setBackground(getBackground());
			gc.fillRectangle(r);

			// draw outer lines:
			Rectangle clientArea = getClientArea();
			gc.setForeground(m_Display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			gc.drawLine(1, r.y, clientArea.x + clientArea.width - 1, r.y);
			gc.drawLine(clientArea.x + clientArea.width - 1, 0, clientArea.x + clientArea.width - 1, r.y - 1);

			// draw left and top border line:
			if (m_Model.getRowCount() > 0) {
				if ((getStyle() & SWT.FLAT) == 0)
					gc.setForeground(m_Display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
				else
					gc.setForeground(m_Display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
				gc.drawLine(0, 0, 0, clientArea.y + clientArea.height - 1);
				gc.drawLine(0, 0, clientArea.x + clientArea.width, 0);
				if (m_LeftColumn + m_ColumnsVisible == m_Model.getColumnCount())
					gc.drawLine(clientArea.x + clientArea.width - 1, 0, clientArea.x + clientArea.width - 1, clientArea.y + clientArea.height - 1);
			}

		} else if ((getStyle() & SWTX.FILL_WITH_LASTCOL) != 0) {
			gc.setBackground(getBackground());
			gc.fillRectangle(r);

			// draw outer lines:
			Rectangle clientArea = getClientArea();
			gc.setForeground(m_Display.getSystemColor(SWT.COLOR_WHITE));
			gc.drawLine(1, r.y, clientArea.x + clientArea.width - 1, r.y);
			if (m_LeftColumn + m_ColumnsVisible == m_Model.getColumnCount())
				gc.drawLine(clientArea.x + clientArea.width - 1, 0, clientArea.x + clientArea.width - 1, r.y - 1);

			// draw left and top border line:
			if (m_Model.getRowCount() > 0) {
				if ((getStyle() & SWT.FLAT) == 0)
					gc.setForeground(m_Display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
				else
					gc.setForeground(m_Display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
				gc.drawLine(0, 0, 0, clientArea.y + clientArea.height - 1);
				gc.drawLine(0, 0, clientArea.x + clientArea.width, 0);
				if (m_LeftColumn + m_ColumnsVisible == m_Model.getColumnCount())
					gc.drawLine(clientArea.x + clientArea.width - 1, 0, clientArea.x + clientArea.width - 1, clientArea.y + clientArea.height);
			}

		} else {
			// draw simple background colored areas
			gc.setBackground(getBackground());
			gc.fillRectangle(r);
			gc.fillRectangle(lastColRight + 2, 0, r.width, r.height);

			gc.setForeground(m_Display.getSystemColor(SWT.COLOR_WHITE));
			gc.drawLine(1, r.y, lastColRight + 1, r.y);
			gc.drawLine(lastColRight + 1, 0, lastColRight + 1, r.y - 1);

			// draw left and top border line:
			if (m_Model.getRowCount() > 0) {
				if ((getStyle() & SWT.FLAT) == 0)
					gc.setForeground(m_Display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
				else
					gc.setForeground(m_Display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
				gc.drawLine(0, 0, 0, r.y - 1);
				gc.drawLine(0, 0, lastColRight, 0);
			}
		}

	}

	// Cells
	/**
	 * Redraws the the cells only in the given area.
	 * 
	 * @param cellsToRedraw
	 *            Defines the area to redraw. The rectangles elements are not
	 *            pixels but cell numbers.
	 */
	public void redraw(Rectangle cellsToRedraw) {
		checkWidget();
		redraw(cellsToRedraw.x, cellsToRedraw.y, cellsToRedraw.width, cellsToRedraw.height);
	}

	/**
	 * Redraws the the cells only in the given area.
	 * 
	 * @param firstCol
	 *            the first column to draw.
	 * @param firstRow
	 *            the first row to draw. (Map if you use a sorted model!)
	 * @param numOfCols
	 *            the number of columns to draw.
	 * @param numOfRows
	 *            the number of rows to draw.
	 */
	public void redraw(int firstCol, int firstRow, int numOfCols, int numOfRows) {
		checkWidget();
		boolean redrawFixedRows = false;
		if (firstRow < getFixedRowCount()) {
			firstRow = m_TopRow;
			redrawFixedRows = true;
		}
		boolean redrawFixedCols = false;
		if (firstCol < getFixedColumnCount()) {
			firstCol = m_LeftColumn;
			redrawFixedCols = true;
		}

		Rectangle clipRect = getClientArea();
		GC gc = new GC(this);
		Rectangle oldClip = setContentAreaClipping(gc);
		drawCells(gc, clipRect, firstCol, firstCol + numOfCols, firstRow, firstRow + numOfRows);
		gc.setClipping(oldClip);

		if (redrawFixedRows)
			drawCells(gc, gc.getClipping(), m_LeftColumn, m_Model.getColumnCount(), 0, getFixedRowCount());
		if (redrawFixedCols)
			drawCells(gc, gc.getClipping(), 0, getFixedColumnCount(), m_TopRow, m_TopRow + m_RowsVisible);
		if (redrawFixedCols || redrawFixedRows) {
			drawCells(gc, gc.getClipping(), 0, getFixedColumnCount(), 0, getFixedRowCount());
			drawBottomSpace(gc);
		}
		gc.dispose();
	}

	protected void drawCells(GC gc, Rectangle clipRect, int fromCol, int toCol, int fromRow, int toRow) {
		Rectangle r;

		// for the starting col and row, we have to check if they
		// are subcells that are part of a span cell.
		Point valid = getValidCell(fromCol, fromRow);
		fromCol = valid.x;
		if (valid.y < fromRow)
			fromRow = valid.y;
		int moveLeft = 1;
		for (int i = Math.min(fromCol + m_ColumnsVisible, toCol); i > fromCol; i -= moveLeft) {
			valid = getValidCell(i, fromRow);
			if (valid.y < fromRow)
				fromRow = valid.y;
			moveLeft = i - valid.x + 1;
		}

		if (m_CellEditor != null) {
			if (!isCellVisible(m_CellEditor.m_Col, m_CellEditor.m_Row)) {
				Rectangle hide = new Rectangle(-101, -101, 100, 100);
				m_CellEditor.setBounds(hide);
			} else {
				m_CellEditor.setBounds(getCellRect(m_CellEditor.m_Col, m_CellEditor.m_Row));
			}
		}

		int fromCol_X = getCellRectIgnoreSpan(fromCol, fromRow).x;
		for (int row = fromRow; row < toRow; row++) {
			// skipping non-visible:
			r = getCellRectIgnoreSpan(fromCol, row, fromCol_X);
			// if (r.y + r.height < clipRect.y && !haveToPaintRowBefore)
			// continue;
			if (r.y > clipRect.y + clipRect.height) {
				break;
			}

			// the right cell border is cached to avoid the expensive col loop.
			int right_border_x = r.x;
			for (int col = fromCol; col < toCol; col++) {
				r = getCellRect(col, row, right_border_x);
				right_border_x += getColumnWidth(col);

				if (r.x > clipRect.x + clipRect.width)
					break;
				if (r.y > clipRect.y + clipRect.height)
					return;

				// check if it is an overlapped cell that must not be drawn:
				Point belongsTo = m_Model.belongsToCell(col, row);
				if (belongsTo != null && (belongsTo.x != col || belongsTo.y != row))
					continue;

				// perform real work:
				if (canDrawCell(r, clipRect))
					drawCell(gc, col, row, r);

			}
		}
	}

	/**
	 * Looks into the model to determine if the given cell is overlapped by a
	 * cell that spans several columns/rows. In that case the index of the cell
	 * responsible for the content (in the left upper corner) is returned.
	 * Otherwise the given cell is returned.
	 * 
	 * @param colToCheck
	 *            The column index of the cell to check.
	 * @param rowToCheck
	 *            The row index of the cell to check. (as seen by the KTable.
	 *            Map if you use a sorted model!)
	 * @return returns the cell that overlaps the given cell, or the given cell
	 *         if no cell overlaps it.
	 * @throws IllegalArgumentException
	 *             If the model returns cells on <code>
	 * Model.belongsToCell()</code>
	 *             that are on the right or below the given cell.
	 * @see KTableSortedModel#mapToTable(int);
	 */
	public Point getValidCell(int colToCheck, int rowToCheck) {
		checkWidget();
		// well, there is no supercell with negative indices, so don't check:
		Point found = new Point(colToCheck, rowToCheck);
		Point lastFound = null;
		while (!found.equals(lastFound)) {
			lastFound = found;
			found = m_Model.belongsToCell(found.x, found.y);
			if (found != null && (found.x > lastFound.x || found.y > lastFound.y))
				throw new IllegalArgumentException("When spanning over several cells, " + "supercells that determine the content of the large cell must " + "always be in the left upper corner!");
			if (found == null)
				return lastFound;
		}
		return found;
	}

	/**
	 * Call when a manual redraw on a cell should be performed. In case headers
	 * should be updated to reflect a focus change, this is performed.
	 * 
	 * @param gc
	 * @param col
	 * @param row
	 */
	protected void drawCell(GC gc, int col, int row) {

		drawCell(gc, col, row, getCellRect(col, row));

		Rectangle oldClip = gc.getClipping();
		gc.setClipping(getClientArea());
		if ((getStyle() & SWTX.MARK_FOCUS_HEADERS) == SWTX.MARK_FOCUS_HEADERS) {
			if (row >= m_TopRow) {
				for (int i = 0; i < m_Model.getFixedHeaderColumnCount(); i++)
					drawCell(gc, i, row, getCellRect(i, row));
				for (int i = 0; i < m_Model.getFixedHeaderRowCount(); i++)
					drawCell(gc, col, i, getCellRect(col, i));
			}
		}
		gc.setClipping(oldClip);
	}

	protected void drawCell(GC gc, int col, int row, Rectangle rect) {
		if ((row < 0) || (row >= m_Model.getRowCount())) {
			return;
		}

		if (rect.width == 0 || rect.height == 0)
			return;

		// set up clipping so that the renderer is only
		// allowed to paint in his area:
		Rectangle oldClip = gc.getClipping();
		Rectangle newClip = new Rectangle(rect.x, rect.y, rect.width + 1, rect.height + 1);
		newClip.intersect(oldClip);
		gc.setClipping(newClip);

		m_Model.getCellRenderer(col, row).drawCell(gc, rect, col, row, m_Model.getContentAt(col, row), showAsSelected(col, row) || highlightSelectedRowCol(col, row), isHeaderCell(col, row),
				col == m_ClickColumnIndex && row == m_ClickRowIndex, m_Model);

		gc.setClipping(oldClip);
	}

	/**
	 * Interface method to update the content of a cell.
	 * <p>
	 * Don't forget to map the row index if a sorted model is used.
	 * 
	 * @param col
	 *            The column index
	 * @param row
	 *            The row index.
	 * @see KTableSortedModel#mapRowIndexToTable(int)
	 */
	public void updateCell(int col, int row) {
		checkWidget();
		if ((row < 0) || (row >= m_Model.getRowCount()) || (col < 0) || (col >= m_Model.getColumnCount()))
			return;

		// be sure it is a valid cell if cells span
		Point valid = getValidCell(col, row);
		// update it:
		GC gc = new GC(this);
		drawCell(gc, valid.x, valid.y);
		gc.dispose();
	}

	/**
	 * @param col
	 *            The column index
	 * @param row
	 *            The row index
	 * @return Returns true if the given cell is a fixed cell, that is a header
	 *         cell. Returns false otherwise.
	 */
	public boolean isFixedCell(int col, int row) {
		return col < getFixedColumnCount() || row < getFixedRowCount();
	}

	/**
	 * @param col
	 *            The column index
	 * @param row
	 *            The row index
	 * @return Returns true if the given cell is within the region specified by
	 *         the model using the methods
	 *         <code>getFixedHeaderColumnCount()</code> and
	 *         <code>getFixedHeaderRowCount()</code>
	 */
	public boolean isHeaderCell(int col, int row) {
		return col < m_Model.getFixedHeaderColumnCount() || row < m_Model.getFixedHeaderRowCount();
	}

	protected boolean showAsSelected(int col, int row) {
		// A cell with an open editor should be drawn without focus
		if (m_CellEditor != null) {
			if (col == m_CellEditor.m_Col && row == m_CellEditor.m_Row)
				return false;
		}
		return isCellSelected(col, row) && (isFocusControl() || isShowSelectionWithoutFocus());
	}

	protected void drawRow(GC gc, int row) {
		drawCells(gc, getClientArea(), 0, getFixedColumnCount(), row, row + 1);
		drawCells(gc, getClientArea(), m_LeftColumn, m_Model.getColumnCount(), row, row + 1);
	}

	protected void drawCol(GC gc, int col) {
		if (col < getFixedColumnCount()) {
			drawCells(gc, getClientArea(), col, col + 1, 0, m_TopRow + m_RowsVisible);
		} else {
			drawCells(gc, getClientArea(), col, col + 1, 0, getFixedRowCount());
			Rectangle oldClip = setContentAreaClipping(gc);
			drawCells(gc, gc.getClipping(), col, col + 1, m_TopRow, m_TopRow + m_RowsVisible);
			gc.setClipping(oldClip);
		}
	}

	/**
	 * Sets the default cursor to the given cursor. This instance is saved
	 * internally and displayed whenever no linecursor or resizecursor is shown.
	 * <p>
	 * The difference to setCursor is that this cursor will be preserved over
	 * action cursor changes.
	 * 
	 * @param cursor
	 *            The cursor to use, or <code>null</code> if the OS default
	 *            cursor should be used.
	 * @param size_below_hotspot
	 *            The number of pixels that are needed to paint the cursor below
	 *            and right of the cursor hotspot (that is the actual location
	 *            the cursor is pointing to).
	 *            <p>
	 *            NOTE that this is just there to allow better positioning of
	 *            tooltips. Currently SWT does not provide an API to get the
	 *            size of the cursor. So these values are taken to calculate the
	 *            position of the tooltip. The the tooltip is placed pt.x pixels
	 *            left and pt.y pixels below the mouse location.<br>
	 *            If you don't know the size of the cursor (for example you use
	 *            a default one), set <code>null</code> or
	 *            <code>new Point(-1, -1)</code>.
	 */
	public void setDefaultCursor(Cursor cursor, Point size_below_hotspot) {
		checkWidget();
		if (m_defaultCursor != null)
			m_defaultCursor.dispose();
		m_defaultCursor = cursor;
		m_defaultCursorSize = size_below_hotspot;
		setCursor(cursor);
	}

	public void setDefaultRowResizeCursor(Cursor cursor) {

		checkWidget();

		if (m_defaultRowResizeCursor != null) {
			m_defaultRowResizeCursor.dispose();
		}

		m_defaultRowResizeCursor = cursor;

	}

	public void setDefaultColumnResizeCursor(Cursor cursor) {

		checkWidget();

		if (m_defaultColumnResizeCursor != null) {
			m_defaultColumnResizeCursor.dispose();
		}

		m_defaultColumnResizeCursor = cursor;

	}

	// ////////////////////////////////////////////////////////////////////////////
	// REACTIONS ON USER ACTIONS
	// ////////////////////////////////////////////////////////////////////////////

	private int getHeaderHeight() {
		int height = 1;
		for (int i = 0; i < m_Model.getFixedHeaderRowCount(); i++)
			height += m_Model.getRowHeight(i);
		return height;
	}

	/* gibt die Nummer einer Modellspalte zur�ck */
	protected int getColumnForResize(int x, int y) {

		if (m_Model == null || y <= 0 || y >= getHeaderHeight()) {
			return -1;
		}

		if (x < getFixedWidth() + m_ResizeAreaSize / 2) {

			for (int i = 0; i < getFixedColumnCount(); i++)

				if (Math.abs(x - getColumnRight(i)) < m_ResizeAreaSize / 2) {

					if (m_Model.isColumnResizable(i)) {
						return i;
					}

					return -1;

				}

		}

		int left = getColumnLeft(m_LeftColumn);
		// -1
		for (int i = m_LeftColumn; i < (m_LeftColumn + m_ColumnsVisible + 1) && i < m_Model.getColumnCount(); i++) {

			if (i >= m_Model.getColumnCount()) {
				return -1;
			}

			int right = left + getColumnWidth(i);

			if (Math.abs(x - right) < m_ResizeAreaSize / 2) {

				if (m_Model.isColumnResizable(i)) {
					return i;
				}

				return -1;

			}

			if ((x >= left + m_ResizeAreaSize / 2) && (x <= right - m_ResizeAreaSize / 2)) {
				break;
			}

			left = right;
		}

		return -1;

	}

	/* gibt die Nummer einer Zeile der Ansicht(!) zur�ck */
	protected int getRowForResize(int x, int y) {

		if (m_Model == null || x <= 0 || x >= getHeaderWidth()) {
			return -1;
		}

		if (y < m_Model.getRowHeight(0) + m_ResizeAreaSize / 2) {

			// allow first row height resize?

			if (Math.abs(m_Model.getRowHeight(0) - y) < m_ResizeAreaSize / 2 && m_Model.isRowResizable(0)) {

				return 0;

			}

			return -1;

		}

		int row = getRowForY(y);
		int row_y = getYforRow(row);
		int next_row_y = getYforRow(row + 1);

		if ((Math.abs(y - next_row_y) < m_ResizeAreaSize / 2) && m_Model.isRowResizable(row)) {
			return row;
		} else if ((Math.abs(y - row_y) < m_ResizeAreaSize / 2) && m_Model.isRowResizable(row - 1)) {
			return row - 1;
		}

		return -1;

	}

	/**
	 * Returns the number of the column that is present at position x or -1, if
	 * out of area. When cells are spanned, returns the address of the spanned
	 * cell.
	 * 
	 * @param x
	 *            the x location of an event in table coordinates
	 * @param y
	 *            The y location of an event in table coordinates
	 * @return Returns the point where x is the column index, y is the row
	 *         index.
	 */
	public Point calcColumnNum(int x, int y) {
		if (m_Model == null)
			return new Point(-1, -1);

		Point toFind = new Point(x, y);
		Point valid = getValidCell(m_LeftColumn, m_TopRow);

		// at first iterate over the fixed columns, that are always shown:
		Point found = checkIfMatchesInColumns(0, getFixedRowCount(), 0, getFixedColumnCount(), toFind, true);
		if (found != null)
			return found;
		found = checkIfMatchesInColumns(valid.y, m_TopRow + m_RowsVisible, 0, getFixedColumnCount(), toFind, true);
		if (found != null)
			return found;

		found = checkIfMatchesInColumns(0, getFixedRowCount(), valid.x, m_LeftColumn + m_ColumnsVisible, toFind, true);
		if (found != null)
			return found;
		found = checkIfMatchesInColumns(valid.y, m_TopRow + m_RowsVisible, valid.x, m_LeftColumn + m_ColumnsVisible, toFind, true);
		if (found != null)
			return found;

		return new Point(-1, -1);

	}

	protected Point calcNonSpanColumnNum(int x, int y) {
		if (m_Model == null)
			return new Point(-1, -1);

		Point toFind = new Point(x, y);
		Point valid = new Point(m_LeftColumn, m_TopRow);

		// at first iterate over the fixed columns, that are always shown:
		Point found = checkIfMatchesInColumns(0, getFixedRowCount(), 0, getFixedColumnCount(), toFind, false);
		if (found != null)
			return found;
		found = checkIfMatchesInColumns(valid.y, m_TopRow + m_RowsVisible, 0, getFixedColumnCount(), toFind, false);
		if (found != null)
			return found;

		found = checkIfMatchesInColumns(0, getFixedRowCount(), valid.x, m_LeftColumn + m_ColumnsVisible, toFind, false);
		if (found != null)
			return found;
		found = checkIfMatchesInColumns(valid.y, m_TopRow + m_RowsVisible, valid.x, m_LeftColumn + m_ColumnsVisible, toFind, false);
		if (found != null)
			return found;

		return new Point(-1, -1);
	}

	/**
	 * Checks for the event location in table coordinates within the region
	 * covered by the columns beginning by startCol and ending by endCol.
	 * 
	 * @param span
	 *            Set to true if for spanning cells we just want to have the
	 *            left-upper-most cell.
	 */
	protected Point checkIfMatchesInColumns(int startRow, int endRow, int startCol, int endCol, Point toFind, boolean span) {

		for (int row = startRow; row < endRow; row++) {
			for (int col = startCol; col < endCol; col++) {

				Rectangle rect = getCellRectIgnoreSpan(col, row);
				// take into account the 1px right and bottom border
				rect.width += 1;
				rect.height += 1;
				if (rect.contains(toFind))
					if (span)
						// take into account the spanning when reporting a
						// match:
						return getValidCell(col, row);
					else
						return new Point(col, row);
			}
		}
		return null;
	}

	public boolean isCellVisible(int col, int row) {
		checkWidget();
		if (m_Model == null)
			return false;
		return ((col >= m_LeftColumn && col < m_LeftColumn + m_ColumnsVisible && row >= m_TopRow && row < m_TopRow + m_RowsVisible)

		|| (col < getFixedColumnCount() && row < getFixedRowCount()));
	}

	public boolean isCellFullyVisible(int col, int row) {
		checkWidget();
		if (m_Model == null)
			return false;
		return ((col >= m_LeftColumn && col < m_LeftColumn + m_ColumnsFullyVisible && row >= m_TopRow && row < m_TopRow + m_RowsFullyVisible)

		|| (col < getFixedColumnCount() && row < getFixedRowCount()));
	}

	/**
	 * @param row
	 *            The row index AS SEEN BY KTABLE. If you use a sorted model,
	 *            don't forget to map the index.
	 * @return Returns true if the row is visible.
	 */
	public boolean isRowVisible(int row) {
		checkWidget();
		if (m_Model == null)
			return false;
		return ((row >= m_TopRow && row < m_TopRow + m_RowsVisible) || row < (getFixedRowCount()));

	}

	/**
	 * @param row
	 *            the row index to check - as seen by ktable. If you use a
	 *            sorted model, don't forget to map.
	 * @return Returns true if the row is fully visible.
	 */
	public boolean isRowFullyVisible(int row) {
		checkWidget();
		if (m_Model == null)
			return false;
		return ((row >= m_TopRow && row < m_TopRow + m_RowsFullyVisible) || row < getFixedRowCount());
	}

	/*
	 * Focusses the given Cell. Assumes that the given cell is in the viewable
	 * area. Does all neccessary redraws.
	 */
	protected void focusCell(int col, int row, int stateMask) {
		// assure it is a valid cell:
		Point orig = new Point(col, row);
		Point valid = getValidCell(col, row);
		if (valid != null) {
			col = valid.x;
			row = valid.y;
		}

		GC gc = new GC(this);

		// close cell editor if active
		if (m_CellEditor != null)
			m_CellEditor.close(true);

		/*
		 * Special rule: in row selection mode the selection if a fixed cell in
		 * a non-fixed row is allowed and handled as a selection of a non-fixed
		 * cell.
		 */
		if (row >= m_Model.getFixedHeaderRowCount() && (col >= m_Model.getFixedHeaderColumnCount() || isRowSelectMode())) {

			if ((stateMask & SWT.CTRL) == 0 && (stateMask & SWT.SHIFT) == 0) {
				// case: no modifier key
				boolean redrawAll = (m_Selection.size() > 1);
				int oldFocusRow = m_FocusRow;
				int oldFocusCol = m_FocusCol;

				clearSelectionWithoutRedraw();
				addToSelectionWithoutRedraw(col, row);
				m_FocusRow = row;
				m_FocusCol = col;
				m_MainFocusRow = row;
				m_MainFocusCol = col;

				if (redrawAll)
					redraw();
				else if (isRowSelectMode()) {
					drawRow(gc, oldFocusRow);
					drawRow(gc, m_FocusRow);
				} else {
					Rectangle origClipping = null;
					if (!isFixedCell(oldFocusCol, oldFocusRow))
						origClipping = setContentAreaClipping(gc);
					drawCell(gc, oldFocusCol, oldFocusRow);
					if (origClipping != null)
						gc.setClipping(origClipping);

					if (!isFixedCell(m_FocusCol, m_FocusRow))
						origClipping = setContentAreaClipping(gc);
					drawCell(gc, m_FocusCol, m_FocusRow);
				}
				// notify non-fixed cell listeners
				fireCellSelection(orig.x, orig.y, stateMask);
			}

			else if ((stateMask & SWT.CTRL) != 0) {
				// case: CTRL key pressed
				boolean success = toggleSelection(col, row);
				if (success) {
					m_FocusCol = col;
					m_FocusRow = row;
				}

				if (isRowSelectMode())
					drawRow(gc, row);
				else
					drawCell(gc, col, row);
				// notify non-fixed cell listeners
				if (success)
					fireCellSelection(m_FocusCol, m_FocusRow, stateMask);
			}

			else if ((stateMask & SWT.SHIFT) != 0) {
				// Ignore when not a multi-selection table.
				if (!isMultiSelectMode()) {
					if (isRowSelectMode())
						drawRow(gc, row);
					else
						drawCell(gc, col, row);
					return;
				}
				// case: SHIFT key pressed
				if (isRowSelectMode()) {
					HashMap oldSelection = new HashMap(m_Selection);
					if (row < m_FocusRow) {
						// backword selection
						while (row != m_FocusRow)
							addToSelectionWithoutRedraw(0, --m_FocusRow);
					} else {
						// foreward selection
						while (row != m_FocusRow)
							addToSelectionWithoutRedraw(0, ++m_FocusRow);
					}
					if (!oldSelection.equals(m_Selection)) {
						oldSelection.putAll(m_Selection);
						Iterator rowIt = oldSelection.entrySet().iterator();
						int min = 0, max = 0;
						if (rowIt.hasNext()) {
							min = ((Integer) ((Entry) rowIt.next()).getValue()).intValue();
							max = min;
						}
						while (rowIt.hasNext()) {
							int r = ((Integer) ((Entry) rowIt.next()).getValue()).intValue();
							if (r < min)
								min = r;
							if (r > max)
								max = r;
						}
						redraw(0, min, m_Model.getColumnCount(), max - min + 1);

						// notify non-fixed cell listeners
						fireCellSelection(orig.x, orig.y, stateMask);
					}
				} else {// cell selection mode

					Point[] sel = getCellSelection();
					Point min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
					Point max = new Point(-1, -1);
					boolean containsCell = false;
					for (int i = 0; i < sel.length; i++) {
						if (sel[i].x > max.x)
							max.x = sel[i].x;
						if (sel[i].y > max.y)
							max.y = sel[i].y;
						if (sel[i].x < min.x)
							min.x = sel[i].x;
						if (sel[i].y < min.y)
							min.y = sel[i].y;
						if (!containsCell && sel[i].x == col && sel[i].y == row)
							containsCell = true;
					}

					if (col < m_MainFocusCol && max.x > m_MainFocusCol) {
						min.x = col;
						max.x = m_MainFocusCol;
					} else if (col > m_MainFocusCol && min.x < m_MainFocusCol) {
						min.x = m_MainFocusCol;
						max.x = col;
					}
					if (row < m_MainFocusRow && max.y > m_MainFocusRow) {
						min.y = row;
						max.y = m_MainFocusRow;
					} else if (row > m_MainFocusRow && min.y < m_MainFocusRow) {
						min.y = m_MainFocusRow;
						max.y = row;
					}

					HashMap oldSelection = new HashMap(m_Selection);
					if (containsCell) {
						clearSelectionWithoutRedraw();

						if (max.x == m_FocusCol)
							max.x = col;
						if (max.y == m_FocusRow)
							max.y = row;
						if (min.x == m_FocusCol)
							min.x = col;
						if (min.y == m_FocusRow)
							min.y = row;

						// set selection:
						for (int r = min.y; r <= max.y; r++)
							for (int c = min.x; c <= max.x; c++)
								addToSelectionWithoutRedraw(c, r);
						if (!oldSelection.equals(m_Selection)) {
							redraw();
							// notify non-fixed cell listeners
							fireCellSelection(orig.x, orig.y, stateMask);
						}
					} else {

						if (col > max.x)
							max.x = col;
						if (row > max.y)
							max.y = row;
						if (col < min.x)
							min.x = col;
						if (row < min.y)
							min.y = row;

						for (int r = min.y; r <= max.y; r++) {

							for (int c = min.x; c <= max.x; c++) {
								addToSelectionWithoutRedraw(c, r);
							}

						}

						// If this is multi selection mode and the highlight
						// selection header style bit is set then we want
						// to force a redraw of the fixed rows and columns.
						// This makes sure that the highlighting of those
						// fixed rows and columns happens.

						if (isMultiSelectMode() && isHighlightSelectionInHeader()) {
							redraw(-1, row, 1, 1);
							redraw(col, -1, 1, 1);
						}

						if (!oldSelection.equals(m_Selection)) {
							redraw(min.x, min.y, max.x - min.x + 1, max.y - min.y + 1);
							// notify non-fixed cell listeners
							fireCellSelection(orig.x, orig.y, stateMask);
						}
					}

					m_FocusRow = row;
					m_FocusCol = col;

				}
			}

		} else {
			// a fixed cell was focused
			drawCell(gc, col, row);
			// notify fixed cell listeners
			fireFixedCellSelection(orig.x, orig.y, stateMask);
		}

		gc.dispose();
	}

	protected void onMouseDown(MouseEvent e) {
		if (e.button == 1) {
			setCapture(true);
			m_Capture = true;

			// Resize column?
			int columnIndex = getColumnForResize(e.x, e.y);
			if (columnIndex >= 0) {
				m_ResizeColumnIndex = columnIndex;
				m_ResizeColumnLeft = getColumnLeft(columnIndex);
				m_NewColumnSize = m_Model.getColumnWidth(columnIndex);
				return;
			}

			// Resize row?
			int rowIndex = getRowForResize(e.x, e.y);
			if (rowIndex >= 0) {
				m_ResizeRowIndex = rowIndex;
				m_ResizeRowTop = getYforRow(rowIndex);
				m_NewRowSize = m_Model.getRowHeight(rowIndex);
				return;
			}
		}

		if (e.button == 1 || isMultiSelectMode() && !clickInSelectedCells(new Point(e.x, e.y))) {
			// focus change
			Point cell = calcNonSpanColumnNum(e.x, e.y);
			if (cell.x == -1 || cell.y == -1) {
				if (m_CellEditor != null)
					m_CellEditor.close(true);
				return;
			}
			m_ClickColumnIndex = cell.x;
			m_ClickRowIndex = cell.y;

			focusCell(cell.x, cell.y, e.stateMask);
		}
	}

	protected boolean clickInSelectedCells(Point click) {
		Point[] selection = getCellSelection();
		if (selection == null || selection.length < 0)
			return false;
		for (int i = 0; i < selection.length; i++) {
			if (getCellRect(selection[i].x, selection[i].y).contains(click))
				return true;
		}
		return false;
	}

	protected void onMouseMove(MouseEvent e) {
		if (m_Model == null)
			return;

		// show resize cursor?
		if ((m_ResizeColumnIndex != -1) || (getColumnForResize(e.x, e.y) >= 0)) {

			if (m_defaultColumnResizeCursor == null) {
				setCursor(new Cursor(m_Display, SWT.CURSOR_SIZEWE));
			} else {
				setCursor(m_defaultColumnResizeCursor);
			}

		} else if ((m_ResizeRowIndex != -1) || (getRowForResize(e.x, e.y) >= 0)) {

			if (m_defaultRowResizeCursor == null) {
				setCursor(new Cursor(m_Display, SWT.CURSOR_SIZENS));
			} else {
				setCursor(m_defaultRowResizeCursor);
			}

		} else { // show default cursor:
			setCursor(m_defaultCursor);
		}

		if (e.stateMask == SWT.BUTTON1 && m_CellEditor == null) {
			// extend selection?
			if (m_ClickColumnIndex != -1 && isMultiSelectMode()) {

				Point cell = calcNonSpanColumnNum(e.x, e.y);
				if (cell.x == -1 || cell.y == -1)
					return;
				if (cell.y >= m_Model.getFixedHeaderRowCount() && cell.x >= m_Model.getFixedHeaderColumnCount()) {

					m_ClickColumnIndex = cell.x;
					m_ClickRowIndex = cell.y;

					focusCell(cell.x, cell.y, (e.stateMask | SWT.SHIFT));
				}
			}

		}
		// column resize?
		if (m_ResizeColumnIndex != -1) {

			Rectangle rect = getClientArea();

			if (e.x > rect.x + rect.width - 1) {
				e.x = rect.x + rect.width - 1;
			}

			int newSize = e.x - m_ResizeColumnLeft;

			if (newSize < 5) {
				newSize = 5;
			}

			m_NewColumnSize = newSize;

			GC gc = new GC(this);

			// restore old line area
			if (m_LineRestore != null) {
				gc.drawImage(m_LineRestore, m_LineX, m_LineY);
			}

			// safe old picture and draw line
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));

			int lineEnd = getRowBottom(m_TopRow + m_RowsVisible - 1);

			m_LineRestore = new Image(m_Display, 1, lineEnd);
			m_LineY = rect.y + 1;
			m_LineX = m_ResizeColumnLeft + m_NewColumnSize - 1;

			gc.copyArea(m_LineRestore, m_LineX, m_LineY);
			gc.drawLine(m_LineX, m_LineY, m_LineX, lineEnd);
			gc.dispose();

		}

		// row resize?
		if (m_ResizeRowIndex != -1) {

			Rectangle rect = getClientArea();
			GC gc = new GC(this);

			// calculate new size
			if (e.y > rect.y + rect.height - 1)
				e.y = rect.y + rect.height - 1;
			m_NewRowSize = e.y - m_ResizeRowTop;
			if (m_NewRowSize < m_Model.getRowHeightMinimum())
				m_NewRowSize = m_Model.getRowHeightMinimum();

			// restore old line area
			if (m_LineRestore != null) {
				gc.drawImage(m_LineRestore, m_LineX, m_LineY);
			}

			// safe old picture and draw line
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			int lineEnd = getColumnRight(m_LeftColumn + m_ColumnsVisible - 1);
			m_LineRestore = new Image(m_Display, lineEnd, 1);
			m_LineX = rect.x + 1;
			m_LineY = m_ResizeRowTop + m_NewRowSize - 1;
			gc.copyArea(m_LineRestore, m_LineX, m_LineY);
			gc.drawLine(m_LineX, m_LineY, rect.x + lineEnd, m_LineY);
			gc.dispose();
		}
	}

	protected void onMouseUp(MouseEvent e) {
		// if (e.button == 1)
		{
			if (m_Model == null)
				return;

			setCapture(false);
			m_Capture = false;

			// do resize:

			if (m_ResizeColumnIndex != -1 || m_ResizeRowIndex != -1) {

				// Do Resize Column

				if (m_ResizeColumnIndex != -1) {

					int column = m_ResizeColumnIndex;
					m_ResizeColumnIndex = -1;

					m_Model.setColumnWidth(column, m_NewColumnSize);

					fireColumnResize(column, m_Model.getColumnWidth(column));
					redraw();

				}

				// Do Resize Row

				if (m_ResizeRowIndex != -1) {

					int row = m_ResizeRowIndex;
					m_ResizeRowIndex = -1;

					m_Model.setRowHeight(row, m_NewRowSize);

					m_LineRestore = null;

					fireRowResize(row, m_NewRowSize);
					redraw();

				}

			} else { // check if we have to edit:
				Point click = new Point(e.x, e.y);
				Point cell = getCellForCoordinates(e.x, e.y);
				if (isHeaderCell(cell.x, cell.y)) {
					KTableCellEditor editor = m_Model.getCellEditor(cell.x, cell.y);
					if (editor != null && ((editor.getActivationSignals() & KTableCellEditor.SINGLECLICK) != 0)
							&& editor.isApplicable(KTableCellEditor.SINGLECLICK, this, cell.x, cell.y, click, null, e.stateMask)) {
						int oldFocusCol = m_FocusCol;
						int oldFocusRow = m_FocusRow;
						m_FocusCol = cell.x;
						m_FocusRow = cell.y;
						openEditorInFocus();
						m_FocusCol = oldFocusCol;
						m_FocusRow = oldFocusRow;
					}
				} else {

					KTableCellEditor editor = m_Model.getCellEditor(m_FocusCol, m_FocusRow);
					Rectangle rect = getCellRect(m_FocusCol, m_FocusRow);
					if (editor != null && rect.contains(click) && (editor.getActivationSignals() & KTableCellEditor.SINGLECLICK) != 0
							&& editor.isApplicable(KTableCellEditor.SINGLECLICK, this, m_FocusCol, m_FocusRow, click, null, e.stateMask)) {
						openEditorInFocus();
					}
				}
			}

			if (m_ClickColumnIndex != -1) {
				int col = m_ClickColumnIndex;
				int row = m_ClickRowIndex;
				m_ClickColumnIndex = -1;
				m_ClickRowIndex = -1;
				if (m_CellEditor == null) {
					GC gc = new GC(this);
					if (!isFixedCell(col, row) && !isRowSelectMode())
						setContentAreaClipping(gc);
					Point valid = getValidCell(col, row);
					drawCell(gc, valid.x, valid.y);
					gc.dispose();
				}
			}
		}
	}

	/**
	 * Finds and returns the cell that is below the control coordinates.
	 * 
	 * @param x
	 *            The x coordinate in the ktable control
	 * @param y
	 *            The y coordinate in the ktable control
	 * @return Returns the cell under the given point, or (-1, -1).
	 * @since Public since March 2006 based on a newsgroup request.
	 * @throws IllegalArgumentException
	 *             If there is something wrong with the spanning values given in
	 *             the model.
	 */
	public Point getCellForCoordinates(int x, int y) {
		Point cell = new Point(0, 0);
		cell.y = getRowForY(y);

		// if (y>m_Model.getRowHeight(0))
		// cell.y=(y-m_Model.getFirstRowHeight())/(m_Model.getRowHeight());
		// cell.y+=m_TopRow;

		int width = 0;
		for (int i = 0; width < x; i++) {
			if (i >= m_Model.getColumnCount())
				return new Point(-1, -1);
			width += getColumnWidth(i);
			if (x < width)
				cell.x = i;

		}
		return getValidCell(cell.x, cell.y);
	}

	protected void onKeyDown(KeyEvent e) {
		boolean focusChanged = false;
		int newFocusRow = m_FocusRow;
		int newFocusCol = m_FocusCol;

		if (m_Model == null)
			return;

		if ((e.character == ' ') || (e.character == '\r')) {
			KTableCellEditor editor = m_Model.getCellEditor(m_FocusCol, m_FocusRow);
			if (editor != null && (editor.getActivationSignals() & KTableCellEditor.KEY_RETURN_AND_SPACE) != 0
					&& editor.isApplicable(KTableCellEditor.KEY_RETURN_AND_SPACE, this, m_FocusCol, m_FocusRow, null, e.character + "", e.stateMask)) {
				openEditorInFocus();
				return;
			}
		}

		if (e.keyCode == SWT.HOME) {
			newFocusCol = m_Model.getFixedHeaderColumnCount();
			if (newFocusRow == -1)
				newFocusRow = m_Model.getFixedHeaderRowCount();
			focusChanged = true;
		} else if (e.keyCode == SWT.END) {
			newFocusCol = m_Model.getColumnCount() - 1;
			if (newFocusRow == -1)
				newFocusRow = m_Model.getFixedHeaderRowCount();
			focusChanged = true;
		} else if (e.keyCode == SWT.ARROW_LEFT || (e.keyCode == SWT.TAB && (e.stateMask & SWT.SHIFT) != 0)) {
			if (!isRowSelectMode()) {
				if (newFocusCol > m_Model.getFixedHeaderColumnCount()) {
					Point current = m_Model.belongsToCell(m_FocusCol, m_FocusRow);
					if (current == null)
						current = new Point(m_FocusCol, m_FocusRow);
					Point newPt = m_Model.belongsToCell(current.x - 1, current.y);
					if (newPt == null)
						newPt = new Point(current.x - 1, current.y);
					newFocusCol = newPt.x;
					newFocusRow = newPt.y;
				} else if (newFocusCol == m_Model.getFixedHeaderColumnCount() && newFocusRow >= m_Model.getFixedHeaderRowCount() && e.keyCode == SWT.TAB) {
					// wrap arount when traversing:
					newFocusCol = m_Model.getColumnCount() - 1;
					newFocusRow--;
				}
			}
			focusChanged = true;
		} else if (e.keyCode == SWT.ARROW_RIGHT || (e.keyCode == SWT.TAB && (e.stateMask & SWT.SHIFT) == 0)) {
			if (!isRowSelectMode()) {
				if (newFocusCol == -1) {
					newFocusCol = m_Model.getFixedHeaderColumnCount();
					newFocusRow = m_Model.getFixedHeaderRowCount();
				} else if (newFocusCol < m_Model.getColumnCount() - 1) {
					Point old = new Point(m_FocusCol, m_FocusRow);
					newFocusCol++;
					Point next = m_Model.belongsToCell(newFocusCol, newFocusRow);
					if (next == null)
						next = new Point(newFocusCol, newFocusRow);
					while (next.equals(old) && newFocusCol < m_Model.getColumnCount() - 1) {
						newFocusCol++;
						next = m_Model.belongsToCell(newFocusCol, newFocusRow);
						if (next == null)
							next = new Point(newFocusCol, newFocusRow);
					}
					newFocusCol = next.x;
				} else if (m_FocusCol == m_Model.getColumnCount() - 1 && m_FocusRow != m_Model.getRowCount() - 1 && e.keyCode == SWT.TAB) {
					newFocusCol = m_Model.getFixedHeaderColumnCount();
					newFocusRow++;
				}
			}
			focusChanged = true;
		} else if (e.keyCode == SWT.ARROW_DOWN) {
			if (newFocusRow == -1) {
				newFocusRow = m_Model.getFixedHeaderRowCount();
				newFocusCol = m_Model.getFixedHeaderColumnCount();
			} else if (newFocusRow < m_Model.getRowCount() - 1) {
				Point old = new Point(m_FocusCol, m_FocusRow);
				newFocusRow++;
				Point next = m_Model.belongsToCell(newFocusCol, newFocusRow);
				if (next == null)
					next = new Point(newFocusCol, newFocusRow);
				while (next.equals(old)) {
					newFocusRow++;
					next = m_Model.belongsToCell(newFocusCol, newFocusRow);
					if (next == null)
						next = new Point(newFocusCol, newFocusRow);
				}
			}
			focusChanged = true;
		} else if (e.keyCode == SWT.ARROW_UP) {
			if (newFocusRow > m_Model.getFixedHeaderRowCount()) {
				Point current = m_Model.belongsToCell(m_FocusCol, m_FocusRow);
				if (current == null)
					current = new Point(m_FocusCol, m_FocusRow);
				newFocusCol = current.x;
				newFocusRow = current.y - 1;
			}
			focusChanged = true;
		} else if (e.keyCode == SWT.PAGE_DOWN) {
			newFocusRow += m_RowsVisible - 1;
			if (newFocusRow >= m_Model.getRowCount())
				newFocusRow = m_Model.getRowCount() - 1;
			if (newFocusCol == -1)
				newFocusCol = m_Model.getFixedHeaderColumnCount();
			focusChanged = true;
		} else if (e.keyCode == SWT.PAGE_UP) {
			newFocusRow -= m_RowsVisible - 1;
			if (newFocusRow < m_Model.getFixedHeaderRowCount())
				newFocusRow = m_Model.getFixedHeaderRowCount();
			if (newFocusCol == -1)
				newFocusCol = m_Model.getFixedHeaderColumnCount();
			focusChanged = true;
		} else if (isEditOnKeyEvent()) {
			// check if a key was pressed that forces editing:
			if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
				KTableCellEditor editor = m_Model.getCellEditor(m_FocusCol, m_FocusRow);
				if (editor != null && (editor.getActivationSignals() & KTableCellEditor.KEY_ANY) != 0
						&& editor.isApplicable(KTableCellEditor.KEY_ANY, this, m_FocusCol, m_FocusRow, null, e.character + "", e.stateMask)) {
					openEditorInFocus();
					if (m_CellEditor != null) {
						m_CellEditor.setContent("");
					}
				}
				return;
			} else if ((Character.isLetterOrDigit(e.character) || e.keyCode > 32 && e.keyCode < 254 && e.keyCode != 127) && e.keyCode != SWT.CTRL && e.keyCode != SWT.ALT
					&& (e.stateMask & SWT.CONTROL) == 0 && (e.stateMask & SWT.ALT) == 0) {

				KTableCellEditor editor = m_Model.getCellEditor(m_FocusCol, m_FocusRow);
				if (editor != null && (editor.getActivationSignals() & KTableCellEditor.KEY_ANY) != 0
						&& editor.isApplicable(KTableCellEditor.KEY_ANY, this, m_FocusCol, m_FocusRow, null, e.character + "", e.stateMask)) {
					openEditorInFocus();
					if (m_CellEditor != null) {
						m_CellEditor.setContent(e.character + "");
					}
					return;
				}
			}
		}

		if (focusChanged) {
			// make sure it is a valid, visible cell and not overlapped:
			Point valid = m_Model.belongsToCell(newFocusCol, newFocusRow);
			if (valid != null) {
				newFocusCol = valid.x;
				newFocusRow = valid.y;
			}
			focusCell(newFocusCol, newFocusRow, e.stateMask);
			if (!isCellFullyVisible(m_FocusCol, m_FocusRow))
				scrollToFocus();
		}
	}

	protected void onMouseDoubleClick(MouseEvent e) {
		if (m_Model == null)
			return;
		if (e.button == 1) {

			if (e.y < getHeaderHeight()) {
				// double click in header area
				int columnIndex = getColumnForResize(e.x, e.y);
				resizeColumnOptimal(columnIndex);
				m_ResizeColumnIndex = -1;
				Point cell = getCellForCoordinates(e.x, e.y);
				fireFixedCellDoubleClicked(cell.x, cell.y, e.stateMask);
				return;
			} else {
				Point click = new Point(e.x, e.y);
				Point cell = getCellForCoordinates(e.x, e.y);
				if (isHeaderCell(cell.x, cell.y)) {
					KTableCellEditor editor = m_Model.getCellEditor(cell.x, cell.y);
					if (editor != null && (editor.getActivationSignals() & KTableCellEditor.DOUBLECLICK) != 0
							&& editor.isApplicable(KTableCellEditor.DOUBLECLICK, this, cell.x, cell.y, click, null, e.stateMask)) {
						int oldFocusCol = m_FocusCol;
						int oldFocusRow = m_FocusRow;
						m_FocusCol = cell.x;
						m_FocusRow = cell.y;
						openEditorInFocus();
						m_FocusCol = oldFocusCol;
						m_FocusRow = oldFocusRow;
					}
					fireFixedCellDoubleClicked(cell.x, cell.y, e.stateMask);
				} else {
					KTableCellEditor editor = m_Model.getCellEditor(m_FocusCol, m_FocusRow);
					Rectangle rect = getCellRect(m_FocusCol, m_FocusRow);
					if (editor != null && rect.contains(click) && (editor.getActivationSignals() & KTableCellEditor.DOUBLECLICK) != 0
							&& editor.isApplicable(KTableCellEditor.DOUBLECLICK, this, m_FocusCol, m_FocusRow, click, null, e.stateMask))
						openEditorInFocus();
					fireCellDoubleClicked(cell.x, cell.y, e.stateMask);
				}
			}
		}
	}

	/**
	 * Listener Class that implements fake tooltips. The tooltip content is
	 * retrieved from the tablemodel.
	 */
	class TooltipListener implements Listener {
		Shell tip = null;
		Label label = null;

		final Listener labelListener = new Listener() {
			public void handleEvent(Event event) {
				Label label = (Label) event.widget;
				Shell shell = label.getShell();
				// forward mouse events directly to the underlying KTable
				switch (event.type) {
				case SWT.MouseDown:
					Event e = new Event();
					e.item = KTable.this;
					e.button = event.button;
					e.stateMask = event.stateMask;
					notifyListeners(SWT.MouseDown, e);
					// fall through
				default:
					shell.dispose();
					break;
				}
			}
		};

		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.Dispose:
			case SWT.KeyDown:
			case SWT.MouseDown:
			case SWT.MouseDoubleClick:
			case SWT.MouseMove:
			case SWT.Selection: // scrolling
			case SWT.MouseExit: {
				if (tip == null)
					break;
				tip.dispose();
				tip = null;
				label = null;
				break;
			}
			case SWT.MouseHover: {
				if (tip != null && !tip.isDisposed())
					tip.dispose();

				Point cell = calcColumnNum(event.x, event.y);
				String tooltip = m_Model.getTooltipAt(cell.x, cell.y);

				// check if there is something to show, and abort otherwise:
				if (((tooltip == null || tooltip.equals("")) && (m_nativTooltip == null || m_nativTooltip.equals(""))) || (cell == null || cell.x == -1 || cell.y == -1)) {
					tip = null;
					label = null;
					return;
				}

				tip = new Shell(getShell(), SWT.ON_TOP);
				GridLayout gl = new GridLayout();
				gl.marginWidth = 2;
				gl.marginHeight = 2;
				tip.setLayout(gl);
				tip.setBackground(getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				label = new Label(tip, SWT.NONE);
				label.setLayoutData(new GridData(GridData.FILL_BOTH));
				label.setForeground(getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
				label.setBackground(getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				if (tooltip != null || !tooltip.equals(""))
					label.setText(tooltip);
				else
					label.setText(m_nativTooltip);
				label.addListener(SWT.MouseExit, labelListener);
				label.addListener(SWT.MouseDown, labelListener);
				label.addListener(SWT.MouseMove, labelListener);
				Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);

				// TODO: Correctly position the tooltip below the cursor.
				int y = 20; // currently the windows default???
				int x = 0;
				if (m_defaultCursorSize != null && m_defaultCursorSize.x >= 0 && m_defaultCursorSize.y >= 0) {
					y = m_defaultCursorSize.y + 1;
					x = -m_defaultCursorSize.x;
				}
				// place the shell under the mouse, but check that the
				// bounds of the table are not overlapped.
				Rectangle tableBounds = KTable.this.getBounds();
				if (event.x + x + size.x > tableBounds.x + tableBounds.width)
					event.x -= event.x + x + size.x - tableBounds.x - tableBounds.width;
				if (event.y + y + size.y > tableBounds.y + tableBounds.height)
					event.y -= event.y + y + size.y - tableBounds.y - tableBounds.height;

				Point pt = toDisplay(event.x + x, event.y + y);
				tip.setBounds(pt.x, pt.y, size.x, size.y);
				tip.setVisible(true);
			}
			}
		}
	}

	/**
	 * Sets the global tooltip for the whole table.<br>
	 * Note that this is only shown if the cell has no tooltip set. For tooltips
	 * on cell level (that overwrite this value), look for the method
	 * <code>getTooltipText()</code>.
	 * 
	 * @see de.kupzog.ktable.KTableModel#getTooltipAt(int, int)
	 * @see de.kupzog.ktable.KTable#getToolTipText()
	 * @param tooltip
	 *            The global tooltip for the table.
	 */
	public void setToolTipText(String tooltip) {
		m_nativTooltip = tooltip;
	}

	/**
	 * Returns the global tooltip for the whole table.<br>
	 * Note that this is not shown when there is a non-empty tooltip for the
	 * cell.
	 * 
	 * @see de.kupzog.ktable.KTable#setToolTipText(String)
	 * @see de.kupzog.ktable.KTableModel#getTooltipAt(int, int)
	 */
	public String getToolTipText() {
		return m_nativTooltip;
	}

	/**
	 * Resizes the given column to its optimal width.
	 * 
	 * Is also called if user doubleclicks in the resize area of a resizable
	 * column.
	 * 
	 * The optimal width is determined by asking the CellRenderers for the
	 * visible cells of the column for the optimal with and taking the minimum
	 * of the results. Note that the optimal width is only determined for the
	 * visible area of the table because otherwise this could take very long
	 * time.
	 * 
	 * @param column
	 *            The column to resize
	 * @return int The optimal with that was determined or -1, if column out of
	 *         range.
	 */
	public int resizeColumnOptimal(int column) {
		checkWidget();
		if (column >= 0 && column < m_Model.getColumnCount()) {
			int optWidth = 5;
			GC gc = new GC(this);
			for (int i = 0; i < m_Model.getFixedHeaderRowCount(); i++) {
				int width = m_Model.getCellRenderer(column, i).getOptimalWidth(gc, column, i, m_Model.getContentAt(column, i), true, m_Model);
				if (width > optWidth)
					optWidth = width;
			}
			for (int i = m_TopRow; i < m_TopRow + m_RowsVisible; i++) {
				int width = m_Model.getCellRenderer(column, i).getOptimalWidth(gc, column, i, m_Model.getContentAt(column, i), true, m_Model);
				if (width > optWidth)
					optWidth = width;
			}
			gc.dispose();
			m_Model.setColumnWidth(column, optWidth);
			redraw();
			return optWidth;
		}
		return -1;
	}

	/**
	 * This method activated the cell editor on the current focus cell, if the
	 * table model allows cell editing for this cell.
	 */
	public void openEditorInFocus() {
		checkWidget();
		m_CellEditor = m_Model.getCellEditor(m_FocusCol, m_FocusRow);
		if (m_CellEditor != null) {
			scrollToFocus();
			Rectangle r = getCellRect(m_FocusCol, m_FocusRow);
			m_CellEditor.open(this, m_FocusCol, m_FocusRow, r);
		}
	}

	/**
	 * Scrolls the table so that the given cell is top left.
	 * 
	 * @param col
	 *            The column index.
	 * @param row
	 *            The row index.
	 */
	public void scroll(int col, int row) {
		if (col < 0 || col >= m_Model.getColumnCount() || row < 0 || row >= m_Model.getRowCount())
			return;

		m_TopRow = row;
		m_LeftColumn = col;
		redraw();
	}

	protected void scrollToFocus() {
		boolean change = false;

		// vertical scroll allowed?
		if (getVerticalBar() != null) {
			if (m_FocusRow < m_TopRow && !(m_FocusRow < getFixedRowCount())) {
				m_TopRow = m_FocusRow;
				change = true;
			}

			if (m_FocusRow >= m_TopRow + m_RowsFullyVisible) {
				m_TopRow = m_FocusRow - m_RowsFullyVisible + 1;
				change = true;
			}
		}

		// horizontal scroll allowed?
		if (getHorizontalBar() != null) {
			if (m_FocusCol < m_LeftColumn && !(m_FocusCol < getFixedColumnCount())) {
				m_LeftColumn = m_FocusCol;
				change = true;
			}

			if (m_FocusCol >= m_LeftColumn + m_ColumnsFullyVisible) {
				int oldLeftCol = m_LeftColumn;
				Rectangle rect = getClientArea();
				int leftColX = getColumnLeft(m_FocusCol);
				int focusCellWidth = getCellRect(m_FocusCol, m_FocusRow).width;
				while (m_LeftColumn < m_FocusCol && (leftColX < 0 || leftColX + focusCellWidth > rect.width + rect.x)) {
					m_LeftColumn++;
					leftColX = getColumnLeft(m_FocusCol);
				}
				change |= (oldLeftCol != m_LeftColumn);
			}
		}

		if (change)
			redraw();
	}

	protected void fireCellSelection(int col, int row, int statemask) {
		for (int i = 0; i < cellSelectionListeners.size(); i++) {
			((KTableCellSelectionListener) cellSelectionListeners.get(i)).cellSelected(col, row, statemask);
		}
	}

	protected void fireCellDoubleClicked(int col, int row, int statemask) {
		for (int i = 0; i < cellDoubleClickListeners.size(); i++) {
			((KTableCellDoubleClickListener) cellDoubleClickListeners.get(i)).cellDoubleClicked(col, row, statemask);
		}
	}

	protected void fireFixedCellDoubleClicked(int col, int row, int statemask) {
		for (int i = 0; i < cellDoubleClickListeners.size(); i++) {
			((KTableCellDoubleClickListener) cellDoubleClickListeners.get(i)).fixedCellDoubleClicked(col, row, statemask);
		}
	}

	protected void fireFixedCellSelection(int col, int row, int statemask) {
		for (int i = 0; i < cellSelectionListeners.size(); i++) {
			((KTableCellSelectionListener) cellSelectionListeners.get(i)).fixedCellSelected(col, row, statemask);
		}
	}

	protected void fireColumnResize(int col, int newSize) {
		for (int i = 0; i < cellResizeListeners.size(); i++) {
			((KTableCellResizeListener) cellResizeListeners.get(i)).columnResized(col, newSize);
		}
	}

	protected void fireRowResize(int row, int newSize) {
		for (int i = 0; i < cellResizeListeners.size(); i++) {
			((KTableCellResizeListener) cellResizeListeners.get(i)).rowResized(row, newSize);
		}
	}

	/**
	 * Adds a listener that is notified when a cell is selected.
	 * 
	 * This can happen either by a click on the cell or by arrow keys. Note that
	 * the listener is not called for each cell that the user selects in one
	 * action using Shift+Click. To get all these cells use the listener and
	 * getCellSelecion() or getRowSelection().
	 * 
	 * @param listener
	 */
	public void addCellSelectionListener(KTableCellSelectionListener listener) {
		cellSelectionListeners.add(listener);
	}

	/**
	 * Adds a listener that is notified when a cell is resized. This happens
	 * when the mouse button is released after a resizing.
	 * 
	 * @param listener
	 */
	public void addCellResizeListener(KTableCellResizeListener listener) {
		cellResizeListeners.add(listener);
	}

	/**
	 * Adds a listener that is notified when a cell is doubleClicked.
	 * 
	 * @param listener
	 */
	public void addCellDoubleClickListener(KTableCellDoubleClickListener listener) {
		cellDoubleClickListeners.add(listener);
	}

	/**
	 * Removes the listener if present. Returns true, if found and removed from
	 * the list of listeners.
	 */
	public boolean removeCellSelectionListener(KTableCellSelectionListener listener) {
		return cellSelectionListeners.remove(listener);
	}

	/**
	 * Removes the listener if present. Returns true, if found and removed from
	 * the list of listeners.
	 */
	public boolean removeCellResizeListener(KTableCellResizeListener listener) {
		return cellResizeListeners.remove(listener);
	}

	/**
	 * Removes the listener if present. Returns true, if found and removed from
	 * the list of listeners.
	 */
	public boolean removeDoubleClickListener(KTableCellDoubleClickListener listener) {
		return cellDoubleClickListeners.remove(listener);
	}

	// ////////////////////////////////////////////////////////////////////////////
	// SELECTION
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns true if in "Full Selection Mode". Mode is determined by style
	 * bits in the constructor or by <code>setStyle()</code>. (style:
	 * SWT.FULL_SELECTION)
	 * 
	 * @return boolean
	 */
	public boolean isRowSelectMode() {
		return (getStyle() & SWT.FULL_SELECTION) == SWT.FULL_SELECTION;
	}

	/**
	 * Returns true if in "Multi Selection Mode". Mode is determined by style
	 * bits in the constructor (SWT.MULTI) or by <code>getStyle()</code>.
	 */
	public boolean isMultiSelectMode() {
		return (getStyle() & SWT.MULTI) == SWT.MULTI;
	}

	/**
	 * @return Returns true if the selection is kept & shown even if the KTable
	 *         has no focus.
	 * @see #setShowSelectionWithoutFocus(boolean)
	 */
	protected boolean isShowSelectionWithoutFocus() {
		return (getStyle() & SWT.HIDE_SELECTION) != SWT.HIDE_SELECTION;
	}

	/**
	 * @return Returns true if selections are also shown in the vertical and
	 *         horizontal table headers.
	 * @see #setHighlightSelectionInHeader(boolean)
	 */
	protected boolean isHighlightSelectionInHeader() {
		return (getStyle() & SWTX.MARK_FOCUS_HEADERS) == SWTX.MARK_FOCUS_HEADERS;
	}

	/**
	 * @return Returns wether the celleditor is activated on a (every) keystroke -
	 *         like in Excel - and not only by ENTER. Activated by style
	 *         <code>SWTX.EDIT_ON_KEY</code>
	 *         <p>
	 *         However, note that every cell editor can specify the event types
	 *         it accepts.
	 * @see #setEditOnKeyEvent(boolean)
	 */
	protected boolean isEditOnKeyEvent() {
		return (getStyle() & SWTX.EDIT_ON_KEY) == SWTX.EDIT_ON_KEY;
	}

	protected void clearSelectionWithoutRedraw() {
		m_Selection.clear();
	}

	/**
	 * Clears the current selection (in all selection modes).
	 */
	public void clearSelection() {
		checkWidget();
		clearSelectionWithoutRedraw();
		m_FocusCol = -1;
		m_FocusRow = -1;
		if (isMultiSelectMode())
			redraw();
	}

	/**
	 * Works in both modes: Cell and Row Selection. Has no redraw functionality!
	 * <p>
	 * 
	 * Returns true, if added to selection.
	 */
	protected boolean toggleSelection(int col, int row) {

		if (isMultiSelectMode()) {
			Object o;
			if (isRowSelectMode()) {
				o = new Integer(row);
			} else {
				o = new Point(col, row);
			}
			if (m_Selection.get(o) != null) {
				m_Selection.remove(o);
				return false;
			} else {
				m_Selection.put(o, o);
				return true;
			}
		}
		return false;
	}

	/**
	 * Works in both modes: Cell and Row Selection. Has no redraw functionality!
	 */
	protected void addToSelectionWithoutRedraw(int col, int row) {
		if (isMultiSelectMode()) {
			if (isRowSelectMode()) {
				Integer o = new Integer(row);
				m_Selection.put(o, o);
			} else {
				Point o = new Point(col, row);
				m_Selection.put(o, o);
			}
		}
	}

	/**
	 * Selects the given cell. If scroll is true, it scrolls to show this cell
	 * if neccessary. In Row Selection Mode, the given row is selected and a
	 * scroll to the given column is done. Does nothing if the cell does not
	 * exist.
	 * <p>
	 * Note that if you use a sorted model, don't forget to map the row index!
	 * 
	 * @param col
	 * @param row
	 * @param scroll
	 */
	public void setSelection(int col, int row, boolean scroll) {
		checkWidget();
		if (col < m_Model.getColumnCount() && col >= m_Model.getFixedHeaderColumnCount() && row < m_Model.getRowCount() && row >= m_Model.getFixedHeaderRowCount()) {
			focusCell(col, row, 0);
			if (scroll) {
				scrollToFocus();
			}
		}
	}

	/**
	 * Selects the given cells - Point.x is column index, Point.y is row index.
	 * If scroll is true, scrolls so that the first of the given cell indexes
	 * becomes fully visible.
	 * <p>
	 * Don't forget to map the row index if a sorted model is used.
	 * 
	 * @param selections
	 *            The selected cells as points. If <code>null</code> or an
	 *            empty array, clears the selection.
	 * @param scroll
	 *            Wether it is reuqested to scroll to the first cell given as
	 *            selection.
	 * @see KTableSortedModel#mapRowIndexToTable(int)
	 */
	public void setSelection(Point[] selections, boolean scroll) {
		checkWidget();
		if (selections == null || selections.length < 1) {
			clearSelection();
			return;

		} else if (isMultiSelectMode()) {
			try {
				this.setRedraw(false);
				for (int i = 0; i < selections.length; i++) {
					int col = selections[i].x;
					int row = selections[i].y;
					if (col < m_Model.getColumnCount() && col >= m_Model.getFixedHeaderColumnCount() && row < m_Model.getRowCount() && row >= m_Model.getFixedHeaderRowCount())
						if (i == 0)
							focusCell(col, row, SWT.CTRL);
						else
							addToSelectionWithoutRedraw(col, row);
				}

				if (scroll)
					scrollToFocus();
			} finally {
				this.setRedraw(true);
			}
		} else {
			setSelection(selections[0].x, selections[0].y, scroll);
		}
	}

	/**
	 * Returns true, if the given cell is selected. Works also in Row Selection
	 * Mode.
	 * 
	 * @param col
	 *            the column index.
	 * @param row
	 *            the row index.
	 * @return boolean Returns true if the given cell is selected.
	 */
	public boolean isCellSelected(int col, int row) {
		checkWidget();
		Point v = getValidCell(col, row);
		col = v.x;
		row = v.y;
		if (!isMultiSelectMode()) {
			if (isRowSelectMode())
				return (row == m_FocusRow);
			return (col == m_FocusCol && row == m_FocusRow);
		}

		if (isRowSelectMode())
			return (m_Selection.get(new Integer(row)) != null);
		else
			return (m_Selection.get(new Point(col, row)) != null);
	}

	/**
	 * Returns true, if the given row is selected. Returns always false if not
	 * in Row Selection Mode!
	 * <p>
	 * If you use a sorted model, don't forget to map the row index first.
	 * 
	 * @param row
	 *            The row index as seen by the KTable.
	 * @return boolean returns true if the row is selected at the moment.
	 */
	public boolean isRowSelected(int row) {
		return (m_Selection.get(new Integer(row)) != null);
	}

	/**
	 * Returns an array of the selected row numbers. Returns null if not in Row
	 * Selection Mode. Returns an array with one or none element if not in Multi
	 * Selection Mode.
	 * <p>
	 * NOTE: This returns the cell indices as seen by the KTable. If you use a
	 * sorting model, don't forget to map the indices properly.
	 * 
	 * @return int[] Returns an array of rows that are selected.
	 * @see KTableSortedModel#mapRowIndexToModel(int)
	 */
	public int[] getRowSelection() {
		checkWidget();
		if (!isRowSelectMode())
			return null;
		if (!isMultiSelectMode()) {
			if (m_FocusRow < 0)
				return new int[0];
			int[] tmp = new int[1];
			tmp[0] = m_FocusRow;
			return tmp;
		}

		Object[] ints = m_Selection.values().toArray();
		int[] erg = new int[ints.length];

		for (int i = 0; i < erg.length; i++) {
			erg[i] = ((Integer) ints[i]).intValue();
		}
		return erg;
	}

	/**
	 * Returns an array of the selected cells as Point[]. The columns are stored
	 * in the x fields, rows in y fields. Returns null if in Row Selection Mode.
	 * <br>
	 * Returns an array with one or none element if not in Multi Selection Mode.
	 * <p>
	 * NOTE: This returns the cell indices as seen by the KTable. If you use a
	 * sorting model, don't forget to map the indices properly.
	 * 
	 * @return int[] Returns an array of points that are selected. These are no
	 *         copies, so don't directly manipulate them...
	 * 
	 * @see KTableSortedModel#mapRowIndexToModel(int)
	 */
	public Point[] getCellSelection() {
		checkWidget();
		if (isRowSelectMode())
			return null;
		if (!isMultiSelectMode()) {
			if (m_FocusRow < 0 || m_FocusCol < 0)
				return new Point[0];
			Point[] tmp = new Point[1];
			tmp[0] = new Point(m_FocusCol, m_FocusRow);
			return tmp;
		}

		return (Point[]) m_Selection.values().toArray(new Point[] {});
	}

	/**
	 * @return Returns the non-fixed cells that are currently visible. The
	 *         returned rectangle has the columns on the x / width coordinates
	 *         and the rows at the y / height coordinates.
	 */
	public Rectangle getVisibleCells() {
		return new Rectangle(m_LeftColumn, m_TopRow, m_ColumnsVisible, m_RowsVisible);
	}

	/**
	 * Internal helper method to determine wether the cell at the given position
	 * is to be highlighted because it is a header cell that corresponds to a
	 * selected cell.
	 * 
	 * @param col
	 *            The column index
	 * @param row
	 *            The row index
	 * @return true if the cell should be highlighted.
	 */
	private boolean highlightSelectedRowCol(int col, int row) {
		if (!isHighlightSelectionInHeader() || !isHeaderCell(col, row))
			return false;

		Point[] sel = getCellSelection();
		if (sel != null) {
			for (int i = 0; i < sel.length; i++) {
				if (sel[i].x == col || sel[i].y == row)
					return true;
				Point valid = getValidCell(sel[i].x, row);
				if (valid.x == col)
					return true;
				valid = getValidCell(col, sel[i].y);
				if (valid.y == row)
					return true;
			}
		}
		return false;
	}

	// ////////////////////////////////////////////////////////////////////////////
	// MODEL
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the table model. The table model provides data to the table.
	 * 
	 * @see de.kupzog.ktable.KTableModel for more information.
	 * @param model
	 *            The KTableModel instance that provides the table with all
	 *            necessary data!
	 */
	public void setModel(KTableModel model) {
		checkWidget();
		m_Model = model;
		m_FocusCol = -1;
		m_FocusRow = -1;
		clearSelectionWithoutRedraw();

		// implement autoscrolling if needed:
		if ((getStyle() & SWTX.AUTO_SCROLL) == SWTX.AUTO_SCROLL)
			updateScrollbarVisibility();

		redraw();
	}

	/**
	 * returns the current table model
	 * 
	 * @return KTableModel
	 */
	public KTableModel getModel() {
		return m_Model;
	}

	/**
	 * Helper method to quickly get the number of fixed columns.
	 * 
	 * @return
	 */
	protected int getFixedColumnCount() {
		return m_Model.getFixedHeaderColumnCount() + m_Model.getFixedSelectableColumnCount();
	}

	/**
	 * Helper method to quickly get the number of fixed rows.
	 * 
	 * @return
	 */
	protected int getFixedRowCount() {
		return m_Model.getFixedHeaderRowCount() + m_Model.getFixedSelectableRowCount();
	}

	protected void updateScrollbarVisibility() {
		try {
			KTableModel model = getModel();
			Rectangle actualSize = getClientArea();
			// vertical:
			boolean showVertBar = false;
			int theoreticalHeight = 1;
			for (int i = 0; i < model.getRowCount(); i++) {
				theoreticalHeight += model.getRowHeight(i);
				if (theoreticalHeight > actualSize.height) {
					showVertBar = true;
					break;
				}
			}
			getVerticalBar().setVisible(showVertBar);
			// horizontal:
			int theoreticalWidth = 0;
			for (int i = 0; i < model.getColumnCount(); i++)
				theoreticalWidth += model.getColumnWidth(i);
			getHorizontalBar().setVisible(actualSize.width < theoreticalWidth);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getColumnWidth(int col) {
		if (col == m_Model.getColumnCount() - 1 && (getStyle() & SWTX.FILL_WITH_LASTCOL) != 0) {
			// expand the width to grab all the remaining space with this last
			// col.
			Rectangle cl = getClientArea();
			int remaining = cl.x + cl.width - 2 - getColumnLeft(col);
			return Math.max(remaining, m_Model.getColumnWidth(col));
		} else
			return m_Model.getColumnWidth(col);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Widget#getStyle()
	 */
	public int getStyle() {
		return m_Style;
	}

	/**
	 * Overwrites the style bits that determine certain behavior of KTable. Note
	 * that not all style bits can be changed after KTable is created.
	 * 
	 * @param style
	 *            The updated style bits (ORed together). Possibilities:
	 *            <ul>
	 *            <li><b>SWTX.FILL_WITH_LASTCOL</b> - Makes the table enlarge
	 *            the last column to always fill all space.</li>
	 *            <li><b>SWTX.FILL_WITH_DUMMYCOL</b> - Makes the table fill
	 *            any remaining space with dummy columns to fill all space.</li>
	 *            <li><b>SWT.FLAT</b> - Does not paint a dark outer border
	 *            line.</li>
	 *            <li><b>SWT.MULTI</b> - Sets the "Multi Selection Mode". In
	 *            this mode, more than one cell or row can be selected. The user
	 *            can achieve this by shift-click and ctrl-click. The selected
	 *            cells/rows can be scattored ofer the complete table. If you
	 *            pass false, only a single cell or row can be selected. This
	 *            mode can be combined with the "Row Selection Mode".</li>
	 *            <li><b>SWT.FULL_SELECTION</b> - Sets the "Full Selection
	 *            Mode". In the "Full Selection Mode", the table always selects
	 *            a complete row. Otherwise, each individual cell can be
	 *            selected. This mode can be combined with the "Multi Selection
	 *            Mode".</li>
	 *            <li><b>SWTX.EDIT_ON_KEY</b> - Activates a possibly present
	 *            cell editor on every keystroke. (Default: only ENTER).
	 *            However, note that editors can specify which events they
	 *            accept.</li>
	 *            <li><b>SWT.HIDE_SELECTION</b> - Hides the selected cells
	 *            when the KTable looses focus.</li>
	 *            <li><b>SWTX.MARK_FOCUS_HEADERS</b> - Makes KTable draw left
	 *            and top header cells in a different style when the focused
	 *            cell is in their row/column. This mimics the MS Excel behavior
	 *            that helps find the currently selected cell(s).</li>
	 *            </ul>
	 */
	public void setStyle(int style) {
		m_Style = style;
	}
}