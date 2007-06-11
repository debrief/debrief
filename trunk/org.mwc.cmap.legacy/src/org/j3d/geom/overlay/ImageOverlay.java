/*****************************************************************************
 *                        J3D.org Copyright (c) 2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.overlay;

// Standard imports
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.media.j3d.Canvas3D;

// Application specific imports
// None

/**
 * An overlay that draws an image on screen at the given position.
 * <p>
 *
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class ImageOverlay extends OverlayBase
{
    /**
     * Construct a new overlay that paints the image in the given space.
     * The image is assumed to have an alpha component but it is not blended.
     *
     * @param canvas The canvas to put the overlay on
     * @param imageSpace The space on the canvas to place the image
     * @param image The image to display
     */
    public ImageOverlay(Canvas3D canvas,
                        Rectangle imageSpace,
                        BufferedImage image)
    {
        this(canvas, imageSpace, true, false, image);
    }

    /**
     * Construct a new overlay that paints the image in the given space
     * and has control over the alpha.
     *
     * @param canvas The canvas to put the overlay on
     * @param imageSpace The space on the canvas to place the image
     * @param clipAlpha true if the image contains alpha
     * @param blendAlpha true if you want to blend the image with the
     *    background of the overlay (ie let the color show through)
     * @param image The image to display
     */
    public ImageOverlay(Canvas3D canvas,
                        Rectangle imageSpace,
                        boolean clipAlpha,
                        boolean blendAlpha,
                        BufferedImage image)
    {
        super(canvas, imageSpace, clipAlpha, blendAlpha, null, 1);

        updateBuffer(image, 0);
        setActiveBuffer(0);
    }

    /**
     * Change the displayed image to this new image.
     *
     * @param img The new image to see
     */
    public void setImage(BufferedImage img)
    {
        updateBuffer(img, 0);
        setActiveBuffer(0);
    }

    public void repaint()
    {
        // Prevent painting
    }
}
