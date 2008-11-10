/*****************************************************************************
 *                        Copyright (c) 2001 Daniel Selman
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.particle;

import javax.vecmath.Vector3d;

/**
 * MaxAgePointEmitter checks the age of a Particle
 * and reinitializes it by moving it to a point in space
 * and clearing resultant force and velocity.
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public class MaxAgePointEmitter implements ParticleInitializer
{
   int maxAge;
   double originX;
   double originY;
   double originZ;

   public MaxAgePointEmitter( int maxAge, double x, double y, double z )
   {
       this.maxAge = maxAge;
       originX = x;
       originY = y;
       originZ = z;
   }

   public boolean initialize( Particle particle )
   {
       particle.setColor( (float) Math.random(), (float) Math.random(), (float) Math.random(), 1 );

       particle.setPosition( originX, originY, originZ );
       particle.setCycleAge( (int) (Math.random() * maxAge) );
       particle.resultantForce.set( 0,0,0 );
       particle.velocity.set( 0,0,0 );
       return true;
   }

   public boolean isAlive( Particle particle )
   {
       return (particle.getCycleAge() < maxAge );
   }
}
