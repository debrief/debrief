/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom;

// Standard imports
//import java.lang.Math.*;

import javax.vecmath.Vector3f;

// Application specific imports

/**
 * A simple cylinder that can be configured to have end caps.
 * <p>
 *
 * The generator is used to create cylinder shaped geometry for the code.
 * Internally we use a triangle array to generate the information as a
 * collection of single triangles. A triangle strip would be more efficient
 * for rendering, but that's too hard for this first cut :)
 *
 * The height of the cone is along the Y axis with the point in the positive
 * Y direstion. The radius is around the X-Z plane. The whole object is
 * centered on the origin.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class CylinderGenerator extends GeometryGenerator
{
    /** The default height of the object */
    private static final float DEFAULT_HEIGHT = 2;

    /** The default radius of the cylinder */
    private static final float DEFAULT_RADIUS = 1;

    /** Default number of segments used in the cone */
    private static final int DEFAULT_FACETS = 16;

    /** The height of the code */
    private float cylinderHeight;

    /** The radius of the bottom of the cone */
    private float radius;

    /** Flag to indicate if the geometry should create the ends */
    private boolean useEnds;

    /** The number of sections used around the cone */
    private int facetCount;

    /** The points on the base of the cone for each facet in [x, z] */
    private float[] baseCoordinates;

    /** The number of values used in the base coordinate array */
    private int numBaseValues;

    /** Flag indicating base values have changed */
    private boolean baseChanged;

    /** Working values for the normal generation */
    private Vector3f normal;

    /**
     * Construct a default cylinder with end caps. The default height is 2
     * and radius 1. There are 16 faces on around the radius.
     */
    public CylinderGenerator()
    {
        this(DEFAULT_HEIGHT, DEFAULT_RADIUS, DEFAULT_FACETS, true);
    }

    /**
     * Construct a default cylinder with the option of having end caps.
     * The default height is 2 and radius 1. There are 16 faces on around the
     * radius.
     *
     * @param ends true to use end caps
     */
    public CylinderGenerator(boolean ends)
    {
        this(DEFAULT_HEIGHT, DEFAULT_RADIUS, DEFAULT_FACETS, ends);
    }

    /**
     * Construct a default cylinder with end caps and selectable number of
     * faces around the radius. The default height is 2 and radius 1. The
     * minimum number of facets is 3.
     *
     * @param facets The number of faces to use around the radius
     * @throws IllegalArgumentException The number of facets is less than 3
     */
    public CylinderGenerator(int facets)
    {
        this(DEFAULT_HEIGHT, DEFAULT_RADIUS, facets, true);
    }

    /**
     * Construct a default cylinder with the option of having end caps and
     * selectable number of faces around the radius. The default height is 2
     * and radius 1.The minimum number of facets is 3.
     *
     * @param facets The number of faces to use around the radius
     * @param ends true to use end caps
     * @throws IllegalArgumentException The number of facets is less than 3
     */
    public CylinderGenerator(int facets, boolean ends)
    {
        this(DEFAULT_HEIGHT, DEFAULT_RADIUS, facets, ends);
    }

    /**
     * Construct a cylinder of a given height and radius with ends. There are
     * 16 faces around the radius.
     *
     * @param height The height of the cylinder to generate
     * @param radius The radis of the cylinder to generate
     */
    public CylinderGenerator(float height, float radius)
    {
        this(height, radius, DEFAULT_FACETS, true);
    }

    /**
     * Construct a cylinder of a given height and radius with ends and
     * selectable number of faces around the radius. The minimum
     * number of facets is 3.
     *
     * @param height The height of the cylinder to generate
     * @param radius The radis of the cylinder to generate
     * @param facets The number of faces to use around the radius
     * @throws IllegalArgumentException The number of facets is less than 3
     */
    public CylinderGenerator(float height, float radius, int facets)
    {
        this(height, radius, facets, true);
    }

    /**
     * Construct a cylinder of a given height and radius with the option of
     * ends. There are 16 faces around the radius.
     *
     * @param height The height of the cylinder to generate
     * @param radius The radis of the cylinder to generate
     * @param ends true to use end caps
     */
    public CylinderGenerator(float height, float radius, boolean ends)
    {
        this(height, radius, DEFAULT_FACETS, ends);
    }

    /**
     * Construct a cylinder of a given height and radius with the option of
     * ends and selectable number of faces around the radius. The minimum
     * number of facets is 3.
     *
     * @param height The height of the cylinder to generate
     * @param radius The radis of the cylinder to generate
     * @param ends true to use end caps
     * @throws IllegalArgumentException The number of facets is less than 3
     */
    public CylinderGenerator(float height,
                             float radius,
                             int facets,
                             boolean ends)
    {
        if(facets < 3)
            throw new IllegalArgumentException("Number of facets is < 3");

        facetCount = facets;
        cylinderHeight = height;
        this.radius = radius;
        useEnds = ends;
        baseChanged = true;
        normal = new Vector3f();
    }

    /**
     * Check to see that this cylinder has ends in use or not
     *
     * @return true if there is are end caps in use
     */
    public boolean hasEnds()
    {
        return useEnds;
    }

    /**
     * Get the dimensions of the cylinder. These are returned as 2 values of
     * height and radius respectively for the array. A new array is
     * created each time so you can do what you like with it.
     *
     * @return The current size of the cone
     */
    public float[] getDimensions()
    {
        return new float[] { cylinderHeight, radius };
    }

    /**
     * Change the dimensions of the cone to be generated. Calling this will
     * make the points be re-calculated next time you ask for geometry or
     * normals.
     *
     * @param height The height of the cone to generate
     * @param radius The radius of the bottom of the cone
     * @param ends True if to generate faces for the ends
     */
    public void setDimensions(float height, float radius, boolean ends)
    {
        if((cylinderHeight != height) || (this.radius != radius))
        {
            baseChanged = true;
            cylinderHeight = height;
            this.radius = radius;
        }

        useEnds = ends;
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

        facetCount = facets;
        baseChanged = true;
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

        switch(data.geometryType)
        {
            case GeometryData.TRIANGLES:
                ret_val = facetCount * 6;
                if(useEnds)
                    ret_val <<= 1;
                break;

            case GeometryData.QUADS:
                ret_val = facetCount * 4;
                if(useEnds)
                    ret_val *= 3;
                break;

            // These all have the same vertex count
            case GeometryData.TRIANGLE_STRIPS:
                ret_val = (facetCount + 1) * 2;

                if(useEnds)
                    ret_val *= 3;
                break;

            case GeometryData.TRIANGLE_FANS:
                ret_val = facetCount * 4;

                if(useEnds)
                    ret_val += (facetCount + 2) * 2;

            case GeometryData.INDEXED_TRIANGLES:
            case GeometryData.INDEXED_QUADS:
            case GeometryData.INDEXED_TRIANGLE_STRIPS:
            case GeometryData.INDEXED_TRIANGLE_FANS:
                ret_val = facetCount * 2;

                if(useEnds)
                    ret_val += 2 + facetCount * 2;
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
        for(int i = facetCount; --i > 0; vtx += 2)
        {
            indexes[idx++] = vtx;
            indexes[idx++] = vtx + 1;
            indexes[idx++] = vtx + 3;
            indexes[idx++] = vtx + 2;
        }

        indexes[idx++] = vtx++;
        indexes[idx++] = vtx++;
        indexes[idx++] = 0;
        indexes[idx++] = 1;

        if(useEnds)
        {
            int middle = vtx++;

            // top face.
            for(int i = facetCount; --i > 0; )
            {
                indexes[idx++] = middle;
                indexes[idx++] = vtx++;
                indexes[idx++] = vtx;
                indexes[idx++] = middle;
            }

            indexes[idx++] = middle;
            indexes[idx++] = vtx++;
            indexes[idx++] = middle + 1;
            indexes[idx++] = middle;

            middle = vtx++;

            // bottom face is same as top.
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
        generateIndexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateIndexedNormals(data);

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
        for(int i = facetCount; --i > 0; vtx += 2)
        {
            // triangle 1
            indexes[idx++] = vtx;
            indexes[idx++] = vtx + 1;
            indexes[idx++] = vtx + 3;

            // triangle 2
            indexes[idx++] = vtx + 3;
            indexes[idx++] = vtx + 2;
            indexes[idx++] = vtx;
        }

        // triangle 1
        indexes[idx++] = vtx;
        indexes[idx++] = vtx + 1;
        indexes[idx++] = 1;

        // triangle 2
        indexes[idx++] = 1;
        indexes[idx++] = 0;
        indexes[idx++] = vtx;

        vtx += 2;

        if(useEnds)
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

            middle = vtx++;

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
            generateTriTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);

        int num_strips = 3;

        if(data.stripCounts == null)
            data.stripCounts = new int[num_strips];
        else if(data.stripCounts.length < num_strips)
            throw new InvalidArraySizeException("Strip counts",
                                                data.stripCounts.length,
                                                num_strips);

        data.stripCounts[0] = (1 + facetCount) * 2;
        data.stripCounts[1] = data.stripCounts[0];
        data.stripCounts[2] = data.stripCounts[0];
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

        int num_strips = facetCount + 2;

        if(data.stripCounts == null)
            data.stripCounts = new int[num_strips];
        else if(data.stripCounts.length < num_strips)
            throw new InvalidArraySizeException("Strip counts",
                                                data.stripCounts.length,
                                                num_strips);

        for(int i = facetCount; --i >= 0; )
            data.stripCounts[i] = 4;

        data.stripCounts[num_strips - 2] = facetCount + 2;
        data.stripCounts[num_strips - 1] = facetCount + 2;
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
        int index_size = (facetCount + 1) * 2 * ((useEnds) ? 3 : 1);
        int num_strips = 1 + ((useEnds) ? 2 : 0);

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

        stripCounts[0] = (facetCount + 1) * 2;

        // The side is one big strip
        for(int i = facetCount; --i >= 0; )
        {
            indexes[idx++] = vtx++;
            indexes[idx++] = vtx++;
        }

        indexes[idx++] = 0;
        indexes[idx++] = facetCount;

        if(useEnds)
        {
            stripCounts[1] = (facetCount + 1) * 2;
            stripCounts[2] = (facetCount + 1) * 2;

            // Do the top face as one strip
            int middle = vtx++;

            for(int i = facetCount; --i >= 0; )
            {
                indexes[idx++] = middle;
                indexes[idx++] = vtx++;
            }

            indexes[idx++] = middle + 1;
            indexes[idx++] = middle;

            // Now the bottom face as one fan. Must wind it backwards compared
            // to the top.
            middle = vtx++;

            for(int i = facetCount; --i >= 0; )
            {
                indexes[idx++] = vtx++;
                indexes[idx++] = middle;
            }

            indexes[idx++] = middle + 1;
            indexes[idx++] = middle;
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
        generateIndexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateIndexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTriTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTriTexture3D(data);

        // now let's do the index list
        int index_size = facetCount * 4 + ((useEnds) ? 2 * (facetCount + 2) : 0);
        int num_strips = facetCount + ((useEnds) ? 2 : 0);

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
            indexes[idx++] = vtx;
            indexes[idx++] = vtx + 1;
            indexes[idx++] = vtx + 3;
            indexes[idx++] = vtx + 2;
            stripCounts[i] = 4;

            vtx += 2;
        }

        indexes[idx++] = vtx;
        indexes[idx++] = vtx + 1;
        indexes[idx++] = 1;
        indexes[idx++] = 0;
        vtx += 2;

        stripCounts[0] = 4;

        if(useEnds)
        {
            // Do the top face as one fan
            int middle = vtx++;
            indexes[idx++] = middle;
            stripCounts[num_strips - 2] = facetCount + 2;

            for(int i = facetCount; --i >= 0; )
                indexes[idx++] = vtx++;

            indexes[idx++] = middle + 1;

            // Now the bottom face as one fan. Must wind it backwards compared
            // to the top.
            middle = vtx++;
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
        int vtx_cnt = facetCount * 6;

        if(useEnds)
            vtx_cnt <<= 1;

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
        float half_height = cylinderHeight / 2;

        for(i = facetCount; --i > 0; )
        {
            //side coords triangle 1
            coords[count++] = baseCoordinates[base_count];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            coords[count++] = baseCoordinates[base_count + 2];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[base_count + 3];

            coords[count++] = baseCoordinates[base_count + 2];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[base_count + 3];

            //side coords triangle 2
            coords[count++] = baseCoordinates[base_count + 2];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[base_count + 3];

            coords[count++] = baseCoordinates[base_count];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            coords[count++] = baseCoordinates[base_count];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            base_count += 2;
        }

        //side coords triangle 1
        coords[count++] = baseCoordinates[base_count];
        coords[count++] = -half_height;
        coords[count++] = baseCoordinates[base_count + 1];

        coords[count++] = baseCoordinates[0];
        coords[count++] = -half_height;
        coords[count++] = baseCoordinates[1];

        coords[count++] = baseCoordinates[0];
        coords[count++] = half_height;
        coords[count++] = baseCoordinates[1];

        //side coords triangle 2
        coords[count++] = baseCoordinates[0];
        coords[count++] = half_height;
        coords[count++] = baseCoordinates[1];

        coords[count++] = baseCoordinates[base_count];
        coords[count++] = half_height;
        coords[count++] = baseCoordinates[base_count + 1];

        coords[count++] = baseCoordinates[base_count];
        coords[count++] = -half_height;
        coords[count++] = baseCoordinates[base_count + 1];

        if(useEnds)
        {
            base_count = 0;

            // Top coordinates
            for(i = facetCount; --i > 0; )
            {
                coords[count++] = baseCoordinates[base_count + 2];
                coords[count++] = half_height;
                coords[count++] = baseCoordinates[base_count + 3];

                coords[count++] = 0;
                coords[count++] = half_height;
                coords[count++] = 0;

                coords[count++] = baseCoordinates[base_count];
                coords[count++] = half_height;
                coords[count++] = baseCoordinates[base_count + 1];

                base_count += 2;
            }

            coords[count++] = baseCoordinates[0];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[1];

            coords[count++] = 0;
            coords[count++] = half_height;
            coords[count++] = 0;

            coords[count++] = baseCoordinates[base_count];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            // Bottom coordinates

            base_count = 0;

            for(i = facetCount; --i > 0; )
            {
                coords[count++] = baseCoordinates[base_count];
                coords[count++] = -half_height;
                coords[count++] = baseCoordinates[base_count + 1];

                coords[count++] = 0;
                coords[count++] = -half_height;
                coords[count++] = 0;

                coords[count++] = baseCoordinates[base_count + 2];
                coords[count++] = -half_height;
                coords[count++] = baseCoordinates[base_count + 3];

                base_count += 2;
            }

            // bottom coords
            coords[count++] = baseCoordinates[base_count];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            coords[count++] = 0;
            coords[count++] = -half_height;
            coords[count++] = 0;

            coords[count++] = baseCoordinates[0];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[1];
        }
    }

    /**
     * Generates new set of unindexed points for triangles strips. The array
     * consists of the side coordinates as one strip with the top and bottom
     * as separate strips.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriStripCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = (facetCount + 1) * 2;

        if(useEnds)
            vtx_cnt *= 3;

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;
        float half_height = cylinderHeight / 2;

        regenerateBase();

        int i;
        int count = 0;
        int base_count = 0;

        // The side is one big strip
        for(i = facetCount; --i >= 0; )
        {
            coords[count++] = baseCoordinates[base_count];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            coords[count++] = baseCoordinates[base_count];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            base_count += 2;
        }

        coords[count++] = baseCoordinates[0];
        coords[count++] = half_height;
        coords[count++] = baseCoordinates[1];

        coords[count++] = baseCoordinates[0];
        coords[count++] = -half_height;
        coords[count++] = baseCoordinates[1];

        if(useEnds)
        {
            // Do the top face as one strip
            base_count = 0;
            for(i = facetCount; --i >= 0; )
            {
                coords[count++] = 0;
                coords[count++] = half_height;
                coords[count++] = 0;

                coords[count++] = baseCoordinates[base_count++];
                coords[count++] = half_height;
                coords[count++] = baseCoordinates[base_count++];
            }

            coords[count++] = 0;
            coords[count++] = half_height;
            coords[count++] = 0;

            coords[count++] = baseCoordinates[0];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[1];

            // Now the bottom face as one fan. Must wind it backwards compared
            // to the top.
            base_count = 0;
            for(i = facetCount; --i >= 0; )
            {
                coords[count++] = baseCoordinates[base_count++];
                coords[count++] = -half_height;
                coords[count++] = baseCoordinates[base_count++];

                coords[count++] = 0;
                coords[count++] = -half_height;
                coords[count++] = 0;
            }

            coords[count++] = baseCoordinates[0];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[1];

            coords[count++] = 0;
            coords[count++] = -half_height;
            coords[count++] = 0;
        }
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

        if(useEnds)
            vtx_cnt += (facetCount + 2) * 2;

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
        float half_height = cylinderHeight / 2;

        for(i = facetCount; --i > 0; )
        {
            //side coords triangle 1
            coords[count++] = baseCoordinates[base_count];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            coords[count++] = baseCoordinates[base_count];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            coords[count++] = baseCoordinates[base_count + 2];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[base_count + 3];

            coords[count++] = baseCoordinates[base_count + 2];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[base_count + 3];

            base_count += 2;
        }

        coords[count++] = baseCoordinates[base_count];
        coords[count++] = half_height;
        coords[count++] = baseCoordinates[base_count + 1];

        coords[count++] = baseCoordinates[base_count];
        coords[count++] = -half_height;
        coords[count++] = baseCoordinates[base_count + 1];

        coords[count++] = baseCoordinates[0];
        coords[count++] = -half_height;
        coords[count++] = baseCoordinates[1];

        coords[count++] = baseCoordinates[0];
        coords[count++] = half_height;
        coords[count++] = baseCoordinates[1];

        if(useEnds)
        {
            base_count = 0;

            coords[count++] = 0;
            coords[count++] = half_height;
            coords[count++] = 0;

            // Top coordinates
            for(i = facetCount; --i >= 0; )
            {
                coords[count++] = baseCoordinates[base_count++];
                coords[count++] = half_height;
                coords[count++] = baseCoordinates[base_count++];
            }

            coords[count++] = baseCoordinates[0];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[1];

            // Bottom coordinates wound in reverse order
            base_count = numBaseValues - 1;

            coords[count++] = 0;
            coords[count++] = -half_height;
            coords[count++] = 0;

            for(i = facetCount; --i >= 0; )
            {
                coords[count++] = baseCoordinates[base_count - 1];
                coords[count++] = -half_height;
                coords[count++] = baseCoordinates[base_count];

                base_count -= 2;
            }

            // bottom coords
            coords[count++] = baseCoordinates[numBaseValues - 2];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[numBaseValues - 1];
        }
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

        if(useEnds)
            vtx_cnt *= 3;

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
        float half_height = cylinderHeight / 2;

        for(i = facetCount; --i > 0; )
        {
            //side coords triangle 1
            coords[count++] = baseCoordinates[base_count];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            coords[count++] = baseCoordinates[base_count + 2];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[base_count + 3];

            coords[count++] = baseCoordinates[base_count + 2];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[base_count + 3];

            coords[count++] = baseCoordinates[base_count];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            base_count += 2;
        }

        coords[count++] = baseCoordinates[base_count];
        coords[count++] = -half_height;
        coords[count++] = baseCoordinates[base_count + 1];

        coords[count++] = baseCoordinates[0];
        coords[count++] = -half_height;
        coords[count++] = baseCoordinates[1];

        coords[count++] = baseCoordinates[0];
        coords[count++] = half_height;
        coords[count++] = baseCoordinates[1];

        coords[count++] = baseCoordinates[base_count];
        coords[count++] = half_height;
        coords[count++] = baseCoordinates[base_count + 1];

        if(useEnds)
        {
            base_count = 0;

            // Top coordinates
            for(i = facetCount; --i > 0; )
            {
                coords[count++] = baseCoordinates[base_count + 2];
                coords[count++] = half_height;
                coords[count++] = baseCoordinates[base_count + 3];

                coords[count++] = 0;
                coords[count++] = half_height;
                coords[count++] = 0;

                coords[count++] = 0;
                coords[count++] = half_height;
                coords[count++] = 0;

                coords[count++] = baseCoordinates[base_count];
                coords[count++] = half_height;
                coords[count++] = baseCoordinates[base_count + 1];

                base_count += 2;
            }

            coords[count++] = baseCoordinates[0];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[1];

            coords[count++] = 0;
            coords[count++] = half_height;
            coords[count++] = 0;

            coords[count++] = 0;
            coords[count++] = half_height;
            coords[count++] = 0;

            coords[count++] = baseCoordinates[base_count];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            // Bottom coordinates

            base_count = 0;

            for(i = facetCount; --i > 0; )
            {
                coords[count++] = baseCoordinates[base_count];
                coords[count++] = -half_height;
                coords[count++] = baseCoordinates[base_count + 1];

                coords[count++] = 0;
                coords[count++] = -half_height;
                coords[count++] = 0;

                coords[count++] = 0;
                coords[count++] = -half_height;
                coords[count++] = 0;

                coords[count++] = baseCoordinates[base_count + 2];
                coords[count++] = -half_height;
                coords[count++] = baseCoordinates[base_count + 3];

                base_count += 2;
            }

            // bottom coords
            coords[count++] = baseCoordinates[base_count];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            coords[count++] = 0;
            coords[count++] = -half_height;
            coords[count++] = 0;

            coords[count++] = 0;
            coords[count++] = -half_height;
            coords[count++] = 0;

            coords[count++] = baseCoordinates[0];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[1];
        }
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
        int vtx_cnt = facetCount * 2;

        if(useEnds)
            vtx_cnt += 2 + facetCount * 2;

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
        float half_height = cylinderHeight / 2;

        for(i = facetCount; --i >=0; )
        {
            coords[count++] = baseCoordinates[base_count];
            coords[count++] = half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            coords[count++] = baseCoordinates[base_count];
            coords[count++] = -half_height;
            coords[count++] = baseCoordinates[base_count + 1];

            base_count += 2;
        }

        if(useEnds)
        {
            coords[count++] = 0;
            coords[count++] = half_height;
            coords[count++] = 0;

            base_count = 0;

            for(i = facetCount; --i >= 0; )
            {
                coords[count++] = baseCoordinates[base_count++];
                coords[count++] = half_height;
                coords[count++] = baseCoordinates[base_count++];
            }

            base_count = 0;

            coords[count++] = 0;
            coords[count++] = -half_height;
            coords[count++] = 0;

            for(i = facetCount; --i >= 0; )
            {
                coords[count++] = baseCoordinates[base_count++];
                coords[count++] = -half_height;
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

        int i;
        float[] normals = data.normals;
        int count = 0;
        int vtx = 0;

        createRadialFlatNormal(vtx++);

        for(i = facetCount; --i > 0; )
        {
            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            normals[count + 9] = normal.x;
            normals[count + 10] = normal.y;
            normals[count + 11] = normal.z;

            normals[count + 12] = normal.x;
            normals[count + 13] = normal.y;
            normals[count + 14] = normal.z;

            createRadialFlatNormal(vtx++);

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            count += 6;
        }

        normals[count++] = normal.x;
        normals[count++] = normal.y;
        normals[count++] = normal.z;

        normals[count + 9] = normal.x;
        normals[count + 10] = normal.y;
        normals[count + 11] = normal.z;

        normals[count + 12] = normal.x;
        normals[count + 13] = normal.y;
        normals[count + 14] = normal.z;

        createRadialFlatNormal(0);

        normals[count++] = normal.x;
        normals[count++] = normal.y;
        normals[count++] = normal.z;

        normals[count++] = normal.x;
        normals[count++] = normal.y;
        normals[count++] = normal.z;

        normals[count++] = normal.x;
        normals[count++] = normal.y;
        normals[count++] = normal.z;

        count += 6;

        // Now generate the bottom if we need it.
        if(useEnds)
        {
            // The three vertices of the top in an unrolled loop
            for(i = facetCount; --i >= 0; )
            {
                normals[count++] = 0;
                normals[count++] = 1;
                normals[count++] = 0;

                normals[count++] = 0;
                normals[count++] = 1;
                normals[count++] = 0;

                normals[count++] = 0;
                normals[count++] = 1;
                normals[count++] = 0;
            }

            // The three vertices of the bottom in an unrolled loop
            for(i = facetCount; --i >= 0; )
            {
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

        int i;
        float[] normals = data.normals;
        int count = 0;
        int vtx = 0;

        for(i = facetCount; --i >= 0; )
        {
            createRadialFlatNormal(vtx++);

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;
        }

        // Last row of the facets is the same as the first items
        normals[count++] = normals[0];
        normals[count++] = normals[1];
        normals[count++] = normals[2];

        normals[count++] = normals[3];
        normals[count++] = normals[4];
        normals[count++] = normals[5];

        // Now generate the bottom if we need it.
        if(useEnds)
        {
            // The vertices of the top
            for(i = facetCount + 1; --i >= 0; )
            {
                normals[count++] = 0;
                normals[count++] = 1;
                normals[count++] = 0;

                normals[count++] = 0;
                normals[count++] = 1;
                normals[count++] = 0;
            }

            // The vertices of the bottom
            for(i = facetCount + 1; --i >= 0; )
            {
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

        int i;
        float[] normals = data.normals;
        int count = 0;
        int vtx = 0;

        createRadialFlatNormal(vtx++);

        for(i = facetCount; --i > 0; )
        {
            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            createRadialFlatNormal(vtx++);

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;
        }

        normals[count++] = normal.x;
        normals[count++] = normal.y;
        normals[count++] = normal.z;

        normals[count++] = normal.x;
        normals[count++] = normal.y;
        normals[count++] = normal.z;

        normals[count++] = normals[0];
        normals[count++] = normals[1];
        normals[count++] = normals[2];

        normals[count++] = normals[3];
        normals[count++] = normals[4];
        normals[count++] = normals[5];

        // Now generate the bottom if we need it.
        if(useEnds)
        {
            // The three vertices of the top in an unrolled loop
            for(i = facetCount + 2; --i >= 0; )
            {
                normals[count++] = 0;
                normals[count++] = 1;
                normals[count++] = 0;
            }

            // The three vertices of the bottom in an unrolled loop
            for(i = facetCount + 2; --i >= 0; )
            {
                normals[count++] = 0;
                normals[count++] = -1;
                normals[count++] = 0;
            }
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

        int i;
        float[] normals = data.normals;
        int count = 0;
        int vtx = 0;

        createRadialFlatNormal(vtx++);

        for(i = facetCount; --i > 0; )
        {
            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            normals[count + 6] = normal.x;
            normals[count + 7] = normal.y;
            normals[count + 8] = normal.z;

            createRadialFlatNormal(vtx++);

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            count += 3;
        }

        normals[count++] = normal.x;
        normals[count++] = normal.y;
        normals[count++] = normal.z;

        normals[count + 6] = normal.x;
        normals[count + 7] = normal.y;
        normals[count + 8] = normal.z;

        createRadialFlatNormal(0);

        normals[count++] = normal.x;
        normals[count++] = normal.y;
        normals[count++] = normal.z;

        normals[count++] = normal.x;
        normals[count++] = normal.y;
        normals[count++] = normal.z;

        count += 3;

        // Now generate the bottom if we need it.
        if(useEnds)
        {
            // The three vertices of the top in an unrolled loop
            for(i = facetCount; --i >= 0; )
            {
                normals[count++] = 0;
                normals[count++] = 1;
                normals[count++] = 0;

                normals[count++] = 0;
                normals[count++] = 1;
                normals[count++] = 0;

                normals[count++] = 0;
                normals[count++] = 1;
                normals[count++] = 0;

                normals[count++] = 0;
                normals[count++] = 1;
                normals[count++] = 0;
            }

            // The three vertices of the bottom in an unrolled loop
            for(i = facetCount; --i >= 0; )
            {
                normals[count++] = 0;
                normals[count++] = -1;
                normals[count++] = 0;

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

        int i;
        float[] normals = data.normals;
        int count = 0;
        int vtx = 0;


        for(i = facetCount; --i >= 0; )
        {
            createRadialFlatNormal(vtx++);

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;

            normals[count++] = normal.x;
            normals[count++] = normal.y;
            normals[count++] = normal.z;
        }

        // Now generate the bottom if we need it.
        if(useEnds)
        {
            // top
            for(i = facetCount + 1; --i >= 0; )
            {
                normals[count++] = 0;
                normals[count++] = 1;
                normals[count++] = 0;
            }

            // bottom
            for(i = facetCount + 1; --i >= 0; )
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

        numBaseValues = facetCount * 2;

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

            x = (float)(radius * Math.cos(angle));
            z = (float)(radius * Math.sin(angle));

            baseCoordinates[count++] = x;
            baseCoordinates[count++] = z;
        }
    }

    /**
     * Create a normal based on the given vertex position, assuming that it is
     * a point in space, relative to the origin but also ignores the Y
     * component. This will create a normal that points directly along the
     * vector from the origin to the point along the X-Z plane. Useful for
     * doing cones and cylinders.
     *
     * @param p The facet index of the point to calculate
     * @param A temporary value containing the normal value
     */
    private void createRadialFlatNormal(int p)
    {
        float x = baseCoordinates[p * 2];
        float z = baseCoordinates[p * 2 + 1];

        float mag = x * x + z * z;

        if(mag != 0.0)
        {
            mag = 1.0f / ((float) Math.sqrt(mag));
            normal.x = x * mag;
            normal.y = 0;
            normal.z = z * mag;
        }
        else
        {
            normal.x = 0;
            normal.y = 0;
            normal.z = 0;
        }
    }
}
