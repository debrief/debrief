package Debrief.ReaderWriter.Replay.extensions;

import java.util.Enumeration;

import junit.framework.TestCase;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDouble;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;
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
    TimeSeriesDouble dataset = findDataset(dataFolder, folder, dataset_name, units);

    // add the measurement
    dataset.add(theDate.getDate().getTime(), measurement);
  }

  private TimeSeriesDouble findDataset(DataFolder parent, String folder,
      String name, String units)
  {
    TimeSeriesDouble res = null;

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

    // get the dataset
    res = getDataset(targetFolder, name, units);

    // did it find it?
    if (res == null)
    {
      res = new TimeSeriesDouble(name, units);
      targetFolder.add(res);
    }

    return res;
  }

  private TimeSeriesDouble getDataset(DataFolder folder, String name, String units)
  {
    TimeSeriesDouble res = null;

    for (DataItem item : folder)
    {
      if (item.getName().equals(name) && (item instanceof TimeSeriesDouble))
      {
        res = (TimeSeriesDouble) item;
        break;
      }
    }

    if (res == null)
    {
      res = new TimeSeriesDouble(name, units);
      folder.add(res);
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
