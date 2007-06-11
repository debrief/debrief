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
import javax.vecmath.Vector3d;

import javax.media.j3d.Node;
import javax.media.j3d.BranchGroup;

import com.sun.j3d.utils.picking.*;
import javax.media.j3d.PickBounds;

/**
 * PickingCollisionMovementFunction uses Java 3D picking utilities
 * to implemen collision response for Particles against any geometry
 * within a scenegraph branch.
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public class PickingCollisionMovementFunction implements MovementFunction
{
   BranchGroup pickRoot;
   PickTool pickTool;
   Point3d particlePostion = new Point3d();
   Point3d previousParticlePostion = new Point3d();
   Vector3d deltaPostion = new Vector3d();

   public PickingCollisionMovementFunction( BranchGroup pickRoot )
   {
       this.pickRoot = pickRoot;
       pickTool = new PickTool( pickRoot );
       pickTool.setMode( PickTool.BOUNDS );
   }

   public boolean apply( Particle particle )
   {
       particle.getPosition( particlePostion );

       // create a PickBounds to pick against
       PickBounds pickBounds = new PickBounds( particle.getBounds() );
       pickTool.setShape( pickBounds, new Point3d( 0,0,0 ) );
       PickResult pickResult = pickTool.pickAny( );

       if ( pickResult != null )
       {
           // because the collision is assumed to be between something much
           // more massive than ourselves we just negate our velocity
           // negate the force and loose 90% of it
           particle.velocity.negate();
           particle.resultantForce.negate();
           particle.resultantForce.scale( 0.1 );
       }

       return false;
   }
}
