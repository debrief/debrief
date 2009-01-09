/*****************************************************************************
 *                        Copyright (c) 2001 Daniel Selman
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.particle;

import javax.media.j3d.Shape3D;

/**
 * Particle that uses QuadArrays as the basic geometry.
 * <p>
 *
 * Update methods are defined for a QuadArray:
 *
 * <pre>
 *  <- width*2 ->
 *  1 --------- 2    / \
 *   |          |     |
 *   |          |     |
 *   |     +    |   height*2
 *   |          |     |
 *   |          |     |
 *  4 --------- 3    \ /
 *
 * </pre>
 * Quad 1: 1,2,3,4
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public class QuadArrayByRefParticle extends ByRefParticle
{
   public static final int NUM_VERTICES_PER_PARTICLE = 4;

   protected double width = 0.1;
   protected double height = 0.1;
   protected double depth = 0.1;

   protected static final int X_COORD_INDEX = 0;
   protected static final int Y_COORD_INDEX = 1;
   protected static final int Z_COORD_INDEX = 2;

   protected static final int COORD_POINT_1 = 0;
   protected static final int COORD_POINT_2 = 3;
   protected static final int COORD_POINT_3 = 6;
   protected static final int COORD_POINT_4 = 9;

   protected static final int TEX_POINT_1 = 0;
   protected static final int TEX_POINT_2 = 2;
   protected static final int TEX_POINT_3 = 4;
   protected static final int TEX_POINT_4 = 6;

   protected static final int S_COORD_INDEX = 0;
   protected static final int T_COORD_INDEX = 1;

   protected static final int RED_COLOR_INDEX = 0;
   protected static final int GREEN_COLOR_INDEX = 1;
   protected static final int BLUE_COLOR_INDEX = 2;
   protected static final int ALPHA_COLOR_INDEX = 3;

   // we need to save these, as none of the vertices in
   // the TriangleArray correspond to the particle position
   protected int startIndex = 0;

   public QuadArrayByRefParticle( Shape3D shape, int index,
                                                 double[] positionRefArray,
                                                 float[] colorRefArray,
                                                 float[] textureCoordRefArray,
                                                 float[] normalRefArray )
   {
       super( shape, index, positionRefArray, colorRefArray, textureCoordRefArray, normalRefArray );

       startIndex = index * NUM_VERTICES_PER_PARTICLE * ByRefParticle.NUM_COORDS;

       setTextureCoordinates();
       setNormals();
   }

   protected void updateColors()
   {
       int colorStartIndex = index * NUM_VERTICES_PER_PARTICLE * ByRefParticle.NUM_COLORS;

       for( int vertex = 0; vertex < NUM_VERTICES_PER_PARTICLE; vertex++ )
       {
           colorRefArray[colorStartIndex + (vertex * ByRefParticle.NUM_COLORS) + RED_COLOR_INDEX] = color.x;
           colorRefArray[colorStartIndex + (vertex * ByRefParticle.NUM_COLORS) + GREEN_COLOR_INDEX] = color.y;
           colorRefArray[colorStartIndex + (vertex * ByRefParticle.NUM_COLORS) + BLUE_COLOR_INDEX] = color.z;
           colorRefArray[colorStartIndex + (vertex * ByRefParticle.NUM_COLORS) + ALPHA_COLOR_INDEX] = color.w;
       }
   }

   protected void updateGeometry()
   {
       // point 1
       positionRefArray[ startIndex + COORD_POINT_1 + X_COORD_INDEX ] = position.x + width;
       positionRefArray[ startIndex + COORD_POINT_1 + Y_COORD_INDEX ] = position.y - height;
       positionRefArray[ startIndex + COORD_POINT_1 + Z_COORD_INDEX ] = position.z;

       // point 2
       positionRefArray[ startIndex + COORD_POINT_2 + X_COORD_INDEX ] = position.x + width;
       positionRefArray[ startIndex + COORD_POINT_2 + Y_COORD_INDEX ] = position.y + height;
       positionRefArray[ startIndex + COORD_POINT_2 + Z_COORD_INDEX ] = position.z;

       // point 3
       positionRefArray[ startIndex + COORD_POINT_3 + X_COORD_INDEX ] = position.x - width;
       positionRefArray[ startIndex + COORD_POINT_3 + Y_COORD_INDEX ] = position.y + height;
       positionRefArray[ startIndex + COORD_POINT_3 + Z_COORD_INDEX ] = position.z;

       // point 4
       positionRefArray[ startIndex + COORD_POINT_4 + X_COORD_INDEX ] = position.x - width;
       positionRefArray[ startIndex + COORD_POINT_4 + Y_COORD_INDEX ] = position.y - height;
       positionRefArray[ startIndex + COORD_POINT_4 + Z_COORD_INDEX ] = position.z;
   }

   private void setTextureCoordinates()
   {
       int texStartIndex = index * NUM_VERTICES_PER_PARTICLE * ByRefParticle.NUM_TEXTURE_COORDS;

       // point 1
       textureCoordRefArray[ texStartIndex + TEX_POINT_1 + S_COORD_INDEX ] = 1;
       textureCoordRefArray[ texStartIndex + TEX_POINT_1 + T_COORD_INDEX ] = 0;

       // point 2
       textureCoordRefArray[ texStartIndex + TEX_POINT_2 + S_COORD_INDEX ] = 1;
       textureCoordRefArray[ texStartIndex + TEX_POINT_2 + T_COORD_INDEX ] = 1;

       // point 3
       textureCoordRefArray[ texStartIndex + TEX_POINT_3 + S_COORD_INDEX ] = 0;
       textureCoordRefArray[ texStartIndex + TEX_POINT_3 + T_COORD_INDEX ] = 1;

       // point 4 = point 1
       textureCoordRefArray[ texStartIndex + TEX_POINT_4 + S_COORD_INDEX ] = 0;
       textureCoordRefArray[ texStartIndex + TEX_POINT_4 + T_COORD_INDEX ] = 0;
   }

   private void setNormals()
   {
       int normalStartIndex = index * NUM_VERTICES_PER_PARTICLE * ByRefParticle.NUM_NORMALS;

       for( int vertex = 0; vertex < NUM_VERTICES_PER_PARTICLE; vertex++ )
       {
           normalRefArray[normalStartIndex + (vertex * ByRefParticle.NUM_NORMALS) + X_COORD_INDEX] = 0;
           normalRefArray[normalStartIndex + (vertex * ByRefParticle.NUM_NORMALS) + Y_COORD_INDEX] = 0;
           normalRefArray[normalStartIndex + (vertex * ByRefParticle.NUM_NORMALS) + Z_COORD_INDEX] = 1;
       }
   }
}
