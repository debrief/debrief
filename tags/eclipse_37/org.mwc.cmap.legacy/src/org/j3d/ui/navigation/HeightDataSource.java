/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

package org.j3d.ui.navigation;

// Standard imports
// none

// Application specific imports
// none

/**
 * A marker interface to indicate that a class can contain direct height
 * information without needing to calculate intersection points.
 * <p>
 *
 * This interface is used to mark any type of class as being able to provide
 * height information that is useful for terrain following. The typical
 * terrain code will store the data class in the userData section of a Java3D
 * node. When the terrain following code goes looking in the userData, it may
 * find an instance of this interface or the {@link org.j3d.geom.GeometryData}
 * to help it speed up the process. In the case of this class, we can perform
 * special case operations that will return height in the local coordinate
 * system Y axis. With a simple height (which may have to be interpolated from
 * the underlying data source), calculations are much easier.
 * <p>
 *
 * An important caveat must be made here - this class is only applicable for
 * single-sided surfaces, such as terrain. To fetch the height of a sphere
 * using this class is totally meaningless as there are two possible answers
 * and the code does no know which "side" of the sphere to give the height
 * for.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public interface HeightDataSource
{
    /**
     * Get the height at the given X,Z coordinate in the local coordinate
     * system. Depending on the nature of the underlying data source, this
     * may need to be interpolated (for example, the data is a height grid).
     * If the height is from an area outside of the extents of this geometry
     * which it should never be!) then return {@link Float.NaN}.
     *
     * @param x The x coordinate for the height sampling
     * @param z The z coordinate for the height sampling
     * @return The height at the current point or NaN
     */
    public float getHeight(float x, float z);
}
