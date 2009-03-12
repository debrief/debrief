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
// none

// Application specific imports
// none

/**
 * Internal data holder class that holds points for a single line.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
class LineData
{
    /** Default size of the arrays */
    private static final int DEFAULT_SIZE = 20;

    /** Increment size for the array */
    private static final int ARRAY_INC = 10;

    /** The x coordinates of the finished line */
    int[] xPoints;

    /** The y coordinates of the finished line */
    int[] yPoints;

    /** The final number of points to be drawn */
    int numPoints;

    /**
     * Construct a new line data instance. The lines contain no points.
     */
    LineData()
    {
        xPoints = new int[DEFAULT_SIZE];
        yPoints = new int[DEFAULT_SIZE];
        numPoints = 0;
    }

    /**
     * Add a new point to the line.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    void addPoint(int x, int y)
    {
        if(numPoints == xPoints.length)
        {
            int[] x_tmp = new int[numPoints + ARRAY_INC];
            int[] y_tmp = new int[numPoints + ARRAY_INC];

            System.arraycopy(xPoints, 0, x_tmp, 0, numPoints);
            System.arraycopy(yPoints, 0, y_tmp, 0, numPoints);

            xPoints = x_tmp;
            yPoints = y_tmp;
        }

        xPoints[numPoints] = x;
        yPoints[numPoints] = y;
        numPoints++;
    }
}