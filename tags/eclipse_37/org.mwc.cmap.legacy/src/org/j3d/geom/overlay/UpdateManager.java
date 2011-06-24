/*****************************************************************************
 *                        Teseract Software, LLP (c) 2001
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
 * A representation of a manager that processes updates.
 * <p>
 *
 * The manager interface does not present methods for adding and removing
 * updatable entities as implementations may choose to do this in a number
 * of different ways. For example a manager may only work with one item,
 * while others might collect a bunch together.
 *
 * @author Will Holcomb
 * @version $Revision: 1.1.1.1 $
 */
public interface UpdateManager
{
    /**
     * Request that the manager update all of the items being managed.
     * This will be scheduled to happen as soon as possible, but won't
     * necessarily happen immediately.
     */
    public void updateRequested();

    /**
     * Check to see if the manager is making updates right now.
     *
     * @return true if the update process is currently happening
     */
    public boolean isUpdating();

    /**
     * Instruct the system to start or stop the update process. This is used
     * to control the whole threaded update system rather than interact with
     * a single update request.
     *
     * @param updating true to set the update to happen, false to stop
     */
    public void setUpdating(boolean updating);
}
