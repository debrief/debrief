/*****************************************************************************
 *                        Copyright (c) 2001 Daniel Selman
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.particle;

/**
 * The ParticleInitializer is registered with a ParticleSystem
 * and is responsible for deciding when a Particle instance should
 * be recycled, as well as reinitializing the Particle.
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public interface ParticleInitializer
{

   /**
    * @return true if the ParticleSytem should keep running.
    */
   public boolean initialize( Particle particle );

   /**
    * @return false if the Particle needs to be reinitialized.
    */
   public boolean isAlive( Particle particle );
}
