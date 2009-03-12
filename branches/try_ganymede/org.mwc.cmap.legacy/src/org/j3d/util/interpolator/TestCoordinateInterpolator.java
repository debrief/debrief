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
 * A test case to check the functionality of the CoordinateInterpolator
 * implementation.
 * <p>
 *
 * The test aims to check insertion and key value generation of the
 * interpolator.
 *
 * @author Justin Couc
 * @version $Revision: 1.1.1.1 $
 */
public class TestCoordinateInterpolator extends TestCase
{
    /** Keys to be used for testing */
    private static float keys[] = { 0.1f, 0.4f, 5f };

    /** Values to correspond to the test keys */
    private static float values[][] =
    {
        { 0f, 0f, 0f, 1f, 1f, 1f },
        { 1f, 1f, 1f, 2f, 2f, 2f },
        { 1f, 5f, 2f, 5f, 5f, 5f }
    };

    /** The interpolator we are testing */
    private CoordinateInterpolator interpolator;

    /**
     * Create an instance of the test case for this particular test
     * name.
     *
     * @param name The name of the test method to be run
     */
    public TestCoordinateInterpolator(String s)
    {
        super(s);
    }

    /**
     * Fetch the suite of tests for this test class to perform.
     *
     * @return A collection of all the tests to be run
     */
    public static Test suite()
    {
        TestSuite testsuite = new TestSuite();
        testsuite.addTest(new TestCoordinateInterpolator("testInvalidInsert"));
        testsuite.addTest(new TestCoordinateInterpolator("testCreateFloat"));
        testsuite.addTest(new TestCoordinateInterpolator("testValueInsertFloat"));
        testsuite.addTest(new TestCoordinateInterpolator("testKeyGenFloat"));
        testsuite.addTest(new TestCoordinateInterpolator("testClamping"));
        return testsuite;
    }

    /**
     * Pre-test instance setup code. We check here to make sure that the key
     * and value arrays are the same length just in case someone has stuffed
     * it up when playing with this code.
     */
    public void setUp()
    {
        assertEquals("Keys and values arrays are not the same size", keys.length, values.length);
        interpolator = new CoordinateInterpolator();
    }

    /**
     * Test the attempts to insert invalid lengths of data into the array
     */
    public void testInvalidInsert()
    {
        try
        {
            interpolator.addKeyFrame(0, null);
            fail("Accepted null key");
        }
        catch(IllegalArgumentException illegalargumentexception)
        {
        }

        try
        {
            float af[] = new float[0];
            interpolator.addKeyFrame(0, af);
            fail("Accepted value of zero length");
        }
        catch(IllegalArgumentException illegalargumentexception1)
        {
        }

        try
        {
            float af1[] = { 0, 1 };
            interpolator.addKeyFrame(0, af1);
            fail("Accepted value length < 3");
        }
        catch(IllegalArgumentException illegalargumentexception2)
        {
        }

        try
        {
            float af2[] = { 0, 1, 3, 5 };
            interpolator.addKeyFrame(0, af2);
            fail("Accepted value length not divisible 3");
        }
        catch(IllegalArgumentException illegalargumentexception3)
        {
        }

    }

    /**
     * Test that we can create a basic array of values using float arrays
     * inserted linearly without generating exceptions.
     */
    public void testCreateFloat()
    {
        int i;
        int len = keys.length;

        for(i = 0; i < len; i++)
            interpolator.addKeyFrame(keys[i], values[i]);

        for(i = 0; i < len; i++)
        {
            float new_vals[] = interpolator.floatValue(keys[i]);

            for(int j = 0; j < new_vals.length; j++)
                assertEquals("key " + i + " coord " + j + " not same",
                             values[i][j],
                             new_vals[j],
                             0);

        }

    }

    /**
     * Test that we can create an array of values with values being inserted
     * between other values.
     */
    public void testValueInsertFloat()
    {
        int j = keys.length;
        assertTrue("Not enough keys ( < 3) to do this test", j > 2);
        interpolator.addKeyFrame(keys[0], values[0]);
        interpolator.addKeyFrame(keys[2], values[2]);
        interpolator.addKeyFrame(keys[1], values[1]);
        for(int i = 0; i < j; i++)
        {
            float af[] = interpolator.floatValue(keys[i]);
            for(int len = 0; len < af.length; len++)
                assertEquals("key " + i + " coord " + len + " not same", values[i][len], af[len], 0.0);

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
        int j = keys.length;
        for(int i = 0; i < j; i++)
            interpolator.addKeyFrame(keys[i], values[i]);

        float f = keys[0] + (keys[1] - keys[0]) / 2;
        float f1 = values[0][0] + (values[1][0] - values[0][0]) / 2;
        float f2 = values[0][0] + (values[1][1] - values[0][1]) / 2;
        float f3 = values[0][0] + (values[1][2] - values[0][2]) / 2;

        float af[] = interpolator.floatValue(f);

        assertEquals("1st X coord not same", f1, af[0], 0.0);
        assertEquals("1st Y coord not same", f2, af[1], 0.0);
        assertEquals("1st Z coord not same", f3, af[2], 0.0);

        f = keys[1] + (keys[2] - keys[1]) / 2;
        f1 = values[1][0] + (values[2][0] - values[1][0]) / 2;
        f2 = values[1][1] + (values[2][1] - values[1][1]) / 2;
        f3 = values[1][2] + (values[2][2] - values[1][2]) / 2;

        af = interpolator.floatValue(f);

        assertEquals("2nd X coord not same", f1, af[0], 0.0);
        assertEquals("2nd Y coord not same", f2, af[1], 0.0);
        assertEquals("2nd Z coord not same", f3, af[2], 0.0);
    }

    /**
     * Test that we can generate values that are clamped to the extent values
     * of the interpolator for keys that are out of range to those inserted.
     */
    public void testClamping()
    {
        int j = keys.length;
        for(int i = 0; i < j; i++)
            interpolator.addKeyFrame(keys[i], values[i]);

        float f = keys[0] - 1;
        float af[] = interpolator.floatValue(f);
        assertEquals("Min X coord not same", values[0][0], af[0], 0.0);
        assertEquals("Min Y coord not same", values[0][1], af[1], 0.0);
        assertEquals("Min Z coord not same", values[0][2], af[2], 0.0);
        j--;

        f = keys[j] + 1;
        af = interpolator.floatValue(f);
        assertEquals("Max X coord not same", values[j][0], af[0], 0.0);
        assertEquals("Max Y coord not same", values[j][1], af[1], 0.0);
        assertEquals("Max Z coord not same", values[j][2], af[2], 0.0);
    }

    public static void main(String args[])
    {
        TestRunner.run(suite());
    }
}
