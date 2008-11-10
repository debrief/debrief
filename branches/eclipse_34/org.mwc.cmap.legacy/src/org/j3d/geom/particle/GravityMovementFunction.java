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
import javax.vecmath.Point3d;
import javax.vecmath.AxisAngle4d;
import javax.media.j3d.Transform3D;

/**
 * GravityMovementFunction applied a gravity force to
 * Particles.
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public class GravityMovementFunction implements MovementFunction
{
   // force of gravity: meters per second
   private Vector3d gravityForce = new Vector3d( 0, -9.8, 0 );

   public GravityMovementFunction()
   {
   }

   public boolean apply( Particle particle )
   {
       gravityForce.x = 0;
       gravityForce.y = -9.8;
       gravityForce.z = 0;

       gravityForce.scale( particle.mass );
       particle.resultantForce.add( gravityForce );
       return false;
   }
}
