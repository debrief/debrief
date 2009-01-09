/*****************************************************************************
// *                      J3D.org Copyright (c) 2000
 *                           Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom;

// Standard imports
import javax.vecmath.Vector3f;

// Application specific imports

/**
 * A generator of Spring geometry with customisable inner radius, outer
 * radius, number of loops, spacing and facet count.
 * <p>
 *
 * The outer radius is the radius of the center of the tube that forms the
 * spring. The spring has the outer radius in the X-Z plane and it increments
 * along the positive Y axis. The first loop starts at the origin on the
 * positive X axis and rotates counter-clockwise when looking down the
 * -Y axis towards the X-Z plane.
 *
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class SpringGenerator extends GeometryGenerator
{
    /** The default inner radius of the torus */
    private static final float DEFAULT_INNER_RADIUS = 0.25f;

    /** The default outer radius of the torus */
    private static final float DEFAULT_OUTER_RADIUS = 1.0f;

    /** Default number of faces around the inner radius */
    private static final int DEFAULT_INNER_FACETS = 16;

    /** Default number of faces around the outer radius of one loop */
    private static final int DEFAULT_OUTER_FACETS = 16;

    /** Default number of loops to generate */
    private static final int DEFAULT_LOOP_COUNT = 4;

    /** Default spacing between loops */
    private static final float DEFAULT_LOOP_SPACING = 1.0f;

    /** The inner radius of the torus to generate */
    private float innerRadius;

    /** The outer radius of the torus to generate */
    private float outerRadius;

    /** The spacing between loops */
    private float loopSpacing;

    /** The number of loops to generate */
    private int loopCount;

    /** The number of sections used around the inner radius */
    private int innerFacetCount;

    /** The number of sections used around the outer radius */
    private int outerFacetCount;

    /**
     * The points on the spring defining the basic shape. The coordinates go
     * in strips around the outer diameter from the centerline to the apex,
     * and then centreline to the lower apex and cover one turn of the spring.
     */
    private float[] shapeCoordinates;

    /** Flag indicating shape values have changed */
    private boolean shapeChanged;

    /**
     * The loop that defines the outer radius center. Used for normal calcs.
     * The numer of items is the same as outerFacetCount
     */
    private float[] oradiusCoordinates;

    /**
     * Construct a default spring that has:<br>
     * inner radius: 0.25<br>
     * outer radius: 1.0<br>
     * inner facet count: 16<br>
     * outer facet count: 16<br>
     * loop count: 4<br>
     * loop spacing: 1.0<br>
     */
    public SpringGenerator()
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             DEFAULT_LOOP_SPACING,
             DEFAULT_LOOP_COUNT,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS);
    }

    /**
     * Construct a spring that has the given radius values with all other
     * values fixed at the defaults
     *
     * @param ir The inner radius to use
     * @param or The outer radius to use
     */
    public SpringGenerator(float ir, float or)
    {
        this(ir,
             or,
             DEFAULT_LOOP_SPACING,
             DEFAULT_LOOP_COUNT,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS);
    }

    /**
     * Construct a spring that has the given number of loops with all other
     * values fixed at the defaults. The loop count must be one or more.
     *
     * @param lc The loop count
     * @throws IllegalArgumentException The loop count was invalid
     */
    public SpringGenerator(int lc)
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             DEFAULT_LOOP_SPACING,
             lc,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS);
    }

    /**
     * Construct a spring with the given loop spacing and all other values
     * fixed at the defaults.
     *
     * @param spacing The spacing between loops
     */
    public SpringGenerator(float spacing)
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             spacing,
             DEFAULT_LOOP_COUNT,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS);
    }

    /**
     * Construct a spring that has the selected number of facets but with all
     * other values fixed at the defaults. The minimum number of facets is 3.
     *
     * @param ifc The number of facets to use around the inner radius
     * @param ofc The number of facets to use around the outer radius
     * @throws IllegalArgumentException The number of facets is less than 3
     */
    public SpringGenerator(int ifc, int ofc)
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             DEFAULT_LOOP_SPACING,
             DEFAULT_LOOP_COUNT,
             ifc,
             ofc);
    }

    /**
     * Construct a spring with the given radius, spacing and loop count
     * information. All other values are defaults. The loop count must be
     * greater than or equal to 1.
     *
     * @param ir The inner radius to use
     * @param or The outer radius to use
     * @param spacing The spacing between loops
     * @param lc The loop count
     * @throws IllegalArgumentException The loop count was invalid
     */
    public SpringGenerator(float ir, float or, float spacing, int lc)
    {
        this(ir, or, spacing, lc, DEFAULT_INNER_FACETS, DEFAULT_OUTER_FACETS);
    }

    /**
     * Construct a spring with the given radius, spacing and loop count
     * information, and facet count. The loop count must be greater than or
     * equal to 1 and the facet counts must be 3 or more.
     *
     * @param ir The inner radius to use
     * @param or The outer radius to use
     * @param spacing The spacing between loops
     * @param lc The loop count
     * @param ifc The number of facets to use around the inner radius
     * @param ofc The number of facets to use around the outer radius
     * @throws IllegalArgumentException The loop count was invalid or facet
     *   counts were less than 4
     */
    public SpringGenerator(float ir,
                           float or,
                           float spacing,
                           int lc,
                           int ifc,
                           int ofc)
    {
        if((ifc < 4) || (ofc < 4))
            throw new IllegalArgumentException("Number of facets is < 4");

        if(ifc % 4 != 0)
            throw new IllegalArgumentException("Inner facets not / 4");

        if(lc < 1)
            throw new IllegalArgumentException("Loop count < 1");

        innerRadius = ir;
        outerRadius = or;

        innerFacetCount = ifc;
        outerFacetCount = ofc;

        loopCount = lc;
        loopSpacing = spacing;

        shapeChanged = true;
    }

    /**
     * Get the dimensions of the spring. These are returned as 2 values of
     * inner and outer radius respectively and then spacing and loop count
     * (converted to a float for this case) for the array. A new array is
     * created each time so you can do what you like with it.
     *
     * @return The current size of the spring
     */
    public float[] getDimensions()
    {
        return new float[] {innerRadius, outerRadius, loopSpacing, loopCount};
    }

    /**
     * Change the dimensions of the torus to be generated. Calling this will
     * make the points be re-calculated next time you ask for geometry or
     * normals.
     *
     * @param ir The ir of the cone to generate
     * @param or The or of the bottom of the cone
     * @param ends True if to generate faces for the ends
     */
    public void setDimensions(float ir, float or)
    {
        if((innerRadius != ir) || (outerRadius != or))
        {
            shapeChanged = true;

            innerRadius = ir;
            outerRadius = or;
        }
    }

    /**
     * Change the loop information. Calling this will make the points be
     * re-calculated next time you ask for geometry or normals.
     *
     * @param spacing The spacing between loops
     * @param lc The loop count
     * @throws IllegalArgumentException The loop count was invalid
     */
    public void setLoopDimensions(float spacing, int lc)
    {
        if((loopSpacing != spacing) || (loopCount != lc))
        {
            shapeChanged = true;

            loopSpacing = spacing;
            loopCount = lc;
        }
    }

    /**
     * Change the number of facets used to create this spring. This will cause
     * the geometry to be regenerated next time they are asked for.
     * The minimum number of facets is 3.
     *
     * @param ifc The number of facets to use around the inner radius
     * @param ofc The number of facets to use around the outer radius
     * @throws IllegalArgumentException The number of facets is less than 4
     */
    public void setFacetCount(int ifc, int ofc)
    {
        if((ifc < 4) || (ofc < 4))
            throw new IllegalArgumentException("Number of facets is < 4");

        if(ifc % 4 != 0)
            throw new IllegalArgumentException("Inner facets not / 4");

        innerFacetCount = ifc;
        outerFacetCount = ofc;

        shapeChanged = true;
    }


    /**
     * Get the number of vertices that this generator will create for the
     * shape given in the definition.
     *
     * @param data The data to shape the calculations on
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
                ret_val = innerFacetCount * outerFacetCount * 6;
                break;
            case GeometryData.QUADS:
                ret_val = innerFacetCount * outerFacetCount * 4;
                break;

            // These all have the same vertex count
            case GeometryData.TRIANGLE_STRIPS:
//            case GeometryData.TRIANGLE_FANS:
                ret_val = innerFacetCount * (outerFacetCount + 1) * 2;
                break;
            case GeometryData.INDEXED_TRIANGLES:
            case GeometryData.INDEXED_QUADS:
            case GeometryData.INDEXED_TRIANGLE_STRIPS:
//            case GeometryData.INDEXED_TRIANGLE_FANS:
                ret_val = innerFacetCount * (outerFacetCount + 1);
                break;

            default:
                throw new UnsupportedTypeException("Unknown geometry type: " +
                                                   data.geometryType);
        }

        ret_val *= loopCount;

        return ret_val;
    }

    /**
     * Generate a new set of geometry items shaped on the passed data. If the
     * data does not contain the right minimum array lengths an exception will
     * be generated. If the array reference is null, this will create arrays
     * of the correct length and assign them to the return value.
     *
     * @param data The data to shape the calculations on
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
//            case GeometryData.TRIANGLE_FANS:
//                triangleFans(data);
//                break;
            case GeometryData.INDEXED_QUADS:
                indexedQuads(data);
                break;
            case GeometryData.INDEXED_TRIANGLES:
                indexedTriangles(data);
                break;
            case GeometryData.INDEXED_TRIANGLE_STRIPS:
                indexedTriangleStrips(data);
                break;
//            case GeometryData.INDEXED_TRIANGLE_FANS:
//                indexedTriangleFans(data);
//                break;

            default:
                throw new UnsupportedTypeException("Unknown geometry type: " +
                                                   data.geometryType);
        }
    }


    /**
     * Generate a new set of points for an unindexed quad array
     *
     * @param data The data to shape the calculations on
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
     * @param data The data to shape the calculations on
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
     * @param data The data to shape the calculations on
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
        int count = 0;

        int i, k;
        int pos;
        int k_facet;

        // Do the first loop
        for(k = 0; k < innerFacetCount - 1; k++)
        {
            k_facet = k * (outerFacetCount + 1);

            for(i = 0; i < outerFacetCount; i++)
            {
                pos = i + k_facet;

                indexes[count++] = pos + outerFacetCount + 1;
                indexes[count++] = pos;
                indexes[count++] = pos + 1;
                indexes[count++] = pos + outerFacetCount + 2;
            }
        }

        k_facet = (innerFacetCount - 1) * (outerFacetCount + 1);

        for(i = 0; i < outerFacetCount; i++)
        {
            pos = i + k_facet;

            indexes[count++] = i;
            indexes[count++] = pos;
            indexes[count++] = pos + 1;
            indexes[count++] = i + 1;
        }

        for(i = 1; i < loopCount; i++)
        {
            System.arraycopy(indexes, 0, indexes, i * count, count);
        }

        // Now increment all the index values by the loop count
        int index_count = indexes[count - 2] + 1;
        int index_in_loop = count;

        for(k = 1; k < loopCount; k++)
        {
            for(i = index_in_loop; --i >= 0; )
                indexes[count++] += index_count * k;
        }
    }

    /**
     * Generate a new set of points for an indexed triangle array
     *
     * @param data The data to shape the calculations on
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
        int count = 0;

        int i, k;
        int pos;
        int k_facet;

        // Do the first loop
        for(k = 0; k < innerFacetCount - 1; k++)
        {
            k_facet = k * (outerFacetCount + 1);

            for(i = 0; i < outerFacetCount; i++)
            {
                pos = i + k_facet;

                // first triangle
                indexes[count++] = pos + outerFacetCount + 1;
                indexes[count++] = pos;
                indexes[count++] = pos + 1;

                // second triangle
                indexes[count++] = pos + 1;
                indexes[count++] = pos + outerFacetCount + 2;
                indexes[count++] = pos + outerFacetCount + 1;
            }
        }

        k_facet = (innerFacetCount - 1) * (outerFacetCount + 1);

        for(i = 0; i < outerFacetCount; i++)
        {
            pos = i + k_facet;

            // first triangle
            indexes[count++] = i;
            indexes[count++] = pos;
            indexes[count++] = pos + 1;

            // second triangle
            indexes[count++] = pos + 1;
            indexes[count++] = i + 1;
            indexes[count++] = i;
        }

        for(i = 1; i < loopCount; i++)
        {
            System.arraycopy(indexes, 0, indexes, i * count, count);
        }

        // Now increment all the index values by the loop count
        int index_count = indexes[count - 3] + 1;
        int index_in_loop = count;

        for(k = 1; k < loopCount; k++)
        {
            for(i = index_in_loop; --i >= 0; )
                indexes[count++] += index_count * k;
        }
    }

    /**
     * Generate a new set of points for a triangle strip array. Each side is a
     * strip of two faces.
     *
     * @param data The data to shape the calculations on
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

        int num_strips = innerFacetCount * loopCount;

        if(data.stripCounts == null)
            data.stripCounts = new int[num_strips];
        else if(data.stripCounts.length < num_strips)
            throw new InvalidArraySizeException("Strip counts",
                                                data.stripCounts.length,
                                                num_strips);

        int strip_length = (outerFacetCount + 1) << 1;
        int[] stripCounts = data.stripCounts;

        for(int i = num_strips; --i >= 0; )
            stripCounts[i] = strip_length;
    }

    /**
     * Generate a new set of points for an indexed triangle strip array. We
     * build the strip from the existing points, and there's no need to
     * re-order the points for the indexes. The strips go around the major
     * circumference.
     *
     * @param data The data to shape the calculations on
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
        int index_size = innerFacetCount * (outerFacetCount + 1) * 2 * loopCount;
        int num_strips = innerFacetCount * loopCount;

        if(data.indexes == null)
            data.indexes = new int[index_size];
        else if(data.indexes.length < index_size)
            throw new InvalidArraySizeException("Coordinates",
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

        int strip_length = (outerFacetCount + 1) << 1;
        int count = 0;
        int i, k;
        int pos;
        int k_facet;

        for(i = num_strips; --i >= 0; )
            stripCounts[i] = strip_length;

        // Wind the top half separately from the bottom
        for(k = 0; k < innerFacetCount - 1; k++)
        {
            k_facet = k * (outerFacetCount + 1);

            for(i = 0; i <= outerFacetCount; i++)
            {
                pos = i + k_facet;

                // first triangle
                indexes[count++] = pos + outerFacetCount + 1;
                indexes[count++] = pos;
            }
        }

        // The last strip contains the first row index values
        k_facet = (innerFacetCount - 1) * (outerFacetCount + 1);

        for(i = 0; i <= outerFacetCount; i++)
        {
            pos = i + k_facet;

            // first triangle
            indexes[count++] = i;
            indexes[count++] = pos;
        }


        for(i = 1; i < loopCount; i++)
            System.arraycopy(indexes, 0, indexes, i * count, count);

        // Now increment all the index values by the loop count
        int index_count = indexes[count - 1] + 1;
        int index_in_loop = count;

        for(k = 1; k < loopCount; k++)
        {
            for(i = index_in_loop; --i >= 0; )
                indexes[count++] += index_count * k;
        }
    }

    /**
     * Generates new set of points suitable for use in an unindexed array. Each
     * shape coordinate will appear twice in this list. The first half of the
     * array is the top, the second half, the bottom.
     *
     * @param data The data to shape the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = innerFacetCount * outerFacetCount * 6 * loopCount;

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;

        generateShape();

        // quad torus generates coordinates at facetCount * 3 indexes apart.
        // Go around and build coordinate arrays from this.
        int i, k;
        int facet_inc = (outerFacetCount + 1) * 3;
        int k_facet;
        int pos;
        int count = 0;
        float y_offset = loopSpacing * (loopCount / 2);

        for(k = 0; k < innerFacetCount; k++)
        {
            k_facet = k * facet_inc;

            for(i = 0; i < outerFacetCount; i++)
            {
                pos = i * 3 + k_facet;

                // triangle 1
                coords[count++] = shapeCoordinates[pos + facet_inc];
                coords[count++] = shapeCoordinates[pos + facet_inc + 1] + y_offset;
                coords[count++] = shapeCoordinates[pos + facet_inc + 2];

                coords[count++] = shapeCoordinates[pos];
                coords[count++] = shapeCoordinates[pos + 1] + y_offset;
                coords[count++] = shapeCoordinates[pos + 2];

                coords[count++] = shapeCoordinates[pos + 3];
                coords[count++] = shapeCoordinates[pos + 4] + y_offset;
                coords[count++] = shapeCoordinates[pos + 5];

                // triangle 2
                coords[count++] = shapeCoordinates[pos + 3];
                coords[count++] = shapeCoordinates[pos + 4] + y_offset;
                coords[count++] = shapeCoordinates[pos + 5];

                coords[count++] = shapeCoordinates[pos + facet_inc + 3];
                coords[count++] = shapeCoordinates[pos + facet_inc + 4] + y_offset;
                coords[count++] = shapeCoordinates[pos + facet_inc + 5];

                coords[count++] = shapeCoordinates[pos + facet_inc];
                coords[count++] = shapeCoordinates[pos + facet_inc + 1] + y_offset;
                coords[count++] = shapeCoordinates[pos + facet_inc + 2];
            }
        }

        // Now that we have one loop, copy the coordinates along the array and
        // then loop and add the Y increment appropriately.
        for(i = 1; i < loopCount; i++)
        {
            System.arraycopy(coords, 0, coords, i * count, count);
        }

        // set count to be the y coordinate
        count++;
        int vtx_in_loop = count / 3;
        y_offset = 0;

        for(i = 1; i < loopCount; i++)
        {
            y_offset += loopSpacing;

            for(k = 0; k < vtx_in_loop; k++)
            {
                coords[count] += y_offset;
                count += 3;
            }
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
        int vtx_cnt = innerFacetCount * outerFacetCount * 4 * loopCount;

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;

        generateShape();

        // quad torus generates coordinates at facetCount * 3 indexes apart.
        // Go around and build coordinate arrays from this.
        int i, k;
        int facet_inc = (outerFacetCount + 1) * 3;
        int k_facet;
        int pos;
        int count = 0;
        float y_offset = loopSpacing * (loopCount / 2);

        for(k = 0; k < innerFacetCount; k++)
        {
            k_facet = k * facet_inc;

            for(i = 0; i < outerFacetCount; i++)
            {
                pos = i * 3 + k_facet;

                coords[count++] = shapeCoordinates[pos + facet_inc];
                coords[count++] = shapeCoordinates[pos + facet_inc + 1] + y_offset;
                coords[count++] = shapeCoordinates[pos + facet_inc + 2];

                coords[count++] = shapeCoordinates[pos];
                coords[count++] = shapeCoordinates[pos + 1] + y_offset;
                coords[count++] = shapeCoordinates[pos + 2];

                coords[count++] = shapeCoordinates[pos + 3];
                coords[count++] = shapeCoordinates[pos + 4] + y_offset;
                coords[count++] = shapeCoordinates[pos + 5];

                coords[count++] = shapeCoordinates[pos + facet_inc + 3];
                coords[count++] = shapeCoordinates[pos + facet_inc + 4] + y_offset;
                coords[count++] = shapeCoordinates[pos + facet_inc + 5];
            }
        }

        // Now that we have one loop, copy the coordinates along the array and
        // then loop and add the Y increment appropriately.
        for(i = 1; i < loopCount; i++)
        {
            System.arraycopy(coords, 0, coords, i * count, count);
        }

        // set count to be the y coordinate
        count++;
        int vtx_in_loop = count / 3;
        y_offset = 0;

        for(i = 1; i < loopCount; i++)
        {
            y_offset += loopSpacing;

            for(k = 0; k < vtx_in_loop; k++)
            {
                coords[count] += y_offset;
                count += 3;
            }
        }
    }

    /**
     * Generates new set of points suitable for use in an triangle strip array.
     * Each strip goes along the outer radius.
     * The first half of the array is the top, the second half, the bottom.
     *
     * @param data The data to shape the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriStripCoordinates(GeometryData data)
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

        generateShape();

        // quad torus generates coordinates at facetCount * 3 indexes apart.
        // Go around and build coordinate arrays from this.
        int i, k;
        int facet_inc = (outerFacetCount + 1) * 3;
        int k_facet;
        int pos;
        int count = 0;
        float y_offset = loopSpacing * (loopCount / 2);

        for(k = 0; k < innerFacetCount - 1; k++)
        {
            k_facet = k * facet_inc;

            for(i = 0; i <= outerFacetCount; i++)
            {
                pos = i * 3 + k_facet;

                coords[count++] = shapeCoordinates[pos + facet_inc];
                coords[count++] = shapeCoordinates[pos + facet_inc + 1] + y_offset;
                coords[count++] = shapeCoordinates[pos + facet_inc + 2];

                coords[count++] = shapeCoordinates[pos];
                coords[count++] = shapeCoordinates[pos + 1] + y_offset;
                coords[count++] = shapeCoordinates[pos + 2];
            }
        }

        k_facet = k * facet_inc;

        for(i = 0; i <= outerFacetCount; i++)
        {
            pos = i * 3 + k_facet;

            coords[count++] = shapeCoordinates[i * 3];
            coords[count++] = shapeCoordinates[i * 3 + 1] + y_offset;
            coords[count++] = shapeCoordinates[i * 3 + 2];

            coords[count++] = shapeCoordinates[pos];
            coords[count++] = shapeCoordinates[pos + 1] + y_offset;
            coords[count++] = shapeCoordinates[pos + 2];
        }

        // Now that we have one loop, copy the coordinates along the array and
        // then loop and add the Y increment appropriately.
        for(i = 1; i < loopCount; i++)
        {
            System.arraycopy(coords, 0, coords, i * count, count);
        }

        // set count to be the y coordinate
        count++;
        int vtx_in_loop = count / 3;
        y_offset = 0;

        for(i = 1; i < loopCount; i++)
        {
            y_offset += loopSpacing;

            for(k = 0; k < vtx_in_loop; k++)
            {
                coords[count] += y_offset;
                count += 3;
            }
        }
    }

    /**
     * Generates new set of points suitable for use in an indexed quad array.
     * This array is your basic shape, but with the bottom part mirrored if
     * need be.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateIndexedCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = getVertexCount(data);

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        int i, k;
        int count = vtx_cnt * 3 / loopCount;
        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;

        generateShape();

        // First copy all the data points for each loop.
        for(i = 0; i < loopCount; i++)
        {
            System.arraycopy(shapeCoordinates, 0, coords, i * count, count);
        }

        // set count to be the y coordinate and now go through and increment
        // the Y offset according to what loop we are in.
        int vtx_in_loop = count / 3;
        float y_offset = loopSpacing * (loopCount / 2);

        count = 1;

        for(i = loopCount; --i >= 0; )
        {
            for(k = vtx_in_loop; --k >= 0; )
            {
                coords[count] += y_offset;
                count += 3;
            }

            y_offset += loopSpacing;
        }
    }

    //------------------------------------------------------------------------
    // Normal generation routines
    //------------------------------------------------------------------------

    /**
     * Generate the normals for this torus generated from unindexed triangle
     * coordinates. Assumes that coordinates have been generated first.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriNormals(GeometryData data)
        throws InvalidArraySizeException
    {
        if(data.normals == null)
            data.normals = new float[data.coordinates.length];
        else if(data.normals.length < data.coordinates.length)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                data.coordinates.length);

        float[] normals = data.normals;
        Vector3f norm;
        int count = 0;
        int facet_offset = 0;
        int i, k;

        // Wonder if we can do any loop unravelling here?
        for(k = 0; k < innerFacetCount; k++)
        {
            facet_offset = 0;

            for(i = outerFacetCount; --i > 0; )
            {
                // triangle 1
                norm = createRadialNormal(data.coordinates,
                                          count,
                                          oradiusCoordinates,
                                          facet_offset);
                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                norm = createRadialNormal(data.coordinates,
                                          count,
                                          oradiusCoordinates,
                                          facet_offset);

                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                norm = createRadialNormal(data.coordinates,
                                          count,
                                          oradiusCoordinates,
                                          facet_offset + 3);

                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                // triangle 2
                norm = createRadialNormal(data.coordinates,
                                          count,
                                          oradiusCoordinates,
                                          facet_offset + 3);
                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                norm = createRadialNormal(data.coordinates,
                                          count,
                                          oradiusCoordinates,
                                          facet_offset + 3);

                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                norm = createRadialNormal(data.coordinates,
                                          count,
                                          oradiusCoordinates,
                                          facet_offset);

                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                facet_offset += 3;
            }

            // triangle 1
            norm = createRadialNormal(data.coordinates,
                                      count,
                                      oradiusCoordinates,
                                      facet_offset);
            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            norm = createRadialNormal(data.coordinates,
                                      count,
                                      oradiusCoordinates,
                                      facet_offset);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            norm = createRadialNormal(data.coordinates,
                                      count,
                                      oradiusCoordinates,
                                      0);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            // triangle 2
            norm = createRadialNormal(data.coordinates,
                                      count,
                                      oradiusCoordinates,
                                      0);
            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            norm = createRadialNormal(data.coordinates,
                                      count,
                                      oradiusCoordinates,
                                      0);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            norm = createRadialNormal(data.coordinates,
                                      count,
                                      oradiusCoordinates,
                                      facet_offset);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;
        }

        // Now that we have one loop, copy the normals along the array for
        // all the other loops as they should all point in the same direction
        for(i = 1; i < loopCount; i++)
        {
            System.arraycopy(normals, 0, normals, i * count, count);
        }
    }

    /**
     * Generate the normals for this torus generated from unindexed quad
     * coordinates. Assumes that coordinates have been generated first.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedQuadNormals(GeometryData data)
        throws InvalidArraySizeException
    {
        if(data.normals == null)
            data.normals = new float[data.coordinates.length];
        else if(data.normals.length < data.coordinates.length)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                data.coordinates.length);

        float[] normals = data.normals;
        Vector3f norm;
        int count = 0;
        int facet_offset = 0;
        int i, k;

        // Wonder if we can do any loop unravelling here?
        for(k = 0; k < innerFacetCount; k++)
        {
            facet_offset = 0;

            norm = createRadialNormal(data.coordinates,
                                      count,
                                      oradiusCoordinates,
                                      facet_offset);
            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            norm = createRadialNormal(data.coordinates,
                                      count,
                                      oradiusCoordinates,
                                      facet_offset);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            for(i = outerFacetCount; --i > 0; )
            {
                facet_offset += 3;

                norm = createRadialNormal(data.coordinates,
                                          count,
                                          oradiusCoordinates,
                                          facet_offset);

                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                normals[count + 6] = norm.x;
                normals[count + 7] = norm.y;
                normals[count + 8] = norm.z;

                norm = createRadialNormal(data.coordinates,
                                          count,
                                          oradiusCoordinates,
                                          facet_offset);

                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                count += 3;
            }

            norm = createRadialNormal(data.coordinates,
                                      count,
                                      oradiusCoordinates,
                                      0);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;

            norm = createRadialNormal(data.coordinates,
                                      count,
                                      oradiusCoordinates,
                                      0);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;
        }

        // Now that we have one loop, copy the normals along the array for
        // all the other loops as they should all point in the same direction
        for(i = 1; i < loopCount; i++)
        {
            System.arraycopy(normals, 0, normals, i * count, count);
        }
    }

    /**
     * Generate the normals for this torus generated from unindexed triangle
     * strip coordinates. Assumes that coordinates have been generated first.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriStripNormals(GeometryData data)
        throws InvalidArraySizeException
    {
        if(data.normals == null)
            data.normals = new float[data.coordinates.length];
        else if(data.normals.length < data.coordinates.length)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                data.coordinates.length);

        float[] normals = data.normals;
        Vector3f norm;
        int count = 0;
        int facet_offset = 0;
        int i, k;

        // Wonder if we can do any loop unravelling here?
        for(k = 0; k < innerFacetCount; k++)
        {
            facet_offset = 0;

            for(i = outerFacetCount + 1; --i >= 0; )
            {
                norm = createRadialNormal(data.coordinates,
                                          count,
                                          oradiusCoordinates,
                                          facet_offset);

                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                norm = createRadialNormal(data.coordinates,
                                          count,
                                          oradiusCoordinates,
                                          facet_offset);

                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                facet_offset += 3;
            }
        }

        // Now that we have one loop, copy the normals along the array for
        // all the other loops as they should all point in the same direction
        for(i = 1; i < loopCount; i++)
        {
            System.arraycopy(normals, 0, normals, i * count, count);
        }
    }

    /**
     * Generate the normals for this torus generated from indexed normals
     * coordinates. Assumes that coordinates have been generated first.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateIndexedNormals(GeometryData data)
        throws InvalidArraySizeException
    {
        if(data.normals == null)
            data.normals = new float[data.coordinates.length];
        else if(data.normals.length < data.coordinates.length)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                data.coordinates.length);

        float[] normals = data.normals;
        Vector3f norm;
        int count = 0;
        int facet_count = 0;
        int i, k;

        // Wonder if we can do any loop unravelling here?
        for(k = 0; k < innerFacetCount; k++)
        {
            facet_count = 0;

            for(i = outerFacetCount + 1; --i >= 0; )
            {
                norm = createRadialNormal(data.coordinates,
                                          count,
                                          oradiusCoordinates,
                                          facet_count);

                normals[count++] = norm.x;
                normals[count++] = norm.y;
                normals[count++] = norm.z;

                facet_count += 3;
            }
        }

        // Now that we have one loop, copy the normals along the array for
        // all the other loops as they should all point in the same direction
        for(i = 1; i < loopCount; i++)
        {
            System.arraycopy(normals, 0, normals, i * count, count);
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
     * @param data The data to shape the calculations on
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
     * @param data The data to shape the calculations on
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

    //------------------------------------------------------------------------
    // Internal shape generation routines
    //------------------------------------------------------------------------

    /**
     * Generates new set of points. If the dimensions have not changed since
     * the last call, the identical array will be returned. Note that you
     * should make a copy of this if you intend to call this method more than
     * once as it will replace the old values with the new ones and not
     * reallocate the array. The points only deal with one loop.
     *
     * @return An array of points representing the geometry vertices
     */
    private void generateShape()
    {
       if(!shapeChanged)
            return;

        shapeChanged = false;

        int vtx_total = (innerFacetCount + 1) * (outerFacetCount + 1) * 3;

        if((shapeCoordinates == null) ||
           (vtx_total > shapeCoordinates.length))
        {
            shapeCoordinates = new float[vtx_total];
            oradiusCoordinates = new float[(outerFacetCount + 1) * 3];
        }

        // Increment angles for the inner and outer radius facets.
        double arc_length = (2 * Math.PI * outerRadius) / outerFacetCount;
        float outer_theta = (float)(arc_length / outerRadius);

        arc_length = (2 * Math.PI * innerRadius) / innerFacetCount;
        float inner_theta = (float)(arc_length / innerRadius);

        int count = 0;
        int i, k;
        float r;            // radius of current loop
        float y;
        float[] cos_theta_table;
        float[] sin_theta_table;

        float y_space = -loopSpacing / 2;
        float y_inc = loopSpacing / outerFacetCount;

        // Generate the upper half. The loop goes around the outer radius
        // adding a quad at a time. When this is finished it moves up one facet
        // around the inner radius. It does this until it reaches the opposite
        // "inner" side of the torus.

        // Now set up the radius coordinate list and first loop as one thing.
        // this is for efficiency to minimise trig calculations
        r = outerRadius + innerRadius;
        cos_theta_table = new float[outerFacetCount];
        sin_theta_table = new float[outerFacetCount];
        float y_offset = loopSpacing * (loopCount / 2);

        for(i = 0 ; i < outerFacetCount; i++)
        {
            cos_theta_table[i] = (float)Math.cos(outer_theta * i);
            sin_theta_table[i] = (float)Math.sin(outer_theta * i);

            oradiusCoordinates[count++] = outerRadius * cos_theta_table[i];
            oradiusCoordinates[count++] = y_space + y_offset;
            oradiusCoordinates[count++] = -outerRadius * sin_theta_table[i];

            y_space += y_inc;
        }

        oradiusCoordinates[count++] = oradiusCoordinates[0];
        oradiusCoordinates[count++] = y_space;
        oradiusCoordinates[count++] = oradiusCoordinates[2];

        // Generate one loop. The loop goes around the outer radius
        // adding a quad at a time. When this is finished it moves up one facet
        // around the inner radius. It does this until it reaches the opposite
        // "inner" side of the torus.
        count = 0;
        for(k = 0; k < innerFacetCount; k++)
        {
            y_space = -loopSpacing / 2;
            r = outerRadius + (innerRadius * ((float) Math.cos(inner_theta * k)));

            // y values don't get recalculed for each quad on this strip
            y = innerRadius * (float)Math.sin(inner_theta * k);

            for(i = 0 ; i < outerFacetCount; i++)
            {
                shapeCoordinates[count++] = r * cos_theta_table[i];
                shapeCoordinates[count++] = y + y_space;
                shapeCoordinates[count++] = -r * sin_theta_table[i];

                // increment for variable to get to the other side of
                // this quad.
                y_space += y_inc;
            }

            // The closing piece of the loop
            shapeCoordinates[count++] = r * cos_theta_table[0];
            shapeCoordinates[count++] = y + y_space;
            shapeCoordinates[count++] = -r * sin_theta_table[0];
        }

        System.arraycopy(shapeCoordinates,
                         0,
                         shapeCoordinates,
                         count,
                         (outerFacetCount + 1) * 3);
    }
}