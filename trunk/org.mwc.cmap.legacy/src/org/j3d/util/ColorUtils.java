/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util;

// Standard imports
import javax.vecmath.Color4f;

// Application specific imports
// none

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
public class ColorUtils
{
    /** The message string when s == 0 and h != NaN */
    private static final String INVALID_H_MSG =
        "Invalid h (it has a value) value when s is zero";

    /**
     * Change an RGB color to HSV color. The value is left in the sharedVector
     * array for copying. We don't bother converting the alpha as that stays
     * the same regardless of color space.
     *
     * @param rgb The array of RGB components to convert
     * @param hsv An array to return the colour values with
     */
    public static void convertRGBtoHSV(float[] rgb, float[] hsv)
    {
        convertRGBtoHSV(rgb[0], rgb[1], rgb[2], hsv);
    }

    /**
     * Change an RGB color to HSV color. The value is left in the sharedVector
     * array for copying. We don't bother converting the alpha as that stays
     * the same regardless of color space.
     *
     * @param r The r component of the color at this key
     * @param g The g component of the color at this key
     * @param b The b component of the color at this key
     * @param hsv An array to return the HSV colour values in
     */
    public static void convertRGBtoHSV(float r, float g, float b, float[] hsv)
    {
        float h = 0;
        float s = 0;
        float v = 0;

        float max = (r > g) ? r : g;
        max = (max > b) ? max : b;

        float min = (r < g) ? r : g;
        min = (min < b) ? max : b;

        s = max;    // this is the value v

        // Calculate the saturation s
        if(max != 0)
            s = (max - min) / max;
        else
            s = 0;

        if(s == 0)
        {
            h = Float.NaN;  // h => UNDEFINED
        }
        else
        {
            // Chromatic case: Saturation is not 0, determine hue
            float delta = max - min;

            if(r == max)
            {
                // resulting color is between yellow and magenta
                h = (g - b) / delta ;
            }
            else if(g == max)
            {
                // resulting color is between cyan and yellow
                h = 2 + (b - r) / delta;
            }
            else if(b == max)
            {
                // resulting color is between magenta and cyan
                h = 4 + (r - g) / delta;
            }

            // convert hue to degrees and make sure it is non-negative
            h = h * 60;
            if(h < 0)
                h += 360;
        }

        // now assign everything....
        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = v;
    }

    /**
     * Change an RGB color to HSV color. The value is left in the sharedVector
     * array for copying. We don't bother converting the alpha as that stays
     * the same regardless of color space.
     *
     * @param h The h component of the color at this key
     * @param s The s component of the color at this key
     * @param v The v component of the color at this key
     * @param rgb An array to return the RGB colour values in
     */
    public static void convertHSVtoRGB(float[] hsv, float[] rgb)
    {
        convertHSVtoRGB(hsv[0], hsv[1], hsv[2], rgb);
    }

    /**
     * Change an RGB color to HSV color. The value is left in the sharedVector
     * array for copying. We don't bother converting the alpha as that stays
     * the same regardless of color space.
     *
     * @param h The h component of the color at this key
     * @param s The s component of the color at this key
     * @param v The v component of the color at this key
     * @param rgb An array to return the RGB colour values in
     */
    public static void convertHSVtoRGB(float h, float s, float v, float[] rgb)
    {
        float r = 0;
        float g = 0;
        float b = 0;

        if(s == 0)
        {
            // this color in on the black white center line <=> h = UNDEFINED
            if(h == Float.NaN)
            {
                // Achromatic color, there is no hue
                r = v;
                g = v;
                b = v;
            }
            else
            {
                throw new IllegalArgumentException(INVALID_H_MSG);
            }
        }
        else
        {
            if(h == 360)
            {
                // 360 is equiv to 0
                h = 0;
            }

            // h is now in [0,6)
            h = h /60;

            int i = (int)Math.floor(h);
            float f = h - i;             //f is fractional part of h
            float p = v * (1 - s);
            float q = v * (1 - (s * f));
            float t = v * (1 - (s * (1 - f)));

            switch(i)
            {
                case 0:
                   r = v;
                   g = t;
                   b = p;
                   break;

                case 1:
                   r = q;
                   g = v;
                   b = p;
                   break;

                case 2:
                   r = p;
                   g = v;
                   b = t;
                   break;

                case 3:
                   r = p;
                   g = q;
                   b = v;
                   break;

                case 4:
                   r = t;
                   g = p;
                   b = v;
                   break;

                case 5:
                   r = v;
                   g = p;
                   b = q;
                   break;
            }
        }

        // now assign everything....
        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;
    }
}
