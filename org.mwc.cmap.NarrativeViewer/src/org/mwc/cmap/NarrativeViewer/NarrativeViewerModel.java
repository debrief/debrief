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

package org.mwc.cmap.NarrativeViewer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridColumnLayout;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.NarrativeViewer.Column.VisibilityListener;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;
import org.mwc.cmap.NarrativeViewer.preferences.NarrativeViewerPrefsPage;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;

public class NarrativeViewerModel {
	private abstract class AbstractTextColumn extends AbstractColumn {
		public AbstractTextColumn(final int index, final String name, final IPreferenceStore store) {
			super(index, name, store);
		}

		@Override
		protected CellLabelProvider createRenderer(final ColumnViewer viewer) {
			return new ColumnLabelProvider() {
				private final Map<java.awt.Color, Color> swtColorMap = new HashMap<java.awt.Color, Color>();

				@Override
				public void dispose() {
					for (final Color color : swtColorMap.values()) {
						color.dispose();
					}
					super.dispose();
				}

				@Override
				public Font getFont(final Object element) {
					if (prefFont != null && !prefFont.isDisposed())
						return prefFont;
					return super.getFont(element);
				}

				@Override
				public Color getForeground(final Object element) {
					if (element instanceof NarrativeEntry) {
						final NarrativeEntry entry = (NarrativeEntry) element;
						final java.awt.Color color = entry.getColor();
						Color swtColor = swtColorMap.get(color);
						if (swtColor == null || swtColor.isDisposed()) {
							swtColor = new Color(Display.getCurrent(), color.getRed(), color.getGreen(),
									color.getBlue());
							swtColorMap.put(color, swtColor);
						}
						return swtColor;
					}
					return BLACK;
				}

				@Override
				public String getText(final Object element) {
					if (element instanceof NarrativeEntry) {
						final NarrativeEntry entry = (NarrativeEntry) element;
						return (String) getProperty(entry);
					}
					return super.getText(element);
				}

			};
		}
	}

	private class ColumnEntry extends AbstractTextColumn {
		private boolean myIsWrapping = true;

		public ColumnEntry(final IPreferenceStore store) {
			super(4, "Entry", store);
		}

		@Override
		public int getColumnWidth() {
			return 250;
		}

		@Override
		public Object getProperty(final NarrativeEntry entry) {
			return entry.getEntry() == null ? "" : entry.getEntry();
		}

		@Override
		public boolean isColumnWidthExpand() {
			return true;
		}

		@Override
		public boolean isWrap() {
			return true;
		}

		public boolean isWrapping() {
			return myIsWrapping;
		}

		@Override
		public boolean isWrapSupport() {
			return true;
		}

		public boolean setWrapping(final boolean shouldWrap) {
			final boolean changed = myIsWrapping ^ shouldWrap;
			if (changed) {
				myIsWrapping = shouldWrap;
			}
			return changed;
		}

	}

	private class ColumnSource extends AbstractTextColumn {
		public ColumnSource(final IPreferenceStore store) {
			super(2, "Source", store);
		}

		@Override
		protected void columnSelection(final NarrativeViewer viewer) {
			viewer.showFilterDialog(this);
		}

		@Override
		public int getColumnWidth() {
			return 40;
		}

		@Override
		public Object getProperty(final NarrativeEntry entry) {
			return entry.getTrackName();
		}
	}

	private class ColumnTime extends AbstractTextColumn {
		private TimeFormatter myTimeFormatter = DEFAULT_TIME;

		public ColumnTime(final IPreferenceStore store) {
			super(1, "Time", store);
		}

		@Override
		public int getColumnWidth() {
			return 50;
		}

		@Override
		public Object getProperty(final NarrativeEntry entry) {
			final HiResDate dtg = entry.getDTG();
			String format = formattedDateCache.get(dtg);

			if (format == null) {
				format = myTimeFormatter.format(dtg);
				formattedDateCache.put(dtg, format);
			}

			return format;
		}

		public void setTimeFormatter(final TimeFormatter formatter) {
			myTimeFormatter = formatter;

			// and clear the cached formatting
			formattedDateCache.clear();
		}
	}

	private class ColumnType extends AbstractTextColumn {
		public ColumnType(final IPreferenceStore store) {
			super(3, "Type", store);
		}

		@Override
		protected void columnSelection(final NarrativeViewer viewer) {
			viewer.showFilterDialog(this);
		}

		@Override
		public int getColumnWidth() {
			return 40;
		}

		@Override
		public Object getProperty(final NarrativeEntry entry) {
			return entry.getType();
		}
	}

	private static class ColumnVisible extends AbstractColumn {

		public ColumnVisible(final IPreferenceStore store) {
			super(0, "Visible", store);

		}

		@Override
		protected ColumnLabelProvider createRenderer(final ColumnViewer viewer) {
			return new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					return getProperty((NarrativeEntry) element).toString();
				}
			};
		}

		@Override
		public CellEditor getCellEditor(final Grid table) {
			final CheckboxCellEditor checkboxCellEditor = new CheckboxCellEditor(table);

			return checkboxCellEditor;
		}

		@Override
		public int getColumnWidth() {
			return 20;
		}

		@Override
		public Object getProperty(final NarrativeEntry entry) {
			return entry.getVisible();
		}

		@Override
		public void setProperty(final NarrativeEntry entry, final Object obj) {
			entry.setVisible((Boolean) obj);
		}
	}

	private static final NarrativeEntry[] NO_ENTRIES = new NarrativeEntry[0];
	protected static final org.eclipse.swt.graphics.Color SWT_WHITE = new org.eclipse.swt.graphics.Color(
			Display.getCurrent(), 255, 255, 254);
	private static final Color BLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

	static final Color MATCH_YELLOW = new Color(Display.getDefault(), 255, 251, 204);
	static final Color[] PHRASES_COLORS = new Color[] { new Color(Display.getDefault(), 178, 180, 255),
			new Color(Display.getDefault(), 147, 232, 207), new Color(Display.getDefault(), 232, 205, 167),
			new Color(Display.getDefault(), 255, 192, 215), };
	static TimeFormatter DEFAULT_TIME = new TimeFormatter() {
		@Override
		public String format(final HiResDate time) {
			return time.toString();
		}
	};

	private final ColumnVisible myColumnVisible;

	private final ColumnTime myColumnTime;
	private final ColumnSource myColumnSource;

	private final ColumnType myColumnType;
	private final ColumnEntry myColumnEntry;

	private final AbstractColumn[] myAllColumns;

	private final ColumnFilter mySourceFilter;

	private final ColumnFilter myTypeFilter;

	private final EntryFilter textFilter;
	private Font prefFont;

	final LinkedList<NarrativeEntry> myVisibleRows = new LinkedList<NarrativeEntry>();

	private NarrativeEntry[] myAllEntries = NO_ENTRIES;

	private IRollingNarrativeProvider myInput;

	private final WeakHashMap<Object, String> formattedDateCache = new WeakHashMap<Object, String>();

	private final Styler SEARCH_STYLE = new Styler() {
		@Override
		public void applyStyles(final TextStyle textStyle) {
			textStyle.background = MATCH_YELLOW;
		}
	};

	private IPreferenceStore store;

	public NarrativeViewerModel(final GridTableViewer viewer, final IPreferenceStore store,
			final EntryFilter textFilter) {
		this.textFilter = textFilter;
		myColumnVisible = new ColumnVisible(store);
		myColumnVisible.setVisible(false);
		myColumnTime = new ColumnTime(store);
		myColumnSource = new ColumnSource(store);
		myColumnType = new ColumnType(store);
		myColumnEntry = new ColumnEntry(store);
		myAllColumns = new AbstractColumn[] { myColumnVisible, //
				myColumnTime, //
				myColumnSource, //
				myColumnType, //
				myColumnEntry //
		};
		viewer.getGrid().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				if (prefFont != null) {
					prefFont.dispose();
				}
			}
		});
		loadFont(store);
		store.addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				if (viewer.getGrid().isDisposed()) {
					return;
				}

				if (!event.getProperty().equals(NarrativeViewerPrefsPage.PreferenceConstants.FONT)) {
					return;
				}

				try {
					viewer.getGrid().setRedraw(false);
					loadFont(store);
					viewer.refresh();
				} finally {
					viewer.getGrid().setRedraw(true);
				}
			}

		});

		mySourceFilter = new ColumnFilter() {
			@Override
			public String getFilterValue(final NarrativeEntry entry) {
				return entry.getTrackName();
			}

			@Override
			protected void valuesSetChanged() {
				updateFilters();
			}
		};
		myTypeFilter = new ColumnFilter() {
			@Override
			public String getFilterValue(final NarrativeEntry entry) {
				return entry.getType();
			}

			@Override
			protected void valuesSetChanged() {
				updateFilters();
			}
		};
		myColumnSource.setFilter(mySourceFilter);
		myColumnType.setFilter(myTypeFilter);
		this.store = store;

	}

	public void createTable(final NarrativeViewer viewer, final GridColumnLayout layout) {
		viewer.getViewer().setItemCount(0);
		final TableViewerColumnFactory factory = new TableViewerColumnFactory(viewer.getViewer());
		viewer.getViewer().setContentProvider(new ILazyContentProvider() {

			Object[] elements;
			private GridTableViewer gridTableViewer;

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
				elements = myVisibleRows == null ? NO_ENTRIES : myVisibleRows.toArray();
				gridTableViewer = (GridTableViewer) viewer;
				gridTableViewer.setItemCount(0);
				gridTableViewer.setItemCount(elements.length);
			}

			@Override
			public void updateElement(final int index) {
				gridTableViewer.replace(elements[index], index);
			}
		});

		for (final AbstractColumn column : myAllColumns) {
			final CellLabelProvider cellRenderer = column.getCellRenderer(viewer.getViewer());

			final GridViewerColumn viewerColumn = factory.createColumn(column.getColumnName(), column.getColumnWidth(),
					cellRenderer, column.isWrap());

			final GridColumn gridColumn = viewerColumn.getColumn();
			gridColumn.addControlListener(new ControlListener() {
				@Override
				public void controlMoved(final ControlEvent e) {
					// ignore
				}

				@Override
				public void controlResized(final ControlEvent e) {
					// trigger cells to recalculate heights
					viewer.refresh();
				}
			});
			final TextHighlightCellRenderer styledTextCellRenderer = new TextHighlightCellRenderer() {
				protected String getFilterText() {
					return viewer.getFilterGrid().getFilterString();
				}

				@Override
				protected StyledString getStyledString(final String text) {
					final String filterText = getFilterText();
					final boolean hasTextFilter = filterText != null && !filterText.trim().isEmpty();

					final String[] phrases = getPhrases();
					if (hasTextFilter || phrases.length > 0) {
						final Map<String, Styler> stylerReg = new HashMap<String, Styler>();

						final StringBuilder group = new StringBuilder();

						boolean addOR = hasTextFilter;
						if (hasTextFilter) {
							group.append("(");
							group.append(Pattern.quote(filterText));
							group.append(")");
							stylerReg.put(filterText.toLowerCase(), SEARCH_STYLE);
						}

						final Styler[] phraseStyles = getPhraseStyles();
						int index = 0;
						for (final String phrase : phrases) {
							if (addOR) {
								group.append("|");
							}

							group.append("(");
							group.append(Pattern.quote(phrase));
							group.append(")");
							addOR = true;
							stylerReg.put(phrase.toLowerCase(), phraseStyles[index]);
							index++;
						}
						final StyledString string = new StyledString();
						final Pattern pattern = Pattern.compile(group.toString(), Pattern.CASE_INSENSITIVE);
						final Matcher matcher = pattern.matcher(text);

						final boolean found = matchRanges(text, matcher, string, stylerReg);

						if (!found) {
							return null;
						} else {
							return string;
						}
					}
					return null;
				}

				private boolean matchRanges(final String text, final Matcher matcher, final StyledString string,
						final Map<String, Styler> stylerReg) {
					boolean found = false;
					int lastindex = 0;
					while (matcher.find()) {

						found = true;
						if (lastindex != matcher.start()) {
							string.append(text.substring(lastindex, matcher.start()));
						}
						string.append(text.substring(matcher.start(), matcher.end()),
								stylerReg.get(matcher.group().toLowerCase()));
						lastindex = matcher.end();
					}
					if (lastindex < text.length())
						string.append(text.substring(lastindex));
					return found;
				}
			};
			styledTextCellRenderer.setWordWrap(column.isWrap());
			gridColumn.setCellRenderer(styledTextCellRenderer);

			gridColumn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					column.columnSelection(viewer);
				}
			});
			final CellEditor cellEditor = column.getCellEditor(viewer.getViewer().getGrid());
			if (cellEditor != null)
				viewerColumn.setEditingSupport(new EditingSupport(viewer.getViewer()) {
					@Override
					protected boolean canEdit(final Object element) {
						return cellEditor != null;
					}

					@Override
					protected CellEditor getCellEditor(final Object element) {
						return cellEditor;
					}

					@Override
					protected Object getValue(final Object element) {
						return column.getProperty((NarrativeEntry) element);
					}

					@Override
					protected void setValue(final Object element, final Object value) {
						column.setProperty((NarrativeEntry) element, value);
					}
				});
			column.addVisibilityListener(new VisibilityListener() {
				@Override
				public void columnVisibilityChanged(final Column column, final boolean actualIsVisible) {
					gridColumn.setVisible(column.isVisible());

					if (column.isVisible()) {
						layout.setColumnData(gridColumn, new ColumnWeightData(column.getColumnWidth()));
					} else {
						layout.setColumnData(gridColumn, new ColumnWeightData(0));
					}
				}
			});
			layout.setColumnData(gridColumn,
					new ColumnWeightData(column.getColumnWidth(), column.isColumnWidthExpand()));

			if (!column.isVisible()) {
				gridColumn.setVisible(column.isVisible());
				layout.setColumnData(gridColumn, new ColumnWeightData(0));
			}
		}
	}

	public AbstractColumn[] getAllColumns() {
		return myAllColumns;
	}

	public ColumnEntry getColumnEntry() {
		return myColumnEntry;
	}

	public Column getColumnSource() {
		return myColumnSource;
	}

	public ColumnTime getColumnTime() {
		return myColumnTime;
	}

	public Column getColumnType() {
		return myColumnType;
	}

	public ColumnVisible getColumnVisible() {
		return myColumnVisible;
	}

	public IRollingNarrativeProvider getInput() {
		return myInput;
	}

	protected String[] getPhrases() {
		final String phrasesText = store.getString(NarrativeViewerPrefsPage.PreferenceConstants.HIGHLIGHT_PHRASES);

		if (phrasesText != null && !phrasesText.trim().isEmpty()) {
			final String[] split = phrasesText.split(",");
			final String[] phrases = new String[split.length];
			for (int i = 0; i < phrases.length; i++) {
				phrases[i] = split[i].trim().toLowerCase();
			}
			return phrases;
		}

		return new String[] {};
	}

	protected Styler[] getPhraseStyles() {
		final Styler[] stylers = new Styler[getPhrases().length];

		for (int i = 0; i < stylers.length; i++) {
			final Color bg = PHRASES_COLORS[i % PHRASES_COLORS.length];
			stylers[i] = new Styler() {
				@Override
				public void applyStyles(final TextStyle textStyle) {
					textStyle.background = bg;
				}
			};

		}

		return stylers;
	}

	public boolean hasInput() {
		return myAllEntries != null;
	}

	public boolean isColumnResizable(final int col) {
		return true;
	}

	public boolean isWrappingEntries() {
		return myColumnEntry.isWrapping();
	}

	private void loadFont(final IPreferenceStore store) {
		final String fontStr = store.getString(NarrativeViewerPrefsPage.PreferenceConstants.FONT);
		if (fontStr == null) {
			if (prefFont != null) {
				prefFont.dispose();
			}
			prefFont = null;
		}

		else {
			if (prefFont != null) {
				prefFont.dispose();
				prefFont = null;
			}

			final FontData[] readFontData = PreferenceConverter.readFontData(fontStr);
			if (readFontData != null) {
				prefFont = new Font(Display.getDefault(), readFontData);
			}
		}
	}

	public void setInput(final IRollingNarrativeProvider entryWrapper) {
		myInput = entryWrapper;
		myAllEntries = null;
		formattedDateCache.clear();
		if (entryWrapper != null) {
			// check it has some data.
			final NarrativeEntry[] entries = entryWrapper.getNarrativeHistory(new String[] {});
			if (entries != null)
				myAllEntries = entries;
			else
				myAllEntries = null;
		}
		updateFilters();
	}

	public void setTimeFormatter(final TimeFormatter timeFormatter) {
		myColumnTime.setTimeFormatter(timeFormatter);
	}

	public boolean setWrappingEntries(final boolean shouldWrap) {
		return myColumnEntry.setWrapping(shouldWrap);
	}

	void updateFilters() {
		myVisibleRows.clear();
		if (!hasInput()) {
			return;
		}

		for (final NarrativeEntry entry : myAllEntries) {
			if (entry.getVisible() && mySourceFilter.accept(entry) && myTypeFilter.accept(entry)
					&& textFilter.accept(entry)) {
				myVisibleRows.add(entry);
			}
		}
	}
}
