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
import javax.vecmath.Point3f;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * A test case to check the functionality of the TorusGenerator implementation.
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
public class TestTorusGenerator extends TestCase
{
    /** A non-standard inner outer radius for testing */
    private static final float TEST_INNER_RADIUS = 0.5f;

    /** A non-standard outer outer radius for testing */
    private static final float TEST_OUTER_RADIUS = 6.3f;

    /** A list of valid inner facet counts to make sure it generates correctly */
    private static final int[] VALID_INNER_FACETS = { 12, 32, 72 };

    /** A list of valid outer facet counts to make sure it generates correctly */
    private static final int[] VALID_OUTER_FACETS = { 10, 20, 32 };

    /** A list of invalid facet counts to make sure it generates exceptions */
    private static final int[] INVALID_INNER_FACETS = { -5, 0, 2, 11 };

    /** A list of invalid facet counts to make sure it generates exceptions */
    private static final int[] INVALID_OUTER_FACETS = { -5, 0, 2};

    /** The interpolator we are testing */
    private TorusGenerator generator;

    /**
     * Create an instance of the test case for this particular test
     * name.
     *
     * @param name The name of the test method to be run
     */
    public TestTorusGenerator(String name)
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
        suite.addTest(new TestTorusGenerator("testCreate"));
        suite.addTest(new TestTorusGenerator("testCoordinateArray"));
        suite.addTest(new TestTorusGenerator("testNormalArray"));
        suite.addTest(new TestTorusGenerator("testInvalidFacets"));
        suite.addTest(new TestTorusGenerator("testValidFacets"));

        return suite;
    }

    /**
     * Test that we can create the Torus generator with different constructors
     * and have it give us the right information for vertex counts.
     */
    public void testCreate()
    {
        // test the default sphere is 1 outer radius. This should give
        // sides: 16 facets * 16 facets * 4 vertex per facet
        // total => 1024 vertices
        generator = new TorusGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.QUADS;

        generator.generate(data);

        assertEquals("Default torus vertex count",
                     1024,
                     data.vertexCount);

        float[] dimensions = generator.getDimensions();

        assertEquals("Default torus inner radius", 0.25f, dimensions[0], 0);
        assertEquals("Default torus outer radius", 1, dimensions[1], 0);

        // Now test changing the dimension on an existing torus
        generator.setDimensions(TEST_INNER_RADIUS, TEST_OUTER_RADIUS);

        data.coordinates = null;
        generator.generate(data);

        assertEquals("Dimensioned vertex count is wrong",
                     1024,
                     data.vertexCount);

        dimensions = generator.getDimensions();

        assertEquals("Dimensioned torus outer radius wrong",
                     TEST_INNER_RADIUS,
                     dimensions[0],
                     0);
        assertEquals("Dimensioned torus inner radius wrong",
                     TEST_OUTER_RADIUS,
                     dimensions[1],
                     0);

        // test the default torus is 2, 2, 2
        generator = new TorusGenerator(TEST_INNER_RADIUS, TEST_OUTER_RADIUS);

        data.coordinates = null;
        generator.generate(data);

        assertEquals("Test torus vertex count is wrong",
                     1024,
                     data.vertexCount);

        dimensions = generator.getDimensions();

        assertEquals("Test torus outer radius wrong",
                     TEST_INNER_RADIUS,
                     dimensions[0],
                     0);
        assertEquals("Test torus inner radius wrong",
                     TEST_OUTER_RADIUS,
                     dimensions[1],
                     0);
    }

    /**
     * Test that the size of the array generated for coordinates is correct.
     * This also makes sure that the calculation routines do not generate
     * errors either
     */
    public void testCoordinateArray()
    {
        // test the default torus is
        generator = new TorusGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.QUADS;

        generator.generate(data);

        int vertices = data.vertexCount;
        float[] coords = data.coordinates;

        assertEquals("Default torus coordinate length wrong",
                     vertices * 3,
                     coords.length);

        generator.setDimensions(TEST_INNER_RADIUS, TEST_OUTER_RADIUS);

        data.coordinates = null;
        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.coordinates;

        assertEquals("Dimensioned torus coordinate length wrong",
                     vertices * 3,
                     coords.length);

        generator = new TorusGenerator(TEST_INNER_RADIUS, TEST_OUTER_RADIUS);

        data.coordinates = null;
        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.coordinates;

        assertEquals("Test torus coordinate length wrong",
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
        // test the default torus is 2, 2, 2
        generator = new TorusGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.QUADS;
        data.geometryComponents = GeometryData.NORMAL_DATA;

        generator.generate(data);

        int vertices = data.vertexCount;
        float[] coords = data.normals;

        assertEquals("Default torus normal length wrong",
                     vertices * 3,
                     coords.length);


        generator.setDimensions(TEST_INNER_RADIUS, TEST_OUTER_RADIUS);

        data.normals = null;
        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.normals;

        assertEquals("Dimensioned torus normal length wrong",
                     vertices * 3,
                     coords.length);

        generator = new TorusGenerator(TEST_INNER_RADIUS, TEST_OUTER_RADIUS);

        data.normals = null;
        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.normals;

        assertEquals("Test torus normal length wrong",
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
        for(i = 0; i < INVALID_INNER_FACETS.length; i++)
        {
            try
            {
                generator = new TorusGenerator(INVALID_INNER_FACETS[i], 4);
                fail("Did not detect bad inner facet count on construction " +
                     INVALID_INNER_FACETS[i]);
            }
            catch(IllegalArgumentException iae)
            {
            }
        }

        for(i = 0; i < INVALID_OUTER_FACETS.length; i++)
        {
            try
            {
                generator = new TorusGenerator(4, INVALID_OUTER_FACETS[i]);
                fail("Did not detect bad outer facet count on construction " +
                     INVALID_OUTER_FACETS[i]);
            }
            catch(IllegalArgumentException iae)
            {
            }
        }

        // Same thing again but using the setFacet method
        generator = new TorusGenerator();
        for(i = 0; i < INVALID_INNER_FACETS.length; i++)
        {
            try
            {
                generator.setFacetCount(INVALID_INNER_FACETS[i], 4);
                fail("Did not detect invalid inner facet count on set " +
                     INVALID_OUTER_FACETS[i]);
            }
            catch(IllegalArgumentException iae)
            {
            }
        }

        for(i = 0; i < INVALID_OUTER_FACETS.length; i++)
        {
            try
            {
                generator.setFacetCount(4, INVALID_OUTER_FACETS[i]);
                fail("Did not detect invalid outer facet count on set " +
                     INVALID_OUTER_FACETS[i]);
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
        float[] coords;

        assertEquals("Valid inner & outer facet lengths",
                     VALID_INNER_FACETS.length,
                     VALID_OUTER_FACETS.length);

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.QUADS;

        // Test with a negative value, zero and value less than 3. All should
        // generate exceptions.
        for(i = 0; i < VALID_INNER_FACETS.length; i++)
        {
            generator = new TorusGenerator(VALID_INNER_FACETS[i],
                                           VALID_OUTER_FACETS[i]);

            data.coordinates = null;
            generator.generate(data);

            reqd_count = VALID_INNER_FACETS[i] * VALID_OUTER_FACETS[i] * 4;

            vtx_count = data.vertexCount;
            assertEquals("Construct vertex count for inner facet" +
                           VALID_INNER_FACETS[i],
                         reqd_count,
                         vtx_count);

            // Now generate the vertices and look at the array
            reqd_count = reqd_count * 3;
            coords = data.coordinates;
            assertEquals("Generated initial vertex count for inner facet" +
                           VALID_INNER_FACETS[i],
                         reqd_count,
                         coords.length);

        }


        // Same thing again but using the setFacet method
        generator = new TorusGenerator();
        for(i = 0; i < VALID_INNER_FACETS.length; i++)
        {
            generator.setFacetCount(VALID_INNER_FACETS[i],
                                    VALID_OUTER_FACETS[i]);
            reqd_count = VALID_INNER_FACETS[i] * VALID_OUTER_FACETS[i] * 4;

            data.coordinates = null;
            generator.generate(data);

            vtx_count = data.vertexCount;
            assertEquals("Construct vertex count for inner facet" +
                           VALID_INNER_FACETS[i],
                         reqd_count,
                         vtx_count);

            // Now generate the vertices and look at the array
            reqd_count = reqd_count * 3;
            coords = data.coordinates;
            assertEquals("Generated initial vertex count for inner facet" +
                           VALID_INNER_FACETS[i],
                         reqd_count,
                         coords.length);

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

