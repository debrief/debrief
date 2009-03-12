/*****************************************************************************
 *  J3D.org Copyright (c) 2000
 *   Java Source
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
 * A generator of Torus geometry values.
 * <p>
 *
 * The outer radius is the radius of the center of the tube that forms the
 * torus.The torus has the outer radius in the X-Z plane and it increments
 * along the positive Y axis. The loop starts at the origin on the
 * positive X axis and rotates counter-clockwise when looking down the
 * -Y axis towards the X-Z plane.
 * <p>
 *
 * <b>TODO</b><br>
 * Look at trig optimisation techniques such as creating lookup tables.
 *
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class TorusGenerator extends GeometryGenerator
{
    /** The default inner radius of the torus */
    private static final float DEFAULT_INNER_RADIUS = 0.25f;

    /** The default outer radius of the torus */
    private static final float DEFAULT_OUTER_RADIUS = 1.0f;

    /** Default number of faces around the inner radius */
    private static final int DEFAULT_INNER_FACETS = 16;

    /** Default number of faces around the outer radius */
    private static final int DEFAULT_OUTER_FACETS = 16;

    /** The inner radius of the torus to generate */
    private float innerRadius;

    /** The outer radius of the torus to generate */
    private float outerRadius;

    /** The number of sections used around the inner radius */
    private int innerFacetCount;

    /** The number of sections used around the outer radius */
    private int outerFacetCount;

    /**
     * The points on the torus defining the basic shape. The coordinates go
     * in strips around the outer diameter from the centerline to the apex,
     * and then centreline to the lower apex.
     */
    private float[] shapeCoordinates;

    /** The number of values used in the shape coordinate array */
    private int numShapeValues;

    /** Flag indicating shape values have changed */
    private boolean shapeChanged;

    /**
     * The loop that defines the outer radius center. Used for normal calcs.
     * The numer of items is the same as outerFacetCount
     */
    private float[] oradiusCoordinates;

    /**
     * Construct a default torus. The inner radius is 0.5, outer radius 2.0
     * and there are 16 facets in each direction for the inner and outer
     * radius.
     */
    public TorusGenerator()
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS);
    }

    /**
     * Construct a torus with the given inner and outer radius values.
     * There are 16 facets in each direction for the inner and outer radius.
     *
     * @param ir The inner radius to use
     * @param or The outer radius to use
     */
    public TorusGenerator(float ir, float or)
    {
        this(ir, or, DEFAULT_INNER_FACETS, DEFAULT_OUTER_FACETS);
    }

    /**
     * Construct a default torus with a selectable number of facets. The inner
     * radius is 0.5 and the outer radius is 2.0. The minimum number of facets
     * is 3.
     *
     * @param ifc The number of facets to use around the inner radius
     * @param ofc The number of facets to use around the outer radius
     * @throws IllegalArgumentException The number of facets is less than 3
     */
    public TorusGenerator(int ifc, int ofc)
    {
        this(DEFAULT_INNER_RADIUS, DEFAULT_OUTER_RADIUS, ifc, ofc);
    }

    /**
     * Construct a torus with given radius values and number of facets. The
     * minimum number of facets is 3.
     *
     * @param ir The inner radius to use
     * @param or The outer radius to use
     * @param ifc The number of facets to use around the inner radius
     * @param ofc The number of facets to use around the outer radius
     * @throws IllegalArgumentException The number of facets is less than 4
     */
    public TorusGenerator(float ir, float or, int ifc, int ofc)
    {
        if((ifc < 4) || (ofc < 4))
            throw new IllegalArgumentException("Number of facets is < 4");

        if(ifc % 2 != 0)
            throw new IllegalArgumentException("Inner facets not / 2");

        innerRadius = ir;
        outerRadius = or;

        innerFacetCount = ifc;
        outerFacetCount = ofc;

        shapeChanged = true;
    }

    /**
     * Get the dimensions of the cylinder. These are returned as 2 values of
     * inner and outer radius respectively for the array. A new array is
     * created each time so you can do what you like with it.
     *
     * @return The current size of the cone
     */
    public float[] getDimensions()
    {
        return new float[] { innerRadius, outerRadius };
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
     * Change the number of facets used to create this cone. This will cause
     * the geometry to be regenerated next time they are asked for.
     * The minimum number of facets is 4.
     *
     * @param ifc The number of facets to use around the inner radius
     * @param ofc The number of facets to use around the outer radius
     * @throws IllegalArgumentException The number of facets is less than 4
     */
    public void setFacetCount(int ifc, int ofc)
    {
        if((ifc < 4) || (ofc < 4))
            throw new IllegalArgumentException("Number of facets is < 4");

        if(ifc % 2 != 0)
            throw new IllegalArgumentException("Inner facets not / 2");


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
            case GeometryData.INDEXED_TRIANGLES:
            case GeometryData.INDEXED_QUADS:
            case GeometryData.INDEXED_TRIANGLE_STRIPS:
//            case GeometryData.INDEXED_TRIANGLE_FANS:
                ret_val = innerFacetCount * (outerFacetCount + 1) * 2;
                break;

            default:
                throw new UnsupportedTypeException("Unknown geometry type: " +
                                                   data.geometryType);
        }

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

        int half = innerFacetCount / 2;
        int i, k;
        int last_facet = outerFacetCount - 1; // always stop one short of the end
        int pos;
        int k_facet;

        // Wind the top half separately from the bottom
        for(k = 0; k < half; k++)
        {
            k_facet = k * innerFacetCount;

            for(i = 0; i < last_facet; i++)
            {
                pos = i + k_facet;
                indexes[count++] = pos + outerFacetCount;
                indexes[count++] = pos;
                indexes[count++] = pos + 1;
                indexes[count++] = pos + outerFacetCount + 1;
            }

            // now the last remaing quad that uses coords 0 & 1
            pos = i + k_facet;

            indexes[count++] = pos + outerFacetCount;
            indexes[count++] = pos;
            indexes[count++] = k_facet;
            indexes[count++] = k_facet + outerFacetCount;
        }

        // Bottom half is wound in the opposite order.
        for(k = half + 1; k <= half * 2; k++)
        {
            k_facet = k * innerFacetCount;

            for(i = 0; i < last_facet; i++)
            {
                pos = i + k_facet;

                indexes[count++] = pos;
                indexes[count++] = pos + outerFacetCount;
                indexes[count++] = pos + outerFacetCount + 1;
                indexes[count++] = pos + 1;
            }

            // now the last remaing quad that uses coords 0 & 1
            pos = i + k_facet;

            indexes[count++] = pos;
            indexes[count++] = pos + outerFacetCount;
            indexes[count++] = k_facet + outerFacetCount;
            indexes[count++] = k_facet;
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

        int half = innerFacetCount / 2;
        int i, k;
        int last_facet = outerFacetCount - 1; // always stop one short of the end
        int pos;
        int k_facet;

        // Wind the top half separately from the bottom
        for(k = 0; k < half; k++)
        {
            k_facet = k * innerFacetCount;

            for(i = 0; i < last_facet; i++)
            {
                pos = i + k_facet;

                // first triangle
                indexes[count++] = pos + outerFacetCount;
                indexes[count++] = pos;
                indexes[count++] = pos + 1;

                // second triangle
                indexes[count++] = pos + 1;
                indexes[count++] = pos + outerFacetCount + 1;
                indexes[count++] = pos + outerFacetCount;
            }

            // now the last remaing quad that uses coords 0 & 1
            pos = i + k_facet;

            indexes[count++] = pos + outerFacetCount;
            indexes[count++] = pos;
            indexes[count++] = k_facet;


            indexes[count++] = k_facet;
            indexes[count++] = k_facet + outerFacetCount;
            indexes[count++] = pos + outerFacetCount;
        }

        // Bottom half is wound in the opposite order.
        for(k = half + 1; k <= half * 2; k++)
        {
            k_facet = k * innerFacetCount;

            for(i = 0; i < last_facet; i++)
            {
                pos = i + k_facet;

                indexes[count++] = pos;
                indexes[count++] = pos + outerFacetCount;
                indexes[count++] = pos + outerFacetCount + 1;

                indexes[count++] = pos + outerFacetCount + 1;
                indexes[count++] = pos + 1;
                indexes[count++] = pos;
            }

            // now the last remaing quad that uses coords 0 & 1
            pos = i + k_facet;

            indexes[count++] = pos;
            indexes[count++] = pos + outerFacetCount;
            indexes[count++] = k_facet + outerFacetCount;

            indexes[count++] = k_facet + outerFacetCount;
            indexes[count++] = k_facet;
            indexes[count++] = pos;
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

        int num_strips = innerFacetCount;

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
        int index_size = innerFacetCount * (outerFacetCount + 1) * 2;
        int num_strips = innerFacetCount;

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

        int count = 0;
        int half = innerFacetCount / 2;
        int i, k;
        int pos;
        int k_facet;

        // Wind the top half separately from the bottom
        for(k = 0; k < half; k++)
        {
            k_facet = k * innerFacetCount;
            stripCounts[k] = (outerFacetCount + 1) << 1;

            for(i = 0; i < outerFacetCount; i++)
            {
                pos = i + k_facet;

                // first triangle
                indexes[count++] = pos + outerFacetCount;
                indexes[count++] = pos;
            }

            // now the last remaing quad that uses coords 0 & 1 again
            indexes[count++] = k_facet + outerFacetCount;
            indexes[count++] = k_facet;
        }

        // Bottom half is wound in the opposite order.
        for(k = half; k < half * 2; k++)
        {
            k_facet = (k + 1) * innerFacetCount;
            stripCounts[k] = (outerFacetCount + 1) << 1;

            for(i = 0; i < outerFacetCount; i++)
            {
                pos = i + k_facet;

                // first triangle
                indexes[count++] = pos;
                indexes[count++] = pos + outerFacetCount;
            }

            // now the last remaing quad that uses coords 0 & 1 again
            indexes[count++] = k_facet;
            indexes[count++] = k_facet + outerFacetCount;
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
        int vtx_cnt = innerFacetCount * outerFacetCount * 6;

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
        int half = innerFacetCount / 2;
        int i, k;
        int facet_inc = outerFacetCount * 3;
        int last_facet = outerFacetCount - 1; // always stop one short of the end
        int k_facet;
        int pos;
        int count = 0;

        for(k = 0; k < half; k++)
        {
            k_facet = k * facet_inc;

            for(i = 0; i < last_facet; i++)
            {
                pos = i * 3 + k_facet;

                // triangle 1
                coords[count++] = shapeCoordinates[pos + facet_inc];
                coords[count++] = shapeCoordinates[pos + facet_inc + 1];
                coords[count++] = shapeCoordinates[pos + facet_inc + 2];

                coords[count++] = shapeCoordinates[pos];
                coords[count++] = shapeCoordinates[pos + 1];
                coords[count++] = shapeCoordinates[pos + 2];

                coords[count++] = shapeCoordinates[pos + 3];
                coords[count++] = shapeCoordinates[pos + 4];
                coords[count++] = shapeCoordinates[pos + 5];

                // triangle 2
                coords[count++] = shapeCoordinates[pos + 3];
                coords[count++] = shapeCoordinates[pos + 4];
                coords[count++] = shapeCoordinates[pos + 5];

                coords[count++] = shapeCoordinates[pos + facet_inc + 3];
                coords[count++] = shapeCoordinates[pos + facet_inc + 4];
                coords[count++] = shapeCoordinates[pos + facet_inc + 5];

                coords[count++] = shapeCoordinates[pos + facet_inc];
                coords[count++] = shapeCoordinates[pos + facet_inc + 1];
                coords[count++] = shapeCoordinates[pos + facet_inc + 2];
            }

            // now the last remaing shape that uses coords 0 & 1
            pos = i * 3 + k_facet;

            // triangle 1
            coords[count++] = shapeCoordinates[pos + facet_inc];
            coords[count++] = shapeCoordinates[pos + facet_inc + 1];
            coords[count++] = shapeCoordinates[pos + facet_inc + 2];

            coords[count++] = shapeCoordinates[pos];
            coords[count++] = shapeCoordinates[pos + 1];
            coords[count++] = shapeCoordinates[pos + 2];

            coords[count++] = shapeCoordinates[k_facet];
            coords[count++] = shapeCoordinates[k_facet + 1];
            coords[count++] = shapeCoordinates[k_facet + 2];

            // triangle 2
            coords[count++] = shapeCoordinates[k_facet];
            coords[count++] = shapeCoordinates[k_facet + 1];
            coords[count++] = shapeCoordinates[k_facet + 2];

            coords[count++] = shapeCoordinates[k_facet + facet_inc];
            coords[count++] = shapeCoordinates[k_facet + facet_inc + 1];
            coords[count++] = shapeCoordinates[k_facet + facet_inc + 2];

            coords[count++] = shapeCoordinates[pos + facet_inc];
            coords[count++] = shapeCoordinates[pos + facet_inc + 1];
            coords[count++] = shapeCoordinates[pos + facet_inc + 2];
        }

        int tempVertCount = count;
        for(k = 0; k < tempVertCount; k += 18)
        {
            // triangle 1
            coords[count++] =  coords[k + 6];
            coords[count++] = -coords[k + 7];
            coords[count++] =  coords[k + 8];
            coords[count++] =  coords[k + 3];
            coords[count++] = -coords[k + 4];
            coords[count++] =  coords[k + 5];
            coords[count++] =  coords[k + 0];
            coords[count++] = -coords[k + 1];
            coords[count++] =  coords[k + 2];

            // triangle 2
            coords[count++] =  coords[k + 15];
            coords[count++] = -coords[k + 16];
            coords[count++] =  coords[k + 17];
            coords[count++] =  coords[k + 12];
            coords[count++] = -coords[k + 13];
            coords[count++] =  coords[k + 14];
            coords[count++] =  coords[k +  9];
            coords[count++] = -coords[k + 10];
            coords[count++] =  coords[k + 11];
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
        int vtx_cnt = innerFacetCount * outerFacetCount * 4;

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
        int half = innerFacetCount / 2;
        int i, k;
        int facet_inc = outerFacetCount * 3;
        int last_facet = outerFacetCount - 1; // always stop one short of the end
        int k_facet;
        int pos;
        int count = 0;

        for(k = 0; k < half; k++)
        {
            k_facet = k * facet_inc;

            for(i = 0; i < last_facet; i++)
            {
                pos = i * 3 + k_facet;

                coords[count++] = shapeCoordinates[pos + facet_inc];
                coords[count++] = shapeCoordinates[pos + facet_inc + 1];
                coords[count++] = shapeCoordinates[pos + facet_inc + 2];

                coords[count++] = shapeCoordinates[pos];
                coords[count++] = shapeCoordinates[pos + 1];
                coords[count++] = shapeCoordinates[pos + 2];

                coords[count++] = shapeCoordinates[pos + 3];
                coords[count++] = shapeCoordinates[pos + 4];
                coords[count++] = shapeCoordinates[pos + 5];

                coords[count++] = shapeCoordinates[pos + facet_inc + 3];
                coords[count++] = shapeCoordinates[pos + facet_inc + 4];
                coords[count++] = shapeCoordinates[pos + facet_inc + 5];
            }

            // now the last remaing shape that uses coords 0 & 1
            pos = i * 3 + k_facet;

            coords[count++] = shapeCoordinates[pos + facet_inc];
            coords[count++] = shapeCoordinates[pos + facet_inc + 1];
            coords[count++] = shapeCoordinates[pos + facet_inc + 2];

            coords[count++] = shapeCoordinates[pos];
            coords[count++] = shapeCoordinates[pos + 1];
            coords[count++] = shapeCoordinates[pos + 2];

            coords[count++] = shapeCoordinates[k_facet];
            coords[count++] = shapeCoordinates[k_facet + 1];
            coords[count++] = shapeCoordinates[k_facet + 2];

            coords[count++] = shapeCoordinates[k_facet + facet_inc];
            coords[count++] = shapeCoordinates[k_facet + facet_inc + 1];
            coords[count++] = shapeCoordinates[k_facet + facet_inc + 2];
        }

        int tempVertCount = count;
        for(k = 0; k < tempVertCount; k += 12)
        {
            coords[count++] =  coords[k + 3];
            coords[count++] = -coords[k + 4];
            coords[count++] =  coords[k + 5];
            coords[count++] =  coords[k];
            coords[count++] = -coords[k + 1];
            coords[count++] =  coords[k + 2];
            coords[count++] =  coords[k + 9];
            coords[count++] = -coords[k + 10];
            coords[count++] =  coords[k + 11];
            coords[count++] =  coords[k + 6];
            coords[count++] = -coords[k + 7];
            coords[count++] =  coords[k + 8];
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
        int vtx_cnt = innerFacetCount * (outerFacetCount + 1) * 2;

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
        int half = innerFacetCount / 2;
        int i, k;
        int facet_inc = outerFacetCount * 3;
        int k_facet;
        int pos;
        int count = 0;

        for(k = 0; k < half; k++)
        {
            k_facet = k * facet_inc;

            for(i = 0; i < outerFacetCount; i++)
            {
                pos = i * 3 + k_facet;

                coords[count++] = shapeCoordinates[pos + facet_inc];
                coords[count++] = shapeCoordinates[pos + facet_inc + 1];
                coords[count++] = shapeCoordinates[pos + facet_inc + 2];

                coords[count++] = shapeCoordinates[pos];
                coords[count++] = shapeCoordinates[pos + 1];
                coords[count++] = shapeCoordinates[pos + 2];
            }

            // now the last remaing shape that uses coords 0 & 1
            coords[count++] = shapeCoordinates[k_facet + facet_inc];
            coords[count++] = shapeCoordinates[k_facet + facet_inc + 1];
            coords[count++] = shapeCoordinates[k_facet + facet_inc + 2];

            coords[count++] = shapeCoordinates[k_facet];
            coords[count++] = shapeCoordinates[k_facet + 1];
            coords[count++] = shapeCoordinates[k_facet + 2];
        }

        // bottom half is wound in the opposite direction so swap the
        // top and bottom coords declarations.
        int tempVertCount = count;
        for(k = 0; k < tempVertCount; k += 6)
        {
            coords[count++] =  coords[k + 3];
            coords[count++] = -coords[k + 4];
            coords[count++] =  coords[k + 5];
            coords[count++] =  coords[k];
            coords[count++] = -coords[k + 1];
            coords[count++] =  coords[k + 2];
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

        data.vertexCount = vtx_cnt;

        generateShape();

        // So the first part is easy - just copy the entire array for the
        // basic points over into this array. Automatically deals with half
        // v full sphere.
        System.arraycopy(shapeCoordinates,
                         0,
                         data.coordinates,
                         0,
                         numShapeValues);
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

        int half = innerFacetCount / 2;
        float[] normals = data.normals;
        Vector3f norm;
        int count = 0;
        int facet_offset = 0;
        int i, k;

        // Wonder if we can do any loop unravelling here?
        for(k = 0; k < half; k++)
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

        int tempVertCount = count;
        for(k = 0; k < tempVertCount; k += 18)
        {
            // triangle 1
            normals[count++] =  normals[k + 6];
            normals[count++] = -normals[k + 7];
            normals[count++] =  normals[k + 8];
            normals[count++] =  normals[k + 3];
            normals[count++] = -normals[k + 4];
            normals[count++] =  normals[k + 5];
            normals[count++] =  normals[k + 0];
            normals[count++] = -normals[k + 1];
            normals[count++] =  normals[k + 2];

            // triangle 2
            normals[count++] =  normals[k + 15];
            normals[count++] = -normals[k + 16];
            normals[count++] =  normals[k + 17];
            normals[count++] =  normals[k + 12];
            normals[count++] = -normals[k + 13];
            normals[count++] =  normals[k + 14];
            normals[count++] =  normals[k +  9];
            normals[count++] = -normals[k + 10];
            normals[count++] =  normals[k + 11];
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

        int half = innerFacetCount / 2;
        float[] normals = data.normals;
        Vector3f norm;
        int count = 0;
        int facet_offset = 0;
        int i, k;

        // Wonder if we can do any loop unravelling here?
        for(k = 0; k < half; k++)
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


        // Normals for the bottom half just copy the top half but have the
        // Y component negated - just like the coordinates.
        int tempVertCount = count;
        for(k = 0; k < tempVertCount; k += 12)
        {
            normals[count++] =  normals[k + 3];
            normals[count++] = -normals[k + 4];
            normals[count++] =  normals[k + 5];
            normals[count++] =  normals[k];
            normals[count++] = -normals[k + 1];
            normals[count++] =  normals[k + 2];
            normals[count++] =  normals[k + 9];
            normals[count++] = -normals[k + 10];
            normals[count++] =  normals[k + 11];
            normals[count++] =  normals[k + 6];
            normals[count++] = -normals[k + 7];
            normals[count++] =  normals[k + 8];
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

        int half = innerFacetCount / 2;
        float[] normals = data.normals;
        Vector3f norm;
        int count = 0;
        int facet_offset = 0;
        int i, k;

        // Wonder if we can do any loop unravelling here?
        for(k = 0; k < half; k++)
        {
            facet_offset = 0;

            for(i = outerFacetCount; --i >= 0; )
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

        // Normals for the bottom half just copy the top half but have the
        // Y component negated - just like the coordinates.
        int tempVertCount = count;
        for(k = 0; k < tempVertCount; k += 6)
        {
            normals[count++] =  normals[k + 3];
            normals[count++] = -normals[k + 4];
            normals[count++] =  normals[k + 5];
            normals[count++] =  normals[k];
            normals[count++] = -normals[k + 1];
            normals[count++] =  normals[k + 2];
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

        int half = innerFacetCount / 2;
        float[] normals = data.normals;
        Vector3f norm;
        int count = 0;
        int facet_count = 0;
        int i, k;

        // Wonder if we can do any loop unravelling here?
        for(k = 0; k <= half; k++)
        {
            facet_count = 0;

            for(i = outerFacetCount; --i >= 0; )
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

        // Normals for the bottom half just copy the top half but have the
        // Y component negated - just like the coordinates.
        int mid_pt = count;
        System.arraycopy(normals,
                         0,
                         normals,
                         mid_pt,
                         mid_pt);

        // loop and negate the y component. The -2 places this as the Y coord
        for(i = (data.vertexCount * 3) - 2; i > mid_pt; i -= 3)
            normals[i] = -normals[i];
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


    /**
     * Generates new set of points. If the dimensions have not changed since
     * the last call, the identical array will be returned. Note that you
     * should make a copy of this if you intend to call this method more than
     * once as it will replace the old values with the new ones and not
     * reallocate the array.
     *
     * @return An array of points representing the geometry vertices
     */
    private void generateShape()
    {
       if(!shapeChanged)
            return;

        shapeChanged = false;

        int vtx_total = (innerFacetCount * outerFacetCount +
                         outerFacetCount * 2) * 3;

        if((shapeCoordinates == null) ||
           (vtx_total > shapeCoordinates.length))
        {
            shapeCoordinates = new float[vtx_total];
            oradiusCoordinates = new float[outerFacetCount * 3];
        }

        numShapeValues = vtx_total;

        // Increment angles for the inner and outer radius facets.
        double arc_length = (2 * Math.PI * outerRadius) / outerFacetCount;
        float outer_theta = (float)(arc_length / outerRadius);

        arc_length = (2 * Math.PI * innerRadius) / innerFacetCount;
        float inner_theta = (float)(arc_length / innerRadius);

        // So that we only calculate the top half of the
        int half = innerFacetCount / 2;

        int count = 0;
        int i, k;
        float r;            // radius of current loop
        float y;
        float[] cos_theta_table;
        float[] sin_theta_table;

        // Generate the upper half. The loop goes around the outer radius
        // adding a quad at a time. When this is finished it moves up one facet
        // around the inner radius. It does this until it reaches the opposite
        // "inner" side of the torus.

        // Now set up the radius coordinate list and first loop as one thing.
        // this is for efficiency to minimise trig calculations
        r = outerRadius + innerRadius;
        cos_theta_table = new float[outerFacetCount];
        sin_theta_table = new float[outerFacetCount];

        for(i = 0 ; i < outerFacetCount; i++)
        {
            cos_theta_table[i] = (float)Math.cos(outer_theta * i);
            sin_theta_table[i] = (float)Math.sin(outer_theta * i);

            shapeCoordinates[count]     = r * cos_theta_table[i];
            shapeCoordinates[count + 1] = 0;
            shapeCoordinates[count + 2] = -r * sin_theta_table[i];

            oradiusCoordinates[count++] = outerRadius * cos_theta_table[i];
            oradiusCoordinates[count++] = 0;
            oradiusCoordinates[count++] = -outerRadius * sin_theta_table[i];
        }

        for(k = 1; k < half; k++)
        {
            r = outerRadius +
                (innerRadius * ((float) Math.cos(inner_theta * k)));

            // Calculate each face in the round. Calculates lower then upper on
            // one side and then moves to the next spot on the loop to calculate
            // the upper then lower values.

            // y values don't get recalculed for each quad on this strip
            y = innerRadius * (float)Math.sin(inner_theta * k);

            for(i = 0 ; i < outerFacetCount; i++)
            {
                shapeCoordinates[count++] = r * cos_theta_table[i];
                shapeCoordinates[count++] = y;
                shapeCoordinates[count++] = -r * sin_theta_table[i];
            }
        }

        // The last one on the inner radius with height = 0
        r = outerRadius - innerRadius;

        for(i = 0 ; i < outerFacetCount; i++)
        {
            shapeCoordinates[count++] = r * cos_theta_table[i];
            shapeCoordinates[count++] = 0;
            shapeCoordinates[count++] = -r * sin_theta_table[i];
        }

        // lower half just mirrors the upper half on y axis but we have to
        // negate the y component.
        int mid_pt = count;
        System.arraycopy(shapeCoordinates,
                         0,
                         shapeCoordinates,
                         mid_pt,
                         mid_pt);

        // loop and negate the y component
        for(i = shapeCoordinates.length - 2; i > mid_pt; i -= 3)
            shapeCoordinates[i] = -shapeCoordinates[i];
    }
}