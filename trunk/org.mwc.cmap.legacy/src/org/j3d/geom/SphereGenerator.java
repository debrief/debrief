/*****************************************************************************
 *                         J3D.org Copyright (c) 2000
 *                               Java Source
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
 * A customisable sphere where you can specify the radius, center and
 * the number of segments to use around the radius.
 * <p>
 *
 * The sphere is located around the origin. Each face of the sphere is
 * generated as a flat four-sided polygon suitable for use in an unindexed
 * quad array.
 * <p>
 *
 * Due to a focus on the generation speed, we require that the facet count
 * is always a multiple of 2. The code generates half a sphere and then mirrors
 * it to generate the other half. Having an odd number of facets makes this
 * impossbile to do and we must do a lot more generation. Although it is
 * simple enough to change the algorithm to allow odd numbers of facets, the
 * trade-off is that we loose the ability to nicely generate hemispheres. The
 * result is the same number of facets around the base as over the top half
 * part and that looks like crap as it does not look exactly like half a full
 * generated sphere of the same setup.
 * <p>
 * The sphere surface is always using smooth normals and every point is such
 * that the normal points directly away from the origin.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class SphereGenerator extends GeometryGenerator
{
    /** The default radius of the cone */
    private static final float DEFAULT_RADIUS = 1.0f;

    /** Default number of segments used in the cone */
    private static final int DEFAULT_FACETS = 16;

    /** The radius of the sphere */
    private float radius;

    /** The number of sections used around the cone */
    private int facetCount;

    /** Flag to indicate whether we should produce a full or half sphere */
    private boolean useHalf;

    /** The points on the quad of the sphere for each facet in [x, y, z] */
    private float[] quadCoordinates;

    /** The number of values used in the quad coordinate array */
    private int numQuadValues;

    /** Flag to indicate quad values changed and need recalculating */
    private boolean quadChanged;

    /** Flag to indicate the facet count or half settings have changed */
    private boolean facetsChanged;

    /**
     * The 2D texture coordinates for the sphere. These match the order of
     * vertex declaration in the quadCoordinates field thus making life
     * easy for dealing with half spheres
     */
    private float[] texCoordinates2D;

    /**
     * Constructs a default sphere of radius 1 and uses 16 segments on the
     * radius.
     */
    public SphereGenerator()
    {
        this(DEFAULT_RADIUS, DEFAULT_FACETS, false);
    }

    /**
     * Constructs a sphere of the given radius and uses 16 segments on the
     * radius.
     *
     * @param r The radius of the sphere
     */
    public SphereGenerator(float r)
    {
        this(r, DEFAULT_FACETS, false);
    }

    /**
     * Constructs a sphere or hemi-sphere of the given radius and uses 16
     * segments on the radius.
     *
     * @param r The radius of the sphere
     * @param half True to generate a hemi-sphere, false for full spher
     */
    public SphereGenerator(float r, boolean useHalf)
    {
        this(r, DEFAULT_FACETS, useHalf);
    }

    /**
     * Constructs a sphere of the given radius and controllable
     * number of segments on the radius. The minimum number of facets is 3.
     *
     * @param r The radius of the sphere
     * @param facets The number of facets to use
     * @throws IllegalArgumentException The number of facets is less than 3
     */
    public SphereGenerator(float r, int facets)
    {
        this(r, facets, false);
    }

    /**
     * Constructs a sphere or hemi-sphere of the given radius and controllable
     * number of segments on the radius.
     * The minimum number of facets is 3.
     *
     * @param r The radius of the sphere
     * @param facets The number of facets to use
     * @param half True to generate a hemi-sphere, false for full sphere
     * @throws IllegalArgumentException The number of facets is less than 4
     *    or not divisible by 2.
     */
    public SphereGenerator(float r, int facets, boolean half)
    {
        if(facets < 4)
            throw new IllegalArgumentException("Number of facets is < 4");

        if(facets % 2 != 0)
            throw new IllegalArgumentException("Number of facets not / 2");

        facetCount = facets;
        radius = r;
        useHalf = half;
        quadChanged = true;
        facetsChanged = true;
    }

    /**
     * Check to see that this sphere is a half sphere or not
     *
     * @return true if there is only a has sphere
     */
    public boolean isHalf()
    {
        return useHalf;
    }

    /**
     * Get the dimensions of the sphere. These are returned as 2 values of
     * height and radius respectively for the array. A new array is
     * created each time so you can do what you like with it.
     *
     * @return The current size of the sphere
     */
    public float getDimension()
    {
        return radius;
    }

    /**
     * Change the dimensions of the sphere to be generated. Calling this will
     * make the points be re-calculated next time you ask for geometry or
     * normals.
     *
     * @param r The radius of the sphere
     * @param facets The number of facets to use
     * @param half True to generate a hemi-sphere, false for full spher
     */
    public void setDimensions(float r, boolean half)
    {
        if((r != radius) || (useHalf != half))
            quadChanged = true;

        if(useHalf != half)
            facetsChanged = true;

        radius = r;
        useHalf = half;

    }

    /**
     * Change the number of facets used to create this cone. This will cause
     * the geometry to be regenerated next time they are asked for.
     * The minimum number of facets is 3.
     *
     * @param facets The number of facets on the side of the cone
     * @throws IllegalArgumentException The number of facets is less than 4
     *    or not divisible by 2.
     */
    public void setFacetCount(int facets)
    {
        if(facets < 4)
            throw new IllegalArgumentException("Number of facets is < 4");

        if(facets % 2 != 0)
            throw new IllegalArgumentException("Number of facets not / 2");

        if(facetCount != facets)
        {
            quadChanged = true;
            facetsChanged = true;
        }

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

        switch(data.geometryType)
        {
            case GeometryData.TRIANGLES:
                ret_val = facetCount * facetCount * 6;
                break;
            case GeometryData.QUADS:
                ret_val = facetCount * facetCount * 4;
                break;

            // These all have the same vertex count
            case GeometryData.TRIANGLE_STRIPS:
//            case GeometryData.TRIANGLE_FANS:
            case GeometryData.INDEXED_TRIANGLES:
            case GeometryData.INDEXED_QUADS:
            case GeometryData.INDEXED_TRIANGLE_STRIPS:
//            case GeometryData.INDEXED_TRIANGLE_FANS:
                ret_val = facetCount * facetCount;
                break;

            default:
                throw new UnsupportedTypeException("Unknown geometry type: " +
                                                   data.geometryType);
        }

        if(useHalf)
            ret_val /= 2;

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
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void unindexedTriangles(GeometryData data)
        throws InvalidArraySizeException
    {
        generateUnindexedTriCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateUnindexedTriTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);
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
            generateNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateUnindexedQuadTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);
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
            generateNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateIndexedTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);

        // now let's do the index list Double the vertex count as we end up
        // sharing every vertex between each face with .
        int index_size = data.vertexCount * 2;

        if(data.indexes == null)
            data.indexes = new int[index_size];
        else if(data.indexes.length < index_size)
            throw new InvalidArraySizeException("Coordinates",
                                                data.indexes.length,
                                                index_size);

        int[] indexes = data.indexes;
        data.indexesCount = index_size;
        int count = 0;

        int half = facetCount / 4;
        int i, k;
        int last_facet = facetCount - 1; // always stop one short of the end
        int pos;
        int k_facet;

        for(k = 0; k < half; k++)
        {
            k_facet = k * facetCount;

            for(i = 0; i < last_facet; i++)
            {
                pos = i + k_facet;
                indexes[count++] = pos + facetCount;
                indexes[count++] = pos;
                indexes[count++] = pos + 1;
                indexes[count++] = pos + facetCount + 1;
            }

            // now the last remaing quad that uses coords 0 & 1
            pos = i + k_facet;

            indexes[count++] = pos + facetCount;
            indexes[count++] = pos;
            indexes[count++] = k_facet;
            indexes[count++] = k_facet + facetCount;
        }

        if(!useHalf)
        {
            // Bottom half is wound in the opposite order.
            for(k = half + 1; k <= half * 2; k++)
            {
                k_facet = k * facetCount;

                for(i = 0; i < last_facet; i++)
                {
                    pos = i + k_facet;

                    indexes[count++] = pos;
                    indexes[count++] = pos + facetCount;
                    indexes[count++] = pos + facetCount + 1;
                    indexes[count++] = pos + 1;
                }

                // now the last remaing quad that uses coords 0 & 1
                pos = i + k_facet;

                indexes[count++] = pos;
                indexes[count++] = pos + facetCount;
                indexes[count++] = k_facet + facetCount;
                indexes[count++] = k_facet;
            }
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
            generateNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateIndexedTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);

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

        int half = facetCount / 4;
        int i, k;
        int last_facet = facetCount - 1; // always stop one short of the end
        int pos;
        int k_facet;

        // Wind the top half separately from the bottom
        for(k = 0; k < half; k++)
        {
            k_facet = k * facetCount;

            for(i = 0; i < last_facet; i++)
            {
                pos = i + k_facet;

                // first triangle
                indexes[count++] = pos + facetCount;
                indexes[count++] = pos;
                indexes[count++] = pos + 1;

                // second triangle
                indexes[count++] = pos + 1;
                indexes[count++] = pos + facetCount + 1;
                indexes[count++] = pos + facetCount;
            }

            // now the last remaing quad that uses coords 0 & 1
            pos = i + k_facet;

            indexes[count++] = pos + facetCount;
            indexes[count++] = pos;
            indexes[count++] = k_facet;


            indexes[count++] = k_facet;
            indexes[count++] = k_facet + facetCount;
            indexes[count++] = pos + facetCount;
        }

        if(!useHalf)
        {
            // Bottom half is wound in the opposite order.
            for(k = half + 1; k <= half * 2; k++)
            {
                k_facet = k * facetCount;

                for(i = 0; i < last_facet; i++)
                {
                    pos = i + k_facet;

                    indexes[count++] = pos;
                    indexes[count++] = pos + facetCount;
                    indexes[count++] = pos + facetCount + 1;

                    indexes[count++] = pos + facetCount + 1;
                    indexes[count++] = pos + 1;
                    indexes[count++] = pos;
                }

                // now the last remaing quad that uses coords 0 & 1
                pos = i + k_facet;

                indexes[count++] = pos;
                indexes[count++] = pos + facetCount;
                indexes[count++] = k_facet + facetCount;

                indexes[count++] = k_facet + facetCount;
                indexes[count++] = k_facet;
                indexes[count++] = pos;
            }
        }
    }

    /**
     * Generate a new set of points for a triangle strip array. Each side is a
     * strip of two faces.
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
            generateNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateUnindexedTriStripTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);

        int num_strips = facetCount;

        if(data.stripCounts == null)
            data.stripCounts = new int[num_strips];
        else if(data.stripCounts.length < num_strips)
            throw new InvalidArraySizeException("Strip counts",
                                                data.stripCounts.length,
                                                num_strips);

        int strip_length = (facetCount + 1) << 1;
        int[] stripCounts = data.stripCounts;

        for(int i = num_strips; --i >= 0; )
            stripCounts[i] = strip_length;
    }

    /**
     * Generate a new set of points for an indexed triangle strip array. We
     * build the strip from the existing points, and there's no need to
     * re-order the points for the indexes this time.
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
            generateNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateIndexedTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);

        // now let's do the index list
        int index_size = facetCount * (facetCount + 1);
        int num_strips = facetCount;

        if(useHalf)
        {
            index_size >>= 1;
            num_strips >>= 1;
        }

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
        int half = facetCount / 4;
        int i, k;
        int pos;
        int k_facet;

        // Wind the top half separately from the bottom
        for(k = 0; k < half; k++)
        {
            k_facet = k * facetCount;
            stripCounts[k] = (facetCount + 1) << 1;

            for(i = 0; i < facetCount; i++)
            {
                pos = i + k_facet;

                // first triangle
                indexes[count++] = pos + facetCount;
                indexes[count++] = pos;
            }

            // now the last remaing quad that uses coords 0 & 1 again
            indexes[count++] = k_facet + facetCount;
            indexes[count++] = k_facet;
        }

        if(!useHalf)
        {
            // Bottom half is wound in the opposite order.
            for(k = half; k < half * 2; k++)
            {
                k_facet = (k + 1) * facetCount;
                stripCounts[k] = (facetCount + 1) << 1;

                for(i = 0; i < facetCount; i++)
                {
                    pos = i + k_facet;

                    // first triangle
                    indexes[count++] = pos;
                    indexes[count++] = pos + facetCount;
                }

                // now the last remaing quad that uses coords 0 & 1 again
                indexes[count++] = k_facet;
                indexes[count++] = k_facet + facetCount;
            }
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
        int vtx_cnt = facetCount * facetCount * 6;

        if(useHalf)
            vtx_cnt /= 2;

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;

        recalculateQuadSphere();

        // quad torus generates coordinates at facetCount * 3 indexes apart.
        // Go around and build coordinate arrays from this.
        int half = facetCount / 4;
        int i, k;
        int facet_inc = facetCount * 3;
        int last_facet = facetCount - 1; // always stop one short of the end
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
                coords[count++] = quadCoordinates[pos + facet_inc];
                coords[count++] = quadCoordinates[pos + facet_inc + 1];
                coords[count++] = quadCoordinates[pos + facet_inc + 2];

                coords[count++] = quadCoordinates[pos];
                coords[count++] = quadCoordinates[pos + 1];
                coords[count++] = quadCoordinates[pos + 2];

                coords[count++] = quadCoordinates[pos + 3];
                coords[count++] = quadCoordinates[pos + 4];
                coords[count++] = quadCoordinates[pos + 5];

                // triangle 2
                coords[count++] = quadCoordinates[pos + 3];
                coords[count++] = quadCoordinates[pos + 4];
                coords[count++] = quadCoordinates[pos + 5];

                coords[count++] = quadCoordinates[pos + facet_inc + 3];
                coords[count++] = quadCoordinates[pos + facet_inc + 4];
                coords[count++] = quadCoordinates[pos + facet_inc + 5];

                coords[count++] = quadCoordinates[pos + facet_inc];
                coords[count++] = quadCoordinates[pos + facet_inc + 1];
                coords[count++] = quadCoordinates[pos + facet_inc + 2];
            }

            // now the last remaing shape that uses coords 0 & 1
            pos = i * 3 + k_facet;

            // triangle 1
            coords[count++] = quadCoordinates[pos + facet_inc];
            coords[count++] = quadCoordinates[pos + facet_inc + 1];
            coords[count++] = quadCoordinates[pos + facet_inc + 2];

            coords[count++] = quadCoordinates[pos];
            coords[count++] = quadCoordinates[pos + 1];
            coords[count++] = quadCoordinates[pos + 2];

            coords[count++] = quadCoordinates[k_facet];
            coords[count++] = quadCoordinates[k_facet + 1];
            coords[count++] = quadCoordinates[k_facet + 2];

            // triangle 2
            coords[count++] = quadCoordinates[k_facet];
            coords[count++] = quadCoordinates[k_facet + 1];
            coords[count++] = quadCoordinates[k_facet + 2];

            coords[count++] = quadCoordinates[k_facet + facet_inc];
            coords[count++] = quadCoordinates[k_facet + facet_inc + 1];
            coords[count++] = quadCoordinates[k_facet + facet_inc + 2];

            coords[count++] = quadCoordinates[pos + facet_inc];
            coords[count++] = quadCoordinates[pos + facet_inc + 1];
            coords[count++] = quadCoordinates[pos + facet_inc + 2];
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
     * Generates new set of points. If the dimensions have not changed since
     * the last call, the identical array will be returned. Note that you
     * should make a copy of this if you intend to call this method more than
     * once as it will replace the old values with the new ones and not
     * reallocate the array.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedQuadCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = getVertexCount(data);

        if(useHalf)
            vtx_cnt /= 2;

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;

        recalculateQuadSphere();

        // quad sphere generates coordinates at facetCount * 3 indexes apart.
        // Go around and build coordinate arrays from this.
        int half = facetCount / 4;
        int i, k;
        int facet_inc = facetCount * 3;
        int last_facet = facetCount - 1; // always stop one short of the end
        int k_facet;
        int pos;
        int count = 0;

        for(k = 0; k < half; k++)
        {
            k_facet = k * facet_inc;

            for(i = 0; i < last_facet; i++)
            {
                pos = i * 3 + k_facet;

                coords[count++] = quadCoordinates[pos + facet_inc];
                coords[count++] = quadCoordinates[pos + facet_inc + 1];
                coords[count++] = quadCoordinates[pos + facet_inc + 2];

                coords[count++] = quadCoordinates[pos];
                coords[count++] = quadCoordinates[pos + 1];
                coords[count++] = quadCoordinates[pos + 2];

                coords[count++] = quadCoordinates[pos + 3];
                coords[count++] = quadCoordinates[pos + 4];
                coords[count++] = quadCoordinates[pos + 5];

                coords[count++] = quadCoordinates[pos + facet_inc + 3];
                coords[count++] = quadCoordinates[pos + facet_inc + 4];
                coords[count++] = quadCoordinates[pos + facet_inc + 5];
            }

            // now the last remaing quad that uses coords 0 & 1
            pos = i * 3 + k_facet;

            coords[count++] = quadCoordinates[pos + facet_inc];
            coords[count++] = quadCoordinates[pos + facet_inc + 1];
            coords[count++] = quadCoordinates[pos + facet_inc + 2];

            coords[count++] = quadCoordinates[pos];
            coords[count++] = quadCoordinates[pos + 1];
            coords[count++] = quadCoordinates[pos + 2];

            coords[count++] = quadCoordinates[k_facet + 0];
            coords[count++] = quadCoordinates[k_facet + 1];
            coords[count++] = quadCoordinates[k_facet + 2];

            coords[count++] = quadCoordinates[k_facet + facet_inc + 0];
            coords[count++] = quadCoordinates[k_facet + facet_inc + 1];
            coords[count++] = quadCoordinates[k_facet + facet_inc + 2];
        }

        if(!useHalf)
        {
            // lower half just mirrors the upper half on y axis but we have to
            // change the winding so that they all remain with the same clockwise
            // ordering of vertices
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
        int vtx_cnt = facetCount * (facetCount + 1) * 2;

        if(useHalf)
            vtx_cnt /= 2;

        if(data.coordinates == null)
            data.coordinates = new float[vtx_cnt * 3];
        else if(data.coordinates.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                vtx_cnt * 3);

        float[] coords = data.coordinates;
        data.vertexCount = vtx_cnt;

        recalculateQuadSphere();

        // quad torus generates coordinates at facetCount * 3 indexes apart.
        // Go around and build coordinate arrays from this.
        int half = facetCount / 2;
        int i, k;
        int facet_inc = facetCount * 3;
        int k_facet;
        int pos;
        int count = 0;

        for(k = 0; k < half; k++)
        {
            k_facet = k * facet_inc;

            for(i = 0; i < facetCount; i++)
            {
                pos = i * 3 + k_facet;

                coords[count++] = quadCoordinates[pos + facet_inc];
                coords[count++] = quadCoordinates[pos + facet_inc + 1];
                coords[count++] = quadCoordinates[pos + facet_inc + 2];

                coords[count++] = quadCoordinates[pos];
                coords[count++] = quadCoordinates[pos + 1];
                coords[count++] = quadCoordinates[pos + 2];
            }

            // now the last remaing shape that uses coords 0 & 1
            coords[count++] = quadCoordinates[k_facet + facet_inc];
            coords[count++] = quadCoordinates[k_facet + facet_inc + 1];
            coords[count++] = quadCoordinates[k_facet + facet_inc + 2];

            coords[count++] = quadCoordinates[k_facet];
            coords[count++] = quadCoordinates[k_facet + 1];
            coords[count++] = quadCoordinates[k_facet + 2];
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
     * Generates new set of points suitable for use in an indexed array.
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

        recalculateQuadSphere();

        // So the first part is easy - just copy the entire array for the
        // basic points over into this array. Automatically deals with half
        // v full sphere.
        System.arraycopy(quadCoordinates,
                         0,
                         data.coordinates,
                         0,
                         numQuadValues);
    }

    //------------------------------------------------------------------------
    // Normal generation routines
    //------------------------------------------------------------------------

    /**
     * Generate the normals for this sphere. Just reads the coordinate array
     * and generates radial normals equivalent. Is completely geometry
     * independent as all spheres are spherical :). Assumes that coordinates
     * have been generated first.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateNormals(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = getVertexCount(data);

        if(data.normals == null)
            data.normals = new float[vtx_cnt * 3];
        else if(data.normals.length < vtx_cnt * 3)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                vtx_cnt * 3);

        float[] normals = data.normals;
        Vector3f norm;
        int count = 0;

        // Wonder if we can do any loop unravelling here?
        for(int i = vtx_cnt; --i >= 0; )
        {
            norm = createRadialNormal(data.coordinates, count);

            normals[count++] = norm.x;
            normals[count++] = norm.y;
            normals[count++] = norm.z;
        }
    }

    //------------------------------------------------------------------------
    // Texture coordinate generation routines
    //------------------------------------------------------------------------

    /**
     * Generate a new set of texCoords for a normal set of unindexed triangle
     * points.
     * <p>
     * This must always be called after the coordinate generation.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriTexture2D(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 2;

        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[vtx_cnt];
        else if(data.textureCoordinates.length < vtx_cnt)
            throw new InvalidArraySizeException("2D Texture coordinates",
                                                data.textureCoordinates.length,
                                                vtx_cnt);

        recalc2DTexture();
    }

    /**
     * Generate a new set of texCoords for a normal set of unindexed quad
     * points.
     * <p>
     * This must always be called after the coordinate generation.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedQuadTexture2D(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 2;

        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[vtx_cnt];
        else if(data.textureCoordinates.length < vtx_cnt)
            throw new InvalidArraySizeException("2D Texture coordinates",
                                                data.textureCoordinates.length,
                                                vtx_cnt);

        float[] tex_coords = data.textureCoordinates;

        recalc2DTexture();

        // quad sphere generates coordinates at facetCount * 3 indexes apart.
        // Go around and build coordinate arrays from this.
        int half = facetCount / 4;
        int i, k;
        int facet_inc = facetCount * 2;
        int last_facet = facetCount - 1; // always stop one short of the end
        int k_facet;
        int pos;
        int count = 0;

        for(k = 0; k < half; k++)
        {
            k_facet = k * facet_inc;

            for(i = 0; i < last_facet; i++)
            {
                pos = i * 2 + k_facet;

                tex_coords[count++] = texCoordinates2D[pos + facet_inc];
                tex_coords[count++] = texCoordinates2D[pos + facet_inc + 1];

                tex_coords[count++] = texCoordinates2D[pos];
                tex_coords[count++] = texCoordinates2D[pos + 1];

                tex_coords[count++] = texCoordinates2D[pos + 2];
                tex_coords[count++] = texCoordinates2D[pos + 3];

                tex_coords[count++] = texCoordinates2D[pos + facet_inc + 2];
                tex_coords[count++] = texCoordinates2D[pos + facet_inc + 3];
            }

            // now the last remaing quad that uses tex_coords 0 & 1
            pos = i * 2 + k_facet;

            tex_coords[count++] = texCoordinates2D[pos + facet_inc];
            tex_coords[count++] = texCoordinates2D[pos + facet_inc + 1];

            tex_coords[count++] = texCoordinates2D[pos];
            tex_coords[count++] = texCoordinates2D[pos + 1];

            tex_coords[count++] = texCoordinates2D[k_facet + 0];
            tex_coords[count++] = texCoordinates2D[k_facet + 1];

            tex_coords[count++] = texCoordinates2D[k_facet + facet_inc + 0];
            tex_coords[count++] = texCoordinates2D[k_facet + facet_inc + 1];
        }

        if(useHalf)
            return;

        for(k = half + 1; k < half * 2; k++)
        {
            k_facet = k * facet_inc;

            for(i = 0; i < last_facet; i++)
            {
                pos = i * 2 + k_facet;

                tex_coords[count++] = texCoordinates2D[pos + 2];
                tex_coords[count++] = texCoordinates2D[pos + 3];

                tex_coords[count++] = texCoordinates2D[pos + facet_inc];
                tex_coords[count++] = texCoordinates2D[pos + facet_inc + 1];

                tex_coords[count++] = texCoordinates2D[pos + facet_inc + 2];
                tex_coords[count++] = texCoordinates2D[pos + facet_inc + 3];

                tex_coords[count++] = texCoordinates2D[pos];
                tex_coords[count++] = texCoordinates2D[pos + 1];
            }

            // now the last remaing quad that uses tex_coords 0 & 1
            pos = i * 2 + k_facet;

            tex_coords[count++] = texCoordinates2D[k_facet + 0];
            tex_coords[count++] = texCoordinates2D[k_facet + 1];

            tex_coords[count++] = texCoordinates2D[pos + facet_inc];
            tex_coords[count++] = texCoordinates2D[pos + facet_inc + 1];

            tex_coords[count++] = texCoordinates2D[k_facet + facet_inc + 0];
            tex_coords[count++] = texCoordinates2D[k_facet + facet_inc + 1];

            tex_coords[count++] = texCoordinates2D[pos];
            tex_coords[count++] = texCoordinates2D[pos + 1];
        }
    }

    /**
     * Generate a new set of texCoords for a normal set of unindexed triangle
     * strip points.
     * <p>
     * This must always be called after the coordinate generation.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriStripTexture2D(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 2;

        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[vtx_cnt];
        else if(data.textureCoordinates.length < vtx_cnt)
            throw new InvalidArraySizeException("2D Texture coordinates",
                                                data.textureCoordinates.length,
                                                vtx_cnt);

        recalc2DTexture();
    }

    /**
     * Generates new set of points suitable for use in an indexed array.
     * This array is your basic shape, but with the bottom part mirrored if
     * need be.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateIndexedTexture2D(GeometryData data)
        throws InvalidArraySizeException
    {
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
    private void generateTexture3D(GeometryData data)
        throws InvalidArraySizeException
    {
        int vtx_cnt = data.vertexCount * 3;

        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[vtx_cnt];
        else if(data.textureCoordinates.length < vtx_cnt)
            throw new InvalidArraySizeException("3D Texture coordinates",
                                                data.textureCoordinates.length,
                                                vtx_cnt);
    }

    /**
     * Convenience method to generate the points of the sphere for quad values.
     * The points start at the centerline and then work their way up to the
     * height of the sphere. ie It only calculates a hemi-sphere worth of
     * points.
     */
    private void recalculateQuadSphere()
    {
        if(!quadChanged)
            return;

        quadChanged = false;

        int vtx_count = facetCount * facetCount;

        if(useHalf)
            vtx_count /= 2;

        if((quadCoordinates == null) ||
           (vtx_count * 3 > quadCoordinates.length))
        {
            quadCoordinates = new float[vtx_count * 3];
        }

        numQuadValues = vtx_count * 3;

        int half = facetCount / 4;

        // local constant to make math calcs faster
        double segment_angle = 2.0 * Math.PI / facetCount;
        int count = 0;
        double y_radius = radius;
        int i, k;
        float y = 0;
        float[] cos_table;
        float[] sin_table;

        cos_table = new float[facetCount];
        sin_table = new float[facetCount];

        for(i = 0; i < facetCount; i++)
        {
            cos_table[i] = (float)Math.cos(segment_angle * i + Math.PI / 2);
            sin_table[i] = (float)Math.sin(segment_angle * i + Math.PI / 2);

            quadCoordinates[count++] = (float)(y_radius * sin_table[i]);
            quadCoordinates[count++] = y;
            quadCoordinates[count++] = (float)(y_radius * cos_table[i]);
        }

        // loop from the centerline to the top
        for(k = 1; k < half; k++)
        {
            y_radius = radius * Math.cos(segment_angle * k);
            y = (float)(radius * Math.sin(segment_angle * k));

            for(i = 0; i < facetCount; i++)
            {
                quadCoordinates[count++] = (float)(y_radius * sin_table[i]);
                quadCoordinates[count++] = y;
                quadCoordinates[count++] = (float)(y_radius * cos_table[i]);
            }
        }

        // Top coords are easy :)
        for(i = facetCount; --i >= 0; )
        {
            quadCoordinates[count++] = 0;
            quadCoordinates[count++] = radius;
            quadCoordinates[count++] = 0;
        }

        if(!useHalf)
        {
            int mid_pt = count;
            System.arraycopy(quadCoordinates,
                             0,
                             quadCoordinates,
                             mid_pt,
                             mid_pt);

            // loop and negate the y component
            for(i = quadCoordinates.length - 2; i > mid_pt; i -= 3)
                quadCoordinates[i] = -quadCoordinates[i];
        }
    }

    /**
     * Recalculate the 2D texture coordinates IAW the coordinate values. This
     * starts by using the circumference as a T value of 0.5 to indicate it is
     * halfway through the texture (we are starting at the middle of the
     * sphere!). Then, if we have a bottom, we calculate the T from 0 to 0.5
     */
    private void recalc2DTexture()
    {
        if(!facetsChanged)
            return;

        // not a good idea because we should also leave this set to recalc
        // the 3D coordinates.
        facetsChanged = false;

        int vtx_count = facetCount * facetCount;

        if(useHalf)
            vtx_count /= 2;

        if((texCoordinates2D == null) ||
           (vtx_count * 2 > texCoordinates2D.length))
        {
            texCoordinates2D = new float[vtx_count * 2];
        }

        numQuadValues = vtx_count * 2;

        int half = facetCount / 4;

        // local constant to make math calcs faster
        double segment_angle = 1 / facetCount;
        int count = 0;
        int i, k;
        float t;
        float[] s_table = new float[facetCount];
        float[] t_table = new float[facetCount];


        for(i = 0; i < facetCount; i++)
        {
            s_table[i] = (float)(i * segment_angle);
            t_table[i] = (float)(i * segment_angle);

            texCoordinates2D[count++] = s_table[i];
            texCoordinates2D[count++] = 0.5f;
        }

        // loop from the centerline to the top
        for(k = 1; k < half; k++)
        {
            t = t_table[k + facetCount / 2];

            // Reverse loop count because it is *much* faster than the forward
            // version.
            for(i = 0; i < facetCount; i++)
            {
                texCoordinates2D[count++] = s_table[i];
                texCoordinates2D[count++] = t;
            }
        }

        // Top coords are easy :)
        for(i = facetCount; --i >= 0; )
        {
            texCoordinates2D[count++] = s_table[i];
            texCoordinates2D[count++] = 1;
        }

        if(!useHalf)
        {
            for(k = 0; k < half; k++)
            {
                t = t_table[k];

                // Reverse loop count because it is *much* faster than the forward
                // version.
                for(i = 0; i < facetCount; i++)
                {
                    texCoordinates2D[count++] = s_table[i];
                    texCoordinates2D[count++] = t;
                }
            }
        }
    }
}