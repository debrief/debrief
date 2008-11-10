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
 * A listener interface used to communicate changes in the navigation state
 * from one handler to another.
 * <p>
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public interface ViewpointSelectionListener
{
    /**
     * A new viewpoint has been selected and this is it. Move to this viewpoint
     * location according to the requested means.
     *
     * @param vp The new viewpoint to use
     */
    public void viewpointSelected(ViewpointData vp);
}
