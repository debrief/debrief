/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.device.input;

// Standard imports
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.j3d.InputDevice;
import javax.media.j3d.Sensor;
import javax.media.j3d.Transform3D;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Point3d;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

// Application specific imports
import org.j3d.util.SAXEntityResolver;
import org.j3d.util.SAXErrorHandler;

/**
 * An input device that takes information from a file and uses that as a
 * set of points for a sensor.
 * <P>
 *
 * The input device uses an XML file to define a series of data points that
 * may be used to drive the information. To locate this application may define
 * a system property <code>org.j3d.device.input.file.data_file</code> to
 * describe a URL to the file to use. If the file described is a relative
 * description it will attempt to locate it relative to the classpath. If the
 * property is not defined by the application it will look for the file named
 * <code>input_data.xml</code> in the classpath.
 * <p>
 * A limitation of the above system is that there can only be one file input
 * device running at a time.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class FileInputDevice implements InputDevice
{
    /** The system property defining the file to load. */
    private static final String FILE_PROP =
        "org.j3d.device.input.file.data_file";

    /** The name of the default file to load */
    private static final String DEFAULT_FILE_NAME = "input_data.xml";

    // Constants related to the DOM/XML access

    /** Element name describing a sensor data item */
    private static final String DATA_ELEMENT = "data";

    /** Element name describing a sensor button item */
    private static final String SENSOR_BUTTON_ELEMENT = "button";

    /** Element name describing the sensor data collection */
    private static final String SENSOR_INFO_ELEMENT = "sensor-info";

    /** Element name describing for a sensor */
    private static final String SENSOR_ELEMENT = "sensor";

    /** Element name describing a piece of sensor data */
    private static final String SENSOR_DATA_ELEMENT = "sensor-data";

    /** Element name describing a piece of button data */
    private static final String BUTTON_DATA_ELEMENT = "button-data";

    /** Attribute name that holds the number of sensors */
    private static final String NUM_SENSOR_ATTR = "number";

    /** Attribute name that holds index information */
    private static final String INDEX_ATTR = "index";

    /** Attribute name that defines the time for the sensor info */
    private static final String TIME_ATTR = "time";

    /** Attribute name of the position information */
    private static final String POSITION_ATTR = "position";

    /** Attribute name that defines how many times to loop the sensor */
    private static final String SENSOR_LOOP_ATTR = "loops";

    /** Attribute name that defines how many buttons a sensor has */
    private static final String BUTTON_CNT_ATTR = "buttons";

    /** Attribute name defining the button value */
    private static final String BUTTON_VALUE_ATTR = "value";

    // Ordinary variables

    /** The list of sensors to use */
    private FileInputSensor[] sensorList;

    /** The number of sensors */
    private int sensorCount;

    /** The mode we are operating in now */
    private int mode;

    private Transform3D position_tx = new Transform3D();
    private Vector3f translation = new Vector3f();

    /**
     * Construct a new file input device that uses the value of the system
     * property to define it's data. Explicit public constructor needed for
     * reflection.
     */
    public FileInputDevice()
    {
        mode = DEMAND_DRIVEN;
    }

    //---------------------------------------------------------
    // Methods required by the InputDevice
    //---------------------------------------------------------

    /**
     * Initialise this instance. During this step, the initialisation will
     * attempt to load the XML file defined by the system property. If the
     * file fails to load then it will return false.
     *
     * @return true if the initialisation worked
     */
    public boolean initialize()
    {
        URL file_url = findInputFile();

        if(file_url == null)
            return false;

        Document doc = parseInputFile(file_url);

        if(doc == null)
            return false;

        // Zip through the document structure looking for setup information.
        return parseDocument(doc);
    }

    /**
     * Get the number of sensors that this input device contains. If no sensors
     * are registered by the file, this returns zero.
     *
     * @return The number of sensors used
     */
    public int getSensorCount()
    {
        return sensorCount;
    }

    /**
     * Get the sensor at the given index. If the index is out of range an
     * array index exception is given.
     *
     * @param index The sensor to get at the given position
     * @return The sensor at the given index
     * @throws ArrayIndexOutOfBoundsException The index was invalid
     */
    public Sensor getSensor(int index)
    {
        return sensorList[index];
    }

    /**
     * Get the mode using for processing input to this event.
     *
     * @return The current processing mode
     */
    public int getProcessingMode()
    {
        return mode;
    }

    /**
     * Set the processing mode to the given mode.
     *
     * @param mode The mode to now use
     */
    public void setProcessingMode(int mode)
    {
        this.mode = mode;
    }

    /**
     * Set the current position as the default location of the sensor values.
     * For each sensor it will use the current position at this point as the
     * new hotspot on the Sensor object.
     */
    public void setNominalPositionAndOrientation()
    {
        Transform3D read = new Transform3D();
        Point3d hotspot = new Point3d();
        Vector3d trans = new Vector3d();
        Sensor sensor;

        for(int i = 0; i < sensorList.length; i++)
        {
            sensor = sensorList[i];

            sensor.getRead(read);
            read.get(trans);
            hotspot.set(trans);

            sensor.setHotspot(hotspot);
        }
    }

    /**
     * Ignored by instruction of the InputDevice documentation.
     */
    public void processStreamInput()
    {
    }

    /**
     * Process the input now and set the sensor values to a new value. This is
     * called on demand depending on the processing mode by the J3D
     *implementation.
     */
    public void pollAndProcessInput()
    {
        long current_time = System.currentTimeMillis();
        float calc_time = 0;

        for(int i = 0; i < sensorList.length; i++)
            sensorList[i].recalculate(current_time, calc_time);
    }

    /**
     * Close the resources used by this device. This implementation has nothing
     * to do.
     */
    public void close()
    {
    }

    //---------------------------------------------------------
    // Miscellaneous local methods
    //---------------------------------------------------------

    /**
     * Convenience method to find the URL of the file we are going to load.
     *
     * @return The URL of the file to load
     */
    private URL findInputFile()
    {
        URL ret_val = null;

        // Fetch the system property if defined.
        String file_name = System.getProperty(FILE_PROP, DEFAULT_FILE_NAME);

        // Just try to create a URL immediately without checking for a
        // protocol or properly formed name. Easiest way and just catch and
        // ignore the exception.
        try
        {
            ret_val = new URL(file_name);
        }
        catch(MalformedURLException mue)
        {
        }

        if(ret_val == null)
            ret_val = ClassLoader.getSystemResource(file_name);

        // well if that failed, maybe it is a full file definition
        try
        {
            File file = new File(file_name);
            if(file.exists())
                ret_val = file.toURL();
        }
        catch(SecurityException se)
        {
            // This is just in case we find ourselves in an applet environment
            // and we can't read from disk.
            System.err.println("Security exception reading local input file");
        }
        catch(MalformedURLException mue)
        {
        }

        return ret_val;
    }

    /**
     * Convenience method to parse the input file and create a DOM
     * representation of it for later processing.
     *
     * @param url The url of the file to process
     * @return A DOM Document representation of the file
     */
    private Document parseInputFile(URL url)
    {
        Document ret_val = null;

        // Now create an instance of our builder so that we could create new
        // documents or parse existing ones.
        try
        {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

            factory.setIgnoringElementContentWhitespace(true);
            factory.setValidating(false);

            // Try to locate a
            DocumentBuilder builder = null;
            builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new SAXEntityResolver());
            builder.setErrorHandler(new SAXErrorHandler());

            ret_val = builder.parse(url.openStream());
        }
        catch(FactoryConfigurationError fce)
        {
            System.err.println("Error configuring the factory. " + fce);
        }
        catch(ParserConfigurationException pce)
        {
            System.err.println("Error configuring the parser. " + pce);
        }
        catch(IOException ioe)
        {
            System.err.println("Error reading data stream " + ioe);
        }
        catch(SAXException spe)
        {
            System.err.println("Error parsing device information " + spe);
        }

        return ret_val;
    }

    /**
     * Walk the DOM tree and build the internal system state needed.
     *
     * @param doc The document to walk
     * @return false if the document didn't contain enough data
     */
    private boolean parseDocument(Document doc)
    {
        Element root = doc.getDocumentElement();

        NodeList children = root.getElementsByTagName(SENSOR_INFO_ELEMENT);

        if(children.getLength() == 0)
            return false;

        Element sensors_data = (Element)children.item(0);

        // Find out how many sensors are here. If someone has entered a dodgy
        // value or negative value, let the exception propogate out for
        // debugging purposes.
        String value = sensors_data.getAttribute(NUM_SENSOR_ATTR);

        sensorCount = Integer.parseInt(value);
        sensorList = new FileInputSensor[sensorCount];

        children = sensors_data.getElementsByTagName(SENSOR_ELEMENT);
        int size = children.getLength();

        if(size < sensorCount)
        {
            System.err.println("Not enough sensor declarations for the " +
                               "number defined in sensors_data");
            return false;
        }

        for(int i = 0; i < size; i++)
            parseSensorData((Element)children.item(i));

        return true;
    }

    /**
     * Parse the sensor information for a single sensor and build the
     * corresponding data structures.
     *
     * @param sensor The sensor to parse information for
     */
    private void parseSensorData(Element sensor)
    {
        // Before doing anything, go through and build up all the values
        String value = sensor.getAttribute(INDEX_ATTR);
        int index = Integer.parseInt(value);

        value = sensor.getAttribute(BUTTON_CNT_ATTR);
        int buttons = Integer.parseInt(value);

        value = sensor.getAttribute(TIME_ATTR);
        int time = Integer.parseInt(value);

        value = sensor.getAttribute(SENSOR_LOOP_ATTR);
        int loops = Integer.parseInt(value);

        // if the index is dud or out of range, let the exception go
        FileInputSensor file_sensor = new FileInputSensor(this, buttons);
        sensorList[index] = file_sensor;

        // Now let's read the sensor values.
        NodeList data = sensor.getElementsByTagName(SENSOR_DATA_ELEMENT);

        int i, j;
        int old_index;
        int size = data.getLength();
        Element el;
        String component;
        float x, y, z;
        float pos_time;

        for(i = 0; i < size; i++)
        {
            el = (Element)data.item(i);
            value = el.getAttribute(POSITION_ATTR);

            index = value.indexOf(' ');
            component = value.substring(0, index);

            // what to do if we get a unparsable value? Recover and ignore?
            try
            {
                x = Float.parseFloat(component);

                old_index = index;
                index = value.indexOf(' ', index);
                component = value.substring(old_index + 1, index);

                y = Float.parseFloat(component);
                component = value.substring(index + 1);

                z = Float.parseFloat(component);

                pos_time = Float.parseFloat(el.getAttribute(TIME_ATTR));

                file_sensor.addPositionData(x, y, z, pos_time);
            }
            catch(NumberFormatException nfe)
            {
                // ignore it and go to the next attribute.
            }
        }

        // Read the button information.
        data = sensor.getElementsByTagName(SENSOR_BUTTON_ELEMENT);

        // what if this length doesn't equal buttons above?
        size = data.getLength();
        int button_index;
        int button_size;
        int button_state, button_value;
        NodeList button_data;

        for(i = 0; i < size; i++)
        {
            el = (Element)data.item(i);
            value = el.getAttribute(INDEX_ATTR);

            button_index = Integer.parseInt(value);

            button_data = el.getElementsByTagName(BUTTON_DATA_ELEMENT);
            button_size = button_data.getLength();

            for(j = 0; j < button_size; j++)
            {
                try
                {
                    el = (Element)button_data.item(j);
                    pos_time = Float.parseFloat(el.getAttribute(TIME_ATTR));

                    value = el.getAttribute(BUTTON_VALUE_ATTR);
                    button_value = Integer.parseInt(value);

                    file_sensor.addButtonData(button_index,
                                              pos_time,
                                              button_value);
                }
                catch(NumberFormatException nfe)
                {
                    // ignore it and go to the next attribute.
                }
            }
        }
    }
}
