package org.mwc.cmap.NarrativeViewer;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

public class TableViewerColumnFactory
{
  private final TableViewer _viewer;

  public TableViewerColumnFactory(TableViewer viewer)
  {
    super();
    this._viewer = viewer;
  }

  public TableViewerColumn createColumn(String header, int width,
      ColumnLabelProvider provider)
  {
    return createColumn(header, width, provider, SWT.LEFT);
  }

  public TableViewerColumn createColumn(String header, int width,
      ColumnLabelProvider provider, int alignment)
  {
    final TableViewerColumn viewerColumn =
        new TableViewerColumn(_viewer, SWT.NONE);
    final TableColumn column = viewerColumn.getColumn();
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
