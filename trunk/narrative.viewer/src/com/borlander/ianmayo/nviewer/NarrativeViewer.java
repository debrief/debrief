package com.borlander.ianmayo.nviewer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import MWC.TacticalData.IRollingNarrativeProvider;

import com.borlander.ianmayo.nviewer.actions.NarrativeViewerActions;
import com.borlander.ianmayo.nviewer.filter.ui.FilterDialog;
import com.borlander.ianmayo.nviewer.model.TimeFormatter;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellDoubleClickAdapter;
import de.kupzog.ktable.KTableCellResizeAdapter;
import de.kupzog.ktable.SWTX;

public class NarrativeViewer extends KTable {

	private final NarrativeViewerModel myModel;
	private NarrativeViewerActions myActions;
	

	public NarrativeViewer(Composite parent, IPreferenceStore preferenceStore) {
		super(parent, SWTX.FILL_WITH_LASTCOL | SWT.V_SCROLL);

		myModel = new NarrativeViewerModel(preferenceStore, new ColumnSizeCalculator() {
			public int getColumnWidth(int col) {
				return getColumnRight(col) - getColumnLeft(col);
			}
		});
		
		myModel.addColumnVisibilityListener(new Column.VisibilityListener() {
			public void columnVisibilityChanged(Column column, boolean actualIsVisible) {
				refresh();
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

		addCellDoubleClickListener(new KTableCellDoubleClickAdapter() {
			public void fixedCellDoubleClicked(int col, int row, int statemask) {
				Column column = myModel.getVisibleColumn(col);
				showFilterDialog(column);
				ColumnFilter filter = column.getFilter();
				if (filter != null){
					
				}
			}
		});
	}
	
	public NarrativeViewerActions getViewerActions(){
		if (myActions == null){
			myActions = new NarrativeViewerActions(this);
		}
		return myActions;
	}
	
	public NarrativeViewerModel getModel() {
		return (NarrativeViewerModel)super.getModel();
	}

	public void showFilterDialog(Column column) {
		if (!myModel.hasInput()) {
			return;
		}
		
		ColumnFilter filter = column.getFilter();
		if (filter == null){
			return;
		}

		FilterDialog dialog = new FilterDialog(getShell(), myModel.getInput(), column);

		if (Dialog.OK == dialog.open()) {
			dialog.commitFilterChanges();
			refresh();
		}
	}

	private void onColumnsResized(boolean force) {
		GC gc = new GC(this);
		myModel.onColumnsResized(gc, force);
		gc.dispose();
	}

	public void setInput(IRollingNarrativeProvider entryWrapper) {
		myModel.setInput(entryWrapper);
		refresh();
	}
	
	

	public void setTimeFormatter(TimeFormatter timeFormatter) {
		myModel.setTimeFormatter(timeFormatter);
		redraw();
	}

	public void refresh() {
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

//    public void setInput(IRollingNarrativeProvider myRollingNarrative)
//    {
//        // convert the rolling narrative to the expected format
//        
//        // create some duff data, and present it.
////        IRollingNarrativeProvider ie = new IRollingNarrativeProvider(){
////            public NarrativeEntry[] getNarrativeHistory(String[] categories)
////            {
////                NarrativeEntry one = new NarrativeEntry("trk", "type1", new HiResDate(new Date()), "a1");
////                NarrativeEntry two = new NarrativeEntry("trk", "type2", new HiResDate(new Date()), "a2");
////                NarrativeEntry three = new NarrativeEntry("trk1", "type1", new HiResDate(new Date()), "a3");
////                NarrativeEntry[] res = new NarrativeEntry[]{one, two, three};
////                return res;
////            }};
//         setInput(myRollingNarrative);
//    }
	

}
