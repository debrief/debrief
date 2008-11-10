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
 * A marker interface to indicate that an item of geometry contains height map
 * information that will be useful for terrain following and collision
 * detection.
 * <p>
 *
 * This implementation is used to mark any geometry items that exists in the
 * scene and extends one of the standard Java3D nodes, such as
 * {@link javax.media.j3d.BranchGroup}. The interface is used by the navigation
 * code to help simplify the calculations needed to follow terrain. When the
 * navigation code does the picking for terrain following and collision
 * detection, it is returned a list of {@link javax.media.j3d.Node}
 * instances. With these, a check is made for the J3D node to be an instance of
 * this interface. If it is, it will ignore any other detail intersection calcs
 * an immediately ask this for resolution of the height for the given X,Z
 * location (it will have previously transformed into the correct coordinate
 * systems so you don't need to worry about that). You return it the height
 * (which may have to be interpolated from your underlying data) at that
 * position.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public interface HeightMapGeometry extends HeightDataSource
{
}
