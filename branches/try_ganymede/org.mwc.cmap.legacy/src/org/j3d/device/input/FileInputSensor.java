/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

package org.j3d.device.input;

// Standard imports
import javax.media.j3d.InputDevice;
import javax.media.j3d.Sensor;
import javax.media.j3d.Transform3D;

import javax.vecmath.Vector3f;

// Application specific imports
import org.j3d.util.interpolator.Interpolator;
import org.j3d.util.interpolator.PositionInterpolator;
import org.j3d.util.interpolator.ScalarInterpolator;

/**
 * An extension of the standard Sensor specific to our file input device
 * <P>
 *
 * There is no implementation at the moment. A work in progress.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
class FileInputSensor extends Sensor
{
    /** Default number of SensorRead objects to keep around */
    private static final int NUM_SENSOR_READS = 5;

    /** An empty array of ints for no button values */
    private static final int[] EMPTY_BUTTONS = new int[0];

    /** The interpolator used for the sensor position information */
    private PositionInterpolator positions;

    /** Interpolator list for button actions. Null if no buttons. */
    private ScalarInterpolator[] buttons;

    /** The transform we use to set the sensor read values */
    private Transform3D transform;

    /** A convenience instance of the Vector for calculations */
    private Vector3f vector;

    /**
     * Construct a default Sensor implementation. This has no buttons and the
     * default sensor read size set by the base class
     *
     * @param dev The device that this sensor belongs to
     */
    FileInputSensor(InputDevice dev)
    {
        super(dev);

        positions = new PositionInterpolator();
        transform = new Transform3D();
        vector = new Vector3f();
    }

    /**
     * Construct a sensor that has the given number of buttons. The sensor
     * read count is set to the internal default size: 5
     *
     * @param dev The device that this sensor belongs to
     * @param numButtons The number of buttons this holds
     */
    FileInputSensor(InputDevice dev, int numButtons)
    {
        super(dev, NUM_SENSOR_READS, numButtons);

        positions = new PositionInterpolator();
        transform = new Transform3D();
        vector = new Vector3f();

        buttons = new ScalarInterpolator[numButtons];

        for(int i = numButtons - 1; i >= 0; i--)
            buttons[i] = new ScalarInterpolator(NUM_SENSOR_READS,
                                                Interpolator.STEP);
    }

    /**
     * Add a new item to the positions for positional data for the given
     * time data.
     *
     * @param x The x component of the position
     * @param y The y component of the position
     * @param z The z component of the position
     * @param time The time that this position occurs at
     */
    void addPositionData(float time, float x, float y, float z)
    {
        positions.addKeyFrame(time, x, y, z);
    }

    /**
     * Add button positional information.
     *
     * @param button The button that this information belongs to
     * @param time The time this information occurs
     * @param value The value of the button at this time
     * @throws IllegalArgumentException The button index is invalid or there
     *    are no buttons for this sensor
     */
    void addButtonData(int button, float time, int value)
    {
        if(buttons == null)
            throw new IllegalArgumentException("No buttons are defined!");

        if((button < 0) || (button >= buttons.length))
            throw new IllegalArgumentException("Button index out of range");

        buttons[button].addKeyFrame(time, value);
    }

    /**
     * Calculate the sensor information for this next time.
     *
     * @param time The current time to calculate values for
     */
    void recalculate(long time, float fraction)
    {
        float[] pos = positions.floatValue(fraction);
        vector.set(pos);
        transform.set(vector);

        int[] button_values = EMPTY_BUTTONS;

        if(buttons != null) {
            button_values = new int[buttons.length];

            for(int i = buttons.length - 1; i >= 0; i--)
                button_values[i] = (int)buttons[i].floatValue(fraction);

        }

        setNextSensorRead(time, transform, button_values);
    }
}
