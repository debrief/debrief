/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util.interpolator;

// Standard imports
import javax.vecmath.Point3f;

// Application specific imports
// none

/**
 * An interpolator that works with positional coordinates.
 * <P>
 *
 * The interpolation routine is just a simple linear interpolation between
 * each of the points. The interpolator may take arbitrarily spaced keyframes
 * and compute correct values.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public abstract class Interpolator
{
    /** The interpolator should act as a linear interpolator between keys */
    public static final int LINEAR = 1;

    /** The interpolator should act as a step interpolator between keys */
    public static final int STEP = 2;

    /** The default number of items in the interpolator */
    protected static final int DEFAULT_SIZE = 20;

    /** The number of items to increment the array with */
    protected static final int ARRAY_INCREMENT = 5;

    /** The current size of the array data */
    protected int allocatedSize;

    /** Current total number of items in the array */
    protected int currentSize;

    /** The keys as a single array for fast searching */
    protected float[] keys;

    /** The type of interpolation routine to use */
    protected final int interpolationType;

    /**
     * Create a new interpolator instance with the default size for the number
     * of key values.
     */
    protected Interpolator()
    {
        this(DEFAULT_SIZE, LINEAR);
    }

    /**
     * Create an interpolator with the given basic size.
     *
     * @param size The starting number of items in interpolator
     */
    protected Interpolator(int size)
    {
        this(size, LINEAR);
    }

    /**
     * Create a interpolator with the given basic size and interpolation
     * type.
     *
     * @param size The starting number of items in interpolator
     * @param type The type of interpolation routine to do
     */
    protected Interpolator(int size, int type)
    {
        interpolationType = type;

        keys = new float[size];
    }

    //---------------------------------------------------------------
    // Misc Internal methods
    //---------------------------------------------------------------

    /**
     * Find the key in the array. Performs a fast binary search of the values
     * to locate the right index. Most of the time the key will not be in the
     * array so this will return the index to the key that is the least
     * smallest of all keys compared to this key. If the key is smaller than
     * all known keys, a value of -1 is returned. A binary search is O(log n).
     *
     * @param key The key to search for
     * @return The index of the key that is just greater than this key
     */
    protected int findKeyIndex(float key)
    {
        // some special case stuff - check the extents of the array to avoid
        // the binary search
        if((key <= keys[0]) || (currentSize == 0))
            return -1;
        else if(key == keys[currentSize - 1])
            return currentSize - 1;
        else if(key > keys[currentSize - 1])
            return currentSize;

        int mid = -1;
        for(int i = 1; i < currentSize; i++)
        {
            if(keys[i] > key)
            {
                mid = i - 1;
                break;
            }
        }

        // This binary search just doesn't seem to work right. Don't know why
        // but we've gone for the horribly in-efficient linear search above.
        // this must be fixed soon....
/*
        int start = 0;
        int end = currentSize - 1;
        int mid = (currentSize - 1) >> 1;

        // basic binary search. Done without being recursive for speed.
        while(start < end)
        {
            mid = ((end - start) >> 1) + start;
            float test = keys[mid];

            if(test == key)
                break;
            else if(key < test)
                end = mid - 1;
            else
                start = mid + 1;
        }

        // An adjustment for if the key is just bigger than the mid point
        // and the tests above have accidently shifted the mid point one
        // place too high (mid + 1) because start and end are one index apart.
        // This normally only happens when we have an odd number of items in
        // the array.
        if(keys[mid] > key)
            mid--;
*/
        return mid;
    }
}
