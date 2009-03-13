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
import java.awt.Rectangle;

// Application specific imports
// none

/**
 * A representation of something that can be seen on screen.
 * <p>
 *
 * @author Will Holcomb
 * @version $Revision: 1.1.1.1 $
 */
public interface ScreenComponent {

    /**
     * Get the bounds of the visible object in screen space coordinates.
     *
     * @return A rectangle representing the bounds in screen coordinates
     */
    public Rectangle getBounds();
}
