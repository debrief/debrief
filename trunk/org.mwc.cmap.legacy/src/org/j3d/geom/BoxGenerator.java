/*****************************************************************************
 *                           J3D.org Copyright (c) 2000
 *                                Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom;

// Standard imports
// none

// Application specific imports
// none

/**
 * Generator of box coordinates, normals and texture coordinates.
 * <p>
 *
 * The width of the box is the X axis. The height of the box is along the Y
 * axis and the depth of the box is along the Z axis. The box is always
 * centered about the origin such that the effective coordinates are 1/2 the
 * values provided here.
 * <p>
 *
 * For the strip and fan arrays, we always use one fan/strip per face. While it
 * is possible to reduce it to one big strip array and then two small ones for
 * top and bottom. the effect is lighting normals that point out at angles from
 * each corner, which is not realistic. So for this generator, there is no real
 * difference between using indexed and unindexed forms as they use the same
 * number of coordinates.
 * <p>
 *
 * 2D texture coordinates follow the classic OpenGL style. 0,0 is in the bottom
 * left corner of each face and 1, 1 is on the top right. For the top, the
 * texture appears the right way up looking down the -Y axis, with the -Z axis
 * as up. For the bottom face, looking along the Y axis, with the +Z axis as up
 * it is the right way up.
 * <p>
 *
 * 3D Texture coordinates look just like the cube. 0, 0, 0 is at the lower left
 * corner of the front face, while 1, 1, 1 is at the top right, rear corner.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class BoxGenerator extends GeometryGenerator
{
    /**
     * The number of vertices to use for all types except
     * unindexed triangles
     */
    private static final int BASIC_VERTEX_COUNT = 24;

    /** Number of verticies when generating unindexed triangles */
    private static final int TRIANGLE_VERTEX_COUNT = 36;

    /** The width of the box to create */
    private float boxWidth;

    /** The height of the box to create */
    private float boxHeight;

    /** The depth of the box to create */
    private float boxDepth;

    /**
     * Construct a box with the default size of 2, 2, 2.
     */
    public BoxGenerator()
    {
        this(2, 2, 2);
    }

    /**
     * Construct a box with the given dimensions.
     *
     * @param width The width of the box in units
     * @param height The height of the box in units
     * @param depth The depth of the box in units
     */
    public BoxGenerator(float width, float height, float depth)
    {
        boxWidth = width;
        boxHeight = height;
        boxDepth = depth;
    }

    /**
     * Get the dimensions of the box. These are returned as 3 value of
     * width, height and depth respectively for the array. A new array is
     * created each time so you can do what you like with it.
     *
     * @return The current size of the box
     */
    public float[] getDimensions()
    {
        return new float[] { boxWidth, boxHeight, boxDepth };
    }

    /**
     * Set the dimensions of the output box to the new values. Calling this
     * will make the points be re-calculated again.
     *
     * @param width The width of the box in units
     * @param height The height of the box in units
     * @param depth The depth of the box in units
     */
    public void setDimensions(float width, float height, float depth)
    {
        boxWidth = width;
        boxHeight = height;
        boxDepth = depth;
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
                ret_val = TRIANGLE_VERTEX_COUNT;
                break;
            case GeometryData.QUADS:
                ret_val = BASIC_VERTEX_COUNT;
                break;
            case GeometryData.TRIANGLE_STRIPS:
                ret_val = BASIC_VERTEX_COUNT;
                break;
            case GeometryData.TRIANGLE_FANS:
                ret_val = BASIC_VERTEX_COUNT;
                break;
            case GeometryData.INDEXED_QUADS:
                ret_val = BASIC_VERTEX_COUNT;
                break;
            case GeometryData.INDEXED_TRIANGLES:
                ret_val = BASIC_VERTEX_COUNT;
                break;
            case GeometryData.INDEXED_TRIANGLE_STRIPS:
                ret_val = BASIC_VERTEX_COUNT;
                break;
            case GeometryData.INDEXED_TRIANGLE_FANS:
                ret_val = BASIC_VERTEX_COUNT;
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
        generateUnindexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateUnindexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);

    }

    /**
     * Generate a new set of points for an indexed quad array
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void indexedQuads(GeometryData data)
        throws InvalidArraySizeException
    {
        generateUnindexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateUnindexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);

        // now let's do the index list
        int index_size = BASIC_VERTEX_COUNT;

        if(data.indexes == null)
            data.indexes = new int[index_size];
        else if(data.indexes.length < index_size)
            throw new InvalidArraySizeException("Coordinates",
                                                data.indexes.length,
                                                index_size);

        int[] indexes = data.indexes;
        data.indexesCount = index_size;

        for(int i = 6; --i >= 0;)
        {
            indexes[i * 4] = i * 4;
            indexes[i * 4 + 1] = i * 4 + 1;
            indexes[i * 4 + 2] = i * 4 + 2;
            indexes[i * 4 + 3] = i * 4 + 3;
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
        generateUnindexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateUnindexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);

        // now let's do the index list
        int index_size = BASIC_VERTEX_COUNT + (BASIC_VERTEX_COUNT >> 1);

        if(data.indexes == null)
            data.indexes = new int[index_size];
        else if(data.indexes.length < index_size)
            throw new InvalidArraySizeException("Coordinates",
                                                data.indexes.length,
                                                index_size);

        int[] indexes = data.indexes;
        data.indexesCount = index_size;

        // each face consists of two triangles, both declared anti-clockwise
        for(int i = 6; --i >= 0;)
        {
            indexes[i * 6] = i * 4;
            indexes[i * 6 + 1] = i * 4 + 1;
            indexes[i * 6 + 2] = i * 4 + 2;
            indexes[i * 6 + 3] = i * 4 + 2;
            indexes[i * 6 + 4] = i * 4 + 3;
            indexes[i * 6 + 5] = i * 4;
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
        generateUnindexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateUnindexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);

        // now let's do the strip list - one strip for each side
        int index_size = 6;

        if(data.stripCounts == null)
            data.stripCounts = new int[index_size];
        else if(data.stripCounts.length < index_size)
            throw new InvalidArraySizeException("Coordinates",
                                                data.stripCounts.length,
                                                index_size);

        int[] stripCounts = data.stripCounts;
        float[] coords = data.coordinates;
        data.numStrips = index_size;

        // each face consists of two triangles, both declared anti-clockwise.
        // Unfortunately the default arrangements don't generate the right
        // triangle strip so we just shuffle them about. The fourth vertex does
        // not need to be touched.
        float tmp_x, tmp_y, tmp_z;

        for(int i = 6; --i >= 0; )
        {
            data.stripCounts[i] = 4;

            tmp_x = coords[i * 12];
            tmp_y = coords[i * 12 + 1];
            tmp_z = coords[i * 12 + 2];

            coords[i * 12]     = coords[i * 12 + 3];
            coords[i * 12 + 1] = coords[i * 12 + 4];
            coords[i * 12 + 2] = coords[i * 12 + 5];

            coords[i * 12 + 3] = coords[i * 12 + 6];
            coords[i * 12 + 4] = coords[i * 12 + 7];
            coords[i * 12 + 5] = coords[i * 12 + 8];

            coords[i * 12 + 6] = tmp_x;
            coords[i * 12 + 7] = tmp_y;
            coords[i * 12 + 8] = tmp_z;
        }
    }

    /**
     * Generate a new set of points for a triangle fan array. Each side is a
     * strip of two faces with the point of the fan being the lower-right
     * corner, so it fits nicely with our boxes that we are already making.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void triangleFans(GeometryData data)
        throws InvalidArraySizeException
    {
        generateUnindexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateUnindexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);

        // now let's do the strip list - one strip for each side
        int index_size = 6;

        if(data.stripCounts == null)
            data.stripCounts = new int[index_size];
        else if(data.stripCounts.length < index_size)
            throw new InvalidArraySizeException("Coordinates",
                                                data.stripCounts.length,
                                                index_size);

        int[] stripCounts = data.stripCounts;
        data.numStrips = index_size;

        // each face consists of two triangles, both declared anti-clockwise.
        // Unfortunately the default arrangements don't generate the right
        // triangle strip so we just shuffle them about. The fourth vertex does
        // not need to be touched.
        float tmp_x, tmp_y, tmp_z;

        for(int i = 6; --i >= 0; )
            stripCounts[i] = 4;
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
        generateUnindexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateUnindexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);

        // now let's do the index list
        int index_size = BASIC_VERTEX_COUNT;

        if(data.indexes == null)
            data.indexes = new int[index_size];
        else if(data.indexes.length < index_size)
            throw new InvalidArraySizeException("Indexes",
                                                data.indexes.length,
                                                index_size);

        if(data.stripCounts == null)
            data.stripCounts = new int[6];
        else if(data.stripCounts.length < 6)
            throw new InvalidArraySizeException("Strip counts",
                                                data.stripCounts.length,
                                                6);

        int[] stripCounts = data.stripCounts;
        int[] indexes = data.indexes;
        data.indexesCount = index_size;
        data.numStrips = 6;

        for(int i = 6; --i >= 0;)
        {
            stripCounts[i] = 4;
            indexes[i * 4] = i * 4 + 1;
            indexes[i * 4 + 1] = i * 4 + 2;
            indexes[i * 4 + 2] = i * 4;
            indexes[i * 4 + 3] = i * 4 + 3;
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
        generateUnindexedCoordinates(data);

        if((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
            generateUnindexedNormals(data);

        if((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
            generateTexture2D(data);
        else if((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
            generateTexture3D(data);

        // now let's do the index list
        int index_size = BASIC_VERTEX_COUNT;

        if(data.indexes == null)
            data.indexes = new int[index_size];
        else if(data.indexes.length < index_size)
            throw new InvalidArraySizeException("Indexes",
                                                data.indexes.length,
                                                index_size);

        if(data.stripCounts == null)
            data.stripCounts = new int[6];
        else if(data.stripCounts.length < 6)
            throw new InvalidArraySizeException("Strip counts",
                                                data.stripCounts.length,
                                                6);

        int[] stripCounts = data.stripCounts;
        int[] indexes = data.indexes;
        data.indexesCount = index_size;
        data.numStrips = 6;

        for(int i = 6; --i >= 0;)
        {
            stripCounts[i] = 4;
            indexes[i * 4] = i * 4;
            indexes[i * 4 + 1] = i * 4 + 1;
            indexes[i * 4 + 2] = i * 4 + 2;
            indexes[i * 4 + 3] = i * 4 + 3;
        }
    }

    // Quad generated points

    /**
     * Generate a new set of raw points for an array. This puts a point at
     * each corner in anti-clockwise ordering in the order - front, left, back,
     * right, top, bottom. Faces vertices are in the order: bottom-right,
     * top-right, top-left, bottom-left when looking directly at the outside of
     * the face. The only exception is the bottom face that starts bottom-left
     * and then continues in a clockwise order. This is so that someone
     * creating a line strip array from this will get a nice looking output.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        if(data.coordinates == null)
            data.coordinates = new float[BASIC_VERTEX_COUNT * 3];
        else if(data.coordinates.length < BASIC_VERTEX_COUNT * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                BASIC_VERTEX_COUNT * 3);

        float[] coords = data.coordinates;
        data.vertexCount = BASIC_VERTEX_COUNT;

        float sx = boxWidth / 2;
        float sy = boxHeight  / 2;
        float sz = boxDepth / 2;

        // front face
        coords[0] =  sx;  coords[1] =  -sy; coords[2] =   sz;
        coords[3] =  sx;  coords[4] =   sy; coords[5] =   sz;
        coords[6] = -sx;  coords[7] =   sy; coords[8] =   sz;
        coords[9] = -sx;  coords[10] = -sy; coords[11] =  sz;

        // left face
        coords[12] = -sx; coords[13] = -sy; coords[14] =  sz;
        coords[15] = -sx; coords[16] =  sy; coords[17] =  sz;
        coords[18] = -sx; coords[19] =  sy; coords[20] = -sz;
        coords[21] = -sx; coords[22] = -sy; coords[23] = -sz;

        // back face
        coords[24] = -sx; coords[25] = -sy; coords[26] = -sz;
        coords[27] = -sx; coords[28] =  sy; coords[29] = -sz;
        coords[30] =  sx; coords[31] =  sy; coords[32] = -sz;
        coords[33] =  sx; coords[34] = -sy; coords[35] = -sz;

        // right face
        coords[36] =  sx; coords[37] = -sy; coords[38] = -sz;
        coords[39] =  sx; coords[40] =  sy; coords[41] = -sz;
        coords[42] =  sx; coords[43] =  sy; coords[44] =  sz;
        coords[45] =  sx; coords[46] = -sy; coords[47] =  sz;

        // top face
        coords[48] =  sx; coords[49] =  sy; coords[50] =  sz;
        coords[51] =  sx; coords[52] =  sy; coords[53] = -sz;
        coords[54] = -sx; coords[55] =  sy; coords[56] = -sz;
        coords[57] = -sx; coords[58] =  sy; coords[59] =  sz;

        // bottom face
        coords[60] = -sx; coords[61] = -sy; coords[62] = -sz;
        coords[63] =  sx; coords[64] = -sy; coords[65] = -sz;
        coords[66] =  sx; coords[67] = -sy; coords[68] =  sz;
        coords[69] = -sx; coords[70] = -sy; coords[71] =  sz;
    }

    /**
     * Generate a new set of normals for a normal set of unindexed points. Each
     * normal faces directly perpendicular for each point. This makes each face
     * seem flat.
     * <p>
     * This must always be called after the coordinate generation.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedNormals(GeometryData data)
        throws InvalidArraySizeException
    {

        if(data.normals == null)
            data.normals = new float[BASIC_VERTEX_COUNT * 3];
        else if(data.normals.length < BASIC_VERTEX_COUNT * 3)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                BASIC_VERTEX_COUNT * 3);

        float[] normals = data.normals;

        // front face
        normals[0] = 0;  normals[1] = 0;  normals[2] = 1;
        normals[3] = 0;  normals[4] = 0;  normals[5] = 1;
        normals[6] = 0;  normals[7] = 0;  normals[8] = 1;
        normals[9] = 0;  normals[10] = 0; normals[11] = 1;

        // left face
        normals[12] =-1; normals[13] = 0; normals[14] = 0;
        normals[15] =-1; normals[16] = 0; normals[17] = 0;
        normals[18] =-1; normals[19] = 0; normals[20] = 0;
        normals[21] =-1; normals[22] = 0; normals[23] = 0;

        // back face
        normals[24] = 0; normals[25] = 0; normals[26] = -1;
        normals[27] = 0; normals[28] = 0; normals[29] = -1;
        normals[30] = 0; normals[31] = 0; normals[32] = -1;
        normals[33] = 0; normals[34] = 0; normals[35] = -1;

        // right face
        normals[36] = 1; normals[37] = 0; normals[38] = 0;
        normals[39] = 1; normals[40] = 0; normals[41] = 0;
        normals[42] = 1; normals[43] = 0; normals[44] = 0;
        normals[45] = 1; normals[46] = 0; normals[47] = 0;

        // top face
        normals[48] = 0; normals[49] = 1; normals[50] = 0;
        normals[51] = 0; normals[52] = 1; normals[53] = 0;
        normals[54] = 0; normals[55] = 1; normals[56] = 0;
        normals[57] = 0; normals[58] = 1; normals[59] = 0;

        // bottom face
        normals[60] = 0; normals[61] =-1; normals[62] = 0;
        normals[63] = 0; normals[64] =-1; normals[65] = 0;
        normals[66] = 0; normals[67] =-1; normals[68] = 0;
        normals[69] = 0; normals[70] =-1; normals[71] = 0;
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
    private void generateTexture2D(GeometryData data)
        throws InvalidArraySizeException
    {
        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[BASIC_VERTEX_COUNT * 2];
        else if(data.textureCoordinates.length < BASIC_VERTEX_COUNT * 2)
            throw new InvalidArraySizeException("2D Texture coordinates",
                                                data.textureCoordinates.length,
                                                BASIC_VERTEX_COUNT * 2);

        float[] texCoords = data.textureCoordinates;

        // front face
        texCoords[0] = 1;  texCoords[1] = 0;
        texCoords[2] = 1;  texCoords[3] = 1;
        texCoords[4] = 0;  texCoords[5] = 1;
        texCoords[6] = 0;  texCoords[7] = 0;

        // left face
        texCoords[8]  = 1;  texCoords[9]  = 0;
        texCoords[10] = 1;  texCoords[11] = 1;
        texCoords[12] = 0;  texCoords[13] = 1;
        texCoords[14] = 0;  texCoords[15] = 0;

        // back face
        texCoords[16] = 1;  texCoords[17] = 0;
        texCoords[18] = 1;  texCoords[19] = 1;
        texCoords[20] = 0;  texCoords[21] = 1;
        texCoords[22] = 0;  texCoords[23] = 0;

        // right face
        texCoords[24] = 1;  texCoords[25] = 0;
        texCoords[26] = 1;  texCoords[27] = 1;
        texCoords[28] = 0;  texCoords[29] = 1;
        texCoords[30] = 0;  texCoords[31] = 0;

        // top face
        texCoords[32] = 1;  texCoords[33] = 0;
        texCoords[34] = 1;  texCoords[35] = 1;
        texCoords[36] = 0;  texCoords[37] = 1;
        texCoords[38] = 0;  texCoords[39] = 0;

        // bottom face
        texCoords[40] = 0;  texCoords[41] = 0;
        texCoords[42] = 1;  texCoords[43] = 0;
        texCoords[44] = 1;  texCoords[45] = 1;
        texCoords[46] = 0;  texCoords[47] = 1;
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

        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[BASIC_VERTEX_COUNT * 3];
        else if(data.textureCoordinates.length < BASIC_VERTEX_COUNT * 3)
            throw new InvalidArraySizeException("3D Texture coordinates",
                                                data.textureCoordinates.length,
                                                BASIC_VERTEX_COUNT * 3);

        float[] texCoords = data.textureCoordinates;

        // front face
        texCoords[0]  = 1; texCoords[1]  = 0; texCoords[2]  = 0;
        texCoords[3]  = 1; texCoords[4]  = 1; texCoords[5]  = 0;
        texCoords[6]  = 0; texCoords[7]  = 1; texCoords[8]  = 0;
        texCoords[9]  = 0; texCoords[10] = 0; texCoords[11] = 0;

        // left face
        texCoords[12] = 0; texCoords[13] = 0; texCoords[14] = 0;
        texCoords[15] = 0; texCoords[16] = 1; texCoords[17] = 0;
        texCoords[18] = 0; texCoords[19] = 1; texCoords[20] = 1;
        texCoords[21] = 0; texCoords[22] = 0; texCoords[23] = 1;

        // back face
        texCoords[24] = 0; texCoords[25] = 0; texCoords[26] = 1;
        texCoords[27] = 0; texCoords[28] = 1; texCoords[29] = 1;
        texCoords[30] = 1; texCoords[31] = 1; texCoords[32] = 1;
        texCoords[33] = 1; texCoords[34] = 0; texCoords[35] = 1;

        // right face
        texCoords[36] = 1; texCoords[37] = 0; texCoords[38] = 1;
        texCoords[39] = 1; texCoords[40] = 1; texCoords[41] = 1;
        texCoords[42] = 1; texCoords[43] = 1; texCoords[44] = 0;
        texCoords[45] = 1; texCoords[46] = 0; texCoords[47] = 0;

        // top face
        texCoords[48] = 1; texCoords[49] = 1; texCoords[50] = 0;
        texCoords[51] = 1; texCoords[52] = 1; texCoords[53] = 1;
        texCoords[54] = 0; texCoords[55] = 1; texCoords[56] = 1;
        texCoords[57] = 0; texCoords[58] = 1; texCoords[59] = 0;

        // bottom face
        texCoords[60] = 0; texCoords[61] = 0; texCoords[62] = 1;
        texCoords[63] = 0; texCoords[64] = 1; texCoords[65] = 1;
        texCoords[66] = 1; texCoords[67] = 1; texCoords[68] = 1;
        texCoords[69] = 1; texCoords[70] = 0; texCoords[71] = 1;
    }

    // Special case stuff for unindexed triangles.

    /**
     * Generate a new set of raw points for a triangle array. This puts a point
     * at each corner and generates two triangles from this in anti-clockwise
     * ordering for the face ordering: front, left, back, right, top, bottom.
     * Faces vertices are in the order: bottom-right, top-right, top-left and
     * top-left, bottom-right, bottom-left when looking directly at the outside of
     * the face. The only exception is the bottom face that starts bottom-left
     * and then continues in a clockwise order. This is so that someone
     * creating a line strip array from this will get a nice looking output.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     */
    private void generateUnindexedTriCoordinates(GeometryData data)
        throws InvalidArraySizeException
    {
        if(data.coordinates == null)
            data.coordinates = new float[TRIANGLE_VERTEX_COUNT * 3];
        else if(data.coordinates.length < TRIANGLE_VERTEX_COUNT * 3)
            throw new InvalidArraySizeException("Coordinates",
                                                data.coordinates.length,
                                                TRIANGLE_VERTEX_COUNT * 3);

        float[] coords = data.coordinates;
        data.vertexCount = TRIANGLE_VERTEX_COUNT;

        float sx = boxWidth / 2;
        float sy = boxHeight  / 2;
        float sz = boxDepth / 2;

        // front face
        coords[0] =  sx;  coords[1] =  -sy; coords[2] =   sz;
        coords[3] =  sx;  coords[4] =   sy; coords[5] =   sz;
        coords[6] = -sx;  coords[7] =   sy; coords[8] =   sz;

        coords[9] =  -sx; coords[10] =  sy; coords[11] =  sz;
        coords[12] = -sx; coords[13] = -sy; coords[14] =  sz;
        coords[15] =  sx; coords[16] = -sy; coords[17] =  sz;

        // left face
        coords[18] = -sx; coords[19] = -sy; coords[20] =  sz;
        coords[21] = -sx; coords[22] =  sy; coords[23] =  sz;
        coords[24] = -sx; coords[25] =  sy; coords[26] = -sz;

        coords[27] = -sx; coords[28] =  sy; coords[29] = -sz;
        coords[30] = -sx; coords[31] = -sy; coords[32] = -sz;
        coords[33] = -sx; coords[34] = -sy; coords[35] =  sz;

        // back face
        coords[36] = -sx; coords[37] = -sy; coords[38] = -sz;
        coords[39] = -sx; coords[40] =  sy; coords[41] = -sz;
        coords[42] =  sx; coords[43] =  sy; coords[44] = -sz;

        coords[45] =  sx; coords[46] =  sy; coords[47] = -sz;
        coords[48] =  sx; coords[49] = -sy; coords[50] = -sz;
        coords[51] = -sx; coords[52] = -sy; coords[53] = -sz;

        // right face
        coords[54] =  sx; coords[55] = -sy; coords[56] = -sz;
        coords[57] =  sx; coords[58] =  sy; coords[59] = -sz;
        coords[60] =  sx; coords[61] =  sy; coords[62] =  sz;

        coords[63] =  sx; coords[64] =  sy; coords[65] =  sz;
        coords[66] =  sx; coords[67] = -sy; coords[68] =  sz;
        coords[69] =  sx; coords[70] = -sy; coords[71] = -sz;

        // top face
        coords[72] =  sx; coords[73] =  sy; coords[74] =  sz;
        coords[75] =  sx; coords[76] =  sy; coords[77] = -sz;
        coords[78] = -sx; coords[79] =  sy; coords[80] = -sz;

        coords[81] = -sx; coords[82] =  sy; coords[83] = -sz;
        coords[84] = -sx; coords[85] =  sy; coords[86] =  sz;
        coords[87] =  sx; coords[88] =  sy; coords[89] =  sz;

        // bottom face
        coords[90] = -sx; coords[91] = -sy; coords[92] = -sz;
        coords[93] =  sx; coords[94] = -sy; coords[95] = -sz;
        coords[96] =  sx; coords[97] = -sy; coords[98] =  sz;

        coords[99] =  sx; coords[100]= -sy; coords[101]=  sz;
        coords[102]= -sx; coords[103]= -sy; coords[104]=  sz;
        coords[105]= -sx; coords[106]= -sy; coords[107]= -sz;
    }

    /**
     * Generate a new set of normals for a normal set of unindexed triangle
     * points. Each normal faces directly perpendicular for each point. This
     * makes each face seem flat.
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

        if(data.normals == null)
            data.normals = new float[TRIANGLE_VERTEX_COUNT * 3];
        else if(data.normals.length < TRIANGLE_VERTEX_COUNT * 3)
            throw new InvalidArraySizeException("Normals",
                                                data.normals.length,
                                                TRIANGLE_VERTEX_COUNT * 3);

        float[] normals = data.normals;

        // front face
        normals[0] = 0;  normals[1] = 0;  normals[2] = 1;
        normals[3] = 0;  normals[4] = 0;  normals[5] = 1;
        normals[6] = 0;  normals[7] = 0;  normals[8] = 1;

        normals[9]  = 0; normals[10] = 0; normals[11] = 1;
        normals[12] = 0; normals[13] = 0; normals[14] = 1;
        normals[15] = 0; normals[16] = 0; normals[17] = 1;

        // left face
        normals[18] =-1; normals[19] = 0; normals[20] = 0;
        normals[21] =-1; normals[22] = 0; normals[23] = 0;
        normals[24] =-1; normals[25] = 0; normals[26] = 0;

        normals[27] =-1; normals[28] = 0; normals[29] = 0;
        normals[30] =-1; normals[31] = 0; normals[32] = 0;
        normals[33] =-1; normals[34] = 0; normals[35] = 0;

        // back face
        normals[36] = 0; normals[37] = 0; normals[38] = -1;
        normals[39] = 0; normals[40] = 0; normals[41] = -1;
        normals[42] = 0; normals[43] = 0; normals[44] = -1;

        normals[45] = 0; normals[46] = 0; normals[47] = -1;
        normals[48] = 0; normals[49] = 0; normals[50] = -1;
        normals[51] = 0; normals[52] = 0; normals[53] = -1;

        // right face
        normals[54] = 1; normals[55] = 0; normals[56] = 0;
        normals[57] = 1; normals[58] = 0; normals[59] = 0;
        normals[60] = 1; normals[61] = 0; normals[62] = 0;

        normals[63] = 1; normals[64] = 0; normals[65] = 0;
        normals[66] = 1; normals[67] = 0; normals[68] = 0;
        normals[69] = 1; normals[70] = 0; normals[71] = 0;

        // top face
        normals[72] = 0; normals[73] = 1; normals[74] = 0;
        normals[75] = 0; normals[76] = 1; normals[77] = 0;
        normals[78] = 0; normals[79] = 1; normals[80] = 0;

        normals[81] = 0; normals[82] = 1; normals[83] = 0;
        normals[84] = 0; normals[85] = 1; normals[86] = 0;
        normals[87] = 0; normals[88] = 1; normals[91] = 0;

        // bottom face
        normals[90] = 0; normals[91] =-1; normals[92] = 0;
        normals[93] = 0; normals[94] =-1; normals[95] = 0;
        normals[96] = 0; normals[97] =-1; normals[98] = 0;

        normals[99] = 0; normals[100]=-1; normals[101]= 0;
        normals[102]= 0; normals[103]=-1; normals[104]= 0;
        normals[105]= 0; normals[106]=-1; normals[107]= 0;
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
    private void generateTriTexture2D(GeometryData data)
        throws InvalidArraySizeException
    {

        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[TRIANGLE_VERTEX_COUNT * 2];
        else if(data.textureCoordinates.length < TRIANGLE_VERTEX_COUNT * 2)
            throw new InvalidArraySizeException("2D Texture coordinates",
                                                data.textureCoordinates.length,
                                                TRIANGLE_VERTEX_COUNT * 2);

        float[] texCoords = data.textureCoordinates;

        // front face
        texCoords[0] = 1;  texCoords[1] = 0;
        texCoords[2] = 1;  texCoords[3] = 1;
        texCoords[4] = 0;  texCoords[5] = 1;

        texCoords[6]  = 0; texCoords[7]  = 1;
        texCoords[8]  = 0; texCoords[9]  = 0;
        texCoords[10] = 1; texCoords[11] = 0;

        // left face
        texCoords[12] = 1;  texCoords[13] = 0;
        texCoords[14] = 1;  texCoords[15] = 1;
        texCoords[16] = 0;  texCoords[17] = 1;

        texCoords[18] = 0;  texCoords[19] = 1;
        texCoords[20] = 0;  texCoords[21] = 0;
        texCoords[22] = 1;  texCoords[23] = 0;

        // back face
        texCoords[24] = 1;  texCoords[25] = 0;
        texCoords[26] = 1;  texCoords[27] = 1;
        texCoords[28] = 0;  texCoords[29] = 1;

        texCoords[30] = 0;  texCoords[31] = 1;
        texCoords[32] = 0;  texCoords[33] = 0;
        texCoords[34] = 1;  texCoords[35] = 0;

        // right face
        texCoords[36] = 1;  texCoords[37] = 0;
        texCoords[38] = 1;  texCoords[39] = 1;
        texCoords[40] = 0;  texCoords[41] = 1;

        texCoords[42] = 0;  texCoords[43] = 1;
        texCoords[44] = 0;  texCoords[45] = 0;
        texCoords[46] = 1;  texCoords[47] = 0;

        // top face
        texCoords[48] = 1;  texCoords[49] = 0;
        texCoords[50] = 1;  texCoords[51] = 1;
        texCoords[52] = 0;  texCoords[53] = 1;

        texCoords[54] = 0;  texCoords[55] = 1;
        texCoords[56] = 0;  texCoords[57] = 0;
        texCoords[58] = 1;  texCoords[59] = 0;

        // bottom face
        texCoords[60] = 0;  texCoords[61] = 0;
        texCoords[62] = 1;  texCoords[63] = 0;
        texCoords[64] = 1;  texCoords[65] = 1;

        texCoords[66] = 1;  texCoords[67] = 1;
        texCoords[68] = 0;  texCoords[69] = 1;
        texCoords[70] = 0;  texCoords[71] = 0;
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

        if(data.textureCoordinates == null)
            data.textureCoordinates = new float[TRIANGLE_VERTEX_COUNT * 3];
        else if(data.textureCoordinates.length < TRIANGLE_VERTEX_COUNT * 3)
            throw new InvalidArraySizeException("3D Texture coordinates",
                                                data.textureCoordinates.length,
                                                TRIANGLE_VERTEX_COUNT * 3);

        float[] texCoords = data.textureCoordinates;

        // front face
        texCoords[0]  = 1; texCoords[1]  = 0; texCoords[2]  = 0;
        texCoords[3]  = 1; texCoords[4]  = 1; texCoords[5]  = 0;
        texCoords[6]  = 0; texCoords[7]  = 1; texCoords[8]  = 0;

        texCoords[9]  = 0; texCoords[10] = 1; texCoords[11]  = 0;
        texCoords[12] = 0; texCoords[13] = 0; texCoords[14] = 0;
        texCoords[15] = 1; texCoords[16] = 0; texCoords[17] = 0;

        // left face
        texCoords[18] = 0; texCoords[19] = 0; texCoords[20] = 0;
        texCoords[21] = 0; texCoords[22] = 1; texCoords[23] = 0;
        texCoords[24] = 0; texCoords[25] = 1; texCoords[26] = 1;

        texCoords[27] = 0; texCoords[28] = 1; texCoords[29] = 1;
        texCoords[30] = 0; texCoords[31] = 0; texCoords[32] = 1;
        texCoords[33] = 0; texCoords[34] = 0; texCoords[35] = 0;

        // back face
        texCoords[36] = 0; texCoords[37] = 0; texCoords[38] = 1;
        texCoords[39] = 0; texCoords[40] = 1; texCoords[41] = 1;
        texCoords[42] = 1; texCoords[43] = 1; texCoords[44] = 1;

        texCoords[45] = 1; texCoords[46] = 1; texCoords[47] = 1;
        texCoords[48] = 1; texCoords[49] = 0; texCoords[50] = 1;
        texCoords[51] = 0; texCoords[52] = 0; texCoords[53] = 1;

        // right face
        texCoords[54] = 1; texCoords[55] = 0; texCoords[56] = 1;
        texCoords[57] = 1; texCoords[58] = 1; texCoords[59] = 1;
        texCoords[60] = 1; texCoords[61] = 1; texCoords[62] = 0;

        texCoords[63] = 1; texCoords[64] = 1; texCoords[65] = 0;
        texCoords[66] = 1; texCoords[67] = 0; texCoords[68] = 0;
        texCoords[69] = 1; texCoords[70] = 0; texCoords[71] = 1;

        // top face
        texCoords[72] = 1; texCoords[72] = 1; texCoords[74] = 0;
        texCoords[75] = 1; texCoords[75] = 1; texCoords[77] = 1;
        texCoords[78] = 0; texCoords[78] = 1; texCoords[80] = 1;

        texCoords[81] = 0; texCoords[81] = 1; texCoords[83] = 1;
        texCoords[84] = 0; texCoords[84] = 1; texCoords[86] = 0;
        texCoords[87] = 1; texCoords[87] = 1; texCoords[89] = 0;

        // bottom face
        texCoords[90] = 0; texCoords[91] = 0; texCoords[92] = 1;
        texCoords[93] = 0; texCoords[94] = 1; texCoords[95] = 1;
        texCoords[96] = 1; texCoords[97] = 1; texCoords[98] = 1;

        texCoords[99] = 1; texCoords[100]= 1; texCoords[101]= 1;
        texCoords[102]= 1; texCoords[103]= 0; texCoords[104]= 1;
        texCoords[105]= 0; texCoords[106]= 0; texCoords[107]= 1;
    }
}
