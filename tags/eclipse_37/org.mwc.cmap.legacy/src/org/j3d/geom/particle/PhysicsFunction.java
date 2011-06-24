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
 * Movement function that performs basic F=M.
 * <p>
 * A movement control on the Particles
 * based on their applied resultantForce. A percentage
 * of the resultantForce is "lost" prior to position
 * calculation to simulate friction/drag.
 * <p>
 * This MovementFunction should be added to the ParticleSystem
 * *after* any MovementFunctions which are applying
 * forces to Particles.
 * <p>
 * Some basic physics equations:
 * <p>
 *
 * acceleration = force / mass;<br>
 * velocity += acceleration * time_diff;<br>
 * pos += velocity * time_diff;<br>
 * ke = 0.5 * m * v * v<br>
 * p.e (grav) = m * g * h<br>
 * p.e (spring) = 0.5 * k * x * x (x = amount of compression)<br>
 * total mech energy = k.e + p.e. (grav) + p.e. (spring)<br>
 * power = work / time<br>
 * power = force * displacement / time<br>
 * f = m * a<br>
 * f = m * delta v / t<br>
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public class PhysicsFunction implements MovementFunction
{
   // the assumed interval between calls to the PhysicsFunction
   private double deltaTime = 1;

   private Vector3d position = new Vector3d();
   private Vector3d acceleration = new Vector3d();

   // percentage of force still avalailable after friction lossage
   private double frictionForce = 0.90;

   // percentage of velocity still avilable
   private double frictionVelocity = 0.95;

   public PhysicsFunction( double deltaTime )
   {
       this.deltaTime = deltaTime;
   }

   public boolean apply( Particle particle )
   {
       particle.resultantForce.scale( frictionForce );
       acceleration.set( particle.resultantForce );

       // get the change in velocity
       acceleration.scale( deltaTime / particle.mass );
       particle.velocity.add( acceleration );

       // get the change in position
       particle.getPosition( position );
       acceleration.set( particle.velocity );
       acceleration.scale( deltaTime );
       position.add( acceleration );

//       if ( ((ByRefParticle) particle).index == 0 )
//       {
//           System.out.println( "force: " + particle.resultantForce );
//           System.out.println( "acceleration: " + acceleration );
//           System.out.println( "velocity: " + particle.velocity );
//           System.out.println( "position: " + position );
//       }

       // update the position
       particle.setPosition( position.x, position.y, position.z );

       particle.velocity.scale( frictionVelocity );

       // return false - the PhysicsFunction
       // should not cause the particle system to continue
       return false;
   }
}
