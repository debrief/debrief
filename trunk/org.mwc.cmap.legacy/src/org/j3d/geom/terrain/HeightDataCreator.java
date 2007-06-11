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
import java.awt.image.*;

import java.awt.color.ColorSpace;
import javax.vecmath.Color4b;

// Application specific imports
import org.j3d.geom.GeometryData;
import org.j3d.geom.InvalidArraySizeException;
import org.j3d.geom.UnsupportedTypeException;
import org.j3d.util.interpolator.ColorInterpolator;

/**
 * A converter utility for changing an image into a height field set of
 * data points.
 * <p>
 *
 * To translate an image to a height field, the width of the image (X values)
 * translates to the width of the geometry. The height of the image (Y values)
 * is translated to the depth of the terrain. A terrain is always generated
 * in the X-Z plain and height values along the Y axis.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class HeightDataCreator
{
    /** The default minimum height */
    private static final float MIN_HEIGHT = 0;

    /** The default maximum height */
    private static final float MAX_HEIGHT = 1;

    /** The default spacing between cells */
    private static final float DEFAULT_SPACING = 1;

    /** The working minimum height */
    private float minHeight;

    /** The working maximum height */
    private float maxHeight;

    /**
     * Create a default data creator with the min and max heights set to
     * 0 and 1.
     */
    public HeightDataCreator()
    {
        this(MIN_HEIGHT, MAX_HEIGHT);
    }

    /**
     * Create a data creator that has the minimum and maximum heights set
     * but uses the default spacing of the cells.
     *
     * @param min The minimum height of the image
     * @param max The maximum height of the image
     * @throws IllegalArgumentException The minimum was greater than max
     */
    public HeightDataCreator(float min, float max)
    {
        if(max < min)
            throw new IllegalArgumentException("Max < min");

        minHeight = min;
        maxHeight = max;
    }

    /**
     * Set the height range values.
     *
     * @param min The minimum height of the image
     * @param max The maximum height of the image
     * @throws IllegalArgumentException The minimum was greater than max
     */
    public void setHeightRange(float min, float max)
    {
        if(max < min)
            throw new IllegalArgumentException("Max < min");

        minHeight = min;
        maxHeight = max;
    }

    /**
     * Convert an image into a set of terrain points. Ideally, the image
     * should be a greyscale image, but color images are fine. Internally,
     * we convert the color into a greyscale and then use that as the basis
     * for creating the elevation grid.
     * <p>
     * Range values are taken according to the preset values supplied
     * elsewhere.
     *
     * @param img The image to convert
     * @return A height field matching the image
     */
    public float[][] createHeightField(BufferedImage img)
    {
        Raster raster = img.getData();

        // if not greyscale, convert it
        ColorModel color_model = img.getColorModel();
        int components = color_model.getNumComponents();

        if(components > 1)
        {
            ColorSpace cspace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cspace, null);
            BufferedImage output = op.filter(img, null);
            raster = output.getData();
        }

        // Raster should now be a single component set of values.

        int width = raster.getWidth();
        int height = raster.getHeight();

        float[][] ret_val = new float[width][height];
        int[] data = new int[width];

        int i, j;
        float x, y, z;
        float range = maxHeight - minHeight;

        // Fetch data from the image one row at a time. This cuts down on
        // the size of the array that needs to be allocated.
        for(i = 0; i < height; i++)
        {
            raster.getSamples(0, i, width, 1, 0, data);

            for(j = 0; j < width; j++)
            {
                ret_val[i][j] = data[j] * range / 255;
            }
        }

        return ret_val;
    }
}
