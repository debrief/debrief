package info.limpet.stackedcharts.ui.view.adapter;

import info.limpet.stackedcharts.model.Dataset;

import java.util.List;

public interface IStackedDatasetAdapter
{
  /** whether this adapter can convert objects of the
   * supplied type.  This is expected to return
   * promptly, so it can be used when determining hover target
   * 
   * @param data the object to convert
   * @return yes/no
   */
  boolean canConvertToDataset(Object data);
  
  
  /** convert the supplied data object into a Limpet dataset
   * if possible
   * @param data the object to convert
   * @return the dataset (or null if this class can't convert it)
   */
  List<Dataset> convertToDataset(Object data);
}
