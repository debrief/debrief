/*****************************************************************************
 *                            (c) j3d.org 2002
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.loaders;

// Standard imports
import javax.vecmath.Point2d;

import com.sun.j3d.loaders.LoaderBase;

// Application specific imports
// none

/**
 * Base Loader definition for all loaders that can load terrain data.
 * <p>
 *
 *
 * @author  Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public abstract class HeightMapLoader extends LoaderBase
{
    /**
     * Construct a new default loader with no flags set
     */
    public HeightMapLoader()
    {
    }

    /**
     * Construct a new loader with the given flags set.
     *
     * @param flags The list of flags to be set
     */
    public HeightMapLoader(int flags)
    {
        super(flags);
    }

    /**
     * Return the height map created for the last stream parsed. If no stream
     * has been parsed yet, this will return null. Height is relative to
     * sea-level which has a value of zero.
     *
     * @return The array of heights in [row][column] order or null
     */
    public abstract float[][] getHeights();

    /**
     * Fetch information about the real-world stepping sizes that this
     * grid uses.
     *
     * @return The stepping information for width and depth
     */
    public abstract Point2d getGridStep();
}
