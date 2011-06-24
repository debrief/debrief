/*****************************************************************************
 *                      J3D.org Copyright (c) 2000
 *                           Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util.interpolator;

// Standard imports
// none

// Application specific imports
// none

/**
 * An interpolator that works with positional coordinates.
 * <P>
 *
 * The interpolation routine is just a simple linear interpolation between
 * each of the points. The interpolator may take arbitrarily spaced keyframes
 * and compute correct values.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class CoordinateInterpolator extends Interpolator
{

    /** Reference to the shared float array return value for key values */
    private float sharedVector[];

    /** The key values indexed as [index][x, y, z] */
    private float keyValues[][];

    /** The smallest number of items in the value array */
    private int valueLength;

    /**
     * Create a new linear interpolator instance with the default size for the
     * number of key values.
     */
    public CoordinateInterpolator()
    {
        this(DEFAULT_SIZE, LINEAR);
    }

    /**
     * Create an linear interpolator with the given basic size.
     *
     * @param size The starting number of items in interpolator
     */
    public CoordinateInterpolator(int size)
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
    public CoordinateInterpolator(int size, int type)
    {
        super(size, type);

        keys = new float[size];
        keyValues = new float[size][];
        valueLength = -1;
    }

    /**
     * Add a key frame set of values at the given key point. This will insert
     * the values at the correct position within the array for the given key.
     * If two keys have the same value, the new key is inserted before the old
     * one.
     *
     * @param key The value of the key to use
     * @param coords The coordinates at this key
     */
    public void addKeyFrame(float key, float coords[])
    {
        int loc = findKeyIndex(key);

        realloc();

        if(loc < 0)
            loc = 0;

        if(coords == null)
            throw new IllegalArgumentException("Coord array is null");

        int len = coords.length;

        if(len < 3 || len % 3 != 0)
            throw new IllegalArgumentException("Coordinates length not x 3");

        float coords1[] = new float[len];

        System.arraycopy(coords, 0, coords1, 0, len);

        if(valueLength > len || valueLength < 0)
            valueLength = len;

        if(loc >= currentSize)
        {
            keyValues[currentSize] = coords1;
        }
        else
        {
            // Check to see if the location is the actual key value or it
            // represents a case of this key being between to values in our
            // array. If so, set the location to be +1 from it's current
            if(keys[loc] != key)
                loc++;

            int k = currentSize - loc;
            System.arraycopy(keyValues, loc, keyValues, loc + 1, k);
            System.arraycopy(keys, loc, keys, loc + 1, k);

            keyValues[loc] = coords1;
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
    public float[] floatValue(float key)
    {
        if(sharedVector == null || sharedVector.length != valueLength)
            sharedVector = new float[valueLength];

        int loc = findKeyIndex(key);

        if(loc < 0)
            System.arraycopy(keyValues[0], 0, sharedVector, 0, valueLength);
        else if(loc >= currentSize - 1)
            System.arraycopy(keyValues[currentSize - 1], 0, sharedVector, 0, valueLength);
        else
        {
            switch(interpolationType)
            {
                case LINEAR:
                    float p1[] = keyValues[loc + 1];
                    float p0[] = keyValues[loc];
                    float fraction = 0;
                    float prev_key = keys[loc];
                    float next_key = keys[loc + 1];
                    float diff;

                    if(next_key != prev_key)
                        fraction = (key - prev_key) / (next_key - prev_key);

                    for(int j = valueLength; --j > 1; )
                    {
                        diff = p1[j] - p0[j];
                        sharedVector[j] = p0[j] + fraction * diff;
                        j--;
                        diff = p1[j] - p0[j];
                        sharedVector[j] = p0[j] + fraction * diff;
                        j--;
                        diff = p1[j] - p0[j];
                        sharedVector[j] = p0[j] + fraction * diff;
                   }

                    break;

                case STEP:
                    System.arraycopy(keyValues[loc], 0, sharedVector, 0, valueLength);
                    break;
            }
        }

        return sharedVector;
    }

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
            float[][] new_values = new float[new_size][];

            System.arraycopy(keyValues, 0, new_values, 0, allocatedSize);

            float[] new_keys = new float[new_size];

            System.arraycopy(keys, 0, new_keys, 0, allocatedSize);

            keys = new_keys;
            keyValues = new_values;
            allocatedSize = new_size;
        }
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer("<Coordinate interpolator>\n");
        stringbuffer.append("First coord for each key\n");
        for(int i = 0; i < currentSize; i++)
        {
            stringbuffer.append(i);
            stringbuffer.append(" key: ");
            stringbuffer.append(keys[i]);
            stringbuffer.append(" x: ");
            stringbuffer.append(keyValues[i][0]);
            stringbuffer.append(" y: ");
            stringbuffer.append(keyValues[i][1]);
            stringbuffer.append(" z: ");
            stringbuffer.append(keyValues[i][2]);
            stringbuffer.append("\n");
        }

        stringbuffer.append("</Coordinate interpolator>");
        return stringbuffer.toString();
    }
}
