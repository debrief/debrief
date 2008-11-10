/*****************************************************************************
 *                        Copyright (c) 2001 Daniel Selman
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.particle;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Color4f;

import javax.media.j3d.Shape3D;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Bounds;

/**
 * Particle that uses PointArrays as the basic geometry type.
 * <p>
 *
 * Update methods are defined for a PointArray
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public class PointArrayByRefParticle extends ByRefParticle
{
   protected static final int X_COORD_INDEX = 0;
   protected static final int Y_COORD_INDEX = 1;
   protected static final int Z_COORD_INDEX = 2;

   protected static final int RED_COLOR_INDEX = 0;
   protected static final int GREEN_COLOR_INDEX = 1;
   protected static final int BLUE_COLOR_INDEX = 2;
   protected static final int ALPHA_COLOR_INDEX = 3;

   public PointArrayByRefParticle( Shape3D shape, int index,
                                              double[] positionRefArray,
                                              float[] colorRefArray,
                                              float[] textureCoordRefArray,
                                              float[] normalRefArray )
   {
       super( shape, index, positionRefArray, colorRefArray, textureCoordRefArray, normalRefArray );
   }

   public void updateGeometry()
   {
       positionRefArray[index*NUM_COORDS+X_COORD_INDEX] = position.x;
       positionRefArray[index*NUM_COORDS+Y_COORD_INDEX] = position.y;
       positionRefArray[index*NUM_COORDS+Z_COORD_INDEX] = position.z;
   }

   public void updateColors()
   {
       colorRefArray[index*NUM_COLORS+RED_COLOR_INDEX] = color.x;
       colorRefArray[index*NUM_COLORS+GREEN_COLOR_INDEX] = color.y;
       colorRefArray[index*NUM_COLORS+BLUE_COLOR_INDEX] = color.z;
       colorRefArray[index*NUM_COLORS+ALPHA_COLOR_INDEX] = color.w;
   }
}
