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
 * A test case to check the functionality of the BoxGenerator implementation.
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
public class TestBoxGenerator extends TestCase
{
    /** A non-standard box shape for testing */
    private static final float[] TEST_BOX = {0.4f, 1, 6.8f};

    /** The interpolator we are testing */
    private BoxGenerator generator;

    /**
     * Create an instance of the test case for this particular test
     * name.
     *
     * @param name The name of the test method to be run
     */
    public TestBoxGenerator(String name)
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
        suite.addTest(new TestBoxGenerator("testCreate"));
        suite.addTest(new TestBoxGenerator("testCoordinateArray"));
        suite.addTest(new TestBoxGenerator("testNormalArray"));

        return suite;
    }

    /**
     * Test that we can create the Box generator with different constructors
     * and have it give us the right information for vertex counts.
     */
    public void testCreate()
    {
        // test the default box is 2, 2, 2
        generator = new BoxGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLES;

        generator.generate(data);

        assertEquals("Default box vertex count is wrong",
                     36,
                     data.vertexCount);

        float[] dimensions = generator.getDimensions();

        assertEquals("Default box width wrong",  2, dimensions[0], 0);
        assertEquals("Default box height wrong", 2, dimensions[1], 0);
        assertEquals("Default box depth wrong",  2, dimensions[2], 0);

        // Now test changing the dimension on an existing box
        generator.setDimensions(TEST_BOX[0], TEST_BOX[1], TEST_BOX[2]);
        data.coordinates = null;
        generator.generate(data);

        assertEquals("Dimensions vertex count is wrong",
                     36,
                     data.vertexCount);

        dimensions = generator.getDimensions();

        assertEquals("Dimensions width wrong",  TEST_BOX[0], dimensions[0], 0);
        assertEquals("Dimensions height wrong", TEST_BOX[1], dimensions[1], 0);
        assertEquals("Dimensions depth wrong",  TEST_BOX[2], dimensions[2], 0);

        // test the default box is 2, 2, 2
        generator = new BoxGenerator(TEST_BOX[0], TEST_BOX[1], TEST_BOX[2]);
        generator.generate(data);

        assertEquals("Test box vertex count is wrong",
                     36,
                     data.vertexCount);

        dimensions = generator.getDimensions();

        assertEquals("Test box width wrong",  TEST_BOX[0], dimensions[0], 0);
        assertEquals("Test box height wrong", TEST_BOX[1], dimensions[1], 0);
        assertEquals("Test box depth wrong",  TEST_BOX[2], dimensions[2], 0);
    }

    /**
     * Test that the size of the array generated for coordinates is correct.
     * This also makes sure that the calculation routines do not generate
     * errors either
     */
    public void testCoordinateArray()
    {
        // test the default box is 2, 2, 2
        generator = new BoxGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLES;

        generator.generate(data);

        int vertices = data.vertexCount;
        float[] coords = data.coordinates;

        assertEquals("Default box coordinate length wrong",
                     vertices * 3,
                     coords.length);


        generator.setDimensions(TEST_BOX[0], TEST_BOX[1], TEST_BOX[2]);

        data.coordinates = null;

        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.coordinates;

        assertEquals("Dimensioned box coordinate length wrong",
                     vertices * 3,
                     coords.length);

        generator = new BoxGenerator(TEST_BOX[0], TEST_BOX[1], TEST_BOX[2]);

        data.coordinates = null;

        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.coordinates;

        assertEquals("Test box coordinate length wrong",
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
        // test the default box is 2, 2, 2
        generator = new BoxGenerator();

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLES;
        data.geometryComponents = GeometryData.NORMAL_DATA;

        generator.generate(data);

        int vertices = data.vertexCount;
        float[] coords = data.normals;

        assertEquals("Default box normal length wrong",
                     vertices * 3,
                     coords.length);


        generator.setDimensions(TEST_BOX[0], TEST_BOX[1], TEST_BOX[2]);

        data.normals = null;

        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.normals;

        assertEquals("Dimensioned box normal length wrong",
                     vertices * 3,
                     coords.length);

        generator = new BoxGenerator(TEST_BOX[0], TEST_BOX[1], TEST_BOX[2]);

        data.normals = null;

        generator.generate(data);

        vertices = data.vertexCount;
        coords = data.normals;

        assertEquals("Test box normal length wrong",
                     vertices * 3,
                     coords.length);
    }

    /**
     * Main method to kick everything off with.
     */
    public static void main(String[] argv)
    {
        TestRunner.run(suite());
    }
}

