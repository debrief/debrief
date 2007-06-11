/*****************************************************************************
 *                        Copyright (c) 2001 Daniel Selman
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.particle;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.media.j3d.Texture;
import javax.media.j3d.Shape3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Appearance;
import javax.media.j3d.PointAttributes;

import javax.media.j3d.PointArray;

import javax.vecmath.Point3d;

/**
 * PointArrayByRefParticleSystem creates a BYREF PointArray
 * to represent the ParticleSystem.
 *
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public class PointArrayByRefParticleSystem extends ByRefParticleSystem
{
   public static final int POINT_ARRAY_BYREF_PARTICLE_SYSTEM = 0;

   public PointArrayByRefParticleSystem( int particleCount, ParticleInitializer particleInitializer, Map environment )
   {
       super( POINT_ARRAY_BYREF_PARTICLE_SYSTEM, particleInitializer, particleCount, environment );
   }

   public GeometryArray createGeometryArray()
   {
       GeometryArray geomArray = new PointArray( particleCount, GeometryArray.COORDINATES |
                                                                GeometryArray.COORDINATES |
                                                                GeometryArray.NORMALS |
                                                                GeometryArray.TEXTURE_COORDINATE_2 |
                                                                GeometryArray.BY_REFERENCE |
                                                                GeometryArray.COLOR_4 );
       return geomArray;
   }

   public Appearance createAppearance()
   {
       Appearance app = new Appearance();
       app.setPointAttributes( new PointAttributes( 10, true ) );
       return app;
   }

   public Particle createParticle( int index )
   {
       return new PointArrayByRefParticle( shape, index, positionRefArray, colorRefArray, textureCoordRefArray, normalRefArray );
   }

   protected int getVertexCount()
   {
       return 1;
   }
}
