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
 * A test case to check the functionality of the SpringGenerator implementation.
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
public class TestSpringGenerator extends TestCase
{
    /** A non-standard inner radius for testing */
    private static final float TEST_INNER_RADIUS = 0.5f;

    /** A non-standard outer radius for testing */
    private static final float TEST_OUTER_RADIUS = 6.3f;

    /** A non-standard loop spacing for testing */
    private static final float TEST_LOOP_SPACING = 3.3f;

    /** A non-standard loop count for testing */
    private static final int TEST_LOOP_COUNT = 7;

    /** A list of valid inner facet counts to make sure it generates correctly */
    private static final int[] VALID_INNER_FACETS = { 12, 32, 72 };

    /** A list of valid outer facet counts to make sure it generates correctly */
    private static final int[] VALID_OUTER_FACETS = { 10, 20, 32 };

    /** A list of invalid facet counts to make sure it generates exceptions */
    private static final int[] INVALID_INNER_FACETS = { -5, 0, 2, 10 };

    /** A list of invalid facet counts to make sure it generates exceptions */
    private static final int[] INVALID_OUTER_FACETS = { -5, 0, 2};

    /** The interpolator we are testing */
    private SpringGenerator generator;

    /**
     * Create an instance of the test case for this particular test
     * name.
     *
     * @param name The name of the test method to be run
     */
    public TestSpringGenerator(String name)
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
        suite.addTest(new TestSpringGenerator("testCreate"));
        suite.addTest(new TestSpringGenerator("testCoordinateArray"));
        suite.addTest(new TestSpringGenerator("testNormalArray"));
        suite.addTest(new TestSpringGenerator("testInvalidFacets"));
        suite.addTest(new TestSpringGenerator("testValidFacets"));

        return suite;
    }

    /**
     * Test that we can create the Spring generator with different constructors
     * and have it give us the right information for vertex counts.
     */
    public void testCreate()
    {
        // test the default spring is 1 outer radius. This should give
        // sides: 16 facets * 16 facets * 4 vertex per facet * 4 loops
        // total => 4096 vertices
        generator = new SpringGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.QUADS;

        generator.generate(data);

        assertEquals("Default spring vertex count",
                     4096,
                     data.vertexCount);

        float[] dimensions = generator.getDimensions();

        assertEquals("Default spring inner radius", 0.25f, dimensions[0], 0);
        assertEquals("Default spring outer radius", 1, dimensions[1], 0);
        assertEquals("Default loop spacing", 1, dimensions[2], 0);
        assertEquals("Default loop count", 4, (int)dimensions[3]);

        // Now test changing the dimension on an existing spring
        generator.setDimensions(TEST_INNER_RADIUS, TEST_OUTER_RADIUS);

        data.coordinates = null;
        generator.generate(data);

        assertEquals("Dimensioned vertex count",
                     4096,
                     data.vertexCount);

        dimensions = generator.getDimensions();

        assertEquals("Dimensioned spring outer radius",
                     TEST_INNER_RADIUS,
                     dimensions[0],
                     0);
        assertEquals("Dimensioned spring inner radius",
                     TEST_OUTER_RADIUS,
                     dimensions[1],
                     0);
        assertEquals("Dimensioned loop spacing", 1, dimensions[2], 0);
        assertEquals("Dimensioned loop count", 4, (int)dimensions[3]);

        // Now try modifying the loop count
        // sides: 16 facets * 16 facets * 4 vertex per facet * 7 loops
        // total => 7168 vertices
        generator.setLoopDimensions(TEST_LOOP_SPACING, TEST_LOOP_COUNT);

        data.coordinates = null;
        generator.generate(data);

        assertEquals("Looped spring vertex count is wrong",
                     7168,
                     data.vertexCount);

        dimensions = generator.getDimensions();

        assertEquals("Looped spring outer radius",
                     TEST_INNER_RADIUS,
                     dimensions[0],
                     0);
        assertEquals("Looped spring inner radius",
                     TEST_OUTER_RADIUS,
                     dimensions[1],
                     0);
        assertEquals("Looped loop spacing",
                     TEST_LOOP_SPACING,
                     dimensions[2],
                     0);
        assertEquals("Looped loop count", TEST_LOOP_COUNT, (int)dimensions[3]);

        // test the non-standard spring size
        generator = new SpringGenerator(TEST_INNER_RADIUS,
                                        TEST_OUTER_RADIUS,
                                        TEST_LOOP_SPACING,
                                        TEST_LOOP_COUNT);

        data.coordinates = null;
        generator.generate(data);

        assertEquals("Test spring vertex count is wrong",
                     7168,
                     data.vertexCount);

        dimensions = generator.getDimensions();

        assertEquals("Test spring outer radius wrong",
                     TEST_INNER_RADIUS,
                     dimensions[0],
                     0);
        assertEquals("Test spring inner radius wrong",
                     TEST_OUTER_RADIUS,
                     dimensions[1],
                     0);
        assertEquals("Test loop spacing",
                     TEST_LOOP_SPACING,
                     dimensions[2],
                     0);
        assertEquals("Test loop count", TEST_LOOP_COUNT, (int)dimensions[3]);
    }

    /**
     * Test that the size of the array generated for coordinates is correct.
     * This also makes sure that the calculation routines do not generate
     * errors either
     */
    public void testCoordinateArray()
    {
        // test the default spring is
        generator = new SpringGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.QUADS;

        generator.generate(data);

        int vertices = data.vertexCount;
        float[] coords = data.coordinates;

        assertEquals("Default spring coordinate length wrong",
                     vertices * 3,
                     coords.length);

        generator.setDimensions(TEST_INNER_RADIUS, TEST_OUTER_RADIUS);

        data.coordinates = null;
        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.coordinates;

        assertEquals("Dimensioned spring coordinate length wrong",
                     vertices * 3,
                     coords.length);

        generator = new SpringGenerator(TEST_INNER_RADIUS, TEST_OUTER_RADIUS);

        data.coordinates = null;
        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.coordinates;

        assertEquals("Test spring coordinate length wrong",
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
        // test the default spring is 2, 2, 2
        generator = new SpringGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.QUADS;
        data.geometryComponents = GeometryData.NORMAL_DATA;

        generator.generate(data);

        int vertices = data.vertexCount;
        float[] coords = data.normals;

        assertEquals("Default spring normal length wrong",
                     vertices * 3,
                     coords.length);


        generator.setDimensions(TEST_INNER_RADIUS, TEST_OUTER_RADIUS);

        data.normals = null;
        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.normals;

        assertEquals("Dimensioned spring normal length wrong",
                     vertices * 3,
                     coords.length);

        generator = new SpringGenerator(TEST_INNER_RADIUS, TEST_OUTER_RADIUS);

        data.normals = null;
        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.normals;

        assertEquals("Test spring normal length wrong",
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
                generator = new SpringGenerator(INVALID_INNER_FACETS[i], 4);
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
                generator = new SpringGenerator(4, INVALID_OUTER_FACETS[i]);
                fail("Did not detect bad outer facet count on construction " +
                     INVALID_OUTER_FACETS[i]);
            }
            catch(IllegalArgumentException iae)
            {
            }
        }

        // Same thing again but using the setFacet method
        generator = new SpringGenerator();
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

        generator = new SpringGenerator();
        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.QUADS;

        generator.generate(data);

        assertEquals("Valid inner & outer facet lengths",
                     VALID_INNER_FACETS.length,
                     VALID_OUTER_FACETS.length);

        // Test with a negative value, zero and value less than 3. All should
        // generate exceptions.
        for(i = 0; i < VALID_INNER_FACETS.length; i++)
        {
            generator = new SpringGenerator(VALID_INNER_FACETS[i],
                                           VALID_OUTER_FACETS[i]);
            // Facet counts * 4 vertex per facet * 4 loops
            reqd_count = VALID_INNER_FACETS[i] * VALID_OUTER_FACETS[i] * 16;

            data.coordinates = null;
            generator.generate(data);

            vtx_count = data.vertexCount;
            assertEquals("Construct vertex count for inner facet " +
                           VALID_INNER_FACETS[i],
                         reqd_count,
                         vtx_count);

            // Now generate the vertices and look at the array
            reqd_count = reqd_count * 3;
            coords = data.coordinates;
            assertEquals("Generated initial vertex count for inner facet " +
                           VALID_INNER_FACETS[i],
                         reqd_count,
                         coords.length);

        }


        // Same thing again but using the setFacet method
        generator = new SpringGenerator();
        for(i = 0; i < VALID_INNER_FACETS.length; i++)
        {
            generator.setFacetCount(VALID_INNER_FACETS[i],
                                    VALID_OUTER_FACETS[i]);
            // Facet counts * 4 vertex per facet * 4 loops

            reqd_count = VALID_INNER_FACETS[i] * VALID_OUTER_FACETS[i] * 16;
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

