/*****************************************************************************
 *                          J3D.org Copyright (c) 2000
 *                                Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom;

// Standard imports
import javax.vecmath.Vector3f;

// Application specific imports
// none

/**
 * Abstract base representation of geometry generator of box raw coordinate
 * and geometry normals.
 * <p>
 *
 * Curved surfaces would like to generate a smooth object most of the time.
 * To do this, the normal values at each vertex are made to smooth the values
 * for each set of faces that use that value (ie the effect is averaged between
 * all the sharing faces). The typical approach to do this is to work with a
 * value called creaseAngle. If the angle between two surfaces is less that
 * the creaseAngle, a smoothed normal is generated. If greater, the normal is
 * perpendicular to the face. If we are playing with different numbers of
 * facets in an object, this gets rather annoying at times as some pieces may
 * or may not be faceted. At the same time there is a performance hit for
 * generating the normals as you have to check every face and build a lot of
 * extra data before you start doing normal calculations.
 * <p>
 *
 * This library takes a much simplified approach - let the geometry generator
 * implementation doDecide. Our aim is for speed here and if we have to doDecide
 * in a general fashion for every normal calculation, that could be a huge
 * impact.
 * <P>
 *
 * Obvious limitations to this are shapes like the cube or a near-degenerate
 * cone that ends up as a pyramid. The smoothing of normals may be there, but
 * no matter how hard you try, the differences between the face angles will
 * just be too great.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public abstract class GeometryGenerator
{
    /** Working values for the normal generation */
    private Vector3f normal;
    private Vector3f v0;
    private Vector3f v1;

    protected GeometryGenerator()
    {
        v0 = new Vector3f();
        v1 = new Vector3f();
        normal = new Vector3f();
    }

    /**
     * Get the number of vertices that this generator will create for the
     * shape given in the definition.
     *
     * @param data The data to base the calculations on
     * @return The vertex count for the object
     * @throws UnsupportedTypeException The generator cannot handle the type
     *   of geometry you have requested.
     */
    public abstract int getVertexCount(GeometryData data)
        throws UnsupportedTypeException;

    /**
     * Generate a new set of geometry items based on the passed data. If the
     * data does not contain the right minimum array lengths an exception will
     * be generated. If the array reference is null, this will create arrays
     * of the correct length and assign them to the return value.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     * @throws UnsupportedTypeException The generator cannot handle the type
     *   of geometry you have requested
     */
    public abstract void generate(GeometryData data)
        throws UnsupportedTypeException, InvalidArraySizeException;

    /**
     * Convenience method to create a normal for the given vertex coordinates
     * and normal array. This performs a cross product of the two vectors
     * described by the middle and two end points.
     *
     * @param coords The coordinate array to read values from
     * @param p The index of the middle point
     * @param p1 The index of the first point
     * @param p2 The index of the second point
     * @return A temporary value containing the normal value
     */
    protected Vector3f createFaceNormal(float[] coords, int p, int p1, int p2)
    {
        v0.x = coords[p1]     - coords[p];
        v0.y = coords[p1 + 1] - coords[p + 1];
        v0.z = coords[p1 + 2] - coords[p + 2];

        v1.x = coords[p]     - coords[p2];
        v1.y = coords[p + 1] - coords[p2 + 1];
        v1.z = coords[p + 2] - coords[p2 + 2];

        normal.cross(v0, v1);
        normal.normalize();

        return normal;
    }

    /**
     * Create a normal based on the given vertex position, assuming that it is
     * a point in space, relative to the origin. This will create a normal that
     * points directly along the vector from the origin to the point.
     *
     * @param coords The coordinate array to read values from
     * @param p The index of the point to calculate
     * @return A temporary value containing the normal value
     */
    protected Vector3f createRadialNormal(float[] coords, int p)
    {
        float x = coords[p];
        float y = coords[p + 1];
        float z = coords[p + 2];

        float mag = x * x + y * y + z * z;

        if(mag != 0.0)
        {
            mag = 1.0f / ((float) Math.sqrt(mag));
            normal.x = x * mag;
            normal.y = y * mag;
            normal.z = z * mag;
        }
        else
        {
            normal.x = 0;
            normal.y = 0;
            normal.z = 0;
        }

        return normal;
    }

    /**
     * Create a normal based on the given vertex position, assuming that it is
     * a point in space, relative to the given point. This will create a normal
     * that points directly along the vector from the given point to the
     * coordinate.
     *
     * @param coords The coordinate array to read values from
     * @param p The index of the point to calculate
     * @param origin The origin to calculate relative to
     * @param originOffset The offset into the origin array to use
     * @return A temporary value containing the normal value
     */
    protected Vector3f createRadialNormal(float[] coords,
                                          int p,
                                          float[] origin,
                                          int originOffset)
    {
        float x = coords[p] - origin[originOffset];
        float y = coords[p + 1] - origin[originOffset + 1];
        float z = coords[p + 2] - origin[originOffset + 2];

        float mag = x * x + y * y + z * z;

        if(mag != 0.0)
        {
            mag = 1.0f / ((float) Math.sqrt(mag));
            normal.x = x * mag;
            normal.y = y * mag;
            normal.z = z * mag;
        }
        else
        {
            normal.x = 0;
            normal.y = 0;
            normal.z = 0;
        }

        return normal;
    }
}
