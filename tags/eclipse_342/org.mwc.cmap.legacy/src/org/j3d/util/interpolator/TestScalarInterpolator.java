/*****************************************************************************
 *                      J3D.org Copyright (c) 2000
 *                           Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util.interpolator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * A test case to check the functionality of the ScalarInterpolator
 * implementation.
 * <p>
 *
 * The test aims to check insertion and key value generation of the
 * interpolator.
 *
 * @author Justin Couc
 * @version $Revision: 1.1.1.1 $
 */
public class TestScalarInterpolator extends TestCase
{
    /** Keys to be used for testing */
    private static float[] keys = { 0.1f, 0.4f, 5f };

    /** Values to correspond to the test keys */
    private static float[] values = { 1, 5, 2 };

    /** The interpolator we are testing */
    private ScalarInterpolator interpolator;

    /**
     * Create an instance of the test case for this particular test
     * name.
     *
     * @param name The name of the test method to be run
     */
    public TestScalarInterpolator(String name)
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
        suite.addTest(new TestScalarInterpolator("testCreate"));
        suite.addTest(new TestScalarInterpolator("testValueInsert"));
        suite.addTest(new TestScalarInterpolator("testKeyGenLinear"));
        suite.addTest(new TestScalarInterpolator("testKeyGenStep"));
        suite.addTest(new TestScalarInterpolator("testClamping"));

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

        interpolator = new ScalarInterpolator();
    }

    /**
     * Test that we can create a basic array of values using float arrays
     * inserted linearly without generating exceptions.
     */
    public void testCreate()
    {
        int i;
        int num_keys = keys.length;

        for(i = 0; i < num_keys; i++)
            interpolator.addKeyFrame(keys[i], values[i]);

        // now fetch these values back again and make sure they are the same
        for(i = 0; i < num_keys; i++)
        {
            float value = interpolator.floatValue(keys[i]);

            assertEquals(i + " value not same", values[i], value, 0);
        }
    }

    /**
     * Test that we can create an array of values with values being inserted
     * between other values.
     */
    public void testValueInsert()
    {
        int i;
        int num_keys = keys.length;

        assertTrue("Not enough keys ( < 3) to do this test", num_keys > 2);

        interpolator.addKeyFrame(keys[0], values[0]);
        interpolator.addKeyFrame(keys[2], values[2]);
        interpolator.addKeyFrame(keys[1], values[1]);

        // now fetch these values back again and make sure they are the same
        for(i = 0; i < num_keys; i++)
        {
            float value = interpolator.floatValue(keys[i]);

            assertEquals(i + " value not same", values[i], value, 0);
        }
    }

    /**
     * Test that we can generate simple values for keys that are in range
     * easily. The earlier tests have made sure that we are returning the
     * right values when the key is exactly equal to one of the end values.
     * Now we are looking at a couple of coordinated points long each axis.
     */
    public void testKeyGenLinear()
    {
        int i;
        int num_keys = keys.length;

        for(i = 0; i < num_keys; i++)
            interpolator.addKeyFrame(keys[i], values[i]);

        float mid_key = keys[0] + ((keys[1] - keys[0]) / 2);
        float test_value = values[0] + ((values[1] - values[0]) / 2);

        float value = interpolator.floatValue(mid_key);

        assertEquals("1st value not same", test_value, value, 0);

        mid_key = keys[1] + ((keys[2] - keys[1]) / 2);
        test_value = values[1] + ((values[2] - values[1]) / 2);

        value = interpolator.floatValue(mid_key);

        assertEquals("2nd value not same", test_value, value, 0);
    }

    /**
     * Test that we can generate simple values for keys that are in range
     * easily. The earlier tests have made sure that we are returning the
     * right values when the key is exactly equal to one of the end values.
     * Now we are looking at a couple of coordinated points long each axis.
     */
    public void testKeyGenStep()
    {
        // readjust the interpolator type from the default
        interpolator = new ScalarInterpolator(3, Interpolator.STEP);

        int i;
        int num_keys = keys.length;

        for(i = 0; i < num_keys; i++)
            interpolator.addKeyFrame(keys[i], values[i]);

        float mid_key = keys[0] + ((keys[1] - keys[0]) / 2);

        float value = interpolator.floatValue(mid_key);

        assertEquals("1st value not same", values[0], value, 0);

        mid_key = keys[1] + ((keys[2] - keys[1]) / 2);

        value = interpolator.floatValue(mid_key);

        assertEquals("2nd value not same", values[1], value, 0);
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
            interpolator.addKeyFrame(keys[i], values[i]);

        // a key value smaller than the smallest key
        float key = keys[0] - 1;

        float value = interpolator.floatValue(key);

        assertEquals("Min value not same", values[0], value, 0);

        // A key value larger than the largest key
        num_keys--;
        key = keys[num_keys] + 1;

        value = interpolator.floatValue(key);

        assertEquals("Max value not same", values[num_keys], value, 0);
    }

    /**
     * Main method to kick everything off with.
     */
    public static void main(String[] argv)
    {
        TestRunner.run(suite());
    }
}

