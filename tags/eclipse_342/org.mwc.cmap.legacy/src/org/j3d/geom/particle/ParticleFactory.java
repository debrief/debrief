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
 * ParticleFactory interface defines a mechanism to
 * create instances of Particles. A ParticleFactory
 * is registered with a ParticleSystem and should
 * initialize the fields of the Particle based on the
 * physical entity being modelled (rain, dust, stones etc.)
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public interface ParticleFactory
{
   public Particle createParticle( int index );
}
