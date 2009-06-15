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
import javax.vecmath.Color4f;

import org.j3d.util.ColorUtils;

/**
 * An interpolator that works with color components.
 * <P>
 *
 * The interpolation routine is just a simple linear interpolation between
 * each of the points. The interpolator may take arbitrarily spaced keyframes
 * and compute correct values.
 * <p>
 * Color interpolation can be done in the standard RGB space (LINEAR) or using
 * the additional type of HSV_LINEAR. This internally converts all color values
 * to HSV space and then interpolates over that instead.
 * <p>
 *
 * The RGB<->HSV color space conversions have been taken from Foley & van Dam
 * <i>Computer Graphics Principles and Practice, 2nd Edition</i>, Addison
 * Wesley, 1990.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class ColorInterpolator extends Interpolator
{
    /** The message string when s == 0 and h != NaN */
    private static final String INVALID_H_MSG =
        "Invalid h (it has a value) value when s is zero";

    /** The interpolator should be in HSV color space */
    public static final int HSV_SPACE = 1;

    /** The interpolator should be RGB color space */
    public static final int RGB_SPACE = 2;

    /** Reference to the shared Color4f return value for key values */
    private Color4f sharedColor;

    /** Reference to the shared float array return value for key values */
    private float[] sharedVector;

    /**
     * The key values indexed as [index][r, g, b, a] or [index][h, s, v, a],
     * (which is still [x, y, z, w] in the color4f) depending on the color
     * space we are operating in.
     */
    private float[][] keyValues;

    /** The color space to interpolate in */
    private int colorSpace;

    /**
     * Create a new linear interpolator instance with the default size for the
     * number of key values and running in RGB color space.
     */
    public ColorInterpolator()
    {
        this(DEFAULT_SIZE, RGB_SPACE, LINEAR);
    }

    /**
     * Create an linear RGB interpolator with the given basic size.
     *
     * @param size The starting number of items in interpolator
     */
    public ColorInterpolator(int size)
    {
        this(size, RGB_SPACE, LINEAR);
    }

    /**
     * Create a new linear interpolator instance with the default size for the
     * number of key values and selectable color space.
     */
    public ColorInterpolator(int size, int colorSpace)
    {
        this(size, colorSpace, LINEAR);
    }

    /**
     * Create a interpolator with the given basic size using the interpolation
     * type and color space to interpolate over.
     *
     * @param size The starting number of items in interpolator
     * @param type The type of interpolation scheme to use
     */
    public ColorInterpolator(int size, int colorSpace, int type)
    {
        super(size, type);

        keys = new float[size];
        keyValues = new float[size][4];

        sharedColor = new Color4f();
        sharedVector = new float[4];

        this.colorSpace = colorSpace;
    }

    /**
     * Add a key frame set of values at the given key point. This will insert
     * the values at the correct color within the array for the given key.
     * If two keys have the same value, the new key is inserted before the old
     * one.
     *
     * @param key The value of the key to use
     * @param r The r component of the color at this key
     * @param g The g component of the color at this key
     * @param b The b component of the color at this key
     * @param a The alpha component of the color at this key
     */
    public void addRGBKeyFrame(float key, float r, float g, float b, float a)
    {
        if(colorSpace == HSV_SPACE)
        {
            convertRGBtoHSV(r, g, b);
            addKeyFrame(key,
                        sharedVector[0],
                        sharedVector[1],
                        sharedVector[2],
                        a);
        }
        else
        {
            addKeyFrame(key, r, g, b, a);
        }
    }

    /**
     * Add a key frame set of values at the given key point. This will insert
     * the values at the correct color within the array for the given key.
     * If two keys have the same value, the new key is inserted before the old
     * one.
     *
     * @param key The value of the key to use
     * @param h The h component of the color at this key
     * @param s The s component of the color at this key
     * @param v The v component of the color at this key
     * @param a The alpha component of the color at this key
     * @throws IllegalArgumentException s is zero and h is not NaN
     */
    public void addHSVKeyFrame(float key, float h, float s, float v, float a)
    {
        if(colorSpace == RGB_SPACE)
        {
            convertHSVtoRGB(h, s, v);
            addKeyFrame(key,
                        sharedVector[0],
                        sharedVector[1],
                        sharedVector[2],
                        a);
        }
        else
        {
            if((s == 0) && (h != Double.NaN))
                throw new IllegalArgumentException(INVALID_H_MSG);

            addKeyFrame(key, h, s, v, a);
        }
    }

    /**
     * Add a key frame set of values at the given key point. This will insert
     * the values at the correct color within the array for the given key.
     * If two keys have the same value, the new key is inserted before the old
     * one.
     *
     * @param key The value of the key to use
     * @param pt The point data to take information from
     */
    public void addRGBKeyFrame(float key, Color4f pt)
    {
        addRGBKeyFrame(key, pt.x, pt.y, pt.z, pt.w);
    }

    /**
     * Add a key frame set of values at the given key point. This will insert
     * the values at the correct color within the array for the given key.
     * If two keys have the same value, the new key is inserted before the old
     * one.
     *
     * @param key The value of the key to use
     * @param pt The point data to take information from
     * @throws IllegalArgumentException s is zero and h is not NaN
     */
    public void addHSVKeyFrame(float key, Color4f pt)
    {
        addHSVKeyFrame(key, pt.x, pt.y, pt.z, pt.w);
    }

    /**
     * Get the interpolated value of the point at the given key value as an
     * RGB value. If the key lies outside the range of the values defined,
     * it will be clamped to the end point value. For speed reasons, this
     * will return a reusable float array. Do not modify the values or keep a
     * reference to this as it will change values between calls.
     * <p>
     * The value will be interpolated according to the colorspace that was
     * specified in the constructor.
     *
     * @param key The key value to get the color for
     * @return A point representation of the HSV value at that color
     */
    public float[] floatRGBValue(float key)
    {
        floatValue(key);

        if(colorSpace == HSV_SPACE)
        {
            // Convert the key values across to RGB, and the results are
            // also left in the sharedVector. Alpha is never touched.
            convertHSVtoRGB(sharedVector[0],
                            sharedVector[1],
                            sharedVector[2]);
        }

        return sharedVector;
    }

    /**
     * Get the interpolated HSV value of the point at the given key value. If
     * the key lies outside the range of the values defined, it will be clamped
     * to the end point value. For speed reasons, this will return a reusable
     * point instance. Do not modify the values or keep a reference to this as
     * it will change values between calls.
     * <p>
     * The value will be interpolated according to the colorspace that was
     * specified in the constructor.
     *
     * @param key The key value to get the color for
     * @return A point representation of the HSV value at that color
     */
    public Color4f pointRGBValue(float key)
    {
        floatValue(key);

        if(colorSpace == HSV_SPACE)
        {
            // Convert the key values across to RGB, and the results are
            // also left in the sharedVector. Alpha is never touched.
            convertHSVtoRGB(sharedVector[0],
                            sharedVector[1],
                            sharedVector[2]);
        }

        sharedColor.x = sharedVector[0];
        sharedColor.y = sharedVector[1];
        sharedColor.z = sharedVector[2];
        sharedColor.w = sharedVector[3];

        return sharedColor;
    }

    /**
     * Get the interpolated value of the point at the given key value as an
     * RGB value. If the key lies outside the range of the values defined,
     * it will be clamped to the end point value. For speed reasons, this
     * will return a reusable float array. Do not modify the values or keep a
     * reference to this as it will change values between calls.
     * <p>
     * The value will be interpolated according to the colorspace that was
     * specified in the constructor.
     *
     * @param key The key value to get the color for
     * @return A point representation of the HSV value at that color
     */
    public float[] floatHSVValue(float key)
    {
        floatValue(key);

        if(colorSpace == RGB_SPACE)
        {
            // Convert the key values across to RGB, and the results are
            // also left in the sharedVector. Alpha is never touched.
            convertRGBtoHSV(sharedVector[0],
                            sharedVector[1],
                            sharedVector[2]);
        }

        return sharedVector;
    }

    /**
     * Get the interpolated value of the point at the given key value. If the
     * key lies outside the range of the values defined, it will be clamped to
     * the end point value. For speed reasons, this will return a reusable
     * float array. Do not modify the values or keep a reference to this as
     * it will change values between calls.
     *
     * @param key The key value to get the color for
     * @return An array of the values at that color [h, s, v, a]
     */
    public Color4f pointHSVValue(float key)
    {
        floatValue(key);

        if(colorSpace == RGB_SPACE)
        {
            // Convert the key values across to HSV, and the results are
            // also left in the sharedVector. Alpha is never touched.
            convertRGBtoHSV(sharedVector[0],
                            sharedVector[1],
                            sharedVector[2]);
        }

        sharedColor.x = sharedVector[0];
        sharedColor.y = sharedVector[1];
        sharedColor.z = sharedVector[2];
        sharedColor.w = sharedVector[3];

        return sharedColor;
    }

    //---------------------------------------------------------------
    // Misc Internal methods
    //---------------------------------------------------------------

    /**
     * Change an RGB color to HSV color. The value is left in the sharedVector
     * array for copying. We don't bother converting the alpha as that stays
     * the same regardless of color space.
     *
     * @param r The r component of the color at this key
     * @param g The g component of the color at this key
     * @param b The b component of the color at this key
     */
    private void convertRGBtoHSV(float r, float g, float b)
    {
        ColorUtils.convertRGBtoHSV(r, g, b, sharedVector);
    }

    /**
     * Change an RGB color to HSV color. The value is left in the sharedVector
     * array for copying. We don't bother converting the alpha as that stays
     * the same regardless of color space.
     *
     * @param h The h component of the color at this key
     * @param s The s component of the color at this key
     * @param v The v component of the color at this key
     */
    private void convertHSVtoRGB(float h, float s, float v)
    {
        ColorUtils.convertHSVtoRGB(h, s, v, sharedVector);
    }

    /**
     * Internal method to assign a keyframe value into the array regardless of
     * the color space used.
     *
     * @param key The value of the key to use
     * @param x The x component of the color at this key
     * @param y The y component of the color at this key
     * @param z The z component of the color at this key
     * @param a The alpha component of the color at this key
     */
    private void addKeyFrame(float key, float x, float y, float z, float a)
    {
        int loc = findKeyIndex(key);
        realloc();

        if(loc < 0)
          loc = 0;

        float[] new_val;

        if(loc >= currentSize)
        {
            // append to the end
            new_val = keyValues[currentSize];
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

            new_val = new float[4];
            keyValues[loc] = new_val;
        }

        new_val[0] = x;
        new_val[1] = y;
        new_val[2] = z;
        new_val[3] = a;

        keys[loc] = key;
        currentSize++;
    }

    /**
     * Internal method to find and interpolate the value in the array. This
     * is done as a either linear or step and we don't care about the color
     * space we are operating in as how that gets converted from these values
     * is the job of the caller. The interpolated value is left in the
     * sharedVector variable.
     *
     * @param key The key value to get the color for
     */
    private void floatValue(float key)
    {
        int loc = findKeyIndex(key);

        if(loc < 0)
        {
           sharedVector[0] = keyValues[0][0];
           sharedVector[1] = keyValues[0][1];
           sharedVector[2] = keyValues[0][2];
           sharedVector[3] = keyValues[0][3];
        }
        else if(loc >= (currentSize - 1))
        {
           sharedVector[0] = keyValues[currentSize - 1][0];
           sharedVector[1] = keyValues[currentSize - 1][1];
           sharedVector[2] = keyValues[currentSize - 1][2];
           sharedVector[3] = keyValues[currentSize - 1][3];
        }
        else
        {
            switch(interpolationType)
            {
                case LINEAR:
                    float[] p1 = keyValues[loc + 1];
                    float[] p0 = keyValues[loc];

                    // In HSV space, [0] may be NaN. That could end up with
                    // some weird problems. For the moment, let's just leave
                    // it and see if we ever get any bug reports about it.
                    float x_dist = p1[0] - p0[0];
                    float y_dist = p1[1] - p0[1];
                    float z_dist = p1[2] - p0[2];
                    float w_dist = p1[3] - p0[3];

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
System.out.println("r " + p0[0] + " x_dist " + x_dist);
System.out.println("g " + p0[1] + " y_dist " + y_dist);
System.out.println("b " + p0[2] + " z_dist " + z_dist);
System.out.println("a " + p0[2] + " w_dist " + w_dist);
*/
                    sharedVector[0] = p0[0] + fraction * x_dist;
                    sharedVector[1] = p0[1] + fraction * y_dist;
                    sharedVector[2] = p0[2] + fraction * z_dist;
                    sharedVector[3] = p0[3] + fraction * w_dist;
                    break;

                case STEP:
                    float[] pnt = keyValues[loc];
                    sharedVector[0] = pnt[0];
                    sharedVector[1] = pnt[1];
                    sharedVector[2] = pnt[2];
                    sharedVector[3] = pnt[3];
                    break;
            }
        }
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

            for(int i = allocatedSize; i < new_size; i++)
                new_values[i] = new float[4];

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
        StringBuffer buf = new StringBuffer("<color interpolator>\n");

        for(int i = 0; i < currentSize; i++)
        {
            buf.append(i);
            buf.append(" key: ");
            buf.append(keys[i]);
            buf.append(" h: ");
            buf.append(keyValues[i][0]);
            buf.append(" s: ");
            buf.append(keyValues[i][1]);
            buf.append(" v: ");
            buf.append(keyValues[i][2]);
            buf.append(" a: ");
            buf.append(keyValues[i][3]);
            buf.append("\n");
        }

        buf.append("</color interpolator>");
        return buf.toString();
    }
}
