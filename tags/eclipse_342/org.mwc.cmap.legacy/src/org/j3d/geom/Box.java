/*****************************************************************************
 *                   J3D.org Copyright (c) 2000
 *                          Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom;

// Standard imports
import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleStripArray;

// Application specific imports
// none

/**
 * A simple box that uses boxs.
 * <p>
 *
 * The created box does not have any capabilities set except for the
 * ability to write the geometry - needed so that we can modify the geometry
 * when you change the height or radius. If you know that you are not going
 * to be changing the geometry you can turn this off.
 * <P>
 *
 * As we assume you may want to use this as a collidable object, we store the
 * {@link GeometryData} instance that is used to create the object in the
 * userData of the underlying {@link javax.media.j3d.TriangleStripArray}. The
 * geometry does not have texture coordinates set.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class Box extends Shape3D
{
    /** The default dimension of the box */
    private static final float DEFAULT_SIZE = 2;

    /** The generator used to modify the geometry */
    private BoxGenerator generator;

    /** Data used to regenerate the box */
    private GeometryData data;

    /**
     * Construct a default box with no appearance set. The default size
     * of the box is:<BR>
     * Width: 2.0<BR>
     * Height: 2.0<BR>
     * Depth: 2.0<BR>
     */
    public Box()
    {
        this(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE, null);
    }

    /**
     * Construct a default box with the given appearance. The default size
     * of the box is:<BR>
     * Width: 2.0<BR>
     * Height: 2.0<BR>
     * Depth: 2.0<BR>
     *
     * @param app The appearance to use
     */
    public Box(Appearance app)
    {
        this(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE, app);
    }

    /**
     * Construct a default box with no appearance set. The dimensions
     * are set to the given values
     *
     * @param width The width of the box (X Axis)
     * @param height The height of the box (Y Axis)
     * @param depth The depth of the box (Z Axis)
     */
    public Box(float width, float height, float depth)
    {
        this(width, height, depth, null);
    }

    /**
     * Construct a default box with the given appearance and dimensions.
     *
     * @param width The width of the box (X Axis)
     * @param height The height of the box (Y Axis)
     * @param depth The depth of the box (Z Axis)
     * @param app The appearance to use
     */
    public Box(float width, float height, float depth, Appearance app)
    {
        data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLE_STRIPS;
        data.geometryComponents = GeometryData.NORMAL_DATA;

        generator = new BoxGenerator(width, height, depth);

        generator.generate(data);

        int format = TriangleStripArray.COORDINATES |
                     TriangleStripArray.NORMALS;

        TriangleStripArray geometry =
            new TriangleStripArray(data.vertexCount, format, data.stripCounts);

        geometry.setCoordinates(0, data.coordinates);
        geometry.setNormals(0, data.normals);
        geometry.setUserData(data);

        setCapability(ALLOW_GEOMETRY_WRITE);

        setAppearance(app);
        setGeometry(geometry);
    }

    /**
     * Change the radius and height of the box to the new values. If the
     * geometry write capability has been turned off, this will not do
     * anything.
     *
     * @param width The width of the box (X Axis)
     * @param height The height of the box (Y Axis)
     * @param depth The depth of the box (Z Axis)
     */
    public void setDimensions(float width, float height, float depth)
    {
        if(!getCapability(ALLOW_GEOMETRY_WRITE))
            return;

        generator.setDimensions(width, height, depth);
        generator.generate(data);

        int format = TriangleStripArray.COORDINATES |
                     TriangleStripArray.NORMALS;

        TriangleStripArray geometry =
            new TriangleStripArray(data.vertexCount, format, data.stripCounts);

        geometry.setCoordinates(0, data.coordinates);
        geometry.setNormals(0, data.normals);
        geometry.setUserData(data);
        setGeometry(geometry);
    }
}