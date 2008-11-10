/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.ui;

// Standard imports
import java.awt.image.BufferedImage;

// Application specific imports
// none

/**
 * An observer for when images have been captured from the Canvas3D.
 *
 * @version $Revision: 1.1.1.1 $
 */
public interface CapturedImageObserver
{
    /**
     * Notification that an image has been captured from the canvas and is
     * ready for processing.
     *
     * @param img The image that was captured
     */
    public void canvasImageCaptured(BufferedImage img);
}

