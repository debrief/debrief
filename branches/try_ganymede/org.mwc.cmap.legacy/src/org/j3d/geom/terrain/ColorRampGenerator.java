/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.terrain;

// Standard imports
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

// Application specific imports
import org.j3d.geom.GeometryData;
import org.j3d.geom.InvalidArraySizeException;
import org.j3d.util.interpolator.ColorInterpolator;

/**
 * Utility class to create colors per vertex for a terrain model where the
 * colour model is based on a ramp value.
 * <p>
 *
 * This class is designed as a complement to the normal geometry generator
 * classes. The input is an existing, already created piece of geometry and
 * this adds the colour array to the {@link org.j3d.geom.GeometryData}. Colour
 * interpolation is based on using the
 * {@link org.j3d.util.interpolator.ColorInterpolator} running in RGB mode.
 * <p>
 *
 * The alpha channel is optional and the caller has to know if they are
 * supplying values with alpha or not. If alpha is used, the output array is
 * in RGBA format in the array.
 * <p>
 *
 * Values outside the range provided are clamped.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class ColorRampGenerator
{
    /** Error message for different array lengths */
    private static final String LENGTH_MSG =
        "Ramp and height arrays not same length";

    /** The colour interpolator we are using */
    private ColorInterpolator interpolator;

    /** A flag to say whether the colour values included an alpha component */
    private boolean hasAlpha;

    /**
     * Construct a ramp generator with no color information set. The defaults
     * are:<BR>
     * Sea Level: 0<BR>
     * Sea Color: 0, 0, 1<BR>
     * Ground color: 1, 0, 0
     */
    public ColorRampGenerator()
    {
        hasAlpha = false;

        // setup the interpolator to have a very small transition between the
        // sea colour and land color.
        interpolator = new ColorInterpolator(2);
        interpolator.addRGBKeyFrame(0f, 0f, 0f, 1f, 0f);
        interpolator.addRGBKeyFrame(0.5f, 1f, 0f, 0f, 0f);
    }

    /**
     * Create a new colour ramp generator that uses the given heights and
     * 3 component colour values for that height for the interpolation.
     *
     * @param heights The array of heights for each color
     * @param ramp The color values at each height
     * @throws IllegalArgumentException The two arrays have differet length
     */
    public ColorRampGenerator(float[] heights, Color3f[] ramp)
    {
        setColorRamp(heights, ramp);
    }

    /**
     * Create a new colour ramp generator that uses the given heights and
     * 4 component colour values for that height for the interpolation.
     *
     * @param heights The array of heights for each color
     * @param ramp The color values at each height
     * @throws IllegalArgumentException The two arrays have differet length
     */
    public ColorRampGenerator(float[] heights, Color4f[] ramp)
    {
        setColorRamp(heights, ramp);
    }

    /**
     * Set the color data for the ramp to the new 3 component values.
     *
     * @param heights The array of heights for each color
     * @param ramp The color values at each height
     * @throws IllegalArgumentException The two arrays have differet length
     */
    public void setColorRamp(float[] heights, Color3f[] ramp)
    {
        if(heights.length != ramp.length)
            throw new IllegalArgumentException(LENGTH_MSG);

        hasAlpha = false;
        interpolator = new ColorInterpolator(heights.length);

        for(int i = 0; i < heights.length; i++)
        {
            interpolator.addRGBKeyFrame(heights[i],
                                        ramp[i].x,
                                        ramp[i].y,
                                        ramp[i].z,
                                        0);
        }
    }

    /**
     * Set the color data for the ramp to the new 4 component values.
     *
     * @param heights The array of heights for each color
     * @param ramp The color values at each height
     * @throws IllegalArgumentException The two arrays have differet length
     */
    public void setColorRamp(float[] heights, Color4f[] ramp)
    {
        if(heights.length != ramp.length)
            throw new IllegalArgumentException(LENGTH_MSG);

        hasAlpha = true;
        interpolator = new ColorInterpolator(heights.length);

        for(int i = 0; i < heights.length; i++)
        {
            interpolator.addRGBKeyFrame(heights[i],
                                        ramp[i].x,
                                        ramp[i].y,
                                        ramp[i].z,
                                        ramp[i].w);
        }
    }

    /**
     * Generate a new set of colors based on the passed data. If the
     * data does not contain the right minimum array lengths an exception will
     * be generated. If the array reference is null, this will create arrays
     * of the correct length and assign them to the return value.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested colours
     * @throws IllegalArgumentException The vertex array is not defined
     */
    public void generate(GeometryData data)
        throws InvalidArraySizeException
    {
        if(data.vertexCount == 0)
            return;

        int vtx_cnt = data.vertexCount * 3;

        if(hasAlpha)
            vtx_cnt += data.vertexCount;

        if((data.colors != null) && (data.colors.length < vtx_cnt))
            throw new InvalidArraySizeException("Color array",
                                                data.colors.length,
                                                vtx_cnt);

        if(data.colors == null)
            data.colors = new float[vtx_cnt];

        int i;
        float[] col;
        float[] coords = data.coordinates;
        float[] colors = data.colors;

        if(hasAlpha)
        {
            int cnt = vtx_cnt - 1;

            for(i = (vtx_cnt * 3 / 4) - 2; i > 0; i -=3)
            {
                col = interpolator.floatRGBValue(coords[i]);

                colors[cnt--] = col[3];
                colors[cnt--] = col[2];
                colors[cnt--] = col[1];
                colors[cnt--] = col[0];
            }
        }
        else
        {
            for(i = vtx_cnt - 2; i > 0; i -= 3)
            {
                col = interpolator.floatRGBValue(coords[i]);

                colors[i + 1] = col[2];
                colors[i]     = col[1];
                colors[i - 1] = col[0];
            }
        }
    }
}
