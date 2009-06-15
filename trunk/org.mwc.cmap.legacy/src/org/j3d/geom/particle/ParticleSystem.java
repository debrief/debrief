/*****************************************************************************
 *                        Copyright (c) 2001 Daniel Selman
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.Node;

/**
 * Abstract ParticleSystem. A ParticleSystem managed a List of Particles
 * created by a ParticleFactory. It applies changes to the Particles using
 * a List of MovementFunctions and a ParticleInitializer.
 * <P>
 * A ParticleSystem can be represented in any way appropriate, the only
 * requirement is that is create a Node to be added to the scenegraph.
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public abstract class ParticleSystem implements ParticleFactory
{
   /**
    * Name of the environment property that holds the texture to use
    * on the particle objects. The value may be either a string, which is a
    * filename, relative to the CLASSPATH, or an instance of a J3D
    * {@link javax.media.j3d.Texture} object.
    */
   public static final String PARTICLE_TEXTURE = "texture";

   protected List<Particle> particles;
   protected int particleCount;

   protected List<MovementFunction> movementFunctions = new ArrayList<MovementFunction>();
   protected ParticleInitializer particleInitializer = null;
   protected ParticleFactory particleFactory = null;

   protected boolean running = false;

   /** The environment entries pass to the system for initialisation. */
   protected Map<String, Object> environment;

   public ParticleSystem( int systemType, Map<String, Object> environment )
   {
       this.environment = new HashMap<String, Object>(environment);
       particleFactory = this;
   }

   protected void createParticles( ParticleInitializer particleInitializer1, int particleCount1 )
   {
       this.particleCount = particleCount1;
       this.particleInitializer = particleInitializer1;

       particles = new ArrayList<Particle>();

       for( int n = 0; n < particleCount1; n++ )
       {
           Particle particle = particleFactory.createParticle( n );
           initParticle( particle );
           particles.add( n, particle );
       }
   }

   public void setParticleInitializer( ParticleInitializer particleInitializer )
   {
       this.particleInitializer = particleInitializer;
   }

   public boolean updateParticle( int index, Particle particle )
   {
       if( particleInitializer.isAlive( particle ) != false )
       {
           MovementFunction function = null;

           for( int n = movementFunctions.size()-1; n >= 0; n-- )
           {
               function = (MovementFunction) movementFunctions.get( n );
               running |= function.apply( particle );
           }
       }
       else
       {
           running |= particleInitializer.initialize( particle );
       }

       return running;
   }


   public void initParticle( Particle particle )
   {
       particleInitializer.initialize( particle );
   }

   public void addMovementFunction( MovementFunction function )
   {
       movementFunctions.add( function );
   }

   public abstract boolean update();
   public abstract Node getNode();
   public abstract Particle createParticle( int index );
}
