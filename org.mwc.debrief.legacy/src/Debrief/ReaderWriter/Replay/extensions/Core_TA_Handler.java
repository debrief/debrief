package Debrief.ReaderWriter.Replay.extensions;

import java.util.Enumeration;

import junit.framework.TestCase;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;
import Debrief.Wrappers.Extensions.Measurements.TimeSeries2Double;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDouble;
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

  private static interface StorageHelper
  {
    void storeHere(long time, DataItem dataset);

    DataItem createTarget(String name, String units);
  }

  /**
   * store this measurement
   * 
   * @param platform_name
   *          the platform to store the data under
   * @param sensor_name
   *          the sensor to store the data under (or null to go in the top level)
   * @param folder
   *          the folder to store the dataset into (use "/" to indicate sub-folders)
   * @param dataset_name
   *          the dataset to put the measurement into
   * @param theDate
   *          the time of the measurement
   * @param measurement
   *          the measurement
   */
  protected void storeMeasurement(final String platform_name,
      final String sensor_name, final String folder, final String dataset_name,
      final String units, final HiResDate theDate, final double measurement)
  {
    StorageHelper helper = new StorageHelper()
    {
      @Override
      public void storeHere(long time, DataItem dataset)
      {
        TimeSeriesDouble ds = (TimeSeriesDouble) dataset;
        ds.add(time, measurement);
      }

      @Override
      public DataItem createTarget(String name, String units)
      {
        return new TimeSeriesDouble(name, units);
      }
    };
    storeMeasurement(platform_name, sensor_name, folder, dataset_name, units,
        theDate, helper);
  }

  /**
   * store this measurement
   * 
   * @param platform_name
   *          the platform to store the data under
   * @param sensor_name
   *          the sensor to store the data under (or null to go in the top level)
   * @param folder
   *          the folder to store the dataset into (use "/" to indicate sub-folders)
   * @param dataset_name
   *          the dataset to put the measurement into
   * @param theDate
   *          the time of the measurement
   * @param measurement
   *          the measurement
   */
  protected void storeMeasurement2D(final String platform_name,
      final String sensor_name, final String folder, final String dataset_name,
      final String units, final HiResDate theDate, final String value1Name,
      final String value2Name, final double measurement1,
      final double measurement2)
  {
    StorageHelper helper = new StorageHelper()
    {
      @Override
      public void storeHere(long time, DataItem dataset)
      {
        TimeSeries2Double ds = (TimeSeries2Double) dataset;
        ds.add(time, measurement1, measurement2);
      }

      @Override
      public DataItem createTarget(String name, String units)
      {
        return new TimeSeries2Double(name, units, value1Name, value2Name);
      }
    };
    storeMeasurement(platform_name, sensor_name, folder, dataset_name, units,
        theDate, helper);
  }

  private void storeMeasurement(final String platform_name,
      final String sensor_name, final String folder, final String dataset_name,
      final String units, final HiResDate theDate, StorageHelper helper)
  {
    // find the platform
    TrackWrapper track = (TrackWrapper) _layers.findLayer(platform_name);
    if (track == null)
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
      if (thisSensor.getName().equals(sensor_name))
      {
        ourSensor = thisSensor;
        break;
      }
    }

    if (ourSensor == null)
    {
      // ok, create an empty sensor?
      ourSensor = new SensorWrapper(sensor_name);
      track.getSensors().add(ourSensor);
    }

    // find the measurements
    DataFolder dataFolder =
        (DataFolder) ourSensor.getAdditionalData()
            .getThisType(DataFolder.class);
    if (dataFolder == null)
    {
      dataFolder = new DataFolder();
      ourSensor.getAdditionalData().add(dataFolder);
    }

    // find the dataset
    DataItem target =
        findDataset(dataFolder, folder, dataset_name, units, helper);

    // add the measurement
    helper.storeHere(theDate.getDate().getTime(), target);
  }

  private DataItem findDataset(DataFolder parent, String folder, String name,
      String units, StorageHelper helper)
  {
    DataItem res = null;

    DataFolder targetFolder = parent;

    // break the folder down, if necessary
    if (folder != null)
    {
      if (folder.contains("/"))
      {
        String[] levels = folder.split("/");
        for (String level : levels)
        {
          targetFolder = getFolder(targetFolder, level);
        }
      }
      else
      {
        targetFolder = getFolder(targetFolder, folder);
      }
    }

    // get the dataset from this folder
    for (DataItem item : targetFolder)
    {
      if (item.getName().equals(name))
      {
        res = item;
        break;
      }
    }

    // did it find it?
    if (res == null)
    {
      res = helper.createTarget(name, units);
      targetFolder.add(res);
    }

    return res;
  }

  /**
   * find (or create) a folder with the given name
   * 
   */
  private DataFolder getFolder(DataFolder folder, String name)
  {
    DataFolder res = null;

    for (DataItem item : folder)
    {
      if (item.getName().equals(name) && (item instanceof DataFolder))
      {
        res = (DataFolder) item;
        break;
      }
    }

    if (res == null)
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
      handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore",
          "Some units", new HiResDate(1200000), 12.33);

      // check it worked
      // find the measurements
      DataFolder topF =
          (DataFolder) sensor.getAdditionalData().getThisType(DataFolder.class);
      if (topF == null)
      {
        topF = new DataFolder();
        sensor.getAdditionalData().add(topF);
      }

      topF.printAll();

      DataFolder subF = (DataFolder) topF.get("Modules");
      TimeSeriesDouble dataset = (TimeSeriesDouble) subF.get("Fore");
      assertEquals("has items", 1, dataset.size());

      handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore",
          "Some units", new HiResDate(1300000), 15.33);
      handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore",
          "Some units", new HiResDate(1400000), 11.33);

      assertEquals("has items", 3, dataset.size());

    }
  }
}
