/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util.device;

// Standard imports
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.media.j3d.AudioDevice;
import javax.media.j3d.InputDevice;

import org.j3d.util.ErrorHandler;

import com.sun.j3d.loaders.Loader;

/**
 * A generalised device manager class for loading system resources related
 * to input devices, audio devices and file loaders.
 * <P>
 *
 * This manager serves as the central point for loading and managing all
 * of the resources that are not core to the Java 3D system in an abstract
 * manner.
 * <P>
 *
 * Definitions of the classes to be loaded by this class are kept in the file
 * named <CODE>j3d_devices.properties</CODE>. This class will be located in
 * the CLASSPATH somewhere. If the file cannot be found then any queries will
 * return empty lists.
 * <P>
 *
 * The following properties can be defined in the properties file:
 * <TABLE>
 * <TR><TH>Property</TH><TH>Description</TH></TR>
 * <TR>
 *   <TD><CODE>input.list</CODE></TD>
 *   <TD>The list of names (no whitespace) of input device properties to
 *       read and load.
 *   </TD>
 * </TR>
 * <TR>
 *   <TD><CODE>audio.list</CODE></TD>
 *   <TD>The list of names (no whitespace) of audio device properties to
 *       read and load.
 *   </TD>
 * </TR>
 * <TR>
 *   <TD><CODE>loader.list</CODE></TD>
 *   <TD>The list of names (no whitespace) of file loader properties to
 *       read and load.
 *   </TD>
 * </TR>
 * <TR>
 *   <TD><CODE><I>list item</I>.name</CODE></TD>
 *   <TD>A short text descriptive name of the item to be loaded</TD>
 * </TR>
 * <TR>
 *   <TD><CODE><I>list item</I>.desc</CODE></TD>
 *   <TD>A long text descriptive item of the item to be loaded</TD>
 * </TR>
 * <TR>
 *   <TD><CODE><I>list item</I>.class</CODE></TD>
 *   <TD>The fully qualified class name of the item to be loaded</TD>
 * </TR>
 * <TR>
 *   <TD><CODE><I>list item</I>.mime</CODE></TD>
 *   <TD>The mime type for files handled by the file loader</TD>
 * </TR>
 * <TR>
 *   <TD><CODE><I>list item</I>.ext</CODE></TD>
 *   <TD>The file extension for files loaded by the file loader</TD>
 * </TR>
 * </TABLE>
 * <P>
 *
 * <B>AudioDevices</B>
 * <P>
 *
 * Audio devices must implement the {@link javax.media.j3d.AudioDevice}
 * interface.
 * <P>
 * Properties used by the loader:
 * <UL>
 * <LI><CODE>.name</CODE></LI>
 * <LI><CODE>.desc</CODE></LI>
 * <LI><CODE>.class</CODE></LI>
 * </UL>
 * <P>
 *
 * <B>InputDevices</B>
 * <P>
 *
 * Input devices must implement the {@link javax.media.j3d.InputDevice}
 * interface.
 * <P>
 * Properties used by the loader:
 * <UL>
 * <LI><CODE>.name</CODE></LI>
 * <LI><CODE>.desc</CODE></LI>
 * <LI><CODE>.class</CODE></LI>
 * </UL>
 * <P>
 *
 * <B>File Loaders</B>
 * <P>
 *
 * File loaders must implement the Sun utility interface
 * {@link com.sun.j3d.loaders.Loader}
 *
 * <P>
 * Properties used by the loader:
 * <UL>
 * <LI><CODE>.name</CODE></LI>
 * <LI><CODE>.desc</CODE></LI>
 * <LI><CODE>.class</CODE></LI>
 * <LI><CODE>.mime</CODE></LI>
 * <LI><CODE>.ext</CODE></LI>
 * </UL>
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class DeviceManager
{
  // Constants for the lists of properties

  /** Property defining the list of names of input devices */
  private static final String INPUT_LIST = "input.list";

  /** Property defining the list of names of audio devices */
  private static final String AUDIO_LIST = "audio.list";

  /** Property defining the list of names of file loaders */
  private static final String LOADER_LIST = "loader.list";

  /** Property suffix for the name item */
  private static final String NAME_PART = ".name";

  /** Property suffix for the description item */
  private static final String DESCRIPTION_PART = ".desc";

  /** Property suffix for the class item */
  private static final String CLASS_PART = ".class";

  /** Property suffix for the file mime type item */
  private static final String MIME_PART = ".mime";

  /** Property suffix for the file extension item */
  private static final String EXTENSION_PART = ".ext";


  /** The name of the properties file with all the definition information */
  private static final String PROP_FILE = "j3d_devices.properties";

  /** The singleton instance of this class */
  private static DeviceManager _instance;

  /** The properties class containing the information from the file */
  private Properties device_props;

  /** The error handler used by this class */
  private ErrorHandler error_handler;

  /** The list of input devices available. Null if not constructed yet. */
  private List<DeviceDescriptor> input_devices;

  /** The list of audio devices available. Null if not constructed yet. */
  private List<DeviceDescriptor> audio_devices;

  /** The list of file loaders available. Null if not constructed yet. */
  private List<DeviceDescriptor> file_loaders;

  /**
   * Private constructor to prevent multiple instantiations of this singleton.
   */
  private DeviceManager()
  {
    device_props = new Properties();

    // setup a default handler in case nobody sets one.
    error_handler = new ErrorOutput();

    try
    {
      InputStream is = ClassLoader.getSystemResourceAsStream(PROP_FILE);

      if(is != null)
      {
        device_props.load(is);
      }
      else
      {
        // put in dummy empty values
        device_props.setProperty(INPUT_LIST, null);
        device_props.setProperty(AUDIO_LIST, null);
        device_props.setProperty(LOADER_LIST, null);
      }
    }
    catch(IOException ioe)
    {
      System.err.println("Error: Could not read device definitions");
    }
  }

  /**
   * Fetch the device manager for this application. If the device manager
   * does not already exist it will be instantiated.
   *
   * @return The device manager instance to use
   */
  public static DeviceManager getDeviceManager()
  {
    if(_instance == null)
      _instance = new DeviceManager();

    return _instance;
  }

  /**
   * Close down the device manager and free up all of the resources used by
   * it. Will ensure that all memory associated with it is free, so long as
   * the calling application also frees any resources it might be holding
   * (for example device descriptors). The manager can be restarted again
   * just by calling <CODE>getDeviceManager()</CODE>.
   */
  public static void shutdown()
  {
    if(_instance != null)
    {
      _instance.stop();
      _instance = null;
    }
  }

  /**
   * Set the error handler used by this class. By setting a value of null, it
   * will clear the current handler and re-instate the default one.
   *
   * @param eh The error handler to set
   */
  public void setErrorHandler(ErrorHandler eh)
  {
    if(eh != null)
      error_handler = eh;
    else
      error_handler = new ErrorOutput();
  }

  /**
   * Get a listing of all the input devices known to the system. This will
   * return a list of {@link org.j3d.util.device.InputDeviceDescriptor}. If
   * none are known then the list will be empty.
   *
   * @return A list of all known input devices
   */
  public List<DeviceDescriptor> getAllInputDevices()
  {
    if(input_devices == null)
    {
      List<String> items = listItems(INPUT_LIST);

      if(items.size() == 0)
        input_devices = Collections.emptyList();
      else
      {
        input_devices = new LinkedList<DeviceDescriptor>();
        Iterator<String> itr = items.iterator();

        while(itr.hasNext())
        {
          String device = ((String)itr.next()).trim();

          String name = device_props.getProperty(device + NAME_PART);
          String desc = device_props.getProperty(device + DESCRIPTION_PART);
          String cls = device_props.getProperty(device + CLASS_PART);

          input_devices.add(new InputDeviceDescriptor(name, desc, cls));
        }
      }
    }

    return input_devices;
  }

  /**
   * Get the named input device from the system. Using the given descroiptor
   * an instance is loaded. If an instance already exists, that is returned.
   * If no instances exist, a new one is created, registered and returned to
   * the caller.
   *
   * @param idd The descriptor for the input device
   * @return The corresponding input device instance
   * @throws NullPointerException The descriptor given was null
   */
  public InputDevice getInputDevice(InputDeviceDescriptor idd)
  {
    InputDevice dev = (InputDevice)idd.getDevice();

    if(dev == null)
    {
      String cls = idd.getClassName();

      try
      {
        dev =
          (InputDevice)DynamicClassLoader.loadCheckedClass(cls,
                                                           InputDevice.class);
      }
      catch(Exception e)
      {
        error_handler.writeError("Unable to find class", e);
      }
    }

    return dev;
  }

  /**
   * Release the named InputDevice from the system. The caller no longer
   * needs this device and is asking the manager to clean up any resources
   * associated with it.
   *
   * @param idd The descriptor for the device
   */
  public void releaseInputDevice(InputDeviceDescriptor idd)
  {
    idd.setDevice(null);
  }

  /**
   * Get a listing of all the audio devices known to the system. This will
   * return a list of {@link org.j3d.util.device.AudioDeviceDescriptor}. If
   * none are known then the list will be empty.
   *
   * @return A list of all known input devices
   */
  public List<DeviceDescriptor> getAllAudioDevices()
  {
    if(audio_devices == null)
    {
    	List<String> items = listItems(AUDIO_LIST);

      if(items.size() == 0)
        audio_devices = Collections.emptyList();
      else
      {
        audio_devices = new LinkedList<DeviceDescriptor>();
        Iterator<String> itr = items.iterator();

        while(itr.hasNext())
        {
          String device = ((String)itr.next()).trim();

          String name = device_props.getProperty(device + NAME_PART);
          String desc = device_props.getProperty(device + DESCRIPTION_PART);
          String cls = device_props.getProperty(device + CLASS_PART);

          audio_devices.add(new AudioDeviceDescriptor(name, desc, cls));
        }
      }
    }

    return audio_devices;
  }

  /**
   * Get the named audio device from the system. Using the given descroiptor
   * an instance is loaded. If an instance already exists, that is returned.
   * If no instances exist, a new one is created, registered and returned to
   * the caller.
   *
   * @param add The descriptor for the audio device
   * @return The corresponding audio device instance
   * @throws NullPointerException The descriptor given was null
   */
  public AudioDevice getAudioDevice(AudioDeviceDescriptor add)
  {
    AudioDevice dev = (AudioDevice)add.getDevice();

    if(dev == null)
    {
      String cls = add.getClassName();

      try
      {
        dev =
          (AudioDevice)DynamicClassLoader.loadCheckedClass(cls,
                                                           AudioDevice.class);
      }
      catch(Exception e)
      {
        error_handler.writeError("Unable to find class", e);
      }
    }

    return dev;
  }

  /**
   * Release the named AudioDevice from the system. The caller no longer
   * needs this device and is asking the manager to clean up any resources
   * associated with it.
   *
   * @param add The descriptor for the device
   */
  public void releaseAudioDevice(AudioDeviceDescriptor add)
  {
    add.setDevice(null);
  }

  /**
   * Get a listing of all the file loaders known to the system. This will
   * return a list of {@link org.j3d.util.device.FileLoaderDescriptor}. If
   * none are known then the list will be empty.
   *
   * @return A list of all known file loaders
   */
  public List<DeviceDescriptor> getAllFileLoaders()
  {
    if(file_loaders == null)
    {
    	List<String> items = listItems(LOADER_LIST);

      if(items.size() == 0)
        file_loaders = Collections.emptyList();
      else
      {
        file_loaders = new LinkedList<DeviceDescriptor>();
        Iterator<String> itr = items.iterator();

        while(itr.hasNext())
        {
          String device = ((String)itr.next()).trim();

          String name = device_props.getProperty(device + NAME_PART);
          String desc = device_props.getProperty(device + DESCRIPTION_PART);
          String cls = device_props.getProperty(device + CLASS_PART);
          String mime = device_props.getProperty(device + MIME_PART);
          String ext = device_props.getProperty(device + EXTENSION_PART);

          FileLoaderDescriptor fld =
            new FileLoaderDescriptor(name, desc, cls, mime, ext);

          file_loaders.add(fld);
        }
      }
    }

    return file_loaders;
  }

  /**
   * Get the named file loader from the system. Using the given descroiptor
   * an instance is loaded. If an instance already exists, that is returned.
   * If no instances exist, a new one is created, registered and returned to
   * the caller.
   *
   * @param fld The descriptor for the file loader
   * @return The corresponding file loader instance
   * @throws NullPointerException The descriptor given was null
   */
  public Loader getFileLoader(FileLoaderDescriptor fld)
  {
    Loader dev = (Loader)fld.getDevice();

    if(dev == null)
    {
      String cls = fld.getClassName();

      try
      {
        dev = (Loader)DynamicClassLoader.loadCheckedClass(cls, Loader.class);
      }
      catch(Exception e)
      {
        error_handler.writeError("Unable to find class", e);
      }
    }

    return dev;
  }

  /**
   * Release the named Loader from the system. The caller no longer
   * needs this device and is asking the manager to clean up any resources
   * associated with it.
   *
   * @param fld The descriptor for the device
   */
  public void releaseFileLoader(FileLoaderDescriptor fld)
  {
    fld.setDevice(null);
  }

  /**
   * List the individual items under a particular property list. The property
   * name will contain a pipe char '|' separated list of characters which is
   * stripped to individual items and returned as a list. If there are no
   * items in the list then an empty list is returned.
   *
   * @param prop The property name to work with
   * @return A list of the items in that property value
   */
  private List<String> listItems(String prop)
  {
    List<String> ret_val = new LinkedList<String>();

    String item_list = device_props.getProperty(prop);

    if(item_list != null)
    {
      StringTokenizer strtok = new StringTokenizer(item_list, "|");
      while(strtok.hasMoreTokens())
      {
        ret_val.add(strtok.nextToken());
      }
    }

    return ret_val;
  }

  /**
   * Close down the instance of this manager. Will free all of the resources
   * needed to keep it running.
   */
  private void stop()
  {
    device_props.clear();
    device_props = null;

    error_handler = null;

    if(input_devices != null)
    {
      input_devices.clear();
      input_devices = null;
    }

    if(audio_devices != null)
    {
      audio_devices.clear();
      audio_devices = null;
    }

    if(file_loaders != null)
    {
      file_loaders.clear();
      file_loaders = null;
    }
  }
}
