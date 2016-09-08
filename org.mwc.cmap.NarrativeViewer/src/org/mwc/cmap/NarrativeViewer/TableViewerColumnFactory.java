package org.mwc.cmap.NarrativeViewer;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;

public class TableViewerColumnFactory
{
  private final TreeViewer _viewer;

  public TableViewerColumnFactory(TreeViewer viewer)
  {
    super();
    this._viewer = viewer;
  }

  public TreeViewerColumn createColumn(String header, int width,
      ColumnLabelProvider provider)
  {
    return createColumn(header, width, provider, SWT.LEFT);
  }

  public TreeViewerColumn createColumn(String header, int width,
      ColumnLabelProvider provider, int alignment)
  {
    final TreeViewerColumn viewerColumn =
        new TreeViewerColumn(_viewer, SWT.NONE);
    final TreeColumn column = viewerColumn.getColumn();
    column.setText(header == null ? "" : header);

    if (width > 0)
    {
      column.setWidth(width);
    }
    column.setResizable(true);
    column.setMoveable(true);
    column.setAlignment(alignment);
    viewerColumn.setLabelProvider(provider);

    return viewerColumn;
  }
}
