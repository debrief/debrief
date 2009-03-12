/*****************************************************************************
 *                      J3D.org Copyright (c) 2000
 *                           Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util.interpolator;

import javax.vecmath.Point3f;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * A test case to check the functionality of the PositionInterpolator
 * implementation.
 * <p>
 *
 * The test aims to check insertion and key value generation of the
 * interpolator.
 *
 * @author Justin Couc
 * @version $Revision: 1.1.1.1 $
 */
public class TestPositionInterpolator extends TestCase
{
    /** Keys to be used for testing */
    private static float[] keys = { 0.1f, 0.4f, 5f };

    /** Values to correspond to the test keys */
    private static float[][] values = {
        {0, 0, 0},
        {1, 1, 1},
        {1, 5, 2}
    };

    /** The interpolator we are testing */
    private PositionInterpolator interpolator;

    /**
     * Create an instance of the test case for this particular test
     * name.
     *
     * @param name The name of the test method to be run
     */
    public TestPositionInterpolator(String name)
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
        suite.addTest(new TestPositionInterpolator("testCreateFloat"));
        suite.addTest(new TestPositionInterpolator("testCreatePoint"));
        suite.addTest(new TestPositionInterpolator("testValueInsertFloat"));
        suite.addTest(new TestPositionInterpolator("testValueInsertPoint"));
        suite.addTest(new TestPositionInterpolator("testKeyGenFloat"));
        suite.addTest(new TestPositionInterpolator("testKeyGenPoint"));
        suite.addTest(new TestPositionInterpolator("testClamping"));

        return suite;
    }

    /**
     * Pre-test instance setup code. We check here to make sure that the key
     * and value arrays are the same length just in case someone has stuffed
     * it up when playing with this code.
     */
    public void setUp()
    {
        assertEquals("Keys and values arrays are not the same size",
                     keys.length,
                     values.length);

        interpolator = new PositionInterpolator();
    }

    /**
     * Test that we can create a basic array of values using float arrays
     * inserted linearly without generating exceptions.
     */
    public void testCreateFloat()
    {
        int i;
        int num_keys = keys.length;

        for(i = 0; i < num_keys; i++)
            interpolator.addKeyFrame(keys[i],
                                     values[i][0],
                                     values[i][1],
                                     values[i][2]);

        // now fetch these values back again and make sure they are the same
        for(i = 0; i < num_keys; i++)
        {
            float[] vals = interpolator.floatValue(keys[i]);

            assertEquals(i + " X coord not same", values[i][0], vals[0], 0);
            assertEquals(i + " Y coord not same", values[i][1], vals[1], 0);
            assertEquals(i + " Z coord not same", values[i][2], vals[2], 0);
        }
    }

    /**
     * Test that we can create a basic array of values using Point3f
     * inserted linearly without generating exceptions.
     */
    public void testCreatePoint()
    {
        int i;
        int num_keys = keys.length;
        Point3f point;

        for(i = 0; i < num_keys; i++)
        {
            point = new Point3f(values[i]);
            interpolator.addKeyFrame(keys[i], point);
        }

        // now fetch these values back again and make sure they are the same
        for(i = 0; i < num_keys; i++)
        {
            Point3f vals = interpolator.pointValue(keys[i]);

            assertEquals(i + " X coord not same", values[i][0], vals.x, 0);
            assertEquals(i + " Y coord not same", values[i][1], vals.y, 0);
            assertEquals(i + " Z coord not same", values[i][2], vals.z, 0);
        }
    }

    /**
     * Test that we can create an array of values with values being inserted
     * between other values.
     */
    public void testValueInsertFloat()
    {
        int i;
        int num_keys = keys.length;

        assertTrue("Not enough keys ( < 3) to do this test", num_keys > 2);

        interpolator.addKeyFrame(keys[0],
                                 values[0][0],
                                 values[0][1],
                                 values[0][2]);

        interpolator.addKeyFrame(keys[2],
                                 values[2][0],
                                 values[2][1],
                                 values[2][2]);

        interpolator.addKeyFrame(keys[1],
                                 values[1][0],
                                 values[1][1],
                                 values[1][2]);

        // now fetch these values back again and make sure they are the same
        for(i = 0; i < num_keys; i++)
        {
            float[] vals = interpolator.floatValue(keys[i]);

            assertEquals(i + " X coord not same", values[i][0], vals[0], 0);
            assertEquals(i + " Y coord not same", values[i][1], vals[1], 0);
            assertEquals(i + " Z coord not same", values[i][2], vals[2], 0);
        }
    }

    /**
     * Test that we can create an array of values with values being inserted
     * between other values.
     */
    public void testValueInsertPoint()
    {
        int i;
        int num_keys = keys.length;
        Point3f point;

        assertTrue("Not enough keys ( < 3) to do this test", num_keys > 2);

        point = new Point3f(values[0]);
        interpolator.addKeyFrame(keys[0], point);

        point = new Point3f(values[2]);
        interpolator.addKeyFrame(keys[2], point);

        point = new Point3f(values[1]);
        interpolator.addKeyFrame(keys[1], point);

        // now fetch these values back again and make sure they are the same
        for(i = 0; i < num_keys; i++)
        {
            Point3f vals = interpolator.pointValue(keys[i]);

            assertEquals(i + " X coord not same", values[i][0], vals.x, 0);
            assertEquals(i + " Y coord not same", values[i][1], vals.y, 0);
            assertEquals(i + " Z coord not same", values[i][2], vals.z, 0);
        }
    }

    /**
     * Test that we can generate simple values for keys that are in range
     * easily. The earlier tests have made sure that we are returning the
     * right values when the key is exactly equal to one of the end values.
     * Now we are looking at a couple of coordinated points long each axis.
     */
    public void testKeyGenFloat()
    {
        int i;
        int num_keys = keys.length;

        for(i = 0; i < num_keys; i++)
            interpolator.addKeyFrame(keys[i],
                                     values[i][0],
                                     values[i][1],
                                     values[i][2]);

        float mid_key = keys[0] + ((keys[1] - keys[0]) / 2);
        float x_val = values[0][0] + ((values[1][0] - values[0][0]) / 2);
        float y_val = values[0][0] + ((values[1][1] - values[0][1]) / 2);
        float z_val = values[0][0] + ((values[1][2] - values[0][2]) / 2);

        float[] vals = interpolator.floatValue(mid_key);

        assertEquals("1st X coord not same", x_val, vals[0], 0);
        assertEquals("1st Y coord not same", y_val, vals[1], 0);
        assertEquals("1st Z coord not same", z_val, vals[2], 0);

        mid_key = keys[1] + ((keys[2] - keys[1]) / 2);
        x_val = values[1][0] + ((values[2][0] - values[1][0]) / 2);
        y_val = values[1][1] + ((values[2][1] - values[1][1]) / 2);
        z_val = values[1][2] + ((values[2][2] - values[1][2]) / 2);

        vals = interpolator.floatValue(mid_key);

        assertEquals("2nd X coord not same", x_val, vals[0], 0);
        assertEquals("2nd Y coord not same", y_val, vals[1], 0);
        assertEquals("2nd Z coord not same", z_val, vals[2], 0);
    }

    /**
     * Test that we can generate simple values for keys that are in range
     * easily. The earlier tests have made sure that we are returning the
     * right values when the key is exactly equal to one of the end values.
     * Now we are looking at a couple of coordinated points long each axis.
     */
    public void testKeyGenPoint()
    {
        int i;
        int num_keys = keys.length;
        Point3f point;

        for(i = 0; i < num_keys; i++)
        {
            point = new Point3f(values[i]);
            interpolator.addKeyFrame(keys[i], point);
        }

        float mid_key = keys[0] + ((keys[1] - keys[0]) / 2);
        float x_val = values[0][0] + ((values[1][0] - values[0][0]) / 2);
        float y_val = values[0][0] + ((values[1][1] - values[0][1]) / 2);
        float z_val = values[0][0] + ((values[1][2] - values[0][2]) / 2);

        Point3f vals = interpolator.pointValue(mid_key);

        assertEquals("1st X coord not same", x_val, vals.x, 0);
        assertEquals("1st Y coord not same", y_val, vals.y, 0);
        assertEquals("1st Z coord not same", z_val, vals.z, 0);

        mid_key = keys[1] + ((keys[2] - keys[1]) / 2);
        x_val = values[1][0] + ((values[2][0] - values[1][0]) / 2);
        y_val = values[1][1] + ((values[2][1] - values[1][1]) / 2);
        z_val = values[1][2] + ((values[2][2] - values[1][2]) / 2);

        vals = interpolator.pointValue(mid_key);

        assertEquals("2nd X coord not same", x_val, vals.x, 0);
        assertEquals("2nd Y coord not same", y_val, vals.y, 0);
        assertEquals("2nd Z coord not same", z_val, vals.z, 0);
    }

    /**
     * Test that we can generate values that are clamped to the extent values
     * of the interpolator for keys that are out of range to those inserted.
     */
    public void testClamping()
    {
        int i;
        int num_keys = keys.length;

        for(i = 0; i < num_keys; i++)
            interpolator.addKeyFrame(keys[i],
                                     values[i][0],
                                     values[i][1],
                                     values[i][2]);

        // a key value smaller than the smallest key
        float key = keys[0] - 1;

        float[] vals = interpolator.floatValue(key);

        assertEquals("Min X coord not same", values[0][0], vals[0], 0);
        assertEquals("Min Y coord not same", values[0][1], vals[1], 0);
        assertEquals("Min Z coord not same", values[0][2], vals[2], 0);

        // A key value larger than the largest key
        num_keys--;
        key = keys[num_keys] + 1;

        vals = interpolator.floatValue(key);

        assertEquals("Max X coord not same", values[num_keys][0], vals[0], 0);
        assertEquals("Max Y coord not same", values[num_keys][1], vals[1], 0);
        assertEquals("Max Z coord not same", values[num_keys][2], vals[2], 0);
    }

    /**
     * Main method to kick everything off with.
     */
    public static void main(String[] argv)
    {
        TestRunner.run(suite());
    }
}

