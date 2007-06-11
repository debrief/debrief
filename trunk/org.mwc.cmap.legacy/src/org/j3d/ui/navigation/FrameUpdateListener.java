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
import  javax.media.j3d.Transform3D;

// Application specific imports
// none

/**
 * A listener interface used internally to notify of an update or change in the
 * system that will effect the display.
 * <p>
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public interface FrameUpdateListener
{
    /**
     * Called after each phase of transition or mouse navigation.
     *
     * @param t3d The transform of the new position
     */
    public void viewerPositionUpdated(Transform3D t3d);

    /**
     * Called when a transition from one position to another has ended.
     *
     * @param t3d The transform of the new position
     */
    public void transitionEnded(Transform3D t3d);
}
