/*****************************************************************************
 *                      J3D.org Copyright (c) 2000
 *                              Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package MWC.GUI.ETOPO;

// Standard imports
// none

// Application specific imports
// none

/**
 * Data representation of geometry information that is created through the
 * various generator classes in this package.
 * <p>
 *
 * This data representation is used to hold information needed to generate
 * geometry from one of the generator classes in this package. In general,
 * data does not get filled in for items that are not requested.
 * <p>
 *
 * The type of data to be produced can be changed with each call. While it is
 * possible to ask for both 2D and 3D texture coordinates, the code will only
 * generate 2D values if asked.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class GeometryData
{
    /** Generate the geometry as individual unindexed triangles */
    public static final int TRIANGLES = 1;

    /** Generate the geometry as individual unindexed quads */
    public static final int QUADS = 2;

    /** Generate the geometry as a triangle strip array(s) */
    public static final int TRIANGLE_STRIPS = 3;

    /** Generate the geometry as a triangle fan array(s) */
    public static final int TRIANGLE_FANS = 4;

    /** Generate the geometry as indexed quads */
    public static final int INDEXED_QUADS = 5;

    /** Generate the geometry as an indexed triangle array */
    public static final int INDEXED_TRIANGLES = 6;

    /** Generate the geometry as an indexed triangle strip array */
    public static final int INDEXED_TRIANGLE_STRIPS = 7;

    /** Generate the geometry as an indexed triangle fan array */
    public static final int INDEXED_TRIANGLE_FANS = 8;

    /** Request for lighting normal data to be produced */
    public static final int NORMAL_DATA = 0x02;

    /** Request for 2D Texture coordinate data to be produced */
    public static final int TEXTURE_2D_DATA = 0x04;

    /** Request for 3D Texture coordinate data to be produced */
    public static final int TEXTURE_3D_DATA = 0x08;

    /** This is the type of geometry that you want to have made */
    public int geometryType = 0;

    /**
     * A generator specific field that describes the type of output
     * algorithm you would like to use for the geometry. May be ignored.
     */
    public int geometrySubType;

    /**
     * The attributes of the geometry you want created. This is an OR'd
     * list of the above variables. It is not possible to generate anything
     * without the raw geometry being computed.
     */
    public int geometryComponents;

    /** The number of vertices stored in the coordinates array */
    public int vertexCount;

    /**
     * Storage for coordinate information. These are stored in flat
     * [x1, y1, z1, x2, y2, z2, ...] configuration
     */
    public float[] coordinates;

    /**
     * Storage for lighting normal information. This should be at least the
     * length of the coordinates array. Data is stored in the same fashion.
     * If normals are requested, the count is the same as vertexCount.
     */
    public float[] normals;

    /** The number of items stored in the indexes array */
    public int indexesCount;

    /** Storage for index information if the shape type requires it. */
    public int[] indexes;

    /** The number of items stored in the strip counts */
    public int numStrips;

    /** Storage for strip counts if the shape type uses it */
    public int[] stripCounts;

    /**
     * Texture coordinate information if requested. May be 2D or 3D depending
     * on the requested type. If 2D the values are stored [s1, t1, s2, t2...]
     * For 3D coordinates it is stores as [r1, s1, t1, r2, s2, t2,...]
     */
    public float[] textureCoordinates;

    /**
     * Colour values if using per-vertex coloring. This array will be identical
     * in length to the coordinate array and index values match etc.
     */
    public float[] colors;
}