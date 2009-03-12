/*****************************************************************************
 *                      J3D.org Copyright (c) 2000
 *                           Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom;

// Standard imports
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * A test case to check the functionality of the SphereGenerator implementation.
 * <p>
 *
 * The test aims to check the basic calculation routines to make sure there
 * are no array overruns and the geometry is updated correctly. It does not
 * do a check on the coordinates generated. That is a visual test and is
 * performed by the example code.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class TestSphereGenerator extends TestCase
{
    /** A non-standard radius for testing */
    private static final float TEST_RADIUS = 0.5f;

    /** A list of valid facet counts to make sure it generates correctly */
    private static final int[] VALID_FACETS = { 12, 32, 72 };

    /** A list of invalid facet counts to make sure it generates exceptions */
    private static final int[] INVALID_FACETS = { -5, 0, 2, 7 };

    /** The interpolator we are testing */
    private SphereGenerator generator;

    /**
     * Create an instance of the test case for this particular test
     * name.
     *
     * @param name The name of the test method to be run
     */
    public TestSphereGenerator(String name)
    {
        super(name);
    }

    /**
     * Fetch the suite of tests for this test class to perform.
     *
     * @return A collection of all the tests to be run
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestSphereGenerator("testCreate"));
        suite.addTest(new TestSphereGenerator("testCoordinateArray"));
        suite.addTest(new TestSphereGenerator("testNormalArray"));
        suite.addTest(new TestSphereGenerator("testInvalidFacets"));
        suite.addTest(new TestSphereGenerator("testValidFacets"));

        return suite;
    }

    /**
     * Test that we can create the Sphere generator with different constructors
     * and have it give us the right information for vertex counts.
     */
    public void testCreate()
    {
        // test the default sphere is 1 radius. This should give
        // sides: 16 facets * 16 facets * 6 vertex per facet
        // total => 1024 vertices
        generator = new SphereGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLES;

        generator.generate(data);

        assertTrue("Default sphere is missing the bottom", !generator.isHalf());
        assertEquals("Default sphere vertex count is wrong",
                     1536,
                     data.vertexCount);

        float radius = generator.getDimension();

        assertEquals("Default sphere radius wrong", 1, radius, 0);

        // Now test changing the dimension on an existing sphere
        generator.setDimensions(TEST_RADIUS, false);
        data.coordinates = null;
        generator.generate(data);

        assertTrue("Dimensioned sphere is missing the bottom",
               !generator.isHalf());
        assertEquals("Dimensioned vertex count is wrong",
                     1536,
                     data.vertexCount);

        radius = generator.getDimension();

        assertEquals("Dimensioned sphere radius wrong",
                     TEST_RADIUS,
                     radius,
                     0);

        // check that the bottom flag is set independently
        generator.setDimensions(TEST_RADIUS, true);

        assertTrue("Dimensioned sphere bottom check is wrong",
               generator.isHalf());

        // test the sphere is radius 0.5
        generator = new SphereGenerator(TEST_RADIUS);
        data.coordinates = null;
        generator.generate(data);

        assertTrue("Test sphere is missing the bottom", !generator.isHalf());
        assertEquals("Test sphere vertex count is wrong",
                     1536,
                     data.vertexCount);

        radius = generator.getDimension();

        assertEquals("Test sphere radius wrong",
                     TEST_RADIUS,
                     radius,
                     0);
    }

    /**
     * Test that the size of the array generated for coordinates is correct.
     * This also makes sure that the calculation routines do not generate
     * errors either
     */
    public void testCoordinateArray()
    {
        // test the default sphere is radius 1
        generator = new SphereGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLES;

        generator.generate(data);

        int vertices = data.vertexCount;
        float[] coords = data.coordinates;

        assertEquals("Default sphere coordinate length wrong",
                     vertices * 3,
                     coords.length);

        generator.setDimensions(TEST_RADIUS, false);

        data.coordinates = null;

        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.coordinates;

        assertEquals("Dimensioned sphere coordinate length wrong",
                     vertices * 3,
                     coords.length);

        generator = new SphereGenerator(TEST_RADIUS);

        data.coordinates = null;

        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.coordinates;

        assertEquals("Test sphere coordinate length wrong",
                     vertices * 3,
                     coords.length);

        // Now check the same things again, but without the bottom
        generator = new SphereGenerator(TEST_RADIUS, true);

        int old_vertices = vertices;

        data.coordinates = null;

        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.coordinates;

        assertEquals("No-bottom sphere vertex count wrong",
                     old_vertices / 2,
                     vertices);

        assertEquals("No-bottom sphere coordinate length wrong",
                     vertices * 3,
                     coords.length);
    }

    /**
     * Test that the size of the array generated for coordinates is correct.
     * This also makes sure that the calculation routines do not generate
     * errors either
     */
    public void testNormalArray()
    {
        // test the default sphere is 2, 2, 2
        generator = new SphereGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLES;
        data.geometryComponents = GeometryData.NORMAL_DATA;

        generator.generate(data);

        int vertices = data.vertexCount;
        float[] coords = data.normals;

        assertEquals("Default sphere normal length wrong",
                     vertices * 3,
                     coords.length);


        generator.setDimensions(TEST_RADIUS, false);

        data.normals = null;

        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.normals;

        assertEquals("Dimensioned sphere normal length wrong",
                     vertices * 3,
                     coords.length);

        generator = new SphereGenerator(TEST_RADIUS);

        data.normals = null;

        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.normals;

        assertEquals("Test sphere normal length wrong",
                     vertices * 3,
                     coords.length);

        // Now check the same things again, but without the bottom
        generator = new SphereGenerator(TEST_RADIUS, true);

        int old_vertices = vertices;

        data.normals = null;

        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.normals;

        assertEquals("No-bottom sphere normal vertex count wrong",
                     old_vertices / 2,
                     vertices);

        assertEquals("No-bottom sphere normal length wrong",
                     vertices * 3,
                     coords.length);
    }

    /**
     * Test to see how the facet handling works. Makes sure that the correct
     * exceptions are generated when setting or changing the facet count.
     */
    public void testInvalidFacets()
    {
        int i;
        // Test with a negative value, zero and value less than 3. All should
        // generate exceptions.
        for(i = 0; i < INVALID_FACETS.length; i++)
        {
            try
            {
                generator = new SphereGenerator(1, INVALID_FACETS[i]);
                fail("Did not detect bad facet count on construction " +
                     INVALID_FACETS[i]);
            }
            catch(IllegalArgumentException iae)
            {
            }
        }


        // Same thing again but using the setFacet method
        generator = new SphereGenerator();
        for(i = 0; i < INVALID_FACETS.length; i++)
        {
            try
            {
                generator.setFacetCount(INVALID_FACETS[i]);
                fail("Did not detect invalid facet count on set " +
                     INVALID_FACETS[i]);
            }
            catch(IllegalArgumentException iae)
            {
            }
        }
    }

    /**
     * Test to makes sure the vertex count has been updated properly and
     * the generated array lengths are correct.
     */
    public void testValidFacets()
    {
        int i;
        int reqd_count;
        int vtx_count;
        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLES;

        // Test with a negative value, zero and value less than 3. All should
        // generate exceptions.
        for(i = 0; i < VALID_FACETS.length; i++)
        {
            generator = new SphereGenerator(1, VALID_FACETS[i]);
            reqd_count = VALID_FACETS[i] * VALID_FACETS[i] * 6;

            data.coordinates = null;
            generator.generate(data);

            vtx_count = data.vertexCount;
            assertEquals("Construct vertex count wrong for " + VALID_FACETS[i],
                         reqd_count,
                         vtx_count);

            // Now generate the vertices and look at the array
            reqd_count = reqd_count * 3;
            assertEquals("Generated initial vertex count wrong for " +
                         VALID_FACETS[i],
                         reqd_count,
                         data.coordinates.length);

        }


        // Same thing again but using the setFacet method
        generator = new SphereGenerator();
        for(i = 0; i < VALID_FACETS.length; i++)
        {
            generator.setFacetCount(VALID_FACETS[i]);
            reqd_count = VALID_FACETS[i] * VALID_FACETS[i] * 6;
            data.coordinates = null;
            generator.generate(data);

            vtx_count = data.vertexCount;
            assertEquals("Set vertex count wrong for " + VALID_FACETS[i],
                         reqd_count,
                         vtx_count);

            reqd_count = reqd_count * 3;
            assertEquals("Generated set vertex count wrong for " +
                         VALID_FACETS[i],
                         reqd_count,
                         data.coordinates.length);
        }
    }

    /**
     * Main method to kick everything off with.
     */
    public static void main(String[] argv)
    {
        TestRunner.run(suite());
    }
}

