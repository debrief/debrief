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
 * A representation of an entity that requires periodic updates.
 * <p>
 *
 * Instances of this interface can be registered with the {@link UpdateManager}
 * and they will have their state observed and updated as required.
 *
 * @author Will Holcomb
 * @version $Revision: 1.1.1.1 $
 */
public interface UpdatableEntity {
    /**
     * Any changes that will affect the screen appearance should be made in here. The
     * UpdateManager is responsible for guaranteeing that this is executed in between
     * frames.
     */
    public void update();
}
