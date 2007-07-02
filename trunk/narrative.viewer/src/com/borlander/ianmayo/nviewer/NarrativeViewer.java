package com.borlander.ianmayo.nviewer;

import java.util.Collection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import com.borlander.ianmayo.nviewer.model.IEntryWrapper;
import com.borlander.ianmayo.nviewer.model.TimeFormatter;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellResizeAdapter;
import de.kupzog.ktable.SWTX;

public class NarrativeViewer extends KTable {

	private final NarrativeViewerModel myModel;

	public NarrativeViewer(Composite parent, IPreferenceStore preferenceStore) {
		super(parent, SWTX.FILL_WITH_LASTCOL | SWT.V_SCROLL);

		myModel = new NarrativeViewerModel(preferenceStore, new ColumnSizeCalculator() {
			public int getColumnWidth(int col) {
				return getColumnRight(col) - getColumnLeft(col);
			}
		});
		setModel(myModel);

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				onColumnsResized(false);
			}
		});

		addCellResizeListener(new KTableCellResizeAdapter() {
			public void columnResized(int col, int newWidth) {
				onColumnsResized(false);
			}
		});
	}

	private void onColumnsResized(boolean force) {
		GC gc = new GC(this);
		myModel.onColumnsResized(gc, force);
		gc.dispose();
	}

	public void setInput(IEntryWrapper entryWrapper) {
		myModel.setInput(entryWrapper);
		refresh();
	}

	public void setVisibleColumnVisible(boolean visible) {
		myModel.setVisibleColumnVisible(visible);
		refresh();
	}

	public void setSourceColumnVisible(boolean visible) {
		myModel.setSourceColumnVisible(visible);
		refresh();
	}

	public void setTypeColumnVisible(boolean visible) {
		myModel.setTypeColumnVisible(visible);
		refresh();
	}

	public void setTimeFormatter(TimeFormatter timeFormatter) {
		myModel.setTimeFormatter(timeFormatter);
		redraw();
	}

	public void setSourceFilter(Collection<String> sourceFilter) {
		myModel.setSourceFilter(sourceFilter);
		refresh();
	}

	public void setTypeFilter(Collection<String> typeFilter) {
		myModel.setTypeFilter(typeFilter);
		refresh();
	}

	private void refresh() {
		onColumnsResized(true);
		redraw();
	}

	public boolean isWrappingEntries() {
		return myModel.isWrappingEntries();
	}

	public void setWrappingEntries(boolean shouldWrap) {
		if (myModel.setWrappingEntries(shouldWrap)) {
			refresh();
		}
	}
}
