/*****************************************************************************
 *                       J3D.org Copyright (c) 2000
 *                             Java Source
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
 * Generator of Cone raw coordinates and geometry normals.
 * <p>
 *
 * The generator is used to create cone shapes for the code. It only generates
 * the geometry array. Internally the representation uses a triangle array
 * and generates all the values requested - textures oordinates, normals and
 * coordinates. Normals are always generated per vertex calculated rather than
 * per-face.
 * <p>
 *
 * The height of the cone is along the Y axis with the point in the positive
 * Y direstion and starting at the origin. The radius is around the X-Z plane
 * and is centered on the origin.
 * <p>
 *
 * When generating strips, we create a strip for all the top and a strip for
 * all the bottom (if used). Generating quads are a bit useless for this
 * geometry type, but is supported non-the-less for completeness.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class ConeGenerator extends GeometryGenerator
{
    /** Default number of segments used in the cone */
    private static final int DEFAULT_FACETS = 16;

    /** The height of the code */
    private float coneHeight;

    /** The radius of the bottom of the cone */
    private float bottomRadius;

    /** Flag to indicate if the geometry should create the base of the cone */
    private boolean useBottom;

    /** The number of sections used around the cone */
    private int facetCount;

    /** The points on the base of the cone for each facet in [x, z] */
    private float[] baseCoordinates;

    /** Flag indicating base values have changed */
    private boolean baseChanged;

    /**
     * Construct a default cone of height 2 and bottom radius of 1.
     * It also includes the bottom faces and uses 16 segments around the base
     * of the cone.
     */
    public ConeGenerator()
    {
        this(2, 1, DEFAULT_FACETS, true);
    }

    /**
     * Create a custom cone of the given height and radius that includes
     * the bottom faces. There are 16 segments around the base of the cone.
     *
     * @param height The height of the cone to generate
     * @param radius The radius of the bottom of the cone
     */
    public ConeGenerator(float height, float radius)
    {
        this(height, radius, DEFAULT_FACETS, true);
    }

    /**
     * Create a custom cone of the given height and radius and can control
     * the number of facets in the cone. The cone always has a base.
     * The minimum number of facets is 3.
     *
     * @param height The height of the cone to generate
     * @param radius The radius of the bottom of the cone
     * @param facets The number of facets on the side of the cone
     * @throws IllegalArgumentException The number of facets is less than 3
     */
    public ConeGenerator(float height, float radius, int facets)
    {
        this(height, radius, facets, true);
    }

    /**
     * Create a custom cone of the given height and radius and can toggle the
     * use of the bottom faces. There are 16 segments around the base of the
     * cone.
     *
     * @param height The height of the cone to generate
     * @param radius The radius of the bottom of the cone
     * @param hasBottom  True if to generate faces for the bottom
     */
    public ConeGenerator(float height, float radius, boolean hasBottom)
    {
        this(height, radius, DEFAULT_FACETS, hasBottom);
    }

    /**
     * Create a custom cone of the given height and radius and can toggle the
     * use of the bottom faces and control the number of facets in the cone.
     * The minimum number of facets is 3.
     *
     * @param height The height of the cone to generate
     * @param radius The radius of the bottom of the cone
     * @param facets The number of facets on the side of the cone
     * @param hasBottom  True if to generate faces for the bottom
     * @throws IllegalArgumentException The number of facets is less than 3
     *    or the radius is not positive
     */
    public ConeGenerator(float height,
                         float radius,
                         int facets,
                         boolean hasBottom)
    {
        if(facets < 3)
            throw new IllegalArgumentException("Number of facets is < 3");

        if(radius <= 0)
            throw new IllegalArgumentException("Radius is negative or zero");

        facetCount = facets;
        coneHeight = height;
        bottomRadius = radius;
        useBottom = hasBottom;
        baseChanged = true;
    }

    /**
     * Get the dimensions of the cone. These are returned as 2 values of
     * height and radius respectively for the array. A new array is
     * created each time so you can do what you like with it.
     *
     * @return The current size of the cone
     */
    public float[] getDimensions()
    {
        return new float[] { coneHeight, bottomRadius };
    }

    /**
     * Check to see that this cone has a bottom in use or not
     *
     * @return true if there is a bottom in use
     */
    public boolean hasBottom()
    {
        return useBottom;
    }

    /**
     * Change the dimensions of the cone to be generated. Calling this will
     * make the points be re-calculated next time you ask for geometry or
     * normals.
     *
     * @param height The height of the cone to generate
     * @param radius The radius of the bottom of the cone
     * @param hasBottom  True if to generate faces for the bottom
     * @throws IllegalArgumentException The radius is not positive
     */
    public void setDimensions(float height, float radius, boolean hasBottom)
    {
        if(radius <= 0)
            throw new IllegalArgumentException("Radius is negative or zero");

        coneHeight = height;
        bottomRadius = radius;
        useBottom = hasBottom;

        baseChanged = true;
    }

    /**
     * Change the number of facets used to create this cone. This will cause
     * the geometry to be regenerated next time they are asked for.
     * The minimum number of facets is 3.
     *
     * @param facets The number of facets on the side of the cone
     * @throws IllegalArgumentException The number of facets is less than 3
     */
    public void setFacetCount(int facets)
    {
        if(facets < 3)
            throw new IllegalArgumentException("Number of facets is < 3");

        if(facetCount != facets)
            baseChanged = true;

        facetCount = facets;
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
    public int getVertexCount(GeometryData data)
        throws UnsupportedTypeException
    {
        int ret_val = 0;
        int bottom_mul = useBottom ? 2 : 1;

        switch(data.geometryType)
        {
            case GeometryData.TRIANGLES:
                ret_val = facetCount * 3 * bottom_mul;
                break;
            case GeometryData.QUADS:
                ret_val = facetCount * 4 * bottom_mul;
                break;

            // These all have the same vertex count
            case GeometryData.TRIANGLE_STRIPS:
            case GeometryData.TRIANGLE_FANS:
            case GeometryData.INDEXED_TRIANGLES:
            case GeometryData.INDEXED_QUADS:
            case GeometryData.INDEXED_TRIANGLE_STRIPS:
            case GeometryData.INDEXED_TRIANGLE_FANS:
                ret_val = facetCount * 2;
                if(useBottom)
                    ret_val += facetCount + 1;
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
//            case GeometryData.QUADS:
//                unindexedQuads(data);
//                break;
//            case GeometryData.TRIANGLE_STRIPS:
//                triangleStrips(data);
//                break;
//            case GeometryData.TRIANGLE_FANS:
//                triangleFans(data);
//                break;
            case GeometryData.INDEXED_QUADS:
                indexedQuads(data);
                break;
            case GeometryData.INDEXED_TRIANGLES:
                indexedTriangles(data);
                break;
//            case GeometryData.INDEXED_TRIANGLE_STRIPS:
//                indexedTriangleStrips(data);
//                break;
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
        generateIndexedTriCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateIndexedTriNormals(data);

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
        for(int i = facetCount; --i > 0; )
        {
            indexes[idx++] = vtx++;
            indexes[idx++] = vtx++;
            indexes[idx++] = vtx + 1;
            indexes[idx++] = vtx + 1;
        }

        indexes[idx++] = vtx++;
        indexes[idx++] = vtx++;
        indexes[idx++] = 1;
        indexes[idx++] = 1;

        if(useBottom)
        {
            int middle = vtx++;
            for(int i = facetCount; --i > 0; )
            {
                indexes[idx++] = middle;
                indexes[idx++] = vtx + 1;
                indexes[idx++] = vtx++;
                indexes[idx++] = middle;
            }

            indexes[idx++] = middle;
            indexes[idx++] = middle + 1;
            indexes[idx++] = vtx;
            indexes[idx++] = middle;
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
        generateIndexedTriCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateIndexedTriNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTriTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);

        // now let's do the index list
        int index_size = data.vertexCount * 3;

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
        for(int i = facetCount; --i > 0; )
        {
            indexes[idx++] = vtx++;
            indexes[idx++] = vtx++;
            indexes[idx++] = vtx + 1;
        }

        indexes[idx++] = vtx++;
        indexes[idx++] = vtx++;
        indexes[idx++] = 1;

        if(useBottom)
        {
            int middle = vtx++;
            for(int i = facetCount; --i > 0; )
            {
                indexes[idx++] = middle;
                indexes[idx++] = vtx + 1;
                indexes[idx++] = vtx++;
            }

            indexes[idx++] = middle;
            indexes[idx++] = middle + 1;
            indexes[idx++] = vtx;
        }
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
        generateIndexedTriCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateIndexedTriNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTriTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);

        // now let's do the index list
        int index_size = facetCount * 3 + ((useBottom) ? facetCount + 2 : 0);
        int num_strips = facetCount + ((useBottom) ? 1 : 0);

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

        // each face consists of an anti-clockwise
        for(int i = facetCount; --i > 0; )
        {
            indexes[idx++] = vtx++;
            indexes[idx++] = vtx++;
            indexes[idx++] = vtx + 1;
            stripCounts[i] = 3;
        }

        indexes[idx++] = vtx++;
        indexes[idx++] = vtx++;
        indexes[idx++] = 0;

        stripCounts[0] = 3;

        if(useBottom)
        {
            // wind the bottom in reverse order so that it can be seen
            int middle = vtx++;
            indexes[idx++] = middle;
            stripCounts[num_strips - 1] = facetCount + 2;

            vtx = data.vertexCount - 1;
            for(int i = facetCount; --i >= 0; )
                indexes[idx++] = vtx--;

            indexes[idx++] = data.vertexCount - 1;
        }
    }

    //------------------------------------------------------------------------
    // Coordinate generation routines
    //------------------------------------------------------------------------

    /**
     * Generates new set of points suitable for use in an unindexed array. Each
     * base coordinate will appear twice in this list. The first half of the
     * array is the top, the second half, the bottom.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = getVertexCount(data);

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
        float height_2 = coneHeight / 2;

        // Reverse loop count because it is *much* faster than the forward
        // version.
        for(i = facetCount; --i > 0; )
        {
            //side coords
            coords[count++] = 0;
            coords[count++] = height_2;
            coords[count++] = 0;

            coords[count++] = baseCoordinates[base_count++];
            coords[count++] = -height_2;
            coords[count++] = baseCoordinates[base_count++];

            coords[count++] = baseCoordinates[base_count];
            coords[count++] = -height_2;
            coords[count++] = baseCoordinates[base_count + 1];
        }

        // The last set of coordinates reuses the first two base coords
        //side coords
        coords[count++] = 0;
        coords[count++] = height_2;
        coords[count++] = 0;

        coords[count++] = baseCoordinates[base_count++];
        coords[count++] = -height_2;
        coords[count++] = baseCoordinates[base_count++];

        coords[count++] = baseCoordinates[0];
        coords[count++] = -height_2;
        coords[count++] = baseCoordinates[1];

        if(useBottom)
        {
            base_count = 0;

            for(i = facetCount; --i > 0;)
            {
                coords[count++] = baseCoordinates[base_count++];
                coords[count++] = -height_2;
                coords[count++] = baseCoordinates[base_count++];

                coords[count++] = 0;
                coords[count++] = -height_2;
                coords[count++] = 0;

                coords[count++] = baseCoordinates[base_count];
                coords[count++] = -height_2;
                coords[count++] = baseCoordinates[base_count + 1];
            }

            coords[count++] = baseCoordinates[base_count++];
            coords[count++] = -height_2;
            coords[count++] = baseCoordinates[base_count++];

            coords[count++] = 0;
            coords[count++] = -height_2;
            coords[count++] = 0;

            coords[count++] = baseCoordinates[0];
            coords[count++] = -height_2;
            coords[count++] = baseCoordinates[1];
        }
    }

    /**
     * Generate a new set of points for use in an indexed array. The first
     * index will always be the cone tip - parallel for each face so that we
     * can get the smoothing right. If the array is to use the bottom,
     * a second set of coordinates will be produced separately for the base
     * so that independent surface normals can be used. These values will
     * start at vertexCount / 2 with the first value as 0,0,0 (the center of
     * the base) and then all the following values as the base.
     */
    private void generateIndexedTriCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = getVertexCount( data);

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
        int base_count = 0;
        int i;
        float height_2 = coneHeight / 2;

        for(i = facetCount; --i >= 0; )
        {
            coords[count++] = 0;
            coords[count++] = height_2;
            coords[count++] = 0;

            coords[count++] = baseCoordinates[base_count++];
            coords[count++] = -height_2;
            coords[count++] = baseCoordinates[base_count++];
        }

        if(useBottom)
        {
            coords[count++] = 0;
            coords[count++] = -height_2;
            coords[count++] = 0;

            base_count = 0;
            for(i = facetCount; --i >= 0; )
            {
               coords[count++] = baseCoordinates[base_count++];
               coords[count++] = -height_2;
               coords[count++] = baseCoordinates[base_count++];
            }
        }
    }

    //------------------------------------------------------------------------
    // Normal generation routines
    //------------------------------------------------------------------------

    /**
     * Generate a new set of normals for a normal set of unindexed points.
     * Smooth normals are used for the sides at the average between the faces.
     * Bottom normals always point down.
     * <p>
     * This must always be called after the coordinate generation. The
     * top normal of the cone is always perpendicular to the face.
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

        int i;
        float[] normals = data.normals;
        Vector3f norm;
        int count = 0;
        vtx_cnt = 0;

        for(i = facetCount; --i >= 0; )
        {
            norm = createFaceNormal(data.coordinates,
                                    vtx_cnt + 3,
                                    vtx_cnt,
                                    vtx_cnt + 6);
            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            norm = createRadialNormal(data.coordinates, vtx_cnt + 3);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            norm = createRadialNormal(data.coordinates, vtx_cnt + 6);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            vtx_cnt += 9;
        }

        // Now generate the bottom if we need it.
        if(useBottom)
        {
            for(i = facetCount; --i >= 0; )
            {
                // The three vertices of the base in an unrolled loop
                normals[count++] = 0;
                normals[count++] = -1;
                normals[count++] = 0;

                normals[count++] = 0;
                normals[count++] = -1;
                normals[count++] = 0;

                normals[count++] = 0;
                normals[count++] = -1;
                normals[count++] = 0;
            }
        }
    }

    /**
     * Generate a new set of normals for a normal set of indexed points.
     * Handles both flat and smooth shading of normals. Flat just has them
     * perpendicular to the face. Smooth has them at the value at the
     * average between the faces. Bottom normals always point down.
     * <p>
     * This must always be called after the coordinate generation. The
     * top normal of the cone is always perpendicular to the face.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateIndexedTriNormals(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 3;

        if(data.normals == null)
            data.normals = new float[vtx_cnt];
        else if(data.normals.length < vtx_cnt)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                vtx_cnt);

        int i;
        float[] normals = data.normals;
        Vector3f norm;
        int count = 0;

        for(i = facetCount; --i > 0; )
        {
            norm = createFaceNormal(data.coordinates,
                                    count + 3,
                                    count,
                                    count + 9);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            norm = createRadialNormal(data.coordinates, count);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;
        }

        norm = createFaceNormal(data.coordinates,
                                count + 3,
                                count,
                                0);

        normals[count++] = norm.x;
        normals[count++] = norm.y;
        normals[count++] = norm.z;

        norm = createRadialNormal(data.coordinates, count);

        normals[count++] = norm.x;
        normals[count++] = norm.y;
        normals[count++] = norm.z;

        // Now generate the bottom if we need it.
        if(useBottom)
        {
            normals[count++] = 0;
            normals[count++] = -1;
            normals[count++] = 0;

            for(i = facetCount; --i >= 0; )
            {
                normals[count++] = 0;
                normals[count++] = -1;
                normals[count++] = 0;
            }
        }
    }

    //------------------------------------------------------------------------
    // Texture coordinate generation routines
    //------------------------------------------------------------------------

    /**
     * Generate a new set of texCoords for a normal set of unindexed points. Each
     * normal faces directly perpendicular for each point. This makes each face
     * seem flat.
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
    }

    /**
     * Generate a new set of texCoords for a normal set of unindexed points. Each
     * normal faces directly perpendicular for each point. This makes each face
     * seem flat.
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
    }

    /**
     * Regenerate the base coordinate points. These are the flat circle that
     * makes up the base of the code. The coordinates are generated based on
     * the 2 PI divided by the number of facets to generate.
     */
    private final void regenerateBase()
    {
        if(!baseChanged)
            return;

        baseChanged = false;

        if((baseCoordinates == null) ||
           (facetCount * 2 > baseCoordinates.length))
        {
            baseCoordinates = new float[facetCount * 2];
        }

        // local constant to make math calcs faster
        double segment_angle = 2.0 * Math.PI / facetCount;
        int count = 0;
        float x, z;
        double angle;
        int i;

        // Reverse loop count because it is *much* faster than the forward
        // version.
        for(i = facetCount; --i >= 0; )
        {
            angle = segment_angle * i;

            x = (float)(bottomRadius * Math.cos(angle));
            z = (float)(bottomRadius * Math.sin(angle));

            baseCoordinates[count++] = x;
            baseCoordinates[count++] = z;
        }
    }
}
