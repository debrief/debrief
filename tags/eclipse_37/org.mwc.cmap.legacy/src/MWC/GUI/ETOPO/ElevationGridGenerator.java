/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package MWC.GUI.ETOPO;

// Standard imports
import javax.vecmath.Vector3f;

// Application specific imports
//import org.j3d.geom.GeometryGenerator;
//import org.j3d.geom.GeometryData;
//import org.j3d.geom.InvalidArraySizeException;
//import org.j3d.geom.UnsupportedTypeException;
//import org.j3d.util.interpolator.ColorInterpolator;

/**
 * A generator that takes a set of height values as a grid and turns it into
 * geometry.
 * <p>
 *
 * The grid can be created either as absolute or relative height values. This
 * setting is controlled as one of the auxillary flags in the
 * {@link org.j3d.geom.GeometryData} class at construction time. In order for
 * this to work, you will also need to provide a base height when setting
 * the terrain.
 * <p>
 *
 * Points are defined in the height arrays in width first order. Normals, are
 * always smooth blended.
 *
 * Alan: There are some cases where texture generation is not complete.
 * Especially in regards to 3D textures.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class ElevationGridGenerator extends GeometryGenerator
{
    /** Auxillary flag to say to generate points as relative values */
    public static final int RELATIVE_HEIGHTS = 0x01;

    /** Auxillary flag to say to generate points as absolute values */
    public static final int ABSOLUTE_HEIGHTS = 0x02;

    /** The default size of the terrain */
    private static final float DEFAULT_SIZE = 100;

    /** The default number of points in each direction */
    private static final int DEFAULT_POINT_COUNT = 2;

    /** The default base height of the terrain */
    private static final float DEFAULT_HEIGHT = 2;

    /** Current width of the terrain */
    private float terrainWidth;

    /** Depth of the terrain to generate */
    private float terrainDepth;

    /** Number of points in the width direction */
    private int widthPoints;

    /** Number of points in the depth direction */
    private int depthPoints;

    /** The basic height when calculating relative values */
    private float baseHeight;

    /** The points to use as a 1D array. */
    private float[] flatHeights;

    /** The points to use as a 2D array. */
    private float[][] arrayHeights;

    /** The number of terrain coordinates in use */
    private int numTerrainValues;

    /** The number of texture coordinates in use */
    private int numTexcoordValues;

    /** The array holding all of the vertices after use */
    private float[] terrainCoordinates;

    /** The array holding all of the normals after use */
    private float[] terrainNormals;

    /** The array holding all of the texture coordinates after use */
    private float[] terrainTexcoords;

    /** Flag to indicate the terrain values have changed */
    private boolean terrainChanged;

    /** Flag to indicate the terrain values have changed */
    private boolean normalsChanged;

    /** Flag to indicate the terrain values have changed */
    private boolean texcoordsChanged;

    /** The number of quads in the terrain */
    private int facetCount;

    /**
     * Construct a default terrain with the following properties:<BR>
     * Size: 100x100
     * Points: 2x2
     */
    public ElevationGridGenerator()
    {
        this(DEFAULT_SIZE,
             DEFAULT_SIZE,
             DEFAULT_POINT_COUNT,
             DEFAULT_POINT_COUNT,
             (float[])null,
             DEFAULT_HEIGHT);
    }

    /**
     * Construct a default terrain with the given dimensions and points
     * in each direction.
     *
     * @param w The width of the terrain
     * @param d The depth of the terrain
     * @param wPnts The number of heights in the width
     * @param dPnts The number of heights in the depth
     * @throws IllegalArgumentException One of the points were <= 1 or the
     *   dimensions are non-positive
     */
    public ElevationGridGenerator(float w, float d, int wPnts, int dPnts)
    {
        this(w, d, wPnts, dPnts, (float[])null, DEFAULT_HEIGHT);
    }

    /**
     * Construct a customised terrain according to the full set of configurable
     * data.
     *
     * @param w The width of the terrain
     * @param d The depth of the terrain
     * @param wPnts The number of heights in the width
     * @param dPnts The number of heights in the depth
     * @param heights The array of height values to use
     * @param baseHeight The base height for relative calcs. May be zero
     * @throws IllegalArgumentException One of the points were <= 1 or the
     *   dimensions are non-positive
     */
    public ElevationGridGenerator(float w,
                                  float d,
                                  int wPnts,
                                  int dPnts,
                                  float[] heights,
                                  float baseHeight)
    {
        if((wPnts < 2) || (dPnts < 2))
            throw new IllegalArgumentException("Point count <= 1");

        if((w <= 0) || (d <= 0))
            throw new IllegalArgumentException("Dimension <= 0");

        terrainWidth = w;
        terrainDepth = d;
        widthPoints = wPnts;
        depthPoints = dPnts;

        facetCount = (depthPoints - 1) * (widthPoints - 1);

        flatHeights = heights;
        this.baseHeight = baseHeight;

        terrainChanged = true;
        normalsChanged = true;
        texcoordsChanged = true;
    }

    /**
     * Construct a default cylinder with the option of having end caps and
     * selectable number of faces around the radius. The default height is 2
     * and radius 1.The minimum number of facets is 3.
     *
     * @param w The width of the terrain
     * @param d The depth of the terrain
     * @param wPnts The number of heights in the width
     * @param dPnts The number of heights in the depth
     * @param heights The array of height values to use
     * @param baseHeight The base height for relative calcs. May be zero
     * @throws IllegalArgumentException One of the points were <= 1 or the
     *   dimensions are non-positive
     */
    public ElevationGridGenerator(float w,
                                  float d,
                                  int wPnts,
                                  int dPnts,
                                  float[][] heights,
                                  float baseHeight)
    {
        if((wPnts < 2) || (dPnts < 2))
            throw new IllegalArgumentException("Point count <= 1");

        if((w <= 0) || (d <= 0))
            throw new IllegalArgumentException("Dimension <= 0");

        terrainWidth = w;
        terrainDepth = d;
        widthPoints = wPnts;
        depthPoints = dPnts;

        facetCount = (depthPoints - 1) * (widthPoints - 1);

        arrayHeights = heights;
        this.baseHeight = baseHeight;

        terrainChanged = true;
        normalsChanged = true;
        texcoordsChanged = true;
    }

    /**
     * Get the dimensions of the terrain. These are returned as 2 values of
     * width and depth respectively for the array. A new array is
     * created each time so you can do what you like with it.
     *
     * @return The current size of the terrain
     */
    public float[] getDimensions()
    {
        return new float[] { terrainWidth, terrainDepth };
    }

    /**
     * Change the dimensions of the cone to be generated. Calling this will
     * make the points be re-calculated next time you ask for geometry or
     * normals.
     *
     * @param w The width of the terrain
     * @param d The depth of the terrain
     * @param wPnts The number of heights in the width
     * @param dPnts The number of heights in the depth
     * @throws IllegalArgumentException One of the points were <= 1 or the
     *   dimensions are non-positive
     */
    public void setDimensions(float w, float d, int wPnts, int dPnts)
    {
        if((terrainWidth != w) || (terrainDepth != d))
        {
            terrainChanged = true;
            normalsChanged = true;
            texcoordsChanged = true;
            terrainDepth = d;
            terrainWidth = w;
        }

        if((wPnts != widthPoints) || (dPnts != depthPoints))
        {
            terrainChanged = true;
            normalsChanged = true;
            texcoordsChanged = true;
            widthPoints = wPnts;
            depthPoints = dPnts;

            facetCount = (depthPoints - 1) * (widthPoints - 1);
        }
    }

    /**
     * Set the details of the terrain height to use a flat array of values.
     *
     * @param heights The array of height values to use
     * @param baseHeight The base height for relative calcs. May be zero
     */
    public void setTerrainDetail(float[] heights, float baseHeight)
    {
        flatHeights = heights;
        this.baseHeight = baseHeight;

        arrayHeights = null;

        terrainChanged = true;
        normalsChanged = true;
    }

    /**
     * Set the details of the terrain height to use a 2D array of values.
     *
     * @param heights The array of height values to use
     * @param baseHeight The base height for relative calcs. May be zero
     */
    public void setTerrainDetail(float[][] heights, float baseHeight)
    {
        arrayHeights = heights;
        this.baseHeight = baseHeight;

        flatHeights = null;

        terrainChanged = true;
        normalsChanged = true;
    }

    /**
     * Get the number of vertices that this generator will create for the
     * shape given in the definition based on the current width and height
     * information.
     *
     * @param data The data to base the calculations on
     * @return The vertex count for the object
     * @throws UnsupportedTypeException The generator cannot handle the type
     *   of geometry you have requested.
     */
    public int getVertexCount(GeometryData data)
        throws UnsupportedTypeException
    {
        int ret_val = 0;

        switch(data.geometryType)
        {
            case GeometryData.TRIANGLES:
                ret_val = facetCount * 6;
                break;

            case GeometryData.QUADS:
                ret_val = facetCount * 4;
                break;

            // These all have the same vertex count
            case GeometryData.TRIANGLE_STRIPS:
                ret_val = widthPoints * 2 * (depthPoints - 1);
                break;

            case GeometryData.TRIANGLE_FANS:
                ret_val = facetCount * 4;

            case GeometryData.INDEXED_TRIANGLES:
            case GeometryData.INDEXED_QUADS:
            case GeometryData.INDEXED_TRIANGLE_STRIPS:
            case GeometryData.INDEXED_TRIANGLE_FANS:
                ret_val = facetCount * 2;
                break;

            default:
                throw new UnsupportedTypeException("Unknown geometry type: " +
                                                   data.geometryType);
        }

        return ret_val;
    }

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
    public void generate(GeometryData data)
        throws UnsupportedTypeException, InvalidArraySizeException
    {
        switch(data.geometryType)
        {
            case GeometryData.TRIANGLES:
                unindexedTriangles(data);
                break;
            case GeometryData.QUADS:
                unindexedQuads(data);
                break;
            case GeometryData.TRIANGLE_STRIPS:
                triangleStrips(data);
                break;
            case GeometryData.TRIANGLE_FANS:
                triangleFans(data);
                break;
            case GeometryData.INDEXED_QUADS:
                indexedQuads(data);
                break;
            case GeometryData.INDEXED_TRIANGLES:
                indexedTriangles(data);
                break;
            case GeometryData.INDEXED_TRIANGLE_STRIPS:
                indexedTriangleStrips(data);
                break;
            case GeometryData.INDEXED_TRIANGLE_FANS:
                indexedTriangleFans(data);
                break;

            default:
                throw new UnsupportedTypeException("Unknown geometry type: " +
                                                   data.geometryType);
        }
    }

    /**
     * Generate a new set of points for an unindexed quad array
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void unindexedTriangles(GeometryData data)
        throws InvalidArraySizeException
    {
        generateUnindexedTriCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateUnindexedTriNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTriTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);
    }


    /**
     * Generate a new set of points for an unindexed quad array
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void unindexedQuads(GeometryData data)
        throws InvalidArraySizeException
    {
        generateUnindexedQuadCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateUnindexedQuadNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateUnindexedQuadTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);
    }

    /**
     * Generate a new set of points for an indexed quad array. Uses the same
     * points as an indexed triangle, but repeats the top coordinate index.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void indexedQuads(GeometryData data)
        throws InvalidArraySizeException
    {
        generateIndexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateIndexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTriTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);

        // now let's do the index list
        int index_size = data.vertexCount * 4;

        if(data.indexes == null)
            data.indexes = new int[index_size];
        else if(data.indexes.length < index_size)
            throw new InvalidArraySizeException("Coordinates",
                                                data.indexes.length,
                                                index_size);

        int[] indexes = data.indexes;
        data.indexesCount = index_size;
        int idx = 0;
        int vtx = 0;

        // each face consists of an anti-clockwise
        for(int i = facetCount; --i >= 0; )
        {
            indexes[idx++] = vtx;
            indexes[idx++] = vtx + widthPoints;
            indexes[idx++] = vtx + widthPoints + 1;
            indexes[idx++] = vtx + 1;

            vtx++;

            if((i % (widthPoints - 1)) == 0)
                vtx++;
        }
    }

    /**
     * Generate a new set of points for an indexed triangle array
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void indexedTriangles(GeometryData data)
        throws InvalidArraySizeException
    {
        generateIndexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateIndexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTriTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);

        // now let's do the index list
        int index_size = data.vertexCount * 6;

        if(data.indexes == null)
            data.indexes = new int[index_size];
        else if(data.indexes.length < index_size)
            throw new InvalidArraySizeException("Coordinates",
                                                data.indexes.length,
                                                index_size);

        int[] indexes = data.indexes;
        data.indexesCount = index_size;
        int idx = 0;
        int vtx = 0;

        // each face consists of an anti-clockwise
        for(int i = facetCount; --i >= 0; )
        {
            // triangle 1
            indexes[idx++] = vtx;
            indexes[idx++] = vtx + widthPoints + 1;
            indexes[idx++] = vtx + 1;

            // triangle 2
            indexes[idx++] = vtx + widthPoints;
            indexes[idx++] = vtx + widthPoints + 1;
            indexes[idx++] = vtx;

            vtx++;

            if((i % (widthPoints - 1)) == 0)
                vtx++;
        }
    }

    /**
     * Generate a new set of points for a triangle strip array. There is one
     * strip for the side and one strip each for the ends.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void triangleStrips(GeometryData data)
        throws InvalidArraySizeException
    {
        generateUnindexedTriStripCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateUnindexedTriStripNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateUnindexedTriStripTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);

        int num_strips = depthPoints - 1;

        if(data.stripCounts == null)
            data.stripCounts = new int[num_strips];
        else if(data.stripCounts.length < num_strips)
            throw new InvalidArraySizeException("Strip counts",
                                                data.stripCounts.length,
                                                num_strips);

        for(int i = num_strips; --i >= 0; )
            data.stripCounts[i] = widthPoints * 2;
    }

    /**
     * Generate a new set of points for a triangle fan array. Each facet on the
     * side of the cylinder is a single fan, but the ends are one big fan each.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void triangleFans(GeometryData data)
        throws InvalidArraySizeException
    {
        generateUnindexedTriFanCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateUnindexedTriFanNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTriTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);

        int num_strips = facetCount;

        if(data.stripCounts == null)
            data.stripCounts = new int[num_strips];
        else if(data.stripCounts.length < num_strips)
            throw new InvalidArraySizeException("Strip counts",
                                                data.stripCounts.length,
                                                num_strips);

        for(int i = facetCount; --i >= 0; )
            data.stripCounts[i] = 4;
    }

    /**
     * Generate a new set of points for an indexed triangle strip array. We
     * build the strip from the existing points starting by working around the
     * side and then doing the top and bottom. To create the ends we start at
     * on radius point and then always refer to the center for each second
     * item. This wastes every second triangle as a degenerate triangle, but
     * the gain is less strips needing to be transmitted - ie less memory
     * usage.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void indexedTriangleStrips(GeometryData data)
        throws InvalidArraySizeException
    {
        generateIndexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateIndexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTriTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);

        // now let's do the index list
        int index_size = widthPoints * (depthPoints - 1) * 2;
        int num_strips = depthPoints - 1;

        if(data.indexes == null)
            data.indexes = new int[index_size];
        else if(data.indexes.length < index_size)
            throw new InvalidArraySizeException("Indexes",
                                                data.indexes.length,
                                                index_size);

        if(data.stripCounts == null)
            data.stripCounts = new int[num_strips];
        else if(data.stripCounts.length < num_strips)
            throw new InvalidArraySizeException("Strip counts",
                                                data.stripCounts.length,
                                                num_strips);

        int[] indexes = data.indexes;
        int[] stripCounts = data.stripCounts;
        data.indexesCount = index_size;
        data.numStrips = num_strips;
        int idx = 0;
        int vtx = 0;
        int total_points = widthPoints * (depthPoints - 1);

        // The side is one big strip
        for(int i = total_points; --i >= 0; )
        {
            indexes[idx++] = vtx;
            indexes[idx++] = vtx + widthPoints;

            vtx++;
        }

        for(int i = num_strips; --i >= 0; )
            stripCounts[i] = widthPoints * 2;
    }

    /**
     * Generate a new set of points for an indexed triangle fan array. We
     * build the strip from the existing points, and there's no need to
     * re-order the points for the indexes this time. As for the simple fan,
     * we use the first index, the lower-right corner as the apex for the fan.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void indexedTriangleFans(GeometryData data)
        throws InvalidArraySizeException
    {
        generateIndexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateIndexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTriTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);

        // now let's do the index list
        int index_size = facetCount * 4;
        int num_strips = facetCount;

        if(data.indexes == null)
            data.indexes = new int[index_size];
        else if(data.indexes.length < index_size)
            throw new InvalidArraySizeException("Indexes",
                                                data.indexes.length,
                                                index_size);

        if(data.stripCounts == null)
            data.stripCounts = new int[num_strips];
        else if(data.stripCounts.length < num_strips)
            throw new InvalidArraySizeException("Strip counts",
                                                data.stripCounts.length,
                                                num_strips);

        int[] indexes = data.indexes;
        int[] stripCounts = data.stripCounts;
        data.indexesCount = index_size;
        data.numStrips = num_strips;
        int idx = 0;
        int vtx = 0;

        // each face consists of an anti-clockwise quad
        for(int i = facetCount; --i >= 0; )
        {
            indexes[idx++] = vtx + widthPoints;
            indexes[idx++] = vtx + widthPoints + 1;
            indexes[idx++] = vtx + 1;
            indexes[idx++] = vtx;

            stripCounts[i] = 4;

            vtx++;

            if((i % (widthPoints - 1)) == 0)
                vtx++;
        }
    }

    //------------------------------------------------------------------------
    // Coordinate generation routines
    //------------------------------------------------------------------------

    /**
     * Generates new set of unindexed points for triangles. The array consists
     * of the side coordinates, followed by the top and bottom.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = depthPoints * widthPoints * 6;

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;

        regenerateBase();

        int count = 0;
        int i = 0;
        int base_count = 0;
        int width_inc = widthPoints * 3;

        // Start of with one less row (width) here because we don't have two
        // sets of coordinates for those.
        for(i = facetCount; --i >= 0; )
        {
            // triangle 1
            coords[count++] = terrainCoordinates[base_count];
            coords[count++] = terrainCoordinates[base_count + 1];
            coords[count++] = terrainCoordinates[base_count + 2];

            coords[count++] = terrainCoordinates[base_count + width_inc];
            coords[count++] = terrainCoordinates[base_count + width_inc + 1];
            coords[count++] = terrainCoordinates[base_count + width_inc + 2];

            coords[count++] = terrainCoordinates[base_count + 3];
            coords[count++] = terrainCoordinates[base_count + 4];
            coords[count++] = terrainCoordinates[base_count + 5];

            // triangle 2
            coords[count++] = terrainCoordinates[base_count + width_inc];
            coords[count++] = terrainCoordinates[base_count + width_inc + 1];
            coords[count++] = terrainCoordinates[base_count + width_inc + 2];

            coords[count++] = terrainCoordinates[base_count + width_inc + 3];
            coords[count++] = terrainCoordinates[base_count + width_inc + 4];
            coords[count++] = terrainCoordinates[base_count + width_inc + 5];

            coords[count++] = terrainCoordinates[base_count + 3];
            coords[count++] = terrainCoordinates[base_count + 4];
            coords[count++] = terrainCoordinates[base_count + 5];

            base_count += 3;

            if((i % (widthPoints - 1)) == 0)
                base_count += 3;
        }

        checkRelativeHeights(data.geometrySubType, coords, vtx_cnt);
    }

    /**
     * Generates new set of unindexed points for triangles strips. The array
     * consists of one strip per width row.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriStripCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = widthPoints * (depthPoints - 1) * 2;

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;

        regenerateBase();

        int i;
        int count = 0;
        int base_count = 0;
        int width_inc = widthPoints * 3;
        int total_points = widthPoints * (depthPoints - 1);

        // Start of with one less row (width) here because we don't have two
        // sets of coordinates for those.
        for(i = total_points; --i >= 0; )
        {
            coords[count++] = terrainCoordinates[base_count];
            coords[count++] = terrainCoordinates[base_count + 1];
            coords[count++] = terrainCoordinates[base_count + 2];

            coords[count++] = terrainCoordinates[base_count + width_inc];
            coords[count++] = terrainCoordinates[base_count + width_inc + 1];
            coords[count++] = terrainCoordinates[base_count + width_inc + 2];

            base_count += 3;
        }

        checkRelativeHeights(data.geometrySubType, coords, vtx_cnt);
    }

    /**
     * Generates new set of unindexed points for triangle fans. For each
     * facet on the side we have one fan. For each end there is a single
     * fan.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriFanCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = facetCount * 4;

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;

        regenerateBase();

        int count = 0;
        int i = 0;
        int base_count = 0;
        int width_inc = widthPoints * 3;

        // Start of with one less row (width) here because we don't have two
        // sets of coordinates for those.
        for(i = facetCount; --i >= 0; )
        {
            coords[count++] = terrainCoordinates[base_count + width_inc];
            coords[count++] = terrainCoordinates[base_count + width_inc + 1];
            coords[count++] = terrainCoordinates[base_count + width_inc + 2];

            coords[count++] = terrainCoordinates[base_count + width_inc + 3];
            coords[count++] = terrainCoordinates[base_count + width_inc + 4];
            coords[count++] = terrainCoordinates[base_count + width_inc + 5];

            coords[count++] = terrainCoordinates[base_count + 3];
            coords[count++] = terrainCoordinates[base_count + 4];
            coords[count++] = terrainCoordinates[base_count + 5];

            coords[count++] = terrainCoordinates[base_count];
            coords[count++] = terrainCoordinates[base_count + 1];
            coords[count++] = terrainCoordinates[base_count + 2];

            base_count += 3;

            if((i % (widthPoints - 1)) == 0)
                base_count += 3;
        }

        checkRelativeHeights(data.geometrySubType, coords, vtx_cnt);
    }

    /**
     * Generates new set of unindexed points for quads. The array consists
     * of the side coordinates, followed by the top and bottom.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedQuadCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = facetCount * 4;

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;

        regenerateBase();

        int count = 0;
        int i = 0;
        int base_count = 0;
        int width_inc = widthPoints * 3;

        // Start of with one less row (width) here because we don't have two
        // sets of coordinates for those.
        for(i = facetCount; --i >= 0; )
        {
            coords[count++] = terrainCoordinates[base_count];
            coords[count++] = terrainCoordinates[base_count + 1];
            coords[count++] = terrainCoordinates[base_count + 2];

            coords[count++] = terrainCoordinates[base_count + width_inc];
            coords[count++] = terrainCoordinates[base_count + width_inc + 1];
            coords[count++] = terrainCoordinates[base_count + width_inc + 2];

            coords[count++] = terrainCoordinates[base_count + width_inc + 3];
            coords[count++] = terrainCoordinates[base_count + width_inc + 4];
            coords[count++] = terrainCoordinates[base_count + width_inc + 5];

            coords[count++] = terrainCoordinates[base_count + 3];
            coords[count++] = terrainCoordinates[base_count + 4];
            coords[count++] = terrainCoordinates[base_count + 5];
/*
System.out.println("Base count is " + base_count + " " + widthPoints);
System.out.println(coords[count - 12] + " " + coords[count - 11] + " " + coords[count - 10]);
System.out.println(coords[count - 9] + " " + coords[count - 8] + " " + coords[count - 7]);
System.out.println(coords[count - 6] + " " + coords[count - 5] + " " + coords[count - 4]);
System.out.println(coords[count - 3] + " " + coords[count - 2] + " " + coords[count - 1]);
System.out.println();

System.out.println("There are " + num_strips);
System.out.println("vertex count " + data.vertexCount);
System.out.println("Alt count " + getVertexCount(data));
System.out.println("index size " + index_size);
System.out.println("Total strip index count " + (num_strips * widthPoints * 2));

*/
            base_count += 3;

            if((i % (widthPoints - 1)) == 0)
                base_count += 3;
        }

        checkRelativeHeights(data.geometrySubType, coords, vtx_cnt);
    }

    /**
     * Generates new set of indexed points for triangles or quads. The array
     * consists of the side coordinates, followed by the center for top, then
     * its points then the bottom center and its points. We do this as they
     * use a completely different set of normals. The side
     * coordinates are interleved as top and then bottom values.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateIndexedCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = widthPoints * depthPoints;

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;

        regenerateBase();

        System.arraycopy(terrainCoordinates, 0, coords, 0, numTerrainValues);

        checkRelativeHeights(data.geometrySubType, coords, vtx_cnt);
    }

    //------------------------------------------------------------------------
    // Normal generation routines
    //------------------------------------------------------------------------

    /**
     * Generate a new set of normals for a normal set of unindexed points.
     * Smooth normals are used for the sides at the average between the faces.
     * Bottom normals always point down.
     * <p>
     * This must always be called after the coordinate generation.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriNormals(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 3;

        if(data.normals == null)
            data.normals = new float[vtx_cnt];
        else if(data.normals.length < vtx_cnt)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                vtx_cnt);

        regenerateNormals();

        int i = 0;
        int count = 0;
        int base_count = 0;
        int width_inc = widthPoints * 3;
        float[] normals = data.normals;

        // Start of with one less row (width) here because we don't have two
        // sets of coordinates for those.
        for(i = facetCount; --i >= 0; )
        {
            // triangle 1
            normals[count++] = terrainNormals[base_count];
            normals[count++] = terrainNormals[base_count + 1];
            normals[count++] = terrainNormals[base_count + 2];

            normals[count++] = terrainNormals[base_count + width_inc];
            normals[count++] = terrainNormals[base_count + width_inc + 1];
            normals[count++] = terrainNormals[base_count + width_inc + 2];

            normals[count++] = terrainNormals[base_count + 3];
            normals[count++] = terrainNormals[base_count + 4];
            normals[count++] = terrainNormals[base_count + 5];

            // triangle 2
            normals[count++] = terrainNormals[base_count + width_inc];
            normals[count++] = terrainNormals[base_count + width_inc + 1];
            normals[count++] = terrainNormals[base_count + width_inc + 2];

            normals[count++] = terrainNormals[base_count + width_inc + 3];
            normals[count++] = terrainNormals[base_count + width_inc + 4];
            normals[count++] = terrainNormals[base_count + width_inc + 5];

            normals[count++] = terrainNormals[base_count + 3];
            normals[count++] = terrainNormals[base_count + 4];
            normals[count++] = terrainNormals[base_count + 5];

            base_count += 3;

            if((i % (widthPoints - 1)) == 0)
                base_count += 3;
        }
    }

    /**
     * Generate a new set of normals for a normal set of unindexed points.
     * Smooth normals are used for the sides at the average between the faces.
     * Bottom normals always point down.
     * <p>
     * This must always be called after the coordinate generation.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriStripNormals(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 3;

        if(data.normals == null)
            data.normals = new float[vtx_cnt];
        else if(data.normals.length < vtx_cnt)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                vtx_cnt);

        regenerateNormals();

        int i;
        float[] normals = data.normals;
        int count = 0;
        int base_count = 0;
        int width_inc = widthPoints * 3;
        int total_points = widthPoints * (depthPoints - 1);

        // Start of with one less row (width) here because we don't have two
        // sets of coordinates for those.
        for(i = total_points; --i >= 0; )
        {
            normals[count++] = terrainNormals[base_count];
            normals[count++] = terrainNormals[base_count + 1];
            normals[count++] = terrainNormals[base_count + 2];

            normals[count++] = terrainNormals[base_count + width_inc];
            normals[count++] = terrainNormals[base_count + width_inc + 1];
            normals[count++] = terrainNormals[base_count + width_inc + 2];

            base_count += 3;
        }
    }

    /**
     * Generate a new set of normals for a normal set of unindexed triangle
     * fan points. Smooth normals are used for the sides at the average
     * between the faces. Bottom normals always point down.
     * <p>
     * This must always be called after the coordinate generation.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriFanNormals(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 3;

        if(data.normals == null)
            data.normals = new float[vtx_cnt];
        else if(data.normals.length < vtx_cnt)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                vtx_cnt);

        regenerateNormals();

        float[] normals = data.normals;
        int count = 0;
        int i = 0;
        int base_count = 0;
        int width_inc = widthPoints * 3;

        // Start of with one less row (width) here because we don't have two
        // sets of coordinates for those.
        for(i = facetCount; --i >= 0; )
        {
            normals[count++] = terrainNormals[base_count + width_inc];
            normals[count++] = terrainNormals[base_count + width_inc + 1];
            normals[count++] = terrainNormals[base_count + width_inc + 2];

            normals[count++] = terrainNormals[base_count + width_inc + 3];
            normals[count++] = terrainNormals[base_count + width_inc + 4];
            normals[count++] = terrainNormals[base_count + width_inc + 5];

            normals[count++] = terrainNormals[base_count + 3];
            normals[count++] = terrainNormals[base_count + 4];
            normals[count++] = terrainNormals[base_count + 5];

            normals[count++] = terrainNormals[base_count];
            normals[count++] = terrainNormals[base_count + 1];
            normals[count++] = terrainNormals[base_count + 2];

            base_count += 3;

            if((i % (widthPoints - 1)) == 0)
                base_count += 3;
        }
    }

    /**
     * Generate a new set of normals for a normal set of unindexed points.
     * Smooth normals are used for the sides at the average between the faces.
     * Bottom normals always point down.
     * <p>
     * This must always be called after the coordinate generation.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedQuadNormals(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 3;

        if(data.normals == null)
            data.normals = new float[vtx_cnt];
        else if(data.normals.length < vtx_cnt)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                vtx_cnt);

        regenerateNormals();

        float[] normals = data.normals;
        int count = 0;
        int i = 0;
        int base_count = 0;
        int width_inc = widthPoints * 3;

        // Start of with one less row (width) here because we don't have two
        // sets of coordinates for those.
        for(i = facetCount; --i >= 0; )
        {
            normals[count++] = terrainNormals[base_count];
            normals[count++] = terrainNormals[base_count + 1];
            normals[count++] = terrainNormals[base_count + 2];

            normals[count++] = terrainNormals[base_count + width_inc];
            normals[count++] = terrainNormals[base_count + width_inc + 1];
            normals[count++] = terrainNormals[base_count + width_inc + 2];

            normals[count++] = terrainNormals[base_count + width_inc + 3];
            normals[count++] = terrainNormals[base_count + width_inc + 4];
            normals[count++] = terrainNormals[base_count + width_inc + 5];

            normals[count++] = terrainNormals[base_count + 3];
            normals[count++] = terrainNormals[base_count + 4];
            normals[count++] = terrainNormals[base_count + 5];
/*
System.out.println("Base count is " + base_count + " " + widthPoints);
System.out.println(normals[count - 12] + " " + normals[count - 11] + " " + normals[count - 10]);
System.out.println(normals[count - 9] + " " + normals[count - 8] + " " + normals[count - 7]);
System.out.println(normals[count - 6] + " " + normals[count - 5] + " " + normals[count - 4]);
System.out.println(normals[count - 3] + " " + normals[count - 2] + " " + normals[count - 1]);
System.out.println();

System.out.println("There are " + num_strips);
System.out.println("vertex count " + data.vertexCount);
System.out.println("Alt count " + getVertexCount(data));
System.out.println("index size " + index_size);
System.out.println("Total strip index count " + (num_strips * widthPoints * 2));

*/
            base_count += 3;

            if((i % (widthPoints - 1)) == 0)
                base_count += 3;
        }
    }

    /**
     * Generate a new set of normals for a normal set of indexed points.
     * Smooth normals are used for the sides at the average between the faces.
     * Bottom normals always point down.
     * <p>
     * This must always be called after the coordinate generation.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateIndexedNormals(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 3;

        if(data.normals == null)
            data.normals = new float[vtx_cnt];
        else if(data.normals.length < vtx_cnt)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                vtx_cnt);

        regenerateNormals();

        System.arraycopy(terrainNormals, 0, data.normals, 0, numTerrainValues);
    }

    //------------------------------------------------------------------------
    // Texture coordinate generation routines
    //------------------------------------------------------------------------
    /**
     * Generates new set of unindexed texture coordinates for triangles strips.
     * The array consists of one strip per width row.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriStripTexture2D(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = widthPoints * (depthPoints - 1) * 2;

        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[vtx_cnt * 2];
        else if(data.textureCoordinates.length < vtx_cnt * 2)
            throw new InvalidArraySizeException("Coordinates",
                                                data.textureCoordinates.length,
                                                vtx_cnt * 2);

        float[] coords = data.textureCoordinates;

        regenerateTexcoords();

        int i;
        int count = 0;
        int base_count = 0;
        int width_inc = widthPoints * 2;
        int total_points = widthPoints * (depthPoints - 1);

        // Start of with one less row (width) here because we don't have two
        // sets of coordinates for those.
        for(i = total_points; --i >= 0; )
        {
            coords[count++] = terrainTexcoords[base_count];
            coords[count++] = terrainTexcoords[base_count + 1];

            coords[count++] = terrainTexcoords[base_count + width_inc];
            coords[count++] = terrainTexcoords[base_count + width_inc + 1];

            base_count += 2;
        }
    }

    /**
     * Generates new set of unindexed texture coordinates for quads. The array
     * consists of one strip per width row.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedQuadTexture2D(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = facetCount * 4;

        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[vtx_cnt * 2];
        else if(data.textureCoordinates.length < vtx_cnt * 2)
            throw new InvalidArraySizeException("Coordinates",
                                                data.textureCoordinates.length,
                                                vtx_cnt * 2);

        float[] coords = data.textureCoordinates;

        regenerateTexcoords();

        int count = 0;
        int i = 0;
        int base_count = 0;
        int width_inc = widthPoints * 2;

        for(i = facetCount; --i >= 0; )
        {
            coords[count++] = terrainTexcoords[base_count];
            coords[count++] = terrainTexcoords[base_count + 1];

            coords[count++] = terrainTexcoords[base_count + width_inc];
            coords[count++] = terrainTexcoords[base_count + width_inc + 1];

            coords[count++] = terrainTexcoords[base_count + width_inc + 2];
            coords[count++] = terrainTexcoords[base_count + width_inc + 3];

            coords[count++] = terrainTexcoords[base_count + 2];
            coords[count++] = terrainTexcoords[base_count + 3];

            base_count += 2;

            if((i % (widthPoints - 1)) == 0)
                base_count += 2;
        }
    }

    /**
     * Generate a new set of texCoords for a set of unindexed points.
     * <p>
     * This must always be called after the coordinate generation.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateTriTexture2D(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 2;

        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[vtx_cnt];
        else if(data.textureCoordinates.length < vtx_cnt)
            throw new InvalidArraySizeException("2D Texture coordinates",
                                                data.textureCoordinates.length,
                                                vtx_cnt);

        regenerateTexcoords();

        System.out.println("Unhandled textured generation case in " +
            "ElevationGridGenerator");
    }

    /**
     * Generate a new set of texCoords for a set of unindexed points.
     * <p>
     * This must always be called after the coordinate generation.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateTriTexture3D(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 2;

        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[vtx_cnt];
        else if(data.textureCoordinates.length < vtx_cnt)
            throw new InvalidArraySizeException("3D Texture coordinates",
                                                data.textureCoordinates.length,
                                                vtx_cnt);

        System.out.println("Unhandled textured generation case in " +
            "ElevationGridGenerator");
    }

    /**
     * Check and update if necessary, the heights for relative values.
     *
     * @param subFlags The value of GeometryData.geometrySubType
     * @param coords The coordinates to work with
     * @param cnt The count of vertices to work on
     */
    private void checkRelativeHeights(int subFlags, float[] coords, int cnt)
    {
        if((subFlags & RELATIVE_HEIGHTS) == 0)
            return;

        for(int i = (cnt * 3 - 2); i >= 0; i -= 3)
            coords[i] += baseHeight;
    }

    /**
     * Regenerate the base coordinate points. These are the flat circle that
     * makes up the base of the code. The coordinates are generated based on
     * the 2 PI divided by the number of facets to generate.
     */
    private final void regenerateBase()
    {
        if(!terrainChanged)
            return;

        terrainChanged = false;

        numTerrainValues = widthPoints * depthPoints * 3;

        if((terrainCoordinates == null) ||
           (numTerrainValues > terrainCoordinates.length))
        {
            terrainCoordinates = new float[numTerrainValues];
        }

        float d = -terrainDepth / 2;
        float w = -terrainWidth / 2;
        float width_inc = terrainWidth / (widthPoints - 1);
        float depth_inc = terrainDepth / (depthPoints - 1);

        int count = 0;

        if(flatHeights != null)
        {
            int num = numTerrainValues / 3;
            for(int i = 1; i <= num; i++)
            {
                terrainCoordinates[count++] = w;
                terrainCoordinates[count++] = flatHeights[i-1];
                terrainCoordinates[count++] = d;

                w += width_inc;

                if(((i % (widthPoints)) == 0))
                {
                    d += depth_inc;
                    w = -terrainWidth / 2;
                }
            }
        }
        else
        {
            for(int i = 0; i < depthPoints; i++)
            {
                for(int j = 0;  j < widthPoints; j++)
                {
                    terrainCoordinates[count++] = w;
                    terrainCoordinates[count++] = arrayHeights[i][j];
                    terrainCoordinates[count++] = d;

                    w += width_inc;
                }

                d += depth_inc;
                w = -terrainWidth / 2;
            }
        }
    }

    /**
     * Regenerate the base normals points. These are the flat circle that
     * makes up the base of the code. The normals are generated based the
     * smoothing of normal averages for interior points. Around the edges,
     * we use the average of the edge value polygons.
     */
    private final void regenerateNormals()
    {
        if(!normalsChanged)
            return;

        normalsChanged = false;

        if((terrainNormals == null) ||
           (numTerrainValues > terrainNormals.length))
        {
            terrainNormals = new float[numTerrainValues];
        }

        Vector3f norm;
        int count = 0;
        int base_count = 0;
        int i, j;
        int width_inc = widthPoints * 3;

        // The first edge
        // corner point - normal based on only that face
        norm = createFaceNormal(terrainCoordinates, width_inc, 0, 3);

        terrainNormals[count++] = norm.x;
        terrainNormals[count++] = norm.y;
        terrainNormals[count++] = norm.z;

        base_count = 3;

        for(i = 1; i < (widthPoints - 1); i++)
        {
            norm = calcSideAverageNormal(terrainCoordinates,
                                         base_count,
                                         base_count + 3,
                                         base_count + width_inc,
                                         base_count - 3);

            terrainNormals[count++] = norm.x;
            terrainNormals[count++] = norm.y;
            terrainNormals[count++] = norm.z;

            base_count += 3;
        }

        // Last corner point of the first row
        norm = createFaceNormal(terrainCoordinates,
                                base_count,
                                base_count + width_inc,
                                base_count - 3);

        terrainNormals[count++] = norm.x;
        terrainNormals[count++] = norm.y;
        terrainNormals[count++] = norm.z;

        base_count += 3;

        // Now, process all of the internal points
        for(i = 1; i < (depthPoints - 1); i++)
        {

            norm = calcSideAverageNormal(terrainCoordinates,
                                         base_count,
                                         base_count - width_inc,
                                         base_count + 3,
                                         base_count + width_inc);

            terrainNormals[count++] = norm.x;
            terrainNormals[count++] = norm.y;
            terrainNormals[count++] = norm.z;

            base_count += 3;

            for(j = 1; j < (widthPoints - 1); j++)
            {

                norm = calcQuadAverageNormal(terrainCoordinates,
                                             base_count,
                                             base_count + 3,
                                             base_count + width_inc,
                                             base_count - 3,
                                             base_count - width_inc);

                terrainNormals[count++] = norm.x;
                terrainNormals[count++] = norm.y;
                terrainNormals[count++] = norm.z;

                base_count += 3;
            }

            // Last point of the row
            norm = calcSideAverageNormal(terrainCoordinates,
                                         base_count,
                                         base_count + width_inc,
                                         base_count - 3,
                                         base_count - width_inc);

            terrainNormals[count++] = norm.x;
            terrainNormals[count++] = norm.y;
            terrainNormals[count++] = norm.z;

            base_count += 3;
        }

        // The last edge
        // corner point - normal based on only that face
        norm = createFaceNormal(terrainCoordinates,
                                base_count,
                                base_count - width_inc,
                                base_count + 3);

        terrainNormals[count++] = norm.x;
        terrainNormals[count++] = norm.y;
        terrainNormals[count++] = norm.z;

        base_count += 3;

        for(i = 1; i < (widthPoints - 1); i++)
        {
            norm = calcSideAverageNormal(terrainCoordinates,
                                         base_count,
                                         base_count - 3,
                                         base_count - width_inc,
                                         base_count + 3);

            terrainNormals[count++] = norm.x;
            terrainNormals[count++] = norm.y;
            terrainNormals[count++] = norm.z;

            base_count += 3;
        }

        // Last corner point of the first row
        norm = createFaceNormal(terrainCoordinates,
                                base_count,
                                base_count - 3,
                                base_count - width_inc);

        terrainNormals[count++] = norm.x;
        terrainNormals[count++] = norm.y;
        terrainNormals[count++] = norm.z;
    }

    /**
     * Convenience method to calculate the average normal value between
     * two quads - ie along the side of an object
     *
     * @param coords The coordinates to generate from
     * @param p The centre point
     * @param p1 The first point of the first side
     * @param p2 The middle, shared side point
     * @param p3 The last point of the second side
     * @return The averaged vector
     */
    private Vector3f calcSideAverageNormal(float[] coords,
                                           int p,
                                           int p1,
                                           int p2,
                                           int p3)
    {
        Vector3f norm;
        float x, y, z;

        // Normal first for the previous quad
        norm = createFaceNormal(terrainCoordinates, p, p1, p2);
        x = norm.x;
        y = norm.y;
        z = norm.z;

        // Normal for the next quad
        norm = createFaceNormal(terrainCoordinates, p, p2, p3);

        // create the average of each compoenent for the final normal
        norm.x = (norm.x + x) / 2;
        norm.y = (norm.y + y) / 2;
        norm.z = (norm.z + z) / 2;

        norm.normalize();

        return norm;
    }

    /**
     * Convenience method to create quad average normal amongst four
     * quads based around a common centre point (the one having the normal
     * calculated).
     *
     * @param coords The coordinates to generate from
     * @param p The centre point
     * @param p1 shared point between first and last quad
     * @param p2 shared point between first and second quad
     * @param p3 shared point between second and third quad
     * @param p4 shared point between third and fourth quad
     * @return The averaged vector
     */
    private Vector3f calcQuadAverageNormal(float[] coords,
                                           int p,
                                           int p1,
                                           int p2,
                                           int p3,
                                           int p4)
    {
        Vector3f norm;
        float x, y, z;

        // Normal first for quads 1 & 2
        norm = createFaceNormal(terrainCoordinates, p, p2, p1);
        x = norm.x;
        y = norm.y;
        z = norm.z;

        // Normal for the quads 2 & 3
        norm = createFaceNormal(terrainCoordinates, p, p2, p3);

        x += norm.x;
        y += norm.y;
        z += norm.z;

        // Normal for quads 3 & 4
        norm = createFaceNormal(terrainCoordinates, p, p3, p4);

        x += norm.x;
        y += norm.y;
        z += norm.z;

        // Normal for quads 1 & 4
        norm = createFaceNormal(terrainCoordinates, p, p4, p1);

        // create the average of each compoenent for the final normal
        norm.x = (norm.x + x) / 4;
        norm.y = (norm.y + y) / 4;
        norm.z = (norm.z + z) / 4;

        norm.normalize();

        return norm;
    }

    /**
     * Regenerate the texture coordinate points.
     * Assumes regenerateBase has been called before this
     */
    private final void regenerateTexcoords()
    {
        if(!texcoordsChanged)
            return;

        texcoordsChanged = false;

        numTexcoordValues = widthPoints * depthPoints * 2;

        if((terrainTexcoords == null) ||
           (numTexcoordValues > terrainTexcoords.length))
        {
            terrainTexcoords = new float[numTexcoordValues];
        }

        float d = 0;
        float w = 0;
        float width_inc = 1.0f / (widthPoints - 1);
        float depth_inc = 1.0f / (depthPoints - 1);

        int count = 0;

        if(flatHeights != null)
        {
            int num = numTerrainValues / 3;
            for(int i = 1; i <= num; i++)
            {
                terrainTexcoords[count++] = w;
                terrainTexcoords[count++] = d;
                w += width_inc;

                if(((i % (widthPoints)) == 0))
                {
                    d += depth_inc;
                    w = 0;
                }
            }
        }
        else
        {
            for(int i = 0; i < depthPoints; i++)
            {
                for(int j = 0;  j < widthPoints; j++)
                {
                    terrainTexcoords[count++] = w;
                    terrainTexcoords[count++] = d;

                    w += width_inc;
                }

                d += depth_inc;
                w = 0;
            }
        }
    }
}
