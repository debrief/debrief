/*****************************************************************************
 *                        Copyright (c) 2001 Daniel Selman
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.particle;

import java.util.Map;

import javax.media.j3d.Shape3D;
import javax.media.j3d.OrientedShape3D;

import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Node;

/**
 * Abstract ParticleSystem for handling ByRef GeometryArrays.
 * <p>
 *
 * The entire geometry for the ParticleSystem represented
 * by a single Shape3D. When an update request is received, the particles
 * update the position and color information in the arrays provided by this
 * class and are updated in the underlying geometry.
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public abstract class ByRefParticleSystem extends ParticleSystem
    implements GeometryUpdater
{
    /** The geometry created for this system */
    protected GeometryArray geometryArray;

    /** Array containing the current position coordinates */
    protected double positionRefArray[];

    /** Array containing the current texture coordinates */
    protected float textureCoordRefArray[];

    /** Array containing the current color values */
    protected float colorRefArray[];

    /** Array containing the current normals */
    protected float normalRefArray[];

    /** The shape containing the geometry */
    protected Shape3D shape;

    /**
     * Create a new particle system that represents the given type.
     *
     * @param systemType An identifier describing the current system type
     * @param particleInitializer Initialised to create the particles
     * @param environment Environment setup information
     * @param environment Environment setup information
     */
    public ByRefParticleSystem( int systemType,
                                ParticleInitializer particleInitializer,
                                int particleCount,
                                Map environment )
    {
        super( systemType, environment );

        shape = new OrientedShape3D();
        initializeArrays( particleCount );
        createParticles( particleInitializer, particleCount );

        geometryArray = createGeometryArray();
        geometryArray.setCapability( GeometryArray.ALLOW_REF_DATA_WRITE );

        shape.setGeometry( geometryArray );
        shape.setAppearance( createAppearance() );

        shape.setCollidable( false );
        shape.setPickable( false );
        shape.setBoundsAutoCompute( false );
    }

    /**
     * Fetch the scene graph node that represents the particle system.
     *
     * @return The shape containing the particles
     */
    public Node getNode()
    {
        return shape;
    }

    /**
     * Request to create the geometry needed by this system.
     *
     * @return The object representing the geometry
     */
    public abstract GeometryArray createGeometryArray();

    /**
     * Create the appearance used to render the objects with. This appearance
     * should have all appropriate information set - including textures.
     *
     * @return The appearance object to use with this system
     */
    public abstract Appearance createAppearance();

    /**
     * Fetch the number of vertices used in this geometry. Value should
     * always be non-negative.
     *
     * @return The number of vertices
     */
    protected abstract int getVertexCount();


    /**
     * Request to force an update of the geometry now.
     *
     * @return true if the system is currently running
     */
    public boolean update()
    {
        geometryArray.updateData( this );
        return running;
    }

    /**
     * Update request on the geometry data that is accessed by reference.
     *
     * @param geometry The geometry object being updated
     */
    public void updateData( Geometry geometry )
    {
        GeometryArray geometryArray = (GeometryArray) geometry;

        for( int n = particleCount-1; n >= 0; n-- )
        {
           Particle particle = (Particle) particles.get( n );
           particle.incAge();
           running |= updateParticle( n, particle );
        }

        geometryArray.setCoordRefDouble( positionRefArray );
        geometryArray.setColorRefFloat( colorRefArray );
        geometryArray.setNormalRefFloat( normalRefArray );
        geometryArray.setTexCoordRefFloat( 0, textureCoordRefArray );
    }

    /**
     * Set up the arrays used internally.
     *
     * @param particleCount The number of particles in use
     */
    synchronized protected void initializeArrays( int particleCount )
    {
        if ( positionRefArray == null )
        {
            int count = getVertexCount();

            int pos = particleCount * count * ByRefParticle.NUM_COORDS;
            int col = particleCount * count * ByRefParticle.NUM_COLORS;
            int norm = particleCount * count * ByRefParticle.NUM_NORMALS;
            int tex = particleCount * count * ByRefParticle.NUM_TEXTURE_COORDS;

            positionRefArray = new double[ pos ];
            colorRefArray = new float [ col ];
            normalRefArray = new float [ norm ];
            textureCoordRefArray = new float[ tex ];
        }
    }
}
