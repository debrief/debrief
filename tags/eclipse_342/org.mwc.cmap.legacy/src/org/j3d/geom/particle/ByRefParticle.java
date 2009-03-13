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
import javax.media.j3d.Transform3D;

/**
 * Particle that stores position, color and texture
 * information in shared BYREF arrays.
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public abstract class ByRefParticle extends Particle
{
   public static final int NUM_COORDS = 3;
   public static final int NUM_COLORS = 4;
   public static final int NUM_TEXTURE_COORDS = 2;
   public static final int NUM_NORMALS = 3;

   // particle index into the RefArray's
   int index;
   double[] positionRefArray;
   float[] colorRefArray;
   float[] textureCoordRefArray;
   float[] normalRefArray;
   Shape3D shape;

   Transform3D localToVWorld = new Transform3D();

   public ByRefParticle( Shape3D shape, int index, double[] positionRefArray,
                                    float[] colorRefArray,
                                    float[] textureCoordRefArray,
                                    float[] normalRefArray )
   {
       this.shape = shape;
       this.index = index;
       this.positionRefArray = positionRefArray;
       this.colorRefArray = colorRefArray;
       this.textureCoordRefArray = textureCoordRefArray;
       this.normalRefArray = normalRefArray;
   }

   public void setPosition( double x, double y, double z )
   {
       super.setPosition( x, y, z );
       updateGeometry();
   }

   public void setColor( float r, float g, float b, float alpha )
   {
       super.setColor( r, g, b, alpha );
       updateColors();
   }

   public void setAlpha( float alpha )
   {
       super.setAlpha( alpha );
       updateColors();
   }

   /**
    * Implement this method to update the BYREF colors for
    * the geometry based on the change to the color field.
    *
    */
   protected abstract void updateColors();

   /**
    * Implement this method to update the BYREF positions of
    * the geometry based on the change to the position field.
    *
    */
   protected abstract void updateGeometry();
}
