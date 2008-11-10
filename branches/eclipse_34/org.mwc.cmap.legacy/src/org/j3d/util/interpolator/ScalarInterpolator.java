/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util.interpolator;

// Standard imports
import javax.vecmath.Point3f;

// Application specific imports
// none

/**
 * An interpolator that works with scalar values.
 * <P>
 *
 * The interpolation routine is either a stepwise or simple linear
 * interpolation between each of the points. The interpolator may take
 * arbitrarily spaced keyframes and compute correct values.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class ScalarInterpolator extends Interpolator
{
    /** The key values where the indicies match the keys */
    private float[] keyValues;

    /**
     * Create a new linear interpolator instance with the default size for the
     * number of key values.
     */
    public ScalarInterpolator()
    {
        this(DEFAULT_SIZE, LINEAR);
    }

    /**
     * Create a linear interpolator with the given basic size.
     *
     * @param size The starting number of items in interpolator
     */
    public ScalarInterpolator(int size)
    {
        this(size, LINEAR);
    }

    /**
     * Create a interpolator with the given basic size using the interpolation
     * type.
     *
     * @param size The starting number of items in interpolator
     * @param type The type of interpolation scheme to use
     */
    public ScalarInterpolator(int size, int type)
    {
        super(size, type);

        keyValues = new float[size];
    }

    /**
     * Add a key frame set of values at the given key point. This will insert
     * the values at the correct position within the array for the given key.
     * If two keys have the same value, the new key is inserted before the old
     * one.
     *
     * @param key The value of the key to use
     * @param value The scalar value at this key
     */
    public void addKeyFrame(float key, float value)
    {
        int loc = findKeyIndex(key);
        realloc();

        if(loc < 0)
          loc = 0;

        float[] new_val;

        if(loc >= currentSize)
        {
            // append to the end
            keyValues[currentSize] = value;
        }
        else
        {
            // Check to see if the location is the actual key value or it
            // represents a case of this key being between to values in our
            // array. If so, set the location to be +1 from it's current
            if(keys[loc] != key)
                loc++;

            // insert. Shuffle everything up one spot
            int num_moving = currentSize - loc;

            System.arraycopy(keyValues, loc, keyValues, loc + 1, num_moving);
            System.arraycopy(keys, loc, keys, loc + 1, num_moving);

            keyValues[loc] = value;
        }

        keys[loc] = key;
        currentSize++;
    }

    /**
     * Get the interpolated value of the point at the given key value. If the
     * key lies outside the range of the values defined, it will be clamped to
     * the end point value. For speed reasons, this will return a reusable
     * float array. Do not modify the values or keep a reference to this as
     * it will change values between calls.
     *
     * @param key The key value to get the position for
     * @return An array of the values at that position [x, y, z]
     */
    public float floatValue(float key)
    {
        int loc = findKeyIndex(key);
        float ret_val;

        if(loc < 0)
        {
           ret_val = keyValues[0];
        }
        else if(loc >= (currentSize - 1))
        {
           ret_val = keyValues[currentSize - 1];
        }
        else
        {
            switch(interpolationType)
            {
                case LINEAR:
                    float p1 = keyValues[loc + 1];
                    float p0 = keyValues[loc];

                    float dist = p1 - p0;

                    float fraction = 0;

                    // just in case we get two keys the same
                    float prev_key = keys[loc];
                    float found_key = keys[loc + 1];

                    if(found_key != prev_key)
                        fraction = (key - prev_key) / (found_key - prev_key);

/*
System.out.println("Prev key " + prev_key);
System.out.println("Next key " + found_key);
System.out.println("Reqd key " + key);
System.out.println("Fraction is " + fraction);
System.out.println("x " + p0 + " dist " + dist);
*/
                    ret_val = p0 + fraction * dist;
                    break;

                case STEP:
                    ret_val = keyValues[loc];
                    break;

                default:
                    ret_val = 0;
            }
        }

        return ret_val;
    }

    //---------------------------------------------------------------
    // Misc Internal methods
    //---------------------------------------------------------------

    /**
     * Resize the allocated space for the keyValues array if needed. Marked
     * as final in order to encourage the compiler to inline the code for
     * faster execution.
     */
    private final void realloc()
    {
        if(currentSize == allocatedSize)
        {
            int new_size = allocatedSize + ARRAY_INCREMENT;

            // Don't acutally allocate the space for the float[3] values as the
            // arraycopy will set these. Just make sure we allocate after that
            // the remaining new, empty, places.
            float[] new_values = new float[new_size];

            System.arraycopy(keyValues, 0, new_values, 0, allocatedSize);

            float[] new_keys = new float[new_size];

            System.arraycopy(keys, 0, new_keys, 0, allocatedSize);

            keys = new_keys;
            keyValues = new_values;

            allocatedSize = new_size;
        }
    }

    /**
     * Create a string representation of this interpolator's values
     *
     * @return A nicely formatted string representation
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer("<scalar interpolator>\n");

        for(int i = 0; i < currentSize; i++)
        {
            buf.append(i);
            buf.append(" key: ");
            buf.append(keys[i]);
            buf.append(" value: ");
            buf.append("\n");
        }

        buf.append("</scalar interpolator>");
        return buf.toString();
    }
}
