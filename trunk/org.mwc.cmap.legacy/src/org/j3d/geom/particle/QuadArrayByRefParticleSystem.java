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

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color4f;

/**
 * A particle system that uses a BYREF QuadArray
 * to represent the ParticleSystem.
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public class QuadArrayByRefParticleSystem extends ByRefParticleSystem
{
    public static final int QUAD_ARRAY_BYREF_PARTICLE_SYSTEM = 1;

    private static PolygonAttributes polygonAttributes;
    private static TransparencyAttributes transparencyAttributes;
    private static TextureAttributes textureAttributes;

    /**
     * Static initializer to create the attribute classes once for all
     * to share.
     */
    static
    {
        polygonAttributes =
            new PolygonAttributes( PolygonAttributes.POLYGON_FILL,
                                   PolygonAttributes.CULL_NONE, 0 );

        transparencyAttributes =
            new TransparencyAttributes( TransparencyAttributes.NICEST, 0.0f );

        textureAttributes =
            new TextureAttributes( TextureAttributes.REPLACE,
                                   new Transform3D(),
                                   new Color4f(),
                                   TextureAttributes.FASTEST );
    }

    /**
     * Create a new particle system with the given number of particles.
     *
     * @param particleCount The number of particles to display
     * @param particleInitializer Initialised to create the particles
     * @param environment Environment setup information
     */
    public QuadArrayByRefParticleSystem(int particleCount,
                                       ParticleInitializer particleInitializer,
                                       Map<String, Object> environment)
    {
       super( QUAD_ARRAY_BYREF_PARTICLE_SYSTEM,
              particleInitializer,
              particleCount,
              environment );
    }

    /**
     * Request to create the geometry needed by this system.
     *
     * @return The object representing the geometry
     */
    public GeometryArray createGeometryArray()
    {
        GeometryArray geomArray =
         new QuadArray( particleCount * QuadArrayByRefParticle.NUM_VERTICES_PER_PARTICLE,
                        GeometryArray.COORDINATES |
                        GeometryArray.NORMALS |
                        GeometryArray.TEXTURE_COORDINATE_2 |
                        GeometryArray.BY_REFERENCE |
                        GeometryArray.COLOR_4 );
        return geomArray;
    }

    /**
     * Create the appearance used to render the objects with. This appearance
     * should have all appropriate information set - including textures.
     *
     * @return The appearance object to use with this system
     */
    public Appearance createAppearance()
    {
        Appearance app = new Appearance();
        app.setPolygonAttributes( polygonAttributes );
        app.setTransparencyAttributes( transparencyAttributes );
        app.setTextureAttributes( textureAttributes );

        // load the texture image and assign to the appearance
        Object prop = environment.get(PARTICLE_TEXTURE);
        Texture tex = null;

        if(prop instanceof String)
        {
            // do stuff with the texture cache
        }
        else if(prop instanceof Texture)
        {
            tex = (Texture)prop;
        }

        app.setTexture( tex );
        return app;
    }

    /**
     * Create a new particle with the given ID.
     *
     * @param index The id of the particle
     * @return A particle corresponding to the given index
     */
    public Particle createParticle( int index )
    {
        return new QuadArrayByRefParticle( shape,
                                           index,
                                           positionRefArray,
                                           colorRefArray,
                                           textureCoordRefArray,
                                           normalRefArray );
    }

    /**
     * Fetch the number of vertices used in this geometry. Value should
     * always be non-negative.
     *
     * @return The number of vertices
     */
    protected int getVertexCount()
    {
        return QuadArrayByRefParticle.NUM_VERTICES_PER_PARTICLE;
    }
}
