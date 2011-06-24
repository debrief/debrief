/*****************************************************************************
 *                 Teseract Software, LLP Copyright (c) 2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.overlay;

// Standard imports
import java.util.EventListener;

// Application specific imports
// none

/**
 * A listener for scroll events.
 * <p>
 *
 * @author Will Holcomb
 * @version $Revision: 1.1.1.1 $
 */
public interface ScrollEventListener extends EventListener
{
    public void itemScrolled(ScrollEvent e);
}
