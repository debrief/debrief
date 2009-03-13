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
 * MovementFunction is the basic interface for
 * functions that can modify the fields of a Particle.
 * Some movement functions will modify the force/energy of
 * a particle while others will convert the energy into
 * position deltas.
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public interface MovementFunction
{
   public boolean apply( Particle particle );
}
