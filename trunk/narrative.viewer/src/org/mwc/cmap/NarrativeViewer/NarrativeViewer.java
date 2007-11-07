package org.mwc.cmap.NarrativeViewer;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.NarrativeViewer.actions.NarrativeViewerActions;
import org.mwc.cmap.NarrativeViewer.app.NViewerView;
import org.mwc.cmap.NarrativeViewer.filter.ui.FilterDialog;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;


import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellDoubleClickAdapter;
import de.kupzog.ktable.KTableCellResizeAdapter;
import de.kupzog.ktable.SWTX;

public class NarrativeViewer extends KTable {

	private final NarrativeViewerModel myModel;
	private NarrativeViewerActions myActions;
    private PropertyChangeListener _timeListener;
	

	public NarrativeViewer(Composite parent, IPreferenceStore preferenceStore) {
		super(parent, SWTX.FILL_WITH_LASTCOL | SWT.V_SCROLL | SWT.FULL_SELECTION);

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

	/** the controlling time has updated
	 * 
	 * @param dtg the selected dtg
	 */
    public void setDTG(HiResDate dtg)
    {
        // find the table entry immediately after or on this DTG
        IRollingNarrativeProvider narr = myModel.getInput();
        NarrativeEntry[] neList = narr.getNarrativeHistory(new String[]{});
        int theIndex = -1;
        for (int index = 0; index < neList.length; index++)
        {
            NarrativeEntry ne = neList[index];
            HiResDate dt = ne.getDTG();
            if(dt.greaterThanOrEqualTo(dtg))
            {
                theIndex = index;
                break;
            }            
        }
        
        // ok, try to select this entry
        if(theIndex > -1)
            super.setSelection(1, theIndex + 1, true);

    }
}
