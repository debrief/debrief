package Debrief.ReaderWriter.Replay.extensions;

import java.util.Enumeration;

import junit.framework.TestCase;

import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Measurements.CoreDataset;
import Debrief.Wrappers.Measurements.DataFolder;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.ExtensibleLineImporter;

abstract class Core_TA_Handler implements ExtensibleLineImporter
{

  private Layers _layers;
  private final String _myType;
  
  protected static final String CENTRE_OF_GRAVITY = "Centre of Gravity";
  
  Core_TA_Handler(final String type)
  {
    _myType = type;
  }

  @Override
  final public void setLayers(final Layers parent)
  {
    _layers = parent;
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
   * @param sensor_name the sensor to store the data under (or null to go in the top level)
   * @param folder the folder to store the dataset into (use "/" to indicate sub-folders)
   * @param dataset_name the dataset to put the measurement into
   * @param theDate the time of the measurement
   * @param measurement the measurement
   */
  protected void storeMeasurement(final String platform_name, final String sensor_name, final String folder,
      final String dataset_name, final HiResDate theDate, final double measurement)
  {
    // find the platform
    TrackWrapper track = (TrackWrapper) _layers.findLayer(platform_name);
    if(track == null)
    {
      System.err.println("Track not found for:" + platform_name);
      return;
    }
    
    // find the sensor
    SensorWrapper ourSensor = null;
    BaseLayer sensors = track.getSensors();    
    Enumeration<Editable> numer = sensors.elements();
    while (numer.hasMoreElements())
    {
      SensorWrapper thisSensor = (SensorWrapper) numer.nextElement();
      if(thisSensor.getName().equals(sensor_name))
      {
        ourSensor = thisSensor;
        break;
      }
    }
    
    if(ourSensor == null)
    {
      // ok, create an empty sensor?
      ourSensor = new SensorWrapper(sensor_name);
      track.getSensors().add(ourSensor);
    }
    
    // find the measurements
    DataFolder dataFolder = ourSensor.getMeasurements();
    
    // find the dataset
    CoreDataset<Long, Double> dataset = findDataset(dataFolder, folder, dataset_name);
    
    // add the measurement
    dataset.add(theDate.getDate().getTime(), measurement);
  }

  private CoreDataset<Long, Double> findDataset(DataFolder parent, String folder, String name)
  {
    CoreDataset<Long, Double> res = null;
    
    DataFolder targetFolder = parent;
    
    // break the folder down, if necessary
    if(folder != null)
    {
      if(folder.contains("/"))
      {
        String [] levels = folder.split("/");
        for(String level: levels)
        {
          targetFolder = getFolder(targetFolder, level);
        }
      }
      else
      {
        targetFolder = getFolder(targetFolder, folder);
      }
    }

    // get the dataset
    res = getDataset(targetFolder, name);
    
    // did it find it?
    if(res == null)
    {
      res = new CoreDataset<Long, Double>(name);
      targetFolder.add(res);
    }
    
    return res;
  }
  
  @SuppressWarnings("unchecked")
  private CoreDataset<Long, Double> getDataset(DataFolder folder,
      String name)
  {
    CoreDataset<Long, Double> res = null;
    
    Enumeration<Editable> ele = folder.elements();
    while (ele.hasMoreElements())
    {
      Editable thisE = (Editable) ele.nextElement();
      if(thisE.getName().equals(name))
      {
        res = (CoreDataset<Long, Double>) thisE;
        break;
      }
    }
    
    if(res == null)
    {
      res = new CoreDataset<Long, Double>(name);
      folder.add(res);
    }
    
    return res;
  }

  /** find (or create) a folder with the given name
   * 
   */
  private DataFolder getFolder(DataFolder folder, String name)
  {
    DataFolder res = null;
    
    Enumeration<Editable> ele = folder.elements();
    while (ele.hasMoreElements())
    {
      Editable thisE = (Editable) ele.nextElement();
      if(thisE.getName().equals(name))
      {
        res = (DataFolder) thisE;
        break;
      }
    }
    
    if(res == null)
    {
      res = new DataFolder(name);
      folder.add(res);
    }
    
    return res;
  }
  
  @Override
  final public String getSymbology()
  {
    return null;
  }
  
  
  public static class TestStorage extends TestCase
  {
    public void testFolders()
    {
      Layers layers = new Layers();
      TrackWrapper track = new TrackWrapper();
      track.setName("Platform");
      SensorWrapper sensor = new SensorWrapper("Sensor");
      track.add(sensor);
      layers.addThisLayer(track);
      
      TA_Modules_DataHandler handler = new TA_Modules_DataHandler();
      handler.setLayers(layers);
      handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore", new HiResDate(1200000), 12.33);
      
      // check it worked
      DataFolder topF = sensor.getMeasurements();
      
      topF.printAll();
      
      DataFolder subF = (DataFolder) topF.get("Modules");
      CoreDataset<?,?> dataset = (CoreDataset<?, ?>) subF.get("Fore");
      assertEquals("has items",  1, dataset.size());

      handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore", new HiResDate(1300000), 15.33);
      handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore", new HiResDate(1400000), 11.33);

      assertEquals("has items",  3, dataset.size());

    }
  }
}
