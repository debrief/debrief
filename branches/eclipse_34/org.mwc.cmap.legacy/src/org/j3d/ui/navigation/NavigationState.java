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
 * A collection of navigation state information constants.
 * <p>
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public interface NavigationState
{
    /** The navigation state is Walking */
    public static int WALK_STATE = 1;

    /** The navigation state is Tilt */
    public static int TILT_STATE = 2;

    /** The navigation state is Panning */
    public static int PAN_STATE = 3;

    /** The navigation state is Flying */
    public static int FLY_STATE = 4;

    /** The navigation state is Examine */
    public static int EXAMINE_STATE = 5;

    /** The navigation state is such that there is no navigation */
    public static int NO_STATE = 0;
}
