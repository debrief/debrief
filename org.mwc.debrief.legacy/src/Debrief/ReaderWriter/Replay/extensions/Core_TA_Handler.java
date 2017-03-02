package Debrief.ReaderWriter.Replay.extensions;

import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.ExtensibleLineImporter;

abstract class Core_TA_Handler implements ExtensibleLineImporter
{

  @SuppressWarnings("unused")
  private Layers _myLayer;
  private final String _myType;
  
  protected static final String CENTRE_OF_GRAVITY = "Centre of Gravity";
  
  Core_TA_Handler(final String type)
  {
    _myType = type;
  }

  @Override
  final public void setLayers(final Layers parent)
  {
    _myLayer = parent;
  }
  
  @Override
  final public String getYourType()
  {
    return ";" + _myType + ":";
  }


  @Override
  final public String exportThis(Plottable theShape)
  {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  final public boolean canExportThis(Object val)
  {
    return false;
  }

  /** store this measurement
   * 
   * @param platform_name the platform to store the data under
   * @param sensor_name the sensor to store the data under
   * @param folder the folder to store the dataset into (use "/" to indicate sub-folders)
   * @param dataset the dataset to put the measurement into
   * @param theDate the time of the measurement
   * @param measurement the measurement
   */
  protected void storeMeasurement(final String platform_name, final String sensor_name, final String folder,
      final String dataset, final HiResDate theDate, final double measurement)
  {
    System.out.println("Storing " + measurement + " at " + theDate.getDate() + " in " + dataset + " in folder:" + folder);
    
    
    // find the platform
    
    // find the sensor
    
      // create an empty sensor?
    
    // find the measurements
    
    // find the dataset
    
    // add the measurement
  }

  @Override
  final public String getSymbology()
  {
    return null;
  }
}
