/*****************************************************************************
 *                   J3D.org Copyright (c) 2000
 *                          Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom;

// Standard imports
import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleStripArray;

// Application specific imports
// none

/**
 * A simple spring that uses triangle strips.
 * <p>
 *
 * The created spring does not have any capabilities set except for the
 * ability to write the geometry - needed so that we can modify the geometry
 * when you change the height or radius. If you know that you are not going
 * to be changing the geometry you can turn this off.
 * <P>
 *
 * As we assume you may want to use this as a collidable object, we store the
 * {@link GeometryData} instance that is used to create the object in the
 * userData of the underlying {@link javax.media.j3d.TriangleStripArray}. The
 * geometry does not have texture coordinates set.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class Spring extends Shape3D
{
    /** The default inner radius of the torus */
    private static final float DEFAULT_INNER_RADIUS = 0.25f;

    /** The default outer radius of the torus */
    private static final float DEFAULT_OUTER_RADIUS = 1.0f;

    /** Default number of faces around the inner radius */
    private static final int DEFAULT_INNER_FACETS = 16;

    /** Default number of faces around the outer radius of one loop */
    private static final int DEFAULT_OUTER_FACETS = 16;

    /** Default number of loops to generate */
    private static final int DEFAULT_LOOP_COUNT = 4;

    /** Default spacing between loops */
    private static final float DEFAULT_LOOP_SPACING = 1.0f;

    /** The generator used to modify the geometry */
    private SpringGenerator generator;

    /** Data used to regenerate the spring */
    private GeometryData data;

    /**
     * Construct a default spring that has:<br>
     * inner radius: 0.25<br>
     * outer radius: 1.0<br>
     * inner facet count: 16<br>
     * outer facet count: 16<br>
     * loop count: 4<br>
     * loop spacing: 1.0<br>
     */
    public Spring()
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             DEFAULT_LOOP_SPACING,
             DEFAULT_LOOP_COUNT,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS,
             null);
    }

    /**
     * Construct a default spring with a given appearance that has:<br>
     * inner radius: 0.25<br>
     * outer radius: 1.0<br>
     * inner facet count: 16<br>
     * outer facet count: 16<br>
     * loop count: 4<br>
     * loop spacing: 1.0<br>
     *
     * @param app The Appearance to use
     */
    public Spring(Appearance app)
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             DEFAULT_LOOP_SPACING,
             DEFAULT_LOOP_COUNT,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS,
             app);
    }

    /**
     * Construct a spring that has the given radius values with all other
     * values fixed at the defaults
     *
     * @param ir The inner radius to use
     * @param or The outer radius to use
     */
    public Spring(float ir, float or)
    {
        this(ir,
             or,
             DEFAULT_LOOP_SPACING,
             DEFAULT_LOOP_COUNT,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS,
             null);
    }

    /**
     * Construct a spring that has the given radius values with all other
     * values fixed at the defaults but a customisable appearance.
     *
     * @param ir The inner radius to use
     * @param or The outer radius to use
     * @param app The Appearance to use
     */
    public Spring(float ir, float or, Appearance app)
    {
        this(ir,
             or,
             DEFAULT_LOOP_SPACING,
             DEFAULT_LOOP_COUNT,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS,
             app);
    }

    /**
     * Construct a spring that has the given number of loops with all other
     * values fixed at the defaults. The loop count must be one or more.
     *
     * @param lc The loop count
     * @throws IllegalArgumentException The loop count was invalid
     */
    public Spring(int lc)
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             DEFAULT_LOOP_SPACING,
             lc,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS,
             null);
    }

    /**
     * Construct a spring that has the given number of loops with all other
     * values fixed at the defaults. The loop count must be one or more. An
     * Appearance value may be supplied.
     *
     * @param lc The loop count
     * @param app The Appearance to use
     * @throws IllegalArgumentException The loop count was invalid
     */
    public Spring(int lc, Appearance app)
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             DEFAULT_LOOP_SPACING,
             lc,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS,
             app);
    }

    /**
     * Construct a spring with the given loop spacing and all other values
     * fixed at the defaults.
     *
     * @param spacing The spacing between loops
     */
    public Spring(float spacing)
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             spacing,
             DEFAULT_LOOP_COUNT,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS,
             null);
    }

    /**
     * Construct a spring with the given loop spacing and all other values
     * fixed at the defaults.
     *
     * @param spacing The spacing between loops
     * @param app The Appearance to use
     */
    public Spring(float spacing, Appearance app)
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             spacing,
             DEFAULT_LOOP_COUNT,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS,
             app);
    }

    /**
     * Construct a spring that has the selected number of facets but with all
     * other values fixed at the defaults. The minimum number of facets is 3.
     *
     * @param ifc The number of facets to use around the inner radius
     * @param ofc The number of facets to use around the outer radius
     * @throws IllegalArgumentException The number of facets is less than 3
     */
    public Spring(int ifc, int ofc)
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             DEFAULT_LOOP_SPACING,
             DEFAULT_LOOP_COUNT,
             ifc,
             ofc,
             null);
    }

    /**
     * Construct a spring that has the selected number of facets but with all
     * other values fixed at the defaults. The minimum number of facets is 3.
     *
     * @param ifc The number of facets to use around the inner radius
     * @param ofc The number of facets to use around the outer radius
     * @param app The Appearance to use
     * @throws IllegalArgumentException The number of facets is less than 3
     */
    public Spring(int ifc, int ofc, Appearance app)
    {
        this(DEFAULT_INNER_RADIUS,
             DEFAULT_OUTER_RADIUS,
             DEFAULT_LOOP_SPACING,
             DEFAULT_LOOP_COUNT,
             ifc,
             ofc,
             app);
    }

    /**
     * Construct a spring with the given radius, spacing and loop count
     * information. All other values are defaults. The loop count must be
     * greater than or equal to 1.
     *
     * @param ir The inner radius to use
     * @param or The outer radius to use
     * @param spacing The spacing between loops
     * @param lc The loop count
     * @throws IllegalArgumentException The loop count was invalid
     */
    public Spring(float ir, float or, float spacing, int lc)
    {
        this(ir,
             or,
             spacing,
             lc,
             DEFAULT_INNER_FACETS,
             DEFAULT_OUTER_FACETS,
             null);
    }

    /**
     * Construct a spring with the given radius, spacing and loop count
     * information, and facet count. The loop count must be greater than or
     * equal to 1 and the facet counts must be 3 or more.
     *
     * @param ir The inner radius to use
     * @param or The outer radius to use
     * @param spacing The spacing between loops
     * @param lc The loop count
     * @param ifc The number of facets to use around the inner radius
     * @param ofc The number of facets to use around the outer radius
     * @throws IllegalArgumentException The loop count was invalid or facet
     *   counts were less than 4
     */
    public Spring(float ir,
                           float or,
                           float spacing,
                           int lc,
                           int ifc,
                           int ofc,
                           Appearance app)
    {
        data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLE_STRIPS;
        data.geometryComponents = GeometryData.NORMAL_DATA;

        generator = new SpringGenerator(ir, or, ifc, ofc);

        generator.generate(data);

        int format = TriangleStripArray.COORDINATES |
                     TriangleStripArray.NORMALS;

        TriangleStripArray geometry =
            new TriangleStripArray(data.vertexCount, format, data.stripCounts);

        geometry.setCoordinates(0, data.coordinates);
        geometry.setNormals(0, data.normals);
        geometry.setUserData(data);

        setCapability(ALLOW_GEOMETRY_WRITE);

        setAppearance(app);
        setGeometry(geometry);
    }

    /**
     * Change the radius values of the spring to the new values. If the
     * geometry write capability has been turned off, this will not do
     * anything.
     *
     * @param innerRadius The inner radius of the spring
     * @param outerRadius The outer radius of the spring
     */
    public void setDimensions(float innerRadius, float outerRadius)
    {
        if(!getCapability(ALLOW_GEOMETRY_WRITE))
            return;

        generator.setDimensions(innerRadius, outerRadius);
        generator.generate(data);

        int format = TriangleStripArray.COORDINATES |
                     TriangleStripArray.NORMALS;

        TriangleStripArray geometry =
            new TriangleStripArray(data.vertexCount, format, data.stripCounts);

        geometry.setCoordinates(0, data.coordinates);
        geometry.setNormals(0, data.normals);
        geometry.setUserData(data);
        setGeometry(geometry);
    }

    /**
     * Change the loop values of the spring to the new values. If the
     * geometry write capability has been turned off, this will not do
     * anything.
     *
     * @param spacing The spacing between loops
     * @param lc The loop count
     */
    public void setLoopDimensions(float spacing, int lc)
    {
        if(!getCapability(ALLOW_GEOMETRY_WRITE))
            return;

        generator.setLoopDimensions(spacing, lc);
        generator.generate(data);

        int format = TriangleStripArray.COORDINATES |
                     TriangleStripArray.NORMALS;

        TriangleStripArray geometry =
            new TriangleStripArray(data.vertexCount, format, data.stripCounts);

        geometry.setCoordinates(0, data.coordinates);
        geometry.setNormals(0, data.normals);
        geometry.setUserData(data);
        setGeometry(geometry);
    }

    /**
     * Set the facet count of the spring to the new value. If the geometry
     * write capability has been turned off, this will not do anything.
     *
     * @param inner The number of faces to use around the inner radius
     * @param outer The number of faces to use around the outer radius
     */
    public void setFacetCount(int inner, int outer)
    {
        if(!getCapability(ALLOW_GEOMETRY_WRITE))
            return;

        generator.setFacetCount(inner, outer);
        generator.generate(data);

        int format = TriangleStripArray.COORDINATES |
                     TriangleStripArray.NORMALS;

        TriangleStripArray geometry =
            new TriangleStripArray(data.vertexCount, format, data.stripCounts);

        geometry.setCoordinates(0, data.coordinates);
        geometry.setNormals(0, data.normals);
        geometry.setUserData(data);
        setGeometry(geometry);
    }
}