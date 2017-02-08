package org.mwc.cmap.NarrativeViewer2;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.AggregateConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;

/**
 * The body layer stack for the viewer.
 * 
 * @param <T>
 */
public class BodyLayerStack<T> extends AbstractLayerTransform
{

  private final SortedList<T> sortedList;
  private final FilterList<T> filterList;

  private final ListDataProvider<T> bodyDataProvider;

  private final GlazedListsEventLayer<T> glazedListsEventLayer;
  private final ColumnReorderLayer columnReorderLayer;
  private final ColumnHideShowLayer columnHideShowLayer;
  private final SelectionLayer selectionLayer;
  private final ViewportLayer viewportLayer;

  private final AggregateConfigLabelAccumulator accumulator;
  private DataLayer bodyDataLayer;

  public BodyLayerStack(final List<T> values,
      final IColumnPropertyAccessor<T> columnPropertyAccessor)
  {
    // wrapping of the list to show into GlazedLists
    // see http://publicobject.com/glazedlists/ for further information
    final EventList<T> eventList = GlazedLists.eventList(values);
    final TransformedList<T, T> rowObjectsGlazedList =
        GlazedLists.threadSafeList(eventList);

    // use the SortedList constructor with 'null' for the Comparator
    // because the Comparator will be set by configuration
    this.sortedList = new SortedList<T>(rowObjectsGlazedList, null);
    // wrap the SortedList with the FilterList
    this.filterList = new FilterList<T>(getSortedList());

    this.bodyDataProvider =
        new ListDataProvider<T>(this.filterList, columnPropertyAccessor);
    bodyDataLayer = new DataLayer(this.bodyDataProvider);

    accumulator = new AggregateConfigLabelAccumulator();
    accumulator.add(new ColumnLabelAccumulator(bodyDataProvider));
    bodyDataLayer.setConfigLabelAccumulator(accumulator);

    // width configuration - last column should take remaining space
    bodyDataLayer.setColumnWidthByPosition(0, 100);
    bodyDataLayer.setColumnWidthByPosition(1, 100);
    bodyDataLayer.setColumnWidthByPosition(2, 100);
    bodyDataLayer.setColumnPercentageSizing(3, true);
    bodyDataLayer.setColumnsResizableByDefault(true);

    // layer for event handling of GlazedLists and PropertyChanges
    glazedListsEventLayer =
        new GlazedListsEventLayer<T>(bodyDataLayer, this.filterList);

    this.columnReorderLayer = new ColumnReorderLayer(glazedListsEventLayer);
    this.columnHideShowLayer = new ColumnHideShowLayer(this.columnReorderLayer);
    this.selectionLayer = new SelectionLayer(this.columnHideShowLayer);
    this.viewportLayer = new ViewportLayer(this.selectionLayer);

    addConfiguration(new DefaultEditBindings());
    addConfiguration(new DefaultEditConfiguration());

    setUnderlyingLayer(viewportLayer);
  }

  public void addConfigLabelAccumulator(final IConfigLabelAccumulator cla)
  {
    accumulator.add(cla);
  }

  public ListDataProvider<T> getBodyDataProvider()
  {
    return this.bodyDataProvider;
  }

  public ColumnHideShowLayer getColumnHideShowLayer()
  {
    return this.columnHideShowLayer;
  }

  public ColumnReorderLayer getColumnReorderLayer()
  {
    return this.columnReorderLayer;
  }

  public FilterList<T> getFilterList()
  {
    return this.filterList;
  }

  public GlazedListsEventLayer<T> getGlazedListsEventLayer()
  {
    return this.glazedListsEventLayer;
  }

  public SelectionLayer getSelectionLayer()
  {
    return this.selectionLayer;
  }
  
  public DataLayer getBodyDataLayer()
  {
    return bodyDataLayer;
  }

  public SortedList<T> getSortedList()
  {
    return this.sortedList;
  }

  public ViewportLayer getViewportLayer()
  {
    return this.viewportLayer;
  }
}