package org.mwc.cmap.NarrativeViewer;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;

public class TableViewerColumnFactory
{
  private final GridTableViewer _viewer;

  public TableViewerColumnFactory(GridTableViewer viewer)
  {
    super();
    this._viewer = viewer;
  }

  public GridViewerColumn createColumn(String header, int width,
      CellLabelProvider provider,boolean wrap)
  {
    return createColumn(header, width, provider, SWT.LEFT,wrap);
  }

  public GridViewerColumn createColumn(String header, int width,
      CellLabelProvider provider, int alignment,boolean wrap)
  {
    final GridViewerColumn viewerColumn =
        new GridViewerColumn(_viewer, SWT.NONE);
    final GridColumn column = viewerColumn.getColumn();
    column.setText(header == null ? "" : header);

    if (width > 0)
    {
      column.setWidth(width);
    }
    column.setResizeable(true);
    column.setMoveable(true);
    column.setAlignment(alignment);
    column.setWordWrap(wrap);
    viewerColumn.setLabelProvider(provider);

    return viewerColumn;
  }
}
