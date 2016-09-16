package org.mwc.cmap.NarrativeViewer;

import org.eclipse.jface.layout.AbstractColumnLayout;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.FilteredTree;

public class FilteredTreeColumnLayout extends AbstractColumnLayout {
	private boolean addListener = true;
	static final boolean IS_GTK = Util.isGtk();
	private static class TreeLayoutListener implements TreeListener {

		public void treeCollapsed(TreeEvent e) {
			update((FilteredTree) e.widget);
		}

		public void treeExpanded(TreeEvent e) {
			update((FilteredTree) e.widget);
		}
		
		private void update(final FilteredTree tree) {
			tree.getDisplay().asyncExec(new Runnable() {

				public void run() {
					tree.update();
					tree.getParent().layout();
				}
				
			});
		}
		
	}
	
	private static final TreeLayoutListener listener = new TreeLayoutListener();
	
	Scrollable getControl(Composite composite) {
    return (Scrollable) composite.getChildren()[0];
  }
	
	protected void layout(Composite composite, boolean flushCache) {
		super.layout(composite, flushCache);
		if( addListener ) {
			addListener=false;
			if(composite instanceof FilteredTree)
			  ((FilteredTree)getControl(composite)).getViewer().getTree().addTreeListener(listener);
			if(composite instanceof Tree)
			  ((Tree)getControl(composite)).addTreeListener(listener);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 3.5
	 */
	protected int getColumnCount(Scrollable tree) {
		return ((FilteredTree) tree).getViewer().getTree().getColumnCount();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 3.5
	 */
	protected void setColumnWidths(Scrollable tree, int[] widths) {
		TreeColumn[] columns = ((FilteredTree) tree).getViewer().getTree().getColumns();
		for (int i = 0; i < widths.length; i++) {
			columns[i].setWidth(widths[i]);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 3.5
	 */
	protected ColumnLayoutData getLayoutData(Scrollable tableTree, int columnIndex) {
		TreeColumn column = ((FilteredTree) tableTree).getViewer().getTree().getColumn(columnIndex);
		return (ColumnLayoutData) column.getData(LAYOUT_DATA);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @since 3.5
	 */
	protected void updateColumnData(Widget column) {
		TreeColumn tColumn = (TreeColumn) column;
		Tree t = tColumn.getParent();
		
		if( ! IS_GTK || t.getColumn(t.getColumnCount()-1) != tColumn ){
			tColumn.setData(LAYOUT_DATA,new ColumnPixelData(tColumn.getWidth()));
			layout(t.getParent(), true);
		}
	}
}