/*****************************************************************************
 *                        J3D.org Copyright (c) 2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.overlay;

// Standard imports
import java.awt.Color;
import java.util.ArrayList;

// Application specific imports
// none

/**
 * Internal data holder class that holds a collection of lines of a given
 * color.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
class LineDetails
{
    /** The color of the enclosed lines */
    Color color;

    /**
     * An array of all lines held by this array. The array contains a list
     * of LineData items.
     */
    ArrayList<LineData> lines;

    /**
     * Construct a new set of line details for a line of the given color.
     * A reference is taken to the color object, rather than a copy.
     *
     * @param c The color to use
     */
    LineDetails(Color c)
    {
        color = c;
        lines = new ArrayList<LineData>();
    }

    /**
     * Create a new line instance and add it to the array. Returns an empty
     * set of LineData for the new line data to be written to.
     *
     * @return The data holder for the new line
     */
    LineData addLine()
    {
        LineData ret_val = new LineData();
        lines.add(ret_val);

        return ret_val;
    }
}