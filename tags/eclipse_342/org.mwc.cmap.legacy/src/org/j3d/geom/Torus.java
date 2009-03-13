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
 * A simple torus that uses triangle strips.
 * <p>
 *
 * The created torus does not have any capabilities set except for the
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
public class Torus extends Shape3D
{
    /** The default outer radius of the torus */
    private static final float DEFAULT_ORADIUS = 1.0f;

    /** The default inner radius of the torus */
    private static final float DEFAULT_IRADIUS = 0.25f;

    /** Default number of segments used in the outer radius */
    private static final int DEFAULT_OFACETS = 16;

    /** Default number of segments used in the inner radius */
    private static final int DEFAULT_IFACETS = 16;

    /** The generator used to modify the geometry */
    private TorusGenerator generator;

    /** Data used to regenerate the torus */
    private GeometryData data;

    /**
     * Construct a default torus with no appearance set. The default size
     * of the torus is: <BR>
     * Outer radius: 2.0<BR>
     * Inner radius: 1.0<BR>
     * Outer radius Faces:  16<BR>
     * Inner radius Faces:  16<BR>
     */
    public Torus()
    {
        this(DEFAULT_ORADIUS,
             DEFAULT_IRADIUS,
             DEFAULT_OFACETS,
             DEFAULT_IFACETS,
             null);
    }

    /**
     * Construct a default torus with the given appearance. The default size
     * of the torus is: <BR>
     * Outer radius: 2.0<BR>
     * Inner radius: 1.0<BR>
     * Outer radius Faces:  16<BR>
     * Inner radius Faces:  16<BR>
     *
     * @param app The appearance to use
     */
    public Torus(Appearance app)
    {
        this(DEFAULT_IRADIUS,
             DEFAULT_ORADIUS,
             DEFAULT_IFACETS,
             DEFAULT_OFACETS,
             app);
    }

    /**
     * Construct a default torus with no appearance set and a custom
     * number of faces. <BR>
     * Outer radius: 2.0<BR>
     * Inner radius: 1.0<BR>
     *
     * @param inner The number of faces to use around the inner radius
     * @param outer The number of faces to use around the outer radius
     */
    public Torus(int inner, int outer)
    {
        this(DEFAULT_IRADIUS, DEFAULT_ORADIUS, inner, outer, null);
    }

    /**
     * Construct a default torus with no appearance set. The height and
     * radius as set to the new value and uses the default face count of:<BR>
     * Outer radius Faces:  16<BR>
     * Inner radius Faces:  16<BR>
     *
     * @param innerRadius The inner radius of the torus
     * @param outerRadius The outer radius of the torus
     */
    public Torus(float innerRadius, float outerRadius)
    {
        this(innerRadius, outerRadius, DEFAULT_IFACETS, DEFAULT_OFACETS, null);
    }

    /**
     * Construct a default torus with the given appearance and a custom
     * number of faces. <BR>
     * Outer radius: 2.0<BR>
     * Inner radius: 1.0<BR>
     *
     * @param inner The number of faces to use around the inner radius
     * @param outer The number of faces to use around the outer radius
     * @param app The appearance to use
     */
    public Torus(int inner, int outer, Appearance app)
    {
        this(DEFAULT_ORADIUS, DEFAULT_IRADIUS, inner, outer, app);
    }

    /**
     * Construct a default torus with the given appearance. The height and
     * radius as set to the new value and uses the default face count of
     * Outer radius Faces:  16<BR>
     * Inner radius Faces:  16<BR>
     *
     * @param innerRadius The inner radius of the torus
     * @param outerRadius The outer radius of the torus
     * @param app The appearance to use
     */
    public Torus(float innerRadius, float outerRadius, Appearance app)
    {
        this(innerRadius, outerRadius, DEFAULT_IFACETS, DEFAULT_OFACETS, app);
    }

    /**
     * Construct a torus with all the values customisable.
     *
     * @param innerRadius The inner radius of the torus
     * @param outerRadius The outer radius of the torus
     * @param inner The number of faces to use around the inner radius
     * @param outer The number of faces to use around the outer radius
     * @param app The appearance to use
     */
    public Torus(float innerRadius,
                 float outerRadius,
                 int inner,
                 int outer,
                 Appearance app)
    {
        data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLE_STRIPS;
        data.geometryComponents = GeometryData.NORMAL_DATA;

        generator = new TorusGenerator(innerRadius, outerRadius, inner, outer);

        generator.generate(data);

        int format = TriangleStripArray.COORDINATES |
                     TriangleStripArray.NORMALS;

        TriangleStripArray geometry =
            new TriangleStripArray(data.vertexCount, format, data.stripCounts);

        geometry.setCoordinates(0, data.coordinates);
        geometry.setNormals(0, data.normals);
        geometry.setUserData(data);

        setCapability(ALLOW_GEOMETRY_WRITE);

        if(app != null)
            setAppearance(app);

        setGeometry(geometry);
    }

    /**
     * Change the radius and height of the torus to the new values. If the
     * geometry write capability has been turned off, this will not do
     * anything.
     *
     * @param innerRadius The inner radius of the torus
     * @param outerRadius The outer radius of the torus
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
     * Set the facet count of the torus to the new value. If the geometry
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